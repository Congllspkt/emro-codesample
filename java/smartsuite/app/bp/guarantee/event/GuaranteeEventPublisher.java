package smartsuite.app.bp.guarantee.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class GuaranteeEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;


}