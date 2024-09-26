package smartsuite.app.bp.rfx.nego.approval;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.rfx.rfx.service.RfxResultApprovalService;
import smartsuite.app.common.status.service.RfxStatusService;

/**
 * 협상 낙찰자선정시
 * @author user
 *
 */
@Service
@Transactional
@SuppressWarnings("unchecked")
public class NegoApprovalService extends RfxResultApprovalService{
	
	@Inject
	RfxStatusService rfxStatusService;

	/**
	 * 결재 반려
	 */
	@Override
	public void doReject(String approvalType, String appId) {
		super.doReject(approvalType, appId);
		// 반려
		// 협상정보가 있을경우  협상진행중으로 변경
		rfxStatusService.updateRfxProgStsByNegoSts(appId);
	}

	/**
	 * 결재 취소
	 */
	@Override
	public void doCancel(String approvalType, String appId) {
		super.doCancel(approvalType, appId);
		// 취소
		// 협상정보가 있을경우 협상진행중으로 변경
		rfxStatusService.updateRfxProgStsByNegoSts(appId);
	}
	
}
