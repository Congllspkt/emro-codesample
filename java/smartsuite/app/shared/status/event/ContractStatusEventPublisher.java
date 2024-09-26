package smartsuite.app.shared.status.event;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.app.shared.SignOrder;

import javax.transaction.Transactional;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class ContractStatusEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;

	/**
	 * Contract 발신한다. 발신 상태로 변경 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public ResultMap send(Map param) {
		String eventId = "contractStatusSend";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 부속서류 요청. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public ResultMap requestAppxToVendor(Map param) {
		String eventId = "contractStatusRequestAppxToVendor";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	/**
	 * 부속서류 검토 진행중. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public ResultMap requestReviewAppxToBuyer(Map param) {
		String eventId = "contractStatusRequestReviewAppxToBuyer";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	/**
	 * 부속서류 반려. <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public ResultMap returnAppxToVendor(Map param) {
		String eventId = "contractStatusReturnAppxToVendor";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	/**
	 * 협력사 반려 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public ResultMap returnContractToBuyer(Map param) {
		String eventId = "contractStatusReturnContractToBuyer";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 계약 완료 <br><br>
	 * <b>Required:</b><br>
	 * param.cntr_uuid - 계약 uuid<br><br>
	 *
	 * @param param
	 */
	public ResultMap completeContract(Map param) {
		String eventId = "contractStatusCompleteContract";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

}