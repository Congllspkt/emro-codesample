package smartsuite.app.common.cert.pki.verification;

import com.crosscert.justoolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.common.cert.pki.SignatureUtil;
import smartsuite.exception.CommonException;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 2018. 8. 27.
 * @author sunghyun
 * ------------------------
 * 개정이력
 * 2018. 8. 27. sunghyun : 최초작성
 * 한국전자인증(Crosscert) 모듈 증명관련 구현체
 */
public class UsCrossCertVerificationProvider implements VerificationProvider{
	@Override
	public String encrypt(byte[] certbuf, int certlen, byte[] contentbuf, int contentlen) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String decrypt(byte[] deckeybuf, int deckeylen, byte[] certbuf, int certlen, byte[] envelopedbuf,
			int envelopedlen) {
		// TODO Auto-generated method stub
		return null;
	}

	private static final Logger LOG = LoggerFactory.getLogger(UsCrossCertVerificationProvider.class);
	private static UsCrossCertVerificationProvider instance = new UsCrossCertVerificationProvider();

	@Value("#{cert['cert.policy']}")
	private String certPolicy;
	@Value("#{cert['crosscert.cert.set.conf.path']}")
	private String certSetConfPath; //NOPMD
	@Value("#{cert['cert.license']}")
	private String certLicense;
	@Value("#{cert['client.toolkit.install.url']}")
	private String clientToolkitInstallUrl;

	@Value("#{cert['client.toolkit.url']}")
	private String clientToolkitUrl;
	@Value("#{cert['client.toolkit.multi.url']}")
	private String clientToolkitMultiUrl;

	@Value("#{cert['client.xml.toolkit.url']}")
	private String clientXmlToolkitUrl;

	justoolkit USToolkit = new justoolkit(); //NOPMD
	
