package smartsuite.app.bp.pro.po.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class PoEvalRepository {
	
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "po-eval.";
	
	public Map<String, Object> findPoInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPoInfo", param);
	}
	
	public List<String> findPoItemCatTypByPoUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findPoItemCatTypByPoUuid", param);
	}
	
	public List<String> findPoPurcTypByPoUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findPoPurcTypByPoUuid", param);
	}
	
	public List<String> findPoSgByPoUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findPoSgByPoUuid", param);
	}
	
	public List<String> findVendorInfoByPoUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findVendorInfoByPoUuid", param);
	}
	
	public List<Map> findListGePrcsByGe(Map poData) {
		return sqlSession.selectList(NAMESPACE + "findListGePrcsByGe", poData);
	}
	
	public void deleteGePrcsEvaltrByGePrcs(Map gePrcs) {
		sqlSession.delete(NAMESPACE + "deleteGePrcsEvaltrByGePrcs", gePrcs);
	}
	
	public void deleteGePrcsByGe(Map poData) {
		sqlSession.delete(NAMESPACE + "deleteGePrcsByGe", poData);
	}
	
	public void insertGe(Map geData) {
		sqlSession.insert(NAMESPACE + "insertGe", geData);
	}
	
	public void updateGe(Map geData) {
		sqlSession.update(NAMESPACE + "updateGe", geData);
	}
	
	public void insertPoGeMapping(Map poData) {
		sqlSession.insert(NAMESPACE + "insertPoGeMapping", poData);
	}
	
	public void insertGePrcs(Map grEvalshtInfoPrcs) {
		sqlSession.insert(NAMESPACE + "insertGePrcs", grEvalshtInfoPrcs);
	}
	
	public void insertGePrcsEvaltr(Map gePrcsEvaltr) {
		sqlSession.insert(NAMESPACE + "insertGePrcsEvaltr", gePrcsEvaltr);
	}
	
	public void deleteGe(Map param) {
		sqlSession.delete(NAMESPACE + "deleteGe", param);
	}
	
	public void deletePoGeMapping(Map param) {
		sqlSession.delete(NAMESPACE + "deletePoGeMapping", param);
	}
}
