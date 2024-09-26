package smartsuite.app.bp.vendorMaster.vendorInfo.mail;

import org.springframework.stereotype.Service;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.mail.Message;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class VendorInvitationMailStrategy implements MailStrategy {

	@Override
	public String getEmailTemplateId() {
		return "VENDOR_INVITATION_MAIL";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {

		TemplateMailData templateMailData = TemplateMailData.getInstance();

		data.put("ctmpl_cont", data.get("invi_cont"));
		templateMailData.setParameter((Map<String, Object>) data);

		// 메일 제목 설정
		templateMailData.setTitle((String) data.get("invi_tit"));

		// 수신자 설정
		TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
				Message.RecipientType.TO,
				(String) data.get("vd_eml"),
				(String) data.get("vd_nm")
		);
		templateMailData.addReceiver(receiver);

		// 발신자 설정
		TemplateMailData.Sender sender = TemplateMailData.Sender.getInstance(
				(String) data.get("invi_pic_eml"),
				(String) data.get("invi_pic_nm")
		);
		templateMailData.setSender(sender);

		return templateMailData;
	}
}
