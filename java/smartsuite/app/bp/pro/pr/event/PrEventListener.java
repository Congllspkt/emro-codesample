package smartsuite.app.bp.pro.pr.event;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import smartsuite.app.bp.pro.pr.service.PrItemService;
import smartsuite.app.bp.pro.pr.service.PrService;
import smartsuite.app.bp.pro.pr.validator.PrValidator;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class PrEventListener {
	static final Logger LOG = LoggerFactory.getLogger(PrEventListener.class);
	@Inject
	ProStatusService proStatusService;
	
	@Inject
	PrValidator prValidator;
	
	@Inject
	PrItemService prItemService;

	@Inject
	PrService prService;
	
	@EventListener(condition = "#event.eventId == 'validateCreateRfxByPr'")
	public void validateCreateRfxByPr(CustomSpringEvent event) {
		ResultMap validator = prValidator.validateCreateRfxByPr((List) event.getData());
		event.setResult(validator);
	}

	@EventListener(condition = "#event.eventId == 'validateCreateRfxByUpcr'")
	public void validateCreateRfxByUpcr(CustomSpringEvent event) {
		ResultMap validator = prValidator.validateCreateRfxByUpcr((List) event.getData());
		event.setResult(validator);
	}

	/**
	 * receivePr
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'receivePr'")
	public void receivePr(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		if(param == null || !(param.containsKey("pr_item_uuids") || param.containsKey("pr_item_uuid")) ){
			LOG.warn("receivePr event no parameter");
		}else{
			proStatusService.receivePr(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'returnPr'")
	public void returnPr(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		if(param == null || !(param.containsKey("pr_item_uuids") || param.containsKey("pr_item_uuid")) ){
			LOG.warn("returnPr event no parameter");
		}else{
			proStatusService.returnPr(param);
		}
	}
	
	/**
	 * 견적품목 삭제에 따른 구매요청 품목 상태 업데이트<br><br>
	 * <b>Required:</b><br><br>
	 * <b>Options:</b><br>
	 * event.data.prItemIds - 구매요청 품목 ID 다건<br>
	 * event.data.prItemId - 구매요청 품목 ID
	 * 
	 * @param event
	 * @return void
	 */
	@EventListener(condition = "#event.eventId == 'deleteRfxItem'")
	public void deleteRfxItem(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		
		if(param != null && param.containsKey("pr_item_uuids")) {
			proStatusService.deleteRfxItem(param);
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
		List<String> prItemIds = (List) event.getData();
		
		if(prItemIds == null) {
			return;
		}
		if(prItemIds.isEmpty()) {
			return;
		}
		
		Map param = Maps.newHashMap();
		param.put("pr_item_uuids", prItemIds);
		proStatusService.multiRoundRfx(param);
	}

	@EventListener(condition = "#event.eventId == 'cancelRfxResultPr'")
	public void cancelRfxResultPr(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param.put("pr_item_uuids", event.getData());
		proStatusService.cancelRfxResult(param);
	}

	@EventListener(condition = "#event.eventId == 'closeRfxResult'")
	public void closeRfxResult(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && !ObjectUtils.isEmpty(param.get("pr_item_uuids"))){
			proStatusService.closeRfxResult(param);
		}
	}

	@EventListener(condition = "#event.eventId == 'temporarySaveRfi'")
	public void temporarySaveRfi(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && !ObjectUtils.isEmpty(param.get("pr_item_uuids"))){
			proStatusService.temporarySaveRfi(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'requestRfi'")
	public void requestRfi(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.requestRfi(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'completeRfi'")
	public void completeRfi(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.completeRfi(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'deleteRfiItem'")
	public void deleteRfiItem(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.deleteRfiItem(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'submitApprovalRfxProg'")
	public void submitApprovalRfxProg(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.submitApprovalRfxProg(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'approveApprovalRfxProg'")
	public void approveApprovalRfxProg(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.approveApprovalRfxProg(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'rejectApprovalRfxProg'")
	public void rejectApprovalRfxProg(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.rejectApprovalRfxProg(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'cancelApprovalRfxProg'")
	public void cancelApprovalRfxProg(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.cancelApprovalRfxProg(param);
		}
	}

	@EventListener(condition = "#event.eventId == 'bypassApprovalRfxProg'")
	public void bypassApprovalRfxProg(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.bypassApprovalRfxProg(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'startRfx'")
	public void startRfx(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.startRfx(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'closeRfx'")
	public void closeRfx(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.closeRfx(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'dropRfx'")
	public void dropRfx(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.dropRfx(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'cancelRfx'")
	public void cancelRfx(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.cancelRfx(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'submitApprovalRfxResult'")
	public void submitApprovalRfxResult(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.submitApprovalRfxResult(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'approveApprovalRfxResult'")
	public void approveApprovalRfxResult(CustomSpringEvent event) {
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		proStatusService.approveApprovalRfxResult((Map) event.getData());
	}
	
	@EventListener(condition = "#event.eventId == 'rejectApprovalRfxResult'")
	public void rejectApprovalRfxResult(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.rejectApprovalRfxResult(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'cancelApprovalRfxResult'")
	public void cancelApprovalRfxResult(CustomSpringEvent event) {
		Map param = Maps.newHashMap();
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		param = (Map) event.getData();
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			proStatusService.cancelApprovalRfxResult(param);
		}
	}
	
	@EventListener(condition = "#event.eventId == 'bypassApprovalRfxResult'")
	public void bypassApprovalRfxResult(CustomSpringEvent event) {
		proStatusService.bypassApprovalRfxResult((Map) event.getData());
	}

	@EventListener(condition ="#event.eventId == 'findRfxItemds'")
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

	@EventListener(condition ="#event.eventId == 'findListPrItemIdsByPrId'")
	public void findListPrItemIds(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		List<Map<String,Object>> result = prItemService.findListPrItemIdsByPrId(param);
		event.setResult(result);
	}

	@EventListener(condition ="#event.eventId == 'findPr'")
	public void findPr(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		Map<String,Object> result = prService.findPr(param);
		event.setResult(result);
	}

	/**
	 *
	 * @param.pr_item_uuids event
	 */
	@EventListener(condition ="#event.eventId == 'findListPrByPrItems'")
	public void findListPrByPrItems(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		List<Map<String,Object>> prItemUuids = (List<Map<String, Object>>) event.getData();
		Map<String,Object> param = Maps.newHashMap();
		param.put("pr_item_uuids",prItemUuids);
		List<Map<String,Object>> result = prService.findListPrByPrItems(param);
		event.setResult(result);
	}

	@EventListener(condition = "#event.eventId == 'saveDraftRfx'")
	public void saveDraftRfxPrDt(CustomSpringEvent event) {
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map param = (Map) event.getData();
		if(param == null || !param.containsKey("pr_item_uuids") ){
			LOG.warn("saveDraftRfx event no parameter");
		}else{
			proStatusService.saveDraftRfx(param);
		}
	}

	@EventListener(condition ="#event.eventId == 'findListPrePrItemList'")
	public void findListPrePrItemList(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		List<Map<String,Object>> result = prItemService.findListPrePrItemList(param);
		event.setResult(result);
	}


	@EventListener(condition ="#event.eventId == 'saveDraftPr'")
	public void saveDraftPr(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		ResultMap result = prService.saveDraftPr(param);
		event.setResult(result);
	}
	@EventListener(condition ="#event.eventId == 'directReqPr'")
	public void directReqPr(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		ResultMap result = prService.directReqPr(param);
		event.setResult(result);
	}


	@EventListener(condition ="#event.eventId == 'findListPrItemByPrItemUuids'")
	public void findListPrItemByPrItemUuids(CustomSpringEvent event){
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		List<Map<String, Object>> result = prItemService.findListPrItemByPrItemUuids(param);
		event.setResult(result);
	}

}