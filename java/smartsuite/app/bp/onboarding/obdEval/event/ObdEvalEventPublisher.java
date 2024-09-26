package smartsuite.app.bp.onboarding.obdEval.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.ObdConst;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class ObdEvalEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;

	/**
	 * 평가수행용 평가항목 조회
	 *
	 * param.evamltmpl_uuid - 평가템플릿 아이디
	 * @return ResultMap
	 */
	public ResultMap findOnboardingEvalfactFulfillInfo(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findEvalfactFulfillInfo", param);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 평가수행 정보 저장
	 *
	 * param.ten_id: ten_id,
	 * param.evalSubjMap: evalSubjectInfo,
	 * param.saveEvaltmplList: evalTemplateList,
	 * param.saveEvalfactList: saveFactList,
	 * param.saveEvalfactScaleList: selectedScaleList
	 * @return ResultMap
	 */
	public ResultMap saveOnboardingEvalFulfillment(Map param) {
		param.put(ObdConst.TEN_ID, Auth.getCurrentTenantId());
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveEvaltrEvalfactRes", param);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 평가요청 건에 대한 평가 대상 점수를 계산한다. (계산항목 집계, 평가항목 점수, 평가항목군 점수 재계산한 후 평가대상 점수 산출)
	 *
	 * param.ten_id
	 * param.eval_req_uuid
	 * @return ResultMap
	 */
	public ResultMap calculateEvalSubjResByEvalReqWithCalcfact(Map param) {
		param.put(ObdConst.TEN_ID, Auth.getCurrentTenantId());
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("calculateEvalSubjResByEvalReqWithCalcfact", param);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
}