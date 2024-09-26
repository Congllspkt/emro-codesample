package smartsuite.app.bp.rfx.rfx.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class RfxValidator {

	@Inject
	SharedService sharedService;

	@Inject
	MessageSource messageSource;

	public ResultMap existRfxId(Map param) {
		String rfxId = (String) param.get("rfx_uuid");
		
		if(rfxId == null) {
			return ResultMap.NOT_EXISTS();
		}
		return ResultMap.SUCCESS();
	}
	
	public ResultMap existRfxNoAndRev(Map param) {
		String rfxNo = (String) param.get("rfx_no");
		String rfxRev = (String) param.get("rfx_rnd");
		
		if(rfxNo == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(rfxRev == null) {
			return ResultMap.NOT_EXISTS();
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 강제 개찰 처리 가능여부 정합성 체크<br><br>
	 * 
	 * @param rfxData - 견적요청 마스터
	 * @param rfxPreInspData - 사전심사 정보
	 * @return ResultMap
	 */
	public ResultMap bypassOpenRfx(Map rfxData, Map rfxPreInspData) {
		// 견적요청 진행상태
		String rfxProgSts = (String) rfxData.get("rfx_sts_ccd");
		// 견적요청 종료상태
		String rfxEndSts = (String) rfxData.get("rfx_res_sts_ccd");
		// 견적요청 유형
		String rfxTyp = (String) rfxData.get("rfx_typ_ccd");
		// 비가격평가 종료 여부
		String npeStsCcd = (String) rfxData.get("npe_sts_ccd");
		// 개찰시간 전 여부 : N인 경우 개찰시간 전
		String checkRfxOpenTimeYn = (String) rfxData.get("check_rfx_open_time_yn");
		
		// 견적요청 진행상태가 '마감'이 아닌 경우
		if(!"CLSG".equals(rfxProgSts)) {
			return ResultMap.INVALID();
		}
		// 견적요청 종료상태가 '개찰전'이 아닌 경우
		if(!"OPEN_WTG".equals(rfxEndSts)) {
			return ResultMap.INVALID();
		}
		// RFP이면서 비가격평가 종료되지 않은 경우
		if("RFP".equals(rfxTyp) && !"EVAL_ED".equals(npeStsCcd)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFP_EVAL_INCOMPELETED)
							.build();
		}
		// 즉시개찰이 아니고 서버시간 기준 개찰시간 전인 경우
		if("N".equals(checkRfxOpenTimeYn)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_BEFORE_OPEN_TIME)
							.build();
		}
		
		// RFx사전심사 사용중일때
		if(rfxPreInspData != null) {
			// 사전심사 사용 여부
			String rfxPresnUseYn = (String) rfxPreInspData.get("rfx_presn_use_yn");
			// 사전심사 완료 여부
			String rfxPreInspCompYn = (String) rfxPreInspData.get("rfx_presn_cmpld_yn");
			// 심사완료가 안되면 개찰 불가
			if("Y".equals(rfxPresnUseYn) && "N".equals(rfxPreInspCompYn)) {
				return ResultMap.builder()
								.resultStatus(RfxConst.NO_COMP_RFX_PRE_INSP)
								.build();
			}
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap saveRfxBidForAgent(Map rfxBid) {
		// 견적요청 진행상태
		String rfxProgSts = (String) rfxBid.get("rfx_sts_ccd");
		// 견적서 진행상태
		String progSts = (String) rfxBid.get("rfx_bid_sts_ccd");
		
		// 견적요청 진행상태가 '진행중'이 아닌 경우
		if(!"PRGSG".equals(rfxProgSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_END)
							.build();
		}
		// '견적 포기'건인 경우
		if("GUP".equals(progSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_REJECT)
							.build();
		}
		// '견적 제출'건 존재하는 경우
		if("SUBM".equals(progSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.QTA_SUBMIT)
							.build();
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap submitRfxBidForAgent(Map rfxBid) {
		// 견적요청 진행상태
		String rfxProgSts = (String) rfxBid.get("rfx_sts_ccd");
		// 견적서 진행상태
		String progSts = (String) rfxBid.get("rfx_bid_sts_ccd");
		
		// 견적요청 진행상태가 '진행중'이 아닌 경우
		if(!"PRGSG".equals(rfxProgSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_END)
							.build();
		}
		// '견적 포기'건인 경우
		if("GUP".equals(progSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_REJECT)
							.build();
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap abandonRfxBidForAgent(Map rfxBid) {
		// 견적요청 진행상태
		String rfxProgSts = (String) rfxBid.get("rfx_sts_ccd");
		// 견적서 진행상태
		String progSts = (String) rfxBid.get("rfx_bid_sts_ccd");
		
		// 견적요청 진행상태가 '진행중'이 아닌 경우
		if(!"PRGSG".equals(rfxProgSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_END)
							.build();
		}
		// '견적 포기'건인 경우
		if("GUP".equals(progSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_REJECT)
							.build();
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 견적 작성 시 기존 작성된 내역이 아닌 신규 수정(추가)건에 대해서 validation 처리
	 * @param rfxBid
	 * @return
	 */
	public ResultMap saveRfxQta(Map rfxBid) {
		String curCcd = rfxBid.get("cur_ccd") == null ? "" : rfxBid.get("cur_ccd").toString();

		if(StringUtils.isNotEmpty(curCcd)) {
			boolean validCur = false;
			List<Map> curCodes = sharedService.findCommonCode("C004");
			for(Map curCode : curCodes) {
				String curCd = (String) curCode.get("data");
				if(curCd.equals(curCcd)) {
					validCur = true;
					break;
				}
			}
			if(!validCur) {
				ResultMap resultMap = ResultMap.getInstance();
				resultMap.setResultData(rfxBid);
				resultMap.setResultStatus(ResultMap.STATUS.FAIL);
				resultMap.setResultMessage(messageSource.getMessage("STD.MW0003", new Object[]{curCcd},  LocaleContextHolder.getLocale()));
				return resultMap;
			}else{
				return ResultMap.SUCCESS();
			}
		}

		return ResultMap.SUCCESS();
	}
	
	public ResultMap forceCloseRfxEval(Map param) {
		// 견적요청 진행상태
		String rfxProgSts = (String) param.get("rfx_sts_ccd");
		// 비가격평가 진행상태
		String npeStsCcd = (String) param.get("npe_sts_ccd");
		
		if(!"CLSG".equals(rfxProgSts)) {
			return ResultMap.INVALID();
		}
		if(!"EVAL_PRGSG".equals(npeStsCcd)) {
			return ResultMap.INVALID();
		}
		
		return ResultMap.SUCCESS();
	}
}