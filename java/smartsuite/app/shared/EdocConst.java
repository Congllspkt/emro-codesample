package smartsuite.app.shared;

public class EdocConst {

	/** PDF파일이 생성되어 있다.  */
	public static final String EXSIT_PDF_FILE  = "E";
	
	/** PDF파일이 생성되어 있지 않다.  */
	public static final String NOT_EXSIT_PDF_FILE  = "NE";
	
	/** 서식내용에 삭제 대상 속성 명칭  */
	public static final String DELETE_TARGET  = "deletetarget";

	/** 동적 생성을 위한 필요한 데이터 목록 (계약항목정보,template내용,template 맵핑될 데이터) 맵핑될 데이터) 서식에 맵핑될 동적 계약항목이 여러개인경우 */
	public static final String DYNAMIC_TEMPLATE_LIST  = "dynamicTmpList";
	
	/** 동적 생성을 위한 필요한 데이터  (계약항목정보,template내용,template 맵핑될 데이터) 서식에 맵핑될 동적 계약항목이 1개인경우 */
	public static final String DYNAMIC_TEMPLATE_INFORMATION  = "dynamicTmpInfo";
	
	/** 동적으로 삭제해야할 데이터 */
	public static final String DELETE_TAG_INFORMATION  = "deleteTagInfo";

	/** 항목타입(그룹코드 : D012) : TEMPLATE 타입 (TEMPLATE) */
	public static final String CONTRACT_ITEM_BOX_TYP_TEMPLATE  = "TMPL";

	/** input tag checked  */
	public static final String CHECKED  = "checked";
}
