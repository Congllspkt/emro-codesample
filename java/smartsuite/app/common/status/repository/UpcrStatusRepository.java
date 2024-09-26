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
public class UpcrStatusRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "upcr-status.";
	
	public void saveDraftUpcrHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftUpcrHd", param);
	}
	public void saveDraftUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftUpcrDt", param);
	}
	public void submitApprovalUpcrHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalUpcrHd", param);
	}
	public void submitApprovalUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalUpcrDt", param);
	}
	public void approveApprovalUpcrHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalUpcrHd", param);
	}
	public void approveApprovalUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalUpcrDt", param);
	}
	public void rejectApprovalUpcrHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalUpcrHd", param);
	}
	public void rejectApprovalUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalUpcrDt", param);
	}
	public void cancelApprovalUpcrHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalUpcrHd", param);
	}
	public void cancelApprovalUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalUpcrDt", param);
	}
	public void bypassApprovalUpcrHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalUpcrHd", param);
	}
	public void bypassApprovalUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalUpcrDt", param);
	}
	public List selectPrevUpcrByModUpcrDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectPrevUpcrByModUpcrDt", param);
	}
	public void closePrevUpcrByModUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "closePrevUpcrByModUpcrDt", param);
	}
	public void returnUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "returnUpcrDt", param);
	}
	public void receiveUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "receiveUpcrDt", param);
	}
	public void temporarySaveRfiUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "temporarySaveRfiUpcrDt", param);
	}
	public void requestRfiUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "requestRfiUpcrDt", param);
	}
	public void completeRfiUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "completeRfiUpcrDt", param);
	}
	public void deleteRfiItemUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "deleteRfiItemUpcrDt", param);
	}
	public void saveDraftRfxUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftRfxUpcrDt", param);
	}
	public void deleteRfxItemUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "deleteRfxItemUpcrDt", param);
	}
	public void submitApprovalRfxProgUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalRfxProgUpcrDt", param);
	}
	public void approveApprovalRfxProgUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalRfxProgUpcrDt", param);
	}
	public void rejectApprovalRfxProgUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalRfxProgUpcrDt", param);
	}
	public void cancelApprovalRfxProgUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalRfxProgUpcrDt", param);
	}
	public void bypassApprovalRfxProgUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalRfxProgUpcrDt", param);
	}
	public void startRfxUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "startRfxUpcrDt", param);
	}
	public void closeRfxUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "closeRfxUpcrDt", param);
	}
	public void dropRfxHdUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "dropRfxHdUpcrDt", param);
	}
	public void cancelRfxHdUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxHdUpcrDt", param);
	}
	public void multiRoundRfxUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "multiRoundRfxUpcrDt", param);
	}
	public void submitApprovalRfxResultUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalRfxResultUpcrDt", param);
	}
	public void approveApprovalRfxResultUpcrDtBySelectedItem(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalRfxResultUpcrDtBySelectedItem", param);
	}
	public void approveApprovalRfxResultUpcrDtByNonSelectedItem(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalRfxResultUpcrDtByNonSelectedItem", param);
	}
	public void rejectApprovalRfxResultUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalRfxResultUpcrDt", param);
	}
	public void cancelApprovalRfxResultUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalRfxResultUpcrDt", param);
	}
	public void bypassApprovalRfxResultUpcrDtBySelectedItem(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalRfxResultUpcrDtBySelectedItem", param);
	}
	public void bypassApprovalRfxResultUpcrDtByNonSelectedItem(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalRfxResultUpcrDtByNonSelectedItem", param);
	}
	public void cancelRfxResultUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxResultUpcrDt", param);
	}
	public void closeRfxResultUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "closeRfxResultUpcrDt", param);
	}
	public void createDraftPriceContractUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "createDraftPriceContractUpcrDt", param);
	}
	public void saveDraftPriceContractUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPriceContractUpcrDt", param);
	}
	public void submitApprovalPriceContractUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPriceContractUpcrDt", param);
	}
	public void approveApprovalPriceContractUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPriceContractUpcrDt", param);
	}
	public void rejectApprovalPriceContractUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPriceContractUpcrDt", param);
	}
	public void cancelApprovalPriceContractUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPriceContractUpcrDt", param);
	}
	public void bypassApprovalPriceContractUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPriceContractUpcrDt", param);
	}
	public void returnElecCntrPriceContractUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "returnElecCntrPriceContractUpcrDt", param);
	}
	public void completeElecCntrPriceContractUpcrDt(Map param) {
		sqlSession.update(NAMESPACE + "completeElecCntrPriceContractUpcrDt", param);
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

	public List<Map<String, Object>> findListReferenceDocFromUpcr(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromUPCRByUPCR",param);
    }

	public List<Map<String, Object>> findListReferenceDocFromUPCRByUpcrItemIds(Map<String, Object> param) {
		 if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))) {
			 return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromUPCRByUpcrItemIds",param);
		 }else{
			 return Collections.emptyList();
		 }
	}

	public List<Map<String, Object>> findListReferenceIds(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceIds",param);
	}

	public List<Map<String, Object>> findPriceGateItems(Map param) {
		return sqlSession.selectList(NAMESPACE+"findPriceGateItems",param);
	}

	public void updatePriceGateByRfx(Map param) {
		sqlSession.update(NAMESPACE + "updatePriceGateByRfx", param);
	}
}
