package smartsuite.app.sp.eform.eformsign.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 간편서명 관련 처리하는 레파지토리 Class입니다.
 *
 * @FileName SpEFormSignRepository.java
 */
@Service
@Transactional
@SuppressWarnings({ "rawtypes" })
public class SpEFormSignRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "sp-eformsign.";

	public Map findCntrInfoByCntorId(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCntrInfoByCntorId", param);
	}

	public Map findDocumentConts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDocumentConts", param);
	}

	public Map findDocumentSignTarget(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDocumentSignTarget", param);
	}

	public void insertEFormSignValue(Map param) {
		sqlSession.insert(NAMESPACE + "insertEFormSignValue", param);
	}

	public Map findCntrInfoForHistrecPdf(String param) {
		return sqlSession.selectOne(NAMESPACE + "findCntrInfoForHistrecPdf", param);
	}

	public List findListEFormCntrHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListEFormCntrHistory", param);
	}

	public List findListSignTargetSignValue(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSignTargetSignValue", param);
	}

	public void updateCntrAtFile(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrAtFile", param);
	}
	
}