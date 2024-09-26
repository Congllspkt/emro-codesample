package smartsuite.app.bp.contract.cntrreq.event;

import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.app.shared.CntrConst;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class ContractReqEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;
	
	
	public ResultMap receiptReq(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("req_uuid", param.get("cntr_req_uuid"));

		String eventId = "";
		String reqTypCcd = (String) param.get("pre_req_typ_ccd");
		if(CntrConst.PRE_REQ_TYPE.RFX.equals(reqTypCcd)) {
			eventId = "receiptReqRfx";
		} else if(CntrConst.PRE_REQ_TYPE.ONBOARDING.equals(reqTypCcd)) {
			eventId = "receiptReqOnboardingContract";
		} else if(CntrConst.PRE_REQ_TYPE.PO.equals(reqTypCcd)) {
			eventId = "receiptCntrChgReqByPo";
		} else {
			return ResultMap.SKIP();
		}
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	public ResultMap rejectReq(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("req_uuid", param.get("cntr_req_uuid"));
		eventParam.put("ret_rsn", param.get("cntr_req_ret_rsn"));
		
		String eventId = "";
		String reqTypCcd = (String) param.get("pre_req_typ_ccd");
		if(CntrConst.PRE_REQ_TYPE.RFX.equals(reqTypCcd)) {
			eventId = "rejectReqRfx";
		} else if(CntrConst.PRE_REQ_TYPE.ONBOARDING.equals(reqTypCcd)) {
			eventId = "rejectReqOnboardingContract";
		} else if(CntrConst.PRE_REQ_TYPE.PO.equals(reqTypCcd)) {
			eventId = "rejectCntrChgReqByPo";
		} else {
			return ResultMap.SKIP();
		}
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
}