package smartsuite.app.bp.cms.attrpool.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemAttributePoolRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "item-attribute-pool.";

	public FloaterStream findListAttributePool(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE +  "findListAttributePool", param);
	}

	public int deleteInfoAttributePool(Map<String, Object> attributeInfo) {
		return sqlSession.delete(NAMESPACE +  "deleteInfoAttributePool", attributeInfo);
	}

	public Map<String, Object> findInfoAttributePool(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE +  "findInfoAttributePool", param);
	}

	public List<Map<String, Object>> findListAssignedAttributePool(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE +  "findListAssignedAttributePool", param);
	}

	public void insertInfoAttributePool(Map<String, Object> info) {
		sqlSession.insert(NAMESPACE +  "insertInfoAttributePool", info);
	}

	public void updateInfoAttributePool(Map<String, Object> item) {
		sqlSession.update(NAMESPACE +  "updateInfoAttributePool", item);
	}

	public int checkCntAttrPool(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE +  "checkCntAttrPool", param);
	}
}