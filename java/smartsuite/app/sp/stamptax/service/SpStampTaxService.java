package smartsuite.app.sp.stamptax.service;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.sp.stamptax.repository.SpStampTaxRepository;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 인지세 관련 처리하는 서비스 Class입니다.
 *
 * @FileName StampTaxService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class SpStampTaxService {

	private static final Logger LOG = LoggerFactory.getLogger(SpStampTaxService.class);

	@Inject
	SpStampTaxRepository spStampTaxRepository;

	@Inject
	SharedService sharedService;

	/**
	 * 인지세 목록 조회
	 *
	 * @param : 인지세 목록을 조회 조건에 해당하는 인자
	 * @return : 인지세 목록
	 */
	public FloaterStream largeFindListStampTax(Map param) {
		// 대용량 처리
		FloaterStream stampTaxList = spStampTaxRepository.largeSearchListStampTax(param);
		return stampTaxList;
	}

	/**
	 * (오프라인)인지세 상세 조회
	 *
	 * @param : Map
	 * @return : Map
	 */
	public ResultMap findOfflStampTaxInfo(Map param) {
		Map<String, Object> stampTaxInfo = spStampTaxRepository.findOfflStampTaxInfo(param);

		return ResultMap.SUCCESS(stampTaxInfo);
	}

	/**
	 * (오프라인)인지세 파일 저장
	 *
	 * @param : Map
	 * @return : Map
	 */
	public  ResultMap saveStampTaxFile(Map param) {
		int staxAmt = Integer.parseInt(String.valueOf(param.get("stax_amt")));
		int toTalEStampTaxAmt = Integer.parseInt(String.valueOf(param.get("sttpymt_amt")));
		String sttpymtStsCcd = checkPaymentSts(staxAmt, toTalEStampTaxAmt);
		param.put("sttpymt_sts_ccd", sttpymtStsCcd);

		spStampTaxRepository.saveStampTaxFile(param);

		return ResultMap.SUCCESS();
	}

	public String checkPaymentSts(int staxAmt, int sttpymtAmt) {

		if(sttpymtAmt == 0){
			return CntrConst.STTPYMT_STATUS.WAITING;
		}else{
			if( staxAmt == sttpymtAmt ) {
				return CntrConst.STTPYMT_STATUS.COMPLETED;
			}else{
				return CntrConst.STTPYMT_STATUS.PROGRESS;
			}
		}
	}

	public void updateStampTaxSts(Map param){
		spStampTaxRepository.updateStampTaxSts(param);
	}

	public List findListStampTaxPayHistory(Map param) {
		List<Map<String, Object>> stampTaxPayList = spStampTaxRepository.findListStampTaxPayHistory(param);

		for(Map<String,Object> stampTaxPay : stampTaxPayList){
			BigDecimal cntrAmt = ConvertUtils.convertBigDecimal(stampTaxPay.get("cntr_amt"));
			int staxAmt = calcurateStampTaxAmt(cntrAmt.intValue());
			stampTaxPay.put("stax_amt", staxAmt);
		}


		return stampTaxPayList;
	}

	public int calcurateStampTaxAmt(int cntrAmt) {
		Map codeParam = Maps.newHashMap();

		codeParam.put("ccd", "D017");
		codeParam.put("cstr_cnd_cd", "MIN_AMT");
		List<Map<String,Object>> minStampTaxSection = sharedService.findListCommonCodeAttributeCode(codeParam);

		codeParam.put("cstr_cnd_cd", "MAX_AMT");
		List<Map<String,Object>> maxStampTaxSection = sharedService.findListCommonCodeAttributeCode(codeParam);
		int staxAmt = 0;

		for(int i = 0; i < minStampTaxSection.size(); i++){
			Map minStampTaxInfo = minStampTaxSection.get(i);
			Map maxStampTaxInfo = maxStampTaxSection.get(i);

			int minAmount = Integer.parseInt((String)minStampTaxInfo.get("cstr_cnd_val"));
			Double maxAmount = Double.parseDouble((String)maxStampTaxInfo.get("cstr_cnd_val"));


			if( minAmount < cntrAmt && cntrAmt <= maxAmount.longValue() ){
				staxAmt = Integer.parseInt(minStampTaxInfo.get("data").toString());
				return staxAmt;
			}
		}

		return staxAmt;
	}

	public ResultMap saveRefundStampTax(Map param) {
		spStampTaxRepository.saveRefundStampTax(param);
		return ResultMap.SUCCESS();
	}
}