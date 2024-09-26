package smartsuite.app.sp.eform.eformsign.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.eform.eformsign.repository.EFormMailRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.inject.Inject;
import javax.mail.Message;
import java.util.List;
import java.util.Map;

/**
 * 간편서명 계약 반려 메일 발송
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class CntrAgrmRetnMailStrategy implements MailStrategy {
	@Inject
	EFormMailRepository eFormMailRepository;
	
	@Override
	public String getEmailTemplateId() {
		return "CNTR_AGRM_RETN";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) {
		List<Map> receiverList = eFormMailRepository.findCntrSgndVdUserList(data);
		
		// Receiver
		List<TemplateMailData.Receiver> receivers = Lists.newArrayList();
		for(Map row : receiverList) {
			TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
				Message.RecipientType.TO,
				(String) row.get("cntrr_eml"),
				(String) row.get("cntrr_nm")
			);
			receivers.add(receiver);
		}
		
		// Parameter
		Map mailParam = Maps.newHashMap(receiverList.get(0));
		mailParam.put("cntr_type_nm", "간편서명문서");
		mailParam.put("ret_rsn", data.get("ret_rsn"));
		
		// TemplateMailData 구성
		TemplateMailData templateMailData = TemplateMailData.getInstance();
		templateMailData.setReceivers(receivers);
		templateMailData.setParameter(mailParam);
		return templateMailData;
	}
}