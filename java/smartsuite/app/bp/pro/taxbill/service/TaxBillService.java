package smartsuite.app.bp.pro.taxbill.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.validator.ValidatorConst;
import smartsuite.app.bp.pro.inv.service.InvoiceItemService;
import smartsuite.app.bp.pro.taxbill.validator.TaxBillValidator;
import smartsuite.app.bp.pro.taxbill.repository.TaxBillRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type TaxBill service.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class TaxBillService {
	
	@Inject
	private SharedService sharedService;
	
	@Inject
	private ProStatusService proStatusProcessor;
	
	@Inject
	private TaxBillValidator taxbillValidator;
	
	@Inject
	private TaxBillRepository taxbillRepository;
	
	@Inject
	private TaxBillItemService taxBillItemService;
	
	@Inject
	private TaxBillCommonService taxBillCommonService;
	
	@Inject
	private InvoiceItemService invoiceItemService;
	
	/**
	 * 세금계산서 발행현황 목록 조회
	 *
	 * @param param 검색조건
	 * @return
	 */
	public FloaterStream searchTaxBill(Map<String, Object> param) {
		// 대용량 처리
		return taxbillRepository.searchTaxBill(param);
	}
	
	/**
	 * 세금계산서 정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findTaxBill(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("taxbillData", taxbillRepository.findTaxBill(param));
		resultMap.put("taxbillItems", taxBillItemService.searchTaxBillItem(param));
		return resultMap;
	}
	
	/**
	 * 복수의 송장 id로 세금계산서 발행을 위한 기본 정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findTaxBillDefaultDataByInvoice(Map<String, Object> param) {
		BigDecimal totSupplyAmt = BigDecimal.ZERO;
		BigDecimal totVatAmt = BigDecimal.ZERO;
		int lno = 0;
		boolean hasNoCdItem = false;
		
		List<Map<String, Object>> items = invoiceItemService.searchTaxBillRequestTargetItem(param);
		for(Map<String, Object> item : items) {
			if(item.get("sup_amt") != null) {
				totSupplyAmt = totSupplyAmt.add((BigDecimal) item.get("sup_amt"));
			}
			if(item.get("vat_amt") != null) {
				totVatAmt = totVatAmt.add((BigDecimal) item.get("vat_amt"));
			}
			if("CDLS".equals(item.get("item_cd_crn_typ_ccd"))) {
				hasNoCdItem = true;
			}
			lno += 10;
			item.put("taxbill_lno", lno);
		}
		
		Map<String, Object> firstItem = items.get(0);
		Map<String, Object> taxbillData = Maps.newHashMap();
		taxbillData.put("oorg_cd", firstItem.get("oorg_cd"));
		taxbillData.put("purc_typ_ccd", firstItem.get("purc_typ_ccd"));
		taxbillData.put("purc_grp_cd", firstItem.get("purc_grp_cd"));
		taxbillData.put("tax_typ_ccd", firstItem.get("tax_typ_ccd"));
		taxbillData.put("pymtmeth_ccd", firstItem.get("pymtmeth_ccd"));
		taxbillData.put("erp_vd_cd", firstItem.get("erp_vd_cd"));
		taxbillData.put("vd_cd", firstItem.get("vd_cd"));
		taxbillData.put("vd_nm", firstItem.get("vd_nm"));
		taxbillData.put("vd_nm_en", firstItem.get("vd_nm_en"));
		taxbillData.put("disp_vd_nm", firstItem.get("disp_vd_nm"));
		taxbillData.put("vd_bizregno", firstItem.get("vd_bizregno"));
		taxbillData.put("vd_ceo_nm", firstItem.get("vd_ceo_nm"));
		taxbillData.put("vd_ceo_nm_en", firstItem.get("vd_ceo_nm_en"));
		taxbillData.put("disp_vd_ceo_nm", firstItem.get("disp_vd_ceo_nm"));
		taxbillData.put("vd_addr", firstItem.get("vd_addr"));
		taxbillData.put("vd_addr_en", firstItem.get("vd_addr_en"));
		taxbillData.put("disp_vd_addr", firstItem.get("disp_vd_addr"));
		taxbillData.put("vd_biztyp", firstItem.get("vd_biztyp"));
		taxbillData.put("vd_ind", firstItem.get("vd_ind"));
		taxbillData.put("sup_amt", totSupplyAmt);
		taxbillData.put("vat_amt", totVatAmt);
		taxbillData.put("ttl_amt", totSupplyAmt.add(totVatAmt));
		taxbillData.put("has_no_cd_item", hasNoCdItem ? "Y" : "N");
		
		Map<String, Object> company = findCompanyByOorgCd(firstItem);
		if(company != null) {
			taxbillData.put("buyer_nm", company.get("logic_org_nm"));
			taxbillData.put("buyer_nm_en", company.get("logic_org_nm_en"));
			taxbillData.put("disp_buyer_nm", company.get("disp_logic_org_nm"));
			taxbillData.put("buyer_bizregno", company.get("bizregno"));
			taxbillData.put("buyer_addr", company.get("addr"));
			taxbillData.put("buyer_addr_en", company.get("addr_en"));
			taxbillData.put("disp_buyer_addr", company.get("disp_addr"));
			taxbillData.put("buyer_ceo_nm", company.get("ceo_nm"));
			taxbillData.put("buyer_ceo_nm_en", company.get("ceo_nm_en"));
			taxbillData.put("disp_buyer_ceo_nm", company.get("disp_ceo_nm"));
			taxbillData.put("buyer_ind", company.get("ind"));
			taxbillData.put("buyer_biztyp", company.get("biztyp"));
			taxbillData.put("buyer_pic_id", company.get("pic_id"));
			taxbillData.put("buyer_pic_nm", company.get("pic_nm"));
		}
		
		Map<String, Object> vdChr = findSupplierPersonInCharge(firstItem);
		if(vdChr != null) {
			taxbillData.put("vd_pic_eml", vdChr.get("vd_pic_eml"));
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("taxbillData", taxbillData);
		resultMap.put("taxbillItems", items);
		return resultMap;
	}
	
	/**
	 * 운영조직코드로 해당 buyer company 정보 조회
	 *
	 * @param compParam
	 * @return
	 */
	private Map<String, Object> findCompanyByOorgCd(Map<String, Object> compParam) {
		/*Map<String, Object> compParam = Maps.newHashMap();
		compParam.put("oorg_cd", firstItem.get("oorg_cd"));*/
		return taxBillCommonService.findCompanyByOorgCd(compParam);
	}
	
	/**
	 * 운영조직코드, 협력사코드로 supplier 담당자 정보 조회
	 *
	 * @param vdChrParam
	 * @return
	 */
	private Map<String, Object> findSupplierPersonInCharge(Map<String, Object> vdChrParam) {
		/*Map<String, Object> vdChrParam = Maps.newHashMap();
		vdChrParam.put("oorg_cd", firstItem.get("oorg_cd"));
		vdChrParam.put("vd_cd"      , firstItem.get("vd_cd"));*/
		return taxBillCommonService.findSupplierPersonInCharge(vdChrParam);
	}
	
	/**
	 * 세금계산서 정보 저장
	 *
	 * @param param
	 * @return
	 */
	private ResultMap saveTaxBill(Map<String, Object> param) {
		Map<String, Object> taxbillData = (Map<String, Object>) param.get("taxbillData");
		List<Map<String, Object>> insertTaxBillItems = (List<Map<String, Object>>) param.get("insertTaxBillItems");
		
		String taxbillUuids = taxbillData.get("taxbill_uuid") == null ? null : taxbillData.get("taxbill_uuid").toString();
		String taxbillNo = taxbillData.get("taxbill_no") == null ? null : taxbillData.get("taxbill_no").toString();
		String taxbillPublDt = taxbillData.get("publ_dt") == null ? null : taxbillData.get("publ_dt").toString();

		if(Strings.isNullOrEmpty(taxbillNo)) {
			taxbillNo = sharedService.generateDocumentNumber("TX");
			taxbillData.put("taxbill_no", taxbillNo);
		}
		if(Strings.isNullOrEmpty(taxbillUuids)) {
			taxbillUuids = UUID.randomUUID().toString();
			taxbillData.put("taxbill_uuid", taxbillUuids);
			taxbillRepository.insertTaxBill(taxbillData);
		} else {
			taxbillRepository.updateTaxBillVendorEmail(taxbillData);
		}
		
		for(Map<String, Object> taxbillItem : insertTaxBillItems) {
			taxbillItem.put("taxbill_item_uuid", UUID.randomUUID().toString());
			taxbillItem.put("taxbill_uuid", taxbillUuids);
			taxbillItem.put("taxbill_no", taxbillNo);
			taxBillItemService.insertTaxBillItem(taxbillItem);
		}
		
		Map<String, Object> resultData = Maps.newHashMap();
		resultData.put("taxbill_uuid", taxbillUuids);
		resultData.put("publ_dt", taxbillPublDt);

		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * 세금계산서 역발행요청
	 *
	 * @param param {taxbillData, insertTaxBillItems, invUuids}
	 * @return
	 */
	public ResultMap requestTaxBill(Map<String, Object> param) {
		Map<String, Object> taxbillData = (Map<String, Object>) param.get("taxbillData");
		Map<String, Object> checkParam = Maps.newHashMap(taxbillData);
		checkParam.put("inv_uuids", param.get("invUuids"));
		
		// check validation
		ResultMap resultMap = taxbillValidator.validate(checkParam);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			resultMap = saveTaxBill(param);
			
			if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
				proStatusProcessor.requestTaxBill((Map<String, Object>) resultMap.getResultData());
			}
		}
		return resultMap;
	}
	
	/**
	 * 세금계산서 역발행요청 취소(삭제)
	 *
	 * @param param taxbillData
	 * @return
	 */
	public ResultMap deleteTaxBill(Map<String, Object> param) {
		// check validation
		ResultMap resultMap = taxbillValidator.validate(param);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			deleteTaxBillAndItems(param);
		}
		return resultMap;
	}
	
	/**
	 * 세금계산서 역발행요청 다중 건 취소(삭제)
	 *
	 * @param param {taxbill_uuids}
	 * @return
	 */
	public ResultMap deleteTaxBills(Map<String, Object> param) {
		// check validation
		ResultMap resultMap = taxbillValidator.validate(param);
		Map<String, Object> resultData = (Map<String, Object>) resultMap.getResultData();
		
		// 유효한 id 리스트
		if(resultData.get(ValidatorConst.VALID_IDS) != null && ((List<String>) resultData.get(ValidatorConst.VALID_IDS)).size() > 0) {
			Map<String, Object> deleteParam = Maps.newHashMap();
			deleteParam.put("taxbill_uuids", resultData.get(ValidatorConst.VALID_IDS));
			
			deleteTaxBillAndItems(deleteParam);
		}
		return resultMap;
	}
	
	/**
	 * 세금계산서 삭제
	 *
	 * @param param {taxbill_uuid or taxbill_uuids}
	 */
	private void deleteTaxBillAndItems(Map<String, Object> param) {
		taxBillItemService.deleteTaxBillItem(param);
		taxbillRepository.deleteTaxBill(param);
	}
	
}
