package smartsuite.app.bp.eform.eformsign.mail;

import java.util.Map;

import javax.inject.Inject;
import javax.mail.Message;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import smartsuite.app.bp.eform.eformsign.repository.EFormMailRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

/**
 * 간편서명 메일 발송
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class ReqSignEFormMailStrategy implements MailStrategy {
	@Inject
	EFormMailRepository eFormMailRepository;
	
	@Override
	public String getEmailTemplateId() {
		return "REQ_SIGN_EFORM";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) {
		// Receiver
		TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
			Message.RecipientType.TO,
			(String) data.get("cntrr_eml"),
			(String) data.get("cntrr_nm")
		);
		// Parameter
		Map mailParam = Maps.newHashMap(data);
		mailParam.put("cntr_type_nm", "간편서명문서");
		
		// TemplateMailData 구성
		TemplateMailData templateMailData = TemplateMailData.getInstance();
		templateMailData.addReceiver(receiver);
		templateMailData.setParameter(mailParam);
		return templateMailData;
	}
}