package smartsuite.app.bp.cms.attrasgn.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemAttributeAsgnRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "item-attribute-asgn.";


	public List<Map<String, Object>> findListAsgnAttrMapping(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListAsgnAttrMapping", param);
	}

	public List<String> findListChildrenItemCatCd(String param) {
		return sqlSession.selectList(NAMESPACE + "findListChildrenItemCatCd", param);
	}

	public void insertInfoItemcatIattr(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertInfoItemcatIattr", param);
	}
	public void updateInfoItemcatIattr(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateInfoItemcatIattr", param);
	}

	public void updateInfoItemcatIattrToChildren(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateInfoItemcatIattrToChildren", param);
	}

	public Map<String, Object>findInfoAsgnAttr(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoAsgnAttr", param);
	}

	public List findListItemcatIattrChildren(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListItemcatIattrChildren", param);
	}

	public void updateInfoItemcatIattrChildren(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateInfoItemcatIattrChildren", param);
	}

	public void insertInfoItemcatIattrChildren(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "insertInfoItemcatIattrChildren", param);
	}

	public void deleteInfoItemcatIattrChildren(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteInfoItemcatIattrChildren", param);
	}

	public void deleteInfoItemcatIattr(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteInfoItemcatIattr", param);
	}

	public List<Map<String, Object>> findListAttrMapping(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListAttrMapping", param);
	}
}
