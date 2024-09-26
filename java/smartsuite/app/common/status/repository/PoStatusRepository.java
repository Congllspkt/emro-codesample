package smartsuite.app.common.status.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class PoStatusRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "po-status.";
	
	public void createDraftPoHd(Map param) {
		sqlSession.update(NAMESPACE + "createDraftPoHd", param);
	}
	public void createDraftPoDt(Map param) {
		sqlSession.update(NAMESPACE + "createDraftPoDt", param);
	}
	public void saveDraftPoHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPoHd", param);
	}
	public void saveDraftPoDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPoDt", param);
	}
	public void submitApprovalPoHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPoHd", param);
	}
	public void submitApprovalPoDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPoDt", param);
	}
	public void approveApprovalPoHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPoHd", param);
	}
	public void approveApprovalPoDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPoDt", param);
	}
	public void rejectApprovalPoHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPoHd", param);
	}
	public void rejectApprovalPoDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPoDt", param);
	}
	public void cancelApprovalPoHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPoHd", param);
	}
	public void cancelApprovalPoDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPoDt", param);
	}
	public void bypassApprovalPoHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPoHd", param);
	}
	public void bypassApprovalPoDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPoDt", param);
	}
	public void returnElecCntrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "returnElecCntrPoHd", param);
	}
	public void returnElecCntrPoAprv(Map param) {
		sqlSession.update(NAMESPACE + "returnElecCntrPoAprv", param);
	}
	public void completeElecCntrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "completeElecCntrPoHd", param);
	}
	public void completeElecCntrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "completeElecCntrPoDt", param);
	}
	public void acceptPoByVendor(Map param) {
		sqlSession.update(NAMESPACE + "acceptPoByVendor", param);
	}
	public void rejectPoByVendor(Map param) {
		sqlSession.update(NAMESPACE + "rejectPoByVendor", param);
	}
	public void saveDraftPoChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPoChangeRequestHd", param);
	}
	public void submitApprovalPoChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPoChangeRequestHd", param);
	}
	public void approveApprovalPoChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPoChangeRequestHd", param);
	}
	public void rejectApprovalPoChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPoChangeRequestHd", param);
	}
	public void cancelApprovalPoChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPoChangeRequestHd", param);
	}
	public void bypassApprovalPoChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPoChangeRequestHd", param);
	}
	public void returnPoChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "returnPoChangeRequestHd", param);
	}
	public void saveDraftPoChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPoChangeHd", param);
	}
	public void submitApprovalPoChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPoChangeHd", param);
	}
	public void approveApprovalPoChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPoChangeHd", param);
	}
	public void rejectApprovalPoChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPoChangeHd", param);
	}
	public void cancelApprovalPoChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPoChangeHd", param);
	}
	public void bypassApprovalPoChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPoChangeHd", param);
	}
	public void closePoHd(Map param) {
		sqlSession.update(NAMESPACE + "closePoHd", param);
	}
	public void closePoDt(Map param) {
		sqlSession.update(NAMESPACE + "closePoDt", param);
	}
	public void requestAsnPoDt(Map param) {
		sqlSession.update(NAMESPACE + "requestAsnPoDt", param);
	}
	public void saveDraftGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrPoDt", param);
	}
	public List selectApprovalGrPoDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectApprovalGrPoDt", param);
	}
	public void approveApprovalGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalGrPoDt", param);
	}
	public List selectBypassApprovalGrPoDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectBypassApprovalGrPoDt", param);
	}
	public void bypassApprovalGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalGrPoDt", param);
	}
	public void rejectRequestAsnPoDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestAsnPoDt", param);
	}
	public void cancelGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrPoHd", param);
	}
	public void cancelGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrPoDt", param);
	}
	public void saveDraftServiceAsnPoHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceAsnPoHd", param);
	}
	public void deleteServiceAsnPoHd(Map param) {
		sqlSession.update(NAMESPACE + "deleteServiceAsnPoHd", param);
	}
	public void deleteServiceAsnPoHdByAllAsnDelete(Map param) {
		sqlSession.update(NAMESPACE + "deleteServiceAsnPoHdByAllAsnDelete", param);
	}
	public List findListAsnHdByDeleteAsnPo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAsnHdByDeleteAsnPo", param);
	}
	public void requestServiceAsnPoHd(Map param) {
		sqlSession.update(NAMESPACE + "requestServiceAsnPoHd", param);
	}
	public void requestServiceAsnPoDt(Map param) {
		sqlSession.update(NAMESPACE + "requestServiceAsnPoDt", param);
	}
	public void saveDraftServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrPoHd", param);
	}
	public void saveDraftServiceGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrPoDt", param);
	}
	public Map selectServiceGrPoHd(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectServiceGrPoHd", param);
	}
	public void deleteServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "deleteServiceGrPoHd", param);
	}
	public void submitApprovalServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalServiceGrPoHd", param);
	}
	public List selectApprovalServiceGrPoHd(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectApprovalServiceGrPoHd", param);
	}
	public void approveApprovalServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrPoHd", param);
	}
	public List selectApprovalServiceGrPoDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectApprovalServiceGrPoDt", param);
	}
	public void approveApprovalServiceGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrPoDt", param);
	}
	public void rejectApprovalServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalServiceGrPoHd", param);
	}
	public void cancelApprovalServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalServiceGrPoHd", param);
	}
	public List selectBypassApprovalServiceGrPoHd(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectBypassApprovalServiceGrPoHd", param);
	}
	public void bypassApprovalServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrPoHd", param);
	}
	public List selectBypassApprovalServiceGrPoDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectBypassApprovalServiceGrPoDt", param);
	}
	public void bypassApprovalServiceGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrPoDt", param);
	}
	public void rejectRequestServiceAsnPoHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestServiceAsnPoHd", param);
	}
	public void rejectRequestServiceAsnPoDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestServiceAsnPoDt", param);
	}
	public void cancelServiceGrPoHdPrePay(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrPoHdPrePay", param);
	}
	public Map selectCancelServiceGrPoHd(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectCancelServiceGrPoHd", param);
	}
	public void cancelServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrPoHd", param);
	}
	public void cancelServiceGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrPoDt", param);
	}
	public void cancelServiceGrPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrPrDt", param);
	}

    public List<Map<String, Object>> findListReferenceDocFromPO(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromPO",param);
    }

	public List<Map<String, Object>> findListReferenceIdsFromPO(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceIds",param);
	}

	public List<Map<String, Object>> findListReferenceDocFromPOByRfxItemIds(Map<String, Object> param) {
		if(param.containsKey("rfx_item_uuids") && param.get("rfx_item_uuids") != null) {
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromPOByRfxItemIds",param);
		}else{
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> findListReferenceDocFromPOByPoItemIds(Map<String, Object> param) {
		if(param.containsKey("po_item_uuids") && param.get("po_item_uuids") != null) {
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromPOByPoItemIds",param);
		}else{
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> findListReferenceDocFromPOByPrItemIds(Map<String, Object> param) {
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null) {
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromPOByPrItemIds",param);
		}else{
			return Collections.emptyList();
		}
	}

	public void createEcntr(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "createEcntr", param);
	}
	public void terminateEcntr(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "terminateEcntr", param);
	}
	public void deleteEcntr(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deleteEcntr", param);
	}

    public void updateCreatePoPr(Map<String, Object> param) {
		sqlSession.update(NAMESPACE+"updateCreatePoPr",param);
    }
	
	public Map findPoReqRcptByPo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPoReqRcptByPo", param);
	}
	
	public List<Map> findListPoReqItemRcptByPo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPoReqItemRcptByPo", param);
	}
	
	public void updatePoReqRcptBySaveDraftPo(Map poReqRcptInfo) {
		sqlSession.update(NAMESPACE + "updatePoReqRcptBySaveDraftPo", poReqRcptInfo);
	}
	
	public void updatePoReqItemRcptQtaBySaveDraftPo(Map poReqItemRcpt) {
		sqlSession.update(NAMESPACE + "updatePoReqItemRcptQtaBySaveDraftPo", poReqItemRcpt);
	}
	
	public void updatePoReqItemRcptBySaveDraftPo(Map poReqItemRcpt) {
		sqlSession.update(NAMESPACE + "updatePoReqItemRcptBySaveDraftPo", poReqItemRcpt);
	}
	
	public void updatePoReqRcptByDeletePo(Map poReqRcptInfo) {
		sqlSession.update(NAMESPACE + "updatePoReqRcptByDeletePo", poReqRcptInfo);
	}
	
	public void deletePoReqItemRcptQtaByDeletePo(Map poReqItemRcpt) {
		sqlSession.update(NAMESPACE + "deletePoReqItemRcptQtaByDeletePo", poReqItemRcpt);
	}
	
	public void updatePoReqItemRcptByDeletePo(Map poReqItemRcpt) {
		sqlSession.update(NAMESPACE + "updatePoReqItemRcptByDeletePo", poReqItemRcpt);
	}
	
	public void updatePoByRequestChgContractDelete(Map poData) {
		sqlSession.update(NAMESPACE + "updatePoByRequestChgContractDelete", poData);
	}
	
	public void updatePoByRequestChgContract(Map poData) {
		sqlSession.update(NAMESPACE + "updatePoByRequestChgContract", poData);
	}
	
	public void updatePoByRequestTrmnContract(Map poData) {
		sqlSession.update(NAMESPACE + "updatePoByRequestTrmnContract", poData);
	}
	
	public void updatePoReqRcptCompleteByCompletePo(Map poReqRcptInfo) {
		sqlSession.update(NAMESPACE + "updatePoReqRcptCompleteByCompletePo", poReqRcptInfo);
	}
	
	public void updatePoReqItemRcptQtaCompleteByCompletePo(Map poReqItemRcpt) {
		sqlSession.update(NAMESPACE + "updatePoReqItemRcptQtaCompleteByCompletePo", poReqItemRcpt);
	}
	
	public void updatePoReqItemRcptCompleteByCompletePo(Map itemParam) {
		sqlSession.update(NAMESPACE + "updatePoReqItemRcptCompleteByCompletePo", itemParam);
	}
}
