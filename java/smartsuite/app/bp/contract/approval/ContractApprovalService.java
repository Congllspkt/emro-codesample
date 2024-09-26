package smartsuite.app.bp.contract.approval;


import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.status.ContractStatusService;

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class ContractApprovalService implements ApprovalStrategy {

	@Inject
	ContractService contractService;
	@Inject
	ContractStatusService contractStatusService;
	
	/**
	 * 승인.
	 *
	 * @param approvalType the approval type
	 * @param : appId the app id
	 */
	@Override
	public void doApprove(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("cntr_uuid", appId);
		Map cntrInfo = contractService.findContract(param); // 계약 정보
		
		String sgnmethCcd = (String) cntrInfo.get("cntr_sgnmeth_ccd");
		
		if(CntrConst.SIGN_METHOD.OFFLINE.equals(sgnmethCcd)) {
			// 오프라인 계약일 경우 계약 완료 처리
			contractService.completeContract(appId);
		} else {
			Map statusParam = Maps.newHashMap();
			statusParam.put("cntr_uuid", appId);
			statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.SIGN_WAITING);
			contractStatusService.watingSign(statusParam);
			contractService.addStatusHistory(appId, CntrConst.CNTR_STATUS.SIGN_WAITING, null);
			
			if(CntrConst.SIGN_METHOD.PKI.equals(sgnmethCcd)) {
				// 공동인증서 서명일 경우 계약서 발송
				contractService.sendContract(cntrInfo);
			}
		}
	}

	/**
	 * 반려.
	 *
	 * @param approvalType the approval type
	 * @param : appId the app id
	 */
	@Override
	public void doReject(String approvalType, String appId) {
		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", appId);
		statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.CREATION);
		contractStatusService.creating(statusParam);
	}

	/**
	 * 결재 취소.
	 *
	 * @param approvalType the approval type
	 * @param : appId the app id
	 */
	@Override
	public void doCancel(String approvalType, String appId) {
		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", appId);
		statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.CREATION);
		contractStatusService.creating(statusParam);
	}

	/**
	 * 상신.
	 *
	 * @param approvalType the approval type
	 * @param : appId the app id
	 */
	@Override
	public void doSubmit(String approvalType, String appId) {
		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", appId);
		statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.APPROVAL);
		contractStatusService.progressingApproval(statusParam);
		contractService.addStatusHistory(appId, CntrConst.CNTR_STATUS.APPROVAL, null);
	}

	/**
	 * 임시저장.
	 *
	 * @param approvalType the approval type
	 * @param : appId the app id
	 */
	@Override
	public void doTemporary(String approvalType, String appId) {
		
	}
	
	/**
	 * 결재서식 param을 위해 업무단 정보 조회
	 *
	 * @param approvalType, appId
	 */
	@Override
	public Map<String, Object> makeParam(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("cntr_uuid", appId);
		
		Map cntrInfo = contractService.findContract(param); // 계약 정보
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("cntrInfo", cntrInfo);
		
		return resultMap;
	}
}
