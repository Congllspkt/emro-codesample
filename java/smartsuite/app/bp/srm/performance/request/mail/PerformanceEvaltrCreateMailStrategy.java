package smartsuite.app.bp.srm.performance.request.mail;

import org.springframework.stereotype.Service;
import smartsuite.app.bp.srm.performance.request.repository.PerformanceEvalMailRepository;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;

import javax.inject.Inject;
import javax.mail.Message;
import java.util.Map;

/**
 * 평가자에게 평가 수행 요청 메일 발송 준비
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class PerformanceEvaltrCreateMailStrategy implements MailStrategy {
	
	@Inject
	PerformanceEvalMailRepository pfmcEvalMailRepository;
	
	@Override
	public String getEmailTemplateId() {
		return "REQUEST_FULFILL_PFMC_EVAL";
	}

	/* [Required]
	*  data.req_type : PE, PE_SUBJ, PE_SUBJ_EVALTR
	*  data.evaltr_id
	*  data.pe_uuid / data.pe_subj_uuid / data.pe_subj_evaltr_uuid 셋 중 1개 필수
	*  [Option]
	*  data.pe_uuid
	*  data.pe_subj_uuid
	*  data.pe_subj_evaltr_uuid
	* */
	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {

		Map peSubjInfo = pfmcEvalMailRepository.findPeSubjEvaltrCreateMailInfo(data);

		// TemplateMailData 구성
		TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
				Message.RecipientType.TO,
				(String) peSubjInfo.get("eml"),
				(String) peSubjInfo.get("usr_nm"));

		TemplateMailData templateMailData = TemplateMailData.getInstance();
		templateMailData.addReceiver(receiver);
		templateMailData.setParameter(peSubjInfo);

		return templateMailData;
	}
}
