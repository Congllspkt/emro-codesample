package smartsuite.app.bp.stamptax.service;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.organizationManager.service.OrganizationManagerService;
import smartsuite.app.bp.stamptax.repository.StampTaxRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.shared.CntrConst;
import smartsuite.data.Floater;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;


/**
 * 인지세 관련 처리하는 서비스 Class입니다.
 *
 * @FileName StampTaxService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class StampTaxService {

	private static final Logger LOG = LoggerFactory.getLogger(StampTaxService.class);

	@Inject
	StampTaxRepository stampTaxRepository;

	@Inject
	SharedService sharedService;

	@Inject
	OrganizationManagerService organizationManagerService;

	/**
	 * 인지세 목록 조회
	 *
	 * @param : 인지세 목록을 조회 조건에 해당하는 인자
	 * @return : 인지세 목록
	 */
	public FloaterStream largeFindListStampTax(Map param) {
		// 대용량 처리
		FloaterStream stampTaxList = stampTaxRepository.largeSearchListStampTax(param);

		stampTaxList.add(new Floater<Map>() {
			public void progress(Map stampTax) {
				String[] staxAmts = stampTax.get("stax_amt").toString().split("\\.");

				int currentStaxAmt = floorDecimalPoint(stampTax.get("stax_amt").toString());
				int cntrMaxRevnoCntrAmt = floorDecimalPoint(stampTax.get("cntr_max_revno_cntr_amt").toString());
				int staxAmt = calcurateStaxAmt(cntrMaxRevnoCntrAmt);    // 최대차수 계약금액 기준 인지세액

				// 환불 조건 : 인지세 중 두번째 최대 차수 && 인지세액 < 납부총액
				if (staxAmt < currentStaxAmt) {
					stampTax.put("refund_yn", "Y");
				} else {
					stampTax.put("refund_yn", "N");
				}
			}
		});

		return stampTaxList;
	}

	private int floorDecimalPoint(String amt){
		String[] amts = amt.split("\\.");
		if(amts.length == 2){
			return Integer.parseInt(amts[0]);
		}else{
			return Integer.parseInt(amt);
		}
	}

	private int calcurateStaxAmt(int cntrAmt) {
		int staxAmt;

		if (cntrAmt > CntrConst.STAX_RANGE_MIN_AMT.STAX_350000) {    // 1000000000
			staxAmt = 350000;
		} else if (cntrAmt > CntrConst.STAX_RANGE_MIN_AMT.STAX_150000) {    //100000000
			staxAmt = 150000;
		} else if (cntrAmt > CntrConst.STAX_RANGE_MIN_AMT.STAX_70000) {    // 50000000
			staxAmt = 70000;
		} else if (cntrAmt > CntrConst.STAX_RANGE_MIN_AMT.STAX_40000) {    // 30000000
			staxAmt = 40000;
		} else if (cntrAmt > CntrConst.STAX_RANGE_MIN_AMT.STAX_20000) {    // 10000000
			staxAmt = 20000;
		} else {
			staxAmt = 0;
		}

		return staxAmt;
	}

	public void updateStampTaxSts(Map param) {
		stampTaxRepository.updateStampTaxSts(param);
	}

	public void requestStampTax(Map<String, Object> param) {

		if (checkStampTaxCreatable(param)) {
			String sttpymtmethCcd = (String) param.get("sttpymtmeth_ccd");
			String sttpymtRoTypCcd = (String) param.get("sttpymt_ro_typ_ccd");
			String cntrAmt = param.get("cntr_amt").toString();
			String cntrUuid = param.get("cntr_uuid").toString();

			Map stampTax = Maps.newHashMap();
			stampTax.put("cntr_uuid", cntrUuid);
			stampTax.put("sttpymtmeth_ccd", sttpymtmethCcd);
			stampTax.put("sttpymt_sts_ccd", CntrConst.STTPYMT_STATUS.WAITING);

			int staxAmt = calcurateStampTaxAmt(Integer.parseInt(cntrAmt));
			long buyerStaxAmt = 0;
			long vendorStaxAmt = 0;

			if (CntrConst.STTPYMT_RATIO.HALF_HALF.equals(sttpymtRoTypCcd)) {
				int buyerSttpymtRatio = 50;
				buyerStaxAmt = Math.round(staxAmt * 0.5);

				stampTax.put("sttpymt_ro", buyerSttpymtRatio);
				stampTax.put("stax_amt", buyerStaxAmt);

				createBuyerStampTax(stampTax);

				int vendorSttpymtRatio = 50;
				vendorStaxAmt = Math.round(staxAmt * 0.5);

				stampTax.put("sttpymt_ro", vendorSttpymtRatio);
				stampTax.put("stax_amt", vendorStaxAmt);

				createVendorStampTax(stampTax);

			} else if (CntrConst.STTPYMT_RATIO.BUYER_100.equals(sttpymtRoTypCcd)) {

				int buyerSttpymtRatio = 100;
				buyerStaxAmt = staxAmt;

				stampTax.put("sttpymt_ro", buyerSttpymtRatio);
				stampTax.put("stax_amt", buyerStaxAmt);

				createBuyerStampTax(stampTax);

			} else if (CntrConst.STTPYMT_RATIO.VENDOR_100.equals(sttpymtRoTypCcd)) {

				int vendorSttpymtRatio = 100;
				vendorStaxAmt = staxAmt;

				stampTax.put("sttpymt_ro", vendorSttpymtRatio);
				stampTax.put("stax_amt", vendorStaxAmt);

				createVendorStampTax(stampTax);
			}
		}

	}

	private boolean checkStampTaxCreatable(Map cntrInfo) {

		// 현재 계약정보
		Map<String, Object> currContractInfo = cntrInfo;

		//1. 이전계약여부 조회
		int preContractCnt = stampTaxRepository.findPreContractCnt(currContractInfo);

		if (preContractCnt > 0) {
			//2. 계약정보 조회 - 계약금액 확인
			List<Map<String, Object>> preContractList = stampTaxRepository.findListPreContractInfo(currContractInfo);
			Map<String, Object> preContractInfo = preContractList.get(0);   // 현재 차수 바로 이전 계약정보

			//3. 이전계약의 인지세 완납 여부 확인
			int totalPreSttpymtAmt = this.calculateTotalAmt(preContractList, "sttpymt_amt");       // 납부한 인지세 총액
			int totalPreRefundAmt = this.calculateTotalAmt(preContractList, "stax_refund_amt");    // 환급받은 총액

			// 총 납부 금액 = 납부한 인지세 총액 - 환급받은 총액
			int finalSttpymtAmt = this.calculateFinalStaxAmt(totalPreSttpymtAmt, totalPreRefundAmt);

			// 현재차수 계약금액 기준 인지세액 조회
			int currCntrAmt = Integer.parseInt(currContractInfo.get("cntr_amt").toString());
			int staxAmt = calcurateStampTaxAmt(currCntrAmt);
			String sttpymtStsCcd = checkPrePaymentSts(staxAmt, finalSttpymtAmt);

			// 이전계약이 존재하며, 계약금액이 감액, 이전계약에서 인지세를 모두 완납한 경우
			return "N".equals(sttpymtStsCcd) ? false : true;

		}

		return true;
	}

	private String checkPrePaymentSts(int staxAmt, int preSttpymtAmt) {
		// 현재차수 인지세액, 기납부 총액
		if (staxAmt <= preSttpymtAmt) {
			return "N";     // 인지세 미생성
		} else {
			return "Y";     // 인지세 생성
		}
	}

	private int calculateTotalAmt(List<Map<String, Object>> list, String key) {
		int totalAmount = 0;
		for (Map<String, Object> row : list) {
			int amt = Integer.parseInt(row.get(key).toString());
			totalAmount += amt;
		}

		return totalAmount;
	}

	private int calculateFinalStaxAmt(int totalSttpymtAmt, int totalRefundAmt) {
		return totalSttpymtAmt - totalRefundAmt;
	}

	public String checkPaymentSts(int staxAmt, int sttpymtAmt) {

		if (sttpymtAmt == 0) {
			return CntrConst.STTPYMT_STATUS.WAITING;
		} else {
			if (staxAmt == sttpymtAmt) {
				return CntrConst.STTPYMT_STATUS.COMPLETED;
			} else {
				return CntrConst.STTPYMT_STATUS.PROGRESS;
			}
		}
	}

	private void createBuyerStampTax(Map stampTax) {
		stampTax.put("stax_uuid", UUID.randomUUID().toString());
		//구매사 정보 조회
		Map contractorParam = Maps.newHashMap();
		contractorParam.put("cntr_uuid", stampTax.get("cntr_uuid"));
		contractorParam.put("cntrr_typ_ccd", CntrConst.USR_TYPE.BUYER);

		Map buyer = stampTaxRepository.findContractorInfo(contractorParam);
		stampTax.put("cntrr_uuid", buyer.get("cntrr_uuid"));
		stampTax.put("cntrr_typ_ccd", buyer.get("cntrr_typ_ccd"));
		stampTax.put("cntr_uuid", stampTax.get("cntr_uuid"));

		if (isExistsStampTax(stampTax)) {
			stampTaxRepository.updateStampTax(stampTax);
		} else {
			stampTaxRepository.insertStampTax(stampTax);
		}

	}

	private boolean isExistsStampTax(Map param) {
		int cnt = stampTaxRepository.findStampTaxCnt(param);

		if (cnt > 0) {
			return true;
		} else {
			return false;
		}
	}

	private void createVendorStampTax(Map stampTax) {
		stampTax.put("stax_uuid", UUID.randomUUID().toString());
		//구매사 정보 조회
		Map contractorParam = Maps.newHashMap();
		contractorParam.put("cntr_uuid", stampTax.get("cntr_uuid"));
		contractorParam.put("cntrr_typ_ccd", CntrConst.USR_TYPE.VENDOR);
		contractorParam.put("rep_vd_yn", "Y");
		Map vendor = stampTaxRepository.findContractorInfo(contractorParam);
		stampTax.put("cntrr_uuid", vendor.get("cntrr_uuid"));
		stampTax.put("cntrr_typ_ccd", vendor.get("cntrr_typ_ccd"));

		if (isExistsStampTax(vendor)) {
			stampTaxRepository.updateStampTax(stampTax);
		} else {
			stampTaxRepository.insertStampTax(stampTax);
		}
	}

	public int calcurateStampTaxAmt(int cntrAmt) {
		Map codeParam = Maps.newHashMap();

		codeParam.put("ccd", "D017");
		codeParam.put("cstr_cnd_cd", "MIN_AMT");
		List<Map<String, Object>> minStampTaxSection = sharedService.findListCommonCodeAttributeCode(codeParam);

		codeParam.put("cstr_cnd_cd", "MAX_AMT");
		List<Map<String, Object>> maxStampTaxSection = sharedService.findListCommonCodeAttributeCode(codeParam);
		int staxAmt = 0;

		for (int i = 0; i < minStampTaxSection.size(); i++) {
			Map minStampTaxInfo = minStampTaxSection.get(i);
			Map maxStampTaxInfo = maxStampTaxSection.get(i);

			int minAmount = Integer.parseInt((String) minStampTaxInfo.get("cstr_cnd_val"));
			Double maxAmount = Double.parseDouble((String) maxStampTaxInfo.get("cstr_cnd_val"));


			if (minAmount < cntrAmt && cntrAmt <= maxAmount.longValue()) {
				staxAmt = Integer.parseInt(minStampTaxInfo.get("data").toString());
				return staxAmt;
			}
		}

		return staxAmt;
	}

	public ResultMap findOfflStampTaxInfo(Map param) {
		Map<String, Object> stampTaxInfo = stampTaxRepository.findOfflStampTaxInfo(param);

		return ResultMap.SUCCESS(stampTaxInfo);
	}

	public ResultMap saveStampTaxFile(Map param) {
		int staxAmt = Integer.parseInt(String.valueOf(param.get("stax_amt")));
		int toTalEStampTaxAmt = Integer.parseInt(String.valueOf(param.get("sttpymt_amt")));

		param.put("sttpymt_sts_ccd", CntrConst.STTPYMT_STATUS.COMPLETED);
		stampTaxRepository.saveStampTaxFile(param);

		return ResultMap.SUCCESS();
	}

	public List findListStampTaxPayHistory(Map param) {
		List<Map<String, Object>> stampTaxPayList = stampTaxRepository.findListStampTaxPayHistory(param);

		for (Map<String, Object> stampTaxPay : stampTaxPayList) {
			BigDecimal cntrAmt = ConvertUtils.convertBigDecimal(stampTaxPay.get("cntr_amt"));
			int staxAmt = calcurateStampTaxAmt(cntrAmt.intValue());
			stampTaxPay.put("stax_amt", staxAmt);
		}

		return stampTaxPayList;
	}

	public ResultMap saveRefundStampTax(Map param) {
		stampTaxRepository.saveRefundStampTax(param);
		return ResultMap.SUCCESS();
	}

	public List findCntrListForStampTax(Map param) {
		List<Map<String, Object>> cntrListForStampTax = stampTaxRepository.findCntrListForStampTax(param);
		List<Map<String, Object>> filteredCntrListForStampTax = new ArrayList<>();
		Map<String, Object> rootLogicOrgCdMap = new HashMap<>();

		for (Map cntrInfo : cntrListForStampTax) {
			String oorgCd = (String) cntrInfo.get("oorg_cd");
			String logicOrgCd = this.removeOperUnit(oorgCd);

			Map rootLogicOrgCdInfo = new HashMap();
			if(rootLogicOrgCdMap.get(logicOrgCd) == null) {
				rootLogicOrgCdInfo = organizationManagerService.findRootLogicOrganizationInfo(logicOrgCd);
				rootLogicOrgCdMap.put(logicOrgCd, rootLogicOrgCdInfo);
			} else {
				rootLogicOrgCdInfo = (Map) rootLogicOrgCdMap.get(logicOrgCd);
			}

			String rootCtryCcd = (String) rootLogicOrgCdInfo.get("ctry_ccd");
			
			// 구매운영조직에 상위 조직 company가 KR인 경우만 인지세 가능
			if ("KR".equals(rootCtryCcd) && checkStampTaxCreatable(cntrInfo)) {
				filteredCntrListForStampTax.add(cntrInfo);
			}
		}
		return filteredCntrListForStampTax;
	}

	private String removeOperUnit(String oorgCd){
		if(oorgCd.indexOf("PO") != -1){
			oorgCd = oorgCd.replace("PO","");
		}else if(oorgCd.indexOf("EO") != -1){
			oorgCd = oorgCd.replace("EO","");
		}
		return oorgCd;
	}

	public ResultMap createStampTax(Map param) {
		List<Map<String, Object>> cntrInfoList = (List<Map<String, Object>>) param.get("cntrInfoList");
		for(Map cntrInfo : cntrInfoList) {
			requestStampTax(cntrInfo);
		}
		return ResultMap.SUCCESS();
	}

	public ResultMap deleteStampTax(Map param) {
		List<Map<String, Object>> stampTaxList = (List<Map<String, Object>>) param.get("stampTaxList");
		for(Map stampTaxInfo : stampTaxList) {
			stampTaxRepository.deleteStampTax(stampTaxInfo);
		}
		return ResultMap.SUCCESS();
	}
}
