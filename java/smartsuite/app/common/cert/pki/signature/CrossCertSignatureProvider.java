package smartsuite.app.common.cert.pki.signature;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import crosscert.Base64;
import crosscert.PrivateKey;
import crosscert.Signer;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.common.cert.pki.SignatureUtil;
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
public class CrossCertSignatureProvider implements SignatureProvider{

	@Value("#{crosscert_config['xpath_query']}")
	private String xpathQuery;

	@Value("#{crosscert_config['policy_oid']}")
	private String policyOid;

	@Value("#{crosscert_config['verify_option']}")
	private String verifyOption;

	private static final Logger LOG = LoggerFactory.getLogger(CrossCertSignatureProvider.class);

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
		PrivateKey privateKey = getPrivateKey(certInfo);
		return getSignValue(privateKey, signSource, certInfo);
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 24.
	 * @Return : PrivateKey
	 * certPath에 존재하는 signPri.key에서 PrivateKey 추출
	 */
	private PrivateKey getPrivateKey(CertInfo certInfo){
		// privateKey 읽기
		byte[] priKey = certInfo.getSignPri();
		String certPw = certInfo.getCertPw();
		
		// privateKey 복호화
		PrivateKey privateKey = new PrivateKey();
		int rtn = privateKey.DecryptPriKey(certPw, priKey, priKey.length);
		if(rtn != 0){
			LOG.error(this.getClass().getName()+".getSignValue(byte[]) : fail to decrypt PrivateKey", "errorCode : " + privateKey.errcode + " , errorMsg : " + privateKey.errmessage);
			throw new CommonException(this.getClass().getName()+".getSignValue(byte[]) : fail to decrypt PrivateKey", "errorCode : " + privateKey.errcode + " , errorMsg : " + privateKey.errmessage);
		}
		
		return privateKey;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 24.
	 * @Param  : PrivateKey 개인키
	 * @param  : byte[] 서명대상
	 * @Return : String
	 * PrivateKey를 이용하여 signSource를 서명
	 */
	private String getSignValue(PrivateKey privateKey, byte[] signSource, CertInfo certInfo){
		// 인증서 읽기
		byte[] cert = certInfo.getSignCert();
		
		// PKCS#7 전자서명(privateKey, 인증서 필요)
		Signer signer = new Signer();
		int result = signer.GetSignedData(privateKey.prikeybuf, privateKey.prikeylen, cert, cert.length, signSource, signSource.length);
		if(result != 0){
			throw new CommonException(this.getClass().getName()+".getSignValue(byte[]) : fail to sign", "errorCode : " + signer.errcode + " , errorMsg : " + signer.errmessage);
		}
		
		return SignatureUtil.encodeBase64(signer.signedbuf);
	}
	
	public String getSignValue(String signSource, CertInfo certInfo) {
		
		return getSignValue(signSource.getBytes(), certInfo);
	}

	/**
	 * @Author : daesung lee
	 * @Date   : 2021. 7. 23.
	 * @Param  : xml, 서명용인증서,서명용인증서키,비밀번호
	 * @Return : 서명값
	 * xml서명
	 */
	@Override 
	public String getXmlSignValue(String xml, byte[] signCertFile, byte[] signPriFile, String pw) { 
		String signXml = "";
		// 2022.12.13 java11 사용시 org.w3c.dom import 에러로 주석 처리함
		/*try {
			prop.setProperty("xpath_query", xpathQuery);
			prop.setProperty("policy_oid", policyOid);
			prop.setProperty("verify_option", verifyOption);
			TaxUtilBin taxUtil = new TaxUtilBin(prop);
			Document document = taxUtil.getDocumentXmlData(xml);
			taxUtil.signXML(document, signCertFile,signPriFile,pw);
			signXml = taxUtil.getStringDocument(document);
			LOG.info("xml 서명값: " + signXml);
		 
			if(taxUtil.getRandom() == null) { 
				LOG.error("ERROR: errorcode :" + taxUtil.getError_code() + " errormsg :" + taxUtil.getError_msg());
				throw new CommonException(
						this.getClass().getName()
						+".getXmlSignValue(Document document, String signCertFilePath, String signPriFilePath, String pw) : fail to sign", "errorMsg : " + taxUtil.getError_msg());
			}else {
				String random = new String(taxUtil.getRandom());
				LOG.info("R value :" + random);
			}
		 
		}catch (Exception e) {
			LOG.error("ERROR: " + e.toString());
			throw new CommonException(
					this.getClass().getName()
					+".getXmlSignValue(Document document, String signCertFilePath, String signPriFilePath, String pw) : fail to sign", "errorMsg : " + e.toString()); 
		}*/
		return signXml;
	 }

	/**
	 * @Author : daesung lee
	 * @Date   : 2021. 7. 23.
	 * @Param  : 인증서 정보
	 * @Return : rvalue
	 * 서버인증서 rvalue값 추출
	 */
	@Override
	public String getRvalue(CertInfo certInfo) {
		String rValue = "";
		byte[] signPriKey = certInfo.getSignPri();
		String pw = certInfo.getCertPw();
		byte[] getRByte = null;
		//개인키 관련 R값 추출
		PrivateKey privateKey = new PrivateKey();
		try {
			int nRet = privateKey.GetRFromKey(pw, signPriKey, signPriKey.length);
			if(nRet != 0) {
				LOG.error("ERROR: 개인키 추출 실패 " + this.getClass().getName()
						+".public String getRvalue(CertInfo certInfo) error", "errorMsg : " + privateKey.errcode + " , errorMsg :" + privateKey.errdetailmessage);
				throw new CommonException(
						this.getClass().getName()
						+".public String getRvalue(CertInfo certInfo) error", "errorMsg : " + privateKey.errcode + " , errorMsg :" + privateKey.errdetailmessage);
			}else {
				getRByte = privateKey.prikeybuf;
				rValue = new String(base64Encode(getRByte));
				LOG.info("base64encode R value : " + rValue);
			}
		}catch(Exception e) {
			LOG.error("ERROR : 개인키 추출 실패 " + this.getClass().getName()
					+".public String getRvalue(CertInfo certInfo) error", "errorMsg : " + privateKey.errcode + " , errorMsg :" + privateKey.errdetailmessage);
			throw new CommonException(
					this.getClass().getName()
					+".public String getRvalue(CertInfo certInfo) error", "errorMsg : " + privateKey.errcode + " , errorMsg :" + privateKey.errdetailmessage);
		}
		
		return rValue;
	}

	@Override
	public void init() {

	}

	/**
	 * @Author : daesung lee
	 * @Date   : 2021. 7. 23.
	 * @Param  : rvalue
	 * @Return : base64 rvalue
	 * rvluae encode
	 */
	private byte[] base64Encode(byte[] content) {
		
		Base64 cBase64 = new Base64();
		int nRet  = cBase64.Encode(content, content.length);
		
		if(nRet == 0 ) {
			return cBase64.contentbuf;
		}else {
			return null;
		}
	}
	/**
	 * @Author : daesung lee
	 * @Date   : 2021. 7. 23.
	 * @Param  : rvalue
	 * @Return : decode rvalue
	 * rvluae decode
	 */
	@SuppressWarnings("unused")
	private String base64Decode(String content) {
		Base64 cBase64 = new Base64();
		int nRet = cBase64.Decode(content.getBytes(), content.length());
		
		if(nRet == 0) {
			return new String(cBase64.contentbuf);
		}else {
			return null;
		}
	}

}
