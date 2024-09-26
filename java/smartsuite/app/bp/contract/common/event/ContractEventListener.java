package smartsuite.app.bp.contract.common.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.bp.contract.common.service.ContractSharedService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class ContractEventListener {
	
	@Inject
	ContractService contractService;

	@Inject
	ContractSharedService contractSharedService;

	@EventListener(condition = "#event.eventId == 'saveContractByLawReview'")
	public void saveContractByLawReview(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(contractService.saveContractByLawReview(param));
	}

	@EventListener(condition = "#event.eventId == 'findContractTypeCount'")
	public void findContractTypeCount(CustomSpringEvent event) {
        Map param = (Map) event.getData();
        event.setResult(contractSharedService.findContractTypeCount(param));
    }

	@EventListener(condition = "#event.eventId == 'findNonStandardContractPercent'")
	public void findNonStandardContractPercent(CustomSpringEvent event) {
        event.setResult(contractSharedService.findNonStandardContractPercent());
    }
	@EventListener(condition = "#event.eventId == 'findContractExpirationNotification'")
	public void findContractExpirationNotification(CustomSpringEvent event) {
        event.setResult(contractSharedService.findContractExpirationNotification());
    }
}
