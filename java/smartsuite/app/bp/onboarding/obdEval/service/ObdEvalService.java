package smartsuite.app.bp.onboarding.obdEval.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.onboarding.obdEval.event.ObdEvalEventPublisher;
import smartsuite.app.bp.onboarding.obdEval.repository.ObdEvalRepository;
import smartsuite.app.bp.onboarding.obdSetup.service.OegSetupService;
import smartsuite.app.bp.onboarding.request.service.OnboardingEvalMonitoringService;
import smartsuite.app.common.ObdConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

/**
 * obd eval 서비스 Class입니다.
 *
 * @author yjPark
 * @see
 * @FileName ObdEvalService.java
 * @package smartsuite.app.bp.vs.obdEval.service
 * @Since 2023. 6. 18
 * @변경이력 : [2023. 6. 18] yjPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class ObdEvalService {

	@Inject
	private ObdEvalRepository obdEvalRepository;

	@Inject
	private OnboardingEvalMonitoringService onboardingEvalMonitoringService;

	@Inject
	private OegSetupService oegSetupService;

	@Inject
	private ObdEvalEventPublisher obdEvalEventPublisher;

	/**
	 * 온보딩평가그룹을 조회한다. (콤보박스용)
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the obd eval group list for combobox
	 * @Date : 2023. 7. 3
	 * @Method Name : findListOegForCombobox
	 */
	/* 처리 상태 콤보박스 목록 조회 */
	public FloaterStream findListOegForCombobox(Map param) {
		return oegSetupService.findListOeg(param);
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
		return obdEvalRepository.findListOnboardingEvalFulfill(param);
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
		List<Map<String, Object>> subjInfoList = obdEvalRepository.findOnboardingEvalSubjectInfo(param);

		if (subjInfoList != null && subjInfoList.size() > 0) {
			// 내부 온보딩평가일 경우, 평가자의 평가항목 권한이 여러개일 경우 평가대상이 여러개 조회될 수 있으므로 subjInfo -> subjInfoList로 조회
			resultData.put("subjInfoList", subjInfoList);
			return ResultMap.SUCCESS(resultData);
		} else {
			return ResultMap.FAIL(resultData);
		}
	}

	/**
	 * 온보딩)평가수행 항목 정보 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the evalfact information to be fulfilled
	 * @Date : 2023. 7. 1
	 * @Method Name : findOnboardingEvalfactFulfillInfo
	 */
	public ResultMap findOnboardingEvalfactFulfillInfo(Map param) {
		return obdEvalEventPublisher.findOnboardingEvalfactFulfillInfo(param);
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
		ResultMap saveResultMap = obdEvalEventPublisher.saveOnboardingEvalFulfillment(param);

		if (saveResultMap.isSuccess()) {
			Map<String, Object> resultData = saveResultMap.getResultData();
			Map<String, Object> evalSubject = (Map<String, Object>) param.get("evalSubjMap");
			String evalPrgsStsCcd = (String) evalSubject.get("eval_prgs_sts_ccd");

			evalSubject.put("evaltr_sc", resultData.get("evaltr_sc"));
			evalSubject.put("eval_sts_ccd", evalPrgsStsCcd); // (평가공통)평가 진행 상태(R401)
			obdEvalRepository.updateObdEvalPrcsEvaltrSc(evalSubject);

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
		obdEvalRepository.updateObdEvalPrcsSc(param);
	}

	/**
	 * 처리 상태 콤보박스 목록 조회
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the oe req sts
	 * @Date : 2023. 6. 22
	 * @Method Name : findOePrgsStsCcd
	 */
	public List findOePrgsStsCcd(String param) {
		return obdEvalRepository.findOePrgsStsCcd(param);
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
		String closedObdEvalYn = (String) obdEvalRepository.getObdEvalPrcsStatusClosedYn(param);
		String username = Auth.getCurrentUserName();

		if(ObdConst.N_STR_VAL.equals(closedObdEvalYn)) {
			obdEvalRepository.startCloseObdEval(param);
		//	try {
				// 평가 집계를 요청한다.
				param.put(ObdConst.EVAL_REQ_UUID, param.getOrDefault(ObdConst.OE_PRCS_UUID, ""));
				obdEvalEventPublisher.calculateEvalSubjResByEvalReqWithCalcfact(param);
		//	} catch(PerformanceEvalException peExcption) {
//				 pfmcEvalSharedService.writeEvalErrorLog(peExcption);
//				throw new CommonException(ErrorCode.FAIL, peExcption);
//			}

			// 남은 평가자가 없고 제출 상태의 경우 다음 절차 진행
			return onboardingEvalMonitoringService.completeOnboardingEvalPrcs(param);
		}
		return ResultMap.FAIL();
	}

	public Map findOeEvalByEvalSubjEvaltrResId(Map data) {
		return obdEvalRepository.findOeEvalByEvalSubjEvaltrResId(data);
	}
}
