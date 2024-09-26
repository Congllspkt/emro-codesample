package smartsuite.app.sp.edoc.estamptax.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.sp.edoc.estamptax.SpEstampTaxService;

import javax.inject.Inject;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpEstampTaxEventListener {

	@Inject
	SpEstampTaxService stampTaxService;
}
