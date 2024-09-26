package smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.sitebriefing.repository.RemoteMeetingRepository;
import smartsuite.exception.CommonException;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class RemoteMeetCallbackEndInfo {

    public static final Logger LOGGER = LoggerFactory.getLogger(RemoteMeetCallbackEndInfo.class);

    // 회의 요청 정보
    private final Map<String,Object> requestInfoMap;

    // 회의 결과 데이터
    private final Map<String,Object> conferenceInfoMap;

    // 화면 캡쳐 링크
    private final List<String> captureUrlList;

    //참석자 정보
    private final List<Map<String,Object>> conferenceAttendUserList;

	@Inject
	private RemoteMeetingRepository remoteMeetingRepository;

    @Inject
    private SqlSession sqlSession;

    public RemoteMeetCallbackEndInfo(){
        this.requestInfoMap = new HashMap<String, Object>();
        this.conferenceInfoMap = new HashMap<String, Object>();
        this.captureUrlList = new ArrayList<String>();
        this.conferenceAttendUserList = new ArrayList<Map<String, Object>>();
    }

    public RemoteMeetCallbackEndInfo(Map<String, Object> requestInfoMap, Map<String, Object> conferenceInfoMap, List<String> captureUrlList, List<Map<String, Object>> conferenceAttendUserList) {
        this.requestInfoMap = requestInfoMap;
        this.conferenceInfoMap = conferenceInfoMap;
        this.captureUrlList = captureUrlList;
        this.conferenceAttendUserList = conferenceAttendUserList;
    }

    public Map<String, Object> getRequestInfoMap() {
        return requestInfoMap;
    }

    public Map<String, Object> getConferenceInfoMap() {
        return conferenceInfoMap;
    }

    public List<String> getCaptureUrlList() {
        return captureUrlList;
    }

    public List<Map<String, Object>> getConferenceAttendUserList() {
        return conferenceAttendUserList;
    }

    /**
     *
     *	 1 callback 요청 ID requestId String
     *	 2 제휴사 사용자 ID clientUserId String
     *	 3 제휴사 고객사 ID clientCompanyId String
     *	 4 회의결과 conference Object 회의 결과 데이터
     *	 4-1 CID conferenceId String 회의 ID (Unique값)
     *	 4-2 회의 이름 title String
     *	 4-3 회의 개설 시간 createdTime Long
     *	 4-4 회의 시작 시간 startTime Long
     *	 4-5 회의 종료 시간 endTime Long
     *	 4-6 회의 최대 참여자 수 maxJoinedEndpointCount Integer
     *	 4-7 회의록 유무 meetingNotesExists boolean true : 있음
     *	 4-8 AI회의록 유무 usedStt boolean true : 있음
     *	 4-9 녹화영상 유무 recorded boolean true : 있음
     *	 4-10 캡처파일 (링크) captureURLs String[ ]
     *	 4-11 참여자 정보 conferenceEndpoints Object[ ]
     *	 4-11-1 이름 name String
     *	 4-11-2 참여 시간 joinedAt Long
     *	 4-11-3 퇴장 시간 leavedAt Long
     *	 4-11-4 OS 이름 osName String
     *	 4-11-5 제휴사 사용자 ID clientUserId String API 개설/참여자
     *	 4-11-6 제휴사 고객사 ID clientCompanyId String API 개설/참여자
     *	 4-11-7 callback 요청 ID requestId String API 개설자
     */
    public  RemoteMeetCallbackEndInfo endCallbackResultInfo(JsonObject jsonObject){

        Map<String,Object> requestInfoMap = new HashMap<String, Object>();
        Map<String,Object> conferenceInfoMap = new HashMap<String, Object>();
        List<String> captureUrlList = new ArrayList<String>();
        List<Map<String,Object>> conferenceAttendUserList = new ArrayList<Map<String, Object>>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm" , Locale.getDefault());


        // 예약 회의 ID
        String requestId = jsonObject.get("requestId") == null?  "" : jsonObject.get("requestId").getAsString();
        requestInfoMap.put("sitebrfg_vconf_uuid",requestId);
        conferenceInfoMap.put("sitebrfg_vconf_uuid",requestId);

        if(StringUtils.isEmpty(requestId)){
            throw new CommonException("Not request Id");
        }

        //생성자 아이디
        String clientUserId = jsonObject.get("clientUserId") == null?  "" : jsonObject.get("clientUserId").getAsString();
        requestInfoMap.put("conf_usr_id",clientUserId);
        conferenceInfoMap.put("conf_usr_id",clientUserId);

        //생성자 부서
        String clientCompanyId = jsonObject.get("clientCompanyId") == null?  "" : jsonObject.get("clientCompanyId").getAsString();
        requestInfoMap.put("conf_dept_cd",clientCompanyId);
        conferenceInfoMap.put("conf_dept_cd",clientCompanyId);


        // 회의 ID
        String conferenceId = jsonObject.get("conferenceId") == null?  "" : jsonObject.get("conferenceId").getAsString();
        requestInfoMap.put("mtg_uuid",conferenceId);
        conferenceInfoMap.put("mtg_uuid",conferenceId);


        //예약회의 정보 가져오기, fi_rmid를 기준으로 가져온다.
        Map<String,Object> reservationConference = remoteMeetingRepository.selectReservationAndRemoteMeetInfoForFirmId(requestInfoMap);
        String rmId = "";
        if(null != reservationConference) {
            // 현장설명회 ID ( 필요에 의해서, app_id로 변경해서 처리 필요 )
            String fiId = reservationConference.get("sitebrfg_uuid") == null ? "" : reservationConference.get("sitebrfg_uuid").toString();
            requestInfoMap.put("task_uuid", fiId);
            requestInfoMap.put("sitebrfg_uuid", fiId);
            conferenceInfoMap.put("task_uuid", fiId);
            conferenceInfoMap.put("sitebrfg_uuid", fiId);

            String sysId = reservationConference.get("sys_id") == null? "" : reservationConference.get("sys_id").toString();
            conferenceInfoMap.put("sys_id",sysId);

            // 현재 화상회의 진행 상태
            String rmProgSts = reservationConference.get("vconf_sts_ccd") == null ? "" : reservationConference.get("vconf_sts_ccd").toString();
            requestInfoMap.put("vconf_sts_ccd", rmProgSts);

            rmId = reservationConference.get("vconf_uuid") == null? "" : reservationConference.get("vconf_uuid").toString();
            requestInfoMap.put("vconf_uuid",rmId);
            conferenceInfoMap.put("vconf_uuid",rmId);
        }else{
            throw new CommonException("Not reservation remote meeting ERROR");
        }



        //현장설명회 제목
        String mtg_tit = jsonObject.get("mtg_tit") == null?  "" : jsonObject.get("mtg_tit").getAsString();
        conferenceInfoMap.put("mtg_tit",mtg_tit);

        //회의 개설 시간
        long createdTime = jsonObject.get("createdTime") == null?  0 : jsonObject.get("createdTime").getAsLong();

        Date createDate=new Date(createdTime);
        String createDateFormat = sdf.format(createDate);

        conferenceInfoMap.put("crn_tm",createDateFormat);

        //회의 시작 시간
        long startTime = jsonObject.get("startTime") == null?  0 : jsonObject.get("startTime").getAsLong();
        Date startDate=new Date(startTime);
        String startDateFormat = sdf.format(startDate);

        conferenceInfoMap.put("st_tm",startDateFormat);

        //회의 종료 시간
        long endTime = jsonObject.get("endTime") == null?  0 : jsonObject.get("endTime").getAsLong();
        Date endDate=new Date(endTime);
        String endDateFormat = sdf.format(endDate);

        conferenceInfoMap.put("ed_tm",endDateFormat);

        //회의 최대 참여자 수
        Integer maxJoinedEndpointCount = jsonObject.get("maxJoinedEndpointCount") == null? 0 : jsonObject.get("maxJoinedEndpointCount").getAsInt();
        conferenceInfoMap.put("attend_usr_cnt",maxJoinedEndpointCount);

        //회의록 유무 ( true : 있음 , false : 없음 )
        boolean meetingNotesExists =  jsonObject.get("meetingNotesExists") == null? false : jsonObject.get("meetingNotesExists").getAsBoolean();
        String meetingNotesExistsYn = "N";
        if(meetingNotesExists){
            meetingNotesExistsYn = "Y";
        }
        conferenceInfoMap.put("mtgmins_yn",meetingNotesExistsYn);

        //AI회의록 유무 ( true : 있음 , false : 없음 )
        boolean usedStt =  jsonObject.get("usedStt") == null? false : jsonObject.get("usedStt").getAsBoolean();
        String usedSttYn = "N";
        if(usedStt){
            usedSttYn = "Y";
        }
        conferenceInfoMap.put("ai_note_yn",usedSttYn);

        //녹화 유무
        boolean recorded =  jsonObject.get("recorded") == null? false : jsonObject.get("recorded").getAsBoolean();
        String recordedYn = "N";
        if(recorded){
            recordedYn = "Y";
        }
        conferenceInfoMap.put("rcrd_yn",recordedYn);

        // 캡처파일 링크
        JsonArray captureURLs = jsonObject.get("captureURLs") == null? new JsonArray() : jsonObject.get("captureURLs").getAsJsonArray();
        if(null != captureURLs && captureURLs.size() > 0){
            for(JsonElement capture : captureURLs){
                String url = capture == null? "" : capture.getAsString();
                if(StringUtils.isNotEmpty(url)) captureUrlList.add(url);
            }
        }


        //참여자 정보
        JsonArray conferenceEndpoints = jsonObject.get("conferenceEndpoints") == null? new JsonArray() : jsonObject.get("conferenceEndpoints").getAsJsonArray();
        if(null != conferenceEndpoints && conferenceEndpoints.size() > 0){
            for(JsonElement attendUserInfo : conferenceEndpoints){
                if(null != attendUserInfo){
                    JsonObject attendUserInfoObject = attendUserInfo.getAsJsonObject();
                    Map<String,Object> attendUserInfoMap = new HashMap<String, Object>();

                    //이름
                    String name = attendUserInfoObject.get("name") == null? "" : attendUserInfoObject.get("name").getAsString();
                    attendUserInfoMap.put("conf_nm",name);

                    //참여 시간
                    long joinedAt = attendUserInfoObject.get("joinedAt") == null? 0 : attendUserInfoObject.get("joinedAt").getAsLong();
                    Date joinDate =new Date(joinedAt);
                    String joinDateFormat = sdf.format(joinDate);
                    attendUserInfoMap.put("join_dt",joinDateFormat);

                    //퇴장 시간
                    long leavedAt = attendUserInfoObject.get("leavedAt") == null? 0 : attendUserInfoObject.get("leavedAt").getAsLong();
                    Date leavedDate =new Date(leavedAt);
                    String leaveDateFormat = sdf.format(leavedDate);
                    attendUserInfoMap.put("leave_dt",leaveDateFormat);


                    //참여자 아이디
                    String joinUserId = attendUserInfoObject.get("clientUserId") == null? "" : attendUserInfoObject.get("clientUserId").getAsString();
                    attendUserInfoMap.put("conf_usr_id",joinUserId);

                    //참여자 부서
                    String joinCompanyId = attendUserInfoObject.get("clientCompanyId") == null? "" : attendUserInfoObject.get("clientCompanyId").getAsString();
                    attendUserInfoMap.put("conf_dept_cd",joinCompanyId);

                    if(StringUtils.isNotEmpty(clientUserId) && StringUtils.isNotEmpty(joinUserId) && clientUserId.equals(joinUserId)){
                        attendUserInfoMap.put("creator_yn","Y");
                    }

                    // remote meeting info key
                    attendUserInfoMap.put("vconf_uuid",rmId);

                    conferenceAttendUserList.add(attendUserInfoMap);
                }


            }
        }

        RemoteMeetCallbackEndInfo remoteMeetCallbackEndInfo = new RemoteMeetCallbackEndInfo(requestInfoMap,conferenceInfoMap,captureUrlList,conferenceAttendUserList);
        return remoteMeetCallbackEndInfo;
    }


    public void remoteMeetCallbackEndInfoLogger () {
        Iterator<String> requestInfo = requestInfoMap.keySet().iterator();
        LOGGER.info("===================== requestInfoMap START =============================");
        while(requestInfo.hasNext()) {
            String id = requestInfo.next();
            String value = requestInfoMap.get(id) == null? "" : requestInfoMap.get(id).toString();
            LOGGER.info("ID :"+id+" || Value :"+value);
        }
        LOGGER.info("===================== requestInfoMap END   =============================");

        Iterator<String> conferenceInfo = conferenceInfoMap.keySet().iterator();
        LOGGER.info("===================== conferenceInfoMap START =============================");
        while(conferenceInfo.hasNext()) {
            String id = conferenceInfo.next();
            String value = conferenceInfoMap.get(id) == null? "" : conferenceInfoMap.get(id).toString();
            LOGGER.info("ID :"+id+" || Value :"+value);
        }
        LOGGER.info("===================== conferenceInfoMap END   =============================");

        LOGGER.info("===================== captureUrlList START =============================");
        for(String url : captureUrlList){
            LOGGER.info("Captrue URL :"+url);
        }
        LOGGER.info("===================== captureUrlList END =============================");


        LOGGER.info("===================== conferenceAttendUserList START =============================");
        for(Map<String,Object> attendUserInfo : conferenceAttendUserList){
            Iterator<String> attendUserKeys = attendUserInfo.keySet().iterator();
            while(attendUserKeys.hasNext()) {
                String id = attendUserKeys.next();
                String value = attendUserInfo.get(id) == null? "" : attendUserInfo.get(id).toString();
                LOGGER.info("ID :"+id+" || Value :"+value);
            }
        }
        LOGGER.info("===================== conferenceAttendUserList END   =============================");
    }
}
