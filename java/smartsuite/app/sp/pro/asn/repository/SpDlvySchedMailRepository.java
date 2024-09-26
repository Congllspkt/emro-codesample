package smartsuite.app.sp.pro.asn.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class SpDlvySchedMailRepository {

    @Inject
    SqlSession sqlSession;

    private static final String NAMESPACE = "sp-dlvy-sched-mail.";

    public List<Map<String, Object>> findReceiversByUsrId(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findReceiversByUsrId", param);
    }
}
