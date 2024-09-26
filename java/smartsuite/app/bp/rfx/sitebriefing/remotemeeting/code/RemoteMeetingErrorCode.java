package smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code;

public enum RemoteMeetingErrorCode {

    ERROR_CODE("STD.E9999", "오류가 발생하였습니다.<br/>관리자에게 문의하세요.", "FAIL"),
    NOT_APP_ID_REQUEST("STD.RM0004", "현장설명회의 정보가 없어, 회의 예약이 불가능 합니다. <br/>관리자에게 문의하세요.", "FAIL");

    private final String messageCode;
    private final String defaultMessage;
    private final String errorCode;

    RemoteMeetingErrorCode(String messageCode, String defaultMessage, String errorCode) {
        this.messageCode = messageCode;
        this.defaultMessage = defaultMessage;
        this.errorCode = errorCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
