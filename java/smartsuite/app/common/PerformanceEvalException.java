package smartsuite.app.common;

import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

@SuppressWarnings ("serial")
public class PerformanceEvalException extends Exception {
	public Map<String, Object> errInfo = Maps.newHashMap();

	public PerformanceEvalException() {
		super();
	}

	private void setErrInfo(final String message){
		final Map<String, Object> errInfo = this.errInfo;
		errInfo.put("ret_msg", MapUtils.getString(errInfo, "ret_msg", message));
	}

	public PerformanceEvalException(final String message) {
		super(message);
		this.setErrInfo(message);
	}

	public PerformanceEvalException(final Throwable cause) {
		super(cause);
	}

	public PerformanceEvalException(final String message, final Throwable cause) {
		super(message, cause);
		this.setErrInfo(message);
	}

	public PerformanceEvalException(final String message, final Map<String, Object> errInfo) {
		super(message);
		this.errInfo = errInfo;
		this.setErrInfo(message);
	}

}
