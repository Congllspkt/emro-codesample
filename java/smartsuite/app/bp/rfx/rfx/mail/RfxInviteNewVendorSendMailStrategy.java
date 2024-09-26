package smartsuite.app.bp.rfx.rfx.mail;

import org.springframework.stereotype.Service;
import smartsuite.app.bp.rfx.rfx.repository.RfxMailRepository;
import smartsuite.app.bp.rfx.rfx.repository.RfxVendorRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.inject.Inject;
import javax.mail.Message;
import java.util.Map;

/**
 * RFX 신규 업체 초대 메일 발송
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxInviteNewVendorSendMailStrategy implements MailStrategy {
	
	@Inject
	RfxMailRepository rfxMailRepository;

	@Inject
	RfxVendorRepository rfxVendorRepository;

	@Override
	public String getEmailTemplateId() {
		return "RFX_INVITE_NEW_VENDOR";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
        TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
                Message.RecipientType.TO,
                (String) data.get("vd_pic_eml"),
                (String) data.get("vd_pic_nm"));
		
		TemplateMailData templateMailData = TemplateMailData.getInstance();
        templateMailData.addReceiver(receiver);
        templateMailData.setParameter(data);
		
		return templateMailData;
	}
}
