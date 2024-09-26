package smartsuite.app.bp.rfx.rfi.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfiEventPublisher {

	@Inject
	ApplicationEventPublisher publisher;

	public ResultMap validateCheckPrStatus(List prItemIds) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("validateCreateRfxByPr", prItemIds);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	public ResultMap validateCheckUpcrStatus(List upcrItemIds) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("validateCreateRfxByUpcr", upcrItemIds);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
}
