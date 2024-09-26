package smartsuite.app.bp.rfx.nego.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class NegoItemRepository {
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "nego-item.";
	

	public List findNegoDetail(Map param) {
		return sqlSession.selectList(NAMESPACE + "findNegoDetail", param);
	}

	public List findNegoBidDetail(Map param) {
		return sqlSession.selectList(NAMESPACE + "findNegoBidDetail", param);
	}
	
	public List searchNegoRfxBidItemStl(Map rfxBid){
		return sqlSession.selectList(NAMESPACE + "searchNegoRfxBidItemStl", rfxBid);
	}
	
	public void insertNegoDetail(Map item) {
		sqlSession.insert(NAMESPACE + "insertNegoDetail", item);
	}
	
	public void deleteNegoTargetDetailSts(Map item) {
		sqlSession.update(NAMESPACE + "cancelNegoTargetDetail", item);
	}
	
	public void updateNegoPrice(Map item) {
		sqlSession.update(NAMESPACE + "updateNegoPrice", item);
	}
}
