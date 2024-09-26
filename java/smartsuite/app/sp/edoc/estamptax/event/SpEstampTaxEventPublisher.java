package smartsuite.app.sp.edoc.estamptax.event;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.event.CustomSpringEvent;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpEstampTaxEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;

	/**
	 * 인지세 납부 이력 조회
	 * @param param
	 * @return List
	 */

	public List findListEStampTaxPayHistory(Map param) {
		String eventId = "findSpListStampTaxPayHistory";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));
		eventParam.put("cntrr_typ_ccd", param.get("cntrr_typ_ccd"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);

		return (List) event.getResult();
	}
}