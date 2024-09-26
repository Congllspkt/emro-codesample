package smartsuite.app.bp.vendorMaster.vendorInfo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.vendorMaster.vendorInfo.service.VendorInfoService;
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
@RequestMapping(value = "**/bp/vendorMaster/vendorInfo/**/")
public class VendorInfoController {

    @Inject
    VendorMasterService vendorMasterService;
	@Inject
	VendorInfoService vendorInfoService;

	/**
     * 운영단위별 운영조직 조회.
	 * @param param
	 * @Date : 2023. 07. 17
	 * @author yjPark
     */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping (value = "findListOorgCdAll.do")
    public @ResponseBody List findListOorgCdAll(@RequestBody String param) {
        return vendorMasterService.findListOorgCdAll(param);
    }

	/**
	 * 목록 조회. (findListVdInfo)
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping(value = "findListVendorProfile.do")
    public @ResponseBody FloaterStream findListVendorProfile(@RequestBody Map param) {
    	// 대용량 처리
        return vendorMasterService.findListVendorProfile(param);
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
		return vendorMasterService.findTotalVdInfo(param);
	}

	/**
	 * 기본정보 / 운영조직정보 조회 (findBasicInfo)
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findBasicVendorInfo.do")
	public @ResponseBody Map<String, Object> findBasicVendorInfo(@RequestBody Map param) {
		return vendorMasterService.findBasicVendorInfo(param);
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
		return vendorMasterService.saveBasicVendorInfo(param);
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
		return vendorMasterService.saveBasicAttachmentInfo(param);
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
	 * 운영조직 탭 운영조직정보 & 협력사관리유형 조회
	 *
	 * @param param
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findVdOorgAndVmtVmgInfo.do")
	public @ResponseBody Map<String, Object> findVdOorgAndVmtVmgInfo(@RequestBody Map param) {
		return vendorMasterService.findVdOorgAndVmtVmgInfo(param);
	}

	/**
	 * 운영조직 탭 - 협력사 운영조직 정보 & 미등록/등록 SG 목록 조회
	 *
	 * @param param 필수 {vd_cd, oorg_cd}, 옵션 {mod_seqno, chg_typ_ccd}
	 * @return Map
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "findVendorOorgInfo.do")
	public @ResponseBody Map<String, Object> findVendorOorgInfo(@RequestBody Map<String, Object> param) {
		return vendorMasterService.findVendorOorgInfo(param);
	}

	/**
	 * 운영조직 탭 - 미등록/등록 SG 목록 조회
	 *
	 * @param param {vd_cd, oorg_cd}
	 * @return Map {unregisteredSgList, registeredSgList}
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "findListSgByVendorOorg.do")
	public @ResponseBody Map<String, Object> findListSgByVendorOorg(@RequestBody Map<String, Object> param) {
		return vendorMasterService.findListSgByVendorOorg(param);
	}

	/**
	 * 운영조직 탭 - 등록 SG 목록 조회
	 *
	 * @param param {vd_cd, oorg_cd}
	 * @return List
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "findListRegisteredSg.do")
	public @ResponseBody List<Map<String, Object>> findListRegisteredSg(@RequestBody Map<String, Object> param) {
		return vendorMasterService.findListRegisteredSg(param);
	}

	/**
	 * 운영조직 탭 운영조직 협력사 담당자 정보 저장
	 *
	 * @param param the param
	 * @Date : 2023. 07. 09
	 * @return the map
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveVendorOperationOrganizationPicInfo.do")
	public @ResponseBody ResultMap saveVendorOperationOrganizationPicInfo(@RequestBody Map param) {
		return vendorMasterService.saveVendorOperationOrganizationPicInfo(param);
	}

	/**
	 * 운영조직 - rfx 추천 대상 여부 저장
	 *
	 * @param param the param
	 * @return the map
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveVendorOperationOrganizationVmgRcmdYn.do")
	public @ResponseBody ResultMap saveVendorOperationOrganizationVmgRcmdYn(@RequestBody Map param) {
		return vendorMasterService.saveVendorOperationOrganizationVmgRcmdYn(param);
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
	 * 임시저장, 결재 전 저장 (발주 협력사 등록 요청 / 협력사관리그룹 등록 요청)
	 *
	 * @param param the param
	 * @Date : 2023. 06. 16
	 * @return the map
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "saveVendorInfoBeforeRegistrationAprv.do")
	public @ResponseBody ResultMap saveVendorInfoBeforeRegistrationAprv(@RequestBody Map param) {
		return vendorMasterService.saveVendorInfoBeforeRegistrationAprv(param);
	}

	/**
	 * 임시저장, 결재 전 저장 (협력사 주요정보 변경요청 / 협력사관리그룹 등록 취소 요청 / 협력사 발주 제한 요청)
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 14
	 * @Method Name : saveVendorInfoBeforeAprv
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
    @RequestMapping(value = "saveVendorInfoBeforeAprv.do")
    public @ResponseBody ResultMap saveVendorInfoBeforeAprv(@RequestBody Map param) {
        return vendorMasterService.saveVendorInfoBeforeAprv(param);
    }

	/**
	 * 품의 Bypass: 확정처리 및 이력관리 (발주 협력사 등록 요청 / 협력사관리그룹 등록 요청)
	 *
	 * @param param the param
	 * @Date : 2023. 06. 16
	 * @return the map
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "saveBypassVendorRegistration.do")
	public @ResponseBody ResultMap saveBypassVendorRegistration(@RequestBody Map param){
		return vendorMasterService.saveBypassVendorRegistration(param);
	}

	/**
	 * 품의 Bypass: 확정처리 및 이력관리 (협력사관리그룹 등록 취소 요청)
	 *
	 * @param param the param
	 * @Date : 2023. 06. 16
	 * @return the map
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "saveBypassVendorVmgRegistrationCancel.do")
	public @ResponseBody ResultMap saveBypassVendorVmgRegistrationCancel(@RequestBody Map param){
		return vendorMasterService.saveBypassVendorVmgRegistrationCancel(param);
	}

	/**
	 * 품의 Bypass: 확정처리 및 이력관리 (발주 제한 요청)
	 *
	 * @param param the param
	 * @Date : 2023. 06. 16
	 * @return the map
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "saveBypassVendorPoLimit.do")
	public @ResponseBody ResultMap saveBypassVendorPoLimit(@RequestBody Map param){
		return vendorMasterService.saveBypassVendorPoLimit(param);
	}

	/**
	 * 품의 Bypass: 확정처리 및 이력관리 (주요 정보 변경 요청)
	 *
	 * @param param the param
	 * @Date : 2023. 06. 16
	 * @return the map
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "saveBypassVendorInfoModify.do")
	public @ResponseBody ResultMap saveBypassVendorInfoModify(@RequestBody Map param){
		return vendorMasterService.saveBypassVendorInfoModify(param);
	}

	/**
	 * 협력사 변경이력 및 결재 삭제
	 *
	 * @param param the param
	 * @Date : 2023. 06. 16
	 * @return the map
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "deleteVendorHistory.do")
	public @ResponseBody ResultMap deleteVendorHistory(@RequestBody Map param){
		return vendorMasterService.deleteVendorHistory(param);
	}

//	/**
//	 * 품의 완료 후 기본거래계약 호출
//	 *
//	 * @param param the param
//	 * @Date : 2024. 03. 04
//	 * @return the map
//	 */
//	@AuthCheck (authCode = Const.SAVE)
//	@RequestMapping(value = "saveVendorOnboardingContract.do")
//	public @ResponseBody ResultMap saveVendorOnboardingContract(@RequestBody Map param){
//		return vendorMasterService.saveVendorOnboardingContract(param);
//	}

