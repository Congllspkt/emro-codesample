package smartsuite.app.bp.rfx.auction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.auction.repository.AuctionVendorItemRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class AuctionVendorItemService {

	@Inject
	AuctionVendorItemRepository auctionVendorItemRepository;

	public void deleteAuctionVendorItemsByRfxItem(Map auctionItem) {
		if(auctionItem == null) {
			return;
		}
		auctionVendorItemRepository.deleteAuctionVendorItemsByRfxItem(auctionItem);
	}

	public void deleteAuctionVendorItemsByVendor(Map auctionVendor) {
		if(auctionVendor == null) {
			return;
		}
		auctionVendorItemRepository.deleteAuctionVendorItemsByVendor(auctionVendor);
	}

	public void insertAuctionVendorItemByAuctionItem(Map rfxData, List auctionItems) {
		if(rfxData == null) {
			return;
		}
		if(auctionItems == null) {
			return;
		}
		if(auctionItems.isEmpty()) {
			return;
		}

		List auctionVendors = auctionVendorItemRepository.searchRfxVendorByRfx(rfxData);
		if(auctionVendors == null) {
			return;
		}
		if(auctionVendors.isEmpty()) {
			return;
		}

		for(Map auctionVendor : (List<Map>) auctionVendors) {
			for(Map auctionItem : (List<Map>) auctionItems) {
				auctionVendor.put("rfx_item_uuid", auctionItem.get("rfx_item_uuid"));
				auctionVendorItemRepository.insertRfxVendorItem(auctionVendor);
			}
		}
	}

	/**
	 * @param rfxData        - 견적마스터
	 * @param auctionVendors - 견적대상협력사 다건
	 */
	public void insertAuctionVendorItemByAuctionVendor(Map rfxData, List auctionVendors) {
		if(rfxData == null) {
			return;
		}
		if(auctionVendors == null) {
			return;
		}
		if(auctionVendors.isEmpty()) {
			return;
		}

		List auctionItems = auctionVendorItemRepository.searchRfxItemByRfx(rfxData);
		if(auctionItems == null) {
			return;
		}
		if(auctionItems.isEmpty()) {
			return;
		}

		for(Map auctionVendor : (List<Map>) auctionVendors) {
			for(Map auctionItem : (List<Map>) auctionItems) {
				auctionVendor.put("rfx_item_uuid", auctionItem.get("rfx_item_uuid"));
				auctionVendorItemRepository.insertRfxVendorItem(auctionVendor);
			}
		}
	}
}
