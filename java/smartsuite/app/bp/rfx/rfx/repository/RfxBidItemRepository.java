package smartsuite.app.bp.rfx.rfx.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxBidItemRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-bid-item.";
	
	public List findListRfxItemWithBidItems(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxItemWithBidItems", param);
	}
	
	public List findListRfxItemWithPrevRevBidItemForAgent(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxItemWithPrevRevBidItemForAgent", param);
	}
	
	public List findListRfxQtaItemForAgent(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxQtaItemForAgent", param);
	}
	
	public void insertRfxBidItem(Map param) {
		sqlSession.insert(NAMESPACE + "insertRfxBidItem", param);
	}
	
	public void updateRfxBidItem(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxBidItem", param);
	}
	
	public void updateRfxBidItemAmt(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxBidItemAmt", param);
	}
	
	public Map selectSumPriceBid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectSumPriceBid", param);
	}
	
	public List selectRfxBidItemStl(Map rfxBid) {
		return sqlSession.selectList(NAMESPACE + "selectRfxBidItemStl", rfxBid);
	}

	public void updateRfxBidItemStl(Map qtaItem) {
		sqlSession.update(NAMESPACE + "updateRfxBidItemStl", qtaItem);
	}
	
	public void updateDecryptBidItemAmount(Map param) {
		sqlSession.update(NAMESPACE + "updateDecryptBidItemAmount", param);
	}
}
