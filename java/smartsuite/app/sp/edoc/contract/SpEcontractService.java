package smartsuite.app.sp.edoc.contract;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.organizationManager.operationUnit.service.OperationUnitManagerService;
import smartsuite.app.bp.edoc.pdfmaker.PdfMakerService;
import smartsuite.app.bp.edoc.contract.service.EcontractService;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.common.cert.pki.SignatureUtil;
import smartsuite.app.common.cert.pki.verification.VerificationProvider;
import smartsuite.app.common.cert.service.CertMgtService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.EdocConst;
import smartsuite.app.shared.SignOrder;
import smartsuite.app.shared.status.ContractStatusService;
import smartsuite.app.sp.contract.common.service.SpContractService;
import smartsuite.app.sp.edoc.contract.event.SpEcontractEventPublisher;
import smartsuite.app.sp.edoc.contract.repository.SpEcontractRepository;
import smartsuite.app.util.DocumentUtil;
import smartsuite.app.util.MakingCntrForm;
import smartsuite.exception.CommonException;
import smartsuite.security.authentication.Auth;
import smartsuite.security.core.crypto.AESIvParameterGenerator;
import smartsuite.security.core.crypto.AESSecretKeyGenerator;
import smartsuite.security.core.crypto.CipherUtil;
import smartsuite.security.web.crypto.AESCipherKey;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 전자계약 관련 처리하는 서비스 Class입니다.
 *
 * @FileName SpEcontractService.java
 */
