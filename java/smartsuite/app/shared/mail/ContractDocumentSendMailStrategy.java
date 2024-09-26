package smartsuite.app.shared.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.contract.common.repository.ContractRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.mail.repository.ContractMailRepository;

import javax.inject.Inject;
import javax.mail.Message;
import java.util.List;
import java.util.Map;

/**
 * 공문 메일 발송
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class ContractDocumentSendMailStrategy implements MailStrategy {

	public static final String emailTemplateId =  "CNTR_DOC_SEND";
	@Inject
	ContractRepository contractRepository;

	@Inject
	ContractMailRepository contractMailRepository;

	@Override
	public String getEmailTemplateId() {
		return emailTemplateId;
	}
	
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) {

		TemplateMailData templateMailData = getTemplateMailData(appId);

		return templateMailData;
	}

	private TemplateMailData getTemplateMailData(String appId) {

		Map param = Maps.newHashMap();
		param.put("cntr_uuid", appId);

		List<Map<String,Object>> contractorList = contractRepository.findListContractor(param);

		// TemplateMailData 구성
		List<TemplateMailData.Receiver> receivers = Lists.newArrayList();
		for(Map contractor : contractorList) {
			String cntrrTypCcd = (String)contractor.get("cntrr_typ_ccd");

			if(CntrConst.USR_TYPE.VENDOR.equals(cntrrTypCcd)) {

				Map userParam = Maps.newHashMap();
				userParam.put("usr_id", contractor.get("vd_cd"));
				Map user = contractMailRepository.findUserInfo(userParam);
				// Receiver
				TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
						Message.RecipientType.TO,
						(String) user.get("eml"),
						(String) user.get("usr_nm")
				);

				receivers.add(receiver);

			}
		}
		Map cntrInfo = contractRepository.findContract(param);
		Map mailParam = Maps.newHashMap();
		mailParam.put("cntr_nm", cntrInfo.get("cntr_nm"));
		mailParam.put("cntr_no", cntrInfo.get("cntr_no"));
		mailParam.put("cntr_revno", cntrInfo.get("cntr_revno"));

		TemplateMailData templateMailData = TemplateMailData.getInstance(
				null,
				receivers,
				null,
				(String) mailParam.get("grp_cd"),
				mailParam
		);

		return templateMailData;
	}
}