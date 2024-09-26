package smartsuite.app.bp.rfx.auction.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class AuctionVendorRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "auction-vendor.";
	
	public List findListAuctionVendor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionVendor", param);
	}
	
	public void insertAuctionVendor(Map param) {
		sqlSession.insert(NAMESPACE + "insertAuctionVendor", param);
	}
	
	public void updateAuctionVendor(Map param) {
		sqlSession.update(NAMESPACE + "updateAuctionVendor", param);
	}
	
	public void deleteAuctionVendorsByRfx(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAuctionVendorsByRfx", param);
	}
	
	public void deleteAuctionVendor(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAuctionVendor", param);
	}
}
