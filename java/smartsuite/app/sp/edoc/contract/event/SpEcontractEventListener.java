package smartsuite.app.sp.edoc.contract.event;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.event.CustomSpringEvent;
import smartsuite.app.sp.edoc.contract.SpEcontractService;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpEcontractEventListener {

	@Inject
	SpEcontractService spEcontractService;
	
	
	@EventListener(condition = "#event.eventId == 'findSpEcontract'")
	public void findSpEcontract(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(spEcontractService.findEcontract(param));
	}
	
	@EventListener(condition = "#event.eventId == 'rejectEcontract'")
	public void rejectEcontract(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		String cntrUUID = (String)param.get("cntr_uuid");
		event.setResult(spEcontractService.rejectEcontract(cntrUUID));
	}
	
}
