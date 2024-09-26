package smartsuite.app.bp.vendorMaster.vdMgmtSetup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.vendorMaster.vdMgmtSetup.service.VdMgmtSetupService;
import smartsuite.app.bp.vendorMaster.vendorInfo.service.VendorMasterService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/vendorMaster/vdMgmtSetup/**/")
public class VdMgmtSetupController {

    @Inject
	VdMgmtSetupService vdMgmtSetupService;

	@Inject
	VendorMasterService vendorMasterService;

	/**
	 * 협력사관리유형 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the vendor mgmt typ list
	 * @Date : 2023. 5. 30
	 * @Method Name : findListVmt
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListVmt.do")
	public @ResponseBody FloaterStream findListVmt(@RequestBody Map param) {
		// 대용량 처리
		return vdMgmtSetupService.findListVmt(param);
	}

	/**
	 * 미등록 협력사관리유형 조회 (findListUnregisteredVendorManagementType)
	 *
	 * @param param
	 * @Date : 2023. 09. 05
	 * @author yjPark
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "**/findListUnregisteredVendorManagementType.do")
	public @ResponseBody List findListUnregisteredVendorManagementType(@RequestBody Map param) {
		return vdMgmtSetupService.findListUnregisteredVendorManagementType(param);
	}

	/**
	 * 협력사관리유형 목록 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 5. 30
	 * @Method Name : saveListVmt
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveListVmt.do")
	public @ResponseBody ResultMap saveListVmt(@RequestBody Map param) {
		return vdMgmtSetupService.saveListVmt(param);
	}

	/**
	 * 협력사관리유형 목록 삭제를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 5. 30
	 * @Method Name : deleteListVendorType
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteListVmt.do")
	public @ResponseBody ResultMap deleteListVmt(@RequestBody Map param) {
		return vdMgmtSetupService.deleteListVmt(param);
	}

	/**
	 *  LEAF 운영조직 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the leaf org list
	 * @Date : 2023. 5. 30
	 * @Method Name : findListOorgCdAll
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListOorgCdAll.do")
	public @ResponseBody List findListOorgCdAll(@RequestBody String param) {
		// 대용량 처리
		return vendorMasterService.findListOorgCdAll(param);
	}

	/**
	 * 사용대상 협력사 유형 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the using vendor mgmt typ list
	 * @Date : 2023. 5. 31
	 * @Method Name : findListVmtUsing
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListVmtUsing.do")
	public @ResponseBody FloaterStream findListVmtUsing(@RequestBody Map param) {
		// 대용량 처리
		return vdMgmtSetupService.findListVmtUsing(param);
	}

	/**
	 * 협력사관리그룹 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the vendor mgmt grp list
	 * @Date : 2023. 6. 1
	 * @Method Name : findListVmg
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListVmg.do")
	public @ResponseBody FloaterStream findListVmg(@RequestBody Map param) {
		// 대용량 처리
		return vdMgmtSetupService.findListVmg(param);
	}

	/**
	 * 협력사관리그룹 목록 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 1
	 * @Method Name : saveListVmg
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveListVmg.do")
	public @ResponseBody ResultMap saveListVmg(@RequestBody Map param) {
		return vdMgmtSetupService.saveListVmg(param);
	}

	/**
	 * 협력사관리그룹 상세 저장을 요청한다.
	 *
	 * @author : kim
	 * @param param the param
	 * @return the Map
	 * @Date : 2024. 7. 23
	 * @Method Name : saveSgDetail
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value= "**/saveSgDetail.do")
	public @ResponseBody ResultMap saveSgDetail(@RequestBody Map param) {
		return vdMgmtSetupService.saveSgDetail(param);
	}
	
	/**
	 * 협력사관리그룹 목록 삭제를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 1
	 * @Method Name : deleteListVmg
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteListVmg.do")
	public @ResponseBody ResultMap deleteListVmg(@RequestBody Map param) {
		return vdMgmtSetupService.deleteListVmg(param);
	}
	
	/**
	 * 소싱그룹 상세정보 조회
	 *
	 * @author : kim
	 * @param param the param
	 * @return List
	 * @Date : 2024. 7. 22
	 * @Method Name : findSourcingGroupDetail
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "**/findSourcingGroupDetail.do")
	public @ResponseBody Map findSourcingGroupDetail(@RequestBody Map param) {
		return vdMgmtSetupService.findSourcingGroupDetail(param);
	}

