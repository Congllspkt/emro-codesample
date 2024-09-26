package smartsuite.app.common.cert.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class CertMgtRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "certMgt.";
	
	public QueryFloaterStream largeFindCertTargetList(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findCertTargetList", param);
	}
	
	public QueryFloaterStream largeFindCertList(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findCertList", param);
	}
	
	public Map findOrgCertInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findOrgCertInfo", param);
	}
	
	public void insertCertInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertCertInfo", param);
	}
	
	public void updateCertInfo(Map param) {
		sqlSession.update(NAMESPACE + "updateCertInfo", param);
	}
	
	public void removeCertInfo(Map param) {
		sqlSession.delete(NAMESPACE + "removeCertInfo", param);
	}

	public Map findOperOrgInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findOperOrgInfo", param); //인증서가 등록되어있는 운영조직들 검사
	}

	public List getCertExpirationEmailTargetList(Map param) {
		return sqlSession.selectList(NAMESPACE + "getCertExpirationEmailTargetList", param);
	}

	public Map findCertInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCertInfo", param);
	}

	public void saveBizRegNo(Map param) {
		sqlSession.update(NAMESPACE + "saveBizRegNo", param);
	}
	public List getCertFileUploadDate(Map param) {
		return sqlSession.selectList(NAMESPACE + "getCertFileUploadDate", param);
	}

}
