package smartsuite.app.bp.vendorMaster.vendorReg.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class VendorRegEventPublisher {
	@Inject
	ApplicationEventPublisher applicationEventPublisher;

	/**
	 *
	 * 온보딩평가 프로세스 평가 담당자 목록 조회를 요청한다.
	 * @param param 온보딩평가 요청 정보
	 */
	public List findListOePrcsEvaltrForSessionUser(Map<String, Object> param) {
		param.put("session_usr_id", (String) Auth.getCurrentUserInfo().get("usr_id"));
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("findListOePrcsEvaltr", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
		return (List) customSpringEvent.getResult();
	}

	/**
	 * 평가수행용 평가항목 조회
	 *
	 * param.evamltmpl_uuid - 평가템플릿 아이디
	 * @return ResultMap
	 */
	public ResultMap findOnboardingEvalfactFulfillInfo(Map param) {
		param.put("eval_task_typ_ccd", "OE");
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findEvalfactFulfillInfo", param);
		applicationEventPublisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 평가수행 정보 저장
	 *
	 * param.ten_id: ten_id,
	 * param.evalSubjMap: evalSubjectInfo,
	 * param.saveEvaltmplList: evalTemplateList,
	 * param.saveEvalfactList: saveFactList,
	 * param.saveEvalfactScaleList: selectedScaleList
	 * @return ResultMap
	 */
	public ResultMap saveOnboardingEvalFulfillment(Map param) {
		param.put("ten_id", Auth.getCurrentTenantId());
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveEvaltrEvalfactRes", param);
		applicationEventPublisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
}
