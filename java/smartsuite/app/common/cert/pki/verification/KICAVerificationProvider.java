package smartsuite.app.common.cert.pki.verification;

import com.google.common.base.Strings;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import signgate.crypto.util.CertUtil;
import signgate.crypto.util.PKCS7Util;
import signgate.sgic.xml.util.XmlSecu;
import signgate.sgic.xml.util.XmlSignature;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.common.cert.pki.SignatureUtil;
import smartsuite.exception.CommonException;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @since 2018. 8. 27.
 * @author sunghyun
 * ------------------------
 * 개정이력
 * 2018. 8. 27. sunghyun : 최초작성
 * 한국정보인증(KICA)모듈 증명관련 구현체
 */
@Service
public class KICAVerificationProvider implements VerificationProvider{

	static final Logger LOG = LoggerFactory.getLogger(KICAVerificationProvider.class);

	private static KICAVerificationProvider instance = new KICAVerificationProvider();
	@Value("#{cert['cert.policy']}")
	private String certPolicy;

	@Value("#{cert['client.toolkit.install.url']}")
	private String clientToolkitInstallUrl;

	@Value("#{cert['client.toolkit.url']}")
	private String clientToolkitUrl;

	@Value("#{cert['client.xml.toolkit.url']}")
	private String clientXmlToolkitUrl;
	private String result;
	private String resultmsg;
	@Value("#{cert['cert.proxy.server.yn']}")
	private String certProxyServerYn;
	@Value("#{cert['cert.proxy.server.url']}")
	private String certProxyServerUrl;
	@Value("#{cert['cert.crl.path']}")
	private String certCrlPath;

