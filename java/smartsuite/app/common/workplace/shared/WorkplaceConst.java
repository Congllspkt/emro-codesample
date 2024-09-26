package smartsuite.app.common.workplace.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkplaceConst {

	/** title 예외 처리 대상(PR 품목) work id */
	final public static List<String> EX_TITLE_PR_ITEM_WORK_IDS = new ArrayList<String>(
			Arrays.asList("WRK_BP01_00005")); // 접수대기
	
	/** title 예외 처리 대상(구매 평가) work id */
	final public static List<String> EX_TITLE_PRO_EVAL_WORK_IDS = new ArrayList<String>(
			Arrays.asList("WRK_BP04_00022", "WRK_BP04_00221"
					, "WRK_BP09_00391", "WRK_BP09_00392"
					, "WRK_BP27_00103", "WRK_BP27_00106")); // RFx평가대기, RFx평가진행, 검수평가대기, 검수평가중, 기성평가대기, 기성평가중 
	
	/** title 예외 처리 대상(발주 품목) work id */
	final public static List<String> EX_TITLE_PO_ITEM_WORK_IDS = new ArrayList<String>(
			Arrays.asList("WRK_BP09_00104", "WRK_SP24_00108")); // 입고-납품대기(물품 발주)
	
	/** due dt 예외 처리 대상(PR) work id */
	final public static List<String> EX_CLOSE_DT_PR_WORK_IDS = new ArrayList<String>(
			Arrays.asList("WRK_BP01_00001", "WRK_BP01_00002", "WRK_BP01_00003", "WRK_BP01_00005"));
	
	/** due dt 예외 처리 대상(RFx 진행) work id */
	final public static List<String> EX_CLOSE_DT_RFX_START_WORK_IDS = new ArrayList<String>(
			Arrays.asList("WRK_BP04_00008", "WRK_BP04_00009", "WRK_BP04_00011", "WRK_BP06_00015", "WRK_BP06_00016", "WRK_BP06_00018"));
	
	/** due dt 예외 처리 대상(RFx 마감) work id */
	final public static List<String> EX_CLOSE_DT_RFX_CLOSE_WORK_IDS = new ArrayList<String>(
			Arrays.asList("WRK_BP04_00013", "WRK_BP04_00014", "WRK_BP06_00020", "WRK_BP06_00021"));
	
	/** due dt 예외 처리 대상(발주) work id */
	final public static List<String> EX_CLOSE_DT_PO_WORK_IDS = new ArrayList<String>(
			Arrays.asList("WRK_BP08_00028", "WRK_BP08_00029", "WRK_BP08_00030", "WRK_BP08_00031", "WRK_BP08_00032", "WRK_BP08_00033", "WRK_BP08_00034"
					, "WRK_SP23_00007"));
	
	/** due dt 예외 처리 대상(기성 납품대기) work id */
	final public static List<String> EX_CLOSE_DT_AR_READY_WORK_IDS = new ArrayList<String>(
			Arrays.asList("WRK_BP27_00105", "WRK_SP28_00107"));
	
	/** due dt 해당 컬럼이 DATE 타입인 대상 */
	final public static List<String> EX_CLOSE_DT_TYPE_DATE_WORK_IDS = new ArrayList<String>(
			Arrays.asList("WRK_BP02_00006", "WRK_BP03_00007", "WRK_BP03_00008", "WRK_BP04_00008", "WRK_BP04_00009", "WRK_BP04_00011", "WRK_BP04_00012", "WRK_BP06_00015", "WRK_BP06_00016", "WRK_BP06_00018", "WRK_BP06_00019"
					, "WRK_SP20_00001", "WRK_SP20_00002", "WRK_SP21_00003", "WRK_SP21_00004", "WRK_SP22_00005", "WRK_SP22_00006"));
	
}