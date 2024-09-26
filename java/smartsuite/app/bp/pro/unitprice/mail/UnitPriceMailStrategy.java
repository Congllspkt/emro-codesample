package smartsuite.app.bp.pro.unitprice.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.mail.Message;
import java.text.ParseException;
import java.util.*;


/**
 * 계약 만료기간 알림 메일 설정
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class UnitPriceMailStrategy implements MailStrategy {

    @Override
    public String getEmailTemplateId() {
        return "UPC_PD_EXP_ALRM_MAIL";
    }

    @Override
    public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws ParseException {
	    Map<String, Object> cntrInfo = Maps.newHashMap();
		List<Map<String, Object>> makeParamMap = (List<Map<String, Object>>) data.get("cntrList");

		for(Map getParam : makeParamMap) {
			cntrInfo.put("cntr_no", getParam.get("cntr_no"));
			cntrInfo.put("eml", getParam.get("email"));
			cntrInfo.put("usr_nm", getParam.get("usr_nm"));
			cntrInfo.put("usr_id", getParam.get("usr_id"));
		}

	    // TemplateMailData 구성
	    List<TemplateMailData.Receiver> receivers = Lists.newArrayList();

	    // 수신자 설정
	    TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
			    Message.RecipientType.TO,
			    (String) cntrInfo.get("eml"),
			    (String) cntrInfo.getOrDefault("usr_id", ""),
			    (String) cntrInfo.get("usr_nm"));
	    receivers.add(receiver);

	    Map<String, Object> mailData = Maps.newHashMap();

	    List<Map<String, Object>> cntrList = (List<Map<String, Object>>) data.get("cntrList");
	    mailData.put("cntrInfo", cntrInfo);
	    mailData.put("cntrList", cntrList);
		mailData.put("getDate", (Integer)data.get("getDate"));

	    TemplateMailData templateMailData = TemplateMailData.getInstance(
			    null,
			    receivers,
			    null,
			    null,
			    mailData
	    );

	    return templateMailData;
    }
}
