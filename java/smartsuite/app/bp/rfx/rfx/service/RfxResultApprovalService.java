package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RfxResultApproval 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @FileName RfxResultApprovalService.java
 * @package smartsuite.app.bp.rfx.rfx.approval
 * @Since 2016. 5. 24
 * @변경이력 : [2016. 5. 24] Yeon-u Kim 최초작성
 * @see
 */
@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class RfxResultApprovalService implements ApprovalStrategy {
	
	/**
	 * The pro status processor.
	 */
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	RfxItemService rfxItemService;
	
	@Inject
	RfxService rfxService;
	
	@Inject
	RfxBidService rfxBidService;
	
	@Inject
	private SharedService sharedService;

	@Inject
	private FormatterProvider formatProvider;
	
	/**
	 * 승인
	 */
	@Override
	public void doApprove(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		
		Map rfxInfo = rfxService.findRfxByResult(param);
		
		rfxStatusService.approveApprovalRfxResult(rfxInfo);
		rfxItemService.updateRfxItemStlInfo(rfxInfo);
		rfxService.completeRfx(rfxInfo);
	}
	
	/**
	 * 반려
	 */
	@Override
	public void doReject(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		rfxStatusService.rejectApprovalRfxResult(param);
	}
	
	/**
	 * 취소
	 */
	@Override
	public void doCancel(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		rfxStatusService.cancelApprovalRfxResult(param);
	}
	
	/**
	 * 상신
	 */
	@Override
	public void doSubmit(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		rfxStatusService.submitApprovalRfxResult(param);
	}
	
	/**
	 * 저장
	 */
	@Override
	public void doTemporary(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		rfxStatusService.temporarySaveApprovalRfxResult(param);
	}
	
	/**
	 * 결재서식 param을 위해 업무단 정보 조회
	 *
	 * @param approvalType, appId
	 */
	@Override
	public Map<String, Object> makeParam(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		param.put("rfx_uuid", appId);
		param.put("apvl_typ_ccd", approvalType);
		
		Map<String, Object> rfxInfo = rfxService.findRfxByResult(param);
		
		if(rfxInfo != null) {
			param.put("rfxData", rfxInfo);
			
			//코드값을 코드명으로 변경
			rfxInfo.put("p2p_purc_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("purc_typ_ccd"), "P045"));            //구매 유형
			rfxInfo.put("rfx_purp_ccd_nm", sharedService.findCodeName(rfxInfo.get("rfx_purp_ccd"), "P049"));    // 구매요청/RFX 목적
			rfxInfo.put("rfx_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("rfx_typ_ccd"), "P033"));            //RFx 유형
			rfxInfo.put("slctn_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("slctn_typ_ccd"), "P007"));    //선정 유형(가격선정방식)
			rfxInfo.put("item_slctn_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("item_slctn_typ_ccd"), "P002"));        //총액/품목별 선정
			
			rfxInfo.put("oper_org_cd_nm", sharedService.findOperationOrganizationName((String) rfxInfo.get("oorg_cd"), "PO"));    //운영조직
			
			//open_yn값 설정.
			rfxInfo.put("open_yn", "Y");
			
			//rfx기본정보 설정
			resultMap.put("rfxInfo", rfxInfo);
			
			if("BYTOT".equals(rfxInfo.get("item_slctn_typ_ccd").toString())) {
				//총액인경우
				Map<String, Object> totalEvalAmtList = rfxService.findListRankingTotalEvalAmount(param);                //협력사 순위List, 협력사별견적내역List 조회하는 함수
				List<Map<String, Object>> vendorRankInfoList = (List<Map<String, Object>>) totalEvalAmtList.get("rfxBids");        //협력사순위
				List<Map<String, Object>> rfxBidsList = (List<Map<String, Object>>) totalEvalAmtList.get("rfxBidItems");        //투찰 상세
				
				//format적용
				vendorRankInfoList = getFormatterVendorRankInfoList(vendorRankInfoList);
				resultMap.put("vendorRankInfoList", vendorRankInfoList);
				//format 적용
				rfxBidsList = getApprovalFormatDataByQtas(rfxBidsList);
				resultMap.put("rfxBidsList", rfxBidsList);
				resultMap.put("rowspanList", checkSameValueForRowspan(rfxBidsList, "BYTOT"));    //서식에서 rowspan을 사용할 곳이 있어 호출되는 함수
			} else {
				//품목인경우
				List<Map<String, Object>> itemVdRankList = rfxService.findListRfxItemRfxAttends(param);    //품목별 협력사 순위 조회 함수
				
				//format적용
				itemVdRankList = getFormatterItemVdRankList(itemVdRankList);
				resultMap.put("itemVdRankList", itemVdRankList);
				resultMap.put("rowspanList", checkSameValueForRowspan(itemVdRankList, "BYITEM"));    //서식에서 rowspan을 사용할 곳이 있어 호출되는 함수
			}
		}
		
		return resultMap;
	}
	
	private List<Map<String, Object>> getFormatterVendorRankInfoList(List<Map<String, Object>> vendorRankInfoList) {
		List<Map<String, Object>> lists = Lists.newArrayList();
		lists = vendorRankInfoList;
		for(Map<String, Object> item : lists) {
			BigDecimal tgtAmt = BigDecimal.ZERO;
			BigDecimal amt = BigDecimal.ZERO;
			if(item.get("rfx_bid_amt") != null) {
				amt = (BigDecimal) item.get("rfx_bid_amt");
			}
			if(item.get("tgt_amt") != null) {
				tgtAmt = (BigDecimal) item.get("tgt_amt");
			}
			item.put("tgt_amt", formatProvider.getPrecFormatZero("amt", tgtAmt, true));
			item.put("rfx_bid_amt", formatProvider.getPrecFormatZero("amt", amt, true));
		}
		return lists;
	}
	
	private List<Map<String, Object>> getApprovalFormatDataByQtas(List<Map<String, Object>> rfxBidsList) {
		List<Map<String, Object>> lists = Lists.newArrayList();
		lists = rfxBidsList;
		for(Map<String, Object> item : lists) {
			BigDecimal qty = BigDecimal.ZERO;
			BigDecimal price = BigDecimal.ZERO;
			BigDecimal tgtPrice = BigDecimal.ZERO;
			BigDecimal amt = BigDecimal.ZERO;
			if(item.get("rfx_qty") != null) {
				qty = (BigDecimal) item.get("rfx_qty");
			}
			if(item.get("rfx_item_subm_uprc") != null) {
				price = (BigDecimal) item.get("rfx_item_subm_uprc");
			}
			if(item.get("rfx_item_subm_amt") != null) {
				amt = (BigDecimal) item.get("rfx_item_subm_amt");
			}
			if(item.get("rfx_trg_uprc") != null) {
				tgtPrice = (BigDecimal) item.get("rfx_trg_uprc");
			}
			item.put("rfx_qty", formatProvider.getPrecFormatZero("qty", qty, true));
			item.put("rfx_item_subm_uprc", formatProvider.getPrecFormatZero("price", price, true));
			item.put("rfx_trg_uprc", formatProvider.getPrecFormatZero("price", tgtPrice, true));
			item.put("rfx_item_subm_amt", formatProvider.getPrecFormatZero("amt", amt, true));
		}
		return lists;
	}
	
	private List<Map<String, Object>> getFormatterItemVdRankList(List<Map<String, Object>> itemVdRankList) {
		List<Map<String, Object>> lists = Lists.newArrayList();
		lists = itemVdRankList;
		for(Map<String, Object> item : lists) {
			BigDecimal price = BigDecimal.ZERO;
			BigDecimal tgtPrice = BigDecimal.ZERO;
			BigDecimal amt = BigDecimal.ZERO;
			BigDecimal stlQty = BigDecimal.ZERO;
			if(item.get("slctn_qty") != null) {
				stlQty = (BigDecimal) item.get("slctn_qty");
			}
			if(item.get("rfx_item_subm_uprc") != null) {
				price = (BigDecimal) item.get("rfx_item_subm_uprc");
			}
			if(item.get("rfx_item_subm_amt") != null) {
				amt = (BigDecimal) item.get("rfx_item_subm_amt");
			}
			if(item.get("rfx_trg_uprc") != null) {
				tgtPrice = (BigDecimal) item.get("rfx_trg_uprc");
			}
			item.put("slctn_qty", formatProvider.getPrecFormatZero("qty", stlQty, true));
			item.put("rfx_item_subm_uprc", formatProvider.getPrecFormatZero("price", price, true));
			item.put("rfx_item_subm_amt", formatProvider.getPrecFormatZero("amt", amt, true));
			item.put("rfx_trg_uprc", formatProvider.getPrecFormatZero("price", tgtPrice, true));
		}
		return lists;
	}
	
	/**
	 * 서식에서 rowspan을 하기위해 동일한 rfx_lno 또는 vd_cd의 수를 구함
	 *
	 * @param list, type
	 */
	private int[] checkSameValueForRowspan(List<Map<String, Object>> list, String type) {
		int[] rowspanList = new int[list.size()];        //서식에서 rowspan을 구할 때, 필요한 배열
		
		int sameCnt = 1;    //동일한 rfx_lno 또는 vd_cd 의 수를 count함
		int baseIdx = 0;
		
		/** 파라미터로 넘어오는 type 이란?
		 * DOC : 총액인경우 투찰 상세 조회 -> vd_cd : 업체코드
		 * ITM : 품목인경우 품목별 협력사 순위 조회 -> rfx_lno : rfx 품목항번
		 * */
		String typeValue = "BYTOT".equals(type) ? "vd_cd" : "rfx_lno";
		
		if(list.size() > 1) {
			Map<String, Object> baseRfxQtas = list.get(baseIdx);
			for(int i = 1; i < list.size(); i++) {
				Map<String, Object> nextRfxQtas = list.get(i);
				
				if(baseRfxQtas.get(typeValue).equals(nextRfxQtas.get(typeValue))) {
					sameCnt++;
				} else {
					rowspanList[baseIdx] = sameCnt;
					baseRfxQtas = nextRfxQtas;
					baseIdx = i;
					sameCnt = 1;
				}
			}
			rowspanList[baseIdx] = sameCnt;
		} else {
			rowspanList[baseIdx] = sameCnt;
		}
		
		return rowspanList;
	}
}
