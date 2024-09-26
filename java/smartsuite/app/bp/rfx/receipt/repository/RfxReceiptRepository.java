package smartsuite.app.bp.rfx.receipt.repository;

import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.QueryFloaterInSqlSession;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfxReceiptRepository {
	
	@Inject
	SqlSession sqlSession;
	
	@Inject
	QueryFloaterInSqlSession queryFloaterInSqlSession;
	
	private static final String NAMESPACE = "rfx-receipt.";
	
	public List findListRfxReceipt(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxReceipt", param);
	}
	
	public List findRfxDefaultDataByReqRcpts(Map param) {
		return sqlSession.selectList(NAMESPACE + "findRfxDefaultDataByReqRcpts", param);
	}
	
	public void insertReceiptReqRfx(Map item) {
		sqlSession.insert(NAMESPACE + "insertReceiptReqRfx", item);
	}
	
	public void receiptReq(Map checkedItem) {
		sqlSession.update(NAMESPACE + "receiptReq", checkedItem);
	}
	
	public void returnReq(Map checkedItem) {
		sqlSession.update(NAMESPACE + "returnReq", checkedItem);
	}
	
	public void changePurcGrp(Map checkedItem) {
		sqlSession.update(NAMESPACE + "changePurcGrp", checkedItem);
	}
	
	public List<Map<String, Object>> compareListRfxReceiptSts(Map param, Object rfxReqRcptUuids) {
		return queryFloaterInSqlSession.selectList(NAMESPACE + "compareListRfxReceiptSts", param, rfxReqRcptUuids);
	}
	
	public void updateProgressStatus(Map param) {
		sqlSession.update(NAMESPACE + "updateProgressStatus", param);
	}
	
	public void updateRfxNxtPrcsStats(Map receiptItem) {
		sqlSession.update(NAMESPACE + "updateRfxNxtPrcsStats", receiptItem);
	}
	
	public void updateNxtPrcsReq(Map touchlessItem) {
		sqlSession.update(NAMESPACE + "updateNxtPrcsReq", touchlessItem);
	}
	
	public void insertQtaNxtReqPrcs(Map touchlessItem) {
		sqlSession.insert(NAMESPACE + "insertQtaNxtReqPrcs", touchlessItem);
	}

	public void updateQtaNxtReqPrcs(Map item) {
		sqlSession.update(NAMESPACE + "updateQtaNxtReqPrcs", item);
	}
	
	public List<Map<String, Object>> findListUnitPriceByRfxReqRcpt(List<String> rfxReqRcptUuids) {
		return queryFloaterInSqlSession.selectList(NAMESPACE + "findListUnitPriceByRfxReqRcpt", Maps.newHashMap(), rfxReqRcptUuids);
	}
	
	public void updateUnitPriceReqCompleted(Map param) {
		sqlSession.update(NAMESPACE + "updateUnitPriceReqCompleted", param);
	}
	
	public List findListRfxReqRcptQta(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxReqRcptQta", param);
	}
	
	public Map findReqRcptByOtherUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findReqRcptByOtherUuid", param);
	}
	
	public List<Map> findListRequestReqInfoByChangeReq(Map reqData) {
		return sqlSession.selectList(NAMESPACE + "findListRequestReqInfoByChangeReq", reqData);
	}
	
	public void deleteRequestReqInfoByChangeReq(Map reqData) {
		sqlSession.update(NAMESPACE + "deleteRequestReqInfoByChangeReq", reqData);
	}
	
	public void recoveryRequestReqInfoByChangeReq(Map reqData) {
		sqlSession.update(NAMESPACE + "recoveryRequestReqInfoByChangeReq", reqData);
	}

	public List<Map<String, Object>> findListRfxReceiptByRfxReqRcptUuids(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxReceiptByRfxReqRcptUuids", param);
	}

	public List<Map<String, Object>> findListRfxReceiptByOtherUuids(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxReceiptByOtherUuids", param);
	}

	public List<Map> findListRfxRcptItemSpotPrItemByItem(Map purcCntr) {
		return sqlSession.selectList(NAMESPACE + "findListRfxRcptItemSpotPrItemByItem", purcCntr);
	}

	public void deleteRequestReqInfoByReqUuid(Map item) {
		sqlSession.update(NAMESPACE + "deleteRequestReqInfoByReqUuid", item);
	}

	public Map findRfxDefaultDataByRfxReqUuid(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxDefaultDataByRfxReqUuid", param);
	}
}