	/**
	 * 협력사관리그룹 품목분류 목록 조회
	 *
	 * @author : kim
	 * @param param the param
	 * @return List
	 * @Date : 2024. 7. 22
	 * @Method Name : findSourcingGroupItemCategoryList
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findSourcingGroupItemCategoryList.do")
	public @ResponseBody List findSourcingGroupItemCategoryList(@RequestBody Map param) {
		return vdMgmtSetupService.findSourcingGroupItemCategoryList(param);
	}

	/**
	 * 협력사관리그룹 품목분류 저장
	 *
	 * @author : kim
	 * @param param the param
	 * @return ResultMap
	 * @Date : 2024. 7. 22
	 * @Method Name : saveSgItemCategoryList
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value = "**/saveSgItemCategoryList.do")
	public @ResponseBody ResultMap saveSgItemCategoryList(@RequestBody Map param) {
		return vdMgmtSetupService.saveSgItemCategoryList(param);
	}

	/**
	 * 운영조직 협력사관리그룹 목록 조회를 요청한다.
	 *
	 * @author : kim
	 * @param param the param
	 * @return FloaterStream
	 * @Date : 2024. 05. 28
	 * @Method Name : findListOorgVmg
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value="**/findListOorgVmg.do")
	public @ResponseBody FloaterStream findListOorgVmg(@RequestBody Map param) {
		return vdMgmtSetupService.findListOorgVmg(param);
	}

	/**
	 * 운영조직 협력사관리그룹 목록 저장을 요청한다.
	 *
	 * @author : kim
	 * @param param the param
	 * @return the Map
	 * @Date : 2024. 05. 28
	 * @Method Name : saveListOorgVmg
	 */
	@AuthCheck (authCode =  Const.SAVE)
	@RequestMapping (value="**/saveListOorgVmg.do")
	public @ResponseBody ResultMap saveListOorgVmg(@RequestBody Map param) {
		return vdMgmtSetupService.saveListOrogVmg(param);
	}

	/**
	 * 운영조직 협력사관리그룹 목록 삭제를 요청한다.
	 *
	 * @author : kim
	 * @param param the param
	 * @return the Map
	 * @Date : 2024. 05. 28
	 * @Method Name : deleteListOorgVmg
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value="**/deleteListOorgVmg.do")
	public @ResponseBody ResultMap deleteListOorgVmg(@RequestBody Map param) {
		return vdMgmtSetupService.deleteListOorgVmg(param);
	}

	/**
	 * 운영조직과 매핑되지 않은 협력사관리그룹 목록 조회를 요청한다.
	 *
	 * @author : kim
	 * @param param the param
	 * @return FloaterStream
	 * @Date : 2024. 05. 28
	 * @Method Name : findListNonMappingOorgVmg
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value="**/findListNonMappingOorgVmg.do")
	public @ResponseBody FloaterStream findListNonMappingOorgVmg(@RequestBody Map param) {
		return vdMgmtSetupService.findListNonMappingOorgVmg(param);
	}

	/**
	 * 협력사관리그룹 평가자 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the vendor mgmt grp evaltr list
	 * @Date : 2023. 8. 7
	 * @Method Name : findListVendorManagementGroupEvaltr
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListVendorManagementGroupEvaltr.do")
	public @ResponseBody FloaterStream findListVendorManagementGroupEvaltr(@RequestBody Map param) {
		// 대용량 처리
		return vdMgmtSetupService.findListVendorManagementGroupEvaltr(param);
	}

	/**
	 * 협력사관리그룹 평가자 목록 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 8. 7
	 * @Method Name : saveListVendorManagementGroupEvaltr
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveListVendorManagementGroupEvaltr.do")
	public @ResponseBody ResultMap saveListVendorManagementGroupEvaltr(@RequestBody Map param) {
		return vdMgmtSetupService.saveListVendorManagementGroupEvaltr(param);
	}

	/**
	 * 협력사관리그룹 평가자 목록 삭제를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 8. 7
	 * @Method Name : deleteListVendorManagementGroupEvaltr
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteListVendorManagementGroupEvaltr.do")
	public @ResponseBody ResultMap deleteListVendorManagementGroupEvaltr(@RequestBody Map param) {
		return vdMgmtSetupService.deleteListVendorManagementGroupEvaltr(param);
	}
}