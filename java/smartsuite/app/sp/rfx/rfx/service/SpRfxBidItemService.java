package smartsuite.app.sp.rfx.rfx.service;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.common.security.BidRSACipherUtil;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.common.util.ListUtils;
import smartsuite.app.sp.rfx.rfx.repository.SpRfxBidItemRepository;
import smartsuite.app.sp.rfx.rfx.validator.SpRfxValidator;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpRfxBidItemService {
	
	@Inject
	SpRfxBidItemRepository spRfxBidItemRepository;
	
	@Inject
	SpRfxBidItemPriceFactorService spRfxBidItemPriceFactorService;
	
	@Inject
	BidRSACipherUtil bidRSACipherUtil;
	
	@Inject
	SpRfxValidator spRfxValidator;
	
	@Inject
	RfxEventPublisher rfxEventPublisher;
	
	public void insertRfxBidItem(Map rfxBidData, List rfxBidItems, List bidPriceFactors) {
		boolean isPriFactSet = "Y".equals(rfxBidData.get("coststr_use_yn"));
		int i = 0;
		
		for(Map rfxBidItem : (List<Map>) rfxBidItems) {
			List rfxBidPriceFactors = Lists.newArrayList();
			
			rfxBidItem.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
			rfxBidItem.put("rfx_bid_item_uuid", UUID.randomUUID().toString());
			rfxBidItem.put("rfx_bid_no", rfxBidData.get("rfx_bid_no"));
			rfxBidItem.put("rfx_bid_revno", rfxBidData.get("rfx_bid_revno"));
			rfxBidItem.put("rfx_bid_lno", ++i);
			rfxBidItem.put("rfx_bid_efct_yn", "Y");
			rfxBidItem.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
			
			// 원가구성항목 설정인 경우
			if(isPriFactSet && bidPriceFactors != null) {
				for(Map bidPriceFactor : (List<Map>) bidPriceFactors) {
					if(rfxBidItem.get("rfx_item_uuid").equals(bidPriceFactor.get("rfx_item_uuid"))) {
						rfxBidPriceFactors.add(bidPriceFactor);
					}
				}
			}
			
			this.encryptRfxBidItemPrice(rfxBidItem);
			spRfxBidItemRepository.insertRfxBidItem(rfxBidItem);
			spRfxBidItemPriceFactorService.insertRfxBidPriceFactor(rfxBidItem, rfxBidPriceFactors);
		}
	}
	
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
			spRfxBidItemRepository.updateRfxBidItem(rfxBidItem);
		}
		
		spRfxBidItemPriceFactorService.updateRfxBidPriceFactor(bidPriceFactors);
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
	
	/**
	 * 견적품목 금액 관련 필드 복호화<br><br>
	 *
	 * @param rfxBidItem - 견적품목
	 */
	private void decryptRfxBidItemPrice(Map param, Map rfxBidItem) {
		if(rfxBidItem == null) {
			return;
		}
		// 현재 로그인한 사용자의 VD_CD 와 RFX의 VD_CD가 동일하지 않으면 볼 수 없음
		ResultMap validator = spRfxValidator.isSameVendor(param);
		if(!validator.isSuccess()) {
			return;
		}
		
		rfxBidItem.put("rfx_item_subm_uprc", bidRSACipherUtil.decrypt((String) rfxBidItem.get("encpt_rfx_item_subm_uprc")));
		rfxBidItem.put("rfx_item_subm_amt", bidRSACipherUtil.decrypt((String) rfxBidItem.get("encpt_rfx_item_subm_amt")));
	}
	
	public List findListRfxItemWithPrevRevBidItem(Map param) {
		List rfxBidItems = spRfxBidItemRepository.findListRfxItemWithPrevRevBidItem(param);
		
		List<String> prItemUuids = ListUtils.getArrayValuesByList(rfxBidItems, "pr_item_uuid");
		List<Map> prInfos = rfxEventPublisher.findListPrByPrItems(prItemUuids);
		rfxBidItems = ListUtils.leftOuterJoin(rfxBidItems, prInfos, Lists.newArrayList("pr_item_uuid"), Lists.newArrayList("pr_uuid"));
		
		for(Map rfxBidItem : (List<Map>) rfxBidItems) {
			this.decryptRfxBidItemPrice(param, rfxBidItem);
		}
		
		return rfxBidItems;
	}
	
	public List findListRfxBidItem(Map param) {
		List rfxBidItems = spRfxBidItemRepository.findListRfxBidItem(param);
		
		List<String> prItemUuids = ListUtils.getArrayValuesByList(rfxBidItems, "pr_item_uuid");
		List<Map> prInfos = rfxEventPublisher.findListPrByPrItems(prItemUuids);
		rfxBidItems = ListUtils.leftOuterJoin(rfxBidItems, prInfos, Lists.newArrayList("pr_item_uuid"), Lists.newArrayList("pr_uuid"));
		
		for(Map rfxBidItem : (List<Map>) rfxBidItems) {
			this.decryptRfxBidItemPrice(param, rfxBidItem);
		}
		
		return rfxBidItems;
	}
}
