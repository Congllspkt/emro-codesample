package smartsuite.app.bp.pro.inv.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceRepository {
	private static final String NAMESPACE = "inv.";
	
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 송장 삭제
	 *
	 * @param param
	 */
	public void deleteInvoice(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteInvoice", param);
	}
	
	/**
	 * 송장 취소사유
	 *
	 * @param updateParam
	 */
	public void updateInvoiceCancelCause(Map<String, Object> updateParam) {
		sqlSession.update(NAMESPACE + "updateInvoiceCancelCause", updateParam);
	}
	
	/**
	 * 송장 수정
	 *
	 * @param invoiceData
	 */
	public void updateInvoice(Map<String, Object> invoiceData) {
		sqlSession.update(NAMESPACE + "updateInvoice", invoiceData);
	}
	
	/**
	 * 송장 생성
	 *
	 * @param invoiceData
	 */
	public void insertInvoice(Map<String, Object> invoiceData) {
		sqlSession.insert(NAMESPACE + "insertInvoice", invoiceData);
	}
	
	/**
	 * 송장 상세 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findInvoice(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findInvoice", param);
	}
	
	/**
	 * 송장현황 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream searchInvoice(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "searchInvoice", param);
	}
	
	
	/**
	 * 세금계산서 발행대상 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream searchTaxBillRequestTarget(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "searchTaxBillRequestTarget", param);
	}
	
	/**
	 * 송장 리스트 상태 비교
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> compareInvoiceListStatus(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "compareInvoiceListStatus", param);
	}
	
	/**
	 * 송장 상태 비교
	 * @param param
	 * @return
	 */
	public Map<String, Object> compareInvoiceStatus(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "compareInvoiceStatus", param);
	}
	
	/**
	 * 완료된 입고/기성 품목 확인
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> checkCompletedGrItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "checkCompletedGrItem", param);
	}

	/**
	 * 세금계산서 발행여부 확인
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> checkTaxbillStatus(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "checkTaxbillStatus", param);
	}

	/**
	 * 송장 반려사유
	 *
	 * @param updateParam
	 */
	public void updateInvoiceReturnCause(Map<String, Object> updateParam) {
		sqlSession.update(NAMESPACE + "updateInvoiceReturnCause", updateParam);
	}

	/**
	 * 송장 품목 조회 (입고/기성 기준)
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchInvoiceItemsByGrIds(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "searchInvoiceItemsByGrIds", param);
	}
}
