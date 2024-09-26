package smartsuite.app.bp.vendorMaster.vendorInfo.event;

import com.google.common.collect.Lists;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VendorInfoEventPublisher {
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
	 * 대상 온보딩평가그룹에 협력사 마스터 변경이력 UUID 저장을 요청한다.
	 *
	 * param oeInfo
	 */
	public void saveOnboardingEvalVendorAprvInfo(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("saveOnboardingEvalVendorAprvInfo", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
	}

	/**
	 * 등록대상 협력사관리그룹에 결재대상여부 변경을 요청한다.
	 *
	 * param oeVmgInfo
	 */
	public void saveOnboardingEvalVmgAprvSubj(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("saveOnboardingEvalVmgAprvSubj", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
	}
	
	/**
	 * 온보딩평가 요청 처리완료를 요청한다.
	 *
	 * param oeVmgInfo
	 */
	public void saveOnboardingEvalPrcsgEnd(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("saveOnboardingEvalPrcsgEnd", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
	}
	
	/**
	 * 대상 온보딩평가그룹에 협력사 마스터 변경이력 UUID 초기화를 요청한다.
	 *
	 * param oeInfo
	 */
	public void saveCancelOnboardingEvalVendorAprvInfo(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("saveCancelOnboardingEvalVendorAprvInfo", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
	}

	/**
	 * 등록대상 협력사관리그룹에 결재대상 취소를 요청한다.
	 *
	 * param oeVmgInfo
	 */
	public void saveCancelOnboardingEvalVmgAprvSubj(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("saveCancelOnboardingEvalVmgAprvSubj", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
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

	/**
	 * 협력사 VMT, VMG 리스트 조회 (findVendorVMTVMGInfo)
	 *
	 * param param the param
	 * @return ResultMap
	 */
	public Map<String, Object> findVendorVMTVMGInfo(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findVendorVMTVMGInfo", param);
		applicationEventPublisher.publishEvent(event);
		return (Map<String, Object>) event.getResult();
	}

	/**
	 * 협력사 운영조직별 미등록 SG 목록 조회 (단, 한 번 통과된 유효한 OEG에 속한 SG여야 한다.)
	 *
	 * @param param {vd_cd, oorg_cd}
	 * @return List
	 */
	public List<Map<String,Object>> findListUnregisteredSgByOeg(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListUnregisteredSgByOeg", param);
		applicationEventPublisher.publishEvent(event);
		return event.getResult() == null ? Lists.newArrayList() : (List<Map<String,Object>>) event.getResult();
	}

	/**
	 * 협력사 운영조직별 등록 SG 목록 조회
	 *
	 * @param param {vd_cd, oorg_cd}
	 * @return List
	 */
	public List<Map<String, Object>> findListRegisteredSg(Map<String, Object> param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListRegisteredSg", param);
		applicationEventPublisher.publishEvent(event);
		return event.getResult() == null ? Lists.newArrayList() : (List<Map<String,Object>>) event.getResult();
	}
}
