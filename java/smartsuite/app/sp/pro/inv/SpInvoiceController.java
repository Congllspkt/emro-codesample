package smartsuite.app.sp.pro.inv;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.pro.inv.service.SpInvoiceService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/sp/pro/inv/")
public class SpInvoiceController {
	
	@Inject
	SpInvoiceService spInvoiceService;
	
	/**
	 * 송장처리대상 품목 조회
	 *
	 * @param param 검색조건
	 * @return
	 */
	@RequestMapping(value = "searchSpInvoiceItemRequestTarget.do")
	public @ResponseBody FloaterStream searchSpInvoiceItemRequestTarget(@RequestBody Map param) {
		// 대용량 처리
		return spInvoiceService.searchInvoiceItemRequestTarget(param);
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
		return spInvoiceService.searchInvoice(param);
	}
	
	/**
	 * 송장정보 조회
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	@RequestMapping(value = "findSpInvoice.do")
	public @ResponseBody Map findSpInvoice(@RequestBody Map param) {
		return spInvoiceService.findInvoice(param);
	}
	
	/**
	 * GR 품목 아이디로 송장 기본정보 조회
	 *
	 * @param param {gr_item_uuids}
	 * @return
	 */
	@RequestMapping(value = "findSpInvoiceDefaultDataByGrItems.do")
	public @ResponseBody Map findSpInvoiceDefaultDataByGrItems(@RequestBody Map param) {
		return spInvoiceService.findInvoiceDefaultDataByGrItems(param);
	}
	
	/**
	 * 송장 확정
	 *
	 * @param param {invoiceData, insertInvoiceItems, updateInvoiceItems, deleteInvoiceItems, grItemIds}
	 * @return
	 */
	@RequestMapping(value = "requestApprovalSpInvoice.do")
	public @ResponseBody ResultMap requestApprovalSpInvoice(@RequestBody Map param) {
		return spInvoiceService.requestApprovalInvoice(param);
	}
	
	/**
	 * 송장 확정취소
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	@RequestMapping(value = "cancelSpInvoice.do")
	public @ResponseBody ResultMap cancelSpInvoice(@RequestBody Map param) {
		return spInvoiceService.cancelInvoice(param);
	}
	
	/**
	 * 송장 다중 건 확정취소
	 *
	 * @param param {inv_uuids}
	 * @return
	 */
	@RequestMapping(value = "cancelSpInvoices.do")
	public @ResponseBody ResultMap cancelSpInvoices(@RequestBody Map param) {
		return spInvoiceService.cancelInvoices(param);
	}
	
	/**
	 * 송장 삭제
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	@RequestMapping(value = "deleteSpInvoice.do")
	public @ResponseBody ResultMap deleteSpInvoice(@RequestBody Map param) {
		return spInvoiceService.deleteInvoice(param);
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
		return spInvoiceService.searchTaxBillRequestTarget(param);
	}
}
