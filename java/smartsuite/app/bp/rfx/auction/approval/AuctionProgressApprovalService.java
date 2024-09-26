package smartsuite.app.bp.rfx.auction.approval;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.rfx.auction.scheduler.AuctionSchedulerService;
import smartsuite.app.bp.rfx.auction.service.AuctionItemService;
import smartsuite.app.bp.rfx.auction.service.AuctionService;
import smartsuite.app.bp.rfx.auction.service.AuctionVendorService;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * 역경매 진행품의 관련 처리하는 서비스 Class.
 *
 * @FileName AuctionProgressApprovalService.java
 * @package smartsuite.app.bp.rfx.auction.approval
 * @author kh_lee
 * @Since 2016. 6. 10
 * @변경이력 : [2016. 6. 10] lee 최초작성
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class AuctionProgressApprovalService implements ApprovalStrategy {

	/** The pro status processor. */
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	AuctionSchedulerService auctionSchedulerService;
	
	@Inject
	AuctionService auctionService;
	
	@Inject
	AuctionItemService auctionItemService;
	
	@Inject
	AuctionVendorService auctionVendorService;
	
	@Inject
	private SharedService sharedService;
	
	/**
	 * 승인
	 */
	@Override
	public void doApprove(String approvalType, String appId){
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		
		// 결재 승인 상태변경 처리 - 공고중, 진행품의 결재승인
		rfxStatusService.approveApprovalRfxProg(param);
		// 스케쥴러 등록 - 역경매 공고
		auctionSchedulerService.noticeAuction(param);
	}

	/**
	 * 반려
	 */
	@Override
	public void doReject(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		
		rfxStatusService.rejectApprovalRfxProg(param);
	}

	/**
	 * 상신취소
	 */
	@Override
	public void doCancel(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);

		rfxStatusService.cancelApprovalRfxProg(param);
	}

	/**
	 * 상신
	 */
	@Override
	public void doSubmit(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		
		rfxStatusService.submitApprovalRfxProg(param);
	}

	/**
	 * 저장
	 */
	@Override
	public void doTemporary(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		
		//proStatusProcessor.saveDraftRfx(param);
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
}
