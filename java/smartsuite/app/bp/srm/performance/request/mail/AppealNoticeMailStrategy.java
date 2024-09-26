package smartsuite.app.bp.srm.performance.request.mail;

import org.springframework.stereotype.Service;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.mail.Message;
import java.util.List;
import java.util.Map;

/**
 * 공급사에게 이의제기 통보 메일 발송 준비
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class AppealNoticeMailStrategy implements MailStrategy {

	@Override
	public String getEmailTemplateId() {
		return "NOTICE_APPEAL";
	}

	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {

		TemplateMailData templateMailData = TemplateMailData.getInstance();
		templateMailData.setParameter((Map<String, Object>) data);

		List<Map<String, Object>> receiverList = (List<Map<String, Object>>)data.get("appealVendorList");
		for(Map<String, Object> receiverInfo : receiverList) {
			templateMailData.addReceiver(TemplateMailData.Receiver.getInstance(
					Message.RecipientType.TO,
					(String) receiverInfo.get("vd_eml"),
					(String) receiverInfo.get("vd_nm")
			));
		}
		return templateMailData;
	}
}
