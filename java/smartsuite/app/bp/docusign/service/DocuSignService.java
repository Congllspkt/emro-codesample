package smartsuite.app.bp.docusign.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.docusign.esign.model.Document;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ktnet.ets.hub.common.util.StringUtil;

import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.bp.docusign.repository.DocuSignRepository;
import smartsuite.app.bp.edoc.contract.service.EcontractService;
import smartsuite.app.bp.edoc.pdfmaker.HtmlToPdfMaker;
import smartsuite.app.bp.edoc.template.service.ClauseService;
import smartsuite.app.bp.edoc.template.service.MainTemplateService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.DocumentMgt;
import smartsuite.app.shared.DocusignConst;
import smartsuite.app.shared.SignOrder;
import smartsuite.app.util.DocuSignUtil;
import smartsuite.exception.CommonException;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;

/**
 * Docusign 관련 처리하는 서비스 Class입니다.
 *
 * @FileName DocuSignService.java
 */
@Service
@Transactional
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DocuSignService {
	private static final Logger LOG = LoggerFactory.getLogger(DocuSignService.class);

	@Inject
	DocuSignRepository docuSignRepository;
	@Inject
	DocuSignUtil docuSignUtil;
	@Inject
	SharedService sharedService;
	@Inject
	StdFileService fileService;
	@Inject
	ContractService contractService;
	@Inject
	EcontractService econtractService;
	@Inject
	HtmlToPdfMaker htmlToPdfMaker;

	@Value("#{docusign['docusign.email']}")
	private String email;


	/**
	 * 계약번호로 docusign 정보 조회
	 * 
	 * @param param
	 * @return
	 */
	public Map findDocusignInfoByEcntrId(Map param) {
		if (StringUtil.isEmpty((String) param.get("ecntr_uuid"))) {
			return null;
		}
		return docuSignRepository.findDocusignInfoByEcntrId(param);
	}

	/**
	 * Docusign 계약서 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteDocusignContract(Map param) {
		Map docusignInfo = docuSignRepository.findDocusignInfoByEcntrId(param);
		if(docusignInfo == null) {
			return ResultMap.NOT_EXISTS();
		}

		docuSignRepository.deleteDocusignUserInfo(docusignInfo);
		docuSignRepository.deleteDocusignInfo(docusignInfo);
		return ResultMap.SUCCESS();
	}

	/**
	 * Docusign Template 생성
	 * @param param
	 * @return
	 */
	public ResultMap createDocusignTemplate(Map param) {
		/** 1. 사용 할 계약 정보 조회 **/
		Map cntrInfo = econtractService.findContractByEcntrId(param); //계약정보 조회

		/** 2. 계약서내용 및 첨부서류를 docusign document로 생성 **/
		List<DocumentMgt> documentMgtList = Lists.newArrayList();
		Document cntrDoc = null;
		String cntrNm = (String) cntrInfo.get("cntr_nm");
		String tmplTypCcd = (String) cntrInfo.get("cntr_tmpl_typ_ccd");

		if(CntrConst.TMPL_TYPE.TEMPLATE.equals(tmplTypCcd)) {
			Map cntrFormInfo = econtractService.getCntrContent(cntrInfo); //계약서 내용 조회
			String content = (String) cntrFormInfo.get("content");
			cntrDoc = this.makeDocument(content, cntrNm);

		} else if(CntrConst.TMPL_TYPE.USR_FILE.equals(tmplTypCcd)) {
			String cntrdocAthgId = (String) cntrInfo.get("tmpl_unud_cntrdoc_athg_uuid");
			FileList fileList = null;
			try {
				fileList = fileService.findFileListWithContents(cntrdocAthgId);
			} catch (Exception e) {
				LOG.error(this.getClass().getName() + ".createDocusignTemplate() : Exception", e);
				throw new CommonException("STD.E9999");	//오류가 발생하였습니다.<br/>관리자에게 문의하세요.
			}
			cntrDoc = this.makeDocument(fileList.getItems().get(0));

		} else {
			return ResultMap.FAIL();
		}

		// docusign template list에 document 추가
		documentMgtList.add(new DocumentMgt(Integer.parseInt(DocusignConst.CNTR_ORDER_NUMBER), cntrDoc));

		// 첨부서류 목록 document로 변환
		List<DocumentMgt> appDocMgtList = null;
		if(CntrConst.TMPL_TYPE.TEMPLATE.equals(tmplTypCcd)) {
			try {
				appDocMgtList = this.makeDocumentWithAppInfo(cntrInfo, DocusignConst.CNTR_ORDER_NUMBER);
			} catch (Exception e) {
				LOG.error(this.getClass() + "makeDocumentWithAppInfo error : ", e);
				return ResultMap.FAIL();
			}
		} else if(CntrConst.TMPL_TYPE.USR_FILE.equals(tmplTypCcd)) {
			try {
				String appxAthgId = (String) cntrInfo.get("tmpl_unud_appx_athg_uuid");
				appDocMgtList = this.makeDocumentWithAppInfo(appxAthgId, DocusignConst.CNTR_ORDER_NUMBER);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		documentMgtList.addAll(appDocMgtList);

		/** 3. docusign template 등록 **/
		Map docusignTempInfo = Maps.newHashMap();
		docusignTempInfo.put("template_name", cntrNm);
		docusignTempInfo.put("bp_email", email);
		this.setSignPosition(docusignTempInfo, cntrDoc); // 서명위치 설정

		// 수신자 정보 조회 (구매사)
		Map buyerInfo = docuSignRepository.findCompInfo();
		//계약자정보 조회
		List<Map> supplierList = econtractService.getSupplierList(cntrInfo);

		// docusign api 사용하기 위한 인증절차
		docuSignUtil.settingAuthentication();
		// template 생성 요청
		Map templateResult = docuSignUtil.createTemplate(docusignTempInfo, buyerInfo, supplierList, documentMgtList);

		/** 4. docusign master 데이터 생성 **/
		Map docusignInfo = Maps.newHashMap();
		String docusignId = UUID.randomUUID().toString();
		docusignInfo.put("dsgn_uuid", docusignId);
		docusignInfo.put("dsgn_no", sharedService.generateDocumentNumber("DS"));
		docusignInfo.put("dsgn_tmpl_ref_uuid", templateResult.get("dsgn_tmpl_ref_uuid"));
		docusignInfo.put("ecntr_uuid", cntrInfo.get("ecntr_uuid"));
		docusignInfo.put("cntrdoc_tmpl_uuid", cntrInfo.get("cntrdoc_tmpl_uuid"));
		docusignInfo.put("cntr_nm", cntrNm);
		docusignInfo.put("dsgn_sts_ccd", DocusignConst.DS_BUYER_CREATE_TEMPLATE);
		docusignInfo.put("vd_cd", cntrInfo.get("vd_cd"));
		docusignInfo.put("return_ui_id", param.get("return_ui_id"));
		docuSignRepository.insertDocusignInfo(docusignInfo);

		/** 5. docusign user 데이터 생성 **/
		buyerInfo.put("dsgn_uuid", docusignId);
		buyerInfo.put("cntrr_typ_ccd", DocusignConst.BUYER_USR_CLS);
		docuSignRepository.insertDocusignUserInfo(buyerInfo);

		for (Map spSigner : supplierList) {
			spSigner.put("dsgn_uuid", docusignId);
			spSigner.put("cntrr_typ_ccd", DocusignConst.SUPPLIER_USR_CLS);
			spSigner.put("cntrr_eml", spSigner.get("sp_rep_email"));
			docuSignRepository.insertDocusignUserInfo(spSigner);
		}

		Map resultData = Maps.newHashMap();
		resultData.put("dsgn_uuid", docusignId);
		return ResultMap.SUCCESS(resultData);
	}
	
	private Document makeDocument(String content, String cntrDocTitle) {
		String orgnFileNm = cntrDocTitle + ".pdf";
		// 계약서 html를 document로 변환
		content = DocuSignUtil.makeHtml(content);
		byte[] bContent = this.makePdf(content);
		// 계약서 html를 docusign document로 생성
		Document cntrDoc = docuSignUtil.makeDocument(DocusignConst.CNTR_ORDER_NUMBER, bContent, orgnFileNm);
		return cntrDoc;
	}

	private Document makeDocument(FileItem fileItem) {
		Document cntrDoc = null;
		String orgnFileNm = fileItem.getName();
		try {
			byte[] bContent = fileItem.toByteArray();
			cntrDoc = docuSignUtil.makeDocument(DocusignConst.CNTR_ORDER_NUMBER, bContent, orgnFileNm);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return cntrDoc;
	}

	/**
	 * html to pdf 변환
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	private byte[] makePdf(String content) {
		String pdfFullPath = htmlToPdfMaker.makePdf(content, false);
		File file = new File(pdfFullPath);
		byte[] pdfFileByte = new byte[0];
		try {
			pdfFileByte = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return pdfFileByte;
	}
	
	/**
	 * 계약서 첨부문서 리스트를 조회하여 docusign document로 리턴한다.
	 * 
	 * @param cntrInfo
	 * @param cntrOrderNumber
	 * @return
	 * @throws Exception 
	 */
	private List<DocumentMgt> makeDocumentWithAppInfo(Map cntrInfo, String cntrOrderNumber) throws Exception {
		List<Map> appList = econtractService.getCntrAppList(cntrInfo);
		List<DocumentMgt> documentMgtList = Lists.newArrayList();
		Document appDoc = null;
		int cnt = 100;

		for (int i = 0; i < appList.size(); i++) {
			cnt = cnt * (i+1);
			Map app = appList.get(i);
			if (app.get("cntr_appx_typ_ccd").equals("TXT")) {
				String appEditContent = (String) app.get("edit_content");

				String addFormTextIndex = app.get("cntr_appx_ord").toString();
				int nAddFormTextIndex = Integer.parseInt(addFormTextIndex) + Integer.parseInt(cntrOrderNumber) + cnt;

				byte[] bAppEditContent = this.makePdf(appEditContent);

				appDoc = docuSignUtil.makeDocument(Integer.toString(nAddFormTextIndex), bAppEditContent, app.get("cntr_appx_nm") + ".pdf");
				documentMgtList.add(new DocumentMgt(nAddFormTextIndex, appDoc));

			} else if (app.get("cntr_appx_typ_ccd").equals("FILE") || app.get("cntr_appx_typ_ccd").equals("FILE_LIST")) {
				FileList fileList = fileService.findFileListWithContents((String) app.get("athg_uuid"));
				int fileCnt = 0;
				for (FileItem fileItem : fileList.getItems()) {
					byte[] fileByte = fileItem.toByteArray();
					String addFormFileIndex = app.get("cntr_appx_ord").toString();
					int nAddFormFileIndex = Integer.parseInt(addFormFileIndex) + Integer.parseInt(cntrOrderNumber) + cnt + fileCnt;
					
					appDoc = docuSignUtil.makeDocument(Integer.toString(nAddFormFileIndex), fileByte, fileItem.getName());
					documentMgtList.add(new DocumentMgt(nAddFormFileIndex, appDoc));
					fileCnt++;
				}
			}
		}

		return documentMgtList;
	}

	/**
	 * 계약서 첨부문서 리스트를 조회하여 docusign document로 리턴한다.
	 *
	 * @param cntrInfo
	 * @param cntrOrderNumber
	 * @return
	 * @throws Exception
	 */
	private List<DocumentMgt> makeDocumentWithAppInfo(String  athgUUID, String cntrOrderNumber) throws Exception {
		FileList fileList = fileService.findFileListWithContents(athgUUID);
		List<DocumentMgt> documentMgtList = Lists.newArrayList();
		List<FileItem> fileItemList = fileList.getItems();
		int index = 100;
		int increaseNm = 0;
		Document appDoc = null;

		for(FileItem fileItem : fileItemList){
			increaseNm++;
			int nAddFormTextIndex = index + increaseNm;
			byte[] fileByte = fileItem.toByteArray();

			appDoc = docuSignUtil.makeDocument(Integer.toString(nAddFormTextIndex), fileByte, fileItem.getName());
			documentMgtList.add(new DocumentMgt(nAddFormTextIndex, appDoc));
		}

		return documentMgtList;
	}

	/**
	 * docusign 서명 tab을 위치 시킨다. 
	 * 템플릿에 특정 문자열을 지정해놓으면 그 위치에 서명 Tab 넣을 수 있음.
	 * 
	 * @param docusignTempInfo
	 * @param doc
	 */
	private void setSignPosition(Map<String, Object> docusignTempInfo, Document doc) {
		docusignTempInfo.put("bp_sign_anchor_name", "Purchaser Signed :"); // 구매사 서명위치를 특정 문자열로 찾기위해 설정
		docusignTempInfo.put("bp_sign_document_id", doc.getDocumentId());  // 구매사 서명 tab 들어갈 doc id
		docusignTempInfo.put("sp_sign_anchor_name", "Supplier Signed :");  // 협력사 서명위치를 특정 문자열로 찾기위해 설정
		docusignTempInfo.put("sp_sign_document_id", doc.getDocumentId());  // 협력사 서명 tab 들어갈 doc id
	}

	/**
	 * Docusign Template 조회
	 * @param param
	 * @return
	 */
	public ResultMap findDocusignTemplate(Map param) {
		Map docusignInfo = docuSignRepository.findDocusignInfo(param);
		if(docusignInfo == null) {
			return ResultMap.NOT_EXISTS();
		}

		docusignInfo.put("return_ui_id", param.get("return_ui_id"));

		docuSignUtil.settingAuthentication();
		Map resultData = docuSignUtil.templateEditView(docusignInfo);

		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * Docusign Template 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteDocusignTemplate(Map param) {
		Map docusignInfo = docuSignRepository.findDocusignInfo(param);
		if (docusignInfo == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		String docusignId = (String) docusignInfo.get("dsgn_uuid");
		this.deleteDocusignData(docusignId);
		
		docuSignUtil.settingAuthentication();
		docuSignUtil.deleteTemplate((String) docusignInfo.get("dsgn_tmpl_ref_uuid")); // 템플릿 삭제
		
		return ResultMap.SUCCESS();
	}

	/**
	 * Docusign Envelope 생성
	 * @param param
	 * @return
	 */
	public ResultMap createDocusignEnvelope(Map param) {
		Map docusignInfo = docuSignRepository.findDocusignInfo(param);
		if (docusignInfo == null) {
			return ResultMap.NOT_EXISTS();
		}

		/**
		 * 1. 봉투가 존재하는지 체크 한다.
		 */
		String envelopeId = (String) docusignInfo.get("dsgn_cntrdoc_ref_uuid");
		if(StringUtil.isNotEmpty(envelopeId)) {
			// 이미 봉투 생성되어있는경우 리턴
			return ResultMap.SUCCESS(docusignInfo);
		}

		/**
		 * 2. 템플릿으로 봉투를 생성한다.
		 */
		String dsProgSts = DocusignConst.SENT;
		docusignInfo.put("docusign_status", dsProgSts);

		// 봉투 생성
		docuSignUtil.settingAuthentication();
		Map envelopeInfo = docuSignUtil.createEnvelope(docusignInfo);

		envelopeInfo.put("dsgn_sts_ccd", dsProgSts);
		envelopeInfo.put("dsgn_uuid", docusignInfo.get("dsgn_uuid"));
		docuSignRepository.updateEnvelopeStatus(envelopeInfo);

		return ResultMap.SUCCESS(envelopeInfo);
	}

	/**
	 * Docusign Envelope 조회
	 * @param param
	 * @return
	 */
	public ResultMap findDocusignEnvelope(Map param) {
		param.put("cntrr_typ_ccd", DocusignConst.BUYER_USR_CLS);

		Map recipientInfo = docuSignRepository.findRecipientInfo(param);
		String envelopeId = (String) recipientInfo.get("dsgn_cntrdoc_ref_uuid");
		if(Strings.isNullOrEmpty(envelopeId)) {
			return ResultMap.NOT_EXISTS();
		}

		Map compInfo = docuSignRepository.findCompInfo();
		String bpCompNm = (String) compInfo.get("bp_comp_nm");

		recipientInfo.put("usr_nm", bpCompNm);
		recipientInfo.put("email", email);
		recipientInfo.put("return_ui_id", param.get("return_ui_id"));
		recipientInfo.put("user_type", "bp");
		docuSignUtil.settingAuthentication();
		Map resultData = docuSignUtil.createRecipientView(recipientInfo);

		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * Docusign Envelope 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteDocusignEnvelope(Map param) {
		Map docusignInfo = docuSignRepository.findDocusignInfo(param);
		if (docusignInfo == null) {
			return ResultMap.NOT_EXISTS();
		}

		docusignInfo.put("dsgn_sts_ccd", DocusignConst.DS_BUYER_CREATE_TEMPLATE);
		docuSignRepository.updateEnvelopeStatus(docusignInfo);

		String envelopeId = (String) docusignInfo.get("dsgn_cntrdoc_ref_uuid");
		docuSignUtil.settingAuthentication();
		docuSignUtil.deleteEnvelope(envelopeId);

		return ResultMap.SUCCESS();
	}
	
	/**
	 * docusign 정보 삭제
	 * 
	 * @param docusignId : docusign 아이디
	 */
	private void deleteDocusignData(String docusignId) {
		Map paramMap = Maps.newHashMap();
		paramMap.put("dsgn_uuid", docusignId);
		
		docuSignRepository.deleteDocusignUserInfo(paramMap);
		docuSignRepository.deleteDocusignInfo(paramMap);
	}
	
	/**
	 * docusign 이력 저장
	 * 
	 * @param docusignId : docusign 아이디
	 */
	private void insertDocusignHistory(String docusignId) {
		Map paramMap = Maps.newHashMap();
		paramMap.put("dsgn_uuid", docusignId);
		
		Map docusignInfo = docuSignRepository.findDocusignInfo(paramMap);
		docuSignRepository.insertDocusignHistory(docusignInfo);
	}
	
	/**
	 * docusign 수신자 이력 저장
	 * 
	 * @param docusignId : docusign 아이디
	 */
	private void insertRecipientHistory(String docusignId) {
		Map paramMap = Maps.newHashMap();
		paramMap.put("dsgn_uuid", docusignId);
		
		List<Map> docusignRecipientList = docuSignRepository.findListRecipient(paramMap);
		for (Map row : docusignRecipientList) {
			docuSignRepository.insertRecipientHistory(row);
		}
	}

	/**
	 * docusign template popup에서 발생한 이벤트 후 처리 함수
	 * @param event
	 * @param docusignId
	 */
	public void returnTemplateEditView(String event, String docusignId) {
		if (DocusignConst.TEMP_CANCEL.equals(event)) { // 템플릿 삭제
			this.deleteDocusignData(docusignId);
		} else if (DocusignConst.TEMP_SAVE.equals(event)) { // 템플릿 저장
			//LOG.info("저장되었습니다.");
		}
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
		Map docusignInfo = docuSignRepository.findDocusignInfo(paramMap);

		String ecntrId = (String) docusignInfo.get("ecntr_uuid");
		String cntrId = (String) docusignInfo.get("cntr_uuid");

		if(DocusignConst.SIGNING_COMPLETE.equals(event)) { // 서명완료
			econtractService.updateBuyerSignYN(ecntrId);

			Map cntrInfo = econtractService.findContractByEcntrId(docusignInfo);
			contractService.sendContract(cntrInfo);

		} else if(DocusignConst.DECLINE.equals(event)) { // 거부
			this.insertDocusignHistory(docusignId);  // docusign 마스터 이력 저장
			this.insertRecipientHistory(docusignId); // docusign 수신자 이력 저장
			this.deleteDocusignData(docusignId);     // docusign 정보 삭제
		}

		Map resultMap = Maps.newHashMap();
		resultMap.put("cntr_uuid", cntrId);
		resultMap.put("sgnord_typ_ccd", SignOrder.BUYER_VD.toString());
		return resultMap;
	}

	public ResultMap updateDocusignStatus(Map param) {
		Map<String, Object> docusignInfo = docuSignRepository.findDocusignInfoByEcntrId(param);

		if(docusignInfo == null) {
			return ResultMap.NOT_EXISTS();
		}

		docusignInfo.put("dsgn_sts_ccd", DocusignConst.DS_BUYER_CREATE_TEMPLATE);
		docuSignRepository.updateEnvelopeStatus(docusignInfo);

		return ResultMap.SUCCESS();
	}
	
}
