package smartsuite.app.sp.pro.pp.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import smartsuite.app.bp.pro.po.service.PoPaymentExpectationService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.app.sp.pro.asn.service.SpAsnItemService;
import smartsuite.app.sp.pro.asn.service.SpAsnService;
import smartsuite.app.sp.pro.po.service.SpPoItemService;
import smartsuite.app.sp.pro.po.service.SpPoPaymentExpectationService;
import smartsuite.app.sp.pro.po.service.SpPoService;
import smartsuite.app.sp.pro.pp.repository.SpProgressPaymentRepository;
import smartsuite.app.sp.pro.pp.validator.SpProgressPaymentValidator;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

/**
 * SpProgressPayment 관련 처리하는 서비스 Class입니다.
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SpProgressPaymentService {

	/** The shared service. */
	@Inject
	private SharedService sharedService;
	
	/** The pro status processor. */
	@Inject
	private ProStatusService proStatusService;
	
	/** The sp po service. */
	@Inject
	private SpAsnService spAsnService;
	
	@Inject
	SpProgressPaymentValidator spProgressPaymentValidator;

	@Inject
	SpProgressPaymentRepository spProgressPaymentRepository;
	
	@Inject
	SpPoService spPoService;
	
	@Inject
	SpPoItemService spPoItemService;
	
	@Inject
	SpAsnItemService spAsnItemService;
	
	@Inject
	SpPoPaymentExpectationService spPoPaymentExpectationService;
	

	/**
	 * 기성요청 가능여부 체크
	 *
	 * @param param the param
	 * @return the map
	 * @Method Name : validateProgressPaymentRequestStatus
	 */
	public ResultMap validateProgressPaymentRequestStatus(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		String poUuId = param.get("po_uuid") == null ? null : param.get("po_uuid").toString();
		String asnUuId = param.get("asn_uuid") == null ? null : param.get("asn_uuid").toString();

		if(Strings.isNullOrEmpty(asnUuId)) {
			// 신규 생성 시
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		} else {
			Map<String, Object> checkResult = spAsnService.getProgressPaymentRequestStatus(param);
			resultMap = spProgressPaymentValidator.compareProgressPaymentRequestStatus(param, checkResult);
		}

		if (ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus()) && !Strings.isNullOrEmpty(poUuId)) {
			Map<String, Object> invalidResult = spPoService.validateProgressPaymentRequestStatus(param);
			resultMap = spProgressPaymentValidator.checkPoStatus(param, invalidResult);
		}

		return resultMap;
	}

	/**
	 * 발주기준 기성현황 목록 조회한다.
	 *
	 * @author : JuEung Kim
	 * @param param the param
	 * @return the list< map< string, object>>
	 * @Date : 2016. 6. 7
	 * @Method Name : findListPamentBill
	 */
	public FloaterStream searchPoForProgressPayment(Map<String, Object> param) {
		// 대용량 처리
		return spPoService.searchPoForProgressPayment(param);
	}
	
	/**
	 * 기성현황 목록을 조회한다.
	 *
	 * @author : JuEung Kim
	 * @param param the param
	 * @return the list< map< string, object>>
	 * @Date : 2016. 6. 7
	 * @Method Name : findListProgressPaymentAr
	 */
	public FloaterStream searchProgressPaymentRequest(Map<String, Object> param) {
		return spProgressPaymentRepository.searchProgressPaymentRequest(param);
	}
	
	/**
	 * 기성요청을 위한 발주정보를 조회한다.
	 */
	public Map<String, Object> initProgressPaymentRequestInfoByPoUuid(Map<String, Object> param) {
		Map<String, Object> asnHdInfo = spPoService.findInitProgressPaymentRequestByPoUuid(param);
		asnHdInfo.put("asn_sts_ccd", "CRNG");
		asnHdInfo.put("asn_typ_ccd", "CONSTSVC");

		List<Map<String, Object>> asnItems = spPoItemService.searchInitProgressPaymentRequestItemByPoUuid(param);
		int maxNo = 10;
		for(Map<String, Object> asnItem : asnItems) {
			asnItem.put("asn_lno", maxNo);
			maxNo += 10;
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("asnHdInfo", asnHdInfo);
		resultMap.put("asnItems", asnItems);
		resultMap.put("paymentPlans", spPoPaymentExpectationService.findListPaymentPlanByPoId(asnHdInfo));
		
		return resultMap;
	}
	
	/**
	 * 기성요청 정보를 조회한다.
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findProgressPaymentRequestInfo(Map<String, Object> param) {
		Map result = Maps.newHashMap();
		Map asnData = spAsnService.findProgressPaymentRequest(param);
		result.put("asnHdInfo", asnData);
		result.put("asnDtList", spAsnItemService.searchProgressPaymentRequestItem(param));
		result.put("paymentPlans", spPoPaymentExpectationService.findListPaymentPlanByPoId(asnData));
		return result;
	}

	/**
	 * 기성요청 아이디로 선급금여부를 조회한다.
	 *
	 * @param param
	 * @return
	 */
	public String findAdvancePaymentYnByAsnUuid(Map<String, Object> param) {
		return spAsnService.findAdvancePaymentYnByAsnUuid((String) param.get("task_uuid"));
	}

	/**
	 * 기성요청 임시저장을 한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 7
	 * @Method Name : saveSpArDraft
	 */
	public ResultMap saveProgressPaymentDraft(Map<String, Object> param) {
		ResultMap resultMap = this.saveAsn(param);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			// 진행상태 update
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("asn_uuid", resultMap.getResultData().get("asnUuid"));
			
			// 기성요청 임시저장 (협력사)
			proStatusService.saveDraftServiceAsn(keyParam);
		}
		return resultMap;
	}
	
	/**
	 * 기성요청 제출
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 9
	 * @Method Name : submitProgressPayment
	 */
	public ResultMap submitProgressPayment(Map<String, Object> param) {
		ResultMap resultMap = this.saveAsn(param);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			// 진행상태 update
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("asn_uuid", resultMap.getResultData().get(("asnUuid")));
			
			// 기성요청 (협력사)
			proStatusService.requestServiceAsn(keyParam);
			
		}
		return resultMap;
	}
	
	/**
	 * 기성요청 삭제
	 *
	 * @param param
	 * @return
	 */
	public ResultMap deleteProgressPaymentRequest(Map<String, Object> param) {
		Map<String, Object> deleteParam = Maps.newHashMap();
		deleteParam.put("asn_uuid"      , param.get("asn_uuid"));
		deleteParam.put("asn_sts_ccd", param.get("asn_sts_ccd"));
		deleteParam.put("apymt_yn" , param.get("apymt_yn"));
		deleteParam.put("po_uuid", param.get("po_uuid"));

		ResultMap resultMap = this.validateProgressPaymentRequestStatus(deleteParam);
		if(!resultMap.isSuccess()) {
			return resultMap;
		}
		this.deleteProgressPaymentRequestProcess(deleteParam);
		return resultMap;
	}
	
	protected void deleteProgressPaymentRequestProcess(Map<String, Object> deleteParam) {
		spAsnService.updateDeleteAsnItemByAsn(deleteParam);
		spAsnService.updateDeleteAsn(deleteParam);
		proStatusService.deleteServiceAsn(deleteParam);
	}
	
	/**
	 * 검수요청헤더를 저장한다.
	 *
	 * @param param the param
	 * @return the string
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 7
	 * @Method Name : saveAsn
	 */
	public ResultMap saveAsn(Map<String, Object> param){
		// 헤더 data
		Map<String, Object> asnHdInfo = (Map<String, Object>)param.get("asnHdInfo");


		Map<String, Object> checkResult = spAsnService.getProgressPaymentRequestStatus(asnHdInfo);
		// validate
		ResultMap resultMap = this.validateProgressPaymentRequestStatus(asnHdInfo);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			// 세션정보
			Map<String, Object> userInfo = Auth.getCurrentUserInfo();
			String vdCd = (String)userInfo.get("vd_cd");
			
			String asnUuid      = (String)asnHdInfo.get("asn_uuid"); 			// 아이디
			String asnNo      = (String)asnHdInfo.get("asn_no"); 			// 번호
			Object asnRevno     = asnHdInfo.get("asn_ordn"); 					// 차수
			String oorgCd = (String)asnHdInfo.get("oorg_cd"); // 운영조직코드
			
			// 헤더 INSERT or UPDATE
			if(Strings.isNullOrEmpty(asnUuid)){	// 신규
				asnUuid = UUID.randomUUID().toString();
				asnHdInfo.put("asn_uuid", asnUuid);
				
				asnNo = sharedService.generateDocumentNumber("AR");
				asnRevno = spAsnService.getNewAsnRev(asnHdInfo);
				asnHdInfo.put("asn_no" , asnNo);   // 검수요청번호
				asnHdInfo.put("asn_ordn", asnRevno); // 차수
				asnHdInfo.put("vd_cd", vdCd); // 협력사 코드
				
				spAsnService.insertAsnData(asnHdInfo);
			} else {
				spAsnService.updateAsnData(asnHdInfo);
			}

			// 디테일 data
			List<Map<String, Object>> insertItems = (List<Map<String, Object>>)param.get("insertItems");
			List<Map<String, Object>> updateItems = (List<Map<String, Object>>)param.get("updateItems");
			
			// 디테일 INSERT
			if(insertItems != null && !insertItems.isEmpty()) {
				for (Map<String, Object> row : insertItems) {
					row.put("asn_item_uuid" , UUID.randomUUID().toString()); 	// 납품예정 품목 아이디
					row.put("asn_uuid"      , asnUuid);
					row.put("asn_no"      , asnNo);							// 납품예정 번호
					row.put("asn_ordn"     , asnRevno);							// 납품예정 회차
					row.put("oorg_cd", oorgCd);
					row.put("vd_cd"      , vdCd);							// 협력사 코드
					spAsnService.insertAsnItem(row);
				}
			}
			
			// 디테일 UPDATE
			if(updateItems != null && !updateItems.isEmpty()) {
				for (Map<String, Object> row : updateItems) {
					spAsnService.updateAsnItem(row);
				}
			}
			//update payment type common code
			spAsnService.updateAsnPaymentTypeCommonCode(asnHdInfo, spAsnService.getAsnPaymentTypeCommonCode(asnHdInfo));
			Map<String, Object> resultData = Maps.newHashMap();
			resultData.put("asnUuid", asnUuid);
			resultMap.setResultData(resultData);
		}
		return resultMap;
	}
	
}
