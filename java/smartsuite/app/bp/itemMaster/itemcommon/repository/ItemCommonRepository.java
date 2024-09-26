package smartsuite.app.bp.itemMaster.itemcommon.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class ItemCommonRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "item-common.";

	public Map<String, Object> checkItemMasterDuplicate(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "checkItemMasterDuplicate", param);
	}

	public void insertItem(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertItem", param);
	}

	public void updateItem(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateItem", param);
	}

	public void insertItemOorg(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertItemOorg", param);
	}

	public int getMaxItemSeq (Map < String, Object > param){
		return sqlSession.selectOne(NAMESPACE + "getMaxItemSeq", param);
	}

	public void insertItemHistrec(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertItemHistrec", param);
	}

	public FloaterStream findListItemMasterFromSearchPopup(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListItemMasterFromSearchPopup", param);
	}

	public void insertInfoMyItem(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertInfoMyItem", param);
	}

	public void deleteInfoMyItem(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "deleteInfoMyItem", param);
	}

	public void deleteAllItemOorg(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteAllItemOorg", param);
	}

	public List<Map<String, Object>> findListItemOorg(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListItemOorg", param);
	}
	
	public Map<String, Object> findItemOorgInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findItemOorgInfo", param);
	}
}