package smartsuite.app.bp.docusign.event;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.docusign.service.DocuSignService;
import smartsuite.app.event.CustomSpringEvent;

/**
 * DocuSign 관련 처리하는 이벤트 리스터 Class입니다.
 *
 * @FileName DocuSignEventListener.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
@Transactional
public class DocuSignEventListener {

	@Inject
	DocuSignService docuSignService;
	
	
	@EventListener(condition = "#event.eventId == 'findDocusignContract'")
	public void findDocusignContract(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(docuSignService.findDocusignInfoByEcntrId(param));
	}
	
	@EventListener(condition = "#event.eventId == 'deleteDocusignContract'")
	public void deleteDocusignContract(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(docuSignService.deleteDocusignContract(param));
	}
	
	@EventListener(condition = "#event.eventId == 'createDocusignTemplate'")
	public void createDocusignTemplate(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(docuSignService.createDocusignTemplate(param));
	}
	
	@EventListener(condition = "#event.eventId == 'findDocusignTemplate'")
	public void findDocusignTemplate(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(docuSignService.findDocusignTemplate(param));
	}
	
	@EventListener(condition = "#event.eventId == 'deleteDocusignTemplate'")
	public void deleteDocusignTemplate(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(docuSignService.deleteDocusignTemplate(param));
	}
	
	@EventListener(condition = "#event.eventId == 'createDocusignEnvelope'")
	public void createDocusignEnvelope(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(docuSignService.createDocusignEnvelope(param));
	}
	
	@EventListener(condition = "#event.eventId == 'findDocusignEnvelope'")
	public void findDocusignEnvelope(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(docuSignService.findDocusignEnvelope(param));
	}
	
	@EventListener(condition = "#event.eventId == 'deleteDocusignEnvelope'")
	public void deleteDocusignEnvelope(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(docuSignService.deleteDocusignEnvelope(param));
	}

	@EventListener(condition = "#event.eventId == 'updateDocusignStatus'")
	public void updateDocusignInfo(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(docuSignService.updateDocusignStatus(param));
	}
}
