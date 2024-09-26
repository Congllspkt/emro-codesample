package smartsuite.app.bp.rfx.nego.mail;

import org.springframework.stereotype.Service;
import smartsuite.app.bp.rfx.nego.repository.NegoRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.inject.Inject;
import javax.mail.Message;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class NegoReqAlramMailStrategy implements MailStrategy {
	
	@Inject
	NegoRepository negoRepository;
	
	@Override
	public String getEmailTemplateId() {
		return "NEGO_REQ_ALRAM_MAIL";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		
		TemplateMailData templateMailData = TemplateMailData.getInstance();
		templateMailData.setParameter(data);
		
		//통보기능 추가
		List<Map<String, Object>> vendorUserList = negoRepository.getUserMailInfo(data);
		if(vendorUserList != null) {
            for(Map<String, Object> vendorUserInfo : vendorUserList){
                TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
                        Message.RecipientType.TO,
                        (String) vendorUserInfo.get("to_addr"),
					(String) vendorUserInfo.get("to_nm"),
					(String) vendorUserInfo.getOrDefault("to_id","")
                );
			
            }
		}
		
		return templateMailData;
	}
}
