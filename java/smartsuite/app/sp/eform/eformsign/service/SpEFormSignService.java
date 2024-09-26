package smartsuite.app.sp.eform.eformsign.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import smartsuite.app.bp.eform.eformsign.service.EFormSignService;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.status.ContractStatusService;
import smartsuite.app.sp.contract.common.service.SpContractService;
import smartsuite.app.sp.edoc.contract.SpEcontractService;
import smartsuite.app.sp.eform.eformsign.repository.SpEFormSignRepository;
import smartsuite.app.util.PdfMakerJsoup;
import smartsuite.exception.CommonException;
import smartsuite.security.authentication.Auth;

/**
 * 간편서명 관련 처리하는 서비스 Class입니다.
 *
 * @FileName SpEFormSignService.java
 */
@Service
@Transactional
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SpEFormSignService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SpEFormSignService.class);

	@Inject
	SpEFormSignRepository spEFormSignRepository;
	@Inject
	EFormSignService eFormSignService;
	@Inject
	SpContractService spContractService;
	@Inject
	SpEcontractService spEcontractService;
	@Inject
	PdfMakerJsoup pdfMakerJsoup;
	@Inject
	MailService mailService;
	@Inject
	ContractStatusService contractStatusService;

	
	/**
	 * 간편서명 을 서명정보 조회 (Mail 서명)
	 * 
	 * @param param
	 * @return
	 */
	public ResultMap findSignTargetInfoFormerNByMail(Map param) {
		Map<String, Object> cntrInfo = spEFormSignRepository.findCntrInfoByCntorId(param);
		if (cntrInfo == null) {
			return ResultMap.FAIL();
		}
		
		Map userInfo = Auth.getCurrentUserInfo();
		if(userInfo != null) {
			String usrId = (String) userInfo.get("usr_id");
			String signerId = (String) cntrInfo.get("usr_id");
			
			if (Strings.isNullOrEmpty(signerId)) {
				// ID/PW 로그인인데 세션ID 남아있을 때
				return ResultMap.builder()
						.resultStatus(ResultMap.STATUS.FAIL)
						.resultMessage("STD.EDO2089") // 로그인된 동일 브라우저 사용자가 있습니다. 세션이 만료되었으니 재시도 해주세요.
						.build();
			} else if (!signerId.equals(usrId)) {
				// ID/PW 로그인 한 사람(세션ID)이 계약대상자의 ID와 맞지 않을 때
				return ResultMap.FAIL();
			}
		}

		String ecntrId = (String)cntrInfo.get("ecntr_uuid");
		String vendorsSignState = spEcontractService.checkVendorsSignState(ecntrId);
		if (CntrConst.VENDORS_SIGN_STATUS.ALL_SIGN.equals(vendorsSignState)) { // 서명 대상자 모두 서명 완료
			return ResultMap.builder()
					.resultStatus(CntrConst.VENDORS_SIGN_STATUS.ALL_SIGN)
					.resultMessage("STD.EDO2025") //서명이 완료된 건 입니다.
					.build();
		}

		// 계약진행상태:전송(SD), 다자간서명(SI) - 서명가능한 상태
		String cntrProgSts = (String) cntrInfo.get("cntr_sts_ccd");
		if (!(CntrConst.CNTR_STATUS.SEND.equals(cntrProgSts)
				|| CntrConst.CNTR_STATUS.VENDOR_CONFIRM.equals(cntrProgSts)
				|| CntrConst.CNTR_STATUS.MULTILATERAL_SIGN.equals(cntrProgSts) )) {
			return ResultMap.builder()
					.resultStatus(ResultMap.STATUS.FAIL)
					.resultMessage("STD.EDO2087") // 서명할 수 있는 상태가 아닙니다.
					.build();
		}

		// 간편서명 계약서 내용
		Map documentConts = spEFormSignRepository.findDocumentConts(cntrInfo);
		// '을' 서명 대상자 검색
		Map signTarget = spEFormSignRepository.findDocumentSignTarget(cntrInfo);
		// 서명 타겟에 맞는 Json Data 추출
		List<Map> jsonList = eFormSignService.findSignTargetFromJsonData(documentConts, signTarget);

		Map resultData = Maps.newHashMap();
		resultData.put("cntrInfo", cntrInfo);
		resultData.put("documentConts", documentConts);
		resultData.put("signTarget", signTarget);
		resultData.put("signJsonList", jsonList);
		return ResultMap.SUCCESS(resultData);
		
	}
	
	/**
	 * 간편서명 을 서명대상 조회
	 * 
	 * @param param
	 * @return
	 */
	public ResultMap findSignTargetInfoFormerN(Map param) {
		// 계약 정보
		Map cntrInfo = spEcontractService.findContractByEcntrId(param);
		
		// '을' 서명완료 여부 확인
		String ecntrId = (String) cntrInfo.get("ecntr_uuid");
		String vendorsSignState = spEcontractService.checkVendorsSignState(ecntrId);
		if (CntrConst.VENDORS_SIGN_STATUS.ALL_SIGN.equals(vendorsSignState)) { // 서명 대상자 모두 서명 완료
			return ResultMap.builder()
					.resultStatus(CntrConst.VENDORS_SIGN_STATUS.ALL_SIGN)
					.resultMessage("STD.EDO2025") //서명이 완료된 건 입니다.
					.build();
		}

		// 계약진행상태:전송(SD), 다자간서명(SI) - 서명가능한 상태
		String cntrProgSts = (String) cntrInfo.get("cntr_sts_ccd");
		if (!(CntrConst.CNTR_STATUS.SEND.equals(cntrProgSts)
				|| CntrConst.CNTR_STATUS.VENDOR_CONFIRM.equals(cntrProgSts)
				|| CntrConst.CNTR_STATUS.MULTILATERAL_SIGN.equals(cntrProgSts) )) {
			return ResultMap.builder()
					.resultStatus(ResultMap.STATUS.FAIL)
					.resultMessage("STD.EDO2087") // 서명할 수 있는 상태가 아닙니다.
					.build();
		}
				
		// 간편서명 계약서 내용
		Map documentConts = spEFormSignRepository.findDocumentConts(param);
		// '을' 서명 대상자 검색
		Map signTarget = spEFormSignRepository.findDocumentSignTarget(cntrInfo);
		// 서명 타겟에 맞는 Json Data 추출
		List<Map> jsonList = eFormSignService.findSignTargetFromJsonData(documentConts, signTarget);
		// 전자서명 lock
		spEcontractService.controlSignatureProgress(param);

		Map resultData = Maps.newHashMap();
		resultData.put("cntrInfo", cntrInfo);
		resultData.put("documentConts", documentConts);
		resultData.put("signTarget", signTarget);
		resultData.put("signJsonList", jsonList);
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * 간편서명 서명 거부
	 * 
	 * @param param
	 * @return
	 */
	public ResultMap rejectSignDocument(Map param) {
		Map signTarget = (Map) param.get("signTarget");
		Map cntrInfo = spEFormSignRepository.findCntrInfoByCntorId(signTarget);

		if(cntrInfo != null && "N".equals(cntrInfo.get("sgn_yn"))) {
			cntrInfo.put("cntr_ret_rsn", param.get("cntr_ret_rsn"));
			spContractService.rejectContract(cntrInfo);
			return ResultMap.SUCCESS();
		} else {
			return ResultMap.FAIL();
		}
	}
	
	/**
	 * 간편서명 서명 제출 및 다음 서명 대상자에게 발송
	 * 
	 * @param param
	 * @return
	 */
	public ResultMap sendNextSignTarget(Map param) {
		String ecntrId = (String) param.get("ecntr_uuid");
		Map signTarget = (Map) param.get("signTarget");
		String cntrFormAttr = (String) param.get("cntrFormAttr");
		
		Map paramMap = Maps.newHashMap();
		paramMap.put("ecntr_uuid", ecntrId);
		Map cntrInfo = spEFormSignRepository.findCntrInfoByCntorId(signTarget);
		
		// 1. 서명정보 json 합친 pdf 생성 및 저장
		paramMap.put("cntr_no", cntrInfo.get("cntr_no"));
		paramMap.put("cntr_revno", cntrInfo.get("cntr_revno"));
		paramMap.put("dgtlsgn_cntrdoc_tmpl_athg_uuid", cntrInfo.get("sgncmpld_cntrdoc_athg_uuid"));
		paramMap.put("dgtlsgn_cntrdoc_lyt_attr", cntrFormAttr);

		Map pdfInfo = eFormSignService.makeEFormDocPdf(paramMap);
		
		if(!Const.SUCCESS.equals(pdfInfo.get(Const.RESULT_STATUS))) {
			return ResultMap.FAIL();
		}
		
		// 2. pdf 파일 계약정보에 업데이트
		spEcontractService.updateSgncmpldCntrdoc(ecntrId, (String) pdfInfo.get("athg_uuid"));

		// 3. 서명값 vector이미지 가져와서 저장
		Map signValueInfo = Maps.newHashMap();
		signValueInfo.put("joint_cert_sgn_val_uuid", UUID.randomUUID().toString());
		signValueInfo.put("ecntr_uuid", ecntrId);
		signValueInfo.put("bizregno", signTarget.get("cntr_sgndusr_uuid"));
		signValueInfo.put("sgndusr_typ_ccd", "VD");
		signValueInfo.put("usr_id", signTarget.get("cntr_sgndusr_uuid"));

		if (signTarget.get("signImageYn") != null && "Y".equals(signTarget.get("signImageYn"))) { // SVG일때
			String signImage = "data:image/svg+xml;base64," + signTarget.get("signImage").toString();
			signValueInfo.put("sgn_val", signImage);
			
		} else if (signTarget.get("signImageYn") != null && "N".equals(signTarget.get("signImageYn"))) { // 이미지일때
			signValueInfo.put("sgn_val", signTarget.get("signImage"));
		}
		
		spEFormSignRepository.insertEFormSignValue(signValueInfo);

		// 4. 서명자 서명완료로 update
		spEcontractService.updateVendorSignY(ecntrId, (String) signTarget.get("cntr_sgndusr_uuid"), null);

		// 5. 다음 서명자 확인 후 알림 발송, 업체서명 모두 완료면 상태 update
		String nextCntrStsCcd = spEcontractService.getNextCntrStatus(ecntrId);
		String cntrId = (String) cntrInfo.get("cntr_uuid");
		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", cntrId);
		statusParam.put("cntr_sts_ccd", nextCntrStsCcd);
		statusParam.put("cntr_sgndusr_uuid", signTarget.get("cntr_sgndusr_uuid"));
		contractStatusService.completeVendorSign(statusParam);
		spContractService.insertHistory(statusParam);
		
		if(CntrConst.CNTR_STATUS.VENDOR_SIGN_COMPLETE.equals(nextCntrStsCcd)) { // 서명 대상자 모두 서명 완료
			// 계약 완료 처리
			spContractService.completeContract(cntrId);
			// 감사추적 파일 생성
			this.createContractHistrecFile(cntrId, pdfInfo);
			// 계약 완료 메일 발송
			mailService.sendAsync("CNTR_AGRM_CFRM", null, cntrInfo);
			
		} else {
			// 서명대상자가 존재하면 메일/문자 발송
			Map nextSignTarget = spEFormSignRepository.findDocumentSignTarget(cntrInfo);
			if (nextSignTarget != null) {
				eFormSignService.sendMailEFormSignTarget(cntrInfo, nextSignTarget);
			}
		}
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 간편서명 감사추적 파일 생성
	 * @param cntrId
	 * @param fileInfo
	 */
	public void createContractHistrecFile(String cntrId, Map fileInfo) {
		Map cntrInfo = spEFormSignRepository.findCntrInfoForHistrecPdf(cntrId); // 계약파일 조회
		List<Map> cntrHisList = spEFormSignRepository.findListEFormCntrHistory(cntrInfo); // 계약히스토리(문서처리내역 - 1.작성, 3.최종완료 내용)
		List<Map> signTargetList = spEFormSignRepository.findListSignTargetSignValue(cntrInfo); // 서명대상자 정보(문서처리내역 - 2. 외부자승인 내용)
		
		String creatorMail = cntrInfo.get("cntr_pic_eml") != null ? cntrInfo.get("cntr_pic_eml").toString() : "";
		String creatorInfo = cntrInfo.get("cntr_pic_nm").toString() + "(" + creatorMail + ")";
		cntrInfo.put("creator_info", creatorInfo); // 계약작성자 정보

		// 감사추적 PDF생성
		Map paramMap = Maps.newHashMap();
		paramMap.put("cntrInfo", cntrInfo);
		paramMap.put("signTargetList", signTargetList);
		paramMap.put("cntrHisList", cntrHisList);
		paramMap.put("fileInfo", fileInfo);
		Map pdfInfo = pdfMakerJsoup.makePdfJsoup(paramMap);
		
		// 감사추적 PDF 파일그룹코드 저장
		cntrInfo.put("cntr_histrec_athg_uuid", pdfInfo.get("athg_uuid"));
		spEFormSignRepository.updateCntrAtFile(cntrInfo);
	}
	
}