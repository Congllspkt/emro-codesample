package smartsuite.app.bp.rfx.auction.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class AuctionBidItemRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "auction-bid-item.";
	
	public List findListAuctionBidItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionBidItem", param);
	}
	
	public List findListAuctionBidItemByRfxNoRfxRev(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionBidItemByRfxNoRfxRev", param);
	}
	
	public void insertAuctionBidItem(Map param) {
		sqlSession.insert(NAMESPACE + "insertAuctionBidItem", param);
	}
	
	public void updateAuctionBidItem(Map param) {
		sqlSession.update(NAMESPACE + "updateAuctionBidItem", param);
	}
	
	public List findListAuctionBidItemAttend(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionBidItemAttend", param);
	}
	
	public List findListItemAttend(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListItemAttend", param);
	}
	
	public List findListVdItemAttend(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVdItemAttend", param);
	}

	public List selectAuctionBidItemStl(Map auctionBid) {
		return sqlSession.selectList(NAMESPACE + "selectAuctionBidItemStl", auctionBid);
	}

	public void updateAuctionBidItemStl(Map bidItem) {
		sqlSession.update(NAMESPACE + "updateAuctionBidItemStl", bidItem);
	}
	
	public List findListItemAttendAmount(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListItemAttendAmount", param);
	}

	public void updateAuctionBidItemRanking(Map attend) {
		sqlSession.update(NAMESPACE + "updateAuctionBidItemRanking", attend);
	}
	
	public List findListSelectedAuctionBidItem(Map auctionBid) {
		return sqlSession.selectList(NAMESPACE + "findListSelectedAuctionBidItem", auctionBid);
	}
}
