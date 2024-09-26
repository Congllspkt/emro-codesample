package smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code;

import java.util.HashMap;
import java.util.Map;

public enum RemoteMeetingReturnCode {

    EXCEPTION_ERROR("40999","알 수 없는 에러가 발생하였습니다. 관리자에게 문의해주세요"),
    NOT_RESERVATION_TIME_ERROR("40740", "화상회의를 예약 가능한 시간이 아닙니다."),
    THE_WRONG_APPROACH_FOR_ENTRY_ERROR("40401","잘못된 접근입니다. 브라우저 종료 후 다시 시도해주세요."),
    ALREADY_DELETE_RESERVATION_ERROR("40735","이미 삭제된 예약입니다."),
    REQUEST_IS_NOT_VALID_ERROR("40400","올바르지 않은 형식으로 요청되었습니다."),
    REQUEST_CONVERT_RECORDING_ERROR("40300","녹화 파일 변환 요청이 실패하였습니다."),
    MATCHING_NOT_DATA_ERROR("40993","조건에 맞는 데이터가 없습니다."),
    CANT_RESERVATION_TIME_ERROR("40070","현재 시간에 예약을 할 수 없습니다. 예약시간을 조정해주세요."),
    SERVER_CONNECTION_TIME_OUT_ERROR("40904","서버 연결이 실패하였습니다.");


    private final String errorCode;
    private final String returnMessage;

    RemoteMeetingReturnCode(String errorCode, String returnMessage) {
        this.errorCode = errorCode;
        this.returnMessage = returnMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public static Map<String,Object> getFindErrorMap(String errorCode) {
        Map<String,Object> returnErrorMap = new HashMap<String, Object>();

        for(RemoteMeetingReturnCode codeInfo : RemoteMeetingReturnCode.values()){
            if(errorCode.equals(codeInfo.getErrorCode())){
                returnErrorMap.put("errorMessage",codeInfo.getReturnMessage());
                returnErrorMap.put("errorCode",codeInfo.getErrorCode());
            }
        }
        return returnErrorMap;
    }

}
