package smartsuite.app.sp.vendorMaster.vendorInfo.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.sp.vendorMaster.vendorInfo.event.SpVendorInfoEventPublisher;
import smartsuite.app.sp.vendorMaster.vendorInfo.repository.SpVendorInfoRepository;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SpVendorInfoService {
    
//	private static final Log LOG = LogFactory.getLog(SpVendorService.class);
    
    @Inject
    private SpVendorInfoRepository spVendorInfoRepository;
	@Inject
	private transient SharedService sharedService;

	@Inject
    private SpVendorInfoEventPublisher spVendorInfoEventPublisher;

    private final SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

	/**
	 * 공장정보를 조회한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 16
	 * @author cyhwang
	 */
	public List<Map<String, Object>> findListVdFatyInfo(Map<String, Object> param) {
		return spVendorInfoRepository.findListVdFatyInfo(param);
	}

	/**
	 * 공장정보를 저장한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 16
	 * @author cyhwang
	 */
	public ResultMap saveListVdFatyInfo(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		int fatySeqNo = 0;

		// 신규 저장
		for(Map<String, Object> row : inserts){
			String vdFatyUuid = (String)UUID.randomUUID().toString(); // UUID 채번
			fatySeqNo = spVendorInfoRepository.findVdFatyMaxSeq(row); // seq 조회
			row.put("vd_faty_uuid", vdFatyUuid);
			row.put("faty_seqno", fatySeqNo);
			spVendorInfoRepository.insertListVdFatyInfo(row);
		}

		// 수정
		for(Map<String, Object> row : updates){
			spVendorInfoRepository.updateListVdFatyInfo(row);
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 공장정보를 삭제한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 16
	 * @author cyhwang
	 */
	public ResultMap deleteListVdFatyInfo(Map<String,Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedFatyInfo");

		for (Map<String, Object> row : deletes) {
			spVendorInfoRepository.deleteListVdFacInfoByFatyUuid(row);    // 설비 삭제
			spVendorInfoRepository.deleteListVdFatyInfo(row);             // 공장 삭제
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
		return spVendorInfoRepository.findListVdFacInfo(param);
	}

	/**
	 * 설비정보를 저장한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 16
	 * @author cyhwang
	 */
	public ResultMap saveListVdFacInfo(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		int facSeqNo = 0;

		// 신규 저장
		for(Map<String, Object> row : inserts){
			String vdFacUuid = (String)UUID.randomUUID().toString(); // UUID 채번
			facSeqNo = spVendorInfoRepository.findVdFacMaxSeq(row); // seq 조회
			row.put("vd_fac_uuid", vdFacUuid);
			row.put("fac_seqno", facSeqNo);
			spVendorInfoRepository.insertListVdFacInfo(row);
		}

		// 수정
		for(Map<String, Object> row : updates){
			spVendorInfoRepository.updateListVdFacInfo(row);
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 설비정보를 삭제한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 16
	 * @author cyhwang
	 */
	public ResultMap deleteListVdFacInfo(Map<String,Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedFacInfo");

		for (Map<String, Object> row : deletes) {
			spVendorInfoRepository.deleteListVdFacInfo(row);  // 설비 삭제 (deletedFacInfo)
		}

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사 재무정보 목록 그리드 조회
	 *
	 * @param param
	 * @Date : 2023. 6. 16
	 * @author cyhwang
	 */
	public List findListVendorFinance(Map<String, Object> param) {
		return spVendorInfoRepository.findListVendorFinance(param);
	}

	/**
	 * 재무정보 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	public List<Map<String, Object>> findListFinanceInfo(Map<String, Object> param) {
		return spVendorInfoRepository.findListFinanceInfo(param);
	}

	/**
	 * 재무정보 그리드를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
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

				spVendorInfoRepository.insertListFinanceInfo(row);
			}

			// 신용평가 등급, 현금흐름 등급, WATCH 등급은 공통코드 X
			vdFnUuid = (String)UUID.randomUUID().toString();
			row.put("vd_fn_uuid", vdFnUuid);
			row.put("fn_acct_typ_ccd", "crrat_grd");
			row.put("fn_acct_typ_val", row.get("crrat_grd"));
			spVendorInfoRepository.insertListFinanceInfo(row);

			vdFnUuid = (String)UUID.randomUUID().toString();
			row.put("vd_fn_uuid", vdFnUuid);
			row.put("fn_acct_typ_ccd", "cashfw_grd");
			row.put("fn_acct_typ_val", row.get("cashfw_grd"));
			spVendorInfoRepository.insertListFinanceInfo(row);

			vdFnUuid = (String)UUID.randomUUID().toString();
			row.put("vd_fn_uuid", vdFnUuid);
			row.put("fn_acct_typ_ccd", "wtch_grd");
			row.put("fn_acct_typ_val", row.get("wtch_grd"));
			spVendorInfoRepository.insertListFinanceInfo(row);
		}

		// 수정
		for(Map<String, Object> row : updates){
			for(Map<String, String> row1: commonCodeList){
				row.put("fn_acct_typ_ccd", row1.get("data").toLowerCase() );
				row.put("fn_acct_typ_val", row.get(row1.get("data").toLowerCase()));

				spVendorInfoRepository.updateListFinanceInfo(row);
			}

			// 신용평가 등급, 현금흐름 등급, WATCH 등급은 공통코드 X
			row.put("fn_acct_typ_ccd", "crrat_grd");
			row.put("fn_acct_typ_val", row.get("crrat_grd"));
			spVendorInfoRepository.updateListFinanceInfo(row);

			row.put("fn_acct_typ_ccd", "cashfw_grd");
			row.put("fn_acct_typ_val", row.get("cashfw_grd"));
			spVendorInfoRepository.updateListFinanceInfo(row);

			row.put("fn_acct_typ_ccd", "wtch_grd");
			row.put("fn_acct_typ_val", row.get("wtch_grd"));
			spVendorInfoRepository.updateListFinanceInfo(row);
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 재무정보 그리드를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	public ResultMap deleteListFinanceInfo(Map<String,Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedFinanceInfo");

		for (Map<String, Object> row : deletes) {
			spVendorInfoRepository.deleteFinanceInfo(row);
		}

		return ResultMap.SUCCESS();
	}


	/**
	 * 직원 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	public List<Map<String, Object>> findListLaborInfo(Map<String, Object> param) {
		return spVendorInfoRepository.findListLaborInfo(param);
	}

	/**
	 * 직원 그리드를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
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

				spVendorInfoRepository.insertListLaborInfo(row);
				//idx++;
			}
		}

		// 수정
		for(Map<String, Object> row : updates){
			//int idx = 0;

			for(Map<String, String> row1: commonCodeList){
				row.put("emp_typ_ccd", row1.get("data") );
				row.put("emp_numc", row.get("emp_typ_ccd_"+row1.get("data")));

				spVendorInfoRepository.updateListLaborInfo(row);
				//idx++;
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 직원 그리드를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	public ResultMap deleteListLaborInfo(Map<String,Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedLaborInfo");

		for (Map<String, Object> row : deletes) {
			spVendorInfoRepository.deleteLaborInfo(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 인증정보 그리드를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	public List<Map<String, Object>> findListCertInfo(Map<String, Object> param) {
		return spVendorInfoRepository.findListCertInfo(param);
	}

	/**
	 * 인증정보를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
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

			spVendorInfoRepository.insertListCertInfo(row);
		}

		// 수정
		for(Map<String, Object> row : updates){
			spVendorInfoRepository.updateListCertInfo(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 인증정보를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 4
	 * @author cyhwang
	 */
	public  ResultMap deleteListCertInfo(Map<String,Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deletedCertInfo");

		for (Map<String, Object> row : deletes) {
			spVendorInfoRepository.deleteCertInfo(row);
		}

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
		Map<String, Object> resultMap = (Map<String, Object>) spVendorInfoEventPublisher.findVendorEvalInfoList(param);
		return ResultMap.SUCCESS(resultMap);
	}
}
