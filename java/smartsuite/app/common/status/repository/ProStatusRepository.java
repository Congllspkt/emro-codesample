package smartsuite.app.common.status.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class ProStatusRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "pro-status.";
	
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
	public void saveDraftPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPoPrDt", param);
	}
	public void deletePoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "deletePoPrDt", param);
	}
	public void deletePoItemPrDt(Map param) {
		sqlSession.update(NAMESPACE + "deletePoItemPrDt", param);
	}
	public void submitApprovalPoHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPoHd", param);
	}
	public void submitApprovalPoDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPoDt", param);
	}
	public void submitApprovalPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPoPrDt", param);
	}
	public void approveApprovalPoHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPoHd", param);
	}
	public void approveApprovalPoDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPoDt", param);
	}
	public void approveApprovalPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPoPrDt", param);
	}
	public void rejectApprovalPoHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPoHd", param);
	}
	public void rejectApprovalPoDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPoDt", param);
	}
	public void rejectApprovalPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPoPrDt", param);
	}
	public void cancelApprovalPoHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPoHd", param);
	}
	public void cancelApprovalPoDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPoDt", param);
	}
	public void cancelApprovalPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPoPrDt", param);
	}
	public void bypassApprovalPoHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPoHd", param);
	}
	public void bypassApprovalPoDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPoDt", param);
	}
	public void bypassApprovalPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPoPrDt", param);
	}
	public void returnElecCntrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "returnElecCntrPoHd", param);
	}
	public void returnElecCntrPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "returnElecCntrPoPrDt", param);
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
	public void completeElecCntrPoPrDt(Map param) {
		sqlSession.update(NAMESPACE + "completeElecCntrPoPrDt", param);
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


	public void saveDraftAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftAsnHd", param);
	}
	public void requestAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "requestAsnHd", param);
	}
	public void requestAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "requestAsnDt", param);
	}
	public void requestAsnPoDt(Map param) {
		sqlSession.update(NAMESPACE + "requestAsnPoDt", param);
	}
	public void requestAsnPrDt(Map param) {
		sqlSession.update(NAMESPACE + "requestAsnPrDt", param);
	}
	public void saveDraftGrHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrHd", param);
	}
	public void saveDraftGrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrDt", param);
	}
	public void saveDraftGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrAsnHd", param);
	}
	public void saveDraftGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrAsnDt", param);
	}
	public void saveDraftGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrPoDt", param);
	}
	public void saveDraftGrPrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftGrPrDt", param);
	}
	public void deleteGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "deleteGrAsnHd", param);
	}
	public void deleteGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "deleteGrAsnDt", param);
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
	public void approveApprovalGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalGrAsnHd", param);
	}
	public void approveApprovalGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalGrAsnDt", param);
	}
	public List selectApprovalGrPoDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectApprovalGrPoDt", param);
	}
	public void approveApprovalGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalGrPoDt", param);
	}
	public List checkPoQtyWithGrQty(Map param) {
		return sqlSession.selectList(NAMESPACE + "checkPoQtyWithGrQty", param);
	}
	public void approveApprovalGrPrDtByItem(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalGrPrDtByItem", param);
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
	public void bypassApprovalGrAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalGrAsnHd", param);
	}
	public void bypassApprovalGrAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalGrAsnDt", param);
	}
	public List selectBypassApprovalGrPoDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectBypassApprovalGrPoDt", param);
	}
	public void bypassApprovalGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalGrPoDt", param);
	}
	public void bypassApprovalGrPrDtByItem(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalGrPrDtByItem", param);
	}
	public void rejectRequestAsnHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestAsnHd", param);
	}
	public void rejectRequestAsnDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestArDt", param);
	}
	public void rejectRequestArPoDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestArPoDt", param);
	}
	public void rejectRequestArPrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestArPrDt", param);
	}
	public void cancelGrHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrHd", param);
	}
	public void cancelGrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrDt", param);
	}
	public void cancelGrArHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrArHd", param);
	}
	public void cancelGrArDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrArDt", param);
	}
	public void cancelGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrPoDt", param);
	}
	public void cancelGrPrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelGrPrDt", param);
	}
	public void saveDraftServiceArHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceArHd", param);
	}
	public void saveDraftServiceArPoHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceArPoHd", param);
	}
	public void deleteServiceArPoHd(Map param) {
		sqlSession.update(NAMESPACE + "deleteServiceArPoHd", param);
	}
	public void deleteServiceArPoHdByAllArDelete(Map param) {
		sqlSession.update(NAMESPACE + "deleteServiceArPoHdByAllArDelete", param);
	}
	public List findListArHdByDeleteArPo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListArHdByDeleteArPo", param);
	}
	public void requestServiceArHd(Map param) {
		sqlSession.update(NAMESPACE + "requestServiceArHd", param);
	}
	public void requestServiceArDt(Map param) {
		sqlSession.update(NAMESPACE + "requestServiceArDt", param);
	}
	public void requestServiceArPoHd(Map param) {
		sqlSession.update(NAMESPACE + "requestServiceArPoHd", param);
	}
	public void requestServiceArPoDt(Map param) {
		sqlSession.update(NAMESPACE + "requestServiceArPoDt", param);
	}
	public void requestServiceArPrDt(Map param) {
		sqlSession.update(NAMESPACE + "requestServiceArPrDt", param);
	}
	public void saveDraftServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrHd", param);
	}
	public void saveDraftServiceGrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrDt", param);
	}
	public void saveDraftServiceGrArHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrArHd", param);
	}
	public void saveDraftServiceGrArDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrArDt", param);
	}
	public void saveDraftServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrPoHd", param);
	}
	public void saveDraftServiceGrPoDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrPoDt", param);
	}
	public void saveDraftServiceGrPrDt(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftServiceGrPrDt", param);
	}
	public void deleteServiceGrArHd(Map param) {
		sqlSession.update(NAMESPACE + "deleteServiceGrArHd", param);
	}
	public void deleteServiceGrArDt(Map param) {
		sqlSession.update(NAMESPACE + "deleteServiceGrArDt", param);
	}
	public Map selectServiceGrPoHd(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectServiceGrPoHd", param);
	}
	public void deleteServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "deleteServiceGrPoHd", param);
	}
	public void submitApprovalServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalServiceGrHd", param);
	}
	public void submitApprovalServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalServiceGrPoHd", param);
	}
	public void approveApprovalServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrHd", param);
	}
	public void approveApprovalServiceGrDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrDt", param);
	}
	public void approveApprovalServiceGrArHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrArHd", param);
	}
	public void approveApprovalServiceGrArDt(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrArDt", param);
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
	public List checkPoAmtWithGrAmt(Map param) {
		return sqlSession.selectList(NAMESPACE + "checkPoAmtWithGrAmt", param);
	}
	public void approveApprovalServiceGrPrDtByItem(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalServiceGrPrDtByItem", param);
	}
	public void rejectApprovalServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalServiceGrHd", param);
	}
	public void rejectApprovalServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalServiceGrPoHd", param);
	}
	public void cancelApprovalServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalServiceGrHd", param);
	}
	public void cancelApprovalServiceGrPoHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalServiceGrPoHd", param);
	}
	public void bypassApprovalServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrHd", param);
	}
	public void bypassApprovalServiceGrDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrDt", param);
	}
	public void bypassApprovalServiceGrArHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrArHd", param);
	}
	public void bypassApprovalServiceGrArDt(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrArDt", param);
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
	public void bypassApprovalServiceGrPrDtByItem(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalServiceGrPrDtByItem", param);
	}
	public void rejectRequestServiceArHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestServiceArHd", param);
	}
	public void rejectRequestServiceArDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestServiceArDt", param);
	}
	public void rejectRequestServiceArPoHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestServiceArPoHd", param);
	}
	public void rejectRequestServiceArPoDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestServiceArPoDt", param);
	}
	public void rejectRequestServiceArPrDt(Map param) {
		sqlSession.update(NAMESPACE + "rejectRequestServiceArPrDt", param);
	}
	public void cancelServiceGrHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrHd", param);
	}
	public void cancelServiceGrDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrDt", param);
	}
	public void cancelServiceGrArHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrArHd", param);
	}
	public void cancelServiceGrArDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelServiceGrArDt", param);
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
	public void saveDraftInvoiceIvHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftInvoiceIvHd", param);
	}
	public void confirmInvoiceIvHd(Map param) {
		sqlSession.update(NAMESPACE + "confirmInvoiceIvHd", param);
	}
	public void cancelInvoiceIvHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelInvoiceIvHd", param);
	}
	public void requestTaxBillTxHd(Map param) {
		sqlSession.update(NAMESPACE + "requestTaxBillTxHd", param);
	}
	public void requestTaxBillTxDt(Map param) {
		sqlSession.update(NAMESPACE + "requestTaxBillTxDt", param);
	}
}
