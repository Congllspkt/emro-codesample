package smartsuite.app.sp.onboarding.obdEval.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.app.sp.onboarding.obdEval.service.SpOnboardingEvalService;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpOnboardingEvalEventListener {
	
	@Inject
	SpOnboardingEvalService spObdEvalService;

	/**
	 * 협력사 외부화면 온보딩평가 정보 저장
	 *
	 * @param param
	 */
	@EventListener(condition ="#event.eventId == 'saveSpOnboardingEvalFulfillment'")
	public void saveSpOnboardingEvalFulfillment(CustomSpringEvent event){
		ResultMap result = spObdEvalService.saveOnboardingEvalFulfillment((Map) event.getData());
		event.setResult(result);
	}
}
