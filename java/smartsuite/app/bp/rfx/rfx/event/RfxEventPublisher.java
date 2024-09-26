package smartsuite.app.bp.rfx.rfx.event;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.ListUtils;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfxEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;


	
	/**
	 * 삭제되는 견적품목들을 타 모듈에 알린다.
	 * 
	 * @param rfxItem - 삭제되는 견적품목 다건
	 * @return void
	 */
	public void deleteRfxItem(List rfxItems) {
		if(rfxItems == null) {
			return;
		}
		if(rfxItems.isEmpty()) {
			return;
		}
		
		List prItemIds = ListUtils.getArrayValuesByList(rfxItems, "pr_item_uuid");
		List upcrItemIds = ListUtils.getArrayValuesByList(rfxItems, "upcr_item_uuid");

		if(prItemIds.size() == 0 && upcrItemIds.size() == 0) {
			return;
		}

		Map<String, Object> param = Maps.newHashMap();
		if(prItemIds != null && prItemIds.size() > 0){
			param.put("pr_item_uuids",prItemIds);
		}
		if(upcrItemIds != null && upcrItemIds.size() > 0){
			param.put("upcr_item_uuids",upcrItemIds);
		}

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteRfxItem", param);
		publisher.publishEvent(event);
	}
	
	
	/**
	 * 견적품목이 삭제되었음을 타 모듈에 알린다.
	 * 
	 * @param rfxItem
	 * @return void
	 */
	public void deleteRfxItem(Map rfxItem) {
		Map param = Maps.newHashMap();
		List<String> prItemIds = Lists.newArrayList();
		List<String> upcrItemIds = Lists.newArrayList();

		if(rfxItem == null) {
			return;
		}
		if(!Strings.isNullOrEmpty((String) rfxItem.get("pr_item_uuid"))) {
			prItemIds.add((String) rfxItem.get("pr_item_uuid"));
			param.put("pr_item_uuids", prItemIds);
		}

		if(!Strings.isNullOrEmpty((String) rfxItem.get("upcr_item_uuid"))) {
			upcrItemIds.add((String) rfxItem.get("upcr_item_uuid"));
			param.put("upcr_item_uuids", upcrItemIds);
		}

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteRfxItem", param);
		publisher.publishEvent(event);
	}
	
	/**
	 * 견적을 생성할 수 있는 구매요청품목인지 PR 모듈에 확인한다.
	 * 
	 * @param prItemIds - 구매요청품목번호
	 * @return
	 */
	public ResultMap validateCreateRfxByPr(List prItemIds) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("validateCreateRfxByPr", prItemIds);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	public ResultMap validateCreateRfxByUpcr(List upcrItemIds) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("validateCreateRfxByUpcr", upcrItemIds);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * PR 품목이 접수되었음을 PR 모듈에 알린다.
	 * 
	 * @param prItemIds 구매요청품목번호
	 */
	public void receivePr(List prItemIds) {
		if(prItemIds == null) {
			return;
		}
		if(prItemIds.isEmpty()) {
			return;
		}

		Map eventParam = Maps.newHashMap();
		eventParam.put("pr_item_uuids", prItemIds);
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("receivePr", eventParam);
		publisher.publishEvent(event);
	}

	public void receiveUpcr(List prItemIds) {
		if(prItemIds == null) {
			return;
		}
		if(prItemIds.isEmpty()) {
			return;
		}

		Map eventParam = Maps.newHashMap();
		eventParam.put("upcr_item_uuids", prItemIds);
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("receiveUpcr", eventParam);
		publisher.publishEvent(event);
	}
	
	/**
	 * 견적 임시저장을 타 모듈에 알린다.
	 * 
	 * @param prItemIds 구매요청품목번호 다건
	 * @param upcrItemIds 단가계약요청 다건
	 */
	public void saveDraftRfx(Map eventParam) {
		if(eventParam == null) {
			return;
		}
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveDraftRfx", eventParam);
		publisher.publishEvent(event);
	}

	public void updateProgStsFromRfx(Map eventParam) {
		if(eventParam == null) {
			return;
		}
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("updateProgStsFromRfx", eventParam);
		publisher.publishEvent(event);
	}
	
	/**
	 * 견적요청 multi round 수행됨을 PR 모듈이 알린다.<br><br>
	 * <b>Required:</b><br>
	 * rfxItems<br>
	 * 
	 * @param rfxItems - 견적요청 품목
	 * @return void
	 */
	public void multiRoundRfx(List rfxItems) {
		if(rfxItems == null) {
			return;
		}
		if(rfxItems.isEmpty()) {
			return;
		}
		
		// 견적요청 품목이 구매요청 품목 ID 가지고 있는지 확인한다.
		List prItemIds = ListUtils.getArrayValuesByList(rfxItems, "pr_item_uuid");
		if(prItemIds.size() == 0) {
			return;
		}
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("multiRoundRfx", prItemIds);
		publisher.publishEvent(event);
	}
	

	public void closeRfxResult(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("closeRfxResult", param);
		publisher.publishEvent(event);
	}

	public void temporarySaveRfi(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("temporarySaveRfi", eventParam);
		publisher.publishEvent(event);
	}

	public void requestRfi(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("requestRfi", eventParam);
		publisher.publishEvent(event);
	}

	public void completeRfi(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("completeRfi", eventParam);
		publisher.publishEvent(event);
	}

	public void deleteRfiItem(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteRfiItem", eventParam);
		publisher.publishEvent(event);
	}

	public void submitApprovalRfxProg(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("submitApprovalRfxProg", eventParam);
		publisher.publishEvent(event);
	}

	public void approveApprovalRfxProg(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("approveApprovalRfxProg", eventParam);
		publisher.publishEvent(event);
	}

	public void rejectApprovalRfxProg(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("rejectApprovalRfxProg", eventParam);
		publisher.publishEvent(event);
	}

	public void cancelApprovalRfxProg(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("cancelApprovalRfxProg", eventParam);
		publisher.publishEvent(event);
	}

	public void bypassApprovalRfxProg(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("bypassApprovalRfxProg", eventParam);
		publisher.publishEvent(event);
	}

	public void startRfx(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("startRfx", eventParam);
		publisher.publishEvent(event);
	}

	public void closeRfx(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("closeRfx", eventParam);
		publisher.publishEvent(event);
	}

	public void dropRfx(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("dropRfx", eventParam);
		publisher.publishEvent(event);
	}

	public void cancelRfx(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("cancelRfx", eventParam);
		publisher.publishEvent(event);
	}

	public void submitApprovalRfxResult(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("submitApprovalRfxResult", eventParam);
		publisher.publishEvent(event);
	}

	public void approveApprovalRfxResult(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("approveApprovalRfxResult", param);
		publisher.publishEvent(event);
	}

	public void rejectApprovalRfxResult(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("rejectApprovalRfxResult", eventParam);
		publisher.publishEvent(event);
	}

	public void cancelApprovalRfxResult(Map eventParam) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("cancelApprovalRfxResult", eventParam);
		publisher.publishEvent(event);
		
	}

	public void bypassApprovalRfxResult(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("bypassApprovalRfxResult", param);
		publisher.publishEvent(event);
	}

	/**
	 * 견적요청 품목 키 리스트를 통해 PRO 모듈에서 견적요청 정보를 얻는다.
	 *
	 * @param prItemUuids - 견적요청 품목 uuid List
	 * @return List<Map>
	 */
	public List<Map> findListPrByPrItems(List<String> prItemUuids) {
		if(prItemUuids == null) {
			return null;
		}
		if(prItemUuids.isEmpty()) {
			return null;
		}

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListPrByPrItems", prItemUuids);
		publisher.publishEvent(event);
		return (List<Map>) event.getResult();
	}

	public List<Map> findListUpcrByUpcrItems(List<String> upcrItemUuids) {
		if(upcrItemUuids == null) {
			return null;
		}
		if(upcrItemUuids.isEmpty()) {
			return null;
		}

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListUpcrByUpcrItems", upcrItemUuids);
		publisher.publishEvent(event);
		return (List<Map>) event.getResult();
	}
	
	/**
	 * 추천업체 유형에 따라 각 모듈에서 추천업체를 받아온다.
	 *
	 * @param rcmdCd - 추천업체 유형
	 * @param itemCds - 견적 품목 리스트
	 * @param oorgCd - 운영조직
	 * @param purcTypCcd - 구매 유형
	 * @return
	 */
	public List<String> findRecommandVendors(String rcmdCd, List<String> itemCds, List<String> sgCds, String oorgCd, String purcTypCcd) {
		if(rcmdCd == null) {
			return null;
		}
		if(itemCds == null || itemCds.isEmpty()) {
			return null;
		}
		if(oorgCd == null) {
			return null;
		}
		if(purcTypCcd == null) {
			return null;
		}
		
		Map param = Maps.newHashMap();
		param.put("rcmd_cd", rcmdCd);
		param.put("item_cds", itemCds);
		param.put("sg_cds", sgCds);
		param.put("oorg_cd", oorgCd);
		param.put("purc_typ_ccd", purcTypCcd);
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(rcmdCd, param);
		publisher.publishEvent(event);
		List<String> rcmdVendors = Lists.newArrayList();
		if(event.getResult() != null) {
			rcmdVendors = (List<String>) event.getResult();
		}
		
		return rcmdVendors;
	}
}