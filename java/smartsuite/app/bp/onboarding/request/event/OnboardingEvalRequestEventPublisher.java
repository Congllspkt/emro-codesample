package smartsuite.app.bp.onboarding.request.event;

import com.google.common.collect.Maps;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.ObdConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class OnboardingEvalRequestEventPublisher {

	@Inject
	ApplicationEventPublisher publisher;

	/**
	 *
	 * @param param 잠재 업체 등록 요청 대상 vendor
	 */
	public ResultMap updateVdOorgObdTypCcdToPtnl(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("updateVdOorgObdTypCcdToPtnl", param);
		publisher.publishEvent(customSpringEvent);
		return (ResultMap) customSpringEvent.getResult();
	}

	/**
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_task_typ_ccd - 업무유형 공통코드
	 * param.oorg_cd - 운영조직 코드
	 * param.evamltmpl_uuid - 평가템플릿 아이디
	**/
	public ResultMap createEvalSubjRes(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("createEvalSubjRes",param);
		publisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	/**
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_task_typ_ccd - 업무유형 공통코드
	 * param.oorg_cd - 운영조직 코드
	 * param.evamltmpl_uuid - 평가템플릿 아이디
	**/
	public ResultMap createEvalSubjEvaltrRes(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("createEvalSubjEvaltrRes",param);
		publisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	/** 평가대상 평가자 결과를 삭제한다.
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_subj_res_uuid - 평가 대상 결과 아이디 <br>
	 * param.evalfact_evaltr_authty_ccd - 평가항목 평가자 권한 공통코드 <br>
	 * param.evaltr_id - 평가자 아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가대상 평가자 결과 아이디 <br>
	 **/
	public ResultMap deleteEvalSubjEvaltrRes(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		param = this.convertDefaultEvalParamMap(param);
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("deleteEvalSubjEvaltrRes",param);
		publisher.publishEvent(completeEvent);
		return (ResultMap) completeEvent.getResult();
	}

	private Map convertDefaultEvalParamMap(Map param) {

		Map<String, Object> defaultParam = Maps.newHashMap(param);
		defaultParam.put(ObdConst.TEN_ID, Auth.getCurrentTenantId());
		defaultParam.put(ObdConst.EVAL_REQ_UUID, param.getOrDefault(ObdConst.OE_PRCS_UUID, ""));
		defaultParam.put(ObdConst.EVAL_TASK_TYP_CCD, ObdConst.EVAL_TASK_TYP_OE);

		return defaultParam;
	}
}