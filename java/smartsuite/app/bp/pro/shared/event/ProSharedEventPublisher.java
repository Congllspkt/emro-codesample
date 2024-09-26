package smartsuite.app.bp.pro.shared.event;

import com.google.common.collect.Lists;
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
public class ProSharedEventPublisher {
     static final Logger LOGGER = LoggerFactory.getLogger(ProSharedEventPublisher.class);

   @Inject
   ApplicationEventPublisher applicationEventPublisher;

    public List<Map<String, Object>> findListYearlyRfxItemByVendor(Map<String, Object> param) {
        List<Map<String,Object>> resultList = Lists.newArrayList();
        CustomSpringEvent completeEvent =  CustomSpringEvent.toCompleteEvent("findListYearlyRfxItemByVendor", param);
        applicationEventPublisher.publishEvent(completeEvent);
        resultList = (List<Map<String, Object>>) completeEvent.getResult();
        return resultList;
    }
}
