package smartsuite.app.sp.vendorMaster.vendorInfo.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpVendorInfoEventPublisher {
	@Inject
	ApplicationEventPublisher applicationEventPublisher;

	/**
	 *
	 * @param param 온보딩평가 요청 대상 온보딩그룹 List
	 */
	public ResultMap saveListReqOnboardingEval(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("saveListReqOnboardingEval", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
		return (ResultMap) customSpringEvent.getResult();
	}

	/**
	 * 온보딩평가 결재진행여부 조회를 요청한다.
	 * @param vmgList 평가취소요청 대상 협력사관리그룹 리스트
	 */
	public Map findReqedOnboardingAprvProgYn(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("findReqedOnboardingAprvProgYn", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
		return (Map) customSpringEvent.getResult();
	}

	/**
	 * 협력사관리그룹 평가 미등록완료를 요청한다.
	 * @param vmgList 평가취소요청 대상 협력사관리그룹 리스트
	 */
	public ResultMap saveListOnboardingEvalUnRegComplete(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("saveListOnboardingEvalUnRegComplete", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
		return (ResultMap) customSpringEvent.getResult();
	}

	/**
	 * 협력사의 평가정보를 요청한다.
	 *
	 * param vendorInfo
	 */
	public Map findVendorEvalInfoList(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<String> evalTaskTypCcdList = (List<String>) param.get("eval_task_typ_ccd");


		if(evalTaskTypCcdList != null && !evalTaskTypCcdList.isEmpty()) {
			for(String evalTaskTypCcd : evalTaskTypCcdList){
				if("OE".equals(evalTaskTypCcd)){	// 온보딩평가
					CustomSpringEvent obdEvent = CustomSpringEvent.toCompleteEvent("findVendorObdEvalInfoList", param);
					applicationEventPublisher.publishEvent(obdEvent);
					List<Map<String, Object>> obdEvalList = (List) obdEvent.getResult();
					resultMap.put("obdEvalList", obdEvalList);
				} else if("PE".equals(evalTaskTypCcd)){		// 퍼포먼스평가
					CustomSpringEvent pfmcEvent = CustomSpringEvent.toCompleteEvent("findVendorPfmcEvalInfoList", param);
					applicationEventPublisher.publishEvent(pfmcEvent);
					List<Map<String, Object>> pfmcEvalList = (List) pfmcEvent.getResult();
					resultMap.put("pfmcEvalList", pfmcEvalList);
				} else if("SE".equals(evalTaskTypCcd)){		// 전략분석평가

				} else if("NPE".equals(evalTaskTypCcd)){	// 비가격평가

				} else if("IE".equals(evalTaskTypCcd)) {	// 검수평가

				}
			}
		}
		return resultMap;
	}
}
