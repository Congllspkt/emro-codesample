package smartsuite.app.bp.pro.upcr.event;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import smartsuite.app.bp.pro.upcr.service.UpcrItemService;
import smartsuite.app.bp.pro.upcr.service.UpcrService;
import smartsuite.app.bp.pro.upcr.validator.UpcrValidator;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.status.service.UpcrStatusService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class UpcrEventListener {
	static final Logger LOG = LoggerFactory.getLogger(UpcrEventListener.class);
	@Inject
	UpcrStatusService upcrStatusService;
	
	@Inject
	UpcrValidator upcrValidator;
	
	@Inject
	UpcrItemService upcrItemService;

	@Inject
	UpcrService upcrService;
	
	@EventListener(condition = "#event.eventId == 'validateCreateRfxByUpcr'")
	public void validateCreateRfxByUpcr(CustomSpringEvent event) {
		ResultMap validator = upcrValidator.validateCreateRfxByUpcr((List) event.getData());
		event.setResult(validator);
	}

	/**
	 * receivePr
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'receiveUpcr'")
	public void receiveUpcr(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		if(param == null || !(param.containsKey("upcr_item_uuids") || param.containsKey("upcr_item_uuid")) ){
			LOG.warn("receiveUpcr event no parameter");
		}else{
			upcrStatusService.receiveUpcr(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'returnUpcr'")
	public void returnUpcr(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		if(param == null || !(param.containsKey("upcr_item_uuids") || param.containsKey("upcr_item_uuid")) ){
			LOG.warn("returnUpcr event no parameter");
		}else{
			upcrStatusService.returnUpcr(param);
		}
	}
	
	/**
	 * 견적품목으로 만들어질 구매요청품목 정보를 조회하여 반환한다.<br><br>
	 * <b>Required:</b><br>
	 * event.data.prItemIds - 속성 설명<br>
	 * 
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'findListRfxItemDefaultDataByUpcrItemIds'")
	public void findListRfxItemDefaultDataByUpcrItemIds(CustomSpringEvent event) {
		List rfxItems = upcrItemService.findListRfxItemDefaultDataByUpcrItemIds((List) event.getData());
		event.setResult(rfxItems);
	}
	
	/**
	 * Rfi품목으로 만들어질 구매요청품목 정보를 조회하여 반환한다.<br><br>
	 * <b>Required:</b><br>
	 * event.data.prItemIds - 속성 설명<br>
	 * 
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'findListRfiItemDefaultDataByUpcrItemIds'")
	public void findListRfiItemDefaultDataByUpcrItemIds(CustomSpringEvent event) {
		Map<String, Object> keyParam = Maps.newHashMap();
		keyParam.put("upcr_item_uuids", (List) event.getData());
		List rfxItems = upcrItemService.findListRfiItemDefaultDataByUpcrItemIds(keyParam);
		event.setResult(rfxItems);
	}
	
	/**
	 * 견적품목 삭제에 따른 구매요청 품목 상태 업데이트<br><br>
	 * <b>Required:</b><br><br>
	 * <b>Options:</b><br>
	 * event.data.upcrItemIds - 단가계약요청 품목 ID 다건<br>
	 * event.data.upcrItemId - 단가계약요청 품목 ID
	 * 
	 * @param event
	 * @return void
	 */
	@EventListener(condition = "#event.eventId == 'deleteRfxItem'")
	public void deleteRfxItem(CustomSpringEvent event) {
		Map param = (Map) event.getData();

		if(param != null && param.containsKey("upcr_item_uuids")) {
			upcrStatusService.deleteRfxItem(param);
		}
	}

	/**
	 * 견적 multi round 수행에 따른 구매요청 품목 상태 업데이트<br><br>
	 * <b>Required:</b><br>
	 * event.data.prItemIds - 견적요청품목과 연결된 구매요청품목 IDs<br>
	 * 
	 * @param event
	 * @return void
	 */
	@EventListener(condition = "#event.eventId == 'multiRoundRfx'")
	public void multiRoundRfx(CustomSpringEvent event) {
		List<String> itemIds = (List) event.getData();
		
		if(itemIds == null) {
			return;
		}
		if(itemIds.isEmpty()) {
			return;
		}
		
		Map param = Maps.newHashMap();
		param.put("upcr_item_uuids", itemIds);
		upcrStatusService.multiRoundRfx(param);
	}

	@EventListener(condition = "#event.eventId == 'cancelRfxResultUpcr'")
	public void cancelRfxResultUpcr(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param.put("upcr_item_uuids", event.getData());
		upcrStatusService.cancelRfxResult(param);
	}

	@EventListener(condition = "#event.eventId == 'closeRfxResult'")
	public void closeRfxResult(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.closeRfxResult(param);
		}
	}

	@EventListener(condition = "#event.eventId == 'temporarySaveRfi'")
	public void temporarySaveRfi(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.temporarySaveRfi(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'requestRfi'")
	public void requestRfi(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.requestRfi(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'completeRfi'")
	public void completeRfi(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.completeRfi(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'deleteRfiItem'")
	public void deleteRfiItem(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.deleteRfiItem(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'submitApprovalRfxProg'")
	public void submitApprovalRfxProg(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.submitApprovalRfxProg(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'approveApprovalRfxProg'")
	public void approveApprovalRfxProg(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.approveApprovalRfxProg(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'rejectApprovalRfxProg'")
	public void rejectApprovalRfxProg(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.rejectApprovalRfxProg(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'cancelApprovalRfxProg'")
	public void cancelApprovalRfxProg(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.cancelApprovalRfxProg(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'bypassApprovalRfxProg'")
	public void bypassApprovalRfxProg(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.bypassApprovalRfxProg(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'startRfx'")
	public void startRfx(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.startRfx(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'closeRfx'")
	public void closeRfx(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.closeRfx(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'dropRfx'")
	public void dropRfx(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.dropRfx(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'cancelRfx'")
	public void cancelRfx(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.cancelRfx(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'submitApprovalRfxResult'")
	public void submitApprovalRfxResult(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.submitApprovalRfxResult(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'approveApprovalRfxResult'")
	public void approveApprovalRfxResult(CustomSpringEvent event) {
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		upcrStatusService.approveApprovalRfxResult((Map) event.getData());
	}
	
	@EventListener(condition = "#event.eventId == 'rejectApprovalRfxResult'")
	public void rejectApprovalRfxResult(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.rejectApprovalRfxResult(param);
		}
	}

	@EventListener(condition = "#event.eventId == 'cancelApprovalRfxResult'")
	public void cancelApprovalRfxResult(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			upcrStatusService.cancelApprovalRfxResult(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'bypassApprovalRfxResult'")
	public void bypassApprovalRfxResult(CustomSpringEvent event) {
		upcrStatusService.bypassApprovalRfxResult((Map) event.getData());
	}

	@EventListener(condition ="#event.eventId == 'findRfxItems'")
	public void findListReferenceDocFromPRByPR(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		String appType = (String) param.get("appType");
		//List<Map<String,Object>> result = proStatusService.findListReferenceDocFromPR(param);
				//sqlSession.selectList(NAMESPACE+"findListReferenceDocFromPR",param);
		List<Map<String,Object>> result  = null;
		event.setResult(result);
	}

	@EventListener(condition ="#event.eventId == 'findListUpcrItemIdsByUpcrId'")
	public void findListUpcrItemIdsByUpcrId(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		List<Map<String,Object>> result = upcrItemService.findListUpcrItemIdsByUpcrId(param);
		event.setResult(result);
	}

	@EventListener(condition ="#event.eventId == 'findUpcr'")
	public void findUpcr(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		Map<String,Object> result = upcrService.findUpcr(param);
		event.setResult(result);
	}

	/**
	 *
	 * @param.pr_item_uuids event
	 */
	@EventListener(condition ="#event.eventId == 'findListUpcrByUpcrItems'")
	public void findListUpcrByUpcrItems(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		List<Map<String,Object>> upcrItemUuids = (List<Map<String, Object>>) event.getData();
		Map<String,Object> param = Maps.newHashMap();
		param.put("upcr_item_uuids",upcrItemUuids);
		List<Map<String,Object>> result = upcrService.findListUpcrByUpcrItems(param);
		event.setResult(result);
	}

	@EventListener(condition = "#event.eventId == 'saveDraftRfx'")
	public void saveDraftRfxPrDt(CustomSpringEvent event) {
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map param = (Map) event.getData();
		if(param == null || !param.containsKey("upcr_item_uuids") ){
			LOG.warn("saveDraftRfx event no parameter");
		}else{
			upcrStatusService.saveDraftRfx(param);
		}
	}

	@EventListener(condition = "#event.eventId == 'createUpcrCost'")
	public void createUpcrCost(CustomSpringEvent event) {
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map param = (Map) event.getData();
		if(param == null || !param.containsKey("proj_cd") ){
			LOG.warn("createUpcrCost event no parameter");
		}else{
			// 차후 하기 로직 TargetPrice 로 이관 필요
			// 통화로 나뉘어지는 case 에 대한 검토하였으나, 현재 targetprice에서 통화가 안 넘어오는 case
			List<Map<String, Object>> upcrDatas = upcrService.findListPriceGateByUpcr(param);

			for (Map<String, Object> upcrData : upcrDatas) {
				param.put("upcrData", upcrData);
				param.put("cur_ccd", MapUtils.getString(upcrData, "cur_ccd"));

				// 차후 하기 로직 TargetPrice 로 이관 필요
				param.put("insertUpcrItems", upcrService.findListPriceGateItemsByUpcr(param));

				ResultMap resultMap = upcrService.saveUpcr(param);
				if(!resultMap.isSuccess()) {
					LOG.warn("createUpcrCost check saveUpcr");
				}

				resultMap = upcrService.directReqUpcr(param);
				if(!resultMap.isSuccess()) {
					LOG.warn("createUpcrCost check directReqUpcr");
				}

				// 차후 하기 로직 TargetPrice Event 호출 필요
				param.put("upcr_uuid", MapUtils.getString(resultMap.getResultData(), "upcr_uuid"));
				upcrService.updatePriceGateByUpcr(param);
			}
		}
	}

	@EventListener(condition ="#event.eventId == 'findListUpcrItemByUpcrItemUuids'")
	public void findListUpcrItemByUpcrItemUuids(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		List<Map<String, Object>> result = upcrItemService.findListUpcrItemByUpcrItemUuids(param);
		event.setResult(result);
	}

	@EventListener(condition ="#event.eventId == 'requestCopyPrsToUpcrs'")
	public void requestCopyPrsToUpcrs(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		List<Map<String,Object>> items = (List<Map<String, Object>>) event.getData();
		List<Map<String,Object>> result = upcrService.requestCopyPrsToUpcrs(items);
		event.setResult(result);
	}

}