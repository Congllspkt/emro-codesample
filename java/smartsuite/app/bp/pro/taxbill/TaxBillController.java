package smartsuite.app.bp.pro.taxbill;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.taxbill.service.TaxBillService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/pro/taxbill/")
public class TaxBillController {
	
	@Inject
	TaxBillService taxBillService;
	
	/**
	 * 세금계산서 발행현황 목록 조회
	 *
	 * @param param 검색조건
	 * @return
	 */
	@RequestMapping(value = "searchTaxBill.do")
	public @ResponseBody FloaterStream searchTaxBill(@RequestBody Map param) {
		// 대용량 처리
		return taxBillService.searchTaxBill(param);
	}
	
	/**
	 * 세금계산서정보 조회
	 *
	 * @param param {taxbill_uuid}
	 * @return
	 */
	@RequestMapping(value = "findTaxBill.do")
	public @ResponseBody Map findTaxBill(@RequestBody Map param) {
		return taxBillService.findTaxBill(param);
	}
	
	/**
	 * 송장 아이디로 세금계산서 기본정보 조회
	 *
	 * @param param {inv_uuids}
	 * @return
	 */
	@RequestMapping(value = "findTaxBillDefaultDataByInvoice.do")
	public @ResponseBody Map findTaxBillDefaultDataByInvoice(@RequestBody Map param) {
		return taxBillService.findTaxBillDefaultDataByInvoice(param);
	}
	
	/**
	 * 세금계산서 역발행요청
	 *
	 * @param param {taxbillData, insertTaxBillItems, invUuids}
	 * @return
	 */
	@RequestMapping(value = "requestTaxBill.do")
	public @ResponseBody ResultMap requestTaxBill(@RequestBody Map param) {
		return taxBillService.requestTaxBill(param);
	}
	
	/**
	 * 세금계산서 역발행요청 취소(삭제)
	 *
	 * @param param taxbillData
	 * @return
	 */
	@RequestMapping(value = "deleteTaxBill.do")
	public @ResponseBody ResultMap deleteTaxBill(@RequestBody Map param) {
		return taxBillService.deleteTaxBill(param);
	}
	
	/**
	 * 세금계산서 역발행요청 다중 건 취소(삭제)
	 *
	 * @param param {tax_inv_uuids}
	 * @return
	 */
	@RequestMapping(value = "deleteTaxBills.do")
	public @ResponseBody ResultMap deleteTaxBills(@RequestBody Map param) {
		return taxBillService.deleteTaxBills(param);
	}
}
