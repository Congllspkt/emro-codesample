package smartsuite.app.bp.pro.gr.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class GrItemRepository {
	private static final String NAMESPACE = "gr-item.";
	
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 검수품목 상세 등록
	 *
	 * @param param
	 */
	public void insertGrItem(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertGrItem", param);
	}
	
	/**
	 * 검수품목 상세 수정
	 *
	 * @param param
	 */
	public void updateGrItem(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateGrItem", param);
	}
	
	/**
	 * 검수품목 개별 삭제
	 *
	 * @param param
	 */
	public void deleteGrItemByGrItem(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteGrItemByGrItem", param);
	}
	
	/**
	 * 검수 품목 삭제
	 *
	 * @param param
	 */
	public void deleteGrItemByGrUuid(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deleteGrItemByGrUuid", param);
	}
	
	/**
	 * 검수 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchGrItemByGrUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchGrItemByGrUuid", param);
	}
	
	/**
	 * AR 품목의 합격수량/반품수량 확인
	 *
	 * @param arItemParam
	 * @return
	 */
	public List<Map<String, Object>> searchAsnItemPassReturnQuantity(Map<String, Object> arItemParam) {
		return sqlSession.selectList(NAMESPACE + "searchAsnItemPassReturnQuantity", arItemParam);
	}
	
	/**
	 * 검수취소가능여부 확인
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> checkGrCancelable(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "checkGrCancelable", param);
	}
}
