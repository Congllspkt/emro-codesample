package smartsuite.app.sp.rfx.rfx.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.sp.rfx.rfx.repository.SpRfxBidRepository;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class SpRfxValidator {

	@Inject
	SpRfxBidRepository spRfxBidRepository;

	@Inject
	SharedService sharedService;

	@Inject
	MessageSource messageSource;
	
	protected String getSessionVendorCode(){
		if(Auth.getCurrentUserInfo() == null)
			return null;
		else
			return String.valueOf(Auth.getCurrentUserInfo().get("vd_cd"));
	}
	
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
	
	public ResultMap isSameVendor(Map param) {
		String vdCd = (String) param.get("vd_cd");
		
		if(vdCd == null) {
			return ResultMap.FAIL();
		}
		if(!this.getSessionVendorCode().equals(vdCd)) {
			return ResultMap.FAIL();
		}
		return ResultMap.SUCCESS();
	}
	
	public ResultMap saveRfxBid(Map rfxBid) {
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
		
		// 컨소시엄 여부: "Y"인 경우
		if("Y".equals(rfxBid.get("cstm_use_yn"))) {
			// 협정서 존재 확인
			Map checkCsData = spRfxBidRepository.checkRfxCsVds(rfxBid);
			if(checkCsData != null) {
				// 구성업체인 경우
				if("N".equals(checkCsData.get("rep_vd_yn"))) {
					// 대표업체가 아니므로 견적서 작성이 불가함
					return ResultMap.builder()
									.resultStatus(RfxConst.CS_NON_REP_VENDOR)
									.build();
				
				// 대표업체인 경우, 아직 협정서를 발송하지 않았거나 일부 구성업체가 승인을 하지 않은 경우
				} else if("N".equals(checkCsData.get("cstm_ptcp_req_snd_yn")) || "N".equals(checkCsData.get("all_sbmt_yn"))) {
					// 공동수급협정서를 완료하지 않아 견적서 작성이 불가함
					return ResultMap.builder()
									.resultStatus(RfxConst.CS_INCOMPELETED)
									.build();
				}
			}
		}

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
	
	public ResultMap submitRfxBid(Map rfxBid) {
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
	
	public ResultMap abandonRfxBid(Map rfxBid) {
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
}