package smartsuite.app.bp.pro.po.repository;

import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.QueryFloaterInSqlSession;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class PoReceiptRepository {
	
	@Inject
	SqlSession sqlSession;
	
	@Inject
	QueryFloaterInSqlSession queryFloaterInSqlSession;
	
	private static final String NAMESPACE = "po-receipt.";
	
	public List findListPoReceipt(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPoReceipt", param);
	}
	
	public void receiptReqPo(Map<String, Object> data) {
		sqlSession.insert(NAMESPACE + "receiptReqPo", data);
	}
	
	public void receiptReq(Map receiptParam) {
		sqlSession.update(NAMESPACE + "receiptReq", receiptParam);
	}
	
	public Map findPoReceiptByUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPoReceiptByUuid", param);
	}
	
	public void returnReq(Map checkedItem) {
		sqlSession.update(NAMESPACE + "returnReq", checkedItem);
	}
	
	public void changePurcGrp(Map checkedItem) {
		sqlSession.update(NAMESPACE + "changePurcGrp", checkedItem);
	}
	
	public void insertReceiptReqPoByUprcItem(Map item) {
		sqlSession.insert(NAMESPACE + "insertReceiptReqPoByUprcItem", item);
	}
	
	public void receiptReqUprcItem(Map receiptParam) {
		sqlSession.update(NAMESPACE + "receiptReqUprcItem", receiptParam);
	}
	
	public void updateUprccntrInfoByPoReqItemRcpt(Map item) {
		sqlSession.update(NAMESPACE + "updateUprccntrInfoByPoReqItemRcpt", item);
	}
	
	public void insertPoReqItemRcptQtaAloc(Map qtaItem) {
		sqlSession.insert(NAMESPACE + "insertPoReqItemRcptQtaAloc", qtaItem);
	}
	
	public List<Map> findListPoReceiptItemByUprcItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPoReceiptItemByUprcItem", param);
	}
	
	public List<Map<String, Object>> findListUnitPriceByPoReqItemRcpt(List poReqItemRcptUuids) {
		return queryFloaterInSqlSession.selectList(NAMESPACE + "findListUnitPriceByPoReqItemRcpt", Maps.newHashMap(), poReqItemRcptUuids);
	}
	
	public List<Map<String, Object>> compareListPoReceiptUprcItemSts(Map param, Object poReqItemRcptUuids) {
		return queryFloaterInSqlSession.selectList(NAMESPACE + "compareListPoReceiptUprcItemSts", param, poReqItemRcptUuids);
	}
	
	public void returnUprcItemReq(Map checkedItem) {
		sqlSession.update(NAMESPACE + "returnUprcItemReq", checkedItem);
	}
	
	public void changeUprcItemPurcGrp(Map checkedItem) {
		sqlSession.update(NAMESPACE + "changeUprcItemPurcGrp", checkedItem);
	}
	
	public List<Map<String, Object>> findListReturnedPoReqItemRcpt(List<String> returnPoReqItemRcptUuids) {
		return queryFloaterInSqlSession.selectList(NAMESPACE + "findListReturnedPoReqItemRcpt", Maps.newHashMap(), returnPoReqItemRcptUuids);
	}
	
	public List findListPoReqItemRcptQta(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPoReqItemRcptQta", param);
	}
	
	public List<Map> findListRequestReqInfoByRfxChangeReq(Map reqData) {
		return sqlSession.selectList(NAMESPACE + "findListRequestReqInfoByRfxChangeReq", reqData);
	}
	
	public void deleteRequestReqInfoByRfxChangeReq(Map reqData) {
		sqlSession.update(NAMESPACE + "deleteRequestReqInfoByRfxChangeReq", reqData);
	}
	
	public void recoveryRequestReqInfoByRfxChangeReq(Map reqData) {
		sqlSession.update(NAMESPACE + "recoveryRequestReqInfoByRfxChangeReq", reqData);
	}
	
	public void deletePoReqItemRcptQtaAloc(Map qtaRemoveItem) {
		sqlSession.delete(NAMESPACE + "deletePoReqItemRcptQtaAloc", qtaRemoveItem);
	}
	
	public void updatePoReqItemRcptQtaAloc(Map updateItem) {
		sqlSession.update(NAMESPACE + "updatePoReqItemRcptQtaAloc", updateItem);
	}
}
