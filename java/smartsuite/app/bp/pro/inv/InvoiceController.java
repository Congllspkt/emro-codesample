package smartsuite.app.bp.pro.inv;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.inv.service.InvoiceService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/pro/inv/")
public class InvoiceController {
	
	@Inject
	InvoiceService invoiceService;
	
	/**
	 * 송장처리대상 품목 조회
	 *
	 * @param param 검색조건
	 * @return
	 */
	@RequestMapping(value = "searchInvoiceItemRequestTarget.do")
	public @ResponseBody FloaterStream searchInvoiceItemRequestTarget(@RequestBody Map param) {
		// 대용량 처리
		return invoiceService.searchInvoiceItemRequestTarget(param);
	}
	
	/**
	 * 송장현황 목록 조회
	 *
	 * @param param 검색조건
	 * @return
	 */
	@RequestMapping(value = "searchInvoice.do")
	public @ResponseBody FloaterStream searchInvoice(@RequestBody Map param) {
		// 대용량 처리
		return invoiceService.searchInvoice(param);
	}
	
	/**
	 * 송장정보 조회
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	@RequestMapping(value = "findInvoice.do")
	public @ResponseBody Map findInvoice(@RequestBody Map param) {
		return invoiceService.findInvoice(param);
	}
	
	/**
	 * GR 품목 아이디로 송장 기본정보 조회
	 *
	 * @param param {gr_item_uuids}
	 * @return
	 */
	@RequestMapping(value = "findInvoiceDefaultDataByGrItems.do")
	public @ResponseBody Map findInvoiceDefaultDataByGrItems(@RequestBody Map param) {
		return invoiceService.findInvoiceDefaultDataByGrItems(param);
	}
	
	/**
	 * 송장 임시저장
	 *
	 * @param param {invoiceData, insertInvoiceItems, updateInvoiceItems, deleteInvoiceItems, grItemIds}
	 * @return
	 */
	@RequestMapping(value = "saveDraftInvoice.do")
	public @ResponseBody ResultMap saveDraftInvoice(@RequestBody Map param) {
		return invoiceService.saveDraftInvoice(param);
	}
	
	/**
	 * 송장 확정
	 *
	 * @param param {invoiceData, insertInvoiceItems, updateInvoiceItems, deleteInvoiceItems, grItemIds}
	 * @return
	 */
	@RequestMapping(value = "confirmInvoice.do")
	public @ResponseBody ResultMap confirmInvoice(@RequestBody Map param) {
		return invoiceService.confirmInvoice(param);
	}
	
	/**
	 * 송장 확정취소
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	@RequestMapping(value = "cancelInvoice.do")
	public @ResponseBody ResultMap cancelInvoice(@RequestBody Map param) {
		return invoiceService.cancelInvoice(param);
	}
	
	/**
	 * 송장 다중 건 확정취소
	 *
	 * @param param {inv_uuids}
	 * @return
	 */
	@RequestMapping(value = "cancelInvoices.do")
	public @ResponseBody ResultMap cancelInvoices(@RequestBody Map param) {
		return invoiceService.cancelInvoices(param);
	}
	
	/**
	 * 송장 삭제
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	@RequestMapping(value = "deleteInvoice.do")
	public @ResponseBody ResultMap deleteInvoice(@RequestBody Map param) {
		return invoiceService.deleteInvoice(param);
	}
	
	/**
	 * 세금계산서 발행대상 목록 조회
	 *
	 * @param param 검색조건
	 * @return
	 */
	@RequestMapping(value = "searchTaxBillRequestTarget.do")
	public @ResponseBody FloaterStream searchTaxBillRequestTarget(@RequestBody Map param) {
		// 대용량 처리
		return invoiceService.searchTaxBillRequestTarget(param);
	}

	/**
	 * 송장 반려
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	@RequestMapping(value = "returnInvoice.do")
	public @ResponseBody ResultMap returnInvoice(@RequestBody Map param) {
		return invoiceService.returnInvoice(param);
	}

}
