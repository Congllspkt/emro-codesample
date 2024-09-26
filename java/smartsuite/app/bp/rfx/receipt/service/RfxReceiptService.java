package smartsuite.app.bp.rfx.receipt.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.job.service.JobService;
import smartsuite.app.bp.admin.validator.ValidatorConst;
import smartsuite.app.bp.contract.contractcnd.service.PurcContractCndService;
import smartsuite.app.bp.itemMaster.itemcommon.service.ItemCommonService;
import smartsuite.app.bp.rfx.receipt.event.RfxReceiptEventPublisher;
import smartsuite.app.bp.rfx.receipt.repository.RfxReceiptRepository;
import smartsuite.app.bp.rfx.receipt.validator.RfxReceiptValidator;
import smartsuite.app.bp.rfx.rfx.service.RfxNxtPrcsService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.common.util.ListUtils;
import smartsuite.app.common.util.MapUtils;
import smartsuite.module.ModuleManager;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxReceiptService {
	
	@Inject
	RfxReceiptRepository rfxReceiptRepository;
	
	@Inject
	RfxReceiptValidator rfxReceiptValidator;
	
	@Inject
	RfxReceiptEventPublisher rfxReceiptEventPublisher;
	
	@Inject
	RfxNxtPrcsService rfxNxtPrcsService;
	
	@Inject
	JobService jobService;
	
	@Inject
	ItemCommonService itemCommonService;
	
	@Inject
	PurcContractCndService purcContractCndService;

	private String pcmModule = "pcm";

	public List findListRfxReceipt(Map param) {
		List<Map> receiptList = rfxReceiptRepository.findListRfxReceipt(param);
		
		if(ModuleManager.exist("PRO")) {
			List<Map> rfxWtgList = Lists.newArrayList();
			for(Map receiptItem : receiptList) {
				if(!"SPTPURC".equals(receiptItem.get("req_purp_ccd"))) {
					continue;
				}
				if(!("RCPT_WTG".equals(receiptItem.get("rcpt_sts_ccd")) || "RCPT".equals(receiptItem.get("rcpt_sts_ccd")))) {
					continue;
				}
				if(!"WTG".equals(receiptItem.get("prgs_sts_ccd"))) {
					continue;
				}
				rfxWtgList.add(receiptItem);
			}
			if(!rfxWtgList.isEmpty()) {
				List<Map<String, Object>> unitPriceList = rfxReceiptRepository.findListUnitPriceByRfxReqRcpt(ListUtils.getArrayValuesByList(rfxWtgList, "rfx_req_rcpt_uuid"));
				Map<String, List<Map>> rfxReqUnitPriceGroupMap = Maps.newHashMap();
				
				for(Map<String, Object> unitPrice : unitPriceList) {
					List<Map> rfxReqUnitPriceList = rfxReqUnitPriceGroupMap.get(unitPrice.get("rfx_req_rcpt_uuid"));
					if(rfxReqUnitPriceList == null) {
						rfxReqUnitPriceList = Lists.newArrayList();
						rfxReqUnitPriceGroupMap.put((String) unitPrice.get("rfx_req_rcpt_uuid"), rfxReqUnitPriceList);
					}
					rfxReqUnitPriceList.add(unitPrice);
				}
				
				// 대기 품목에서만 반복문 수행
				for(Map wtgItem : rfxWtgList) {
					List<Map> rfxReqUnitPriceList = rfxReqUnitPriceGroupMap.get(wtgItem.get("rfx_req_rcpt_uuid"));
					if(rfxReqUnitPriceList == null) {
						continue;
					}
					// 현재 시점 단가계약 싱글 밴더이므로 바로 매핑
					/*if(rfxReqUnitPriceList.size() == 1) {
						Map rfxReqUnitPrice = rfxReqUnitPriceList.get(0);
						// 확정되지 않음
						// 진행 시점에 확정
						wtgItem.put("req_purp_ccd", "UPRCCNTR_PURC");
						wtgItem.put("cntr_cnfd_yn", "N");
						wtgItem.put("multi_vd_yn", "N");
						wtgItem.put("cntr_uuid", rfxReqUnitPrice.get("cntr_uuid"));
						wtgItem.put("cntr_no", rfxReqUnitPrice.get("cntr_no"));
						wtgItem.put("cntr_revno", rfxReqUnitPrice.get("cntr_revno"));
						wtgItem.put("purc_cntr_uuid", rfxReqUnitPrice.get("purc_cntr_uuid"));
						wtgItem.put("purc_cntr_info_uuid", rfxReqUnitPrice.get("purc_cntr_info_uuid"));
						wtgItem.put("purc_cntr_item_uuid", rfxReqUnitPrice.get("purc_cntr_item_uuid"));
						wtgItem.put("purc_cntr_item_lno", rfxReqUnitPrice.get("purc_cntr_item_lno"));
					} else if(rfxReqUnitPriceList.size() > 1) {
						// 멀티 밴더이므로 멀티 여부만 체크
						wtgItem.put("cntr_cnfd_yn", "N");
						wtgItem.put("multi_vd_yn", "Y");
					}*/
					wtgItem.put("bpa_yn", "Y");
				}
			}
		}
		return receiptList;
	}
	
	public List findRfxDefaultDataByReqRcpts(List rfxReqRcptUuids) {
		Map param = Maps.newHashMap();
		param.put("rfx_req_rcpt_uuids", rfxReqRcptUuids);
		
		return rfxReceiptRepository.findRfxDefaultDataByReqRcpts(param);
	}

	public Map findRfxDefaultDataByRfxReqUuid(Map<String, Object> param) {
		return rfxReceiptRepository.findRfxDefaultDataByRfxReqUuid(param);
	}
	
	/**
	 * RFX 요청데이터 적재
	 *
	 * @param data
	 * @return
	 */
	public ResultMap receiptReqRfx(List<Map> requestList) {
		if(requestList == null) {
			return ResultMap.NOT_EXISTS("data not exists");
		}
		if(requestList.isEmpty()) {
			return ResultMap.NOT_EXISTS("data not exists");
		}
		
		// 구매요청이며 단가계약이 존재하는 경우 RFX 모듈 스킵 후 발주 요청으로 넘긴다.
		// 단가계약의 MOQ 보다 요청 수량이 적은 경우 발주로 넘기지 않고 견적이 받는다.
		// 발주 요청에서 반려 시 RFX 요청 목록으로 떨군다.
		List<Map> requestPoItemList = Lists.newArrayList();
		List<Map> receiptRfxItemList = Lists.newArrayList();
		for(Map requestItem : requestList) {
			if(!"SPTPURC".equals(requestItem.get("req_purp_ccd"))) {
				receiptRfxItemList.add(requestItem);
				continue;
			}
			
			BigDecimal reqQty = ConvertUtils.convertBigDecimal(requestItem.get("req_qty"));
			List<Map> unitPriceList = rfxReceiptEventPublisher.findListUnitPriceByItemAndOorg(requestItem);
			for(Map unitPriceItem : unitPriceList) {
				BigDecimal moq = ConvertUtils.convertBigDecimal(unitPriceItem.get("moq"));
				
				if(reqQty.compareTo(moq) >= 0) {
					requestItem.put("po_req_uuid", requestItem.get("rfx_req_rcpt_uuid"));
					requestPoItemList.add(requestItem);
					break;
				}
			}
			if(!requestPoItemList.contains(requestItem)) {
				receiptRfxItemList.add(requestItem);
			}
		}
		
		this.receiptList(receiptRfxItemList);
		
		//발주에서 처리하기로 진행함에 따라 afterProcess 로직을 변경하여 단가계약 존재여부에 따라 발주 전송만 진행한다.
		rfxReceiptEventPublisher.requestPoByUprcItem(requestPoItemList);
		//this.afterRequestPoByUprcItemProcess(requestList);
		//this.afterProcess(requestList);
		return ResultMap.SUCCESS();
	}
	
	private void receiptList(List<Map> requestList) {
		Date now = new Date();
		Map<String, List<String>> purcGrpMap = Maps.newHashMap();
		
		for(Map requestItem : requestList) {
			List<String> reqUsrPurcGrpList = purcGrpMap.get((String) requestItem.get("req_pic_id"));
			if(reqUsrPurcGrpList == null) {
				Map jobParam = Maps.newHashMap();
				jobParam.put("purc_grp_typ_ccd", "PURC");
				jobParam.put("usr_id", requestItem.get("req_pic_id"));
				List<Map> purcGrpList = jobService.findListPurcGrpByUsr(jobParam);
				
				reqUsrPurcGrpList = Lists.newArrayList();
				for(Map purcGrp : purcGrpList) {
					reqUsrPurcGrpList.add((String) purcGrp.get("purc_grp_cd"));
				}
				purcGrpMap.put((String) requestItem.get("req_pic_id"), reqUsrPurcGrpList);
			}
			Map item = this.makeReceiptData(now, requestItem, reqUsrPurcGrpList);
			//item = this.setItemOorgData(item);
			
			rfxReceiptRepository.insertReceiptReqRfx(item);
			
			// 구매그룹 내 요청자가 존재하는 경우 자동 접수
			if(reqUsrPurcGrpList.contains(requestItem.get("purc_grp_cd"))) {
				this.receiptData(item, (String) requestItem.get("req_pic_id"));
			}
		}
	}
	
	private void afterRequestPoByUprcItemProcess(List<Map> itemList) {
		List<Map> requestPoByUprcItemList = Lists.newArrayList();
		
		for(Map item : itemList) {
			if(!"SPTPURC".equals(item.get("req_purp_ccd"))) {
				continue;
			}
			
			List<Map> unitPriceList = rfxReceiptEventPublisher.findListUnitPriceByItemAndOorg(item);
			if(unitPriceList.size() > 0) {
				item.put("po_req_uuid", item.get("rfx_req_rcpt_uuid"));
				requestPoByUprcItemList.add(item);
			}
		}
		
		rfxReceiptEventPublisher.requestPoByUprcItem(requestPoByUprcItemList);
	}
	
	private void afterProcess(List<Map> itemList) {
		List<Map> touchlessItemList = Lists.newArrayList();
		
		for(Map item : itemList) {
			String touchlessYn = (String) item.get("tl_yn");
			String qtaYn = (String) item.get("qta_yn");
			if(touchlessYn == null) {
				continue;
			}
			if("N".equals(touchlessYn)) {
				continue;
			}
			if("UPRCCNTR_SGNG".equals(item.get("req_purp_ccd")) || "PSR".equals(item.get("req_purp_ccd"))) {
				continue;
			}
			// 쿼터 아닌 경우 싱글 벤더 단가만 존재해야 자동발주(touchless PO) 가능
			if(qtaYn == null || "N".equals(qtaYn)) {
				// Touchless 인 경우 단가 확인
				List<Map> unitPriceList = rfxReceiptEventPublisher.findListUnitPriceByItemAndOorg(item);
				if(unitPriceList.size() != 1) {
					continue;
				}
				Map unitPriceInfo = unitPriceList.get(0);
				item.put("cntr_uuid", unitPriceInfo.get("cntr_uuid"));
				item.put("cntr_no", unitPriceInfo.get("cntr_no"));
				item.put("cntr_revno", unitPriceInfo.get("cntr_revno"));
				item.put("purc_cntr_uuid", unitPriceInfo.get("purc_cntr_uuid"));
				item.put("purc_cntr_info_uuid", unitPriceInfo.get("purc_cntr_info_uuid"));
				item.put("purc_cntr_item_uuid", unitPriceInfo.get("purc_cntr_item_uuid"));
				item.put("purc_cntr_item_lno", unitPriceInfo.get("purc_cntr_item_lno"));
				touchlessItemList.add(item);
			} else {
				// 쿼터 단가계약 정보 확인
				List<Map> qtaUnitPriceList = rfxReceiptEventPublisher.findListUnitPriceQtaInfoByItemAndOorg(item);
				
				BigDecimal totalQtaRate = BigDecimal.ZERO;
				for(Map qtaUnitPrice : qtaUnitPriceList) {
					if(qtaUnitPrice.get("qtarate") == null) {
						continue;
					}
					totalQtaRate = totalQtaRate.add((BigDecimal) qtaUnitPrice.get("qtarate"));
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
					
					// 쿼터인 경우 배분 수량 내용 적재
					qtaItem.put("rfx_req_rcpt_qta_aloc_uuid", UUID.randomUUID().toString());
					qtaItemList.add(qtaItem);
				}
				
				this.calculateAlocQta(item, qtaItemList);
				for(Map qtaItem : qtaItemList) {
					rfxReceiptRepository.insertQtaNxtReqPrcs(qtaItem);
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
		
		this.requestNxtPrcs(cntrGroupTouchless, true);
	}
	
	/**
	 * 후속프로세스 touchless 전송
	 *
	 * @param touchlessItemList
	 */
	private void requestNxtPrcs(Map<String, List<Map>> cntrGroupItem, boolean touchless) {
		for(String key : cntrGroupItem.keySet()) {
			this.requestNxtPrcs(key, cntrGroupItem.get(key), touchless);
		}
	}
	
	private void requestNxtPrcs(String purcCntrUuid, List<Map> itemList, boolean touchless) {
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
		Map rfxNxtPrcsReqData = Maps.newHashMap();
		rfxNxtPrcsReqData.put("oorg_cd", firstItem.get("oorg_cd"));
		rfxNxtPrcsReqData.put("purc_typ_ccd", firstItem.get("purc_typ_ccd"));
		rfxNxtPrcsReqData.put("rfx_purp_ccd", "SPTPURC");
		rfxNxtPrcsReqData.put("purc_grp_cd", firstItem.get("purc_grp_cd"));
		rfxNxtPrcsReqData.put("cnfd_yn", "Y");
		rfxNxtPrcsReqData.put("nxt_prcs_ccd", "PO");
		
		Map copyPurcCntrData = Maps.newHashMap(purcCntrData);
		copyPurcCntrData.put("purc_cntr_uuid", null);
		
		Map copyPurcCntrInfoData = Maps.newHashMap(purcCntrInfoData);
		copyPurcCntrInfoData.put("purc_cntr_info_uuid", null);
		
		List<Map> copyItemList = Lists.newArrayList();
		for(Map item : itemList) {
			copyItemList.add(Maps.newHashMap(item));
		}
		for(Map copyItem : copyItemList) {
			// Key 가 다른 필드 매핑
			MapUtils.copyToDiffKeyValue(copyItem, Lists.newArrayList(
					"ref_cntr_uuid",
					"ref_cntr_no",
					"ref_cntr_revno",
					"ref_purc_cntr_uuid",
					"ref_purc_cntr_info_uuid",
					"ref_purc_cntr_item_uuid",
					"ref_purc_cntr_item_lno"
			), copyItem, Lists.newArrayList(
					"cntr_uuid",
					"cntr_no",
					"cntr_revno",
					"purc_cntr_uuid",
					"purc_cntr_info_uuid",
					"purc_cntr_item_uuid",
					"purc_cntr_item_lno"
			));
			
			copyItem.put("item_lno", copyItem.get("req_lno"));
			
			// 기 체결된 단가계약 단가 정보 세팅
			Map purcCntrItem = purcCntrItemMap.get(copyItem.get("purc_cntr_item_uuid"));
			if(purcCntrItem != null) {
				copyItem.put("item_uprc", purcCntrItem.get("item_uprc"));
			}
			
			String qtaYn = (String) copyItem.get("qta_yn");
			if(qtaYn == null || "N".equals(qtaYn)) {
				copyItem.put("item_qty", copyItem.get("req_qty"));
			} else {
				// 쿼터인 경우 배분 수량으로 세팅
				copyItem.put("item_qty", copyItem.get("aloc_qty"));
			}
			
			BigDecimal itemQty = ConvertUtils.convertBigDecimal(copyItem.get("item_qty"));
			BigDecimal itemUprc = ConvertUtils.convertBigDecimal(copyItem.get("item_uprc"));
			BigDecimal itemAmt = itemQty.multiply(itemUprc);
			copyItem.put("item_amt", itemAmt);
			copyItem.put("cntr_st_dt", copyItem.get("const_st_dt"));
			copyItem.put("cntr_exp_dt", copyItem.get("const_exp_dt"));
		}
		
		Map nxtPrcsParam = Maps.newHashMap();
		nxtPrcsParam.put("rfxNxtPrcsReqData", rfxNxtPrcsReqData);
		nxtPrcsParam.put("purcCntrData", copyPurcCntrData);
		nxtPrcsParam.put("purcCntrInfoData", copyPurcCntrInfoData);
		nxtPrcsParam.put("insertItems", copyItemList);
		ResultMap result = rfxNxtPrcsService.createRfxNxtPrcsReq(nxtPrcsParam);
		Map resultData = result.getResultData();
		
		this.updateNxtPrcsReqInfo((String) resultData.get("rfx_nxt_prcs_req_uuid"), itemList);
		
		List<Map> checkedRows = Lists.newArrayList();
		Map checked = Maps.newHashMap();
		checked.put("rfx_nxt_prcs_req_uuid", resultData.get("rfx_nxt_prcs_req_uuid"));
		checkedRows.add(checked);
		
		Map requestParam = Maps.newHashMap();
		requestParam.put("checkedRows", checkedRows);
		
		rfxNxtPrcsService.requestNxtPrcsByRfxRcpt(requestParam, touchless);
	}
	
	private void updateNxtPrcsReqInfo(String rfxNxtPrcsReqUuid, List<Map> itemList) {
		Set<String> itemGroup = Sets.newHashSet();
		for(Map item : itemList) {
			itemGroup.add((String) item.get("rfx_req_rcpt_uuid"));
			item.put("rfx_nxt_prcs_req_uuid", rfxNxtPrcsReqUuid);
			String qtaYn = (String) item.get("qta_yn");
			if("Y".equals(qtaYn)) {
				rfxReceiptRepository.updateQtaNxtReqPrcs(item);
			} else {
				rfxReceiptRepository.updateNxtPrcsReq(item);
			}
		}
		for(String key : itemGroup) {
			Map param = Maps.newHashMap();
			param.put("rfx_req_rcpt_uuid", key);
			rfxReceiptRepository.updateUnitPriceReqCompleted(param);
		}
	}
	
	/*private Map setItemOorgData(Map item) {
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
	}*/
	
	private Map makeReceiptData(Date now, Map requestItem, List<String> reqUsrPurcGrpList) {
		Map item = requestItem;
		
		item.put("rfx_req_rcpt_uuid", UUID.randomUUID().toString());
		if("SPTPURC".equals(requestItem.get("req_purp_ccd"))) {
			item.put("req_doc_typ_ccd", "PR");
			item.put("pr_item_uuid", requestItem.get("req_item_uuid"));
			item.put("pr_uuid", requestItem.get("req_uuid"));
			item.put("pr_no", requestItem.get("req_no"));
			item.put("pr_revno", requestItem.get("req_revno"));
			item.put("pr_lno", requestItem.get("req_lno"));
		} else if("UPRCCNTR_SGNG".equals(requestItem.get("req_purp_ccd"))) {
			item.put("req_doc_typ_ccd", "UPCR");
			item.put("upcr_item_uuid", requestItem.get("req_item_uuid"));
			item.put("upcr_uuid", requestItem.get("req_uuid"));
			item.put("upcr_no", requestItem.get("req_no"));
			item.put("upcr_revno", requestItem.get("req_revno"));
			item.put("upcr_lno", requestItem.get("req_lno"));
		} else if(ModuleManager.exist(pcmModule) && "PSR".equals(requestItem.get("req_purp_ccd"))) {
			item.put("req_doc_typ_ccd", "SR");
		}
		item.put("req_dt", now);
		item.put("rcpt_sts_ccd", "RCPT_WTG");
		item.put("prgs_sts_ccd", "WTG");
		item.put("rmng_qty", requestItem.get("req_qty"));
		item.put("qta_yn", "N");
		item.put("tl_yn", "N");
		return item;
	}
	
	protected void receiptData(Map param, String rfxPicId) {
		Map receiptParam = Maps.newHashMap(param);
		receiptParam.put("rfx_pic_id", rfxPicId);
		rfxReceiptRepository.receiptReq(receiptParam);
		rfxReceiptEventPublisher.receiptReq(receiptParam);
	}
	
	protected ResultMap reqValidator(List<Map> list, List<String> availableStsList) {
		List<String> rfxReqRcptUuids = ListUtils.getArrayValuesByList(list, "rfx_req_rcpt_uuid");
		
		Map validateParam = Maps.newHashMap();
		validateParam.put("availableStsList", availableStsList);
		validateParam.put("rfx_req_rcpt_uuids", rfxReqRcptUuids);
		
		ResultMap validator = rfxReceiptValidator.validate(validateParam);
		return validator;
	}
	
	/**
	 * RFX 요청데이터 접수
	 *
	 * @param param
	 * @return
	 */
	public ResultMap receiptReqs(Map param) {
		String rfxPicId = Auth.getCurrentUserName();
		List<Map> checkedList = (List<Map>) param.get("checkedList");
		ResultMap validator = this.reqValidator(checkedList, Lists.newArrayList("RCPT_WTG"));
		Map resultData = validator.getResultData();
		
		List<String> validRfxReqRcptUuids = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		
		for(Map checkedItem : checkedList) {
			if(!validRfxReqRcptUuids.contains(checkedItem.get("rfx_req_rcpt_uuid"))) {
				continue;
			}
			this.receiptData(checkedItem, rfxPicId);
		}
		return validator;
	}
	
	/**
	 * RFX 요청데이터 반려
	 *
	 * @param param
	 * @return
	 */
	public ResultMap returnReqs(Map param) {
		List<Map> checkedList = (List<Map>) param.get("checkedList");
		ResultMap validator = this.reqValidator(checkedList, Lists.newArrayList("RCPT_WTG", "RCPT"));
		Map resultData = validator.getResultData();
		
		List<String> validRfxReqRcptUuids = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		
		for(Map checkedItem : checkedList) {
			if(!validRfxReqRcptUuids.contains(checkedItem.get("rfx_req_rcpt_uuid"))) {
				continue;
			}
			checkedItem.put("req_ret_rsn", param.get("req_ret_rsn"));
			rfxReceiptRepository.returnReq(checkedItem);
			rfxReceiptEventPublisher.returnReq(checkedItem);
		}
		
		return validator;
	}
	
	public ResultMap changePurcGrp(Map param) {
		List<Map> checkedList = (List<Map>) param.get("checkedList");
		ResultMap validator = this.reqValidator(checkedList, Lists.newArrayList("RCPT_WTG", "RCPT"));
		Map resultData = validator.getResultData();
		
		List<String> validRfxReqRcptUuids = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		
		for(Map checkedItem : checkedList) {
			if(!validRfxReqRcptUuids.contains(checkedItem.get("rfx_req_rcpt_uuid"))) {
				continue;
			}
			checkedItem.put("purc_grp_cd", param.get("purc_grp_cd"));
			rfxReceiptRepository.changePurcGrp(checkedItem);
		}
		return validator;
	}
	
	public ResultMap checkReqItemsForNextStep(Map param) {
		List<Map> checkedList = (List<Map>) param.get("checkedList");
		return this.reqValidator(checkedList, Lists.newArrayList("RCPT_WTG", "RCPT"));
	}
	
	public void updateProgressStatus(String rfxReqRcptUuid, String status) {
		if(rfxReqRcptUuid == null) {
			return;
		}
		
		Map param = Maps.newHashMap();
		param.put("rfx_req_rcpt_uuid", rfxReqRcptUuid);
		param.put("prgs_sts_ccd", status);
		rfxReceiptRepository.updateProgressStatus(param);
	}
	
	public void cancelProcess(List<Map> items) {
		List<String> rfxReqRcptUuids = ListUtils.getArrayValuesByList(items, "rfx_req_rcpt_uuid");
		if(rfxReqRcptUuids != null && !rfxReqRcptUuids.isEmpty()) {
			for(String rfxReqRcptUuid : rfxReqRcptUuids) {
				this.updateProgressStatus(rfxReqRcptUuid, "WTG");
			}
		}
	}
	
	public ResultMap saveNxtPrcsReq(Map param) {
		List<Map> receiptList = this.findRfxDefaultDataByReqRcpts((List) param.get("rfx_req_rcpt_uuids"));
		if(receiptList.isEmpty()) {
			return null;
		}
		
		String rfxPicId = Auth.getCurrentUserName();
		for(Map receiptItem : receiptList) {
			// 접수 대기중인 경우 접수
			if("RCPT_WTG".equals(receiptItem.get("rcpt_sts_ccd"))) {
				this.receiptData(receiptItem, rfxPicId);
			}
		}
		
		int maxNo = 10;
		for(Map receiptItem : receiptList) {
			receiptItem.put("item_qty", receiptItem.get("req_qty"));
			receiptItem.put("item_uprc", receiptItem.get("req_uprc"));
			receiptItem.put("item_amt", receiptItem.get("req_amt"));
			receiptItem.put("item_lno", maxNo);
			maxNo += 10;
			
			receiptItem.put("cntr_st_dt", receiptItem.get("const_st_dt"));
			receiptItem.put("cntr_exp_dt", receiptItem.get("const_exp_dt"));
		}
		
		Map firstReceiptItem = (Map) receiptList.get(0);
		Map rfxNxtPrcsReqData = Maps.newHashMap();
		rfxNxtPrcsReqData.put("oorg_cd", firstReceiptItem.get("oorg_cd"));
		rfxNxtPrcsReqData.put("purc_typ_ccd", firstReceiptItem.get("purc_typ_ccd"));
		rfxNxtPrcsReqData.put("cur_ccd", firstReceiptItem.get("cur_ccd"));
		rfxNxtPrcsReqData.put("rfx_purp_ccd", firstReceiptItem.get("rfx_purp_ccd"));
		rfxNxtPrcsReqData.put("disp_purc_grp_nm", firstReceiptItem.get("disp_purc_grp_nm"));
		rfxNxtPrcsReqData.put("purc_grp_cd", firstReceiptItem.get("purc_grp_cd"));
		rfxNxtPrcsReqData.put("cnfd_yn", "N");
		rfxNxtPrcsReqData.put("req_sts_ccd", null);
		
		if("UPRCCNTR_SGNG".equals(rfxNxtPrcsReqData.get("rfx_purp_ccd")) || "PSR".equals(rfxNxtPrcsReqData.get("rfx_purp_ccd"))) {
			rfxNxtPrcsReqData.put("nxt_prcs_ccd", "UPRCCNTR");
		} else {
			// SPOT구매 인 경우 계약 기본값
			rfxNxtPrcsReqData.put("nxt_prcs_ccd", "CNTR");
		}
		
		Map nxtPrcsParam = Maps.newHashMap();
		nxtPrcsParam.put("rfxNxtPrcsReqData", rfxNxtPrcsReqData);
		nxtPrcsParam.put("insertItems", receiptList);
		ResultMap result = rfxNxtPrcsService.createRfxNxtPrcsReq(nxtPrcsParam);
		Map resultData = result.getResultData();
		
		this.updateRfxNxtPrcsStats(receiptList, (String) resultData.get("rfx_nxt_prcs_req_uuid"), "NXT_REQ_CRNG");
		return ResultMap.SUCCESS(resultData);
	}
	
	public void updateRfxNxtPrcsStats(List<Map> receiptList, String rfxNxtPrcsReqUuid, String prgsStsCcd) {
		if(receiptList == null) {
			return;
		}
		if(receiptList.isEmpty()) {
			return;
		}
		if(rfxNxtPrcsReqUuid == null) {
			return;
		}
		for(Map receiptItem : receiptList) {
			if(Strings.isNullOrEmpty((String) receiptItem.get("rfx_req_rcpt_uuid"))
					&& Strings.isNullOrEmpty((String) receiptItem.get("pr_item_uuid"))
					&& Strings.isNullOrEmpty((String) receiptItem.get("upcr_item_uuid"))
			) {
				continue;
			}
			receiptItem.put("rfx_nxt_prcs_req_uuid", rfxNxtPrcsReqUuid);
			receiptItem.put("prgs_sts_ccd", prgsStsCcd);
			rfxReceiptRepository.updateRfxNxtPrcsStats(receiptItem);
		}
	}
	
	public List findListUprccntr(Map param) {
		Map rfxReqRcptInfo = param;
		List<Map> unitPriceList = rfxReceiptEventPublisher.findListUnitPriceQtaInfoByItemAndOorg(param);
		
		if("Y".equals(rfxReqRcptInfo.get("qta_yn"))) {
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
				this.calculateAlocQta(rfxReqRcptInfo, existQtaList);
			}
		}
		
		return unitPriceList;
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
	
	public ResultMap requestUprccntrPo(Map param) {
		List<Map> unTouchlessItemList = Lists.newArrayList();
		
		List<Map> checkedList = (List<Map>) param.get("checkedList");
		for(Map item : checkedList) {
			String qtaYn = (String) item.get("qta_yn");
			// 쿼터 아닌 경우 싱글 벤더 단가만 존재해야 자동발주(touchless PO) 가능
			if(qtaYn == null || "N".equals(qtaYn)) {
				// 사용할 단가 선택 완료된 시점
				unTouchlessItemList.add(item);
			} else {
				// 쿼터 단가계약 정보 확인
				List<Map> qtaUnitPriceList = (List<Map>) item.get("qtaList");
				
				List<Map> qtaItemList = Lists.newArrayList();
				for(Map qtaUnitPrice : qtaUnitPriceList) {
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
					qtaItem.put("rfx_req_rcpt_qta_aloc_uuid", UUID.randomUUID().toString());
					qtaItemList.add(qtaItem);
					
					rfxReceiptRepository.insertQtaNxtReqPrcs(qtaItem);
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
		
		this.requestNxtPrcs(cntrGroupUnTouchless, false);
		return ResultMap.SUCCESS();
	}
	
	public List findListRfxReqRcptQta(Map param) {
		return rfxReceiptRepository.findListRfxReqRcptQta(param);
	}
	
	public ResultMap returnUprcItemReq(List<Map> returnUprcItemList) {
		this.receiptList(returnUprcItemList);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap saveRfxNxtPrcsReq(Map param) {
		ResultMap resultMap = rfxNxtPrcsService.saveRfxNxtPrcsReq(param);
		if(resultMap.isFail()) {
			return resultMap;
		}
		Map resultData = resultMap.getResultData();
		List<Map> insertItems = (List<Map>) param.get("insertItems");
		if(insertItems != null && !insertItems.isEmpty()) {
			for(Map insertItem : insertItems) {
				Map rfxReqData = rfxReceiptRepository.findReqRcptByOtherUuid(insertItem);
				// 접수 대기중인 경우 접수
				if("RCPT_WTG".equals(rfxReqData.get("rcpt_sts_ccd"))) {
					this.receiptData(rfxReqData, Auth.getCurrentUserName());
				}
			}
			
			this.updateRfxNxtPrcsStats(insertItems, (String) resultData.get("rfx_nxt_prcs_req_uuid"), "NXT_REQ_CRNG");
		}
		
		return ResultMap.SUCCESS(resultData);
	}
	
	public ResultMap directRequestNxtPrcs(Map param) {
		ResultMap resultMap = this.saveRfxNxtPrcsReq(param);
		if(resultMap.isFail()) {
            return resultMap;
        }
		Map resultData = resultMap.getResultData();
		
		List<Map> checkedRows = Lists.newArrayList();
		Map checked = Maps.newHashMap();
		checked.put("rfx_nxt_prcs_req_uuid", resultData.get("rfx_nxt_prcs_req_uuid"));
		checkedRows.add(checked);
		
		Map requestParam = Maps.newHashMap();
		requestParam.put("checkedRows", checkedRows);
		
		rfxNxtPrcsService.requestNxtPrcsByRfxRcpt(requestParam, false);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap validateDeleteRequestReqInfoByChangeReq(Map reqData) {
		if(reqData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		List<Map> receiptList = rfxReceiptRepository.findListRequestReqInfoByChangeReq(reqData);
		for(Map receiptItem : receiptList) {
			// 특정 품목이라도 이미 진행된 경우 리턴
			if(receiptItem.get("prgs_sts_ccd") != null && !"WTG".equals(receiptItem.get("prgs_sts_ccd"))) {
				return ResultMap.INVALID();
			}
		}
		
		return rfxReceiptEventPublisher.validateDeleteRequestReqInfoByRfxChangeReq(reqData);
	}
	
	public ResultMap deleteRequestReqInfoByChangeReq(Map reqData) {
		if(reqData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		rfxReceiptRepository.deleteRequestReqInfoByChangeReq(reqData);
		return rfxReceiptEventPublisher.deleteRequestReqInfoByRfxChangeReq(reqData);
	}
	
	public ResultMap recoveryRequestReqInfoByChangeReq(Map reqData) {
		if(reqData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		rfxReceiptRepository.recoveryRequestReqInfoByChangeReq(reqData);
		return rfxReceiptEventPublisher.recoveryRequestReqInfoByRfxChangeReq(reqData);
	}

	public ResultMap createRfxreqFromPcm(List<Map<String, Object>> srItemList) {
		Date now = new Date();
		List resultList = Lists.newArrayList();
		for(Map<String, Object> srItemInfo : srItemList) {
			srItemInfo.put("req_purp_ccd", "PSR");

			Map<String, Object> item = this.makeReceiptData(now, srItemInfo, Lists.newArrayList());
			rfxReceiptRepository.insertReceiptReqRfx(item);
			resultList.add(item);
		}

        return ResultMap.SUCCESS(resultList);
    }

	public ResultMap requestCopyPrToUpcr(Map param) {
		if (param == null ||!param.containsKey("rfx_req_rcpt_uuids")) {
            return ResultMap.NOT_EXISTS();
        }
		List<Map<String, Object>> rfxReqRcptList = rfxReceiptRepository.findListRfxReceiptByRfxReqRcptUuids(param);
		List<String> prItemUuids = rfxReqRcptList.stream()
				.map(map -> (String) map.get("pr_item_uuid"))
				.collect(Collectors.toList());

		Map<String, Object> copyPrParam = Maps.newHashMap();
		copyPrParam.put("pr_item_uuids", prItemUuids);
		List<Map<String, Object>> prItems = rfxReceiptEventPublisher.findListPrItemByPrItemUuids(copyPrParam);
		List<Map<String, Object>> copyPrToUpcrs= rfxReceiptEventPublisher.requestCopyPrsToUpcrs(prItems);

		List<String> upcrItemUuids = copyPrToUpcrs.stream()
				.map(map -> (String) map.get("upcr_item_uuid"))
				.collect(Collectors.toList());
		Map<String, Object> upcrParam = Maps.newHashMap();
		upcrParam.put("upcr_item_uuids", upcrItemUuids);
		List<Map<String,Object>> copyPrToUpcrsRfxReqRcptList = rfxReceiptRepository.findListRfxReceiptByOtherUuids(upcrParam);
		return ResultMap.SUCCESS(copyPrToUpcrsRfxReqRcptList);
	}

}
