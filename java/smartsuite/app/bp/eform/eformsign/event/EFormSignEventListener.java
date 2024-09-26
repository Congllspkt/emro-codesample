package smartsuite.app.bp.eform.eformsign.event;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.eform.eformsign.service.EFormSignService;
import smartsuite.app.event.CustomSpringEvent;

/**
 * 간편 서명 관련 처리하는 이벤트 리스터 Class입니다.
 *
 * @FileName EFromSignEventListener.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
@Transactional
public class EFormSignEventListener {

	@Inject
	EFormSignService eFormSignService;
	
	
	@EventListener(condition = "#event.eventId == 'deleteEFormTemplate'")
	public void deleteEFormTemplate(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(eFormSignService.deleteEFormTemplate(param));
	}
	
}
