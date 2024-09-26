package smartsuite.app.bp.rfx.sitebriefing.remotemeeting;

import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code.RemoteMeetingProgCode;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.RemoteMeetCallbackEndInfo;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.RemoteMeetCallbackStartInfo;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.scheduler.core.ScheduleService;
import smartsuite.spring.tenancy.context.TenancyContext;
import smartsuite.spring.tenancy.context.TenancyContextHolder;
import smartsuite.spring.tenancy.core.DefaultTenant;
import smartsuite.spring.tenancy.core.Tenant;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * REMOTE MEETING 시 CALLBACK으로 받아온 내역에 대해서 담당하는 CLASS
 */

@Service
@Transactional
public class RemoteMeetingResultService {


    private static final String NAMESPACE = "remote-meeting.";

    @Inject
    private SqlSession sqlSession;

    @Inject
    RemoteMeetCallbackStartInfo remoteMeetCallbackStartInfo;

    @Inject
    RemoteMeetCallbackEndInfo remoteMeetCallbackEndInfo;

    @Inject
    ScheduleService scheduleService;

    public void startCallbackResultProcess(JsonObject jsonObject){


        Map<String,Object> startResultMap = remoteMeetCallbackStartInfo.startCallbackResultInfo(jsonObject);

        String sysId = startResultMap.get("sys_id") == null? "" : startResultMap.get("sys_id").toString();
        Tenant tenant = new DefaultTenant().createInstance(sysId);
        TenancyContext tenancyContext = TenancyContextHolder.createEmptyContext();
        tenancyContext.setTenant(tenant);
        TenancyContextHolder.setContext(tenancyContext);


        if(null != startResultMap){
            // 1. 회의 참여 링크를 예약 테이블에 넣어준다. ( SITEBRFG_VCONF )
            sqlSession.update(NAMESPACE +"updateJoinURLReservationMeetingForFirmId",startResultMap);

            //REMOTE_MEETING_CONFERENCE_NOT_START 일 경우만, 회의 생성 및 참석자 등재
            String rmProgSts = startResultMap.get("vconf_sts_ccd") == null ? "" : startResultMap.get("vconf_sts_ccd").toString();
            if(RemoteMeetingProgCode.REMOTE_MEETING_CONFERENCE_NOT_START.getCode().equals(rmProgSts)){
                // 2. 진행 상태를 회의 개설 REMOTE_MEETING_CONFERENCE_START 로 변경한다 ( SITEBRFG )
                startResultMap.put("vconf_sts_ccd", RemoteMeetingProgCode.REMOTE_MEETING_CONFERENCE_START.getCode());
                sqlSession.update(NAMESPACE +"updateFiProgStsReservationMeeting",startResultMap);


                // 3. 화상회의 개설 정보를 insert 한다
                sqlSession.insert(NAMESPACE +"insertRemoteMeetingInfo",startResultMap);
            }
        }
    }

