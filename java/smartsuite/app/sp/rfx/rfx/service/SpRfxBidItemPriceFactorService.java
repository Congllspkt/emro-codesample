package smartsuite.app.sp.rfx.rfx.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.security.BidRSACipherUtil;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.sp.rfx.rfx.repository.SpRfxBidItemPriceFactorRepository;
import smartsuite.app.sp.rfx.rfx.validator.SpRfxValidator;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpRfxBidItemPriceFactorService {
	
	@Inject
	SpRfxBidItemPriceFactorRepository spRfxBidItemPriceFactorRepository;
	
	@Inject
	BidRSACipherUtil bidRSACipherUtil;
	
	@Inject
	SpRfxValidator spRfxValidator;
	
	public void insertRfxBidPriceFactor(Map rfxBidItem, List rfxBidPriceFactors) {
		if(rfxBidItem == null) {
			return;
		}
		if(rfxBidPriceFactors == null) {
			return;
		}
		if(rfxBidPriceFactors.isEmpty()) {
			return;
		}
		
		for(Map rfxBidPriceFactor : (List<Map>) rfxBidPriceFactors) {
			rfxBidPriceFactor.put("rfx_bid_item_uuid", rfxBidItem.get("rfx_bid_item_uuid"));
			
			this.encryptRfxBidItemPriceFactorAmt(rfxBidPriceFactor);
			spRfxBidItemPriceFactorRepository.insertBidPriceFactor(rfxBidPriceFactor);
		}
	}

	public void updateRfxBidPriceFactor(List rfxBidPriceFactors) {
		for(Map rfxBidPriceFactor : (List<Map>) rfxBidPriceFactors) {
			this.encryptRfxBidItemPriceFactorAmt(rfxBidPriceFactor);
			spRfxBidItemPriceFactorRepository.updateBidPriceFactor(rfxBidPriceFactor);
		}
	}
	
	/**
	 * 견적품목 원가구성 금액 관련 필드 암호화<br><br>
	 * 
	 * @param rfxBidPriceFactor - 원가구성항목
	 */
	private void encryptRfxBidItemPriceFactorAmt(Map rfxBidPriceFactor) {
		if(rfxBidPriceFactor == null) {
			return;
		}
		
		String price = ConvertUtils.convertStringNullZero(rfxBidPriceFactor.get("costfact_subm_uprc"));
		rfxBidPriceFactor.put("encpt_costfact_subm_uprc", bidRSACipherUtil.encrypt(price));
		rfxBidPriceFactor.put("costfact_subm_uprc", 0);
		
		String itemAmt = ConvertUtils.convertStringNullZero (rfxBidPriceFactor.get("costfact_subm_amt"));
		if(itemAmt == null) {
			itemAmt = "0";
		}
		rfxBidPriceFactor.put("encpt_costfact_subm_amt", bidRSACipherUtil.encrypt(itemAmt));
		rfxBidPriceFactor.put("costfact_subm_amt", 0);
	}
	
	/**
	 * 견적품목 원가구성 금액 관련 필드 복호화<br><br>
	 *
	 * @param rfxBidData
	 * @param rfxBidPriceFactor - 원가구성항목
	 */
	private void decryptRfxBidItemPriceFactorAmt(Map rfxBidData, Map rfxBidPriceFactor) {
		if(rfxBidData == null) {
			return;
		}
		if(rfxBidPriceFactor == null) {
			return;
		}
		
		// 현재 로그인한 사용자의 VD_CD 와 RFX의 VD_CD가 동일하지 않으면 볼 수 없음
		ResultMap validator = spRfxValidator.isSameVendor(rfxBidData);
		if(!validator.isSuccess()) {
			return;
		}
		
		String encPrice = (String) rfxBidPriceFactor.get("encpt_costfact_subm_uprc");
		rfxBidPriceFactor.put("costfact_subm_uprc", bidRSACipherUtil.decrypt(encPrice));
		
		String encItemAmt = (String) rfxBidPriceFactor.get("encpt_costfact_subm_amt");
		rfxBidPriceFactor.put("costfact_subm_amt", bidRSACipherUtil.decrypt(encItemAmt));
	}

	public List findListBidItemPriceFactor(Map param) {
		Map rfxBidData = spRfxBidItemPriceFactorRepository.findRfxBid(param);
		List rfxBidItemPriceFactors = spRfxBidItemPriceFactorRepository.findListBidItemPriceFactor(param);

		for (Map bidPriceFactor : (List<Map>) rfxBidItemPriceFactors) {
			this.decryptRfxBidItemPriceFactorAmt(rfxBidData, bidPriceFactor);
		}
		return rfxBidItemPriceFactors;
	}

	public List findListPrevRevBidItemPriceFactor(Map param) {
		List rfxBidItemPriceFactors = spRfxBidItemPriceFactorRepository.findListPrevRevBidItemPriceFactor(param);
		for (Map bidPriceFactor : (List<Map>) rfxBidItemPriceFactors) {
			this.decryptRfxBidItemPriceFactorAmt(param, bidPriceFactor);
		}
		return rfxBidItemPriceFactors;
	}

}
