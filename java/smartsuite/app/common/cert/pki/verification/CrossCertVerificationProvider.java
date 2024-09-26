package smartsuite.app.common.cert.pki.verification;

import crosscert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.common.cert.pki.SignatureUtil;
import smartsuite.exception.CommonException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @since 2018. 8. 27.
 * @author sunghyun
 * ------------------------
 * 개정이력
 * 2018. 8. 27. sunghyun : 최초작성
 * 한국전자인증(Crosscert) 모듈 증명관련 구현체
 */

@Service
public class CrossCertVerificationProvider implements VerificationProvider{
	@Value("#{cert['cert.policy']}")
	private String certPolicy;

	@Value("#{cert['cert.crl.path']}")
	private String certSetConfPath;

	@Value("#{cert['client.toolkit.install.url']}")
	private String clientToolkitInstallUrl;

	@Value("#{cert['client.toolkit.url']}")
	private String clientToolkitUrl;

	@Value("#{cert['client.toolkit.multi.url']}")
	private String clientToolkitMultiUrl;

	@Value("#{cert['client.xml.toolkit.url']}")
	private String clientXmlToolkitUrl;

	@Value("#{crosscert_config['xpath_query']}")
	private String xpathQuery;

	@Value("#{crosscert_config['policy_oid']}")
	private String policyOid;

	@Value("#{crosscert_config['verify_option']}")
	private String verifyOption;

	private static final Logger LOG = LoggerFactory.getLogger(CrossCertVerificationProvider.class);

