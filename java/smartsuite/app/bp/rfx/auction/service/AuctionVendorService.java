package smartsuite.app.bp.rfx.auction.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.auction.repository.AuctionVendorRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class AuctionVendorService {

	@Inject
	AuctionVendorRepository auctionVendorRepository;

	@Inject
	AuctionVendorItemService auctionVendorItemService;

	public List findListAuctionVendor(Map param) {
		return auctionVendorRepository.findListAuctionVendor(param);
	}

	public void deleteAuctionVendor(List auctionVendors) {
		if(auctionVendors == null) {
			return;
		}
		if(auctionVendors.isEmpty()) {
			return;
		}

		for(Map auctionVendor : (List<Map>) auctionVendors) {
			this.deleteAuctionVendor(auctionVendor);
		}
	}

	private void deleteAuctionVendor(Map auctionVendor) {
		if(auctionVendor == null) {
			return;
		}

		auctionVendorItemService.deleteAuctionVendorItemsByVendor(auctionVendor);
		auctionVendorRepository.deleteAuctionVendor(auctionVendor);
	}

	public void insertAuctionVendor(Map rfxData, List auctionVendors) {
		if(rfxData == null) {
			return;
		}
		if(auctionVendors == null) {
			return;
		}
		if(auctionVendors.isEmpty()) {
			return;
		}

		for(Map auctionVendor : (List<Map>) auctionVendors) {
			this.insertAuctionVendor(rfxData, auctionVendor);
		}

		auctionVendorItemService.insertAuctionVendorItemByAuctionVendor(rfxData, auctionVendors);
	}

	private void insertAuctionVendor(Map rfxData, Map auctionVendor) {
		if(rfxData == null) {
			return;
		}
		if(auctionVendor == null) {
			return;
		}

		auctionVendor.put("rfx_vd_uuid", UUID.randomUUID().toString());
		auctionVendor.put("rfx_uuid", rfxData.get("rfx_uuid"));
		auctionVendor.put("rfx_no", rfxData.get("rfx_no"));
		auctionVendor.put("cur_ccd", rfxData.get("cur_ccd"));
		auctionVendor.put("rfx_rnd", rfxData.get("rfx_rnd"));

		auctionVendorRepository.insertAuctionVendor(auctionVendor);
	}

	public void updateAuctionVendor(List auctionVendors) {
		if(auctionVendors == null) {
			return;
		}
		if(auctionVendors.isEmpty()) {
			return;
		}

		for(Map auctionVendor : (List<Map>) auctionVendors) {
			this.updateAuctionVendor(auctionVendor);
		}
	}

	private void updateAuctionVendor(Map auctionVendor) {
		if(auctionVendor == null) {
			return;
		}
		auctionVendorRepository.updateAuctionVendor(auctionVendor);
	}

	public void deleteAuctionVendorByAuction(Map param) {
		List auctionVendors = this.findListAuctionVendor(param);
		this.deleteAuctionVendor(auctionVendors);
	}

	public List findListAuctionVendorByMultiRound(String rfxId, int vdSubmitCnt) {
		if(rfxId == null) {
			return null;
		}

		Map param = Maps.newHashMap();
		param.put("rfx_uuid", rfxId);
		param.put("multi_round_yn", "Y");

		if(vdSubmitCnt != 0) {
			// 제출한 업체만 조회하기 위함
			param.put("rfx_bid_sts_ccd", "SUBM");
		}

		return this.findListAuctionVendor(param);
	}
}
