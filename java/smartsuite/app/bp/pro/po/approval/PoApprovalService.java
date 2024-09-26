package smartsuite.app.bp.pro.po.approval;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.pro.po.event.PoEventPublisher;
import smartsuite.app.bp.pro.po.service.PoItemService;
import smartsuite.app.bp.pro.po.service.PoService;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * PO 결재관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @see
 * @FileName PoApprovalService.java
 * @package smartsuite.app.bp.pro.po
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 */
@Service
@Transactional
public class PoApprovalService implements ApprovalStrategy {

	@Inject
	private ProStatusService proStatusService;

	@Inject
	private PoService poService;
	
	@Inject
	private PoItemService poItemService;
	
	@Inject
	private SharedService sharedService;
	
	@Inject
	private FormatterProvider formatProvider;

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
		
		proStatusService.approveApprovalPo(param);	// 1. 상태 업데이트
		poService.updatePoCreDate(appId);			// 2. 발주 생성 일자 수정
		poService.updateCurrentPo(param);		// 3. 현재 유효한 발주여부(EFCT_PO_YN)를 업데이트 한다.
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
		proStatusService.rejectApprovalPo(param);
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
		proStatusService.cancelApprovalPo(param);
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
		proStatusService.submitApprovalPo(param);
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
			List<Map<String, Object>> poItems = poItemService.findListPoItemByPoId(newParam);
			
			//코드명으로 변환
			poInfo.put("purc_typ_nm", sharedService.findCodeName(poInfo.get("purc_typ_ccd"), "P045"));			//구매 유형 공통코드
			poInfo.put("shipper_type_nm", sharedService.findCodeName(poInfo.get("domovrs_div_ccd"), "C024"));	//내외자구분
			poInfo.put("pymtmeth_ccd_nm", sharedService.findCodeName(poInfo.get("pymtmeth_ccd"), "P009"));	//대금지급조건
			poInfo.put("dely_terms_cd_nm", sharedService.findCodeName(poInfo.get("dlvymeth_ccd"), "P010"));	//인도조건
			poInfo.put("tax_cd_nm", sharedService.findCodeName(poInfo.get("tax_typ_ccd"), "C031"));				//세금코드 
			
			poInfo.put("perfor_bond_cls_nm", sharedService.findCodeName(poInfo.get("cpgur_typ_ccd"), "P064"));		//보증종류
			poInfo.put("pre_pay_bond_cls_nm", sharedService.findCodeName(poInfo.get("apymtgur_typ_ccd"), "P064"));	//보증종류
			poInfo.put("maint_bond_cls_nm", sharedService.findCodeName(poInfo.get("defpgur_typ_ccd"), "P064"));		//보증종류
			poInfo.put("maint_bond_typ_nm", sharedService.findCodeName(poInfo.get("defpgur_pd_typ_ccd"), "P048"));		//하자보증기간기준
			
			poInfo.put("oorg_cd_nm", sharedService.findOperationOrganizationName((String) poInfo.get("oorg_cd"), "PO"));		//구매운영조직

			//Yn값 변환 
			//poInfo.put("elec_cntr_yn_str", changeYntoYesOrNo(poInfo.get("cntr_use_yn")));
			poInfo.put("elec_cntr_yn_str", ("ECNTR".equals((String)poInfo.get("cntr_sgng_typ_ccd")) ? "Yes" : "No"));
			poInfo.put("bas_cntr_yn_str", changeYntoYesOrNo(poInfo.get("mstagt_yn")));
			poInfo.put("pay_terms_yn_str", changeYntoYesOrNo(poInfo.get("pymtmeth_use_yn")));
			poInfo.put("dely_terms_yn_str", changeYntoYesOrNo(poInfo.get("dlvymeth_use_yn")));
			
			//면세여부Yn 변환
			for(Map<String, Object> item : poItems) {
				item.put("duty_free_yn_str", changeYntoYesOrNo(item.get("df_yn")));
			}
			
			// \n값 변환
			poInfo.put("po_rem", goToNextLine(poInfo.get("po_rem")));
			poInfo.put("pymtmeth_cnd", goToNextLine(poInfo.get("pymtmeth_cnd")));
			
			//format적용
			poInfo = getApprovalFormatData(poInfo);
			poItems = getApprovalFormatDataByItems(poItems);
			
			resultMap.put("poInfo", poInfo);
			resultMap.put("poItems", poItems);
		}
		
		return resultMap;
	}
	
	/**
	 * approval format data의 값을 반환한다.
	 *
	 * @param obj the obj
	 * @return approval format data
	 */
	private Map<String, Object> getApprovalFormatData(Map<String, Object> obj){
		String[] amtFormatFields = {"item_amt","po_amt","cntr_amt","sup_amt","vat_amt","cpgur_amt","apymtgur_amt","defpgur_amt"};
    	String[] decimalFormatFields = {"cpgur_ro","apymtgur_ro","defpgur_ro","dfrm_ro"};
    	List<String> amtFormatFieldsArr = Lists.newArrayList(Arrays.asList(amtFormatFields));
    	List<String> decimalFormatFieldsArr = Lists.newArrayList(Arrays.asList(decimalFormatFields));
    	
    	for (Entry<String, Object> entry : obj.entrySet()) {
    		String strKey = entry.getKey();
    		Object strValue = entry.getValue();
    		BigDecimal value = BigDecimal.ZERO;
        	if(amtFormatFieldsArr.contains(strKey)){
        		if(strValue != null){
        			value = (BigDecimal)strValue;
        		}
        		obj.put(strKey, formatProvider.getPrecFormatZero("amt", value, true));
        	}else if(decimalFormatFieldsArr.contains(strKey)){
        		if(strValue != null){
        			value = (BigDecimal)strValue;
        		}
        		obj.put(strKey, formatProvider.getPrecFormatZero("decimal", value, true));
        	}
        }
		return obj;
		
	}
	
	private List<Map<String, Object>> getApprovalFormatDataByItems(List<Map<String, Object>> poitems){
		List<Map<String, Object>> lists = Lists.newArrayList();
		lists = poitems;
		for(Map<String, Object> item: lists){
			BigDecimal qty = BigDecimal.ZERO;
			BigDecimal price = BigDecimal.ZERO;
			BigDecimal amt = BigDecimal.ZERO;
			if(item.get("po_qty") != null){
				qty = (BigDecimal)item.get("po_qty");
			}
			if(item.get("po_uprc") != null){
				price = (BigDecimal)item.get("po_uprc");
			}
			if(item.get("po_amt") != null){
				amt = (BigDecimal)item.get("po_amt");
			}
			item.put("po_qty", formatProvider.getPrecFormatZero("qty", qty, true));
			item.put("po_uprc", formatProvider.getPrecFormatZero("price", price, true));
			item.put("po_amt", formatProvider.getPrecFormatZero("amt", amt, true));
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

}
