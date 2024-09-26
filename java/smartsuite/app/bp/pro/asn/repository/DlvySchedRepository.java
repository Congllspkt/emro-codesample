package smartsuite.app.bp.pro.asn.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class DlvySchedRepository {
    private static final String NAMESPACE = "dlvy-sched.";
    @Inject
    private SqlSession sqlSession;

    public int insertDlvySchedChgReq(Map<String, Object> param) {
        return sqlSession.insert(NAMESPACE + "insertDlvySchedChgReq", param);
    }

    public int insertDlvySchedChgPoItem(Map<String, Object> param) {
        return sqlSession.insert(NAMESPACE + "insertDlvySchedChgPoItem", param);
    }

    public List<Map<String, Object>> findDlvySchedByDlvySchedUuid(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findDlvySchedByDlvySchedUuid", param);
    }
    public List<Map<String, Object>> findDlvySchedChgReqByDlvySchedUuids(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findDlvySchedChgReqByDlvySchedUuids", param);
    }

    public List<Map<String, Object>> findListDlvySchedByPoItemUuids(Map param) {
        return sqlSession.selectList(NAMESPACE + "findListDlvySchedByPoItemUuids", param);
    }

    public List<Map<String, Object>> findDlvySchedChgReqDetail(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findDlvySchedChgReqDetail", param);
    }

    public int updateDlvySchedSts(Map<String, Object> param) {
        return sqlSession.update(NAMESPACE + "updateDlvySchedSts", param);
    }


}
