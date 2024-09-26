package smartsuite.app.sp.rfx.rfxpreinsp.mail;

import org.springframework.stereotype.Service;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;
import smartsuite.app.sp.rfx.rfxpreinsp.repository.SpRfxPreInspRepository;

import javax.inject.Inject;
import javax.mail.Message;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxPreInspBpStrategy implements MailStrategy {
	
	@Inject
	SpRfxPreInspRepository spRfxPreInspRepository;
	
	@Override
	public String getEmailTemplateId() {
		return "RFX_PRE_INSP_BP";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		
		TemplateMailData templateMailData = TemplateMailData.getInstance();
		
		Map rfxPreInspApp = spRfxPreInspRepository.findRfxPreInspApp(data);
		Map mailInfo = spRfxPreInspRepository.findRfxPurcChrMailInfo(data);
		if(mailInfo != null) {
			mailInfo.put("vd_cd", rfxPreInspApp.get("vd_cd"));
			mailInfo.put("vd_nm", rfxPreInspApp.get("vd_nm"));
			
			templateMailData.setParameter(mailInfo);
			
			TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
					Message.RecipientType.TO,
					(String) mailInfo.get("purc_chr_email"),
					(String) mailInfo.get("purc_chr_nm"),
					(String) mailInfo.getOrDefault("purc_pic_id","")
			);
			templateMailData.addReceiver(receiver);
		}
		
		return templateMailData;
	}
}
