package smartsuite.app.bp.rfx.auction.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class AuctionBidRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "auction-bid.";
	
	public List findListAuctionBid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionBid", param);
	}
	
	public Map findAuctionBid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAuctionBid", param);
	}
	
	public Map findAuctionBidPrevRev(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAuctionBidPrevRev", param);
	}
	
	public void insertAuctionBid(Map param) {
		sqlSession.insert(NAMESPACE + "insertAuctionBid", param);
	}
	
	public void updateAuctionBid(Map param) {
		sqlSession.update(NAMESPACE + "updateAuctionBid", param);
	}
	
	public List findListAuctionBidAttend(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionBidAttend", param);
	}
	
	public void updateAuctionBidValidYn(Map param) {
		sqlSession.update(NAMESPACE + "updateAuctionBidValidYn", param);
	}
	
	public void updateAuctionBidByAbandon(Map param) {
		sqlSession.update(NAMESPACE + "updateAuctionBidByAbandon", param);
	}

	public List findListVdAttend(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVdAttend", param);
	}

	public void updateAuctionBidStl(Map auctionBid) {
		sqlSession.update(NAMESPACE + "updateAuctionBidStl", auctionBid);
	}

	public List selectAuctionBids(Map rfxData) {
		return sqlSession.selectList(NAMESPACE + "selectAuctionBids", rfxData);
	}
	
	public List findListVdAttendAmount(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVdAttendAmount", param);
	}

	public void updateAuctionBidRanking(Map attend) {
		sqlSession.update(NAMESPACE + "updateAuctionBidRanking", attend);
	}

	public List findListAuctionTargetVd(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionTargetVd", param);
	}

	public List findListAuctionSubmitVd(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionSubmitVd", param);
	}

	public List findListSelectedAuctionBid(Map auctionInfo) {
		return sqlSession.selectList(NAMESPACE + "findListSelectedAuctionBid", auctionInfo);
	}

	public Map findSelectedAuctionBid(Map auctionInfo) {
		return sqlSession.selectOne(NAMESPACE + "findSelectedAuctionBid", auctionInfo);
	}

	public void updateAuctionBidStlRsn(Map auctionInfo) {
		sqlSession.update(NAMESPACE+"updateAuctionBidStlRsn",auctionInfo);
	}
	
	public List<Map> findListAuctionBidSlctnByAuction(Map auctionInfo) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionBidSlctnByAuction", auctionInfo);
	}
	
	public List findAuctionBidSlctnItemByAuctionAndVdCd(Map updateItem) {
		return sqlSession.selectList(NAMESPACE + "findAuctionBidSlctnItemByAuctionAndVdCd", updateItem);
	}
}
