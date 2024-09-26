package smartsuite.app.bp.pro.po.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PoItemRepository {
	/** The sql session. */
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "po-item.";
	
	/**
	 * 발주품목을 등록한다.
	 *
	 * @param param the param
	 * @Method Name : insertPoItem
	 */
	public void insertPoItem(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertPoItem", param);
	}
	

	/**
	 * 발주품목을 수정한다.
	 *
	 * @param param the param
	 * @Method Name : updatePoItem
	 */
	public void updatePoItem(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updatePoItem", param);
	}
	
	/**
	 * 발주품목 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list po item
	 * @Method Name : findListPoItem
	 */
	public FloaterStream findListPoItem(Map<String, Object> param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListPoItem", param);
	}

	/**
	 * 발주별 발주품목 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list po item
	 * @Method Name : findListPoItemByPoId
	 */
	public List<Map<String, Object>> findListPoItemByPoId(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPoItemByPoId", param);
	}

	/**
	 * 발주별 발주품목 목록을 조회한다.(발주변경시)
	 *
	 * @param param the param
	 * @return the list po item
	 * @Method Name : findListPoItemModifyByPoId
	 */
	public List<Map<String, Object>> findListPoItemModifyByPoId(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPoItemModifyByPoId", param);
	}
	
	/**
	 * 발주품목 발주종료 처리한다.
	 *
	 * @param param the param
	 * @Method Name : updatePoItemCrcEnd
	 */
	public void updatePoItemCrcEnd(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updatePoItemCrcEnd", param);
	}

	/**
	 * 발주품목 발주완료여부 수정(발주수량과 입고수량 비교)한다.
	 *
	 * @param param the param
	 * @Method Name : updatePoItemCompleteByQty
	 */
	public void updatePoItemCompleteByQty(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updatePoItemCompleteByQty", param);
	}
	
	/**
	 * 발주품목을 삭제한다.
	 *
	 * @param param the param
	 * @Method Name : deletePoItemByPoId
	 */
	public void deletePoItemByPoId(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deletePoItemByPoId", param);
	}

	/**
	 * 발주품목을 삭제한다.
	 *
	 * @param param the param
	 * @Method Name : deletePoItem
	 */
	public void deletePoItem(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deletePoItem", param);
	}


	public List<Map<String, Object>> findPrevPoItemByModification(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findPrevPoItemByModification", param);
	}


	public void updatePrevPoItemByModification(Map<String, Object> modInfo) {
		sqlSession.update(NAMESPACE + "updatePrevPoItemByModification", modInfo);
	}


	public List<Map<String, Object>> findPreviousListPoItemByPoId(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findPreviousListPoItemByPoId", param);
	}


	public List<Map<String, Object>> findListComparePoItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListComparePoItem", param);
	}


	public List<Map<String, Object>> findPoItemCompleteByAmt(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findPoItemCompleteByAmt", param);
	}


	public void updatePoItemCompleteByAmt(Map<String, Object> info) {
		 sqlSession.update(NAMESPACE + "updatePoItemCompleteByAmt", info);
	}


	public void updatePoVendor(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updatePoVendor", param);
	}


	public List<Map<String, Object>> checkGrCreatable(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "checkGrCreatable", param);
	}


	public List<Map<String, Object>> findListPoItemHistory(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPoItemHistory", param);
	}

    public List<Map<String, Object>> findListPoByRfxItemIds(Map<String, Object> data) {
		if(data.containsKey("rfx_item_uuids") && data.get("rfx_item_uuids") != null){
			return sqlSession.selectList(NAMESPACE+"findListPoByRfxItemIds",data);
		}else{
			return Collections.emptyList();
		}
    }

    public List<Map<String, Object>> findListPrItemAutoPoY(Map<String, Object> pr) {
		return sqlSession.selectList(NAMESPACE+"findListPrItemAutoPoY",pr);
    }
	
	/**
	 * 선급금요청을 위한 발주품목 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchAdvancePaymentItemByPoUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchAdvancePaymentItemByPoUuid", param);
	}
	
	/**
	 * 발주품목의 납품예정수량 초기화
	 *
	 * @param param the param
	 */
	public void updateInitAsnQuantityOfPoItem(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateInitAsnQuantityOfPoItem", param);
	}
	
	/**
	 * 발주품목 검수수량 확인
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> checkQuantityList(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "checkGrQuantity", param);
	}
	
	/**
	 * 발주품목으로 입고품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findGrItemByPoItemUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findGrItemByPoItemUuid", param);
	}
	
	public Map<String, Object> findPoEvalSetInfoByPoItemUuid(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPoEvalSetInfoByPoItemUuid", param);
	}
	
	public List<Map<String, Object>> searchProgressPaymentItemByPo(Map param) {
		return sqlSession.selectList(NAMESPACE + "searchProgressPaymentItemByPo", param);
	}

	/**
	 * 발주 품목의 입고 수량 업데이트
	 *
	 * @param param
	 */
	public int updatePoItemGrQuantity(Map<String, Object> param) {
		return sqlSession.update(NAMESPACE + "updatePoItemGrQuantity", param);
	}

	/**
	 * 발주 품목의 납품예정 요청/진행중 수량 업데이트
	 *
	 * @param param
	 */
	public int updatePoItemAsnQuantity(Map<String, Object> param) {
		return sqlSession.update(NAMESPACE + "updatePoItemAsnQuantity", param);
	}
}
