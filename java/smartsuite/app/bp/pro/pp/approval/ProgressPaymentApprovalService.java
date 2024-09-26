package smartsuite.app.bp.pro.pp.approval;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.pro.pp.service.ProgressPaymentService;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;

/**
 * 기성승인 결재관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @FileName ProgressPaymentApprovalService.java
 * @package smartsuite.app.bp.pro.gr.approval
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 * @see
 */
@Service
@Transactional
@SuppressWarnings("unchecked")
public class ProgressPaymentApprovalService implements ApprovalStrategy {
	
	@Inject
	private ProStatusService proStatusProcessor;
	
	@Inject
	private ProgressPaymentService progressPaymentService;
	
	@Inject
	private SharedService sharedService;
	
	/**
	 * 승인.
	 *
	 * @param approvalType the approval type
	 * @param appId        the app id
	 */
	@Override
	public void doApprove(String approvalType, String appId) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("gr_uuid", appId);
		proStatusProcessor.approveApprovalServiceGr(param);
		
		progressPaymentService.updatePoItem(param); // po item의 발주완료여부 수정 / 발주의 발주완료여부 수정
		
		//grEvalService.closeEval(param); // 기성 승인 시 평가 마감 처리
		//grEventPublisher.closeEval(param);
	}
	
	/**
	 * 반려.
	 *
	 * @param approvalType the approval type
	 * @param appId        the app id
	 */
	@Override
	public void doReject(String approvalType, String appId) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("gr_uuid", appId);
		proStatusProcessor.rejectApprovalServiceGr(param);
	}
	
	/**
	 * 결재 취소.
	 *
	 * @param approvalType the approval type
	 * @param appId        the app id
	 */
	@Override
	public void doCancel(String approvalType, String appId) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("gr_uuid", appId);
		proStatusProcessor.cancelApprovalServiceGr(param);
	}
	
	/**
	 * 상신.
	 *
	 * @param approvalType the approval type
	 * @param appId        the app id
	 */
	@Override
	public void doSubmit(String approvalType, String appId) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("gr_uuid", appId);
		proStatusProcessor.submitApprovalServiceGr(param);
	}
	
	/**
	 * 임시저장.
	 *
	 * @param approvalType the approval type
	 * @param appId        the app id
	 */
	@Override
	public void doTemporary(String approvalType, String appId) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("gr_uuid", appId);
	}
	
	/**
	 * 결재서식 param을 위해 업무단 정보 조회
	 *
	 * @param appId
	 */
	@Override
	public Map<String, Object> makeParam(String approvalType, String appId) {
		Map<String, Object> newParam = Maps.newHashMap();
		Map<String, Object> resultMap = Maps.newHashMap();
		
		newParam.put("gr_uuid", appId);
		
		Map<String, Object> grInfo = progressPaymentService.findProgressPayment(newParam);
		if(grInfo != null) {
			List<Map<String, Object>> grItems = (List<Map<String, Object>>) grInfo.get("items");
			
			//구매운영조직
			grInfo.put("oorg_cd_nm", sharedService.findOperationOrganizationName((String) grInfo.get("oorg_cd"), "PO"));
			
			//금액설정
			BigDecimal poAmt = (grInfo.get("po_amt") == null) ? BigDecimal.ZERO : (BigDecimal) grInfo.get("po_amt");
			BigDecimal preGrTotAmt = (grInfo.get("pre_gr_ttl_amt") == null) ? BigDecimal.ZERO : (BigDecimal) grInfo.get("pre_gr_ttl_amt");
			BigDecimal arTotAmt = (grInfo.get("asn_amt") == null) ? BigDecimal.ZERO : (BigDecimal) grInfo.get("asn_amt");
			BigDecimal grTtlAmt = preGrTotAmt.add(arTotAmt);
			/**
			 * gr_ttl_amt      : 전체누계금액(요청)
			 * gr_ttl_rate     : 전체누계진척율(요청)
			 * pre_gr_ttl_rate : 전회누계진척율
			 * asn_rate     : 금회진척율(요청)
			 **/
			grInfo.put("gr_ttl_amt", grTtlAmt);
			grInfo.put("gr_ttl_rate", poAmt.compareTo(BigDecimal.ZERO) > 0 ? grTtlAmt.multiply(new BigDecimal(100)).divide(poAmt, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
			grInfo.put("pre_gr_ttl_rate", poAmt.compareTo(BigDecimal.ZERO) > 0 ? preGrTotAmt.multiply(new BigDecimal(100)).divide(poAmt, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
			grInfo.put("asn_rate", poAmt.compareTo(BigDecimal.ZERO) > 0 ? arTotAmt.multiply(new BigDecimal(100)).divide(poAmt, 2, RoundingMode.HALF_UP) : 0);
			
			// \n값 변환
			grInfo.put("gr_rem", goToNextLine(grInfo.get("gr_rem")));
			
			for(Map<String, Object> item : grItems) {
				item.put("remain_amt", getRemainAmt(item));
				item.put("gr_ttl_rate", getGrTotRate(item));
			}
			
			resultMap.put("grInfo", grInfo);
			resultMap.put("grItems", grItems);
		}
		return resultMap;
	}
	
	/**
	 * TextArea에 들어가있는 \n 값을 <br>로 변환하는 함수.
	 *
	 * @param str
	 */
	private String goToNextLine(Object str) {
		String resultStr = "";
		
		if(str != null) {
			resultStr = str.toString().replaceAll("\\n", "<br/>");
		}
		
		return resultStr;
	}
	
	/**
	 * 품목의 잔여금액을 구하는 함수
	 * es-payment-bill-gr-detail.html 의 onRemainAmtConverter 함수 참고
	 *
	 * @param Map<String, Object> item
	 */
	private BigDecimal getRemainAmt(Map<String, Object> item) {
		BigDecimal itemAmt = (item.get("po_amt") == null) ? BigDecimal.ZERO : (BigDecimal) item.get("po_amt");
		BigDecimal grTotAmt = (item.get("gr_ttl_amt") == null) ? BigDecimal.ZERO : (BigDecimal) item.get("gr_ttl_amt");
		
		//Math.max(0, itemAmt - grTotAmt);
		//BigDecimal remainAmt = new BigDecimal(Math.max(0, itemAmt.subtract(grTotAmt).intValue())); --> 데이터 타입이 int로 변환되면 안됨
		BigDecimal remainAmt = BigDecimal.ZERO.max(itemAmt.subtract(grTotAmt));
		
		return remainAmt;
	}
	
	/**
	 * 품목의 기성율을 구하는 함수
	 * es-payment-bill-detail.html 의 onGrTotRateConverter 함수 참고
	 *
	 * @param Map<String, Object> item
	 */
	private BigDecimal getGrTotRate(Map<String, Object> item) {
		BigDecimal itemAmt = (item.get("po_amt") == null) ? BigDecimal.ZERO : (BigDecimal) item.get("po_amt");
		BigDecimal grTotAmt = (item.get("gr_ttl_amt") == null) ? BigDecimal.ZERO : (BigDecimal) item.get("gr_ttl_amt");
		
		//Math.max(0, (100 * grTotAmt / itemAmt));
		//BigDecimal grTotRate = new BigDecimal(Math.max(0, grTotAmt.divide(itemAmt, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue())); --> 데이터 타입이 int로 변환되면 안됨
		BigDecimal grTotRate = BigDecimal.ZERO.max(new BigDecimal(100).multiply(grTotAmt).divide(itemAmt, 2, RoundingMode.HALF_UP));
		
		return grTotRate;
	}
}
