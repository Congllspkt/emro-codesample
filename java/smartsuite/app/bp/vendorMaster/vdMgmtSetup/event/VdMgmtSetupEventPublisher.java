package smartsuite.app.bp.vendorMaster.vdMgmtSetup.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VdMgmtSetupEventPublisher {
	@Inject
	ApplicationEventPublisher applicationEventPublisher;

	/**
	 *
	 * @param param 협력사관리그룹 평가자 저장 정보
	 */
	public ResultMap saveSyncVendorManagementGroupEvaltrToSourcingGroupPic(Map<String, Object> param) {
		param.put("evaltr_yn", "Y");
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("mergeSourcingGroupPic", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
		return (ResultMap) customSpringEvent.getResult();
	}
	
	/**
	 *
	 * @param param 협력사관리그룹 평가자 삭제 정보
	 */
	public ResultMap deleteSyncVendorManagementGroupEvaltrToSourcingGroupPic(Map<String, Object> param) {
		param.put("evaltr_yn", "N");
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("mergeSourcingGroupPic", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
		return (ResultMap) customSpringEvent.getResult();
	}

	/**
	 *
	 * @param param 협력사관리그룹 정보
	 */
	public List findListSourcingGroupUser(Map<String, Object> param) {
		param.put("evaltr_yn", "Y");
		param.put("sg_cd", param.get("vmg_cd"));
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("findListSourcingGroupUser", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
		return (List) customSpringEvent.getResult();
	}
}
