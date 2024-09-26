package smartsuite.app.sp.rfx.auction.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class SpAuctionBidItemRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-auction-bid-item.";
	
	/**
	 * 역경매 견적 품목 제출 목록을 조회한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return list : 역경매 견적 품목 제출 목록
	 * @Date : 2016. 6. 1
	 */
	public List<Map> findListAuctionBidItemAttend(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionBidItemAttend", param);
	}
	
	/**
	 * 역경매 견적 품목 목록을 조회한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디, rfx_bid_uuid : 역경매 견적 아이디}
	 * @return list : 역경매 견적 품목 목록
	 * @Date : 2016. 6. 1
	 */
	public List<Map> findListAuctionBidItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionBidItem",param);
	}
	
	/**
	 * 이전차수의 역경매 견적 품목 목록을 조회한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디, rfx_rnd : 역경매 이전차수}
	 * @return list : 이전차수의 역경매 견적 품목 목록
	 * @Date : 2016. 6. 1
	 */
	public List<Map> findListAuctionBidItemByRfxNoRfxRev(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionBidItemByRfxNoRfxRev",param);
	}

	public void insertAuctionBidItem(Map auctionBidItem) {
		sqlSession.insert(NAMESPACE + "insertAuctionBidItem", auctionBidItem);
	}

	public void updateAuctionBidItem(Map auctionBidItem) {
		sqlSession.update(NAMESPACE + "updateAuctionBidItem", auctionBidItem);
	}

	public List<Map> findListMinBidItemAmt(Map auctionBid) {
		return sqlSession.selectList(NAMESPACE + "findListMinBidItemAmt", auctionBid);
	}
}
