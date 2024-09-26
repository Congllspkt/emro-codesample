package smartsuite.app.common.cert.pki.verification;

import java.util.Map;

import org.springframework.stereotype.Service;
import smartsuite.app.common.cert.pki.CertInfo;

public interface VerificationProvider {
	String getSource(String signValue);
	void verifySignValue(String signValue, boolean certVerification);
	Map<String,String> getCertInfo(String signValue);
	Map<String,String> getCertificatetInfo(Map<String,Object> certInfo);
	void validateCertPasswd(Map<String,Object> certInfo);
	void validateCert(Map<String,Object> certInfo,boolean certVerification);
	void verifySignValue(String signValue, String rvalue, String ssn, boolean certVerification);
	String getCertPem(CertInfo certInfo); 
	void verifySignXml(String signedXml, String kmCertFilePath, String kmPriFilePath, String certPw, String rvalue, String ssn);
	void verifySignXml(String signedXml);
	void verifyCertificate(byte[] certPerm, String rvalue, String ssn, boolean certVerification);
	String encrypt(byte[] certbuf, int certlen, byte[] contentbuf, int contentlen);
	String decrypt(byte[] deckeybuf, int deckeylen, byte[] certbuf, int certlen, byte[] envelopedbuf, int envelopedlen);
	boolean verifyClientToolkitInstallComplete(String installStatus);
	String getClientToolkitInstallUrl();
	String getClientToolkitUrl();
	String getClientToolkitMultiUrl();
	String getClientXmlToolkitUrl();
}
