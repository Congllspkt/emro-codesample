package smartsuite.app.bp.onboarding.request.service;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.onboarding.obdEval.service.ObdEvalService;
import smartsuite.app.bp.onboarding.request.event.OnboardingEvalRequestEventPublisher;
import smartsuite.app.bp.onboarding.request.repository.OnboardingEvalRequestRepository;
import smartsuite.app.common.ObdConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * obd eval request 서비스 Class입니다.
 *
 * @author yjPark
 * @see
 * @FileName OnboardingEvalRequestService.java
 * @package smartsuite.app.bp.onboarding.result.service
 * @Since 2023. 6. 18
 * @변경이력 : [2023. 6. 18] yjPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class OnboardingEvalRequestService {

	@Inject
	private OnboardingEvalRequestRepository onboardingEvalRequestRepository;

	@Inject
	private ObdEvalService obdEvalService;

	@Inject
	private OnboardingEvalMonitoringService onboardingEvalMonitoringService;

	@Inject
	private OnboardingEvalRequestEventPublisher onboardingEvalRequestEventPublisher;

	private final SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

	/**
	 * 온보딩평가 요청 대상인 협력사 목록을 조회한다.
	 *
	 * @author : swnam
	 * @param param
	 * @Date : 2024. 05. 02
	 */
    public FloaterStream findListVendorAll(Map<String, Object> param) {
	    return onboardingEvalRequestRepository.findListVendorAll(param);
    }
	
	/**
	 * 온보딩평가를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 30
	 * @Method Name : saveListReqOnboardingEval
	 */
	public ResultMap saveListReqOnboardingEval(Map param) {
		List<Map<String, Object>> lists = (List<Map<String, Object>>)param.get("checkList");

		String oeUuid = "";
        String oeVmgUuid = "";

		for(Map<String, Object> row : lists){
			// 1. 온보딩평가 요청 저장
			String oeExistsYn = onboardingEvalRequestRepository.findRegOeExistInfo(row);  // 1-1. 평가요청한 온보딩평가그룹 요청 정보(=OE) 존재여부 확인
			if("N".equals(oeExistsYn)){  // OE(=온보딩평가 요청) 미존재
				oeUuid = (String) UUID.randomUUID().toString();
				row.put("oe_uuid", oeUuid);
				row.put("oe_req_dt", fm.format(new Date()));

				Map obdEvalshtInfo = onboardingEvalRequestRepository.findOeReqObdEvalshtInfo(row);  // 1-2.  OE 생성 전 평가그룹의 유효 평가시트 정보를 조회한다.

				if(obdEvalshtInfo != null) {
					onboardingEvalRequestRepository.insertOe(row);  // 1-2. 온보딩평가 요청 저장

					String prcsApplSubjYn = (String)obdEvalshtInfo.getOrDefault("prcs_appl_subj_yn", "N");
					// 프로세스 적용 대상 여부가 Y 인 경우 프로세스 생성 
					if("Y".equals(prcsApplSubjYn)) {
						this.createReqOnboardingEvalProces(row);  // 1-3. 온보딩평가 요청 프로세스 저장
					} 
					// 프로세스 적용 대상 여부가 N 인 경우 OE 상태를 평가 완결로 변경
					else {
						onboardingEvalRequestRepository.updateOeByPrcsApplSubjN(row);
					}
				}

			} else {
				row.put("oe_uuid", oeUuid);
			}

			// 2. 온보딩평가 요청 온보딩그룹 저장
            row.put("oe_vmg_uuid", (String)UUID.randomUUID().toString());
			onboardingEvalRequestRepository.insertOeVmg(row);
        }

		Map<String, Object> resultInfo = new HashMap<>();
		resultInfo.put("oe_uuid", oeUuid);
		return ResultMap.SUCCESS(resultInfo);
	}

	/**
	 * 온보딩평가 요청 프로세스를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023.07.02
	 * @Method Name : createReqOnboardingEvalProces
	 */
	public ResultMap createReqOnboardingEvalProces(final Map<String, Object> param) {
		Map<String, Object> resultInfo = Maps.newHashMap();

		// 1. 온보딩평가 프로세스 저장(PE_PRCS)
		// 등록대상 온보딩평가그룹의 온보딩평가 프로세스를 생성한다.
		// 온보딩평가그룹의 대상 온보딩평가 프로세스를 온보딩평가 프로세스에 등록한다.
		onboardingEvalRequestRepository.insertOePrcs(param);
		// 2. 온보딩평가 프로세스 평가자 저장(OE_PRCS_EVALTR)
		onboardingEvalRequestRepository.insertOePrcsEvaltr(param);

		// 전체 절차의 이전 평가 이력을 조회하여 AUTO PASS 와 이전 강제패스 인정 여부에 따른 결과를 셋팅한다.
		// 해당 심사그룹절차의 유효기간 및 유효인정시작일 이후의 평가를 셋팅한다.
		// 의존 대상 시트의 평가결과 중 최종 결과를 가져와 대상의 합격/불합격을 판단한다.
		// 최종 심사일자의 결과를 auto pass의 여부로 판단한다.
		String preResultYn = ObdConst.N_STR_VAL;
		// 3. 생성한 전체 절차 List의 OEG 설정 조회
		final List<Map<String, Object>> list = onboardingEvalRequestRepository.findListAllOePrcs(param);

		if (CollectionUtils.isNotEmpty(list)) {
			for (final Map<String, Object> map : list) {
				// 4. 이전 온보딩평가 프로세스 결과 적용여부 체크
				preResultYn = ObdConst.Y_STR_VAL;
				if (ObdConst.Y_STR_VAL.equals(map.get("pre_oe_tcappl_yn"))) {  // 자동 패스 여부 == 'Y'
					// 4-1. 이전 온보딩평가 프로세스 중 최종 결과를 가져온다.
					// 처리완료일 기준으로 유효 월 수에 해당 하며 재갱신기간에 포함 되지 않은 유효한 데이터를 조회한다.
					final Map<String, Object> result = onboardingEvalRequestRepository.findPreObdEvalPrcsResult(map);
					if (MapUtils.isEmpty(result)) {
						// 이전 심사결과가 존재하지 않는 경우
						preResultYn = ObdConst.N_STR_VAL;
					} else {
						final String oePrcsStsCcd = (String)result.get("oe_prcs_sts_ccd");
						if (StringUtils.isNotEmpty(oePrcsStsCcd) && oePrcsStsCcd.equals("PRCS_FLR")) {
							// 이전 심사결과가 FAIL이면 이전 심사결과를 참조하지 않음
							preResultYn = ObdConst.N_STR_VAL;
						}
						map.put("pre_oe_prcs_pass_dt", result.get("pre_oe_prcs_pass_dt"));
						map.put("oe_prcs_sts_ccd", result.get("oe_prcs_sts_ccd"));
						map.put("pre_oe_prcs_uuid", result.get("oe_prcs_uuid"));
					}
				} else {
					// BYPASS 인정 안 함
					preResultYn = ObdConst.N_STR_VAL;
				}

				// 5. 이전 온보딩평가 프로세스 결과 적용
				if (ObdConst.Y_STR_VAL.equals(preResultYn)) {
					// 5-1. 이전 온보딩평가 프로세스를 참조하여 현재 프로세스의 결과 적용
					onboardingEvalRequestRepository.updateObdEvalPrcdResult(map);
					// 5-2. 잠재 업체 등록
					final String ptnlVdSlctnCrtraYn = (String)map.get("ptnl_vd_slctn_crtra_yn");
					if (StringUtils.isNotEmpty(ptnlVdSlctnCrtraYn) && ptnlVdSlctnCrtraYn.equals("Y")) {
						onboardingEvalRequestEventPublisher.updateVdOorgObdTypCcdToPtnl(map);
					}
				}
			}
		}

		// 6. 완료된 평가 설정의 최초 평가 프로세스(= 평가대상)의 평가결과를 생성한다.
		// 6-1. 평가대상 온보딩평가그룹 조회
		final Map<String, Object> obdEvalGrp = onboardingEvalRequestRepository.findObdEvalTargObdEvalGrp(param);
		if(MapUtils.isNotEmpty(obdEvalGrp)){
			// 2. 다음 프로세스의 평가 결과 생성
			ResultMap evalSubjResResultMap = this.createNextOnboardingEvalProcessEvalRes(obdEvalGrp);
			resultInfo = evalSubjResResultMap.getResultData();
			if (MapUtils.isEmpty(resultInfo) || !evalSubjResResultMap.isSuccess()) {
				return ResultMap.FAIL(resultInfo);
			}
		}

		return ResultMap.SUCCESS(resultInfo);
	}

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
		} else { // 생성할 다음 프로세스 코드가 존재
			// 2. 다음 온보딩 평가 프로세스 결과 생성
			param.put("oe_prcs_uuid", targetPrcsInfo);
			// 2-1. 결과생성 대상 다음 온보딩 평가 프로세스
			final Map<String, Object> nextOnboardingEvalProcess = onboardingEvalRequestRepository.findNextOnboardingEvalProcess(param);

			if (MapUtils.isNotEmpty(nextOnboardingEvalProcess)) {
				// 2-3. 평가대상 결과 생성을 요청한다.
				nextOnboardingEvalProcess.put("eval_req_uuid", targetPrcsInfo);
				nextOnboardingEvalProcess.put(ObdConst.EVAL_TASK_TYP_CCD, ObdConst.EVAL_TASK_TYP_OE);
				ResultMap evalSubjResResultMap = onboardingEvalRequestEventPublisher.createEvalSubjRes(nextOnboardingEvalProcess);

				if(!evalSubjResResultMap.isSuccess()){
					return ResultMap.FAIL("평가대상 결과 저장 실패 : " + evalSubjResResultMap.getResultMessage());
				} else {
					// 2-4. 평가대상 결과 아이디를 온보딩평가대상(OE_PRCS) 테이블에 저장
					Map resultData = evalSubjResResultMap.getResultData();
					resultData.put("oe_prcs_uuid", nextOnboardingEvalProcess.get("oe_prcs_uuid"));
					onboardingEvalRequestRepository.updateEvalSubjResKey(resultData);

					String createPrgsSts = resultData.get("create_prgs_sts") == null ? "" : resultData.get("create_prgs_sts").toString();  // QUANT : 정량 평가 상태(정량항목 존재, 정성항목 미존재 시), QUALI : 정성 평가 상태(정성평가 존재시))

					if("QUANT".equals(createPrgsSts)){  // 시스템 평가
						resultData.put("prcs_eval_sc", resultData.get("eval_sc"));
						resultData.put("oe_prcs_sts_ccd", "eval_prgsg"); // 온보딩평가 프로세스 상태(E007)
						// 2-5. 온보딩평가 프로세스 점수, 상태 업데이트
						obdEvalService.updateObdEvalPrcsSc(resultData);

						// 2-6. 온보딩평가 프로세스 결과처리 한다.
						return onboardingEvalMonitoringService.completeOnboardingEvalPrcs(resultData);
					}else{  // 평가자 지정 평가
						// 2-5. 평가대상 평가자 결과 생성한다.
						nextOnboardingEvalProcess.put(ObdConst.EVAL_SUBJ_RES_UUID, resultData.get(ObdConst.EVAL_SUBJ_RES_UUID));
						ResultMap evalSubjEvaltrResResultMap =  this.createOePrcsEvalSubjEvaltrRes(nextOnboardingEvalProcess);

						if(!evalSubjEvaltrResResultMap.isSuccess()) {
							return evalSubjEvaltrResResultMap;
						}else{
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
		final Map<String, Object> map = onboardingEvalRequestRepository.findActOrFailOnboardingEvalProcessCnt(param);

		//try{
			if(map == null || map.isEmpty()){
				//evExecuteService.throwSrmExceptionByErrorCode(param, "E011");
				// 24.01.24 TODO : 평가가 존재하지 않는 OE 생성 시 수정
				// 평가 프로세스가 존재하지 않는 경우
				returnVal = "NON_EVAL";
			}else if (MapUtils.isNotEmpty(map) && MapUtils.getInteger(map, "fail_cnt").equals(0) && MapUtils.getInteger(map,"act_cnt").equals(0)) {
				// 2. 불합격과 진행중인 평가가 없는경우 프로세스 대기 중인 다음 절차 UUID를 추출한다.
				returnVal = onboardingEvalRequestRepository.findNextOnboardingEvalProcessPrcsUuid(param);

				if (StringUtils.isEmpty(returnVal)) {  // 다음 온보딩 평가 프로세스 없음
					// 3. 온보딩 평가 종료에 따른 온보딩평가 평가 완료 처리
					// 진행할 프로세스가 없음으로 마감 처리를 한다.
					// 자동진행인 경우 자동마감이며 수동진행인경우 프로세스 진행을 통해 접근된다.
					onboardingEvalRequestRepository.saveOnboardingEvalComplete(param);
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
		List<Map<String, Object>> oePrcsEvalSubjEvaltrList = onboardingEvalRequestRepository.findListOePrcsEvalSubjEvaltrByOePrcsEvalSubjRes(param);

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
		ResultMap evaltrResResultMap = onboardingEvalRequestEventPublisher.createEvalSubjEvaltrRes(param);

		if(evaltrResResultMap.isSuccess()) {
			Map resultData = evaltrResResultMap.getResultData();
			resultData.put("oe_prcs_evaltr_uuid", param.get("oe_prcs_evaltr_uuid"));
			onboardingEvalRequestRepository.updateEvalSubjEvaltrResKey(resultData);
		} else {
			return ResultMap.FAIL("평가대상 평가자 결과 생성 실패 : " + evaltrResResultMap.getResultMessage());
		}

		return ResultMap.SUCCESS();
	}
}
