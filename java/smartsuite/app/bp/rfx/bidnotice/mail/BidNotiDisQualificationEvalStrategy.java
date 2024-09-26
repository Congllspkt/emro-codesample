package smartsuite.app.bp.rfx.bidnotice.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.rfx.bidnotice.repository.BidNoticeRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.inject.Inject;
import javax.mail.Message;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class BidNotiDisQualificationEvalStrategy implements MailStrategy {
	
	@Inject
	BidNoticeRepository bidNoticeRepository;

	//비적격 코드 : C067
	private static final String PTCP_QUALEVAL_RES_CCD = "DQ";
	
	@Override
	public String getEmailTemplateId() {
		return "BID_DIS_NOTI_QUAL_EVAL";
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		
		Map param = Maps.newHashMap();
		param.put("rfx_prentc_uuid", appId);
		param.put("ptcp_qualeval_res_ccd", PTCP_QUALEVAL_RES_CCD);
		List<Map> vdList = bidNoticeRepository.findListBidNotiQualificationEval(param);
		List<TemplateMailData.Receiver> receivers = Lists.newArrayList();

		for(Map vendor : vdList) {
			TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
					Message.RecipientType.TO,
					(String) vendor.get("vd_pic_eml"),
					(String) vendor.get("vd_pic_nm")
			);
			receivers.add(receiver);
		}
		TemplateMailData templateMailData = TemplateMailData.getInstance(
				null,
				receivers,
				null,
				null,
				data
		);
		
		return templateMailData;
	}
}
