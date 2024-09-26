package smartsuite.app.bp.commonEval;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import com.google.common.collect.Maps;

@SuppressWarnings ("serial")
public class EvalException extends Exception {
	public Map<String, Object> errInfo = Maps.newHashMap();
	
	public EvalException() {
		super();
	}
	
	private void setErrInfo(final String message){
		final Map<String, Object> errInfo = this.errInfo;
		errInfo.put("ret_msg", MapUtils.getString(errInfo, "ret_msg", message));
	} 

	public EvalException(final String message) {
		super(message);
		this.setErrInfo(message);
	}

	public EvalException(final Throwable cause) {
		super(cause);
	}

	public EvalException(final String message, final Throwable cause) {
		super(message, cause);
		this.setErrInfo(message);
	}

	public EvalException(final String message, final Map<String, Object> errInfo) {
		super(message);
		this.errInfo = errInfo;
		this.setErrInfo(message);
	}
	
//	public SrmException(final String message, final Throwable cause, final Boolean enableSuppression, final Boolean writableStackTrace) {
//		super(message, cause, enableSuppression, writableStackTrace);
//	}
}
