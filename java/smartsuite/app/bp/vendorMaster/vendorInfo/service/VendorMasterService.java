package smartsuite.app.bp.vendorMaster.vendorInfo.service;

import com.google.common.collect.Lists;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalService;
//import smartsuite.app.bp.contract.cntrreq.service.ContractReqService;
//import smartsuite.app.bp.contract.common.service.ContractService;
//import smartsuite.app.bp.contract.data.ContractReq;
import smartsuite.app.bp.vendorMaster.vendorInfo.event.VendorInfoEventPublisher;
import smartsuite.app.bp.vendorMaster.vendorInfo.repository.VendorMasterRepository;
import smartsuite.app.bp.vendorMaster.vendorReg.service.VendorRegService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;

import smartsuite.app.bp.admin.auth.service.RoleService;
import smartsuite.module.ModuleManager;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class VendorMasterService {

	private static final Logger LOG = LoggerFactory.getLogger(VendorMasterService.class);

	@Inject
	private RoleService roleService;

	@Inject
	private ApprovalService approvalService;

	@Inject
	private VendorMasterRepository vendorMasterRepository;

	@Inject
	private VendorInfoService vendorInfoService;

	@Inject
	private VendorInfoEventPublisher vendorInfoEventPublisher;

	@Inject
	private transient SharedService sharedService;

	@Inject
	VendorRegService vendorRegService;

//	@Inject
//	private ContractReqService contractReqService;
//
//	@Inject
//	private ContractService contractService;

    private final SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

	/**
	 * 운영단위별 운영조직 조회.
	 *
	 * @param param
	 * @Date : 2023. 07. 17
	 * @author yjPark
	 */
	public List<Map<String, Object>> findListOorgCdAll(String param) {
        return vendorMasterRepository.findListOorgCdAll(param);
    }

	/**
	 * 목록 조회. (findListVdInfo)
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    public FloaterStream findListVendorProfile(Map<String, Object> param) {
	    return vendorMasterRepository.findListVendorProfile(param);
    }

	/**
	 * 통합정보 조회 (findTotalInfo)
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	public Map findTotalVdInfo(Map<String, Object> param) {
		Map<String, Object> resultMap  = new HashMap<String, Object>();

		// 기본정보 조회 (대표자정보, 회사일반현황)
		resultMap.put("basicInfo", vendorMasterRepository.findBasicVendorInfo(param));

		// 신용정보 조회 (신용정보)
		resultMap.put("financeList", vendorInfoService.findListFinanceInfo(param));

		// 운영조직정보 조회 (운영조직등록현황)
		resultMap.put("vdOorgList", vendorMasterRepository.findVendorOperationOrganizationInfo(param));

		// 소싱그룹 발주/입고 금액 정보 조회 (5. 소싱그룹별 구매액)
		resultMap.put("sgPurcAmtList", vendorMasterRepository.findListVendorPoGrAmtBySG(param));
		// rfx정보 조회 (6.RFX정보)
		resultMap.put("rfxInfoList", vendorMasterRepository.findListYearlyVendorRfxInfo(param));

		// 정기평가 정보 조회 (7.평가분석)
		List<String> evalTaskTypCcdList = new ArrayList<>();
		evalTaskTypCcdList.add("PE");
		param.put("eval_task_typ_ccd", evalTaskTypCcdList);
		ResultMap evalInfo = vendorInfoService.findVendorEvalInfoList(param);
		resultMap.put("evalResultList", evalInfo.getResultData().get("pfmcEvalList"));

		// 인증정보 조회 (8.인증정보)
		resultMap.put("certInfoList", vendorInfoService.findListCertInfo(param));

		return resultMap;
	}

	/**
	 * 진행중인 협력사 결재 정보 조회
	 *
	 * @param param
	 * @Date : 2023. 07. 12
	 * @author yjPark
	 */
	public Map<String, Object> findVendorApprovalInfo(Map<String, Object> param) {
		Map<String, Object> resultMap  = new HashMap<String, Object>();

		Map<String, Object> aprvRockInfo = new HashMap<String, Object>();
		List<Map<String, Object>> aprvRockList = vendorMasterRepository.findVendorApprovalRockYn(param);
		if(aprvRockList.size() == 0){
			aprvRockInfo.put("rock_yn", "N");
		}else{
			aprvRockInfo = aprvRockList.get(0);  // 일반적으로, OORG_ROCK 발생 가능성 X / 동일 유형 ROCK 발생 가능성 X
		}

		Map<String, Object> aprvInfo = vendorMasterRepository.findProgressingVendorApprovalInfo(param);
		param.put("view", true);
		Map<String, Object> modStsInfo  = this.findVendorModifyStatus(param);

		resultMap.put("aprvRockInfo", aprvRockInfo);
		resultMap.put("aprvInfo", aprvInfo);
		resultMap.put("modStsInfo", modStsInfo);
		return resultMap;
	}

	/**
	 * 기본정보 / 운영조직정보 조회 (findBasicInfo)
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	public Map<String, Object> findBasicVendorInfo(Map<String, Object> param) {
		Map<String, Object> resultMap  = new HashMap<String, Object>();

		Map<String, Object> basicInfo  = vendorMasterRepository.findBasicVendorInfo(param);
		Map<String, Object> aprvInfo = vendorMasterRepository.findProgressingVendorApprovalInfo(param);
		Map<String, Object> vdOorgInfo  = vendorMasterRepository.findVendorOperationOrganizationInfo(param);
		Map<String, Object> modStsInfo  = this.findVendorModifyStatus(param);
		List<Map<String, Object>> athfList = vendorMasterRepository.findVendorAttachmentList(param);
		List<Map<String, Object>> bnkAcctList = vendorMasterRepository.findVendorBankAccountInfo(param);

		//VDOG에 값이 없을 경우 목록에서 넘어온 값으로 셋팅
		if(vdOorgInfo == null) vdOorgInfo = param;

		resultMap.put("basicInfo", basicInfo);
		resultMap.put("vdOorgInfo", vdOorgInfo);
		resultMap.put("modStsInfo", modStsInfo);
		resultMap.put("athfList", athfList);
		resultMap.put("aprvInfo", aprvInfo);
		resultMap.put("bnkAcctList", bnkAcctList); // 계좌정보

		return resultMap;
	}

	/**
	 * 기본정보 저장, 수정 (saveListBasicInfo)
	 *
	 * @param param
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	public ResultMap saveBasicVendorInfo(Map<String, Object> param){
		Map<String, Object> vdOorgInfo = (Map<String, Object>) param.get("vdOorgInfo");
		Map<String, Object> basicInfo = (Map<String, Object>) param.get("basicInfo");

		List<Map<String, Object>> athfList = (List<Map<String, Object>>)param.get("athfList");

		//vd_oorg 체크
		if(vdOorgInfo.get("vd_oorg_uuid") == null || "".equals(vdOorgInfo.get("vd_oorg_uuid"))) {
			String vdOorgUuid = UUID.randomUUID().toString();
			vdOorgInfo.put("vd_oorg_uuid", vdOorgUuid);
			// insert
			vendorMasterRepository.insertVendorOperationOrganizationInfo(vdOorgInfo);
		} else {
			// update
			vendorMasterRepository.updateVendorOperationOrganizationInfo(vdOorgInfo);
		}

		//vd update
		vendorMasterRepository.updateBasicVendorInfo(basicInfo);

		// 첨부파일 저장
		this.saveVendorAttachmentInfo(athfList);

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 기본정보 첨부파일 저장, 수정 (saveVendorAttachmentInfo)
	 *
	 * @param param
	 * @Date : 2023. 07. 28
	 * @author yjpark
	 */
	public ResultMap saveVendorAttachmentInfo(List<Map<String, Object>> param) {
		if(param != null && !param.isEmpty()){
			for (Map<String, Object> athfInfo : param){
				if(athfInfo.get("vd_athf_uuid") == null || "".equals(athfInfo.get("vd_athf_uuid"))){
					String vdAthfUuid = UUID.randomUUID().toString();
					athfInfo.put("vd_athf_uuid", vdAthfUuid);
					athfInfo.put("use_yn", "Y");
					vendorMasterRepository.insertVendorAttachmentInfo(athfInfo);
				} else {
					athfInfo.put("sts", "U");
					vendorMasterRepository.updateVendorAttachmentInfo(athfInfo);
				}
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 기본정보 첨부파일 저장, 수정 (saveBasicAttachInfo)
	 *
	 * @param param
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	public ResultMap saveBasicAttachmentInfo(Map<String, Object> param) {
		String vdAthfUuid = (String)param.get("vd_athf_uuid");
		if(!vdAthfUuid.isEmpty()) {
			param.put("sts", "U");
			vendorMasterRepository.updateVendorAttachmentInfo(param);
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 운영조직 탭 운영조직정보 & 협력사관리유형 조회
	 *
	 * @param param(vd_cd, oorg_cd)
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	public Map<String, Object> findVdOorgInfo(Map<String, Object> param) {
		Map resultMap = Maps.newHashMap();

		Map<String, Object> vdOorgInfo = vendorMasterRepository.findVendorOperationOrganizationInfo(param);
		Map<String, Object> modStsInfo  = this.findVendorModifyStatus(param);
		if (vdOorgInfo == null) {
			vdOorgInfo = param;
			// 협력사 운영조직 정보가 없는 경우는 신규업체로 간주함
			vdOorgInfo.put("vd_oorg_uuid", null);
			vdOorgInfo.put("obd_typ_ccd", "NEW");
			vdOorgInfo.put("po_poss_yn", "N");
		}

		resultMap.put("vdOorgInfo", vdOorgInfo); // 협력사 운영조직정보
		resultMap.put("modStsInfo", modStsInfo);
		return resultMap;
	}

	/**
	 * 사용대상 협력사관리유형 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the list using vendor mgmt type
	 * @Date : 2023. 5. 31
	 * @Method Name : findListVmtUsing
	 */
	public List<Map<String, Object>> findListVmtUsing(Map<String, Object> param) {
		// 대용량 처리
		return vendorMasterRepository.findListVmtUsing(param);
	}

	/**
	 * 운영조직 탭 운영조직정보 & 협력사관리유형 & 협력사관리그룹 조회
	 *
	 * @param param(vd_cd, oorg_cd)
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	public Map<String, Object> findVdOorgAndVmtVmgInfo(Map<String, Object> param) {
		Map<String, Object> resultMap = this.findVdOorgInfo(param);

		if(MapUtils.isNotEmpty(vendorInfoEventPublisher.findVendorVMTVMGInfo(param))) {
			resultMap.putAll(vendorInfoEventPublisher.findVendorVMTVMGInfo(param));
		}

		return resultMap;
	}

	/**
	 * 협력사 운영조직 정보 & 미등록/등록 SG 목록 조회
	 *
	 * @param param 필수 {vd_cd, oorg_cd}, 옵션 {mod_seqno, chg_typ_ccd}
	 * @return Map
	 */
	public Map<String, Object> findVendorOorgInfo(Map<String, Object> param) {
		Map<String, Object> resultMap = this.findVdOorgInfo(param);
		resultMap.putAll(this.findListSgByVendorOorg(param));
		return resultMap;
	}

	/**
	 * 미등록/등록 SG 목록 조회
	 *
	 * @param param {vd_cd, oorg_cd}
	 * @return Map {unregisteredSgList, registeredSgList}
	 */
	public Map<String, Object> findListSgByVendorOorg(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();

		if(ModuleManager.exist("VS")) {
			resultMap.put("unregisteredSgList", vendorInfoEventPublisher.findListUnregisteredSgByOeg(param));
			resultMap.put("registeredSgList", this.findListRegisteredSg(param));
		}

		return resultMap;
	}

	/**
	 * 등록 SG 목록 조회
	 *
	 * @param param {vd_cd, oorg_cd}
	 * @return List
	 */
	public List<Map<String, Object>> findListRegisteredSg(Map<String, Object> param) {
		List<Map<String, Object>> resultList = Lists.newArrayList();

		if(ModuleManager.exist("VS")) {
			resultList.addAll(vendorInfoEventPublisher.findListRegisteredSg(param));
		}

		return resultList;
	}

	/**
	 * 운영조직 탭 운영조직 협력사 담당자 정보 저장
	 *
	 * @param param
	 * @Date : 2023. 07. 09
	 * @author sykim
	 */
	public ResultMap saveVendorOperationOrganizationPicInfo(Map<String, Object> param) {
		Map<String, Object> vdOorgInfo = (Map<String, Object>) param.get("vdOorgInfo");
		if (vdOorgInfo == null || !vdOorgInfo.containsKey("oorg_cd")) {
			return ResultMap.FAIL();
		} else {
			// 협력사 운영조직 정보(VD_OORG)가 존재하지 않는 경우 insert
			String vdOorgUuid = (String) vdOorgInfo.get("vd_oorg_uuid");	// 협력사 운영조직 정보 아이디
			if(vdOorgUuid == null || "".equals(vdOorgUuid)) {
				vdOorgUuid = UUID.randomUUID().toString();
				vdOorgInfo.put("vd_oorg_uuid", vdOorgUuid);
				vendorMasterRepository.insertVendorOperationOrganizationInfo(vdOorgInfo);
			} else {
				vendorMasterRepository.updateVendorOperationOrganizationPicInfo(vdOorgInfo); //담당자 정보만 수정
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 운영조직 - rfx 추천 대상 여부를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2019. 01. 23
	 * @author sykim
	 */
	public ResultMap saveVendorOperationOrganizationVmgRcmdYn(Map<String, Object> param) {
		List<Map<String, Object>> updates = (List<Map<String, Object>>) param.get("updateList");

		if(updates != null && !updates.isEmpty()) {
			for (Map<String,Object> row : updates) {
				vendorMasterRepository.saveVendorOperationOrganizationVmgRcmdYn(row);
			}
			return ResultMap.SUCCESS();
		} else {
			return ResultMap.FAIL();
		}
	}

	/**
	 * 추가 거래가능 협력사관리그룹 조회 (findListUnregisteredVendorManagementGroup)
	 *
	 * @param param
	 * @Date : 2023. 06. 22
	 * @author yjPark
	 */
	public FloaterStream findListUnregisteredVendorManagementGroup(Map<String, Object> param) {
		return vendorMasterRepository.findListUnregisteredVendorManagementGroup(param);
	}

	/**
	 * 협력사 변경 상태 조회 (특정 품의에 대한)
	 *
	 * @param param
	 * @Date : 2023. 06. 16
	 * @author sykim
	 */
	public Map<String, Object> findVendorModifyStatus(Map<String, Object> param) {
		if(!param.containsKey("view") || !(Boolean)param.get("view")){
			Integer rev = vendorMasterRepository.findVdHistrecModSeq(param);
			param.put("mod_seqno", rev == null ? 0 : rev.intValue());
		}
		return vendorMasterRepository.findVendorModifyStatus(param);
	}

	/**
	 * 협력사 변경 전 저장
	 *
	 * @param param
	 * @Date : 2023. 06. 16
	 * @author sykim
	 */
	public ResultMap saveVendorInfoHistoryBeforeModify(Map<String, Object> param, String chgTypCcd){
		Map resultMap = Maps.newHashMap();

		Map<String, Object> vdOorgInfo = (Map<String, Object>) param.get("vdOorgInfo");
		Map<String, Object> basicInfo = (Map<String, Object>) param.get("basicInfo");
		List<Map<String, Object>> athfList = (List<Map<String, Object>>) param.get("athfList");       // 첨부파일
		List<Map<String, Object>> bnkAcctList = (List<Map<String, Object>>) param.get("bnkAcctList"); // 계좌정보
		Map<String, Object> modStsInfo = (Map<String, Object>) param.get("modStsInfo");

		if(basicInfo == null || vdOorgInfo == null){
			return ResultMap.FAIL(resultMap);
		}

		// 1.협력사 운영조직 정보(VD_OORG)가 존재하지 않는 경우 insert
		String vdOorgUuid = (String) vdOorgInfo.get("vd_oorg_uuid");	// 협력사 운영조직 정보 아이디
		if(vdOorgUuid == null || "".equals(vdOorgUuid)) {
			vdOorgUuid = (String)UUID.randomUUID().toString();
			vdOorgInfo.put("vd_oorg_uuid", vdOorgUuid);
			vendorMasterRepository.insertVendorOperationOrganizationInfo(vdOorgInfo);
		}

		// 2.운영조직 협력사관리그룹 협력사 매핑(VD_OORG_VMG) 데이터가 존재하지 않는 경우
		List<Map<String, Object>> regTargVmgList = (List<Map<String, Object>>)param.get("regTargVmgList");
		if(regTargVmgList != null && !regTargVmgList.isEmpty()){
			for(Map<String, Object> row : regTargVmgList){
				String vdOorgVmgUuid = (String) row.get("vd_oorg_vmg_uuid");
				if (vdOorgVmgUuid == null || "".equals(vdOorgVmgUuid)) {
					vdOorgVmgUuid = (String)UUID.randomUUID().toString();

					// 등록 요청한 협력사관리유형이 협력사관리그룹에 매핑되어 있는 경우 INSERT
					if (row.get("vmg_oorg_uuid") != null && !"".equals(row.get("vmg_oorg_uuid"))) {
						row.put("vd_oorg_vmg_uuid", vdOorgVmgUuid);
						row.put("vd_oorg_uuid", vdOorgUuid);
						vendorMasterRepository.insertVdOorgVmg(row);
					}
				}
			}
		}

		// 협력사 변경 이력 정보 저장
		String vdMstChgHistrecUuid = ""; // 협력사 변경이력 아이디
		String chgReqStsCcd = null; // 변경 요청 상태
		Integer modSeqno = null; // 변경이력 일련번호

		if (modStsInfo == null || modStsInfo.isEmpty() || modStsInfo.get("vd_mst_chg_histrec_uuid") == null || "".equals(modStsInfo.get("vd_mst_chg_histrec_uuid"))) {
			// 신규 이력정보 INSERT
			modSeqno = vendorMasterRepository.findVdHistrecModSeq(basicInfo);
			modSeqno = modSeqno == null ? 1 : modSeqno.intValue() + 1;
			chgReqStsCcd = "CRNG";

			// 협력사 변경이력 아이디 채번
			vdMstChgHistrecUuid = (String) UUID.randomUUID().toString();
			String vdOorgChgHistrecUuid = (String) UUID.randomUUID().toString();

			basicInfo.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);
			basicInfo.put("mod_seqno", modSeqno);
			basicInfo.put("chg_req_sts_ccd", chgReqStsCcd); // 임시저장
			basicInfo.put("chg_typ_ccd", chgTypCcd); // 변경유형코드

			vdOorgInfo.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);
			vdOorgInfo.put("vd_oorg_chg_histrec_uuid", vdOorgChgHistrecUuid);

			vendorMasterRepository.insertVdInfoHistory(basicInfo);
			vendorMasterRepository.insertVdOorgInfoHistory(vdOorgInfo);

			// 운영조직 협력사 협력사관리그룹 변경이력에 관리그룹 정보 insert
			if(regTargVmgList != null && !regTargVmgList.isEmpty()) {
				for (Map<String, Object> row : regTargVmgList) {
					String apvlSubjYn = row.get("apvl_subj_yn") == null ? "" : row.get("apvl_subj_yn").toString();
					if ("Y".equals(apvlSubjYn)) {
						String vdOorgVmgChgHistrecUuid = (String) UUID.randomUUID().toString();
						row.put("vd_oorg_vmg_chg_histrec_uuid", vdOorgVmgChgHistrecUuid);
						row.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);
						if("VMG_REG_CNCL_REQ".equals(chgTypCcd)){  // 협력사관리그룹 등록 취소 요청
							row.put("supavl_yn", "N");
						}else{
							row.put("supavl_yn", "Y");
						}
						vendorMasterRepository.insertVdOorgVmgInfoHistory(row);
					}
				}
			}
		} else {
			vdMstChgHistrecUuid = (String) modStsInfo.get("vd_mst_chg_histrec_uuid");
			chgReqStsCcd = (String) modStsInfo.get("chg_req_sts_ccd");

			if ("CRNG".equals(chgReqStsCcd) || "RET".equals(chgReqStsCcd)){
				//변경이력 정보가 존재하고 작성중(CRNG)/반려(RET) 상태일 때 업데이트
				if(modStsInfo.get("mod_seqno") instanceof BigDecimal) {
					modSeqno = ((BigDecimal) modStsInfo.get("mod_seqno")).intValue();
				} else {
					modSeqno = (Integer) modStsInfo.get("mod_seqno");
				}

				basicInfo.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);
				basicInfo.put("mod_seqno", modSeqno);
				basicInfo.put("chg_req_sts_ccd", "CRNG");
				basicInfo.put("chg_typ_ccd", chgTypCcd);
				vdOorgInfo.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);
				vdOorgInfo.put("mod_seqno", modSeqno);

				vendorMasterRepository.updateVdInfoHistory(basicInfo);
				vendorMasterRepository.updateVdOorgInfoHistory(vdOorgInfo);

				// 운영조직 협력사 협력사관리그룹 변경이력에 관리그룹 정보 update
				if(regTargVmgList != null && !regTargVmgList.isEmpty()) {
					for (Map<String, Object> row : regTargVmgList) {
						String vdOorgVmgChgHistrecUuid = row.get("vd_oorg_vmg_chg_histrec_uuid") == null ? "" : row.get("vd_oorg_vmg_chg_histrec_uuid").toString();
						String apvlSubjYn = row.get("apvl_subj_yn") == null ? "" : row.get("apvl_subj_yn").toString();
						if (!"".equals(vdOorgVmgChgHistrecUuid) && "N".equals(apvlSubjYn)) {
							vendorMasterRepository.deleteVdOorgVmgInfoHistory(row);
						} else if ("".equals(vdOorgVmgChgHistrecUuid) && "Y".equals(apvlSubjYn)) {
							vdOorgVmgChgHistrecUuid = (String) UUID.randomUUID().toString();
							row.put("vd_oorg_vmg_chg_histrec_uuid", vdOorgVmgChgHistrecUuid);
							row.put("vd_oorg_vmg_chg_histrec_uuid", vdOorgVmgChgHistrecUuid);
							if("VMG_REG_CNCL_REQ".equals(chgTypCcd)){  // 협력사관리그룹 등록 취소 요청
								row.put("supavl_yn", "N");
							}else{
								row.put("supavl_yn", "Y");
							}
							vendorMasterRepository.insertVdOorgVmgInfoHistory(row);
						}
					}
				}
			}
		}

		// 첨부파일
		if(athfList != null && !athfList.isEmpty()){
			for (Map<String, Object> athfInfo : athfList){
				if(athfInfo.get("vd_athf_chg_histrec_uuid") == null || "".equals(athfInfo.get("vd_athf_chg_histrec_uuid"))){
					athfInfo.put("vd_athf_chg_histrec_uuid", UUID.randomUUID().toString());
					athfInfo.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);
					athfInfo.put("use_yn", "Y");
					vendorMasterRepository.insertVendorAttachmentHistrecInfo(athfInfo);
				} else {
					athfInfo.put("sts", "U");
					vendorMasterRepository.updateVendorAttachmentHistrecInfo(athfInfo);
				}
			}
		}

		// 계좌정보
		if(bnkAcctList != null && !bnkAcctList.isEmpty()){
			// 기존 작성된 이력 delete 후 insert
			vendorMasterRepository.deleteVendorBankAccountHistrecInfo(basicInfo);

			for (Map<String, Object> bnkAcctInfo : bnkAcctList){
				bnkAcctInfo.put("vd_bnkacct_chg_hist_uuid", UUID.randomUUID().toString()); // 협력사 계좌정보 이력 UUID
				bnkAcctInfo.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);
				vendorMasterRepository.insertVendorBankAccountHistrecInfo(bnkAcctInfo);
			}
		}

		resultMap.put("chg_typ_ccd", chgTypCcd);
		resultMap.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);
		resultMap.put("vd_oorg_uuid", vdOorgUuid);
		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 결재 승인 상태 업데이트 (발주 협력사 등록 요청 / 협력사관리그룹 등록 요청)
	 *
	 * @param param
	 * @Date : 2023. 06. 19
	 * @author sykim
	 */
	public void updateVendorRegistrationInfoByApprovedApproval(Map<String, Object> param) {
		// 협력사 변경이력 결재승인정보 update
		param.put("chg_req_sts_ccd", "APVD");  // 협력사 변경 요청 결재 상태
		param.put("supavl_yn", "Y");  // 공급 가능 여부

		// 1. 협력사 운영조직 협력사관리그룹 변경이력 결재 승인 update
		vendorMasterRepository.updateVdOorgVmgHistoryByApproval(param);

		String chgTypCcd = param.get("chg_typ_ccd").toString();
		if("PO_POSS_VD_REG_REQ".equals(chgTypCcd)){  // 변경구분이 '발주 협력사 등록 요청' 인 경우
			// 2. 협력사 운영조직 변경이력 결재 승인 update
			param.put("po_poss_yn", "Y");  // 발주 가능 여부
			vendorMasterRepository.updateVdOorgHistoryByRegApproval(param);

			// 2-1. 기본거래계약 대상 협력사일 경우 계약요청 생성
			this.saveVendorOnboardingContractRequest(param);
		}

		// 3. 협력사 마스터 변경이력 결재 승인 정보 update
		vendorMasterRepository.updateVdHistoryByApproval(param);

		// 평가 결과, '발주 협력사 등록 요청'/'협력사관리그룹 등록 요청' 인 경우,
		// 4. 온보딩평가 요청 처리완료
		vendorInfoEventPublisher.saveOnboardingEvalPrcsgEnd(param);

		// 5. 이력정보를 기준으로 원본 update
		this.updateVendorRegistrationStatusByHistory(param);
	}

	/**
	 * 협력사 등록요청 결재 (승인 제외) 상태 업데이트
	 *
	 * @param param
	 * @Date : 2023. 06. 28
	 * @author sykim
	 */
	public void updateVendorHistoryByApproval(Map<String, Object> param) {
		// 협력사 마스터 변경이력 결재 상태 update
		vendorMasterRepository.updateVdHistoryByApproval(param);
	}

	/**
	 * 결재 승인 상태 업데이트 (협력사관리그룹 등록 취소 요청)
	 *
	 * @param param
	 * @Date : 2023. 06. 29
	 * @author sykim
	 */
	public void updateVendorVmgRegistrationCancelByApprovedApproval(Map<String, Object> param) {
		// 협력사 변경이력 결재승인정보 update
		param.put("chg_req_sts_ccd", "APVD");  // 협력사 변경 요청 결재 상태
		param.put("supavl_yn", "N");  // 공급 가능 여부

		// 1. 협력사 운영조직 협력사관리그룹 변경이력 등록 종료 update
		vendorMasterRepository.updateVdOorgVmgHistoryByApproval(param);
		// 2. 협력사 운영조직 변경이력 등록 종료 update
		// 협력사 운영조직에 해당하는 모든 관리그룹에 대하여 등록이 종료된 경우 운영조직의 발주가능여부도 등록종료로 변경
		//vendorMasterRepository.updateVdOorgHistoryByStopApproval(param);
		// 3. 협력사 마스터 이력 등록 종료 결재 승인 정보 update
		vendorMasterRepository.updateVdHistoryByApproval(param);

		// 4. 이력정보를 기준으로 원본 update
		this.updateVendorRegistrationStatusByHistory(param);
	}

	/**
	 * 결재 승인 상태 업데이트 (주요 정보 변경 요청)
	 *
	 * @param param
	 * @Date : 2023. 07. 27
	 * @author yjpark
	 */
	public void updateVendorInfoModifyByApprovedApproval(Map<String, Object> param) {
		// 협력사 변경이력 결재승인정보 update

		// 1. 협력사 마스터 이력 등록 종료 결재 승인 정보 update
		param.put("chg_req_sts_ccd", "APVD");  // 협력사 변경 요청 결재 상태
		vendorMasterRepository.updateVdHistoryByApproval(param);
		// 2. 협력사 요청 주요정보변경요청 존재하는 경우, 승인 정보 update
		vendorMasterRepository.updateChgReqStsCcdAboutVdInfoChg(param);

		// 3. 이력정보를 기준으로 원본 update
		this.updateVendorRegistrationStatusByHistory(param);

		// 4. 기본거래계약 대상 협력사일 경우 계약요청 생성
		this.saveVendorOnboardingContractRequest(param);
	}

	/**
	 * 협력사 변경 이력 결재 매핑정보 삭제
	 *
	 * @param param
	 * @Date : 2023. 06. 19
	 * @author sykim
	 */
	public void deleteVendorModifyApprovalInfo(Map<String, Object> param){
		// 결재 정보 삭제 오류 무시하고 업무 진행
		try {
			Map<String, Object> aprvInfo = Maps.newHashMap();
			Map<String, Object> vdHistrecApvlParam = vendorMasterRepository.findVdHistrecApprovalInfoForDelete(param);

			if(vdHistrecApvlParam != null && !vdHistrecApvlParam.isEmpty()){
				aprvInfo.put("deleteApproval", vdHistrecApvlParam);
				approvalService.deleteApproval(aprvInfo);
				aprvInfo.remove("deleteApproval");
			}
		}catch(Exception e){
			if(LOG.isErrorEnabled()){
				LOG.error(e.getMessage());
			}
		}
	}

	/**
	 * 협력사 이력으로 협력사 정보 업데이트
	 *
	 * @param param
	 * @Date : 2023. 06. 19
	 * @author sykim
	 */
	public void updateVendorRegistrationStatusByHistory(Map<String, Object> param){
		String chgTypCcd = param.get("chg_typ_ccd").toString();
		// 0. 품의 속성(=정보 변경 대상 여부) 확인
		param.put("ccd", "E027");
		param.put("dtlcd", chgTypCcd);
		param.put("cstr_cnd_cd", "INFO_CHG_TARG_YN");
		String infoChgTargYn = sharedService.getCommonCodeConstraintConditionValue(param);
		infoChgTargYn = infoChgTargYn == null ? "N" : infoChgTargYn.replace("_FIX", "");

		// 1. 협력사 변경이력 정보 조회
		Map<String, Object> vdInfoHistory = vendorMasterRepository.findBasicVendorHistrecInfo(param);
		Map<String, Object> vdOorgInfoHistory = vendorMasterRepository.findVendorOperationOrganizationHistrecInfo(param);
		List<Map<String, Object>> orgVdVmgInfoHistoryList = vendorMasterRepository.findVendorOperationOrganizationVmgInfoHistory(param);
		param.put("vd_cd", vdInfoHistory.get("vd_cd"));
		List<Map<String, Object>> athfList = vendorMasterRepository.findAttachmentListVendorHistrecInfo(param);
		List<Map<String, Object>> bnkAcctList = vendorMasterRepository.findVendorBankAccountHistrecList(param);

		String erpVdCd = (String)vdInfoHistory.get("erp_vd_cd");
		String vdCd = (String)vdInfoHistory.get("vd_cd");

		// 2. ERP_VD_CD 채번
		if(erpVdCd == null || "".equals(erpVdCd)) {
			vdInfoHistory.put("erp_vd_cd", findErpVdCode(vdCd));
		}

		// 3. 거래등록승인요청 시 거래 적용 대상 온보딩 유형인 경우 정규 협력사 Role 부여, 
		// 4. 최초등록일자 저장
		String obdTypTrdApplyYn = vdOorgInfoHistory.get("obd_typ_trd_apply_yn") == null ? "N" : vdOorgInfoHistory.get("obd_typ_trd_apply_yn").toString();
		if ("Y".equals(vdOorgInfoHistory.get("po_poss_yn")) && "Y".equals(obdTypTrdApplyYn)) {
			Map roleParam = Maps.newHashMap();
			roleParam.put("role_cd", "V500");
			roleParam.put("usr_id", vdCd);
			roleParam.put("vd_oorg_uuid", vdOorgInfoHistory.get("vd_oorg_uuid"));
			int cnt = vendorMasterRepository.findVendorAuthOfficial(roleParam);
			if (cnt == 0) {
				roleService.insertRoleUser(roleParam); //insertRegularVendorAuth
			}
			vdOorgInfoHistory.put("oorg_ini_transn_st_dt", fm.format(new Date()));
		}

		// 5. 운영조직 협력사 협력사관리그룹(VD_OORG_VMG) 결재정보 update
		for(Map<String, Object> orgVdVmgInfoHistory : orgVdVmgInfoHistoryList){
			orgVdVmgInfoHistory.put("vd_oorg_uuid", vdOorgInfoHistory.get("vd_oorg_uuid"));
			if (param.containsKey("supavl_st_dt") && param.get("supavl_st_dt") != null && !"".equals(param.get("supavl_st_dt"))) {
				orgVdVmgInfoHistory.put("supavl_st_dt", param.get("supavl_st_dt")); // 공급가능 시작일 = 등록요청 결재 승인일
			}
			vendorMasterRepository.updateVdOorgVmgInfo(orgVdVmgInfoHistory);
		}

		// 6. 정보 변경 대상(= 품의 속성)인 경우만, 정보 이력 현행화
		if("Y".equals(infoChgTargYn)){
			// 6-1. 협력사 계좌정보 결재정보 update
			if (bnkAcctList != null && !bnkAcctList.isEmpty()) {
				vendorMasterRepository.deleteVendorBankAccountInfo(param); // 기존 계좌정보 delete -> histrec 데이터를 insert

				for (Map<String, Object> bnkAcctInfo : bnkAcctList){
					bnkAcctInfo.put("vd_bnkacct_uuid", UUID.randomUUID().toString()); // 협력사 계좌정보 이력 UUID
					vendorMasterRepository.insertVendorBankAccountInfo(bnkAcctInfo);
				}
			}

			// 6-1. 협력사 첨부파일 결재정보 update
			this.saveVendorAttachmentInfo(athfList);

			// 6-1. 협력사 운영조직 결재정보 update (변경 정보)
			vendorMasterRepository.updateVdOorgInfoChg(vdOorgInfoHistory);
			// 6-1. 협력사 마스터 결재정보 update (변경정보)
			vendorMasterRepository.updateVdInfoChg(vdInfoHistory);
		}
		// 6. 정보 변경 대상(= 품의 속성)이 아닌 경우, 정보 현행 이력에 업데이트
		else{
			Map<String, Object> mergeParam = new HashMap<String, Object>();
			mergeParam.put("vd_mst_chg_histrec_uuid", vdInfoHistory.get("vd_mst_chg_histrec_uuid"));
			mergeParam.put("vd_cd", vdInfoHistory.get("vd_cd"));
			mergeParam.put("oorg_cd", vdInfoHistory.get("oorg_cd"));

			// 6-2. 협력사 계좌정보 현행 > 이력 update
			vendorMasterRepository.mergeVendorBankAccountInfoFromCurrentToHistory(mergeParam);

			// 6-2. 협력사 첨부파일 현행 > 이력 update
 			vendorMasterRepository.mergeVendorAttachmentInfoFromCurrentToHistory(mergeParam);

			// 6-2. 협력사 운영조직 현행 > 이력 update
			vendorMasterRepository.updateVdOorgInfoChgFromCurrentToHistory(mergeParam);
			// 6-2. 협력사 마스터 현행 > 이력 update
			vendorMasterRepository.updateVdInfoChgFromCurrentToHistory(mergeParam);
		}

		// 7. ERP_VD_CD 신규 채번된 경우
		if(erpVdCd == null || "".equals(erpVdCd)) {
			vendorMasterRepository.updateVdPoPossInfo(vdInfoHistory);
		}

		// 8. 발주 상태 변경 품의(= 변경유형)인 경우만, 발주 상태 이력 현행화
		// PO_POSS_VD_REG_REQ : 발주 협력사 등록 요청
		// PO_LMT_REQ : 발주 제한 요청
		// VMG_REG_CNCL_REQ : 협력사관리그룹 등록 취소 요청
		if("PO_POSS_VD_REG_REQ".equals(chgTypCcd) || "PO_LMT_REQ".equals(chgTypCcd) /*|| "VMG_REG_CNCL_REQ".equals(chgTypCcd)*/){
			// 협력사 운영조직 결재정보 update
			vendorMasterRepository.updateVdOorgPoPossInfo(vdOorgInfoHistory);
		}
		// 8. 발주 상태 변경 품의(= 변경유형)이 아닌 경우, 발주 상태 현행 이력에 업데이트
		else{
			Map<String, Object> mergeParam = new HashMap<String, Object>();
			mergeParam.put("vd_mst_chg_histrec_uuid", vdInfoHistory.get("vd_mst_chg_histrec_uuid"));
			mergeParam.put("vd_cd", vdInfoHistory.get("vd_cd"));
			mergeParam.put("oorg_cd", vdInfoHistory.get("oorg_cd"));

			// 협력사 운영조직 현행 > 이력 update
			vendorMasterRepository.updateVdOorgPoPossInfoFromCurrentToHistory(mergeParam);
		}
	}

	/**
	 * 임시저장, 결재 전 저장 (발주 협력사 등록 요청 / 협력사관리그룹 등록 요청)
	 *
	 * @param param
	 * @Date : 2023. 06. 16
	 * @author sykim
	 */
	public ResultMap saveVendorInfoBeforeRegistrationAprv(Map<String, Object> param){
		// 1. 임시저장, 결재 전 저장 (발주 협력사 등록 요청 / 협력사관리그룹 등록 요청)
		ResultMap resultMap = this.saveVendorInfoBeforeAprv(param);
		if(!resultMap.isSuccess()) {
			return resultMap;
		}

	    Map<String, Object> basicInfo = (Map<String, Object>) param.get("basicInfo");
	    List<Map<String, Object>> lists = (List<Map<String, Object>>)param.get("regTargVmgList");

	    List<String> uniqueGrpIds = new ArrayList<String>();
		Map<String, Object> resultnfo = resultMap.getResultData();
		String vdMstChgHistrecUuid = (String) resultnfo.get("vd_mst_chg_histrec_uuid");
	    for(Map<String, Object> row : lists){
			// 등록대상 온보딩평가
	        // 2. 중목을 제외한 온보딩평가 아이디 기준으로, 대상 온보딩평가그룹에 협력사 마스터 변경이력 UUID update
	        String oeUuid = (String) row.get("oe_uuid");
	        if(!uniqueGrpIds.contains(oeUuid)){
				row.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);
	            vendorInfoEventPublisher.saveOnboardingEvalVendorAprvInfo(row);
	            uniqueGrpIds.add(vdMstChgHistrecUuid);
	        }

			// 등록대상 협력사관리그룹
	        // 3. 등록대상 협력사관리그룹에 결재대상여부 변경
			if(!"Y".equals(row.get("apvl_subj_yn"))) {
	            row.put("apvl_subj_yn", "N");
	        }
	        vendorInfoEventPublisher.saveOnboardingEvalVmgAprvSubj(row);
	    }

		return resultMap;
	}

	/**
	 * 임시저장, 결재 전 저장 (발주 협력사 등록 요청 / 협력사관리그룹 등록 요청 / 협력사 주요정보 변경요청 / 협력사관리그룹 등록 취소 요청 / 협력사 발주 제한 요청)
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 14
	 * @Method Name : saveVendorInfoBeforeAprv
	 */
    public ResultMap saveVendorInfoBeforeAprv(Map<String, Object> param){
        String chgTypCcd = param.get("chgTypCcd").toString();
		return this.saveVendorInfoHistoryBeforeModify(param, chgTypCcd);
    }

	/**
	 * 품의 Bypass: 확정처리 및 이력관리 (발주 협력사 등록 요청 / 협력사관리그룹 등록 요청)
	 *
	 * @param param
	 * @Date : 2023. 06. 16
	 * @author sykim
	 */
	public ResultMap saveBypassVendorRegistration(Map param) {
		// 1. 임시저장, 결재 전 저장 (발주 협력사 등록 요청 / 협력사관리그룹 등록 요청)
		ResultMap resultMap = this.saveVendorInfoBeforeRegistrationAprv(param);

		if(resultMap.isSuccess()) {
			// 2. 결재 매핑 정보 삭제
			this.deleteVendorModifyApprovalInfo((Map)param.get("modStsInfo"));
			// 3. 결재 승인 상태 업데이트 (발주 협력사 등록 요청 / 협력사관리그룹 등록 요청)
			this.updateVendorRegistrationInfoByApprovedApproval(resultMap.getResultData());

			return ResultMap.SUCCESS();
		} else {
			return resultMap;
		}
	}

	/**
	 * 품의 Bypass: 확정처리 및 이력관리 (협력사관리그룹 등록 취소 요청)
	 *
	 * @param param
	 * @Date : 2023. 06. 29
	 * @author sykim
	 */
	public ResultMap saveBypassVendorVmgRegistrationCancel(Map param) {
		// 1. 임시저장, 결재 전 저장 (협력사관리그룹 등록 취소 요청)
		ResultMap resultMap = this.saveVendorInfoBeforeAprv(param);

		if(resultMap.isSuccess()) {
			// 2. 결재 매핑 정보 삭제
			this.deleteVendorModifyApprovalInfo((Map) param.get("modStsInfo"));
			// 3. 결재 승인 상태 업데이트 (협력사관리그룹 등록 취소 요청)
			this.updateVendorVmgRegistrationCancelByApprovedApproval(resultMap.getResultData());

			return ResultMap.SUCCESS();
		} else {
			return resultMap;
		}
	}

	/**
	 * 품의 Bypass: 확정처리 및 이력관리 (발주 제한 요청)
	 *
	 * @param param
	 * @Date : 2023. 06. 29
	 * @author sykim
	 */
	public ResultMap saveBypassVendorPoLimit(Map param) {
		// 1. 임시저장, 결재 전 저장 (발주 제한 요청)
		ResultMap resultMap = this.saveVendorInfoBeforeAprv(param);

		if(resultMap.isSuccess()) {
			// 2. 결재 매핑 정보 삭제
			this.deleteVendorModifyApprovalInfo((Map) param.get("modStsInfo"));
			// 3. 결재 승인 상태 업데이트 (발주 제한 요청)
			this.updateVendorPoLimitByApprovedApproval(resultMap.getResultData());

			return ResultMap.SUCCESS();
		} else {
			return resultMap;
		}
	}

	/**
	 * 품의 Bypass: 확정처리 및 이력관리 (주요 정보 변경 요청)
	 *
	 * @param param
	 * @Date : 2023. 06. 29
	 * @author sykim
	 */
	public ResultMap saveBypassVendorInfoModify(Map param) {
		// 1. 임시저장, 결재 전 저장 (주요 정보 변경 요청)
		ResultMap resultMap = this.saveVendorInfoBeforeAprv(param);

		if(resultMap.isSuccess()) {
			// 2. 결재 매핑 정보 삭제
			this.deleteVendorModifyApprovalInfo((Map) param.get("modStsInfo"));
			// 3. 결재 승인 상태 업데이트 (주요 정보 변경 요청)
			this.updateVendorInfoModifyByApprovedApproval(resultMap.getResultData());

			return ResultMap.SUCCESS();
		} else {
			return resultMap;
		}
	}

	/**
	 * 협력사 변경이력 및 결재 삭제
	 *
	 * @param param
	 * @Date : 2023. 06. 28
	 * @author sykim
	 */
	public ResultMap deleteVendorHistory(Map param) {
		Map<String, Object> rejectItem = (Map<String, Object>) param.get("rejectItem");
		if(rejectItem != null && !rejectItem.isEmpty()) {
			param = this.findBasicVendorInfo(rejectItem);
		}

		Map<String, Object> vdOorgInfo = (Map<String, Object>) param.get("vdOorgInfo");
		Map<String, Object> basicInfo = (Map<String, Object>) param.get("basicInfo");
		Map<String, Object> modStsInfo = (Map<String,Object>) param.get("modStsInfo");

		// 변경 요청상태가 작성중, 반려 상태일 때 삭제 가능
		if(modStsInfo != null && !modStsInfo.isEmpty()
				&& ("CRNG".equals(modStsInfo.get("chg_req_sts_ccd")) || "RET".equals(modStsInfo.get("chg_req_sts_ccd")))) {
			Integer modSeqNo =  Integer.parseInt(modStsInfo.get("mod_seqno").toString());  // vendorMasterRepository.findVdHistrecModSeq(basicInfo);
			String vdMstChgHistrecUuid = (String) modStsInfo.get("vd_mst_chg_histrec_uuid");

			basicInfo.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);
			basicInfo.put("mod_seqno", modSeqNo);
			vdOorgInfo.put("vd_mst_chg_histrec_uuid", vdMstChgHistrecUuid);

			// 1. 협력사 정보변경이력 삭제
			vendorMasterRepository.deleteVendorBankAccountHistrecInfo(basicInfo);// 계좌정보
			vendorMasterRepository.deleteVendorAttachmentHistrecInfo(basicInfo); // 첨부파일
			vendorMasterRepository.deleteVdOorgVmgInfoHistory(vdOorgInfo);       // 운영조직 협력사 협력사관리그룹
			vendorMasterRepository.deleteVdOorgInfoHistory(vdOorgInfo);          // 운영조직 협력사
			vendorMasterRepository.deleteVdInfoHistory(basicInfo);               // 협력사

			// 2. 협력사 변경이력과 연결된 결재 정보 삭제
			this.deleteVendorModifyApprovalInfo(modStsInfo);

			// 3. 평가 결과, 발주 협력사 등록 요청인 경우,
			// 등록대상 협력사관리그룹에 결재대상 취소
			vendorInfoEventPublisher.saveCancelOnboardingEvalVmgAprvSubj(basicInfo);
			// 대상 온보딩평가그룹에 협력사 마스터 변경이력 UUID 초기화
			vendorInfoEventPublisher.saveCancelOnboardingEvalVendorAprvInfo(basicInfo);

			// 4. 협력사 주요정보변경요청인 경우 접수완료->변경요청 상태 update
			String vdInfoChgReqUuid = basicInfo.get("vd_info_chg_req_uuid") == null ? "" : basicInfo.get("vd_info_chg_req_uuid").toString();	// 외)협력사 주요정보변경 요청 아이디
			if(!"".equals(vdInfoChgReqUuid)) {
				basicInfo.put("chg_req_sts_ccd", "REQ");  // 변경요청
				vendorMasterRepository.updateChgReqStsCcdAboutVdInfoChg(basicInfo);
			}

			return ResultMap.SUCCESS();
		}

		return ResultMap.FAIL();
	}

	/**
	 * 기본거래계약 요청
	 *
	 * @param param
	 * @Date : 2024. 03. 04
	 * @author sykim
	 */
	public ResultMap saveVendorOnboardingContractRequest(Map param) {
		Map<String, Object> vdInfoHistory = vendorMasterRepository.findBasicVendorHistrecInfo(param);
		Map<String, Object> vdOorgInfoHistory = vendorMasterRepository.findVendorOperationOrganizationHistrecInfo(param);

		// 기본거래계약 대상 여부 확인
		String ctryCcd = (String) vdInfoHistory.get("ctry_ccd");
		String orgCtryCcd = (String) vdOorgInfoHistory.get("org_ctry_ccd");
		String cntrSubjYn = (String) vdOorgInfoHistory.get("cntr_subj_yn");

		if(ctryCcd == null || orgCtryCcd == null) {
			return ResultMap.NOT_EXISTS();
//		} else if ( "KR".equals(ctryCcd) && "KR".equals(orgCtryCcd) && (cntrSubjYn != null && "Y".equals(cntrSubjYn)) ) {
//			// 기본거래계약 대상 (운영조직, 협력사 모두 KR & 계약대상여부 Y)
//			if(vdOorgInfoHistory.get("cntr_req_rcpt_uuid") != null && !"".equals(vdOorgInfoHistory.get("cntr_req_rcpt_uuid"))) {
//				// 생성된 계약 요청 존재
//				return ResultMap.SUCCESS(vdOorgInfoHistory);
//			} else {
//				// 계약 요청 신규 생성
//				ContractReq data = ContractReq.ONBOARDING(
//						(String) param.get("vd_mst_chg_histrec_uuid")
//						,"Create Onboarding Contract Test : [" + vdInfoHistory.get("vd_cd") + "] " + vdInfoHistory.get("vd_nm")
//						, Auth.getCurrentUserName()
//						, (String) vdOorgInfoHistory.getOrDefault("cntr_pic_id", "")
//						, (String) vdInfoHistory.get("vd_cd")
//						, (String) vdOorgInfoHistory.get("oorg_cd")
//				); //TODO : 계약요청명(reqNm) 수정
//
//				ResultMap cntrResult = contractReqService.requestContract(data);
//				if (cntrResult.isSuccess()) {
//					Map<String, Object> reqInfo = cntrResult.getResultData();
//					vdOorgInfoHistory.put("cntr_req_rcpt_uuid", reqInfo.get("cntr_req_rcpt_uuid"));
//					vendorMasterRepository.updateVdOorgHistrecContractReqInfo(vdOorgInfoHistory);
//					return ResultMap.SUCCESS(vdOorgInfoHistory);
//				} else {
//					return cntrResult;
//				}
//			}
		} else {
			return ResultMap.SKIP();
		}
	}

	/**
	 * 기본거래계약 체결 정보 update
	 *
	 * @param param
	 * @Date : 2024. 03. 22
	 * @author sykim
	 */
	public ResultMap saveVendorOperationOrganizationContractInfo(Map param) {
		// 계약정보 조회 (param: vd_cd, cntr_uuid)
//		Map<String, Object> cntrInfo = contractService.findContract(param).getResultData();
//
//		// 계약정보 협력사 정보에 update
//		param.put("oorg_cd", cntrInfo.get("oorg_cd")); // 협력사 운영조직 코드
//		param.put("cntr_sgng_yn", "Y"); // 계약 체결 여부
//		param.put("cntr_no", cntrInfo.get("cntr_no"));
//		param.put("cntr_st_dt", cntrInfo.get("cntr_st_dt"));
//		param.put("cntr_exp_dt", cntrInfo.get("cntr_exp_dt"));
//
//		vendorMasterRepository.updateVdOorgContractInfo(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사 ERP Code 채번(getErpVendorCode)
	 *
	 * @param vdCd the param
	 * @Date : 2023. 06. 19
	 * @author sykim
	 */
	public static String findErpVdCode(String vdCd){
		// ERP_VD_CD 채번 Business Logic 전개 - SITE별 로직 추가
		// Package Default VA -> ERP
		String erpVdCd = vdCd.replaceAll("VA", "ERP");
		return erpVdCd;
	}

	/**
	 * 협력사관리그룹 현황을 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 26
	 * @author cyhwang
	 */
    public FloaterStream findListVendorManagementGroupCursitu(Map<String, Object> param) {
        // 대용량 처리
	    return vendorMasterRepository.findListVendorManagementGroupCursitu(param);
    }

	/**
	 * 온보딩평가요청 - 등록요청을 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 06. 30
	 * @author yjPark
	 */
	public ResultMap saveListReqOnboardingEval(Map<String, Object> param) {
        List<Map<String, Object>> lists = (List<Map<String, Object>>)param.get("checkList");

        if(lists == null || lists.isEmpty()){
        	return ResultMap.FAIL();
        }

        // VD_OORG에 체크
		Map<String, Object> pivotMap = (Map<String, Object>) lists.get(0);
        String vdOorgUuid = (String) pivotMap.get("vd_oorg_uuid");
        if(vdOorgUuid == null || "".equals(vdOorgUuid)) {
        	vdOorgUuid = (String)UUID.randomUUID().toString();
            pivotMap.put("vd_oorg_uuid", vdOorgUuid);
            vendorMasterRepository.insertVendorOperationOrganizationInfo(pivotMap);
        }

		ResultMap resultMap = vendorInfoEventPublisher.saveListReqOnboardingEval(param);  // 온보딩평가요청
		Map returnInfo = lists.get(0);
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			Map<String, Object> resultInfo = resultMap.getResultData();
			returnInfo.put("oe_uuid", resultInfo.get("oe_uuid"));
		}

        return ResultMap.SUCCESS(returnInfo);
    }

	/**
	 * 온보딩평가요청 - 등록취소를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 07. 11
	 * @author yjPark
	 */
	public ResultMap saveListReqOnboardingEvalCancel(Map<String, Object> param) {
		// 1. 온보딩평가 결재진행여부 조회
        Map<String, Object> aprvProgMap = vendorInfoEventPublisher.findReqedOnboardingAprvProgYn(param);
		String aprvProgYn = aprvProgMap.get("aprv_prog_yn") == null ? "" : aprvProgMap.get("aprv_prog_yn").toString();
    	if("Y".equals(aprvProgYn)) { // 협력사관리그룹 결재 진행 중
    		return ResultMap.INVALID();
    	}

    	// 2. 온보딩평가 품의 - 미등록완료 저장 호출
    	return vendorInfoEventPublisher.saveListOnboardingEvalUnRegComplete(param);
    }

	/**
	 * vendor 잠재 업체로 update
	 *
	 * @param param
	 * @Date : 2023. 07. 02
	 * @author sykim
	 */
	public void updateVdOorgObdTypCcdToPtnl(Map<String, Object> param) {
		// 협력사 마스터 변경이력 결재 상태 update
		vendorMasterRepository.updateVdOorgObdTypCcdToPtnl(param);
	}

	/**
     * 등록승인요청 시 Vendor 정보 Editable 여부를 체크한다.
     *
     * @author : yjPark
     * @param param the param
     * @return the map
     * @Date : 2023. 07. 06
     * @Method Name : findIsEditableRegReq
     */
	public ResultMap findIsEditableRegReq(Map<String, Object> param){
		// 1. 전 운영조직에 대한 등록정보 체크
    	Map<String, Object> editableInfo = vendorMasterRepository.findIsEditableRegReq(param);
		// 2. 현재 운영조직에 대한 등록정보 체크
    	Map<String, Object> operEditableInfo = vendorMasterRepository.findIsEditableRegReqByOorgCd(param);

    	Map<String, Object> resultInfo  = new HashMap<String, Object>();

    	resultInfo.put("editableInfo", editableInfo);
    	// 전 운영조직 통틀어 등록정보가 있지만 현재 운영조직의 등록정보가 없는 경우, mod로 표시
        if(editableInfo != null && editableInfo.containsKey("new_yn") && "N".equals((String)editableInfo.get("new_yn"))
        	&& operEditableInfo != null && operEditableInfo.containsKey("new_yn") && "Y".equals((String)operEditableInfo.get("new_yn"))){
        		resultInfo.remove("editableInfo");
        		operEditableInfo.put("regOorgExist", "Y");
        		resultInfo.put("editableInfo", operEditableInfo);
        }
    	return ResultMap.SUCCESS(resultInfo);
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
	public List findVendorTargVmgInfo(Map<String, Object> param) {
    	return vendorMasterRepository.findVendorTargVmgInfo(param);
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
	public Map findVendorHistrecInfo(Map<String, Object> param) {
    	Map<String, Object> resultMap  = new HashMap<String, Object>();

		Map<String, Object> basicInfo  = new HashMap<String, Object>();
		Map<String, Object> vdOorgInfo  = new HashMap<String, Object>();
		List<Map<String, Object>> athfList  = new ArrayList<Map<String, Object>>();
		Map<String, Object> nextBasicInfo  = new HashMap<String, Object>();
		Map<String, Object> nextVdOorgInfo  = new HashMap<String, Object>();
		List<Map<String, Object>> nextAthfList  = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bnkAcctList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> nextBnkAcctList = new ArrayList<Map<String, Object>>();

		Boolean view = param.get("view") == null ? false : (Boolean) param.get("view");
		if(view){  // 이력 조회 모드
			String chgTypCcd = param.get("chg_typ_ccd") == null ? "" : param.get("chg_typ_ccd").toString();

			// 1. nextInfo : 선택한 차수의 이력
			nextBasicInfo = vendorMasterRepository.findBasicVendorHistrecInfo(param);
			nextVdOorgInfo = vendorMasterRepository.findVendorOperationOrganizationHistrecInfo(param);
			nextAthfList = vendorMasterRepository.findAttachmentListVendorHistrecInfo(param);
			nextBnkAcctList = vendorMasterRepository.findVendorBankAccountHistrecList(param);

			// 2. 품의 속성(=정보 변경 대상 여부) 확인
			param.put("ccd", "E027");
			param.put("dtlcd", chgTypCcd);
			param.put("cstr_cnd_cd", "INFO_CHG_TARG_YN");
			String infoChgTargYn = sharedService.getCommonCodeConstraintConditionValue(param);
			infoChgTargYn = infoChgTargYn == null ? "N" : infoChgTargYn.replace("_FIX", "");

			// 3. 품의 속성(=정보 변경 대상 여부) : 정보 변경 대상 품의 인 경우 이전차수 이력 존재여부 확인, 나머지의 경우 선택한 차수의 이력만 조회
			if("Y".equals(infoChgTargYn)){
				String modSeqno = (nextBasicInfo == null || nextBasicInfo.get("mod_seqno") == null) ? "0" : nextBasicInfo.get("mod_seqno").toString();
				String oorgCd = (param.get("oorg_cd") == null) ? "" : param.get("oorg_cd").toString();
				nextBasicInfo.put("mod_seqno", modSeqno);
				nextBasicInfo.put("oorg_cd", oorgCd);

				// 3-1. (선택한 차수의 이전차수) 협력사 마스터 이력 존재여부 확인
				nextBasicInfo.put("IS_INFO_CHG_APRV", infoChgTargYn);  // 정보 변경 대상 품의 중에서 이전차수 이력 조회
				Map<String, Object> prevBasicInfo = vendorMasterRepository.findPrevHistrecForVendorInfoChange(nextBasicInfo);
				if(prevBasicInfo != null){  // 이력 존재
					// 3-2. 이전차수 협력사 마스터 이력 정보 조회
					basicInfo = vendorMasterRepository.findBasicVendorHistrecInfo(prevBasicInfo);
					athfList = vendorMasterRepository.findAttachmentListVendorHistrecInfo(prevBasicInfo);
					bnkAcctList = vendorMasterRepository.findVendorBankAccountHistrecList(prevBasicInfo);
				}

				// 4-1. (선택한 차수의 이전차수) 협력사 운영조직 이력 존재여부 확인
				Map<String, Object> prevOorgInfo = vendorMasterRepository.findPrevOorgHistrecForVendorInfoChange(nextBasicInfo);
				if(prevOorgInfo != null){  // 이력 존재
					// 4-2. 이전차수 협력사 운영조직 이력 정보 조회
					vdOorgInfo = vendorMasterRepository.findVendorOperationOrganizationHistrecInfo(prevOorgInfo);
				}
			}
		} else{    // 변경 진행 모드
			// 1. 현행 협력사정보 조회
			basicInfo  = vendorMasterRepository.findBasicVendorInfo(param);
			vdOorgInfo  = vendorMasterRepository.findVendorOperationOrganizationInfo(param);
			// VDOG에 값이 없을 경우 목록에서 넘어온 값으로 셋팅
			if(vdOorgInfo == null) vdOorgInfo = param;
			param.put("vd_histrec_yn", "Y"); // 협력사 변경이력 조회 flag
			athfList = vendorMasterRepository.findVendorAttachmentList(param);
			bnkAcctList = vendorMasterRepository.findVendorBankAccountInfo(param);

			String vdMstChgHistrecUuid = param.get("vd_mst_chg_histrec_uuid") == null ? "" : param.get("vd_mst_chg_histrec_uuid").toString();
			if(!"".equals(vdMstChgHistrecUuid)){
				// 이력 MOD 상태 (임시저장 이력 존재)
				// 2. next 이력 조회
				nextBasicInfo = vendorMasterRepository.findBasicVendorHistrecInfo(param);
				nextVdOorgInfo = vendorMasterRepository.findVendorOperationOrganizationHistrecInfo(param);
				nextBnkAcctList = vendorMasterRepository.findVendorBankAccountHistrecList(param);
			} else{
				// 이력 New 상태 (임시저장 이력 미존재)
				// 2. next 이력 셋팅
				nextBasicInfo = new HashMap<String, Object>(basicInfo);
				nextVdOorgInfo = new HashMap<String, Object>(vdOorgInfo);
				nextBnkAcctList = new ArrayList<Map<String, Object>>(bnkAcctList);
			}
			nextAthfList = vendorMasterRepository.findAttachmentListVendorHistrecInfo(param);
		}

		resultMap.put("basicInfo", basicInfo);
		resultMap.put("vdOorgInfo", vdOorgInfo);
		resultMap.put("athfList", athfList);
		resultMap.put("nextBasicInfo", nextBasicInfo);
		resultMap.put("nextVdOorgInfo", nextVdOorgInfo);
		resultMap.put("nextAthfList", nextAthfList);
		resultMap.put("bnkAcctList", bnkAcctList);
		resultMap.put("nextBnkAcctList", nextBnkAcctList);
		return resultMap;
	}

	/**
	 * 정보변경요청 접수 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2022.07.21
	 * @Method Name : findListVendorReqMainInfoChangeReceipt
	 */
	public List<Map<String, Object>> findListVendorReqMainInfoChangeReceipt(Map<String, Object> param) {
		return vendorMasterRepository.findListVendorReqMainInfoChangeReceipt(param);
	}

	/**
	 * 정보변경요청을 반려한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2022.07.21
	 * @Method Name : rejectVendorReqMainInfoChange
	 */
	public ResultMap rejectVendorReqMainInfoChange(Map<String,Object> param) {
		List<Map<String, Object>> rejectList = (List<Map<String, Object>>)param.get("rejectList");
		Map<String, Object> rejectInfo = (Map<String, Object>)param.get("rejectInfo");

		String chgReqRetRsn = rejectInfo.get("chg_req_ret_rsn") == null ? null : rejectInfo.get("chg_req_ret_rsn").toString();
		String buyerPicId = rejectInfo.get("buyer_pic_id") == null ? null : rejectInfo.get("buyer_pic_id").toString();
		for (Map<String, Object> row : rejectList) {
			row.put("chg_req_sts_ccd", "RET");
			row.put("chg_req_ret_rsn", chgReqRetRsn);
			row.put("buyer_pic_id", buyerPicId);

			// 1. 정보변경요청 반려정보 update
			vendorMasterRepository.updateVdInfoChgRejectInfo(row);
			// 2. 정보변경요청 변경요청상태 update
			vendorMasterRepository.updateChgReqStsCcdAboutVdInfoChg(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 정보변경요청을 접수한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2022.07.24
	 * @Method Name : saveReceiptVendorReqMainInfoChange
	 */
	public ResultMap saveReceiptVendorReqMainInfoChange(Map<String,Object> param) {
		// 1. 임시저장, 결재 전 저장 (협력사 주요정보 변경요청)
		ResultMap resultMap = this.saveVendorInfoBeforeAprv(param);
		if(!resultMap.isSuccess()) {
			return resultMap;
		}

		// 2. 정보변경요청 변경요청상태 update
		Map<String, Object> basicInfo = (Map<String, Object>) param.get("basicInfo");
		basicInfo.put("chg_req_sts_ccd", "RCPT");  // 접수
		vendorMasterRepository.updateChgReqStsCcdAboutVdInfoChg(basicInfo);

		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사 정보 변경 요청을 상세 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : findVendorReqMainInfoChange
	 */
	public ResultMap findVendorReqMainInfoChange(Map<String, Object> param) {
		Map<String, Object> resultData = new HashMap<String,Object>();
		Map<String, Object> basicInfo = new HashMap<String,Object>();
		Map<String, Object> vdOorgInfo = new HashMap<String,Object>();
		List<Map<String, Object>> athfList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> bnkAcctList = new ArrayList<Map<String, Object>>();

		// 1. 선택한 차수의 협력사 정보 변경 요청 정보 조회
		Map<String, Object> nextBasicInfo = vendorMasterRepository.findBasicVendorReqMainInfoChange(param);
		Map<String, Object> nextVdOorgInfo = vendorMasterRepository.findVendorOperationOrganizationReqMainInfoChange(param);
		List<Map<String, Object>> nextAthfList = vendorMasterRepository.findAttachmentListVendorReqMainInfoChange(param);
		List<Map<String, Object>> nextBnkAcctList = vendorMasterRepository.findBankAccountVendorReqMainInfoChange(param);

		// 2-1. 선택한 차수의 이전차수, 협력사 변경이력 존재여부 확인
		Map<String, Object> prevBasicInfo = vendorMasterRepository.findPrevHistrecForVendorReqMainInfoChange(nextBasicInfo);
		if(prevBasicInfo != null){  // 이력 존재
			// 2. 이전 협력사 정보 변경 요청 정보 조회
			basicInfo = vendorMasterRepository.findBasicVendorHistrecInfo(prevBasicInfo);
			athfList = vendorMasterRepository.findAttachmentListVendorHistrecInfo(prevBasicInfo);
			bnkAcctList = vendorMasterRepository.findVendorBankAccountHistrecList(prevBasicInfo);
		}
		// 3-1. 선택한 차수의 이전차수, 협력사 운영조직 변경이력 존재여부 확인
		Map<String, Object> prevOorgInfo = vendorMasterRepository.findPrevOorgHistrecForVendorReqMainInfoChange(nextBasicInfo);
		if(prevOorgInfo != null){  // 이력 존재
			// 3. 이전 협력사 정보 변경 요청 정보 조회
			vdOorgInfo = vendorMasterRepository.findVendorOperationOrganizationHistrecInfo(prevBasicInfo);
		}

		resultData.put("basicInfo", basicInfo);
		resultData.put("nextBasicInfo", nextBasicInfo);
		resultData.put("vdOorgInfo", vdOorgInfo);
		resultData.put("nextVdOorgInfo", nextVdOorgInfo);
		resultData.put("athfList", athfList);
		resultData.put("nextAthfList", nextAthfList);
		resultData.put("bnkAcctList", bnkAcctList);
		resultData.put("nextBnkAcctList", nextBnkAcctList);
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 협력사 결재 이력을 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 07. 20
	 * @author sykim
	 */
	public FloaterStream findListVendorApprovalHistory(Map<String, Object> param) {
		// 대용량 처리
		return vendorMasterRepository.findListVendorApprovalHistory(param);
	}

	/**
	 * 협력사 기본정보 이력을 조회한다. (기본정보만 조회)
	 *
	 * @param param the param
	 * @Date : 2024. 03. 22
	 * @author sykim
	 */
	public Map<String, Object> findBasicVendorHistrecInfo(Map<String, Object> param) {
		return vendorMasterRepository.findBasicVendorHistrecInfo(param);
	}

	/**
	 * 협력사 발주 제한 요청 (미결재 변경 유형)
	 *
	 * @param param
	 * @Date : 2023. 08. 23
	 * @author yjpark
	 */
	public ResultMap limitPoFromVendorNotAprv(Map<String, Object> param){
		Map<String, Object> vdOorgInfo = (Map<String, Object>) param.get("vdOorgInfo");

		// 1. 협력사 운영조직에 등록된 모든 협력사관리그룹 등록 종료 update
		vendorMasterRepository.updateVdOorgVmgInfo(vdOorgInfo);

		// 2. 협력사 운영조직 등록 종료 update
		// 협력사 운영조직에 해당하는 모든 관리그룹에 대하여 등록이 종료된 경우 운영조직의 발주가능여부도 등록종료로 변경
		vendorMasterRepository.updateVdOorgInfoByStop(vdOorgInfo);

		return ResultMap.SUCCESS();
	}

	/**
	 * 결재 승인 상태 업데이트 (발주 제한 요청)
	 *
	 * @param param
	 * @Date : 2023. 08. 23
	 * @author yjpark
	 */
	public void updateVendorPoLimitByApprovedApproval(Map<String, Object> param) {
		// 협력사 변경이력 결재승인정보 update
		param.put("chg_req_sts_ccd", "APVD");  // 협력사 변경 요청 결재 상태
		param.put("po_poss_yn", "N");  // 발주 가능 여부

		// 1. 협력사 운영조직 변경이력 결재 승인 update
		vendorMasterRepository.updateVdOorgHistoryByRegApproval(param);

		// 2. 협력사 마스터 변경이력 결재 승인 정보 update
		vendorMasterRepository.updateVdHistoryByApproval(param);

		// 3. 이력정보를 기준으로 원본 update
		this.updateVendorRegistrationStatusByHistory(param);

		// 4. 협력사 운영조직에 등록된 모든 협력사관리그룹 등록 종료 update
		Map<String, Object> vdOorgInfoHistory = vendorMasterRepository.findVendorOperationOrganizationHistrecInfo(param);
		vdOorgInfoHistory.put("supavl_yn", "N");  // 공급 가능 여부
		vendorMasterRepository.updateVdOorgVmgInfo(vdOorgInfoHistory);

		// 5. 협력사 이력 : '협력사관리그룹 등록 요청(= VMG_REG_REQ)' 품의가 '작성중(=CRNG)' 인 건 조회
		Map<String, Object> crngProcParam = new HashMap<String,Object>();
		crngProcParam.put("vd_cd", vdOorgInfoHistory.get("vd_cd"));
		crngProcParam.put("oorg_cd", vdOorgInfoHistory.get("oorg_cd"));
		crngProcParam.put("chg_typ_ccd", "VMG_REG_REQ");
		List<Map<String, Object>> crngProcTargetList = vendorMasterRepository.findListVdChgHistrecInCrngSts(crngProcParam);

		// 5-1. '협력사관리그룹 등록 요청(= VMG_REG_REQ)' 품의가 '작성중(=CRNG)' 인 건 이력 삭제
		for(Map<String, Object> crngProcTarget : crngProcTargetList){
			Map<String, Object> deleteTarget = this.findVendorHistrecInfo(crngProcTarget);
			// 협력사 변경 상태 조회 (특정 품의에 대한)
			crngProcTarget.put("view", true);
			deleteTarget.put("modStsInfo", crngProcTarget);

			this.deleteVendorHistory(deleteTarget);
		}
	}

	public Map findVendorHistrecInfoForApprovalForm(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		Map<String, Object> basicInfo = vendorMasterRepository.findBasicVendorHistrecInfo(param);
		Map<String, Object> vdOorgInfo = vendorMasterRepository.findVendorOperationOrganizationHistrecInfo(param);
		vdOorgInfo.put("view", true);
		List<Map<String, Object>> orgVdVmgList = vendorMasterRepository.findVendorTargVmgInfo(vdOorgInfo);
		List<Map<String, Object>> athfList = vendorMasterRepository.findAttachmentListVendorHistrecInfo(basicInfo);
		List<Map<String, Object>> bnkAcctList = vendorMasterRepository.findVendorBankAccountHistrecList(basicInfo);

		String chgTypCcd = param.get("chg_typ_ccd").toString();
		// 0. 품의 속성(=정보 변경 대상 여부) 확인
		param.put("ccd", "E027");
		param.put("dtlcd", chgTypCcd);
		param.put("cstr_cnd_cd", "INFO_CHG_TARG_YN");
		String infoChgTargYn = sharedService.getCommonCodeConstraintConditionValue(param);
		infoChgTargYn = infoChgTargYn == null ? "N" : infoChgTargYn.replace("_FIX", "");

		resultMap.put("basicInfo", basicInfo);
		resultMap.put("vdOorgInfo", vdOorgInfo);
		resultMap.put("orgVdVmgList", orgVdVmgList);
		resultMap.put("athfList", athfList);
		resultMap.put("bnkAcctList", bnkAcctList);
		resultMap.put("infoChgTargYn", infoChgTargYn);
		return resultMap;
	}

	public ResultMap uploadVdMasterList(Map<String, Object> param) {
		List<Map<String,Object>> vdList = (List<Map<String, Object>>) param.get("vdList");
		List<Map<String,Object>> vmgList = (List<Map<String, Object>>) param.get("vmgList");

		List<Map<String,Object>> requiredVdFields = new ArrayList<>();
		addField(requiredVdFields, "ERP_VD_CD", "ERP 협력사 코드");
		addField(requiredVdFields, "VD_NM", "협력사 명");
		addField(requiredVdFields, "CTRY_CCD", "국가");
		addField(requiredVdFields, "VD_SIZE_CCD", "협력사 규모");

		List<Map<String,Object>> requiredVMGFields = new ArrayList<>();
		addField(requiredVMGFields, "OORG_CD", "운영조직");
		addField(requiredVMGFields, "ERP_VD_CD", "ERP 협력사 코드");
		addField(requiredVMGFields, "VMG_CD", "SG 코드");

		// 1. 업로드 사용자 기준으로 데이터 리셋
		vendorMasterRepository.deleteVMGUploadByUser();
		vendorMasterRepository.deleteVdMasterUploadByUser();

		// 2.1 협력사 마스터 데이터 insert
		for(Map<String, Object> vendor : vdList){
			vendorMasterRepository.insertVdMasterUploadInfo(vendor);
		}
		// 2.2 업체 VMG 정보 insert
		for(Map<String, Object> vmg : vmgList){
			vendorMasterRepository.insertVMGMasterUploadInfo(vmg);
		}

		// 3. 협력사 데이터 유효성 검증 체크
		List<Map<String,Object>> vdResultList = vendorMasterRepository.uploadVdMasterValidate();
		this.checkInvalidRequiredFields(requiredVdFields, vdResultList);

		// 3.2 협력사 VMG 데이터 유효성 검증
		List<Map<String,Object>> vmgResultList = vendorMasterRepository.uploadVMGMasterValidate();
		this.checkInvalidRequiredFields(requiredVMGFields, vmgResultList);

		long invalidCountVD = vdResultList.stream().filter(result -> "N".equals(result.get("valid_yn"))).count();
		long invalidCountVMG = vmgResultList.stream().filter(result -> "N".equals(result.get("valid_yn"))).count();

		// 결과 값 세팅
		Map<String, Object> resultData = new HashMap<>();
		resultData.put("vdList", vdResultList);
		resultData.put("vmgList", vmgResultList);

		ResultMap resultMap = ResultMap.getInstance();
		resultMap.setResultData(resultData);
		resultMap.setResultStatus(invalidCountVD + invalidCountVMG > 0 ? Const.INVALID_STATUS_ERR : Const.SUCCESS);

		// 모두 성공 시
		if(Const.SUCCESS.equals(resultMap.getResultStatus())){
			// 협력사 정보 insert
			for(Map<String, Object> vendor : vdResultList){
				ResultMap vendorInfo = vendorRegService.saveBasicVdInfo(vendor);

				// ERP 코드 등록
				vendorMasterRepository.updateVdPoPossInfo(vendor);

				String vdCd = (String) vendorInfo.getResultData().get("vd_cd");
				vmgResultList.stream()
						.filter(vmg -> vmg.get("erp_vd_cd").equals(vendor.get("erp_vd_cd")))
						.forEach(vmg -> vmg.put("vd_cd", vdCd));
			}

			// 1. 업체코드로 그룹핑
			Map<String, List<Map<String, Object>>> groupedMap = vmgResultList.stream()
				.collect(Collectors.groupingBy(map -> map.get("vd_cd").toString()));

			// 2. 업체코드 별 운영조직 그룹핑
			groupedMap.forEach((vdCd, groupList) -> {

				Map<String, List<Map<String, Object>>> vdOorgMap = groupList.stream().collect(Collectors.groupingBy(group -> group.get("oorg_cd").toString()));

				vdOorgMap.forEach((oorgCd, oorgVmgList) -> {
					String vdOorgUuid = UUID.randomUUID().toString();

					// 1. Insert VD_OORG
					Map vdOorgParam = Maps.newHashMap();
					vdOorgParam.put("vd_oorg_uuid", vdOorgUuid);
					vdOorgParam.put("vd_cd", vdCd);
					vdOorgParam.put("oorg_cd", oorgCd);
					vendorMasterRepository.insertVendorOperationOrganizationInfo(vdOorgParam);


					// 2. Insert VD_OORG_VMG
					oorgVmgList.stream().forEach(vmgMap -> {
						// Insert Vendor OperationOrg VMG
						String vdOorgVmgUuid = (String)UUID.randomUUID().toString();
						vmgMap.put("vd_oorg_vmg_uuid", vdOorgVmgUuid);
						vmgMap.put("vd_oorg_uuid", vdOorgUuid);
						vendorMasterRepository.insertVdOorgVmg(vmgMap);

						// Update Supply Info
						vmgMap.put("supavl_yn", "Y");
						vendorMasterRepository.updateVdOorgVmgInfo(vmgMap);
					});

					// 3. Update VD_OORG -> Official
					vdOorgParam.put("po_poss_yn", "Y");
					vdOorgParam.put("obd_typ_ccd", "OFC");
					vdOorgParam.put("oorg_ini_transn_st_dt", fm.format(new Date()));
					vendorMasterRepository.updateVdOorgPoPossInfo(vdOorgParam);
				});

				// 4. Insert User Role -> Official
				if(!vdOorgMap.isEmpty()){
					Map roleParam = Maps.newHashMap();
					roleParam.put("usr_id", vdCd);
					roleParam.put("role_cd", "V500");
					roleService.insertRoleUser(roleParam);
				}
			});
		}
		return resultMap;
	}

	public void checkInvalidRequiredFields(List<Map<String,Object>> requiredVdFields, List<Map<String,Object>> targetList){
		targetList.stream().map(result -> {
			// 1. 필수값 체크
			String rquiedFields = requiredVdFields.stream()
					.filter(field -> Strings.isNullOrEmpty((String) result.get(String.valueOf(field.get("key")).toLowerCase())))
					.map(field -> field.get("label").toString())
					.collect(Collectors.joining(","));

			if(!Strings.isNullOrEmpty(rquiedFields)){
				result.put("not_exists_fields", rquiedFields);
				result.put("valid_yn", "N");
			}
			return result;
		}).collect(Collectors.toList());
	}

	public static void addField(List<Map<String,Object>> targetList, String key, String label){
		Map<String, Object> map = new HashMap<>();
		map.put("key", key);
		map.put("label", label);
		targetList.add(map);
	}
    
    /**
     * 협력사 운영 조직 merge
     * @param param
     * @return
     */
    public ResultMap mergeVdOorgCd(Map param){
        //vd_oorg 체크
        if(param.get("vd_oorg_uuid") == null || "".equals(param.get("vd_oorg_uuid"))) {
            String vdOorgUuid = UUID.randomUUID().toString();
            param.put("vd_oorg_uuid", vdOorgUuid);
            // insert
            vendorMasterRepository.insertVendorOperationOrganizationInfo(param);
        } else {
            // update
            vendorMasterRepository.updateVendorOperationOrganizationInfo(param);
        }
        
        return ResultMap.SUCCESS();
    }
    
    /**
     * 협력사 운영 조직 저장
     * @param vendorInfo
     * @return
     */
    public ResultMap saveVdOorgCd(Map vendorInfo) {
        List<Map> vdOorgInfos = vendorMasterRepository.searchVendorOorgInfo(vendorInfo);
        
        for(Map vdOorgInfo : vdOorgInfos) {
            //협력사 운영 조직 merge
            mergeVdOorgCd(vdOorgInfo);
        }
        
        return ResultMap.SUCCESS(vendorInfo);
    }
    
    /**
     * 운영조직 협력사 추가
     * @param param
     */
    public void insertVendorOperationOrganizationInfo(Map param) {
        vendorMasterRepository.insertVendorOperationOrganizationInfo(param);
    }
}