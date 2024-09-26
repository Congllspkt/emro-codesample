package smartsuite.app.bp.pro.shared;

public class ProConst {

	/**구매 유형 공통코드 (P045): 수량*/
	final public static String PURC_TYP_CCD_MT = "QTY";
	/**구매 유형 공통코드 (P045) : 공사/용역 구매*/
	final public static String PURC_TYP_CCD_CT = "CONSTSVC";
	
	/**구매요청 진행 상태 (P044) : 구매요청저장 */
	final public static String PR_STS_CCD_RN = "PR_CRNG";
	/**구매요청 진행 상태 (P044) : 구매요청품의상신 */
	final public static String PR_STS_CCD_RP = "PR_APVL_RPTG";
	/**구매요청 진행 상태 (P044) : 구매요청품의반려 */
	final public static String PR_STS_CCD_RR = "PR_APVL_RET";
	/**구매요청 진행 상태 (P044) : 접수대기 */
	final public static String PR_STS_CCD_RW = "PR_RCPT_WTG";
	/**구매요청 진행 상태 (P044) : 접수 */
	final public static String PR_STS_CCD_RV = "PR_RCPT";
	/**구매요청 진행 상태 (P044) : 구매반려 */
	final public static String PR_STS_CCD_RD = "PR_RET";
	/**구매요청 진행상태  (P044): 구매요청변경저장 */
	final public static String PR_STS_CCD_RQ = "PR_CHG_CRNG";
	/**구매요청 진행상태  (P044): 구매요청변경품의상신 */
	final public static String PR_STS_CCD_RA = "PR_CHG_APVL_RPTG";
	/**구매요청 진행상태 (P044) : 구매요청변경품의반려 */
	final public static String PR_STS_CCD_RB = "PR_CHG_APVL_RET";
	
	
	/** 구매요청 변경 상태 그룹*/
	final public static String PR_PROG_STS_RM_RX_GROUP_STS = "PRRM";
	
	/**구매요청 진행상태 : 변경승인후 변경 대상건 삭제상태*/
	final public static String PR_PROG_STS_RX = "RX";
	
	/**구매요청 진행상태 : 변경승인후 변경대상건 변경상태*/
	final public static String PR_PROG_STS_RM = "PR_CHG";
			
	/**변경 요청 진행 상태 (P040): 변경 가능*/
	final public static String PR_CHG_YN_N = "N";
	/**구매요청변경 : 변경 건*/
	final public static String PR_CHG_YN_Y = "Y";
	
	
	/**구매요청 진행 상태 : RFI저장 */
	final public static String PR_PROG_STS_IN = "RFI_CRNG";
	/**구매요청 진행 상태 : RFI완료 */
	final public static String PR_PROG_STS_IC = "RFI_CMPLD";
	
	/**구매요청 진행 상태 : RFx저장 */
	final public static String PR_PROG_STS_QN = "RFX_CRNG";
	/**구매요청 진행 상태 : RFx품의상신 */
	final public static String PR_PROG_STS_QP = "RFX_APVL_RPTG";
	/**구매요청 진행 상태 : RFx품의반려 */
	final public static String PR_PROG_STS_QB = "RFX_APVL_RET";
	/**구매요청 진행 상태 : RFx업체선정중 */
	final public static String PR_PROG_STS_VS = "VS";
	/**구매요청 진행 상태 : RFx업체선정품의상신 */
	final public static String PR_PROG_STS_VP = "VP";
	/**구매요청 진행 상태 : RFx업체선정품의반려 */
	final public static String PR_PROG_STS_VB = "VB";
	
	/**구매요청 진행 상태 : 발주대기 */
	final public static String PR_PROG_STS_PW = "PW";
	/**구매요청 진행 상태 : 발주저장 */
	final public static String PR_PROG_STS_PN = "PN";
	/**구매요청 진행 상태 : 발주품의상신 */
	final public static String PR_PROG_STS_PP = "PP";
	/**구매요청 진행 상태 : 발주품의반려 */
	final public static String PR_PROG_STS_PR = "PR";
	
	
	/**구매요청 진행 상태 : 계약대기 */
	final public static String PR_PROG_STS_CW = "CW";
	
	
	/**결재상태 : 미상신 건*/
	final public static String PR_APRV_STS_T = "T";
	/**결재 상태 : 승인건(결재없이 승인처리)*/
	final public static String PR_APRV_STS_K = "K";
	/**결재상태 : 승인*/
	final public static String PR_APRV_STS_C = "C";
	/**결재 상태 : 반려*/
	final public static String PR_APRV_STS_R = "R";
	/**결재 상태 : 결재 취소*/
	final public static String PR_APRV_STS_D = "D";
	
	
	final public static String PR_ITEM_STS_ERR = "PR_ITEM_STS_ERR";

	final public static String UPCR_ITEM_STS_ERR = "UPCR_ITEM_STS_ERR";
	final public static String RFX_END = "RFX_END";
	final public static String RFX_REJECT = "RFX_REJECT";
	final public static String QTA_SUBMIT = "QTA_SUBMIT";
	final public static String RFX_CLOSE_BYPASS_ERR = "RFX_CLOSE_BYPASS_ERR";
	final public static String RFP_NON_PRI_EVAL_SET_INCOMPELETED = "RFP_NON_PRI_EVAL_SET_INCOMPELETED";
	final public static String RFP_NO_EVALUATOR = "RFP_NO_EVALUATOR";
	final public static String RFX_PRI_FACT_SET_INCOMPELETED = "RFX_PRI_FACT_SET_INCOMPELETED";
	final public static String CS_INCOMPELETED = "CS_INCOMPELETED";
	final public static String CS_NON_REP_VENDOR = "CS_NON_REP_VENDOR";
	final public static String HAS_QTA = "HAS_QTA";
	
	final public static String AUCTION_AMT_CHECK_ERR = "AUCTION_AMT_CHECK_ERR";
	final public static String RFP_EVAL_INCOMPELETED = "RFP_EVAL_INCOMPELETED";
	final public static String RFX_BEFORE_OPEN_TIME = "RFX_BEFORE_OPEN_TIME";
	
	final public static String HAS_GR_ITEM = "HAS_GR_ITEM";
	final public static String GR_QTY_ERR = "GR_QTY_ERR";
	final public static String ASN_QTY_ERR = "ASN_QTY_ERR";
	final public static String PO_STATE_ERR = "PO_STATE_ERR";
	final public static String PO_CHANGE_PROGRESS = "PO_CHANGE_PROGRESS";
	final public static String PO_COMPLETE = "PO_COMPLETE";
	final public static String GR_STATE_ERR = "GR_STATE_ERR";
	final public static String IV_STATE_ERR = "IV_STATE_ERR";
	
}