	@Override
	public String getClientXmlToolkitUrl() {
		return clientXmlToolkitUrl;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값
	 * @Return : String
	 * 서명값 검증 및 서명 원본 반환
	 */
	@Override
	public String getSource(String signValue) {
		PKCS7Util pkcs7Util = verifySignValueRtnPKCS7Util(signValue);
		return new String(pkcs7Util.getRecvData());
	}

	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값, boolean 인증서유효성검사 여부
	 * @Return : void
	 * 서명값 및 인증서유효성을 검증
	 */
	@Override
	public void verifySignValue(String signValue, boolean certVerification) {
		PKCS7Util pkcs7Util = verifySignValueRtnPKCS7Util(signValue);
		try {
			// 인증서 유효성 검증
			if(certVerification){
				verifyCert(pkcs7Util);
			}
		}catch(Exception e) {
			LOG.error("ERROR : 서명값 검증 실패" + this.getClass() + "verifySignValue(String signValue, boolean certVerification)" + e.toString());
			throw new CommonException("KICAVerificationProvider.java verifySignValue() 인증서 검증 실패 : " + e.toString());
		}
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값
	 * @Return : Map<String,String>
	 * 서명값을 검증하고, 서명값으로부터 인증서를 추출하여 반환
	 */
	@Override
	public Map<String, String> getCertInfo(String signValue) {
		CertUtil cu = getCertificate(verifySignValueRtnPKCS7Util(signValue));
		return makeCertInfoMap(cu);
	}
	
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : PKCS7Util
	 * @Return : void
	 * 인증서 유효성 검증 및 정책(oid) 검사
	 */
	private void verifyCert(PKCS7Util pkcs7Util) throws Exception{
		CertUtil cu = getCertificate(pkcs7Util);
		boolean certValid;
		
		if(certProxyServerYn.equals("Y")) {
			String pem = cu.derToPem();
			certValid = verifyCertKicaUrl(pem);
			if(!certValid){
				throw new CommonException(this.getClass().getName()+".verifyCert() : fail to validate certificate", "errorMsg : " + resultmsg);
			}			
		}else {
			certValid = pkcs7Util.isValidCertificate();
			if(!certValid){
				throw new CommonException(this.getClass().getName()+".verifyCert() : fail to validate certificate", "errorMsg : " + pkcs7Util.getErrorMsg());
			}			
		}

		boolean oidValid = cu.isValidPolicyOid(certPolicy.split("\\|"));
		if(!oidValid){
			throw new CommonException(this.getClass().getName()+".verifyCert() : fail to validate policy oid", "errorMsg : " + cu.getErrorMsg());
		}
	}
	
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : PKCS7Util
	 * @Return : CertUtil
	 * 인증서객체 추출
	 */
	@SuppressWarnings("rawtypes")
	private CertUtil getCertificate(PKCS7Util pkcs7Util){
		Set certSet = pkcs7Util.getCertificateSet();
		Iterator it = certSet.iterator();
		
		CertUtil cu = null;
		try{
			X509Certificate cert = (X509Certificate) it.next();
			byte[] signCert = cert.getEncoded();
			
			cu = new CertUtil(signCert);
		}catch(Exception e){
			LOG.error("error : 인증서 추출 실패" + this.getClass() + "getCertificate(PKCS7Util pkcs7Util)");
			throw new CommonException(this.getClass().getName()+".verifyCert() : " + e.getMessage(), "fail to read Certification from signValue");
		}
		
		return cu;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : String 서명값
	 * @Return : PKCS7Util
	 * 서명값 검증 
	 */
	private PKCS7Util verifySignValueRtnPKCS7Util(String signValue){
		PKCS7Util pkcs7Util = new PKCS7Util();
		if(!pkcs7Util.verify(signValue)) {
			throw new CommonException(this.getClass().getName()+".verifySignValue() : fail to verify signValue", "errorMsg : " + pkcs7Util.getErrorMsg());
		}
		
		return pkcs7Util;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 27.
	 * @Param  : CertUtil
	 * @Return : Map<String,String>
	 * 인증서 내용을 추출하여 Map형식으로 반환
	 */
	private Map<String,String> makeCertInfoMap(CertUtil cu){
		Map<String, String> certInfo = new HashMap<String,String>();
		certInfo.put("version"       , String.valueOf(cu.getVersion()));  // 버전
		certInfo.put("serial"        , cu.getSerialNumber());             // 일련번호 
		certInfo.put("issuer"        , cu.getIssuerDN());                 // 발급자 DN
		certInfo.put("subject"       , cu.getSubjectDN());                // 주체자 DN
		certInfo.put("subjectAlgId"  , cu.getPublicKey().getAlgorithm()); // 공개키 알고리즘
		certInfo.put("from"          , cu.getNotBefore());                // 유효기간 시작
		certInfo.put("to"            , cu.getNotAfter());                 // 유효기간 끝
		certInfo.put("signatureAlgId", cu.getSigAlgName());               // 서명 알고리즘
		certInfo.put("pubkey"        , cu.getPublicKey().toString());     // 공개키
		certInfo.put("issuerAltName" , cu.getIssuer().getName());         // 발급자 대체 이름
		certInfo.put("subjectAltName", cu.getSubject().getName());        // 주체 대체 이름
		certInfo.put("policy"        , cu.getPolicyOid());                // 보안 정책
		certInfo.put("distributionPoint", cu.getCrlDP());                 // CRL 배포 지점
		SignatureUtil.printMap(certInfo);
		
		return certInfo;
	}

	public void validateCert(Map<String,Object> certInfo, boolean certVerification){
		byte[] der = (byte[]) certInfo.get("signCert.der");
		CertUtil cu;
		try {
			
			cu = new CertUtil(der);

			//인증서 유효성 검증
			if(certVerification) {
				if(certProxyServerYn.equals("Y")) {
					String pem = cu.derToPem();
					if(!verifyCertKicaUrl(pem)) {
						LOG.error("ERROR : 인증서 유효성 검증실패" + this.getClass().getName() + ".validateCert() : fail to verify certificate", "errorMsg : " + resultmsg);
						throw new CommonException(this.getClass().getName() + ".validateCert() : fail to verify certificate", "errorMsg : " + resultmsg);
					}
				} else {
					LOG.info("***certCrlPath:" + certCrlPath);
					if (!cu.isValid(true, certCrlPath)) {
						LOG.error("ERROR : 인증서 유효성 검증 실패 " + this.getClass().getName() + ".validateCert() : fail to verify certificate", "errorMsg : " + cu.getErrorMsg());
						throw new CommonException(this.getClass().getName() + ".validateCert() : fail to verify certificate", "errorMsg : " + cu.getErrorMsg());
					}
				}

				Boolean oidValid = cu.isValidPolicyOid(certPolicy.split("\\|"));
				//인증서 oid 검증
				if(!oidValid){
					LOG.error("ERROR : 인증서 OID 검증 실패 " + this.getClass().getName()+".validateCert() : fail to verify certificate", "errorMsg : " + cu.getErrorMsg());
					throw new CommonException(this.getClass().getName()+".validateCert() : fail to verify certificate", "errorMsg : " + cu.getErrorMsg());
				}
			}
		} catch (CertificateException e) {
			LOG.error("ERROR : 인증서 유효성 검증 실패 " + this.getClass().getName()+".validateCert() : fail to verify certificate", "errorMsg : " + e.toString());
			throw new CommonException(this.getClass().getName()+".validateCert() : fail to verify certificate", "errorMsg : " + e.toString());
		} catch (Exception e) {
			LOG.error("ERROR : 인증서 유효성 검증 실패 " +this.getClass().getName()+".validateCert() : fail to verify certificate", "errorMsg : " + e.toString());
			throw new CommonException(this.getClass().getName()+".validateCert() : fail to verify certificate", "errorMsg : " + e.toString());
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
		byte[] priKey = (byte[])certInfo.get("signPri.key");
		byte[] cert = (byte[])certInfo.get("signCert.der");
		String certPw = (String)certInfo.get("cert_pwd");
		
		try {
			PKCS7Util pkcs7Util = new PKCS7Util();
			String signValue = pkcs7Util.genSignedData(priKey, certPw, cert, "1".getBytes());
			
		} catch (Exception e) {
			LOG.error("error : 서명값 생성실패 " + this.getClass().getName()+".validateCertPasswd() : fail to password", "errorMsg : " + e.toString());
			throw new CommonException(this.getClass().getName()+".validateCertPasswd() : fail to password", "errorMsg : " + e.toString());
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
		CertUtil cu;
		Map<String,String> result = new HashMap<String,String>();
		try {
			cu = new CertUtil(der);
			result = makeCertInfoMap(cu);
		} catch (CertificateException e) {
			LOG.error("error : 인증서 추출 실패 " + this.getClass().getName()+".getCertificatetInfo()" + "error:" + e.toString());
			throw new CommonException(this.getClass().getName()+".getCertificatetInfo()" + "error:" + e.toString());
		}
		return result;
	}
	
	/**
	 * @Author : daesung lee
	 * @Date   : 2019. 4. 22
	 * @Param  : 서명값, 서명값에서 추출한 랜덤값, 사업자번호, 유호성 체크 여부
	 * @Return : void
	 * 
	 */	
	public void verifySignValue(String signValue, String rvalue, String ssn, boolean certVerification) {
		//verifyInit
		CertUtil cu = getCertificate(verifySignValueRtnPKCS7Util(signValue));
		if(!cu.isValidUser(ssn, rvalue)) { //신원확인 실패 ( 인증서 사업자번호와 ssn값이 일치하는지 확인)
			throw new CommonException(this.getClass().getName()+".verifySignValue()" + "error:" + cu.getErrorMsg()+ ", 신원확인 실패");
		}

		verifySignValue(signValue, certVerification);
	}

	//사용하지 않는 Method로 검출됨, 추후 사용을 위하여 제외 처리
	@SuppressWarnings("PMD")
	private Map verifyCertKicaSslUrl(String encdata){

		Map map = new HashMap();
		OutputStream os = null;
		
		final String cert = "/usr/local/npki/cert.jks"; //ssl인증서 파일
		String auth = "";

		JSONObject json = new JSONObject();
		BufferedReader br = null;
		try{
			URL url = new URL(certProxyServerUrl);
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();

			conn.setHostnameVerifier(new HostnameVerifier(){
				@Override
				public boolean verify(String arg0, SSLSession arg1){
					return true;
				}
			});

			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[]{new X509TrustManager(){
				
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException{
					//EMPTY METHOD
				}
				
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException{
					FileInputStream fis = null;
					try{
						KeyStore trustStore = KeyStore.getInstance("JKS");
						fis = new FileInputStream(cert);
						trustStore.load(fis, cert.toCharArray());
						TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
						tmf.init(trustStore);
						TrustManager[] tms = tmf.getTrustManagers();
						((X509TrustManager)tms[0]).checkServerTrusted(chain, authType);
					}catch(KeyStoreException e){
						LOG.error("error : 인증서 유효성 검증 실패 " + this.getClass() + e.toString());
						throw new CommonException(e.getMessage());
					}catch(NoSuchAlgorithmException e){
						LOG.error("error : 인증서 유효성 검증 실패 " + this.getClass() + e.toString());
						throw new CommonException(e.getMessage());
					}catch(IOException e){
						LOG.error("error : 인증서 유효성 검증 실패 " + this.getClass() + e.toString());
						throw new CommonException(e.getMessage());
					}finally {
						if(fis != null) {
							try {
								fis.close();
							} catch (IOException e) {
								LOG.error("class : " + this.getClass().toString() + "verifyCertKicaSslUrl error : "+ e.toString());
							}
						}
					}
				}

				@Override
				public X509Certificate[] getAcceptedIssuers(){
					return null;
				}

			}}, null);

			conn.setSSLSocketFactory(context.getSocketFactory());

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Authorization", auth);

			conn.setUseCaches(false);
			conn.setReadTimeout(30000);
			conn.setRequestMethod("POST");
			conn.connect();
			conn.setInstanceFollowRedirects(true);
			os = conn.getOutputStream();

			String paramm = "param=" + URLEncoder.encode(encdata, "UTF-8");
			os.write(paramm.getBytes("UTF-8"));
//			json.put("param", encdata);
//			os.write(json.toString().getBytes("UTF-8"));
			
			os.close();
			StringBuffer sb = new StringBuffer();

			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while (true){
				String line = br.readLine();
				if(Strings.isNullOrEmpty(line)) break;
				sb.append(line); sb.append("\n");

			}

			//br.close();

			conn.disconnect();
			JSONObject resultJson = new JSONObject(sb.toString().replace("\"", "\""));
			String result = resultJson.getString("result");
			String resultmsg = resultJson.getString("msg");
			map.put("result", URLDecoder.decode(result, "UTF-8"));
			map.put("resultmsg", URLDecoder.decode(resultmsg, "UTF-8"));

		}catch(Exception e){
			LOG.error("class : " + this.getClass().toString() + "verifyCertKicaSslUrl error : " + e.toString());
			map.put("result", "failed");
			map.put("resutmsg", e.toString());
		}finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LOG.error("class : " + this.getClass().toString() + "verifyCertKicaSslUrl error : " + e.toString());
				}
			}
			if(os != null) {
				try {
					os.close();
				} catch (IOException e) {
					LOG.error("class : " + this.getClass().toString() + "verifyCertKicaSslUrl error : " + e.toString());
				}
			}
				
		}

		return map;
	}	
	
	private boolean verifyCertKicaUrl(String encdata){

		Map map = new HashMap();
		OutputStream os = null;
		BufferedReader br = null;

		JSONObject json = new JSONObject();
		StringBuffer sb = new StringBuffer();

		try{
			URL url = new URL(certProxyServerUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			//conn.connect();

			os = conn.getOutputStream();

			String paramm = "param=" + URLEncoder.encode(encdata, "UTF-8"); 
			os.write(paramm.getBytes("UTF-8"));
			os.flush();
			os.close();
			
			try {

				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				while (true) {
					String line = br.readLine();
					if(Strings.isNullOrEmpty(line)) break;
					sb.append(line); sb.append("\n");
				}
	
				br.close();
	
				
				JSONObject resultJson = new JSONObject(sb.toString().replace("\"", "\""));
				String results = resultJson.getString("result");
				String resultmsgs = resultJson.getString("resultmsg");
			
				result = URLDecoder.decode(results, "UTF-8");
				resultmsg = URLDecoder.decode(resultmsgs, "UTF-8");
			}catch(IOException e) {
				LOG.error("IOException occurr");
			}catch(Exception e) {
				LOG.error("class : " + this.getClass().toString() + "verifyCertKicaUrl error : " + e.toString());
				result = "fail";
				resultmsg = "connection fail";
			}finally {
				if(conn != null) {
					conn.disconnect();
				}
				if(br != null) {
					br.close();
				}				
			}

			if(result.equals("success")) {
				return true;
			}else {
				return false;
			}			

		}catch(Exception e){
			LOG.error("class : " + this.getClass().toString() + "verifyCertKicaUrl error : " + e.toString());
			return false;
		}
	}

	@Override
	public String getCertPem(CertInfo certInfo) {
		byte[] serverKmCert = certInfo.getKmCert();
		String serverKmCertPem;
		try {
			serverKmCertPem = CertUtil.derToPem( serverKmCert );
		}catch (Exception e) {
			LOG.error(this.getClass().getName() + ".getCertPem()" + "error msg:" + e.getMessage() + "error:"+e.toString());
			throw new CommonException(this.getClass().getName() + ".getCertPem()" + "error:" + e.getMessage());
		}
		
		return serverKmCertPem;
	}

	@Override
	public void verifySignXml(String signedXml, String kmCertFilePath, String kmPriFilePath, String certPw,
			String rvalue, String ssn) {
		XmlSecu x =  new XmlSecu();
		try {
			XmlSignature xmlsign = new XmlSignature(x);
			
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "verifySignXml error : " + e.toString());
			// TODO Auto-generated catch block
			throw new CommonException(e.getMessage());
		}
		
		// TODO Auto-generated method stub
	}
	@Override
	public void verifyCertificate(byte[] certPerm, String rvalue, String ssn, boolean certVerification) {
		CertUtil cu = null;
		if(certVerification) {
			try {
				cu = new CertUtil(certPerm);
			} catch (CertificateException e) {
				LOG.error("verifyCertificate error : " + e.toString());
				throw new CommonException(this.getClass().getName() + ".verifyCertificate()" + "error:" + cu.getErrorMsg());
			}
			//신원확인
			if(!cu.isValidUser(ssn, rvalue)) {//실패
				LOG.error("신원확인 실패 verifyCertificate error : " + cu.getErrorMsg());
				throw new CommonException(this.getClass().getName() + ".verifyCertificate()" + "error:" + cu.getErrorMsg());
			}
			//인증서 유효성 검사
			if(!cu.isValid()) {
				LOG.error("인증서 유효성 검사 실패 verifyCertificate error : " + cu.getErrorMsg());
				throw new CommonException(this.getClass().getName() + ".verifyCertificate()" + "error:" + cu.getErrorMsg());
			}
			
			//서명값 검증 로직 구현해야함.
			
		}
	}

	@Override
	public void verifySignXml(String signedXml) {
		// TODO Auto-generated method stub
		
	}

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

	@Override
	public boolean verifyClientToolkitInstallComplete(String installStatus) {
		return true;
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
		return clientToolkitUrl;
	}
}
