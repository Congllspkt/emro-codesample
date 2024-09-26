package smartsuite.app.bp.contract.common.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import smartsuite.app.bp.admin.organizationManager.operationUnit.service.OperationUnitManagerService;
import smartsuite.app.bp.admin.organizationManager.service.OrganizationManagerService;
import smartsuite.app.bp.contract.common.event.ContractEventPublisher;
import smartsuite.app.bp.contract.common.repository.ContractRepository;
import smartsuite.app.bp.contract.contractcnd.factory.ContractCndFactory;
import smartsuite.app.bp.contract.contractcnd.service.ContractCndService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.SignOrder;
import smartsuite.app.shared.status.ContractStatusService;
import smartsuite.data.FloaterStream;
import smartsuite.exception.CommonException;
import smartsuite.module.ModuleManager;
import smartsuite.security.account.service.AccountService;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 계약 관련 처리하는 서비스 Class입니다.
 *
 * @FileName ContractService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class ContractService {

	private static final Logger LOG = LoggerFactory.getLogger(ContractService.class);
	private static final String DATE_FORMAT = "yyyyMMdd";

	@Value("#{smartsuiteProperties['smartsuite.upload.path']}")
	private String fileUploadPath;

	@Inject
	ContractRepository contractRepository;
	@Inject
	SharedService sharedService;
	@Inject
	AccountService accountService;
	@Inject
	ContractEventPublisher contractEventPublisher;
	@Inject
	ContractCndFactory contractCndFactory;
	@Inject
	OperationUnitManagerService operationUnitManagerService;
	@Inject
	OrganizationManagerService organizationManagerService;
	@Inject
	ContractNxtPrcsService contractNxtPrcsService;
	@Inject
	ContractStatusService contractStatusService;

	/**
	 * 계약 목록 조회
	 * @param param
	 * @return
	 */
	public FloaterStream largeFindListContract(Map param) {
		return contractRepository.largeFindListContract(param);
	}

	/**
	 * 계약 정보 저장
	 * @param cntrInfo
	 * @param vendorList
	 * @return
	 */
	public Map saveContract(Map cntrInfo, List<Map> vendorList) {
		String cntrId = (String) cntrInfo.get("cntr_uuid");
		String cntrNo = (String) cntrInfo.get("cntr_no");
		int cntrRevno = cntrInfo.get("cntr_revno") == null ? 1 : Integer.parseInt(cntrInfo.get("cntr_revno").toString());
		boolean isNew = false;

		if(Strings.isNullOrEmpty(cntrId)) {
			isNew = true;
			cntrId = UUID.randomUUID().toString();

			if(Strings.isNullOrEmpty(cntrNo)) {
				cntrNo = sharedService.generateDocumentNumber("CT");
				cntrRevno = 1;
			} else {
				String maxCntrRevno = contractRepository.getMaxCntrRevNo(cntrNo);
				cntrRevno = Integer.parseInt(maxCntrRevno) + 1;
			}
		}

		cntrInfo.put("cntr_uuid", cntrId);
		cntrInfo.put("cntr_no", cntrNo);
		cntrInfo.put("cntr_revno", cntrRevno);

		SignOrder signOrder = contractEventPublisher.getSignOrder(cntrInfo);
		cntrInfo.put("sgnord_typ_ccd", signOrder.toString());

		if(isNew) {
			Map<String, Object> userInfo = Auth.getCurrentUserInfo();
			cntrInfo.put("cntr_pic_id", userInfo.get("usr_id")); //계약 담당자
			cntrInfo.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.CREATION); // 계약 상태

			String cntrTypCcd = (String) cntrInfo.get("cntr_typ_ccd"); // 계약 유형
			cntrInfo.put("cntr_typ_ccd", Strings.isNullOrEmpty(cntrTypCcd) ? CntrConst.CNTR_TYPE.NEW : cntrTypCcd);

			// 논리 조직 코드 조회
			String oorgCd = (String) cntrInfo.get("oorg_cd");
			String rootLogicOrgCd = this.findRootLogicOrganizationWithOperationOrg(oorgCd);
			cntrInfo.put("logic_org_cd", rootLogicOrgCd);

			// 계약 마스터 정보 저장
			contractRepository.insertContract(cntrInfo);
			contractStatusService.creating(cntrInfo);

			// 계약자 정보 저장 (구매사)
			Map<String, Object> buyerInfo = Maps.newHashMap();
			buyerInfo.put("cntr_uuid", cntrId);
			buyerInfo.put("cntrr_typ_ccd", CntrConst.USR_TYPE.BUYER);
			buyerInfo.put("usr_id", userInfo.get("usr_id"));
			buyerInfo.put("cntrr_eml", userInfo.get("eml"));
			buyerInfo.put("cntrr_mob", userInfo.get("mob"));
			contractRepository.insertContractor(buyerInfo);

			// 계약자 정보 저장 (공급사)
			if(vendorList == null || vendorList.isEmpty()) {
				Map<String, Object> vendorInfo = Maps.newHashMap();
				vendorInfo.put("cntr_uuid", cntrId);
				vendorInfo.put("cntrr_typ_ccd", CntrConst.USR_TYPE.VENDOR);
				vendorInfo.put("vd_cd", cntrInfo.get("vd_cd"));
				vendorInfo.put("cntrr_eml", cntrInfo.get("cntrr_eml"));
				vendorInfo.put("cntrr_mob", cntrInfo.get("cntrr_mob"));
				vendorInfo.put("rep_vd_yn", "Y");
				contractRepository.insertContractor(vendorInfo);
			} else {
				for(Map row : vendorList) {
					row.put("cntr_uuid", cntrId);
					row.put("cntrr_typ_ccd", CntrConst.USR_TYPE.VENDOR);
					if(cntrInfo.get("vd_cd").equals(row.get("vd_cd"))) {
						row.put("rep_vd_yn", "Y");
					} else {
						row.put("rep_vd_yn", "N");
					}
					contractRepository.insertContractor(row);
				}
			}

		} else {
			// 계약 마스터 정보 저장
			contractRepository.updateContract(cntrInfo);

			// 계약자 정보 저장 (공급사)
			if(vendorList == null || vendorList.isEmpty()) {
				Map<String, Object> vendorInfo = Maps.newHashMap();
				vendorInfo.put("cntr_uuid", cntrId);
				vendorInfo.put("vd_cd", cntrInfo.get("vd_cd"));
				vendorInfo.put("cntrr_eml", cntrInfo.get("cntrr_eml"));
				vendorInfo.put("cntrr_mob", cntrInfo.get("cntrr_mob"));
				contractRepository.updateContractorByVdCd(vendorInfo);
			} else {
				for(Map row : vendorList) {
					contractRepository.updateContractor(row);
				}
			}
		}

		cntrInfo.put("isNew", isNew);
		return cntrInfo;
	}

	public String findRootLogicOrganizationWithOperationOrg(String oorgCd) {
		List operUnitList = operationUnitManagerService.findListOperationUnit(null);
		String logicOrgCd = this.removeOunitCd(oorgCd, operUnitList);
		Map rootLogicOrgCdMap = organizationManagerService.findRootLogicOrganizationInfo(logicOrgCd);
		String rootLogicOrgCd = (String) rootLogicOrgCdMap.get("logic_org_cd");
		return rootLogicOrgCd;
	}

	private String removeOunitCd(String oorgCd, List<Map<String, Object>> ounitList) {
		String logicOrgCd = "";
		for(Map<String, Object> ounit : ounitList) {
			String ounitCd = (String) ounit.get("ounit_cd");
			int length = ounitCd.length();
			if(ounitCd.equals(oorgCd.substring(0, length))) {
				logicOrgCd = oorgCd.replace(ounitCd, "");
				return logicOrgCd;
			}
		}
		return logicOrgCd;
	}

	/**
	 * 계약 정보 조회
	 * @param param
	 * @return
	 */
	public Map findContract(Map param) {
		return contractRepository.findContract(param);
	}

	/**
	 * 계약 정보 조회
	 * @param param
	 * @return
	 */
	public ResultMap findContractDetail(Map param) {
		Map cntr = contractRepository.findContract(param);
		Map cntrDoc = contractEventPublisher.findEcontract(cntr);
		String ecntrId = (String) cntrDoc.get("ecntr_uuid");

		// 계약 정보
		Map cntrInfo;
		if(Strings.isNullOrEmpty(ecntrId)) {
			cntrInfo = Maps.newHashMap(cntr);
			cntrInfo.put("cntrdoc_tmpl_nm", cntrDoc.get("cntrdoc_tmpl_nm"));
		} else {
			cntrInfo = Maps.newHashMap(cntrDoc);
			cntrInfo.put("cntr_req_uuid", cntr.get("cntr_req_uuid"));
			cntrInfo.put("apvl_uuid", cntr.get("apvl_uuid"));
			cntrInfo.put("apvl_sts_ccd", cntr.get("apvl_sts_ccd"));
			cntrInfo.put("apvl_typ_ccd", cntr.get("apvl_typ_ccd"));
		}
		// 계약자 리스트
		List<Map> contractorList = contractRepository.findListContractor(cntrInfo);
		for(Map row : contractorList) {
			String repVdYn = (String) row.get("rep_vd_yn");
			if(repVdYn.equals("Y")) {
				cntrInfo.put("cntrr_eml", row.get("cntrr_eml"));
				cntrInfo.put("cntrr_mob", row.get("cntrr_mob"));
				cntrInfo.put("bizregno", row.get("bizregno"));
			}
		}

		Map resultData = Maps.newHashMap();
		resultData.put("cntrInfo", cntrInfo);
		resultData.put("contractorList", contractorList);
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 변경 계약 정보 조회
	 * @param param
	 * @return
	 */
	public ResultMap findChangeContractDetail(Map param) {
		String cntrTypCcd = (String) param.get("cntr_typ_ccd");
		Map searchParam = Maps.newHashMap();
		searchParam.put("cntr_uuid", param.get("orgn_cntr_uuid"));

		// 계약 정보
		Map cntrInfo = Maps.newHashMap();
		if(CntrConst.CNTR_TYPE.TERMINATION.equals(cntrTypCcd)) {
			// 해지인 경우, 해지 프로세스 사용 여부가 N이면 기존 계약정보를 리턴한다.
			boolean isTerminationProcess = this.getTerminationProcessSetup();
			if(!isTerminationProcess) {
				ResultMap result = this.findContractDetail(searchParam);
				cntrInfo = (Map) result.getResultData().get("cntrInfo");
				cntrInfo.put("cntr_typ_ccd", CntrConst.CNTR_TYPE.TERMINATION);
				cntrInfo.put("cntr_req_rcpt_uuid", param.get("cntr_req_rcpt_uuid"));
				cntrInfo.put("cntr_req_uuid", param.get("cntr_req_uuid"));
				cntrInfo.put("cntr_trmn_rsn", param.get("cntr_req_rsn"));
				cntrInfo.put("orgn_cntr_cnd_uuid", cntrInfo.get("cntr_cnd_uuid"));
				return result;
			}
		}

		cntrInfo = contractRepository.findChangeContract(searchParam);
		cntrInfo.put("cntr_typ_ccd", cntrTypCcd);

		// 계약 조건 정보
		Map cntrCndInfo = this.copyCntrCndData((String) cntrInfo.get("cntrdoc_typ_ccd"), (String) cntrInfo.get("orgn_cntr_cnd_uuid"));
		cntrInfo.put("cntrCndInfo", cntrCndInfo);

		// 계약자 리스트
		List<Map> contractorList = contractRepository.findListContractor(searchParam);
		for(Map row : contractorList) {
			String repVdYn = (String) row.get("rep_vd_yn");
			if(repVdYn.equals("Y")) {
				cntrInfo.put("cntrr_eml", row.get("cntrr_eml"));
				cntrInfo.put("cntrr_mob", row.get("cntrr_mob"));
				cntrInfo.put("bizregno", row.get("bizregno"));
			}
		}

		Map resultData = Maps.newHashMap();
		resultData.put("cntrInfo", cntrInfo);
		resultData.put("contractorList", contractorList);
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 계약 정보 삭제
	 * @param param
	 * @return
	 */
	public void deleteContract(Map param) {
		// 전자계약 정보 삭제
		String ecntrId = (String) param.get("ecntr_uuid");
		if(!Strings.isNullOrEmpty(ecntrId)) {
			contractEventPublisher.deleteEcontract(param);
		}
		// 계약 조건 삭제
		this.deleteCntrCnd((String) param.get("cntrdoc_typ_ccd"), (String) param.get("cntr_cnd_uuid"));
		// 계약 정보 삭제
		contractRepository.deleteContractHistory(param);
		contractRepository.deleteContractor(param);
		contractStatusService.deleteContract(param);
	}

	/**
	 * 계약 서명방법 공통코드 조회
	 * @param param
	 * @return
	 */
	public List findListCommonCodeCntrSgnMeth(Map param) {
		List<Map<String, Object>> resultCodes = Lists.newArrayList();
		List<Map<String, Object>> codes = sharedService.findListCommonCodeAttributeCode(param);
		resultCodes.addAll(codes);

		List<String> modules = ModuleManager.getModules();
		if(modules.contains("EDOC")) {
			param.put("cstr_cnd_val", "EDOC");
			codes = sharedService.findListCommonCodeAttributeCode(param);
			resultCodes.addAll(codes);
		}
		if(modules.contains("EDOC-DOCUSIGN")) {
			param.put("cstr_cnd_val", "EDOC-DOCUSIGN");
			codes = sharedService.findListCommonCodeAttributeCode(param);
			resultCodes.addAll(codes);
		}
		if(modules.contains("EDOC-EFORM")) {
			param.put("cstr_cnd_val", "EDOC-EFORM");
			codes = sharedService.findListCommonCodeAttributeCode(param);
			resultCodes.addAll(codes);
		}
		if(modules.contains("EDOC-ADOBESIGN")) {
			param.put("cstr_cnd_val", "EDOC-ADOBESIGN");
			codes = sharedService.findListCommonCodeAttributeCode(param);
			resultCodes.addAll(codes);
		}

		Collections.sort(resultCodes, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				BigDecimal sort1 = (BigDecimal) o1.get("dtlcd_sort");
				BigDecimal sort2 = (BigDecimal) o2.get("dtlcd_sort");
				return sort1.compareTo(sort2);
			}
		});

		return resultCodes;
	}

	/**
	 * 계약 생성
	 * @param param
	 * @return
	 */
	public ResultMap createContractByReq(Map param) {
		Map cntrInfo = Maps.newHashMap(param);
		List<Map> vendorList = Lists.newArrayList();

		String cntrdocTypCcd = (String) cntrInfo.get("cntrdoc_typ_ccd");
		String cntrCndId = (String) cntrInfo.get("cntr_cnd_uuid");
		Map cntrCndInfo = this.findCntrCnd(cntrdocTypCcd, cntrCndId);

		// 계약 조건 저장
		if(MapUtils.isNotEmpty(cntrCndInfo)) {
			// 계약 조건 아이디 복사
			String newCntrCndId = this.copyCntrCnd(cntrdocTypCcd, cntrCndId);
			cntrInfo.put("cntr_cnd_uuid", newCntrCndId);

			// 요청 업무의 계약 조건 중 필요한 정보를 계약 정보에 저장
			if(CntrConst.CNTRDOC_TYPE.PO.equals(cntrdocTypCcd) || CntrConst.CNTRDOC_TYPE.UNIT_PRICE.equals(cntrdocTypCcd)) {
				Map purcCntrData = (Map) cntrCndInfo.get("purcCntrData");
				Map purcCntrInfoData = (Map) cntrCndInfo.get("purcCntrInfoData");
				vendorList = (List) cntrCndInfo.get("purcCntrCstmList");

				cntrInfo.put("cntr_st_dt", purcCntrData.get("cntr_st_dt"));
				cntrInfo.put("cntr_exp_dt", purcCntrData.get("cntr_exp_dt"));
				cntrInfo.put("cntr_amt", purcCntrData.get("cntr_amt"));
				cntrInfo.put("sup_amt", purcCntrData.get("sup_amt"));
				cntrInfo.put("cur_ccd", purcCntrData.get("cur_ccd"));
				cntrInfo.put("stax_yn", purcCntrInfoData.get("stax_yn"));

				String staxYn = (String) purcCntrInfoData.get("stax_yn");
				if(!Strings.isNullOrEmpty(staxYn) && "Y".equals(staxYn)) {
					cntrInfo.put("sttpymt_ro_typ_ccd", purcCntrInfoData.get("sttpymt_ro_typ_ccd"));
					cntrInfo.put("sttpymtmeth_ccd", purcCntrInfoData.get("sttpymtmeth_ccd"));
				}
			}
		}

		// 계약 정보 저장
		Map resultData = this.saveContract(cntrInfo, vendorList);

		// 계약 이력 저장
		this.addStatusHistory((String) resultData.get("cntr_uuid"), CntrConst.CNTR_STATUS.CREATION, null);

		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 계약 임시 저장
	 * @param param
	 * @return
	 */
	public ResultMap saveDraftContract(Map param) {
		Map cntrInfo = (Map) param.get("cntrInfo");
		Map cntrCndInfo = (Map) param.get("cntrCndInfo");

		// 계약 조건 저장
		String cntrdocTypCcd = (String) cntrInfo.get("cntrdoc_typ_ccd");
		String cntrCndId = this.saveCntrCnd(cntrdocTypCcd, cntrCndInfo);
		cntrInfo.put("cntr_cnd_uuid", cntrCndId);

		// 계약 정보 저장
		if(cntrCndInfo != null) {
			cntrInfo.put("cntr_amt", cntrCndInfo.get("cntrAmt"));
		}
		Map resultData = this.saveContract(cntrInfo, null);

		// 계약 이력 저장
		boolean isNew = (boolean) resultData.get("isNew");
		if(isNew) {
			this.addStatusHistory((String) resultData.get("cntr_uuid"), CntrConst.CNTR_STATUS.CREATION, null);
		}

		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 계약서 생성
	 * @param param
	 * @return
	 */
	public ResultMap createContractDocument(Map param) {
		Map cntrInfo = (Map) param.get("cntrInfo");
		Map cntrCndInfo = (Map) param.get("cntrCndInfo");

		// 계약 일자
		DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
		Date date = new Date();
		if(cntrInfo.get("cntr_dt") == null) {
			cntrInfo.put("cntr_dt", format.format(date).toString());
		}

		// 계약 조건 저장
		String cntrdocTypCcd = (String) cntrInfo.get("cntrdoc_typ_ccd");
		String cntrCndId = this.saveCntrCnd(cntrdocTypCcd, cntrCndInfo);
		cntrInfo.put("cntr_cnd_uuid", cntrCndId);

		// 계약 정보 저장
		Map resultData = this.saveContract(cntrInfo, null);

		// 계약 이력 저장
		boolean isNew = (boolean) resultData.get("isNew");
		if(isNew) {
			this.addStatusHistory((String) resultData.get("cntr_uuid"), CntrConst.CNTR_STATUS.CREATION, null);
		}

		// 계약 서명방법 별 후처리
		String sgnMethCcd = (String) resultData.get("cntr_sgnmeth_ccd");
		if(CntrConst.SIGN_METHOD.OFFLINE.equals(sgnMethCcd)) {
			//임시 validation 로직 추가 3자 테스트 완료 후 제거 예정 lds
			if(cntrCndInfo != null) {
				Map purcCntrInfoData = (Map) cntrCndInfo.get("purcCntrInfoData");
				if(purcCntrInfoData != null) {
					String sttpymtmethCcd = (String) purcCntrInfoData.get("sttpymtmeth_ccd");
					if(CntrConst.STTPYMT_METHOD.ELEC_STAMP_TAX.equals(sttpymtmethCcd)) {
						return ResultMap.FAIL("서명방법이 오프라인인 경우는 전자수입인지 인지세납부방식을 할 수 없습니다.");
					}
				}
			}

			// 오프라인 계약인 경우 계약 완료 처리
			this.completeContract((String) resultData.get("cntr_uuid"));
		} else {
			// 전자계약 정보 생성
			contractEventPublisher.createEcontract(resultData);
		}

		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 일괄 계약 생성
	 * @param param
	 * @return
	 */
	public ResultMap createContractDocumentList(Map param) {
		List<Map> vendorList = (List) param.get("vendorList");
		Map cntrInfoParam = new HashMap();
		String cntrStsCcd = (String) param.get("cntrStsCcd");
		String batCntrGrpUUID = UUID.randomUUID().toString(); // 일괄 계약 그룹 UUID

		for(Map vendor : vendorList) {
			Map cntrInfo = (Map) param.get("cntrInfo");
			cntrInfo.put("bat_cntr_grp_uuid", batCntrGrpUUID);
			cntrInfoParam.put("cntrInfo", mapMerge(vendor, cntrInfo));

			createContractDocument(cntrInfoParam); // 계약작성

			if(CntrConst.CNTR_STATUS.SEND.equals(cntrStsCcd)) {
				sendContract((Map) cntrInfoParam.get("cntrInfo")); // 발송
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 계약서 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteContractDocument(Map param) {
		contractEventPublisher.deleteEcontract(param);
		contractStatusService.deleteContractDocument(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 계약 조건 조회
	 * @param cntrdocTypCcd
	 * @param cntrCndId
	 * @return
	 */
	public Map findCntrCnd(String cntrdocTypCcd, String cntrCndId) {
		if(Strings.isNullOrEmpty(cntrdocTypCcd) || Strings.isNullOrEmpty(cntrCndId)) {
			return null;
		}

		ContractCndService cndService = contractCndFactory.getModule(cntrdocTypCcd);
		Map cntrCndInfo = Maps.newHashMap();
		if(cndService != null) {
			cntrCndInfo = cndService.getTemplate(cntrCndId);
		}
		return cntrCndInfo;
	}

	/**
	 * 계약 조건 복사
	 * @param cntrdocTypCcd
	 * @param cntrCndId
	 * @return
	 */
	private String copyCntrCnd(String cntrdocTypCcd, String cntrCndId) {
		if(Strings.isNullOrEmpty(cntrdocTypCcd) || Strings.isNullOrEmpty(cntrCndId)) {
			return null;
		}

		ContractCndService cndService = contractCndFactory.getModule(cntrdocTypCcd);
		String newCntrCndId = null;
		if(cndService != null) {
			newCntrCndId = cndService.copy(cntrCndId);
		}
		return newCntrCndId;
	}

	private Map copyCntrCndData(String cntrdocTypCcd, String cntrCndId) {
		if(Strings.isNullOrEmpty(cntrdocTypCcd) || Strings.isNullOrEmpty(cntrCndId)) {
			return null;
		}

		ContractCndService cndService = contractCndFactory.getModule(cntrdocTypCcd);
		Map newCntrCndInfo = null;
		if(cndService != null) {
			newCntrCndInfo = cndService.copyData(cntrCndId);
		}
		return newCntrCndInfo;
	}

	/**
	 * 계약 조건 저장
	 * @param cntrdocTypCcd
	 * @param cntrCndInfo
	 * @return
	 */
	private String saveCntrCnd(String cntrdocTypCcd, Map cntrCndInfo) {
		if(Strings.isNullOrEmpty(cntrdocTypCcd) || MapUtils.isEmpty(cntrCndInfo)) {
			return null;
		}

		ContractCndService cndService = contractCndFactory.getModule(cntrdocTypCcd);
		String cntrCndId = null;
		if(cndService != null) {
			cntrCndId = cndService.save(cntrCndInfo);
		}
		return cntrCndId;
	}

	/**
	 * 계약 조건 삭제
	 * @param cntrdocTypCcd
	 * @param cntrCndId
	 * @return
	 */
	private void deleteCntrCnd(String cntrdocTypCcd, String cntrCndId) {
		if(Strings.isNullOrEmpty(cntrdocTypCcd) || Strings.isNullOrEmpty(cntrCndId)) {
			return;
		}

		ContractCndService cndService = contractCndFactory.getModule(cntrdocTypCcd);
		if(cndService != null) {
			cndService.delete(cntrCndId);
		}
	}

	/**
	 * 계약 이력 추가
	 * @param histInfo
	 * @return
	 */
	public void insertHistory(Map histInfo) {
		histInfo.put("cntr_prgs_histrec_uuid", UUID.randomUUID().toString());

		Date today = new Date();
		long curTimeInMs = today.getTime();
		Date afterAddingMins = new Date(curTimeInMs + 1000);
		histInfo.put("histRegDate", afterAddingMins);

		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		String ip = accountService.getClientIpAddress(request);
		String browser = sharedService.getClientBrowser(request);
		histInfo.put("usr_ip", ip);
		histInfo.put("usr_webbr_kind", browser);

		contractRepository.insertCntrHistory(histInfo);
	}

	/**
	 * 계약 이력 조회
	 * @param param
	 * @return
	 */
	public List findListCntrHistory(Map param) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> historyList = contractRepository.findListCntrHistory(param);

		if(historyList != null) {
			Map<String, Object> cpRow = Maps.newHashMap();
			Map<String, Object> dtRow = Maps.newHashMap();

			//row 정렬 : CP(계약완료) -> DT(계약해지)
			for(Map<String, Object> rowMap : historyList) {
				String progSts = (rowMap.get("cntr_sts_ccd") == null) ? null : rowMap.get("cntr_sts_ccd").toString();

				if(Strings.isNullOrEmpty(progSts)) {
					continue;
				}
				if(CntrConst.CNTR_STATUS.COMPLETE.equals(progSts)) {
					cpRow = rowMap;
				} else if(CntrConst.CNTR_STATUS.TERMINATION.equals(progSts)) {
					dtRow = rowMap;
				} else {
					resultList.add(rowMap);
				}
			}

			if(!cpRow.isEmpty()) {resultList.add(cpRow);}
			if(!dtRow.isEmpty()) {resultList.add(dtRow);}
		}

		return resultList;
	}

	/**
	 * 협력사 부속서류 요청
	 * @param param
	 * @return
	 */
	public ResultMap requestAppxToVendor(Map param) {
		String cntrId = (String) param.get("cntr_uuid");
		String reason = (String) param.get("reason");

		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", cntrId);
		statusParam.put("reason", reason);
		statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.APPX_REQUEST);
		contractStatusService.requestAppxToVendor(statusParam);

		this.addStatusHistory(cntrId, CntrConst.CNTR_STATUS.APPX_REQUEST, reason);
		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사 부속서류 거부
	 * @param param
	 * @return
	 */
	public ResultMap rejectAppxToVendor(Map param) {
		String cntrId = (String) param.get("cntr_uuid");
		String reason = (String) param.get("reason");

		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", cntrId);
		statusParam.put("reason", reason);
		statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.APPX_REJECT);
		contractStatusService.returnAppxToVendor(statusParam);

		this.addStatusHistory(cntrId, CntrConst.CNTR_STATUS.APPX_REJECT, reason);
		return ResultMap.SUCCESS();
	}

	/**
	 * 계약 발신
	 * @param param
	 * @return
	 */
	public ResultMap sendContract(Map param) {
		SignOrder sgnordTypCcd = SignOrder.valueOf((String) param.get("sgnord_typ_ccd"));
		String sgnMethCcd = (String) param.get("cntr_sgnmeth_ccd");
		ResultMap resultMap = ResultMap.getInstance();

		if(SignOrder.BUYER_VD.equals(sgnordTypCcd) && CntrConst.SIGN_METHOD.PKI.equals(sgnMethCcd)) {
			resultMap = contractEventPublisher.signEcontract(param);
		} else {
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		}

		if(resultMap.isSuccess()) {
			String cntrUuid = (String) param.get("cntr_uuid");
			Map statusParam = Maps.newHashMap();
			statusParam.put("cntr_uuid", cntrUuid);
			statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.SEND);
			contractStatusService.send(statusParam);
			this.addStatusHistory(cntrUuid, CntrConst.CNTR_STATUS.SEND, null);
		}

		return resultMap;
	}

	/**
	 * 계약 완료
	 * @param cntrId
	 */
	public void completeContract(String cntrId) {
		// 상태 업데이트
		Map statusParam = Maps.newHashMap();
		statusParam.put("cntr_uuid", cntrId);
		statusParam.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.COMPLETE);
		contractStatusService.completeContract(statusParam);
		// 이력 추가
		this.addStatusHistory(cntrId, CntrConst.CNTR_STATUS.COMPLETE, null);
		// 후속 프로세스
		contractNxtPrcsService.completeContract(cntrId);
	}

	/**
	 * 계약 상태 업데이트 및 이력 추가
	 * @param cntrId
	 * @param cntrStsCcd
	 * @param reason
	 */
	public void addStatusHistory(String cntrId, String cntrStsCcd, String reason) {
		Map saveParam = Maps.newHashMap();
		saveParam.put("cntr_uuid", cntrId);
		saveParam.put("cntr_sts_ccd", cntrStsCcd);
		saveParam.put("cntr_ret_rsn", reason);
		this.insertHistory(saveParam);
	}

	/**
	 * 계약자 목록 조회
	 * @param param
	 * @return
	 */
	public List findListContractor(Map param) {
		return contractRepository.findListContractor(param);
	}

	/**
	 * 계약 대상 협력사 목록 조회
	 * @param param
	 * @return
	 */
	public List findListCntrVendor(Map param) {
		return contractRepository.findListCntrVendor(param);
	}

	/**
	 * Docusign Template 생성
	 * @param param
	 * @return
	 */
	public ResultMap createDocusignTemplate(Map param) {
		return contractEventPublisher.createDocusignTemplate(param);
	}

	/**
	 * Docusign Template 조회
	 * @param param
	 * @return
	 */
	public ResultMap findDocusignTemplate(Map param) {
		return contractEventPublisher.findDocusignTemplate(param);
	}

	/**
	 * Docusign Template 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteDocusignTemplate(Map param) {
		return contractEventPublisher.deleteDocusignTemplate(param);
	}

	/**
	 * Docusign Envelope 생성
	 * @param param
	 * @return
	 */
	public ResultMap createDocusignEnvelope(Map param) {
		return contractEventPublisher.createDocusignEnvelope(param);
	}

	/**
	 * Docusign Envelope 조회
	 * @param param
	 * @return
	 */
	public ResultMap findDocusignEnvelope(Map param) {
		return contractEventPublisher.findDocusignEnvelope(param);
	}

	/**
	 * Docusign Envelope 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteDocusignEnvelope(Map param) {
		return contractEventPublisher.deleteDocusignEnvelope(param);
	}

	/**
	 * 간편 서명 Template 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteEFormTemplate(Map param) {
		return contractEventPublisher.deleteEFormTemplate(param);
	}

	/**
	 * AdobeSign 계약서 생성
	 * @param param
	 * @return
	 */
	public ResultMap onCreateAdobeSign(Map param) {
		return contractEventPublisher.onCreateAdobeSign(param);
	}

	/**
	 * AdobeSign 계약서 재생성
	 * @param param
	 * @return
	 */
	public ResultMap onReCreateAdobeSign(Map param) {
		return contractEventPublisher.onReCreateAdobeSign(param);
	}

	/**
	 * AdobeSign 계약서 보기
	 * @param param
	 * @return
	 */
	public ResultMap getAdobeSignUrlInfo(Map param) {
		return contractEventPublisher.getAdobeSignUrlInfo(param);
	}

	/**
	 * AdobeSign 진행상태 체크
	 * @param param
	 * @return
	 */
	public ResultMap checkAdobeSignStatus(Map param) {
		return contractEventPublisher.checkAdobeSignStatus(param);
	}

	/**
	 * AdobeSign 계약서 삭제
	 * @param param
	 * @return
	 */
	public ResultMap onDeleteAdobeSign(Map param) {
		return contractEventPublisher.onDeleteAdobeSign(param);
	}

	/**
	 * AdobeSign 구매사 서명 url 조회
	 * @param param
	 * @return
	 */
	public ResultMap getBpAdobeSignUrlInfo(Map param) {
		return contractEventPublisher.getBpAdobeSignUrlInfo(param);
	}

	/**
	 * 계약 해지
	 * @param param
	 * @return
	 */
	public ResultMap terminateContract(Map param) {
		contractStatusService.terminationContract(param);
		contractNxtPrcsService.terminationContract(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 계약 해지 프로세스 셋팅 조회
	 * @return
	 */
	public boolean getTerminationProcessSetup() {
		Map codeParam = Maps.newHashMap();
		codeParam.put("cstr_cnd_cd", "TRMN_PRCS_YN"); // 계약 해지 프로세스 사용 여부
		codeParam.put("ccd", CntrConst.CODE.USE_YN_COMMON_CODE);

		List<Map<String,Object>> ccdList = sharedService.findListCommonCodeAttributeCode(codeParam);
		Map ccd = (Map)ccdList.get(0);
		String cstrCndVal = (String)ccd.get("cstr_cnd_val");
		if("Y".equals(cstrCndVal)) {
			return true;
		}else if("N".equals(cstrCndVal)) {
			return false;
		}else{
			LOG.error("class : " + this.getClass().toString() + "Invalid TRMN_PRCS_YN property value for D001 common code.");
			throw new CommonException(this.getClass().toString() + "Invalid TRMN_PRCS_YN property value for D001 common code.");
		}
	}

	/**
	 * 검토 완료된 계약 작성
	 *
	 * @param param
	 * @return
	 */
	public ResultMap saveContractByLawReview(Map param) {
		ResultMap result = this.createContractDocument(param);
		Map cntrInfo = result.getResultData();

		Map resultData = Maps.newHashMap();
		resultData.put("cntr_uuid", cntrInfo.get("cntr_uuid"));
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 계약 회수
	 * @param param
	 * @return
	 */
	public ResultMap withdrawalContractDoc(Map param) {
		// 계약 회수 가능여부 check
		String withdrawalPossYn = this.checkWithdrawalPossYn(param);

		if (CntrConst.SGN_LCKD_STS.SIGNABLE.equals(withdrawalPossYn)) {
			// 1. 계약서 회수 전 선작업
			this.withdrawalBeforeProcess(param);

			// 2. 업무 결재 연결해제
			contractRepository.updateTaskApvlUseYn(param);

			// 3. 회수 처리(계약상태 작성완료 상태로 변경)
			contractStatusService.withdrawalContract(param);
			return ResultMap.SUCCESS();
		} else if(CntrConst.SGN_LCKD_STS.SIGNED_LOCK.equals(withdrawalPossYn)) {
			// 서명 잠금 상태
			return ResultMap.FAIL(withdrawalPossYn);
	    } else {
			return ResultMap.FAIL();
		}
	}

	/**
	 * 계약서 회수 가능여부 체크
	 * @param param
	 * @return
	 */
	private String checkWithdrawalPossYn(Map param) {
		// 1. 계약 진행상태 check
		String possYnByCntrSts = contractRepository.getContractDocWithdrawalPossYn(param);

		if(CntrConst.SGN_LCKD_STS.SIGNABLE.equals(possYnByCntrSts)) {
			// 2. 서명 잠금상태 check
			String possYnByLckdSts = contractEventPublisher.findWdPossYnByLckdSts(param);
			if(CntrConst.SGN_LCKD_STS.SIGNABLE.equals(possYnByLckdSts)) {
				return CntrConst.SGN_LCKD_STS.SIGNABLE;
			} else {
				return CntrConst.SGN_LCKD_STS.SIGNED_LOCK;
			}
		}

		return CntrConst.SGN_LCKD_STS.SOMEONE_ELSE_SIGN_USING;
	}

	private void withdrawalBeforeProcess(Map param) {
		contractEventPublisher.withdrawalEcontract(param);
	}

	private Map<String, Object> mapMerge(Map<String, Object> resultMap, Map<String, Object> valueMap) {
		Iterator<String> keys = valueMap.keySet().iterator();

		while (keys.hasNext()) {
			String key = keys.next();
			//MyBatis Number타입은 BigDecimal 타입으로 반환되기 오류가 발생할수도있음 - ex)Sybase 에서 integer 값이 BigDecimal 로 넘어오면서 DB 가 끈기는 현상이 발생
			String value="";
			if(valueMap.get(key) != null) {
				value = valueMap.get(key).toString();
			}
			//String value = String.valueOf(valueMap.get(key));
			if(Strings.isNullOrEmpty(value) || "null".equals(value)){
				value = "";
			}
			resultMap.put(key, value);
		}

		return resultMap;
	}

	/**
	 * 일괄 계약 작성 > 업체 검증
	 * @param param
	 * @return
	 */
	public ResultMap validateCompanyList(Map param) {
		List<Map> vendorList = (List) param.get("vendorList");

		List<Map> notExistVendorList = new ArrayList<>();
		List<Map> existVendorList = new ArrayList<>();

		for(Map vendor : vendorList) {
			vendor.put("erp_vd_cd", vendor.get("erp_vd_cd").toString());
			String isExist = contractRepository.validateCompanyInfo(vendor);
			if("N".equals(isExist)) {
				notExistVendorList.add(vendor);
			} else {
				existVendorList.add(vendor);
			}
		}

		Map resultData = Maps.newHashMap();
		resultData.put("notExistVendorList", notExistVendorList);
		resultData.put("existVendorList", existVendorList);
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 계약 일괄 다운로드
	 * @param param
	 * @return
	 */
	public void downloadAllCntr(HttpServletRequest request, HttpServletResponse response, String cntrUUIDs) {
		String[] cntrList = cntrUUIDs.split(",");
		File attFile = contractEventPublisher.downloadAllCntr(cntrList);

		OutputStream ops = null;
		FileInputStream fis = null;
		byte[] pdfFileBytes;

		try {
			fis = new FileInputStream(attFile);
			pdfFileBytes = IOUtils.toByteArray(fis);

			response.setContentType("application/octet-stream; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ URLEncoder.encode("contract_list.zip", "UTF-8").replaceAll("\\+", "%20"));
			ops = response.getOutputStream();
			ops.write(pdfFileBytes);
			ops.flush();

		} catch (IOException e) {
			LOG.error("class : " + this.getClass().toString() + " downloadAllCntr error:" + e.toString());
			response.setHeader("Set-Cookie", "fileDownload=false; path=/");
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Content-Type","text/html; charset=utf-8");
			throw new CommonException(this.getClass().getName()+".downloadAllCntr : " + e.getMessage(), e.toString());
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (ops != null)
					ops.close();
				if( attFile != null)
					attFile.delete();
			} catch (Exception e) {
				LOG.error("class : " + this.getClass().toString() + " downloadAllCntr error:" + e.toString());
				throw new CommonException(this.getClass().getName()+".downloadAllCntr : " + e.getMessage(), e.toString());
			}
		}
	}
}