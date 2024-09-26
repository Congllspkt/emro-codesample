package smartsuite.app.bp.itemMaster.master.event;

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
public class ItemMasterEventPublisher {

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
	/**
	 * 품목을 변경할 때 픔목 품목속성을 변경한다.
	 *
	 * @param
	 * @return void
	 */
	public void insertItemIattrAfterDelete(Map<String, Object> param, List<Map<String, Object>> list) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("itemInfo", param);
		paramMap.put("asgnAttrList", list);
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("insertItemIattrAfterDelete", paramMap);
		publisher.publishEvent(event);
	}

	/**
	 * ITEM_IATTR 기반으로 된 배정 속성정보를 가져온다.
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListItemAsgnAttrFromItemMaster(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListItemAsgnAttrFromItemMaster", param);
		publisher.publishEvent(event);
		return (List<Map<String, Object>>) event.getResult();
	}

	/**
	 * ITEMCAT_IATTR 기반으로 된 배정 속성정보를 가져온다.
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListItemAsgnAttr(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListItemAsgnAttr", param);
		publisher.publishEvent(event);
		return (List<Map<String, Object>>) event.getResult();
	}

	/**
	 * 품목 변경 요청 하기 위한 조건 정보 조회
	 *
	 * @param
	 * @return the boolean
	 */
	public Boolean checkExistedItemRegReqFromItemMaster(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("checkExistedItemRegReq", param);
		publisher.publishEvent(event);
		return (Boolean) event.getResult();
	}

	/**
	 * 품목 변경 요청 하기 위한 조건 정보 조회
	 *
	 * @param
	 * @return the boolean
	 */
	public Map<String, Object> findInfoChangeItemFromItemMaster(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findInfoModifyItemReq", param);
		publisher.publishEvent(event);
		return (Map<String, Object>) event.getResult();
	}

	/**
	 * 품목 변경 요청 하기 위한 배정속성 정보 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListItemAsgnAttrByItemcatFromItemMaster(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListItemAsgnAttrByItemcat", param);
		publisher.publishEvent(event);
		return (List<Map<String, Object>>) event.getResult();
	}

	/**
	 * 품목 변경 요청 발송
	 *
	 * @param
	 * @return the resultmap
	 */
	public ResultMap saveInfoChangeItemReq(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveInfoChangeItemReq", param);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
}
