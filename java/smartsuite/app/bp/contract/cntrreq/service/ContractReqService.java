package smartsuite.app.bp.contract.cntrreq.service;

import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import smartsuite.app.bp.contract.cntrreq.event.ContractReqEventPublisher;
import smartsuite.app.bp.contract.cntrreq.repository.ContractReqRepository;
import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.bp.contract.data.ContractReq;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.shared.CntrConst;
import smartsuite.data.FloaterStream;


/**
 * 계약 요청 관련 처리하는 서비스 Class입니다.
 *
 * @FileName ContractReqService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class ContractReqService {

	private static final Logger LOG = LoggerFactory.getLogger(ContractReqService.class);

	@Inject
	ContractReqRepository contractReqRepository;
	@Inject
	ContractReqEventPublisher contractReqEventPublisher;
	@Inject
	ContractService contractService;
	@Inject
	SharedService sharedService;
	
	
	/**
	 * 계약 요청
	 * @param reqInfo
	 * @return
	 */
	public ResultMap requestContract(ContractReq contractReq) {
		// TODO 데이터 유효성 검증 로직 추가 개발
		if(Strings.isNullOrEmpty(contractReq.getPreReqTypCcd())) {
			return ResultMap.FAIL();
		}
		
		Map reqInfo = Maps.newHashMap();
		
		String reqRcptId = UUID.randomUUID().toString();
		String reqRcptNo = sharedService.generateDocumentNumber("CTR");
		
		reqInfo.put("cntr_req_rcpt_uuid", reqRcptId);
		reqInfo.put("cntr_req_rcpt_no", reqRcptNo);
		reqInfo.put("cntr_req_rcpt_sts_ccd", CntrConst.REQ_RCPT_STATUS.RECEIPT_WAITING);
		reqInfo.put("cntr_req_uuid", contractReq.getReqId());
		reqInfo.put("cntr_cnd_uuid", contractReq.getCndId());
		reqInfo.put("pre_req_typ_ccd", contractReq.getPreReqTypCcd());
		reqInfo.put("cntr_req_typ_ccd", contractReq.getCntrReqTypCcd());
		reqInfo.put("cntrdoc_typ_ccd", contractReq.getCntrdocTypCcd());
		reqInfo.put("cntr_req_nm", contractReq.getReqNm());
		reqInfo.put("cntr_reqr_id", contractReq.getReqrId());
		reqInfo.put("vd_cd", contractReq.getVdCd());
		reqInfo.put("oorg_cd", contractReq.getOorgCd());
		reqInfo.put("purc_grp_cd", contractReq.getPurcGrpCd());
		reqInfo.put("cntr_req_rsn", contractReq.getReqRsn());

		String cntrId = contractReq.getCntrId();
		if(!Strings.isNullOrEmpty(cntrId)) {
			Map paramMap = Maps.newHashMap();
			paramMap.put("cntr_uuid", cntrId);
			Map cntrInfo = contractService.findContract(paramMap);
			
			reqInfo.put("cntr_uuid", cntrId);
			reqInfo.put("cntr_no", cntrInfo.get("cntr_no"));
			reqInfo.put("cntr_revno", cntrInfo.get("cntr_revno"));
		}
		
		contractReqRepository.insertContractReq(reqInfo);

		return ResultMap.SUCCESS(reqInfo);
	}
	
	/**
	 * 계약 요청 목록 조회
	 * @param param
	 * @return
	 */
	public FloaterStream largeFindListContractReq(Map param) {
		return contractReqRepository.largeFindListContractReq(param);
	}

	/**
	 * 계약 요청 접수
	 * @param param
	 * @return
	 */
	public ResultMap receiveContractReq(Map param) {
		Map reqInfo = contractReqRepository.findContractReq(param);
		if(reqInfo == null) {
			return ResultMap.FAIL();
		}
		
		String cntrReqRcptId = (String) reqInfo.get("cntr_req_rcpt_uuid");

		// 이미 접수 상태인 경우, 상태 업데이트 하지 않음
		if(!CntrConst.REQ_RCPT_STATUS.RECEIPT.equals(reqInfo.get("cntr_req_rcpt_sts_ccd"))) {
			this.updateContractReqSts(cntrReqRcptId, CntrConst.REQ_RCPT_STATUS.RECEIPT, null);
			contractReqEventPublisher.receiptReq(reqInfo); // 업무 후처리
		}
		
		// 자동 생성인 경우, 계약정보 생성
		ResultMap resultMap = ResultMap.SUCCESS();
		boolean autoReceive = (boolean) param.get("autoReceive");
		if(autoReceive) {
			String cntrReqTypCcd = (String) reqInfo.get("cntr_req_typ_ccd");
			if(CntrConst.CNTR_TYPE.NEW.equals(cntrReqTypCcd)) {
				resultMap = contractService.createContractByReq(reqInfo);
				
			} else if(CntrConst.CNTR_TYPE.CHANGE.equals(cntrReqTypCcd)) {
				reqInfo.put("cntr_uuid", null);
				reqInfo.put("cntr_typ_ccd", cntrReqTypCcd);
				resultMap = contractService.createContractByReq(reqInfo);
				
			} else if(CntrConst.CNTR_TYPE.TERMINATION.equals(cntrReqTypCcd)) {
				reqInfo.put("cntr_uuid", null);
				reqInfo.put("cntr_typ_ccd", cntrReqTypCcd);
				reqInfo.put("cntr_trmn_rsn", reqInfo.get("cntr_req_rsn"));
				resultMap = contractService.createContractByReq(reqInfo);
			}
			
			if(resultMap.isSuccess()) {
				this.updateContractReqSts(cntrReqRcptId, CntrConst.REQ_RCPT_STATUS.CONTRACT_PROGRESSING, null);
			}
		}
		
		return resultMap;
	}

	/**
	 * 계약 요청 반려
	 * @param param
	 * @return
	 */
	public ResultMap returnContractReq(Map param) {
		Map reqInfo = contractReqRepository.findContractReq(param);
		if(reqInfo == null) {
			return ResultMap.FAIL();
		}
		
		// 생성된 계약정보가 있는 경우 삭제
		if(param.containsKey("cntr_uuid")) {
			Map cntrInfo = contractService.findContract(param);
			if(cntrInfo != null) {
				contractService.deleteContract(cntrInfo);
			}
		}
		
		// 계약 요청 접수 상태 변경
		String cntrReqRcptId = (String) reqInfo.get("cntr_req_rcpt_uuid");
		String cntrReqRetRsn = (String) param.get("reason");
		this.updateContractReqSts(cntrReqRcptId, CntrConst.REQ_RCPT_STATUS.RETURN, cntrReqRetRsn);
		
		// 업무 후처리
		reqInfo.put("cntr_req_ret_rsn", cntrReqRetRsn);
		contractReqEventPublisher.rejectReq(reqInfo);
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 계약 요청 목록으로 반환 (계약정보 삭제)
	 * @param param
	 * @return
	 */
	public ResultMap returnContract(Map param) {
		Map cntrInfo = contractService.findContract(param);
		if(cntrInfo == null) {
			return ResultMap.FAIL();
		}
		
		// 계약정보 삭제
		contractService.deleteContract(cntrInfo);
		
		// 계약 요청 정보 있는 경우, 계약 요청 접수 상태 변경
		String cntrReqRcptId = (String) cntrInfo.get("cntr_req_rcpt_uuid");
		if(!Strings.isNullOrEmpty(cntrReqRcptId)) {
			this.updateContractReqSts(cntrReqRcptId, CntrConst.REQ_RCPT_STATUS.RECEIPT_WAITING, null);
		}
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 계약 요청 접수 상태 변경
	 * @param cntrReqRcptId
	 * @param cntrReqRcptStsCcd
	 * @param cntrReqRetRsn
	 */
	private void updateContractReqSts(String cntrReqRcptId, String cntrReqRcptStsCcd, String cntrReqRetRsn) {
		Map saveParam = Maps.newHashMap();
		saveParam.put("cntr_req_rcpt_uuid", cntrReqRcptId);
		saveParam.put("cntr_req_rcpt_sts_ccd", cntrReqRcptStsCcd);
		saveParam.put("cntr_req_ret_rsn", cntrReqRetRsn);
		contractReqRepository.updateContractReqSts(saveParam);
	}

	public Map<String, Object> findContractReqWk(Map<String, Object> param) {
		return contractReqRepository.findContractReqWk(param);
	}
}