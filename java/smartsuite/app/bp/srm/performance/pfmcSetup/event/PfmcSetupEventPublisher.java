package smartsuite.app.bp.srm.performance.pfmcSetup.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class PfmcSetupEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;

	/**
	 * 저장할 평가 템플릿을 타 모듈에 알린다.
	 * 
	 * @param evalTmplInfo - 저장할 평가 템플릿 및 fact 정보
	 * @return void
	 */
	public ResultMap saveEvalTmplInfo(Map evalTmplParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveEvalTmplInfo", evalTmplParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 저장할 평가 템플릿을 타 모듈에 알린다.
	 *
	 * @param pfmcEvalshtInfo - 확정중인 퍼포번스 평가시트정보 (evaltmpl_uuid, cnfd_yn 필수)
	 * @return void
	 */
	public void updateCnfdYnEvalTmpl(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("updateCnfdYnEvalTmpl", param);
		publisher.publishEvent(event);
	}

	/**
	 * PEG 대상 협력사관리그룹의 평가자 조회를 요청한다.
	 *
	 * @param
	 * @return the vmg uuid list
	 */
	public List findListVendorManagementGroupEvaltrForView(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListVendorManagementGroupEvaltrForView", param);
		publisher.publishEvent(event);
		return (List) event.getResult();
	}

	/**
	 * 특정 퍼포먼스평가그룹 협력사관리그룹으로 퍼포먼스평가 통보 전 임시저장 데이터 조회를 요청한다.
	 *
	 * @param
	 * @return the vmg uuid list
	 */
	public List findPeInfoBeforeCrngVmg(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findPeInfoBeforeCrngVmg", param);
		publisher.publishEvent(event);
		return (List) event.getResult();
	}
}