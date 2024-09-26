package smartsuite.app.bp.itemMaster.shared.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemSharedRepository {
	
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * The NAMESPACE.
	 */
	private static final String NAMESPACE = "item-shared.";
	
	public List<Map> findListCate(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCate", param);
	}
	
	public List<Map> findListCateItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCateItem", param);
	}
	
	public void insertMyItem(Map insertMyItem) {
		sqlSession.insert(NAMESPACE + "insertMyItem", insertMyItem);
	}
	
	public void deleteMyItem(Map deleteMyItem) {
		sqlSession.delete(NAMESPACE + "deleteMyItem", deleteMyItem);
	}

	public List<Map> findListCateItemWithUprcCntr(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCateItemWithUprcCntr", param);
	}

}
