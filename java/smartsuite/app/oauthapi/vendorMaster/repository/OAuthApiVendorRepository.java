package smartsuite.app.oauthapi.vendorMaster.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class OAuthApiVendorRepository {
	
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "oauth-vendor-master.";
	
	
	public List<Map> findListVendor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendor", param);
	}
}
