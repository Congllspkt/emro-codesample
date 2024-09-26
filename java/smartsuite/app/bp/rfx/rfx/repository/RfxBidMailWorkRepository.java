package smartsuite.app.bp.rfx.rfx.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfxBidMailWorkRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-bid-mail-work.";
	
	/**
	 * RFX QTA 조회
	 * @param param
	 */
	public Map findRfxBidByMailWork(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxBidByMailWork",param);
	}
	
	public List<Map> findRfxBidItemsByMailWork(Map rfxBidExcelData) {
		return sqlSession.selectList(NAMESPACE + "findRfxBidItemsByMailWork", rfxBidExcelData);
	}
}
