package smartsuite.app.bp.rfx.rfx.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfxNxtPrcsEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;
	
	public void requestPo(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("requestPo", eventParam);
		publisher.publishEvent(event);
	}
	
	public void createPo(Map poParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("createPo", poParam);
		publisher.publishEvent(event);
	}
	
	public void createPoPr(Map poParam) {
		CustomSpringEvent completeEvent =  CustomSpringEvent.toCompleteEvent("createPoPr", poParam);
		publisher.publishEvent(completeEvent);
	}
	
	public void createDraftPoPr(Map poParam) {
		CustomSpringEvent completeEvent =  CustomSpringEvent.toCompleteEvent("createDraftPoPr", poParam);
		publisher.publishEvent(completeEvent);
	}
}
