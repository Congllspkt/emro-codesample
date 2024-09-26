package smartsuite.app.shared;

public class ElecGuaranteeConst {

	// 보증보험 응답 코드
	final public static String ACK_CD_SUCCESS = "SA";
	final public static String ACK_CD_FAIL = "SR";
	
	// 보증보험 진행상태
	/**보증 대기 상태(발행대기)**/
	final public static String GUAR_READY = "PUBL_WTG";
	/**보증을 요청한 상태(수신대기)**/
	final public static String GUAR_REQUEST = "RCPT_WTG";
	/**승인 상태(승인)**/
	final public static String GUAR_APPROVE = "APVD";
	/**반려 상태(반려)**/
	final public static String GUAR_REJECT = "RCPT_RET";
	/**보증서 발급받은 상태(승인대기)**/
	final public static String GUAR_RECEIVE = "APVL_WTG";
	/**발급 받은 보증서 완료 후 파기 한 상태(파기)**/
	final public static String GUAR_DESTROY = "DESTR";
	
	// 보증보험사 종류
	/**서울보증보험**/
	final public static String SGI = "SGI";
	/**소프트공제조합**/
	final public static String KSFC = "KSFC";
	
	// 보증 구분
	/** 계약이행보증 **/
	final public static String CON = "002";
	/** 선금급이행보증 **/
	final public static String PRE = "004";
	/** 하자이행보증 **/
	final public static String FLR = "003";
	
	// 보증 명칭
	/** 계약이행보증 **/
	final public static String CONINF = "CONINF";
	/** 선급금이행보증 **/
	final public static String PREINF = "PREINF";
	/** 하자이행보증 **/
	final public static String FLRINF = "FLRINF";
	/** 지급보증 **/
	final public static String PAYINF = "PAYINF";
}
