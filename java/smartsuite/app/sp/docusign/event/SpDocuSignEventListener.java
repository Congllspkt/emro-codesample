package smartsuite.app.sp.docusign.event;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.event.CustomSpringEvent;
import smartsuite.app.sp.docusign.service.SpDocuSignService;

/**
 * DocuSign 관련 처리하는 이벤트 리스터 Class입니다.
 *
 * @FileName SpDocuSignEventListener.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpDocuSignEventListener {

	@Inject
	SpDocuSignService SpDocuSignService;
	
	
	@EventListener(condition = "#event.eventId == 'findSpDocusignEnvelope'")
	public void findDocusignEnvelope(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(SpDocuSignService.findDocusignEnvelope(param));
	}
	
}
