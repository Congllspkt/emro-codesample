package smartsuite.app.bp.rfx.auction.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class AuctionVendorItemRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "auction-vendor-item.";
	
	public void insertAuctionVendorItem(Map param) {
		sqlSession.insert(NAMESPACE + "insertAuctionVendorItem", param);
	}
	
	public void deleteAuctionVendorItemsByRfx(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAuctionVendorItemsByRfx", param);
	}
	
	public void deleteAuctionVendorItemsByRfxItem(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAuctionVendorItemsByRfxItem", param);
	}
	
	public void deleteAuctionVendorItemsByVendor(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAuctionVendorItemsByVendor", param);
	}

	public List searchRfxVendorByRfx(Map rfxData) {
		return sqlSession.selectList(NAMESPACE + "searchRfxVendorByRfx", rfxData);
	}
	
	public List searchRfxItemByRfx(Map rfxData) {
		return sqlSession.selectList(NAMESPACE + "searchRfxItemByRfx", rfxData);
	}

	public void insertRfxVendorItem(Map auctionVendor) {
		sqlSession.insert(NAMESPACE + "insertRfxVendorItem", auctionVendor);
	}
}
