package smartsuite.app.common.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.service.RfxSharedService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/bp/rfx/**/")
public class RfxSharedController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RfxSharedController.class);
	
	@Inject
	RfxSharedService rfxSharedService;
	
	/**
	 * rfx, 역경매 상태바 조회한다.
	 *
	 * @param param the param
	 * @return Map
	 */
	@RequestMapping(value = "findListProStatus.do")
	public @ResponseBody Map findListProStatus(@RequestBody Map param) {
		return rfxSharedService.findListProStatus(param);
	}
	
	@RequestMapping (value = "/**/findVendorBasicInfo.do")
	public @ResponseBody Map findVendorBasicInfo(@RequestBody Map param) {
		return rfxSharedService.findVendorBasicInfo(param);
	}
	
	/**
	 * 특정 협력사에 대한 연도별 구매품목, 연도별 RFx, 연도별 구매이력 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "/**/findYearlyPerformByVendor.do")
	public @ResponseBody Map findYearlyPerformByVendor(@RequestBody Map param) {
		return rfxSharedService.findYearlyPerformByVendor(param);
	}
	
	/**
	 * 품목 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "/**/findItemBasicInfo.do")
	public @ResponseBody Map findItemBasicInfo(@RequestBody Map param) {
		return rfxSharedService.findItemBasicInfo(param);
	}
	
	/**
	 * 특정 품목에 대한 협력사 별 발주금액 합계, 발주단가 목록 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "/**/findPerformByItem.do")
	public @ResponseBody Map findPerformByItem(@RequestBody Map param) {
		return rfxSharedService.findPerformByItem(param);
	}

	@RequestMapping(value = "findListPic.do")
	public @ResponseBody List findListPic(@RequestBody Map param) {
		return rfxSharedService.findListPic(param);
	}

	/**
	 * 프로젝트 정보 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "/**/findInfoSrFromRfx.do")
	public @ResponseBody ResultMap findInfoSrFromRfx(@RequestBody Map param) {
		return rfxSharedService.findInfoSrFromRfx(param);
	}
	/**
	 * 생산판매계획 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "/**/findListSalesPlanFromRfx.do")
	public @ResponseBody ResultMap findListSalesPlanFromRfx(@RequestBody Map param) {
		return rfxSharedService.findListSalesPlanFromRfx(param);
	}
    
    /**
     * 업체정보 중복 체크
     *
     * @param param
     * @return
     */
    @RequestMapping("checkDuplicatedVdInfo.do")
    public @ResponseBody List checkDuplicatedVdInfo(@RequestBody Map param) {
        return rfxSharedService.checkDuplicatedVdInfo(param);
    }
    
    /**
     * 업체 기본 정보 저장
     * @param param
     * @return
     */
	/*@RequestMapping("saveBasicVdInfo.do")
	public @ResponseBody ResultMap saveBasicVdInfo(@RequestBody Map param) {
		return rfxSharedService.saveBasicVdInfo(param);
	}*/
    
    /**
     * 업체 기본 정보 저장
     *
     * @param param
     * @return
     */
    @RequestMapping("saveNewVdOorg.do")
    public @ResponseBody ResultMap saveNewVdOorg(@RequestBody Map param) {
        return rfxSharedService.saveNewVdOorg(param);
    }
    
    /**
     * 추가 거래가능 협력사관리그룹 조회
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "findListUnregisteredVendorManagementGroup.do")
    public @ResponseBody FloaterStream findListUnregisteredVendorManagementGroup(@RequestBody Map param) {
        return rfxSharedService.findListUnregisteredVendorManagementGroup(param);
    }
    
    /**
     * 사용대상 협력사관리유형 목록을 조회한다.
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "findListVmtUsing.do")
    public @ResponseBody List<Map<String, Object>> findListVmtUsing(@RequestBody Map param) {
        return rfxSharedService.findListVmtUsing(param);
    }
    
    /**
     * 온보딩평가요청 - 등록취소를 저장한다.
     * @param param
     * @return
     */
    @RequestMapping(value = "saveListReqOnboardingEvalCancel.do")
    public @ResponseBody ResultMap saveListReqOnboardingEvalCancel(@RequestBody Map param) {
		return rfxSharedService.saveListReqOnboardingEvalCancel(param);
    }
    
    /**
     * 온보딩평가요청 - 등록요청을 저장한다.
     * @param param
     * @return
     */
    @RequestMapping(value = "saveListReqOnboardingEval.do")
    public @ResponseBody ResultMap saveListReqOnboardingEval(@RequestBody Map param) {
        return rfxSharedService.saveListReqOnboardingEval(param);
    }
}