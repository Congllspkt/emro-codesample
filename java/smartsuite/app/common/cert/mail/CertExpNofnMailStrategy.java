package smartsuite.app.common.cert.mail;

import java.util.Map;

import javax.mail.Message;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

/**
 * 서버인증서 만료 도래 안내 메일 발송
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class CertExpNofnMailStrategy implements MailStrategy {
	
	@Override
	public String getEmailTemplateId() {
		return "CERT_EXP_NOFN";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		// Receiver
		TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
			Message.RecipientType.TO,
			(String) data.get("to_addr"),
			(String) data.get("to_nm")
		);
		
		// Parameter
		Map mailParam = Maps.newHashMap(data);
		
		// TemplateMailData 구성
		TemplateMailData templateMailData = TemplateMailData.getInstance();
		templateMailData.addReceiver(receiver);
		templateMailData.setParameter(mailParam);
		return templateMailData;
	}
}