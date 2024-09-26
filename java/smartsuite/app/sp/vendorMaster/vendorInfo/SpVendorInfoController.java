package smartsuite.app.sp.vendorMaster.vendorInfo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.vendorMaster.vendorInfo.service.SpVendorMasterService;
import smartsuite.app.sp.vendorMaster.vendorInfo.service.SpVendorInfoService;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/sp/vendorMaster/vendorInfo/**/")
public class SpVendorInfoController {

    @Inject
    SpVendorMasterService spVendorMasterService;

	@Inject
	SpVendorInfoService spVendorInfoService;

	/**
     * 운영단위별 운영조직 조회.
	 * @param param
	 * @Date : 2023. 07. 17
	 * @author yjPark
     */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping (value = "findListOorgCdAll.do")
    public @ResponseBody List findListOorgCdAll(@RequestBody String param) {
        return spVendorMasterService.findListOorgCdAll(param);
    }

	/**
	 * 목록 조회. (findListVdInfo)
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping(value = "findListVdProfile.do")
    public @ResponseBody FloaterStream findListVdProfile(@RequestBody Map param) {
        return spVendorMasterService.findListVdProfile(param);
    }

	/**
	 * 통합정보 조회 (findTotalInfo)
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findTotalVdInfo.do")
	public @ResponseBody Map<String, Object> findTotalVdInfo(@RequestBody Map param) {
		return spVendorMasterService.findTotalVdInfo(param);
	}

	/**
	 * 기본정보 조회 (findBasicInfo)
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findBasicVendorInfo.do")
	public @ResponseBody Map<String, Object> findBasicVendorInfo(@RequestBody Map param) {
		return spVendorMasterService.findBasicVendorInfo(param);
	}

	/**
	 * 기본정보 저장, 수정 (saveListBasicInfo)
	 *
	 * @param param
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveBasicVendorInfo.do")
	public @ResponseBody ResultMap saveBasicVendorInfo(@RequestBody Map param) {
		return spVendorMasterService.saveBasicVendorInfo(param);
	}

	/**
	 * 기본정보 첨부파일 저장, 수정 (saveBasicAttachInfo)
	 *
	 * @param param
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveBasicAttachmentInfo.do")
	public @ResponseBody ResultMap saveBasicAttachmentInfo(@RequestBody Map param) {
		return spVendorMasterService.saveBasicAttachmentInfo(param);
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
		return spVendorMasterService.findListVmtUsing(param);
	}

	/**
	 * 운영조직 탭 운영조직정보 & 협력사관리유형 조회
	 *
	 * @param param
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findVdOorgAndVmtVmgInfo.do")
	public @ResponseBody Map<String, Object> findVdOorgAndVmtVmgInfo(@RequestBody Map param) {
		return spVendorMasterService.findVdOorgAndVmtVmgInfo(param);
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
		return spVendorMasterService.findListUnregisteredVendorManagementGroup(param);
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
		return spVendorMasterService.saveListReqOnboardingEval(param);
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
		return spVendorMasterService.saveListReqOnboardingEvalCancel(param);
	}

	/**
	 * 공장정보 조회를 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 16
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVdFatyInfo.do")
	public @ResponseBody List findListVdFatyInfo(@RequestBody Map param) {
		return spVendorInfoService.findListVdFatyInfo(param);
	}

	/**
	 * 공장정보 저장을 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 16
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveListVdFatyInfo.do")
	public @ResponseBody ResultMap saveListVdFatyInfo(@RequestBody Map param) {
		return spVendorInfoService.saveListVdFatyInfo(param);
	}

	/**
	 * 공장정보 삭제를 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 16
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "deleteListVdFatyInfo.do")
	public @ResponseBody ResultMap deleteListVdFatyInfo(@RequestBody Map param) {
		return spVendorInfoService.deleteListVdFatyInfo(param);
	}

	/**
	 * 설비정보 조회를 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 16
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVdFacInfo.do")
	public @ResponseBody List findListVdFacInfo(@RequestBody Map param) {
		return spVendorInfoService.findListVdFacInfo(param);
	}

	/**
	 * 설비정보 저장을 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 16
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveListVdFacInfo.do")
	public @ResponseBody ResultMap saveListVdFacInfo(@RequestBody Map param) {
		return spVendorInfoService.saveListVdFacInfo(param);
	}

	/**
	 * 설비정보 삭제를 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 16
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "deleteListVdFacInfo.do")
	public @ResponseBody ResultMap deleteListVdFacInfo(@RequestBody Map param) {
		return spVendorInfoService.deleteListVdFacInfo(param);
	}

	/**
	 * 협력사 재무정보 목록 그리드 조회
	 *
	 * @param param
	 * @Date : 2023. 6. 16
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVendorFinance.do")
	public @ResponseBody List findListVendorFinance(@RequestBody Map param) {
		return spVendorInfoService.findListVendorFinance(param);
	}

	/**
	 * 계좌정보 그리드를 조회한다. (자사정보)
	 *
	 * @param param the param
	 * @Date : 2023. 11. 29
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findVendorBankAccountInfo.do")
	public @ResponseBody List findVendorBankAccountInfo(@RequestBody Map param) {
		return spVendorMasterService.findVendorBankAccountInfo(param);
	}

	/**
	 * 계좌정보 그리드를 저장한다. (자사정보)
	 *
	 * @param param the param
	 * @Date : 2023. 11. 29
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "saveVendorBankAccountInfo.do")
	public @ResponseBody ResultMap saveVendorBankAccountInfo(@RequestBody Map param) {
		return spVendorMasterService.saveVendorBankAccountInfo(param);
	}

	/**
	 * 계좌정보 그리드를 삭제한다. (자사정보)
	 *
	 * @param param the param
	 * @Date : 2023. 11. 29
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "deleteVendorBankAccountInfo.do")
	public @ResponseBody ResultMap deleteVendorBankAccountInfo(@RequestBody Map param) {
		return spVendorMasterService.deleteVendorBankAccountInfo(param);
	}

	/**
	 * 재무정보 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListFinanceInfo.do")
	public @ResponseBody List findListFinanceInfo(@RequestBody Map param) {
		return spVendorInfoService.findListFinanceInfo(param);
	}

	/**
	 * 재무정보 그리드를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "saveListFinanceInfo.do")
	public @ResponseBody ResultMap saveListFinanceInfo(@RequestBody Map param) {
		return spVendorInfoService.saveListFinanceInfo(param);
	}

	/**
	 * 재무정보 그리드를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "deleteListFinanceInfo.do")
	public @ResponseBody ResultMap delteListFinanceInfo(@RequestBody Map param) {
		return spVendorInfoService.deleteListFinanceInfo(param);
	}

	/**
	 * 직원 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListLaborInfo.do")
	public @ResponseBody List findListLaborInfo(@RequestBody Map param) {
		return spVendorInfoService.findListLaborInfo(param);
	}

	/**
	 * 직원 그리드를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "saveListLaborInfo.do")
	public @ResponseBody ResultMap saveListLaborInfo(@RequestBody Map param) {
		return spVendorInfoService.saveListLaborInfo(param);
	}

	/**
	 * 직원 그리드를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "deleteListLaborInfo.do")
	public @ResponseBody ResultMap delteListLaborInfo(@RequestBody Map param) {
		return spVendorInfoService.deleteListLaborInfo(param);
	}

	/**
	 * 인증정보 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListCertInfo.do")
	public @ResponseBody List findListCertInfo(@RequestBody Map param) {
		return spVendorInfoService.findListCertInfo(param);
	}

	/**
	 * 인증정보를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "saveListCertInfo.do")
	public @ResponseBody ResultMap saveListCertInfo(@RequestBody Map param) {
		return spVendorInfoService.saveListCertInfo(param);
	}

	/**
	 * 인증정보를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "deleteListCertInfo.do")
	public @ResponseBody ResultMap delteListCertInfo(@RequestBody Map param) {
		return spVendorInfoService.deleteListCertInfo(param);
	}

	/**
	 * 주요정보 변경관리 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : findListVdInfoChangeReq
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVendorInfoChangeRequest.do")
	public @ResponseBody List findListVendorInfoChangeRequest(@RequestBody Map param) {
		return spVendorMasterService.findListVendorInfoChangeRequest(param);
	}

	/**
	 * 협력사 정보 변경 요청 가능 여부를 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : checkChangeRequestYn
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "checkChangeRequestYn.do")
	public @ResponseBody ResultMap checkChangeRequestYn(@RequestBody Map param) {
		return spVendorMasterService.checkChangeRequestYn(param);
	}

	/**
	 * 협력사 정보 변경 요청을 상세 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : findVendorInfoChangeRequestInfo
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findVendorInfoChangeRequestInfo.do")
	public @ResponseBody ResultMap findVendorInfoChangeRequestInfo(@RequestBody Map param) {
		return spVendorMasterService.findVendorInfoChangeRequestInfo(param);
	}

	/**
	 * 협력사 정보 변경 요청을 임시저장한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : saveVendorChangeRequestInfo
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveVendorChangeRequestInfo.do")
	public @ResponseBody ResultMap saveVendorChangeRequestInfo(@RequestBody Map param) {
		return spVendorMasterService.saveVendorChangeRequestInfo(param);
	}


	/**
	 * 협력사 정보 변경 요청을 저장한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : sendVendorChangeRequestInfo
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "sendVendorChangeRequestInfo.do")
	public @ResponseBody ResultMap sendVendorChangeRequestInfo(@RequestBody Map param) {
		return spVendorMasterService.sendVendorChangeRequestInfo(param);
	}


	/**
	 * 협력사 정보 변경 요청을 재요청한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : copyVendorChangeRequestInfo
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "copyVendorChangeRequestInfo.do")
	public @ResponseBody ResultMap copyVendorChangeRequestInfo(@RequestBody Map param) {
		return spVendorMasterService.copyVendorChangeRequestInfo(param);
	}

	/**
	 * 협력사 정보 변경 요청을 삭제한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : deleteVendorChangeRequestInfo
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "deleteVendorChangeRequestInfo.do")
	public @ResponseBody ResultMap deleteVendorChangeRequestInfo(@RequestBody Map param) {
		return spVendorMasterService.deleteVendorChangeRequestInfo(param);
	}

	/**
	 * 정규 상태인 협력사 운영조직을 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : findPoPossOorgCdByVd
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findPoPossOorgCdByVd.do")
	public @ResponseBody List findPoPossOorgCdByVd(@RequestBody Map param) {
		return spVendorMasterService.findPoPossOorgCdByVd(param);
	}
	/**
	 * 운영조직 별 협력사 계좌 변경 요청 정보를 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : findVendorBankInfoChangeRequestInfo
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findVendorBankInfoChangeRequestInfo.do")
	public @ResponseBody ResultMap findVendorBankInfoChangeRequestInfo(@RequestBody Map param) {
		return spVendorMasterService.findVendorBankInfoChangeRequestInfo(param);
	}

	/**
	 * 협력사 평가 정보 리스트를 조회한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 29
	 * @Method Name : findVendorEvalInfoList
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="findVendorEvalInfoList.do")
	public @ResponseBody ResultMap findVendorEvalInfoList(@RequestBody Map param){
		return spVendorInfoService.findVendorEvalInfoList(param);
	}
}
