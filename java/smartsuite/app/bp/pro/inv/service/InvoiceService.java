package smartsuite.app.bp.pro.inv.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.validator.ValidatorConst;
import smartsuite.app.bp.pro.inv.repository.InvoiceRepository;
import smartsuite.app.bp.pro.inv.validator.InvoiceValidator;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class InvoiceService {
	
	@Inject
	private SharedService sharedService;
	
	@Inject
	private InvoiceValidator invoiceValidator;
	
	@Inject
	private ProStatusService proStatusProcessor;
	
	@Inject
	private InvoiceItemService invoiceItemService;
	
	@Inject
	private InvoiceRepository invoiceRepository;
	
	/**
	 * 송장처리대상 품목 조회
	 *
	 * @param param 검색조건
	 * @return
	 */
	public FloaterStream searchInvoiceItemRequestTarget(Map<String, Object> param) {
		// 대용량 처리
		return invoiceItemService.searchInvoiceItemRequestTarget(param);
	}
	
	/**
	 * 송장현황 목록 조회
	 *
	 * @param param 검색조건
	 * @return
	 */
	public FloaterStream searchInvoice(Map<String, Object> param) {
		// 대용량 처리
		return invoiceRepository.searchInvoice(param);
	}
	
	/**
	 * 송장정보 조회
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	public Map<String, Object> findInvoice(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("invoiceData", invoiceRepository.findInvoice(param));
		resultMap.put("invoiceItems", invoiceItemService.searchInvoiceItem(param));
		return resultMap;
	}
	
	/**
	 * GR 품목 아이디로 송장 기본정보 조회
	 *
	 * @param param {gr_item_uuids}
	 * @return
	 */
	public Map<String, Object> findInvoiceDefaultDataByGrItems(Map<String, Object> param) {
		BigDecimal totGrAmt = BigDecimal.ZERO;
		String finalGrDt = null;
		int lno = 0;
		boolean hasNoCdItem = false;
		List<Map<String, Object>> invoiceItems = invoiceItemService.searchInvoiceTargetItemByGrItemUuid(param);
		for(Map<String, Object> invoiceItem : invoiceItems) {
			if(invoiceItem.get("gr_dt") != null) {
				String grDate = invoiceItem.get("gr_dt").toString();
				if(Strings.isNullOrEmpty(finalGrDt) || finalGrDt.compareTo(grDate) < 0) {
					finalGrDt = grDate;
				}
			}
			if(invoiceItem.get("gr_amt") != null) {
				BigDecimal grAmt = (BigDecimal) invoiceItem.get("gr_amt");
				totGrAmt = totGrAmt.add(grAmt);
			}
			if("CDLS".equals(invoiceItem.get("item_cd_crn_typ_ccd"))) {
				hasNoCdItem = true;
			}
			lno += 10;
			invoiceItem.put("inv_lno", lno);
		}
		
		Map<String, Object> firstItem = invoiceItems.get(0);
		
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		
		Map<String, Object> invoiceData = Maps.newHashMap();
		invoiceData.put("oorg_cd", firstItem.get("oorg_cd"));
		invoiceData.put("purc_typ_ccd", firstItem.get("purc_typ_ccd"));
		invoiceData.put("purc_grp_cd", firstItem.get("purc_grp_cd"));
		invoiceData.put("tax_typ_ccd", firstItem.get("tax_typ_ccd"));
		invoiceData.put("pymtmeth_ccd", firstItem.get("pymtmeth_ccd"));
		invoiceData.put("cur_ccd", firstItem.get("cur_ccd"));
		invoiceData.put("erp_vd_cd", firstItem.get("erp_vd_cd"));
		invoiceData.put("vd_cd", firstItem.get("vd_cd"));
		invoiceData.put("vd_nm", firstItem.get("vd_nm"));
		invoiceData.put("vd_nm_en", firstItem.get("vd_nm_en"));
		invoiceData.put("disp_vd_nm", firstItem.get("disp_vd_nm"));
		invoiceData.put("accg_yr", year);
		invoiceData.put("fnl_gr_dt", finalGrDt);
		invoiceData.put("sup_amt", totGrAmt);
		invoiceData.put("has_no_cd_item", hasNoCdItem ? "Y" : "N");
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("invoiceData", invoiceData);
		resultMap.put("invoiceItems", invoiceItems);
		return resultMap;
	}
	
	/**
	 * 송장 정보 저장
	 *
	 * @param param {invoiceData, insertInvoiceItems, updateInvoiceItems, deleteInvoiceItems}
	 * @return
	 */
	private ResultMap saveInvoice(Map<String, Object> param) {
		Map<String, Object> invoiceData = (Map<String, Object>) param.get("invoiceData");
		List<Map<String, Object>> insertInvoiceItems = (List<Map<String, Object>>) param.get("insertInvoiceItems");
		List<Map<String, Object>> updateInvoiceItems = (List<Map<String, Object>>) param.get("updateInvoiceItems");
		List<Map<String, Object>> deleteInvoiceItems = (List<Map<String, Object>>) param.get("deleteInvoiceItems");
		
		String invUuid = invoiceData.get("inv_uuid") == null ? null : invoiceData.get("inv_uuid").toString();
		String invNo = invoiceData.get("inv_no") == null ? null : invoiceData.get("inv_no").toString();
		
		if(Strings.isNullOrEmpty(invNo)) {
			invNo = sharedService.generateDocumentNumber("IV");
			invoiceData.put("inv_no", invNo);
		}
		if(Strings.isNullOrEmpty(invUuid)) {
			invUuid = UUID.randomUUID().toString();
			invoiceData.put("inv_uuid", invUuid);
			invoiceRepository.insertInvoice(invoiceData);
		} else {
			invoiceRepository.updateInvoice(invoiceData);
		}
		
		for(Map<String, Object> invoiceItem : insertInvoiceItems) {
			invoiceItem.put("inv_item_uuid", UUID.randomUUID().toString());
			invoiceItem.put("inv_uuid", invUuid);
			invoiceItemService.insertInvoiceItem(invoiceItem);
		}
		for(Map<String, Object> invoiceItem : updateInvoiceItems) {
			invoiceItemService.updateInvoiceItem(invoiceItem);
		}
		for(Map<String, Object> invoiceItem : deleteInvoiceItems) {
			invoiceItemService.deleteInvoiceItem(invoiceItem);
		}
		
		Map<String, Object> resultData = Maps.newHashMap();
		resultData.put("inv_uuid", invUuid);
		
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * 송장 임시저장
	 *
	 * @param param {invoiceData, insertInvoiceItems, updateInvoiceItems, deleteInvoiceItems, grItemUuids}
	 * @return
	 */
	public ResultMap saveDraftInvoice(Map<String, Object> param) {
		Map<String, Object> invoiceData = (Map<String, Object>) param.get("invoiceData");
		Map<String, Object> checkParam = Maps.newHashMap(invoiceData);
		checkParam.put("gr_item_uuids", param.get("grItemUuids"));
		
		// check validation
		ResultMap resultMap = invoiceValidator.validate(checkParam);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			resultMap = saveInvoice(param);
			
			if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
				Map<String, Object> resultData = (Map<String, Object>) resultMap.getResultData();
				proStatusProcessor.saveDraftInvoice(resultData);
			}
		}
		return resultMap;
	}
	
	/**
	 * 송장 확정
	 *
	 * @param param {invoiceData, insertInvoiceItems, updateInvoiceItems, deleteInvoiceItems, grItemUuids}
	 * @return
	 */
	public ResultMap confirmInvoice(Map<String, Object> param) {
		Map<String, Object> invoiceData = (Map<String, Object>) param.get("invoiceData");
		Map<String, Object> checkParam = Maps.newHashMap(invoiceData);
		checkParam.put("gr_item_uuids", param.get("grItemUuids"));
		
		// check validation
		ResultMap resultMap = invoiceValidator.validate(checkParam);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			// 확정 담당자 아이디
			invoiceData.put("cnfdinv_pic_id", Auth.getCurrentUserName());
			resultMap = saveInvoice(param);
			
			if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
				Map<String, Object> resultData = (Map<String, Object>) resultMap.getResultData();
				proStatusProcessor.confirmInvoice(resultData);
			}
		}
		return resultMap;
	}
	
	/**
	 * 송장 확정 취소
	 *
	 * @param param {inv_uuid, inv_cnfd_cncl_rsn}
	 * @return
	 */
	public ResultMap cancelInvoice(Map<String, Object> param) {
		// check validation
		ResultMap resultMap = invoiceValidator.validate(param);

		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			invoiceRepository.updateInvoiceCancelCause(param);
			proStatusProcessor.cancelInvoice(param);
		}
		return resultMap;
	}
	
	/**
	 * 송장 다중 건 확정 취소
	 *
	 * @param param {inv_uuids, inv_cnfd_cncl_rsn}
	 * @return
	 */
	public ResultMap cancelInvoices(Map<String, Object> param) {
		// check validation
		ResultMap resultMap = invoiceValidator.validate(param);
		Map<String, Object> resultData = (Map<String, Object>) resultMap.getResultData();
		// 유효한 id 리스트
		if(resultData.get(ValidatorConst.VALID_IDS) != null && ((List<String>) resultData.get(ValidatorConst.VALID_IDS)).size() > 0) {
			Map<String, Object> updateParam = Maps.newHashMap();
			updateParam.put("inv_uuids", resultData.get(ValidatorConst.VALID_IDS));
			updateParam.put("inv_cnfd_cncl_rsn", param.get("inv_cnfd_cncl_rsn"));
			// 세금계산서 발행 여부 확인
			Map checkData = checkTaxbillStatus(updateParam);

			if("Y".equals(checkData.get("checkStatus"))) {
				return resultMap.FAIL();
			} else {
				invoiceRepository.updateInvoiceCancelCause(updateParam);
				proStatusProcessor.cancelInvoice(updateParam);
			}
		}
		return resultMap;
	}
	
	/**
	 * 송장 삭제
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	public ResultMap deleteInvoice(Map<String, Object> param) {
		// check validation
		ResultMap resultMap = invoiceValidator.validate(param);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			invoiceItemService.deleteInvoiceItemByInvoice(param);
			invoiceRepository.deleteInvoice(param);
		}
		return resultMap;
	}
	
	/**
	 * 세금계산서 발행대상 목록 조회
	 *
	 * @param param 검색조건
	 * @return
	 */
	public FloaterStream searchTaxBillRequestTarget(Map<String, Object> param) {
		// 대용량 처리
		return invoiceRepository.searchTaxBillRequestTarget(param);
	}

	/**
	 * 세금계산서 발행여부 확인
	 *
	 * @param param
	 * @return
	 */
	public Map checkTaxbillStatus(Map<String, Object> param) {
		Map resultMap = Maps.newHashMap();
		List checkList = Lists.newArrayList();

		List<Map<String, Object>> checkStatus = (List<Map<String, Object>>) invoiceRepository.checkTaxbillStatus(param);

		int count = 0;
		for (Map status : checkStatus) {
			if ("Y".equals(status.get("check_status"))) {
				checkList.add(count++);
			}
		}

		if(checkList.size() > 0) {
			resultMap.put("checkStatus", "Y");
		}

		return resultMap;
	}

	/**
	 * 송장 반려
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	public ResultMap returnInvoice(Map<String, Object> param) {
		// check validation
		ResultMap resultMap = invoiceValidator.validate(param);

		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			invoiceRepository.updateInvoiceReturnCause(param);  // 디비 추가 후 주석 해제
			proStatusProcessor.returnInvoice(param);
		}
		return resultMap;
	}
}
