package smartsuite.app.bp.edoc.template.event;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class TemplateEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;
	
	
}