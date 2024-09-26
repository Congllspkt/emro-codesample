package smartsuite.app.shared.mail.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.app.shared.mail.ContractDocumentSendMailStrategy;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class ContractMailEventListener {

	@Inject
	MailService mailService;
	/**
	 * Contract 발신한다. 발신 상태로 변경 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param : param
	 */
	@EventListener(condition = "#event.eventId == 'contractStatusSend'")
	public void contractStatusSend(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		String cntrUUID = (String)param.get("cntr_uuid");
		mailService.sendAsync(ContractDocumentSendMailStrategy.emailTemplateId, cntrUUID);
	}

	/**
	 * 부속서류 요청. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param : param
	 */
	@EventListener(condition = "#event.eventId == 'contractStatusRequestAppxToVendor'")
	public void contractStatusRequestAppxToVendor(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		String cntrUUID = (String)param.get("cntr_uuid");
	}

	/**
	 * 부속서류 검토 진행중. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param : param
	 */
	@EventListener(condition = "#event.eventId == 'contractStatusRequestReviewAppxToBuyer'")
	public void contractStatusRequestReviewAppxToBuyer(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		String cntrUUID = (String)param.get("cntr_uuid");
	}

	/**
	 * 부속서류 반려. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param : param
	 */
	@EventListener(condition = "#event.eventId == 'contractStatusReturnAppxToVendor'")
	public void contractStatusReturnAppxToVendor(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		String cntrUUID = (String)param.get("cntr_uuid");
	}

	/**
	 * 협력사 반려 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param : param
	 */
	@EventListener(condition = "#event.eventId == 'contractStatusReturnContractToBuyer'")
	public void contractStatusReturnContractToBuyer(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		String cntrUUID = (String)param.get("cntr_uuid");
	}

	/**
	 * 계약 완료 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param : param
	 */
	@EventListener(condition = "#event.eventId == 'contractStatusCompleteContract'")
	public void contractStatusCompleteContract(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		String cntrUUID = (String)param.get("cntr_uuid");
	}

}