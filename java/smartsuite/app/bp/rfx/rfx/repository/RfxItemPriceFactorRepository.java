package smartsuite.app.bp.rfx.rfx.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfxItemPriceFactorRepository {
	
	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "rfx-item-price-factor.";
	
	public List searchRfxItemWithPriceGroup(Map param) {
		return sqlSession.selectList(NAMESPACE + "searchRfxItemWithPriceGroup", param);
	}
	
	public List searchPriceGroupByItemGrpTyp(Map param) {
		return sqlSession.selectList(NAMESPACE + "searchPriceGroupByItemGrpTyp", param);
	}
	
	public List searchPriceFactorByRfxItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "searchPriceFactorByRfxItem", param);
	}
	
	public void insertPriceFactor(Map param) {
		sqlSession.insert(NAMESPACE + "insertPriceFactor", param);
	}
	
	public void deletePriceFactorByRfxItem(Map param) {
		sqlSession.delete(NAMESPACE + "deletePriceFactorByRfxItem", param);
	}
	
	public void deletePriceFactorByRfx(Map param) {
		sqlSession.delete(NAMESPACE + "deletePriceFactorByRfx", param);
	}
	
	public int checkRfxItemNotMappedPriFact(String rfxId) {
		return sqlSession.selectOne(NAMESPACE + "checkRfxItemNotMappedPriFact", rfxId);
	}

	public void copyRfxItemPriceFactors(Map param) {
		sqlSession.update(NAMESPACE + "copyRfxItemPriceFactors", param);
	}
}
