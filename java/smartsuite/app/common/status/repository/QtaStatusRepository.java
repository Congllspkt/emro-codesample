package smartsuite.app.common.status.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class QtaStatusRepository {
    @Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "qta-status.";


    public void bypassApprovalQta(Map<String, Object> qta) {
        sqlSession.update(NAMESPACE+"bypassApprovalQta",qta);
    }

    public void saveDraftQta(Map qta) {
        sqlSession.update(NAMESPACE+"saveDraftQta",qta);
    }

    public void submitApprovalQta(Map param) {
        sqlSession.update(NAMESPACE+"submitApprovalQta",param);
    }

    public void cancelApprovalQta(Map param) {
        sqlSession.update(NAMESPACE+"cancelApprovalQta",param);
    }

    public void rejectApprovalQta(Map param) {
        sqlSession.update(NAMESPACE+"rejectApprovalQta",param);
    }

    public void approveApprovalQta(Map param) {
        sqlSession.update(NAMESPACE+"approveApprovalQta",param);
    }

}
