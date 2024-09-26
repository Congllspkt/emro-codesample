package smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.RemoteMeetingAPIService;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.RemoteMeetingResultService;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code.RemoteMeetingProgCode;
import smartsuite.app.bp.rfx.sitebriefing.service.SiteBriefingService;
import smartsuite.spring.tenancy.context.TenancyContext;
import smartsuite.spring.tenancy.context.TenancyContextHolder;
import smartsuite.spring.tenancy.core.DefaultTenant;
import smartsuite.spring.tenancy.core.Tenant;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.SimpleMultipartFileItem;
import smartsuite.upload.util.AthfServiceUtil;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service
public class RemoteMeetScheduler {

    private final static Logger LOGGER = LoggerFactory.getLogger("remoteMeetAopExecution");

    @Inject
    private SqlSession sqlSession;

    @Inject
    RemoteMeetingAPIService remoteMeetingAPIService;

    @Inject
    RemoteMeetingResultService remoteMeetingResultService;

    @Inject
    SiteBriefingService fiService;

	@Inject
	StdFileService stdFileService;


    @Value("#{smartsuiteProperties['smartsuite.upload.path']}")
    private String fileUploadPath;

    private static final String NAMESPACE = "remote-meeting.";


    // 회의록 수신 API 스케줄러
    public void noteResponseApiCall(HashMap<String,Object> map){
        String conferenceId = map.get("mtg_uuid") == null? "" : map.get("mtg_uuid").toString();
        String rmId = map.get("vconf_uuid") == null? "" : map.get("vconf_uuid").toString();

        Map<String,Object> notesMap = remoteMeetingAPIService.callConferenceNotesFind(conferenceId);

        if( null != notesMap && notesMap.keySet().size() > 0){

            String sysId = map.get("sys_id") == null? "" : map.get("sys_id").toString();
            Tenant tenant = new DefaultTenant().createInstance(sysId);
            TenancyContext tenancyContext = TenancyContextHolder.createEmptyContext();
            tenancyContext.setTenant(tenant);
            TenancyContextHolder.setContext(tenancyContext);

            String noteId = UUID.randomUUID().toString();
            notesMap.put("mtgmins_uuid",noteId);
            notesMap.put("vconf_uuid",rmId);
            // 회의록 insert
            sqlSession.insert(NAMESPACE + "insertConferenceNotes",notesMap);

            List<Map<String,Object>> taskMapList = notesMap.get("taskMapList") == null? new ArrayList<Map<String, Object>>() : (List<Map<String,Object>>) notesMap.get("taskMapList");
            if(null != taskMapList && taskMapList.size() > 0){
                for(Map<String,Object> taskInfo : taskMapList){  // 회의록 별 Task insert
                    taskInfo.put("mtgmins_uuid",noteId);
                    taskInfo.put("vconf_uuid",rmId);
                    sqlSession.insert(NAMESPACE + "insertConferenceNoteInfoTask",taskInfo);
                }
            }
        }
    }


    // 녹화 저장 스케줄러
    public void recVideoDown(HashMap<String,Object> param){
        List<String> urlListMap = new ArrayList<String>();

        List<Map<String, Object>> remoteMeetList =  sqlSession.selectList( NAMESPACE + "selectRemoteMeetingInfoAndNoteList", param);

        for(Map<String,Object> remoteMeetInfo :  remoteMeetList){
            // 녹화 여부
            String recYn = remoteMeetInfo.get("rcrd_yn") == null? "N": remoteMeetInfo.get("rcrd_yn").toString();
            if(!("N").equals(recYn)){
                String conferenceId = remoteMeetInfo.get("mtg_uuid") == null? "" : remoteMeetInfo.get("mtg_uuid").toString();
                String fiTitle = remoteMeetInfo.get("sitebrfg_tit") == null? "" : remoteMeetInfo.get("sitebrfg_tit").toString();
                String fiNo = remoteMeetInfo.get("sitebrfg_no") == null? "" : remoteMeetInfo.get("sitebrfg_no").toString();


                String recFileGrpCd = remoteMeetInfo.get("rcrd_athg_uuid") == null? "" : remoteMeetInfo.get("rcrd_athg_uuid").toString();

                String sysId = param.get("sys_id") == null? "" : param.get("sys_id").toString();
                Tenant tenant = new DefaultTenant().createInstance(sysId);
                TenancyContext tenancyContext = TenancyContextHolder.createEmptyContext();
                tenancyContext.setTenant(tenant);
                TenancyContextHolder.setContext(tenancyContext);


                // 녹화 정보 가져오기
                Map<String,Object> recodeInfoMap = remoteMeetingAPIService.callConferenceRecodeVideoFileDownloadInfoSearch(conferenceId);

                if(recodeInfoMap.keySet().size() > 0){
                    List<Map<String,Object>> recordFileMapList = recodeInfoMap.get("recordFiles") == null? new ArrayList<Map<String, Object>>(): (List<Map<String, Object>>) recodeInfoMap.get("recordFiles");

                    for(Map<String,Object> recordFile : recordFileMapList){
                        String recordFileId =  recordFile.get("recordedFileId") == null? "" : recordFile.get("recordedFileId").toString();
                        Map<String,Object> recodeFileInfo = remoteMeetingAPIService.callConferenceRecodeVideoFileDownloadRequest(recordFileId);

                        String progressCheckUrl = recodeFileInfo.get("progressCheckUrl") == null? "" : recodeFileInfo.get("progressCheckUrl").toString();
                        Map<String,Object> fileDownInfo = remoteMeetingAPIService.callConferenceRecodeVideoFileDownloadUrlRequest(progressCheckUrl);

                        String downLoadUrl = fileDownInfo.get("downloadUrl") == null? "" : fileDownInfo.get("downloadUrl").toString();

                        Map<String,Object> downLoadFileInfo = remoteMeetingAPIService.callConferenceRecodeVideoFileDownloadStart(downLoadUrl);
                        String replaceDownLoadUrl = downLoadFileInfo.get("url") == null? "" :downLoadFileInfo.get("url").toString();
                        replaceDownLoadUrl =replaceDownLoadUrl.replace(" ","+"); // 리모트콜 쪽에서 공백처리랑 URLEncoding을 안해서 넘겨서, 파일을 받기 위해, 공백을 + 로 변경함
                        urlListMap.add(replaceDownLoadUrl);

                        //파일 다운로드
                        this.recVideoDownLoadUtils(replaceDownLoadUrl,fiNo+"-"+fiTitle,recFileGrpCd);
                    }
                }
            }
        }

    }



