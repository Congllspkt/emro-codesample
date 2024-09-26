package smartsuite.app.bp.srm.performance.approval;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.srm.performance.request.service.PerformanceEvalRequestService;
import smartsuite.app.bp.srm.performance.result.service.PerformanceEvalResultService;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.PerformanceEvalStatusService;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 입고 결재관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @FileName PerformanceEvalApprovalService.java
 * @package smartsuite.app.bp.performance.approval
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 * @see
 */
@Service
@Transactional
@SuppressWarnings("unchecked")
public class PerformanceEvalApprovalService implements ApprovalStrategy {
	
	@Inject
	private PerformanceEvalStatusService pfmcEvalStatusProcessor;
	
	@Inject
	private PerformanceEvalRequestService pfmcEvalReqService;
	@Inject
	private PerformanceEvalResultService pfmcEvalResService;
	
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
		param.put("pe_uuid", appId);
		pfmcEvalStatusProcessor.approveApprovalPfmcEval(param);
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
		param.put("pe_uuid", appId);
		pfmcEvalStatusProcessor.rejectApprovalPfmcEval(param);
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
		param.put("pe_uuid", appId);
		pfmcEvalStatusProcessor.cancelApprovalPfmcEval(param);
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
		param.put("pe_uuid", appId);
		pfmcEvalStatusProcessor.submitApprovalPfmcEval(param);
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
		param.put("pe_uuid", appId);
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
		
		newParam.put("pe_uuid", appId);

		Map<String, Object> pfmcResInfo = pfmcEvalReqService.findPfmcEvalPrgsInfo(newParam);
		if(pfmcResInfo != null) {
			//
			Map<String, Object> peInfo = (Map<String, Object>)pfmcResInfo.get("data");
			peInfo.put("oorg_cd_nm", sharedService.findOperationOrganizationName((String) peInfo.get("oorg_cd"), "SO"));
			resultMap.put("peInfo", peInfo);
		}
		List<Map<String, Object>> peSubjResItems = pfmcEvalResService.findListPeSubjResult(newParam);
		resultMap.put("peSubjResItems", peSubjResItems);
		return resultMap;
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
	 * String으로 된 날짜를 Date 형식으로 변화하는 함수
	 *
	 * @param String
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
