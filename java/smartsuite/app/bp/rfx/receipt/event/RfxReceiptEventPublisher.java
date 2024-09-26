package smartsuite.app.bp.rfx.receipt.event;

import com.google.common.collect.Maps;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfxReceiptEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;
	
	public ResultMap receiptReq(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("req_doc_typ_ccd") == null) {
			return ResultMap.NOT_EXISTS("req_doc_typ_ccd not exists");
		}
		
		String eventName = null;
		Map eventParam = Maps.newHashMap(param);
		if("PR".equals(eventParam.get("req_doc_typ_ccd"))) {
			eventName = "receivePr";
			eventParam.put("pr_item_uuid", param.get("req_item_uuid"));
		} else if("UPCR".equals(eventParam.get("req_doc_typ_ccd"))) {
			eventName = "receiveUpcr";
			eventParam.put("upcr_item_uuid", param.get("req_item_uuid"));
		} else if("SR".equals(eventParam.get("req_doc_typ_ccd"))) {
			eventName = "updateProgStsFromRfxReq";
            eventParam.put("sr_item_uuid", param.get("req_item_uuid"));
            eventParam.put("prog_sts_ccd", "RCPT");
		}
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventName, eventParam);
		publisher.publishEvent(event);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap returnReq(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("req_doc_typ_ccd") == null) {
			return ResultMap.NOT_EXISTS("req_doc_typ_ccd not exists");
		}
		
		String eventName = null;
		Map eventParam = Maps.newHashMap(param);
		if("PR".equals(eventParam.get("req_doc_typ_ccd"))) {
			eventName = "returnPr";
			eventParam.put("pr_item_uuid", param.get("req_item_uuid"));
			eventParam.put("pr_ret_rsn", param.get("req_ret_rsn"));
		} else if("UPCR".equals(eventParam.get("req_doc_typ_ccd"))) {
			eventName = "returnUpcr";
			eventParam.put("upcr_item_uuid", param.get("req_item_uuid"));
			eventParam.put("upcr_ret_rsn", param.get("req_ret_rsn"));
		} else if("SR".equals(eventParam.get("req_doc_typ_ccd"))) {
			eventName = "updateProgStsFromRfxReq";
            eventParam.put("sr_item_uuid", param.get("req_item_uuid"));
            eventParam.put("prog_sts_ccd", "RET");
			eventParam.put("ret_rsn", param.get("req_ret_rsn"));
		}
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventName, eventParam);
		publisher.publishEvent(event);
		return ResultMap.SUCCESS();
	}
	
	public List<Map> findListUnitPriceByItemAndOorg(Map item) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("item_cd", item.get("item_cd"));
		eventParam.put("oorg_cd", item.get("oorg_cd"));
		eventParam.put("item_oorg_cd", item.get("item_oorg_cd"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListUnitPriceByItemAndOorg", eventParam);
		publisher.publishEvent(event);
		return (List<Map>) event.getResult();
	}
	
	public List findListUnitPriceQtaInfoByItemAndOorg(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListUnitPriceQtaInfoByItemAndOorg", param);
		publisher.publishEvent(event);
		return (List<Map>) event.getResult();
	}
	
	/**
	 * 현재 시점 단가계약이 존재하여 견적 스킵하고 바로 발주 요청
	 *
	 * @param requestPoByUprcItemList
	 */
	public void requestPoByUprcItem(List<Map> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("requestPoByUprcItem", param);
		publisher.publishEvent(event);
	}
	
	public ResultMap validateDeleteRequestReqInfoByRfxChangeReq(Map reqData) {
		if(reqData == null) {
			return ResultMap.NOT_EXISTS();
		}
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("validateDeleteRequestReqInfoByRfxChangeReq", reqData);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	public ResultMap deleteRequestReqInfoByRfxChangeReq(Map reqData) {
		if(reqData == null) {
			return ResultMap.NOT_EXISTS();
		}
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteRequestReqInfoByRfxChangeReq", reqData);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	public ResultMap recoveryRequestReqInfoByRfxChangeReq(Map reqData) {
		if(reqData == null) {
			return ResultMap.NOT_EXISTS();
		}
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("recoveryRequestReqInfoByRfxChangeReq", reqData);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

    public List<Map<String, Object>> requestCopyPrsToUpcrs(List<Map<String, Object>> prItems) {
		if(prItems == null) {
			return null;
		}
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("requestCopyPrsToUpcrs", prItems);
		publisher.publishEvent(event);
		return (List<Map<String, Object>>) event.getResult();
	}

	public List<Map<String, Object>> findListPrItemByPrItemUuids(Map<String, Object> copyPrParam) {
		if(copyPrParam == null) {
			return null;
		}
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListPrItemByPrItemUuids", copyPrParam);
		publisher.publishEvent(event);
		return (List<Map<String, Object>>) event.getResult();
	}
}
