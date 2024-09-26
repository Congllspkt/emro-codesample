package smartsuite.app.util;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartsuite.app.common.shared.Const;
import smartsuite.app.shared.ElecGuaranteeConst;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public final class ElecGuaranteeUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(ElecGuaranteeUtil.class);
	
	/**
	 * 통보서 인터페이스 정의서에 맞게 변경
	 * 
	 * @param param
	 * @return the map
	 */
	public static Map setBondDataToIFNoticDevinition(Map bondInfo){
		String cntrNo = (String)bondInfo.get("cntr_no");
		Object cntrRevNo = (Object)bondInfo.get("cntr_revno");
		String sCntrRev = "";
		
		if(Integer.class == cntrRevNo.getClass()) {
			sCntrRev = String.format("%02d", (Integer)cntrRevNo);
		} else if(BigDecimal.class == cntrRevNo.getClass()) {
			sCntrRev = String.format("%02d", ((BigDecimal)cntrRevNo).intValue());
		}
		
		String contNumbText = cntrNo + sCntrRev.trim();
		
		bondInfo.put("cont_numb_text", contNumbText); // 계약번호
		bondInfo.put("revision_no", sCntrRev);
		
		String bpBizRegNo = (String)bondInfo.get("bp_bizregno");
		bpBizRegNo = bpBizRegNo.replace("-", "");
		String headMesgSend = "A" + bpBizRegNo + "00";
		
		bondInfo.put("head_mesg_send", headMesgSend); // 송신자 ID
		
		BigDecimal bondPenlAmnt = (BigDecimal)bondInfo.get("bond_penl_amnt");
		BigDecimal bBondPenlAmnt = bondPenlAmnt.setScale(0, RoundingMode.FLOOR); // 소수점 절삭

		bondInfo.put("bond_penl_amnt", bBondPenlAmnt); // 보험가입금액

		BigDecimal prepPaymAmnt = (BigDecimal)bondInfo.get("prep_paym_amnt");
		BigDecimal bPrepPaymAmnt = prepPaymAmnt.setScale(0, RoundingMode.FLOOR); // 소수점 절삭

		bondInfo.put("prep_paym_amnt", bPrepPaymAmnt);

		BigDecimal cntrAmt = (BigDecimal)bondInfo.get("cntr_amt");
		BigDecimal bCntrAmt = cntrAmt.setScale(0, RoundingMode.FLOOR); // 소수점 절삭
		
		bondInfo.put("cont_main_amnt", bCntrAmt);	// 계약금액

		String contTermText = "";
		String bondTermText = "";
        String cntrStartDateStr = (String)bondInfo.get("cntr_st_dt");
        String cntrEndDateStr = (String)bondInfo.get("cntr_exp_dt");
        String bondBegnDateStr = (String)bondInfo.get("bond_begn_date");
        String bondFnshDateStr = (String)bondInfo.get("bond_fnsh_date");
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        
        Date cntrStartDate = null;
        Date cntrEndDate = null;
        Date bondBegnDate = null;
        Date bondFnshDate = null;
        
        try {
            cntrStartDate = formatter.parse(cntrStartDateStr);
            cntrEndDate = formatter.parse(cntrEndDateStr);
            bondBegnDate = formatter.parse(bondBegnDateStr);
            bondFnshDate = formatter.parse(bondFnshDateStr);
        } catch(ParseException e) {
            bondInfo.put(Const.RESULT_STATUS, Const.FAIL);
            LOG.error("setBondDataToInterfaceDevinition funtion error : " + e.toString());
            return bondInfo;
        }
        
        long cntrDiff = cntrEndDate.getTime() - cntrStartDate.getTime();
        long cntrDiffDays = cntrDiff / (24 * 60 * 60 * 1000) + 1;
        contTermText = cntrDiffDays + "일";
        
        long bondDiff = bondFnshDate.getTime() - bondBegnDate.getTime();
        long bondDiffDays = bondDiff / (24 * 60 * 60 * 1000) + 1;
        bondTermText = bondDiffDays + "일";
        
		if(ElecGuaranteeConst.PAYINF.equals(bondInfo.get("gur_typ_ccd"))) {
			BigDecimal paymBefrAmnt = (BigDecimal)bondInfo.get("paym_befr_amnt");
			int iPaymBefrAmnt = paymBefrAmnt.intValue();   // 소수점 절삭
			bondInfo.put("paym_befr_amnt", iPaymBefrAmnt); // 보험개시전 채무액
			
			BigDecimal undeTotaAmnt = (BigDecimal)bondInfo.get("unde_tota_amnt");
			int iUndeTotaAmnt = undeTotaAmnt.intValue();   // 소수점 절삭
			bondInfo.put("unde_tota_amnt", iUndeTotaAmnt); // 총계약금액
			
			BigDecimal undeNumbCont = (BigDecimal)bondInfo.get("unde_numb_cont");
			int iUndeNumbCont = undeNumbCont.intValue();   // 소수점 절삭
			bondInfo.put("unde_numb_cont", iUndeNumbCont); // 선급지급(예정)전체회수
			
			List<Map<String,Object>> prePayList = (List<Map<String,Object>>)bondInfo.get("prePayList");
			if(prePayList != null) {
				for(Map<String,Object> row: prePayList) {
					row.put("unde_sequ_cont", ((BigDecimal)row.get("unde_sequ_cont")).intValue()); // 선급지급(예정)전체회수
					row.put("unde_sequ_amnt", ((BigDecimal)row.get("unde_sequ_amnt")).intValue()); // 선금지급(예정)금액
				}
			}
			bondInfo.put("paym_term_text", contTermText); // 계약기간 = 계약종료일자-계약시작일자(계약기간 총 일수)
		}
		
		bondInfo.put("cont_term_text", contTermText);
		bondInfo.put("prep_term_text", contTermText);
        bondInfo.put("morg_term_text", bondTermText);
		
		String corpRegNo = (String)bondInfo.get("corp_reg_no");
		corpRegNo = corpRegNo.replace("-", "");
		
		bondInfo.put("cred_orga_numb", corpRegNo);
		bondInfo.put("cred_orps_iden", bpBizRegNo);
		
		String spBizRegNo = (String)bondInfo.get("sp_bizregno");
		spBizRegNo = spBizRegNo.replace("-", "");
		bondInfo.put("appl_orps_iden", spBizRegNo);
		
		String bpPostNo = (String)bondInfo.get("bp_zipcd");
		bpPostNo = bpPostNo.replace("-", "");
		bondInfo.put("cred_orga_post", bpPostNo);
		
		String spPostNo = (String)bondInfo.get("sp_zipcd");
		if(!Strings.isNullOrEmpty(spPostNo)) {
			spPostNo = spPostNo.replace("-", "");
		} else {
			spPostNo = "";
		}
		
		bondInfo.put("appl_orga_post", spPostNo);
		
		String deptNm = (String)bondInfo.get("dept_nm");
		if(Strings.isNullOrEmpty(deptNm)) {
			bondInfo.put("cred_dept_name", "none");
		} else {
			bondInfo.put("cred_dept_name", deptNm);
		}
		
		BigDecimal bdContAsgnRate = (BigDecimal)bondInfo.get("cont_asgn_rate");
		
		if(bdContAsgnRate == null) {
			bondInfo.put("cont_asgn_rate", 0);	
		} else {
			Double dContAsgnRate = bdContAsgnRate.doubleValue();
			dContAsgnRate = (Math.round(dContAsgnRate*100) / 1000.0);
			bondInfo.put("cont_asgn_rate", dContAsgnRate);
		}
		
		BigDecimal bdcontPricRate = (BigDecimal)bondInfo.get("cont_pric_rate");
		if(bdcontPricRate == null) {
			bondInfo.put("cont_pric_rate", 0);	
		} else {
			Double dContPricRate = bdcontPricRate.doubleValue();
			dContPricRate = (Math.round(dContPricRate*1000d) / 1000d);
			bondInfo.put("cont_pric_rate", dContPricRate);
		}
		
		BigDecimal bdMorgPricRate = (BigDecimal)bondInfo.get("morg_pric_rate");
		
		if(bdMorgPricRate == null) {
			bondInfo.put("morg_pric_rate", 0);	
		} else {
			Double dMorgPricRate = bdMorgPricRate.doubleValue();
			dMorgPricRate = (Math.round(dMorgPricRate*1000d) / 1000d);
			bondInfo.put("morg_pric_rate", dMorgPricRate);
		}
		
		BigDecimal bdPrepPricRate = (BigDecimal)bondInfo.get("prep_pric_rate");
		
		if(bdPrepPricRate == null) {
			bondInfo.put("prep_pric_rate", 0);	
		} else {
			Double dPrepPricRate = bdPrepPricRate.doubleValue();
			dPrepPricRate = (Math.round(dPrepPricRate*1000d) / 1000d);
			bondInfo.put("prep_pric_rate", dPrepPricRate);
		}
		
		BigDecimal bdGrntRate = (BigDecimal)bondInfo.get("gur_ro");
		
		if(bdGrntRate == null) {
			bondInfo.put("gur_ro", 0);
		} else {
			Double dGrntRate = bdGrntRate.doubleValue();
			dGrntRate = (Math.round(dGrntRate*1000d) / 1000d);
			bondInfo.put("gur_ro", dGrntRate);
		}
		
		Iterator i = bondInfo.keySet().iterator();
		while(i.hasNext()) {
			String key = (String)i.next();
			Object value = bondInfo.get(key);
			LOG.info(key + "=" + value);
		}
		return bondInfo; 
	}
	
	/**
	 * 최종응답서 인터페이스 정의서에 맞게 변경
	 * 
	 * @param param
	 * @return the map
	 */
	public static Map setBondDataToIFResponseDevinition(Map bondInfo){
		
		String docuNumbText = "";
		String headDocuNumb = (String)bondInfo.get("head_docu_numb");
		docuNumbText = headDocuNumb.substring(6,headDocuNumb.length());
		
		bondInfo.put("docu_numb_text", docuNumbText);
		
		String docuKindCode = "";
		String headRefrNumb = (String)bondInfo.get("head_docu_numb");
		docuKindCode = headRefrNumb.substring(3,6);
		
		bondInfo.put("docu_kind_code", docuKindCode);
		
		BigDecimal contMainAmnt = (BigDecimal)bondInfo.get("cont_main_amnt");
		BigDecimal bContMainAmnt = contMainAmnt.setScale(0, RoundingMode.FLOOR); // 소수점 절삭
		
		bondInfo.put("cont_main_amnt", bContMainAmnt);
		
		return bondInfo;
	}
	
	/**
	 * 보증서 수신 후 후처리 하기 위한 map 변수 셋팅
	 * 
	 * @param param
	 * @return the map
	 */
	public static Map setRecvBondData(Map bondInfo){
		
		String bondNumbText = (String)bondInfo.get("bond_numb_text");
		
		bondNumbText = bondNumbText.substring(6, bondNumbText.length()-3);
		bondInfo.put("gur_bond_no", bondNumbText);
		
		String contNumbText = (String)bondInfo.get("cont_numb_text");
		bondInfo.put("cntr_no", contNumbText.substring(0, contNumbText.length()-2));
		int ecntrRevNo = Integer.parseInt(contNumbText.substring(contNumbText.length()-2, contNumbText.length()));
		bondInfo.put("cntr_revno", ecntrRevNo);

		return bondInfo;
	}

	public static Map setBondDataStringToInteger(Map bondInfo) {

		String bondPenlAmnt = (String) bondInfo.get("bond_penl_amnt");  // 보증금액
		int newBondPenlAmnt = Integer.parseInt(bondPenlAmnt);
		bondInfo.put("bond_penl_amnt", newBondPenlAmnt);

		String bondPremAmnt = (String) bondInfo.get("bond_prem_amnt");  // 보험료
		int newBondPremAmnt = Integer.parseInt(bondPremAmnt);
		bondInfo.put("bond_prem_amnt", newBondPremAmnt);

		String contMainAmnt = (String) bondInfo.get("cont_main_amnt");  // 계약금액
		int newContMainAmnt = Integer.parseInt(contMainAmnt);
		bondInfo.put("cont_main_amnt", newContMainAmnt);

		String bondPricRate = (String) bondInfo.get("bond_pric_rate");  // 보증금율
		Double newBondPricRate = Double.parseDouble(bondPricRate);
		bondInfo.put("bond_pric_rate", newBondPricRate);

		/* 선급금 이행 보증 */
		if(ElecGuaranteeConst.PREINF.equals(bondInfo.get("mesg_type"))) {
			String contPaymAmnt = (String) bondInfo.get("cont_paym_amnt");  // 선금액
			int newContPaymAmnt = Integer.parseInt(contPaymAmnt);
			bondInfo.put("cont_paym_amnt", newContPaymAmnt);
		}

		return bondInfo;
	}
}
