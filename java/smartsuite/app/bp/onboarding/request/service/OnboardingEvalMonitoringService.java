package smartsuite.app.bp.onboarding.request.service;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.onboarding.obdEval.service.ObdEvalService;
import smartsuite.app.bp.onboarding.request.event.OnboardingEvalRequestEventPublisher;
import smartsuite.app.bp.onboarding.request.repository.OnboardingEvalMonitoringRepository;
import smartsuite.app.common.ObdConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * obd eval monitoring 서비스 Class입니다.
 *
 * @author yjPark
 * @see
 * @FileName OnboardingEvalMonitoringService.java
 * @package smartsuite.app.bp.onboarding.result.service
 * @Since 2023. 6. 18
 * @변경이력 : [2023. 6. 18] yjPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class OnboardingEvalMonitoringService {

	@Inject
	private OnboardingEvalMonitoringRepository onboardingEvalMonitoringRepository;

	@Inject
	private OnboardingEvalRequestService onboardingEvalRequestService;

	@Inject
	private ObdEvalService obdEvalService;

	@Inject
	private OnboardingEvalRequestEventPublisher onboardingEvalRequestEventPublisher;

	/**
	 * 온보딩평가 요청 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 18
	 * @Method Name : findListReqOnboardingEval
	 */
	public FloaterStream findListReqOnboardingEval(Map param) {
		// 대용량 처리
		return onboardingEvalMonitoringRepository.findListReqOnboardingEval(param);
	}

	/**
	 * 온보딩평가 절차 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 18
	 * @Method Name : findListOnboardingEvalProcess
	 */
	public List findListOnboardingEvalProcess(Map param) {
		return onboardingEvalMonitoringRepository.findListOnboardingEvalProcess(param);
	}

	/**
     * 온보딩평가 모니터링 - 평가를 평가마감 한다.
     *
	 * @author : yjPark
     * @param param the param
     * @return the map
	 * @Date : 2023. 6. 26
	 * @Method Name : saveListOnboardingEvalComplete
     */
    public ResultMap saveListOnboardingEvalComplete(Map<String, Object> param) {
        List<Map<String, Object>> lists = (List<Map<String, Object>>)param.get("checkList");

        for(Map<String, Object> row : lists){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            row.put("oe_ed_dt", simpleDateFormat.format(new Date()));
			onboardingEvalMonitoringRepository.saveListOnboardingEvalCompleteOe(row);       // 1. 온보딩평가 마감 저장
			onboardingEvalMonitoringRepository.saveListOnboardingEvalCompleteOePrcs(row);   // 2. 온보딩평가 프로세스 마감 저장
        }

        return ResultMap.SUCCESS();
    }

	/**
	 * 온보딩평가 모니터링 - 프로세스를 조건부합격 한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 26
	 * @Method Name : saveProcessConditionalPass
	 */
	public ResultMap saveProcessConditionalPass(Map param) {
		ResultMap resultMap = ResultMap.getInstance();

		onboardingEvalMonitoringRepository.saveProcessConditionalPass(param);
		onboardingEvalMonitoringRepository.saveOnboardingEvalConditionalPass(param); // 조건부합격시 OE 요청 결과를 진행중(PRGSG)으로 UPDATE

		return this.completeOnboardingEvalPrcs(param);
	}

	/**
	 * 온보딩평가 프로세스 결과처리 한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 05
	 * @Method Name : completeOnboardingEvalPrcs
	 */
	public ResultMap completeOnboardingEvalPrcs(final Map<String, Object> param) {
		Map<String, Object> resultInfo = Maps.newHashMap();

		// 1. 온보딩평가 프로세스 조회
		final Map<String, Object> onboardingEvalPrcs = onboardingEvalMonitoringRepository.findOnboardingEvalPrcsInfo(param);

		if(MapUtils.isEmpty(onboardingEvalPrcs)){
			// 결과 처리할 업체의 등록심사 절차 데이타가 없습니다.
			return ResultMap.FAIL();
		}else{
			// 2. 온보딩평가 프로세스 결과 계산
			this.calculateOnboardingEvalPrcs(onboardingEvalPrcs);
		}

		if(ObdConst.Y_STR_VAL.equals((String)onboardingEvalPrcs.get("auto_prcsg_yn"))){
			// 3. 다음 프로세스의 평가 결과 생성
			ResultMap evalSubjResResultMap = onboardingEvalRequestService.createNextOnboardingEvalProcessEvalRes(onboardingEvalPrcs);
			resultInfo = evalSubjResResultMap.getResultData();
			if (MapUtils.isEmpty(resultInfo) || !evalSubjResResultMap.isSuccess()) {
				return ResultMap.FAIL(resultInfo);
			}
		}
		return ResultMap.SUCCESS(resultInfo);
	}

	/**
	 * 온보딩평가 프로세스 결과를 계산 한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 05
	 * @Method Name : calculateOnboardingEvalPrcs
	 */
	public void calculateOnboardingEvalPrcs(final Map<String, Object> param) {
		// 온보딩평가 프로세스 평가 결과를 UPDATE 한다.
		// 프로세스 합격/프로세스 조건부 합격/이전 온보딩평가 결과 적용인 경우 점수를 UPDATE 하지않는다.
		// 불합격인경우 점수를 UPDATE 하지않는다.
		// 1. 협력사 심사그룹 평가 절차 상태 UPDATE OE_PRCS
		onboardingEvalMonitoringRepository.updateOnboardingEvalPrcsResultSts(param);

		// + 불합격일 경우 온보딩 평가 요청 결과 update
		onboardingEvalMonitoringRepository.updateOnboardingEvalReqResultFail(param);

		// 2. 잠재 업체 등록
		onboardingEvalRequestEventPublisher.updateVdOorgObdTypCcdToPtnl(param);
	}

	/**
	 * 온보딩평가 모니터링 - 절차이동 한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 26
	 * @Method Name : saveMoveNextPrcsProcess
	 */
	public ResultMap saveMoveNextPrcsProcess(Map param) {
		ResultMap resultMap = ResultMap.getInstance();
		Map<String, Object> resultInfo = Maps.newHashMap();

		ResultMap evalSubjResResultMap = onboardingEvalRequestService.createNextOnboardingEvalProcessEvalRes(param);
		resultInfo = evalSubjResResultMap.getResultData();
		if (MapUtils.isEmpty(resultInfo) || !evalSubjResResultMap.isSuccess()) {
			return ResultMap.FAIL(resultInfo);
		}
		String resultStatus = evalSubjResResultMap.getResultStatus();

		resultMap.setResultStatus(resultStatus);
		resultMap.setResultData(resultInfo);
		return resultMap;
	}

	/**
	 * 온보딩평가 요청 목록 팝업을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 25
	 * @Method Name : findListReqInfo
	 */
	public List findListReqInfo(Map param) {
		// 대용량 처리
		return onboardingEvalMonitoringRepository.findListReqInfo(param);
	}

	/**
	 * 온보딩평가 평가결과상세 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 25
	 * @Method Name : findListObdEvalDetailScoreInfo
	 */
	public List findListObdEvalDetailScoreInfo(Map param) {
		// 대용량 처리
		return onboardingEvalMonitoringRepository.findListObdEvalDetailScoreInfo(param);
	}

	/**
	 * 온보딩평가 프로세스 정보를 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 18
	 * @Method Name : findListOnboardingEvalProcess
	 */
	public Map findOePrcsInfo(Map param) {
		Map<String,Object> resultMap = onboardingEvalMonitoringRepository.findOePrcsInfo(param);  // 온보딩평가 프로세스 정보 조회
    	if (resultMap != null && !resultMap.isEmpty()) {
    		resultMap.put("evaltr_grp_list", onboardingEvalMonitoringRepository.findOePrcsInfoEvaltrGrp(resultMap));  // 온보딩평가 프로세스 평가자 그룹 목록 조회
    	}
    	return resultMap;
	}

	/**
	 * 온보딩평가 프로세스 평가 담당자 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the List<<map< string, object>>
	 * @Date : 2023. 7. 05
	 * @Method Name : findListOnboardingEvalProcess
	 */
	public List findListOePrcsEvaltr(Map param) {
    	return onboardingEvalMonitoringRepository.findListOePrcsEvaltr(param);
	}

	/**
	 * 온보딩평가 프로세스 평가 담당자를 저장한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 05
	 * @Method Name : saveOePrcsEvaltr
	 */
	public ResultMap saveOePrcsEvaltr(Map<String, Object> param){
		final List<Map<String, Object>> oePrcsEvalSubjEvaltrList = (List<Map<String, Object>>)param.get("insertList");

		// TODO saveListPrgsPeSubjEvaltr 참고하여 validation 추가 예정

		if (oePrcsEvalSubjEvaltrList != null && !oePrcsEvalSubjEvaltrList.isEmpty()) {
			for (final Map<String, Object> oePrcsEvalSubjEvaltr : oePrcsEvalSubjEvaltrList) {
				// 1. 온보딩평가그룹 온보딩평가 프로세스 담당자 생성
				oePrcsEvalSubjEvaltr.put("oe_prcs_evaltr_uuid", UUID.randomUUID().toString());
				onboardingEvalMonitoringRepository.insertOePrcsEvaltrForManagenent(oePrcsEvalSubjEvaltr);

				onboardingEvalRequestService.createEvalSubjEvaltrResByOeSubjEvaltr(oePrcsEvalSubjEvaltr);
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 온보딩평가 프로세스 평가 담당자를 삭제한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 05
	 * @Method Name : deleteOePrcsEvaltr
	 */
	public ResultMap deleteOePrcsEvaltr(Map<String, Object> param){
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		if (deletes != null && !deletes.isEmpty()) {
			for (final Map<String, Object> row : deletes) {
				// 1. 온보딩평가그룹 온보딩평가 프로세스 담당자 삭제
				onboardingEvalMonitoringRepository.deleteOePrcsEvaltr(row);
				// 2. 평가대상 평가자 결과 삭제
				onboardingEvalRequestEventPublisher.deleteEvalSubjEvaltrRes(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 온보딩평가 프로세스를 마감한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 05
	 * @Method Name : closeReqObdEvalPrcs
	 */
	public ResultMap closeReqObdEvalPrcs(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		Map<String, Object> oePrcsInfo = onboardingEvalMonitoringRepository.findOePrcsInfo(param);  // 1. 온보딩평가 프로세스 정보 조회

		if (oePrcsInfo == null || oePrcsInfo.isEmpty() ||
				(!"EVAL_WTG".equals((String)oePrcsInfo.get("oe_prcs_sts_ccd")) && !"EVAL_PRGSG".equals((String)oePrcsInfo.get("oe_prcs_sts_ccd")))) {  /* EVAL_WTG: 평가 대기, EVAL_PRGSG: 평가 진행중 */
			// 온보딩평가 프로세스 정보가 없거나 평가대기 또는 평가 진행중 상태가 아닌경우 마감 불가
			return ResultMap.INVALID();
		}

		// 2. 온보딩평가 프로세스 미제출 평가 담당자 목록 조회
		List<Map<String, Object>> deleteChrList = onboardingEvalMonitoringRepository.findListEvartrOfIncompleteOePrcs(param);
		if (deleteChrList != null && !deleteChrList.isEmpty()) {
			// 3. 온보딩평가 프로세스 미제출 평가 담당자 삭제
			param.put("deleteList", deleteChrList);
			resultMap = this.deleteOePrcsEvaltr(param);
		} else {
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		}

		if (ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			obdEvalService.closeObdEvalPrcs(param); // 온보딩평가 프로세스를 마감(= 집계)한다.
		}
		return resultMap;
	}

	/**
	 * 온보딩평가 결재진행여부를 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return void
	 * @Date : 2023. 07. 11
	 * @Method Name : findReqedOnboardingAprvProgYn
	 */
	public Map findReqedOnboardingAprvProgYn(Map param) {
		return onboardingEvalMonitoringRepository.findReqedOnboardingAprvProgYn(param);
	}

	/**
	 * 온보딩 평가시트 사용여부 체크 (OE 생성 여부)
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the isCreateOeYn
	 * @Date : 2023. 7. 21
	 * @Method Name : findCreateOeYnByObdEvalsht
	 */
	public String findCreateOeYnByObdEvalsht(Map param) {
		return onboardingEvalMonitoringRepository.findCreateOeYnByObdEvalsht(param);
	}

	/**
	 * 온보딩평가그룹 사용여부 체크 (OE 생성 여부)
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the isCreateOeYn
	 * @Date : 2023. 7. 23
	 * @Method Name : findCreateOeYnByOeg
	 */
	public String findCreateOeYnByOeg(Map param) {
		return onboardingEvalMonitoringRepository.findCreateOeYnByOeg(param);
	}
}