@Service
@Transactional
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SpEcontractService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SpEcontractService.class);
	
	@Value("#{globalProperties['blockchain.use']}")
	private String blockChainUseYn;
	@Value("#{cert['cert.verifiable']}")
	private boolean certVerifiable;
	@Value("#{cert['cert.mgt']}")
	private String certMgt;
	@Inject
	MakingCntrForm makingCntrForm;
	@Inject
	VerificationProvider verificationProvider;
	@Inject
	PdfMakerService pdfMakerService;
	@Inject
	CertMgtService certMgtService;
	@Inject
	AESSecretKeyGenerator keyGenerator;
	@Inject
	AESIvParameterGenerator parameterGenerator;
	@Inject
	CipherUtil cipherUtil;
	@Inject
	StdFileService fileService;	
	@Inject
	OperationUnitManagerService operationUnitManagerService;
	@Inject
	SpEcontractRepository spEcontractRepository;
	@Inject
	SpEcontractEventPublisher spEcontractEventPublisher;
	@Inject
	SpContractService spContractService;
	@Inject
	EcontractService econtractService;
	@Inject
	ContractStatusService contractStatusService;
	
	
	/**
	 * 계약서 PDF 생성하기(PD4ML + Aspose Lib 이용)
	 * @param param   : 계약정보(cntr_no, cntr_rev 필수)
	 * @param sign    : 서명여부
	 * @param horizon : 수평생성여부
	 * @return
	 */
	public Map<String,Object> generateCntrPdf(Map<String,Object> param, boolean sign, boolean horizon) {
		Map<String,Object> cntrInfo = spEcontractRepository.getCntrContent(param);
		List<Map<String, Object>> appList = spEcontractRepository.getAppContent(param);
		
		CertInfo certInfo = null;
		if(sign) {
			//계약일자를 서명일자로 변경하는 함수
			String content = replaceSignDate((String) cntrInfo.get("sign_source"));

			cntrInfo.put("sign_source", content);	//변경된 계약서 서명일자를 업데이트
			cntrInfo.put("content", content);		//변경된 계약서 서명일자를 업데이트

			spEcontractRepository.updateBeforeSignContent(cntrInfo);
			
			List operUnitList = operationUnitManagerService.findListOperationUnit(null);
			String logicOrgCd = DocumentUtil.removeOunitCd((String)cntrInfo.get("oorg_cd"), operUnitList );
			certInfo = certMgtService.getCertInfo(logicOrgCd);
		}
		
		Map<String,Object> pdfAttach = pdfMakerService.generatePdfUsingHtml(cntrInfo, appList, sign, certInfo, horizon);
		return pdfAttach;
	}

	/**
	 * 사용자 파일 유형의 계약서 PDF 생성
	 * @param param
	 * @param sign
	 * @return
	 */
	private Map<String, Object> generateCntrPdfByUserFile(Map param, boolean sign) {
		Map<String, Object> cntrInfo = spEcontractRepository.getNonStandardSignContent(param);
		String cntrdocAthgUuid = (String) cntrInfo.get("tmpl_unud_cntrdoc_athg_uuid");
		String appxAthgUuid = (String) cntrInfo.get("tmpl_unud_appx_athg_uuid");
		
		CertInfo certInfo = null;
		if(sign) {
			List operUnitList = operationUnitManagerService.findListOperationUnit(null);
			String logicOrgCd = DocumentUtil.removeOunitCd((String) cntrInfo.get("oorg_cd"), operUnitList );
			certInfo = certMgtService.getCertInfo(logicOrgCd);
		}
		
		Map<String, Object> pdfInfo = pdfMakerService.generatePdfUsingFile(cntrInfo, cntrdocAthgUuid, appxAthgUuid, sign, certInfo);
		return pdfInfo;
	}
	
	/** 첨부서류 내용조회 **/
	public Map<String, Object> findAppData(Map<String, Object> param) {
		Map<String, Object> result = Maps.newHashMap();
		
		result.put("appData", spEcontractRepository.getAppData(param));

		Map<String, Object> itemParam = Maps.newHashMap();
		itemParam.put("use_yn", "Y");
		itemParam.put("mod_poss_yn", "Y");
		itemParam.put("mand_yn", "Y");
		
		result.put("itemList", spEcontractRepository.findListCntrItem(itemParam));

		return result;
	}


	/** 첨부서식 파일 저장 **/
	public Map<String, Object> saveAppFormFile(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();

		if (param.containsKey("manager")) {
			// 실제파일 존재여부 체크
			String checkYn = spEcontractRepository.checkAttFileYn(param);
			if ("N".equals(checkYn)) {
				param.put("athg_uuid", null);
			}
			
			spEcontractRepository.updateAppFormInSts(param);
		} else {
			spEcontractRepository.updateAppFormFile(param);
		}

		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}
	
	/** 서명조회 **/
	public Map<String, Object> checkedSign(Map<String, Object> param) {
		Map<String, Object> result = Maps.newHashMap();
		// 업체서명조회
		
		result.put("ownerList", spEcontractRepository.selectCheckedSignForOwner(param));
		// 협력사서명조회
		result.put("vendorList", spEcontractRepository.selectCheckedSignForVendor(param));

		return result;
	}

	/** 실제서명조회 **/
	public Map<String, String> getCertInfoView(Map<String, Object> param) {
		Map<String, String> result = Maps.newHashMap();
		param.put("signValue", spEcontractRepository.getSignValue(param));
		param.put("signedFileGrpCd", param.get("athg_uuid"));

		try {
			// DB 검증
			result = verifyAndGetCertInfoFromDB(param);
			result.put("ctry_ccd", (String) param.get("ctry_ccd"));
			result.put("sgndusr_typ_ccd", (String) param.get("sgndusr_typ_ccd"));
			result.put("blockChainUseYn", "N");

			// BLOCK 검증
			if ("Y".equals(blockChainUseYn) && "Y".equals(param.get("ecntr_use_yn")) && "Y".equals(param.get("tmpl_use_yn"))) {
				//CntrRcvData rcvBlock = verifyAndGetBlockInfoFromBlock(param);
				ResultMap resultMap = spEcontractEventPublisher.verifyAndGetBlockInfo(param);
				Map resultData = resultMap.getResultData();
				
				result.put("blockNum", (String)resultData.get("blockNum"));
				result.put("action", (String)resultData.get("action"));
				result.put("txId", (String)resultData.get("txId"));
				result.put("cntrProgChDate", (String)resultData.get("cntrProgChDate"));
				
				result.put("blockChainUseYn", "Y"); // 온라인, 표준계약서식일 경우에만 블록체인 로직을 탐
			}

		} catch (CommonException e) {
			LOG.error(this.getClass().getName() + ".getCertInfoView() : " + e.toString());
			throw e;
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "getCertInfoView error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".getCertInfoView() : " + e.getMessage(),
					"fail to verify");
		}

		result.put(Const.RESULT_STATUS, Const.SUCCESS);

		return result;

	}

	/** DB에 있는 서명값 검증 **/
	public Map<String, String> verifyAndGetCertInfoFromDB(Map<String, Object> param) throws Exception {
		Map<String, String> certInfo = Maps.newHashMap();
		String signValue = (String) param.get("signValue");
		String signedFileGrpCd = (String) param.get("signedFileGrpCd");
		String hashValueFromSignValue = "";
		
		FileItem fileItem = this.getFileItemByGrpCd(signedFileGrpCd);
		String hashValueFromFile = SignatureUtil.getHashValueFromFile(fileItem);

		// 서명값 검증
		if ("BUYER".equals(param.get("sgndusr_typ_ccd")) || "N".equals(blockChainUseYn)) {
			// 갑 또는 을(국내)인 경우
			certInfo = verificationProvider.getCertInfo(signValue);
			hashValueFromSignValue = verificationProvider.getSource(signValue);

			if (certInfo == null || !hashValueFromSignValue.equals(hashValueFromFile)) {
				throw new CommonException(
						this.getClass().getName() + ".verifyAndGetCertInfoFromDB() : fail to verify(KR)"+
						"hashValues aren't eqaul"+"hashValue from signValue(DB) : " + hashValueFromSignValue + " , "
								+ "hashValue from file : " + hashValueFromFile);
			}
		} else {
			// 을(해외)인 경우
			ResultMap resultMap = spEcontractEventPublisher.getSignSourceFromSignValue(signValue);
			Map resultData = resultMap.getResultData();
			hashValueFromSignValue = (String)resultData.get("hashValue");

			if (!hashValueFromSignValue.equals(hashValueFromFile)) {
				throw new CommonException(
						this.getClass().getName() + ".verifyAndGetCertInfoFromDB() : fail to verify(not KR)"+
						"hashValues aren't eqaul"+ "hashValue from signValue(DB) : " + hashValueFromSignValue + " , "
								+ "hashValue from file : " + hashValueFromFile);
			}
		}
		return certInfo;
	}
	
	public List<Map<String, Object>> searchAtt(Map<String, Object> param) {
		return spEcontractRepository.getCntrAppFormListInSts(param);
	}
	
	/**
	 * 계약서에 계약일자를 서명일자로 변경
	 *
	 * @author : lee daesung
	 * @param : the param
	 * @return the string
	 * @Date : 2020. 3. 05
	 * @Method Name : replaceSignDate
	 */
	private String replaceSignDate(String content) {
		if( Strings.isNullOrEmpty(content) ) {
			throw new CommonException("계약서 내용이 없습니다.");
		}
		
		String result = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Date date = new Date();
		String signDate = format.format(date);
		
		/* 계약서 계약일자를 서명일자로 변환*/
		signDate = signDate.substring(0, 4) +  "년 " + signDate.substring(4, 6) + "월 " + signDate.substring(6, 8) + "일";
		// 계약서에 내용에 계약일자를 서명일자로 변경함. 예시: <span name="[[!@#sgn_dttm!@#]]">2019년 2월 3일</span>  -> <span name="[[!@#sgn_dttm!@#]]">2020년 3월 5일</span>
		result = DocumentUtil.replaceTagContent(content, "span", "[[!@#sgn_dttm!@#]]", signDate);
		
		return result;
	}
	
	public String controlSignatureProgress(Map<String,Object> param){
		String signableSts = "";
		int signLockCnt = spEcontractRepository.findSignLockCnt(param);
		String isetLockYn = spEcontractRepository.findSignLock(param);
		
		if(signLockCnt == 0) { //아무도 락을 안 걸엇을 경우 서명가능
			signableSts = CntrConst.SGN_LCKD_STS.SIGNABLE;
			spEcontractRepository.updateSignLock(param);
			
		}else {
			if( "Y".equals(isetLockYn) ) { //내가 락을 걸었을 경우 서명가능
				signableSts = CntrConst.SGN_LCKD_STS.SIGNABLE;
			}else {	//누군가 락일 걸엇을 경우 서명 불가능
				signableSts = CntrConst.SGN_LCKD_STS.SOMEONE_ELSE_SIGN_USING;
			}
		}
		
		return signableSts;
	}
	
	public String checkSignPdfFileStatus(Map<String,Object> param) {
		String pdfFileStatus = "";
		int fileCnt = spEcontractRepository.findSignPdfFileCnt(param);
		
		if(fileCnt == 0) { //pdf 서명한 파일은 존재하지 않음
			pdfFileStatus = EdocConst.NOT_EXSIT_PDF_FILE;
		}else if(fileCnt == 1) { //pdf 서명한 파일이 존재함
			pdfFileStatus = EdocConst.EXSIT_PDF_FILE;
		}else {	//2개이상 존재하면 안되기에 여기는 조건은 에러임
			pdfFileStatus = "X";
		}
		
		return pdfFileStatus;
	}
	
	/**
	 * 계약서 내용 조회
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the map
	 * @Date : 2020. 11. 18
	 * @Method Name : getCntrContent
	 */
	public Map<String,Object> getCntrContent(Map<String,Object> param){
		return spEcontractRepository.getCntrContent(param);
	}
	
	/**
	 * 첨부서류 내용 조회
	 * @param param
	 * @return
	 */
	public List<Map<String,Object>> getAppContent(Map<String,Object> param) {
		return spEcontractRepository.getAppContent(param);
	}
	
	/**
	 * 그룹 코드로 파일 조회 (1건)
	 * @param grpCd
	 * @return
	 */
	public FileItem getFileItemByGrpCd(String grpCd) {
		FileList fileList;
		FileItem fileItem;
		try {
			fileList = fileService.findFileListWithContents(grpCd);
			fileItem = fileList.getItems().get(0);
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "getFileByGrpCd error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".getFileByGrpCd(String grpCd) : " + e.getMessage(), e.toString());
		}
		return fileItem;
	}
	
	/**
	 * 비표준계약서 PDF 화면 출력
	 * @param request
	 * @param response
	 * @param cntrdocAthgUuid
	 */
	public void getNonStandardCntrPdf(HttpServletRequest request, HttpServletResponse response, String cntrdocAthgUuid) {
		OutputStream ops = null;
		Writer writer = null;

		try {
			FileList fileList = fileService.findFileListWithContents(cntrdocAthgUuid);

			if(fileList != null && fileList.getSize() > 0) {
				FileItem fileItem = fileList.getItems().get(0);
				byte[] pdfFileBytes = fileItem.toByteArray();
				
				response.setContentType("application/pdf; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename="
						+ URLEncoder.encode(fileItem.getName(), "UTF-8").replaceAll("\\+", "%20"));
				ops = response.getOutputStream();
				ops.write(pdfFileBytes);
				ops.flush();

			} else {
				writer = response.getWriter();
				response.setContentType("text/html");
				writer.write("PDF파일이 존재하지 않습니다.");
				writer.flush();
			}
		} catch (Exception e) {
			LOG.error(this.getClass().getName() + ".getEFormPdf() : Exception", e);
			throw new CommonException(this.getClass().getName() + ".getEformPdf() : " + e.toString());
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
				if(ops != null) {
					ops.close();
				}
			} catch (Exception e) {
				LOG.error(this.getClass().getName() + ".getEFormPdf() : Exception", e);
			}
		}
	}

	public ResultMap rejectEcontract(String cntrUUID) {
		String ecntrUUID = spEcontractRepository.getEcntrUUID(cntrUUID);
		Map<String, Object> param = Maps.newHashMap();
		param.put("ecntr_uuid", ecntrUUID);
		param.put("sgncmpld_cntrdoc_athg_uuid", null);
		spEcontractRepository.updateSignPdfNull(param);
		spEcontractRepository.updateAllSignN(param);
		spEcontractRepository.deleteSignValue(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사 서명정보 가져오기
	 */
	public Map<String, Object> findSignEContractInfo(Map<String, Object> param) throws Exception {
		Map<String, Object> userInfo = Auth.getCurrentUserInfo();
		Map<String,Object> cntrInfo = spEcontractRepository.getSignCntrContent(param);
		String sgncmpldCntrdocAthgUuid = (String) cntrInfo.get("sgncmpld_cntrdoc_athg_uuid");
		SignOrder signOrder = SignOrder.valueOf((String) cntrInfo.get("sgnord_typ_ccd"));

		if(!SignOrder.BUYER_VD.equals(signOrder)) {
			boolean sign = true;
			Map<String,Object> signSource = Maps.newHashMap();
			
			String tmplTypCcd = (String) cntrInfo.get("cntr_tmpl_typ_ccd");
			if(CntrConst.TMPL_TYPE.TEMPLATE.equals(tmplTypCcd)) {
				signSource = this.generateCntrPdf(cntrInfo, sign, false);
			} else if(CntrConst.TMPL_TYPE.USR_FILE.equals(tmplTypCcd)) {
				signSource = this.generateCntrPdfByUserFile(cntrInfo, sign);
			}
			
			sgncmpldCntrdocAthgUuid = (String) signSource.get("athg_uuid");
			cntrInfo.put("sgncmpld_cntrdoc_athg_uuid", sgncmpldCntrdocAthgUuid);
		}
		
		FileItem fileItem = this.getFileItemByGrpCd(sgncmpldCntrdocAthgUuid);
		String hashValue = SignatureUtil.getHashValueFromFile(fileItem);

		cntrInfo.put("hash_value", hashValue);

		String bizRegNo = spEcontractRepository.getVdBizRegNo();
		cntrInfo.put("bizregno", bizRegNo);
		cntrInfo.put("usr_id", userInfo.get("usr_id"));
		cntrInfo.put("callbackUrl" , "sp/edoc/contract/SpContractController/saveSignatureValue.do");
		/** html 제거 **/
		cntrInfo.remove("sign_source");
		cntrInfo.remove("content");
		/** html 제거 **/
		Map<String, Object> signInfo = Maps.newHashMap();
		signInfo.put("signContentInfo", cntrInfo);
		signInfo.put("_aesCipherKey", new AESCipherKey(keyGenerator.getKey(), keyGenerator.getPassPhrase(), keyGenerator.getIterationCount(), parameterGenerator.getIv()));
		return signInfo;
	}

	/** 협력사 서명값 저장 **/
	public Map<String, Object> saveSignatureValue(Map<String, Object> param) {
		Map<String, Object> paramMap = param;
		Map<String, Object> resultMap = Maps.newHashMap();
		Map cntrInfo = spEcontractRepository.findContract(paramMap);
		
		/* 세션 체크 */
		String usrId = (String) paramMap.get("usr_id");
		String cntrStsCcd = (String) cntrInfo.get("cntr_sts_ccd");

		if( !possibleSignSts(cntrStsCcd) ) {   //서명 가능상태 확인
			resultMap.put(Const.RESULT_STATUS, Const.FAIL);
			return resultMap;
		}

		if( !checkSessionUser(usrId) ) {    //세션 유저 체크
			resultMap.put(Const.RESULT_STATUS, Const.FAIL);
			return resultMap;
		}

		String signatureValue = paramMap.get("sign_value").toString(); // 을 서명값
		String rvalue = cipherUtil.decrypt((String)paramMap.get("rvalue"));
		String ssn = (String) paramMap.get("ssn");
		Map verifyCertResult = verifySameCertificate(signatureValue);

		if( ! Const.SUCCESS.equals(verifyCertResult.get(Const.RESULT_STATUS))  ){
			return verifyCertResult;
		}

		verificationProvider.verifySignValue(signatureValue, rvalue, ssn, certVerifiable);

		String ecntrId = (String) cntrInfo.get("ecntr_uuid");
		String athgId = (String) paramMap.get("sgncmpld_cntrdoc_athg_uuid");
		FileItem fileItem = this.getFileItemByGrpCd(athgId);
		String hashValue = SignatureUtil.getHashValueFromFile(fileItem);

		if( !verifySignatureForgery(signatureValue, hashValue) ){  //위변조 검사
			throw new CommonException(this.getClass().getName() + ".verifySignatureForgery()"+
					"The original text does not match the signature value.");
		}
		
		this.updateSgncmpldCntrdoc(ecntrId, athgId);

		Map<String, Object> signValueMap = Maps.newHashMap();
		signValueMap.put("joint_cert_sgn_val_uuid", UUID.randomUUID().toString());
		signValueMap.put("ecntr_uuid", ecntrId);
		signValueMap.put("sign_value", signatureValue);
		signValueMap.put("usr_id", usrId);
		signValueMap.put("sgndusr_typ_ccd", "VD");
		signValueMap.put("signer_biz_reg_no", spEcontractRepository.getVdBizRegNo());
		// 협력사 서명값 저장
		spEcontractRepository.insertSignValue(signValueMap);
		
		this.updateVendorSignY(ecntrId, null, null);

		String nextCntrStsCcd = this.getNextCntrStatus(ecntrId);
		String cntrId = (String) cntrInfo.get("cntr_uuid");
		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", cntrId);
		statusParam.put("cntr_sts_ccd", nextCntrStsCcd);
		contractStatusService.completeVendorSign(statusParam);
		spContractService.addStatusHistory(cntrId, nextCntrStsCcd, null);
		
		if(CntrConst.CNTR_STATUS.VENDOR_SIGN_COMPLETE.equals(nextCntrStsCcd)) {
			SignOrder sgnordTypCcd = SignOrder.valueOf((String) cntrInfo.get("sgnord_typ_ccd"));

			if(SignOrder.BUYER_VD.equals(sgnordTypCcd)) {
				spContractService.completeContract(cntrId);
			}else if(SignOrder.SIMUL_SGN.equals(sgnordTypCcd)) {
				econtractService.signEcontract(cntrInfo); // buyer 서명
			}
		}
		
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}
	
	/**
	 * 계약 완료 파일 업데이트
	 * @param ecntrId
	 * @param athgId
	 */
	public void updateSgncmpldCntrdoc(String ecntrId, String athgId) {
		Map paramMap = Maps.newHashMap();
		paramMap.put("ecntr_uuid", ecntrId);
		paramMap.put("sgncmpld_cntrdoc_athg_uuid", athgId);
		spEcontractRepository.updateCntrFileHash(paramMap);
	}

	/**
	 * 협력사 서명 여부 업데이트
	 * @param ecntrId
	 * @param vdCd
	 */
	public void updateVendorSignY(String ecntrId, String signUserId, String vdCd) {
		Map paramMap = Maps.newHashMap();
		paramMap.put("ecntr_uuid", ecntrId);
		paramMap.put("cntr_sgndusr_uuid", signUserId);
		paramMap.put("vd_cd", vdCd);
		paramMap.put("sgn_yn", "Y");
		spEcontractRepository.updateVendorSignYN(paramMap);
	}

	
	/**
	 * 협력사 서명상태 체크하여 다음 진행상태 반환
	 * @param ecntrId
	 * @return
	 */
	public String getNextCntrStatus(String ecntrId) {
		String vendorsSignState = this.checkVendorsSignState(ecntrId);
		String cntrStsCcd = "";
		if(CntrConst.VENDORS_SIGN_STATUS.ALL_SIGN.equals(vendorsSignState)) {
			cntrStsCcd = CntrConst.CNTR_STATUS.VENDOR_SIGN_COMPLETE;
		}else if(CntrConst.VENDORS_SIGN_STATUS.FIRST_SIGN.equals(vendorsSignState)){
			cntrStsCcd = CntrConst.CNTR_STATUS.MULTILATERAL_SIGN;
		}else if(CntrConst.VENDORS_SIGN_STATUS.ALL_NOT_SIGN.equals(vendorsSignState)){
			LOG.info("error");
		}
		return cntrStsCcd;
	}

	/**
	 * 협력사 서명상태 체크
	 * @param ecntrId
	 * @return
	 */
	public String checkVendorsSignState(String ecntrId) {
		Map paramMap = Maps.newHashMap();
		paramMap.put("ecntr_uuid", ecntrId);
		return spEcontractRepository.getVendorsSignState(paramMap);
	}

	private boolean verifySignatureForgery(String signValueJC, String hashValue) {
		String signSource = verificationProvider.getSource(signValueJC);
		if( signSource.equals(hashValue) ) {
			return true;
		}else {
			LOG.info("signSource : " + signSource + " ," + " hashValue : " +hashValue);
			return true;
		}
	}

	private Map<String, Object> verifySameCertificate(String signValueJC) {
		Map<String,Object> resultMap = Maps.newHashMap();
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		if("Y".equals(certMgt)){
			Map<String, String> certInfo = verificationProvider.getCertInfo(signValueJC);

			Map<String, Object> certMgtInfo = spEcontractRepository.findCertManagerInfo();

			// DN값 && 시리얼 번호 체크
			if(certMgtInfo == null ||
					!(certInfo.get("subject").toString().equals(certMgtInfo.get("cert_dm_nm").toString())) &&
							!(certInfo.get("serial").toString().equals(certMgtInfo.get("cert_seqno").toString()))){
				resultMap.put(Const.RESULT_STATUS, Const.NOT_EXIST);
				return resultMap;
			}

			// 인증서 유효한지 체크(만료일자(CERT_END_DATE))
			if("N".equals(certMgtInfo.get("cert_valid_yn"))){
				resultMap.put(Const.RESULT_STATUS, Const.USED);
				return resultMap;
			}
		}

		return resultMap;
	}

	private boolean checkSessionUser(String usrId) {
		Map<String, Object> userInfo = Auth.getCurrentUserInfo();
		String sessionUsrId = (String) userInfo.get("usr_id");
		if( sessionUsrId.equals(usrId) ) {
			return true;
		}else {
			return false;
		}
	}

	private boolean possibleSignSts(String cntrStsCcd) {
		if(CntrConst.CNTR_STATUS.SEND.equals(cntrStsCcd)
			|| CntrConst.CNTR_STATUS.VENDOR_CONFIRM.equals(cntrStsCcd)
			|| CntrConst.CNTR_STATUS.MULTILATERAL_SIGN.equals(cntrStsCcd)
			|| CntrConst.CNTR_STATUS.BUYER_REJECT.equals(cntrStsCcd)) {
			return true;
		}else {
			return false;
		}
	}

	public Map<String, Object> findExistSignEContractInfo(Map<String, Object> param) throws Exception {
		Map<String, Object> signInfo = Maps.newHashMap();
		Map<String, Object> userInfo = Auth.getCurrentUserInfo();

		Map<String, Object> signContentInfo = spEcontractRepository.getSignCntrContent(param);
		String cntrGrpCd = (String) signContentInfo.get("sgncmpld_cntrdoc_athg_uuid");
		FileItem fileItem = this.getFileItemByGrpCd(cntrGrpCd);
		String hashValue = SignatureUtil.getHashValueFromFile(fileItem);
		LOG.info("hashValue:" + hashValue);
		signContentInfo.put("hash_value", hashValue);

		String bizRegNo = spEcontractRepository.getVdBizRegNo();
		signContentInfo.put("bizregno", bizRegNo);
		signContentInfo.put("usr_id", userInfo.get("usr_id"));
		signContentInfo.put("callbackUrl" , "sp/edoc/contract/SpEcontractController/saveSignatureValue.do");
		/** html 제거 **/
		signContentInfo.remove("sign_source");
		signContentInfo.remove("content");

		signInfo.put("signContentInfo", signContentInfo);

		signInfo.put("_aesCipherKey", new AESCipherKey(keyGenerator.getKey(), keyGenerator.getPassPhrase(), keyGenerator.getIterationCount(), parameterGenerator.getIv()));
		return signInfo;
	}

	public Map findEcontractDetail(Map param) {
		Map<String, Object> result = Maps.newHashMap();
		//계약서 내용
		Map cntrCont = spEcontractRepository.cntrView(param);
		//부속서류 리스트
		List appFormList = spEcontractRepository.getCntrAppFormListInSts(param);

		result.put("appFormList", appFormList);
		result.put("cntrCont", cntrCont.get("content"));
		return result;
	}

	/**
	 * PDF 미리보기
	 * @param request
	 * @param response
	 * @param ecntrId
	 */
	public void previewPdfByEcntrId(HttpServletRequest request, HttpServletResponse response, String ecntrId) {
		Map<String, Object> pdfInfo = new HashMap<String, Object>();
		pdfInfo.put("ecntr_uuid", ecntrId);
		
		Map<String,Object> cntrInfo = spEcontractRepository.getCntrContent(pdfInfo);
		List<Map<String, Object>> appList = spEcontractRepository.getAppContent(pdfInfo);
		
		String orgnFileNm = cntrInfo.get("ecntr_no") + "-" + cntrInfo.get("ecntr_revno") + ".pdf";

		OutputStream ops = null;
		Writer writer = null;
		FileInputStream fis = null;
		byte[] pdfFileBytes;
		File pdfFile = null;

		try {
			String pdfFilePath = pdfMakerService.makePdfUsingHtml(cntrInfo, appList, false);
			pdfFile = new File(pdfFilePath);
			
			if(pdfFile.exists()) {
				fis = new FileInputStream(pdfFile);
				pdfFileBytes = IOUtils.toByteArray(fis);
				
				response.setContentType("application/pdf; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename="
						+ URLEncoder.encode(orgnFileNm, "UTF-8").replaceAll("\\+", "%20"));
				ops = response.getOutputStream();
				ops.write(pdfFileBytes);
				ops.flush();
			} else{
				writer = response.getWriter();
				response.setContentType("text/html");
				writer.write("PDF파일이 존재하지 않습니다.");
				writer.flush();
			}

		} catch (Exception e) {
			response.setHeader("Set-Cookie", "fileDownload=false; path=/");
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Content-Type","text/html; charset=utf-8");
			throw new CommonException(this.getClass().getName()+".previewPdfByEcntrId : " + e.getMessage(), e.toString());
		} finally {
			try{
				if(null != fis) fis.close();
				if(null != writer) writer.close();
				if(null != ops) ops.close();
				if(pdfFile.exists()) pdfFile.delete(); // 임시파일 삭제
			} catch (Exception e) {
				throw new CommonException(this.getClass().getName() + ".previewPdfByEcntrId() : Exception", e.getMessage());
			}
		}
	}

	public Map findEcontract(Map param) {
		//계약정보조회
		Map cntrInfo = spEcontractRepository.findContract(param);
		
		String ecntrId = (String) cntrInfo.get("ecntr_uuid");
		if(Strings.isNullOrEmpty(ecntrId)) {
			return Maps.newHashMap();
		}
		
		//서명대상여부 확인
		boolean signable = spEcontractRepository.checkSignableContractor(cntrInfo);
		cntrInfo.put("signable", signable);
		//서명여부 확인
		String signYn = spEcontractRepository.getSignCheck(cntrInfo);
		cntrInfo.put("sign_yn", signYn);
		
		String sgnMethCcd = (String) cntrInfo.get("cntr_sgnmeth_ccd");
		if(CntrConst.SIGN_METHOD.DOCUSIGN.equals(sgnMethCcd)) {
			//docusign 정보 조회
			Map docusignInfo = spEcontractEventPublisher.findDocusignContract(cntrInfo);
			if(docusignInfo != null) {
				cntrInfo.put("dsgn_uuid", docusignInfo.get("dsgn_uuid"));
				cntrInfo.put("dsgn_sts_ccd", docusignInfo.get("dsgn_sts_ccd"));
			}
		} else if(CntrConst.SIGN_METHOD.ADOBESIGN.equals(sgnMethCcd)) {
			//adobesign 정보 조회
			Map adobeSignInfo = spEcontractEventPublisher.findAdobeSignContract(cntrInfo);
			if(adobeSignInfo != null) {
				cntrInfo.put("asgn_uuid", adobeSignInfo.get("asgn_uuid"));
				cntrInfo.put("asgn_sts_ccd", adobeSignInfo.get("asgn_sts_ccd"));
				cntrInfo.put("agreement_id", adobeSignInfo.get("agt_id"));
			}
		}
		
		return cntrInfo;
	}

	public Map findContractByEcntrId(Map param) {
		String ecntrId = (String) param.get("ecntr_uuid");
		String cntrId = spEcontractRepository.getCntrUUID(ecntrId);
		
		Map searchParam = Maps.newHashMap();
		searchParam.put("cntr_uuid", cntrId);
		Map cntrInfo = spEcontractRepository.findContract(searchParam);
		
		return cntrInfo;
	}
	
}
