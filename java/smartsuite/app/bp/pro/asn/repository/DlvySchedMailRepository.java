package smartsuite.app.bp.pro.asn.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.hibernate.engine.jdbc.Size.LobMultiplier.G;

@Service
public class DlvySchedMailRepository {

    @Inject
    SqlSession sqlSession;

    private static final String NAMESPACE = "dlvy-sched-mail.";

    public List<Map> findReceiversByUsrId(Map param) {
        return sqlSession.selectList(NAMESPACE + "findReceiversByUsrId", param);
    }
}
