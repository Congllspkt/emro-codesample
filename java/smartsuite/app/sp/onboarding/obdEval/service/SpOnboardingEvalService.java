package smartsuite.app.sp.onboarding.obdEval.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.ObdConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.onboarding.obdEval.event.SpOnboardingEvalEventPublisher;
import smartsuite.app.sp.onboarding.obdEval.repository.SpOnboardingEvalRepository;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * sp obd eval 서비스 Class입니다.
 *
 * @author sykim
 * @see
 * @FileName spOnboardingEvalService.java
 * @package smartsuite.app.sp.onboarding.obdEval.service
 * @Since 2023. 6. 29
 * @변경이력 : [2023. 6. 29] sykim 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SpOnboardingEvalService {

	@Inject
	private SpOnboardingEvalRepository spOnboardingEvalRepository;

	@Inject
	private SpOnboardingEvalRequestService spOnboardingEvalRequestService;

	@Inject
	private SpOnboardingEvalEventPublisher spOnboardingEvalEventPublisher;

	/**
	 * 운영단위별 운영조직 조회.
	 *
	 * @param param
	 * @Date : 2023. 07. 17
	 * @author yjPark
	 */
	public List<Map<String, Object>> findListOorgCdAll(String param) {
		return spOnboardingEvalRepository.findListOorgCdAll(param);
	}

	/**
	 * 온보딩평가 수행 목록을 조회한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 23
	 * @Method Name : findListOnboardingEvalFulfill
	 */
	public FloaterStream findListOnboardingEvalFulfill(Map param) {
		// 대용량 처리
		return spOnboardingEvalRepository.findListOnboardingEvalFulfill(param);
	}

	/**
	 * 온보딩평가 평가수행 대상 정보를 조회한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 1
	 * @Method Name : findOnboardingEvalSubjectInfo
	 */
	public ResultMap findOnboardingEvalSubjectInfo(Map param) {
		Map<String, Object> resultData = Maps.newHashMap();
		Map<String, Object> subjInfo = spOnboardingEvalRepository.findOnboardingEvalSubjectInfo(param);
		if (subjInfo != null) {
			resultData.put("subjInfo", subjInfo);
			return ResultMap.SUCCESS(resultData);
		} else {
			return ResultMap.FAIL(resultData);
		}
	}

	/**
	 * (온보딩)평가수행 항목 정보 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the evalfact information to be fulfilled
	 * @Date : 2023. 7. 1
	 * @Method Name : findOnboardingEvalfactFulfillInfo
	 */
	public ResultMap findOnboardingEvalfactFulfillInfo(Map param) {
		return spOnboardingEvalEventPublisher.findOnboardingEvalfactFulfillInfo(param);
	}

	/**
	 * (온보딩)평가수행 정보를 저장한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 13
	 * @Method Name : saveOnboardingEvalFulfillment
	 */
	public ResultMap saveOnboardingEvalFulfillment(Map param) {
		// 평가수행 정보 저장
		ResultMap saveResultMap = spOnboardingEvalEventPublisher.saveOnboardingEvalFulfillment(param);

		if (saveResultMap.isSuccess()) {
			Map<String, Object> resultData = saveResultMap.getResultData();
			Map<String, Object> evalSubject = (Map<String, Object>) param.get("evalSubjMap");
			String evalPrgsStsCcd = (String) evalSubject.get("eval_prgs_sts_ccd");

			evalSubject.put("evaltr_sc", resultData.get("evaltr_sc"));
			evalSubject.put("eval_sts_ccd", evalPrgsStsCcd); // (평가공통)평가 진행 상태(R401)
			spOnboardingEvalRepository.updateObdEvalPrcsEvaltrSc(evalSubject);

			evalSubject.put("prcs_eval_sc", resultData.get("eval_sc"));
			evalSubject.put("oe_prcs_sts_ccd", "EVAL_PRGSG"); // 온보딩평가 프로세스 상태(E007)
			this.updateObdEvalPrcsSc(evalSubject);

			// 각 DB Vendor마다 반환하는 타입이 달라 상위 클래스인 Number로 처리 (BigDecimal, Long, Integer)
			Number notSubmEvaltrCnt = (Number) resultData.get("not_subm_evaltr_cnt");  // 평가대상 별 남은 평가자 수

			if (notSubmEvaltrCnt.intValue() == 0 && "SUBM".equals(evalPrgsStsCcd)) {
				// 온보딩평가 프로세스를 마감(= 집계)
				return this.closeObdEvalPrcs(evalSubject);
			}
		}

		return saveResultMap;
	}

	/**
	 * 온보딩평가 프로세스 점수, 상태 업데이트
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the oe req sts
	 * @Date : 2023. 6. 22
	 * @Method Name : updateObdEvalPrcsSc
	 */
	public void updateObdEvalPrcsSc(Map param) {
		spOnboardingEvalRepository.updateObdEvalPrcsSc(param);
	}

	/**
	 * 온보딩 평가 종료에 따른 온보딩평가 평가 완료 처리를 한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return void
	 * @Date : 2023. 07. 02
	 * @Method Name : saveOnboardingEvalComplete
	 */
	public void saveOnboardingEvalComplete(Map param) {
		spOnboardingEvalRepository.saveOnboardingEvalComplete(param);
	}

	/**
	 * 온보딩평가 프로세스를 마감(= 집계)한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 08. 11
	 * @Method Name : closeObdEvalPrcs
	 */
	public ResultMap closeObdEvalPrcs(Map<String, Object> param) {
		String closedObdEvalYn = (String) spOnboardingEvalRepository.getObdEvalPrcsStatusClosedYn(param);
		String username = Auth.getCurrentUserName();

		if(ObdConst.N_STR_VAL.equals(closedObdEvalYn)) {
			spOnboardingEvalRepository.startCloseObdEval(param);
		//	try {
				// 평가 집계를 요청한다.
				param.put(ObdConst.EVAL_REQ_UUID, param.getOrDefault(ObdConst.OE_PRCS_UUID, ""));
				spOnboardingEvalEventPublisher.calculateEvalSubjResByEvalReqWithCalcfact(param);
		//	} catch(PerformanceEvalException peExcption) {
//				 pfmcEvalSharedService.writeEvalErrorLog(peExcption);
//				throw new CommonException(ErrorCode.FAIL, peExcption);
//			}

			// 남은 평가자가 없고 제출 상태의 경우 다음 절차 진행
			return spOnboardingEvalRequestService.completeOnboardingEvalPrcs(param);
		}
		return ResultMap.FAIL();
	}
}
