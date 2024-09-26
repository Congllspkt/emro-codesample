package smartsuite.app.cxfapi.po.bean;

public class WsResult {

	String resultStatus;
	
	String resultCode;
	
	String resultMessage;
	
	String resultData;
	
	public WsResult(String resultStatus, String resultMessage, String resultData) {
		this.resultStatus = resultStatus;
        this.resultMessage = resultMessage;
        this.resultData = resultData;
	}
	
	public String getResultStatus() {
		return resultStatus;
	}
	
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	
	public String getResultCode() {
		return resultCode;
	}
	
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	
	public String getResultMessage() {
		return resultMessage;
	}
	
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	
	public String getResultData() {
		return resultData;
	}
	
	public void setResultData(String resultData) {
		this.resultData = resultData;
	}
}
