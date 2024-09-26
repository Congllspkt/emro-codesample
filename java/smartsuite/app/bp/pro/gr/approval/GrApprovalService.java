package smartsuite.app.bp.pro.gr.approval;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.pro.gr.service.GrService;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 입고 결재관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @FileName GrApprovalService.java
 * @package smartsuite.app.bp.pro.gr
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 * @see
 */
@Service
@Transactional
@SuppressWarnings("unchecked")
public class GrApprovalService implements ApprovalStrategy {
	
	@Inject
	private ProStatusService proStatusProcessor;
	
	@Inject
	private GrService grService;
	
	@Inject
	private SharedService sharedService;
	
	@Inject
	private FormatterProvider formatterProvider;
	
	private final SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
	
	
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
		proStatusProcessor.approveApprovalGr(param);
		
		grService.updatePoItemAsn(param); // po item의 검수요청중인수량, 입고수량 및 발주완료여부 수정 / 발주의 발주완료여부 수정
		
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
		proStatusProcessor.rejectApprovalGr(param);
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
		proStatusProcessor.cancelApprovalGr(param);
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
		proStatusProcessor.submitApprovalGr(param);
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
	 * @param approvalType, appId
	 */
	@Override
	public Map<String, Object> makeParam(String approvalType, String appId) {
		Map<String, Object> newParam = Maps.newHashMap();
		Map<String, Object> resultMap = Maps.newHashMap();
		
		newParam.put("gr_uuid", appId);
		
		Map<String, Object> grInfo = grService.findGr(newParam);
		if(grInfo != null) {
			List<Map<String, Object>> grItems = (List<Map<String, Object>>) grInfo.get("items");
			
			//구매운영조직
			grInfo.put("oorg_cd_nm", sharedService.findOperationOrganizationName((String) grInfo.get("oorg_cd"), "PO"));
			
			//Yn값 변환 
			grInfo.put("prtl_delay_yn_str", changeYntoYesOrNo(grInfo.get("prtl_delay_yn")));
			grInfo.put("dfrm_exmt_yn_str", changeYntoYesOrNo(grInfo.get("dfrm_exmt_yn")));
			
			// \n값 변환
			grInfo.put("gr_opn", goToNextLine(grInfo.get("gr_opn")));
			grInfo.put("delay_rsn", goToNextLine(grInfo.get("delay_rsn")));
			grInfo.put("prtl_delay_rsn", goToNextLine(grInfo.get("prtl_delay_rsn")));
			grInfo.put("dfrm_exmt_rsn", goToNextLine(grInfo.get("dfrm_exmt_rsn")));
			
			for(Map<String, Object> item : grItems) {
				//구매운영조직
				item.put("oorg_cd_nm", sharedService.findOperationOrganizationName((String) item.get("oorg_cd"), "PO"));
				item.put("delay_dcnt", getDelayDay(item));    //지체일수
				item.put("delay_amt", getDelayAmt(item));    //지체상금금액
				item.put("ret_amt", getReturnAmt(item));    //반품금액
			}
			
			//format 적용
			grInfo = getFormatDisPlayByGrInfo(grInfo);
			resultMap.put("grInfo", grInfo);
			
			//format적용
			grItems = getApprovalFormatDataByItems(grItems);
			resultMap.put("grItems", grItems);
		}
		
		return resultMap;
	}
	
	private Map<String, Object> getFormatDisPlayByGrInfo(Map<String, Object> grInfo) {
		BigDecimal delyTotAmt = BigDecimal.ZERO;
		BigDecimal delayAmt = BigDecimal.ZERO;
		if(grInfo.get("gr_amt") != null) {
			delyTotAmt = (BigDecimal) grInfo.get("gr_amt");
		}
		if(grInfo.get("delay_amt") != null) {
			delayAmt = (BigDecimal) grInfo.get("delay_amt");
		}
		grInfo.put("gr_amt", formatterProvider.getPrecFormatZero("amt", delyTotAmt, true));
		grInfo.put("delay_amt", formatterProvider.getPrecFormatZero("amt", delayAmt, true));
		return grInfo;
	}
	
