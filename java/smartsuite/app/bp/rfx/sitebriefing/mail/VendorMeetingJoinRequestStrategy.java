package smartsuite.app.bp.rfx.sitebriefing.mail;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.rfx.sitebriefing.repository.SiteBriefingRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class VendorMeetingJoinRequestStrategy implements MailStrategy {
	
	@Inject
	SiteBriefingRepository siteBriefingRepository;
	
	@Override
	public String getEmailTemplateId() {
		return "VENDOR_MEETING_JOIN_REQUEST";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		Map param = Maps.newHashMap();
		param.put("sitebrfg_uuid", appId);
		
		Map fiInfo = siteBriefingRepository.findInfoFieldIntro(param);
		List<TemplateMailData.Receiver> receivers = siteBriefingRepository.findListFiVendorReceiver(fiInfo);
		
		TemplateMailData templateMailData = TemplateMailData.getInstance();
		templateMailData.setReceivers(receivers);
		templateMailData.setParameter(fiInfo);
		
		return templateMailData;
	}
}
