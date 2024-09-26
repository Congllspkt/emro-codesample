package smartsuite.app.common.shoppingcart.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class ShoppingCartEventPublisher {
    @Inject
    ApplicationEventPublisher applicationEventPublisher;

    public List<Map<String, Object>> findListUprcItemWithCatalog(Map<String, Object> param) {
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListUprcItemWithCatalog",param);
        applicationEventPublisher.publishEvent(completeEvent);
        return (List<Map<String, Object>>) completeEvent.getResult();
    }


    public List<Map<String, Object>> findListUprcItemWithoutCatalog(Map param) {
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListUprcItemWithoutCatalog",param);
        applicationEventPublisher.publishEvent(completeEvent);
        return (List<Map<String, Object>>) completeEvent.getResult();
    }


    public List<Map<String, Object>> findListCateItemWithUprcCntr(Map param) {
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListCateItemWithUprcCntr",param);
        applicationEventPublisher.publishEvent(completeEvent);
        return (List<Map<String, Object>>) completeEvent.getResult();
    }

    public List<Map<String, Object>> findListPrePrItemList(Map param) {
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListPrePrItemList",param);
        applicationEventPublisher.publishEvent(completeEvent);
        return (List<Map<String, Object>>) completeEvent.getResult();
    }

    public ResultMap saveDraftPr(Map param) {
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("saveDraftPr",param);
        applicationEventPublisher.publishEvent(completeEvent);
        return (ResultMap) completeEvent.getResult();
    }

    public ResultMap directReqPr(Map param) {
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("directReqPr",param);
        applicationEventPublisher.publishEvent(completeEvent);
        return (ResultMap) completeEvent.getResult();
    }
}
