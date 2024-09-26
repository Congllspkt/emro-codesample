package smartsuite.app.common.status.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class GrStatusRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "gr-status.";
	
	public void saveDraftGrHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrHd", param);
	}
	public void saveDraftGrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrDt", param);
	}
	
	public void submitGrEval(Map param) {
		sqlSession.update(NAMESPACE + "submitGrEval", param);
	}
	
	public void completeGrEval(Map param) {
		sqlSession.update(NAMESPACE + "completeGrEval", param);
	}
	
	public void submitApprovalGrHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalGrHd", param);
	}
	public void approveApprovalGrHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalGrHd", param);
	}
	public void approveApprovalGrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalGrDt", param);
	}
	public void rejectApprovalGrHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalGrHd", param);
	}
	public void cancelApprovalGrHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalGrHd", param);
	}
	public void bypassApprovalGrHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalGrHd", param);
	}
	public void bypassApprovalGrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalGrDt", param);
	}
	public void cancelGrHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrHd", param);
	}
	public void cancelGrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrDt", param);
	}
	public void saveDraftServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrHd", param);
	}
	public void saveDraftServiceGrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrDt", param);
	}
	public void submitApprovalServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalServiceGrHd", param);
	}
	public void approveApprovalServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrHd", param);
	}
	public void approveApprovalServiceGrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrDt", param);
	}
	public void rejectApprovalServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalServiceGrHd", param);
	}
	public void cancelApprovalServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalServiceGrHd", param);
	}
	public void bypassApprovalServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrHd", param);
	}
	public void bypassApprovalServiceGrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrDt", param);
	}
	public void cancelServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrHd", param);
	}
	public void cancelServiceGrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrDt", param);
	}

    public List<Map<String, Object>> findListReferenceDocIdsFromGR(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceDocIdsFromGR",param);
    }

	public List<Map<String, Object>> findListReferenceDocFromGR(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromGR",param);
	}

	public List<Map<String, Object>> findListReferenceDocFromGRByPrItemIds(Map<String, Object> param) {
		if (param.containsKey("pr_item_uuids") && !ObjectUtils.isEmpty(param.get("pr_item_uuids"))) {
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromGRByPrItemIds",param);
		}else{
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> findListReferenceDocFromGRByPoItemIds(Map<String, Object> param) {
		 if(param.containsKey("po_item_uuids") && param.get("po_item_uuids") != null) {
			 return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromGRByPoItemIds",param);
		 }else{
			 return Collections.emptyList();
		 }
	}

	public List<Map<String, Object>> findListReferenceDocFromGRByAsnItemIds(Map<String, Object> param) {
		if (param.containsKey("asn_item_uuids") && param.get("asn_item_uuids") != null) {
			return sqlSession.selectList(NAMESPACE + "findListReferenceDocFromGRByAsnItemIds", param);
		}else{
			return Collections.emptyList();
		}
	}
}