    public void endCallbackResultProcess(JsonObject jsonObject){
        RemoteMeetCallbackEndInfo startResultMap = remoteMeetCallbackEndInfo.endCallbackResultInfo(jsonObject);

        //log
        startResultMap.toString();

        String conferenceId = "";
        String noteYn = "N";
        String recYn = "N";
        Object[] args = null;


        Map<String,Object> conferenceInfoMap = startResultMap.getConferenceInfoMap();

        String sysId = conferenceInfoMap.get("sys_id") == null? "" : conferenceInfoMap.get("sys_id").toString();

        Tenant tenant = new DefaultTenant().createInstance(sysId);
        TenancyContext tenancyContext = TenancyContextHolder.createEmptyContext();
        tenancyContext.setTenant(tenant);
        TenancyContextHolder.setContext(tenancyContext);

        // 1. 회의의 상태를 Close로 변경한다. 진행 상태를 회의 개설 REMOTE_MEETING_CONFERENCE_END 로 변경 및 정보 값들을 update 한다 ( SITEBRFG )
        if(null != startResultMap.getConferenceInfoMap()){
            startResultMap.getConferenceInfoMap().put("vconf_sts_ccd", RemoteMeetingProgCode.REMOTE_MEETING_CONFERENCE_END.getCode());
            sqlSession.update(NAMESPACE +"deleteRemoteMeetingForFiIDAndNotReservation", startResultMap.getConferenceInfoMap());
            sqlSession.update(NAMESPACE +"deleteRemoteMeetingInfo", startResultMap.getConferenceInfoMap());
            // 넘어온 회의정보 update
            sqlSession.update(NAMESPACE +"updateFiRemoteMeetingInfo", startResultMap.getConferenceInfoMap());
            conferenceId = startResultMap.getConferenceInfoMap().get("mtg_uuid") == null? "" : startResultMap.getConferenceInfoMap().get("mtg_uuid").toString();
            noteYn = startResultMap.getConferenceInfoMap().get("mtgmins_yn") == null? "N" : startResultMap.getConferenceInfoMap().get("mtgmins_yn").toString();
            recYn = startResultMap.getConferenceInfoMap().get("rcrd_yn") == null? "N" : startResultMap.getConferenceInfoMap().get("rcrd_yn").toString();
            args = new Object[]{startResultMap.getConferenceInfoMap()};
        }

        // 2. 개설자를 참석자로 등록한다.
        if(null!= startResultMap.getConferenceAttendUserList() && startResultMap.getConferenceAttendUserList().size() > 0){
            List<Map<String,Object>> conferenceAttendUserList = startResultMap.getConferenceAttendUserList();
            for(Map<String,Object> attendUser : conferenceAttendUserList){
                sqlSession.insert(NAMESPACE +"insertRemoteMeetingAttendUser",attendUser);
            }
        }

        // 3. 회의록은 같은 트렉젝션에서 처리하지 않고 스케줄러를 등록시켜 3분 뒤 동작하도록 진행한다 ( 회의록을 가져오다가 롤백될수있으니. )
        // 회의 ID가 존재하고 , 회의록이 존재할 경우 스케줄러 등록
        if(StringUtils.isNotEmpty(conferenceId) && ("Y").equals(noteYn)){
            registNoteResponseJob(conferenceId,args);
        }

        // 4. 녹화파일이 있을 경우, 녹화파일을 다운받기 위한 스케줄러 등록
        if(("Y").equals(recYn)){
            registRecVideoDownLoadJob(conferenceId,args);
        }
    }


    public void registNoteResponseJob(String conferenceId, Object[] args) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.MINUTE, 3);
            scheduleService.registSchedule(Class.forName("smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.RemoteMeetScheduler"),
                    "noteResponseApiCall",
                    args,
                    cal.getTime(),
                    "RM_NOTE_RESPONSE",
                    conferenceId,
                    "RM_END_PROC_NOTE_RESPONSE",
                    null);
        } catch (ClassNotFoundException e) {
            throw new CommonException(ErrorCode.FAIL, e);
        }
    }



    public void registRecVideoDownLoadJob(String conferenceId, Object[] args) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.MINUTE, 3);
            scheduleService.registSchedule(Class.forName("smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.RemoteMeetScheduler"),
                    "recVideoDown",
                    args,
                    cal.getTime(),
                    "RM_REC_VIDEO_DOWNLOAD",
                    conferenceId,
                    "RM_END_PROC_REC_VIDEO_DOWNLOAD",
                    null);
        } catch (ClassNotFoundException e) {
            throw new CommonException(ErrorCode.FAIL, e);
        }
    }




    public boolean isEndRemoteCallFieldJobExistCheck(){
        try {
            return scheduleService.isJobExist("FI_REMOTEMEETING","FI_END_REMOTEMEETING");
        } catch (SchedulerException e) {
            throw new CommonException(ErrorCode.FAIL, e);
        }
    }


    public void registEndRemoteCallFieldJob(String fiId, Date fiEndDt, Object[] args) {
        try {
            scheduleService.registSchedule(Class.forName("smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.RemoteMeetScheduler"),
                    "deleteRemoteMeetingInfo",
                    args,
                    fiEndDt,
                    "FI_REMOTEMEETING",
                    fiId,
                    "FI_END_REMOTEMEETING",
                    null);
        } catch (ClassNotFoundException e) {
            throw new CommonException(ErrorCode.FAIL, e);
        }
    }


    /**
     * 현장 설명회 종료 처리 스케줄러 삭제
     *
     * @param fiId 현장 설명회 아이디
     */
    public void deleteEndRemoteCallFieldJobJob(String fiId) {
        if(fiId != null) {
            try {
                scheduleService.removeScheduleTrigger(Class.forName("smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.RemoteMeetScheduler"),
                        "deleteRemoteMeetingInfo",
                        "FI_REMOTEMEETING",
                        fiId);
            } catch (ClassNotFoundException e) {
                throw new CommonException(ErrorCode.FAIL, e);
            }
        }

    }
}
