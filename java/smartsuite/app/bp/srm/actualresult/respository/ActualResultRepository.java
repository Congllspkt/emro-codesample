package smartsuite.app.bp.srm.actualresult.respository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class ActualResultRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "actual-result.";

	public FloaterStream findListVdAcres(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListVdAcres", param);
	}

	public FloaterStream findListSgVendorInfo(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListSgVendorInfo", param);
	}

	public void insertVdAcresList(Map param) {
		sqlSession.insert(NAMESPACE + "insertVdAcresList", param);
	}

	public void updateVdAcresList(Map param) {
		sqlSession.update(NAMESPACE + "updateVdAcresList", param);
	}

	public void deleteVdAcresList(Map param) {
		sqlSession.delete(NAMESPACE + "deleteVdAcresList", param);
	}

	public Map findSgVdNm(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSgVdNm", param);
	}
}
