package smartsuite.app.bp.pro.po.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class PoCntrReqRepository {
	
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "po-cntr-req.";
	
	
	public Map findPoCntrReq(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPoCntrReq", param);
	}
	
	public void insertPoCntrReq(Map poCntrReqData) {
		sqlSession.insert(NAMESPACE + "insertPoCntrReq", poCntrReqData);
	}
	
	public void updatePoCntrReq(Map poCntrReqData) {
		sqlSession.update(NAMESPACE + "updatePoCntrReq", poCntrReqData);
	}
	
	public void updateRequestStatus(Map param) {
		sqlSession.update(NAMESPACE + "updateRequestStatus", param);
	}
	
	public void deletePoCntrReq(Map param) {
		sqlSession.delete(NAMESPACE + "deletePoCntrReq", param);
	}
}
