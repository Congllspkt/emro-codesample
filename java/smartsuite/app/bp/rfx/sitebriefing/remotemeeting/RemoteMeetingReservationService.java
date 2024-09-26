package smartsuite.app.bp.rfx.sitebriefing.remotemeeting;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code.RemoteMeetingErrorCode;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code.RemoteMeetingProgCode;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code.RemoteMeetingReturnCode;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.ReservationRemoteMeetInfo;
import smartsuite.app.common.shared.Const;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.scheduler.core.ScheduleService;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 리모트콜내에서는 예약 프로세스는 사용이 불가하다 ( 레퍼런스 용도로만 확인필요 )
 */
@Transactional
@Service
public class RemoteMeetingReservationService {

    private final static Logger LOGGER = LoggerFactory.getLogger("remoteMeetAopExecution");

    @Inject
    private SqlSession sqlSession;

    @Inject
    RemoteMeetingAPIService remoteMeetingAPIService;

    private static final String NAMESPACE = "remote-meeting.";

    @Inject
    ScheduleService scheduleService;

    /**
     * 리모트 회의 예약 진행
     * 회의예약시간이 지낫을경우, 아예 생성이 되지 않음, 스케줄러를 생성 시켜야함
     *
     * @param fiData
     */
    @Transactional
    public Map<String,Object> reservationRemoteMeetingConference(Map<String,Object> fiData){

        Map<String,Object> reservationInfo = new HashMap<String, Object>();

        //넘어온 현장설명회 map을 remotecall reservation info map으로 변경
        Map<String,Object> remoteMeetInfo = ReservationRemoteMeetInfo.remoteMeetingReservationSetInfo(fiData);
        Map<String,Object> reservationResultMap = null;

        try{
            //화상회의 예약하기 ( 화상회의 예약 일시 ID 가져오기 )
            reservationResultMap = remoteMeetingAPIService.callConferenceReservation(remoteMeetInfo);

            String reservationDateID = reservationResultMap.get("reservationDateID") == null? "" :  reservationResultMap.get("reservationDateID").toString();

            //회상회의 예약 후 정보 조회하기 ( URL / 예약 아이디 )
            if(StringUtils.isNotEmpty(reservationDateID)){
                reservationInfo = remoteMeetingAPIService.callConferenceReservationFind(reservationResultMap);
            }else{
                String message = reservationResultMap.get("message") == null? "" : reservationResultMap.get("message").toString();
                throw new CommonException(message); //화상 회의 예약 ID가 넘어오지 않는다면
            }

            //화상회의 정보가 넘어왔을 경우, 예약 테이블에 insert 한다.
            if(null != reservationInfo){
                sqlSession.insert(NAMESPACE +"insertReservationRemoteMeeting",reservationInfo);
                sqlSession.update(NAMESPACE +"updateFiProgStsReservationMeeting",reservationInfo);

                Date reservationBeforeNineMinDate = remoteMeetInfo.get("reservationDate") == null? null : (Date)remoteMeetInfo.get("reservationDate");
                if(null != reservationBeforeNineMinDate){ // 예약 시간이 없을 경우, 등록안함.
                    String fiId = remoteMeetInfo.get("sitebrfg_uuid") == null? "" : remoteMeetInfo.get("sitebrfg_uuid").toString();
                    Object[] args = new Object[]{remoteMeetInfo};

                    if(!isRegistReservationRemoteCallJobExistCheck()){ // 예약시간에 따른 자동 개설 스케줄러
                        registReservationRemoteMeetingStartJob(fiId,reservationBeforeNineMinDate,args);
                    }
                }
            }
        }catch (RuntimeException r){
            String message = "";
            if(null != reservationResultMap){
                message = reservationResultMap.get("message") == null? "" : reservationResultMap.get("message").toString();
            }
            throw new CommonException(message);
        }

        return reservationInfo;
    }