	private static CrossCertVerificationProvider instance = new CrossCertVerificationProvider();
	private final Properties prop = new Properties();

	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값
	 * @Return : String
	 * 서명값 검증 및 서명 원본 반환
	 */
	public String getSource(String signValue) {
		Verifier verifier = verifySignValueRtnVerifier(signValue);
		return new String(verifier.contentbuf);
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
		Verifier verifier = verifySignValueRtnVerifier(signValue);

		// 인증서 유효성 검증 
		if(certVerification){
			validateCert(verifier);
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
		Certificate cert = getCertificate(verifySignValueRtnVerifier(signValue));
		Map<String, String> certInfo = makeCertInfoMap(cert);
		
		return certInfo;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값
	 * @Return : byte[]
	 * Base64 인코딩된 서명값을 Base64 디코딩하여 반환
	 */
	private byte[] decodeSignValue(String signValue){
		byte[] decodedSignValue = null;
		
		try {
			byte[] signValueBytes = signValue.getBytes("KSC5601");
			decodedSignValue = SignatureUtil.decodeBase64(signValueBytes);
		} catch (UnsupportedEncodingException e) {
			LOG.error("ERROR : 서명값 디코딩 실패 " + this.getClass().getName() + ".getSource(String) :" + e.getMessage(), "fail to convert bytes (signValue)");
			throw new CommonException(this.getClass().getName() + ".getSource(String) :" + e.getMessage(), "fail to convert bytes (signValue)");
		}
		
		return decodedSignValue;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값
	 * @Return : Verifier
	 * 서명값 검증
	 */
	private Verifier verifySignValueRtnVerifier(String signValue){
		byte[] decodedSignValue = decodeSignValue(signValue);
		
		Verifier verifier = new Verifier();
		int rtn = verifier.VerSignedData(decodedSignValue, decodedSignValue.length);
		if(rtn != 0){
			throw new CommonException(this.getClass().getName()+".getSource(String signValue) : fail to verify signValue", "errorCode : " + verifier.errcode + " , errorMsg : " + verifier.errmessage);
		}
		
		return verifier;
	}

	@Override
	public String getClientXmlToolkitUrl() {
		return clientXmlToolkitUrl;
	}

	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : Verifier
	 * @Return : Certificate
	 * 인증서 정보 추출
	 */
	private Certificate getCertificate(Verifier verifier){
		Certificate cert = new Certificate();
		int rtn = cert.ExtractCertInfo(verifier.certbuf, verifier.certlen);
		if(rtn != 0){
			throw new CommonException(this.getClass().getName()+".Certificate(Verifier verifier) : fail to extract certificate", "errorCode : " + verifier.errcode + " , errorMsg : " + verifier.errmessage);
		}
		return cert;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : Verifier
	 * @Return : Certificate
	 * 인증서 유효성을 검사
	 */
	private Certificate validateCert(Verifier verifier){
		Certificate cert = new Certificate();
		cert.SetConfPath(certSetConfPath);
		int rtn = cert.ValidateCert(verifier.certbuf, verifier.certlen, certPolicy, 1);
		if(rtn!=0){
			throw new CommonException(this.getClass().getName()+".validateCert(Verifier verifier) : fail to validate certificate errorMsg : " + cert.errmessage, "errorCode : " + cert.errcode + " , errorMsg : " + cert.errmessage);
		}

		return cert;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : Certifcate 인증서
	 * @Return : Map<String,String>
	 * 인증서 내용을 추출하여 Map형식으로 반환
	 */
	private Map<String,String> makeCertInfoMap(Certificate cert){
		Map<String, String> certInfo = new HashMap<String,String>();
		certInfo.put("version"       , cert.version);            // 버전
		certInfo.put("serial"        , cert.serial);             // 일련번호 
		certInfo.put("issuer"        , cert.issuer);             // 발급자 DN
		certInfo.put("subject"       , cert.subject);            // 주체자 DN
		certInfo.put("subjectAlgId"  , cert.subjectAlgId);       // 공개키 알고리즘
		certInfo.put("from"          , cert.from);               // 유효기간 시작
		certInfo.put("to"            , cert.to);                 // 유효기간 끝
		certInfo.put("signatureAlgId", cert.signatureAlgId);     // 서명 알고리즘
		certInfo.put("pubkey"        , cert.pubkey);             // 공개키
		certInfo.put("signature"     , cert.signature);          // 서명값
		certInfo.put("issuerAltName" , cert.issuerAltName);      // 발급자 대체 이름
		certInfo.put("subjectAltName", cert.subjectAltName);     // 주체 대체 이름
		certInfo.put("keyusage"      , cert.keyusage);           // 키 사용 용도
		certInfo.put("policy"        , cert.policy);             // 보안 정책
		certInfo.put("basicConstraint"  , cert.basicConstraint);   // 기본 제한
		certInfo.put("policyConstraint" , cert.policyConstraint);  // 정책 제한
		certInfo.put("distributionPoint", cert.distributionPoint); // CRL 배포 지점
		certInfo.put("authorityKeyId"   , cert.authorityKeyId);    // 발급자 키 식별자
		certInfo.put("subjectKeyId"     , cert.subjectKeyId);      // 주체 키 식별자
		SignatureUtil.printMap(certInfo);
		
		return certInfo;
	}
	
	public void validateCert(Map<String,Object> certInfo, boolean certVerification){
		byte[] der = (byte[])certInfo.get("signCert.der");
		Certificate cert = new Certificate();
		int nRet = cert.ExtractCertInfo(der, der.length);
		
		//인증서 추출
		if(nRet != 0) {
			throw new CommonException(this.getClass().getName()+".validateCert(Verifier verifier) : fail to validate certificate errorCode :" + cert.errcode +", error msg : " + cert.errmessage, "errorCode : " + cert.errcode + " , errorMsg : " + cert.errmessage);
		}
		//라이센스키 기본 위치는  윈도우 C:\Program Files\NPKI , 리눅스 /usr/local/NPKI이며 변경시 아래 소스를 주석 풀고 경로 작성
		cert.SetConfPath(certSetConfPath); //CCLicense.key 파일 기본경로 변경 함수  
		
		if(certVerification){ // 인증서 유효성 검증
			nRet = cert.ValidateCert(der, der.length, certPolicy, 1); //인증서 유효성 검사
			if(nRet != 0) {
				throw new CommonException(this.getClass().getName()+".validateCert(Verifier verifier) : fail to validate certificate errorCode :" + cert.errcode +", error msg : " + cert.errmessage, "errorCode : " + cert.errcode + " , errorMsg : " + cert.errmessage);
			}
		}
	}
	
	/**
	 * @Author : daesung lee
	 * @Date   : 2019. 2. 28
	 * @Return : PrivateKey
	 * 파일 path와 비밀번호로 존재하는 signPri.key에서 PrivateKey 추출
	 */
	public void validateCertPasswd(Map<String,Object> certInfo){
		// privateKey 읽기
		byte[]	priKey  = (byte[])certInfo.get("signPri.key");
		String certPw = (String) certInfo.get("cert_pwd");
		// privateKey 복호화
		PrivateKey privateKey = new PrivateKey();
		int rtn = privateKey.DecryptPriKey(certPw, priKey, priKey.length);
		if(rtn != 0){
			throw new CommonException(this.getClass().getName()+".validateCertPasswd(String certPath, String certPw) : fail to validate certificate errorCode : " + privateKey.errcode + ", errorMsg:" + privateKey.errmessage, "errorCode : " + privateKey.errcode + " , errorMsg : " + privateKey.errmessage);
		}
	
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
		
		Certificate cert = new Certificate();
		int nRet = cert.ExtractCertInfo(der, der.length);
		if(nRet != 0){
			throw new CommonException(this.getClass().getName()+".getCertificatetInfo(Map certFileINfo) : fail to extract certificate", "errorCode : " + cert.errcode + " , errorMsg : " + cert.errmessage);
		}
		return makeCertInfoMap(cert);
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
		Verifier verifier = verifySignValueRtnVerifier(signValue);
		Certificate cert = null;
		// 인증서 유효성 검증 
		if(certVerification){
			cert = validateCert(verifier);
			int nRet = cert.VerifyVID(verifier.certbuf, verifier.certlen, rvalue.getBytes(), rvalue.length(), ssn);
			if(nRet != 0) { //0이면 신원확인 성공
				throw new CommonException(this.getClass().getName()+".verifySignValue(String signValue, String rvalue, String ssn, boolean certVerification) : fail to validate certificate , errorCode : " + cert.errcode + " , errorMsg : " + cert.errmessage, "errorCode : " + cert.errcode + " , errorMsg : " + cert.errmessage);
			}
		}
	}
	/**
	 * @Author : daesung lee
	 * @Date   : 2021. 7. 23
	 * @Param  : 인증서 정보
	 * @Return : 암호용 인증서 pem 형식으로 변환하여 리턴
	 * 암호용 인증서 pem형식으로 변환
	 */
	@Override
	public String getCertPem(CertInfo certInfo) {
		Base64 base64 = new Base64();
		byte[] kmCertByte = certInfo.getKmCert();
		int nRet = base64.Encode(kmCertByte, kmCertByte.length);
		
		if(nRet != 0) {
			LOG.error(this.getClass() + "public String getCertPem(CertInfo certInfo) error : " + base64.errcode + ", error msg : " + base64.errmessage);
			throw new CommonException(this.getClass() + "public String getCertPem(CertInfo certInfo) error : " + base64.errcode + ", error msg : " + base64.errmessage);
		}
		return new String(base64.contentbuf);
	}

	/**
	 * @Author : daesung lee
	 * @Date   : 2021. 7. 23
	 * @Param  : 서명한xml, 암호용 인증서 정보, 암호용 인증서 키, 암호용인증서 비밀번호, rvalue, 사업자번호
	 * @Return : 
	 * 서명한 xml 검증
	 */
	@Override
	public void verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) {



	    // 2022.12.13 java11 사용시 org.w3c.dom import 에러로 주석 처리함
	    /* Document document = null;
		int nRet = 0;
		try {
			prop.setProperty("xpath_query", xpathQuery);
			prop.setProperty("policy_oid", policyOid);
			prop.setProperty("verify_option", verifyOption);

			TaxUtilBin taxUtil = new TaxUtilBin(prop);
			document = taxUtil.getDocumentXmlData(signedXml);
			boolean validFlag = true; 
			//xml 데이터 검증
			validFlag = taxUtil.verifyXML(document);
			
			if(!validFlag){	//xml 데이터 검증 실패
				LOG.error(this.getClass() + " public void verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + taxUtil.getError_code() + " , error msg : " + taxUtil.getError_msg() );
				throw new CommonException(this.getClass().getName()+".verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + taxUtil.getError_code() + " , error msg : " + taxUtil.getError_msg() ); 
			}else {
				LOG.info("XML 검증 성공");
				Certificate certificate = new Certificate();
				certificate = taxUtil.getCertificate();
				
				if(certificate != null) {
					LOG.info("인증서 DN : " + certificate.subject); 
					LOG.info("유효기간 시작일 : " + certificate.from);
					LOG.info("유효기간 종료일 : " + certificate.to); 
				}
				else { 
					LOG.error(this.getClass() + " public void verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + certificate.errcode + " , error msg : " + certificate.errmessage );
					throw new CommonException(this.getClass().getName()+".verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + certificate.errcode + " , error msg : " + certificate.errmessage );
				}
				
				Base64 base64 = new Base64();
				Node x509Certificate = document.getElementsByTagName("ds:X509Certificate").item(0);
				
				String userCert = "";
				NodeList useCertNodeList = x509Certificate.getChildNodes();
				Node k2 = useCertNodeList.item(0); 
				userCert = k2.getNodeValue().replaceAll("\n", "");
				LOG.info("추출한 인증서 == >" + userCert);
				nRet = base64.Decode(userCert.getBytes(), userCert.getBytes().length);
				
				LOG.info(" CrossCertVerificationProvider.verifySignXml.nRet :" + nRet);
				
				if (nRet != 0){ //실패
					LOG.error(this.getClass() + " public void verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + base64.errcode + " , error msg : " + base64.errmessage );
					throw new CommonException(this.getClass().getName()+".verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + base64.errcode + " , error msg : " + base64.errmessage ); 
				}
				
				Decrypt decrypt = new Decrypt();
				byte[] decRandombuf = null;
				String strdecRandom = "";
				
				nRet = decrypt.GetRFromEnvelnEncData(kmCertFilePath, kmPriFilePath, pw.getBytes(), rvalue.getBytes(), 2);
				
				LOG.info(" CrossCertVerificationProvider.verifySignXml.nRet2 :" + nRet); 
				
				if (nRet == 0) { //성공
					decRandombuf = decrypt.contentbuf;
					strdecRandom = new String(decRandombuf);
					LOG.info("decrypt R Value : " + strdecRandom);
					//신원확인
					nRet = certificate.VerifyVID(base64.contentbuf, base64.contentlen, strdecRandom.getBytes(), strdecRandom.getBytes().length, ssn);
					
					if(nRet != 0) { //0이면 신원확인 성공 
						LOG.error(this.getClass() + " public void verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + certificate.errcode + " , error msg : " + certificate.errmessage );
						throw new CommonException(this.getClass().getName()+".verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + certificate.errcode + " , error msg : " + certificate.errmessage ); 
					}
				} else { 
					LOG.error("decrypt.GetRFromEnvelnEncData nRet base64 : fail"+ "<br>");
					LOG.error("decrypt.GetRFromEnvelnEncData errcode : " + Integer.toHexString(decrypt.errcode) + " " + decrypt.errmessage + " " + decrypt.errdetailmessage);
					throw new CommonException(this.getClass().getName()+".verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + decrypt.errcode + " , error msg : " + decrypt.errmessage ); 
				}
			}
		}catch(Exception e) {
			LOG.error(this.getClass() + " public void verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + e.getMessage());
			throw new CommonException(this.getClass().getName()+".verifySignXml(String signedXml, String kmCertFilePath , String kmPriFilePath, String pw, String rvalue, String ssn) error:" + e.getMessage());
		}*/
	}

	/**
	 * @Author : daesung lee
	 * @Date   : 2021. 7. 23
	 * @Param  : 서명한 xml
	 * @Return : 
	 * 서명한 xml 검증 (서버 서명용)
	 */
	@Override
	public void verifySignXml(String signedXml) {
	    // 2022.12.13 java11 사용시 org.w3c.dom import 에러로 주석 처리함
		/*Document document = null;
		try { 
			TaxUtilBin taxUtil = new TaxUtilBin(prop);
			document = taxUtil.getDocumentXmlData(signedXml);
			boolean validFlag = true;
			validFlag = taxUtil.verifyXML(document);
			
			if(!validFlag) {	//xml 검증실패
				LOG.error(this.getClass() + "public void verifySignXml(String signedXml) error code : " + taxUtil.getError_code() + " , error msg : " + taxUtil.getError_msg());
				throw new CommonException(this.getClass() + "public void verifySignXml(String signedXml) error code : " + taxUtil.getError_code() + " , error msg : " + taxUtil.getError_msg());
			}else {
				LOG.info("서명검증, 인증서검증 성공!");
			}
		} catch(Exception e) {
			LOG.error(this.getClass() + "public void verifySignXml(String signedXml) error  : " + e.toString());
			throw new CommonException(this.getClass() + "public void verifySignXml(String signedXml) error : " + e.toString());
		}*/
	 }

	@Override
	public void verifyCertificate(byte[] certPerm, String rvalue, String ssn, boolean certVerification) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String encrypt(byte[] certbuf, int certlen, byte[] contentbuf, int contentlen) {
		Encrypt encrypt = new Encrypt();
		
		
		int nRet = encrypt.EncEnvelopedData(certbuf,certlen,contentbuf,contentlen);
		
		if (nRet != 0)
		{
			LOG.info("암호화1 에러내용 : " + encrypt.errmessage);
			LOG.info("암호화1 에러코드 : " + encrypt.errcode);
			//return;
		}
		LOG.info("<br><br> " );
		
		LOG.info("암호문(PKCS) 길이 : " + encrypt.envelopedlen  + "<br>" );
		
		String EncString = "";

		Base64 CBase64 = new Base64();
							  //바이트 배열     //바이트 배열길이
		nRet = CBase64.Encode(encrypt.envelopedbuf, encrypt.envelopedlen);
		if(nRet==0) 
		{
			LOG.info("암호화값 Base64 Encode 결과 : 성공<br>") ;
			EncString = new String(CBase64.contentbuf);
			LOG.info("암호화값 Base64 Decode 값 : " + EncString + "<br>끝") ;
		}
		else
		{
			LOG.info("암호화값 Base64 Decode 결과 : 실패<br>") ;
			LOG.info("에러내용 : " + CBase64.errmessage + "<br>");
			LOG.info("에러코드 : " + CBase64.errcode + "<br>");
		}
		
		return EncString;
	}
	
	@Override
	public String decrypt(byte[] deckeybuf, int deckeylen, byte[] certbuf, int certlen, byte[] envelopedbuf, int envelopedlen) {
		PrivateKey CPrivateKey = new PrivateKey(); 
		int nRet = CPrivateKey.DecryptPriKey("crosscert12!@", deckeybuf, deckeylen);
		String sOrgData2=null;
		if (nRet != 0)
		{
			LOG.info("에러내용 : " + CPrivateKey.errmessage + "<br>");
			LOG.info("에러코드 : " + CPrivateKey.errcode + "<br>");
		}
		
		Base64 CBase64 = new Base64(); 
		nRet = CBase64.Decode(envelopedbuf,envelopedbuf.length);
		Decrypt decrypt = new Decrypt(); 
		nRet = decrypt.DecEnvelopedData(CPrivateKey.prikeybuf,CPrivateKey.prikeylen, certbuf, certlen,CBase64.contentbuf, CBase64.contentlen);
		
		if (nRet != 0)
		{
			LOG.info("복호화1 에러내용 : " + decrypt.errmessage);
			LOG.info("복호화1 에러코드 : " + decrypt.errcode);
			//return;
		}
		else
		{
			LOG.info("복호문(PKCS) 결과 : " + Integer.toHexString(nRet) + "<br>");
			try {
				sOrgData2 = new String( decrypt.contentbuf, "KSC5601");
			} catch (UnsupportedEncodingException e) {
				LOG.error("error : 복호화 실패" + this.getClass() + "decrypt(byte[] deckeybuf, int deckeylen, byte[] certbuf, int certlen, byte[] envelopedbuf, int envelopedlen)" + e.toString());
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			LOG.info("원문 : " + sOrgData2 + "<br>");
			LOG.info("원문길이 : String - " + sOrgData2.length() + ", byte - " + decrypt.contentlen );
		}
		
		//LOG.info("암호화된 값 복호화 : " + sDecData + "₩n");
		return sOrgData2;
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
}
