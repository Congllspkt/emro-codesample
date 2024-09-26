package smartsuite.app.bp.pro.unitprice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UnitPriceEventPublisher {

     static final Logger LOGGER = LoggerFactory.getLogger(UnitPriceEventPublisher.class);

   @Inject
   ApplicationEventPublisher applicationEventPublisher;

    public List<Map<String, Object>> findListUpcrItemByUpcrItemUuids(Map<String, Object> param) {
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findListUpcrItemByUpcrItemUuids", param);
        applicationEventPublisher.publishEvent(completeEvent);
        return (List<Map<String, Object>>) completeEvent.getResult();
    }

    public void resetSpotPrItem(Map param) {
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("resetSpotPrItem", param);
        applicationEventPublisher.publishEvent(completeEvent);
    }
}
