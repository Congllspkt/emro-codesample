package smartsuite.app.bp.rfx.rfi.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.rfx.rfi.repository.RfiRepository;
import smartsuite.app.bp.rfx.rfi.repository.RfiVendorRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.inject.Inject;
import javax.mail.Message;
import java.util.List;
import java.util.Map;

/**
 * RFI 요청 메일
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfiCompleteMailStrategy implements MailStrategy {
	
	@Inject
	RfiRepository rfiRepository;
	@Inject
	RfiVendorRepository rfiVendorRepository;

	@Override
	public String getEmailTemplateId() {
		return "RFI_COMPLETE";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		// rfi 정보
		Map param = Maps.newHashMap();
		param.put("rfi_uuid", appId);

		Map rfiInfo = rfiRepository.findRfi(param);
		List rfiVendorList = rfiVendorRepository.findListRfiVendor(param);

		// TemplateMailData 구성
		List<TemplateMailData.Receiver> receivers = Lists.newArrayList();

		for(Map rfiVendorInfo : (List<Map>) rfiVendorList) {
			TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
					Message.RecipientType.TO,
					(String) rfiVendorInfo.get("vd_pic_eml"),
					(String) rfiVendorInfo.get("vd_pic_nm"));
			receivers.add(receiver);
		}
		
		TemplateMailData templateMailData = TemplateMailData.getInstance(
				null,
				receivers,
				null,
				null,
				rfiInfo
		);
		
		return templateMailData;
	}
}
