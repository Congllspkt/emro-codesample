package smartsuite.app.bp.vendorMaster.vendorImprove.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VendorImproveEventPublisher {
	@Inject
	ApplicationEventPublisher applicationEventPublisher;


}
