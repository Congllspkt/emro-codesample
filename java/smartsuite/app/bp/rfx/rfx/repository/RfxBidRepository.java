package smartsuite.app.bp.rfx.rfx.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxBidRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-bid.";
	
	public Map findRfx(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfx", param);
	}
	
	public FloaterStream findListRfxBidBuyer(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListRfxBidBuyer", param);
	}
	
	public Map findRfxBid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxBid", param);
	}
	
	public Map findRfxBidPrevRev(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxBidPrevRev", param);
	}
	
	public void insertRfxBid(Map param) {
		sqlSession.insert(NAMESPACE + "insertRfxBid", param);
	}
	
	public void updateRfxBid(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxBid", param);
	}
	
	public void updateRfxBidAmt(Map rfxBid) {
		sqlSession.update(NAMESPACE + "updateRfxBidAmt", rfxBid);
	}
	
	public void updateRfxBidValidYn(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxBidValidYn", param);
	}
	
	public void updateRfxBidByAbandon(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxBidByAbandon", param);
	}
	
	public List findListRfxBidWithBidItems(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidWithBidItems", param);
	}

	public void updateRfxBidStl(Map rfxBid) {
		sqlSession.update(NAMESPACE + "updateRfxBidStl", rfxBid);
	}

	public List selectRfxBidsStl(Map rfxData) {
		return sqlSession.selectList(NAMESPACE + "selectRfxBidsStl", rfxData);
	}

	public List findListRfxBid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBid", param);
	}

	public List findListRfxBidHeaderAndList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidHeaderAndList", param);
	}

	public void updateDecryptBidAmount(Map param) {
		sqlSession.update(NAMESPACE + "updateDecryptBidAmount", param);
	}

	public List findListRfxBidScore(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidScore", param);
	}
	
	public List findListRfxBidItemAmt(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidItemAmt", param);
	}

	public void updateRfxBidScoreAndRanking(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxBidScoreAndRanking", param);
	}

	public void updateRfxBidItemRanking(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxBidItemRanking", param);
	}

	public List findListRfxBidCompareByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidCompareByRfx", param);
	}

	public List findListRfxBidCompareByRfxNo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidCompareByRfxNo", param);
	}
	
	public List findListRfxBidItemComparePriceDoctor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidItemComparePriceDoctor", param);
	}
	
	public List findListCsByBid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCsByBid", param);
	}

	public void updateRfxStlRsn(Map rfxBidData) {
		sqlSession.update(NAMESPACE+"updateRfxStlRsn",rfxBidData);
	}
	
	public List findListNegotiableRfxBid(Map rfxData) {
		return sqlSession.selectList(NAMESPACE + "findListNegotiableRfxBid", rfxData);
	}

	public Map findRfxBidForBidEffective(Map rfxData) {
		return sqlSession.selectOne(NAMESPACE + "findRfxBidForBidEffective", rfxData);
	}
	
	public List<Map> findListRfxBidSlctnByRfx(Map rfxInfo) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidSlctnByRfx", rfxInfo);
	}
	
	public List findRfxBidSlctnItemByRfxAndVdCd(Map param) {
		return sqlSession.selectList(NAMESPACE + "findRfxBidSlctnItemByRfxAndVdCd", param);
	}
}
