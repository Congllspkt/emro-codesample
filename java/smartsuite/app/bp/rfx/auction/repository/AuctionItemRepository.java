package smartsuite.app.bp.rfx.auction.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class AuctionItemRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "auction-item.";
	
	public List findListAuctionItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionItem", param);
	}
	
	public void insertAuctionItem(Map param) {
		sqlSession.insert(NAMESPACE + "insertAuctionItem", param);
	}
	
	public void updateAuctionItem(Map param) {
		sqlSession.update(NAMESPACE + "updateAuctionItem", param);
	}
	
	public void deleteAuctionItemsByRfx(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAuctionItemsByRfx", param);
	}
	
	public void deleteAuctionItem(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAuctionItem", param);
	}

	public List selectAuctionItemStlInfo(Map auctionInfo) {
		return sqlSession.selectList(NAMESPACE + "selectAuctionItemStlInfo", auctionInfo);
	}

	public void updateAuctionItemStlInfo(Map auctionItemStl) {
		sqlSession.update(NAMESPACE + "updateAuctionItemStlInfo", auctionItemStl);
	}
}
