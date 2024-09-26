package smartsuite.app.bp.vendorMaster.vendorInfo.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.vendorMaster.vendorInfo.service.VendorMasterService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class VendorInfoEventListener {

	@Inject
	VendorMasterService vendorMasterService;

	/**
	 *
	 * @param param 잠재 업체 등록 요청 대상 vendor
	 */
	@EventListener(condition ="#event.eventId == 'updateVdOorgObdTypCcdToPtnl'")
	public void updateVdOorgObdTypCcdToPtnl(CustomSpringEvent event){
		vendorMasterService.updateVdOorgObdTypCcdToPtnl((Map) event.getData());
	}

	/**
	 * 협력사 변경 상태 조회 (findVendorModifyStatus)
	 *
	 * @param VendorInfo
	 */
	@EventListener(condition ="#event.eventId == 'findVendorModifyStatus'")
	public void findVendorModifyStatus(CustomSpringEvent event){
		Map<String, Object> resultMap = vendorMasterService.findVendorModifyStatus((Map) event.getData());
		event.setResult(resultMap);
	}

	/**
	 * 기본거래계약 요청 접수 시
	 *
	 * @param
	 */
	@EventListener(condition ="#event.eventId == 'receiptReqOnboardingContract'")
	public void receiptReqOnboardingContract(CustomSpringEvent event){
		Map<String, Object> eventMap = (Map) event.getData();

		// TODO 추후 로직 확정 시 수정
		event.setResult(ResultMap.SUCCESS());
	}

	/**
	 * 기본거래계약 요청 반려 시
	 *
	 * @param
	 */
	@EventListener(condition ="#event.eventId == 'rejectReqOnboardingContract'")
	public void rejectReqOnboardingContract(CustomSpringEvent event){
		Map<String, Object> eventMap = (Map) event.getData();

		// TODO 추후 로직 확정 시 수정
		event.setResult(ResultMap.SUCCESS());
	}

	/**
	 * 기본거래계약 체결 시
	 *
	 * @param
	 */
	@EventListener(condition ="#event.eventId == 'completeElementaryContract'")
	public void completeElementaryContract(CustomSpringEvent event){
		ResultMap resultMap = vendorMasterService.saveVendorOperationOrganizationContractInfo((Map) event.getData());
		event.setResult(resultMap);
	}
    
    /**
     * 추가 거래가능 협력사관리그룹 조회 (findListUnregisteredVendorManagementGroup)
     * @param e
     */
    @EventListener(condition = "#e.eventId == 'findListUnregisteredVendorManagementGroup'")
    public void findListUnregisteredVendorManagementGroup(CustomSpringEvent e){
        e.setResult(vendorMasterService.findListUnregisteredVendorManagementGroup((Map) e.getData()));
    }
    
    /**
     * 사용대상 협력사관리유형 목록을 조회한다.
     * @param e
     */
    @EventListener(condition = "#e.eventId == 'findListVmtUsing'")
    public void findListVmtUsing(CustomSpringEvent e){
        e.setResult(vendorMasterService.findListVmtUsing((Map) e.getData()));
    }
    
    /**
     * 운영조직 협력사 추가
     * @param e
     */
    @EventListener(condition = "#e.eventId == 'insertVendorOperationOrganizationInfo'")
    public void insertVendorOperationOrganizationInfo(CustomSpringEvent e){
        vendorMasterService.insertVendorOperationOrganizationInfo((Map) e.getData());
    }
    
    
    
    
    
}
