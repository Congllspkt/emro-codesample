package smartsuite.app.shared;

public class CntrConst {

	/**
	 * 이전 업무 유형 (D030)
	 **/
	public static class PRE_REQ_TYPE {
		public static final String RFX = "RFX";
		public static final String ONBOARDING = "OBD";
		public static final String PO = "PO";
	}

	/**
	 * 계약 업무 유형 (D025) CNTR_REQ_TYPE, CNTR_TYPE
	 **/
	public class CNTR_TYPE {
		public static final String NEW = "NEW";
		public static final String CHANGE = "CHG";
		public static final String TERMINATION = "TRMN";
	}

	/**
	 * 계약 요청 접수 상태 (D026)
	 **/
	public class REQ_RCPT_STATUS {
		public static final String RECEIPT_WAITING = "RCPT_WTG";
		public static final String RECEIPT = "RCPT";
		public static final String RETURN = "RET";
		public static final String CONTRACT_PROGRESSING = "CNTR_PRGSG";
	}

	/**
	 * 계약서 유형 (D003)
	 **/
	public static class CNTRDOC_TYPE {
		public static final String ELEMENTARY = "ELEM_CNTRDOC";
		public static final String UNIT_PRICE = "UPRC_AGT";
		public static final String PO = "PO_CNTRDOC";
		public static final String GENERAL = "GEN_CNTRDOC";
	}

	/**
	 * 계약 서명방법 (D020)
	 **/
	public static class SIGN_METHOD {
		public static final String PKI = "PKI";
		public static final String DOCUSIGN = "DOCUSIGN";
		public static final String EFORM = "EFORM";
		public static final String OFFLINE = "OFFLINE";
		public static final String ADOBESIGN = "ADOBESIGN";
	}

	/**
	 * 계약 템플릿 유형 (D022)
	 **/
	public static class TMPL_TYPE {
		public static final String TEMPLATE = "TMPL";
		public static final String USR_FILE = "USR_FILE";
	}

	/**
	 * 계약 상태 (D002)
	 **/
	public static class CNTR_STATUS {
		public static final String CREATION = "CCMPLD";
		public static final String APPX_REQUEST = "APPX_REQ";
		public static final String APPENDIX_VD_SAVE = "APPX_RVG";
		public static final String APPX_REJECT = "APPX_RET";
		public static final String APPROVAL = "APVL_PRGSG";
		public static final String SIGN_WAITING = "SGN_WTG";
		public static final String SEND = "SND";
		public static final String VENDOR_CONFIRM = "VD_RCPT_CONFM";
		public static final String MULTILATERAL_SIGN = "MULTLT_SGN";
		public static final String VENDOR_SIGN_COMPLETE = "VD_SGN_CMPLD";
		public static final String TERMINATION = "CNTR_TRMN";
		public static final String VENDOR_REJECT = "VD_RET";
		public static final String BUYER_REJECT = "RET";
		public static final String COMPLETE = "CNTR_CMPLD";
	}

	/**
	 * 협력사 서명 상태
	 **/
	public static class VENDORS_SIGN_STATUS {
		public static final String ALL_SIGN = "A";
		public static final String ALL_NOT_SIGN = "NA";
		public static final String SIGNING = "SI";
		public static final String FIRST_SIGN = "FS";
	}

	/**
	 * 서명유형 (D009)
	 */
	public static class CODE {
		public static final String USE_YN_COMMON_CODE = "D001";
		public static final String DEFAULT_SIGN_ORDER_COMMON_CODE = "D028";
	}

	/**
	 * 사용자 유형 (C016)
	 */
	public static class USR_TYPE {
		public static final String BUYER = "BUYER";
		public static final String VENDOR = "VD";
	}

	/**
	 * 인지세납부 상태 (D027)
	 */
	public static class STTPYMT_STATUS {
		public static final String WAITING = "WTG";
		public static final String PROGRESS = "PRGS";
		public static final String COMPLETED = "CMPLD";
	}

	/**
	 * 인지세납부방법 (D016)
	 */
	public static class STTPYMT_METHOD {
		public static final String OFFLINE = "OFFL";
		public static final String ELEC_STAMP_TAX = "ESTAX";
	}

	/**
	 * 인지세납부자 비율 유형 (D016)
	 */
	public static class STTPYMT_RATIO {
		public static final String BUYER_100 = "BUYER";
		public static final String VENDOR_100 = "VD";
		public static final String HALF_HALF = "BUYER_VD";
	}

	/**
	 * 보증 종류 (P064)
	 */
	public static class GUARANTEE_KIND {
		public static final String GUARANTEE_INSURANCE = "GUR_INS";
		public static final String PROMISSORY_NOTE = "PN";
		public static final String CASH = "CASH";
		public static final String PAYMENT_MEMORANDUM = "PYMT_MEMRD";
		public static final String BANK_PAYMENT_GUARANTEE = "BNK_PYMT_GUR";
	}

	/**
	 * 지급 유형 (P091)
	 */
	public static class PAYMENT_TYPE {
		public static final String ADVANCE_PAYMENT = "APYMT";
		public static final String INTERMEDIATE_PAYMENT = "IPYMT";
		public static final String BALANCE = "BAL";

	}

	/**
	 * 데이터 타입 (D006)
	 */
	public static class CLAUSE_DAT_TYP {
		public static final String STRING = "STR";
		public static final String DSCRIPT = "DSCRT";

		public static final String NUMBER = "NUMC";
		public static final String YYYYMMDD_TYPE1 = "YYYY년 MM월 DD일";
		public static final String YYYYMMDD_TYPE2 = "YYYY/MM/DD";
		public static final String MMDDYYYY_TYPE1 = "MM/DD/YYYY";
		public static final String YYYYMMDD_TYPE3 = "YYYY.MM.DD";
		public static final String YYYYMMDD_TYPE4 = "YYYY-MM-DD";
		public static final String TEMPLATE = "TMPL";
		public static final String CHECKBOX = "CHECK";

	}
	
	/**
	 * 인지세 구간 (D017)
	 * 최소 금액 (MIN)
	 */
	public static class STAX_RANGE_MIN_AMT {
		public static final int STAX_20000 = 10000000;
		public static final int STAX_40000 = 30000000;
		public static final int STAX_70000 = 50000000;
		public static final int STAX_150000 = 100000000;
		public static final int STAX_350000 = 1000000000;
	}

	/**
	 * 서명 잠금 상태
	 */
	public static class SGN_LCKD_STS {
		/** 서명 가능 */
		public static final String SIGNABLE = "Y";

		/** 다른 협력사가 서명을 진행중이다.  */
		public static final String SOMEONE_ELSE_SIGN_USING  = "N";

		/** 협력사가 서명 버튼 클릭 후 서명 잠금상태  */
		public static final String SIGNED_LOCK = "SIGNED_LOCK";

	}

	public static class CCD {
		/** 하자이행보증 기간 유형 */
		public static final String DEFPGUR_PD_TYP_CCD = "P048";

		/** 납품 방법 */
		public static final String DLVYMETH_CCD = "P010";

		/** 지급 방법 */
		public static final String PYMTMETH_CCD = "P009";
	}

	
}
