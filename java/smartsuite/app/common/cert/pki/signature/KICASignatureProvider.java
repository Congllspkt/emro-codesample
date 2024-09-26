package smartsuite.app.common.cert.pki.signature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import signgate.crypto.auth.KICAAuth;
import signgate.crypto.util.PKCS7Util;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.exception.CommonException;

/**
 * @since 2018. 8. 24.
 * @author sunghyun
 * ------------------------
 * 개정이력
 * 2018. 8. 24. sunghyun : 최초작성
 * 한국정보인증(KICA)모듈 전자서명 구현체
 */
@Service
public class KICASignatureProvider implements SignatureProvider{
	private static final Logger LOG = LoggerFactory.getLogger(KICASignatureProvider.class);

	@Value("#{cert['server.license']}")
	private String serverLicense;
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 24.
	 * @Param  : byte[] 서명대상
	 * @Return : String
	 * signSource를 서명하여 서명값을 리턴
	 */
	private String getSignValue(byte[] signSource, CertInfo certInfo){
		// 인증서 및 PrivateKey 읽기
		byte[] priKey = certInfo.getSignPri();
		byte[] cert   = certInfo.getSignCert();
		String certPw = certInfo.getCertPw();
		
		// PKCS#7 전자서명 (privateKey, 인증서 필요)
		PKCS7Util pkcs7Util = new PKCS7Util();
		String signValue = "";
		try {
			signValue = pkcs7Util.genSignedData(priKey, certPw, cert, signSource);
		} catch (Exception e) {
			LOG.error("error : 서명값 생성 실패 " + this.getClass().getName() + ".getSignValue(byte[]) :" + e.getMessage(), "fail to getSignValue");
			throw new CommonException(this.getClass().getName() + ".getSignValue(byte[]) :" + e.getMessage(), "fail to getSignValue");
		}
		// passwd가 암호화된 값일 경우 아래 코드 사용 (운영에서 사용)
		/*
		try {
			String encCertPw = FileUtils.readFileToString(new File(certPath + "passwd"), "UTF-8");
			signValue = pkcs7Util.genEncPassSignedData(priKey, encCertPw, cert, signSource); 
		} catch (Exception e) {
			throw new CommonException(this.getClass().getName() + ".getSignValue(byte[]) :" + e.getMessage(), "fail to read encrypted password");
		}*/
		
		return signValue;
	}
	
	public String getSignValue(String signSource, CertInfo certInfo) {
		return getSignValue(signSource.getBytes(), certInfo);
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

	@Override
	public void init() {
		try {

			LOG.info("한국정보인증 PKI toolkit init");

			//한국전자인증 서버 라이선스 필요 lib 버전일 경우만 사용
			//KICAAuth.init(serverLicense);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