	private void init() {


		try {
			USToolkit.init(certLicense);
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "init error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ":" + e.getMessage(), "Invalid License");
		}
	}

	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값
	 * @Return : String
	 * 서명값 검증 및 서명 원본 반환
	 */
	public String getSource(String signValue){
		String source = "";
		
		init();
		
		try {
			byte[] binSigndata = USToolkit.UTIL_Base64Decode(signValue);
			
			// SignedData 검증 (원문 획득)
			byte[] plainData = USToolkit.CMS_VerifySignedData(binSigndata);
			source = new String(plainData);
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "getSource error" + e.toString());
			throw new CommonException(this.getClass().getName() + ".getSource :" + e.getMessage(), "원문 추출 중 오류 발생");
		}finally {
			USToolkit.finish();
		}
		return source;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값, boolean 인증서유효성검사 여부
	 * @Return : void
	 * 서명값 및 인증서유효성을 검증
	 */
	public void verifySignValue(String signValue, boolean certVerification) {
		// 서명값 검증
		init();
		
		byte [] verifier = verifySignValue(signValue);
		
		try {
		// 인증서 유효성 검증 
			if(certVerification){
				validateCert(verifier);
			}
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "verifySignValue error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".verifySignValue :" + e.getMessage(), "서명검증 중 오류 발생");			
		}finally {
			USToolkit.finish();
		}
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값
	 * @Return : Map<String,String>
	 * 서명값을 검증하고, 서명값으로부터 인증서를 추출하여 반환
	 */
	public Map<String, String> getCertInfo(String signValue) {
		init();
		long cert = getCertificate(verifySignValue(signValue));
		Map<String, String> certInfo = makeCertInfoMap(cert);
		USToolkit.finish();
		return certInfo;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값
	 * @Return : byte[]
	 * Base64 인코딩된 서명값을 Base64 디코딩하여 반환
	 */
//	private byte[] decodeSignValue(String signValue){
//		byte[] decodedSignValue = null;
//		
//		try {
//			byte[] signValueBytes = signValue.getBytes("KSC5601");
//			decodedSignValue = SignatureUtil.decodeBase64(signValueBytes);
//		} catch (UnsupportedEncodingException e) {
//			throw new CommonException(this.getClass().getName() + ".getSource(String) :" + e.getMessage(), "fail to convert bytes (signValue)");
//		}
//		
//		return decodedSignValue;
//	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값
	 * @Return : Verifier
	 * 서명값 검증
	 */
	private byte[] verifySignValue(String signValue){
		byte[] binSigndata;
		try {
			binSigndata = USToolkit.UTIL_Base64Decode(signValue);
			
			// SignedData 검증 (원문 획득)
			byte[] plainData = USToolkit.CMS_VerifySignedData(binSigndata);
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "verifySignValue error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".verifySignValue :" + e.getMessage(), "원문 추출 중 오류 발생");
		}
		return binSigndata;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : Verifier
	 * @Return : Certificate
	 * 인증서 정보 추출
	 */
	private long getCertificate(byte[] binSigndata){
		String errMsg = "";
		int errCode = 0;
		long certobj;
		try {
			// SignedData 검증 (원문 획득)
			byte[] plainData = USToolkit.CMS_VerifySignedData(binSigndata);
			
			byte[] signedCert = USToolkit.CMS_GetCertWithSignedData(0, binSigndata);
			
			certobj = USToolkit.CERT_Init(signedCert);	

		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "getCertificate error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".validateCert : 인증서 검증 실패", "errorCode : " +  errCode  + " , errorMsg : " + errMsg);
		}
		return certobj;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : Verifier
	 * @Return : Certificate
	 * 인증서 유효성을 검사
	 */
	private boolean validateCert(byte[] verifier){
		
		String errMsg = "";
		int errCode = 0;
		try {
			byte[] signedCert = USToolkit.CMS_GetCertWithSignedData(0, verifier);
			
			long certobj = USToolkit.CERT_Init(signedCert);	
			
			if(checkCertPolicy(certobj)) {
				LOG.info("인증서 정책검증 성공");
			} else {
				errMsg = "인증서 정책검증 실패 <br>";
				LOG.error("인증서 정책검증 실패");
				return false;
			}
			
			USToolkit.CERT_Finalize(certobj);
			
			//인증서 유효성검증 요청		
			boolean bVerifyCert = USToolkit.CERT_VerifyCertificate(USToolkit.UST_CERT_VERIFY_CRL, signedCert);			
			
			if(bVerifyCert){
				LOG.info("인증서 유효성검증 성공");
				return true;
			} else {
				errCode = USToolkit.API_GetLastErrorCode();
				errMsg = "인증서 유효성검증 실패 : \r\n" + "LastErr : " + USToolkit.API_GetLastError() + "<br>" + "LastDebugErr : " + USToolkit.API_GetLastDebugError()+ "<br>";
				LOG.error("인증서 유효성검증 실패 : \r\n" + "LastErr : " + USToolkit.API_GetLastError() + "<br>" + "LastDebugErr : " + USToolkit.API_GetLastDebugError()+ "<br>");
				LOG.error("에러코드: "+ USToolkit.API_GetLastErrorCode() + "<br>");
				return false;
			}
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "validateCert error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".validateCert : 인증서 검증 실패", "errorCode : " +  errCode  + " , errorMsg : " + errMsg);
		}
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : Certifcate 인증서
	 * @Return : Map<String,String>
	 * 인증서 내용을 추출하여 Map형식으로 반환
	 */
	private Map<String,String> makeCertInfoMap(long certobj){
		Map<String, String> certInfo = new HashMap<String,String>();
		try {
			certInfo.put("version"       , USToolkit.CERT_GetVersion(certobj));            // 버전
			certInfo.put("serial"        , USToolkit.CERT_GetSerial(certobj));             // 일련번호 
			certInfo.put("issuer"        , USToolkit.CERT_GetIssuerDN(certobj));             // 발급자 DN
			certInfo.put("subject"       , USToolkit.CERT_GetSubjectDN(certobj));            // 주체자 DN
			certInfo.put("subjectAlgId"  , USToolkit.CERT_GetPublicKeyAlgorithm(certobj));       // 공개키 알고리즘
			certInfo.put("from"          , USToolkit.CERT_GetCertValidityNotBefore(certobj));               // 유효기간 시작
			certInfo.put("to"            , USToolkit.CERT_GetCertValidityNotAfter(certobj));                 // 유효기간 끝
			certInfo.put("signatureAlgId", USToolkit.CERT_GetSignatureAlgorithm(certobj));     // 서명 알고리즘
			certInfo.put("pubkey"        , USToolkit.CERT_GetPublicKeyInfo(certobj));             // 공개키
			certInfo.put("subjectAltName", USToolkit.CERT_GetSubjectAltName(certobj));     // 주체 대체 이름
			certInfo.put("keyusage"      , USToolkit.CERT_GetKeyUsage(certobj));           // 키 사용 용도
			certInfo.put("policy"        , USToolkit.CERT_GetCertPolicy(certobj));             // 보안 정책
			certInfo.put("authorityKeyId"   , USToolkit.CERT_GetAuthorityKeyIdentifier(certobj));    // 발급자 키 식별자
			SignatureUtil.printMap(certInfo);
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "makeCertInfoMap error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".makeCertInfoMap : 인증서 정보 추출 실패");
		}	
		return certInfo;
	}
	
	public void validateCert(Map<String,Object> certInfo , boolean certVerification){
		
		init();
		
		byte[] der = (byte[])certInfo.get("signCert.der");
		String errMsg = "";
		int errCode = 0;
		
		try {
			byte[] signedCert = USToolkit.CMS_GetCertWithSignedData(0, der);
			
			long certobj = USToolkit.CERT_Init(signedCert);	
			
			if(checkCertPolicy(certobj)) {
				LOG.info("인증서 정책검증 성공"+ "<br>");
			} else {
				errMsg = "인증서 정책검증 실패 <br>";
				throw new CommonException(this.getClass().getName()+".validateCert : 인증서 정책검증 실패", "rrorMsg : " + errMsg);
			}
			
			USToolkit.CERT_Finalize(certobj);
			
			//인증서 유효성검증 요청		
			boolean bVerifyCert = USToolkit.CERT_VerifyCertificate(USToolkit.UST_CERT_VERIFY_CRL, signedCert);			
			if(certVerification) {
				if(bVerifyCert){
					LOG.info("인증서 유효성검증 성공"+ "<br>");
					//return true;
				} else {
					errCode = USToolkit.API_GetLastErrorCode();
					errMsg = "인증서 유효성검증 실패 : \r\n" + "LastErr : " + USToolkit.API_GetLastError() + "<br>" + "LastDebugErr : " + USToolkit.API_GetLastDebugError()+ "<br>";
					LOG.error("인증서 유효성검증 실패 : \r\n" + "LastErr : " + USToolkit.API_GetLastError() + "<br>" + "LastDebugErr : " + USToolkit.API_GetLastDebugError()+ "<br>");
					LOG.error("에러코드: "+ USToolkit.API_GetLastErrorCode() + "<br>");
					throw new CommonException(this.getClass().getName()+".validateCert : 인증서 검증 실패", "errorCode : " +  errCode  + " , errorMsg : " + errMsg);
					//return false;
				}
			}
			
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "validateCert error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".validateCert : 인증서 검증 실패", "errorCode : " +  errCode  + " , errorMsg : " + errMsg);
		}finally {
			USToolkit.finish();
		}
		
//		//인증서 추출
//		if(!validateCert(der)) {
//			throw new CommonException(this.getClass().getName()+".validateCert(Verifier verifier) : fail to validate certificate");
//		}
//		//라이센스키 기본 위치는  윈도우 C:\Program Files\NPKI , 리눅스 /usr/local/NPKI이며 변경시 아래 소스를 주석 풀고 경로 작성
//		cert.SetConfPath(certSetConfPath); //CCLicense.key 파일 기본경로 변경 함수  
//		nRet = cert.ValidateCert(der, der.length, certPolicy, 1);
//		//인증서 유효성 검사
//		if(nRet != 0) {
//			throw new CommonException(this.getClass().getName()+".validateCert(Verifier verifier) : fail to validate certificate", "errorCode : " + cert.errcode + " , errorMsg : " + cert.errmessage);
//		}
		
	}
	
	/**
	 * @Author : daesung lee
	 * @Date   : 2019. 2. 28
	 * @Return : PrivateKey
	 * 파일 path와 비밀번호로 존재하는 signPri.key에서 PrivateKey 추출
	 */
	public void validateCertPasswd(Map<String,Object> certInfo){
		// privateKey 읽기
		init();
		
		byte[]	priKey  = (byte[])certInfo.get("signPri.key");
		String certPw = (String) certInfo.get("cert_pwd");
		// privateKey 복호화
		try {
			byte[] pkey = USToolkit.CERT_DecryptPrikey(certPw.getBytes(), priKey);
			if(pkey == null) {
				throw new CommonException(this.getClass().getName()+".validateCertPasswd(String certPath, String certPw) : fail to validate certificate", "errorCode : " + USToolkit.API_GetLastErrorCode() + " , errorMsg : " + USToolkit.API_GetLastError());
			}
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "validateCertPasswd error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".validateCertPasswd(String certPath, String certPw) : fail to validate certificate", "errorCode : " + USToolkit.API_GetLastErrorCode());
		}finally {
			USToolkit.finish();
		}
/*		
		PrivateKey privateKey = new PrivateKey();
		int rtn = privateKey.DecryptPriKey(certPw, priKey, priKey.length);
		if(rtn != 0){
			throw new CommonException(this.getClass().getName()+".validateCertPasswd(String certPath, String certPw) : fail to validate certificate", "errorCode : " + privateKey.errcode + " , errorMsg : " + privateKey.errmessage);
		}
*/
	}
	
	/**
	 * @Author : daesung lee
	 * @Date   : 2019. 2. 28
	 * @Param  : CertUtil
	 * @Return : Map<String,String>
	 * 인증서 내용을 추출하여 Map형식으로 반환
	 */
	public Map<String,String> getCertificatetInfo(Map<String,Object> certFileInfo){
		byte[] der = (byte[])certFileInfo.get("signCert.der");
		long certobj;
		Map<String,String> returnMap = new HashMap<String,String>();
		
		try {
			certobj = USToolkit.CERT_Init(der);
			returnMap = makeCertInfoMap(certobj);
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "getCertificatetInfo error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".getCertificatetInfo(Map<String,Object> certFileInfo) : fail to get certificate", "errorCode : " + USToolkit.API_GetLastErrorCode());
		}finally {
			USToolkit.finish();
		}
		
		return returnMap;
		
/*		
		Certificate cert = new Certificate();
		int nRet = cert.ExtractCertInfo(der, der.length);
		if(nRet != 0){
			throw new CommonException(this.getClass().getName()+".getCertificatetInfo(Map certFileINfo) : fail to extract certificate", "errorCode : " + cert.errcode + " , errorMsg : " + cert.errmessage);
		}
		return makeCertInfoMap(cert);
*/		

	}
	
	/**
	 * @Author : daesung lee
	 * @Date   : 2019. 4. 22
	 * @Param  : String 서명값, boolean 인증서유효성검사 여부 주민/사업자번호 getR 값을 통한 신원확인
	 * @Return : void
	 * 서명값 및 인증서유효성을 검증
	 */
	public void verifySignValue(String signValue, String rvalue, String ssn, boolean certVerification) {
		// 서명값 검증
		init();
		
		byte [] verifier = verifySignValue(signValue);
		
		String errMsg = "";
		int errCode = 0;
		try {
			byte[] signedCert = USToolkit.CMS_GetCertWithSignedData(0, verifier);
			
			byte[] binRval = USToolkit.UTIL_HexStringToBin(rvalue);
			boolean bVerifyRvalue = USToolkit.CERT_VerifyVID(signedCert, binRval, ssn);			
			
			long certobj = USToolkit.CERT_Init(signedCert);	
			
			if(checkCertPolicy(certobj)) {
				LOG.info("인증서 정책검증 성공"+ "<br>");
			} else {
				errMsg = "인증서 정책검증 실패 <br>";
				throw new CommonException(this.getClass().getName()+".validateCert : 인증서 정책검증 실패", "rrorMsg : " + errMsg);
			}
			
			USToolkit.CERT_Finalize(certobj);
			
			//인증서 유효성검증 요청		
			boolean bVerifyCert = USToolkit.CERT_VerifyCertificate(USToolkit.UST_CERT_VERIFY_CRL, signedCert);			
			
			if(bVerifyCert){
				LOG.info("인증서 유효성검증 성공"+ "<br>");
			} else {
				errCode = USToolkit.API_GetLastErrorCode();
				errMsg = "인증서 유효성검증 실패 : \r\n" + "LastErr : " + USToolkit.API_GetLastError() + "<br>" + "LastDebugErr : " + USToolkit.API_GetLastDebugError()+ "<br>";
				LOG.error("인증서 유효성검증 실패 : \r\n" + "LastErr : " + USToolkit.API_GetLastError() + "<br>" + "LastDebugErr : " + USToolkit.API_GetLastDebugError()+ "<br>");
				LOG.error("에러코드: "+ USToolkit.API_GetLastErrorCode() + "<br>");
				throw new CommonException(this.getClass().getName()+".validateCert : 인증서 검증 실패", "errorCode : " +  errCode  + " , errorMsg : " + errMsg);
			}
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "verifySignValue error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".validateCert : 인증서 검증 실패", "errorCode : " +  errCode  + " , errorMsg : " + errMsg);
		}finally {
			USToolkit.finish();
		}
/*		
		Verifier verifier = verifySignValue(signValue);
		Certificate cert = null;
		// 인증서 유효성 검증 
		if(certVerification){
			cert = validateCert(verifier);
			int nRet = cert.VerifyVID(verifier.certbuf, verifier.certlen, rvalue.getBytes(), rvalue.length(), ssn);
			if(nRet != 0) { //0이면 신원확인 성공
				throw new CommonException(this.getClass().getName()+".verifySignValue(String signValue, String rvalue, String ssn, boolean certVerification) : fail to validate certificate", "errorCode : " + cert.errcode + " , errorMsg : " + cert.errmessage);
			}
		}
*/		
	}
	
	/**
	 * @Author : daesung lee
	 * @Date   : 2019. 2. 28
	 * @Param  : CertUtil
	 * @Return : Map<String,String>
	 * 인증서 내용을 추출하여 Map형식으로 반환
	 */
	private boolean checkCertPolicy(long certobj){

		try {
			LOG.info("추출한 인증서 정책  : " + USToolkit.CERT_GetCertPolicy(certobj));
			LOG.info("허용한 인증서 정책  : " + certPolicy);
			
			if(certPolicy.indexOf(USToolkit.CERT_GetCertPolicy(certobj)) > -1) {
				LOG.info("사용 가능한 인정서 정책입니다.");
				return true;
			}else {
				LOG.info("사용 불가능한 인정서 정책입니다.");
				return false;
			}
			
		}catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "checkCertPolicy error : " + e.toString());
			return false;
		}	

		
/*		
		Certificate cert = new Certificate();
		int nRet = cert.ExtractCertInfo(der, der.length);
		if(nRet != 0){
			throw new CommonException(this.getClass().getName()+".getCertificatetInfo(Map certFileINfo) : fail to extract certificate", "errorCode : " + cert.errcode + " , errorMsg : " + cert.errmessage);
		}
		return makeCertInfoMap(cert);
*/		
		
	}

	@Override
	public String getCertPem(CertInfo certInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void verifySignXml(String signedXml, String kmCertFilePath, String kmPriFilePath, String certPw,
			String rvalue, String ssn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifySignXml(String signedXml) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyCertificate(byte[] certPerm, String rvalue, String ssn, boolean certVerification) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean verifyClientToolkitInstallComplete(String installStatus) {
		if("BEFORE".equals(installStatus)) {
			return false;
		}else if("COMPLETE".equals(installStatus)) {
			return true;
		}else{
			return false;
		}
	}
	@Override
	public String getClientToolkitInstallUrl() {
		return clientToolkitInstallUrl;
	}

	@Override
	public String getClientToolkitUrl() {
		return clientToolkitUrl;
	}

	@Override
	public String getClientToolkitMultiUrl() {
		return clientToolkitMultiUrl;
	}

	@Override
	public String getClientXmlToolkitUrl() {
		return clientXmlToolkitUrl;
	}
}

