package smartsuite.app.bp.pro.gr.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class GrEvalSetupEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;
	
	public ResultMap saveEvalTmplInfo(Map evalTmplInfo) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveEvalTmplInfo", evalTmplInfo);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	public void updateCnfdYnEvalTmpl(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("updateCnfdYnEvalTmpl", param);
		publisher.publishEvent(event);
	}
}