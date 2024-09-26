package smartsuite.app.bp.vendorMaster.vendorInfo.mail;

import org.springframework.stereotype.Service;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.mail.Message;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class VendorInfoRenewRequestMailStrategy implements MailStrategy {

	@Override
	public String getEmailTemplateId() {
		return "VENDOR_RENEW_REQ_ML";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {

		TemplateMailData templateMailData = TemplateMailData.getInstance();
		templateMailData.setParameter((Map<String, Object>) data.get("mailData"));

		List<Map<String, Object>> receiverList = (List<Map<String, Object>>)data.get("mailList");
		for(Map<String, Object> receiverInfo : receiverList) {
			templateMailData.addReceiver(TemplateMailData.Receiver.getInstance(
					Message.RecipientType.TO,
					(String) receiverInfo.get("eml"),
					(String) receiverInfo.get("usr_nm"),
					(String) receiverInfo.getOrDefault("usr_id","")
			));
		}
		return templateMailData;
	}
}
