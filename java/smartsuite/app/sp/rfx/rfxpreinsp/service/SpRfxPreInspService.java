package smartsuite.app.sp.rfx.rfxpreinsp.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.ListUtils;
import smartsuite.app.sp.rfx.rfxpreinsp.repository.SpRfxPreInspRepository;
import smartsuite.app.sp.rfx.rfxpreinsp.validator.SpRfxPreInspValidator;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpRfxPreInspService {
	
	@Inject
	SpRfxPreInspRepository spRfxPreInspRepository;
	
	@Inject
	SpRfxPreInspValidator spRfxPreInspValidator;
	
	@Inject
	MailService mailService;
	
	@Inject
	RfxEventPublisher rfxEventPublisher;
	
	/**
	 * vdcd의 값을 반환한다.
	 *
	 * @return vdcd
	 */
	protected String getSessionVendorCode() {
		if(Auth.getCurrentUserInfo() == null)
			return null;
		else
			return String.valueOf(Auth.getCurrentUserInfo().get("vd_cd"));
	}
	
	public List findListRfxPreInsp(Map param) {
		return spRfxPreInspRepository.findListRfxPreInsp(param);
	}
	
	public Map findRfxPreInspApp(Map param) {
		String rfxPreInspAppId = (String) param.get("rfx_presn_afp_uuid");
		
		List attachList = Lists.newArrayList();
		if(Strings.isNullOrEmpty(rfxPreInspAppId)) {
			attachList = spRfxPreInspRepository.findListRfxPreInspAppAttach(param);
		} else {
			boolean isExistsAttach = spRfxPreInspRepository.isExistRfxPreInspAppAttach(param);
			if(isExistsAttach) {
				attachList = spRfxPreInspRepository.findListRfxPreInspAppedAttach(param);
			}
		}
		
		List<Map> rfxItemList = spRfxPreInspRepository.findListRfxItem(param);
		
		List<String> prItemUuids = ListUtils.getArrayValuesByList(rfxItemList, "pr_item_uuid");
		List<Map> prInfos = rfxEventPublisher.findListPrByPrItems(prItemUuids);
		rfxItemList = ListUtils.leftOuterJoin(rfxItemList, prInfos, Lists.newArrayList("pr_item_uuid"), Lists.newArrayList("pr_uuid"));
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxPreInspInfo", spRfxPreInspRepository.findRfxPreInspApp(param));
		resultMap.put("vendorInfo", spRfxPreInspRepository.findInfoVendor(param));
		resultMap.put("attachList", attachList);
		resultMap.put("rfxData", spRfxPreInspRepository.findRfxData(param));
		resultMap.put("rfxItemList", rfxItemList);
		
		return resultMap;
	}
	
	public ResultMap saveRfxPreInspApp(Map param) {
		//협력사 코드 체크
		if(getSessionVendorCode() == null) {
			return ResultMap.FAIL();
		}
		
		Map rfxPreInspAppData = (Map) param.get("rfxPreInspAppData");
		List attachList = (List) param.get("attachList");
		
		//사전심사시간 체크
		ResultMap validator = spRfxPreInspValidator.validate(rfxPreInspAppData);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		Map rfxPreInspAppInfo = spRfxPreInspRepository.findRfxPreInspApp(rfxPreInspAppData);
		
		this.saveRfxPreInspAppData(rfxPreInspAppData, rfxPreInspAppInfo);
		// 첨부파일 저장 
		this.saveRfxPreInspAppAttach(rfxPreInspAppData, attachList);
		
		String sbmtSts = (String) rfxPreInspAppData.get("subm_sts_ccd");
		String infoSbmtSts = (String) rfxPreInspAppInfo.get("subm_sts_ccd");
		
		if("SUBM".equals(sbmtSts)) {
			// 제출시 메일 발송
			mailService.sendAsync("RFX_PRE_INSP_BP", null, rfxPreInspAppData);
		}
		
		ResultMap resultMap = ResultMap.getInstance();
		if("SUBM".equals(sbmtSts) && "SUBM".equals(infoSbmtSts)) {
			// 제출 건 다시 제출 시
			resultMap.setResultStatus(RfxConst.RESUBMIT);
		} else {
			resultMap.setResultStatus(Const.SUCCESS);
		}
		
		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("rfx_presn_uuid", rfxPreInspAppData.get("rfx_presn_uuid"));
		resultDataMap.put("rfx_presn_afp_uuid", rfxPreInspAppData.get("rfx_presn_afp_uuid"));
		resultDataMap.put("rfx_uuid", rfxPreInspAppData.get("rfx_uuid"));
		resultMap.setResultData(resultDataMap);
		return resultMap;
	}
	
	private void saveRfxPreInspAppData(Map rfxPreInspAppData, Map rfxPreInspAppInfo) {
		if(rfxPreInspAppData == null) {
			return;
		}
		if(rfxPreInspAppInfo == null) {
			return;
		}
		
		String rfxPreInspAppId = (String) rfxPreInspAppInfo.get("rfx_presn_afp_uuid");
		if(Strings.isNullOrEmpty(rfxPreInspAppId)) {
			rfxPreInspAppData.put("rfx_presn_afp_uuid", UUID.randomUUID().toString());
			spRfxPreInspRepository.insertRfxPreInspApp(rfxPreInspAppData);
		} else {
			spRfxPreInspRepository.updateRfxPreInspApp(rfxPreInspAppData);
		}
	}
	
	public void saveRfxPreInspAppAttach(Map rfxPreInspAppData, List attachList) {
		if(rfxPreInspAppData == null) {
			return;
		}
		if(attachList == null) {
			return;
		}
		if(attachList.isEmpty()) {
			return;
		}
		
		for(Map attach : (List<Map>) attachList) {
			String sbmtDocId = (String) attach.get("rfx_presn_afp_submddoc_uuid");
			if(Strings.isNullOrEmpty(sbmtDocId)) {
				attach.put("rfx_presn_afp_submddoc_uuid", UUID.randomUUID().toString());
				attach.put("rfx_presn_afp_uuid", rfxPreInspAppData.get("rfx_presn_afp_uuid"));
				attach.put("vd_cd", rfxPreInspAppData.get("vd_cd"));
				attach.put("rfx_presn_uuid", rfxPreInspAppData.get("rfx_presn_uuid"));
				attach.put("rfx_no", rfxPreInspAppData.get("rfx_no"));
				attach.put("rfx_rnd", rfxPreInspAppData.get("rfx_rnd"));
				
				spRfxPreInspRepository.insertRfxPreInspAppAttach(attach);
			} else {
				spRfxPreInspRepository.updateRfxPreInspAppAttach(attach);
			}
		}
	}
	
	public ResultMap deleteRfxPreInspApp(Map param) {
		//협력사 코드 체크
		if(getSessionVendorCode() == null) {
			return ResultMap.FAIL();
		}
		
		Map rfxPreInspAppData = (Map) param.get("rfxPreInspAppData");
		Map rfxPreInspAppInfo = spRfxPreInspRepository.findRfxPreInspApp(rfxPreInspAppData);
		ResultMap validator = spRfxPreInspValidator.deleteRfxPreInspApp(rfxPreInspAppInfo);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		spRfxPreInspRepository.deleteRfxPreInspAttach(rfxPreInspAppInfo);
		spRfxPreInspRepository.deleteRfxPreInspApp(rfxPreInspAppInfo);
		
		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("rfx_presn_uuid", rfxPreInspAppData.get("rfx_presn_uuid"));
		resultDataMap.put("rfx_presn_afp_uuid", rfxPreInspAppData.get("rfx_presn_afp_uuid"));
		resultDataMap.put("rfx_uuid", rfxPreInspAppData.get("rfx_uuid"));
		return ResultMap.SUCCESS(resultDataMap);
	}
	
	public ResultMap checkValidRfxPreInspAppReg(Map param) {
		//협력사 코드 체크
		if(getSessionVendorCode() == null) {
			return ResultMap.FAIL();
		}
		
		//사전심사시간 체크
		ResultMap validator = spRfxPreInspValidator.validate(param);
		if(!validator.isSuccess()) {
			return validator;
		}
		return ResultMap.SUCCESS(param);
	}
	
	public ResultMap checkRfxPreInspCompYByRfx(Map param) {
		//마감이고 제출이 없는경우 NOT VALID
		/*Map checkResult = spRfxPreInspRepository.validRfxPreInspCloseStsByRfxId(param);
		if(checkResult == null) {
			//마감이고 제출안됨
			return ResultMap.builder()
			                .resultStatus(RfxConst.CLOSE_RFX_PRE_INSP)
			                .resultData(param)
			                .build();
		}*/
		//rfx 종료여부 체크

		//견적서 제출 통과 여부 체크
		Map checkValid = spRfxPreInspRepository.checkValidRfxPreInspApp(param);
		if(checkValid == null) {
			//심사
			return ResultMap.builder()
			                .resultStatus(RfxConst.BEFORE_RFX_PRE_INSP)
			                .resultData(param)
			                .build();
		}
		
		String validYn = (String) checkValid.get("presn_res_ccd");
		if("DQ".equals(validYn)) {
			return ResultMap.builder()
			                .resultStatus(RfxConst.NO_PASS)
			                .resultData(param)
			                .build();
		}
		if(Strings.isNullOrEmpty(validYn)) {
			return ResultMap.builder()
			                .resultStatus(RfxConst.BEFORE_RFX_PRE_INSP)
			                .resultData(param)
			                .build();
		}
		
		return ResultMap.SUCCESS(param);
	}
	
}
