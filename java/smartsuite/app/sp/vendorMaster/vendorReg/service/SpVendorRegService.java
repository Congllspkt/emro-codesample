package smartsuite.app.sp.vendorMaster.vendorReg.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import smartsuite.app.common.mail.CommonMailService;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.sp.vendorMaster.vendorInfo.service.SpVendorMasterService;
import smartsuite.app.sp.vendorMaster.vendorReg.event.SpVendorRegEventPublisher;
import smartsuite.app.sp.vendorMaster.vendorReg.repository.SpVendorRegRepository;
import smartsuite.security.authentication.Auth;
import smartsuite.security.authentication.PasswordGenerator;
import smartsuite.security.authentication.ProxyPasswordEncoder;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SpVendorRegService {

	@Inject
	private SpVendorRegRepository spVendorRegRepository;

	@Inject
	SpVendorMasterService spVendorMasterService;

	@Inject
    private SharedService sharedService;

	@Inject
	private SpVendorRegEventPublisher spVendorRegEventPublisher;

	@Inject
	MailService mailService;

	@Inject
	PasswordGenerator passwordGenerator;

	/**
	 * 로그인한 유저정보 비교
	 *
	 * @param Param the param
	 */
	public ResultMap checkCurrentUserByVdCd(Map<String, Object> param){
		Map<String, Object> user = Auth.getCurrentUserInfo();
		if(user.get("usr_id").equals(param.get("vd_cd"))){
			return ResultMap.SUCCESS();
		}else{
			return ResultMap.FAIL();
		}
	}
	
	/**
	 * 중복체크
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    public List checkDuplicatedVdInfo(Map param) {
        return spVendorRegRepository.checkDuplicatedVdInfo(param);
    }
    
    /**
	 * 약관 정보 조회(현재일자에 유효한 약관)
     *
     * @param param
     * @Date : 2023. 05. 31
     * @author sykim
     */
	public List findTermsList(Map param) {
		return spVendorRegRepository.findTermsList(param);
	}
	
   /**
	* 협력사 신규 등록 저장
    *
    * @param param
    * @Date : 2023. 05. 31
    * @author sykim
    */
    public ResultMap saveBasicVdInfo(Map<String, Object> param) {
        String tenant = param.get("tenant").toString();
        String vdCd = sharedService.generateDocumentNumberByTenant("VA", tenant);
        ProxyPasswordEncoder pe = new ProxyPasswordEncoder("SHA-512");
        String tempPassword = passwordGenerator.generate();
		param.put("vd_cd", vdCd);
        
        param.put("vd_cd", vdCd);
        param.put("pwd", pe.encode(tempPassword)); // 패스워드 1111로 암호화
        param.put("eml", param.get("vd_eml")); // 계정 email = 담당자 이메일

        // 본사사업자 번호 없을 시 사업자번호로 기본 셋팅
        if(param.get("hq_bizregno") == null || "".equals(param.get("hq_bizregno"))){
        	param.put("hq_bizregno", param.get("bizregno"));
        }
	    spVendorRegRepository.insertVdInfo(param);                   // ESMVDGL - 협력사 마스터
	    spVendorRegRepository.insertUserInfo(param);                 // ESAUSER - 사용자 정보
	    spVendorRegRepository.insertUserAuth(param);                 // ESAAURP - 사용자 롤

        // 담당자 약관 동의 저장
        List<Map<String, Object>> termsList = (List<Map<String, Object>>) param.get("termsList");
        if(!termsList.isEmpty()){
        	for(Map<String, Object> termsInfo : termsList){
        		String termcndCnstUuid = UUID.randomUUID().toString();
        		termsInfo.put("termcnd_cnst_uuid", termcndCnstUuid);
        		termsInfo.put("termcnd_cnst_usr_uuid", vdCd);
        		termsInfo.put("tenant", tenant);
        		termsInfo.put("login_ip", param.get("login_ip"));
		        spVendorRegRepository.insertTermcndHist(termsInfo); // TERMCND_CNST_HISTREC 약관동의이력
        	}
        }
        
        // 메일 발송
	    Map<String, Object> mailParam = new HashMap<String, Object>();

	    mailParam.put("tempPw", tempPassword);
	    mailParam.put("usr_id", param.get("vd_cd"));
	    mailParam.put("usr_nm", param.get("vd_nm"));
	    mailParam.put("eml", param.get("eml"));

	    mailService.sendAsync("NEW_USER", null, mailParam);

	    //resultData 셋팅
	    Map resultDataMap = Maps.newHashMap();
	    resultDataMap.put("vd_cd", vdCd);
	    return ResultMap.SUCCESS(resultDataMap);
    }
    
    /**
	* 협력사 주요정보 조회
	*
	* @param param
    * @Date : 2023. 05. 31
    * @author sykim
	*/
	public Map findDetailRegVdInfo(Map param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		Map<String, Object> detailInfo = spVendorRegRepository.findDetailRegVdInfo(param);
		List<Map<String, Object>> athfList = spVendorRegRepository.findVendorAttachmentList(param);

		resultMap.put("detailInfo", detailInfo);
		resultMap.put("athfList", athfList);

		return resultMap;
	}
	
    /**
	* 협력사 주요정보 저장
	*
	* @param Param
    * @Date : 2023. 05. 31
    * @author sykim
	*/
	public ResultMap saveDetailVdInfo(Map param) {
		// 로그인한 유저정보 비교
		ResultMap checkResultDataMap = checkCurrentUserByVdCd((Map<String, Object>)param.get("vdInfo"));

		if (Const.SUCCESS.equals(checkResultDataMap.getResultStatus())) {
			Map<String, Object> detailInfo = (Map<String, Object>) param.get("detailInfo");
			List<Map<String, Object>> athfList = (List<Map<String, Object>>)param.get("athfList");

			if(!detailInfo.isEmpty()){
				spVendorRegRepository.updateBasicInfoMaster(detailInfo);                   // VD - 협력사 마스터 수정
			}

			// 첨부파일
			if(athfList != null && !athfList.isEmpty()){
				for (Map<String, Object> athfInfo : athfList){
					if(athfInfo.get("vd_athf_uuid") == null || "".equals(athfInfo.get("vd_athf_uuid"))){
						String vdAthfUuid = UUID.randomUUID().toString();
						athfInfo.put("vd_athf_uuid", vdAthfUuid);
						athfInfo.put("use_yn", "Y");
						spVendorRegRepository.insertVendorAttachmentInfo(athfInfo);
					} else {
						athfInfo.put("sts", "U");
						spVendorRegRepository.updateVendorAttachmentInfo(athfInfo);
					}
				}
			}

			//resultData 셋팅
			Map resultDataMap = Maps.newHashMap();
			return ResultMap.SUCCESS(resultDataMap);
		}

		return checkResultDataMap;
	}

	/**
	 * 협력사 재무정보 조회
	 *
	 * @param Param the param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	public Map findVendorFinanceInfo(Map param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		List<Map<String, Object>> financeInfoList = spVendorRegRepository.findVendorFinanceInfo(param);
		List<Map<String, Object>> bnkAcctList = spVendorRegRepository.findVendorBankAccountInfo(param);

		resultMap.put("financeInfoList", financeInfoList);
		resultMap.put("bnkAcctList", bnkAcctList);

		return resultMap;
	}

	/**
	 * 협력사 재무정보 저장
	 *
	 * @param Param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	public ResultMap saveVendorFinanceInfo(Map param) {
		// 로그인한 유저정보 비교
		ResultMap checkResultDataMap = checkCurrentUserByVdCd((Map<String, Object>)param.get("vdInfo"));

		if (Const.SUCCESS.equals(checkResultDataMap.getResultStatus())) {
			// 계좌정보 저장
			if (param.containsKey("bnkAcctInfo")) {
				Map<String, Object> bnkAcctInfo = (Map<String, Object>) param.get("bnkAcctInfo");
				if (bnkAcctInfo != null && !bnkAcctInfo.isEmpty()) {
					List<Map<String, Object>> inserts = (List<Map<String, Object>>)bnkAcctInfo.get("insertList");
					List<Map<String, Object>> updates = (List<Map<String, Object>>)bnkAcctInfo.get("updateList");
					List<Map<String, Object>> deletes = (List<Map<String, Object>>)bnkAcctInfo.get("deleteList");

					String vdBnkAcctUuid = "";

					// 신규 저장
					for(Map<String, Object> row : inserts){
						// uuid 채번
						vdBnkAcctUuid = UUID.randomUUID().toString();
						row.put("vd_bnkacct_uuid", vdBnkAcctUuid);
						spVendorRegRepository.insertVendorBankAccountInfo(row);
					}

					// 수정
					for(Map<String, Object> row : updates){
						spVendorRegRepository.updateVendorBankAccountInfo(row);
					}

					// 삭제
					for (Map<String, Object> row : deletes) {
						spVendorRegRepository.deleteVendorBankAccountInfo(row);
					}
				}
			}

			// 재무정보 저장
			if (param.containsKey("financeInfo")) {
				List<Map> commonCodeList = sharedService.findCommonCode("E067");
				Map<String, Object> financeInfo = (Map<String, Object>) param.get("financeInfo");
				String vdFnUuid = (financeInfo.get("vd_fn_uuid") == null) ? null : (String)financeInfo.get("vd_fn_uuid");

				if(Strings.isNullOrEmpty(vdFnUuid)) {
					for(Map<String, String> row1: commonCodeList) {
						vdFnUuid = (String) UUID.randomUUID().toString();
						financeInfo.put("vd_fn_uuid", vdFnUuid);
						financeInfo.put("fn_acct_typ_ccd", row1.get("data").toLowerCase());
						financeInfo.put("fn_acct_typ_val", financeInfo.get(row1.get("data").toLowerCase()));
						spVendorRegRepository.insertVendorFinanceInfo(financeInfo);
					}

					// 신용평가 등급, 현금흐름 등급, WATCH 등급은 공통코드 X
					vdFnUuid = (String)UUID.randomUUID().toString();
					financeInfo.put("vd_fn_uuid", vdFnUuid);
					financeInfo.put("fn_acct_typ_ccd", "crrat_grd");
					financeInfo.put("fn_acct_typ_val", financeInfo.get("crrat_grd"));
					spVendorRegRepository.insertVendorFinanceInfo(financeInfo);

					vdFnUuid = (String)UUID.randomUUID().toString();
					financeInfo.put("vd_fn_uuid", vdFnUuid);
					financeInfo.put("fn_acct_typ_ccd", "cashfw_grd");
					financeInfo.put("fn_acct_typ_val", financeInfo.get("cashfw_grd"));
					spVendorRegRepository.insertVendorFinanceInfo(financeInfo);

					vdFnUuid = (String)UUID.randomUUID().toString();
					financeInfo.put("vd_fn_uuid", vdFnUuid);
					financeInfo.put("fn_acct_typ_ccd", "wtch_grd");
					financeInfo.put("fn_acct_typ_val", financeInfo.get("wtch_grd"));
					spVendorRegRepository.insertVendorFinanceInfo(financeInfo);

				} else {
					for(Map<String, String> row1: commonCodeList) {
						financeInfo.put("fn_acct_typ_ccd", row1.get("data").toLowerCase());
						financeInfo.put("fn_acct_typ_val", financeInfo.get(row1.get("data").toLowerCase()));
						spVendorRegRepository.updateVendorFinanceInfo(financeInfo);
					}

					// 신용평가 등급, 현금흐름 등급, WATCH 등급은 공통코드 X
					financeInfo.put("fn_acct_typ_ccd", "crrat_grd");
					financeInfo.put("fn_acct_typ_val", financeInfo.get("crrat_grd"));
					spVendorRegRepository.updateVendorFinanceInfo(financeInfo);

					financeInfo.put("fn_acct_typ_ccd", "cashfw_grd");
					financeInfo.put("fn_acct_typ_val", financeInfo.get("cashfw_grd"));
					spVendorRegRepository.updateVendorFinanceInfo(financeInfo);

					financeInfo.put("fn_acct_typ_ccd", "wtch_grd");
					financeInfo.put("fn_acct_typ_val", financeInfo.get("wtch_grd"));
					spVendorRegRepository.updateVendorFinanceInfo(financeInfo);
				}
			}

			//resultData 셋팅
			Map resultDataMap = Maps.newHashMap();
			return ResultMap.SUCCESS(resultDataMap);
		}

		return checkResultDataMap;
	}

	/**
	 * 온보딩평가요청 - 등록요청을 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 15
	 * @author yjPark
	 */
	public ResultMap saveListReqOnboardingEval(Map param) {
		// 로그인한 유저정보 비교
		ResultMap checkResultDataMap = checkCurrentUserByVdCd((Map<String, Object>)param.get("vdInfo"));

		if (Const.SUCCESS.equals(checkResultDataMap.getResultStatus())) {
			ResultMap resultMap = spVendorMasterService.saveListReqOnboardingEval(param);
			if (ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
				Map<String, Object> reqOnboardingEvalTarg = (Map<String, Object>) resultMap.getResultData();
				List evalTrVdList = spVendorRegEventPublisher.findListOePrcsEvaltrForSessionUser(reqOnboardingEvalTarg);
				resultMap.setResultList(evalTrVdList);
			}
			return resultMap;
		}

		return checkResultDataMap;
	}

	/**
	 * 온보딩평가요청 - 등록취소를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 15
	 * @author yjPark
	 */
	public ResultMap saveListReqOnboardingEvalCancel(Map param) {
		// 로그인한 유저정보 비교
		ResultMap checkResultDataMap = checkCurrentUserByVdCd((Map<String, Object>)param.get("vdInfo"));
		if (Const.SUCCESS.equals(checkResultDataMap.getResultStatus())) {
			return spVendorMasterService.saveListReqOnboardingEvalCancel(param);
		}
		return checkResultDataMap;
	}

	/**
	 * 온보딩평가수행 항목 정보 조회를 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 1
	 * @author yjPark
	 */
	public ResultMap findOnboardingEvalfactFulfillInfo(Map param) {
		return spVendorRegEventPublisher.findOnboardingEvalfactFulfillInfo(param);
	}

	/**
	 * 협력사 온보딩평가수행 정보 저장
	 *
	 * @param Param
	 * @Date : 2023. 07. 20
	 * @author sykim
	 */
	public ResultMap saveOnboardingEvalFulfillment(Map param) {
		// 로그인한 유저정보 비교
		ResultMap checkResultDataMap = checkCurrentUserByVdCd((Map<String, Object>)param.get("vdInfo"));
		if (Const.SUCCESS.equals(checkResultDataMap.getResultStatus())) {
			return spVendorRegEventPublisher.saveSpOnboardingEvalFulfillment(param);
		}
		return checkResultDataMap;
	}
}
