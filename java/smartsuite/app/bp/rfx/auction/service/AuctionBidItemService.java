package smartsuite.app.bp.rfx.auction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.auction.repository.AuctionBidItemRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class AuctionBidItemService {

	@Inject
	AuctionBidItemRepository auctionBidItemRepository;

	public List findListAuctionBidItem(Map param) {
		return auctionBidItemRepository.findListAuctionBidItem(param);
	}

	public List findListAuctionBidItemByRfxNoRfxRev(Map param) {
		return auctionBidItemRepository.findListAuctionBidItemByRfxNoRfxRev(param);
	}

	public List findListAuctionBidItemAttend(Map param) {
		return auctionBidItemRepository.findListAuctionBidItemAttend(param);
	}

	public void insertAuctionBidItem(Map rfxBidData, List rfxBidItems) {
		if(rfxBidData == null) {
			return;
		}
		if(rfxBidItems == null) {
			return;
		}
		if(rfxBidItems.isEmpty()) {
			return;
		}

		int i = 0;
		for(Map auctionBidItem : (List<Map>) rfxBidItems) {
			this.insertAuctionBidItem(rfxBidData, auctionBidItem, ++i);
		}
	}

	private void insertAuctionBidItem(Map rfxBidData, Map auctionBidItem, int bidItemLno) {
		if(rfxBidData == null) {
			return;
		}
		if(auctionBidItem == null) {
			return;
		}
		auctionBidItem.put("rfx_bid_item_uuid", UUID.randomUUID().toString());
		auctionBidItem.put("rfx_bid_lno", bidItemLno);
		auctionBidItem.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		auctionBidItem.put("rfx_bid_no", rfxBidData.get("rfx_bid_no"));
		auctionBidItem.put("rfx_bid_revno", rfxBidData.get("rfx_bid_revno"));
		auctionBidItem.put("rfx_bid_efct_yn", "Y");
		auctionBidItem.put("rfx_uuid",rfxBidData.get("rfx_uuid"));

		auctionBidItemRepository.insertAuctionBidItem(auctionBidItem);
	}

	public void updateAuctionBidItem(Map rfxBidData, List rfxBidItems) {
		if(rfxBidData == null) {
			return;
		}
		if(rfxBidItems == null) {
			return;
		}
		if(rfxBidItems.isEmpty()) {
			return;
		}

		int i = 0;
		for(Map auctionBidItem : (List<Map>) rfxBidItems) {
			this.updateAuctionBidItem(rfxBidData, auctionBidItem, ++i);
		}
	}

	private void updateAuctionBidItem(Map rfxBidData, Map auctionBidItem, int bidItemLno) {
		auctionBidItem.put("rfx_bid_lno", bidItemLno);
		auctionBidItem.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		auctionBidItem.put("rfx_bid_no", rfxBidData.get("rfx_bid_no"));
		auctionBidItem.put("rfx_bid_revno", rfxBidData.get("rfx_bid_revno"));
		auctionBidItem.put("rfx_bid_efct_yn", "Y");

		auctionBidItemRepository.updateAuctionBidItem(auctionBidItem);
	}

	public List findListItemAttend(Map param) {
		return auctionBidItemRepository.findListItemAttend(param);
	}

	public List findListVdItemAttend(Map param) {
		return auctionBidItemRepository.findListVdItemAttend(param);
	}

	public void updateAuctionBidStl(Map auctionBid) {
		if(auctionBid == null) {
			return;
		}

		List bidItems = auctionBidItemRepository.selectAuctionBidItemStl(auctionBid);
		for(Map bidItem : (List<Map>) bidItems) {
			this.updateAuctionBidItemStl(bidItem);
		}
	}

	public void updateAuctionBidItemStl(Map auctionBidItem) {
		auctionBidItemRepository.updateAuctionBidItemStl(auctionBidItem);
	}

	public List findListItemAttendAmount(Map param) {
		return auctionBidItemRepository.findListItemAttendAmount(param);
	}

	public void updateAuctionBidItemRanking(Map param) {
		auctionBidItemRepository.updateAuctionBidItemRanking(param);
	}

	public List findListSelectedAuctionBidItem(Map auctionBid) {
		return auctionBidItemRepository.findListSelectedAuctionBidItem(auctionBid);
	}
}
