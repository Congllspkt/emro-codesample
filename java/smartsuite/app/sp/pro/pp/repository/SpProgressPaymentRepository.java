package smartsuite.app.sp.pro.pp.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.Map;

@Service
public class SpProgressPaymentRepository {
    private static final String NAMESPACE = "sp-pp.";
    @Inject
    private SqlSession sqlSession;

    /**
     * 기성요청 목록 조회
     * @param param
     * @return
     */
    public FloaterStream searchProgressPaymentRequest(Map<String, Object> param) {
        return new QueryFloaterStream(sqlSession, NAMESPACE + "searchProgressPaymentRequest", param);
    }
}
