package smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model;

import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code.RemoteMeetingProgCode;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReservationRemoteMeetInfo {

    /**
     * 화상회의 예약 API를 호출하기 이전 기본 Call Info 정보
     * @param fiData
     * @return
     */
    public static Map<String,Object> remoteMeetingReservationSetInfo(Map<String,Object> fiData){

        Map<String,Object> remoteMeetInfo = new HashMap<String, Object>();

        String mtg_tit = fiData.get("sitebrfg_tit") == null? "" : fiData.get("sitebrfg_tit").toString();
        String fiId = fiData.get("sitebrfg_uuid") == null? "" : fiData.get("sitebrfg_uuid").toString();
        Date remoteMeetingStartDate = fiData.get("sitebrfg_st_dttm") == null? null : (Date)fiData.get("sitebrfg_st_dttm");
        Date remoteMeetingEndDate = fiData.get("sitebrfg_clsg_dttm") == null? null : (Date)fiData.get("sitebrfg_clsg_dttm");
        remoteMeetInfo.put("debugMode","1");
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd HHmm", Locale.getDefault());

        String startDate = fm.format(remoteMeetingStartDate);
        String endDate = fm.format(remoteMeetingEndDate);

        // 회의 예약 10분전에 회의 생성이 자동으로 등록되도록 스케줄러에 넘겨줄 시간 값 ( 10분에 딱맞춰서 넘겨준다면 제대로 예약이 처리 되지 않을수도 있어 9분전에 실행시킴)
        Date remoteMeetReservationDate = fiData.get("sitebrfg_st_dttm") == null? null : (Date)fiData.get("sitebrfg_st_dttm");
        if(null != remoteMeetReservationDate){
            Calendar cal = Calendar.getInstance();
            cal.setTime(remoteMeetReservationDate);
            cal.add(Calendar.MINUTE,-9);
            Date reservationDate = new Date(cal.getTimeInMillis());
            remoteMeetInfo.put("reservationDate",reservationDate); // 회의 예약 9분전 일시   yyyyMMdd hhmm
        }

        remoteMeetInfo.put("mtg_tit",mtg_tit); //회의 제목
        remoteMeetInfo.put("timezone","Asia/Seoul"); // 타임존 ex) Asia/Seoul
        remoteMeetInfo.put("description",""); // 회의 내용
        remoteMeetInfo.put("startDate",startDate); // 회의 시작 일시  yyyyMMdd hhmm
        remoteMeetInfo.put("endDate",endDate); // 회의 종료 일시   yyyyMMdd hhmm
        remoteMeetInfo.put("sitebrfg_uuid",fiId); // 현장설명회 ID ( Remote Call 호출 request id )
        remoteMeetInfo.put("sitebrfg_vconf_uuid", UUID.randomUUID().toString()); //  Remote Call 호출 request id

        return remoteMeetInfo;
    }


    /**
     * 바로 개설 처리 시도 Start Call back이 오기 전까진 예약 테이블로 처리한다.
     * @param fiData
     * @return
     */
    public static Map<String,Object> remoteMeetingNowStartSetInfo(Map<String,Object> fiData){

        Map<String,Object> remoteMeetInfo = new HashMap<String, Object>();

        String mtg_tit = fiData.get("sitebrfg_tit") == null? "" : fiData.get("sitebrfg_tit").toString();
        String fiId = fiData.get("sitebrfg_uuid") == null? "" : fiData.get("sitebrfg_uuid").toString();

        remoteMeetInfo.put("sitebrfg_uuid",fiId); // 현장설명회 ID ( Remote Call 호출 request id )
        remoteMeetInfo.put("mtg_tit",mtg_tit); // 현장설명회 제목
        remoteMeetInfo.put("sitebrfg_vconf_uuid", UUID.randomUUID().toString()); //  Remote Call 호출 request id
        remoteMeetInfo.put("vconf_rsvn_dttm_uuid","");
        remoteMeetInfo.put("vconf_rsvn_uuid","");
        remoteMeetInfo.put("vconf_rsvn_url","");
        remoteMeetInfo.put("vconf_sts_ccd", RemoteMeetingProgCode.REMOTE_MEETING_CONFERENCE_NOT_START.getCode());

        return remoteMeetInfo;
    }

}
