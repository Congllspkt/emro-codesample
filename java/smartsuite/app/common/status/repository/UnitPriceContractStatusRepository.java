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
public class UnitPriceContractStatusRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "unit-price-contract-status.";
	
	public void createDraftPriceContractHd(Map param) {
		sqlSession.update(NAMESPACE + "createDraftPriceContractHd", param);
	}
	public void saveDraftPriceContractHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPriceContractHd", param);
	}
	public void submitApprovalPriceContractHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPriceContractHd", param);
	}
	public void approveApprovalPriceContractHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPriceContractHd", param);
	}
	public void rejectApprovalPriceContractHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPriceContractHd", param);
	}
	public void cancelApprovalPriceContractHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPriceContractHd", param);
	}
	public void bypassApprovalPriceContractHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPriceContractHd", param);
	}
	public void returnElecCntrPriceContractHd(Map param) {
		sqlSession.update(NAMESPACE + "returnElecCntrPriceContractHd", param);
	}
	public void returnElecCntrPriceContractAprv(Map param) {
		sqlSession.update(NAMESPACE + "returnElecCntrPriceContractAprv", param);
	}
	public void completeElecCntrPriceContractHd(Map param) {
		sqlSession.update(NAMESPACE + "completeElecCntrPriceContractHd", param);
	}
	public void saveDraftPriceContractChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPriceContractChangeRequestHd", param);
	}
	public void submitApprovalPriceContractChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPriceContractChangeRequestHd", param);
	}
	public void approveApprovalPriceContractChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPriceContractChangeRequestHd", param);
	}
	public void rejectApprovalPriceContractChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPriceContractChangeRequestHd", param);
	}
	public void cancelApprovalPriceContractChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPriceContractChangeRequestHd", param);
	}
	public void bypassApprovalPriceContractChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPriceContractChangeRequestHd", param);
	}
	public void returnPriceContractChangeRequestHd(Map param) {
		sqlSession.update(NAMESPACE + "returnPriceContractChangeRequestHd", param);
	}
	public void saveDraftPriceContractChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftPriceContractChangeHd", param);
	}
	public void submitApprovalPriceContractChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalPriceContractChangeHd", param);
	}
	public void approveApprovalPriceContractChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalPriceContractChangeHd", param);
	}
	public void rejectApprovalPriceContractChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPriceContractChangeHd", param);
	}
	public void cancelApprovalPriceContractChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPriceContractChangeHd", param);
	}
	public void bypassApprovalPriceContractChangeHd(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalPriceContractChangeHd", param);
	}
	public void closePriceContractHd(Map param) {
		sqlSession.update(NAMESPACE + "closePriceContractHd", param);
	}

    public List<Map<String, Object>> findListReferenceDocFromContractByUprccntrItemIds(Map<String, Object> param) {
		if(param.containsKey("uprccntr_item_uuids") && param.get("uprccntr_item_uuids") != null){
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromContractByUprccntrItemIds",param);
		}else{
			return Collections.emptyList();
		}
    }

	public List<Map<String, Object>> findListReferenceDocFromContractByContract(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromContractByContract",param);
	}

	public List<Map<String, Object>> findListReferenceDocFromContractByRfxItemIds(Map<String, Object> param) {
		if(param.containsKey("rfx_item_uuids") && param.get("rfx_item_uuids") != null){
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromContractByRfxItemIds",param);
		}else{
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> findListReferenceDocFromContractByPRItemIds(Map<String, Object> param) {
		if(param.containsKey("pr_item_uuids") && !ObjectUtils.isEmpty(param.get("pr_item_uuids"))){
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromContractByPRItemIds",param);
		}else{
			return Collections.emptyList();
		}
	}

    public List<Map<String, Object>> findListReferenceDocIdsFromCONTRACT(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceDocIdsFromCONTRACT",param);
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
}
