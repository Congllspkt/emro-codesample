package smartsuite.app.bp.pro.po.event;

import com.google.common.collect.Maps;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class PoEventPublisher {
	
	@Inject
	ApplicationEventPublisher applicationEventPublisher;
	
	public Map<String, Object> findRfx(Map<String, Object> rfxParam) {
		if(rfxParam == null) {
			return null;
		}
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("findRfx",rfxParam);
        applicationEventPublisher.publishEvent(completeEvent);
        return (Map<String, Object>) completeEvent.getResult();
	}
	
	public String checkGrPoItems(Map<String, Object> param){
		CustomSpringEvent completeEvent = CustomSpringEvent.toCompleteEvent("checkGrPoItems", param);
		applicationEventPublisher.publishEvent(completeEvent);
		return (String) completeEvent.getResult();
	}

	public Map<String, Object> excuteEvalNotify(Map<String, Object> evalParam) {
		CustomSpringEvent completeEvent =  CustomSpringEvent.toCompleteEvent("excuteEvalNotify", evalParam);
		applicationEventPublisher.publishEvent(completeEvent);
		return (Map<String, Object>) completeEvent.getResult();
	}

	/**
	 * TODO: 쓰는지확인
	 * @param e
	 * @param evalParam
	 */
	public void writeEvalErrorLog(Exception e, Map<String, Object> evalParam) {
		applicationEventPublisher.publishEvent(CustomSpringEvent.toCompleteEvent("writeEvalErrorLog", evalParam));
	}

    public void cancelRfxResult(Map<String, Object> keyParam) {
		CustomSpringEvent completeEvent =  CustomSpringEvent.toCompleteEvent("cancelRfxResult", keyParam);
		applicationEventPublisher.publishEvent(completeEvent);
    }

	public void closeRfxItemResult(Map<String, Object> keyParam) {
		CustomSpringEvent completeEvent =  CustomSpringEvent.toCompleteEvent("closeRfxItemResult", keyParam);
		applicationEventPublisher.publishEvent(completeEvent);
	}

	/**
	 * 전자계약 발송
	 * @param keyParam
	 * @return
	 */
	public Map<String, Object> sendRefContract(Map<String, Object> keyParam) {
		CustomSpringEvent event =  CustomSpringEvent.toCompleteEvent("sendRefContract", keyParam);
		applicationEventPublisher.publishEvent(event);
		return (Map<String, Object>) event.getResult();
	}
	/**
	 * 전자계약 생성
	 * @param keyParam
	 * @return
	 */
	public Map<String, Object> saveRefContract(Map<String, Object> keyParam) {
		CustomSpringEvent event =  CustomSpringEvent.toCompleteEvent("saveDraftPo", keyParam);
		applicationEventPublisher.publishEvent(event);
		return (Map<String, Object>) event.getResult();
	}
}
