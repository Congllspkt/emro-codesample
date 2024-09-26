package smartsuite.app.bp.vendorMaster.vendorReg;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.vendorMaster.vendorInfo.service.VendorMasterService;
import smartsuite.app.bp.vendorMaster.vendorReg.service.VendorRegService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/vendorMaster/vendorReg/**/")
public class VendorRegController {
	
    @Inject
    VendorRegService vendorRegService;

	@Inject
	VendorMasterService vendorMasterService;

	/**
     * 운영단위별 운영조직 조회.
	 * @param param
	 * @Date : 2023. 07. 17
	 * @author yjPark
     */
    @RequestMapping (value = "findListOorgCdAll.do")
    public @ResponseBody List findListOorgCdAll(@RequestBody String param) {
        return vendorMasterService.findListOorgCdAll(param);
    }

	/**
	 * 중복 체크.
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    @RequestMapping(value = "checkDuplicatedVdInfo.do")
    public @ResponseBody List checkDuplicatedVdInfo(@RequestBody Map param) {
        return vendorRegService.checkDuplicatedVdInfo(param);
    }

	/**
	 * 협력사 신규등록 저장
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	@RequestMapping(value = "saveBasicVdInfo.do")
	public @ResponseBody ResultMap saveBasicVdInfo(@RequestBody Map param) {
		return vendorRegService.saveBasicVdInfo(param);
	}

	/**
	 * 협력사 주요정보 조회
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	@RequestMapping(value="findDetailRegVdInfo.do")
	public @ResponseBody Map findDetailRegVdInfo(@RequestBody Map param){
		return vendorRegService.findDetailRegVdInfo(param);
	}

	/**
	 * 협력사 주요정보 저장
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	@RequestMapping(value="saveDetailVdInfo.do")
	public @ResponseBody ResultMap saveDetailVdInfo(@RequestBody Map param){
		return vendorRegService.saveDetailVdInfo(param);
	}

	/**
	 * 협력사 재무정보 조회 (findVdStsInfo)
	 *
	 * @param Param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	@RequestMapping(value="findVendorFinanceInfo.do")
	public @ResponseBody List findVendorFinanceInfo(@RequestBody Map param){
		return vendorRegService.findVendorFinanceInfo(param);
	}

	/**
	 * 협력사 재무정보 저장 (saveVdStsInfo)
	 *
	 * @param Param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	@RequestMapping(value="saveVendorFinanceInfo.do")
	public @ResponseBody ResultMap saveVendorFinanceInfo(@RequestBody Map param){
		return vendorRegService.saveVendorFinanceInfo(param);
	}

	/**
	 * 사용대상 협력사 유형 목록 조회를 요청한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 01
	 * @author yjPark
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVmtUsing.do")
	public @ResponseBody List<Map<String, Object>> findListVmtUsing(@RequestBody Map param) {
		return vendorMasterService.findListVmtUsing(param);
	}

	/**
	 * 추가 거래가능 협력사관리그룹 조회 (findListUnregisteredVendorManagementGroup)
	 *
	 * @param param
	 * @Date : 2023. 06. 22
	 * @author yjPark
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListUnregisteredVendorManagementGroup.do")
	public @ResponseBody FloaterStream findListUnregisteredVendorManagementGroup(@RequestBody Map param) {
		return vendorMasterService.findListUnregisteredVendorManagementGroup(param);
	}

	/**
	 * 온보딩평가요청 - 등록요청을 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 30
	 * @author yjPark
	 */
    @AuthCheck (authCode = Const.VENDOR_SAVE)
    @RequestMapping(value = "saveListReqOnboardingEval.do")
    public @ResponseBody ResultMap saveListReqOnboardingEval(@RequestBody Map param) {
        return vendorRegService.saveListReqOnboardingEval(param);
    }

	/**
	 * 온보딩평가요청 - 등록취소를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 11
	 * @author yjPark
	 */
    @AuthCheck (authCode = Const.VENDOR_SAVE)
    @RequestMapping(value = "saveListReqOnboardingEvalCancel.do")
    public @ResponseBody ResultMap saveListReqOnboardingEvalCancel(@RequestBody Map param) {
		return vendorMasterService.saveListReqOnboardingEvalCancel(param);
    }

	/**
	 * 온보딩평가수행 항목 정보 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the evalfact information to be fulfilled
	 * @Date : 2023. 7. 1
	 * @Method Name : findOnboardingEvalfactFulfillInfo
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findOnboardingEvalfactFulfillInfo.do")
	public @ResponseBody ResultMap findOnboardingEvalfactFulfillInfo(@RequestBody Map param) {
		return vendorRegService.findOnboardingEvalfactFulfillInfo(param);
	}

	/**
	 * 온보딩평가수행 정보 저장을 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 13
	 * @Method Name : saveOnboardingEvalFulfillment
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "saveOnboardingEvalFulfillment.do")
	public @ResponseBody ResultMap saveOnboardingEvalFulfillment(@RequestBody Map param) {
		return vendorRegService.saveOnboardingEvalFulfillment(param);
	}
}
