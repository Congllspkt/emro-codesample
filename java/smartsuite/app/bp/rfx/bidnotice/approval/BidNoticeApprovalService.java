package smartsuite.app.bp.rfx.bidnotice.approval;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.rfx.bidnotice.service.BidNoticeService;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class BidNoticeApprovalService implements ApprovalStrategy {

	@Inject
	BidNoticeService bidNoticeService;
	
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	private SharedService sharedService;
	
	@Override
	public void doApprove(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_prentc_uuid", appId);
		rfxStatusService.approvalBidNotiProg(param);
		bidNoticeService.regeditBidNoti(param);
	}

	@Override
	public void doReject(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_prentc_uuid", appId);
		rfxStatusService.rejectApprovalBidNoti(param);
	}

	@Override
	public void doCancel(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_prentc_uuid", appId);
		rfxStatusService.cancelApprovalBidNoti(param);
	}

	@Override
	public void doSubmit(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_prentc_uuid", appId);
		rfxStatusService.submitApprovalBidNoti(param);
	}

	@Override
	public void doTemporary(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("rfx_prentc_uuid", appId);
	}

	/**
	 * 결재서식 param을 위해 업무단 정보 조회
	 *
	 * @param approvalType, appId
	 */
	@Override
	public Map makeParam(String approvalType, String appId) {
		Map newParam = Maps.newHashMap();
		Map resultMap = Maps.newHashMap();
		
		newParam.put("rfx_prentc_uuid", appId);
		
		Map bidNotiInfo = bidNoticeService.findBidNoti(newParam);
		
		if(bidNotiInfo != null) {
			bidNotiInfo.forEach((k, v)-> System.out.println("Key : " + k + " Value : " + v));

			List vendorInfoList = bidNoticeService.findListBidNotiVendor(newParam);
			//공통코드값 코드명으로 변환
			bidNotiInfo.put("comp_typ_ccd_nm", sharedService.findCodeName(bidNotiInfo.get("comp_typ_ccd"), "P216"));			//경쟁 유형
			bidNotiInfo.put("rfx_prentc_typ_ccd_nm", sharedService.findCodeName(bidNotiInfo.get("rfx_prentc_typ_ccd"), "P221"));	//RFX 사전공고 유형
			
			//운영조직
			bidNotiInfo.put("oper_org_cd_nm", sharedService.findOperationOrganizationName((String) bidNotiInfo.get("oorg_cd"), "PO"));
			
			//입찰공고시간 설정하는 함수 
			this.setStartCloseDt(bidNotiInfo);
			
			resultMap.put("bidNotiInfo", bidNotiInfo);
			resultMap.put("vendorInfoList", vendorInfoList);
		}
		
		return resultMap;
	}

	/**
	 * 년월일, 시, 분을 설정하기 위한 함수
	 * 
	 */
	private void setStartCloseDt(Map bidNotiInfo) {
		DecimalFormat df = new DecimalFormat("00");
		//rfx시작 년월일, 시, 분 설정
		if(bidNotiInfo.get("rfx_prentc_st_dttm") != null) {
			Date startDt = (Date)bidNotiInfo.get("rfx_prentc_st_dttm");

			Calendar cal = Calendar.getInstance(); 
			cal.setTime(startDt);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
			
			bidNotiInfo.put("rfx_prentc_st_dttm_hour", df.format(hour));
			bidNotiInfo.put("rfx_prentc_st_dttm_min", df.format(min));
			bidNotiInfo.put("rfx_prentc_st_dttm_ymd", ft.format(startDt));
		}
		
		//rfx종료 년월일, 시, 분 설정
		if(bidNotiInfo.get("rfx_prentc_clsg_dttm") != null) {
			Date closeDt = (Date)bidNotiInfo.get("rfx_prentc_clsg_dttm");
			
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(closeDt);
			int closeHour = cal.get(Calendar.HOUR_OF_DAY);
			int closeMin = cal.get(Calendar.MINUTE);
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
			
			bidNotiInfo.put("rfx_prentc_clsg_dttm_hour", df.format(closeHour));
			bidNotiInfo.put("rfx_prentc_clsg_dttm_min", df.format(closeMin));
			bidNotiInfo.put("rfx_prentc_clsg_dttm_ymd", ft.format(closeDt));
		}
	}
}
