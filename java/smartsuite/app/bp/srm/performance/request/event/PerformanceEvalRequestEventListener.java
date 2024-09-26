package smartsuite.app.bp.srm.performance.request.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.performance.request.service.PerformanceEvalRequestService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class PerformanceEvalRequestEventListener {

	@Inject
	PerformanceEvalRequestService performanceEvalRequestService;

	/**
	 * 특정 퍼포먼스평가그룹 협력사관리그룹으로 퍼포먼스평가 통보 전 임시저장 데이터를 조회한다.
	 *
	 * @param param PEG - VMG Mapping Info
	 */
	@EventListener(condition ="#event.eventId == 'findPeInfoBeforeCrngVmg'")
	public void findPeInfoBeforeCrngVmg(CustomSpringEvent event){
		List result = performanceEvalRequestService.findPeInfoBeforeCrngVmg((Map) event.getData());
		event.setResult(result);
	}
}
