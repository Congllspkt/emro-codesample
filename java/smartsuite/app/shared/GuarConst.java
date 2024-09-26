package smartsuite.app.shared;

public class GuarConst {

	/** 보증상태 (D011) */
	public static class GUAR_STATUS {
		public static final String REQUEST_WAITING = "GUR_REQ_WTG";
		public static final String PUBLIC_WAITING = "PUBL_WTG";
		public static final String RECEIPT_WAITING = "RCPT_WTG";
		public static final String APPROVAL_WAITING = "APVL_WTG";
		public static final String APPROVED = "APVD";
		public static final String DESTRUCTION = "DESTR";
		public static final String RECEIPT_REJECTION = "RCPT_RET";
	}
	/** 보증 첨부 유형 (D029) */
	public static class ATTACH_TYPE {
		public static final String ELECTRONIC_GUARANTEE = "EGUR";
		public static final String OFFLINE = "OFFL";

	}

	/** 보증 보험사 (D013) */
	public static class INSURANCE_COMPANY {
		/** 서울보증보험 */
		public static final String SGI = "SGI";
		/** 소프트공제조합 */
		public static final String KSFC = "KSFC";

	}

	/** 보증 유형 (D010) */
	public static class GUARANTEE_TYPE {
		public static final String CONTRACT_PERFORMANCE_GUARANTEE = "CPGUR";
		public static final String ADVANCE_PAYMENTPERFORMANCE_GUARANTEE = "APYMTPGUR";
		public static final String DEFECT_PERFORMANCE_GAURANTEE = "DEFPGUR";
		public static final String PAYMENT_PERFORMANCE_GAURANTEE = "PYMTPGUR";

	}


}
