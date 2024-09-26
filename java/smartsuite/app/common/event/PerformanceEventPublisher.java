package smartsuite.app.common.event;

import com.google.common.collect.Maps;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.PfmcConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PerformanceEventPublisher {

	@Inject
	ApplicationEventPublisher applicationEventPublisher;

	/**
	 * 평가대상 결과를 삭제한다.
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_subj_res_uuid - 평가대상 결과 아이디 <br>
	 * @return the ResultMap
	 * @Date : 2023. 6. 18
	 * @Method Name : deleteEvalSubjRes
	 **/
	public ResultMap deleteEvalSubjRes(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		param = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("deleteEvalSubjRes",param);
		applicationEventPublisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	/**
	 * 여러 건의 평가대상 결과를 생성한다.
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_req_uuid - 평가요청 아이디 <br>
	 * param.evalSubjList - 평가대상 목록 <br>
	 * @return the ResultMap
	 * @Date : 2023. 6. 18
	 * @Method Name : createEvalSubjResEvalReq
	**/
	public ResultMap createEvalSubjResEvalReq(Map<String, Object> param) {
		if(param == null) {
			return ResultMap.FAIL();
		}
		param = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("createEvalSubjResEvalReq",param);
		applicationEventPublisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	/**
	 * 1건의 평가대상 결과를 생성한다.
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_task_typ_ccd - 업무유형 공통코드 <br>
	 * param.oorg_cd - 운영조직 코드 <br>
	 * param.evamltmpl_uuid - 평가템플릿 아이디 <br>
	 * @return the ResultMap
	 * @Date : 2023. 6. 18
	 * @Method Name : createEvalSubjResAdd
	**/
	public ResultMap createEvalSubjResAdd(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		param = this.convertDefaultEvalParamMap(param);

		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("createEvalSubjResAdd",param);
		applicationEventPublisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	/**
	 * 평가대상 평가자 결과를 생성한다.
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_subj_res_uuid - 평가 대상 결과 아이디 <br>
	 * param.evalfact_evaltr_authty_ccd - 평가항목 평가자 권한 공통코드 <br>
	 * param.evaltr_id - 평가자 아이디 <br>
	 * @return the ResultMap
	 * @Date : 2023. 6. 18
	 * @Method Name : createEvalSubjEvaltrRes
	**/
	public ResultMap createEvalSubjEvaltrRes(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		param = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("createEvalSubjEvaltrRes",param);
		applicationEventPublisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	/** 평가대상 평가자 결과를 삭제한다.
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_subj_res_uuid - 평가 대상 결과 아이디 <br>
	 * param.evalfact_evaltr_authty_ccd - 평가항목 평가자 권한 공통코드 <br>
	 * param.evaltr_id - 평가자 아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가대상 평가자 결과 아이디 <br>
	 * @return the ResultMap
	 * @Date : 2023. 6. 23
	 * @Method Name : deleteEvalSubjEvaltrRes
	 **/
	public ResultMap deleteEvalSubjEvaltrRes(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		param = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("deleteEvalSubjEvaltrRes",param);
		applicationEventPublisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	/** 평가대상 평가자 결과의 평가자아이디를 변경한다.
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가 대상 평가자 결과 아이디 <br>
	 * param.evaltr_id - 변경할 평가자 아이디 <br>
	 * @return the ResultMap
	 * @Date : 2023. 7. 2
	 * @Method Name : changeEvalSubjEvaltrRes
	 **/
	public ResultMap changeEvalSubjEvaltrRes(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		param = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("changeEvalSubjEvaltrRes",param);
		applicationEventPublisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	/** 평가 요청 아이디로 평가 결과를 모두 삭제 요청한다.
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_req_uuid - 평가 요청 아이디 (pe_uuid) <br>
	 * @return the ResultMap
	 * @Date : 2023. 7. 6
	 * @Method Name : deleteEvalByEvalReqUuid
	 **/
	public ResultMap deleteEvalByEvalReqUuid(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		param = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("deleteEvalByEvalReqUuid",param);
		applicationEventPublisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	/**
	 * 정성평가 진행상태 "제출" 상태를 취소한다.
	 * @author : hj.jang
	 * @param param the param
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가 대상 평가자 결과 아이디 (pe_uuid) <br>
	 * @return the ResultMap
	 * @Date : 2023. 7. 6
	 * @Method Name : updateCancleSubmEvaltrPrgsSts
	 *
	 **/
	public ResultMap updateCancleSubmEvaltrPrgsSts(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		param = this.convertDefaultEvalParamMap(param);

		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("updateCancleSubmEvaltrPrgsSts",param);
		applicationEventPublisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	/**
	 * 평가대상 평가항목 결과 조회
	 * <b>Required : </b>
	 * param.ten_id: ten_id,
	 * param.eval_req_uuid
	 * @return List
	 */
	public List findListEvalSubjEvalfactRes(Map param) {
		param = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListEvalSubjEvalfactRes", param);
		applicationEventPublisher.publishEvent(event);
		return (List) event.getResult();
	}

	/**
	 * 평가대상 평가항목 결과 조회
	 * <b>Required : </b>
	 * param.ten_id: ten_id,
	 * param.eval_req_uuid : pe_uuid
	 * @return List
	 */
	public List findListEvalSubjCalcfactRes(Map param) {
		Map<String, Object> evalReqParam = this.convertDefaultEvalParamMap(param);

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListEvalSubjCalcfactRes", evalReqParam);
		applicationEventPublisher.publishEvent(event);
		return (List) event.getResult();
	}

	/**
	 * 평가대상 평가항목 결과 조회
	 * <b>Required : </b>
	 * param.ten_id: ten_id,
	 * param.eval_req_uuid : pe_uuid
	 * @return List
	 */
	public List findListEvalSubjRes(Map param) {
		Map<String, Object> evalReqParam = this.convertDefaultEvalParamMap(param);

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListEvalSubjRes", evalReqParam);
		applicationEventPublisher.publishEvent(event);
		return (List) event.getResult();
	}

/**
	 * 퍼포먼스 평가대상 계산항목 조정점수 수정 요청
	 * <b>Required : </b>
	 * param.ten_id: ten_id,
	 * param.eval_req_uuid : pe_uuid
	 * param.eval_subj_calcfact_res_uuid
	 * @return List
	 */
	public ResultMap updateEvalSubjCalcfactByAdj(Map param) {
		Map<String, Object> calcfactParam = this.convertDefaultEvalParamMap(param);

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("updateEvalSubjCalcfactByAdj", calcfactParam);
		applicationEventPublisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 평가요청 건에 대한 평가 대상 점수를 계산한다. (계산항목 집계하지 않고 평가 항목 점수, 평가항목군 점수 재계산한 후 평가대상 점수 산출)
	 *
	 * param.ten_id
	 * param.eval_req_uuid
	 * @return ResultMap
	 */
	public ResultMap reCalculateQuantEvalfact(Map param) {
		Map<String, Object> calcfactParam = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("reCalculateQuantEvalfact", calcfactParam);
		applicationEventPublisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 평가요청 건에 대한 평가 대상 점수를 계산한다. (계산항목 집계하지 않고 평가 항목 점수, 평가항목군 점수 재계산한 후 평가대상 점수 산출)
	 *
	 * param.ten_id
	 * param.eval_req_uuid
	 * @return ResultMap
	 */
	public ResultMap reCalculateCalcfactVal(Map param) {
		Map<String, Object> calcfactParam = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("reCalculateCalcfactVal", calcfactParam);
		applicationEventPublisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 퍼포먼스 평가 대상 결과의 개선관리 등록 데이터를 요청한다.
	 *
	 * param.ten_id
	 * param.pe_subj_res_uuid
	 * @return ResultMap
	 */
	public List findListPeSubjVdImprove(Map param) {
		Map<String, Object> vdImproveParam = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListPeSubjVdImprove", vdImproveParam);
		applicationEventPublisher.publishEvent(event);
		return (List) event.getResult();
	}

	/**
	 * 퍼포먼스 평가 대상 결과의 개선관리 등록 데이터를 요청한다.
	 *
	 * param.ten_id
	 * param.pe_subj_res_uuid
	 * @return ResultMap
	 */
	public void createPeSubjVdImprove(Map param) {
		Map<String, Object> vdImproveParam = this.convertDefaultEvalParamMap(param);
		vdImproveParam.put("p_improv_reqr_id", Auth.getCurrentUser().getUsername());
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("createPeSubjVdImprove", vdImproveParam);
		applicationEventPublisher.publishEvent(event);
	}

	private Map convertDefaultEvalParamMap(Map param) {

		Map<String, Object> defaultParam = Maps.newHashMap(param);
		defaultParam.put(PfmcConst.TEN_ID, Auth.getCurrentTenantId());
		defaultParam.put(PfmcConst.EVAL_REQ_UUID, param.getOrDefault(PfmcConst.PE_UUID, ""));
		defaultParam.put(PfmcConst.EVAL_TASK_TYP_CCD, PfmcConst.EVAL_TASK_TYP_PE);

		return defaultParam;
	}

	private Map convertDefaultVendorMasterParamMap(Map param) {

		Map<String, Object> defaultParam = Maps.newHashMap(param);
		defaultParam.put(PfmcConst.TEN_ID, Auth.getCurrentTenantId());
		defaultParam.put(PfmcConst.EVAL_TASK_TYP_CCD, PfmcConst.EVAL_TASK_TYP_PE);

		return defaultParam;
	}

}
