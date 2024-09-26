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
public class InvoiceItemRepository {
	private static final String NAMESPACE = "inv-item.";
	
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 송장 아이디로 송장 품목 삭제
	 *
	 * @param param
	 */
	public void deleteInvoiceItemByInvoice(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteInvoiceItemByInvoice", param);
	}
	
	/**
	 * 송장 품목 삭제
	 *
	 * @param invoiceItem
	 */
	public void deleteInvoiceItem(Map<String, Object> invoiceItem) {
		sqlSession.delete(NAMESPACE + "deleteInvoiceItem", invoiceItem);
	}
	
	/**
	 * 송장 품목 수정
	 *
	 * @param invoiceItem
	 */
	public void updateInvoiceItem(Map<String, Object> invoiceItem) {
		sqlSession.update(NAMESPACE + "updateInvoiceItem", invoiceItem);
	}
	
	/**
	 * 송장 품목 생성
	 *
	 * @param invoiceItem
	 */
	public void insertInvoiceItem(Map<String, Object> invoiceItem) {
		sqlSession.insert(NAMESPACE + "insertInvoiceItem", invoiceItem);
	}
	
	/**
	 * 검수 아이템 아이디로 송장처리대상 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchInvoiceTargetItemByGrItemUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchInvoiceTargetItemByGrItemUuid", param);
	}
	
	/**
	 * 송장 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchInvoiceItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchInvoiceItem", param);
	}
	
	/**
	 * 송장처리대상 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream searchInvoiceItemRequestTarget(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "searchInvoiceItemRequestTarget", param);
	}
	
	/**
	 * 세금계산서 발행대상 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchTaxBillRequestTargetItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchTaxBillRequestTargetItem", param);
	}
}
