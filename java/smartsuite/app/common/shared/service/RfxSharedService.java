package smartsuite.app.common.shared.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.event.RfxSharedEventPublisher;
import smartsuite.app.common.shared.repository.RfxSharedRepository;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfxSharedService {
	
	@Inject
	RfxSharedRepository rfxSharedRepository;
	
	@Inject
	RfxSharedEventPublisher rfxSharedEventPublisher;
	
	/**
	 * rfx, 역경매 상태바 조회한다.
	 *
	 * @param param the param
	 * @return Map
	 */
	public Map findListProStatus(Map param) {
		Map result = Maps.newHashMap();
		
		//main progress bar 데이터 조회
		List<Map> mainProStatusList = rfxSharedRepository.findListProStatusForMainProgressBarView(param);
		
		//sub progress bar 데이터 조회
		List<Map> subProStatusList = rfxSharedRepository.findListProSubStatusForMainProgressBarView(param);
		
		result.put("mainProStatusList", mainProStatusList);
		result.put("subProStatusList", subProStatusList);
		return result;
	}
	
	/**
	 * 협력사 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map findVendorBasicInfo(Map param) {
		return rfxSharedRepository.findVendorBasicInfo(param);
	}

	public List<Map> findListYearlyRfxItemByVendor(Map param){
		return rfxSharedRepository.findListYearlyRfxItemByVendor(param);
	}
	
	public Map findYearlyPerformByVendor(Map param) {
		Map result = Maps.newHashMap();
		
		List<Map> yearlyRfxItemList = this.findListYearlyRfxItemByVendor(param);
		result.put("yearlyRfxItemList", yearlyRfxItemList);
		
		// PRO 모듈로 인터페이스
		List<Map> yearlyPoItemList = rfxSharedEventPublisher.findListYearlyPoItemByVendor(param);
		List<Map> yearlyPoTotAmtList = rfxSharedEventPublisher.findListYearlyPoTotAmtByVendor(param);
		List<Map> monthlyPoTotAmtList = rfxSharedEventPublisher.findListMonthlyPoTotAmtByVendor(param);
		result.put("yearlyPoItemList", yearlyPoItemList);
		result.put("yearlyPoTotAmtList", yearlyPoTotAmtList);
		result.put("monthlyPoTotAmtList", monthlyPoTotAmtList);
		
		return result;
	}
	
	/**
	 * 품목 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map findItemBasicInfo(Map param) {
		return rfxSharedRepository.findItemBasicInfo(param);
	}
	
	public Map findPerformByItem(Map param) {
		List<Map> vendorPoTotAmtList = rfxSharedEventPublisher.findListVendorPoTotAmtByItem(param);
		List<Map> vendorPoItemPriceList = rfxSharedEventPublisher.findListVendorPoItemPriceByItem(param);
		
		Map result = Maps.newHashMap();
		result.put("vendorPoTotAmtList", vendorPoTotAmtList);
		result.put("vendorPoItemPriceList", vendorPoItemPriceList);
		return result;
	}

	public List findListPic(Map<String, Object> param) {
		return rfxSharedRepository.findListPic(param);
	}

	public ResultMap findInfoSrFromRfx(Map<String, Object> param) {
		return rfxSharedEventPublisher.findInfoSrFromRfx(param);
	}

	public ResultMap findListSalesPlanFromRfx(Map param) {
        return rfxSharedEventPublisher.findListSalesPlanFromRfx(param);
    }
    
    /**
     * 업체정보 중복체크
     * @param param
     * @return
     */
    public List checkDuplicatedVdInfo(Map param) {
        return rfxSharedEventPublisher.checkDuplicatedVdInfo(param);
    }
    
    /**
     * 업체 기본 정보 저장
     * @param param
     * @return
     */
    public ResultMap saveBasicVdInfo(Map param) {
        return rfxSharedEventPublisher.saveBasicVdInfo(param);
    }
    public ResultMap saveNewVdOorg(Map param) {
        return rfxSharedEventPublisher.saveNewVdOorg(param);
    }
    
    /**
     * 초대 협력사 조회
     * @param param
     * @return
     */
    public Map findRfxInviteVendor(Map param) {
        return rfxSharedRepository.findRfxInviteVendor(param);
    }
    
    /**
     * 추가 거래가능 협력사관리그룹 조회
     * @param param
     * @return
     */
    public FloaterStream findListUnregisteredVendorManagementGroup(Map param) {
        return rfxSharedEventPublisher.findListUnregisteredVendorManagementGroup(param);
    }
    
    /**
     * 사용대상 협력사 유형 목록 조회를 요청한다.
     * @param param
     * @return
     */
    public List<Map<String, Object>> findListVmtUsing(Map param) {
        return rfxSharedEventPublisher.findListVmtUsing(param);
    }
    
    /**
     * 온보딩평가요청 - 등록취소를 저장한다.
     * @param param
     * @return
     */
    public ResultMap saveListReqOnboardingEvalCancel(Map param) {
        // 1. 온보딩평가 결재진행여부 조회
        Map<String, Object> aprvProgMap = rfxSharedEventPublisher.findReqedOnboardingAprvProgYn(param);
        String aprvProgYn = aprvProgMap.get("aprv_prog_yn") == null ? "" : aprvProgMap.get("aprv_prog_yn").toString();
        if("Y".equals(aprvProgYn)) { // 협력사관리그룹 결재 진행 중
            return ResultMap.INVALID();
        }

        // 2. 온보딩평가 품의 - 미등록완료 저장 호출
        return rfxSharedEventPublisher.saveListOnboardingEvalUnRegComplete(param);
    }
    
    /**
     * 온보딩평가요청 - 등록요청을 저장한다.
     * @param param
     * @return
     */
    public ResultMap saveListReqOnboardingEval(Map param) {
        List<Map<String, Object>> lists = (List<Map<String, Object>>)param.get("checkList");

        if(lists == null || lists.isEmpty()){
            return ResultMap.FAIL();
        }
        
        // VD_OORG에 체크
        Map<String, Object> pivotMap = lists.get(0);
        String vdOorgUuid = (String) pivotMap.get("vd_oorg_uuid");
        if(vdOorgUuid == null || vdOorgUuid.isEmpty()) {
            vdOorgUuid = UUID.randomUUID().toString();
            pivotMap.put("vd_oorg_uuid", vdOorgUuid);
            rfxSharedEventPublisher.insertVendorOperationOrganizationInfo(pivotMap);
        }
        
        ResultMap resultMap = rfxSharedEventPublisher.saveListReqOnboardingEval(param);  // 온보딩평가요청
        Map returnInfo = lists.get(0);
        if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
            Map<String, Object> resultInfo = resultMap.getResultData();
            returnInfo.put("oe_uuid", resultInfo.get("oe_uuid"));
        }

        return ResultMap.SUCCESS(returnInfo);
    }
}