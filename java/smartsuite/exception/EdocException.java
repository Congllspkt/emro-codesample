package smartsuite.exception;

public class EdocException extends RuntimeException{
	
	private String errorCause;
	private String parameter;
	private String errorCode;
	
	public EdocException(){
		super();
	}
	
	public EdocException(final String message){
		super(message);
	}
	
	public EdocException(final String message, final String errorCause){
		super(message);
		this.errorCause = errorCause;
	}
	
	public EdocException(final String message, final String errorCause, final String parameter){
		super(message);
		this.errorCause = errorCause;
		this.parameter = parameter;
	}
	
	public EdocException(final String message, final String errorCause, final String parameter, final String errorCode){
		super(message);
		this.errorCause = errorCause;
		this.parameter = parameter;
		this.errorCode = errorCode;
	}

	public String getErrorCause() {
		return errorCause;
	}

	public void setErrorCause(String errorCause) {
		this.errorCause = errorCause;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	
}
