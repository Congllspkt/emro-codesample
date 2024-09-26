package smartsuite.app.bp.vendorMaster.vendorInfo.service;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.document.service.ApprovalDocumentService;
import smartsuite.app.bp.vendorMaster.vendorInfo.event.VendorInfoEventPublisher;
import smartsuite.app.bp.vendorMaster.vendorInfo.repository.VendorInfoRepository;
import smartsuite.app.bp.admin.template.service.TemplateService;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.util.ListUtils;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class VendorInfoService {

	static final Logger LOG = LoggerFactory.getLogger(ApprovalDocumentService.class);

	@Inject
	private VendorInfoRepository vendorInfoRepository;
    @Inject
	private TemplateService templateService;
	@Inject
	private VendorInfoEventPublisher vendorInfoEventPublisher;
	@Inject
	MailService mailService;

	@Inject
	private transient SharedService sharedService;

    private final SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

	/**
	 * 공장정보를 조회한다. (findListPlantInfo)
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	public List<Map<String, Object>> findListVdFatyInfo(Map<String, Object> param) {
		return vendorInfoRepository.findListVdFatyInfo(param);
	}

	/**
	 * 공장정보를 저장한다. (saveListPlantInfo)
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	public ResultMap saveListVdFatyInfo(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		int fatySeqNo = 0;

		// 신규 저장
		for(Map<String, Object> row : inserts){
			String vdFatyUuid = (String)UUID.randomUUID().toString(); // UUID 채번
			fatySeqNo = vendorInfoRepository.findVdFatyMaxSeq(row); // seq 조회
			row.put("vd_faty_uuid", vdFatyUuid);
			row.put("faty_seqno", fatySeqNo);
			vendorInfoRepository.insertListVdFatyInfo(row);
		}

		// 수정
		for(Map<String, Object> row : updates){
			vendorInfoRepository.updateListVdFatyInfo(row);
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 공장정보를 삭제한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	public ResultMap deleteListVdFatyInfo(Map<String,Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedPlantInfo");

		for (Map<String, Object> row : deletes) {
			vendorInfoRepository.deleteListVdFacInfoByFatyUuid(row);    // 설비 삭제
			vendorInfoRepository.deleteListVdFatyInfo(row);             // 공장 삭제
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 설비정보를 조회한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	public List<Map<String, Object>> findListVdFacInfo(Map<String, Object> param) {
		return vendorInfoRepository.findListVdFacInfo(param);
	}

	/**
	 * 설비정보를 저장한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	public ResultMap saveListVdFacInfo(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		int facSeqNo = 0;

		// 신규 저장
		for(Map<String, Object> row : inserts){
			String vdFacUuid = (String)UUID.randomUUID().toString(); // UUID 채번
			facSeqNo = vendorInfoRepository.findVdFacMaxSeq(row); // seq 조회
			row.put("vd_fac_uuid", vdFacUuid);
			row.put("fac_seqno", facSeqNo);
			vendorInfoRepository.insertListVdFacInfo(row);
		}

		// 수정
		for(Map<String, Object> row : updates){
			vendorInfoRepository.updateListVdFacInfo(row);
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 설비정보를 삭제한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 02
	 * @author sykim
	 */
	public ResultMap deleteListVdFacInfo(Map<String,Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedEquipmentsInfo");

		for (Map<String, Object> row : deletes) {
			vendorInfoRepository.deleteListVdFacInfo(row);  // 설비 삭제 (deleteEquipmentsInfo)
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 협력사 초청메일 목록을 조회한다.
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	public List findVdInviteMailList(Map param) {
		return vendorInfoRepository.findVdInviteMailList(param);
	}

	/**
	 * 중복체크
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	public List checkDuplicatedVdInfo(Map param) { return vendorInfoRepository.checkDuplicatedVdInfo(param); }

	/**
	 * 협력사 초청메일 정보 template을 조회한다.
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	public ResultMap findMailTemplate(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String,Object>();

		String mailKey = param.get("mail_key") == null ? "" : param.get("mail_key").toString();
		Map<String, Object> tmp = templateService.findTemplateContentByMailSetKey(mailKey);

		try {
			//FreeMaker to HTML
			String mailTit = tmp.get("ctmpl_nm") == null ? "" : tmp.get("ctmpl_nm").toString();
			String mailCont = tmp.get("ctmpl_cont") == null ? "" : tmp.get("ctmpl_cont").toString();
			resultMap.put("mail_tit", mailTit);
			resultMap.put("mail_cont", mailCont);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 협력사 초청메일 정보를 조회한다.
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	public ResultMap findVdInviteMailInfo(Map param)  {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap = vendorInfoRepository.findVdInviteMailInfo(param);

		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 협력사 초청메일 정보를 저장한다.
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	public ResultMap sendVendorInviteMail(Map<String, Object> param) {

		String vdInviUuid = (String)UUID.randomUUID().toString();
		param.put("vd_invi_uuid", vdInviUuid);
		vendorInfoRepository.insertVendorInviteMail(param);

		// 메일 발송
		mailService.sendAsync("VENDOR_INVITATION_MAIL", null, param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사 초청메일 정보를 여러건 발송한다.
	 *
	 * @param param
	 * @Date : 2023. 6. 1
	 * @author cyhwang
	 */
	public ResultMap sendVendorInviteMailMulti(Map<String, Object> param) {

		List<Map> vendorInviteList = (List<Map>) param.get("vendorInviteList");
		param.put("bizregnos", ListUtils.getArrayValuesByList(vendorInviteList, "bizregno"));

		List<Map<String,Object>> validResultList = vendorInfoRepository.findListVendorInviteValidate(param);
		List<Map<String,Object>> invalidResultList = (List<Map<String, Object>>) validResultList.stream().filter(result -> "N".equals(result.get("valid_yn"))).collect(Collectors.toList());
		if(invalidResultList.size() > 0) {
			return ResultMap.FAIL(invalidResultList);
		}

		for(Map inviteInfo : vendorInviteList){
			String vdInviUuid = (String)UUID.randomUUID().toString();
			inviteInfo.put("vd_invi_uuid", vdInviUuid);
			vendorInfoRepository.insertVendorInviteMail(inviteInfo);

			// 메일 발송
			mailService.sendAsync("VENDOR_INVITATION_MAIL", null, inviteInfo);
		}
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 협력사 재무정보 목록 그리드 조회
	 *
	 * @param param
	 * @Date : 2023. 6. 14
	 * @author cyhwang
	 */
	public List findListVendorFinance(Map<String, Object> param) {
		return vendorInfoRepository.findListVendorFinance(param);
	}

	/**
	 * 인증서 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 14
	 * @author cyhwang
	 */
	public List<Map<String, Object>> findListCertManager(Map<String, Object> param) {
		return vendorInfoRepository.findListCertManager(param);
	}

	/**
	 * 공장정보, 설비정보를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 14
	 * @author cyhwang
	 */
	public List<Map<String, Object>> findListVdFatyFac(Map<String, Object> param) {
		return vendorInfoRepository.findListVdFatyFac(param);
	}

	/**
	 * 재무정보 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	public List<Map<String, Object>> findListFinanceInfo(Map<String, Object> param) {
		return vendorInfoRepository.findListFinanceInfo(param);
	}

	/**
	 * 재무정보 그리드를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	public ResultMap saveListFinanceInfo(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		List<Map> commonCodeList = sharedService.findCommonCode("E067");

		String vdFnUuid = "";

		// 신규 저장
		for(Map<String, Object> row : inserts){

			for(Map<String, String> row1: commonCodeList){
				// uuid 채번
				vdFnUuid = (String)UUID.randomUUID().toString();
				row.put("vd_fn_uuid", vdFnUuid);
				row.put("fn_acct_typ_ccd", row1.get("data").toLowerCase() );
				row.put("fn_acct_typ_val", row.get(row1.get("data").toLowerCase()));

				vendorInfoRepository.insertListFinanceInfo(row);
			}

			// 신용평가 등급, 현금흐름 등급, WATCH 등급은 공통코드 X
			vdFnUuid = (String)UUID.randomUUID().toString();
			row.put("vd_fn_uuid", vdFnUuid);
			row.put("fn_acct_typ_ccd", "crrat_grd");
			row.put("fn_acct_typ_val", row.get("crrat_grd"));
			vendorInfoRepository.insertListFinanceInfo(row);

			vdFnUuid = (String)UUID.randomUUID().toString();
			row.put("vd_fn_uuid", vdFnUuid);
			row.put("fn_acct_typ_ccd", "cashfw_grd");
			row.put("fn_acct_typ_val", row.get("cashfw_grd"));
			vendorInfoRepository.insertListFinanceInfo(row);

			vdFnUuid = (String)UUID.randomUUID().toString();
			row.put("vd_fn_uuid", vdFnUuid);
			row.put("fn_acct_typ_ccd", "wtch_grd");
			row.put("fn_acct_typ_val", row.get("wtch_grd"));
			vendorInfoRepository.insertListFinanceInfo(row);
		}

		// 수정
		for(Map<String, Object> row : updates){
			for(Map<String, String> row1: commonCodeList){
				row.put("fn_acct_typ_ccd", row1.get("data").toLowerCase() );
				row.put("fn_acct_typ_val", row.get(row1.get("data").toLowerCase()));

				vendorInfoRepository.updateListFinanceInfo(row);
			}

			// 신용평가 등급, 현금흐름 등급, WATCH 등급은 공통코드 X
			row.put("fn_acct_typ_ccd", "crrat_grd");
			row.put("fn_acct_typ_val", row.get("crrat_grd"));
			vendorInfoRepository.updateListFinanceInfo(row);

			row.put("fn_acct_typ_ccd", "cashfw_grd");
			row.put("fn_acct_typ_val", row.get("cashfw_grd"));
			vendorInfoRepository.updateListFinanceInfo(row);

			row.put("fn_acct_typ_ccd", "wtch_grd");
			row.put("fn_acct_typ_val", row.get("wtch_grd"));
			vendorInfoRepository.updateListFinanceInfo(row);
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 재무정보 그리드를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	public ResultMap deleteListFinanceInfo(Map<String,Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedFinanceInfo");


		for (Map<String, Object> row : deletes) {
			vendorInfoRepository.deleteFinanceInfo(row);
		}

		return ResultMap.SUCCESS();
	}


	/**
	 * 직원 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	public List<Map<String, Object>> findListLaborInfo(Map<String, Object> param) {
		return vendorInfoRepository.findListLaborInfo(param);
	}

	/**
	 * 직원 그리드를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	public ResultMap saveListLaborInfo(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		List<Map> commonCodeList = sharedService.findCommonCode("E001");

		String vdEmpCursituUuid = "";
		// 신규 저장
		for(Map<String, Object> row : inserts){
			//int idx = 0;

			for(Map<String, String> row1: commonCodeList){
				// uuid 채번
				vdEmpCursituUuid = (String)UUID.randomUUID().toString();
				row.put("vd_emp_cursitu_uuid", vdEmpCursituUuid);

				row.put("emp_typ_ccd", row1.get("data") );
				row.put("emp_numc", row.get("emp_typ_ccd_"+row1.get("data")));

				vendorInfoRepository.insertListLaborInfo(row);
				//idx++;
			}
		}

		// 수정
		for(Map<String, Object> row : updates){
			//int idx = 0;

			for(Map<String, String> row1: commonCodeList){
				row.put("emp_typ_ccd", row1.get("data") );
				row.put("emp_numc", row.get("emp_typ_ccd_"+row1.get("data")));

				vendorInfoRepository.updateListLaborInfo(row);
				//idx++;
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 직원 그리드를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	public ResultMap deleteListLaborInfo(Map<String,Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedLaborInfo");


		for (Map<String, Object> row : deletes) {
			vendorInfoRepository.deleteLaborInfo(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 인증정보 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	public List<Map<String, Object>> findListCertInfo(Map<String, Object> param) {
		return vendorInfoRepository.findListCertInfo(param);
	}

	/**
	 * 인증정보를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	public ResultMap saveListCertInfo(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		String vdCertUuid = "";

		// 신규 저장
		for(Map<String, Object> row : inserts){

			// uuid 채번
			vdCertUuid = (String)UUID.randomUUID().toString();
			row.put("vd_cert_uuid", vdCertUuid);

			vendorInfoRepository.insertListCertInfo(row);
		}

		// 수정
		for(Map<String, Object> row : updates){
			vendorInfoRepository.updateListCertInfo(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 인증정보를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 22
	 * @author cyhwang
	 */
	public  ResultMap deleteListCertInfo(Map<String,Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedCertInfo");

		for (Map<String, Object> row : deletes) {
			vendorInfoRepository.deleteCertInfo(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사 정보 갱신 요청 메일을 발송한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 29
	 * @author cyhwang
	 */
	public ResultMap infoRenewRequest(Map param) {

		mailService.sendAsync("VENDOR_RENEW_REQ_ML", null, param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사 평가 정보 리스트를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 29
	 * @author : cyhwang
	 */
	public ResultMap findVendorEvalInfoList(Map param) {
		Map<String, Object> resultMap = (Map<String, Object>) vendorInfoEventPublisher.findVendorEvalInfoList(param);
		return ResultMap.SUCCESS(resultMap);
	}

}