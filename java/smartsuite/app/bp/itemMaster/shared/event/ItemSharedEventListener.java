package smartsuite.app.bp.itemMaster.shared.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.itemMaster.shared.service.ItemSharedService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemSharedEventListener {
	
	@Inject
	ItemSharedService itemSharedService;
	
	/**
	 * 품목 조회
	 *
	 * @param event
	 *
	 *
	 */
	@EventListener(condition = "#event.eventId == 'findListCateItemWithUprcCntr'")
	public void findListCateItemWithUprcCntr(CustomSpringEvent event) throws Exception {
		List<Map> resultList = itemSharedService.findListCateItemWithUprcCntr((Map) event.getData());
		event.setResult(resultList);
	}
}
