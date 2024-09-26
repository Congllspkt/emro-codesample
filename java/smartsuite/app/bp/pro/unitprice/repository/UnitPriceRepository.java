package smartsuite.app.bp.pro.unitprice.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class UnitPriceRepository {
	
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "unit-price.";
	
	/**
	 * 단가정보 목록을 조회한다.
	 *
	 * @param param
	 */
	public List<Map> findListUnitPrice(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUnitPrice", param);
	}
	
	public void deleteUnitPrice(Map param) {
		sqlSession.delete(NAMESPACE + "deleteUnitPrice", param);
	}
	
	public void updateUnitPriceTerms(Map param) {
		sqlSession.update(NAMESPACE + "updateUnitPriceTerms", param);
	}
	
	public void updateUnitPriceLastN(Map param) {
		sqlSession.update(NAMESPACE + "updateUnitPriceLastN", param);
	}
	
	public void insertUnitPrice(Map param) {
		sqlSession.insert(NAMESPACE + "insertUnitPrice", param);
	}
	
	public List findListUnitPriceHistrec(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUnitPriceHistrec", param);
	}
	
	public Map findListUnitPriceHistrecSummary(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findListUnitPriceHistrecSummary", param);
	}
	
	public List<String> findListVendorRcmdItemUprccntr(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorRcmdItemUprccntr", param);
	}
	
	public List<String> findListVendorRcmdSgUprccntr(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorRcmdSgUprccntr", param);
	}

    public List<Map<String, Object>> findListBpaYnAndMutlVdYnByItemCd(Map param) {
		return sqlSession.selectList(NAMESPACE+"findListBpaYnAndMutlVdYnByItemCd",param);
    }
	
	public List<Map> findListUnitPriceByItemAndOorg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUnitPriceByItemAndOorg", param);
	}
	
	public List<Map> findListUnitPriceQtaInfoByItemAndOorg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUnitPriceQtaInfoByItemAndOorg", param);
	}

	public List<Map<String, Object>> findMailSendTargetList(Map param) {
		return sqlSession.selectList(NAMESPACE+"findMailSendTargetList", param);
	}
}
