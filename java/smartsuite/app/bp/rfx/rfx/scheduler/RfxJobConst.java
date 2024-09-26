package smartsuite.app.bp.rfx.rfx.scheduler;

/**
 * RFX 스케줄러 상수 Class 입니다.
 *
 * @author Yeon-u Kim
 */
public class RfxJobConst {
	
	/** The Constant RFX_SERVICE_CLASS_NAME. */
	public static final String RFX_SERVICE_CLASS_NAME = "smartsuite.app.bp.rfx.rfx.service.RfxService";
	
	/** The Constant RFX_JOB_GROUP. */
	public static final String RFX_JOB_GROUP = "RFX";
	
	/** The Constant RFX_START_JOB_NAME. */
	public static final String RFX_START_JOB_NAME = "RFX_START";
	
	/** The Constant RFX_START_METHOD_NAME. */
	public static final String RFX_START_METHOD_NAME = "startRfx";

	/** The Constant RFX_CLOSE_JOB_NAME. */
	public static final String RFX_CLOSE_JOB_NAME = "RFX_CLOSE";
	
	/** The Constant RFX_CLOSE_METHOD_NAME. */
	public static final String RFX_CLOSE_METHOD_NAME = "closeRfx";
	
	/** The Constant RFX_EVAL_END_JOB_NAME. */
	public static final String RFX_EVAL_END_JOB_NAME = "RFX_EVAL_END";
	
	/** The Constant RFX_EVAL_END_METHOD_NAME. */
	public static final String RFX_EVAL_END_METHOD_NAME = "endRfxEval";
	
	/** 자동연장 최대 횟수 */
	public static final int MAX_AUTO_EXT_COUNT = 3;
	
}
