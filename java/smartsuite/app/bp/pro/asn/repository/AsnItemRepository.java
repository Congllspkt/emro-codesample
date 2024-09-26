package smartsuite.app.bp.pro.asn.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class AsnItemRepository {
	private static final String NAMESPACE = "asn-item.";
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 검수요청의 상세품목 목록조회
	 *
	 * @param param the param
	 * @return the list
	 */
	public List<Map<String, Object>> searchAsnItemByAsnUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchAsnItemByAsnUuid", param);
	}
	
	/**
	 * 기성요청별 품목 목록
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchProgressPaymentRequestItemByAsnUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchProgressPaymentRequestItemByAsnUuid", param);
	}
	
	/**
	 * 선급금 요청 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchAdvancePaymentRequestItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchAdvancePaymentRequestItem", param);
	}
	
	/**
	 * 검수요청아이디로 입고품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchGrItemByAsnUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchGrItemByAsnUuid", param);
	}
	
	/**
	 * AR 품목의 합격수량/반품수량 수정
	 *
	 * @param arItem
	 */
	public void updateAsnItemPassReturnQuantity(Map<String, Object> arItem) {
		sqlSession.update(NAMESPACE + "updateAsnItemPassReturnQuantity", arItem);
	}

	/**
	 * 발주 품목의 ASN 요청/진행중 수량
	 *
	 * @param param
	 */
	public List<Map<String, Object>> searchAsnItemQty(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchAsnItemQty", param);
	}
}
