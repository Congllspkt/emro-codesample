package smartsuite.app.bp.vendorMaster.vendorReg.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.vendorMaster.vendorInfo.service.VendorMasterService;
import smartsuite.app.bp.vendorMaster.vendorReg.event.VendorRegEventPublisher;
import smartsuite.app.bp.vendorMaster.vendorReg.repository.VendorRegRepository;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.security.authentication.PasswordGenerator;
import smartsuite.security.authentication.ProxyPasswordEncoder;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class VendorRegService {

	@Inject
	private VendorRegRepository vendorRegRepository;

	@Inject
	VendorMasterService vendorMasterService;

	@Inject
	SharedService sharedService;

	@Inject
	private VendorRegEventPublisher vendorRegEventPublisher;

	@Inject
	MailService mailService;

	@Inject
	PasswordGenerator passwordGenerator;

	/**
	 * 중복체크.
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    public List checkDuplicatedVdInfo(Map param) {
        return vendorRegRepository.checkDuplicatedVdInfo(param);
    }

	/**
	 * 협력사 신규 등록 저장
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	public ResultMap saveBasicVdInfo(Map param) {
		String vdCd = sharedService.generateDocumentNumber("VA");
		ProxyPasswordEncoder pe = new ProxyPasswordEncoder("SHA-512");
		String tempPassword = passwordGenerator.generate();
		param.put("vd_cd", vdCd);

		param.put("pwd", pe.encode(tempPassword)); // 패스워드 난수로 암호화
		param.put("eml", param.get("vd_eml")); // 계정 email = 담당자 이메일

		// 본사사업자 번호 없을 시 사업자번호로 기본 셋팅
		if(param.get("hq_bizregno") == null || "".equals(param.get("hq_bizregno"))){
			param.put("hq_bizregno", param.get("bizregno"));
		}
		vendorRegRepository.insertVdInfo(param);                   // VD - 협력사 마스터
		vendorRegRepository.insertUserInfo(param);                 // ESAUSER - 사용자 정보
		vendorRegRepository.insertUserAuth(param);                 // ESAAURP - 사용자 롤

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

		Map<String, Object> detailInfo = vendorRegRepository.findDetailRegVdInfo(param);
		List<Map<String, Object>> athfList = vendorRegRepository.findVendorAttachmentList(param);

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
		Map<String, Object> detailInfo = (Map<String, Object>) param.get("detailInfo");
		List<Map<String, Object>> athfList = (List<Map<String, Object>>)param.get("athfList");

		if(!detailInfo.isEmpty()){
			vendorRegRepository.updateBasicInfoMaster(detailInfo);                // VD - 협력사 마스터 수정
		}

		// 첨부파일
		if(athfList != null && !athfList.isEmpty()){
			for (Map<String, Object> athfInfo : athfList){
				if(athfInfo.get("vd_athf_uuid") == null || "".equals(athfInfo.get("vd_athf_uuid"))){
					String vdAthfUuid = UUID.randomUUID().toString();
					athfInfo.put("vd_athf_uuid", vdAthfUuid);
					athfInfo.put("use_yn", "Y");
					vendorRegRepository.insertVendorAttachmentInfo(athfInfo);
				} else {
					athfInfo.put("sts", "U");
					vendorRegRepository.updateVendorAttachmentInfo(athfInfo);
				}
			}
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 협력사 재무정보 조회
	 *
	 * @param Param the param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	public List findVendorFinanceInfo(Map param) {
		return vendorRegRepository.findVendorFinanceInfo(param);
	}

	/**
	 * 협력사 재무정보 저장
	 *
	 * @param Param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	public ResultMap saveVendorFinanceInfo(Map param) {
		if (!param.isEmpty()) {
			List<Map> commonCodeList = sharedService.findCommonCode("E067");

			String vdFnUuid = (param.get("vd_fn_uuid") == null) ? null : (String)param.get("vd_fn_uuid");

			if(Strings.isNullOrEmpty(vdFnUuid)) {
				for(Map<String, String> row1: commonCodeList) {
					vdFnUuid = (String) UUID.randomUUID().toString();
					param.put("vd_fn_uuid", vdFnUuid);
					param.put("fn_acct_typ_ccd", row1.get("data").toLowerCase());
					param.put("fn_acct_typ_val", param.get(row1.get("data").toLowerCase()));
					vendorRegRepository.insertVendorFinanceInfo(param);
				}

				// 신용평가 등급, 현금흐름 등급, WATCH 등급은 공통코드 X
				vdFnUuid = (String)UUID.randomUUID().toString();
				param.put("vd_fn_uuid", vdFnUuid);
				param.put("fn_acct_typ_ccd", "crrat_grd");
				param.put("fn_acct_typ_val", param.get("crrat_grd"));
				vendorRegRepository.insertVendorFinanceInfo(param);

				vdFnUuid = (String)UUID.randomUUID().toString();
				param.put("vd_fn_uuid", vdFnUuid);
				param.put("fn_acct_typ_ccd", "cashfw_grd");
				param.put("fn_acct_typ_val", param.get("cashfw_grd"));
				vendorRegRepository.insertVendorFinanceInfo(param);

				vdFnUuid = (String)UUID.randomUUID().toString();
				param.put("vd_fn_uuid", vdFnUuid);
				param.put("fn_acct_typ_ccd", "wtch_grd");
				param.put("fn_acct_typ_val", param.get("wtch_grd"));
				vendorRegRepository.insertVendorFinanceInfo(param);

			} else {
				for(Map<String, String> row1: commonCodeList) {
					param.put("fn_acct_typ_ccd", row1.get("data").toLowerCase());
					param.put("fn_acct_typ_val", param.get(row1.get("data").toLowerCase()));
					vendorRegRepository.updateVendorFinanceInfo(param);
				}

				// 신용평가 등급, 현금흐름 등급, WATCH 등급은 공통코드 X
				param.put("fn_acct_typ_ccd", "crrat_grd");
				param.put("fn_acct_typ_val", param.get("crrat_grd"));
				vendorRegRepository.updateVendorFinanceInfo(param);

				param.put("fn_acct_typ_ccd", "cashfw_grd");
				param.put("fn_acct_typ_val", param.get("cashfw_grd"));
				vendorRegRepository.updateVendorFinanceInfo(param);

				param.put("fn_acct_typ_ccd", "wtch_grd");
				param.put("fn_acct_typ_val", param.get("wtch_grd"));
				vendorRegRepository.updateVendorFinanceInfo(param);
			}
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 온보딩평가요청 - 등록요청을 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 15
	 * @author yjPark
	 */
	public ResultMap saveListReqOnboardingEval(Map param) {
		ResultMap resultMap = vendorMasterService.saveListReqOnboardingEval(param);
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			Map<String, Object> reqOnboardingEvalTarg = (Map<String, Object>) resultMap.getResultData();
			List evalTrVdList = vendorRegEventPublisher.findListOePrcsEvaltrForSessionUser(reqOnboardingEvalTarg);
			resultMap.setResultList(evalTrVdList);
    	}
		return resultMap;
	}

	/**
	 * 온보딩평가수행 항목 정보 조회를 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 1
	 * @author yjPark
	 */
	public ResultMap findOnboardingEvalfactFulfillInfo(Map param) {
		return vendorRegEventPublisher.findOnboardingEvalfactFulfillInfo(param);
	}

	/**
	 * 온보딩평가수행 정보 저장을 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 13
	 * @author yjPark
	 */
	public ResultMap saveOnboardingEvalFulfillment(Map param) {
		return vendorRegEventPublisher.saveOnboardingEvalFulfillment(param);
	}
    
    /**
     * 협력사 운영 조직 저장
     * @param param
     * @return
     */
    public ResultMap saveVdOorgCd(Map param) {
        return vendorMasterService.saveVdOorgCd(param);
    }
    
    /**
     * 신규 협력사, 운영조직 저장
     * @param param
     * @return
     */
    public ResultMap saveNewVdOorg(Map param) {
        //신규생성
        String vdCd = sharedService.generateDocumentNumber("VA");
        ProxyPasswordEncoder pe = new ProxyPasswordEncoder("SHA-512");
        String tempPassword = passwordGenerator.generate();
        param.put("vd_cd", vdCd);

        param.put("pwd", pe.encode(tempPassword)); // 패스워드 난수로 암호화
        param.put("eml", param.get("vd_eml")); // 계정 email = 담당자 이메일

        // 본사사업자 번호 없을 시 사업자번호로 기본 셋팅
        if(param.get("hq_bizregno") == null || "".equals(param.get("hq_bizregno"))){
            param.put("hq_bizregno", param.get("bizregno"));
        }
        vendorRegRepository.insertVdInfo(param);                   // VD - 협력사 마스터
        vendorRegRepository.insertUserInfo(param);                 // ESAUSER - 사용자 정보
        vendorRegRepository.insertUserAuth(param);                 // ESAAURP - 사용자 롤
        
        //운영조직 생성
        this.saveVdOorgCd(param);
        //성공한 데이터 전달
        return ResultMap.SUCCESS();
    }
    
}
