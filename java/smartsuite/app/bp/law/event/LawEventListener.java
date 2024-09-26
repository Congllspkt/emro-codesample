package smartsuite.app.bp.law.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.law.service.LawService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

/**
 * DocuSign 관련 처리하는 이벤트 리스터 Class입니다.
 *
 * @FileName DocuSignEventListener.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
@Transactional
public class LawEventListener {

	@Inject
	LawService lawService;


	@EventListener(condition = "#event.eventId == 'findReviewCntrdocTmpl'")
	public void findReviewCntrdocTmpl(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(lawService.findReviewCntrdocTmpl(param));
	}

	@EventListener(condition = "#event.eventId == 'findListReviewAppxTmpl'")
	public void findListReviewAppxTmpl(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(lawService.findListReviewAppxTmpl(param));
	}

}
