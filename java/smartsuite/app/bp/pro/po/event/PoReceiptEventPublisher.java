package smartsuite.app.bp.pro.po.event;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class PoReceiptEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;
	
	public void receiptReq(Map receiptItem) {
		if(receiptItem.get("req_doc_typ_ccd") == null) {
			return;
		}
		String eventName = null;
		if("RFX".equals(receiptItem.get("req_doc_typ_ccd"))) {
			eventName = "receiptReqRfx";
		} else if("CNTR".equals(receiptItem.get("req_doc_typ_ccd"))) {
			eventName = "";
		}
		if(Strings.isNullOrEmpty(eventName)) {
			return;
		}
		Map eventParam = Maps.newHashMap();
		eventParam.put("req_uuid", receiptItem.get("po_req_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventName, eventParam);
		publisher.publishEvent(event);
	}
	
	public void returnReq(Map receiptItem) {
		if(receiptItem.get("req_doc_typ_ccd") == null) {
			return;
		}
		String eventName = null;
		if("RFX".equals(receiptItem.get("req_doc_typ_ccd"))) {
			eventName = "rejectReqRfx";
		} else if("CNTR".equals(receiptItem.get("req_doc_typ_ccd"))) {
			eventName = "";
		}
		if(Strings.isNullOrEmpty(eventName)) {
			return;
		}
		Map eventParam = Maps.newHashMap();
		eventParam.put("req_uuid", receiptItem.get("po_req_uuid"));
		eventParam.put("ret_rsn", receiptItem.get("req_ret_rsn"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventName, eventParam);
		publisher.publishEvent(event);
	}
	
	public void returnUprcItemReq(List<Map<String, Object>> returnUprcItemList) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("returnUprcItemReq", returnUprcItemList);
		publisher.publishEvent(event);
	}
	
	public Map findItemByItemCd(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findItemByItemCd", param);
		publisher.publishEvent(event);
		return (Map) event.getResult();
	}
}
