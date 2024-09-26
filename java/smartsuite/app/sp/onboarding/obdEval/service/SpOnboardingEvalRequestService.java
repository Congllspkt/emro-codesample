package smartsuite.app.sp.onboarding.obdEval.service;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.ObdConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.onboarding.obdEval.repository.SpOnboardingEvalRequestRepository;
import smartsuite.app.sp.onboarding.obdEval.event.SpOnboardingEvalEventPublisher;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * obd eval request 서비스 Class입니다.
 *
 * @author yjPark
 * @see
 * @FileName SpOnboardingEvalRequestService.java
 * @package smartsuite.app.bp.onboarding.obdEval.service
 * @Since 2023. 6. 30
 * @변경이력 : [2023. 6. 30] yjPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SpOnboardingEvalRequestService {

	@Inject
	private SpOnboardingEvalRequestRepository spOnboardingEvalRequestRepository;

	@Inject
	private SpOnboardingEvalService spOnboardingEvalService;

	@Inject
	private SpOnboardingEvalEventPublisher spOnboardingEvalEventPublisher;

	/**
	 * 온보딩평가 다음 프로세스의 평가 결과를 생성한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023.07.02
	 * @Method Name : createNextOnboardingEvalProcessEvalRes
	 */
	public ResultMap createNextOnboardingEvalProcessEvalRes(final Map<String, Object> param) {
		Map<String, Object> resultInfo = Maps.newHashMap();

		// 1. 다음 온보딩 평가 프로세스 절차순서 정보 조회
		final String targetPrcsInfo = this.getNextOnboardingEvalProcessPrcsSort(param);

		if (targetPrcsInfo == null || StringUtils.isEmpty(targetPrcsInfo)) { // 절차를 이동할 수 없는 상태 또는 오류로 다음 절차 코드가 NULL
			return ResultMap.INVALID();
		} else if ("NON_EVAL".equals(targetPrcsInfo) || "END".equals(targetPrcsInfo)) {
			// 24.01.24 TODO : 평가가 존재하지 않는 OE 생성 시 수정
			// 평가 프로세스가 존재하지 않는 경우(NON_EVAL) 또는 심사가 완료 되어 다음 절차 코드가 없는 상태(END)
			resultInfo.put("complete_prcs_yn", "Y");
			return ResultMap.SUCCESS(resultInfo);
		} else if ("PRGSG".equals(targetPrcsInfo)) {
			// 불합격 또는 진행중인 평가가 있는 경우(PRGSG)
			resultInfo.put("complete_prcs_yn", "N");
			return ResultMap.SUCCESS(resultInfo);
		} else {
			// 생성할 다음 프로세스 코드가 존재
			// 2. 다음 온보딩 평가 프로세스 결과 생성
			param.put("oe_prcs_uuid", targetPrcsInfo);
			// 2-1. 결과생성 대상 다음 온보딩 평가 프로세스
			final Map<String, Object> nextOnboardingEvalProcess = spOnboardingEvalRequestRepository.findNextOnboardingEvalProcess(param);

			if (MapUtils.isNotEmpty(nextOnboardingEvalProcess)) {
				// 2-3. 평가대상 결과 생성을 요청한다.
				nextOnboardingEvalProcess.put("eval_req_uuid", targetPrcsInfo);
				nextOnboardingEvalProcess.put(ObdConst.EVAL_TASK_TYP_CCD, ObdConst.EVAL_TASK_TYP_OE);
				ResultMap evalSubjResResultMap = spOnboardingEvalEventPublisher.createEvalSubjRes(nextOnboardingEvalProcess);

				if(!evalSubjResResultMap.isSuccess()){
					return ResultMap.FAIL("평가대상 결과 저장 실패 : " + evalSubjResResultMap.getResultMessage());
				} else {
					// 2-4. 평가대상 결과 아이디를 온보딩평가대상(OE_PRCS) 테이블에 저장
					Map resultData = evalSubjResResultMap.getResultData();
					resultData.put("oe_prcs_uuid", nextOnboardingEvalProcess.get("oe_prcs_uuid"));
					spOnboardingEvalRequestRepository.updateEvalSubjResKey(resultData);

					String createPrgsSts = resultData.get("create_prgs_sts") == null ? "" : resultData.get("create_prgs_sts").toString();  // QUANT : 정량 평가 상태(정량항목 존재, 정성항목 미존재 시), QUALI : 정성 평가 상태(정성평가 존재시))

					if("QUANT".equals(createPrgsSts)){  // 시스템 평가
						resultData.put("prcs_eval_sc", resultData.get("eval_sc"));
						resultData.put("oe_prcs_sts_ccd", "eval_prgsg"); // 온보딩평가 프로세스 상태(E007)
						// 2-5. 온보딩평가 프로세스 점수, 상태 업데이트
						spOnboardingEvalService.updateObdEvalPrcsSc(resultData);

						// 2-6. 온보딩평가 프로세스 결과처리 한다.
						return this.completeOnboardingEvalPrcs(resultData);
					}else{  // 평가자 지정 평가
						// 2-5. 평가대상 평가자 결과 생성한다.
						nextOnboardingEvalProcess.put(ObdConst.EVAL_SUBJ_RES_UUID, resultData.get(ObdConst.EVAL_SUBJ_RES_UUID));
						ResultMap evalSubjEvaltrResResultMap = this.createOePrcsEvalSubjEvaltrRes(nextOnboardingEvalProcess);

						if (!evalSubjEvaltrResResultMap.isSuccess()) {
							return evalSubjEvaltrResResultMap;
						} else {
							resultInfo = evalSubjResResultMap.getResultData();
						}
					}
				}
			}
			resultInfo.put("complete_prcs_yn", "N");
		}
		return ResultMap.SUCCESS(resultInfo);
	}

	/**
	 * 다음 온보딩 평가 프로세스 절차순서를 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023.07.02
	 * @Method Name : getNextOnboardingEvalProcessPrcsSort
	 */
	public String getNextOnboardingEvalProcessPrcsSort(final Map<String, Object> param) {
		String returnVal = "";
		// 1. 불합격 또는 진행중인 평가 프로세스 조회
		final Map<String, Object> map = spOnboardingEvalRequestRepository.findActOrFailOnboardingEvalProcessCnt(param);

		//try{
			if(map == null || map.isEmpty()){
				//evExecuteService.throwSrmExceptionByErrorCode(param, "E011");
				// 24.01.24 TODO : 평가가 존재하지 않는 OE 생성 시 수정
				// 평가 프로세스가 존재하지 않는 경우
				returnVal = "NON_EVAL";
			}else if (MapUtils.isNotEmpty(map) && MapUtils.getInteger(map, "fail_cnt").equals(0) && MapUtils.getInteger(map,"act_cnt").equals(0)) {
				// 2. 불합격과 진행중인 평가가 없는경우 프로세스 대기 중인 다음 절차 UUID를 추출한다.
				returnVal = spOnboardingEvalRequestRepository.findNextOnboardingEvalProcessPrcsUuid(param);

				if (StringUtils.isEmpty(returnVal)) {  // 다음 온보딩 평가 프로세스 없음
					// 3. 온보딩 평가 종료에 따른 온보딩평가 평가 완료 처리
					// 진행할 프로세스가 없음으로 마감 처리를 한다.
					// 자동진행인 경우 자동마감이며 수동진행인경우 프로세스 진행을 통해 접근된다.
					spOnboardingEvalService.saveOnboardingEvalComplete(param);
					returnVal = "END";
				}
			}else if (MapUtils.isNotEmpty(map)
					&& (MapUtils.getInteger(map, "fail_cnt") > 0 || MapUtils.getInteger(map,"act_cnt") > 0)) {
				// 불합격 또는 진행중인 평가가 있는 경우
				returnVal = "PRGSG";
			}
		//}catch(EvalException se){
		//	//srmSharedService.writeEvalErrorLog(se);
		//	throw new CommonException(ErrorCode.FAIL, se);
		//}
		return returnVal;
	}

	/**
	 * 평가대상 평가자 결과 생성한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023.07.03
	 * @Method Name : createOePrcsEvalSubjEvaltrRes
	 */
	public ResultMap createOePrcsEvalSubjEvaltrRes(final Map<String, Object> param) {
		// 평가대상 평가자 결과 생성을 위해
		// 1. 온보딩 프로세스 평가대상 평가자 목록 조회 (oe_prcs_uuid 단위로 조회)
		List<Map<String, Object>> oePrcsEvalSubjEvaltrList = spOnboardingEvalRequestRepository.findListOePrcsEvalSubjEvaltrByOePrcsEvalSubjRes(param);

		if(oePrcsEvalSubjEvaltrList != null) {
			for(Map<String, Object> oePrcsEvalSubjEvaltr : oePrcsEvalSubjEvaltrList) {
				oePrcsEvalSubjEvaltr.put(ObdConst.EVAL_SUBJ_RES_UUID, param.get(ObdConst.EVAL_SUBJ_RES_UUID));
				this.createEvalSubjEvaltrResByOeSubjEvaltr(oePrcsEvalSubjEvaltr);
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 온보딩평가대상 평가자에 해당하는 평가대상 평가자 결과를 생성한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * param.eval_subj_res_uuid  - 평가대상 결과 아이디
	 * param.evalfact_evaltr_authty_ccd - 평가항목 평가자 권한 공통코드
	 * param.evaltr_id - 평가자 아이디
	 * @return resultmap
	 * @Date : 2023.06.25
	 * @Method Name : createEvalSubjEvaltrResByOeSubjEvaltr
	 */
	public ResultMap createEvalSubjEvaltrResByOeSubjEvaltr(final Map<String, Object> param) {

		// 1. 평가대상 평가자 결과 생성을 요청한다.
		ResultMap evaltrResResultMap = spOnboardingEvalEventPublisher.createEvalSubjEvaltrRes(param);

		if(evaltrResResultMap.isSuccess()) {
			Map resultData = evaltrResResultMap.getResultData();
			resultData.put("oe_prcs_evaltr_uuid", param.get("oe_prcs_evaltr_uuid"));
			spOnboardingEvalRequestRepository.updateEvalSubjEvaltrResKey(resultData);
		} else {
			return ResultMap.FAIL("평가대상 평가자 결과 생성 실패 : " + evaltrResResultMap.getResultMessage());
		}

		return ResultMap.SUCCESS();
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
		return spOnboardingEvalRequestRepository.findListOnboardingEvalProcess(param);
	}

	/**
	 * 온보딩평가 프로세스 마감을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return void
	 * @Date : 2023. 6. 26
	 * @Method Name : saveListOnboardingEvalCompleteOePrcs
	 */
	public void saveListOnboardingEvalCompleteOePrcs(Map param) {
		spOnboardingEvalRequestRepository.saveListOnboardingEvalCompleteOePrcs(param);
	}

	/**
	 * 온보딩평가 프로세스 결과처리 한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 05
	 * @Method Name : saveProcessConditionalPass
	 */
	public ResultMap completeOnboardingEvalPrcs(final Map<String, Object> param) {
		Map<String, Object> resultInfo = Maps.newHashMap();

		// 1. 온보딩평가 프로세스 조회
		final Map<String, Object> onboardingEvalPrcs = spOnboardingEvalRequestRepository.findOnboardingEvalPrcsInfo(param);

		if(MapUtils.isEmpty(onboardingEvalPrcs)){
			// 결과 처리할 업체의 등록심사 절차 데이타가 없습니다.
			return ResultMap.FAIL();
		}else{
			// 2. 온보딩평가 프로세스 결과 계산
			this.calculateOnboardingEvalPrcs(onboardingEvalPrcs);
		}

		if(ObdConst.Y_STR_VAL.equals((String)onboardingEvalPrcs.get("auto_prcsg_yn"))){
			// 3. 다음 프로세스의 평가 결과 생성
			ResultMap evalSubjResResultMap = this.createNextOnboardingEvalProcessEvalRes(onboardingEvalPrcs);
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
		spOnboardingEvalRequestRepository.updateOnboardingEvalPrcsResultSts(param);

		// + 불합격일 경우 온보딩 평가 요청 결과 update
		spOnboardingEvalRequestRepository.updateOnboardingEvalReqResultFail(param);

		// 2. 잠재 업체 등록
		spOnboardingEvalEventPublisher.updateVdOorgObdTypCcdToPtnl(param);
	}
}
