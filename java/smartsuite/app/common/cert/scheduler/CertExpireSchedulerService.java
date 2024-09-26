package smartsuite.app.common.cert.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.common.cert.repository.CertMgtRepository;

import smartsuite.app.common.mail.MailService;

@Service
@Transactional
public class CertExpireSchedulerService {
	/*@Inject
	CommonMailService commonMailService;*/
	@Inject
	MailService mailService;
	
	@Inject
	CertMgtRepository certMgtRepository;
	
	// 인증서 만료 안내 메일
	@SuppressWarnings("unchecked")
	public void sendCertExpireMail(HashMap<String, Object> param) {
		//만료 되는 인증서 정보 조회
		List<Map<String, Object>> expireCertList = certMgtRepository.getCertExpirationEmailTargetList(param);
		for(Map<String, Object> row : expireCertList) {
			mailService.sendAsync("CERT_EXP_NOFN", null, row);
		}
		
		//메일 발송
		//commonMailService.addMail("MAIL_CERT_EXPIRE", expireCertList);
		
		//LOG.info("======== sendCertExpireMail ========");
	}
}
