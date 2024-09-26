package smartsuite.app.bp.pro.pp.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.asn.service.AsnItemService;
import smartsuite.app.bp.pro.asn.service.AsnService;
import smartsuite.app.bp.pro.gr.service.GrEvalService;
import smartsuite.app.bp.pro.gr.service.GrService;
import smartsuite.app.bp.pro.po.service.PoPaymentExpectationService;
import smartsuite.app.bp.pro.pp.repository.ProgressPaymentRepository;
import smartsuite.app.bp.pro.pp.validator.ProgressPaymentValidator;
import smartsuite.app.bp.pro.po.service.PoItemService;
import smartsuite.app.bp.pro.po.service.PoService;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 납품예정 관련 처리하는 서비스 Class입니다.
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class ProgressPaymentService {
	
	/**
	 * The gr service.
	 */
	@Inject
	private GrService grService;
	
	@Inject
	private PoService poService;
	
	@Inject
	private ProgressPaymentValidator progressPaymentValidator;
	
	/**
	 * The pro status processor.
	 */
	@Inject
	private ProStatusService proStatusService;
	
	@Inject
	private ProgressPaymentRepository progressPaymentRepository;
	
	@Inject
	private ProgressPaymentItemService progressPaymentItemService;
	
	@Inject
	private PoItemService poItemService;
	
	@Inject
	private GrEvalService grEvalService;
	
	@Inject
	private AsnItemService asnItemService;
	
	@Inject
	private AsnService asnService;
	
	@Inject
	private PoPaymentExpectationService poPaymentExpectationService;
	
	/**
	 * 기성 반려사유를 수정한다.
	 *
	 * @param param the param
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : updateAsnRejectOpn
	 */
	public void updateAsnRejectOpn(Map<String, Object> param) {
		asnService.updateAsnRejectOpn(param);
	}
	
	/**
	 * 기성요청대상 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list payment bill
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findListProgressPayment
	 */
	public FloaterStream searchProgressPaymentRequestTarget(Map<String, Object> param) {
		// 대용량 처리
		return poService.searchProgressPaymentRequestTarget(param);
	}
	
	/**
	 * PO별 기성요청목록 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list ar
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : searchProgressPaymentRequestByPoNo
	 */
	public FloaterStream searchProgressPaymentRequestByPoNo(Map<String, Object> param) {
		// 대용량 처리
		return progressPaymentRepository.searchProgressPaymentRequestByPoNo(param);
	}
	
	/**
	 * 기성요청/기성등록 아이디로 선급금여부를 조회한다.
	 *
	 * @param param
	 * @return
	 */
	public String findAdvancePaymentYnByAppId(Map<String, Object> param) {
		String prcsCd = param.get("prcs_cd").toString();
		if("ASN".equals(prcsCd)) {
			return asnService.findAdvancePaymentYnByAsnUuid((String) param.get("task_uuid"));
		} else if("GR".equals(prcsCd)) {
			return progressPaymentRepository.findAdvancePaymentYnByGrId((String) param.get("task_uuid"));
		}
		return null;
	}
	
	/**
	 * 기성요청 별 품목 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list ar item
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findListAsnItemByAsnId
	 */
	public List<Map<String, Object>> searchProgressPaymentRequestItemByAsnUuid(Map<String, Object> param) {
		return asnItemService.searchProgressPaymentRequestItemByAsnUuid(param);
	}
	
	/**
	 * 기성승인 별 품목 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list gr item
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findListGrItemByGrId
	 */
	public List<Map<String, Object>> searchProgressPaymentItemByGrUuid(Map<String, Object> param) {
		return progressPaymentItemService.searchProgressPaymentItemByGrUuid(param);
	}
	
	/**
	 * 기성요청 상세정보를 조회한다.
	 *
	 * @param param the param
	 * @return map
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findAsnProgressPayment
	 */
	public Map<String, Object> findProgressPaymentRequest(Map<String, Object> param) {
		Map<String, Object> arData = asnService.findProgressPaymentRequest(param);
		arData.put("items", this.searchProgressPaymentRequestItemByAsnUuid(param));
		arData.put("paymentPlans", poPaymentExpectationService.findListPaymentExpectationByPoId(arData));
		return arData;
	}
	
	/**
	 * 기성승인 상세정보를 조회한다.
	 *
	 * @param param the param
	 * @return map
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findGr
	 */
	public Map<String, Object> findProgressPayment(Map<String, Object> param) {
		Map<String, Object> grData = progressPaymentRepository.findProgressPayment(param);
		grData.put("items", this.searchProgressPaymentItemByGrUuid(param));
		grData.put("paymentPlans", poPaymentExpectationService.findListPaymentExpectationByPoNoandPoRevno(grData));
		return grData;
	}
	
	/**
	 * 선급금 승인 기본정보 조회
	 */
	public Map<String, Object> findAdvancePaymentGeneralInfomationByAsnUuid(Map<String, Object> param) {
		Map<String, Object> grData = asnService.findProgressPaymentByAsnUuId(param);
		Map<String, Object> poEvalSetData = asnService.findPoEvalSetInfo(param);
		if(poEvalSetData != null) {
			grData.put("ge_subj_yn", "Y");
			grData.put("po_ge_uuid", poEvalSetData.get("po_ge_uuid"));
		} else {
			grData.put("ge_subj_yn", "N");
		}
		
		grData.put("gr_typ_ccd", "CONSTSVC");
		grData.put("gr_sts_ccd", "CRNG");
		grData.put("gr_pic_id", Auth.getCurrentUserName());
		grData.put("apymt_yn", "N");
		
		List<Map<String, Object>> grItems = asnItemService.searchAdvancePaymentRequestItem(param);
		int maxNo = 10;
		boolean hasNoCdItem = false;
		for(Map<String, Object> grItem : grItems) {
			grItem.put("gr_lno", maxNo);
			maxNo += 10;
			if("CDLS".equals(grItem.get("item_cd_crn_typ_ccd"))) {
				hasNoCdItem = true;
			}
		}
		grData.put("has_no_cd_item", hasNoCdItem ? "Y" : "N");
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("grData", grData);
		resultMap.put("grItems", grItems);
		resultMap.put("paymentPlans", poPaymentExpectationService.findListPaymentExpectationByPoId(grData));
		return resultMap;
	}
	
	/**
	 * 기성승인 임시저장 한다.
	 *
	 * @param param {"grData", "insertItems", "updateItems"}
	 * @return grUuid
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftGr
	 */
	public ResultMap saveDraftProgressPayment(Map<String, Object> param) {
		Map<String, Object> grData = (Map<String, Object>) param.get("grData");
		
		// validate
		ResultMap resultMap = progressPaymentValidator.validate(grData);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			String grUuid = grService.saveGr(param); // 데이터 저장
			
			this.deleteGrEvalByGr(grData);
			this.copyGeEvalSetByPoEvalProcess(grData, grUuid);
			this.updatePaymentTypeCommonCode(grData, grUuid);
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("gr_uuid", grUuid);
			proStatusService.saveDraftServiceGr(keyParam); // 진행상태 수정
			Map<String, Object> returnMap = Maps.newHashMap();
			returnMap.put("grUuid", grUuid);
			resultMap.setResultData(returnMap);
		}
		return resultMap;
	}
	
	public ResultMap submitGrEvalByGr(Map param) {
		Map<String, Object> grData = (Map<String, Object>) param.get("grData");
		
		// validate
		ResultMap resultMap = progressPaymentValidator.validate(grData);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			String grUuid = grService.saveGr(param); // 데이터 저장
			
			this.copyGeEvalSetByPoEvalProcess(grData, grUuid);
			this.updatePaymentTypeCommonCode(grData, grUuid);
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("gr_uuid", grUuid);
			proStatusService.saveDraftServiceGr(keyParam); // 진행상태 수정
			
			ResultMap submitResultMap = grEvalService.submitGrEvalByGr(grUuid);
			if(submitResultMap.isSuccess()) {
				proStatusService.submitGrEval(keyParam);
				
				Map<String, Object> tempResultData = Maps.newHashMap();
				tempResultData.put("grUuid", grUuid);
				return ResultMap.builder()
				                .resultData(tempResultData)
				                .build();
			} else if(submitResultMap.isFail()) {
				return submitResultMap;
			}
		}
		return resultMap;
	}
	
	/**
	 * 입고평가 설정 후 대상 여부 '아니오' 인 경우 입고평가 설정 정보 삭제
	 * @param grData
	 */
	private void deleteGrEvalByGr(Map<String, Object> grData) {
		if(grData == null) {
			return;
		}
		if("Y".equals(grData.get("ge_subj_yn"))) {
			return;
		}
		if(Strings.isNullOrEmpty((String) grData.get("ge_uuid"))) {
			return;
		}
		grEvalService.deleteGrEval(grData);
	}
	
	/**
	 * 발주 평가 설정 -> 입고평가 설정 복사
	 * @param grData
	 * @param grUuid
	 * @return
	 */
	protected void copyGeEvalSetByPoEvalProcess(Map<String, Object> grData, String grUuid) {
		if(grData == null) {
			return;
		}
		if(grUuid == null) {
			return;
		}
		// 입고평가 대상여부 '아니오' 인 경우 스킵
		if("N".equals(grData.get("ge_subj_yn"))) {
			return;
		}
		// 발주, 납품예정에서 넘어오지 않은 경우 복사하지 않고 스킵
		if(Strings.isNullOrEmpty((String) grData.get("po_ge_uuid"))) {
			return;
		}
		
		// 발주에서 설정한 평가정보 존재하는 경우 입고/기성 평가 설정 정보로 복사한다.
		this.copyGeEvalSetByPoEval((String) grData.get("po_ge_uuid"), grUuid);
	}
	
	protected void copyGeEvalSetByPoEval(String poGeUuid, String grUuid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("ge_uuid", poGeUuid);
		
		Map<String, Object> geInfoResult = grEvalService.findGeInfo(param);
		
		Map<String, Object> ge = (Map<String, Object>) geInfoResult.get("gegInfo");
		List<Map<String, Object>> gePrcses = grEvalService.findListGePrcsByGe(param);
		List<Map<String, Object>> gePrcsEvaltrs = (List<Map<String, Object>>) geInfoResult.get("gePrcsEvaltrs");
		
		String geUuid = UUID.randomUUID().toString();
		ge.put("ge_uuid", geUuid);
		
		//입고평가 프로세스 초기화
		for(Map<String, Object> gePrcs : gePrcses) {
			String gePrcsUuid = UUID.randomUUID().toString();
			gePrcs.put("ge_uuid", geUuid);
			
			for(Map<String, Object> gePrcsEvaltr : gePrcsEvaltrs) {
				if(gePrcs.get("ge_prcs_uuid").equals(gePrcsEvaltr.get("ge_prcs_uuid"))) {
					gePrcsEvaltr.put("ge_prcs_evaltr_uuid", UUID.randomUUID().toString());
					gePrcsEvaltr.put("ge_prcs_uuid", gePrcsUuid);
					
					// 담당자 프로세스 인 경우 입고 진행 담당자 아이디로 평가자를 변경
					if("PURC_PIC".equals(gePrcs.get("evaltr_typ_ccd"))) {
						gePrcsEvaltr.put("evaltr_id", Auth.getCurrentUser().getUsername());
					}
				}
			}
			gePrcs.put("ge_prcs_uuid", gePrcsUuid);
		}
		
		grEvalService.copyGeEvalSetByPoEval(grUuid, ge, gePrcses, gePrcsEvaltrs);
	}
	
	/**
	 * 기성승인을 한다.
	 *
	 * @param param {"grData", "insertItems", "updateItems"}
	 * @return grUuid
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveSubmitGr
	 */
	public ResultMap saveSubmitProgressPayment(Map<String, Object> param) {
		Map<String, Object> grData = (Map<String, Object>) param.get("grData");
		
		// validate
		ResultMap resultMap = progressPaymentValidator.validate(grData);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			String grUuid = grService.saveGr(param); // 데이터 저장
			
			this.updatePaymentTypeCommonCode(grData, grUuid);
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("gr_uuid", grUuid);
			proStatusService.bypassApprovalServiceGr(keyParam); // 진행상태 수정
			
			this.updatePoItem(keyParam); // po item의 발주완료여부 수정 / 발주의 발주완료여부 수정
			//grEvalService.closeEval(keyParam); // 기성 승인 시 평가 마감 처리
			//grEventPublisher.closeEval(keyParam);
			
			Map<String, Object> returnMap = Maps.newHashMap();
			returnMap.put("grUuid", grUuid);
			resultMap.setResultData(returnMap);
		}
		return resultMap;
	}
	
	/**
	 * 기성반려 처리한다.
	 *
	 * @param param {"asn_uuid"}
	 * @return grUuid
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectAdvancePaymentRequest
	 */
	public ResultMap rejectAdvancePaymentRequest(Map<String, Object> param) {
		// validate
		ResultMap resultMap = progressPaymentValidator.validate(param);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			this.updateAsnRejectOpn(param); // 기성 반려 사유를 수정한다
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("asn_uuid", param.get("asn_uuid"));
			proStatusService.rejectRequestServiceAsn(keyParam); // 반려
		}
		return resultMap;
	}
	
	/**
	 * 선급금 등록 가능여부 체크
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Method Name : checkCreatablePayment
	 */
	public ResultMap checkCreatablePayment(Map<String, Object> param) {
		return progressPaymentValidator.validate(param);
	}
	
	/**
	 * 발주기준 선급금 등록 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findAdvancePaymentGeneralInfomationByPoUuid(Map<String, Object> param) {
		Map<String, Object> prePayGr = poService.findAdvancePaymentInfomationByPoUuId(param);
		prePayGr.put("gr_sts_ccd", "CRNG");
		prePayGr.put("apymt_yn", "Y");
		prePayGr.put("gr_typ_ccd", "CONSTSVC");
		prePayGr.put("gr_pic_id", Auth.getCurrentUserName());
		
		List<Map<String, Object>> prePayGrItems = poItemService.searchAdvancePaymentItemByPoUuid(param);
		int maxNo = 10;
		boolean hasNoCdItem = false;
		for(Map<String, Object> prePayGrItem : prePayGrItems) {
			prePayGrItem.put("gr_lno", maxNo);
			prePayGrItem.put("gr_sts_ccd", prePayGr.get("gr_sts_ccd"));
			prePayGrItem.put("gr_typ_ccd", prePayGr.get("gr_typ_ccd"));
			maxNo += 10;
			if("CDLS".equals(prePayGrItem.get("item_cd_crn_typ_ccd"))) {
				hasNoCdItem = true;
			}
		}
		prePayGr.put("has_no_cd_item", hasNoCdItem ? "Y" : "N");
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("grData", prePayGr);
		resultMap.put("grItems", prePayGrItems);
		resultMap.put("paymentPlans", poPaymentExpectationService.findListPaymentExpectationByPoId(param));
		
		return resultMap;
	}
	
	/**
	 * 선급금 임시 저장한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 14
	 * @Method Name : saveAdvancePaymentDraft
	 */
	public ResultMap saveAdvancePaymentDraft(Map<String, Object> param) {
		Map<String, Object> grData = (Map<String, Object>) param.get("grData");
		
		// validate
		ResultMap resultMap = progressPaymentValidator.validate(grData);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			String grUuid = grService.saveGr(param); // 데이터 저장
			
			this.updatePaymentTypeCommonCode(grData, grUuid);
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("gr_uuid", grUuid);
			proStatusService.saveDraftServiceGr(keyParam); // 진행상태 수정
			
			Map<String, Object> returnMap = Maps.newHashMap();
			returnMap.put("grUuid", grUuid);
			resultMap.setResultData(returnMap);
		}
		return resultMap;
	}
	
	/**
	 * 선급금 승인 저장한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 14
	 * @Method Name : saveAdvancePaymentSubmit
	 */
	public ResultMap saveAdvancePaymentSubmit(Map<String, Object> param) {
		Map<String, Object> grData = (Map<String, Object>) param.get("grData");
		
		// validate
		ResultMap resultMap = progressPaymentValidator.validate(grData);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			String grUuid = grService.saveGr(param); // 데이터 저장
			
			this.updatePaymentTypeCommonCode(grData, grUuid);
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("gr_uuid", grUuid);
			
			proStatusService.approveApprovalServiceGr(keyParam); // 진행상태 수정
			
			this.updatePoItem(keyParam); // po item의 발주완료여부 수정 / 발주의 발주완료여부 수정
			
			Map<String, Object> returnMap = Maps.newHashMap();
			returnMap.put("grUuid", grUuid);
			resultMap.setResultData(returnMap);
		}
		return resultMap;
	}
	
	/**
	 * 기성취소를 저장한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 14
	 * @Method Name : cancelProgressPayment
	 */
	public ResultMap cancelProgressPayment(Map<String, Object> param) {
		// 기성취소 가능여부 확인
		ResultMap resultMap = checkProgressPaymentCancelable(param);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			proStatusService.cancelServiceGr(param);
			
			this.updatePoItem(param); // po item의 발주완료여부 수정 / 발주의 발주완료여부 수정
		}
		return resultMap;
	}
	
	/**
	 * 기성취소 가능여부를 확인한다.
	 *
	 * @param param
	 * @return
	 */
	private ResultMap checkProgressPaymentCancelable(Map<String, Object> param) {
		Map<String, Object> invalidResult = progressPaymentRepository.checkProgressPaymentCancelable(param);
		if(invalidResult == null) {
			return ResultMap.SUCCESS();
		}
		
		return ResultMap.builder().resultStatus(ProConst.PO_STATE_ERR).resultData(invalidResult).build();
	}
	
	/**
	 * 선급금 입고헤더 조회한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 14
	 * @Method Name : findInfoGrHeader
	 */
	public Map<String, Object> findAdvancePaymentByGrUuid(Map<String, Object> param) {
		return progressPaymentRepository.findAdvancePaymentByGrUuid(param);
	}
	
	/**
	 * 선급금 입고상세 조회한다.
	 *
	 * @param param the param
	 * @return the list< map< string, object>>
	 * @author : JuEung Kim
	 */
	public List<Map<String, Object>> searchAdvancePaymentItemByGrUuid(Map<String, Object> param) {
		return progressPaymentItemService.searchAdvancePaymentItemByGrUuid(param);
	}
	
	/**
	 * po item의 발주완료여부 수정 / 발주의 발주완료여부 수정
	 *
	 * @param param {"gr_uuid"}
	 * @return grUuid
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : updatePoItem
	 */
	public void updatePoItem(Map<String, Object> param) {
		List<Map<String, Object>> items = this.searchProgressPaymentItemByGrUuid(param);
		
		List<String> poIds = new ArrayList<String>();
		List<String> poItemIds = new ArrayList<String>();
		for(Map<String, Object> row : items) {
			String poItemId = (String) row.get("po_item_uuid");
			String poId = (String) row.get("po_uuid");
			
			// 발주품목 발주완료여부 수정을 위한 po_item_uuid
			if(poItemIds.indexOf(poItemId) < 0) {
				poItemIds.add(poItemId);
			}
			
			// 발주의 발주완료여부 수정을 위한 po_uuid
			if(poIds.indexOf(poId) < 0) {
				poIds.add(poId);
			}
		}
		
		// 발주품목 발주완료여부 수정(발주수량과 입고수량 비교)한다.
		Map<String, Object> itemParam = Maps.newHashMap();
		itemParam.put("po_item_uuids", poItemIds);
		poItemService.updatePoItemCompleteByAmt(itemParam);
		
		// 발주의 발주완료여부 수정
		Map<String, Object> poParam = Maps.newHashMap();
		poParam.put("po_uuids", poIds);
		poService.updatePoComplete(poParam);
	}
	
	/**
	 * 선급금등록(임시저장) 삭제
	 *
	 * @param param
	 * @return
	 */
	public ResultMap deleteAdvancePayment(Map<String, Object> param) {
		Map<String, Object> deleteParam = Maps.newHashMap();
		deleteParam.put("gr_uuid", param.get("gr_uuid"));
		deleteParam.put("gr_sts_ccd", param.get("gr_sts_ccd"));
		deleteParam.put("asn_uuid", param.get("asn_uuid"));
		deleteParam.put("apymt_yn", "Y");
		
		// validate
		ResultMap resultMap = progressPaymentValidator.validate(deleteParam);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			grService.deleteGrByGrUuid(deleteParam);
			grService.deleteGrItemByGrUuid(deleteParam);
			
			proStatusService.deleteServiceGr(deleteParam);
		}
		return resultMap;
	}
	
	/**
	 * 기성등록(임시저장) 삭제
	 *
	 * @param param
	 * @return
	 */
	public ResultMap deleteProgressPayment(Map<String, Object> param) {
		Map<String, Object> deleteParam = Maps.newHashMap();
		deleteParam.put("gr_uuid", param.get("gr_uuid"));
		deleteParam.put("gr_sts_ccd", param.get("gr_sts_ccd"));
		deleteParam.put("asn_uuid", param.get("asn_uuid"));
		deleteParam.put("gr_no", param.get("gr_no"));
		deleteParam.put("gr_ordn", param.get("gr_ordn"));
		
		
		// validate
		ResultMap resultMap = progressPaymentValidator.validate(deleteParam);
		if(!resultMap.isSuccess()) {
			return resultMap;
		}
		this.deleteProgressPaymentProcess(deleteParam);
		return resultMap;
	}
	
	protected void deleteProgressPaymentProcess(Map<String, Object> deleteParam) {
		grService.deleteGrByGrUuid(deleteParam);
		grService.deleteGrItemByGrUuid(deleteParam);
		proStatusService.deleteServiceGr(deleteParam);
	}
	
	public ResultMap evalForceClose(Map param) {
		List<Map<String, Object>> checkedItems = (List<Map<String, Object>>) param.get("checkedItems");
		for(Map<String, Object> checkedItem : checkedItems) {
			grEvalService.evalForceClose(checkedItem);
		}
		
		return ResultMap.SUCCESS();
	}
	
	public Map findPpDefaultDataByPo(Map param) {
		Map<String, Object> progressPaymentHeader = poService.findProgressPaymentDefaultDataByPo(param);
		progressPaymentHeader.put("gr_sts_ccd", "CRNG");
		progressPaymentHeader.put("apymt_yn", "N");
		progressPaymentHeader.put("gr_typ_ccd", "CONSTSVC");
		progressPaymentHeader.put("gr_pic_id", Auth.getCurrentUserName());
		
		List<Map<String, Object>> progressPaymentItems = poItemService.searchProgressPaymentItemByPo(param);
		int maxNo = 10;
		boolean hasNoCdItem = false;
		for(Map<String, Object> prePayGrItem : progressPaymentItems) {
			prePayGrItem.put("gr_lno", maxNo);
			prePayGrItem.put("gr_sts_ccd", progressPaymentHeader.get("gr_sts_ccd"));
			prePayGrItem.put("gr_typ_ccd", progressPaymentHeader.get("gr_typ_ccd"));
			maxNo += 10;
			if("CDLS".equals(prePayGrItem.get("item_cd_crn_typ_ccd"))) {
				hasNoCdItem = true;
			}
		}
		progressPaymentHeader.put("has_no_cd_item", hasNoCdItem ? "Y" : "N");
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("grData", progressPaymentHeader);
		resultMap.put("grItems", progressPaymentItems);
		resultMap.put("paymentPlans", poPaymentExpectationService.findListPaymentExpectationByPoId(progressPaymentHeader));
		
		return resultMap;
	}
	
	public Map findAdvancePayment(Map param) {
		Map result = Maps.newHashMap();
		// 분류정보
		Map grData = this.findAdvancePaymentByGrUuid(param);
		result.put("grData", grData);
		result.put("grDtList", this.searchAdvancePaymentItemByGrUuid(param));
		result.put("paymentPlans", poPaymentExpectationService.findListPaymentExpectationByPoNoandPoRevno(grData));
		
		return result;
	}
	
	/**
	 * map에 지급 유형을 지정한다.
	 * @param grData
	 */
	public void setPaymentTypeCommonCode(Map<String, Object> progressPayment, Map<String, Object> grData) {
		if(grData.containsKey("apymt_yn") && "Y".equals(grData.get("apymt_yn"))) {
			progressPayment.put("pymt_typ_ccd", "APYMT");
		} else {
			progressPayment.put("pymt_typ_ccd", this.getProgressPaymentTypeCommonCode(grData));
		}
	}
	
	public String getProgressPaymentTypeCommonCode(Map<String, Object> grData) {
		Map grInfo = progressPaymentRepository.getProgressPaymentTypeRelatedInfo(grData);
		String paymentType = "BAL";
		BigDecimal poAmt = new BigDecimal(String.valueOf(grInfo.get("po_amt")));
		BigDecimal grAmt = new BigDecimal(String.valueOf(grInfo.get("gr_amt")));
		BigDecimal ttlGrAmt = new BigDecimal(String.valueOf(grInfo.get("ttl_gr_amt")));
		
		if(poAmt.subtract(ttlGrAmt.add(grAmt)).compareTo(BigDecimal.ZERO) > 0) {
			paymentType = "IPYMT";
		}
		
		return paymentType;
	}
	
	public void updatePaymentTypeCommonCode(Map<String, Object> grData, String grUuid) {
		Map<String, Object> progressPayment = Maps.newHashMap();
		progressPayment.put("gr_uuid", grUuid);
		setPaymentTypeCommonCode(progressPayment, grData);
		
		progressPaymentRepository.updatePaymentTypeCommonCode(progressPayment);
	}
}
