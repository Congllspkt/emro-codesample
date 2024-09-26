package smartsuite.app.bp.pro.po.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.printout.service.PrintoutService;
import smartsuite.app.bp.pro.po.event.PoEventPublisher;
import smartsuite.app.bp.pro.po.repository.PoRepository;
import smartsuite.app.bp.pro.po.validator.PoValidator;
import smartsuite.app.bp.pro.pr.service.PrItemService;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.common.AttachService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.common.util.ListUtils;
import smartsuite.app.common.util.MapUtils;
import smartsuite.app.common.workingday.service.WorkingdayService;
import smartsuite.data.FloaterStream;
import smartsuite.exception.CommonException;
import smartsuite.module.ModuleManager;
import smartsuite.security.authentication.Auth;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * PO 관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @FileName PoService.java
 * @package smartsuite.app.bp.pro.po
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 * @see
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class PoService {

	@Inject
	private SharedService sharedService;
	
	@Inject
	StdFileService stdFileService;
	
	@Inject
	PoValidator poValidator;
	
	@Inject
	PoEvalService poEvalService;
	
	@Inject
	PrintoutService printoutService;
	
	@Inject
	FormatterProvider formatterProvider;
	
	@Inject
	private ProStatusService proStatusService;

	@Inject
	WorkingdayService workingdayService;

	@Inject
	PoReceiptService poReceiptService;

	@Inject
	PrItemService prItemService;

	private static final String DATE_FORMAT = "yyyyMMdd";
	
	private static final String DOC_ID = "po_doc";
	private static final String PROJECT_NAME = "printout";
	private static final String FORM_NAME = "po_doc";
	
	private final DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

	
	@Inject
	PoRepository poRepository;
	
	@Inject
	PoItemService poItemService;
	
	@Inject
	PoPaymentExpectationService poPaymentExpectationService;
	
	@Inject
	PoConsortiumVendorService poConsortiumService;
	
	@Inject
	PoEventPublisher poEventPublisher;
	

	private String getTodayString() {
		return format.format(Calendar.getInstance().getTime());
	}
	
	/**
	 * 이전 차수의 발주헤더를 발주변경/발주종료로 수정한다. 이전 차수의 발주품목을 발주종료로 수정한다.
	 *
	 * @param param the param
	 * @Date : 2016. 2. 2
	 * @Method Name : updatePrevPoCompleteByModification
	 */
	public void updatePrevPoByModification(Map<String, Object> param, String poStsCcd) {
		Map<String, Object> info = poRepository.findPrevPoByModification(param);
		if(info != null) {
			info.put("po_sts_ccd", poStsCcd);
			poRepository.updatePrevPoByModification(info);
		}
		List<Map<String, Object>> modInfos = poItemService.findPrevPoItemByModification(param);
		
		if(!modInfos.isEmpty()) {
			for(Map<String, Object> modInfo : modInfos) {
				poItemService.updatePrevPoItemByModification(modInfo);
			}
		}
	}


	/**
	 * 발주의 발주완료여부 수정한다.
	 *
	 * @param param the param
	 * @Method Name : updatePoComplete
	 */
	public void updatePoComplete(Map<String, Object> param) {
		List<Map<String, Object>> infos = poRepository.findPoComplete(param);
		for(Map<String, Object> info : infos) {
			poRepository.updatePoComplete(info);
			proStatusService.updatePoReceiptComplete(info);
		}
	}


	/**
	 * 발주의 차수를 구한다
	 *
	 * @param param the param
	 * @return int
	 * @Method Name : getMaxRevisionPoHeader
	 */
	private int getMaxRevisionPoHeader(Map<String, Object> param) {
		Integer rev = poRepository.getMaxRevisionPoHeader(param);
		return rev == null ? 1 : rev.intValue();
	}


	/**
	 * 발주 상세정보를 조회한다.
	 *
	 * @param param the param
	 * @return map
	 * @Method Name : findPo
	 */
	public Map<String, Object> findPo(Map<String, Object> param) {
		Map<String, Object> data = poRepository.findPoHeader(param);
		if(data != null) {
			data.put("items", poItemService.findListPoItemByPoId(param));
			data.put("paymentPlans", poPaymentExpectationService.findListPaymentExpectationByPoId(param));
			
			param.put("po_no", data.get("po_no"));
			int maxRev = this.getMaxRevisionPoHeader(param);
			int poRev = data.get("po_revno") == null ? 1 : Integer.parseInt(data.get("po_revno").toString()); // 발주 차수
			
			// 마지막 차수
			data.put("max_po_rev", maxRev);
			data.put("max_rev_yn", poRev == maxRev ? "Y" : "N");
		}
		return data;
	}
	
	public Map findDefaultDataByPoReqRcpt(Map param) {
		Map result = poReceiptService.findPoDefaultDataByReqRcpt(param);

		Map poData = (Map) result.get("poData");
		// 입고평가 대상 여부 default 값 세팅
		poData.put("ge_subj_yn", ModuleManager.getModulePropertyValues("PRO", "po.eval.default.yn", "N"));

		List<Map> poItems = (List<Map>) result.get("poItems");
		int lno = 0;
		List<String> holiday = workingdayService.findListHolidayFromNowByOorgCd((String) poData.get("oorg_cd"));

		for(Map item : poItems) {
			lno += 10;
			item.put("po_lno", lno);

			if("QTY".equals(poData.get("purc_typ_ccd"))) {
				String reqDlvyDt = ConvertUtils.convertString(item.get("req_dlvy_dt"));
				String dlvyLdtm = ConvertUtils.convertString(item.get("dlvy_ldtm"));
				Object dlvyDt;
				if(item.get("dlvy_dt") == null) {
					dlvyDt = reqDlvyDt;
				} else {
					dlvyDt = item.getOrDefault("dlvy_dt", reqDlvyDt);
				}

				// 납품일자 계산
				if(!holiday.isEmpty() && !Strings.isNullOrEmpty(dlvyLdtm)) {
					ResultMap resultMap = workingdayService.calculateWorkingday(holiday, reqDlvyDt, dlvyLdtm);
					if(resultMap.isSuccess()) {
						dlvyDt = resultMap.getResultData().get("dlvy_date");
					} else {
						throw new CommonException(resultMap.getResultMessage());
					}
				}
				item.put("dlvy_dt", dlvyDt);
			}

			if(item.get("pr_item_uuid") != null) {
				Map prItemData = prItemService.findPrItemByUuid((String) item.get("pr_item_uuid"));
				MapUtils.copyToKeyValue(item, prItemData, Lists.newArrayList(
						"pr_realusr_id",
						"pr_realusr_nm",
						"pr_realusr_req_cont",
						"gr_pic_id",
						"gr_pic_nm"
				));
			}
		}

		return result;
	}

	/**
	 * 발주 상세정보를 조회한다.
	 *
	 * @param param the param
	 * @return map
	 * @Date : 2016. 2. 2
	 * @Method Name : findModifyPo
	 */
	public Map<String, Object> findModifyPo(Map<String, Object> param) {
		Map<String, Object> poData = poRepository.findPoHeader(param);
		poData.put("items", poItemService.findListPoItemModifyByPoId(param));
		poData.put("paymentPlans", poPaymentExpectationService.findListPaymentExpectationByPoId(param));

		param.put("po_no", poData.get("po_no"));
		int maxRev = this.getMaxRevisionPoHeader(param);
		poData.put("max_po_rev", maxRev);

		int poRev = poData.get("po_revno") == null ? 1 : Integer.parseInt(poData.get("po_revno").toString()); // 발주 차수
		
		// 마지막 차수
		poData.put("max_po_rev", maxRev);
		poData.put("max_rev_yn", poRev == maxRev ? "Y" : "N");
		
		// 변경유형(C:변경, R:해지)
		String modTyp = param.get("modify_type") == null ? null : param.get("modify_type").toString();
		if(!Strings.isNullOrEmpty(modTyp)) {
			poData.put("po_chg_typ_ccd", modTyp);
		}
		
		// 요청유형(PURC_REQR:변경요청, PURC_PIC:변경)
		String reqTyp = param.get("requester_type") == null ? null : param.get("requester_type").toString();
		if(!Strings.isNullOrEmpty(reqTyp)) {
			poData.put("po_chg_req_typ_ccd", reqTyp);
		}
		
		if("CHG".equals(modTyp)) {            //변경
			poData.put("po_uuid", "");
			poData.put("cntr_uuid", "");
			poData.put("po_trmn_rsn", "");
			poData.put("po_revno", poRev + 1);

			poData.put("po_chg_req_dt", null);
			poData.put("po_chg_req_rsn", "");

			if("PURC_REQR".equals(reqTyp)) {            // 발주 변경요청
				poData.put("po_chg_req_sts_ccd", "CHG_REQ_CRNG");

				// 세션정보
				Map<String, Object> userInfo = Auth.getCurrentUserInfo();
				poData.put("po_chg_reqr_id", userInfo.get("usr_id"));
				poData.put("mod_req_nm", userInfo.get("usr_nm"));
				poData.put("po_chg_req_dept_cd", userInfo.get("dept_nm"));
			} else if("PURC_PIC".equals(reqTyp)) {    // 발주 변경
				poData.put("po_sts_ccd", "CRNG");

				poData.put("po_chg_reqr_id", null);
				poData.put("mod_req_nm", null);
				poData.put("po_chg_req_dept_cd", null);
			}

			// 내부참조용 첨부파일 복사
			String attNo = (String) poData.get("athg_uuid");
			if(!Strings.isNullOrEmpty(attNo)) {
				String newAttNo = stdFileService.copyFile(attNo);
				poData.put("athg_uuid", newAttNo);
			}
			// 외부참조용 첨부파일 복사
			String cntrAttNo = (String) poData.get("cntr_athg_uuid");
			if(!Strings.isNullOrEmpty(cntrAttNo)) {
				String newCntrAttNo = stdFileService.copyFile(cntrAttNo);
				poData.put("cntr_athg_uuid", newCntrAttNo);
			}

		} else if("TRMN".equals(modTyp)) {    //해지
			poData.put("po_trmn_rsn", "");
			poData.put("po_sts_ccd", "CRNG");

			//세션정보
			Map<String, Object> userInfo = Auth.getCurrentUserInfo();
			poData.put("po_trmn_pic_id", userInfo.get("usr_id"));
			poData.put("po_close_chr_nm", userInfo.get("usr_nm"));
		}
		
		return poData;
	}

	/**
	 * 발주를 생성한다. (PR -> 직발주생성, RFx/역경매 업체선정 완료 -> 발주대기 상태 생성)
	 *
	 * @param param {"poData", "insertItems"}
	 * @return poId
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : createPo
	 */
	public String createPo(Map<String, Object> param) {
		Map<String, Object> poData = (Map<String, Object>) param.get("poData");
		String cur = poData.get("cur_ccd") == null ? null : poData.get("cur_ccd").toString();
		List<String> holiDay = Lists.newArrayList();

		//poData.put("po_chg_typ_ccd", "NEW");    // 신규
		poData.put("po_cmpld_yn", "N");    // 완료여부
		poData.put("mstagt_yn", "N");    // 기본계약여부
		//poData.put("cntr_use_yn", "N");	// 전자계약여부

		BigDecimal vatAmt = BigDecimal.ZERO;
		
		List<Map<String, Object>> insertItems = (List<Map<String, Object>>) param.get("insertItems");
		int lno = 0;
		String oorgCd = (String) poData.get("oorg_cd");
		holiDay = workingdayService.findListHolidayFromNowByOorgCd(oorgCd);

		for(Map<String, Object> item : insertItems) {
			lno += 10;
			item.put("po_lno", lno);

			if("QTY".equals(item.get("purc_typ_ccd"))) {
				String reqDlvyDt = (String) item.getOrDefault("req_dlvy_dt", "0");
				String dlvyLdtm = (String) item.getOrDefault("dlvy_ldtm", "0");
				Object dlvyDt;
				if(item.get("dlvy_dt") == null) {
					dlvyDt = reqDlvyDt;
				} else {
					dlvyDt = item.getOrDefault("dlvy_dt", reqDlvyDt);
				}
				//발주납품일자 계산
				if(holiDay != null && !StringUtils.isEmpty(dlvyLdtm)) {
					ResultMap resultMap = workingdayService.calculateWorkingday(holiDay, reqDlvyDt, dlvyLdtm);
					if(resultMap.isSuccess()) {
						dlvyDt = resultMap.getResultData().get("dlvy_date");
					} else {
						throw new CommonException(resultMap.getResultMessage());
					}
				}
				item.put("dlvy_dt", dlvyDt);
			}// end~ qty

			BigDecimal itemAmt = (BigDecimal) item.get("po_amt");
			if(null != poData.get("tax_typ_ccd") && !"".equals(poData.get("tax_typ_ccd"))) {
				Map<String, Object> codeInfo = Maps.newHashMap();
				codeInfo.put("ccd", "C031");
				codeInfo.put("cstr_cnd_cd", "TAXN_RATE");
				codeInfo.put("dtlcd", poData.get("tax_typ_ccd"));
				BigDecimal taxRate = new BigDecimal(sharedService.getCommonCodeConstraintConditionValue(codeInfo)).divide(new BigDecimal(100));

				Integer dp = formatterProvider.getDecimalPrecision("amt", cur);
				if(dp == null) {
					vatAmt = vatAmt.add(itemAmt.multiply(taxRate));
				} else {
					// scale 처리 (절사하기로 함 - 2019.07.10)
					vatAmt = vatAmt.add(itemAmt.multiply(taxRate).setScale(dp, BigDecimal.ROUND_DOWN));
				}
			}// end tax typ ccd

		}
		
		BigDecimal poAmt = (BigDecimal) poData.get("po_amt"); // 발주총금액
		BigDecimal supplyAmt = poAmt;
		BigDecimal cntrAmt = supplyAmt.add(vatAmt);

		poData.put("sup_amt", supplyAmt);    // 공급가액
		poData.put("vat_amt", vatAmt);        // 세액
		poData.put("cntr_amt", cntrAmt);        // 계약금액 (공급가액 + 세액)
		
		if(poData.get("cpgur_ro") != null) {
			BigDecimal perforBondRate = (BigDecimal) poData.get("cpgur_ro");
			poData.put("cpgur_amt", formatterProvider.getPrecFormat("amt", cntrAmt.multiply(perforBondRate).divide(new BigDecimal(100)), cur));
		}
		if(poData.get("defpgur_ro") != null) {
			BigDecimal maintBondRate = (BigDecimal) poData.get("defpgur_ro");
			poData.put("defpgur_amt", formatterProvider.getPrecFormat("amt", cntrAmt.multiply(maintBondRate).divide(new BigDecimal(100)), cur));
		}
		
		// 입고평가 대상 여부 default 값 세팅
		poData.put("ge_subj_yn", "N");
		
		if(!param.containsKey("paymentPlans")) {
			// 지불조건 (잔금 100% 로 기본생성)
			List<Map<String, Object>> paymentPlans = Lists.newArrayList();
			Map<String, Object> plan = Maps.newHashMap();
			plan.put("pymt_typ_ccd", "BAL");
			plan.put("pymt_ro", new BigDecimal(100));
			plan.put("pymt_amt", poAmt);
			paymentPlans.add(plan);

			param.put("paymentPlans", paymentPlans);
		}
		
		// 데이터 저장
		String poId = this.savePo(param);

		Map<String, Object> keyParam = Maps.newHashMap();
		keyParam.put("po_uuid", poId);
		return poId;
	}

	/**
	 * 발주 정보를 등록/수정한다.
	 *
	 * @param param {"poData", "insertItems", "updateItems", "deleteItems"}
	 * @return poId
	 * @Method Name : savePo
	 */
	public String savePo(Map<String, Object> param) {
		Map<String, Object> poData = (Map<String, Object>) param.get("poData");
		List<Map<String, Object>> insertItems = (List<Map<String, Object>>) ((param.get("insertItems") == null) ? Lists.newArrayList() : param.get("insertItems"));
		List<Map<String, Object>> updateItems = (List<Map<String, Object>>) ((param.get("updateItems") == null) ? Lists.newArrayList() : param.get("updateItems"));
		List<Map<String, Object>> deleteItems = (List<Map<String, Object>>) ((param.get("deleteItems") == null) ? Lists.newArrayList() : param.get("deleteItems"));
		List<Map<String, Object>> paymentPlans = (List<Map<String, Object>>) ((param.get("paymentPlans") == null) ? Lists.newArrayList() : param.get("paymentPlans"));

		String poUuid = (String) poData.get("po_uuid"); // 발주 아이디
		String poNo = (String) poData.get("po_no"); // 발주 문서번호
		int poRevno = poData.get("po_revno") == null ? 1 : Integer.parseInt(poData.get("po_revno").toString()); // 발주 차수
		String oorgCd = (String) poData.get("oorg_cd"); // 운영조직코드
		String purcTypCcd = (String) poData.get("purc_typ_ccd"); // 구매 유형 공통코드
		String curCcd = (String) poData.get("cur_ccd"); // 통화
		String vdCd = (String) poData.get("vd_cd"); // 협력사코드
		String erpVdCd = (String) poData.get("erp_vd_cd"); // ERP협력사코드
		String geSubjYn = (String) poData.get("ge_subj_yn");
		String geUuid = (String) poData.get("ge_uuid");

		boolean isNew = false;

		if(Strings.isNullOrEmpty(poNo)) { // 문서번호 및 차수 생성
			poNo = sharedService.generateDocumentNumber("PO");
			poRevno = 1;
		}
		poData.put("po_no", poNo); // 문서번호
		poData.put("po_revno", poRevno); // 차수

		if(Strings.isNullOrEmpty(poUuid)) { // 신규
			isNew = true;
			poUuid = UUID.randomUUID().toString();
			poData.put("po_uuid", poUuid);

			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
			poData.put("po_crn_dt", format.format(new Date()));

			poRepository.insertPoHeader(poData);
		} else {
			poRepository.updatePoHeader(poData);
			poItemService.updatePoVendor(poData);
		}

		if(deleteItems != null && !deleteItems.isEmpty()) {
			for(Map<String, Object> row : deleteItems) {
				proStatusService.deletePoItem(row);
				poItemService.deletePoItem(row);
			}
		}
		if(insertItems != null && !insertItems.isEmpty()) {
			for(Map<String, Object> row : insertItems) {
				String poItemId = UUID.randomUUID().toString(); // 발주품목 아이디

				row.put("po_item_uuid", poItemId);
				row.put("po_uuid", poUuid);
				row.put("po_no", poNo);
				row.put("po_revno", poRevno);
				row.put("oorg_cd", oorgCd);
				row.put("purc_typ_ccd", purcTypCcd);
				row.put("cur_ccd", curCcd);
				row.put("vd_cd", vdCd);
				row.put("erp_vd_cd", erpVdCd);
				
				// 미등록 품목(무코드 품목)인 경우, 무코드품목번호 채번
				String itemCd = row.get("item_cd") == null ? null : row.get("item_cd").toString();
				String prItemId = row.get("pr_item_uuid") == null ? null : row.get("pr_item_uuid").toString();
				String rfxItemId = row.get("rfx_item_uuid") == null ? null : row.get("rfx_item_uuid").toString();
				if(Strings.isNullOrEmpty(itemCd) && poRevno == 1 && Strings.isNullOrEmpty(prItemId) && Strings.isNullOrEmpty(rfxItemId) && "CDLS".equals(row.get("item_cd_crn_typ_ccd"))) {
					row.put("item_nm", row.get("disp_item_nm"));
					row.put("item_nm_en", row.get("disp_item_nm"));
					//row.put("item_cd", sharedService.generateDocumentNumber("NOCDMT"));
				}
				
				if(Strings.isNullOrEmpty((String) row.get("po_cmpld_yn"))) {
					row.put("po_cmpld_yn", "N");
				}
				if(Strings.isNullOrEmpty((String) row.get("po_ery_ed_yn"))) {
					row.put("po_ery_ed_yn", "N");
				}
				if(Strings.isNullOrEmpty((String) row.get("df_yn"))) {
					row.put("df_yn", "N");
				}

				if((Integer)row.get("po_revno") > 1) {
//					BigDecimal poQty = new BigDecimal((Integer)row.get("po_qty"));
//					BigDecimal grQty = new BigDecimal((Integer)row.get("gr_qty"));
					BigDecimal poQty = (BigDecimal) row.get("po_qty");
					BigDecimal grQty = (BigDecimal) row.get("gr_qty");;
					if(poQty.compareTo(grQty) != 1) {
						row.put("po_item_sts_ccd", "GR_CMPLD");
						row.put("po_cmpld_yn", "Y");
					} else {
						row.put("po_item_sts_ccd", "PO_WTG");
					}
				}


				poItemService.insertPoItem(row);
			}
		}
		if(updateItems != null && !updateItems.isEmpty()) {
			for(Map<String, Object> row : updateItems) {
				row.put("cur_ccd", curCcd);
				row.put("vd_cd", vdCd);
				row.put("erp_vd_cd", erpVdCd);
				
				// 미등록 품목(무코드 품목)인 경우, 무코드품목번호 채번
				String itemCd = row.get("item_cd") == null ? null : row.get("item_cd").toString();
				String prItemId = row.get("pr_item_uuid") == null ? null : row.get("pr_item_uuid").toString();
				String rfxItemId = row.get("rfx_item_uuid") == null ? null : row.get("rfx_item_uuid").toString();
				if(Strings.isNullOrEmpty(itemCd) && poRevno == 1 && Strings.isNullOrEmpty(prItemId) && Strings.isNullOrEmpty(rfxItemId) && "CDLS".equals(row.get("item_cd_crn_typ_ccd"))) {
					row.put("item_nm", row.get("disp_item_nm"));
					row.put("item_nm_en", row.get("disp_item_nm"));
					//row.put("item_cd", sharedService.generateDocumentNumber("NOCDMT"));
				}
				poItemService.updatePoItem(row);
			}
		}
		
		if(!paymentPlans.isEmpty()) {
			// 기존 지급계획 삭제
			Map<String, Object> delParam = Maps.newHashMap();
			delParam.put("po_uuid", poUuid);
			poPaymentExpectationService.deletePaymentExpectationsByPoId(delParam);
			
			// 지급계획을 지급예정일로 sort
			Collections.sort(paymentPlans, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> plan1, Map<String, Object> plan2) {
					return ((String) plan1.get("pymt_expt_dt")).compareTo((String) plan2.get("pymt_expt_dt"));
				}
			});
			// 지급계획 insert
			int rev = 1;
			for(Map<String, Object> paymentPlan : paymentPlans) {
				paymentPlan.put("po_pymt_expt_uuid", UUID.randomUUID().toString());
				paymentPlan.put("po_uuid", poUuid);
				paymentPlan.put("pymt_revno", rev++);
				poPaymentExpectationService.insertPaymentExpectation(paymentPlan);
			}
		}
		
		// 공동수급 건 신규생성 시
		if("Y".equals(poData.get("cstm_yn")) && isNew) {
			// RFx업체선정 시 insertPoCss 넘김, 그 외 없음
			List<Map<String, Object>> insertPoCss = (List<Map<String, Object>>) ((param.get("insertPoCss") == null) ? Lists.newArrayList() : param.get("insertPoCss"));
			
			// 2차수 이상 신규 생성인 경우
			if(poRevno > 1) {
				Map<String, Object> csParam = Maps.newHashMap();
				csParam.put("po_no", poData.get("po_no"));
				csParam.put("po_revno", poRevno - 1);
				csParam.put("rep_vd_cd", poData.get("vd_cd"));
				
				// 이전차수의 공동수급협력사 정보 복사
				insertPoCss = poConsortiumService.findListPoCsVd(csParam);
			}
			for(Map<String, Object> insertPoCs : insertPoCss) {
				insertPoCs.put("po_uuid", poUuid);
				insertPoCs.put("po_no", poNo);
				insertPoCs.put("po_revno", poRevno);
				poConsortiumService.insertPoConsortium(insertPoCs);
			}
		}
		
		// 변경 발주 최초 저장 시 입고기성 평가대상 여부 Y 인 경우 기존 차수 발주와 연결된 평가 설정 정보와 연결한다.
		if(isNew && geSubjYn != null && "Y".equals(geSubjYn) && geUuid != null) {
			poEvalService.insertPoGeMapping(poData);
		}
		return poUuid;
	}

	/**
	 * 발주생성을 임시저장 한다.
	 *
	 * @param param {"poData", "insertItems", "updateItems", "deleteItems"}
	 * @return poId
	 * @Method Name : saveDraftPo
	 */
	public ResultMap saveDraftPo(Map<String, Object> param) {
		Map poData = (Map) param.get("poData");
		ResultMap resultMap = poValidator.validate(poData);
		
		if(!resultMap.isSuccess()) {
			return resultMap;
		}
		
		// 신규 품목이 구매요청 품목에서부터 시작되었다면 구매요청 품목의 상태 변경 작업 필요
		List<Map> insertItems = (List<Map>) param.get("insertItems");
		if(!insertItems.isEmpty()) {
			List<String> prItemIds = ListUtils.getArrayValuesByList(insertItems, "pr_item_uuid");
			if(!prItemIds.isEmpty()) {
				Map prParam = Maps.newHashMap();
				prParam.put("pr_item_uuids", prItemIds);

				// 구매요청품목 접수 처리 (workplace 구매요청품목 티켓 소멸용)
				proStatusService.receivePr(prParam);
			}
		}

		String poId = this.savePo(param); // 데이터 저장

		Map<String, Object> keyParam = Maps.newHashMap();
		keyParam.put("po_uuid", poId);
		proStatusService.saveDraftPo(keyParam);

		return ResultMap.SUCCESS(keyParam);
	}

	/**
	 * 해당 발주의 납품예정/기성요청 품목 존재여부를 리턴한다.
	 *
	 * @param param {po_uuid}
	 * @return has_gr_item_yn
	 */
	public String checkGrPoItems(Map<String, Object> param) {
		//return sqlSession.selectOne(NAMESPACE + "checkGrPoItems", param);
		return poEventPublisher.checkGrPoItems(param);
	}
	
	/**
	 * 발주변경/해지 시 발주품목 상태 체크
	 *
	 * @param param
	 * @return
	 */
	public ResultMap checkPoItemState(Map<String, Object> param) {
		
		List<Map<String, Object>> invalidPoItems = poRepository.checkPoItemState(param);
		if(invalidPoItems.isEmpty()) {
			return ResultMap.SUCCESS();
		} else {
			return ResultMap.builder().resultStatus(ProConst.PO_STATE_ERR).resultList(invalidPoItems).build();
			//resultMap.put(Const.RESULT_STATUS, ProConst.PO_STATE_ERR);
			//resultMap.put(Const.RESULT_DATA, invalidPoItems);
		}
	}
	
	/**
	 * 변경요청을 임시저장 한다.
	 *
	 * @param param {"poData", "insertItems", "updateItems", "deleteItems"}
	 * @return poId
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : modifyReqDraftPo
	 */
	public ResultMap modifyReqDraftPo(Map<String, Object> param) {
		Map<String, Object> poData = (Map<String, Object>) param.get("poData");
		if("Y".equals(checkGrPoItems(poData))) {
			return ResultMap.builder()
					.resultStatus(ProConst.HAS_GR_ITEM)
					.build();
		}
		
		// validate
		ResultMap result = poValidator.validate(poData);
		// validate
		if(result.isSuccess()) {
			poData.put("po_chg_req_dt", this.getTodayString());    // 변경요청일자
			
			String poId = this.savePo(param); // 데이터 저장

			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("po_uuid", poId);
			proStatusService.saveDraftPoChangeRequest(keyParam);
			
			// 재조회를 위해 id 리턴
			result.setResultData(keyParam);
		}
		return result;
	}

	/**
	 * 변경 건에 대하여 임시저장 한다.
	 *
	 * @param param {"poData", "insertItems", "updateItems", "deleteItems"}
	 * @return poId
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : modifyDraftPo
	 */
	public ResultMap modifyDraftPo(Map<String, Object> param) {
		Map<String, Object> poData = (Map<String, Object>) param.get("poData");
		if("Y".equals(checkGrPoItems(poData))) {
			return ResultMap.builder().resultStatus(ProConst.HAS_GR_ITEM).build();
		}
		
		ResultMap result = poValidator.validate(poData);
		// validate
		if(result.isSuccess()) {
			String poId = this.savePo(param); // 데이터 저장
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("po_uuid", poId);
			proStatusService.saveDraftPoChange(keyParam);
			
			// 재조회를 위해 id 리턴
			result.setResultData(keyParam);
		}
		return result;
	}

	/**
	 * 발주요청 한다.
	 *
	 * @param param {"poData", "insertItems", "updateItems", "deleteItems"}
	 * @return poId
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveSubmitPo
	 */
	public ResultMap saveSubmitPo(Map<String, Object> param) {
		Map poData = (Map) param.get("poData");
		ResultMap resultMap = poValidator.validate(poData);
		
		if(!resultMap.isSuccess()) {
			return resultMap;
		}
		
		// 신규 품목이 구매요청 품목에서부터 시작되었다면 구매요청 품목의 상태 변경 작업 필요
		List<Map> insertItems = (List<Map>) param.get("insertItems");
		if(!insertItems.isEmpty()) {
			List<String> prItemIds = ListUtils.getArrayValuesByList(insertItems, "pr_item_uuid");
			if(!prItemIds.isEmpty()) {
				Map prParam = Maps.newHashMap();
				prParam.put("pr_item_uuids", prItemIds);

				// 구매요청품목 접수 처리 (workplace 구매요청품목 티켓 소멸용)
				proStatusService.receivePr(prParam);
			}
		}

		// 1. 데이터 저장
		String poId = this.savePo(param);

		Map<String, Object> keyParam = Maps.newHashMap();
		keyParam.put("po_uuid", poId);

		// 2. 상태 업데이트
		proStatusService.bypassApprovalPo(keyParam);

		// 3. 발주 생성 일자 수정
		// 발주 작성 시점과 요청 시점이 다를 수 있으므로 요청 시점으로 재 업데이트 수행
		this.updatePoCreDate(poId);

		// 4. 현재 유효한 발주여부(EFCT_PO_YN)를 업데이트 한다.
		this.updateCurrentPo(keyParam);

		return ResultMap.SUCCESS(keyParam);
	}
	
	/**
	 * 현재 유효한 발주여부(EFCT_PO_YN)를 업데이트 한다.
	 *
	 * @param param
	 */
	public void updateCurrentPo(Map<String, Object> param) {
		poRepository.updatePreviousPo(param);
		poRepository.updateCurrentPo(param);
	}
	
	/**
	 * 발주일자를 현재일자로 업데이트
	 *
	 * @param poId
	 * @Date : 2017. 4. 12
	 */
	public void updatePoCreDate(String poId) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("po_uuid", poId);
		param.put("po_crn_dt", this.getTodayString());    // 발주 생성 일자
		poRepository.updatePoHeaderPoCreDate(param);
	}

	/**
	 * 발주 변경요청을 한다.
	 *
	 * @param param {"poData", "insertItems", "updateItems", "deleteItems"}
	 * @return poId
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : modifyReqSubmitPo
	 */
	public ResultMap modifyReqSubmitPo(Map<String, Object> param) {
		Map<String, Object> poData = (Map<String, Object>) param.get("poData");
		if("Y".equals(checkGrPoItems(poData))) {
			return ResultMap.builder().resultStatus(ProConst.HAS_GR_ITEM).build();
		}
		
		// validate
		ResultMap resultMap = poValidator.validate(poData);
		
		if(resultMap.isSuccess()) {
			poData.put("po_chg_req_dt", this.getTodayString()); // 변경요청일자
			
			String poId = this.savePo(param); // 데이터 저장
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("po_uuid", poId);
			
			proStatusService.bypassApprovalPoChangeRequest(keyParam);
			
			// 재조회를 위해 id 리턴
			resultMap.setResultData(keyParam);
		}
		return resultMap;
	}

	/**
	 * 발주 변경승인 한다.
	 *
	 * @param param {"poData", "insertItems", "updateItems", "deleteItems"}
	 * @return poId
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : modifySubmitPo
	 */
	public ResultMap modifySubmitPo(Map<String, Object> param) {
		Map<String, Object> poData = (Map<String, Object>) param.get("poData");
		if("Y".equals(checkGrPoItems(poData))) {
			return ResultMap.builder().resultStatus(ProConst.HAS_GR_ITEM).build();
		}
		
		// validate
		ResultMap resultMap = poValidator.validate(poData);
		
		if(resultMap.isSuccess()) {
			String poId = this.savePo(param);                        // 1. 데이터 저장
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("po_uuid", poId);
			
			String cntrSgngTypCcd = (poData.get("cntr_sgng_typ_ccd") == null) ? "NA" : (String) poData.get("cntr_sgng_typ_ccd");
			
			keyParam.put("cntr_sgng_typ_ccd", cntrSgngTypCcd);
			
			proStatusService.bypassApprovalPoChange(keyParam);    // 2. 상태 업데이트
			
			this.updatePrevPoByModification(keyParam, "PO_CHG");                // 3. 이전 차수의 상태 업데이트
			this.updateCurrentPo(keyParam);                            // 4. 현재 유효한 발주여부(EFCT_PO_YN)를 업데이트 한다.
			
			// 발주 변경 시 이전 차수의 평가 설정 정보 복사
			//TODO:[PO] 테이블이 사라져서 보류
			//poEvalService.copyPoEvalSet(poData);
			
			// 재조회를 위해 id 리턴
			resultMap.setResultData(keyParam);
		}
		return resultMap;
	}

	/**
	 * 발주 해지승인 한다.
	 *
	 * @param param
	 * @return poId
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : modifyClosePo
	 */
	public ResultMap modifyClosePo(Map<String, Object> param) {
		Map<String, Object> poData = (Map<String, Object>) param.get("poData");
		if("Y".equals(checkGrPoItems(poData))) {
			return ResultMap.builder().resultStatus(ProConst.HAS_GR_ITEM).build();
		}
		
		ResultMap resultMap = poValidator.validate(poData);
		
		if(resultMap.isSuccess()) {
			poData.put("po_trmn_dt", this.getTodayString()); // 해지일자
			
			String poId = this.savePo(param); // 데이터 저장
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("po_uuid", poId);
			
			proStatusService.closePo(keyParam);
			
			// 재조회를 위해 id 리턴
			resultMap.setResultData(keyParam);
		}
		return resultMap;
	}

	/**
	 * 발주 변경요청을 반송 한다.
	 *
	 * @param param {"po_uuid"}
	 * @return poId
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : modifyReturnPo
	 */
	public ResultMap modifyReturnPo(Map<String, Object> param) {
		// validate
		ResultMap resultMap = poValidator.validate(param);
		
		if(resultMap.isSuccess()) {
			String poId = (String) param.get("po_uuid");
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("po_uuid", poId);
			proStatusService.returnPoChangeRequest(keyParam);
			
			// 재조회를 위해 id 리턴
			resultMap.setResultData(keyParam);
		}
		return resultMap;
	}

	/**
	 * 발주를 삭제 한다.
	 *
	 * @param param {"po_uuid"}
	 * @return poId
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : deletePo
	 */
	public ResultMap deletePo(Map<String, Object> param) {
		// validate
		ResultMap resultMap = poValidator.validate(param);
		if(!resultMap.isSuccess()) {
			return resultMap;
		}
		this.deletePoProcess(param);
		return resultMap;
	}
	
	protected void deletePoProcess(Map<String, Object> param) {
		proStatusService.deletePo(param);
		poConsortiumService.deletePoConsortiumByPoId(param);
		poPaymentExpectationService.deletePaymentExpectationsByPoId(param);
		poItemService.deletePoItemByPoId(param);
		poRepository.deletePoHeader(param);
	}

	/**
	 * 발주 품목 강제종료 처리
	 *
	 * @param param {"po_uuids", "po_item_uuids"}
	 * @return poId
	 * @Date : 2016. 2. 2
	 * @Method Name : saveListPoCrcEnd
	 */
	public ResultMap saveListPoCrcEnd(Map<String, Object> param) {
		poItemService.updatePoItemCrcEnd(param);
		this.updatePoComplete(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 검수등록 가능여부 체크
	 *
	 * @param param
	 * @return
	 */
	public ResultMap checkGrCreatable(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		//유효하지 않은 PO 품목 리스트
		List<Map<String, Object>> invalidPoItemList = poItemService.checkGrCreatable(param);
		if(invalidPoItemList.isEmpty()) {
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		} else {
			resultMap.setResultStatus(ProConst.PO_STATE_ERR);
			Map<String, Object> resultData = Maps.newHashMap();
			resultData.put("invalidPoItemList", invalidPoItemList);
			resultMap.setResultData(resultData);
		}
		return resultMap;
	}
	
	/**
	 * 현재 유효한 발주의 정보를 조회한다.
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findCurrentPo(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		
		Map<String, Object> currentPoData = poRepository.findCurrentPo(param);
		resultMap.put("currentPoData", currentPoData);
		return resultMap;
	}
	
	/**
	 * 발주 변경 이력 조회
	 *
	 * @param param
	 * @return
	 */
	public ResultMap findListPoHistory(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("poList", poRepository.findListPoHistory(param));
		resultMap.put("poItemList", poItemService.findListPoItemHistory(param));
		return ResultMap.SUCCESS(resultMap);
	}
	
	/**
	 * 발주 변경 비교 조회
	 *
	 * @param param
	 * @return
	 */
	public ResultMap findPoCompare(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		
		Map<String, Object> comparePoData = poRepository.findComparePoData(param);
		resultMap.put("comparePoData", comparePoData);
		resultMap.put("comparePoItems", poItemService.findListComparePoItem(param));
		
		Map<String, Object> prevPo = Maps.newHashMap();
		prevPo.put("po_uuid", comparePoData.get("prev_po_uuid"));
		resultMap.put("prevPaymentPlans", poPaymentExpectationService.findListPaymentExpectationByPoId(prevPo));
		
		Map<String, Object> postPo = Maps.newHashMap();
		postPo.put("po_uuid", comparePoData.get("post_po_uuid"));
		resultMap.put("postPaymentPlans", poPaymentExpectationService.findListPaymentExpectationByPoId(postPo));
		
		return ResultMap.SUCCESS(resultMap);
	}
	
	/**
	 * 결재서식에서 이전차수 구매요청정보 조회하는 함수
	 *
	 * @param param(po_no: 구매요청 번호,po_revno: 현재차수-1한 값)
	 * @return
	 * @author : LMS
	 * @Date : 2017. 05. 30
	 * @Method Name : findPreviousPoHeader
	 */
	public Map<String, Object> findPreviousPoHeader(Map<String, Object> param) {
		return poRepository.findPoHeaderByPoNoAndPoRev(param);
	}
	
	/**
	 * 결재서식에서 이전차수 구매요청 item 정보를 조회하는 함수
	 *
	 * @param param(po_no: 구매요청 번호,po_revno: 현재차수-1한 값)
	 * @return
	 * @author : LMS
	 * @Date : 2017. 05. 30
	 * @Method Name : findPreviousListPoItemByPoId
	 */
	public List<Map<String, Object>> findPreviousListPoItemByPoId(Map<String, Object> param) {
		return poItemService.findPreviousListPoItemByPoId(param);
	}
	
	public FloaterStream findListPo(Map param) {
		return poRepository.findListPo(param);
	}

	public FloaterStream findListCntr(Map param) {
		return poRepository.findListCntr(param);
	}


	public FloaterStream findListPoItem(Map param) {
		param.put("purc_typ_ccd", "QTY"); // 구매 유형 공통코드 물품
		param.put("po_prog_stss", Arrays.asList("PC", "GR", "GC", "GD", "GP")); // 발주완료, 납품예정, 검수완료, 검수취소, 검수진행
		// 대용량 처리
		return poItemService.findListPoItem(param);
	}
	
	/**
	 * 인지세 금액 구간 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findStamptaxAmtRange(Map<String, Object> param) {
		return poRepository.findStamptaxAmtRange(param);
	}


	public Map<String, Object> findPoHeaderByPoNoAndPoRev(Map<String, Object> param) {
		return poRepository.findPoHeaderByPoNoAndPoRev(param);
	}

	public Map<String, Object> findPoHeader(Map<String, Object> param) {
		return poRepository.findPoHeader(param);
	}

	public Map<String, Object> findOrderCntrInfo(Map<String, Object> param) {
		return poRepository.findOrderCntrInfo(param);
	}

	/**
	 * 기성요청대상 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list payment bill
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findListProgressPayment
	 */
	public FloaterStream searchProgressPaymentRequestTarget(Map<String, Object> param) {
		// 대용량 처리
		return poRepository.searchProgressPaymentRequestTarget(param);
	}
	
	/**
	 * 발주 기준 선급금 등록 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findAdvancePaymentInfomationByPoUuId(Map<String, Object> param) {
		return poRepository.findAdvancePaymentInfomationByPoUuId(param);
	}
	
	public List<Map<String, Object>> findListYearlyPoItemByVendor(Map<String, Object> param) {
		return poRepository.findListYearlyPoItemByVendor(param);
	}
	
	public List<Map<String, Object>> findListYearlyPoTotAmtByVendor(Map<String, Object> param) {
		return poRepository.findListYearlyPoTotAmtByVendor(param);
	}
	
	public List<Map<String, Object>> findListMonthlyPoTotAmtByVendor(Map<String, Object> param) {
		return poRepository.findListMonthlyPoTotAmtByVendor(param);
	}
	
	public List<Map<String, Object>> findListVendorPoTotAmtByItem(Map<String, Object> param) {
		return poRepository.findListVendorPoTotAmtByItem(param);
	}
	
	public List<Map<String, Object>> findListVendorPoItemPriceByItem(Map<String, Object> param) {
		return poRepository.findListVendorPoItemPriceByItem(param);
	}
	
	/**
	 * 문서 출력 정보 조회를 위한 파라미터 셋팅
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> documentOutputParameterSet(Map<String, Object> param) {
		param.put("doc_id", DOC_ID);
		param.put("projectName", PROJECT_NAME);
		param.put("formName", FORM_NAME);
		return param;
	}
	
	/**
	 * 문서 출력을 위한 po정보(단건) 조회
	 *
	 * @param param
	 * @return
	 */
	public ResultMap findInfoDocumentOutputPo(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		Map<String, Object> parameter = this.documentOutputParameterSet(param);
		Map<String, Object> documentOutputDataInfo = Maps.newHashMap();
		List<Map<String, Object>> docOutpDataByPoHeader = Lists.newArrayList();
		List<Map<String, Object>> docOutpDataByPoDetail = Lists.newArrayList();
		
		Map<String, Object> callDocOutpDataInfo = printoutService.findDocumentOutputManagement(parameter);            // 문서 출력 정보 조회
		Map<String, Object> paramGroupList = callDocOutpDataInfo.get("paramGroupList") == null ? new HashMap<String, Object>() : (Map<String, Object>) callDocOutpDataInfo.get("paramGroupList");
		
		Map<String, Object> poHeader = poRepository.findInfoDocumentOutputPoHeader(param);
		List<Map<String, Object>> poDetail = poRepository.findListDocumentOutputPoDetail(param);
		
		for(String documentGroupName : paramGroupList.keySet()) {
			if(("poHeader").equals(documentGroupName)) {
				docOutpDataByPoHeader.add(poHeader);
			} else if(("poDetail").equals(documentGroupName)) {
				docOutpDataByPoDetail.addAll(poDetail);
			}
		}
		
		// Converting JSON
		Gson gson = new Gson();
		String docOutpDataInfoByPoHeader = gson.toJson(docOutpDataByPoHeader);
		String docOutpDataInfoByPoDetail = gson.toJson(docOutpDataByPoDetail);
		
		Map<String, Object> docOutpInfo = Maps.newHashMap();
		docOutpInfo.put("poHeader", docOutpDataInfoByPoHeader);
		docOutpInfo.put("poDetail", docOutpDataInfoByPoDetail);
		
		Map<String, Object> responseInfo = printoutService.makeUBIFormParameter(parameter);            // 유비폼 호출 파라미터 셋팅
		documentOutputDataInfo.put("datasetList", docOutpInfo);
		documentOutputDataInfo.put("param", responseInfo.get("param"));

		resultMap.setResultData(documentOutputDataInfo);
		return ResultMap.SUCCESS(resultMap.getResultData());
	}
	
	/**
	 * 문서 출력을 위한 po정보(복수건) 조회
	 *
	 * @param param
	 * @return
	 */
	public ResultMap findListDocumentOutputPo(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		Map<String, Object> docOutpDataInfo = Maps.newHashMap();
		Map<String, Object> parameter = this.documentOutputParameterSet(param);
		List<Map<String, Object>> docOutpDataByPoHeader = Lists.newArrayList();
		List<Map<String, Object>> docOutpDataByPoDetail = Lists.newArrayList();
		
		List<String> poUuidList = (List<String>) param.get("po_uuid");
		for(String poUuid : poUuidList) {
			Map<String, Object> poUuidInfo = Maps.newHashMap();
			poUuidInfo.put("po_uuid", poUuid);
			
			Map<String, Object> callDocOutpDataInfo = printoutService.findDocumentOutputManagement(parameter);            // 문서 출력 정보 조회
			Map<String, Object> paramGroupList = callDocOutpDataInfo.get("paramGroupList") == null ? new HashMap<String, Object>() : (Map<String, Object>) callDocOutpDataInfo.get("paramGroupList");

			Map<String, Object> poHeader = poRepository.findInfoDocumentOutputPoHeader(poUuidInfo);
			List<Map<String, Object>> poDetail = poRepository.findListDocumentOutputPoDetail(poUuidInfo);

			for(String documentGroupName : paramGroupList.keySet()) {
				if(("poHeader").equals(documentGroupName)) {
					docOutpDataByPoHeader.add(poHeader);
				} else if(("poDetail").equals(documentGroupName)) {
					docOutpDataByPoDetail.addAll(poDetail);
				}
			}
		}
		
		// Converting JSON
		Gson gson = new Gson();
		String docOutpDataInfoByPoHeader = gson.toJson(docOutpDataByPoHeader);
		String docOutpDataInfoByPoDetail = gson.toJson(docOutpDataByPoDetail);
		
		Map<String, Object> docOutpInfo = Maps.newHashMap();
		docOutpInfo.put("poHeader", docOutpDataInfoByPoHeader);
		docOutpInfo.put("poDetail", docOutpDataInfoByPoDetail);
		
		Map<String, Object> responseInfo = printoutService.makeUBIFormParameter(parameter);            // 유비폼 호출 파라미터 셋팅
		docOutpDataInfo.put("datasetList", docOutpInfo);
		docOutpDataInfo.put("param", responseInfo.get("param"));
		
		resultMap.setResultData(docOutpDataInfo);
		return ResultMap.SUCCESS(resultMap.getResultData());
	}

	public String findRfxIdByPoId(Map<String, Object> param) {
		return poRepository.findRfxIdByPoId(param);
	}

	/**
	 * 전자계약 대상 발주 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListOrderCntrMaker(Map<String, Object> param) {
		return poRepository.findListOrderCntrMaker(param);
	}

	public List<String> findListVendorRcmdWthnoneyrItemPo(Map param) {
		return poRepository.findListVendorRcmdWthnoneyrItemPo(param);
	}
	
	public List<String> findListVendorRcmdWthnoneyrSgPo(Map param) {
		return poRepository.findListVendorRcmdWthnoneyrSgPo(param);
	}
	
	public Map<String, Object> findProgressPaymentDefaultDataByPo(Map param) {
		return poRepository.findProgressPaymentDefaultDataByPo(param);
	}

	/**
	 * 구매요청 발주 거부 카운트 조회
	 * @param param
	 * @return
	 */
	public Map findReturnedPoCount(Map<String, Object> param) {
		return poRepository.findReturnedPoCount(param);
	}

	/**
	 * 발주 유형 카운트
	 * @param param
	 * @return
	 */
	public Map findPoTypeCount(Map<String, Object> param) {
		return poRepository.findPoTypeCount(param);
	}

	public List findListWorkplaceDashboardPoData(Map<String, Object> param) {
		return poRepository.findListWorkplaceDashboardPoData(param);
	}

}
