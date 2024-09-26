package smartsuite.app.bp.rfx.auction.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class AuctionRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "auction.";
	
	/**
	 * 역경매 진행현황을 조회한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건
	 * @return list : 역경매 목록
	 * @Date : 2016. 5.24
	 * @Method Name : findListAuction
	 */
	public FloaterStream findListAuction(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListAuction", param);
	}
	
	/**
	 * 역경매 정보를 조회한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return map : 역경매 정보
	 * @Date : 2016. 5.24
	 * @Method Name : findAuction
	 */
	public Map findAuction(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAuction", param);
	}
	
	public Integer findAuctionMaxRevision(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAuctionMaxRevision", param);
	}
	
	public void insertAuction(Map param) {
		sqlSession.insert(NAMESPACE + "insertAuction", param);
	}
	
	public void updateAuction(Map param) {
		sqlSession.update(NAMESPACE + "updateAuction", param);
	}
	
	public void updateCloseDt(Map param) {
		sqlSession.update(NAMESPACE + "updateCloseDt", param);
	}
	
	public void deleteAuction(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAuction", param);
	}
	
	public Map compareAuctionHdSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "compareAuctionHdSts", param);
	}

	public int checkAuctionResultApproval(Map rfxData) {
		return sqlSession.selectOne(NAMESPACE + "checkAuctionResultApproval", rfxData);
	}

	public String findAuctionResultApprovalId(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAuctionResultApprovalId", param);
	}
	
	public void insertAuctionSlctnVd(Map slctnVd) {
		sqlSession.insert(NAMESPACE + "insertAuctionSlctnVd", slctnVd);
	}
	
	public List findListAuctionSlctnVd(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAuctionSlctnVd", param);
	}
	
	public void updateAuctionSlctnNxtPrc(Map updateItem) {
		sqlSession.update(NAMESPACE + "updateAuctionSlctnNxtPrc", updateItem);
	}
}