    // AI 회의록 수신 API 스케줄러
    public void callAIConferenceNotesAPI(HashMap<String,Object> map){
        String conferenceId = map.get("mtg_uuid") == null? "" : map.get("mtg_uuid").toString();
        String rmId = map.get("vconf_uuid") == null? "" : map.get("vconf_uuid").toString();

        Map<String,Object> notesMap = remoteMeetingAPIService.callAIConferenceNotesFind(conferenceId);

        if( null != notesMap && notesMap.keySet().size() > 0){

            List<Map<String,Object>> noteList = notesMap.get("chattingList") == null? new ArrayList<Map<String, Object>>() : (ArrayList<Map<String, Object>>)notesMap.get("chattingList");

            if(noteList.size() > 0){
                //채팅 들어온 순서대로 나열
                Collections.sort(noteList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
                        return (Integer)obj1.get("seq") - (Integer)obj2.get("seq");
                    }
                });

                StringBuffer chattingString = new StringBuffer();

                boolean check = false;

                for(Map<String,Object> chat : noteList){
                    String sentence = chat.get("sentence") == null ? "": chat.get("sentence").toString();
                    String displayName = chat.get("displayName") == null ? "": chat.get("displayName").toString();
                    String timestamp = chat.get("timestamp") == null ? "": chat.get("timestamp").toString();
                    Long time = Long.parseLong(timestamp);
                    Date timestampDate = new Date(time);
                    SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm", Locale.getDefault());
                    String timestampFormat = sdf.format(timestampDate);

                    if(!check){
                        SimpleDateFormat haederDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        chattingString.append("----------------");
                        chattingString.append(haederDate.format(timestampDate));
                        chattingString.append("----------------\r\n");
                        check = true;
                    }

                    //대화 내용처럼 저장.
                    chattingString.append("[ ");
                    chattingString.append(displayName);
                    chattingString.append(" ]");
                    chattingString.append(" [ ");
                    chattingString.append(timestampFormat);
                    chattingString.append(" ] ");
                    chattingString.append(sentence);
                    chattingString.append("\r\n");
                }

            }
        }
    }


    // 회의 시간은 잡아놨으나, API 내에서 END CALL BACK 오지 않는다면, 해당 스케줄러를 실행 시킨다.
    public void deleteRemoteMeetingInfo(HashMap<String,Object> map){
        Map<String,Object> reservationConference = sqlSession.selectOne(NAMESPACE +"selectReservationRemoteMeetingForFirmId",map);
        if( null !=  reservationConference){
            String rmProgSts = reservationConference.get("vconf_sts_ccd") == null? "" : reservationConference.get("vconf_sts_ccd").toString();
            if(StringUtils.isNotEmpty(rmProgSts) && rmProgSts.equals(RemoteMeetingProgCode.REMOTE_MEETING_CONFERENCE_NOT_START.getCode())){
                sqlSession.update(NAMESPACE +"deleteRemoteMeetingInfo", map);
            }
        }
    }

    public void recVideoDownLoadUtils(String downLoadUrl, String fiTitle, String recFileGrpCd){
        InputStream inputStream = null;
        URL url = null;
        HttpURLConnection conn = null;
        File file = null;
        FileOutputStream fileOutputStream = null;

        String fileNm = fiTitle+".mp4";

        try{
            file = new File(fileUploadPath  + UUID.randomUUID());

            url = new URL(downLoadUrl);
            conn = (HttpURLConnection)url.openConnection();

            if (HttpURLConnection.HTTP_OK != conn.getResponseCode()) {
                throw new IOException(conn.getResponseMessage());
            }
            inputStream = conn.getInputStream();

            fileOutputStream = new FileOutputStream(file);
            final int BUFFER_SIZE = 4096;
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((inputStream.read(buffer)) != -1) {
                bytesRead = inputStream.read(buffer);
                fileOutputStream.write(buffer, 0, bytesRead);
            }
	
	        SimpleMultipartFileItem fileItem = AthfServiceUtil.newMultipartFileItem(
					UUID.randomUUID().toString(),
					recFileGrpCd,
					fileNm,
					file
	        );
			stdFileService.createWithMultipart(fileItem);

            fileOutputStream.close();
            inputStream.close();
        }catch (IOException io){
            LOGGER.error(io.getMessage());
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }finally {
            if( null != fileOutputStream){
                try {
                    fileOutputStream.close();
                }catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
            if( null != inputStream){
                try {
                    inputStream.close();
                }catch (Exception e){
                    LOGGER.error(e.getMessage());
                }
            }

        }
    }



}
