package smartsuite.app.sp.docusign.repository;

import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DocuSign 관련 처리하는 레파지토리 Class입니다.
 *
 * @FileName SpDocuSignRepository.java
 */
@Service
@Transactional
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SpDocuSignRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "sp-docusign.";

	public Map findDocusignInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDocusignInfo", param);
	}
	
	public Map findRecipientInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRecipientInfo", param);
	}

	public Map findCompInfo() {
		return sqlSession.selectOne(NAMESPACE + "findCompInfo");
	}

	public void updateDocusignStatus(Map param) {
		sqlSession.update(NAMESPACE + "updateDocusignStatus", param);
	}

}
