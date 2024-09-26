package smartsuite.app.common.cert.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.cert.CertConst;
import smartsuite.app.common.cert.pki.SignatureUtil;
import smartsuite.app.common.cert.pki.factory.SignModule;
import smartsuite.app.common.cert.pki.signature.SignatureProvider;
import smartsuite.app.common.cert.pki.verification.VerificationProvider;
import smartsuite.app.common.cert.service.CertMgtService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class CertEventListener {
	@Value("#{cert['cert.verifiable']}")
	private boolean certVerifiable;

	@Value("#{cert['pki.module']}")
	private String pkiModule;

	@Value("#{cert['client.toolkit.install.url']}")
	private String clientToolkitInstallUrl;

	@Value("#{cert['client.toolkit.url']}")
	private String clientToolkitUrl;

	@Value("#{cert['client.toolkit.multi.url']}")
	private String clientToolkitMultiUrl;

	@Value("#{cert['client.xml.toolkit.url']}")
	private String clientXmlToolkitUrl;

	@Inject
	VerificationProvider verificationProvider;
	@Inject
	SignatureProvider signatureProvider;
	@Inject
	CertMgtService certMgtService;



	/**
	 * 서명값 검증<br><br>
	 * <b>Required: sign_value</b><br>
	 * param.sign_value - 서명값<br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'verifySignValue'")
	public void verifySignValue(CustomSpringEvent event) {
		Map param = (Map)event.getData();
		String signValue = (String)param.get("sign_value");
		verificationProvider.verifySignValue(signValue,certVerifiable);
	}

	/**
	 * 서명값 검증과 신원확인 <br><br>
	 * <b>Required: signValue, rvalue, ssn</b><br>
	 * param.signValue - 서명값<br>
	 * param.rvalue - 서버에서 신원확인을 위한 R값<br>
	 * param.ssn - 사업자번호<br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'verifySignValueAndIdentification'")
	public void verifySignValueAndIdentification(CustomSpringEvent event) {
		Map data = (Map)event.getData();
		String signValue = (String)data.get("signValue");
		String rvalue = (String)data.get("rvalue");
		String ssn = (String)data.get("ssn");

		verificationProvider.verifySignValue(signValue, rvalue, ssn, certVerifiable);

	}


	/**
	 * String 값을 hash값으로 변환 <br><br>
	 * <b>Required: String 값 </b><br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'getHashValueFromStr'")
	public void getHashValueFromStr(CustomSpringEvent event) {

		String hashValue = SignatureUtil.getHashValueFromStr((String) event.getData());
		event.setResult(hashValue);
	}

	/**
	 * 랜덤값 생성 <br><br>
	 * <b>Required: size, format</b><br>
	 * param.size - 랜덤값 생성 사이즈<br>
	 * param.size - 랜덤값 format<br>
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'getRandomString'")
	public void getRandomString(CustomSpringEvent event) {
		Map data = (Map) event.getData();
		int size = (Integer)data.get("size");
		String format = (String)data.get("format");

		String random = EdocStringUtil.getRandomString(size,format);
		event.setResult(random);
	}

	/**
	 * 툴킷 설치 페이지<br><br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'clientToolkitInstallUrl'")
	public void clientToolkitInstallUrl(CustomSpringEvent event) {
		event.setResult(this.clientToolkitInstallUrl);
	}

	/**
	 * 툴킷 페이지<br><br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'clientToolkitUrl'")
	public void clientToolkitUrl(CustomSpringEvent event) {
		event.setResult(this.clientToolkitUrl);
	}

	/**
	 * PKI 툴킷 종류 <br><br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'findSignModule'")
	public void findSignModule(CustomSpringEvent event) {
		event.setResult(pkiModule);
	}

	/**
	 * Client 툴킷 설치 완료 여부 <br><br>
	 * <b>Required: installStatus</b><br>
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'verifyClientToolkitInstallComplete'")
	public void verifyClientToolkitInstallComplete(CustomSpringEvent event) {
		Map data = (Map) event.getData();
		String installStatus = (String) data.get("installStatus");
		boolean installComplete = verificationProvider.verifyClientToolkitInstallComplete(installStatus);
		event.setResult(installComplete);
	}

	/**
	 * 인증서 정보 조회 <br><br>
	 * <b>Required: LOGIC_ORG_CD, LOGIC_ORG_TYP_CCD</b><br>
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'findOrgCertInfo'")
	public void findOrgCertInfo(CustomSpringEvent event) {
		Map data = (Map) event.getData();
		ResultMap resultMap = certMgtService.findOrgCertInfo(data);
		event.setResult(resultMap);	}



}
