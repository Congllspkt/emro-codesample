package smartsuite.app.bp.pro.po.event;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ObjectUtils;
import smartsuite.app.bp.pro.po.service.PoConsortiumVendorService;
import smartsuite.app.bp.pro.po.service.PoItemService;
import smartsuite.app.bp.pro.po.service.PoService;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.exception.CommonException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PoEventListener {

	static final Logger LOGGER = LoggerFactory.getLogger(PoEventListener.class);

	@Inject
	PoItemService poItemService;
	
	@Inject
	PoService poService;

	@Inject
	PoConsortiumVendorService poConsortiumVendorService;
	
	@Inject
	ProStatusService proStatusProcessor;

	@EventListener(condition = "#event.eventId == 'updatePoItemCompleteByAmt'")
	public void updatePoItemCompleteByAmt(CustomSpringEvent event) {
		Map<String, Object> resultData = (Map<String, Object>) event.getData();
		poItemService.updatePoItemCompleteByAmt(resultData);
	}
	

	/**
	 * 전자계약 반송 시 호출
	 * @param event
	 * @param.po_uuid : 발주 uuid
	 * @return
	 */
	@EventListener(condition = "#event.eventId == 'returnOrderContract'")
	public void returnOrderContract(CustomSpringEvent event) {
		Map<String,Object> param = (Map<String, Object>) event.getData();
		Map<String, Object> keyParam = Maps.newHashMap();
		keyParam.put("po_uuid", param.get("po_uuid"));
		
		proStatusProcessor.returnElecCntrPo(keyParam);	// 상태 업데이트
	}

	@EventListener(condition ="#event.eventId == 'updatePoItemCompleteByQty'")
	public void updatePoItemCompleteByQty(CustomSpringEvent event) {
		Map<String, Object> data = (Map<String, Object>) event.getData();
		if(data != null) {
			poItemService.updatePoItemCompleteByQty(data);
		}
	}

	@EventListener(condition ="#event.eventId == 'findListPoByRfxItemIds'")
	public void findListPoByRfxItemIds(CustomSpringEvent event) {
		Map<String, Object> data = (Map<String, Object>) event.getData();
		if(data  != null){
			List<Map<String,Object>> result = poItemService.findListPoByRfxItemIds(data);
			event.setResult(result);
		}
	}

	/**
	 * event.data.pr
	 * event.data.prAth
	 * @param event
	 * @param.poData
	 * @param.insertItems
	 * @Result void
	 */
	@EventListener(condition = "#event.eventId == 'createPoPr'")
	public void createPoPr(CustomSpringEvent event) {
		Map<String,Object> poParam = (Map<String, Object>) event.getData();
		String poId = poService.createPo(poParam);                // 1. 데이터 생성

		Map<String, Object> keyParam = (Map<String, Object>) poParam.get("poData");
		keyParam.put("po_uuid", poId);

		LOGGER.info("CustomSpringEvent.directPoPr::: TransactionSynchronizationManager.getCurrentTransactionName()" + TransactionSynchronizationManager.getCurrentTransactionName());

		proStatusProcessor.bypassApprovalPo(keyParam);            // 2. 상태 업데이트

		poService.updatePoCreDate(poId);                        // 3. 발주 생성 일자 수정
		poService.updateCurrentPo(keyParam);                    // 4. 현재 유효한 발주여부(EFCT_PO_YN)를 업데이트 한다.
		proStatusProcessor.updateCreatePoPr(keyParam);
	}

	@EventListener(condition = "#event.eventId == 'createDraftPoPr'")
	public void createDraftPoPr(CustomSpringEvent event) {
		Map<String,Object> poParam = (Map<String, Object>) event.getData();
		String poId = poService.createPo(poParam);                // 1. 데이터 생성

		Map<String, Object> keyParam = (Map<String, Object>) poParam.get("poData");
		keyParam.put("po_uuid", poId);

		LOGGER.info("CustomSpringEvent.createDraftPoPr::: TransactionSynchronizationManager.getCurrentTransactionName()" + TransactionSynchronizationManager.getCurrentTransactionName());

		proStatusProcessor.saveDraftPo(keyParam);            // 2. 상태 업데이트

		poService.updatePoCreDate(poId);                        // 3. 발주 생성 일자 수정
		poService.updateCurrentPo(keyParam);                    // 4. 현재 유효한 발주여부(EFCT_PO_YN)를 업데이트 한다.
	}

	/**
	 * createPo
	 * @param event
	 * @param.poData
	 * @param.insertItems
	 * @result.param.po_uuid
	 */
	@EventListener(condition = "#event.eventId == 'createPo'")
	public void createPo(CustomSpringEvent event) {
		Map<String,Object> poParam = (Map<String, Object>) event.getData();
		List<Map<String, Object>> insertItems = (List<Map<String, Object>>)poParam.get("insertItems");

		if(!poParam.containsKey("poData") || poParam.get("poData") == null){
			LOGGER.info("EventListenerError [createPo]  NO poData");
			throw new CommonException("STD.E000");
		}else{
			String poId = poService.createPo(poParam);                // 1. 데이터 생성
			Map<String, Object> keyParam = new HashMap<String, Object>();
			keyParam.put("po_uuid", poId);
			event.setResult(keyParam);
			proStatusProcessor.createDraftPo(keyParam);
		}
	}


	/**
	 * 전자계약 작성 시 발주 정보 조회
	 * @param event
	 * @param.po_uuid
	 * @result map
	 */
	@EventListener(condition = "#event.eventId == 'findOrderCntrInfo'")
	public void findOrderCntrInfo(CustomSpringEvent event) {
		Map<String,Object> param = (Map<String, Object>) event.getData();
		Map<String,Object> result = poService.findOrderCntrInfo(param);
		event.setResult(result);
	}
	
	@EventListener(condition = "#event.eventId == 'findListYearlyPoItemByVendor'")
	public void findListYearlyPoItemByVendor(CustomSpringEvent event) {
		Map<String, Object> param = (Map<String, Object>) event.getData();
		
		List<Map<String, Object>> yearlyPoItemList = poService.findListYearlyPoItemByVendor(param);
		event.setResult(yearlyPoItemList);
	}
	
	@EventListener(condition = "#event.eventId == 'findListYearlyPoTotAmtByVendor'")
	public void findListYearlyPoTotAmtByVendor(CustomSpringEvent event) {
		Map<String, Object> param = (Map<String, Object>) event.getData();
		
		List<Map<String, Object>> yearlyPoTotAmtList = poService.findListYearlyPoTotAmtByVendor(param);
		event.setResult(yearlyPoTotAmtList);
	}
	
	@EventListener(condition = "#event.eventId == 'findListMonthlyPoTotAmtByVendor'")
	public void findListMonthlyPoTotAmtByVendor(CustomSpringEvent event) {
		Map<String, Object> param = (Map<String, Object>) event.getData();
		
		List<Map<String, Object>> monthlyPoTotAmtList = poService.findListMonthlyPoTotAmtByVendor(param);
		event.setResult(monthlyPoTotAmtList);
	}
	
	@EventListener(condition = "#event.eventId == 'findListVendorPoTotAmtByItem'")
	public void findListVendorPoTotAmtByItem(CustomSpringEvent event) {
		Map<String, Object> param = (Map<String, Object>) event.getData();
		
		List<Map<String, Object>> vendorPoTotAmtList = poService.findListVendorPoTotAmtByItem(param);
		event.setResult(vendorPoTotAmtList);
	}
	
	@EventListener(condition = "#event.eventId == 'findListVendorPoItemPriceByItem'")
	public void findListVendorPoItemPriceByItem(CustomSpringEvent event) {
		Map<String, Object> param = (Map<String, Object>) event.getData();
		
		List<Map<String, Object>> vendorPoItemPriceList = poService.findListVendorPoItemPriceByItem(param);
		event.setResult(vendorPoItemPriceList);
	}
	
	/**
	 * 전자계약 대상 발주 목록 조회
	 * @param event
	 * @result list
	 */
	@EventListener(condition = "#event.eventId == 'findListOrderCntrMaker'")
	public void findListOrderCntrMaker(CustomSpringEvent event) {
		Map<String,Object> param = (Map<String, Object>) event.getData();
		List<Map<String,Object>> resultList = poService.findListOrderCntrMaker(param);
		event.setResult(resultList);
	}
	
	/**
	 * 발주 컨소시엄 업체 목록 조회
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'findConsortiumListByPoId'")
	public void findConsortiumListByPoId(CustomSpringEvent event) {
		Map<String,Object> param = (Map<String, Object>) event.getData();
		List<Map<String,Object>> resultList = poConsortiumVendorService.findConsortiumListByPoId(param);
		event.setResult(resultList);
	}
	
	/**
	 * 전자계약 생성시 호출
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'createOrderElecContract'")
	public void createOrderElecContract(CustomSpringEvent event) {
		Map<String,Object> param = (Map<String, Object>) event.getData();
		proStatusProcessor.createPoEcntr(param);
	}
	
	/**
	 * 전자계약 해지시 호출
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'terminateOrderElecContract'")
	public void terminateOrderElecContract(CustomSpringEvent event) {
		Map<String,Object> param = (Map<String, Object>) event.getData();
		proStatusProcessor.terminatePoEcntr(param);
	}
	
	/**
	 * 전자계약 삭제시 호출
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'deleteOrderElecContract'")
	public void deleteOrderElecContract(CustomSpringEvent event) {
		Map<String,Object> param = (Map<String, Object>) event.getData();
		proStatusProcessor.deletePoEcntr(param);
	}
	
	@EventListener(condition = "#event.eventId == 'WTHNONEYR_ITEM_PO'")
	public void findListVendorRcmdWthnoneyrItemPo(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(poService.findListVendorRcmdWthnoneyrItemPo(param));
	}
	
	@EventListener(condition = "#event.eventId == 'WTHNONEYR_SG_PO'")
	public void findListVendorRcmdWthnoneyrSgPo(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(poService.findListVendorRcmdWthnoneyrSgPo(param));
	}

	@EventListener(condition = "#event.eventId == 'findReturnedPoCount'")
	public void findReturnedPoCount(CustomSpringEvent event) {
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		event.setResult(poService.findReturnedPoCount(param));
	}
	@EventListener(condition = "#event.eventId == 'findPoTypeCount'")
	public void findPoTypeCount(CustomSpringEvent event) {
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map<String, Object>) event.getData();
		event.setResult(poService.findPoTypeCount(param));
	}
	@EventListener(condition = "#event.eventId == 'findListWorkplaceDashboardPoData'")
	public void findListWorkplaceDashboardPoData(CustomSpringEvent event) {
		if(event.getData() == null || ObjectUtils.isEmpty(event.getData())){
			return;
		}
		Map<String,Object> param = (Map) event.getData();
		event.setResult(poService.findListWorkplaceDashboardPoData(param));
	}
}
