package smartsuite.app.bp.pro.po.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class PoPaymentExptRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "po-payment-expectation.";
	
	public List<Map<String, Object>> findListPaymentExpectationByPoId(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPaymentExpectationByPoId", param);
	}
	
	public void deletePaymentExpectationsByPoId(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deletePaymentExpectationsByPoId", param);
	}

	public void insertPaymentExpectation(Map<String, Object> paymentPlan) {
		sqlSession.insert(NAMESPACE+"insertPaymentExpectation",paymentPlan);
	}
	
	public List<Map<String, Object>> findListPaymentExpectationByPoNoandPoRevno(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPaymentExpectationByPoNoandPoRevno", param);
	}
}
