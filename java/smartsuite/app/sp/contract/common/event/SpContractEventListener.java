package smartsuite.app.sp.contract.common.event;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.sp.contract.common.service.SpContractService;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpContractEventListener {
	
	@Inject
	SpContractService spContractService;
	
	
}
