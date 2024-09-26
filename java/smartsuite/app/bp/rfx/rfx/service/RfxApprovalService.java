package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.rfx.rfx.scheduler.RfxSchedulerService;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * RfxApproval 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @see 
 * @FileName RfqApprovalService.java
 * @package smartsuite.app.bp.rfx.rfx
 * @Since 2016. 5. 11
 * @변경이력 : [2016. 5. 11] Yeon-u Kim 최초작성
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class RfxApprovalService implements ApprovalStrategy {

	/** The rfx service. */
	@Inject
	RfxService rfxService;
	
	@Inject
	RfxItemService rfxItemService;
	
	@Inject
	RfxEvalService rfxEvalService;
	
	/** The rfq schduler service. */
	@Inject
	RfxSchedulerService rfxSchedulerService;
	
	/** The pro status processor. */
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	private SharedService sharedService;
	
	@Inject
	private FormatterProvider formatProvider;
	
	/**
	 * 승인.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doApprove(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		
		Map rfxInfo = rfxService.findRfx(param);
		// 결재 승인 상태변경 처리 - 공고중, 진행품의 결재승인
		rfxStatusService.approveApprovalRfxProg(param);
		// 스케쥴러 등록 - RFx 공고
		rfxSchedulerService.noticeRfx(rfxInfo);
        //신규업체 메일 발송로직 추가
        rfxService.inviteVendorForRfx(rfxInfo);
		// 즉시 시작인 경우 바로 시작 로직 수행
		if("Y".equals(rfxInfo.get("immed_st_use_yn"))) {
			HashMap data = Maps.newHashMap();
			data.put("rfx_uuid", rfxInfo.get("rfx_uuid"));
			data.put("rfx_typ_ccd", rfxInfo.get("rfx_typ_ccd"));
			rfxService.startRfx(data);
		}
		//2020/03/12 SHH RFX생성시 현장설명회테이블에 넣어주지 않는다.(RQHD Table안 Column 활용)
		// 현장설명회 직접 생성
		//rfxService.directFieldIntro(param);
	}

	/**
	 * 반려.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doReject(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		
		// 상태 업데이트
		rfxStatusService.rejectApprovalRfxProg(param);
	}

	/**
	 * 결재 취소.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doCancel(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		
		// 상태 업데이트
		rfxStatusService.cancelApprovalRfxProg(param);
	}

	/**
	 * 상신.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doSubmit(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		
		// 상태 업데이트
		rfxStatusService.submitApprovalRfxProg(param);
	}

	/**
	 * 저장.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doTemporary(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		// 상태 업데이트
		//proStatusProcessor.saveDraftRfx(param);
	}
	
	/**
	 * 결재서식 param을 위해 업무단 정보 조회
	 *
	 * @param approvalType, appId
	 */
	@Override
	public Map<String, Object> makeParam(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		Map resultMap = Maps.newHashMap();
		
		param.put("rfx_uuid", appId);
		Map rfxInfo = rfxService.findRfx(param);
		
		if(rfxInfo != null) {
			List rfxItems = rfxItemService.searchRfxItemByRfx(rfxInfo);
			List vendorInfoList = rfxService.findListRfxVdBid(rfxInfo);
			
			//공통코드값 코드명으로 변환
			rfxInfo.put("p2p_purc_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("purc_typ_ccd"), "P045"));			//구매 유형
			rfxInfo.put("rfx_purp_ccd_nm", sharedService.findCodeName(rfxInfo.get("rfx_purp_ccd"), "P049"));	// 구매요청/RFX 목적
			rfxInfo.put("rfx_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("rfx_typ_ccd"), "P033"));			//RFx 유형
			rfxInfo.put("comp_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("comp_typ_ccd"), "P211"));	//경쟁 유형
			rfxInfo.put("item_slctn_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("item_slctn_typ_ccd"), "P002"));		//총액/품목별 선정
			rfxInfo.put("domovrs_div_ccd_nm", sharedService.findCodeName(rfxInfo.get("domovrs_div_ccd"), "C024"));	//내외자구분
			rfxInfo.put("slctn_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("slctn_typ_ccd"), "P007"));	//선정 유형(가격선정방식)
			
			rfxInfo.put("pymtmeth_ccd_nm", sharedService.findCodeName(rfxInfo.get("pymtmeth_ccd"), "P009"));		//대금지급조건
			rfxInfo.put("dlvymeth_ccd_nm", sharedService.findCodeName(rfxInfo.get("dlvymeth_ccd"), "P010"));	//인도조건
			
			//운영조직
			rfxInfo.put("oper_org_cd_nm", sharedService.findOperationOrganizationName((String) rfxInfo.get("oorg_cd"), "PO"));
			
			//Yn값 변환
			rfxInfo.put("coststr_use_yn_str", changeYntoYesOrNo(rfxInfo.get("coststr_use_yn")));
			rfxInfo.put("bfg_yn_str", changeYntoYesOrNo(rfxInfo.get("bfg_yn")));
			rfxInfo.put("presn_yn_str", changeYntoYesOrNo(rfxInfo.get("presn_yn")));
			rfxInfo.put("prtl_bid_perm_yn_str", changeYntoYesOrNo(rfxInfo.get("prtl_bid_perm_yn")));
			
			//rfx시간 설정하는 함수 
			this.setRfxStartCloseDt(rfxInfo);
			
			resultMap.put("rfxInfo", rfxInfo);
			//formatter 적용
			rfxItems = getApprovalFormatDataByItems(rfxItems);
			
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
	private void setRfxStartCloseDt(Map<String, Object> rfxInfo) {
		//rfx시작 년월일, 시, 분 설정
		DecimalFormat df = new DecimalFormat("00");
		if(rfxInfo.get("rfx_st_dttm") != null) {
			Date rfxStartDt = (Date)rfxInfo.get("rfx_st_dttm");

			Calendar cal = Calendar.getInstance(); 
			cal.setTime(rfxStartDt);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
			
			rfxInfo.put("rfx_st_dttm_hour", df.format(hour));
			rfxInfo.put("rfx_st_dttm_min", df.format(min));
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
			
			rfxInfo.put("rfx_clsg_dttm_hour", df.format(closeHour));
			rfxInfo.put("rfx_clsg_dttm_min", df.format(closeMin));
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
			
			rfxInfo.put("open_dttm_hour", df.format(openHour));
			rfxInfo.put("open_dttm_min", df.format(openMin));
			rfxInfo.put("open_dttm_ymd", ft.format(openDt));
		}
	}
	
	private List<Map<String, Object>> getApprovalFormatDataByItems(List<Map<String, Object>> rfxItems) {
		List<Map<String, Object>> lists = Lists.newArrayList();
		lists = rfxItems;
		for(Map<String, Object> item: lists){
			BigDecimal qty = BigDecimal.ZERO;
			BigDecimal price = BigDecimal.ZERO;
			BigDecimal tgtPrice = BigDecimal.ZERO;
			BigDecimal amt = BigDecimal.ZERO;
			if(item.get("rfx_qty") != null){
				qty = (BigDecimal)item.get("rfx_qty");
			}
			if(item.get("rfx_req_uprc") != null){
				price = (BigDecimal)item.get("rfx_req_uprc");
			}
			if(item.get("rfq_amt") != null){
				amt = (BigDecimal)item.get("rfq_amt");
			}
			if(item.get("rfx_trg_uprc") != null){
				tgtPrice = (BigDecimal)item.get("rfx_trg_uprc");
			}
			item.put("rfx_qty", formatProvider.getPrecFormatZero("qty", qty, true));
			item.put("rfx_req_uprc", formatProvider.getPrecFormatZero("price", price, true));
			item.put("rfx_trg_uprc", formatProvider.getPrecFormatZero("price", tgtPrice, true));
			item.put("rfq_amt", formatProvider.getPrecFormatZero("amt", amt, true));
		}
		return lists;
	}
	
}
