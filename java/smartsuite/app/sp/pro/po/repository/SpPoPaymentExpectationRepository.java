package smartsuite.app.sp.pro.po.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class SpPoPaymentExpectationRepository {

    @Inject
    private SqlSession sqlSession;

    private static final String NAMESPACE = "sp-po-payment-expectation.";

    public List findListPaymentPlanByPoId(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findListPaymentPlanByPoId", param);
    }
}
