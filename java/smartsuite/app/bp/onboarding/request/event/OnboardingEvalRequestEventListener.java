package smartsuite.app.bp.onboarding.request.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.onboarding.request.service.OnboardingEvalMonitoringService;
import smartsuite.app.bp.onboarding.request.service.OnboardingEvalRequestService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class OnboardingEvalRequestEventListener {

	@Inject
	OnboardingEvalRequestService onboardingEvalRequestService;

	@Inject
	OnboardingEvalMonitoringService onboardingEvalMonitoringService;

	/**
	 *
	 * @param param 온보딩평가 요청 대상 온보딩그룹 List
	 */
	@EventListener(condition ="#event.eventId == 'saveListReqOnboardingEval'")
	public void saveListReqOnboardingEval(CustomSpringEvent event){
		ResultMap result = onboardingEvalRequestService.saveListReqOnboardingEval((Map) event.getData());
		event.setResult(result);
	}

	/**
	 *
	 * 온보딩평가 프로세스 평가 담당자 목록을 조회한다.
	 * @param param 온보딩평가 요청 정보
	 */
	@EventListener(condition ="#event.eventId == 'findListOePrcsEvaltr'")
	public void findListOePrcsEvaltr(CustomSpringEvent event){
		List result = onboardingEvalMonitoringService.findListOePrcsEvaltr((Map) event.getData());
		event.setResult(result);
	}

	/**
	 * 온보딩평가 결재진행여부를 조회한다.
	 * @param vmgList 평가취소요청 대상 협력사관리그룹 리스트
	 */
	@EventListener(condition ="#event.eventId == 'findReqedOnboardingAprvProgYn'")
	public void findReqedOnboardingAprvProgYn(CustomSpringEvent event){
		Map<String, Object> result = onboardingEvalMonitoringService.findReqedOnboardingAprvProgYn((Map) event.getData());
		event.setResult(result);
	}
}
