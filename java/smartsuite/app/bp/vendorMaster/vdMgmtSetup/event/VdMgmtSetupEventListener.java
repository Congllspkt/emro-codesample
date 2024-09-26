package smartsuite.app.bp.vendorMaster.vdMgmtSetup.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.vendorMaster.vdMgmtSetup.service.VdMgmtSetupService;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class VdMgmtSetupEventListener {
    @Inject
    VdMgmtSetupService vdMgmtSetupService;

	/**
     * 협력사관리그룹 수정(수정한 소싱그룹)
	 *
     * @param
     */
    @EventListener(condition ="#event.eventId == 'updateVenderManagementGroupNameBySourcingGroup'")
    public void updateVenderManagementGroupNameBySourcingGroup(CustomSpringEvent event){
        vdMgmtSetupService.updateVenderManagementGroupNameBySourcingGroup((Map) event.getData());
    }

	/**
     * 협력사관리그룹 평가자 목록 조회
	 *
     * @param
     */
    @EventListener(condition ="#event.eventId == 'findListVendorManagementGroupEvaltrForView'")
    public void findListVendorManagementGroupEvaltrForView(CustomSpringEvent event){
        List result = vdMgmtSetupService.findListVendorManagementGroupEvaltrForView((Map) event.getData());
		event.setResult(result);
    }

	/**
     * 소싱그룹 담당자의 평가자여부에 따라 협력사관리그룹 평가자 추가
	 *
     * @param
     */
    @EventListener(condition ="#event.eventId == 'insertVendorManagementGroupEvaltrBySourcingGroup'")
    public void insertVendorManagementGroupEvaltrBySourcingGroup(CustomSpringEvent event){
        vdMgmtSetupService.insertVendorManagementGroupEvaltrBySourcingGroup((Map) event.getData());
    }

	/**
     * 소싱그룹 담당자의 평가자여부에 따라 협력사관리그룹 평가자 삭제
	 *
     * @param
     */
    @EventListener(condition ="#event.eventId == 'deleteVendorManagementGroupEvaltrBySourcingGroup'")
    public void deleteVendorManagementGroupEvaltrBySourcingGroup(CustomSpringEvent event){
        vdMgmtSetupService.deleteVendorManagementGroupEvaltrBySourcingGroup((Map) event.getData());
    }
}
