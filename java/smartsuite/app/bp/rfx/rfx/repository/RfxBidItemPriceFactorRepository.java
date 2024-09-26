package smartsuite.app.bp.rfx.rfx.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxBidItemPriceFactorRepository {
	
	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "rfx-bid-item-price-factor.";

	public List findListPrevRevBidItemPriceFactorForAgent(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPrevRevBidItemPriceFactorForAgent", param);
	}
	
	public List findListBidItemPriceFactorForAgent(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListBidItemPriceFactorForAgent", param);
	}
	
	public void insertRfxBidPriceFactor(Map rfxBidPriceFactor) {
		sqlSession.insert(NAMESPACE + "insertRfxBidPriceFactor", rfxBidPriceFactor);
	}
	
	public void updateRfxBidPriceFactor(Map rfxBidPriceFactor) {
		sqlSession.update(NAMESPACE + "updateRfxBidPriceFactor", rfxBidPriceFactor);
	}
	
	public List selectSumPriceFactorBidItems(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectSumPriceFactorBidItems", param);
	}
	
	public List findListRfxBidItemPriceFactorCompare(Map rfxInfo) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidItemPriceFactorCompare", rfxInfo);
	}
	
	public List findListBidItemPriceFactor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListBidItemPriceFactor", param);
	}

	public void updateDecryptBidPriceFactor(Map param) {
		sqlSession.update(NAMESPACE + "updateDecryptBidPriceFactor", param);
	}
}
