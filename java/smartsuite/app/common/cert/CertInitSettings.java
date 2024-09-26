package smartsuite.app.common.cert;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartsuite.app.common.cert.pki.signature.SignatureProvider;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/***
 * pki 관련 셋팅 클래스
 */
public class CertInitSettings {
	@Inject
	private SignatureProvider signatureProvider;
	
	private final static Logger LOG = LoggerFactory.getLogger(CertInitSettings.class);

	@PostConstruct
	private void setup(){ // NOPMD
		signatureProvider.init(); //PKI 툴킷 SETUP 함수 호출
	}
	
}
