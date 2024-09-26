package smartsuite.app.shared;

public class DocusignConst {

	/** docusign 서명그룹 구분 */
	public static final String BUYER_SIGN_GROUP_NAME = "GN_BUYER";
	/** docusign 서명그룹 구분 */
	public static final String SUPPLIER_SIGN_GROUP_NAME = "GN_SUPPLIER";
	
	/** 유저 구분 (구매사)*/
	public static final String BUYER_USR_CLS = "BUYER";
	/** 유저 구분 (협력사)*/
	public static final String SUPPLIER_USR_CLS = "VD";
	
	/** docusign api 상태 : TEMPLATE 생성 */
	public static final String DS_BUYER_CREATE_TEMPLATE = "bp_temp";
	/** docusign api 상태 : TEMPLATE 생성 */
	public static final String DS_SUPPLIER_CREATE_TEMPLATE = "sp_temp";
	/** docusign api 상태 : 봉투 전송 */
	public static final String SENT = "sent";
	
	/** docusign envelope event 상태 : 거부 */
	public static final String DECLINE = "decline";
	/** docusign envelope event 상태  : 서명 */
	public static final String SIGNING_COMPLETE = "signing_complete";
	
	/** docusign template event 상태  : 취소 */
	public static final String TEMP_CANCEL = "Cancel";
	/** docusign template event 상태  : 저장 */
	public static final String TEMP_SAVE = "Save";
	
	/** docusign document 순서 : 메인 계약서 순서 */
	public static final String CNTR_ORDER_NUMBER = "1";
	
}
