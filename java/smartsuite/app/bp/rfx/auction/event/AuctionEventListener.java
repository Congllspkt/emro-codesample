package smartsuite.app.bp.rfx.auction.event;

import com.google.common.collect.Lists;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.auction.service.AuctionService;
import smartsuite.app.common.util.ReferenceDocumentLinkUtils;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AuctionEventListener {

    @Inject
    AuctionService auctionService;

    @EventListener(condition = "#event.eventId == 'findListReferenceDocIdsFromAUCTION'")
    public void findListReferenceDocIdsFromAUCTION(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();
        List<Map<String,Object>> searchList = auctionService.findListAuctionReferenceIds(param);
        event.setResult(ReferenceDocumentLinkUtils.findListReferenceDocIds(searchList));
     }

	 @EventListener(condition = "#event.eventId == 'findListReferenceDocFromAUCTION'")
    public void findListReferenceDocFromAUCTION(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();

        String appType = (String) param.get("appType");

         if("AUCTION".equals(appType)){
             result = auctionService.findListReferenceDocFromAUCTION(param);
		 }else if("UPCR".equals(appType)) {
             result = auctionService.findListReferenceDocFromAUCTIONByUPCR(param);
         }else if("PR".equals(appType)) {
             result = auctionService.findListReferenceDocFromAUCTIONByPR(param);
         }else{
             result = auctionService.findListReferenceDocFromAUCTIONByRfxItemUuids(param);
         }
         event.setResult(result);
    }
}
