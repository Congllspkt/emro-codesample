package smartsuite.app.bp.pro.po.approval;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.pro.po.event.PoEventPublisher;
import smartsuite.app.bp.pro.po.service.PoItemService;
import smartsuite.app.bp.pro.po.service.PoService;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PO변경 결재관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @see
 * @FileName ModifyPoApprovalService.java
 * @package smartsuite.app.bp.pro.po
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 */
@Service
@Transactional
public class ModifyPoApprovalService implements ApprovalStrategy {

	@Inject
	private ProStatusService proStatusService;

	@Inject
	private PoService poService;
	
	@Inject
	private PoItemService poItemService;
	
	@Inject
	private SharedService sharedService;

	@Inject
	private PoEventPublisher poEventPublisher;

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
		
		Map<String, Object> poData = poService.findPoHeader(param);
		
		// 계약체결유형
		String cntrSgngTypCcd = (poData.get("cntr_sgng_typ_ccd") == null) ? "" : (String)poData.get("cntr_sgng_typ_ccd");
		
		param.put("cntr_sgng_typ_ccd", cntrSgngTypCcd);
		param.put("cntr_uuid", poData.get("cntr_uuid"));
		
		proStatusService.approveApprovalPoChange(param);	// 1. 상태 업데이트
		
		poService.updatePrevPoByModification(param, "PO_CHG");	// 2. 이전 차수의 상태 업데이트
		poService.updateCurrentPo(param);				// 3. 현재 유효한 발주여부(EFCT_PO_YN)를 업데이트 한다.
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
		proStatusService.rejectApprovalPoChange(param);
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
		proStatusService.cancelApprovalPoChange(param);
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
		proStatusService.submitApprovalPoChange(param);
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
	
	/**
	 * 결재서식 param을 위해 업무단 정보 조회
	 *
	 * @param approvalType, appId
	 */
	@Override
	public Map<String, Object> makeParam(String approvalType, String appId) {
		Map<String, Object> newParam = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		newParam.put("po_uuid", appId);
		
		Map<String, Object> poInfo = poService.findPoHeader(newParam);
		
		if(poInfo != null) {
			//이전차수 발주정보를 조회하기 위해 param 설정
			newParam.put("po_no", poInfo.get("po_no"));
			newParam.put("po_revno", Integer.parseInt(poInfo.get("po_revno").toString())-1 );
			
			//현재차수 발주 Item조회
			List<Map<String, Object>> poItems = poItemService.findListPoItemByPoId(newParam);
			
			//이전차수 발주정보 조회
			Map<String, Object> previousPoInfo = poService.findPreviousPoHeader(newParam);
			List<Map<String, Object>> previousPoItems = poService.findPreviousListPoItemByPoId(newParam);
			
			//코드명으로 변환
			poInfo.put("purc_typ_nm", sharedService.findCodeName(poInfo.get("purc_typ_ccd"), "P045"));			//구매 유형 공통코드
			poInfo.put("shipper_type_nm", sharedService.findCodeName(poInfo.get("domovrs_div_ccd"), "C024"));	//내외자구분
			poInfo.put("pymtmeth_ccd_nm", sharedService.findCodeName(poInfo.get("pymtmeth_ccd"), "P009"));	//대금지급조건
			poInfo.put("dely_terms_cd_nm", sharedService.findCodeName(poInfo.get("dlvymeth_ccd"), "P010"));	//인도조건
			poInfo.put("tax_cd_nm", sharedService.findCodeName(poInfo.get("tax_typ_ccd"), "C031"));				//세금코드 
			
			poInfo.put("perfor_bond_cls_nm", sharedService.findCodeName(poInfo.get("cpgur_typ_ccd"), "P064"));		//보증종류
			poInfo.put("pre_pay_bond_cls_nm", sharedService.findCodeName(poInfo.get("apymtgur_typ_ccd"), "P064"));
			poInfo.put("maint_bond_cls_nm", sharedService.findCodeName(poInfo.get("defpgur_typ_ccd"), "P064"));
			poInfo.put("maint_bond_typ_nm", sharedService.findCodeName(poInfo.get("defpgur_pd_typ_ccd"), "P048"));		//하자보증기간기준
			
			poInfo.put("oorg_cd_nm", sharedService.findOperationOrganizationName((String) poInfo.get("oorg_cd"), "PO"));		//구매운영조직
			
			//Yn값 변환 
			poInfo.put("elec_cntr_yn_str", ("ECNTR".equals((String)poInfo.get("cntr_sgng_typ_ccd")) ? "Yes" : "No"));
			poInfo.put("bas_cntr_yn_str", changeYntoYesOrNo(poInfo.get("mstagt_yn")));
			poInfo.put("pay_terms_yn_str", changeYntoYesOrNo(poInfo.get("pymtmeth_use_yn")));
			poInfo.put("dely_terms_yn_str", changeYntoYesOrNo(poInfo.get("dlvymeth_use_yn")));
			
			//면세여부Yn 변환
			for(Map<String, Object> item : poItems) {
				item.put("duty_free_yn_str", changeYntoYesOrNo(item.get("df_yn")));
			}
			
			for(Map<String, Object> previousItem : previousPoItems) {
				previousItem.put("duty_free_yn_str", changeYntoYesOrNo(previousItem.get("df_yn")));
			}
			
			// \n값 변환
			poInfo.put("po_rem", goToNextLine(poInfo.get("po_rem")));				//특약사항
			poInfo.put("pymtmeth_cnd", goToNextLine(poInfo.get("pymtmeth_cnd")));			//대금지급조건
			poInfo.put("po_chg_req_rsn", goToNextLine(poInfo.get("po_chg_req_rsn")));//변경사유
			
			//현재차수 발주정보 설정
			resultMap.put("poInfo", poInfo);
			resultMap.put("poItems", poItems);
			
			//이전차수 발주정보 설정
			resultMap.put("previousPoInfo", previousPoInfo);
			resultMap.put("previousPoItems", previousPoItems);
		}
		
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
}
