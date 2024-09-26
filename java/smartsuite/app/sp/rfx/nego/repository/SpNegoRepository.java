package smartsuite.app.sp.rfx.nego.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class SpNegoRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-nego.";

	public List findNegoTargetList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findNegoTargetList", param);
	}

	public Map findNegoRfxData(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findNegoRfxData", param);
	}

	public List findNegoQtaData(Map param) {
		return sqlSession.selectList(NAMESPACE + "findNegoQtaData", param);
	}

	public void saveNegoHeader(Map negoData) {
		sqlSession.update(NAMESPACE + "saveNegoHeader", negoData);
	}

	public Map checkNegoProgSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkNegoProgSts", param);
	}

	public Map checkVdProgSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkVdProgSts", param);
	}
}
