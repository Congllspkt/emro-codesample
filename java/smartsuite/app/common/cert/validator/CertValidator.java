package smartsuite.app.common.cert.validator;

import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import smartsuite.app.common.cert.CertConst;
import smartsuite.app.common.cert.pki.verification.VerificationProvider;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.security.core.crypto.CipherUtil;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class CertValidator {

	@Value("#{cert['cert.verifiable']}")
	private boolean certVerifiable;
	
	@Inject
	CipherUtil cipherUtil;

	@Inject
	VerificationProvider verificationProvider;
	
	private static final Logger LOG = LoggerFactory.getLogger(CertValidator.class);
	
	public ResultMap validateCertPasswd(Map certFileInfo) {
		if(certFileInfo == null) {
			LOG.info("인증서 정보가 존재하지 않습니다.");
			return ResultMap.NOT_EXISTS();
		}
		
		try {
			verificationProvider.validateCertPasswd(certFileInfo);
		}catch(Exception e) {
			LOG.error(e.toString() + "인증서 비밀번호가 일치하지 않습니다.");
			return ResultMap.FAIL(CertConst.CERT_PW_WRONG);
		}
		
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap validateCert(Map certFileInfo) {
		if(certFileInfo == null) {
			LOG.info("인증서 정보가 존재하지 않습니다.");
			return ResultMap.NOT_EXISTS();
		}
		
		try {
			verificationProvider.validateCert(certFileInfo, certVerifiable);
		}catch(Exception e) {
			LOG.error(e.toString() + "인증서가 유효하지 않습니다.");
			return ResultMap.FAIL(CertConst.CERT_VALIDATE_FAIL);
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap verifySignValue(Map param) {
		
		Map<String,Object> result = Maps.newHashMap();
		result.put(Const.RESULT_STATUS, Const.SUCCESS);
		String signValue = (String)param.get("sign_value");
		String rvalue = (String)param.get("rvalue");
		String ssn = (String)param.get("ssn");
		rvalue = cipherUtil.decrypt(rvalue); //rvalue 복호화
		
		verificationProvider.verifySignValue(signValue, rvalue, ssn, certVerifiable);
		
		return ResultMap.SUCCESS(); 
	}

	public Map<String,String> getCertificatetInfo(Map<String,Object> certFileInfo){
		return verificationProvider.getCertificatetInfo(certFileInfo);
	}

}