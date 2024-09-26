package smartsuite.app.bp.rfx.rfx.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class RfxVendorRcmdRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-vendor-rcmd.";
	
	/**
	 * RFx 업체 추천 옵션 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public List findListRfxRcmdOption(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxRcmdOption", param);
	}
	
	/**
	 * RFx 업체 추천 대상 조회
	 *
	 * @param param
	 * @return
	 */
	public List findListRfxRcmdTargVendor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxRcmdTargVendor", param);
	}
	
	public List<String> findListVendorByQtaItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorByQtaItem", param);
	}
	
	public List<String> findListVendorByStlItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorByStlItem", param);
	}
	
	public List<String> findListVendorByQtaSG(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorByQtaSG", param);
	}
	
	public List<String> findListVendorByStlSG(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorByStlSG", param);
	}
	
	public List<Map> searchRfxVendorRcmdByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "searchRfxVendorRcmdByRfx", param);
	}
}
