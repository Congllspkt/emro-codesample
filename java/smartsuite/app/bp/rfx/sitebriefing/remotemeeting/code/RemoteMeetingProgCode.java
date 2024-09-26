package smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code;

import java.util.HashMap;
import java.util.Map;

public enum RemoteMeetingProgCode {
	
    REMOTE_MEETING_CONFERENCE_START("VCONF_CRN","회의 생성"),
    REMOTE_MEETING_CONFERENCE_END("VCONF_ED","회의 종료"),
    REMOTE_MEETING_RESERVATION_CONFERENCE_END("VCONF_RSVN_ED","예약 회의 종료"),
    REMOTE_MEETING_CONFERENCE_NOT_START("VCONF_CRN_WTG","회의 생성 대기중"),
    REMOTE_MEETING_STATUS_NULL("","비대면 화상회의 진행 안함"),

    //회의 공유 비공유는 prog code로 넣는게 적합한지 모르겠음. 일단 관련 코드는 지움
    REMOTE_MEETING_CONFERENCE_NOT_SHARE("VCONF_UNSHR","회의 비공유"),
    REMOTE_MEETING_CONFERENCE_SHARE("VCONF_SHR","회의 공유");

    private String code;
    private String codeName;

    RemoteMeetingProgCode(String code,String codeName) {
        this.code = code;
        this.codeName = codeName;
    }

    public String getCode() {
        return code;
    }

    public String findCodeName() {
        return codeName;
    }

    public static Map<String,Object> getFindRemoteMeetProgCodeMap(String progCode) {
        Map<String,Object> returnProgCode = new HashMap<String, Object>();
        for(RemoteMeetingProgCode codeInfo : RemoteMeetingProgCode.values()){
            if(progCode.equals(codeInfo.getCode())){
                returnProgCode.put("code",codeInfo.getCode());
                returnProgCode.put("codeName",codeInfo.findCodeName());
            }
        }
        return returnProgCode;
    }

    public static String getFindRemoteMeetProgCode(String progCode) {
        for(RemoteMeetingProgCode codeInfo : RemoteMeetingProgCode.values()){
            if(progCode.equals(codeInfo.getCode())){
                return codeInfo.getCode();
            }
        }
        return "";
    }

    public static String getFindRemoteMeetProgCodeName(String progCode) {
        for(RemoteMeetingProgCode codeInfo : RemoteMeetingProgCode.values()){
            if(progCode.equals(codeInfo.getCode())){
                return codeInfo.findCodeName();
            }
        }
        return "";
    }


    /**
     * 현재 화상회의 진행 상태가 진행된 상태인지 체크 Validation 용
     * @param progCode
     * @return
     */
    public static boolean procCheckConference(String progCode){
        boolean checkProcConference = false;

        if(!(REMOTE_MEETING_STATUS_NULL.getCode().equals(progCode) || REMOTE_MEETING_CONFERENCE_NOT_START.getCode().equals(progCode))){
            checkProcConference = true;
        }
        return checkProcConference;
    }

    /**
     * 현재 화상회의 진행 상태가 종료된 상태인지 체크 Validation 용
     * @param progCode
     * @return
     */
    public static boolean endCheckConference(String progCode){
        boolean checkEndConference = false;

        if(REMOTE_MEETING_CONFERENCE_END.getCode().equals(progCode)){
            checkEndConference = true;
        }
        return checkEndConference;
    }
}
