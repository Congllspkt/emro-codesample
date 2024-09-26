package smartsuite.app.bp.pro.pp.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class ProgressPaymentItemRepository {
	private static final String NAMESPACE = "pp-item.";
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 기성 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchProgressPaymentItemByGrUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchProgressPaymentItemByGrUuid", param);
	}
	
	/**
	 * 검수 품목 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchAdvancePaymentItemByGrUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchAdvancePaymentItemByGrUuid", param);
	}
}
