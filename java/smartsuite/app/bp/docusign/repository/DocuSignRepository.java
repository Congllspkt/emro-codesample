package smartsuite.app.bp.docusign.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DocuSign 관련 처리하는 레파지토리 Class입니다.
 *
 * @FileName DocuSignRepository.java
 */
@Service
@Transactional
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DocuSignRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "docusign.";

	public Map findDocusignInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDocusignInfo", param);
	}

	public Map findDocusignInfoByEcntrId(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDocusignInfoByEcntrId", param);
	}

	public Map findRecipientInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRecipientInfo", param);
	}

	public List findListRecipient(Map param) { 
		return sqlSession.selectList(NAMESPACE + "findListRecipient", param);
	}

	public Map findCompInfo() {
		return sqlSession.selectOne(NAMESPACE + "findCompInfo");
	}
	
	public void insertDocusignInfo(Map param) {
		sqlSession.selectOne(NAMESPACE + "insertDocusignInfo", param);
	}

	public void insertDocusignUserInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertDocusignUserInfo", param);
	}

	public void deleteDocusignInfo(Map param) {
		sqlSession.delete(NAMESPACE + "deleteDocusignInfo", param);
	}

	public void deleteDocusignUserInfo(Map param) {
		sqlSession.delete(NAMESPACE + "deleteDocusignUserInfo", param);
	}

	public void insertDocusignHistory(Map param) {
		sqlSession.insert(NAMESPACE + "insertDocusignHistory", param);
	}
	
	public void insertRecipientHistory(Map param) {
		sqlSession.insert(NAMESPACE + "insertRecipientHistory", param);
	}

	public void updateEnvelopeStatus(Map param) {
		sqlSession.update(NAMESPACE + "updateEnvelopeStatus", param);
	}
	
}
