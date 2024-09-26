package smartsuite.app.bp.pro.pr.approval;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.pro.pr.service.PrItemService;
import smartsuite.app.bp.pro.pr.service.PrService;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Pr 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @see
 * @FileName PrService.java
 * @package smartsuite.app.bp.pro
 * @Since 2016. 3. 8
 * @변경이력 : [2016. 3. 8] Yeon-u Kim 최초작성
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class PrApprovalService implements ApprovalStrategy {

	@Inject
	ProStatusService proStatusProcessor;

	@Inject
	PrService prService;

	@Inject
	PrItemService prItemService;

	@Inject
	SharedService sharedService;

	@Inject
	FormatterProvider formatProvider;

	/**
	 * 승인.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doApprove(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("pr_uuid", appId);
		param.put("pr_chg_yn", ProConst.PR_CHG_YN_N);

		proStatusProcessor.approveApprovalPr(param);
		prService.requestRfx(param);
		//prService.createAutoTouchlessPo(param);
	}

	/**
	 * 반려.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doReject(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("pr_uuid", appId);
		param.put("pr_chg_yn", ProConst.PR_CHG_YN_N);
		proStatusProcessor.rejectApprovalPr(param);
	}

	/**
	 * 결재 취소.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doCancel(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("pr_uuid", appId);
		param.put("pr_chg_yn", ProConst.PR_CHG_YN_N);
		proStatusProcessor.cancelApprovalPr(param);
	}

	/**
	 * 상신.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doSubmit(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("pr_uuid", appId);
		param.put("pr_chg_yn", ProConst.PR_CHG_YN_N);
		proStatusProcessor.submitApprovalPr(param);
	}

	/**
	 * 임시저장.
	 *
	 * @param approvalType the approval type
	 * @param appId the app id
	 */
	@Override
	public void doTemporary(String approvalType, String appId) {
		Map param = Maps.newHashMap();
		param.put("pr_uuid", appId);
		/*param.put("pr_sts_ccd", "T");
		// 접수대기
		param.put("pr_sts_ccd", "PR_CRNG");*/
		//proStatusProcessor.saveDraftPr(param);
	}

	/**
	 * 결재서식 param을 위해 업무단 정보 조회
	 *
	 * @param approvalType, appId
	 */
	@Override
	public Map makeParam(String approvalType, String appId) {
		Map newParam = Maps.newHashMap();
		Map resultMap = Maps.newHashMap();

		newParam.put("pr_uuid", appId);

		Map prData =  prService.findPr(newParam);
		if(prData != null) {
			// 구매 유형 공통코드(P045), 계약형태(P049), 구매운영조직값 변환
			String purcTypNm = sharedService.findCodeName(prData.get("purc_typ_ccd"), "P045");				//구매 유형 공통코드
			String prPurpNm = sharedService.findCodeName(prData.get("pr_purp_ccd"), "P049");		//계약형태

			String operOrgCdNm = sharedService.findOperationOrganizationName((String) prData.get("oorg_cd"), "PO");		//구매운영조직

			prData.put("purc_typ_nm", purcTypNm);
			prData.put("pr_purp_nm", prPurpNm);
			prData.put("oorg_cd_nm", operOrgCdNm);

			BigDecimal prTotAmt = BigDecimal.ZERO;
			prTotAmt = (BigDecimal)prData.get("pr_amt");
			prData.put("pr_amt", formatProvider.getPrecFormatZero("amt",prTotAmt,true));
			resultMap.put("prInfo", prData);

			List<Map> prItems = prItemService.findListPrItem(prData);
        	List<Map<String, Object>> listOfMapsStringObject = (List<Map<String, Object>>)(List<?>) prItems;
			//formatter 적용
			Map<String, Object> formatFields = Maps.newHashMap();
			formatFields.put("item_qty", "qty");
			formatFields.put("pr_price", "price");
			formatFields.put("pr_amt", "amt");
			List<Map<String, Object>> prItemsByFormatting = formatProvider.getListPrecFormatZero(formatFields,listOfMapsStringObject,true);
			resultMap.put("prItems", prItemsByFormatting);


			resultMap.put("prInfo", prData);
			resultMap.put("prItems", prItems);
		}
		return resultMap;
	}
	
}
