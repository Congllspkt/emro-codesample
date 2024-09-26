package smartsuite.app.bp.pro.po.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PoConsortiumVedorRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "po-consortium-vd.";

	public void deletePoConsortiumByPoId(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deletePoConsortiumByPoId", param);
	}

	public List<Map<String, Object>> findListPoCsVd(Map<String, Object> csParam) {
		return sqlSession.selectList(NAMESPACE + "findListPoCsVd", csParam);
	}

	public void insertPoConsortium(Map<String, Object> insertPoCs) {
		sqlSession.insert(NAMESPACE + "insertPoConsortium", insertPoCs);
	}

	public List<Map<String, Object>> findConsortiumListByPoId(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findConsortiumListByPoId", param);
	}

	public Map<String,Object> findPoCs(Map<String, Object> csParam) {
		return sqlSession.selectOne(NAMESPACE+"findPoCs",csParam);
	}

	public void updatePoConsortium(Map<String, Object> updatePoCs) {
		sqlSession.update(NAMESPACE + "updatePoConsortium", updatePoCs);
	}
}
