package smartsuite.app.sp.rfx.bidnotice.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class SpBidNoticeStatsRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-bid-notice-stats.";
	
	public int getBidNoticeTotalCount(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getBidNoticeTotalCount", param);
	}
	
	public Map findRfxInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxInfo", param);
	}
	
	public List<Map> findRfxItemList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findRfxItemList", param);
	}
	
	public List<Map> findPagingBidNoticeList(Map pageInfo) {
		return sqlSession.selectList(NAMESPACE + "findPagingBidNoticeList", pageInfo);
	}
}
