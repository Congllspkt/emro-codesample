package smartsuite.app.bp.rfx.rfi.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfiRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfi.";

	public FloaterStream findListRfi(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListRfi", param);
	}

	public FloaterStream findListRfiSubmitVendor(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListRfiSubmitVendor", param);
	}

	public Map findRfi(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfi", param);
	}

	public Map compareRfiHdSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "compareRfiHdSts", param);
	}

	public void insertRfi(Map rfiData) {
		sqlSession.insert(NAMESPACE + "insertRfi", rfiData);
	}

	public void updateRfi(Map rfiData) {
		sqlSession.update(NAMESPACE + "updateRfi", rfiData);
	}

	public void deleteRfi(Map param) {
		sqlSession.delete(NAMESPACE + "deleteRfi", param);
	}

	public Map getRfiData(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getRfiData", param);
	}

	public boolean checkRfiData(Map param) {
		return sqlSession.selectOne(NAMESPACE +"checkRfiData", param);
	}

	public void updateRfiByRfxUuid(Map param) {
		sqlSession.update(NAMESPACE + "updateRfiByRfxUuid", param);
	}
}
