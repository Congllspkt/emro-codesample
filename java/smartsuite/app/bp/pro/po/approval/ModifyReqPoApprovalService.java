package smartsuite.app.bp.pro.po.approval;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.common.status.service.ProStatusService;

/**
 * PO변경요청 결재관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @see
 * @FileName ModifyReqPoApprovalService.java
 * @package smartsuite.app.bp.pro.po
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 */
@Service
@Transactional
public class ModifyReqPoApprovalService implements ApprovalStrategy {

	@Inject
	private ProStatusService proStatusProcessor;

	/**
	 * 승인.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doApprove(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("po_uuid", appId);
		proStatusProcessor.approveApprovalPoChangeRequest(param);
	}

	/**
	 * 반려.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doReject(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("po_uuid", appId);
		proStatusProcessor.rejectApprovalPoChangeRequest(param);
	}

	/**
	 * 결재 취소.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doCancel(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("po_uuid", appId);
		proStatusProcessor.cancelApprovalPoChangeRequest(param);
	}

	/**
	 * 상신.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doSubmit(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("po_uuid", appId);
		proStatusProcessor.submitApprovalPoChangeRequest(param);
	}

	/**
	 * 임시저장.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doTemporary(String approvalType, String appId) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("po_uuid", appId);
	}

	@Override
	public Map<String, Object> makeParam(String approvalType, String appId) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
