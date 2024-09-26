package smartsuite.app.common.status.repository;

import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class AsnStatusRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "asn-status.";
	
	public void saveDraftAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftAsnHd", param);
	}
	public void saveDraftAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftAsnDt", param);
	}
	public void requestAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "requestAsnHd", param);
	}
	public void requestAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "requestAsnDt", param);
	}
	public void saveDraftGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrAsnHd", param);
	}
	public void saveDraftGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrAsnDt", param);
	}
	public void deleteGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "deleteGrAsnHd", param);
	}
	public void deleteGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "deleteGrAsnDt", param);
	}
	public void approveApprovalGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalGrAsnHd", param);
	}
	public void approveApprovalGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalGrAsnDt", param);
	}
	public void bypassApprovalGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalGrAsnHd", param);
	}
	public void bypassApprovalGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalGrAsnDt", param);
	}
	public void rejectRequestAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestAsnHd", param);
	}
	public void rejectRequestAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestAsnDt", param);
	}
	public void cancelGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrAsnHd", param);
	}
	public void cancelGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrAsnDt", param);
	}
	public void saveDraftServiceAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceAsnHd", param);
	}
	public void requestServiceAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "requestServiceAsnHd", param);
	}
	public void requestServiceAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "requestServiceAsnDt", param);
	}
	public void saveDraftServiceGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrAsnHd", param);
	}
	public void saveDraftServiceGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrAsnDt", param);
	}
	public void deleteServiceGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "deleteServiceGrAsnHd", param);
	}
	public void deleteServiceGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "deleteServiceGrAsnDt", param);
	}
	public void approveApprovalServiceGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrAsnHd", param);
	}
	public void approveApprovalServiceGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrAsnDt", param);
	}
	public void bypassApprovalServiceGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrAsnHd", param);
	}
	public void bypassApprovalServiceGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrAsnDt", param);
	}
	public void rejectRequestServiceAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestServiceAsnHd", param);
	}
	public void rejectRequestServiceAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestServiceAsnDt", param);
	}
	public void cancelServiceGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrAsnHd", param);
	}
	public void cancelServiceGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrAsnDt", param);
	}

	public void updateAsnEvalProgSts(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateAsnEvalProgSts", param);
	}
}
