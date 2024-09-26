package smartsuite.app.common.shared.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfxSharedRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-shared.";
	
	public List<Map> findListProStatusForMainProgressBarView(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListProStatusForMainProgressBarView", param);
	}
	
	public List<Map> findListProSubStatusForMainProgressBarView(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListProSubStatusForMainProgressBarView", param);
	}
	
	public Map findVendorBasicInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findVendorBasicInfo", param);
	}
	
	public List<Map> findListYearlyRfxItemByVendor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListYearlyRfxItemByVendor", param);
	}
	
	public Map findItemBasicInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findItemBasicInfo", param);
	}
	public List findListPic(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPic", param);
	}
    
    public Map findRfxInviteVendor(Map param) {
        return sqlSession.selectOne(NAMESPACE + "findRfxInviteVendor", param);
    }
    
}
