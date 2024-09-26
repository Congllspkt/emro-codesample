package smartsuite.app.bp.contract.cntrreq.event;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.contract.cntrreq.service.ContractReqService;
import smartsuite.app.bp.contract.data.ContractReq;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class ContractReqEventListener {
	
	@Inject
	ContractReqService contractReqService;
	
	@EventListener(condition = "#event.eventId == 'requestContract'")
	public void requestContract(CustomSpringEvent event) {
		ContractReq contractReq = (ContractReq) event.getData();
		ResultMap resultMap = contractReqService.requestContract(contractReq);
		event.setResult(resultMap);
	}
}
