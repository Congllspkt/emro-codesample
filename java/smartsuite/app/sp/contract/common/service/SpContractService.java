package smartsuite.app.sp.contract.common.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import smartsuite.app.bp.contract.common.service.ContractNxtPrcsService;
import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.status.ContractStatusService;
import smartsuite.app.sp.contract.common.event.SpContractEventPublisher;
import smartsuite.app.sp.contract.common.repository.SpContractRepository;
import smartsuite.data.FloaterStream;
import smartsuite.security.account.service.AccountService;


/**
 * 계약 관련 처리하는 서비스 Class입니다.
 *
 * @FileName SpContractService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class SpContractService {

	private static final Logger LOG = LoggerFactory.getLogger(SpContractService.class);

	@Inject
	SpContractRepository spContractRepository;
	@Inject
	SpContractEventPublisher spContractEventPublisher;
	@Inject
	ContractNxtPrcsService contractNxtPrcsService;
	@Inject
	ContractStatusService contractStatusService;
	@Inject
	SharedService sharedService;
	@Inject
	AccountService accountService;
	
	/**
	 * 계약 목록 조회
	 * @param param
	 * @return
	 */
	public FloaterStream largeFindListSpContract(Map param) {
		return spContractRepository.largeFindListSpContract(param);
	}

	/**
	 * 계약 정보 조회
	 * @param param
	 * @return
	 */
	public ResultMap findContractDetail(Map param) {
		Map resultData = Maps.newHashMap();
		Map cntrInfo = spContractRepository.findContract(param);
		Map cntrDoc = spContractEventPublisher.findEcontract(cntrInfo);
		
		if(cntrDoc == null || cntrDoc.isEmpty()) {
			resultData = Maps.newHashMap(cntrInfo);
		} else {
			resultData = Maps.newHashMap(cntrDoc);
			resultData.put("cntr_req_uuid", cntrInfo.get("cntr_req_uuid"));
			resultData.put("cntrr_eml", cntrInfo.get("cntrr_eml"));
			resultData.put("cntrr_mob", cntrInfo.get("cntrr_mob"));
		}
		
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * 계약 정보 조회
	 * @param param
	 * @return
	 */
	public Map findContract(Map param) {
		return spContractRepository.findContract(param);
	}

	/**
	 * 계약서 최초 조회시 계약서 확인 상태로 변경
	 * @param param
	 */
	public void confirmContractDocument(Map param) {
		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", param.get("cntr_uuid"));
		statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.VENDOR_CONFIRM);
		contractStatusService.confirmVendorReceipt(statusParam);
	}

	/**
	 * 부속서류 저장
	 * @param param
	 * @return
	 */
	public ResultMap saveAppendix(Map param) {
		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", param.get("cntr_uuid"));
		statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.APPENDIX_VD_SAVE);
		contractStatusService.requestReviewAppxToBuyer(statusParam);

		return ResultMap.SUCCESS();
	}

	/**
	 * 계약서 반려
	 *
	 * @param : the param
	 * @return the map
	 * @Method Name : rejectContract
	 */
	public ResultMap rejectContract(Map param) {
		String cntrId = (String) param.get("cntr_uuid");
		String reason = (String) param.get("cntr_ret_rsn");

		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", cntrId);
		statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.VENDOR_REJECT);
		statusParam.put("reason", reason);
		contractStatusService.returnContractToBuyer(statusParam);

		this.addStatusHistory(cntrId, CntrConst.CNTR_STATUS.VENDOR_REJECT, reason);

		// 전자계약 반려 처리
		spContractEventPublisher.rejectEcontract(param);
		
		// 결재 연결 제거
		spContractRepository.updateApprovalUseYn(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 계약 이력 추가
	 * @param histInfo
	 * @return
	 */
	public void insertHistory(Map histInfo) {
		histInfo.put("cntr_prgs_histrec_uuid", UUID.randomUUID().toString());

		Date today = new Date();
		long curTimeInMs = today.getTime();
		Date afterAddingMins = new Date(curTimeInMs + 1000);
		histInfo.put("histRegDate", afterAddingMins);

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String ip = accountService.getClientIpAddress(request);
		String browser = sharedService.getClientBrowser(request);
		histInfo.put("usr_ip", ip);
		histInfo.put("usr_webbr_kind", browser);

		spContractRepository.insertCntrHistory(histInfo);
	}

	/**
	 * 계약 상태 이력 추가
	 * @param cntrId
	 * @param cntrStsCcd
	 * @param reason
	 */
	public void addStatusHistory(String cntrId, String cntrStsCcd, String reason) {
		Map saveParam = Maps.newHashMap();
		saveParam.put("cntr_uuid", cntrId);
		saveParam.put("cntr_sts_ccd", cntrStsCcd);
		saveParam.put("cntr_ret_rsn", reason);
		this.insertHistory(saveParam);
	}
	
	/**
	 * 계약 완료
	 * @param cntrId
	 */
	public void completeContract(String cntrId) {
		// 상태 업데이트
		Map statusMap = Maps.newHashMap();
		statusMap.put("cntr_uuid", cntrId);
		statusMap.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.COMPLETE);
		contractStatusService.completeContract(statusMap);
		// 이력 추가
		this.addStatusHistory(cntrId, CntrConst.CNTR_STATUS.COMPLETE, null);
		// 후속 프로세스
		contractNxtPrcsService.completeContract(cntrId);
	}

	/**
	 * 계약진행상황조회
	 *
	 * @param : Map
	 * @return : List
	 */
	public List findListCntrHistory(Map param) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> historyList = spContractRepository.findListCntrHistory(param);

		if(historyList != null) {
			Map<String, Object> cpRow = Maps.newHashMap();
			Map<String, Object> dtRow = Maps.newHashMap();
	
			//row 정렬 : CP(계약완료) -> DT(계약해지)
			for(Map<String, Object> rowMap : historyList) {
				String progSts = (rowMap.get("cntr_sts_ccd") == null) ? null : rowMap.get("cntr_sts_ccd").toString();
	
				if(Strings.isNullOrEmpty(progSts)) {
					continue;
				}
				if(CntrConst.CNTR_STATUS.COMPLETE.equals(progSts)) {
					cpRow = rowMap;
				} else if(CntrConst.CNTR_STATUS.TERMINATION.equals(progSts)) {
					dtRow = rowMap;
				} else {
					resultList.add(rowMap);
				}
			}
	
			if(!cpRow.isEmpty()) {resultList.add(cpRow);}
			if(!dtRow.isEmpty()) {resultList.add(dtRow);}
		}
		
		return resultList;
	}

	/**
	 * Docusign Envelope 조회
	 * @param param
	 * @return
	 */
	public ResultMap findDocusignEnvelope(Map param) {
		return spContractEventPublisher.findDocusignEnvelope(param);
	}

	/**
	 * AdobeSign 협력사 서명 url 조회
	 * @param param
	 * @return
	 */
	public ResultMap getSpAdobeSignUrlInfo(Map param) {
		return spContractEventPublisher.getSpAdobeSignUrlInfo(param);
	}

	/**
	 * AdobeSign 진행상태 체크
	 * @param param
	 * @return
	 */
	public ResultMap checkAdobeSignStatus(Map param) {
		return spContractEventPublisher.checkAdobeSignStatus(param);
	}
}