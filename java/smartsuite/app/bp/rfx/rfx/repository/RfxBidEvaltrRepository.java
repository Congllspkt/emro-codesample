package smartsuite.app.bp.rfx.rfx.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxBidEvaltrRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-bid-evaltr.";
	
	public void insertRfxBidEvaltr(Map param) {
		sqlSession.insert(NAMESPACE + "insertRfxBidEvaltr", param);
	}
	
	public List<Map> findListRfxBidEvaltr(Map rfxInfo) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidEvaltr", rfxInfo);
	}
	
	public void deleteRfxBidEvaltrByRfxBidUuid(Map submRfxBid) {
		sqlSession.delete(NAMESPACE + "deleteRfxBidEvaltrByRfxBidUuid", submRfxBid);
	}
}
