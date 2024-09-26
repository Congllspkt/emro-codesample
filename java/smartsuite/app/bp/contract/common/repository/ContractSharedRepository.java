package smartsuite.app.bp.contract.common.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class ContractSharedRepository {

    @Inject
    SqlSession sqlSession;

	private static final String NAMESPACE = "contract-shared.";

    public Map findContractTypeCount(Map param) {
        return sqlSession.selectOne(NAMESPACE+"findContractTypeCount",param);
    }

    public int findNonStandardContractCount(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE+"findNonStandardContractCount", param);
    }

    public Map<String,Object> findContractExpirationNotification() {
        return sqlSession.selectOne(NAMESPACE+"findContractExpirationNotification");
    }
}
