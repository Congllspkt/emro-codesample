package smartsuite.app.bp.pro.upcr.approval;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.bp.pro.upcr.service.UpcrItemService;
import smartsuite.app.bp.pro.upcr.service.UpcrService;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.UpcrStatusService;

import javax.inject.Inject;
import java.util.Map;

/**
 * Upcr 관련 처리하는 서비스 Class입니다.
 *
 * @see
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class UpcrApprovalService implements ApprovalStrategy {

	@Inject
	UpcrStatusService upcrStatusProcessor;

	@Inject
	UpcrService upcrService;
	
	@Inject
	UpcrItemService upcrItemService;
	
	@Inject
	SharedService sharedService;

	/**
	 * 승인.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doApprove(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("upcr_uuid", appId);
		param.put("upcr_chg_yn", ProConst.PR_CHG_YN_N);
		upcrService.requestRfx(param);
		upcrStatusProcessor.approveApprovalUpcr(param);
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
		param.put("upcr_uuid", appId);
		param.put("upcr_chg_yn", ProConst.PR_CHG_YN_N);
		upcrStatusProcessor.rejectApprovalUpcr(param);
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
		param.put("upcr_uuid", appId);
		param.put("upcr_chg_yn", ProConst.PR_CHG_YN_N);
		upcrStatusProcessor.cancelApprovalUpcr(param);
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
		param.put("upcr_uuid", appId);
		param.put("upcr_chg_yn", ProConst.PR_CHG_YN_N);
		upcrStatusProcessor.submitApprovalUpcr(param);
	}

	/**
	 * 임시저장.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doTemporary(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("upcr_uuid", appId);
		/*param.put("upcr_sts_ccd", "T");
		// 접수대기
		param.put("upcr_sts_ccd", "PR_CRNG");*/
		//upcroStatusProcessor.saveDraftPr(param);
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
		
		newParam.put("upcr_uuid", appId);
		
		Map upcrData =  upcrService.findUpcr(newParam);
		if(upcrData != null) {
			// 구매 유형 공통코드(P045), 계약형태(P049), 구매운영조직값 변환
			String purcTypNm = sharedService.findCodeName(upcrData.get("purc_typ_ccd"), "P045");				//구매 유형 공통코드
			String upcrPurpNm = sharedService.findCodeName(upcrData.get("upcr_purp_ccd"), "P049");		//계약형태
			
			String operOrgCdNm = sharedService.findOperationOrganizationName((String) upcrData.get("oorg_cd"), "PO");		//구매운영조직
			
			upcrData.put("purc_typ_nm", purcTypNm);
			upcrData.put("upcr_purp_nm", upcrPurpNm);
			upcrData.put("oorg_cd_nm", operOrgCdNm);
			
			resultMap.put("upcrInfo", upcrData);
			resultMap.put("upcrItems", upcrItemService.findListUpcrItem(upcrData));
		}
		return resultMap;
	}
	
}
