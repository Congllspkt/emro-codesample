package smartsuite.app.bp.eform.eformsign.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * 간편서명 관련 처리하는 레파지토리 Class입니다.
 *
 * @FileName EFormSignRepository.java
 */
@Service
@Transactional
@SuppressWarnings({ "rawtypes" })
public class EFormSignRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "eformsign.";

	public Map findDocumentSignTarget(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDocumentSignTarget", param);
	}

	public void updateCntrGrpCd(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrGrpCd", param);
	}

	public void insertEFormSignValue(Map param) {
		sqlSession.insert(NAMESPACE + "insertEFormSignValue", param);
	}

	public Map findDocumentConts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDocumentConts", param);
	}

	public List<Map> findDocumentUserList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findDocumentUserList", param);
	}
	
	public void updateDgtlsgnCntrdocTmpl(Map param) {
		sqlSession.update(NAMESPACE + "updateDgtlsgnCntrdocTmpl", param);
	}

	public List<Map> findListUseClause(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUseClause", param);
	}

	public void updateDocCntrConts(Map param) {
		sqlSession.update(NAMESPACE + "updateDocCntrConts", param);
	}
}