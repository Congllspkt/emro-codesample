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
public class RfxStatusRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-status.";
	
	public List<String> findListRfxReqRcptUuidByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxReqRcptUuidByRfx", param);
	}
	
	public List<String> selectPrItemIdByRfi(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectPrItemIdByRfi", param);
	}

	public List<String> selectUpcrItemIdByRfi(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectUpcrItemIdByRfi", param);
	}
	
	public void temporarySaveRfi(Map param) {
		sqlSession.update(NAMESPACE + "temporarySaveRfi", param);
	}
	
	public void requestRfi(Map param) {
		sqlSession.update(NAMESPACE + "requestRfi", param);
	}
	
	public void closeRfi(Map param) {
		sqlSession.update(NAMESPACE + "closeRfi", param);
	}
	
	public void completeRfi(Map param) {
		sqlSession.update(NAMESPACE + "completeRfi", param);
	}
	
	public List<String> selectPrItemIdByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectPrItemIdByRfx", param);
	}

	public List<String> selectUpcrItemIdByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectUpcrItemIdByRfx", param);
	}

	public List<Map<String, Object>> selectSrItemIdByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectSrItemIdByRfx", param);
	}
	public Map<String, Object> selectRfxHeaderForSr(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectRfxHeaderForSr", param);
	}

	public void saveDraftRfx(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftRfx", param);
	}

	public void updatePreRfxByDeleteCurrentRfx(Map param) {
		sqlSession.update(NAMESPACE + "updatePreRfxByDeleteCurrentRfx", param);
	}
	
	public void submitApprovalRfxProg(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalRfxProg", param);
	}
	
	public void approveApprovalRfxProg(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalRfxProg", param);
	}
	
	public void rejectApprovalRfxProg(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalRfxProg", param);
	}
	
	public void cancelApprovalRfxProg(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalRfxProg", param);
	}
	
	public void bypassApprovalRfxProg(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalRfxProg", param);
	}
	
	public void startRfx(Map param) {
		sqlSession.update(NAMESPACE + "startRfx", param);
	}
	
	public void closeRfx(Map param) {
		sqlSession.update(NAMESPACE + "closeRfx", param);
	}
	
	public void openRfx(Map param) {
		sqlSession.update(NAMESPACE + "openRfx", param);
	}
	
	public void reExecuteNpeEval(Map param) {
		sqlSession.update(NAMESPACE + "reExecuteNpeEval", param);
	}
	
	public void dropRfxHd(Map param) {
		sqlSession.update(NAMESPACE + "dropRfxHd", param);
	}

	public void dropRfxQtaHd(Map param) {
		sqlSession.update(NAMESPACE + "dropRfxQtaHd", param);
	}

	public void dropRfxQtaDt(Map param) {
		sqlSession.update(NAMESPACE + "dropRfxQtaDt", param);
	}
	
	public void cancelRfxHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxHd", param);
	}

	public void cancelRfxQtaHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxQtaHd", param);
	}

	public void cancelRfxQtaDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxQtaDt", param);
	}

	public void cancelRfxItems(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxItems", param);
	}

	public void multiRoundRfxHd(Map param) {
		sqlSession.update(NAMESPACE + "multiRoundRfxHd", param);
	}

	public void multiRoundRfxQtaHd(Map param) {
		sqlSession.update(NAMESPACE + "multiRoundRfxQtaHd", param);
	}

	public void multiRoundRfxQtaDt(Map param) {
		sqlSession.update(NAMESPACE + "multiRoundRfxQtaDt", param);
	}

	public void temporarySaveApprovalRfxResult(Map param) {
		sqlSession.update(NAMESPACE + "temporarySaveApprovalRfxResult", param);
	}

	public void submitApprovalRfxResult(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalRfxResult", param);
	}
	
	public List<Map> selectStlPrItemIdByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectStlPrItemIdByRfx", param);
	}
	public List<Map> selectStlUpcrItemIdByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectStlUpcrItemIdByRfx", param);
	}
	public List<Map> selectStlSrItemIdByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectStlSrItemIdByRfx", param);
	}
	public void approveApprovalRfxResult(Map param) {
		sqlSession.update(NAMESPACE + "approveApprovalRfxResult", param);
	}

	public void rejectApprovalRfxResult(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalRfxResult", param);
	}

	public void cancelApprovalRfxResult(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalRfxResult", param);
	}

	public void bypassApprovalRfxResult(Map param) {
		sqlSession.update(NAMESPACE + "bypassApprovalRfxResult", param);
	}
	
	public void startRfxEval(Map param) {
		sqlSession.update(NAMESPACE + "startRfxEval", param);
	}
	
	public void completeRfxEval(Map param) {
		sqlSession.update(NAMESPACE + "completeRfxEval", param);
	}

	public void closeRfxItems(Map param) {
		sqlSession.update(NAMESPACE + "closeRfxItems", param);
	}

	public void cancelRfxResultAprv(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxResultAprv", param);
	}

	public void cancelRfxQtaResultDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxQtaResultDt", param);
	}

	public void cancelRfxQtaResultHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxQtaResultHd", param);
	}

	public void cancelRfxResultDt(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxResultDt", param);
	}

	public void cancelRfxResultHd(Map param) {
		sqlSession.update(NAMESPACE + "cancelRfxResultHd", param);
	}
	
	public int findCountItemEndFlagByRfx(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCountItemEndFlagByRfx", param);
	}

	public int findCountItemSlctnByRfx(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCountItemSlctnByRfx", param);
	}
	
	public List<Map> findListVdSlctnByCancelRfxItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVdSlctnByCancelRfxItem", param);
	}
	
	public void updateRfxItemEnd(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxItemEnd", param);
	}
	
	public void updateRfxReqRcptByCancelRfxItem(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxReqRcptByCancelRfxItem", param);
	}

	public List selectRfxQtaResultHd(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectRfxQtaResultHd", param);
	}

	public void closeRfxQtaResultHd(Map rfxBid) {
		sqlSession.update(NAMESPACE + "closeRfxQtaResultHd", rfxBid);
	}

	public List selectRfxResultDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectRfxResultDt", param);
	}

	public void closeRfxResultDt(Map rfxItem) {
		sqlSession.update(NAMESPACE + "closeRfxResultDt", rfxItem);
	}

	public List selectRfxResultPrDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectRfxResultPrDt", param);
	}

	public List selectRfxResultUpcrDt(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectRfxResultUpcrDt", param);
	}

	public void saveNego(Map rfxData) {
		sqlSession.update(NAMESPACE + "saveNego", rfxData);
	}
	
	public void cancelNego(Map rfxData) {
		sqlSession.update(NAMESPACE + "cancelNego", rfxData);
	}

	public void negoSuccess(Map negoData) {
		sqlSession.update(NAMESPACE + "negoSuccess", negoData);
	}

	public void negoFailure(Map negoData) {
		sqlSession.update(NAMESPACE + "negoFailure", negoData);
	}

	public void updateRfxProgStsByNegoSts(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxProgStsByNegoSts", param);
	}

	public void updateCloseNego(Map param) {
		sqlSession.update(NAMESPACE + "updateCloseNego", param);
	}
	
	public void saveDraftBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftBidNoti", param);
	}
	
	public void submitApprovalBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "submitApprovalBidNoti", param);
	}
	
	public void rejectApprovalBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "rejectApprovalBidNoti", param);
	}
	
	public void cancelApprovalBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "cancelApprovalBidNoti", param);
	}
	
	public void approvalBidNotiProg(Map param) {
		sqlSession.update(NAMESPACE + "approvalBidNotiProg", param);
	}
	
	public void closeBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "closeBidNoti", param);
	}
	
	public void waitBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "waitBidNoti", param);
	}
	
	public void processBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "processBidNoti", param);
	}
	
	public void startBidNotiProg(Map param) {
		sqlSession.update(NAMESPACE + "startBidNotiProg", param);
	}
	
	public void closeBidNotiProg(Map param) {
		sqlSession.update(NAMESPACE + "closeBidNotiProg", param);
	}
	
	public void cancelBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "cancelBidNoti", param);
	}
	
	public void saveDraftBidNotiTimeChange(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftBidNotiTimeChange", param);
	}
	
	public void passBidNotiTimeChange(Map param) {
		sqlSession.update(NAMESPACE + "passBidNotiTimeChange", param);
	}
	
	public void correctPrevBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "correctPrevBidNoti", param);
	}
	
	public void progressReBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "progressReBidNoti", param);
	}
	
	public void saveSiteBriefingProgSts(Map param) {
		sqlSession.update(NAMESPACE + "saveSiteBriefingProgSts", param);
	}
	
	public void notifySiteBriefingProgSts(Map param) {
		sqlSession.update(NAMESPACE + "notifySiteBriefingProgSts", param);
	}
	
	public void cancelSiteBriefingProgSts(Map param) {
		sqlSession.update(NAMESPACE + "cancelSiteBriefingProgSts", param);
	}
	
	public void closeSiteBriefingProgSts(Map param) {
		sqlSession.update(NAMESPACE + "closeSiteBriefingProgSts", param);
	}

    public List<Map<String,Object>> findListReferenceIds(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceIds",param);
    }

	public List<Map<String, Object>> findListReferenceDocFromRFX(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromRFX",param);
	}

	public List<Map<String, Object>> findListReferenceDocFromRFXByPR(Map<String, Object> param) {
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromRFXByPR",param);
		}else{
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> findListReferenceDocFromRFXByUPCR(Map<String, Object> param) {
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromRFXByUPCR",param);
		}else{
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> findListReferenceDocFromRFXByRfxItemUuids(Map<String, Object> param) {
		if(param.containsKey("rfx_item_uuids") && param.get("rfx_item_uuids") != null){
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromRFXByRfxItemUuids",param);
		}else{
			return Collections.emptyList();
		}
	}


	public List<Map<String, Object>> findListReferenceDocFromAUCTION(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromAUCTION",param);
	}

	public List<Map<String, Object>> findListReferenceDocFromAUCTIONByPR(Map<String, Object> param) {
		if(param.containsKey("pr_item_uuids") && param.get("pr_item_uuids") != null){
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromAUCTIONByPR",param);
		}else{
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> findListReferenceDocFromAUCTIONByUPCR(Map<String, Object> param) {
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromAUCTIONByUPCR",param);
		}else{
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> findListReferenceDocFromAUCTIONByRfxItemUuids(Map<String, Object> param) {
		if(param.containsKey("rfx_item_uuids") && param.get("rfx_item_uuids") != null){
			return sqlSession.selectList(NAMESPACE+"findListReferenceDocFromAUCTIONByRfxItemUuids",param);
		}else{
			return Collections.emptyList();
		}
	}
	
	/**
	 * 협상을 통한 RFX인지 판별
	 * @param param
	 * @return
	 */
	public Boolean isNegoRfx(Map param) {
		return sqlSession.selectOne(NAMESPACE + "isNegoRfx", param);
	}
	
	/**
	 * 협상 선정 초기화
	 * @param param
	 */
	public void initNegoStlYN(Map param) {
		sqlSession.update(NAMESPACE + "initNegoStlYN", param);
	}

}
