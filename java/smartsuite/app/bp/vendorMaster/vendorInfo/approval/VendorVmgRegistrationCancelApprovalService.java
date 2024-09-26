package smartsuite.app.bp.vendorMaster.vendorInfo.approval;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.vendorMaster.vendorInfo.service.VendorMasterService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * 협력사관리그룹 등록 취소 요청 결재관련 처리하는 서비스 Class입니다.
 *
 * @author Sykim
 * @FileName VendorVmgRegistrationCancelApprovalService.java
 * @package smartsuite.app.bp.vendorMaster.vendorInfo.approval
 * @Since 2023. 06. 29
 * @변경이력 : [2023. 06. 29] Sykim 최초작성
 */
@Service
@Transactional
@SuppressWarnings("unchecked")
public class VendorVmgRegistrationCancelApprovalService implements ApprovalStrategy {
	
	@Inject
	private VendorMasterService vendorMasterService;

	/**
	 * 승인.
	 *
	 * @param approvalType the approval type
	 * @param appId        the app id
	 */
	@Override
	public void doApprove(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("vd_mst_chg_histrec_uuid", appId);
		param.put("chg_typ_ccd", approvalType);
		// 결재 승인 상태 업데이트 (협력사관리그룹 등록 취소 요청)
		vendorMasterService.updateVendorVmgRegistrationCancelByApprovedApproval(param);
	}
	
	/**
	 * 반려.
	 *
	 * @param approvalType the approval type
	 * @param appId        the app id
	 */
	@Override
	public void doReject(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("vd_mst_chg_histrec_uuid", appId);
		param.put("chg_typ_ccd", approvalType);
		param.put("chg_req_sts_ccd", "RET"); // 반려
		vendorMasterService.updateVendorHistoryByApproval(param);
	}
	
	/**
	 * 결재 취소.
	 *
	 * @param approvalType the approval type
	 * @param appId        the app id
	 */
	@Override
	public void doCancel(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("vd_mst_chg_histrec_uuid", appId);
		param.put("chg_typ_ccd", approvalType);
		param.put("chg_req_sts_ccd", "CRNG"); // 결재 작성중
		vendorMasterService.updateVendorHistoryByApproval(param);
	}
	
	/**
	 * 상신.
	 *
	 * @param approvalType the approval type
	 * @param appId        the app id
	 */
	@Override
	public void doSubmit(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("vd_mst_chg_histrec_uuid", appId);
		param.put("chg_typ_ccd", approvalType);
		param.put("chg_req_sts_ccd", "PRGSG"); // 결재 진행중
		vendorMasterService.updateVendorHistoryByApproval(param);
	}
	
	/**
	 * 임시저장.
	 *
	 * @param approvalType the approval type
	 * @param appId        the app id
	 */
	@Override
	public void doTemporary(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("vd_mst_chg_histrec_uuid", appId);
		param.put("chg_typ_ccd", approvalType);
		param.put("chg_req_sts_ccd", "CRNG"); // 결재 작성중
		vendorMasterService.updateVendorHistoryByApproval(param);
	}

	/**
	 * 결재서식 param을 위해 업무단 정보 조회
	 *
	 * @param approvalType, appId
	 */
	@Override
	public Map<String, Object> makeParam(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("vd_mst_chg_histrec_uuid", appId);
		param.put("chg_typ_ccd", approvalType);
		return vendorMasterService.findVendorHistrecInfoForApprovalForm(param);
	}
}
