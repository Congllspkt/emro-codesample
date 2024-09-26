package smartsuite.app.bp.rfx.rfx.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxMailWorkRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-mail-work.";
	
	public List<Map<String, Object>> findListRfxVendorListAndHistory(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxVendorListAndHistory", param);
	}
	
	public Map<String, Object> findRfxVendorHistoryProcess(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxVendorHistoryProcess", param);
	}
}
