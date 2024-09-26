package smartsuite.app.sp.vendorMaster.vendorInfo.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.vendorMaster.vendorInfo.event.SpVendorInfoEventPublisher;
import smartsuite.app.sp.vendorMaster.vendorInfo.repository.SpVendorMasterRepository;
import smartsuite.data.FloaterStream;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SpVendorMasterService {
    
//	private static final Log LOG = LogFactory.getLog(SpVendorService.class);
    
    @Inject
    SpVendorMasterRepository spVendorMasterRepository;

	@Inject
    SpVendorInfoEventPublisher spVendorInfoEventPublisher;

	@Inject
	StdFileService stdFileService;

    private final SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

	/**
	 * 운영단위별 운영조직 조회.
	 *
	 * @param param
	 * @Date : 2023. 07. 17
	 * @author yjPark
	 */
	public List<Map<String, Object>> findListOorgCdAll(String param) {
        return spVendorMasterRepository.findListOorgCdAll(param);
    }

	/**
	 * 목록 조회. (findListVdInfo)
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    public FloaterStream findListVdProfile(Map<String, Object> param) {
        return spVendorMasterRepository.findListVdProfile(param);
    }

	/**
	 * 통합정보 조회 (findTotalInfo)
	 *
	 * @param param
	 */
	public Map<String, Object> findTotalVdInfo(Map<String, Object> param) {
		Map<String, Object> resultMap  = new HashMap<String, Object>();

		resultMap.put("basicInfo", spVendorMasterRepository.findBasicVendorInfo(param));
//		resultMap.put("financeList", this.findListFinanceInfo(param) );
//		resultMap.put("operOrgRegList", this.findListOperOrgReg(param) );
//		resultMap.put("rfxInfoList", this.findListRfxInfoList(param) );
//		resultMap.put("certInfoList", this.findListCertInfo(param) );

		return resultMap;
	}

	/**
	 * 기본정보 조회 (findBasicInfo)
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	public Map<String, Object> findBasicVendorInfo(Map<String, Object> paramMap) {
		Map<String, Object> param = paramMap;
		if(param == null){
			param = new HashMap<String,Object>();
		}

		Map<String, Object> resultMap  = new HashMap<String, Object>();
		Map<String, Object> basicInfo  = spVendorMasterRepository.findBasicVendorInfo(param);
		List<Map<String, Object>> athfList = spVendorMasterRepository.findVendorAttachmentList(param);      // 첨부파일
		List<Map<String, Object>> bnkAcctList = this.findVendorBankAccountInfo(param);  // 계좌정보
//
//		Map<String, Object> certMgtInfo = sqlSession.selectOne(NAMESPACE + "findCertManagerInfo", param);
//		if(certMgtInfo == null){
//			certMgtInfo = new HashMap<String, Object>();
//		}
//		certMgtInfo.put("cert_mgt", DocumentProperties.get("cert.mgt"));
//
//		/** 블록체인 관련 코드 **/
//		basicInfo.put("blockChainUseYn", blockChainUseYn);
//		if(!"KR".equals(basicInfo.get("nat_cd")) && "Y".equals(blockChainUseYn)){
//			String vdCd = (String) basicInfo.get("vd_cd");
//			Map<String, Object> resultInfo = blockChainIF.findCertificate(vdCd);
//			boolean certVerified = (Boolean) resultInfo.get("certVerified");
//			if(certVerified){
//				basicInfo.put("notBefore", resultInfo.get("notBefore")); // 사용 시작일
//				basicInfo.put("notAfter" , resultInfo.get("notAfter"));  // 사용 만료일
//			}
//			basicInfo.put("certVerified", certVerified);
//
//		}
//		/** 블록체인 관련 코드 **/

		resultMap.put("basicInfo", basicInfo);
		resultMap.put("athfList", athfList);
		resultMap.put("bnkAcctList", bnkAcctList);
//		resultMap.put("certMgtInfo", certMgtInfo);

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
		Map<String, Object> basicInfo = (Map<String, Object>) param.get("basicInfo");
		List<Map<String, Object>> athfList = (List<Map<String, Object>>)param.get("athfList");

		if(param.containsKey("operInfo")){
			Map<String, Object> operInfo = (Map<String, Object>) param.get("operInfo");

			//vd_oorg 체크
			if(operInfo.get("vd_oorg_uuid") == null || "".equals(operInfo.get("vd_oorg_uuid"))) {
				String vdOorgUuid = UUID.randomUUID().toString();
				operInfo.put("vd_oorg_uuid", vdOorgUuid);
				// insert
				spVendorMasterRepository.insertVendorOperationOrganizationInfo(operInfo);
			} else {
				// update
				spVendorMasterRepository.updateVendorOperationOrganizationInfo(operInfo);
			}
		}

		//vd update
		spVendorMasterRepository.updateBasicVendorInfo(basicInfo);

		// 첨부파일
		if(athfList != null && !athfList.isEmpty()){
			for (Map<String, Object> athfInfo : athfList){
				if(athfInfo.get("vd_athf_uuid") == null || "".equals(athfInfo.get("vd_athf_uuid"))){
					String vdAthfUuid = UUID.randomUUID().toString();
					athfInfo.put("vd_athf_uuid", vdAthfUuid);
					athfInfo.put("use_yn", "Y");
					spVendorMasterRepository.insertVendorAttachmentInfo(athfInfo);
				} else {
					athfInfo.put("sts", "U");
					spVendorMasterRepository.updateVendorAttachmentInfo(athfInfo);
				}
			}
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 기본정보 첨부파일 저장, 수정
	 *
	 * @param param
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	public ResultMap saveBasicAttachmentInfo(Map<String, Object> param) {
		String vdAthfUuid = (String)param.get("vd_athf_uuid");
		if(!vdAthfUuid.isEmpty()) {
			param.put("sts", "U");
			spVendorMasterRepository.updateVendorAttachmentInfo(param);
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 기본정보 계좌정보 조회
	 *
	 * @param param
	 * @Date : 2023. 11. 29
	 * @author sykim
	 */
	public List<Map<String, Object>> findVendorBankAccountInfo(Map<String, Object> param) {
		return spVendorMasterRepository.findVendorBankAccountInfo(param);
	}

	/**
	 * 기본정보 계좌정보 저장 (자사정보)
	 *
	 * @param param
	 * @Date : 2023. 11. 29
	 * @author sykim
	 */
	public ResultMap saveVendorBankAccountInfo(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		String vdBnkAcctUuid = "";

		// 신규 저장
		for(Map<String, Object> row : inserts){
			// uuid 채번
			vdBnkAcctUuid = UUID.randomUUID().toString();
			row.put("vd_bnkacct_uuid", vdBnkAcctUuid);
			spVendorMasterRepository.insertVendorBankAccountInfo(row);
		}

		// 수정
		for(Map<String, Object> row : updates){
			spVendorMasterRepository.updateVendorBankAccountInfo(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 기본정보 계좌정보 삭제 (자사정보)
	 *
	 * @param param
	 * @Date : 2023. 11. 29
	 * @author sykim
	 */
	public ResultMap deleteVendorBankAccountInfo(Map<String, Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedBankAccountInfo");

		for (Map<String, Object> row : deletes) {
			spVendorMasterRepository.deleteVendorBankAccountInfo(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사-운영조직 : 협력사관리유형 정보 조회 (findListVendorManagementTypeInfo)
	 *
	 * @param param
	 * @Date : 2023. 06. 15
	 * @author sykim
	 */
	public List findListVendorManagementTypeInfo(Map<String, Object> param) {
		return spVendorMasterRepository.findListVendorManagementTypeInfo(param);
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
		return spVendorMasterRepository.findListVmtUsing(param);
	}

	/**
	 * 운영조직 탭 운영조직정보 & 협력사관리유형 & 협력사관리그룹 조회
	 *
	 * @param param(vd_cd, oorg_cd)
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	public Map<String, Object> findVdOorgAndVmtVmgInfo(Map<String, Object> param) {
		Map<String, Object> resultMap = this.findVdOorgAndVmtInfo(param);
		resultMap.put("obdEdMgmtGrpList", spVendorMasterRepository.findListVendorManagementGroup(param)); // 협력사-운영조직 : 거래가능 협력사관리그룹 정보
		return resultMap;
	}

	/**
	 * 운영조직 탭 운영조직정보 & 협력사관리유형 조회
	 *
	 * @param param
	 * @Date : 2023. 06. 01
	 * @author sykim
	 */
	public Map<String, Object> findVdOorgAndVmtInfo(Map<String, Object> param) {
		Map resultMap = Maps.newHashMap();

		Map<String, Object> vdOorgInfo = spVendorMasterRepository.findVendorOperationOrganizationInfo(param);
		if (vdOorgInfo == null) {
			vdOorgInfo = param;
			// 협력사 운영조직 정보가 없는 경우는 신규업체로 간주함
			vdOorgInfo.put("vd_oorg_uuid", null);
			vdOorgInfo.put("obd_typ_ccd", "NEW");
			vdOorgInfo.put("po_poss_yn", "N");
		}

		resultMap.put("vdOorgInfo", vdOorgInfo); // 협력사 운영조직정보
		resultMap.put("vendorManagementTypeList", spVendorMasterRepository.findListVendorManagementTypeInfo(param)); // 협력사-운영조직 : 협력사관리유형 정보
		return resultMap;
	}

	/**
	 * 추가 거래가능 협력사관리그룹 조회 (findListUnregisteredVendorManagementGroup)
	 *
	 * @param param
	 * @Date : 2023. 06. 22
	 * @author yjPark
	 */
	public FloaterStream findListUnregisteredVendorManagementGroup(Map<String, Object> param) {
		return spVendorMasterRepository.findListUnregisteredVendorManagementGroup(param);
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
        	vdOorgUuid = UUID.randomUUID().toString();
            pivotMap.put("vd_oorg_uuid", vdOorgUuid);
            spVendorMasterRepository.insertVendorOperationOrganizationInfo(pivotMap);
        }

		ResultMap resultMap = spVendorInfoEventPublisher.saveListReqOnboardingEval(param);  // 온보딩평가요청
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
        Map<String, Object> aprvProgMap = spVendorInfoEventPublisher.findReqedOnboardingAprvProgYn(param);
		String aprvProgYn = aprvProgMap.get("aprv_prog_yn") == null ? "" : aprvProgMap.get("aprv_prog_yn").toString();
    	if("Y".equals(aprvProgYn)) { // 협력사관리그룹 결재 진행 중
    		return ResultMap.INVALID();
    	}

    	// 2. 온보딩평가 품의 - 미등록완료 저장 호출
    	return spVendorInfoEventPublisher.saveListOnboardingEvalUnRegComplete(param);
    }

	/**
	 * 주요정보 변경관리 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023.07.13
	 * @Method Name : findListVendorInfoChangeRequest
	 */
	public List<Map<String, Object>> findListVendorInfoChangeRequest(Map<String, Object> param) {
		return spVendorMasterRepository.findListVendorInfoChangeRequest(param);
	}

	/**
	 * 협력사 정보 변경 요청 가능 여부를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023.07.13
	 * @Method Name : checkChangeRequestYn
	 */
	public ResultMap checkChangeRequestYn(Map<String, Object> param) {
		Map<String, Object> resultData = new HashMap<String,Object>();

		resultData = spVendorMasterRepository.checkErpVdCd(param);
		String erpVdCd = resultData == null || resultData.get("erp_vd_cd") == null ? null : resultData.get("erp_vd_cd").toString();
		if(erpVdCd != null && !"".equals(erpVdCd)) {
			resultData = spVendorMasterRepository.checkChangeRequestYn(param);
			return ResultMap.SUCCESS(resultData);
		}else {
			return ResultMap.FAIL();
		}
	}

	/**
	 * 협력사 정보 변경 요청을 상세 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023.07.13
	 * @Method Name : findVendorInfoChangeRequestInfo
	 */
	public ResultMap findVendorInfoChangeRequestInfo(Map<String, Object> param) {
		Map<String, Object> resultData = new HashMap<String,Object>();

		Map<String, Object> basicInfo = new HashMap<String,Object>();
		Map<String, Object> nextBasicInfo = new HashMap<String,Object>();

		List<Map<String, Object>> athfList  = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> nextAthfList  = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> bnkAcctList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> nextBnkAcctList = new ArrayList<Map<String, Object>>();

		param.put("info_chg_req_yn", "Y"); // 정보변경요청 조회 flag
		if(param.get("vd_info_chg_req_uuid") == null || "".equals(param.get("vd_info_chg_req_uuid"))){  // '신규' 버튼으로 조회
			//Map<String, Object> prevBasicInfo = spVendorMasterRepository.findPrevHistrecForVendorReqMainInfoChange(param);
			//String modSeqno = (prevBasicInfo == null || prevBasicInfo.get("mod_seqno") == null) ? "0" : prevBasicInfo.get("mod_seqno").toString();
//			if(prevBasicInfo != null){  // 이력 존재
//				// 2. 이전 협력사 정보 변경 요청 정보 조회
//				basicInfo = spVendorMasterRepository.findBasicVendorHistrecInfo(prevBasicInfo);
//				athfList = spVendorMasterRepository.findAttachmentListVendorHistrecInfo(prevBasicInfo);
//				bnkAcctList = spVendorMasterRepository.findVendorBankAccountHistrecList(prevBasicInfo);
//			} else {
				// 신규 작성 시 현행 정보 조회
				basicInfo  = spVendorMasterRepository.findBasicVendorInfo(param);
				athfList = spVendorMasterRepository.findVendorAttachmentList(param);
				bnkAcctList = spVendorMasterRepository.findVendorBankAccountInfo(param);
//			}

			// 신규차수 정보도 이전차수와 동일함
			nextBasicInfo = basicInfo;
			nextBasicInfo.put("chg_req_sts_ccd", null);		 // 신규 정보 변경 요청 상태 null로 세팅
			nextBasicInfo.put("vd_info_chg_req_uuid", null); // 신규 협력사 정보 변경 요청 UUID null로 세팅
			nextAthfList = athfList;
			nextBnkAcctList = bnkAcctList;
		} else {  // 변경 목록에서 조회(수정 중인 차수가 존재하는 경우)
			nextBasicInfo  = spVendorMasterRepository.findVendorInfoChangeRequestInfo(param);           // 협력사 변경 요청 테이블 조회
			nextAthfList = spVendorMasterRepository.findVendorAttachmentChangeRequestList(param);	    // 첨부파일 변경 요청 테이블 조회
			nextBnkAcctList = spVendorMasterRepository.findVendorBankAccountChangeRequestList(param);   // 계좌정보 변경 요청 테이블 조회

			// 2-1. 선택한 차수의 이전차수, 협력사 변경이력 존재여부 확인
			Map<String, Object> prevBasicInfo = spVendorMasterRepository.findPrevHistrecForVendorReqMainInfoChange(nextBasicInfo);
			String modSeqno = (prevBasicInfo == null || prevBasicInfo.get("mod_seqno") == null) ? "0" : prevBasicInfo.get("mod_seqno").toString();
			if(prevBasicInfo != null){  // 이력 존재
				// 2. 이전 협력사 정보 변경 요청 정보 조회
				basicInfo = spVendorMasterRepository.findBasicVendorHistrecInfo(prevBasicInfo);
				athfList = spVendorMasterRepository.findAttachmentListVendorHistrecInfo(prevBasicInfo);
				bnkAcctList = spVendorMasterRepository.findVendorBankAccountHistrecList(prevBasicInfo);
			} else {
				// 이력 존재하지 않을 경우 이전 정보 = 협력사의 현재 정보
				basicInfo  = spVendorMasterRepository.findBasicVendorInfo(param);
				athfList = spVendorMasterRepository.findVendorAttachmentList(param);
				bnkAcctList = spVendorMasterRepository.findVendorBankAccountInfo(param);
			}

			// 3-1. 선택한 차수의 이전차수, 협력사 운영조직 변경이력 존재여부 확인
			/*Map<String, Object> prevOorgInfo = spVendorMasterRepository.findPrevOorgHistrecForVendorReqMainInfoChange(nextBasicInfo);
			if(prevOorgInfo != null){  // 이력 존재
				// 3. 이전 협력사 정보 변경 요청 정보 조회
				vdOorgInfo = spVendorMasterRepository.findVendorOperationOrganizationHistrecInfo(prevBasicInfo);

				// bankInfo 추가예정
				//bankInfo = spVendorMasterRepository.findVendorBankHistrecInfo(param);
			}*/
		}

		resultData.put("basicInfo", basicInfo);
		resultData.put("nextBasicInfo", nextBasicInfo);
		resultData.put("athfList", athfList);
		resultData.put("nextAthfList", nextAthfList);
		resultData.put("bnkAcctList", bnkAcctList);
		resultData.put("nextBnkAcctList", nextBnkAcctList);
		return ResultMap.SUCCESS(resultData);
	}

	/* 협력사 정보변경 요청이력 차수 조회 */
	public int getMaxVendorChangeReqRev(Map<String, Object> param) {
		Integer rev = spVendorMasterRepository.getMaxVendorChangeReqRev(param);
		return rev == null ? 0 : rev.intValue();
	}

	/**
	 * 협력사 정보 변경 요청을 임시저장한다.
	 *
	 * @param param the param
	 * @Date : 2023.07.13
	 * @Method Name : saveVendorChangeRequestInfo
	 */
	public ResultMap saveVendorChangeRequestInfo(Map<String, Object> param){
		Map<String, Object> resultData = new HashMap<String,Object>();

		Map<String, Object> basicInfo = (Map<String, Object>) param.get("basicInfo");
		List<Map<String, Object>> athfList = (List<Map<String, Object>>)param.get("athfList");	// 첨부파일 변경 요청 리스트
		List<Map<String, Object>> bnkAcctList = (List<Map<String, Object>>) param.get("bnkAcctList"); // 계좌정보 변경 요청 리스트

		if(basicInfo == null || !basicInfo.containsKey("oorg_cd")){
			return ResultMap.FAIL();
		}

		// 협력사 변경 이력 정보 저장
		String vdInfoChgReqUuid = "";	// 협력사 변경이력 아이디
		if(basicInfo.get("vd_info_chg_req_uuid") == null || "".equals(basicInfo.get("vd_info_chg_req_uuid"))){
			// 신규 이력정보 INSERT
			Integer chgReqRevno = this.getMaxVendorChangeReqRev(basicInfo);
			chgReqRevno = chgReqRevno == null ? 1 : chgReqRevno.intValue() + 1;
			// 협력사 변경이력 아이디 채번
			vdInfoChgReqUuid = UUID.randomUUID().toString();

			basicInfo.put("vd_info_chg_req_uuid", vdInfoChgReqUuid);
			basicInfo.put("chg_req_revno", chgReqRevno);
			basicInfo.put("chg_req_sts_ccd", "SAVE");	// 저장
			basicInfo.put("mod_cls", "C");	// 주요정보 변경요청

			spVendorMasterRepository.insertVendorInfoChangeRequest(basicInfo);		// 협력사 정보 변경 요청
			resultData.put("chg_req_revno", chgReqRevno);
		} else {
			vdInfoChgReqUuid = (String) basicInfo.get("vd_info_chg_req_uuid");
			String chg_req_sts_ccd = (String) basicInfo.get("chg_req_sts_ccd");
			if("SAVE".equals(chg_req_sts_ccd) || "RET".equals(chg_req_sts_ccd)) {
				// 변경요청이력 정보가 존재하고 임시저장/결재반려인 경우만 업데이트
				basicInfo.put("chg_req_sts_ccd", "SAVE");
				spVendorMasterRepository.updateVendorInfoChange(basicInfo);		// 협력사 정보 변경 요청
				resultData.put("chg_req_revno", basicInfo.get("chg_req_revno"));
			}
		}

		// 첨부파일
		if(athfList != null && !athfList.isEmpty()){
			for (Map<String, Object> athfInfo : athfList){
				if(athfInfo.get("vd_athf_chg_req_uuid") == null || "".equals(athfInfo.get("vd_athf_chg_req_uuid"))){
					String vdAthfChgReqUuid = UUID.randomUUID().toString();
					athfInfo.put("vd_athf_chg_req_uuid", vdAthfChgReqUuid);	// 협력사 첨부파일 변경 요청 UUID
					athfInfo.put("vd_info_chg_req_uuid", vdInfoChgReqUuid);	// 협력사 정보 변경 요청 UUID
					athfInfo.put("use_yn", "Y");
					spVendorMasterRepository.insertVendorAttachmentChangeRequest(athfInfo);
				} else {
					athfInfo.put("sts", "U");
					spVendorMasterRepository.updateVendorAttachmentChangeRequest(athfInfo);
				}
			}
		}

		// 계좌정보
		if(bnkAcctList != null && !bnkAcctList.isEmpty()) {
			// 기존 작성된 요청 delete 후 insert
			spVendorMasterRepository.deleteVendorBankAccountChangeRequestInfo(basicInfo);

			for (Map<String, Object> bnkAcctInfo : bnkAcctList){
				bnkAcctInfo.put("vd_bnkacct_chg_req_uuid", UUID.randomUUID().toString());// 협력사 계좌정보 변경 요청 UUID
				bnkAcctInfo.put("vd_info_chg_req_uuid", vdInfoChgReqUuid);               // 협력사 정보 변경 요청 UUID
				spVendorMasterRepository.insertVendorBankAccountInfoChange(bnkAcctInfo); // 협력사 계좌 변경 요청
			}
		}

		resultData.put("vd_info_chg_req_uuid", vdInfoChgReqUuid);
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 협력사 정보 변경 요청을 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023.07.13
	 * @Method Name : sendVendorChangeRequestInfo
	 */
	public ResultMap sendVendorChangeRequestInfo(Map<String, Object> param){
		param.put("chg_req_sts_ccd", "REQ");  // 요청
		spVendorMasterRepository.sendVendorInfoChange(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사 정보 변경 요청을 복사한다.
	 *
	 * @param param the param
	 * @Date : 2023.07.13
	 * @Method Name : copyVendorChangeRequestInfo
	 */
	public ResultMap copyVendorChangeRequestInfo(Map<String, Object> param){
		Map<String, Object> resultData = new HashMap<String,Object>();

		Map<String, Object> basicInfo = (Map<String, Object>) param.get("basicInfo");
		if(basicInfo == null){
			return ResultMap.FAIL();
		}

		String newVdInfoChgReqUuid = UUID.randomUUID().toString();	// 협력사 변경이력 아이디

		// 협력사 변경 요청 정보 저장
		Integer chgReqRevno = this.getMaxVendorChangeReqRev(basicInfo);
		chgReqRevno = chgReqRevno == null ? 1 : chgReqRevno.intValue() + 1;

		basicInfo.put("new_vd_info_chg_req_uuid", newVdInfoChgReqUuid);
		basicInfo.put("chg_req_revno", chgReqRevno);
		basicInfo.put("chg_req_sts_ccd", "SAVE");	// 저장
		spVendorMasterRepository.insertCopyVendorInfoChange(basicInfo);

		// 협력사 첨부파일 변경 요청 복사
		List<Map<String, Object>> athfList = spVendorMasterRepository.findListVendorAttachmentChangeRequest(basicInfo);
		if(athfList != null && !athfList.isEmpty()) {
			for (Map<String, Object> athfInfo : athfList) {
				String attNo = (String)athfInfo.get("athg_uuid") != null ? (String)athfInfo.get("athg_uuid") : "" ;
				if (!"".equals(attNo)) {
					String newVdAthfChgReqUuid = (String)UUID.randomUUID().toString();
					String newAttNo = stdFileService.copyFile(attNo);
					athfInfo.put("new_vd_athf_chg_req_uuid", newVdAthfChgReqUuid);
					athfInfo.put("new_athg_uuid", newAttNo);
					athfInfo.put("new_vd_info_chg_req_uuid", newVdInfoChgReqUuid);
					spVendorMasterRepository.insertCopyVEndorAttachmentChangeRequest(athfInfo);
				}
			}
		}

		// 협력사 계좌정보 변경 요청 복사
		// 계좌정보는 어떤 값을 복사하든 차수와 관계 없이 현행 정보를 복사
		List<Map<String, Object>> bnkAcctList = spVendorMasterRepository.findVendorBankAccountInfo(basicInfo);
		if(bnkAcctList != null && !bnkAcctList.isEmpty()) {
			for (Map<String, Object> bnkAcctInfo : bnkAcctList) {
				bnkAcctInfo.put("vd_bnkacct_chg_req_uuid", UUID.randomUUID().toString()); // 협력사 계좌변경요청 아이디
				bnkAcctInfo.put("vd_info_chg_req_uuid", newVdInfoChgReqUuid);
				spVendorMasterRepository.insertVendorBankAccountInfoChange(bnkAcctInfo);  // 협력사 계좌 변경 요청
			}
		}

		resultData.put("new_vd_info_chg_req_uuid", newVdInfoChgReqUuid);
		resultData.put("req_rev", chgReqRevno);
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 협력사 정보 변경 요청을 삭제한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : deleteVendorChangeRequestInfo
	 */
	public ResultMap deleteVendorChangeRequestInfo(Map<String, Object> param){
		Map<String, Object> basicInfo = (Map<String, Object>) param.get("basicInfo");

		spVendorMasterRepository.deleteVendorBankAccountChangeRequestInfo(basicInfo); // 변경 요청 계좌 삭제
		spVendorMasterRepository.deleteVendorAttachmentChangeRequestInfo(basicInfo); // 변경 요청 첨부파일 삭제
		spVendorMasterRepository.deleteVendorChangeRequestInfo(basicInfo); // 변경 요청 삭제

		return ResultMap.SUCCESS();
	}

	/**
	 * 정규 상태인 협력사 운영조직을 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : findPoPossOorgCdByVd
	 */
	public List findPoPossOorgCdByVd(Map<String, Object> param){
		Map<String, Object> basicInfo = (Map<String, Object>) param.get("basicInfo");
		return spVendorMasterRepository.findPoPossOorgCdByVd(basicInfo);
	}

	/**
	 * 운영조직 별 협력사 계좌 변경 요청 정보를 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023.07.13
	 * @Method Name : findVendorBankInfoChangeRequestInfo
	 */
	public ResultMap findVendorBankInfoChangeRequestInfo(Map<String, Object> param){
		Map<String, Object> resultData = new HashMap<String,Object>();
		Map<String, Object> bankInfo = new HashMap<String,Object>();
		Map<String, Object> nextBankInfo = new HashMap<String,Object>();

		// TODO : 계좌정보 수정 필요
		if(param.get("vd_info_chg_req_uuid") == null || "".equals(param.get("vd_info_chg_req_uuid"))){  // '신규' 버튼으로 조회
			bankInfo = spVendorMasterRepository.findVendorBankInfo(param);
			nextBankInfo = bankInfo;

		} else {  // 수정 중인 차수가 존재하는 경우
			bankInfo = spVendorMasterRepository.findVendorBankInfo(param);
			nextBankInfo = bankInfo;
		}
		resultData.put("bankInfo", bankInfo);
		resultData.put("nextBankInfo", nextBankInfo);
		return ResultMap.SUCCESS(resultData);
	}
}
