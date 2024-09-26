package smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model;

import com.google.gson.JsonObject;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.rfx.sitebriefing.repository.RemoteMeetingRepository;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RemoteMeetCallbackStartInfo {

    public static final Logger LOGGER = LoggerFactory.getLogger("remoteMeetAopExecution");

    private static final String NAMESPACE = "remote-meeting.";

	@Inject
	private RemoteMeetingRepository remoteMeetingRepository;


    /**
     * remote call 화상회의 생성 callback url 수신 시 필요 정보 셋업
     * TABLE : VCONF , VCONF_ATNE
     *  [parameter]
     *  1 callback 요청 ID requestId String
     *  2 제휴사 사용자 ID clientUserId String
     *  3 제휴사 고객사 ID clientCompanyId String
     *  4 CID conferenceId String
     *  5 회의 참여 URL joinUrl String 회의 참여 링크
     * @param param
     * @return
     */
    public Map<String,Object> startCallbackResultInfo(JsonObject jsonObject){

        Map<String,Object> resultMap = new HashMap<String, Object>();
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd HHmm", Locale.getDefault());

        // 예약 회의 ID
        String requestId = jsonObject.get("requestId") == null? "" : jsonObject.get("requestId").getAsString();
        resultMap.put("sitebrfg_vconf_uuid",requestId);

        // 회의 ID
        String conferenceId = jsonObject.get("conferenceId") == null? "" : jsonObject.get("conferenceId").getAsString();
        resultMap.put("mtg_uuid",conferenceId);

        //회의 참석 URL
        String joinUrl = jsonObject.get("joinUrl") == null? "" : jsonObject.get("joinUrl").getAsString();
        resultMap.put("mtg_url",joinUrl);

        //화상회의 아이디 ( 개설 시 등재 )
        String rmId = UUID.randomUUID().toString();
        resultMap.put("vconf_uuid",rmId);

        //예약회의 정보 가져오기, fi_rmid를 기준으로 가져온다.
        Map<String,Object> reservationConference = remoteMeetingRepository.selectReservationRemoteMeetingForFirmId(resultMap);

        if(null != reservationConference){
            // 현장설명회 ID ( 필요에 의해서, app_id로 변경해서 처리 필요 )
            String fiId = reservationConference.get("sitebrfg_uuid") == null? "" : reservationConference.get("sitebrfg_uuid").toString();
            resultMap.put("task_uuid",fiId);
            resultMap.put("sitebrfg_uuid",fiId);

            // 현재 화상회의 진행 상태
            String rmProgSts = reservationConference.get("vconf_sts_ccd") == null ? "" : reservationConference.get("vconf_sts_ccd").toString();
            resultMap.put("vconf_sts_ccd", rmProgSts);

            String sysId = reservationConference.get("sys_id") == null? "" : reservationConference.get("sys_id").toString();
            resultMap.put("sys_id",sysId);


            // 현장설명회 제목
            String mtg_tit = reservationConference.get("sitebrfg_tit") == null? "" : reservationConference.get("sitebrfg_tit").toString();
            resultMap.put("mtg_tit",mtg_tit);


            Date remoteMeetingStartDate = reservationConference.get("sitebrfg_st_dttm") == null? null : (Date)reservationConference.get("sitebrfg_st_dttm");
            Date remoteMeetingEndDate = reservationConference.get("sitebrfg_clsg_dttm") == null? null : (Date)reservationConference.get("sitebrfg_clsg_dttm");

            String startDate = fm.format(remoteMeetingStartDate);
            String endDate = fm.format(remoteMeetingEndDate);

            resultMap.put("crn_tm",fm.format(new Date())); // 회의 생성 일시 yyyyMMdd hhmm

            // 현장설명회 시작일시
            resultMap.put("st_tm",startDate); // 회의 시작 일시  yyyyMMdd hhmm

            // 현장설명회 종료일시
            resultMap.put("ed_tm",endDate); // 회의 종료 일시   yyyyMMdd hhmm
        }



        // 생성자 여부 VCONF_ATNE
        resultMap.put("creator_yn","Y");

        //생성자 아이디
        String clientUserId = jsonObject.get("clientUserId") == null? "" : jsonObject.get("clientUserId").getAsString();

        //생성자 부서
        String clientCompanyId = jsonObject.get("clientCompanyId") == null? "" : jsonObject.get("clientCompanyId").getAsString();

        resultMap.put("conf_usr_id",clientUserId);
        resultMap.put("conf_dept_cd",clientCompanyId);

        /*if(StringUtils.isNotEmpty(clientUserId)){
            Map<String,Object> userInfo = sqlSession.selectOne("infra-UserDetails.getSessionUserInfo", clientUserId);
            String userName = userInfo.get("usr_nm") == null? "" : userInfo.get("usr_nm").getAsString();
            resultMap.put("conf_nm",userName);
        }*/

        remoteMeetCallbackStartInfoLogger(resultMap);

        return resultMap;
    }

    public void remoteMeetCallbackStartInfoLogger(Map<String,Object> param){
        Iterator<String> requestInfo = param.keySet().iterator();
        LOGGER.info("===================== start callback all START =============================");
        while(requestInfo.hasNext()) {
            String id = requestInfo.next();
            String value = param.get(id).toString();
            LOGGER.info("ID :"+id+" || Value :"+value);
        }
        LOGGER.info("===================== start callback all END   =============================");
    }
}
