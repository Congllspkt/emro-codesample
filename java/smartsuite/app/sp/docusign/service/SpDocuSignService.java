package smartsuite.app.sp.docusign.service;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.ktnet.ets.hub.common.util.StringUtil;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.shared.DocusignConst;
import smartsuite.app.shared.SignOrder;
import smartsuite.app.sp.contract.common.service.SpContractService;
import smartsuite.app.sp.docusign.repository.SpDocuSignRepository;
import smartsuite.app.sp.edoc.contract.SpEcontractService;
import smartsuite.app.util.DocuSignUtil;

/**
 * DocuSign 관련 처리하는 서비스 Class입니다.
 *
 * @FileName SpDocuSignService.java
 */
@Service
@Transactional
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SpDocuSignService {
	private static final Logger LOG = LoggerFactory.getLogger(SpDocuSignService.class);

	@Inject
	SpDocuSignRepository spDocuSignRepository;
	@Inject
	DocuSignUtil docuSignUtil;
	@Inject
	SpContractService spContractService;
	@Inject
	SpEcontractService spEcontractService;

	@Value("#{docusign['docusign.email']}")
	private String email;


	/**
	 * Docusign Envelope 조회
	 * @param param
	 * @return
	 */
	public ResultMap findDocusignEnvelope(Map param) {
		param.put("cntrr_typ_ccd", DocusignConst.SUPPLIER_USR_CLS);

		Map recipientInfo = spDocuSignRepository.findRecipientInfo(param);
		String envelopeId = (String) recipientInfo.get("dsgn_cntrdoc_ref_uuid");
		if (StringUtil.isEmpty(envelopeId)) {
			return ResultMap.NOT_EXISTS();
		}

		Map compInfo = spDocuSignRepository.findCompInfo();
		String spCompNm = (String) compInfo.get("sp_comp_nm");
		String spCompEmail = (String) compInfo.get("sp_comp_email");
		
		recipientInfo.put("usr_nm", spCompNm);
		recipientInfo.put("email", spCompEmail);
		recipientInfo.put("return_ui_id", param.get("return_ui_id"));
		recipientInfo.put("user_type", "sp");

		docuSignUtil.settingAuthentication();
		Map resultData = docuSignUtil.createRecipientView(recipientInfo);

		// 전자서명 lock
		this.updateSignedLockStatus(param);

		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 서명잠금상태 lock
	 * @param param
	 * @return
	 */
	private void updateSignedLockStatus(Map param) {
		Map<String, Object> docusignInfo = spDocuSignRepository.findDocusignInfo(param);
		spEcontractService.controlSignatureProgress(docusignInfo);
	}
	
	/**
	 * docusign envelope popup에서 발생한 이벤트 후 처리 함수
	 * @param event
	 * @param docusignId
	 * @return
	 */
	public Map returnRecipientView(String event, String docusignId) {
		Map paramMap = Maps.newHashMap();
		paramMap.put("dsgn_uuid", docusignId);
		Map docusignInfo = spDocuSignRepository.findDocusignInfo(paramMap);
		
		String ecntrId = (String) docusignInfo.get("ecntr_uuid");
		String cntrId = (String) docusignInfo.get("cntr_uuid");

		if(DocusignConst.SIGNING_COMPLETE.equals(event)) { // 서명완료
			try {
				String envelopeId = (String) docusignInfo.get("dsgn_cntrdoc_ref_uuid");
				String orgnFileNm = docusignInfo.get("cntr_no") + "-" + docusignInfo.get("cntr_revno") + ".pdf";

				docuSignUtil.settingAuthentication();
				String fileGrpCd = docuSignUtil.downloadEnvelopeDocument(envelopeId, orgnFileNm);
				
				spEcontractService.updateSgncmpldCntrdoc(ecntrId, fileGrpCd);
				
			} catch (Exception e) {
				LOG.error(this.getClass() + "saveSignedPdf error : ", e);
			}

			this.completeDocuSgn(docusignId);
			spEcontractService.updateVendorSignY(ecntrId, null, null);
			spContractService.completeContract(cntrId);
			
		} else if(DocusignConst.DECLINE.equals(event)) { // 거부
			paramMap.put("cntr_uuid", cntrId);
			spContractService.rejectContract(paramMap);
		}
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("cntr_uuid", cntrId);
		resultMap.put("sgnord_typ_ccd", SignOrder.BUYER_VD.toString());
		return resultMap;
	}

	/**
	 * docusign 서명완료 후 진행 상태 업데이트
	 * @param ecntrId
	 * @param athgId
	 */
	private void completeDocuSgn(String docusignId) {
		Map docusignInfo = Maps.newHashMap();
		docusignInfo.put("dsgn_uuid", docusignId);
		docusignInfo.put("dsgn_sts_ccd", DocusignConst.SIGNING_COMPLETE);
		spDocuSignRepository.updateDocusignStatus(docusignInfo);
	}
	
}
