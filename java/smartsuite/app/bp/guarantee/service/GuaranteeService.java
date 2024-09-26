package smartsuite.app.bp.guarantee.service;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.guarantee.event.GuaranteeEventPublisher;
import smartsuite.app.bp.guarantee.repository.GuaranteeRepository;
import smartsuite.app.common.shared.Const;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.GuarConst;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 보증 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @FileName GuaranteeService.java
 */

@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class GuaranteeService {
	
	private static final Logger LOG = LoggerFactory.getLogger(GuaranteeService.class);

	/** The sql session. */
	@Inject
	GuaranteeRepository guaranteeRepository;
	@Inject
	GuaranteeEventPublisher guaranteeEventPublisher;

	/**
	 * 대용량 보증요청대상 조회
	 * @param : Map
	 * @return : List
	 */
	public FloaterStream largeFindGuarRequestList(Map<String, Object> param) {
		// 대용량 처리
		return guaranteeRepository.largeSearchGuarRequestList(param);
	}

	public void requestGuar(Map param) {

		List<Map<String,Object>> requestGuarList = (List<Map<String,Object>>)param.get("checkedList");

		for(Map row : requestGuarList) {
			row.put("gur_sts_ccd", GuarConst.GUAR_STATUS.PUBLIC_WAITING);
			guaranteeRepository.updateGuarSts(row);
		}
	}

	/**
	 * 보증보험 처리현황 조회
	 */
	public FloaterStream largeFindGuarList(Map<String, Object> param) {
		// 대용량 처리
		return guaranteeRepository.largeSearchGuarList(param);
	}

	/**
	 * 오프라인 보증정보 조회
	 */
	public Map<String,Object> getOfflineBondInfo(Map<String,Object> param){

		return guaranteeRepository.getOfflineBondInfo(param);
	}

	public Map saveOfflineApprove(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();

		param.put("gur_sts_ccd", GuarConst.GUAR_STATUS.APPROVED);

		guaranteeRepository.updateOfflineGuarInfo(param);

		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}

	public Map saveOfflineReject(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();

		param.put("gur_sts_ccd", GuarConst.GUAR_STATUS.RECEIPT_REJECTION);

		guaranteeRepository.updateOfflineGuarInfo(param);

		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}

	public void requestGuarantee(Map<String, Object> param) {
		String cpgurTypCcd = (String)param.get("cpgur_typ_ccd");
		String defpgurTypCcd = (String)param.get("defpgur_typ_ccd");
		String apymtgurTypCcd = (String)param.get("apymtgur_typ_ccd");
		Map cntrInfo = guaranteeRepository.findGuaranteeWithCntr(param);

		if(CntrConst.GUARANTEE_KIND.GUARANTEE_INSURANCE.equals(cpgurTypCcd)){
			Map guarantee = Maps.newHashMap();
			guarantee.put("gur_uuid", UUID.randomUUID().toString());
			guarantee.put("cntr_amt", cntrInfo.get("cntr_amt"));
			guarantee.put("cntr_no", cntrInfo.get("cntr_no"));
			guarantee.put("cntr_rev", cntrInfo.get("cntr_rev"));
			guarantee.put("vd_cd", cntrInfo.get("vd_cd"));
			guarantee.put("gur_typ_ccd", GuarConst.GUARANTEE_TYPE.CONTRACT_PERFORMANCE_GUARANTEE);
			guarantee.put("cntr_uuid", cntrInfo.get("cntr_uuid"));
			guarantee.put("gur_ro", param.get("cpgur_ro"));
			guarantee.put("gur_st_dt", param.get("cpgur_st_dt"));
			guarantee.put("gur_exp_dt", param.get("cpgur_exp_dt"));
			guarantee.put("gur_sts_ccd", GuarConst.GUAR_STATUS.REQUEST_WAITING);
			guaranteeRepository.insertGuarantee(guarantee);
		}

		if(CntrConst.GUARANTEE_KIND.GUARANTEE_INSURANCE.equals(defpgurTypCcd)){
			Map guarantee = Maps.newHashMap();
			guarantee.put("gur_uuid", UUID.randomUUID().toString());
			guarantee.put("cntr_amt", cntrInfo.get("cntr_amt"));
			guarantee.put("cntr_no", cntrInfo.get("cntr_no"));
			guarantee.put("cntr_rev", cntrInfo.get("cntr_rev"));
			guarantee.put("vd_cd", cntrInfo.get("vd_cd"));
			guarantee.put("gur_typ_ccd", GuarConst.GUARANTEE_TYPE.DEFECT_PERFORMANCE_GAURANTEE);
			guarantee.put("cntr_uuid", cntrInfo.get("cntr_uuid"));
			guarantee.put("gur_ro", param.get("defpgur_ro"));
			guarantee.put("gur_st_dt_cnd_typ_ccd", param.get("defpgur_pd_typ_ccd"));
			guarantee.put("gur_pd_mnths", param.get("defpgur_pd"));
			guarantee.put("gur_sts_ccd", GuarConst.GUAR_STATUS.REQUEST_WAITING);
			guaranteeRepository.insertGuarantee(guarantee);
		}

		if(CntrConst.GUARANTEE_KIND.GUARANTEE_INSURANCE.equals(apymtgurTypCcd)){
			Map guarantee = Maps.newHashMap();
			guarantee.put("gur_uuid", UUID.randomUUID().toString());
			guarantee.put("cntr_amt", cntrInfo.get("cntr_amt"));
			guarantee.put("cntr_no", cntrInfo.get("cntr_no"));
			guarantee.put("cntr_rev", cntrInfo.get("cntr_rev"));
			guarantee.put("vd_cd", cntrInfo.get("vd_cd"));
			guarantee.put("gur_typ_ccd", GuarConst.GUARANTEE_TYPE.ADVANCE_PAYMENTPERFORMANCE_GUARANTEE);
			guarantee.put("cntr_uuid", cntrInfo.get("cntr_uuid"));
			guarantee.put("gur_ro", param.get("apymtgur_ro"));
			guarantee.put("gur_st_dt", param.get("apymtgur_st_dt"));
			guarantee.put("gur_exp_dt", param.get("apymtgur_exp_dt"));
			guarantee.put("gur_sts_ccd", GuarConst.GUAR_STATUS.REQUEST_WAITING);
			guarantee.put("apymt_pymt_dt", param.get("pymt_expt_dt"));          // 지급 예정일자(선급금 지급 일자)
			guarantee.put("apymt_pymt_amt", param.get("apymt_pymt_amt"));       // 선급금 지급 금액
			guaranteeRepository.insertGuarantee(guarantee);
		}

	}


}
