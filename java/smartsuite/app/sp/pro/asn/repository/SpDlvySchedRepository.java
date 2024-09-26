package smartsuite.app.sp.pro.asn.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class SpDlvySchedRepository {
    private static final String NAMESPACE = "sp-dlvy-sched.";

    @Inject
    private SqlSession sqlSession;

    /**
     * 협력사 납품 일정 등록
     *
     */
    public int insertDlvySched(Map<String, Object> param) {
        return sqlSession.insert(NAMESPACE + "insertDlvySched", param);
    }

    /**
     * 협력사 납품 일정 리스트 조회
     *
     */
    public List<Map<String, Object>> findDlvySchedByPoItemUuids(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findDlvySchedByPoItemUuids", param);
    }

    /**
     * 협력사 납품 일정 변경 요청 리스트 조회
     *
     */
    public List<Map<String, Object>> findDlvySchedChgReqByDlvySchedUuids(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findDlvySchedChgReqByDlvySchedUuids", param);
    }

    /**
     * 협력사 납품 일정 변경 요청 품목 조회
     *
     */
    public List<Map<String, Object>> findDlvySchedChgRetItems(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findDlvySchedChgRetItems", param);
    }

    /**
     * 협력사 납품 일정 변경 요청 단일 조회
     *
     */
    public Map<String, Object> findOneDlvySchedChgReq(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "findOneDlvySchedChgReq", param);
    }

    /**
     * 협력사 납품 일정 수정
     */
    public int updateDlvySched(Map<String, Object> param) {
        return sqlSession.update(NAMESPACE + "updateDlvySched", param);
    }

    /**
     * 협력사 납품 일정 변경 요청 상태 수정
     *
     */
    public int updateDlvySchedChgReqSts(Map<String, Object> param) {
        return sqlSession.update(NAMESPACE + "updateDlvySchedChgReqSts", param);
    }

    /**
     * 협력사 납품 일정 변경 요청 품목 최종값 등록 (수정)
     *
     */
    public int updateDlvySchedChgPoItem(Map<String, Object> param) {
        return sqlSession.update(NAMESPACE + "updateDlvySchedChgPoItem", param);
    }

    /**
     * 협력사 납품 일정 삭제
     *
     */
    public int deleteDlvySched(Map<String, Object> param) {
        return sqlSession.update(NAMESPACE + "deleteDlvySched", param);
    }

    /**
     * 협력사 납품 일정 변경 요청 품목 삭제
     *
     */
    public int deleteDlvySchedChgPoItem(Map<String, Object> param) {
        return sqlSession.update(NAMESPACE + "deleteDlvySchedChgPoItem", param);

    }

    public boolean checkChgReqCmpld(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "checkChgReqCmpld", param);
    }
}
