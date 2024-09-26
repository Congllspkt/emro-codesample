package smartsuite.app.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.oltu.oauth2.common.token.BasicOAuthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.FoldersApi;
import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.client.auth.AccessTokenListener;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.Account;
import com.docusign.esign.client.auth.OAuth.UserInfo;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.Envelope;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.EnvelopeTemplate;
import com.docusign.esign.model.FoldersRequest;
import com.docusign.esign.model.RecipientViewRequest;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.ReturnUrlRequest;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;
import com.docusign.esign.model.TemplateSummary;
import com.docusign.esign.model.ViewUrl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ktnet.ets.hub.common.util.StringUtil;

import smartsuite.app.common.shared.Const;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.app.shared.DocumentMgt;
import smartsuite.app.shared.DocusignConst;
import smartsuite.app.shared.EdocConst;
import smartsuite.exception.CommonException;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.SimpleMultipartFileItem;
import smartsuite.upload.util.AthfServiceUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class DocuSignUtil {

	private static final Logger LOG = LoggerFactory.getLogger(DocuSignUtil.class);

	@Value("#{docusign['docusign.integrator.key']}")
	private String integratorKey;

	//generate a client secret for the integrator key you supply above, again through sandbox admin menu
	@Value("#{docusign['docusign.client.secret']}")
	private String clientSecret;

	//must match a redirect URI (case-sensitive) you configured on the key
	//private static String RedirectURI = "http://localhost:8080/sp/edoc/contract/docusign/getAccessCode.do";
	@Value("#{docusign['docusign.redirect.uri']}")
	private String redirectURI;

	// use demo authentication server (remove -d for production)
	@Value("#{docusign['docusign.auth.server.url']}")
	private String authServerUrl;

	// point to the demo (sandbox) environment. For production requests your account sub-domain 
	// will vary, you should always use the base URI that is returned from authentication to
	// ensure your integration points to the correct endpoints (in both environments)
	@Value("#{docusign['docusign.restapi.url']}")
	private String restApiUrl;

	//API Username
	@Value("#{docusign['docusign.user.id']}")
	String userId;
	//static final String serviceUrl = "https://prime-dsa-devctr.docusign.net:8080/sapiws/dss.asmx";	// The service URL

	@Value("#{docusign['docusign.private.key.path']}")
	private String privateKeyPath;

	private String accountId = "";

	private ApiClient apiClient = new ApiClient(authServerUrl, "docusignAccessCode", integratorKey, clientSecret);

	@Inject
	StdFileService fileService;

	@Value("#{smartsuiteProperties['smartsuite.upload.path']}")
	private String fileUploadPath;

	/**
	 * 인증정보 셋팅
	 * 
	 * @param IntegratorKey : integrator keys are created through developer
	 *        sandbox accounts then migrated to your production account when
	 *        ready. Note that your integrator key also acts as your client ID
	 *        during oauth requests ClientSecret : generate a client secret for
	 *        the integrator key you supply above, again through sandbox admin
	 *        menu RedirectURI : must match a redirect URI (case-sensitive) you
	 *        configured on the key AuthServerUrl : use demo authentication server
	 *        (remove -d for production) RestApiUrl : point to the demo (sandbox)
	 *        environment. For production requests your account sub-domain will
	 *        vary, you should always use the base URI that is returned from
	 *        authentication to ensure your integration points to the correct
	 *        endpoints (in both environments)
	 * @return
	 */
	public void settingAuthentication() {
		// instantiate the api client and configure auth server
		// set the base path for REST API requests
		apiClient.setBasePath(restApiUrl);

		// configure the authorization flow on the api client
		apiClient.configureAuthorizationFlow(integratorKey, clientSecret, redirectURI);

		java.util.List<String> scopes = new ArrayList<String>();
		scopes.add(OAuth.Scope_SIGNATURE);
		scopes.add(OAuth.Scope_IMPERSONATION);

		byte[] privateKeyBytes = null;
		try {
			privateKeyBytes = Files.readAllBytes(Paths.get(privateKeyPath));
			OAuth.OAuthToken oAuthToken = apiClient.requestJWTUserToken(integratorKey, userId, scopes, privateKeyBytes, 3600);
			LOG.info("getAccessToken : " + oAuthToken.getAccessToken());
			LOG.info("getExpiresIn : " + oAuthToken.getExpiresIn());

			apiClient.setAccessToken(oAuthToken.getAccessToken(), oAuthToken.getExpiresIn());
			UserInfo userInfo = apiClient.getUserInfo(oAuthToken.getAccessToken());
			List<Account> accountList = userInfo.getAccounts();
			Account account = accountList.get(0);
			accountId = account.getAccountId();

			LOG.info("UserInfo: " + userInfo);
		} catch (IllegalArgumentException e) {
			LOG.error("error : " + e.toString() + " , error msg : " + e.getMessage());
			throw new CommonException(this.getClass() + "settingAuthentication error:" + e.toString() + " , error msg : " + e.getMessage());
		} catch (IOException e) {
			LOG.error("error : " + e.toString() + " , error msg : " + e.getMessage());
			throw new CommonException(this.getClass() + "settingAuthentication error:" + e.toString() + " , error msg : " + e.getMessage());
		} catch (ApiException e) {
			LOG.error("error : " + e.toString() + " , error msg : " + e.getMessage());
			throw new CommonException(this.getClass() + "settingAuthentication error:" + e.toString() + " , error msg : " + e.getMessage());
		}

		// set as default api client in your configuration
		Configuration.setDefaultApiClient(apiClient);
	}

	/**
	 * 인증코드로 접근토큰을 생성
	 * 
	 * @param code
	 */
	public void getAccessToken(String code) {
		//apiClient.getTokenEndPoint()
		apiClient.getTokenEndPoint().setCode(code);
		// optionally register to get notified when a new token arrives
		apiClient.registerAccessTokenListener(new AccessTokenListener() {
			public void notify(BasicOAuthToken token) {//토큰 도착
				LOG.info("Got a fresh token: " + token.getAccessToken());
			}
		});

		apiClient.updateAccessToken();
	}

	/**
	 * 계정 id 조회
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAccountId() throws Exception {
		UserInfo userInfo;
		userInfo = apiClient.getUserInfo(apiClient.getAccessToken());
		List<Account> userList = userInfo.getAccounts();
		Account user = userList.get(0);
		String accountId = user.getAccountId();

		return accountId;
	}

	/**
	 * 템플릿 수정 화면
	 * 
	 * @param formInfo
	 * @return
	 */
	public Map templateEditView(Map formInfo) {
		Map resultMap = Maps.newHashMap();
		TemplatesApi templatesApi = new TemplatesApi();
		ReturnUrlRequest returnUrlRequest = new ReturnUrlRequest();
		String templateId = (String) formInfo.get("dsgn_tmpl_ref_uuid");
		String docusignId = (String) formInfo.get("dsgn_uuid");
		String returnUiId = (String) formInfo.get("return_ui_id");
		
		//set the url where you want the sender to go once they are done editing/sending the envelope
		//ReturnUrlRequest returnUrl = new ReturnUrlRequest();
		//returnUrl.setReturnUrl("https://www.docusign.com/devcenter");
		ViewUrl view;
		String uri = getCurrentReqUri();
		try {
			returnUrlRequest.setReturnUrl(uri + "/bp/docusign/returnTemplateEditView.do?return_ui_id=" + returnUiId + "&dsgn_uuid=" + docusignId);
			view = templatesApi.createEditView(accountId, templateId, returnUrlRequest);

			LOG.info("SenderView URL: " + view.getUrl());
			resultMap.put("url", view.getUrl());
			resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		} catch (ApiException e) {
			LOG.error("ERROR : " + e.toString() + ", error msg : " + e.getMessage());
			throw new CommonException(this.getClass() + "templateEditView error:" + e.toString() + " , error msg : " + e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 수신자용 view document 화면 생성
	 * 
	 * @param recipientInfo
	 * @return
	 */
	public Map createRecipientView(Map recipientInfo) {
		Map resultMap = Maps.newHashMap();
		// instantiate a new EnvelopesApi object
		EnvelopesApi envelopesApi = new EnvelopesApi();
		// set the url where you want the recipient to go once they are done signing
		RecipientViewRequest view = new RecipientViewRequest();
		
		String uri = getCurrentReqUri();
		view.setReturnUrl(uri + "/" + recipientInfo.get("user_type")
				+ "/docusign/returnRecipientView.do?dsgn_sgndusr_uuid=" + recipientInfo.get("dsgn_sgndusr_uuid")
				+ "&dsgn_uuid=" + recipientInfo.get("dsgn_uuid")
				+ "&cntrr_typ_ccd=" + recipientInfo.get("cntrr_typ_ccd")
				+ "&return_ui_id=" + recipientInfo.get("return_ui_id"));
		view.setAuthenticationMethod("email");

		// recipient information must match embedded recipient info we provided in step #2
		String email = (String) recipientInfo.get("email");
		String userName = (String) recipientInfo.get("usr_nm");
		String recipientId = (String) recipientInfo.get("dsgn_sgndusr_ref_uuid");
		String clientId = (String) recipientInfo.get("dsgn_clt_ref_uuid");
		String envelopeId = (String) recipientInfo.get("dsgn_cntrdoc_ref_uuid");
		view.setEmail(email);
		view.setUserName(userName);
		view.setRecipientId(recipientId);
		view.setClientUserId(clientId);

		// call the CreateRecipientView API
		ViewUrl recipientView;
		try {
			recipientView = envelopesApi.createRecipientView(accountId, envelopeId, view);

			LOG.info("Signing URL = " + recipientView.getUrl());
			resultMap.put("url", recipientView.getUrl());
			resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		} catch (ApiException e) {
			LOG.error("ERROR : " + e.toString() + ", error msg : " + e.getMessage());
			throw new CommonException(this.getClass() + "createRecipientView error:" + e.toString() + " , error msg : " + e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * html 태그를 붙여 리턴한다.
	 * 
	 * @param content
	 * @return
	 */
	public static String makeHtml(String content) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\" />");
		sb.append("</head>");
		sb.append("<body>");
		sb.append(content);
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	/**
	 * byte 데이터를 docusign Document로 리턴한다.
	 * 
	 * @param documentId
	 * @param bData
	 * @param orgnFileNm
	 * @return
	 */
	public Document makeDocument(String documentId, byte[] bData, String orgnFileNm) {
		Document doc = new Document();
		String base64Doc = Base64.encodeBase64String(bData);

		doc.setDocumentBase64(base64Doc);
		doc.setName(orgnFileNm); // can be different from actual file name
		//확장자 찾기
		String extension = orgnFileNm.substring(orgnFileNm.lastIndexOf("."), orgnFileNm.length());

		doc.setFileExtension(extension);
		doc.setDocumentId(documentId);
		return doc;
	}

	/**
	 * docusign template 생성
	 * 
	 * @param tempInfo
	 * @param buyerInfo
	 * @param supplierList
	 * @param documentMgtList
	 * @return
	 */
	public Map createTemplate(Map tempInfo, Map buyerInfo, List<Map> supplierList, List<DocumentMgt> documentMgtList) {
		Map resultMap = Maps.newHashMap();
		TemplatesApi templatesApi = new TemplatesApi();
		EnvelopeTemplate envelopeTemplate = new EnvelopeTemplate();
		String templateName = (String) tempInfo.get("template_name"); //템플릿 명
		int supplierSignOrder = 0;
		
		try {
			List<Signer> signerList = Lists.newArrayList();

			//구매사 정보 셋팅
			Signer bpSigner = new Signer();

			String bpRecipientId = UUID.randomUUID().toString();
			String bpClientUserId = UUID.randomUUID().toString();
			String bpAnchorString = (String) tempInfo.get("bp_sign_anchor_name");
			String bpSignDocumentId = (String) tempInfo.get("bp_sign_document_id");
			String bpEmail = (String) tempInfo.get("bp_email");
			
			//서명 위치 설정
			SignHere signHere = makeSignHere(bpAnchorString, bpSignDocumentId, bpRecipientId);
			List<SignHere> signHereTabs = Lists.newArrayList();
			signHereTabs.add(signHere);

			Tabs tabs = new Tabs();
			tabs.setSignHereTabs(signHereTabs);
			
			bpSigner.setTabs(tabs);
			bpSigner.setSigningGroupName(DocusignConst.BUYER_SIGN_GROUP_NAME);
			bpSigner.setName((String) buyerInfo.get("bp_comp_nm"));
			bpSigner.setEmail(bpEmail);
			bpSigner.setRecipientId(bpRecipientId);
			bpSigner.setClientUserId(bpClientUserId); //client id 셋팅하지 않으면 메일로 발송이 된다.
			
			buyerInfo.put("dsgn_sgndusr_ref_uuid", bpRecipientId);
			buyerInfo.put("client_user_id", bpClientUserId);

			bpSigner.setRoutingOrder("1"); //구매사 서명 순서
			supplierSignOrder = 2;

			signerList.add(bpSigner);

			//협력사 정보
			for (int i = 0; i < supplierList.size(); i++) {
				Map supplier = supplierList.get(i);
				Signer spSigner = new Signer();
				String spRecipientId = UUID.randomUUID().toString();
				String spClientUserId = UUID.randomUUID().toString();

				String spAnchorString = (String) tempInfo.get("sp_sign_anchor_name");
				String spSignDocumentId = (String) tempInfo.get("sp_sign_document_id");
				SignHere spSignHere = makeSignHere(spAnchorString, spSignDocumentId, spRecipientId);
				
				List<SignHere> spSignHereTabs = Lists.newArrayList();
				spSignHereTabs.add(spSignHere);

				Tabs spTabs = new Tabs();
				spTabs.setSignHereTabs(spSignHereTabs);
				
				spSigner.setTabs(spTabs);
				spSigner.setSigningGroupName(DocusignConst.SUPPLIER_SIGN_GROUP_NAME);
				spSigner.setName((String) supplier.get("sp_comp_nm"));
				spSigner.setEmail((String) supplier.get("sp_rep_email"));
				spSigner.setRecipientId(spRecipientId);
				spSigner.setClientUserId(spClientUserId);
				
				supplier.put("dsgn_sgndusr_ref_uuid", spRecipientId);
				supplier.put("client_user_id", spClientUserId); //client id 셋팅하지 않으면 메일로 발송이 된다.
				
				spSigner.setRoutingOrder(Integer.toString(supplierSignOrder + i));
				//spSigner.setNote((String)supplier.get("vd_cd"));
				signerList.add(spSigner);
			}

			Collections.sort(documentMgtList); // index기준으로 정렬
			List<Document> documentList = Lists.newArrayList();
			for (DocumentMgt row : documentMgtList) {
				documentList.add(row.getDocument());
			}
			
			envelopeTemplate.setDocuments(documentList);
			envelopeTemplate.setRecipients(new Recipients());
			envelopeTemplate.getRecipients().setSigners(signerList);
			envelopeTemplate.setName(templateName);

			TemplateSummary templateInfo = templatesApi.createTemplate(accountId, envelopeTemplate);
			
			resultMap.put("dsgn_tmpl_ref_uuid", templateInfo.getTemplateId());
			resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);

		} catch (ApiException e) {
			LOG.error("error : " + e.toString() + " , error msg : " + e.getMessage());
			throw new CommonException(this.getClass() + "createTemplate error:" + e.toString() + " , error msg : " + e.getMessage());
		}

		return resultMap;
	}

	/**
	 * docusign envelope 삭제
	 * 
	 * @param envelopeId
	 */
	public void deleteEnvelope(String envelopeId) {
		try {
			Envelope envelope = new Envelope();
			envelope.setStatus("voided");
			envelope.voidedReason("voided");

			EnvelopesApi envelopesApi = new EnvelopesApi();
			envelopesApi.update(accountId, envelopeId, envelope);
			
		} catch (ApiException e) {
			LOG.error("ERROR : " + e.toString() + ", error msg : " + e.getMessage());
			throw new CommonException(this.getClass() + "deleteEnvelope error:" + e.toString() + " , error msg : " + e.getMessage());
		}
	}

	/**
	 * docusign template 삭제
	 * 
	 * @param templateId
	 */
	public void deleteTemplate(String templateId) {
		FoldersApi foldersApi = new FoldersApi();
		FoldersRequest foldersRequest = new FoldersRequest();
		List<String> envelopeIds = Lists.newArrayList();
		envelopeIds.add(templateId);
		foldersRequest.setEnvelopeIds(envelopeIds);

		try {
			foldersApi.moveEnvelopes(accountId, "recyclebin", foldersRequest); //docusign template을 휴지통으로 옴기는 api
		} catch (ApiException e) {
			LOG.error("error : " + e.toString() + " , error msg : " + e.getMessage());
			throw new CommonException(this.getClass() + "deleteTemplate error:" + e.toString() + " , error msg : " + e.getMessage());
		}
	}
	
	/**
	 * 봉투를 생성한다.
	 * 
	 * @param templateInfo
	 * @return
	 */
	public Map createEnvelope(Map templateInfo) {
		EnvelopeDefinition envDef = new EnvelopeDefinition();
		String templateId = (String) templateInfo.get("dsgn_tmpl_ref_uuid");
		String emailSubject = (String) templateInfo.get("cntr_nm");
		String docusignstatus = (String) templateInfo.get("docusign_status");
		
		envDef.setTemplateId(templateId);
		envDef.setEmailSubject(emailSubject);
		if (StringUtil.isNotEmpty(docusignstatus)) {
			envDef.setStatus(docusignstatus);
		}
		
		EnvelopesApi envelopesApi = new EnvelopesApi();
		EnvelopeSummary envelopeSummary;
		try {
			envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);
		} catch (ApiException e) {
			LOG.error("ERROR : " + e.toString() + ", error msg : " + e.getMessage());
			throw new CommonException(this.getClass() + "createEnvelope error:" + e.toString() + " , error msg : " + e.getMessage());
		}
		
		String envelopeId = envelopeSummary.getEnvelopeId();
		
		Map resultData = Maps.newHashMap();
		resultData.put("dsgn_cntrdoc_ref_uuid", envelopeId);
		return resultData;
	}
	
	/**
	 * 서명위치 설정
	 * 
	 * @param anchorString
	 * @param documentId
	 * @param recipientId
	 * @return
	 */
	public SignHere makeSignHere(String anchorString, String documentId, String recipientId) {
		SignHere signHere = new SignHere();
		signHere.setDocumentId(documentId);
		signHere.setRecipientId(recipientId);
		signHere.setAnchorString(anchorString);
		signHere.setScaleValue("120");
		signHere.setAnchorXOffset("20");
		signHere.setAnchorYOffset("40");

		return signHere;
	}

	/**
	 * 현재 요청의 URI 리턴
	 * 
	 * @return
	 */
	private String getCurrentReqUri() {
		String uri = "";
		ServletUriComponentsBuilder serveltCB = ServletUriComponentsBuilder.fromCurrentContextPath();
		UriComponents uc = serveltCB.build();
		String scheme = uc.getScheme();
		String host = uc.getHost();

		uri = scheme + "://" + host;

		if (uc.getPort() != -1) {
			uri = uri + ":" + uc.getPort();
		}
		if (!EdocStringUtil.isEmpty(uc.getPath())) {
			uri = uri + uc.getPath();
		}

		LOG.info("uri : " + uri);
		return uri;
	}

	/**
	 * 서명완료 된 통합파일 저장 후 파일그룹코드 반환
	 * 
	 * @param envelopeId
	 * @param orgnFileNm
	 * @return
	 */
	public String downloadEnvelopeDocument(String envelopeId, String orgnFileNm) {
		EnvelopesApi envelopesApi = new EnvelopesApi();
		String grpCd = UUID.randomUUID().toString();
		File pdfFile = null;

		try {
			String fileFullPath = fileUploadPath + UUID.randomUUID().toString();
			byte[] documentByte = envelopesApi.getDocument(accountId, envelopeId, "combined");
			Files.write(Paths.get(fileFullPath), documentByte);
			pdfFile = new File(fileFullPath);
			
			SimpleMultipartFileItem fileItem = AthfServiceUtil.newMultipartFileItem (
					UUID.randomUUID().toString(),
					grpCd,
					orgnFileNm,
					pdfFile
			);
			fileService.createWithMultipart(fileItem);
		} catch (Exception e) {
			throw new CommonException(this.getClass() + "downloadEnvelopeDocument(String envelopeId, String orgnFileNm):" + e.toString()
					+ " , error msg : " + e.getMessage());
		} finally {
			try {
				if (pdfFile.exists())
					pdfFile.delete(); //임시 파일 삭제
			} catch (Exception e) {
				throw new CommonException(this.getClass() + "downloadEnvelopeDocument(String envelopeId, String orgnFileNm):" + e.toString()
						+ " , error msg : " + e.getMessage());
			}
		}

		return grpCd;
	}
}