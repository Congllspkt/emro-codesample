package smartsuite.app.bp.itemMaster.itemcat.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.event.CustomSpringEvent;

import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class ItemCatEventPublisher {

	@Autowired
	ApplicationEventPublisher publisher;

	/**
	 * Itemcat_iattr 삭제 요청
	 *
	 * @param
	 * @return void
	 */
	public void deleteInfoItemcatIattrFromItemcat(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteInfoItemcatIattrFromItemcat", param);
		publisher.publishEvent(event);
	}
}
