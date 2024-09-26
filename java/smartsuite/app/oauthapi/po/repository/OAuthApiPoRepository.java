package smartsuite.app.oauthapi.po.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service
public class OAuthApiPoRepository {
	
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "oauth-po.";
	
	public void createPo(Map header) {
		sqlSession.insert(NAMESPACE + "createPo", header);
	}
	
	public void createPoItem(Map item) {
		sqlSession.insert(NAMESPACE + "createPoItem", item);
	}
}
