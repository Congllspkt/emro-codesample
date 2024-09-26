package smartsuite.app.common.cert;

public class CertConst {
	public final static String SIGN_CERT_DER = "signCert.der";

	public final static String SIGN_CERT_DER_FILE_PATH = "signCert.der_file_path";
	public final static String SIGN_PRI_KEY = "signPri.key";

	public final static String SIGN_PRI_KEY_FILE_PATH = "signPri.key_file_path";
	public final static String KM_CERT_DER = "kmCert.der";

	public final static String KM_CERT_DER_FILE_PATH = "kmCert.der_file_path";
	public final static String KM_PRI_KEY = "kmPri.key";

	public final static String KM_PRI_KEY_FILE_PATH = "kmPri.key_file_path";
	public final static String PFX = "pfx";
	public final static String PFX_FILE_PATH = "pfx_file_path";
	/**신규*/
	public final static String NEW = "NEW";
	/**수정*/
	public final static String EDIT = "EDIT";
	/**인증서 비밀번호 틀림*/
	public final static String CERT_PW_WRONG = "W";
	/**인증서 검증 실패*/
	public final static String CERT_VALIDATE_FAIL = "F";
	/**인증서 등록*/
	public final static String CERT_REG_Y = "Y";
	/**인증서 등록*/
	public final static String CERT_REG_N = "N";
	
	/**한국전자인증 툴킷 설치 URL*/
	public final static String CROSSCERT_TOOLKIT_INSTALL_URL = "econtract/crosscert/checkCrossCertInstall";
	/**한국전자인증 툴킷 URL*/
	public final static String CROSSCERT_TOOLKIT_URL = "econtract/crosscert/signCrossCert";
	/**한국전자인증 툴킷 URL 멀티서명*/
	public final static String CROSSCERT_TOOLKIT_URL_MULTI = "econtract/crosscert/signCrossCert_multi";
	/** 한국전자인증 XML 툴킷 URL*/
	public final static String CROSSCERT_TOOLKIT_XML_URL = "econtract/crosscert/signXmlCrossCert_multi";
	/**한국정보인증 XML 툴킷  URL*/
	public final static String KICA_TOOLKIT_XML_URL = "econtract/kica/signKICA_TAX";
	/**한국정보인증 툴킷 URL*/
	public final static String KICA_TOOLKIT_URL = "econtract/kica/signKICA_html";
	/**서명완료 url*/
	public final static String SIGN_COMPLETE = "econtract/signComplete";
	
	/** 한국전자인증 툴킷 설치 전*/
	public final static String BEFORE = "BEFORE";
	
	/** 한국전자인증 툴킷 설치 후*/
	public final static String COMPLETE = "COMPLETE";
	
	/** 로컬 테스트*/
	public final static String LOCAL_SIGN_TEST = "LOCAL_SIGN_TEST";
	
	/** 로컬 멀티 서명 테스트*/
	public final static String LOCAL_MULTI_SIGN_TEST = "LOCAL_MULTI_SIGN_TEST";
	
}
