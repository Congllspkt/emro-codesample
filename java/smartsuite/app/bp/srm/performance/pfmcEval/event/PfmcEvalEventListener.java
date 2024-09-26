package smartsuite.app.bp.srm.performance.pfmcEval.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.performance.pfmcEval.service.PfmcEvalService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class PfmcEvalEventListener {

	@Inject
	PfmcEvalService pfmcEvalService;

	/**
	 * 협력사별 퍼포먼스평가 결과 목록을 조회한다.
	 *
	 * @param param 협력사 정보
	 */
	@EventListener(condition ="#event.eventId == 'findVendorPfmcEvalInfoList'")
	public void findVendorPfmcEvalInfoList(CustomSpringEvent event){
		List result = pfmcEvalService.findVendorPfmcEvalInfoList((Map) event.getData());
		event.setResult(result);
	}
}
