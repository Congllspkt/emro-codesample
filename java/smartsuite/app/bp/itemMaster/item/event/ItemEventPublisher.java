package smartsuite.app.bp.itemMaster.item.event;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class ItemEventPublisher {

	@Autowired
	ApplicationEventPublisher publisher;

	/**
	 * 품목현황에서 넘어온 진행중인 품목등록요청 cnt 구하기
	 *
	 * @param
	 * @return int
	 */
	public int findCntProgressingItemRegReq(Map<String, Object> param) {
		CustomSpringEvent event =  CustomSpringEvent.toCompleteEvent("findCntProgressingItemRegReq", param);
		publisher.publishEvent(event);
		return (int) event.getResult();
	}
}
