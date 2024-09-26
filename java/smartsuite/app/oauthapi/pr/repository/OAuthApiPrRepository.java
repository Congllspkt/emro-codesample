package smartsuite.app.oauthapi.pr.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class OAuthApiPrRepository {
	
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "oauth-pr.";
	
	
	public List<Map> findListPrHeader(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPrHeader", param);
	}
	
	public List<Map> findListPrItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPrItem", param);
	}
}
