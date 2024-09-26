package smartsuite.app.bp.itemMaster.master.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.itemMaster.master.service.ItemMasterService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class ItemMasterEventListener {
	
	@Inject
	ItemMasterService itemMasterService;
	
	@EventListener(condition = "#event.eventId == 'findItemByItemCd'")
	public void findItemByItemCd(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		Map item = itemMasterService.findItemByItemCd(param);
		event.setResult(item);
	}
}
