package smartsuite.app.bp.rfx.rfx.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import org.springframework.transaction.annotation.Transactional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfxVendorItemRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "rfx-vendor-item.";
	
	public void deleteRfxVendorItemByRfx(Map rfxData) {
		sqlSession.delete(NAMESPACE + "deleteRfxVendorItemByRfx", rfxData);
	}

	public void deleteRfxVendorItemByRfxItem(Map rfxItem) {
		sqlSession.delete(NAMESPACE + "deleteRfxVendorItemByRfxItem", rfxItem);
	}
	
	public void deleteRfxVendorItemByVendor(Map rfxVendor) {
		sqlSession.delete(NAMESPACE + "deleteRfxVendorItemByVendor", rfxVendor);
	}
	
	public List searchRfxVendorByRfx(Map rfxData) {
		return sqlSession.selectList(NAMESPACE + "searchRfxVendorByRfx", rfxData);
	}
	
	public List searchRfxItemByRfx(Map rfxData) {
		return sqlSession.selectList(NAMESPACE + "searchRfxItemByRfx", rfxData);
	}

	public void insertRfxVendorItem(Map rfxVendorItem) {
		sqlSession.insert(NAMESPACE + "insertRfxVendorItem", rfxVendorItem);
	}
}
