package smartsuite.app.sp.contract.common.event;

import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpContractEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;

	public Map findEcontract(Map param) {
		String eventId = "findSpEcontract";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (Map) event.getResult();
	}

	public ResultMap rejectEcontract(Map param) {
		String eventId = "rejectEcontract";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	public ResultMap findDocusignEnvelope(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("dsgn_uuid", param.get("dsgn_uuid"));
		eventParam.put("return_ui_id", param.get("return_ui_id"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findSpDocusignEnvelope", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	public ResultMap rejectAdobeSign(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("asgn_uuid", param.get("asgn_uuid"));
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		eventParam.put("agreement_id", param.get("agreement_id"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("rejectAdobeSign", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	public ResultMap getSpAdobeSignUrlInfo(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("asgn_uuid", param.get("asgn_uuid"));
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		eventParam.put("agreement_id", param.get("agreement_id"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("getSpAdobeSignUrlInfo", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	public ResultMap checkAdobeSignStatus(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("agreement_id", param.get("agreement_id"));
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		eventParam.put("popup_type", param.get("popup_type"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("checkAdobeSignStatus", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

}