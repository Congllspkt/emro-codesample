package smartsuite.app.common.cert.pki.signature;

import smartsuite.app.common.cert.pki.CertInfo;

public interface SignatureProvider {
	String getSignValue(String signSource, CertInfo certInfo);
	String getXmlSignValue(String xml, byte[] signCertFile, byte[] signPriFile, String pw);
	String getRvalue(CertInfo certInfo);
	void init();
	
}
