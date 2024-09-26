package smartsuite.app.bp.rfx.nego.mail;

import org.springframework.stereotype.Service;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.mail.Message;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class NegoAlramMailStrategy implements MailStrategy {
	
	@Override
	public String getEmailTemplateId() {
		return "NEGO_ALRAM_MAIL";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		
		TemplateMailData templateMailData = TemplateMailData.getInstance();
		templateMailData.setParameter((Map<String, Object>) data.get("parameter"));
		
		List<Map<String, Object>> receiverList = (List<Map<String, Object>>) data.get("receivers");
		for(Map<String, Object> receiverInfo : receiverList) {
			templateMailData.addReceiver(TemplateMailData.Receiver.getInstance(
					Message.RecipientType.TO,
					(String) receiverInfo.get("to_addr"),
					(String) receiverInfo.get("to_nm"),
					(String) receiverInfo.getOrDefault("to_id", "")
			));
		}
		
		return templateMailData;
	}
}
