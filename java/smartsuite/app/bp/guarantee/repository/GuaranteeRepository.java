package smartsuite.app.bp.guarantee.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class GuaranteeRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "guarantee.";

	public FloaterStream largeSearchGuarRequestList(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE+"findGuarRequestList", param);
	}

	public void updateGuarSts(Map param) {
		sqlSession.update(NAMESPACE + "updateGuarSts", param);
	}

	public FloaterStream largeSearchGuarList(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE+"findGuarList", param);
	}

	public Map getOfflineBondInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getOfflineBondInfo", param);
	}

	public void updateOfflineGuarInfo(Map param) {
		sqlSession.update(NAMESPACE + "updateOfflineGuarInfo", param);
	}

	public Map findGuaranteeWithCntr(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findGuaranteeWithCntr", param);
	}

	public void insertGuarantee(Map param) {
		sqlSession.insert(NAMESPACE + "insertGuarantee", param);
	}

}
