package smartsuite.app.oauthapi.itemMaster.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class OAuthApiItemRepository {
	
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "oauth-item-master.";
	
	
	public List<Map> findListItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListItem", param);
	}
	
	public List<Map> findListItemCategory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListItemCategory", param);
	}
}
