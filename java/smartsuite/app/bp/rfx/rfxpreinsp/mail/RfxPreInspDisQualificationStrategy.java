package smartsuite.app.bp.rfx.rfxpreinsp.mail;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.rfx.rfxpreinsp.repository.RfxPreInspRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.inject.Inject;
import javax.mail.Message;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxPreInspDisQualificationStrategy implements MailStrategy {
	
	@Inject
	RfxPreInspRepository rfxPreInspRepository;

	//적격 코드 : C067
	private static final String PRESN_RES_CCD = "DQ";
	
	@Override
	public String getEmailTemplateId() {
		return "RFX_PRE_INSP_DIS_QUAL";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		
		TemplateMailData templateMailData = TemplateMailData.getInstance();
		
		Map param = Maps.newHashMap();
		param.put("rfx_presn_uuid", appId);
		param.put("presn_res_ccd", PRESN_RES_CCD);
		List<Map> vdList = rfxPreInspRepository.findListRfxPreInspQualification(param);

		for(Map vendor : vdList) {
			TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
					Message.RecipientType.TO,
					(String) vendor.get("vd_pic_eml"),
					(String) vendor.get("vd_pic_nm")
			);
			templateMailData.addReceiver(receiver);
		}
		
		return templateMailData;
	}
}
