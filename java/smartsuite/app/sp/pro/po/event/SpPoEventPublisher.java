package smartsuite.app.sp.pro.po.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;

@Service
public class SpPoEventPublisher {

    @Inject
    ApplicationEventPublisher applicationEventPublisher;

    public Object findRfxQta(Map<String, Object> rfxParam) {
        if(rfxParam == null) {
            return Collections.emptyMap();
        }
        CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findRfxQta",rfxParam);
        applicationEventPublisher.publishEvent(completeEvent);
        return (Map<String, Object>) completeEvent.getResult();
    }
}
