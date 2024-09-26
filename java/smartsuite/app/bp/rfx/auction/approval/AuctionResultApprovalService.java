package smartsuite.app.bp.rfx.auction.approval;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.rfx.auction.service.AuctionBidService;
import smartsuite.app.bp.rfx.auction.service.AuctionItemService;
import smartsuite.app.bp.rfx.auction.service.AuctionService;
import smartsuite.app.bp.rfx.auction.service.AuctionVendorService;
import smartsuite.app.bp.rfx.rfx.service.RfxItemService;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * 역경매 결과품의 관련 처리하는 서비스 Class.
 *
 * @FileName AuctionProgressApprovalService.java
 * @package smartsuite.app.bp.rfx.auction.approval
 * @author kh_lee
 * @Since 2016. 6. 10
 * @변경이력 : [2016. 6. 10] lee 최초작성
 *
 */
@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class AuctionResultApprovalService implements ApprovalStrategy {

	/** The pro status processor. */
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	RfxItemService rfxItemService;
	
	@Inject
	AuctionService auctionService;
	
	@Inject
	AuctionItemService auctionItemService;
	
	@Inject
	AuctionVendorService auctionVendorService;
	
	@Inject
	AuctionBidService auctionBidService;
	
	@Inject
	private SharedService sharedService;
	
	/**
	 * 승인
	 */
	@Override
	public void doApprove(String approvalType, String appId){
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		
		Map auctionInfo = auctionService.findAuction(param);
		
		rfxStatusService.approveApprovalRfxResult(auctionInfo);
		rfxItemService.updateRfxItemStlInfo(auctionInfo);
		auctionService.completeAuction(auctionInfo);
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
	 * 상신취소
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

	@Override
	public Map makeParam(String approvalType, String appId) {
		Map newParam = Maps.newHashMap();
		Map resultMap = Maps.newHashMap();
		
		newParam.put("rfx_uuid",appId);
		newParam.put("apvl_typ_ccd", approvalType);
		
		Map auctionInfo = auctionService.findAuction(newParam);
		
		if(auctionInfo != null) {
			List<Map> rfxItems = auctionItemService.findListAuctionItem(newParam);
			List<Map> vendorInfoList = auctionVendorService.findListAuctionVendor(newParam);
			
			//공통코드값 코드명으로 변환
			auctionInfo.put("p2p_purc_typ_ccd_nm", sharedService.findCodeName(auctionInfo.get("purc_typ_ccd"), "P045"));			//구매 유형
			auctionInfo.put("rfx_purp_ccd_nm", sharedService.findCodeName(auctionInfo.get("rfx_purp_ccd"), "P049"));	// 구매요청/RFX 목적
			auctionInfo.put("rfx_typ_ccd_nm", sharedService.findCodeName(auctionInfo.get("rfx_typ_ccd"), "P033"));			//RFx 유형
			auctionInfo.put("comp_typ_ccd_nm", sharedService.findCodeName(auctionInfo.get("comp_typ_ccd"), "P211"));	//경쟁 유형
			auctionInfo.put("item_slctn_typ_ccd_nm", sharedService.findCodeName(auctionInfo.get("item_slctn_typ_ccd"), "P002"));		//총액/품목별 선정
			auctionInfo.put("domovrs_div_ccd_nm", sharedService.findCodeName(auctionInfo.get("domovrs_div_ccd"), "C024"));	//내외자구분
			auctionInfo.put("slctn_typ_ccd_nm", sharedService.findCodeName(auctionInfo.get("slctn_typ_ccd"), "P007"));	//선정 유형(가격선정방식)
			
			auctionInfo.put("pymtmeth_ccd_nm", sharedService.findCodeName(auctionInfo.get("pymtmeth_ccd"), "P009"));		//대금지급조건
			auctionInfo.put("dlvymeth_ccd_nm", sharedService.findCodeName(auctionInfo.get("dlvymeth_ccd"), "P010"));	//인도조건
			
			//운영조직
			auctionInfo.put("oper_org_cd_nm", sharedService.findOperationOrganizationName((String) auctionInfo.get("oorg_cd"), "PO"));
			
			//Yn값 변환
			auctionInfo.put("coststr_use_yn_str", changeYntoYesOrNo(auctionInfo.get("coststr_use_yn")));
			auctionInfo.put("bfg_yn_str", changeYntoYesOrNo(auctionInfo.get("bfg_yn")));
			auctionInfo.put("presn_yn_str", changeYntoYesOrNo(auctionInfo.get("presn_yn")));
			auctionInfo.put("prtl_bid_perm_yn_str", changeYntoYesOrNo(auctionInfo.get("prtl_bid_perm_yn")));
			
			//rfx시간 설정하는 함수 
			this.setRfxStartCloseDt(auctionInfo);
			
			if("BYTOT".equals(auctionInfo.get("item_slctn_typ_ccd").toString())) {
				//총액인경우
				Map findListVdAttend = auctionBidService.findListVdAttend(newParam);	//품목별 협력사 순위 조회 함수
				
				List<Map> vendorRankInfoList = (List<Map>)findListVdAttend.get("auctionQtas");		//협력사순위
				List<Map> rfxBidsList = (List<Map>)findListVdAttend.get("rfxBidItems");		//투찰 상세
				
				resultMap.put("vendorRankInfoList", vendorRankInfoList);	
				resultMap.put("auctionBidsList", rfxBidsList);		
				resultMap.put("rowspanList", checkSameValueForRowspan(rfxBidsList, "BYTOT"));	//서식에서 rowspan을 사용할 곳이 있어 호출되는 함수
				
			} else {
				//품목인경우
				List<Map> findListAllItemAttend = auctionBidService.findListAllItemAttend(newParam);
				
				resultMap.put("itemVdRankList", findListAllItemAttend);
				resultMap.put("rowspanList", checkSameValueForRowspan(findListAllItemAttend, "BYITEM"));	//서식에서 rowspan을 사용할 곳이 있어 호출되는 함수
			}
			
			resultMap.put("auctionInfo", auctionInfo);
			resultMap.put("rfxItems", rfxItems);
			resultMap.put("vendorInfoList", vendorInfoList);
		}
		
		return resultMap;
	}
	
	/**
	 * Yn 값이 Y 일때는 Yes, N 일때는 No로 변경해주는 함수.
	 *
	 * @param ynValue
	 */
	private String changeYntoYesOrNo(Object ynValue) {
		String yesOrNoValue = "";
		
		if(ynValue != null) {
			if("Y".equals(ynValue.toString())) {
				yesOrNoValue = "Yes";
			} else {
				yesOrNoValue = "No";
			}
		}
		
		return yesOrNoValue;
	}
	
	/**
	 * 년월일, 시, 분을 설정하기 위한 함수
	 * 
	 */
	private void setRfxStartCloseDt(Map rfxInfo) {
		//rfx시작 년월일, 시, 분 설정
		if(rfxInfo.get("rfx_st_dttm") != null) {
			Date rfxStartDt = (Date)rfxInfo.get("rfx_st_dttm");
			
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(rfxStartDt);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
			
			rfxInfo.put("rfx_st_dttm_hour", hour);
			rfxInfo.put("rfx_st_dttm_min", min);
			rfxInfo.put("rfx_st_dttm_ymd", ft.format(rfxStartDt));
		}
		
		//rfx종료 년월일, 시, 분 설정
		if(rfxInfo.get("rfx_clsg_dttm") != null) {
			Date rfxColseDt = (Date)rfxInfo.get("rfx_clsg_dttm");
			
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(rfxColseDt);
			int closeHour = cal.get(Calendar.HOUR_OF_DAY);
			int closeMin = cal.get(Calendar.MINUTE);
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
			
			rfxInfo.put("rfx_clsg_dttm_hour", closeHour);
			rfxInfo.put("rfx_clsg_dttm_min", closeMin);
			rfxInfo.put("rfx_clsg_dttm_ymd", ft.format(rfxColseDt));
		}
		
		//rfx개찰 년원일, 시, 분 설정
		if(rfxInfo.get("open_dttm") != null) {
			Date openDt = (Date)rfxInfo.get("open_dttm");
			
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(openDt);
			int openHour = cal.get(Calendar.HOUR_OF_DAY);
			int openMin = cal.get(Calendar.MINUTE);
			SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd", Locale.getDefault());
			
			rfxInfo.put("open_dttm_hour", openHour);
			rfxInfo.put("open_dttm_min", openMin);
			rfxInfo.put("open_dttm_ymd", ft.format(openDt));
		}
	}
	
	/**
	 * 서식에서 rowspan을 하기위해 동일한 rfx_lno 또는 vd_cd의 수를 구함
	 *
	 * @param list, type
	 */
	private int[] checkSameValueForRowspan(List<Map> list, String type) {
		int[] rowspanList = new int[list.size()];		//서식에서 rowspan을 구할 때, 필요한 배열
		
		int sameCnt = 1;	//동일한 rfx_lno 또는 vd_cd 의 수를 count함
		int baseIdx = 0;
		
		/** 파라미터로 넘어오는 type 이란?
		 * DOC : 총액인경우 투찰 상세 조회 -> vd_cd : 업체코드
		 * ITM : 품목인경우 품목별 협력사 순위 조회 -> rfx_lno : rfx 품목항번
		 * */
		String typeValue = "BYTOT".equals(type) ? "vd_cd" : "rfx_lno";
		
		if(list.size() > 1) {
			Map baseRfxQtas = list.get(baseIdx);
			for(int i = 1 ; i < list.size() ; i++) {
				Map nextRfxQtas = list.get(i);
				
				if(baseRfxQtas.get(typeValue).equals(nextRfxQtas.get(typeValue))){	
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
