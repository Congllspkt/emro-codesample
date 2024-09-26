package smartsuite.app.bp.srm.sourcinggroup.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SourcingGroupEventPublisher {
	@Inject
	ApplicationEventPublisher applicationEventPublisher;

	/**
	 * 협력사관리그룹 수정(수정한 소싱그룹)
	 *
	 * @param param
	 */
	public ResultMap updateVenderManagementGroupNameBySourcingGroup(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("updateVenderManagementGroupNameBySourcingGroup", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
		return (ResultMap) customSpringEvent.getResult();
	}

	/**
	 * 협력사관리그룹 평가자 추가
	 *
	 * @param param
	 */
	public void insertVendorManagementGroupEvaltr(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("insertVendorManagementGroupEvaltrBySourcingGroup", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
	}
	/**
	 * 협력사관리그룹 평가자 삭제
	 *
	 * @param param
	 */
	public void deleteVendorManagementGroupEvaltr(Map<String, Object> param) {
		CustomSpringEvent customSpringEvent = CustomSpringEvent.toCompleteEvent("deleteVendorManagementGroupEvaltrBySourcingGroup", param);
		applicationEventPublisher.publishEvent(customSpringEvent);
	}
}
