package smartsuite.app.bp.law.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.transaction.Transactional;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class LawEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;


	public ResultMap saveContract(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveContractByLawReview", param);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

}
