package smartsuite.app.bp.rfx.bidnotice.repository;

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
public class BidNoticeEvalRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "bid-notice-eval.";
	
	public FloaterStream findListBidNotiEval(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListBidNotiEval", param);
	}
	
	public List<Map> findListBidNotiEvalDetailByOpen(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListBidNotiEvalDetailByOpen", param);
	}
	
	public List<Map> findListAttachCnt(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAttachCnt", param);
	}
	
	public List<Map> findListBidNotiEvalDetail(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListBidNotiEvalDetail", param);
	}
	
	public void disqualBidNotiEval(Map param) {
		sqlSession.update(NAMESPACE + "disqualBidNotiEval", param);
	}
	
	public void qualBidNotiEval(Map param) {
		sqlSession.update(NAMESPACE + "qualBidNotiEval", param);
	}
	
	public Map findBidNotiPartInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findBidNotiPartInfo", param);
	}
	
	public List<Map> findListBidPartedAttach(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListBidPartedAttach", param);
	}
	
	public Map compareBidNotiHdStsByEval(Map param) {
		return sqlSession.selectOne(NAMESPACE + "compareBidNotiHdStsByEval", param);
	}
	
	public int findListEvalVdCnt(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findListEvalVdCnt", param);
	}
	
	public void updateEvalRstYes(Map param) {
		sqlSession.update(NAMESPACE + "updateEvalRstYes", param);
	}
	
	public void updateEvalRstNo(Map param) {
		sqlSession.update(NAMESPACE + "updateEvalRstNo", param);
	}
	
	public void updateBidNotiEvalVd(Map param) {
		sqlSession.update(NAMESPACE + "updateBidNotiEvalVd", param);
	}
	
	public Map findBidNotiInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findBidNotiInfo", param);
	}
}
