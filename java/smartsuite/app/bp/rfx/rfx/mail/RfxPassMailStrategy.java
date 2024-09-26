package smartsuite.app.bp.rfx.rfx.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.rfx.rfx.repository.RfxMailRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.inject.Inject;
import javax.mail.Message;
import java.util.List;
import java.util.Map;

/**
 * 업체 선정 메일 발송 준비
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxPassMailStrategy implements MailStrategy {
	
	@Inject
	RfxMailRepository rfxMailRepository;
	
	@Override
	public String getEmailTemplateId() {
		return "RFX_PASS_MAIL";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		// rfx정보
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		Map rfxInfo = rfxMailRepository.findRfxResultInfo(param);
		
		// 선정된 업체정보 조회
		List passVdList = rfxMailRepository.findListRfxResultPassVd(rfxInfo);
		
		// TemplateMailData 구성
		List<TemplateMailData.Receiver> receivers = Lists.newArrayList();
		for(Map passVendor : (List<Map>) passVdList) {
			TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
					Message.RecipientType.TO,
					(String) passVendor.get("vd_pic_eml"),
					(String) passVendor.get("vd_pic_nm"));
			receivers.add(receiver);
		}
		
		TemplateMailData templateMailData = TemplateMailData.getInstance(
				null,
				receivers,
				null,
				null,
				rfxInfo
		);
		
		return templateMailData;
	}
}
