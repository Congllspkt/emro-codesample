package smartsuite.app.common.status.event;

import com.google.common.collect.Lists;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import smartsuite.app.common.status.repository.*;
import smartsuite.app.common.util.ReferenceDocumentLinkUtils;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProStatusListener {

    @Inject
    AsnStatusRepository asnStatusRepository;

    @Inject
    PoStatusRepository poStatusRepository;

    @Inject
    GrStatusRepository grStatusRepository;

    @Inject
    InvStatusRepository invStatusRepository;

    @Inject
    UnitPriceContractStatusRepository unitPriceContractStatusRepository;

    @Inject
    PrStatusRepository prStatusRepository;

    @Inject
    UpcrStatusRepository upcrStatusRepository;

    @Inject
    ProStatusPublisher proSharedPublisher;

 /*   @EventListener(condition = "#event.eventId == 'findListReferenceDocFromCONTRACT'")
    public void findListReferenceDocFromCONTRACT(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();

        String appType = (String) param.get("appType");

         if("CONTRACT".equals(appType)){
             result = unitPriceContractStatusRepository.findListReferenceDocFromContractByContract(param);
         }else if("RFX".equals(appType)) {
             result = unitPriceContractStatusRepository.findListReferenceDocFromContractByRfxItemIds(param);
         }else if("PR".equals(appType)) {
             result = unitPriceContractStatusRepository.findListReferenceDocFromContractByPRItemIds(param);
         }else{
             result = unitPriceContractStatusRepository.findListReferenceDocFromContractByUprccntrItemIds(param);
         }
         event.setResult(result);
    }*/

    @EventListener(condition = "#event.eventId == 'findListReferenceDocFromPR'")
    public void findListReferenceDocFromPR(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();

         if("PR".equals(param.get("appType"))){
             result = prStatusRepository.findListReferenceDocFromPR(param);
             event.setResult(result);
         }else{
             result = prStatusRepository.findListReferenceDocFromPRByPrItemIds(param);
         }
         event.setResult(result);
    }

    @EventListener(condition = "#event.eventId == 'findListReferenceDocFromUPCR'")
    public void findListReferenceDocFromUPCR(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();

         if("UPCR".equals(param.get("appType"))){
             result = upcrStatusRepository.findListReferenceDocFromUpcr(param);
             event.setResult(result);
         }else{
             result = upcrStatusRepository.findListReferenceDocFromUPCRByUpcrItemIds(param);
         }
         event.setResult(result);
    }

    @EventListener(condition = "#event.eventId == 'findListReferenceDocFromPO'")
    public void findListReferenceDocFromPO(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();

        String appType = (String) param.get("appType");

         if("PO".equals(appType)){
             result = poStatusRepository.findListReferenceDocFromPO(param);
         }else if("RFX".equals(appType)) {
                 //event
                 result = poStatusRepository.findListReferenceDocFromPOByRfxItemIds(param);
         }else if("PR".equals(appType)) {
                //event
                result = poStatusRepository.findListReferenceDocFromPOByPrItemIds(param);
         }else{
                 //event
                result = poStatusRepository.findListReferenceDocFromPOByPoItemIds(param);
         }
         event.setResult(result);
    }

    @EventListener(condition = "#event.eventId == 'findListReferenceDocFromGR'")
    public void findListReferenceDocFromGR(CustomSpringEvent event) {
        List<Map<String, Object>> result = Lists.newArrayList();
        Map<String, Object> param = (Map<String, Object>) event.getData();

        String appType = (String) param.get("appType");
        if("GR".equals(appType)){
            result = grStatusRepository.findListReferenceDocFromGR(param);
        }else if("PR".equals(appType)) {
            result = grStatusRepository.findListReferenceDocFromGRByPrItemIds(param);
         }else if("RFX".equals(appType)) {
            //rfx_item_uuid -> po_item_uuids
             if(param.containsKey("rfx_item_uuids") && param.get("rfx_item_uuids") != null) {
                 List<Map<String,Object>> poItemUuids =  proSharedPublisher.findListPoByRfxItemIds(param);
                 if(poItemUuids != null && !CollectionUtils.isEmpty(poItemUuids)){
                     param.putAll(ReferenceDocumentLinkUtils.findListReferenceDocIds(poItemUuids));
                     result = grStatusRepository.findListReferenceDocFromGRByPoItemIds(param);
                 }
             }
         }else if("PO".equals(appType)) {
                result = grStatusRepository.findListReferenceDocFromGRByPoItemIds(param);
         }else if("ASN".equals(appType)){
                result = grStatusRepository.findListReferenceDocFromGRByAsnItemIds(param);
         }
         event.setResult(result);
    }

    @EventListener(condition = "#event.eventId == 'findListReferenceDocIdsFromPR'")
    public void findListReferenceDocIdsFromPR(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();
        List<Map<String,Object>> searchList = prStatusRepository.findListReferenceIds(param);
        event.setResult(ReferenceDocumentLinkUtils.findListReferenceDocIds(searchList));
     }

     @EventListener(condition = "#event.eventId == 'findListReferenceDocIdsFromUPCR'")
    public void findListReferenceDocIdsFromUPCR(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();
        List<Map<String,Object>> searchList = upcrStatusRepository.findListReferenceIds(param);
        event.setResult(ReferenceDocumentLinkUtils.findListReferenceDocIds(searchList));
     }

     @EventListener(condition = "#event.eventId == 'findListReferenceDocIdsFromPO'")
    public void findListReferenceDocIdsFromPO(CustomSpringEvent event){
        Map<String,Object> param = (Map<String, Object>) event.getData();
        List<Map<String,Object>> searchList = poStatusRepository.findListReferenceIdsFromPO(param);
        event.setResult(ReferenceDocumentLinkUtils.findListReferenceDocIds(searchList));
     }

     @EventListener(condition = "#event.eventId == 'findListReferenceDocIdsFromCONTRACT'")
    public void findListReferenceDocIdsFromCONTRACT(CustomSpringEvent event){
        Map<String,Object> param = (Map<String, Object>) event.getData();
        List<Map<String,Object>> searchList = unitPriceContractStatusRepository.findListReferenceDocIdsFromCONTRACT(param);
        event.setResult(ReferenceDocumentLinkUtils.findListReferenceDocIds(searchList));
     }

     @EventListener(condition = "#event.eventId == 'findListReferenceDocIdsFromGR'")
    public void findListReferenceDocIdsFromGR(CustomSpringEvent event){
        Map<String,Object> param = (Map<String, Object>) event.getData();
        List<Map<String,Object>> searchList = grStatusRepository.findListReferenceDocIdsFromGR(param);
        event.setResult(ReferenceDocumentLinkUtils.findListReferenceDocIds(searchList));
     }
}
