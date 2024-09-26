package smartsuite.app.bp.onboarding.result.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class OnboardingEvalResultEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;

	/**
	 * 협력사 변경 상태 조회 (findVendorModifyStatus)
	 *
	 * param param the param
	 * @return ResultMap
	 */
	public Map<String, Object> findVendorModifyStatus(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findVendorModifyStatus", param);
		publisher.publishEvent(event);
		return (Map<String, Object>) event.getResult();
	}
}