    /**
     * 화상회의 통보 취소 / 강제 종료 / 삭제 처리 간 프로세스
     * @param fiData
     * @return
     */
    public Map<String,Object> reservationRemoteMeetingDelete(Map<String,Object> fiData){

        Map<String,Object> reservationDeleteResultMap = new HashMap<String, Object>();

        try{
            //화상회의 예약 가져오기
            Map<String,Object> reservationConference = sqlSession.selectOne(NAMESPACE +"selectReservationRemoteMeetingForFiID",fiData);

            if(null != reservationConference) {

                String reservationDateID = reservationConference.get("vconf_rsvn_uuid") == null ? "" : reservationConference.get("vconf_rsvn_uuid").toString();

                if (StringUtils.isNotEmpty(reservationDateID)) {

                    //화상회의 삭제
                    reservationDeleteResultMap = remoteMeetingAPIService.callConferenceReservationDelete(reservationConference);

                    String getReturnCode = reservationDeleteResultMap.get("returnCode") == null ? "" : reservationDeleteResultMap.get("returnCode").toString();
                    Map<String, Object> errorMap = RemoteMeetingReturnCode.getFindErrorMap(getReturnCode);

                    fiData.put("vconf_rsvn_uuid",reservationDateID);
                    sqlSession.update(NAMESPACE + "deleteReservationRemoteMeetingForFiID",fiData);

                    fiData.put("vconf_sts_ccd", RemoteMeetingProgCode.REMOTE_MEETING_STATUS_NULL.getCode());
                    sqlSession.update(NAMESPACE + "deleteFiProgStsReservationMeeting",fiData);

                    if (null != errorMap) {
                        reservationDeleteResultMap.put(Const.RESULT_STATUS, Const.FAIL);
                        reservationDeleteResultMap.put(Const.RESULT_MSG, errorMap.get("errorCode") + ":" + errorMap.get("errorMessage"));
                    }
                }
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }

        return reservationDeleteResultMap;
    }


    /**
     * 예약회의 참석을 눌럿을 경우 사용하는 메소드
     * 해당 회의가 예약되엇을 경우, 예약된 회의에 대한 진행을 하고, 진행되지 않은 회의는 처리하지 않는다.
     * @param fiData
     * @return
     */
    public Map<String,Object> reservationNowStartRemoteMeeting(Map<String,Object> fiData){

        Map<String,Object> reservationStartResultMap = new HashMap<String, Object>();

        try{
            //화상회의 예약 가져오기
            Map<String,Object> reservationConference = sqlSession.selectOne(NAMESPACE +"selectFiAndRemoteMeetInfoForFiId",fiData);

            if(null != reservationConference) {
                String rmProgSts = reservationConference.get("vconf_sts_ccd") == null? "" : reservationConference.get("vconf_sts_ccd").toString();

                // 회의 미진행일 경우만 바로 시작 가능
                if(RemoteMeetingProgCode.REMOTE_MEETING_CONFERENCE_NOT_START.getCode().equals(RemoteMeetingProgCode.getFindRemoteMeetProgCode(rmProgSts))){
                    Date fiStartDt = reservationConference.get("sitebrfg_st_dttm") == null? null : (Date) reservationConference.get("sitebrfg_st_dttm");
                    Date fiEndDt = reservationConference.get("sitebrfg_clsg_dttm") == null? null : (Date) reservationConference.get("sitebrfg_clsg_dttm");
                    Date now = new Date();
                    if(null != fiStartDt && now.before(fiStartDt) && now.before(fiEndDt) ){
                        String confrenceJoinName = Auth.getCurrentUserName();
                        String reservationDateID = reservationConference.get("vconf_rsvn_uuid") == null ? "" : reservationConference.get("vconf_rsvn_uuid").toString();
                        String firmId = reservationConference.get("sitebrfg_vconf_uuid") == null ? "" : reservationConference.get("sitebrfg_vconf_uuid").toString();
                        if (StringUtils.isNotEmpty(reservationDateID)) {
                            //화상회의 예약 건 바로 시작
                            reservationStartResultMap =  remoteMeetingAPIService.callConferenceReservationStart(reservationDateID,confrenceJoinName,firmId);
                        }
                    }else{
                        reservationStartResultMap.put(Const.RESULT_STATUS,Const.FAIL);
                        reservationStartResultMap.put(Const.RESULT_MSG,"현재는 참석가능한 회의 시간이 아닙니다. 다시 한 번 확인해주세요.");
                    }
                }else if(RemoteMeetingProgCode.REMOTE_MEETING_RESERVATION_CONFERENCE_END.getCode().equals(rmProgSts)){
                    // 회의를 예약 하였으나, Callback URL로 예약 종료가 들어왔을 경우 ( VCONF_STS_CCD 를 "T" 로 변경 처리 함)
                    reservationConference.put("vconf_sts_ccd","VCONF_RSVN_ED");
                    sqlSession.update(NAMESPACE +"updateFiProgStsReservationMeeting",reservationConference);
                }else if(RemoteMeetingProgCode.REMOTE_MEETING_CONFERENCE_START.getCode().equals(RemoteMeetingProgCode.getFindRemoteMeetProgCode(rmProgSts))){
                    // 회의 생성 CALLBACK URL 수신 후 처리 ( 회의 예약 관련 URL이 아닌, 회의 참석 URL로 변경 )
                    String joinUrl= reservationConference.get("mtg_url") == null ? "" : reservationConference.get("mtg_url").toString();
                    reservationStartResultMap.put("url",joinUrl);
                }else{
                    reservationStartResultMap.put(Const.RESULT_STATUS,Const.FAIL);
                    reservationStartResultMap.put(Const.RESULT_MSG,"현재 해당 현장설명회는 예약된 시간에 도래하지 않았거나, 진행 중인 상태가 아닙니다. 다시 한 번 확인해주세요.");
                }
            }else{
                reservationStartResultMap.put(Const.RESULT_STATUS,Const.FAIL);
                reservationStartResultMap.put(Const.RESULT_MSG,"예약된 회의가 없습니다.");
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            throw new CommonException(RemoteMeetingErrorCode.ERROR_CODE.getDefaultMessage());
        }

        return reservationStartResultMap;
    }


    /**
     * 회의 예약을 진행 할 경우, 9분전 자동으로 job이 등록되도록 처리
     * ( 사용안함 , 리모트콜의 경우 회의예약 및 회의 바로개설 시 리모트콜의 화면을 모두 랜더링 하지않는이상 callback url을 던져주지 않아 스케줄러로 사용이 불가함)
     * @param fiId
     * @param reservationDate
     * @param args
     */
    private void registReservationRemoteMeetingStartJob(String fiId, Date reservationDate, Object[] args) {
        try {
            scheduleService.registSchedule(Class.forName("smartsuite.app.bp.rfx.sitebriefing.remotemeeting.RemoteMeetingReservationService"),
                    "reservationRemoteMeetingStringJob",
                    args,
                    reservationDate,
                    "RM_RESERVATION",
                    fiId,
                    "RM_RESERVATION_START",

                    null);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new CommonException(ErrorCode.FAIL, e);
        }
    }


    public boolean isRegistReservationRemoteCallJobExistCheck(){
        try {
            return scheduleService.isJobExist("RM_RESERVATION","RM_RESERVATION_START");
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage());
            throw new CommonException(ErrorCode.FAIL, e);
        }
    }


    /*private void registReservationRemoteMeetingEndJob(String fiId, Date reservationDate, Object[] args) {
        try {
            scheduleService.removeScheduleTrigger(Class.forName("smartsuite.app.bp.rfx.fi.remotemeeting.RemoteMeetingReservationService"),
                    "endFieldIntro",
                    "RM_RESERVATION",
                    fiId);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new CommonException(ErrorCode.FAIL, e);
        }
    }*/


    public void reservationRemoteMeetingStringJob(HashMap<String,Object> fiData){

        Map<String,Object> reservationStartResultMap = new HashMap<String, Object>();

        try{
            //화상회의 예약 가져오기
            Map<String,Object> reservationConference = sqlSession.selectOne(NAMESPACE +"selectFiAndRemoteMeetInfoForFiId",fiData);

            if(null != reservationConference) {
                String rmProgSts = reservationConference.get("vconf_sts_ccd") == null? "" : reservationConference.get("vconf_sts_ccd").toString();

                // 회의 미진행일 경우만 바로 시작 가능
                if(RemoteMeetingProgCode.REMOTE_MEETING_CONFERENCE_NOT_START.getCode().equals(RemoteMeetingProgCode.getFindRemoteMeetProgCode(rmProgSts))){
                    Date fiStartDt = reservationConference.get("sitebrfg_st_dttm") == null? null : (Date) reservationConference.get("sitebrfg_st_dttm");
                    Date fiEndDt = reservationConference.get("sitebrfg_clsg_dttm") == null? null : (Date) reservationConference.get("sitebrfg_clsg_dttm");
                    Date now = new Date();
                    if(null != fiStartDt && now.before(fiStartDt) && now.before(fiEndDt) ){
                        // 현장설명회 담당자를 회의 생성자 형태로 넘김.
                        String confrenceJoinName = reservationConference.get("sitebrfg_pic_id") == null ? "" : reservationConference.get("sitebrfg_pic_id").toString();
                        String reservationDateID = reservationConference.get("vconf_rsvn_uuid") == null ? "" : reservationConference.get("vconf_rsvn_uuid").toString();
                        String firmId = reservationConference.get("sitebrfg_vconf_uuid") == null ? "" : reservationConference.get("sitebrfg_vconf_uuid").toString();
                        if (StringUtils.isNotEmpty(reservationDateID)) {
                            //화상회의 예약 건 바로 시작
                            reservationStartResultMap =  remoteMeetingAPIService.callConferenceReservationStart(reservationDateID,confrenceJoinName,firmId);
                        }
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            throw new CommonException(RemoteMeetingErrorCode.ERROR_CODE.getDefaultMessage());
        }
    }

}
