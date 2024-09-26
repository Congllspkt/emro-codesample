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
public class RfxOpenMailStrategy implements MailStrategy {
	
	@Inject
	RfxMailRepository rfxMailRepository;

	// 공개  : 견적서 제출 업체 현황 조회
	private static final String COMP_TYP_CCD = "OBID";
	
	@Override
	public String getEmailTemplateId() {
		return "RFX_OPEN";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		// rfx정보
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", appId);
		Map rfxInfo = rfxMailRepository.findRfxResultInfo(param);

		String compTypeCode = rfxInfo.get("comp_typ_ccd") == null? "" : (String) rfxInfo.get("comp_typ_ccd");

		List submitRfxBidList = Lists.newArrayList();

		if(COMP_TYP_CCD.equals(compTypeCode)){
			submitRfxBidList = rfxMailRepository.findListRfxBidSubmit(rfxInfo);
		}else {
			submitRfxBidList = rfxMailRepository.findListRfxVdBidSubmit(rfxInfo);
		}
		
		// TemplateMailData 구성
		List<TemplateMailData.Receiver> receivers = Lists.newArrayList();
		for(Map passVendor : (List<Map>) submitRfxBidList) {
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
