package smartsuite.app.bp.srm.sourcinggroup.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.sourcinggroup.service.SourcingGroupService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SourcingGroupEventListener {

	@Inject
	SourcingGroupService sourcingGroupService;

	/**
	 * 소싱그룹 담당자 중 평가자 목록 조회
	 *
	 * @param param
	 */
	@EventListener(condition ="#event.eventId == 'findListSourcingGroupUser'")
	public void findListSourcingGroupUser(CustomSpringEvent event){
		List result = sourcingGroupService.findListSourcingGroupUser((Map) event.getData());
		event.setResult(result);
	}

	/**
	 * 협력사관리그룹 평가자 저장/삭제 정보, 소싱그룹 담당자 정보에 동기화
	 *
	 * @param param
	 */
	@EventListener(condition ="#event.eventId == 'mergeSourcingGroupPic'")
	public void mergeSourcingGroupPic(CustomSpringEvent event){
		sourcingGroupService.mergeSourcingGroupPic((Map) event.getData());
	}
}
