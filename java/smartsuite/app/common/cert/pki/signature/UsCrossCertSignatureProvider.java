package smartsuite.app.common.cert.pki.signature;

import com.crosscert.justoolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.exception.CommonException;

/**
 * @since 2018. 8. 24.
 * @author sunghyun
 * ------------------------
 * 개정이력
 * 2018. 8. 24. sunghyun : 최초작성
 * 한국전자인증(CrossCert)모듈 전자서명 구현체
 */
@Service
public class UsCrossCertSignatureProvider implements SignatureProvider{
	private static final Logger LOG = LoggerFactory.getLogger(UsCrossCertSignatureProvider.class);
	@Value("#{cert['server.license']}")
	private String serverLicense;

	justoolkit USToolkit = new justoolkit(); //NOPMD

	@Override
	public void init() {
		try {
			USToolkit.init(serverLicense);
		}catch(Exception e) {
			LOG.error("error : 라이선스 적용 실패"  + this.getClass().getName() + ":" + e.getMessage(), "Invalid License");
			throw new CommonException(this.getClass().getName() + ":" + e.getMessage(), "Invalid License");
		}
	}

	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 24.
	 * @Param  : String 서명대상
	 * @Return : String 서명값
	 * signSource를 서명하여 서명값을 리턴
	 */
	/*public String getSignValue(String signSource) {
		return getSignValue(signSource.getBytes());
	}*/
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 24.
	 * @Param  : byte[] 서명대상
	 * @Return : String 서명값
	 * signSource를 서명하여 서명값을 리턴
	 */
	private String getSignValue(byte[] signSource, CertInfo certInfo){
		byte[] privateKey = getPrivateKey(certInfo);
		return getSignValue(privateKey, signSource, certInfo);
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 24.
	 * @Return : PrivateKey
	 * certPath에 존재하는 signPri.key에서 PrivateKey 추출
	 */
	private byte[] getPrivateKey(CertInfo certInfo){
		// privateKey 읽기
		//byte[] priKey = this.signPriKey;
		byte[] priKey = certInfo.getSignPri();
		String certPw = certInfo.getCertPw(); //NOPMD
		byte[] bindeprikey; //NOPMD
		
		try {
			//bindeprikey = USToolkit.CERT_DecryptPrikey(certPw.getBytes(), priKey); //NOPMD
			
			return priKey;
			
		}catch(Exception e) {
			LOG.error("error: 개인키 추출 실패 " + e.toString());
			return null;
		}
		

	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 24.
	 * @Param  : PrivateKey 개인키
	 * @param  : byte[] 서명대상
	 * @Return : String
	 * PrivateKey를 이용하여 signSource를 서명
	 */
	private String getSignValue(byte[] privateKey, byte[] signSource, CertInfo certInfo){
		// 인증서 읽기
		byte[] signCert = certInfo.getSignCert();
		String certPw = certInfo.getCertPw();
		String b64Signeddata = "";
		try {
		    byte[] signeddata = USToolkit.CMS_SignedData2(USToolkit.USC_ALG_SIGN_CERTSIGN, USToolkit.UST_CMS_SIGN_CONTENT, USToolkit.UST_PKCS7_VER_1_5_YESSIGN, certPw.getBytes(), signCert, privateKey, signSource);
		    b64Signeddata = USToolkit.UTIL_Base64Encode(signeddata);
		}catch(Exception e) {
			LOG.error("ERROR : 서명값 생성 실패 " + this.getClass() + "getSignValue(byte[] privateKey, byte[] signSource, CertInfo certInfo)" + e.toString());
			throw new CommonException(e.getMessage());
		}
		return b64Signeddata;
	}
	
	public String getSignValue(String signSource, CertInfo certInfo) {
		String returnStr = getSignValue(signSource.getBytes(), certInfo);
		USToolkit.finish();
		return returnStr;
	}

	@Override
	public String getXmlSignValue(String xml, byte[] signCertFile, byte[] signPriFile, String pw) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRvalue(CertInfo certInfo) {
		// TODO Auto-generated method stub
		return null;
	}

}
