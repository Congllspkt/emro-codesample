package smartsuite.app.bp.pro.shared;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.shared.service.ProSharedService;

import smartsuite.app.common.status.service.ProStatusService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PrShared 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @author Yeon-u Kim
 * @see 
 * @since 2016. 3. 11
 * @FileName ProSharedController.java
 * @package smartsuite.app.bp.pro.shared
 * @변경이력 : [2016. 3. 11] Yeon-u Kim 최초작성
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/pro/shared/**/")
public class ProSharedController {
	
	/** The pr shared service. */
	@Inject
	ProSharedService proSharedService;

	/** The processor. */
	@Inject
	ProStatusService processor;

	/**
	 * 단가계약 품목 목록 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 3. 11
	 * @Method Name : findListCateItemAndBpa
	 */
	@RequestMapping(value="findListCateItemAndBpa.do")
	public @ResponseBody List findListCateItemAndBpa(@RequestBody Map param){
		return proSharedService.findListCateItemAndBpa(param);
	}

	/**
	 * list reference doc 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 24
	 * @Method Name : findListReferenceDoc
	 */
	@RequestMapping (value = "findListReferenceDoc.do")
	public @ResponseBody List findListReferenceDoc(@RequestBody Map param) {
		return processor.findListReferenceDoc(param);
	}
	
	/**
	 * 품목 별 계약 및 발주 이력 조회
	 * 
	 * @param param : {oorg_cds, item_cd, cntr_typ}
	 * @return list
	 */
	@RequestMapping(value = "/**/findListItemContractHistory.do")
	public @ResponseBody Map findListItemContractHistory(@RequestBody Map param){
		return proSharedService.findListItemContractHistory(param);
	}
	
	/**
	 * 협력사 별 계약 및 발주 이력 조회
	 * 
	 * @param param : {oorg_cds, vd_cd, cntr_typ}
	 * @return list
	 */
	@RequestMapping(value = "/**/findListVendorContractHistory.do")
	public @ResponseBody Map findListVendorContractHistory(@RequestBody Map param){
		return proSharedService.findListVendorContractHistory(param);
	}

	/**
	 * 품목 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "/**/findItemBasicInfo.do")
	public @ResponseBody Map findItemBasicInfo(@RequestBody Map param) {
		return proSharedService.findItemBasicInfo(param);
	}

	/**
	 * 특정 품목에 대한 협력사 별 발주금액 합계, 발주단가 목록 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "/**/findVendorPoByItem.do")
	public @ResponseBody Map findListVendorPoItemTotAmt(@RequestBody Map param) {
		List<Map<String, Object>> vendorPoTotAmtList = proSharedService.findListVendorPoTotAmtByItem(param);
		List<Map<String, Object>> vendorPoItemPriceList = proSharedService.findListVendorPoItemPriceByItem(param);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("vendorPoTotAmtList", vendorPoTotAmtList);
		result.put("vendorPoItemPriceList", vendorPoItemPriceList);
		return result;
	}

	/**
	 * 협력사 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "/**/findVendorBasicInfo.do")
	public @ResponseBody Map findVendorBasicInfo(@RequestBody Map param) {
		return proSharedService.findVendorBasicInfo(param);
	}

	/**
	 * 특정 협력사에 대한 연도별 구매품목, 연도별 RFx, 연도별 구매이력 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "/**/findYearlyPoByVendor.do")
	public @ResponseBody Map findYearlyPoByVendor(@RequestBody Map param) {
		List<Map<String, Object>> yearlyPoItemList = proSharedService.findListYearlyPoItemByVendor(param);
		List<Map<String, Object>> yearlyRfxItemList = proSharedService.findListYearlyRfxItemByVendor(param);
		List<Map<String, Object>> yearlyPoTotAmtList = proSharedService.findListYearlyPoTotAmtByVendor(param);
		List<Map<String, Object>> monthlyPoTotAmtList = proSharedService.findListMonthlyPoTotAmtByVendor(param);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("yearlyPoItemList", yearlyPoItemList);
		result.put("yearlyRfxItemList", yearlyRfxItemList);
		result.put("yearlyPoTotAmtList", yearlyPoTotAmtList);
		result.put("monthlyPoTotAmtList", monthlyPoTotAmtList);
		return result;
	}
}
