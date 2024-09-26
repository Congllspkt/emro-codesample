package smartsuite.app.bp.rfx.rfx.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.rfx.rfx.repository.RfxMailRepository;
import smartsuite.app.bp.rfx.rfx.repository.RfxVendorRepository;
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
public class RfxNoticeSendMailStrategy implements MailStrategy {
	
	@Inject
	RfxMailRepository rfxMailRepository;

	@Inject
	RfxVendorRepository rfxVendorRepository;

	@Override
	public String getEmailTemplateId() {
		return "RFX_NOTICE_SEND";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		// rfx정보
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		Map rfxInfo = rfxMailRepository.findRfxResultInfo(param);

		List rfxVendorList = rfxVendorRepository.searchRfxVendorByRfx(rfxInfo);

		// TemplateMailData 구성
		List<TemplateMailData.Receiver> receivers = Lists.newArrayList();
		for(Map passVendor : (List<Map>) rfxVendorList) {
            //신규업체의 경우는 별도의 이메일로 구성한다.
            if(null != passVendor.get("OBD_TYP_CCD") && "NEW".equals(passVendor.get("OBD_TYP_CCD"))){
                continue;
            }
            
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
