package smartsuite.app.bp.pro.po.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.auth.service.UserService;
import smartsuite.app.bp.admin.job.service.JobService;
import smartsuite.app.bp.admin.validator.ValidatorConst;
import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.bp.contract.contractcnd.service.PurcContractCndService;
import smartsuite.app.bp.itemMaster.itemcommon.service.ItemCommonService;
import smartsuite.app.bp.pro.po.event.PoReceiptEventPublisher;
import smartsuite.app.bp.pro.po.repository.PoReceiptRepository;
import smartsuite.app.bp.pro.po.validator.PoReceiptValidator;
import smartsuite.app.bp.pro.pr.service.PrItemService;
import smartsuite.app.bp.pro.unitprice.service.UnitPriceService;
import smartsuite.app.common.message.MessageUtil;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.common.util.ListUtils;
import smartsuite.app.common.util.MapUtils;
import smartsuite.app.common.workingday.service.WorkingdayService;
import smartsuite.exception.CommonException;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class PoReceiptService {
	
	@Inject
	PoReceiptRepository poReceiptRepository;
	
	@Inject
	PoReceiptValidator poReceiptValidator;
	
	@Inject
	PoReceiptEventPublisher poReceiptEventPublisher;
	
	@Inject
	PurcContractCndService purcContractCndService;
	
	@Inject
	PoService poService;
	
	@Inject
	ProStatusService proStatusService;
	
	@Inject
	JobService jobService;
	
	@Inject
	UserService userService;
	
	@Inject
	SharedService sharedService;
	
	@Inject
	ContractService contractService;
	
	@Inject
	ItemCommonService itemCommonService;
	
	@Inject
	UnitPriceService unitPriceService;
	
	@Inject
	WorkingdayService workingdayService;
	
	@Inject
	PrItemService prItemService;
	
	@Inject
	MessageUtil messageUtil;
	
	public List findListPoReceipt(Map param) {
		return poReceiptRepository.findListPoReceipt(param);
	}
	
	/**
	 * PO 요청데이터 적재
	 *
	 * @param receiptItem
	 */
	public void receiptReqPo(Map<String, Object> receiptItem) {
		if(receiptItem == null) {
			return;
		}
		if(!receiptItem.containsKey("oorg_cd")) {
			return;
		}
		if(!receiptItem.containsKey("vd_cd")) {
			return;
		}
		if(!receiptItem.containsKey("po_req_uuid")) {
			return;
		}
		if(!receiptItem.containsKey("po_cnd_uuid")) {
			return;
		}

		receiptItem.put("po_req_rcpt_uuid", UUID.randomUUID().toString());
		receiptItem.put("po_req_rcpt_no", sharedService.generateDocumentNumber("POR"));
		receiptItem.put("req_dt", new Date());
		receiptItem.put("rcpt_sts_ccd", "RCPT_WTG");
		receiptItem.put("prgs_sts_ccd", "WTG");
		poReceiptRepository.receiptReqPo(receiptItem);
		
		Map reqPicParam = Maps.newHashMap();
		reqPicParam.put("usr_id", receiptItem.get("req_pic_id"));
		Map reqPicInfo = userService.findUserByUserId(reqPicParam);
		
		List<String> reqUsrPurcGrpList = Lists.newArrayList();
		Map jobParam = Maps.newHashMap();
		jobParam.put("purc_grp_typ_ccd", "PURC");
		jobParam.put("co_cd", reqPicInfo.get("co_cd"));
		jobParam.put("usr_id", reqPicInfo.get("usr_id"));
		List<Map> purcGrpList = jobService.findListPurcGrpByUsr(jobParam);
		
		for(Map purcGrp : purcGrpList) {
			reqUsrPurcGrpList.add((String) purcGrp.get("purc_grp_cd"));
		}
		if(reqUsrPurcGrpList.contains(receiptItem.get("purc_grp_cd"))) {
			// 구매그룹 내 요청자가 존재하는 경우 자동 접수
			this.receiptData(receiptItem, (String) reqPicInfo.get("co_cd"), (String) receiptItem.get("req_pic_id"));
		}
	}
	
	public Map findPoReceiptByUuid(Map param) {
		return poReceiptRepository.findPoReceiptByUuid(param);
	}
	
	public Map findPoDefaultDataByReqRcpt(Map<String, Object> param) {
		Map poReqRcptData = this.findPoReceiptByUuid(param);
		
		Map purcContractParam = Maps.newHashMap();
		purcContractParam.put("purc_cntr_uuid", poReqRcptData.get("po_cnd_uuid"));
		
		Map purcCntr = purcContractCndService.find(purcContractParam);
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		Map purcCntrInfoData = (Map) purcCntr.get("purcCntrInfoData");
		List<Map> purcCntrItemList = (List<Map>) purcCntr.get("purcCntrItemList");
		List<Map> purcCntrPymtExptList = (List<Map>) purcCntr.get("purcCntrPymtExptList");
		List<Map> purcCntrCstmList = (List<Map>) purcCntr.get("purcCntrCstmList");
		
		Map poData = this.makePoDataByPurcCntr(poReqRcptData, purcCntrData, purcCntrInfoData, purcCntrItemList, purcCntrPymtExptList, purcCntrCstmList);
		List<Map> poItems = this.makePoItemDataByPurcCntr(poReqRcptData, purcCntrData, purcCntrItemList);
		
		Map result = Maps.newHashMap();
		result.put("poData", poData);
		result.put("poItems", poItems);
		result.put("paymentPlans", purcCntrPymtExptList);
		result.put("poCstmVds", purcCntrCstmList);
		return result;
	}
	
	private Map makePoDataByPurcCntr(Map<String, Object> poReqRcptData, Map purcCntrData, Map purcCntrInfoData, List<Map> purcCntrItemList, List<Map> purcCntrPymtExptList, List<Map> purcCntrCstmList) {
		Map poData = Maps.newHashMap(purcCntrInfoData);
		MapUtils.copyToKeyValue(poData, poReqRcptData, Lists.newArrayList(
				"oorg_cd",
				"purc_typ_ccd",
				"purc_grp_cd",
				"po_typ_ccd",
				"po_chg_typ_ccd",
				"vd_cd",
				"erp_vd_cd",
				"vd_nm"
		));
		MapUtils.copyToKeyValue(poData, purcCntrData, Lists.newArrayList(
				"domovrs_div_ccd",
				"cur_ccd",
				"cntr_st_dt",
				"cntr_exp_dt",
				"cntr_amt",
				"sup_amt",
				"vat_amt"
		));
		
		poData.put("po_tit", poReqRcptData.get("req_tit"));
		poData.put("po_req_typ_ccd", poReqRcptData.get("req_doc_typ_ccd"));
		/*poData.put("po_typ_ccd", this.makePoTypCcd(poReqRcptData));*/
		poData.put("po_req_rcpt_uuid", poReqRcptData.get("po_req_rcpt_uuid"));
		poData.put("po_sts_ccd", "WTG");
		poData.put("po_cmpld_yn", "N");
		if("CNTR".equals(poReqRcptData.get("req_doc_typ_ccd"))) {
			Map contractParam = Maps.newHashMap();
			contractParam.put("cntr_uuid", poReqRcptData.get("po_req_uuid"));
			Map contractInfo = contractService.findContract(contractParam);
			if("OFFLINE".equals(contractInfo.get("cntr_sgnmeth_ccd"))) {
				poData.put("cntr_sgng_typ_ccd", "OFFLN_CNTR");
			} else {
				poData.put("cntr_sgng_typ_ccd", "ECNTR");
			}
			
			poData.put("cntr_uuid", poReqRcptData.get("po_req_uuid"));
			poData.put("cntr_no", poReqRcptData.get("po_req_no"));
		} else {
			poData.put("cntr_sgng_typ_ccd", "NA");
		}
		MapUtils.copyToDiffKeyValue(poData, Lists.newArrayList(
				"conv_cur_ccd",
				"po_amt",
				"po_conv_amt"
		), purcCntrData, Lists.newArrayList(
				"cur_ccd",
				"sup_amt",
				"sup_amt"
		));
		
		if(!purcCntrItemList.isEmpty()) {
			Map firstItem = purcCntrItemList.get(0);
			
			// 동일한 단가계약 건을 하나의 발주로 묶었으므로 품목의 단가 및 견적 정보는 하나만 존재
			MapUtils.copyToDiffKeyValue(poData, Lists.newArrayList(
					"uprccntr_no",
					"rfx_no",
					"rfx_rnd"
			), firstItem, Lists.newArrayList(
					"ref_cntr_no",
					"rfx_no",
					"rfx_rnd"
			));
		}
		
		BigDecimal apymtRo = BigDecimal.ZERO;
		BigDecimal apymtAmt = BigDecimal.ZERO;
		BigDecimal ipymtRo = BigDecimal.ZERO;
		BigDecimal ipymtAmt = BigDecimal.ZERO;
		BigDecimal balRo = BigDecimal.ZERO;
		BigDecimal balAmt = BigDecimal.ZERO;
		for(Map purcCntrPymtExpt : purcCntrPymtExptList) {
			String pymtTypCcd = (String) purcCntrPymtExpt.get("pymt_typ_ccd");
			BigDecimal pymtRo = ConvertUtils.convertBigDecimal(purcCntrPymtExpt.get("pymt_ro"));
			BigDecimal pymtAmt = ConvertUtils.convertBigDecimal(purcCntrPymtExpt.get("pymt_amt"));
			
			if("APYMT".equals(pymtTypCcd)) {
				apymtRo = apymtRo.add(pymtRo);
				apymtAmt = apymtAmt.add(pymtAmt);
			} else if("IPYMT".equals(pymtTypCcd)) {
				ipymtRo = ipymtRo.add(pymtRo);
				ipymtAmt = ipymtAmt.add(pymtAmt);
			} else {
				balRo = balRo.add(pymtRo);
				balAmt = balAmt.add(pymtAmt);
			}
		}
		poData.put("apymt_ro", apymtRo);
		poData.put("apymt_amt", apymtAmt);
		poData.put("ipymt_ro", ipymtRo);
		poData.put("ipymt_amt", ipymtAmt);
		poData.put("bal_ro", balRo);
		poData.put("bal_amt", balAmt);
		
		if(purcCntrCstmList.isEmpty()) {
			poData.put("cstm_yn", "N");
		} else {
			Map firstCstmItem = purcCntrCstmList.get(0);
			
			poData.put("cstm_yn", "Y");
			poData.put("cstm_typ_ccd", firstCstmItem.get("cstm_typ_ccd"));
		}
		
		return poData;
	}
	
	private List<Map> makePoItemDataByPurcCntr(Map<String, Object> poReqRcptData, Map purcCntrData, List<Map> purcCntrItemList) {
		List<Map> poItems = Lists.newArrayList(purcCntrItemList);
		for(Map poItem : poItems) {
			poItem.remove("purc_cntr_uuid");
			poItem.remove("purc_cntr_item_uuid");
			MapUtils.copyToKeyValue(poItem, poReqRcptData, Lists.newArrayList(
					"oorg_cd",
					"vd_cd",
					"purc_typ_ccd",
					"purc_grp_cd",
					"purc_grp_nm"
			));
			poItem.put("cur_ccd", purcCntrData.get("cur_ccd"));
			MapUtils.copyToDiffKeyValue(poItem, Lists.newArrayList(
					"po_qty",
					"po_uprc",
					"po_amt",
					"const_st_dt",
					"const_exp_dt",
					"cntr_uuid",
					"cntr_no",
					"cntr_revno",
					"purc_cntr_item_uuid"
			), poItem, Lists.newArrayList(
					"item_qty",
					"item_uprc",
					"item_amt",
					"cntr_st_dt",
					"cntr_exp_dt",
					"ref_cntr_uuid",
					"ref_cntr_no",
					"ref_cntr_revno",
					"ref_purc_cntr_item_uuid"
			));
			
		}
		
		return poItems;
	}
	
	private void receiptData(Map<String, Object> receiptItem, String coCd, String poPicId) {
		Map receiptParam = Maps.newHashMap(receiptItem);
		receiptParam.put("co_cd", coCd);
		receiptParam.put("po_pic_id", poPicId);
		poReceiptRepository.receiptReq(receiptParam);
		poReceiptEventPublisher.receiptReq(receiptItem);
		
		// 만약 SPOT계약 접수인 경우 자동으로 발주 생성 및 업체 전송
		receiptItem.put("co_cd", coCd);
		this.createPoBySpotCntr(receiptItem);
	}
	
	private void createPoBySpotCntr(Map<String, Object> receiptItem) {
		if(receiptItem == null) {
			return;
		}
		if(!"NEW".equals(receiptItem.get("po_chg_typ_ccd"))) {
			return;
		}
		if(!"SPOTCNTR".equals(receiptItem.get("po_typ_ccd"))) {
			return;
		}
		Map poDefaultData = this.findPoDefaultDataByReqRcpt(receiptItem);
		
		Map poData = (Map) poDefaultData.get("poData");
		List<Map> poItems = (List<Map>) poDefaultData.get("poItems");
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
		
		Map poCreateParam = Maps.newHashMap();
		poCreateParam.put("poData", poData);
		poCreateParam.put("insertItems", poItems);
		poCreateParam.put("paymentPlans", poDefaultData.get("paymentPlans"));
		poCreateParam.put("insertPoCss", poDefaultData.get("poCstmVds"));
		// 단가계약 기반 발주는 지불 조건 및 컨소시엄 없음
		String poUuid = poService.savePo(poCreateParam);
		
		Map keyParam = Maps.newHashMap();
		keyParam.put("po_uuid", poUuid);
		
		proStatusService.bypassApprovalPo(keyParam); // 2. 상태 업데이트
		poService.updatePoCreDate(poUuid);           // 3. 발주 생성 일자 수정
		poService.updateCurrentPo(keyParam);         // 4. 현재 유효한 발주여부(EFCT_PO_YN)를 업데이트 한다.
	}
	
	/**
	 * 발주 요청데이터 접수
	 *
	 * @param param
	 * @return
	 */
	public ResultMap receiptReqs(Map param) {
		Map checkedItem = (Map) param.get("checkedItem");
		
		ResultMap validator = poReceiptValidator.receiptValidate(checkedItem);
		if(!validator.isSuccess()) {
			return validator;
		}
		String poPicId = Auth.getCurrentUserName();
		String coCd = (String) Auth.getCurrentUserInfo().get("co_cd");
		this.receiptData(checkedItem, coCd, poPicId);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap returnReqs(Map param) {
		Map checkedItem = (Map) param.get("checkedItem");
		
		ResultMap validator = poReceiptValidator.rejectValidate(checkedItem);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		checkedItem.put("req_ret_rsn", param.get("req_ret_rsn"));
		poReceiptRepository.returnReq(checkedItem);
		poReceiptEventPublisher.returnReq(checkedItem);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap changePurcGrp(Map param) {
		Map checkedItem = (Map) param.get("checkedItem");
		
		ResultMap validator = poReceiptValidator.changePurcGrpValidate(checkedItem);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		checkedItem.put("purc_grp_cd", param.get("purc_grp_cd"));
		poReceiptRepository.changePurcGrp(checkedItem);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap receiptReqPoByUprcItem(List<Map> requestList) {
		if(requestList == null) {
			return ResultMap.NOT_EXISTS("data not exists");
		}
		if(requestList.isEmpty()) {
			return ResultMap.NOT_EXISTS("data not exists");
		}
		Date now = new Date();
		List<String> reqUsrPurcGrpList = Lists.newArrayList();
		
		for(Map requestItem : requestList) {
			if(reqUsrPurcGrpList.isEmpty()) {
				Map jobParam = Maps.newHashMap();
				jobParam.put("purc_grp_typ_ccd", "PURC");
				jobParam.put("usr_id", requestItem.get("req_pic_id"));
				List<Map> purcGrpList = jobService.findListPurcGrpByUsr(jobParam);
				
				for(Map purcGrp : purcGrpList) {
					reqUsrPurcGrpList.add((String) purcGrp.get("purc_grp_cd"));
				}
			}
			
			Map item = this.makeReceiptData(now, requestItem);
			item = this.setItemOorgData(item);
			
			poReceiptRepository.insertReceiptReqPoByUprcItem(item);
			
			// 구매그룹 내 요청자가 존재하는 경우 자동 접수
			if(reqUsrPurcGrpList.contains(requestItem.get("purc_grp_cd"))) {
				this.receiptUprcItemData(item, (String) requestItem.get("req_pic_id"));
			}
		}
		this.afterProcess(requestList);
		return ResultMap.SUCCESS();
	}
	
	private void afterProcess(List<Map> itemList) {
		List<Map> touchlessItemList = Lists.newArrayList();
		
		for(Map item : itemList) {
			BigDecimal reqQty = ConvertUtils.convertBigDecimal(item.get("req_qty"));
			
			// 카탈로그 구매로 넘어온 경우 사용할 단가계약을 바로 배분 테이블에 저장
			if(item.get("cntr_uuid") != null) {
				item.put("po_req_item_rcpt_qta_aloc_uuid", UUID.randomUUID().toString());
				item.put("aloc_qty", reqQty);
				poReceiptRepository.insertPoReqItemRcptQtaAloc(item);
			}
			
			String touchlessYn = (String) item.get("tl_yn");
			String qtaYn = (String) item.get("qta_yn");
			if(touchlessYn == null) {
				continue;
			}
			if("N".equals(touchlessYn)) {
				continue;
			}
			
			// 카탈로그 구매로 넘어온 경우
			if(item.get("cntr_uuid") != null) {
				touchlessItemList.add(item);
			}
			// 쿼터 아닌 경우 싱글 벤더 단가만 존재해야 자동발주(touchless PO) 가능
			else if(qtaYn == null || "N".equals(qtaYn)) {
				// Touchless 인 경우 단가 확인
				List<Map> unitPriceList = unitPriceService.findListUnitPriceByItemAndOorg(item);
				List<Map> validUnitPriceList = Lists.newArrayList();
				
				for(Map unitPriceItem : unitPriceList) {
					BigDecimal moq = ConvertUtils.convertBigDecimal(unitPriceItem.get("moq"));
					
					if(reqQty.compareTo(moq) >= 0) {
						validUnitPriceList.add(unitPriceItem);
					}
				}
				
				if(validUnitPriceList.size() != 1) {
					continue;
				}
				Map unitPriceInfo = validUnitPriceList.get(0);
				
				item.put("po_req_item_rcpt_qta_aloc_uuid", UUID.randomUUID().toString());
				item.put("aloc_qty", reqQty);
				item.put("cntr_uuid", unitPriceInfo.get("cntr_uuid"));
				item.put("cntr_no", unitPriceInfo.get("cntr_no"));
				item.put("cntr_revno", unitPriceInfo.get("cntr_revno"));
				item.put("purc_cntr_uuid", unitPriceInfo.get("purc_cntr_uuid"));
				item.put("purc_cntr_info_uuid", unitPriceInfo.get("purc_cntr_info_uuid"));
				item.put("purc_cntr_item_uuid", unitPriceInfo.get("purc_cntr_item_uuid"));
				item.put("purc_cntr_item_lno", unitPriceInfo.get("purc_cntr_item_lno"));
				
				//poReceiptRepository.updateUprccntrInfoByPoReqItemRcpt(item);
				poReceiptRepository.insertPoReqItemRcptQtaAloc(item);
				touchlessItemList.add(item);
			} else {
				// 쿼터 단가계약 정보 확인
				List<Map> qtaUnitPriceList = unitPriceService.findListUnitPriceQtaInfoByItemAndOorg(item);
				List<Map> validQtaUnitPriceList = this.validQtaUnitPriceList(reqQty, qtaUnitPriceList);
				
				List<Map> qtaItemList = Lists.newArrayList();
				for(Map qtaUnitPrice : validQtaUnitPriceList) {
					Map qtaItem = Maps.newHashMap(item);
					qtaItem.put("cntr_uuid", qtaUnitPrice.get("cntr_uuid"));
					qtaItem.put("cntr_no", qtaUnitPrice.get("cntr_no"));
					qtaItem.put("cntr_revno", qtaUnitPrice.get("cntr_revno"));
					qtaItem.put("purc_cntr_uuid", qtaUnitPrice.get("purc_cntr_uuid"));
					qtaItem.put("purc_cntr_info_uuid", qtaUnitPrice.get("purc_cntr_info_uuid"));
					qtaItem.put("purc_cntr_item_uuid", qtaUnitPrice.get("purc_cntr_item_uuid"));
					qtaItem.put("purc_cntr_item_lno", qtaUnitPrice.get("purc_cntr_item_lno"));
					qtaItem.put("qtarate", qtaUnitPrice.get("qtarate"));
					qtaItem.put("aloc_qty", qtaUnitPrice.get("aloc_qty"));
					qtaItem.put("qta_item_uuid", qtaUnitPrice.get("qta_item_uuid"));
					qtaItem.put("qta_uuid", qtaUnitPrice.get("qta_uuid"));
					qtaItem.put("qta_no", qtaUnitPrice.get("qta_no"));
					qtaItem.put("moq", qtaUnitPrice.get("moq"));
					qtaItem.put("ctq", qtaUnitPrice.get("ctq"));
					
					// 쿼터인 경우 배분 수량 내용 적재
					qtaItem.put("po_req_item_rcpt_qta_aloc_uuid", UUID.randomUUID().toString());
					qtaItemList.add(qtaItem);
				}
				
				/*BigDecimal totalQtaRate = BigDecimal.ZERO;
				boolean validQuota = true;
				for(Map qtaUnitPrice : qtaUnitPriceList) {
					if(qtaUnitPrice.get("qta_item_uuid") == null) {
						validQuota = false;
						break;
					}
					totalQtaRate = totalQtaRate.add((BigDecimal) qtaUnitPrice.get("qtarate"));
				}
				// 쿼터 정보가 없는 계약 건 존재
				if(!validQuota) {
					continue;
				}
				// 쿼터율 100% 아닌 경우 통과
				if(totalQtaRate.compareTo(BigDecimal.valueOf(100)) != 0) {
					continue;
				}
				
				List<Map> qtaItemList = Lists.newArrayList();
				for(Map qtaUnitPrice : qtaUnitPriceList) {
					if(qtaUnitPrice.get("qtarate") == null) {
						continue;
					}
					Map qtaItem = Maps.newHashMap(item);
					qtaItem.put("qtarate", qtaUnitPrice.get("qtarate"));
					qtaItem.put("cntr_uuid", qtaUnitPrice.get("cntr_uuid"));
					qtaItem.put("cntr_no", qtaUnitPrice.get("cntr_no"));
					qtaItem.put("cntr_revno", qtaUnitPrice.get("cntr_revno"));
					qtaItem.put("purc_cntr_uuid", qtaUnitPrice.get("purc_cntr_uuid"));
					qtaItem.put("purc_cntr_info_uuid", qtaUnitPrice.get("purc_cntr_info_uuid"));
					qtaItem.put("purc_cntr_item_uuid", qtaUnitPrice.get("purc_cntr_item_uuid"));
					qtaItem.put("purc_cntr_item_lno", qtaUnitPrice.get("purc_cntr_item_lno"));
					qtaItem.put("qta_item_uuid", qtaUnitPrice.get("qta_item_uuid"));
					qtaItem.put("qta_uuid", qtaUnitPrice.get("qta_uuid"));
					qtaItem.put("qta_no", qtaUnitPrice.get("qta_no"));
					qtaItem.put("moq", qtaUnitPrice.get("moq"));
					qtaItem.put("ctq", qtaUnitPrice.get("ctq"));
					
					// 쿼터인 경우 배분 수량 내용 적재
					qtaItem.put("po_req_item_rcpt_qta_aloc_uuid", UUID.randomUUID().toString());
					qtaItemList.add(qtaItem);
				}
				
				// 수량 분배
				this.calculateAlocQta(item, qtaItemList);
				
				boolean alocValid = true;
				for(Map qtaItem : qtaItemList) {
					BigDecimal alocQty = ConvertUtils.convertBigDecimal(qtaItem.get("aloc_qty"));
					BigDecimal moq = ConvertUtils.convertBigDecimal(qtaItem.get("moq"));
					
					// 배분 수량이 moq (최소 주문 수량) 보다 작은 경우
					if(alocQty.compareTo(moq) < 0) {
						alocValid = false;
						break;
					}
				}
				if(!alocValid) {
					continue;
				}*/
				for(Map qtaItem : qtaItemList) {
					poReceiptRepository.insertPoReqItemRcptQtaAloc(qtaItem);
				}
				touchlessItemList.addAll(qtaItemList);
			}
		}
		
		Map<String, List<Map>> cntrGroupTouchless = Maps.newHashMap();
		for(Map touchlessItem : touchlessItemList) {
			String purcCntrUuid = (String) touchlessItem.get("purc_cntr_uuid");
			List<Map> groupItemList = cntrGroupTouchless.get(purcCntrUuid);
			if(groupItemList == null) {
				groupItemList = Lists.newArrayList();
			}
			groupItemList.add(touchlessItem);
			cntrGroupTouchless.put(purcCntrUuid, groupItemList);
		}
		
		this.createPoGroup(cntrGroupTouchless, true);
	}
	
	private void receiptUprcItemData(Map item, String reqPicId) {
		Map receiptParam = Maps.newHashMap(item);
		receiptParam.put("po_pic_id", reqPicId);
		poReceiptRepository.receiptReqUprcItem(receiptParam);
		proStatusService.receivePr(receiptParam);
	}
	
	private Map setItemOorgData(Map item) {
		Map result = item;
		
		Map itemParam = Maps.newHashMap();
		itemParam.put("item_cd", item.get("item_cd"));
		itemParam.put("oorg_cd", item.get("item_oorg_cd"));
		Map itemOorgInfo = itemCommonService.findItemOorgInfo(itemParam);
		if(itemOorgInfo != null) {
			result.put("qta_yn", itemOorgInfo.get("qta_yn"));
			result.put("tl_yn", itemOorgInfo.get("tl_yn"));
		}
		return result;
	}
	
	private Map makeReceiptData(Date now, Map requestItem) {
		Map item = requestItem;
		
		item.put("po_req_item_rcpt_uuid", UUID.randomUUID().toString());
		item.put("req_doc_typ_ccd", "RFX");
		item.put("pr_item_uuid", requestItem.get("req_item_uuid"));
		item.put("pr_uuid", requestItem.get("req_uuid"));
		item.put("pr_no", requestItem.get("req_no"));
		item.put("pr_revno", requestItem.get("req_revno"));
		item.put("pr_lno", requestItem.get("req_lno"));
		
		item.put("req_dt", now);
		item.put("rcpt_sts_ccd", "RCPT_WTG");
		item.put("prgs_sts_ccd", "WTG");
		item.put("qta_yn", "N");
		item.put("tl_yn", "N");
		return item;
	}
	
	private List<Map> validQtaUnitPriceList(BigDecimal reqQty, List<Map> qtaUnitPriceList) {
		List<Map> existQtaList = Lists.newArrayList();
		for(Map qtaUnitPriceItem : qtaUnitPriceList) {
			if(qtaUnitPriceItem.get("qta_item_uuid") == null) {
				continue;
			}
			existQtaList.add(qtaUnitPriceItem);
		}
		return this.validQtaUnitPriceAlocMoqList(reqQty, existQtaList);
	}
	
	private List<Map> validQtaUnitPriceAlocMoqList(BigDecimal reqQty, List<Map> list) {
		List<Map> validList = Lists.newArrayList();
		
		BigDecimal sumAlocQty = BigDecimal.ZERO;
		int size = list.size();
		for(int i = 1; i <= size; i++) {
			boolean isLast = false;
			if(i == size) {
				isLast = true;
			}
			Map item = list.get(i - 1);
			BigDecimal qtarate = ConvertUtils.convertBigDecimal(item.get("qtarate"));
			BigDecimal moq = ConvertUtils.convertBigDecimal(item.get("moq"));
			BigDecimal alocQty;
			
			if(!isLast) {
				alocQty = reqQty.multiply(qtarate).divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_EVEN);
				sumAlocQty = sumAlocQty.add(alocQty);
			} else {
				// 마지막 쿼터 품목 배분 수량 = 요청수량 - (마지막 쿼터 전까지의 모든 배분 수량 합)
				alocQty = reqQty.subtract(sumAlocQty);
			}
			
			item.put("aloc_qty", alocQty);
			if(alocQty.compareTo(moq) >= 0) {
				if(size == 1) {
					// 쿼터인데 품목이 하나인 경우에도 배분율 100%로 진행
					item.put("qtarate", 100);
				}
				validList.add(item);
			}
		}
		
		if(list.size() != validList.size()) {
			return validQtaUnitPriceAlocMoqList(reqQty, this.convertQtarateTo100Percent(validList));
		} else {
			return validList;
		}
	}
	
	/**
	 * 쿼터율 100% 환산
	 *
	 * @param validList
	 * @return
	 */
	private List<Map> convertQtarateTo100Percent(List<Map> validList) {
		BigDecimal totalQtarate = BigDecimal.ZERO;
		for(Map validItem : validList) {
			totalQtarate = totalQtarate.add(ConvertUtils.convertBigDecimal(validItem.get("qtarate")));
		}
		
		BigDecimal hundred = BigDecimal.valueOf(100);
		BigDecimal sumQtarate = BigDecimal.ZERO;
		int size = validList.size();
		for(int i = 1; i <= size; i++) {
			boolean isLast = false;
			if(i == size) {
				isLast = true;
			}
			Map item = validList.get(i - 1);
			BigDecimal qtarate = ConvertUtils.convertBigDecimal(item.get("qtarate"));
			
			if(!isLast) {
				BigDecimal translateQtarate = qtarate.divide(totalQtarate, 10, RoundingMode.DOWN).multiply(hundred).setScale(0, RoundingMode.DOWN);
				item.put("qtarate", translateQtarate);
				sumQtarate = sumQtarate.add(translateQtarate);
			} else {
				// 마지막 품목의 경우 남은 쿼터율을 가져간다.
				item.put("qtarate", hundred.subtract(sumQtarate));
			}
		}
		
		return validList;
	}
	
	private void calculateAlocQta(Map item, List<Map> unitPriceList) {
		// 쿼터율이 100% 인 경우 배분 가능
		BigDecimal reqQty = ConvertUtils.convertBigDecimal(item.get("req_qty"));
		BigDecimal sumAlocQty = BigDecimal.ZERO;
		
		int qtaUnitPriceSize = unitPriceList.size();
		for(int i = 1; i <= qtaUnitPriceSize; i++) {
			boolean isLast = false;
			if(i == qtaUnitPriceSize) {
				isLast = true;
			}
			Map qtaUnitPrice = unitPriceList.get(i - 1);
			BigDecimal qtarate = ConvertUtils.convertBigDecimal(qtaUnitPrice.get("qtarate"));
			if(qtarate == null) {
				continue;
			}
			BigDecimal alocQty;
			if(!isLast) {
				alocQty = reqQty.multiply(qtarate).divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_EVEN);
				sumAlocQty = sumAlocQty.add(alocQty);
			} else {
				// 마지막 쿼터 품목 배분 수량 = 요청수량 - (마지막 쿼터 전까지의 모든 배분 수량 합)
				alocQty = reqQty.subtract(sumAlocQty);
			}
			
			qtaUnitPrice.put("aloc_qty", alocQty);
		}
	}
	
	private void createPoGroup(Map<String, List<Map>> cntrGroupItem, boolean touchless) {
		for(String key : cntrGroupItem.keySet()) {
			this.createPo(key, cntrGroupItem.get(key), touchless);
		}
	}
	
	private void createPo(String purcCntrUuid, List<Map> itemList, boolean touchless) {
		Map param = Maps.newHashMap();
		param.put("purc_cntr_uuid", purcCntrUuid);
		
		Map purcCntr = purcContractCndService.find(param);
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		Map purcCntrInfoData = (Map) purcCntr.get("purcCntrInfoData");
		List<Map> purcCntrItemList = (List<Map>) purcCntr.get("purcCntrItemList");
		Map<String, Map> purcCntrItemMap = Maps.newHashMap();
		for(Map purcCntrItem : purcCntrItemList) {
			purcCntrItemMap.put((String) purcCntrItem.get("purc_cntr_item_uuid"), purcCntrItem);
		}
		
		Map firstItem = itemList.get(0);
		Map poData = Maps.newHashMap();
		poData.put("oorg_cd", firstItem.get("oorg_cd"));
		poData.put("purc_typ_ccd", firstItem.get("purc_typ_ccd"));
		poData.put("purc_grp_cd", firstItem.get("purc_grp_cd"));
		poData.put("po_req_typ_ccd", "PR");
		poData.put("uprccntr_no", firstItem.get("cntr_no"));
		
		Locale locale = LocaleContextHolder.getLocale();
		String itemName;
		if("en_US".equals(locale.toString())) {
			itemName = (String) firstItem.getOrDefault("disp_item_nm", "item_nm_en");
		} else {
			itemName = (String) firstItem.getOrDefault("disp_item_nm", "item_nm");
		}
		
		String title;
		if(itemList.size() == 1) {
			title = itemName;
		} else {
			title = messageUtil.getCodeMessage("STD.N3100", Lists.newArrayList(
				itemName,
				itemList.size() - 1
		), null);
		}
		
		poData.put("po_tit", title);
		
		if(touchless) {
			poData.put("po_typ_ccd", "TLAUTOPO");
		} else {
			poData.put("po_typ_ccd", "NTLAUTOPO");
		}
		poData.put("po_chg_typ_ccd", "NEW");
		MapUtils.copyToKeyValue(poData, purcCntrData, Lists.newArrayList(
				"vd_cd",
				"erp_vd_cd",
				"vd_nm",
				"domovrs_div_ccd",
				"cur_ccd"
		));
		MapUtils.copyToKeyValue(poData, purcCntrInfoData, Lists.newArrayList(
				"pymtmeth_ccd",
				"pymtmeth_expln",
				"pymtmeth_use_yn",
				"dlvymeth_ccd",
				"dlvymeth_expln",
				"dlvymeth_use_yn",
				"tax_typ_ccd",
				"otrexp",
				"otrexp_expln",
				"pymtmeth_cnd",
				"dfrm_ro",
				"cpgur_use_yn",
				"cpgur_typ_ccd",
				"cpgur_ro",
				"cpgur_amt",
				"cpgur_st_dt",
				"cpgur_exp_dt",
				"defpgur_use_yn",
				"defpgur_typ_ccd",
				"defpgur_ro",
				"defpgur_amt",
				"defpgur_pd_typ_ccd",
				"defpgur_pd",
				"apymtgur_use_yn",
				"apymtgur_typ_ccd",
				"apymtgur_ro",
				"apymtgur_amt",
				"apymtgur_st_dt",
				"apymtgur_exp_dt",
				"stax_yn",
				"sttpymt_ro_typ_ccd",
				"sttpymtmeth_ccd"
		));
		
		String minCntrStartDate = null;
		String maxCntrEndDate = null;
		BigDecimal sumItemAmt = BigDecimal.ZERO;
		for(Map item : itemList) {
			// 기 체결된 단가계약 단가 정보 세팅
			Map purcCntrItem = purcCntrItemMap.get(item.get("purc_cntr_item_uuid"));
			if(purcCntrItem != null) {
				item.put("po_uprc", purcCntrItem.get("item_uprc"));
			}
			
			if(item.get("aloc_qty") == null) {
				item.put("po_qty", item.get("req_qty"));
			} else {
				// 쿼터인 경우 배분 수량으로 세팅
				item.put("po_qty", item.get("aloc_qty"));
			}
			
			BigDecimal itemQty = ConvertUtils.convertBigDecimal(item.get("po_qty"));
			BigDecimal itemUprc = ConvertUtils.convertBigDecimal(item.get("po_uprc"));
			BigDecimal itemAmt = itemQty.multiply(itemUprc);
			item.put("po_amt", itemAmt);
			
			sumItemAmt = sumItemAmt.add(itemAmt);
			
			if(minCntrStartDate == null) {
				minCntrStartDate = (String) purcCntrItem.get("cntr_st_dt");
			} else {
				if(purcCntrItem.get("cntr_st_dt") != null && minCntrStartDate.compareTo((String) purcCntrItem.get("cntr_st_dt")) > 0) {
					minCntrStartDate = (String) purcCntrItem.get("cntr_st_dt");
				}
			}
			if(maxCntrEndDate == null) {
				maxCntrEndDate = (String) purcCntrItem.get("cntr_exp_dt");
			} else {
				if(purcCntrItem.get("cntr_exp_dt") != null && maxCntrEndDate.compareTo((String) purcCntrItem.get("cntr_exp_dt")) < 0) {
					maxCntrEndDate = (String) purcCntrItem.get("cntr_exp_dt");
				}
			}
		}
		poData.put("cntr_st_dt", minCntrStartDate);
		poData.put("cntr_exp_dt", maxCntrEndDate);
		
		//BigDecimal cntrAmt = BigDecimal.ZERO;
		poData.put("po_amt", sumItemAmt);
		/*BigDecimal vatAmt = BigDecimal.ZERO;
		
		if(purcCntrInfoData.get("tax_typ_ccd") != null) {
			Map codeParam = Maps.newHashMap();
			codeParam.put("ccd", "C031");
			codeParam.put("dtlcd", purcCntrInfoData.get("tax_typ_ccd"));
			codeParam.put("cstr_cnd_cd", "TAXN_RATE");
			List codeAttributes = sharedService.findListCommonCodeAttributeCode(codeParam);
			if(codeAttributes.size() == 1) {
				Map codeAttribute = (Map) codeAttributes.get(0);
				BigDecimal cstrCndVal = ConvertUtils.convertBigDecimal(codeAttribute.get("cstr_cnd_val"));
				vatAmt = supAmt.multiply(cstrCndVal).divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_DOWN);
			}
		}
		cntrAmt = supAmt.add(vatAmt);
		poData.put("cntr_amt", cntrAmt.doubleValue());
		poData.put("sup_amt", supAmt.doubleValue());
		poData.put("vat_amt", vatAmt.doubleValue());*/
		
		Map poCreateParam = Maps.newHashMap();
		poCreateParam.put("poData", poData);
		poCreateParam.put("insertItems", itemList);
		// 단가계약 기반 발주는 지불 조건 및 컨소시엄 없음
		String poUuid = poService.createPo(poCreateParam);
		
		Map keyParam = Maps.newHashMap();
		keyParam.put("po_uuid", poUuid);
		
		if(touchless) {
			proStatusService.bypassApprovalPo(keyParam); // 2. 상태 업데이트
			poService.updatePoCreDate(poUuid);           // 3. 발주 생성 일자 수정
			poService.updateCurrentPo(keyParam);         // 4. 현재 유효한 발주여부(EFCT_PO_YN)를 업데이트 한다.
		} else {
			proStatusService.saveDraftPo(keyParam);
		}
	}
	
	public List findListPoReceiptItemByUprcItem(Map param) {
		List<Map> receiptList = poReceiptRepository.findListPoReceiptItemByUprcItem(param);
		
		List<Map> poWtgItemList = Lists.newArrayList();
		for(Map receiptItem : receiptList) {
			if(!("RCPT_WTG".equals(receiptItem.get("rcpt_sts_ccd")) || "RCPT".equals(receiptItem.get("rcpt_sts_ccd")))) {
				continue;
			}
			if(!"WTG".equals(receiptItem.get("prgs_sts_ccd"))) {
				continue;
			}
			// 카탈로그 구매 or 이미 배분 완료 진행한 경우
			if("Y".equals(receiptItem.get("exist_aloc_yn"))) {
				continue;
			}
			poWtgItemList.add(receiptItem);
		}
		if(!poWtgItemList.isEmpty()) {
			List<Map<String, Object>> unitPriceList = poReceiptRepository.findListUnitPriceByPoReqItemRcpt(ListUtils.getArrayValuesByList(poWtgItemList, "po_req_item_rcpt_uuid"));
			Map<String, List<Map>> poReqUnitPriceGroupMap = Maps.newHashMap();
			
			for(Map<String, Object> unitPrice : unitPriceList) {
				List<Map> poReqItemUnitPriceList = poReqUnitPriceGroupMap.get(unitPrice.get("po_req_item_rcpt_uuid"));
				if(poReqItemUnitPriceList == null) {
					poReqItemUnitPriceList = Lists.newArrayList();
					poReqUnitPriceGroupMap.put((String) unitPrice.get("po_req_item_rcpt_uuid"), poReqItemUnitPriceList);
				}
				poReqItemUnitPriceList.add(unitPrice);
			}
			
			// 대기 품목에서만 반복문 수행
			for(Map wtgItem : poWtgItemList) {
				List<Map> poReqItemUnitPriceList = poReqUnitPriceGroupMap.get(wtgItem.get("po_req_item_rcpt_uuid"));
				if(poReqItemUnitPriceList == null) {
					continue;
				}
				// 현재 시점 단가계약 싱글 밴더이므로 바로 매핑
				if(poReqItemUnitPriceList.size() == 1) {
					Map poReqItemUnitPrice = poReqItemUnitPriceList.get(0);
					
					BigDecimal moq = ConvertUtils.convertBigDecimal(poReqItemUnitPrice.get("moq"));
					BigDecimal reqQty = ConvertUtils.convertBigDecimal(wtgItem.get("req_qty"));
					
					// 싱글 밴더이더라도 요청수량이 최수주문수량보다 적으면 통과
					if(reqQty.compareTo(moq) < 0) {
						continue;
					}
					
					Map qtaItem = Maps.newHashMap();
					qtaItem.put("prgs_sts_ccd", "WTG");
					qtaItem.put("uom_ccd", poReqItemUnitPrice.get("uom_ccd"));
					qtaItem.put("cur_ccd", poReqItemUnitPrice.get("cur_ccd"));
					qtaItem.put("uprccntr_uprc", poReqItemUnitPrice.get("uprccntr_uprc"));
					qtaItem.put("cntr_uuid", poReqItemUnitPrice.get("cntr_uuid"));
					qtaItem.put("cntr_no", poReqItemUnitPrice.get("cntr_no"));
					qtaItem.put("cntr_revno", poReqItemUnitPrice.get("cntr_revno"));
					qtaItem.put("purc_cntr_uuid", poReqItemUnitPrice.get("purc_cntr_uuid"));
					qtaItem.put("purc_cntr_info_uuid", poReqItemUnitPrice.get("purc_cntr_info_uuid"));
					qtaItem.put("purc_cntr_item_uuid", poReqItemUnitPrice.get("purc_cntr_item_uuid"));
					qtaItem.put("purc_cntr_item_lno", poReqItemUnitPrice.get("purc_cntr_item_lno"));
					qtaItem.put("vd_cd", poReqItemUnitPrice.get("vd_cd"));
					qtaItem.put("disp_vd_nm", poReqItemUnitPrice.get("disp_vd_nm"));
					qtaItem.put("moq", poReqItemUnitPrice.get("moq"));
					qtaItem.put("ctq", poReqItemUnitPrice.get("ctq"));
					qtaItem.put("qta_no", poReqItemUnitPrice.get("qta_no"));
					qtaItem.put("qta_item_uuid", poReqItemUnitPrice.get("qta_item_uuid"));
					qtaItem.put("qtarate", poReqItemUnitPrice.get("qtarate"));
					qtaItem.put("aloc_qty", wtgItem.get("req_qty"));
					wtgItem.put("po_qty", wtgItem.get("req_qty"));
					
					List<Map> qtaList = Lists.newArrayList(qtaItem);
					wtgItem.put("qtaList", qtaList);
					
					/*wtgItem.put("multi_vd_yn", "N");
					wtgItem.put("po_qty", wtgItem.get("req_qty"));
					wtgItem.put("cntr_uuid", poReqItemUnitPrice.get("cntr_uuid"));
					wtgItem.put("cntr_no", poReqItemUnitPrice.get("cntr_no"));
					wtgItem.put("cntr_revno", poReqItemUnitPrice.get("cntr_revno"));
					wtgItem.put("purc_cntr_uuid", poReqItemUnitPrice.get("purc_cntr_uuid"));
					wtgItem.put("purc_cntr_info_uuid", poReqItemUnitPrice.get("purc_cntr_info_uuid"));
					wtgItem.put("purc_cntr_item_uuid", poReqItemUnitPrice.get("purc_cntr_item_uuid"));
					wtgItem.put("purc_cntr_item_lno", poReqItemUnitPrice.get("purc_cntr_item_lno"));
					wtgItem.put("vd_cd", poReqItemUnitPrice.get("vd_cd"));
					wtgItem.put("disp_vd_nm", poReqItemUnitPrice.get("disp_vd_nm"));*/
				} else {
					wtgItem.put("po_qty", 0);
				}
				/*else if(poReqItemUnitPriceList.size() > 1) {
					// 멀티 밴더이므로 멀티 여부만 체크
					wtgItem.put("multi_vd_yn", "Y");
					if(wtgItem.get("po_qty") == null) {
						wtgItem.put("po_qty", 0);
					}
				}*/
			}
		}
		return receiptList;
	}
	
	public ResultMap receiptUprcItemReqs(Map param) {
		String poPicId = Auth.getCurrentUserName();
		List<Map> checkedList = (List<Map>) param.get("checkedList");
		ResultMap validator = this.reqValidator(checkedList, Lists.newArrayList("RCPT_WTG"));
		Map resultData = validator.getResultData();
		
		List<String> validPoReqItemRcptUuids = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		
		for(Map checkedItem : checkedList) {
			if(!validPoReqItemRcptUuids.contains(checkedItem.get("po_req_item_rcpt_uuid"))) {
				continue;
			}
			this.receiptUprcItemData(checkedItem, poPicId);
		}
		return validator;
	}
	
	protected ResultMap reqValidator(List<Map> list, List<String> availableStsList) {
		List<String> poReqItemRcptUuids = ListUtils.getArrayValuesByList(list, "po_req_item_rcpt_uuid");
		
		Map validateParam = Maps.newHashMap();
		validateParam.put("availableStsList", availableStsList);
		validateParam.put("po_req_item_rcpt_uuids", poReqItemRcptUuids);
		
		ResultMap validator = poReceiptValidator.ReceiptUprcItemValidate(validateParam);
		return validator;
	}
	
	public ResultMap returnUprcItemReqs(Map param) {
		List<Map> checkedList = (List<Map>) param.get("checkedList");
		ResultMap validator = this.reqValidator(checkedList, Lists.newArrayList("RCPT_WTG", "RCPT"));
		Map resultData = validator.getResultData();
		
		List<String> validPoReqItemRcptUuids = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		
		List<String> returnPoReqItemRcptUuids = Lists.newArrayList();
		for(Map checkedItem : checkedList) {
			if(!validPoReqItemRcptUuids.contains(checkedItem.get("po_req_item_rcpt_uuid"))) {
				continue;
			}
			
			returnPoReqItemRcptUuids.add((String) checkedItem.get("po_req_item_rcpt_uuid"));
			
			checkedItem.put("req_ret_rsn", param.get("req_ret_rsn"));
			poReceiptRepository.returnUprcItemReq(checkedItem);
			proStatusService.returnPr(checkedItem);
		}
		
		List<Map<String, Object>> returnUprcItemList = poReceiptRepository.findListReturnedPoReqItemRcpt(returnPoReqItemRcptUuids);
		poReceiptEventPublisher.returnUprcItemReq(returnUprcItemList);
		return validator;
	}
	
	public ResultMap changeUprcItemPurcGrp(Map param) {
		List<Map> checkedList = (List<Map>) param.get("checkedList");
		ResultMap validator = this.reqValidator(checkedList, Lists.newArrayList("RCPT_WTG", "RCPT"));
		Map resultData = validator.getResultData();
		
		List<String> validPoReqItemRcptUuids = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		
		for(Map checkedItem : checkedList) {
			if(!validPoReqItemRcptUuids.contains(checkedItem.get("po_req_item_rcpt_uuid"))) {
				continue;
			}
			checkedItem.put("purc_grp_cd", param.get("purc_grp_cd"));
			poReceiptRepository.changeUprcItemPurcGrp(checkedItem);
		}
		return validator;
	}
	
	public Map findItemUprcAloc(Map param) {
		Map result = Maps.newHashMap();
		
		Map itemInfo = poReceiptEventPublisher.findItemByItemCd(param);
		if(itemInfo != null) {
			MapUtils.copyToKeyValue(param, itemInfo, Lists.newArrayList(
					"item_cd",
					"disp_item_nm",
					"item_spec",
					"mfgr_nm",
					"mdl_no",
					"disp_itemcat_lvl_1_nm",
					"disp_itemcat_lvl_2_nm",
					"disp_itemcat_lvl_3_nm",
					"disp_itemcat_lvl_4_nm"
			));
		}
		
		result.put("itemInfo", param);
		result.put("unitpriceList", this.findListUprccntr(param));
		
		List<Map> qtaList = (List<Map>) param.get("qtaList");
		if(qtaList == null || qtaList.isEmpty()) {
			result.put("alocList", this.findListPoReqItemRcptQta(param));
		} else {
			result.put("alocList", qtaList);
		}
		
		return result;
	}
	
	public List findListUprccntr(Map param) {
		List<Map> unitPriceList = unitPriceService.findListUnitPriceQtaInfoByItemAndOorg(param);
		
		/*if("Y".equals(poReqItemRcptInfo.get("qta_yn"))) {
			BigDecimal totalQtaRate = BigDecimal.ZERO;
			for(Map unitPrice : unitPriceList) {
				if(unitPrice.get("qtarate") == null) {
					continue;
				}
				totalQtaRate = totalQtaRate.add(ConvertUtils.convertBigDecimal(unitPrice.get("qtarate")));
			}
			// 쿼터율 100% 아닌 경우 통과
			if(totalQtaRate.compareTo(BigDecimal.valueOf(100)) == 0) {
				List<Map> existQtaList = Lists.newArrayList();
				for(Map unitPriceItem : unitPriceList) {
					if(unitPriceItem.get("qtarate") == null) {
						continue;
					}
					existQtaList.add(unitPriceItem);
				}
				this.calculateAlocQta(poReqItemRcptInfo, existQtaList);
			}
		}*/
		
		return unitPriceList;
	}
	
	public List findListPoReqItemRcptQta(Map param) {
		return poReceiptRepository.findListPoReqItemRcptQta(param);
	}
	
	public ResultMap createUprccntrPo(Map param) {
		List<Map> unTouchlessItemList = Lists.newArrayList();
		
		List<Map> checkedList = (List<Map>) param.get("checkedList");
		for(Map item : checkedList) {
			// 단가계약 배분 정보 확인
			List<Map> qtaUnitPriceList = (List<Map>) item.get("qtaList");
			if(qtaUnitPriceList == null || qtaUnitPriceList.isEmpty()) {
				qtaUnitPriceList = poReceiptRepository.findListPoReqItemRcptQta(item);
				for(Map qtaUnitPrice : qtaUnitPriceList) {
					// 대기중이 아닌 분배 건은 스킵
					// 이미 발주를 생성한 건임
					if(!"WTG".equals(qtaUnitPrice.get("prgs_sts_ccd"))) {
						continue;
					}
					Map qtaItem = Maps.newHashMap(item);
					qtaItem.put("po_req_item_rcpt_qta_aloc_uuid", qtaUnitPrice.get("po_req_item_rcpt_qta_aloc_uuid"));
					qtaItem.put("qtarate", qtaUnitPrice.get("qtarate"));
					// 이미 배분 수량 계산된 시점
					qtaItem.put("aloc_qty", qtaUnitPrice.get("aloc_qty"));
					qtaItem.put("cntr_uuid", qtaUnitPrice.get("cntr_uuid"));
					qtaItem.put("cntr_no", qtaUnitPrice.get("cntr_no"));
					qtaItem.put("cntr_revno", qtaUnitPrice.get("cntr_revno"));
					qtaItem.put("purc_cntr_uuid", qtaUnitPrice.get("purc_cntr_uuid"));
					qtaItem.put("purc_cntr_info_uuid", qtaUnitPrice.get("purc_cntr_info_uuid"));
					qtaItem.put("purc_cntr_item_uuid", qtaUnitPrice.get("purc_cntr_item_uuid"));
					qtaItem.put("purc_cntr_item_lno", qtaUnitPrice.get("purc_cntr_item_lno"));
					qtaItem.put("qta_item_uuid", qtaUnitPrice.get("qta_item_uuid"));
					qtaItem.put("qta_no", qtaUnitPrice.get("qta_no"));
					unTouchlessItemList.add(qtaItem);
				}
			} else {
				List<Map> qtaItemList = Lists.newArrayList();
				for(Map qtaUnitPrice : qtaUnitPriceList) {
					// 대기중이 아닌 분배 건은 스킵
					// 이미 발주를 생성한 건임
					if(!"WTG".equals(qtaUnitPrice.get("prgs_sts_ccd"))) {
						continue;
					}
					Map qtaItem = Maps.newHashMap(item);
					qtaItem.put("qtarate", qtaUnitPrice.get("qtarate"));
					// 이미 배분 수량 계산된 시점
					qtaItem.put("aloc_qty", qtaUnitPrice.get("aloc_qty"));
					qtaItem.put("cntr_uuid", qtaUnitPrice.get("cntr_uuid"));
					qtaItem.put("cntr_no", qtaUnitPrice.get("cntr_no"));
					qtaItem.put("cntr_revno", qtaUnitPrice.get("cntr_revno"));
					qtaItem.put("purc_cntr_uuid", qtaUnitPrice.get("purc_cntr_uuid"));
					qtaItem.put("purc_cntr_info_uuid", qtaUnitPrice.get("purc_cntr_info_uuid"));
					qtaItem.put("purc_cntr_item_uuid", qtaUnitPrice.get("purc_cntr_item_uuid"));
					qtaItem.put("purc_cntr_item_lno", qtaUnitPrice.get("purc_cntr_item_lno"));
					qtaItem.put("qta_item_uuid", qtaUnitPrice.get("qta_item_uuid"));
					qtaItem.put("qta_no", qtaUnitPrice.get("qta_no"));
					// 쿼터인 경우 배분 수량 내용 적재
					qtaItem.put("po_req_item_rcpt_qta_aloc_uuid", UUID.randomUUID().toString());
					qtaItemList.add(qtaItem);
					
					poReceiptRepository.insertPoReqItemRcptQtaAloc(qtaItem);
				}
				unTouchlessItemList.addAll(qtaItemList);
			}
		}
		
		Map<String, List<Map>> cntrGroupUnTouchless = Maps.newHashMap();
		for(Map unTouchlessItem : unTouchlessItemList) {
			String purcCntrUuid = (String) unTouchlessItem.get("purc_cntr_uuid");
			List<Map> groupItemList = cntrGroupUnTouchless.get(purcCntrUuid);
			if(groupItemList == null) {
				groupItemList = Lists.newArrayList();
			}
			groupItemList.add(unTouchlessItem);
			cntrGroupUnTouchless.put(purcCntrUuid, groupItemList);
		}
		
		this.createPoGroup(cntrGroupUnTouchless, false);
		return ResultMap.SUCCESS();
	}
	
	private void deletePoReqItemRcptQtaAloc(List<Map> qtaRemoveList) {
		for(Map qtaRemoveItem : qtaRemoveList) {
			if(Strings.isNullOrEmpty((String) qtaRemoveItem.get("po_req_item_rcpt_qta_aloc_uuid"))) {
				continue;
			}
			poReceiptRepository.deletePoReqItemRcptQtaAloc(qtaRemoveItem);
		}
	}
	
	public ResultMap validateDeleteRequestReqInfoByRfxChangeReq(Map reqData) {
		if(reqData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		List<Map> receiptList = poReceiptRepository.findListRequestReqInfoByRfxChangeReq(reqData);
		for(Map receiptItem : receiptList) {
			// 특정 품목이라도 이미 진행된 경우 리턴
			if(receiptItem.get("prgs_sts_ccd") != null && !"WTG".equals(receiptItem.get("prgs_sts_ccd"))) {
				return ResultMap.INVALID();
			}
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap deleteRequestReqInfoByRfxChangeReq(Map reqData) {
		if(reqData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		poReceiptRepository.deleteRequestReqInfoByRfxChangeReq(reqData);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap recoveryRequestReqInfoByRfxChangeReq(Map reqData) {
		if(reqData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		poReceiptRepository.recoveryRequestReqInfoByRfxChangeReq(reqData);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap savePoReqItemRcptQtaAloc(Map param) {
		Map receiptData = (Map) param.get("receiptData");
		List<Map> insertItems = (List<Map>) param.get("insertItems");
		List<Map> updateItems = (List<Map>) param.get("updateItems");
		List<Map> deleteItems = (List<Map>) param.get("deleteItems");
		
		for(Map insertItem : insertItems) {
			insertItem.put("po_req_item_rcpt_qta_aloc_uuid", UUID.randomUUID().toString());
			insertItem.put("po_req_item_rcpt_uuid", receiptData.get("po_req_item_rcpt_uuid"));
			poReceiptRepository.insertPoReqItemRcptQtaAloc(insertItem);
		}
		for(Map updateItem : updateItems) {
			poReceiptRepository.updatePoReqItemRcptQtaAloc(updateItem);
		}
		for(Map deleteItem : deleteItems) {
			poReceiptRepository.deletePoReqItemRcptQtaAloc(deleteItem);
		}
		
		return ResultMap.SUCCESS();
	}
}
