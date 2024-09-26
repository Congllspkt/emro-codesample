package smartsuite.app.bp.edoc.estamptax.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.edoc.estamptax.service.EstampTaxService;

import javax.inject.Inject;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class EStampTaxEventListener {

	@Inject
	EstampTaxService stampTaxService;
}
