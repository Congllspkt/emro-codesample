package smartsuite.app.bp.pro.inv.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.inv.repository.InvoiceItemRepository;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InvoiceItemService {
	@Inject
	private InvoiceItemRepository invoiceItemRepository;
	
	/**
	 * 송장 아이디로 송장 품목 삭제
	 *
	 * @param param
	 */
	public void deleteInvoiceItemByInvoice(Map<String, Object> param) {
		invoiceItemRepository.deleteInvoiceItemByInvoice(param);
	}
	
	/**
	 * 송장 품목 삭제
	 *
	 * @param invoiceItem
	 */
	public void deleteInvoiceItem(Map<String, Object> invoiceItem) {
		invoiceItemRepository.deleteInvoiceItem(invoiceItem);
	}
	
	/**
	 * 송장 품목 수정
	 *
	 * @param invoiceItem
	 */
	public void updateInvoiceItem(Map<String, Object> invoiceItem) {
		invoiceItemRepository.updateInvoiceItem(invoiceItem);
	}
	
	/**
	 * 송장 품목 생성
	 *
	 * @param invoiceItem
	 */
	public void insertInvoiceItem(Map<String, Object> invoiceItem) {
		invoiceItemRepository.insertInvoiceItem(invoiceItem);
	}
	
	/**
	 * 검수 아이템 아이디로 송장처리대상 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchInvoiceTargetItemByGrItemUuid(Map<String, Object> param) {
		return invoiceItemRepository.searchInvoiceTargetItemByGrItemUuid(param);
	}
	
	/**
	 * 송장 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchInvoiceItem(Map<String, Object> param) {
		return invoiceItemRepository.searchInvoiceItem(param);
	}
	
	/**
	 * 송장처리대상 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream searchInvoiceItemRequestTarget(Map<String, Object> param) {
		return invoiceItemRepository.searchInvoiceItemRequestTarget(param);
	}
	
	/**
	 * 세금계산서 발행대상 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchTaxBillRequestTargetItem(Map<String, Object> param) {
		return invoiceItemRepository.searchTaxBillRequestTargetItem(param);
	}
}
