package smartsuite.app.common.cert.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.auth.service.UserService;
import smartsuite.app.common.AttachService;
import smartsuite.app.common.cert.CertConst;
import smartsuite.app.common.cert.event.CertEventPublisher;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.common.cert.repository.CertMgtRepository;
import smartsuite.app.common.cert.validator.CertValidator;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.data.FloaterStream;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.security.core.crypto.AESCipherUtil;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 전자계약 서식관리 관련 처리를 하는 서비스 Class입니다.
 *
 * @author daesung lee
 * @see 
 * @FileName CertService.java
 * @package smartsuite.app.bp.edoc.contract
 * @since 2019.02.27
 * @변경이력 : [2019.02.27] daesung lee 최초작성
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class CertMgtService {

	private static final Logger LOG = LoggerFactory.getLogger(CertMgtService.class);
	
	@Value("#{cert['cert.path']}")
	private String certPath;
	
	@Value("#{cert['pfx.cert.path']}")
	private String pfxCertPath;
	
	@Value("#{cert['cert.pw']}")
	private String certPw;
	
	@Value("#{cert['cert.save.type']}")
	private String certSaveType;
	
	@Value("#{globalProperties['server.cls']}")
	private String serverCls;
	
	/** The sql session. */
	@Inject
	private CertMgtRepository certMgtRepository;
	
	@Inject
	private AttachService attachService;
	
	@Inject
	private UserService userService;

	@Inject
	CertEventPublisher certEventPublisher;
	
	@Inject
	CertValidator certValidator;
	
	@Inject
	StdFileService fileService;
	
	/**
	 * 인증서 대상 정보 조회
	 * 
	 * @author : daesung lee
	 * @param : the param
	 * @return the list
	 * @Date : 2019. 2. 27
	 * @Method Name : largeFindCertTargetList
	 */
	public FloaterStream largeFindCertTargetList(Map<String,Object> param){
		return certMgtRepository.largeFindCertTargetList(param);
	}
	
	/**
	 * 인증서 정보 조회
	 * 
	 * @author : daesung lee
	 * @param : the param
	 * @return the list
	 * @Date : 2019. 2. 27
	 * @Method Name : largeFindCertList
	 */
	public FloaterStream largeFindCertList(Map<String,Object> param){
		// 대용량 처리
		return certMgtRepository.largeFindCertList(param);
	}
	
	/**
	 * 인증서 저장
	 * 
	 * @author : daesung lee
	 * @param : the param
	 * @return the map
	 * @Date : 2019. 2. 27
	 * @Method Name : saveCert
	 */
	public ResultMap saveCert(Map<String,Object> param)throws IOException{
		Map resultData = Maps.newHashMap();
		
		param.put("athg_uuid", param.get("cert_athg_uuid"));
		List<Map<String,Object>> certList = attachService.findListAttach(param);
		param.put("athg_uuid", param.get("pfx_cert_athg_uuid"));
		List<Map<String,Object>> pfxCertList = attachService.findListAttach(param);
		param.put("athg_uuid", param.get("wtm_ct_img_athg_uuid"));
		List<Map<String,Object>> watermarkList = attachService.findListAttach(param);
		param.put("athg_uuid", param.get("wtm_auth_img_athg_uuid"));
		List<Map<String,Object>> watermarkSmallList = attachService.findListAttach(param);

		if(certList == null || certList.size() == 0 ||
				pfxCertList == null || pfxCertList.size() == 0){
			return ResultMap.NOT_EXISTS();
		}

		certList.addAll(pfxCertList);
		certList.addAll(watermarkList);
		certList.addAll(watermarkSmallList);

		//인증서 blog 형태로 저장
		for(Map row : certList) {
			try {
				FileItem fileItem = fileService.findFileItemWithContents((String) row.get("athf_uuid"), false);
				byte[] fileContent = fileItem.toByteArray();
				row.put("athf_orig_dat", fileContent);
				attachService.updateCertFileInfo(row);
			} catch (Exception e) {
				LOG.error(e.toString());
				return ResultMap.FAIL();
			}
		}
		
		Map<String,Object> certFileInfo = getCertFileInfo(param);
		certFileInfo.put("cert_pwd", param.get("cert_pwd"));
		
		//인증서 비밀번호 확인
		ResultMap validator = certValidator.validateCertPasswd(certFileInfo);
		if(!validator.isSuccess()) {
			return validator;
		}
		//인증서 검증 확인
		validator = certValidator.validateCert(certFileInfo);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		Map<String,String> certificateInfo = certValidator.getCertificatetInfo(certFileInfo);
		
		param.put("cert_nm", certificateInfo.get("issuer"));
		param.put("cert_plcy", certificateInfo.get("policy"));
		
		String certSd = (String)certificateInfo.get("from");
		String certEd = (String)certificateInfo.get("to");
		
		certSd = certSd.replace(".", ""); 
		certSd = certSd.replace("/", ""); 
		certSd = certSd.replace("-", ""); 
		certEd = certEd.replace(".", "");
		certEd = certEd.replace("/", "");
		certEd = certEd.replace("-", "");
		
		param.put("cert_st_dt", certSd.substring(0, 8));
		param.put("cert_exp_dt", certEd.substring(0, 8));
		
		param.put("cert_sno", certificateInfo.get("serial"));
		
		//패스워드 암호화 처리
		String password = (String)param.get("cert_pwd");
		String encPassword = getFixedEncrypt(password);

		param.put("cert_pwd", encPassword);
		param.put("usr_id", param.get("pic_id"));
		if(!Strings.isNullOrEmpty((String)param.get("pic_id"))) {
			Map<String, Object> userInfo = userService.findUserByUserId(param);
			param.put("pic_tel", userInfo.get("tel"));
			param.put("pic_mob", userInfo.get("mob"));
		}

		Map certInfo = certMgtRepository.findCertInfo(param);

		if(certInfo == null) {
			certMgtRepository.insertCertInfo(param);
		}else{
			certMgtRepository.updateCertInfo(param);
		}
		resultData.put("logic_org_cd", param.get("logic_org_cd"));
		resultData.put("logic_org_typ_ccd", param.get("logic_org_typ_ccd"));
		
		return ResultMap.SUCCESS(resultData);
	}
	/**
	 * 인증서 삭제
	 *
	 * @author : daesung lee
	 * @param : the param
	 * @return the map
	 * @Date : 2019. 3. 04
	 * @Method Name : deleteCertInfo
	 */
	public ResultMap deleteCertInfo(Map<String,Object> param){
		List<Map<String,Object>> deleteItem = (List<Map<String,Object>>)param.get("deleteItem");
		for(Map row :  deleteItem) {
			certMgtRepository.removeCertInfo(row);
		}
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 인증서 검증
	 * 
	 * @author : daesung lee
	 * @param : the param
	 * @return the map
	 * @Date : 2019. 3. 05
	 * @Method Name : verifyCertInfo
	 */
	public ResultMap verifyCertInfo(Map<String,Object> param){
		Map<String,Object> certInfo = this.getCertFileInfo(param);
		
		//인증서 검증 확인
		ResultMap validator = certValidator.validateCert(certInfo);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 인증서 파일 blog 조회
	 * 
	 * @author : daesung lee
	 * @param : the param
	 * @return the map
	 * @Date : 2019. 3. 08
	 * @Method Name : getFileCont
	 */
	private byte[] getFileCont(String filePath) throws IOException{
		return FileUtils.readFileToByteArray(new File(EdocStringUtil.correctFilePath(filePath)));
	}
	
	/**
	 * 인증서 정보 가져오는 함수
	 * 
	 * @author : daesung lee
	 * @param : the param
	 * @return the map
	 * @Date : 2019. 3. 05
	 * @Method Name : getCertFileInfo
	 */
	private Map<String,Object> getCertFileInfo(Map<String,Object> param){
		Map<String,Object> certFileInfo = Maps.newHashMap();
		
		//인증서 조회
		try {
			FileList fileList = null;
			if("LOCAL".equals(serverCls)
				|| "DEV".equals(serverCls) ) {
				param.put("athg_uuid", (String)param.get("cert_athg_uuid"));
				List<Map<String,Object>> attachList = attachService.findListAttach(param);
				for(Map attach : attachList) {
					Map fileParam = Maps.newHashMap();
					fileParam.put("athf_uuid", attach.get("athf_uuid"));
					Map attachItem = attachService.findAttachByAttId(fileParam);
					byte[] fileContent = (byte[]) attachItem.get("athf_orig_dat");
					certFileInfo.put((String)attach.get("athf_orig_nm"), fileContent);
					certFileInfo.put((String)attach.get("athf_orig_nm") + "_file_path", attach.get("athf_path"));
				}
			}else{
				fileList = fileService.findFileListWithContents((String) param.get("cert_athg_uuid"));
				for(FileItem fileItem : fileList.getItems()) {
					byte[] fileContent = fileItem.toByteArray();
					certFileInfo.put(fileItem.getName(), fileContent);
					certFileInfo.put(fileItem.getName() + "_file_path", fileItem.getReference());
				}
			}

		} catch (Exception e) {
			LOG.error("error: 인증서 정보 추출 실패" + e.toString() + this.getClass() + " getCertFileInfo");
			throw new CommonException(ErrorCode.FAIL, e);
		}

		//pfx 인증서 조회
		try {

			if("LOCAL".equals(serverCls)
					|| "DEV".equals(serverCls) ) {
				param.put("athg_uuid", (String)param.get("pfx_cert_athg_uuid"));
				List<Map<String,Object>> attachList = attachService.findListAttach(param);
				for(Map attach : attachList) {
					Map fileParam = Maps.newHashMap();
					fileParam.put("athf_uuid", attach.get("athf_uuid"));
					Map attachItem = attachService.findAttachByAttId(fileParam);
					byte[] fileContent = (byte[]) attachItem.get("athf_orig_dat");
					certFileInfo.put("pfx", fileContent);
					certFileInfo.put("pfx_file_path", attach.get("athf_path"));
				}
			}else{
				FileList fileList = fileService.findFileListWithContents((String) param.get("pfx_cert_athg_uuid"));
				for(FileItem fileItem : fileList.getItems()) {
					byte[] fileContent = fileItem.toByteArray();
					certFileInfo.put("pfx", fileContent);
					certFileInfo.put("pfx_file_path", fileItem.getReference());
				}
			}

		} catch (Exception e) {
			LOG.error("error: 인증서 정보 추출 실패" + e.toString() + this.getClass() + " getCertFileInfo");
			throw new CommonException(ErrorCode.FAIL, e);
		}

		return certFileInfo;
	}
	
	/**
	 * 고정키 사용 암호화
	 * 
	 * @author : daesung lee
	 * @param : String
	 * @return the String
	 * @Date : 2019. 3. 05
	 * @Method Name : getFixedEncrypt
	 */
	private String getFixedEncrypt(String plainText){
		AESCipherUtil aesChiperUtil = new AESCipherUtil();
		return aesChiperUtil.encrypt(plainText);
	}
	/**
	 * 고정키 사용 복호화
	 * 
	 * @author : daesung lee
	 * @param : String
	 * @return the String
	 * @Date : 2019. 3. 05
	 * @Method Name : getFixedEncrypt
	 */
	private String getFixedDecrypt(String encryptedText){
		AESCipherUtil aesChiperUtil = new AESCipherUtil();
		return aesChiperUtil.decrypt(encryptedText);
	}
	
	/**
	 * 운영조직에 맞는 인증서 정보 추출
	 *
	 * @author : daesung lee
	 * @param : the param
	 * @return the list
	 * @Date : 2019. 3.07
	 * @Method Name : getCertInfo
	 */
	public CertInfo getCertInfo(String logicOrgCd){
		if("DB".equals(certSaveType)) { //DB로 인증서를 저장한 경우
			return getCertInfoInDB(logicOrgCd);
		}else if("PROP".equals(certSaveType)) {	//EDOC.PROPERTIES경로로 인증서를 관리하는 경우
			return getCertInfoInProp();
		}else{
			return getCertInfoInDB(logicOrgCd);
		}
	}
	
	/**
	 * 조직, 조직유형으로 서버인증서 조회
	 *
	 * @author : daesung lee
	 * @param : the param
	 * @return the list
	 * @Date : 2019. 3.07
	 * @Method Name : getCertInfo
	 */
	public CertInfo getCertInfo(String logicOrgCd, String logicOrgTypCCD){
		if("DB".equals(certSaveType)) { //DB로 인증서를 저장한 경우
			return getCertInfoInDB(logicOrgCd, logicOrgTypCCD);
		}else if("PROP".equals(certSaveType)) {	//EDOC.PROPERTIES경로로 인증서를 관리하는 경우
			return getCertInfoInProp();
		}else{
			return getCertInfoInDB(logicOrgCd, logicOrgTypCCD);
		}
	}
	
	/**
	 * 인증서 DB에서 가져오기
	 * 
	 * @author : daesung lee
	 * @param : the String
	 * @return the CertInfo
	 * @Date : 2019. 3. 20
	 * @Method Name : getCertInfoInDB
	 */
	private CertInfo getCertInfoInDB(String LogicOrgCd){
		Map param = Maps.newHashMap();
		if(Strings.isNullOrEmpty(LogicOrgCd)) {
			throw new CommonException(ErrorCode.FAIL, new Exception(this.getClass().getName()+".getCertInfo: oorg_cd is null") );
		}
		param.put("logic_org_cd", LogicOrgCd);
		
		Map<String, Object> operOrgInfo = Maps.newHashMap();
		int cnt = 0;
		String certRegYn = CertConst.CERT_REG_N;
		
		while(CertConst.CERT_REG_N.equals(certRegYn)) {
			operOrgInfo = certMgtRepository.findOperOrgInfo(param); //인증서가 등록되어있는 운영조직들 검사

			param.put("logic_org_cd", operOrgInfo.get("parnode_logic_org_cd")); //상위운영조직으로 다시 셋팅

			if(CertConst.CERT_REG_N.equals(operOrgInfo.get("cert_reg_yn"))) {
				if("ROOT".equals(operOrgInfo.get("parnode_logic_org_cd"))){
					LOG.info(this.getClass().getName()+".getCertInfo: 조직정보에 등록된 인증서가 없습니다.");
					throw new CommonException("STD.EDO1083");
				}
			}else if(CertConst.CERT_REG_Y.equals(operOrgInfo.get("cert_reg_yn"))) {
				certRegYn = CertConst.CERT_REG_Y;
			}
			cnt++;
			if(cnt == 200) {//무한 루프 방지
				LOG.info(this.getClass().getName()+".getCertInfo: 조직정보에 등록된 인증서가 없습니다.");
				throw new CommonException("STD.EDO1083");
			}
		}
		Map certInfo = getCertFileInfo(operOrgInfo); //인증서 정보 조회

		String certPwd = getFixedDecrypt((String)operOrgInfo.get("cert_pwd"));
		return getCertInfo(certInfo, certPwd);
	}
	
	/**
	 * 인증서 DB에서 가져오기
	 * 
	 * @author : daesung lee
	 * @param : the String
	 * @return the CertInfo
	 * @Date : 2019. 3. 20
	 * @Method Name : getCertInfoInDB
	 */
	private CertInfo getCertInfoInDB(String logicOrgCd, String logicOrgTypCCD){
		Map param = Maps.newHashMap();
		if(Strings.isNullOrEmpty(logicOrgCd) || Strings.isNullOrEmpty(logicOrgTypCCD)) {
			//throw new EdocException(this.getClass().getName()+".getCertInfo: oorg_cd is null");
			throw new CommonException(ErrorCode.FAIL, new Exception(this.getClass().getName()+".getCertInfo: oorg_cd is null") );
		}

		param.put("logic_org_cd", logicOrgCd);
		param.put("logic_org_typ_ccd", logicOrgTypCCD);
		Map certInfo = certMgtRepository.findCertInfo(param);

		Map certFileInfo = getCertFileInfo(certInfo); //인증서 정보 조회

		String certPwd = getFixedDecrypt((String)certInfo.get("cert_pwd"));

		return getCertInfo(certFileInfo, certPwd);
	}
	
	/**
	 * 인증서 edoc.properties 에서 가져오기
	 * 
	 * @author : daesung lee
	 * @param : the String
	 * @return the String
	 * @Date : 2019. 3. 20
	 * @Method Name : getCertInfoInProp
	 */
	private CertInfo getCertInfoInProp(){
		byte[] signCert;
		byte[] signPri;
		byte[] kmCert;
		byte[] kmPri;
		byte[] pfx;
		try {
			signCert = getFileCont(certPath + CertConst.SIGN_CERT_DER);
			signPri = getFileCont(certPath + CertConst.SIGN_PRI_KEY);
			kmCert = getFileCont(certPath + CertConst.KM_CERT_DER);
			kmPri = getFileCont(certPath + CertConst.KM_PRI_KEY);
			pfx = getFileCont(pfxCertPath);
		} catch (IOException e) {
			LOG.error("error : 인증서경로에서 인증서 정보 추출 실패" + this.getClass() + "getCertInfoInProp " + e.toString());
			throw new CommonException(ErrorCode.FAIL, e);
		}
		return new CertInfo(signCert,signPri,kmCert,kmPri,pfx,certPw
				,certPath + CertConst.SIGN_CERT_DER
				,certPath + CertConst.SIGN_PRI_KEY
				,certPath + CertConst.KM_CERT_DER
				,certPath + CertConst.KM_PRI_KEY
				,pfxCertPath
				);
	}
	
	private CertInfo getCertInfo(Map certInfo, String certPw) {
		if(certInfo == null) {
			throw new CommonException("not certInfo");
		}

		if(certInfo.containsKey(CertConst.SIGN_CERT_DER_FILE_PATH)
			&& certInfo.get(CertConst.SIGN_CERT_DER_FILE_PATH) != null
			){
			return new CertInfo((byte[])certInfo.get(CertConst.SIGN_CERT_DER)
					, (byte[])certInfo.get(CertConst.SIGN_PRI_KEY)
					, (byte[])certInfo.get(CertConst.KM_CERT_DER)
					, (byte[])certInfo.get(CertConst.KM_PRI_KEY)
					, (byte[])certInfo.get(CertConst.PFX)
					, certPw
					, (String)certInfo.get(CertConst.SIGN_CERT_DER_FILE_PATH)
					, (String)certInfo.get(CertConst.SIGN_PRI_KEY_FILE_PATH)
					, (String)certInfo.get(CertConst.KM_CERT_DER_FILE_PATH)
					, (String)certInfo.get(CertConst.KM_PRI_KEY_FILE_PATH)
					, (String)certInfo.get(CertConst.PFX_FILE_PATH)
					);
		}else{
			return new CertInfo((byte[])certInfo.get(CertConst.SIGN_CERT_DER)
					, (byte[])certInfo.get(CertConst.SIGN_PRI_KEY)
					, (byte[])certInfo.get(CertConst.KM_CERT_DER)
					, (byte[])certInfo.get(CertConst.KM_PRI_KEY)
					, (byte[])certInfo.get(CertConst.PFX)
					, certPw );
		}
	}

	/**
	 * 인증서 정보 조회
	 *
	 * @author : daesung lee
	 * @param : the param
	 * @return the map
	 * @Date : 2019. 2. 27
	 * @Method Name : findOrgCertInfo
	 */
	public ResultMap findOrgCertInfo(Map<String, Object> param) {
		Map<String, Object> certInfo = certMgtRepository.findOrgCertInfo(param);

		Map resultData = Maps.newHashMap();

		if(verifyCertSame(certInfo)){
			certInfo.put("same_yn", "Y");
		}else{
			certInfo.put("same_yn", "N");
		}

		resultData.put("certInfo", certInfo);
		String watermarkType = certEventPublisher.findWatermarkType();
		resultData.put("watermarkType", watermarkType);
		return ResultMap.SUCCESS(resultData);
	}

	private boolean verifyCertSame(Map<String, Object> certInfo) {
		Map fileParam = Maps.newHashMap();
		fileParam.put("athg_uuid", certInfo.get("cert_athg_uuid"));
		List<Map<String,Object>> certUploadDateList = certMgtRepository.getCertFileUploadDate(fileParam);
		if(certUploadDateList.size() == 0){
			return false;
		}
		Map certUploadDateMap = certUploadDateList.get(0);

		fileParam.put("athg_uuid", certInfo.get("pfx_cert_athg_uuid"));
		List<Map<String,Object>> pfxUploadDateList = certMgtRepository.getCertFileUploadDate(fileParam);
		if(pfxUploadDateList.size() == 0){
			return false;
		}
		Map pfxUploadDateMap = pfxUploadDateList.get(0);

		Date certUploadDate = (Date)certUploadDateMap.get("upload_dttm");
		Date pfxUploadDate = (Date)pfxUploadDateMap.get("upload_dttm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		String certUploadDateStr = dateFormat.format(certUploadDate);
		String pfxUploadDateStr = dateFormat.format(pfxUploadDate);

		if(certUploadDateStr.equals(pfxUploadDateStr)){
			return true;
		}else{
			return false;
		}

	}

	/**
	 * 사업자번호 저장
	 *
	 * @param : the param
	 * @return the map
	 * @Method Name : saveBizRegNo
	 */
	public void saveBizRegNo(Map param) {
		certMgtRepository.saveBizRegNo(param);
	}
}