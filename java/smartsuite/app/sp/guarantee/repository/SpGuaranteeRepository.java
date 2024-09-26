package smartsuite.app.sp.guarantee.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpGuaranteeRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-guarantee.";


	public FloaterStream LargeSearchSpGuarList(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE +"findSpGuarList", param);
	}

	public Map getOfflineBondInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getOfflineBondInfo", param);
	}

	public void saveOfflineGuarInfo(Map param) {
		sqlSession.update(NAMESPACE + "saveOfflineGuarInfo", param);
	}

	public void saveOfflineGuarCancel(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "saveOfflineGuarCancel", param);
	}

	public Map getRequestBond(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getRequestBond", param);
	}
}
