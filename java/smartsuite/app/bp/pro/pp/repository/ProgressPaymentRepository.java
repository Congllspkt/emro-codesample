package smartsuite.app.bp.pro.pp.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.Map;

@Service
public class ProgressPaymentRepository {
	private static final String NAMESPACE = "pp.";
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 기성요청 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream searchProgressPaymentRequestByPoNo(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "searchProgressPaymentRequestByPoNo", param);
	}
	
	/**
	 * 기성 아이디로 선급금 여부 조회
	 *
	 * @param appId
	 * @return
	 */
	public String findAdvancePaymentYnByGrId(String appId) {
		return sqlSession.selectOne(NAMESPACE + "findAdvancePaymentYnByGrId", appId);
	}
	
	/**
	 * 기성승인 상세정보
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findProgressPayment(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findProgressPayment", param);
	}
	
	/**
	 * 검수취소 가능여부
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> checkProgressPaymentCancelable(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "checkProgressPaymentCancelable", param);
	}
	
	/**
	 * 검수 상세 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findAdvancePaymentByGrUuid(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findAdvancePaymentByGrUuid", param);
	}
	
	public Map getProgressPaymentTypeRelatedInfo(Map<String, Object> grData) {
		return sqlSession.selectOne(NAMESPACE + "getProgressPaymentTypeRelatedInfo", grData);
	}
	
	public void updatePaymentTypeCommonCode(Map progressPayment) {
		sqlSession.update(NAMESPACE + "updatePaymentTypeCommonCode", progressPayment);
	}
}
