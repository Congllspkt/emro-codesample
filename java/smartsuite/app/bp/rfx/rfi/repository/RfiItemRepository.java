package smartsuite.app.bp.rfx.rfi.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfiItemRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfi-item.";

	public void deleteRfiItem(Map rfiItem) {
		sqlSession.delete(NAMESPACE + "deleteRfiItem", rfiItem);
	}

	public void insertRfiItem(Map rfiItem) {
		sqlSession.insert(NAMESPACE + "insertRfiItem", rfiItem);
	}

	public void updateRfiItem(Map rfiItem) {
		sqlSession.update(NAMESPACE + "updateRfiItem", rfiItem);
	}

	public void deleteRfiItemByRfi(Map param) {
		sqlSession.delete(NAMESPACE + "deleteRfiItemByRfi", param);
	}
	
	public List findListRfiItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfiItem", param);
	}
}
