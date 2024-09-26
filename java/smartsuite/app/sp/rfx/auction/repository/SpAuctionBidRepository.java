package smartsuite.app.sp.rfx.auction.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class SpAuctionBidRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-auction-bid.";
	
	/**
	 * 역경매 현황 목록을 조회한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건
	 * @return list : 역경매 현황 목록
	 * @Date : 2016. 6. 1
	 * @Method Name : findListAuctionVd
	 */
	public FloaterStream findListAuctionVd(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE+"findListAuctionVd", param);
	}

	/**
	 * 역경매 견적 정보를 조회한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return map : 역경매 견적 정보
	 * @Date : 2016. 6. 1
	 */
	public Map findAuctionBid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAuctionBid", param);
	}

	/**
	 * 이전차수의 역경매 견적 정보를 조회한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디, rfx_rnd: 이전차수}
	 * @return map : 이전차수의 역경매 견적 정보
	 * @Date : 2016. 6. 1
	 */
	public Map findAuctionBidPrevRev(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAuctionBidPrevRev", param);
	}
	
	/**
	 * 역경매 견적 제출 목록을 조회한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return list : 역경매 견적 제출 목록
	 * @Date : 2016. 6. 1
	 */
	public List<Map> findListAuctionBidAttend(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionBidAttend", param);
	}

	public void insertAuctionBid(Map rfxBidData) {
		sqlSession.insert(NAMESPACE + "insertAuctionBid", rfxBidData);
	}

	public void updateAuctionBid(Map rfxBidData) {
		sqlSession.update(NAMESPACE + "updateAuctionBid", rfxBidData);
	}

	public void updateAuctionBidValidYn(Map prevRfxBid) {
		sqlSession.update(NAMESPACE + "updateAuctionBidValidYn", prevRfxBid);
	}

	public Map findMinBidAmt(Map auctionBid) {
		return sqlSession.selectOne(NAMESPACE + "findMinBidAmt", auctionBid);
	}

	public void updateAuctionBidProgSts(Map updateBid) {
		sqlSession.update(NAMESPACE + "updateAuctionBidProgSts", updateBid);
	}
	
	public Map findAuctionByRfxVdUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAuctionByRfxVdUuid", param);
	}
}