	/**
	 * 공장정보 조회를 요청한다. (findListPlantInfo)
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVdFatyInfo.do")
	public @ResponseBody List findListVdFatyInfo(@RequestBody Map param) {
		return vendorInfoService.findListVdFatyInfo(param);
	}

	/**
	 * 공장정보 저장을 요청한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveListVdFatyInfo.do")
	public @ResponseBody ResultMap saveListVdFatyInfo(@RequestBody Map param) {
		return vendorInfoService.saveListVdFatyInfo(param);
	}

	/**
	 * 공장정보 삭제를 요청한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "deleteListVdFatyInfo.do")
	public @ResponseBody ResultMap deleteListVdFatyInfo(@RequestBody Map param) {
		return vendorInfoService.deleteListVdFatyInfo(param);
	}

	/**
	 * 설비정보 조회를 요청한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVdFacInfo.do")
	public @ResponseBody List findListVdFacInfo(@RequestBody Map param) {
		return vendorInfoService.findListVdFacInfo(param);
	}

	/**
	 * 설비정보 저장을 요청한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveListVdFacInfo.do")
	public @ResponseBody ResultMap saveListVdFacInfo(@RequestBody Map param) {
		return vendorInfoService.saveListVdFacInfo(param);
	}

	/**
	 * 설비정보 삭제를 요청한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "deleteListVdFacInfo.do")
	public @ResponseBody ResultMap deleteListVdFacInfo(@RequestBody Map param) {
		return vendorInfoService.deleteListVdFacInfo(param);
	}

	/**
	 * 협력사 초청메일 목록 조회
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findVdInviteMailList.do")
	public @ResponseBody List findVdInviteMailList(@RequestBody Map param) {
		return vendorInfoService.findVdInviteMailList(param);
	}
	/**
	 * 중복 체크
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	@RequestMapping(value = "checkDuplicatedVdInfo.do")
	public @ResponseBody List checkDuplicatedVdInfo(@RequestBody Map param) {
		return vendorInfoService.checkDuplicatedVdInfo(param);
	}
	/**
	 * 협력사 초청메일 정보 template 조회
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findMailTemplate.do")
	public @ResponseBody ResultMap findMailTemplate(@RequestBody Map param) {
		return vendorInfoService.findMailTemplate(param);
	}
	/**
	 * 협력사 초청메일 정보 조회
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findVdInviteMailInfo.do")
	public @ResponseBody ResultMap findVdInviteMailInfo(@RequestBody Map param) {
		return vendorInfoService.findVdInviteMailInfo(param);
	}

	/**
	 * 협력사 초청메일 정보 저장
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "sendVendorInviteMail.do")
	public @ResponseBody ResultMap sendVendorInviteMail(@RequestBody Map param) {
		return vendorInfoService.sendVendorInviteMail(param);
	}

	/**
	 * 협력사 초청메일 정보 저장
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "sendVendorInviteMailMulti.do")
	public @ResponseBody ResultMap sendVendorInviteMailMulti(@RequestBody Map param) {
		return vendorInfoService.sendVendorInviteMailMulti(param);
	}

	/**
	 * 협력사 재무정보 목록 그리드 조회
	 *
	 * @param param
	 * @Date : 2023. 6. 14
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVendorFinance.do")
	public @ResponseBody List findListVendorFinance(@RequestBody Map param) {
		return vendorInfoService.findListVendorFinance(param);
	}


	/**
	 * 인증서 리스트 조회를 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 14
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListCertManager.do")
	public @ResponseBody List findListCertManager(@RequestBody Map param) {
		return vendorInfoService.findListCertManager(param);
	}

	/**
	 * 공장정보, 설비정보를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 14
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVdFatyFac.do")
	public @ResponseBody List findListVdFatyFac(@RequestBody Map param) {
		return vendorInfoService.findListVdFatyFac(param);
	}


	/**
	 * 재무정보 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListFinanceInfo.do")
	public @ResponseBody List findListFinanceInfo(@RequestBody Map param) {
		return vendorInfoService.findListFinanceInfo(param);
	}

	/**
	 * 재무정보 그리드를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveListFinanceInfo.do")
	public @ResponseBody ResultMap saveListFinanceInfo(@RequestBody Map param) {
		return vendorInfoService.saveListFinanceInfo(param);
	}

	/**
	 * 재무정보 그리드를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "deleteListFinanceInfo.do")
	public @ResponseBody ResultMap delteListFinanceInfo(@RequestBody Map param) {
		return vendorInfoService.deleteListFinanceInfo(param);
	}

	/**
	 * 직원 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListLaborInfo.do")
	public @ResponseBody List findListLaborInfo(@RequestBody Map param) {
		return vendorInfoService.findListLaborInfo(param);
	}

	/**
	 * 직원 그리드를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveListLaborInfo.do")
	public @ResponseBody ResultMap saveListLaborInfo(@RequestBody Map param) {
		return vendorInfoService.saveListLaborInfo(param);
	}

	/**
	 * 직원 그리드를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "deleteListLaborInfo.do")
	public @ResponseBody ResultMap delteListLaborInfo(@RequestBody Map param) {
		return vendorInfoService.deleteListLaborInfo(param);
	}

	/**
	 * 인증정보 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListCertInfo.do")
	public @ResponseBody List findListCertInfo(@RequestBody Map param) {
		return vendorInfoService.findListCertInfo(param);
	}

	/**
	 * 인증정보를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveListCertInfo.do")
	public @ResponseBody ResultMap saveListCertInfo(@RequestBody Map param) {
		return vendorInfoService.saveListCertInfo(param);
	}

	/**
	 * 인증정보를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "deleteListCertInfo.do")
	public @ResponseBody ResultMap delteListCertInfo(@RequestBody Map param) {
		return vendorInfoService.deleteListCertInfo(param);
	}

	/**
	 * 협력사관리그룹 현황을 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 26
	 * @author cyhwang
	 */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping(value = "findListVendorManagementGroupCursitu.do")
    public @ResponseBody FloaterStream findListVendorManagementGroupCursitu(@RequestBody Map param) {
        // 대용량 처리
        return vendorMasterService.findListVendorManagementGroupCursitu(param);
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
        return vendorMasterService.saveListReqOnboardingEval(param);
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
	 * 진행중인 협력사 결재 정보를 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 12
	 * @Method Name : findVendorApprovalInfo
	 */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping(value = "findVendorApprovalInfo.do")
    public @ResponseBody Map findVendorApprovalInfo(@RequestBody Map param) {
		return vendorMasterService.findVendorApprovalInfo(param);
    }

	/**
     * 협력사 변경 상태 조회 (findVendorModifyStatus)
     *
     * @author : yjpark
	 * @param param the param
	 * @return the vdHistrecInfo
	 * @Date : 2023. 7. 14
	 * @Method Name : findVendorModifyStatus
     */
    @AuthCheck(authCode = Const.READ)
    @RequestMapping(value = "findVendorModifyStatus.do")
    public @ResponseBody Map findVendorModifyStatus(@RequestBody Map param){
    	return vendorMasterService.findVendorModifyStatus(param);
    }

	/**
	 * 협력사 등록/등록취소 요청 대상정보를 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 07
	 * @Method Name : findVendorTargVmgInfo
	 */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping(value = "findVendorTargVmgInfo.do")
    public @ResponseBody List findVendorTargVmgInfo(@RequestBody Map param) {
		return vendorMasterService.findVendorTargVmgInfo(param);
    }

	/**
	 * 협력사 이력정보를 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 12
	 * @Method Name : findVendorHistrecInfo
	 */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping(value = "findVendorHistrecInfo.do")
    public @ResponseBody Map findVendorHistrecInfo(@RequestBody Map param) {
		return vendorMasterService.findVendorHistrecInfo(param);
    }

	/**
	 * 협력사 정보갱신을 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 29
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
    @RequestMapping(value = "infoRenewRequest.do")
    public @ResponseBody ResultMap infoRenewRequest(@RequestBody Map param){
    	return vendorInfoService.infoRenewRequest(param);
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
		return vendorInfoService.findVendorEvalInfoList(param);
	}

	/**
	 * 정보변경요청 접수 목록 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.21
	 * @Method Name : findListVendorReqMainInfoChangeReceipt
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVendorReqMainInfoChangeReceipt.do")
	public @ResponseBody List findListVendorReqMainInfoChangeReceipt(@RequestBody Map param) {
		return vendorMasterService.findListVendorReqMainInfoChangeReceipt(param);
	}

	/**
	 * 정보변경요청 반려를 요청한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.21
	 * @Method Name : rejectVendorReqMainInfoChange
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "rejectVendorReqMainInfoChange.do")
	public @ResponseBody ResultMap rejectVendorReqMainInfoChange(@RequestBody Map param) {
		return vendorMasterService.rejectVendorReqMainInfoChange(param);
	}

	/**
	 * 정보변경요청 접수를 요청한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.24
	 * @Method Name : saveReceiptVendorReqMainInfoChange
	 */
	@AuthCheck (authCode = Const.VENDOR_SAVE)
	@RequestMapping(value = "saveReceiptVendorReqMainInfoChange.do")
	public @ResponseBody ResultMap saveReceiptVendorReqMainInfoChange(@RequestBody Map param) {
		return vendorMasterService.saveReceiptVendorReqMainInfoChange(param);
	}

	/**
	 * 협력사 정보 변경 요청을 상세 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : findVendorReqMainInfoChange
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findVendorReqMainInfoChange.do")
	public @ResponseBody ResultMap findVendorReqMainInfoChange(@RequestBody Map param) {
		return vendorMasterService.findVendorReqMainInfoChange(param);
	}

	/**
	 * 협력사 결재 이력을 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.20
	 * @Method Name : findListVendorApprovalHistory
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListVendorApprovalHistory.do")
	public @ResponseBody FloaterStream findListVendorApprovalHistory(@RequestBody Map param) {
		// 대용량 처리
		return vendorMasterService.findListVendorApprovalHistory(param);
	}

	/**
	 * 협력사 기본정보 이력을 조회한다. (기본정보만 조회)
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2024.03.22
	 * @Method Name : findBasicVendorHistrecInfo
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findBasicVendorHistrecInfo.do")
	public @ResponseBody Map findBasicVendorHistrecInfo(@RequestBody Map param) {
		return vendorMasterService.findBasicVendorHistrecInfo(param);
	}

	/**
	 * 협력사 발주 제한 요청 (미결재 변경 유형)
	 *
	 * @param param the param
	 * @Date : 2023. 08. 23
	 * @return the map
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "limitPoFromVendorNotAprv.do")
	public @ResponseBody ResultMap limitPoFromVendorNotAprv(@RequestBody Map param) {
		return vendorMasterService.limitPoFromVendorNotAprv(param);
	}

	/**
	 * 업체 마스터 일괄 데이터 업로드
	 *
	 * @param param the param
	 * @Date : 2024. 07. 23
	 * @return the map
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "uploadVdMasterList.do")
	public @ResponseBody ResultMap uploadVdMasterList(@RequestBody Map param) {
		return vendorMasterService.uploadVdMasterList(param);
	}
}