	private List<Map<String, Object>> getApprovalFormatDataByItems(List<Map<String, Object>> poitems) {
		List<Map<String, Object>> lists = Lists.newArrayList();
		lists = poitems;
		for(Map<String, Object> item : lists) {
			String[] amtFormatFields = {"po_amt", "ret_amt", "gr_amt", "delay_amt"};
			String[] qtyFormatFields = {"po_qty", "asn_qty", "ret_qty", "gr_qty", "po_gr_qty"};
			String[] decimalFormatFields = {"dfrm_ro"};
			List<String> amtFormatFieldsArr = Lists.newArrayList(Arrays.asList(amtFormatFields));
			List<String> qtyFormatFieldsArr = Lists.newArrayList(Arrays.asList(qtyFormatFields));
			List<String> decimalFormatFieldsArr = Lists.newArrayList(Arrays.asList(decimalFormatFields));
			
			for(Entry<String, Object> entry : item.entrySet()) {
				String strKey = entry.getKey();
				Object strValue = entry.getValue();
				BigDecimal value = BigDecimal.ZERO;
				if(amtFormatFieldsArr.contains(strKey)) {
					if(strValue != null) {
						value = (BigDecimal) strValue;
					}
					item.put(strKey, formatterProvider.getPrecFormatZero("amt", value, true));
				} else if(qtyFormatFieldsArr.contains(strKey)) {
					if(strValue != null) {
						value = (BigDecimal) strValue;
					}
					item.put(strKey, formatterProvider.getPrecFormatZero("qty", value, true));
				} else if(decimalFormatFieldsArr.contains(strKey)) {
					if(strValue != null) {
						value = (BigDecimal) strValue;
					}
					item.put(strKey, formatterProvider.getPrecFormatZero("decimal", value, true));
				}
			}
		}
		return lists;
	}
	
	/**
	 * Yn 값이 Y 일때는 Yes, N 일때는 No로 변경해주는 함수.
	 *
	 * @param ynValue
	 */
	private String changeYntoYesOrNo(Object ynValue) {
		String yesOrNoValue = "";
		
		if(ynValue != null) {
			if("Y".equals(ynValue.toString())) {
				yesOrNoValue = "Yes";
			} else {
				yesOrNoValue = "No";
			}
		}
		
		return yesOrNoValue;
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
	 * 검수품목의 지체일수를 계산하는 함수
	 * 계산방식은 es-gr-detail.html 에 있는 onDelayDayConverter 함수를 참고함.
	 *
	 * @param Map<String, Object> item
	 */
	private BigDecimal getDelayDay(Map<String, Object> item) {
		BigDecimal delayDay = BigDecimal.ZERO;
		
		if(item.get("req_dlvy_dt") != null && item.get("gr_dt") != null) {
			Date rdDate = stringToDate(item.get("req_dlvy_dt"));    //납기일자
			Date grDate = stringToDate(item.get("gr_dt"));    //검수일자
			
			delayDay = BigDecimal.ZERO.max(new BigDecimal((grDate.getTime() - rdDate.getTime()) / (1000 * 60 * 60 * 24)).setScale(0, RoundingMode.UP));
		}
		
		return delayDay;
	}
	
	/**
	 * 검수품목의 지체상금금액을 계산하는 함수
	 * 계산방식은 es-gr-detail.html 에 있는 onDelayAmtConverter 함수를 참고함.
	 *
	 * @param Map<String, Object> item
	 */
	private BigDecimal getDelayAmt(Map<String, Object> item) {
		BigDecimal delayDay = (BigDecimal) item.get("delay_dcnt");
		BigDecimal itemAmt = BigDecimal.ZERO;
		BigDecimal delayRate = BigDecimal.ZERO;
		
		if(item.get("po_amt") != null) {
			itemAmt = (BigDecimal) item.get("po_amt");
		}
		
		if(item.get("dfrm_ro") != null) {
			delayRate = (BigDecimal) item.get("dfrm_ro");
		}
		
		if(BigDecimal.ZERO.compareTo(delayDay) == 0 || BigDecimal.ZERO.compareTo(delayRate) == 0) {
			return BigDecimal.ZERO;
		}
		return formatterProvider.getPrecFormat("amt", itemAmt.multiply(delayDay).multiply(delayRate).divide(new BigDecimal(1000)), item.get("cur_ccd").toString());
	}
	
	/**
	 * 검수품목의 반품금액을 계산하는 함수
	 * 계산방식은 es-gr-detail.html 에 있는 onReturnAmtConverter 함수를 참고함.
	 *
	 * @param Map<String, Object> item
	 */
	private BigDecimal getReturnAmt(Map<String, Object> item) {
		BigDecimal returnQty = BigDecimal.ZERO;
		BigDecimal itemPrice = BigDecimal.ZERO;
		
		if(item.get("ret_qty") != null) {
			returnQty = (BigDecimal) item.get("ret_qty");
		}
		
		if(item.get("po_uprc") != null) {
			itemPrice = (BigDecimal) item.get("po_uprc");
		}
		
		return returnQty.multiply(itemPrice);
	}
	
	/**
	 * String으로 된 날짜를 Date 형식으로 변화하는 함수
	 *
	 * @param Map<String, Object> item
	 */
	private Date stringToDate(Object strDate) {
		Date newDate = new Date();
		
		if(strDate != null) {
			try {
				newDate = fm.parse(strDate.toString());
			} catch(ParseException e) {
				e.printStackTrace();
			}
		}
		
		return newDate;
	}
	
}
