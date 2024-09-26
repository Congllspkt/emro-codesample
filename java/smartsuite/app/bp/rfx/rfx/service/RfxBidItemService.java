package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.bp.rfx.rfx.repository.RfxBidItemRepository;
import smartsuite.app.common.security.BidRSACipherUtil;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.common.util.ListUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxBidItemService {
	
	@Inject
	RfxBidItemRepository rfxBidItemRepository;
	
	@Inject
	RfxBidItemPriceFactorService rfxBidItemPriceFactorService;
	
	@Inject
	RfxEventPublisher rfxEventPublisher;
	
	@Inject
	BidRSACipherUtil bidRSACipherUtil;

	@Inject
	SharedService sharedService;
	
	/**
	 * 견적기준 협력사 견적서 품목을 조회한다.<br><br>
	 *
	 * @param param
	 * @return
	 */
	public List findListRfxItemWithBidItems(Map param) {
		return rfxBidItemRepository.findListRfxItemWithBidItems(param);
	}
	
	/**
	 * 견적요청 multi round인 경우 제외업체 외 이전차수의 견적서 정보를 조회한다.<br><br>
	 *
	 * @param rfxBidData
	 * @return
	 */
	public List findListRfxItemWithPrevRevBidItemForAgent(Map rfxBidData) {
		List<Map> rfxQtaItemList = rfxBidItemRepository.findListRfxItemWithPrevRevBidItemForAgent(rfxBidData);
		rfxQtaItemList = this.decryptRfxItem(rfxBidData,rfxQtaItemList);


		List<String> prItemUuids = ListUtils.getArrayValuesByList(rfxQtaItemList, "pr_item_uuid");
		List<Map> prInfos = rfxEventPublisher.findListPrByPrItems(prItemUuids);
		
		return ListUtils.leftOuterJoin(rfxQtaItemList, prInfos, Lists.newArrayList("pr_item_uuid"), Lists.newArrayList("pr_uuid"));
	}
	
	/**
	 * RFx 업체대행 시 견적서 작성을 위한 품목정보 조회한다.<br><br>
	 *
	 * @param rfxBidData
	 * @return
	 */
	public List findListRfxBidItemForAgent(Map rfxBidData) {
		List<Map> rfxQtaItemList = rfxBidItemRepository.findListRfxQtaItemForAgent(rfxBidData);
		rfxQtaItemList = this.decryptRfxItem(rfxBidData,rfxQtaItemList);

		List<String> prItemUuids = ListUtils.getArrayValuesByList(rfxQtaItemList, "pr_item_uuid");
		List<Map> prInfos = rfxEventPublisher.findListPrByPrItems(prItemUuids);
		
		return ListUtils.leftOuterJoin(rfxQtaItemList, prInfos, Lists.newArrayList("pr_item_uuid"), Lists.newArrayList("pr_uuid"));
	}

	/**
	 * rfx Items 암호화 복호화 작업
	 * @param rfxBidData
	 * @param rfxQtaItemList
	 * @return
	 */
	public List decryptRfxItem(Map<String,Object> rfxBidData, List<Map> rfxQtaItemList){

		String rfxOperationOrgCode = rfxBidData.get("oorg_cd") == null? "" : rfxBidData.get("oorg_cd").toString();

		if(this.existRfxOperationOrganizationByUser(rfxOperationOrgCode)){
			for(Map<String,Object> rfxQtaItem : rfxQtaItemList){
				String encryptRfxItemSubmitUnitPrice = rfxQtaItem.get("encpt_rfx_item_subm_uprc") ==null? "0" : rfxQtaItem.get("encpt_rfx_item_subm_uprc").toString();
				if(StringUtils.isNotEmpty(encryptRfxItemSubmitUnitPrice) && !("0").equals(encryptRfxItemSubmitUnitPrice))  rfxQtaItem.put("rfx_item_subm_uprc",bidRSACipherUtil.decrypt(encryptRfxItemSubmitUnitPrice));

				String encryptRfxItemSubmitAmt = rfxQtaItem.get("encpt_rfx_item_subm_amt") ==null? "0" : rfxQtaItem.get("encpt_rfx_item_subm_amt").toString();
				if(StringUtils.isNotEmpty(encryptRfxItemSubmitAmt) && !("0").equals(encryptRfxItemSubmitAmt))  rfxQtaItem.put("rfx_item_subm_amt",bidRSACipherUtil.decrypt(encryptRfxItemSubmitAmt));
			}
		}

		return rfxQtaItemList;
	}
	
	/**
	 * RFx 업체대행 견적서 품목 신규생성한다.<br><br>
	 *
	 * @param rfxBidData
	 * @param rfxBidItems
	 * @param bidPriceFactors
	 */
	public void insertRfxBidItem(Map rfxBidData, List rfxBidItems, List bidPriceFactors) {
		boolean isPriFactSet = "Y".equals(rfxBidData.get("coststr_use_yn"));
		int i = 0;
		
		for(Map rfxBidItem : (List<Map>) rfxBidItems) {
			List rfxBidPriceFactors = Lists.newArrayList();
			String rfxQtaItemId = UUID.randomUUID().toString();
			
			rfxBidItem.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
			rfxBidItem.put("rfx_bid_item_uuid", rfxQtaItemId);
			rfxBidItem.put("rfx_bid_lno", ++i);
			rfxBidItem.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
			rfxBidItem.put("rfx_bid_no", rfxBidData.get("rfx_bid_no"));
			rfxBidItem.put("rfx_bid_revno", rfxBidData.get("rfx_bid_revno"));
			rfxBidItem.put("rfx_bid_efct_yn", "Y");
			// 원가구성항목 설정인 경우
			if(isPriFactSet) {
				for(Map bidPriceFactor : (List<Map>) bidPriceFactors) {
					if(rfxBidItem.get("rfx_item_uuid").equals(bidPriceFactor.get("rfx_item_uuid"))) {
						rfxBidPriceFactors.add(bidPriceFactor);
					}
				}
			}
			
			this.encryptRfxBidItemPrice(rfxBidItem);
			rfxBidItemRepository.insertRfxBidItem(rfxBidItem);
			rfxBidItemPriceFactorService.insertRfxBidPriceFactor(rfxBidItem, rfxBidPriceFactors);
		}
	}
	
	/**
	 * RFx 업체대행 견적서 품목 수정한다.<br><br>
	 *
	 * @param rfxQtaItem
	 */
	public void updateRfxBidItem(Map rfxBidData, List rfxBidItems, List bidPriceFactors) {
		boolean isPriFactSet = "Y".equals(rfxBidData.get("coststr_use_yn"));
		
		for(Map rfxBidItem : (List<Map>) rfxBidItems) {
			rfxBidItem.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
			rfxBidItem.put("rfx_bid_no", rfxBidData.get("rfx_bid_no"));
			rfxBidItem.put("rfx_bid_revno", rfxBidData.get("rfx_bid_revno"));
			rfxBidItem.put("rfx_bid_efct_yn", "Y");
			/* SHH 24/02/13 null을 주는 이유를 파악할 수 없음. 이로 인해 수정시 업체의 가격이 0으로 들어가 수정이 필요함.
			// 원가구성항목 설정인 경우
			if(isPriFactSet) {
				rfxBidItem.put("rfx_item_subm_uprc", null);
				rfxBidItem.put("rfx_item_subm_amt", null);
			}*/
			
			this.encryptRfxBidItemPrice(rfxBidItem);
			rfxBidItemRepository.updateRfxBidItem(rfxBidItem);
		}
		
		// 원가구성항목
		rfxBidItemPriceFactorService.updateRfxBidPriceFactor(bidPriceFactors);
	}
	
	/**
	 * 견적품목 금액 관련 필드 암호화<br><br>
	 *
	 * @param rfxBidItem - 견적품목
	 */
	private void encryptRfxBidItemPrice(Map rfxBidItem) {
		if(rfxBidItem == null) {
			return;
		}
		
		String itemPrice = ConvertUtils.convertStringNullZero(rfxBidItem.get("rfx_item_subm_uprc"));
		rfxBidItem.put("encpt_rfx_item_subm_uprc", bidRSACipherUtil.encrypt(itemPrice));
		rfxBidItem.put("rfx_item_subm_uprc", 0);
		
		String itemAmt = ConvertUtils.convertStringNullZero (rfxBidItem.get("rfx_item_subm_amt"));
		rfxBidItem.put("encpt_rfx_item_subm_amt", bidRSACipherUtil.encrypt(itemAmt));
		rfxBidItem.put("rfx_item_subm_amt", 0);
	}
	
	public void updateRfxBidItemAmt(List rfxBidItems) {
		if(rfxBidItems == null) {
			return;
		}
		if(rfxBidItems.isEmpty()) {
			return;
		}
		
		for(Map rfxBidItem : (List<Map>) rfxBidItems) {
			rfxBidItemRepository.updateRfxBidItemAmt(rfxBidItem);
		}
	}
	
	public Map selectSumPriceBid(Map param) {
		return rfxBidItemRepository.selectSumPriceBid(param);
	}
	
	/**
	 * 해당 메소드 행위<br><br>
	 * <b>Required:</b><br>
	 * param.xxx - 속성 설명<br>
	 * param.zzz - 속성 설명
	 *
	 * @param rfxBid
	 */
	public void updateRfxBidStl(Map rfxBid) {
		List rfxBidItems = rfxBidItemRepository.selectRfxBidItemStl(rfxBid);
		for(Map rfxBidItem : (List<Map>) rfxBidItems) {
			this.updateRfxBidItemStl(rfxBidItem);
		}
	}
	
	public void updateRfxBidItemStl(Map rfxBidItem) {
		rfxBidItemRepository.updateRfxBidItemStl(rfxBidItem);
	}
	
	public void updateDecryptBidItemAmount(Map param) {
		rfxBidItemRepository.updateDecryptBidItemAmount(param);
	}

	/**
	 *  rfx 운영조직에 사용자 운영조직이 존재하는지 체크
	 * @param rfxOperOrgCode
	 * @return
	 */
	public boolean existRfxOperationOrganizationByUser(String rfxOperOrgCode) {
		boolean checkOperOrgYn = false;
		List<Map<String, Object>> userOperOrgCodeList = sharedService.findListOperationOrganizationByUser("PO");
		for(Map<String,Object> operOrgInfo : userOperOrgCodeList){
			String operOrgCode = operOrgInfo.get("oorg_cd") == null? "" : (String)operOrgInfo.get("oorg_cd");
			if(rfxOperOrgCode.equals(operOrgCode)) {
				checkOperOrgYn = true;
				break;
			}
		}
		return checkOperOrgYn;
	}
}
