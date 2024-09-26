package smartsuite.app.bp.rfx.rfi.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfiVendorRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfi-vendor.";

	public void deleteRfiVendor(Map rfiVendor) {
		sqlSession.delete(NAMESPACE + "deleteRfiVendor", rfiVendor);
	}

	public void insertRfiVendor(Map rfiVendor) {
		sqlSession.insert(NAMESPACE + "insertRfiVendor", rfiVendor);
	}

	public void updateRfiVendor(Map rfiVendor) {
		sqlSession.update(NAMESPACE + "updateRfiVendor", rfiVendor);
	}

	public void deleteRfiVendorByRfi(Map param) {
		sqlSession.delete(NAMESPACE + "deleteRfiVendorByRfi", param);
	}
	
	public List findListRfiVendor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfiVendor", param);
	}
}
