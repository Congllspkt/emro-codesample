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
public class PrStatusRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "pr-status.";
	
	public void saveDraftPrHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPrHd", param);
	}
	public void saveDraftPrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPrDt", param);
	}
	public void submitApprovalPrHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPrHd", param);
	}
	public void submitApprovalPrDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPrDt", param);
	}
	public void approveApprovalPrHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPrHd", param);
	}
	public void approveApprovalPrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPrDt", param);
	}
	public void rejectApprovalPrHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPrHd", param);
	}
	public void rejectApprovalPrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPrDt", param);
	}
	public void cancelApprovalPrHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPrHd", param);
	}
	public void cancelApprovalPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPrDt", param);
	}
	public void bypassApprovalPrHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPrHd", param);
	}
	public void bypassApprovalPrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPrDt", param);
	}
	public List selectPrevPrByModPrDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectPrevPrByModPrDt", param);
	}
	public void closePrevPrByModPrDt(Map param) {
		sqlSession.update(NAMESPACE + "closePrevPrByModPrDt", param);
	}
	public void returnPrDt(Map param) {
		sqlSession.update(NAMESPACE + "returnPrDt", param);
	}
	public void receivePrDt(Map param) {
		sqlSession.update(NAMESPACE + "receivePrDt", param);
	}
	public void temporarySaveRfiPrDt(Map param) {
		sqlSession.update(NAMESPACE + "temporarySaveRfiPrDt", param);
	}
	public void requestRfiPrDt(Map param) {
		sqlSession.update(NAMESPACE + "requestRfiPrDt", param);
	}
	public void completeRfiPrDt(Map param) {
		sqlSession.update(NAMESPACE + "completeRfiPrDt", param);
	}
	public void deleteRfiPrDt(Map param) {
		sqlSession.update(NAMESPACE + "deleteRfiPrDt", param);
	}
	public void deleteRfiItemPrDt(Map param) {
		sqlSession.update(NAMESPACE + "deleteRfiItemPrDt", param);
	}
	public void saveDraftRfxPrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftRfxPrDt", param);
	}
	public void deleteRfxPrDt(Map param) {
		sqlSession.update(NAMESPACE + "deleteRfxPrDt", param);
	}
	public void deleteRfxItemPrDt(Map param) {
		sqlSession.update(NAMESPACE + "deleteRfxItemPrDt", param);
	}
	public void submitApprovalRfxProgPrDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalRfxProgPrDt", param);
	}
	public void approveApprovalRfxProgPrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalRfxProgPrDt", param);
	}
	public void rejectApprovalRfxProgPrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalRfxProgPrDt", param);
	}
	public void cancelApprovalRfxProgPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalRfxProgPrDt", param);
	}
	public void bypassApprovalRfxProgPrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalRfxProgPrDt", param);
	}
	public void startRfxPrDt(Map param) {
		sqlSession.update(NAMESPACE + "startRfxPrDt", param);
	}
	public void closeRfxPrDt(Map param) {
		sqlSession.update(NAMESPACE + "closeRfxPrDt", param);
	}
	public void dropRfxHdPrDt(Map param) {
		sqlSession.update(NAMESPACE + "dropRfxHdPrDt", param);
	}
	public void cancelRfxHdPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxHdPrDt", param);
	}
	public void multiRoundRfxPrDt(Map param) {
		sqlSession.update(NAMESPACE + "multiRoundRfxPrDt", param);
	}
	public void submitApprovalRfxResultPrDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalRfxResultPrDt", param);
	}
	public void approveApprovalRfxResultPrDtBySelectedItem(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalRfxResultPrDtBySelectedItem", param);
	}
	public void approveApprovalRfxResultPrDtByNonSelectedItem(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalRfxResultPrDtByNonSelectedItem", param);
	}
	public void rejectApprovalRfxResultPrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalRfxResultPrDt", param);
	}
	public void cancelApprovalRfxResultPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalRfxResultPrDt", param);
	}
	public void bypassApprovalRfxResultPrDtBySelectedItem(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalRfxResultPrDtBySelectedItem", param);
	}
	public void bypassApprovalRfxResultPrDtByNonSelectedItem(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalRfxResultPrDtByNonSelectedItem", param);
	}
	public void cancelRfxResultPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxResultPrDt", param);
	}
	public void closeRfxResultPrDt(Map param) {
		sqlSession.update(NAMESPACE + "closeRfxResultPrDt", param);
	}
	public void saveDraftPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPoPrDt", param);
	}
	public void deletePoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "deletePoPrDt", param);
	}
	public void deletePoItemPrDt(Map param) {
		sqlSession.update(NAMESPACE + "deletePoItemPrDt", param);
	}
	public void submitApprovalPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPoPrDt", param);
	}
	public void approveApprovalPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPoPrDt", param);
	}
	public void rejectApprovalPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPoPrDt", param);
	}
	public void cancelApprovalPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPoPrDt", param);
	}
	public void bypassApprovalPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPoPrDt", param);
	}
	public void closePoPrDt(Map param) {
        sqlSession.update(NAMESPACE + "closePoPrDt", param);
    }
	public void returnElecCntrPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "returnElecCntrPoPrDt", param);
	}
	public void completeElecCntrPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "completeElecCntrPoPrDt", param);
	}
	public void createDraftPriceContractPrDt(Map param) {
		sqlSession.update(NAMESPACE + "createDraftPriceContractPrDt", param);
	}
	public void saveDraftPriceContractPrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPriceContractPrDt", param);
	}
	public void submitApprovalPriceContractPrDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPriceContractPrDt", param);
	}
	public void approveApprovalPriceContractPrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPriceContractPrDt", param);
	}
	public void rejectApprovalPriceContractPrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPriceContractPrDt", param);
	}
	public void cancelApprovalPriceContractPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPriceContractPrDt", param);
	}
	public void bypassApprovalPriceContractPrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPriceContractPrDt", param);
	}
	public void returnElecCntrPriceContractPrDt(Map param) {
		sqlSession.update(NAMESPACE + "returnElecCntrPriceContractPrDt", param);
	}
	public void completeElecCntrPriceContractPrDt(Map param) {
		sqlSession.update(NAMESPACE + "completeElecCntrPriceContractPrDt", param);
	}
	public void requestAsnPrDt(Map param) {
		sqlSession.update(NAMESPACE + "requestAsnPrDt", param);
	}
	public void saveDraftGrPrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrPrDt", param);
	}
	public List checkPoQtyWithGrQty(Map param) {
		return sqlSession.selectList(NAMESPACE + "checkPoQtyWithGrQty", param);
	}
	public void approveApprovalGrPrDtByItem(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalGrPrDtByItem", param);
	}
	public List selectBypassApprovalGrPoDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectBypassApprovalGrPoDt", param);
	}
	public void bypassApprovalGrPrDtByItem(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalGrPrDtByItem", param);
	}
	public void rejectRequestAsnPrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestAsnPrDt", param);
	}
	public void cancelGrPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrPrDt", param);
	}
	public void requestServiceAsnPrDt(Map param) {
		sqlSession.update(NAMESPACE + "requestServiceAsnPrDt", param);
	}
	public void saveDraftServiceGrPrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrPrDt", param);
	}
	public List checkPoAmtWithGrAmt(Map param) {
		return sqlSession.selectList(NAMESPACE + "checkPoAmtWithGrAmt", param);
	}
	public void approveApprovalServiceGrPrDtByItem(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrPrDtByItem", param);
	}
	public List selectBypassApprovalServiceGrPoDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectBypassApprovalServiceGrPoDt", param);
	}
	public void bypassApprovalServiceGrPrDtByItem(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrPrDtByItem", param);
	}
	public void rejectRequestServiceAsnPrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestServiceAsnPrDt", param);
	}
	public void cancelServiceGrPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrPrDt", param);
	}

	public List<Map<String, Object>> findListReferenceDocFromPR(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromPRByPR",param);
    }

	public List<Map<String, Object>> findListReferenceDocFromPRByPrItemIds(Map<String, Object> param) {
		 if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null) {
			 return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromPRByPrItemIds",param);
		 }else{
			 return Collections.emptyList();
		 }
	}

	public List<Map<String, Object>> findListReferenceIds(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceIds",param);
	}

}
