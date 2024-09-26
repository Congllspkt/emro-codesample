package smartsuite.app.shared.status;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.contract.cntrreq.repository.ContractReqRepository;
import smartsuite.app.bp.contract.common.repository.ContractRepository;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.status.event.ContractStatusEventPublisher;

import javax.inject.Inject;
import java.util.Map;

/**
 * Contract 상태값 변경 관련 처리하는 서비스 Class입니다.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class ContractStatusService {

	@Inject
	ContractRepository contractRepository;
	@Inject
	ContractReqRepository contractReqRepository;
	@Inject
	ContractStatusEventPublisher contractStatusEventPublisher;

	/**
	 * Contract 저장한다. 작성중 상태로 변경 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void creating(Map param){
		contractRepository.updateContractStatus(param);
	}

	/**
	 * Contract 발신한다. 발신 상태로 변경 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void send(Map param){
		contractRepository.updateContractStatus(param);
		contractStatusEventPublisher.send(param);
	}

	/**
	 * Contract 삭제한다. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void deleteContract(Map param){
		contractRepository.deleteContract(param);
	}

	/**
	 * 계약서 삭제
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void deleteContractDocument(Map param) {
		param.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.CREATION);
		contractRepository.updateContractStatus(param);
	}

	/**
	 * 부속서류 요청. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void requestAppxToVendor(Map param){
		contractRepository.updateContractStatus(param);
		contractStatusEventPublisher.requestAppxToVendor(param);
	}

	/**
	 * 부속서류 검토 진행중. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void requestReviewAppxToBuyer(Map param){
		contractRepository.updateContractStatus(param);
		contractStatusEventPublisher.requestReviewAppxToBuyer(param);
	}

	/**
	 * 부속서류 반려. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void returnAppxToVendor(Map param){
		contractRepository.updateContractStatus(param);
		contractStatusEventPublisher.returnAppxToVendor(param);
	}

	/**
	 * 결재 진행중. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void progressingApproval(Map param){
		contractRepository.updateContractStatus(param);
	}

	/**
	 * 서명대기. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void watingSign(Map param){
		contractRepository.updateContractStatus(param);
	}

	/**
	 * 협력사 수신 확인 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void confirmVendorReceipt(Map param){
		contractRepository.updateContractStatus(param);
	}

	/**
	 * 다자간 서명 진행중 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void progressingMultilateralSign(Map param){
		contractRepository.updateContractStatus(param);
	}

	/**
	 * 협력사 서명 완료 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void completeAllVendorSign(Map param){
		contractRepository.updateContractStatus(param);
	}

	public void completeVendorSign(Map param){
		String cntrStsCcd = (String)param.get("cntr_sts_ccd");
		if(CntrConst.CNTR_STATUS.VENDOR_SIGN_COMPLETE.equals(cntrStsCcd)) {
			this.completeAllVendorSign(param);
		}else if(CntrConst.CNTR_STATUS.MULTILATERAL_SIGN.equals(cntrStsCcd)) {
			this.progressingMultilateralSign(param);
		}
	}

	/**
	 * 협력사 반려 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void returnContractToBuyer(Map param){
		contractRepository.updateContractStatus(param);
	}

	/**
	 * 구매사 반려 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void returnContractToVendor(Map param){
		contractRepository.updateContractStatus(param);
	}

	/**
	 * 계약 완료 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void completeContract(Map param){
		contractRepository.updateContractStatus(param);
		contractStatusEventPublisher.completeContract(param);
	}

	/**
	 * 계약 해지 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void terminationContract(Map param){
		contractRepository.terminateContract(param);

		String cntrReqRcptId = (String) param.get("cntr_req_rcpt_uuid");
		if(!Strings.isNullOrEmpty(cntrReqRcptId)) {
			Map statusParam = Maps.newHashMap();
			statusParam.put("cntr_req_rcpt_uuid", cntrReqRcptId);
			statusParam.put("cntr_req_rcpt_sts_ccd", CntrConst.REQ_RCPT_STATUS.CONTRACT_PROGRESSING);
			contractReqRepository.updateContractReqSts(statusParam);
		}
	}

	/**
	 * 계약서 회수 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public void withdrawalContract(Map param) {
		param.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.CREATION);
		contractRepository.updateContractStatus(param);
	}

}
