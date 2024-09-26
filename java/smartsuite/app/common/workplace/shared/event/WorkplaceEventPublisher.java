package smartsuite.app.common.workplace.shared.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.event.CustomSpringEvent;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WorkplaceEventPublisher {
    @Autowired
    ApplicationEventPublisher publisher;


    public Map findReturnedPoCount(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findReturnedPoCount", param);
		publisher.publishEvent(event);
		Map<String,Object> result = event.getResult() == null? Maps.newHashMap() : (Map<String,Object>) event.getResult();
		return result;
	}
    public Map findPoTypeCount(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findPoTypeCount", param);
		publisher.publishEvent(event);
		Map<String,Object> result = event.getResult() == null? Maps.newHashMap() : (Map<String,Object>) event.getResult();
		return result;
	}

public List findListWorkplaceDashboardPoData(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListWorkplaceDashboardPoData", param);
		publisher.publishEvent(event);
		List<Map<String,Object>> resultList = event.getResult() == null? Lists.newArrayList() : (List) event.getResult();
		return resultList;
	}

    public List findUnitPriceAdequacyCount(Map<String, Object> param) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findUnitPriceAdequacyCount", param);
        publisher.publishEvent(event);
		List<Map<String,Object>> resultList = event.getResult() == null? Lists.newArrayList() : (List) event.getResult();
        return resultList;
    }


    public List findListWorkplaceDashboardUnitPrice(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListWorkplaceDashboardUnitPrice", param);
		publisher.publishEvent(event);
		List<Map<String,Object>> resultList = event.getResult() == null? Lists.newArrayList() : (List) event.getResult();
		return resultList;
	}

    public Map findContractTypeCount(Map<String, Object> param) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findContractTypeCount", param);
		publisher.publishEvent(event);
		Map<String,Object> result = event.getResult() == null? Maps.newHashMap() : (Map<String,Object>) event.getResult();
		return result;
    }

	public Map findNonStandardContractPercent(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findNonStandardContractPercent", param);
		publisher.publishEvent(event);
		Map<String,Object> result = event.getResult() == null? Maps.newHashMap() : (Map<String,Object>) event.getResult();
		return result;
	}

	public Map findContractExpirationNotification(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findContractExpirationNotification", param);
		publisher.publishEvent(event);
		Map<String,Object> result = event.getResult() == null? Maps.newHashMap() : (Map<String,Object>) event.getResult();
		return result;
	}
}
