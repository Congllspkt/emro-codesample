package smartsuite.app.bp.rfx.rfx.event;

import com.google.common.collect.Lists;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.service.RfxEvalService;
import smartsuite.app.bp.rfx.rfx.service.RfxService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.app.common.util.ReferenceDocumentLinkUtils;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxEventListener {
	
	@Inject
	RfxService rfxService;
	
	@Inject
	RfxEvalService rfxEvalService;
	
	@Inject
	RfxStatusService rfxStatusService;
	
	@EventListener(condition = "#event.eventId == 'findRfx'")
	public void findRfx(CustomSpringEvent event) {
		Map rfxInfo = rfxService.findRfx((Map) event.getData());
		event.setResult(rfxInfo);
	}
	
	@EventListener(condition = "#event.eventId == 'closeRfxItemResult'")
	public void closeRfxItemResult(CustomSpringEvent event) {
		rfxStatusService.closeRfxItemResult((Map) event.getData());
	}
	
	@EventListener(condition = "#event.eventId == 'findListReferenceDocIdsFromRFX'")
    public void findListReferenceDocIdsFromRFX(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();
        List<Map<String,Object>> searchList = rfxStatusService.findListReferenceIds(param);
        event.setResult(ReferenceDocumentLinkUtils.findListReferenceDocIds(searchList));
     }

	 @EventListener(condition = "#event.eventId == 'findListReferenceDocFromRFX'")
    public void findListReferenceDocFromRFX(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();

        String appType = (String) param.get("appType");

         if("RFX".equals(appType)) {
             result = rfxStatusService.findListReferenceDocFromRFX(param);
		 }else if("UPCR".equals(appType)) {
             result = rfxStatusService.findListReferenceDocFromRFXByUPCR(param);
         }else if("PR".equals(appType)) {
             result = rfxStatusService.findListReferenceDocFromRFXByPR(param);
         }else{
             result = rfxStatusService.findListReferenceDocFromRFXByRfxItemUuids(param);
         }
         event.setResult(result);
    }

}
