package smartsuite.app.sp.rfx.auction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.sp.rfx.auction.repository.SpAuctionBidItemRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpAuctionBidItemService {
	
	@Inject
	SpAuctionBidItemRepository spAuctionBidItemRepository;
	
	/**
	 * 역경매 견적 품목 제출 목록을 조회한다.
	 *
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return list : 역경매 견적 품목 제출 목록
	 * @author kh_lee
	 * @Date : 2016. 6. 1
	 */
	public List<Map> findListAuctionBidItemAttend(Map param) {
		return spAuctionBidItemRepository.findListAuctionBidItemAttend(param);
	}
	
	/**
	 * 역경매 견적 품목 목록을 조회한다.
	 *
	 * @param param : 검색조건 {rfx_uuid: 아이디, rfx_bid_uuid : 역경매 견적 아이디}
	 * @return list : 역경매 견적 품목 목록
	 * @author kh_lee
	 * @Date : 2016. 6. 1
	 */
	public List<Map> findListAuctionBidItem(Map param) {
		return spAuctionBidItemRepository.findListAuctionBidItem(param);
	}
	
	/**
	 * 이전차수의 역경매 견적 품목 목록을 조회한다.
	 *
	 * @param param : 검색조건 {rfx_uuid: 아이디, rfx_rnd : 역경매 이전차수}
	 * @return list : 이전차수의 역경매 견적 품목 목록
	 * @author kh_lee
	 * @Date : 2016. 6. 1
	 */
	public List<Map> findListAuctionBidItemByRfxNoRfxRev(Map param) {
		return spAuctionBidItemRepository.findListAuctionBidItemByRfxNoRfxRev(param);
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
		
		spAuctionBidItemRepository.insertAuctionBidItem(auctionBidItem);
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
		
		spAuctionBidItemRepository.updateAuctionBidItem(auctionBidItem);
	}
}
