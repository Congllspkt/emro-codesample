package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.repository.RfxBidItemPriceFactorRepository;
import smartsuite.app.common.security.BidRSACipherUtil;
import smartsuite.app.common.util.ConvertUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxBidItemPriceFactorService {
	
	@Inject
	RfxBidItemPriceFactorRepository rfxBidItemPriceFactorRepository;

	@Inject
	RfxBidItemService rfxBidItemService;

	@Inject
	BidRSACipherUtil bidRSACipherUtil;

	/**
	 * 이전 견적요청 차수의 견적서 원가구성 항목 정보를 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 * param.rfx_no - 견적요청 번호<br>
	 * param.rfx_rnd - 견적요청 이전차수<br>
	 * param.vd_cd - 업체 코드<br><br>
	 * 
	 * @param rfxBidData - 견적서
	 * @return List
	 */
	public List findListPrevRevBidItemPriceFactorForAgent(Map rfxBidData) {
		if(rfxBidData == null) {
			return null;
		}
		
		if("N".equals(rfxBidData.get("coststr_use_yn"))) {
			return null;
		}
		
		Map param = Maps.newHashMap();
		param.put("rfx_uuid" , rfxBidData.get("rfx_uuid"));
		param.put("rfx_no" , rfxBidData.get("rfx_no"));
		param.put("rfx_rnd", Integer.parseInt(rfxBidData.get("rfx_rnd").toString()) - 1);
		param.put("vd_cd"  , rfxBidData.get("vd_cd"));

		List rfxBidItemPriceFactors = rfxBidItemPriceFactorRepository.findListPrevRevBidItemPriceFactorForAgent(param);

		for (Map rfxBidItemPriceFactor : (List<Map>) rfxBidItemPriceFactors) {
			this.decryptRfxBidItemPriceFactorAmt(rfxBidData, rfxBidItemPriceFactor);
		}

		return rfxBidItemPriceFactors;
	}
	
	/**
	 * 견적대행 작성 시 품목별 원가구성 정보 저장한다.<br><br>
	 * @param rfxBidItem
	 * @param rfxBidPriceFactors - 신규 저장할 원가구성 항목
	 */
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
			rfxBidItemPriceFactorRepository.insertRfxBidPriceFactor(rfxBidPriceFactor);
		}
	}

	/**
	 * 견적대행 작성 시 품목별 원가구성 정보 수정한다.<br><br>
	 *
	 * @param bidPriceFactors - 수정할 원가구성 항목
	 */
	public void updateRfxBidPriceFactor(List bidPriceFactors) {
		if(bidPriceFactors == null) {
			return;
		}
		if(bidPriceFactors.isEmpty()) {
			return;
		}
		
		for(Map rfxBidPriceFactor : (List<Map>) bidPriceFactors) {
			this.encryptRfxBidItemPriceFactorAmt(rfxBidPriceFactor);
			rfxBidItemPriceFactorRepository.updateRfxBidPriceFactor(rfxBidPriceFactor);
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
	
	public List selectSumPriceFactorBidItems(Map param) {
		return rfxBidItemPriceFactorRepository.selectSumPriceFactorBidItems(param);
	}
	
	public List findListBidItemPriceFactorForAgent(Map param) {
		return rfxBidItemPriceFactorRepository.findListBidItemPriceFactorForAgent(param);
	}
	
	/**
	 * 견적서 원가구성 항목 정보를 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 * param.rfx_bid_uuid - 견적서 ID<br>
	 * param.vd_cd - 업체 코드<br><br>
	 * 
	 * @param rfxBidData - 견적서
	 * @return List
	 */
	public List findListBidItemPriceFactor(Map rfxBidData) {
		if(rfxBidData == null) {
			return null;
		}
		
		if("N".equals(rfxBidData.get("coststr_use_yn"))) {
			return null;
		}

		List rfxBidItemPriceFactors = rfxBidItemPriceFactorRepository.findListBidItemPriceFactor(rfxBidData);

		for (Map rfxBidItemPriceFactor : (List<Map>) rfxBidItemPriceFactors) {
			this.decryptRfxBidItemPriceFactorAmt(rfxBidData, rfxBidItemPriceFactor);
		}

		return rfxBidItemPriceFactors;
	}

	private void decryptRfxBidItemPriceFactorAmt(Map rfxBidData, Map rfxBidItemPriceFactor) {
		if(rfxBidData == null) {
			return;
		}
		if(rfxBidItemPriceFactor == null) {
			return;
		}

		String rfxOperationOrgCode = rfxBidData.get("oorg_cd") == null? "" : rfxBidData.get("oorg_cd").toString();

		if(rfxBidItemService.existRfxOperationOrganizationByUser(rfxOperationOrgCode)){
			String encryptPriceFactorSubmitUnitPrice = (String) rfxBidItemPriceFactor.get("encpt_costfact_subm_uprc");
			rfxBidItemPriceFactor.put("costfact_subm_uprc", bidRSACipherUtil.decrypt(encryptPriceFactorSubmitUnitPrice));

			String encryptPriceFactorSubmitAmt = (String) rfxBidItemPriceFactor.get("encpt_costfact_subm_amt");
			rfxBidItemPriceFactor.put("costfact_subm_amt", bidRSACipherUtil.decrypt(encryptPriceFactorSubmitAmt));
		}
	}
	
	public List findListRfxBidItemPriceFactorCompare(Map rfxInfo) {
		return rfxBidItemPriceFactorRepository.findListRfxBidItemPriceFactorCompare(rfxInfo);
	}
	
	/**
	 * 원가구성인 경우 견적서 품목 원가구성항목 암호화된 금액 전부 복호화 수행한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 * 
	 * @param param
	 * @return void
	 */
	public void updateDecryptBidPriceFactorAmount(Map param) {
		if(param == null) {
			return;
		}
		
		List rfxQtaItemPriceFactors = rfxBidItemPriceFactorRepository.findListBidItemPriceFactor(param);
		for(Map rfxQtaPriceFactor : (List<Map>) rfxQtaItemPriceFactors) {
			String encPrice = (String) rfxQtaPriceFactor.get("encpt_costfact_subm_uprc");
			if(encPrice == null) {
				encPrice = "0";
			}
			if(!"0".equals(encPrice)) {
				rfxQtaPriceFactor.put("costfact_subm_uprc", bidRSACipherUtil.decrypt(encPrice));
			}
			
			String encItemAmt = (String) rfxQtaPriceFactor.get("encpt_costfact_subm_amt");
			if(encItemAmt == null) {
				encItemAmt = "0";
			}
			if(!"0".equals(encItemAmt)) {
				rfxQtaPriceFactor.put("costfact_subm_amt", bidRSACipherUtil.decrypt(encItemAmt));
			}

			rfxBidItemPriceFactorRepository.updateDecryptBidPriceFactor(rfxQtaPriceFactor);
		}
	}
}
