package smartsuite.app.common.shared.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfxSharedEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;
	
	public List<Map> findListYearlyPoItemByVendor(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListYearlyPoItemByVendor", param);
		publisher.publishEvent(event);
		
		if(event.getResult() != null) {
			return (List<Map>) event.getResult();
		} else {
			return null;
		}
	}
	
	public List<Map> findListYearlyPoTotAmtByVendor(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListYearlyPoTotAmtByVendor", param);
		publisher.publishEvent(event);
		
		if(event.getResult() != null) {
			return (List<Map>) event.getResult();
		} else {
			return null;
		}
	}
	
	public List<Map> findListMonthlyPoTotAmtByVendor(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListMonthlyPoTotAmtByVendor", param);
		publisher.publishEvent(event);
		
		if(event.getResult() != null) {
			return (List<Map>) event.getResult();
		} else {
			return null;
		}
	}
	
	public List<Map> findListVendorPoTotAmtByItem(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListVendorPoTotAmtByItem", param);
		publisher.publishEvent(event);
		
		if(event.getResult() != null) {
			return (List<Map>) event.getResult();
		} else {
			return null;
		}
	}
	
	public List<Map> findListVendorPoItemPriceByItem(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListVendorPoItemPriceByItem", param);
		publisher.publishEvent(event);
		
		if(event.getResult() != null) {
			return (List<Map>) event.getResult();
		} else {
			return null;
		}
	}

	public ResultMap findInfoSrFromRfx(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findInfoSrFromRfx", param);
		publisher.publishEvent(event);

		if(event.getResult() != null) {
			return (ResultMap) event.getResult();
		} else {
			return null;
		}
	}

	public ResultMap findListSalesPlanFromRfx(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListSalesPlanFromRfx", param);
		publisher.publishEvent(event);

		if(event.getResult() != null) {
			return (ResultMap) event.getResult();
		} else {
			return null;
		}
	}
    
    /**
     * 업체정보 중복체크
     * @param param
     * @return
     */
    public List checkDuplicatedVdInfo(Map param) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("checkDuplicatedVdInfo", param);
        publisher.publishEvent(event);
        return (List) event.getResult();
    }
    
    /**
     * 업체 기본 정보 저장
     * @param param
     * @return
     */
    public ResultMap saveBasicVdInfo(Map param) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveBasicVdInfo", param);
        publisher.publishEvent(event);
        return (ResultMap) event.getResult();
    }
    /**
     * 신규업체 운영조직 정보 저장
     * @param param
     * @return
     */
    public ResultMap saveNewVdOorg(Map param) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveNewVdOorg", param);
        publisher.publishEvent(event);
        return (ResultMap) event.getResult();
    }
    
    /**
     * 추가 거래가능 협력사관리그룹 조회
     * @param param
     * @return
     */
    public FloaterStream findListUnregisteredVendorManagementGroup(Map param) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListUnregisteredVendorManagementGroup", param);
        publisher.publishEvent(event);
        return (FloaterStream) event.getResult();
    }
    
    /**
     * 사용대상 협력사관리유형 목록을 조회한다.
     * @param param
     * @return
     */
    public List<Map<String, Object>> findListVmtUsing(Map param) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListVmtUsing", param);
        publisher.publishEvent(event);
        return (List) event.getResult();
    }
    
    /**
     * 온보딩평가요청 - 등록요청을 저장한다.
     * @param param
     * @return
     */
    public ResultMap saveListReqOnboardingEval(Map param) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveListReqOnboardingEval", param);
        publisher.publishEvent(event);
        return (ResultMap) event.getResult();
    }
    
    /**
     * 온보딩평가 결재진행여부 조회를 요청한다.
     * @param param
     * @return
     */
    public Map<String, Object> findReqedOnboardingAprvProgYn(Map param) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findReqedOnboardingAprvProgYn", param);
        publisher.publishEvent(event);
        return (Map) event.getResult();
    }
    
    /**
     * 협력사관리그룹 평가 미등록완료를 요청한다.
     * @param param
     * @return
     */
    public ResultMap saveListOnboardingEvalUnRegComplete(Map param) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveListOnboardingEvalUnRegComplete", param);
        publisher.publishEvent(event);
        return (ResultMap) event.getResult();
    }
    
    /**
     * 운영조직 협력사 추가
     * @param pivotMap
     */
    public void insertVendorOperationOrganizationInfo(Map<String, Object> pivotMap) {
        CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("insertVendorOperationOrganizationInfo", pivotMap);
        publisher.publishEvent(event);
    }
}
