package smartsuite.app.bp.rfx.sitebriefing.remotemeeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code.RemoteMeetingProgCode;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.RemoteMeetProperties;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.security.authentication.Auth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class RemoteMeetingAPIService {


    private static Logger logger = LoggerFactory.getLogger("remoteMeetAopExecution");

    private static String accessToken = "";

    // callback url
    @Value("#{globalProperties['remote.api.startCallBackUrl']}")
    private String startcallbackurl;

    @Value("#{globalProperties['remote.api.endCallBackUrl']}")
    private String endcallbackurl;

    // 임시 Data
    private static final String CLIENTUSERID = "emro";
    private static final String CLIENTCOMPANYID = "emro_solution_team";


    // Result Type
    private static final String RESULT_TYPE_JSON = "JSON";
    private static final String RESULT_TYPE_NONE = "NONE";


    // Request Type
    private static final String REQUEST_METHOD_POST = "POST";
    private static final String REQUEST_METHOD_GET = "GET";
    private static final String REQUEST_METHOD_DELETE = "DELETE";


    // Content Type
    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    private static final String CONTENT_TYPE_URL_ENCODING = "application/x-www-form-urlencoded;charset=UTF-8";


    // Authorization
    private static final String AUTHORIATION_BASIC ="basic";
    private static final String AUTHORIATION_BEARER ="Bearer";


    // parameter Type
    private static final String PARAMETER_TYPE_JSON = "json";
    private static final String PARAMETER_TYPE_STRING = "string";

    private static final String URL_CONTEXT_SLASH = "/";

    /**
     *
     * 최초 access token이 없을 경우 취득해오는 과정
     *
     *
    */
    public void callRemoteCallOauthToken(String authorization) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        String apiUrl = "https://api.remotemeeting.com/oauth/token";

        accessToken = ""; // accessToken 초기화
        HttpURLConnection http = null;

        try{
            URL url = new URL(apiUrl);

            http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod(REQUEST_METHOD_POST);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // remoteCall API 에서 CLIENTID 및 secretKey는 base64로 암호화
            byte[] authKeyByte = new String(RemoteMeetProperties.CLIENTID + ":" + RemoteMeetProperties.SECRETKEY).getBytes();
            String base64EncodingAuth = Base64.encodeBase64String(authKeyByte);

            http.setRequestProperty("Authorization", AUTHORIATION_BASIC +" " +base64EncodingAuth);


            // Parameter 입력
            http.setDoOutput(true);
            //API Parameter
            Map<String,Object> parameter = new LinkedHashMap<String, Object>();
            parameter.put("grant_type","client_credentials");
            parameter.put("scope","USER");

            StringBuilder postData = new StringBuilder();
            for(Map.Entry<String,Object> param : parameter.entrySet()) {
                if(postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            logger.info("POST DATA PARAMETER STRING = " + postData.toString());
            byte[] postDataByte = postData.toString().getBytes("UTF-8");
            http.setDoOutput(true);
            http.getOutputStream().write(postDataByte);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> resultTokenMap = mapper.readValue(http.getInputStream(), Map.class);

            for(String getKey : resultTokenMap.keySet()){

                String key = getKey;
                String value = resultTokenMap.get(getKey) == null ? "" : resultTokenMap.get(getKey).toString();

                /*LOGGER.info(getKey + " = " + value);*/

                if(key.equals("returnCode")) throw new CommonException("에러 발생"); // error 코드가 넘어올 경우, 자체적으로 해당 메소드에서 끊어준다.
            }

            accessToken = resultTokenMap.get("access_token") == null ? "" : resultTokenMap.get("access_token").toString();

            logger.info("accessToken ="+accessToken);
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            if(null != http) try{
                http.getInputStream().close();
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
    }


    /**
     * API 호출 후 Result가 StringBuffer일 경우 사용
     param apiUrl
     * @param parameter
     * @param method
     * @param resultType
     * @param contentType
     * @param authorization
     * @param parameterType
     * @return

     */
    private StringBuffer remoteCallApiConnectionResultStringBuffer(String apiUrl , Map<String,Object> parameter , String method , String resultType , String contentType, String authorization , String parameterType) {

        Map<String, Object> resultDataMap = new HashMap<String, Object>();

        InputStream is = null;
        StringBuffer stringResultData = new StringBuffer();
        BufferedReader in = null;
        HttpURLConnection http = null;

        try{
            http = remoteCallApiConnectionCore(apiUrl,parameter,method,resultType,contentType,authorization,parameterType);
            is = http.getInputStream();
            if(null != is){
                try{
                    in = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
                    while(null != in.readLine()) { // response 출력
                        stringResultData.append(in.readLine());
                    }
                }catch (Exception e){
                    throw new CommonException(ErrorCode.FAIL, e);
                }finally {
                    if(null != in) in.close();
                }
            }else{
                logger.info(" Connection error ");
            }

        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            if(null != http) try{
                http.getInputStream().close();
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
        return stringResultData;
    }


    /**
     * 화상회의를 연결하는 코어 소스
     * @param apiUrl
     * @param parameter
     * @param method
     * @param resultType
     * @param contentType
     * @param authorization
     * @param parameterType
     * @return

     */
    public HttpURLConnection remoteCallApiConnectionCore(String apiUrl , Map<String,Object> parameter , String method , String resultType , String contentType, String authorization , String parameterType) {

        HttpURLConnection http = null;
        URL url = null;
        StringBuilder postData = new StringBuilder();
        JsonObject jsonObject = new JsonObject();
        byte[] postDataByte = null;
        String apiUrlGet = apiUrl;
        String methodGet = method;

        try{

            String requestId = parameter.get("sitebrfg_vconf_uuid") == null?  "" : parameter.get("sitebrfg_vconf_uuid").toString();

            //token 가져오기,
            this.callRemoteCallOauthToken(authorization);

            //API Parameter
            parameter.put("access_token",accessToken); //Oauth token
            parameter.put("clientId",RemoteMeetProperties.CLIENTID); // EMRO Client ID 값

            String userName = Auth.getCurrentUserName();
            if(StringUtils.isNotEmpty(userName)){
                parameter.put("clientUserId",userName); // 요청자 user id
            }else{
                parameter.put("clientUserId", CLIENTUSERID); //user id
            }

            Map<String,Object> userInfo =  Auth.getCurrentUserInfo() == null ? new HashMap<String, Object>() : Auth.getCurrentUserInfo();
            if(null != userInfo && userInfo.keySet().size() > 0){
                String deptCd = Auth.getCurrentUserInfo().get("dept_cd") == null? "" : Auth.getCurrentUserInfo().get("dept_cd").toString();
                if(StringUtils.isNotEmpty(deptCd)){
                    parameter.put("clientCompanyId",deptCd); // 요청자 user dept cd
                }else{
                    parameter.put("clientCompanyId", CLIENTCOMPANYID); //user id의 상위 형태로 보는 모양, 내부적으로는 Dept_cd를 사용하면 될 것으로 생각됨.
                }
            }else{
                parameter.put("clientCompanyId", CLIENTCOMPANYID);
            }

            /** callback 함수를 받으려면 request ID를 생성해서 보내줘야하는 것으로 확인
             ( callback 함수로온 내역과 request id를 기준으로 매칭 시킬때 필요한 키로 보임 )
             **/
            parameter.put("requestId", requestId);
            parameter.put("debugMode", "1");

            logger.info("========================================REMOTE CALL API START ======================================");
            logger.info("TIME"+new Date());
            logger.info("REQUEST ID =" + requestId);
            logger.info("ACCESS TOKEN =" + accessToken);
            logger.info("CLIENT USER ID =" + CLIENTUSERID);
            logger.info("START CALL BACK URL =" + startcallbackurl);
            logger.info("END CALL BACK URL =" + endcallbackurl);
            logger.info("========================================REMOTE CALL API END   ======================================");


            if(parameterType.equals(PARAMETER_TYPE_STRING)) {
                for (Map.Entry<String, Object> param : parameter.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }


                postDataByte = postData.toString().getBytes("UTF-8");

            }else if(parameterType.equals(PARAMETER_TYPE_JSON)){

                //json object 에 밀어넣은 값은 URL Encoding 하면 받지못함.
                for (Map.Entry<String, Object> param : parameter.entrySet()) {
                    jsonObject.addProperty(param.getKey(),String.valueOf(param.getValue()));
                }

                postDataByte = jsonObject.toString().getBytes("UTF-8");
            }


            if(methodGet.equals(REQUEST_METHOD_GET)) apiUrlGet += "?"+postData;
            logger.info("REQUEST URL ="+apiUrlGet);

            url = new URL(apiUrlGet);

            http = (HttpURLConnection) url.openConnection();

            if(StringUtils.isEmpty(methodGet)) methodGet =REQUEST_METHOD_POST;
            http.setRequestMethod(methodGet);
            http.setRequestProperty("Content-Type", contentType);
            if(authorization.equals(AUTHORIATION_BASIC)){
                http.setRequestProperty("Authorization", AUTHORIATION_BASIC +" " +accessToken);
            }else if(authorization.equals(AUTHORIATION_BEARER)){
                http.setRequestProperty("Authorization", AUTHORIATION_BEARER +" " +accessToken);
            }else {
                http.setRequestProperty("Authorization", AUTHORIATION_BASIC +" " +accessToken);
            }

            // GET Method인 경우, callback url로 수신받음.
            if(methodGet.equals(REQUEST_METHOD_POST)) {
                // Parameter 입력
                http.setDoOutput(true);
                http.getOutputStream().write(postDataByte);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            if( null != http){
                try{
                    http.getOutputStream().close();
                } catch (Exception e){
                    logger.error(e.getMessage());
                }
            }
        }
        return http;
    }


    /**
     * 화상회의 화면을 노출하기 위해서, 받아온 생성한 URL을 화면에 던져주는 메소드
     * @param apiUrl
     * @param parameter
     * @param method
     * @param resultType
     * @param contentType
     * @param authorization
     * @param parameterType
     * @return

     */
    public Map<String,Object> remoteCallApiConnectionRedirectCallUrlGenerate(String apiUrl , Map<String,Object> parameter , String method , String resultType , String contentType, String authorization , String parameterType ,String firmId) {

        Map<String,Object> resultMap = new HashMap<String, Object>();
        StringBuilder postData = new StringBuilder();
        JsonObject jsonObject = new JsonObject();
        byte[] postDataByte = null;
        String apiUrlGet = apiUrl;

        try{
            //access token이 존재하지 않는다면, 새로 취득
            this.callRemoteCallOauthToken(authorization);

            //API Parameter
            parameter.put("access_token",accessToken); //Oauth token
            parameter.put("clientId",RemoteMeetProperties.CLIENTID); // EMRO Client ID 값

            parameter.put("clientUserId", CLIENTUSERID); //user id
            parameter.put("clientCompanyId", CLIENTCOMPANYID); //user id의 상위 형태로 보는 모양, 내부적으로는 Dept_cd를 사용하면 될 것으로 생각됨.

            /** callback 함수를 받으려면 request ID를 생성해서 보내줘야하는 것으로 확인
             ( callback 함수로온 내역과 request id를 기준으로 매칭 시킬때 필요한 키로 보임 )
             **/

            parameter.put("requestId", firmId);

            logger.info("========================================REMOTE CALL API START ======================================");
            logger.info("REQUEST ID(firm Id) =" + firmId);
            logger.info("START CALL BACK URL =" + startcallbackurl);
            logger.info("END CALL BACK URL =" + endcallbackurl);
            logger.info("========================================REMOTE CALL API END   ======================================");


            if(parameterType.equals(PARAMETER_TYPE_STRING)) {
                for (Map.Entry<String, Object> param : parameter.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }


                postDataByte = postData.toString().getBytes("UTF-8");

            }else if(parameterType.equals(PARAMETER_TYPE_JSON)){

                //json object 에 밀어넣은 값은 URL Encoding 하면 받지못함.
                for (Map.Entry<String, Object> param : parameter.entrySet()) {
                    jsonObject.addProperty(param.getKey(),param.getValue().toString());
                }

                postDataByte = jsonObject.getAsString().getBytes("UTF-8");
            }

            if(method.equals(REQUEST_METHOD_GET)) apiUrlGet += "?"+postData;
            logger.info("REQUEST URL ="+apiUrlGet);

            resultMap.put("url",apiUrlGet);
        }catch (Exception e){
            logger.error(e.getMessage());
        }

        return resultMap;
    }


    /**
     * 화상회의 녹화 다운로드
     * @param apiUrl
     * @param parameter
     * @param method
     * @param resultType
     * @param contentType
     * @param authorization
     * @param parameterType
     * @return
     */
    public Map<String,Object> remoteCallApiRecDownloadCallUrlGenerate(String downloadUrl , Map<String,Object> parameter , String method , String resultType , String contentType, String authorization , String parameterType ) {

        Map<String,Object> resultMap = new HashMap<String, Object>();
        StringBuilder postData = new StringBuilder();
        JsonObject jsonObject = new JsonObject();
        byte[] postDataByte = null;
        String downloadUrlGet = downloadUrl;

        try{
            //access token이 존재하지 않는다면, 새로 취득
            this.callRemoteCallOauthToken(authorization);


            //API Parameter
            if(authorization.equals(AUTHORIATION_BASIC)){
                parameter.put("authorization", AUTHORIATION_BASIC +" " +accessToken); //Oauth token
            }else if(authorization.equals(AUTHORIATION_BEARER)){
                parameter.put("authorization", AUTHORIATION_BEARER +" " +accessToken); //Oauth token
            }else {
                parameter.put("authorization", AUTHORIATION_BASIC +" " +accessToken); //Oauth token
            }


            if(parameterType.equals(PARAMETER_TYPE_STRING)) {
                for (Map.Entry<String, Object> param : parameter.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }


                postDataByte = postData.toString().getBytes("UTF-8");

            }else if(parameterType.equals(PARAMETER_TYPE_JSON)){

                //json object 에 밀어넣은 값은 URL Encoding 하면 받지못함.
                for (Map.Entry<String, Object> param : parameter.entrySet()) {
                    jsonObject.addProperty(param.getKey(),param.getValue().toString());
                }

                postDataByte = jsonObject.getAsString().getBytes("UTF-8");
            }

            if(method.equals(REQUEST_METHOD_GET)) downloadUrlGet += "&"+postData;
            logger.info("REQUEST URL ="+downloadUrlGet);

            resultMap.put("url",downloadUrlGet);
        }catch (Exception e){
            logger.error(e.getMessage());
        }

        return resultMap;
    }




    /**
         *
         * Remote Call API
         *
         * 회의 개설 화면 호출하기
         *
         *
         * URI           : /openapi/conference/start-form
         * 기능          : 회의 개설 페이지 호출 Method GET
         * Request Type  : Parameter
         * Response Type : -
         * Method        : GET
         * 설명          : 회의를 개설할 수 있는 RemoteMeeting 서비스 페이지를 호출합니다.
         *                 일반적인 웹페이지 링크 방식입니다.
         *                 회의가 개설되면 callback URL 로 회의 정보가 전달됩니다.
         */

    public StringBuffer callConferenceStartForm() {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        String apiUrl = "https://www.remotemeeting.com/openapi/conference/start-form";

        Map<String,Object> parameter = new HashMap<String, Object>();

        //TODO 해당 URL은 자체적으로 만든 URL을 담아 Result 받은내역을 표기하는? 화면으로 사용 필요
        parameter.put("startCallbackUrl", startcallbackurl);
        parameter.put("endCallbackUrl", endcallbackurl);
        parameter.put("debugMode","1");

        Map<String,Object> responseMap = new HashMap<String, Object>();
        StringBuffer responseData = this.remoteCallApiConnectionResultStringBuffer(apiUrl,parameter,REQUEST_METHOD_GET ,RESULT_TYPE_NONE,CONTENT_TYPE_URL_ENCODING,AUTHORIATION_BASIC,PARAMETER_TYPE_STRING);


        return responseData;
    }


    /**
     *
     * Remote Call API
     *
     * 회의 바로 개설하기
     *
     *
     * URI           : /openapi/conference/start
     * 기능          : 회의 바로 개설 호출 Method GET
     * Request Type  : Parameter
     * Response Type : -
     * Method        : GET
     * 설명          : 고객사 시스템에서 새로운 창(탭)으로 바로 회의를 개설합니다.
     *                 회의가 개설되면 callback URL 로 회의 정보가 전달됩니다
     *
     *
     */

    public Map<String,Object> callConferenceNowStart(Map<String,Object> param) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        String apiUrl = "https://www.remotemeeting.com/openapi/conference/start";

        Map<String,Object> parameter = new HashMap<String, Object>();

        String displayName = Auth.getCurrentUserName();
        String mtg_tit = param.get("mtg_tit") == null? "" :param.get("mtg_tit").toString();
        String type = param.get("type") == null? RemoteMeetProperties.CONFERENCE_OPTION :param.get("type").toString();
        String videoQuality = param.get("videoQuality") == null? RemoteMeetProperties.VIDEO_QUALITY_OPTION :param.get("videoQuality").toString();

        //TASK_UUID 넣어야함
        String firmId = param.get("sitebrfg_vconf_uuid") == null? "" :  param.get("sitebrfg_vconf_uuid").toString();


        //TODO 해당 URL은 자체적으로 만든 URL을 담아 Result 받은내역을 표기하는? 화면으로 사용 필요
        parameter.put("startCallbackUrl", startcallbackurl);
        parameter.put("endCallbackUrl", endcallbackurl);
        parameter.put("displayName",displayName); //개설자 이름
        parameter.put("mtg_tit",mtg_tit); //회의 제목
        parameter.put("debugMode","1");

        /**
         * 회의 모드
         *
         * option: 영상회의 : video
         *         문서공유 : document
         *         화면공유 : screen
         *         세미나 : semina
         */
        parameter.put("type",type);

        /**
         * 해상도
         *
         *  option : 일반화질 : 360
         *           HD화질 : 720
         */
        parameter.put("videoQuality",videoQuality); //API 문서에서 360을 기본으로 쓰라고 추천

        Map<String,Object> resultMap = new HashMap<String, Object>();

        try{
            resultMap = this.remoteCallApiConnectionRedirectCallUrlGenerate(apiUrl,parameter,REQUEST_METHOD_GET,RESULT_TYPE_NONE,CONTENT_TYPE_URL_ENCODING,AUTHORIATION_BASIC,PARAMETER_TYPE_STRING,firmId);
        }catch (Exception e){
            new CommonException("RemoteCall Meeting Conection ERROR");
        }

        return resultMap;
    }





    public Map<String,Object> conferenceJoinUser(Map<String,Object> param) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        String apiUrl = "https://remotemeeting.com/openapi/conference/start";

        Map<String,Object> parameter = new HashMap<String, Object>();

        String displayName = Auth.getCurrentUserName();
        String userId = Auth.getCurrentUserInfo().get("usr_id") == null? "" : Auth.getCurrentUserInfo().get("usr_id").toString();
        String deptCd = Auth.getCurrentUserInfo().get("dept_cd") == null? "" : Auth.getCurrentUserInfo().get("dept_cd").toString();
        //TASK_UUID 넣어야함
        String firmId = param.get("sitebrfg_vconf_uuid") == null? "" :  param.get("sitebrfg_vconf_uuid").toString();
        String conferenceId = param.get("mtg_uuid") == null? "" :  param.get("mtg_uuid").toString();

        parameter.put("clientUserId" , userId);
        parameter.put("clientCompanyId" , deptCd);
        parameter.put("displayName" , displayName);
        parameter.put("requestId" , firmId);
        parameter.put("conferenceId" , conferenceId);

        parameter.put("debugMode","1");

        Map<String,Object> resultMap = new HashMap<String, Object>();

        try{
            resultMap = this.remoteCallApiConnectionRedirectCallUrlGenerate(apiUrl,parameter,REQUEST_METHOD_GET,RESULT_TYPE_NONE,CONTENT_TYPE_URL_ENCODING,AUTHORIATION_BASIC,PARAMETER_TYPE_STRING,firmId);
        }catch (Exception e){
            new CommonException("RemoteCall Meeting Conection ERROR");
        }

        return resultMap;
    }




    /**
     *
     * Remote Call API
     *
     * 회의 예약하기
     * - 회의가 필요한 시간에 미리 회의를 예약하고 회의를 진행할 수 있습니다. 예약 회의는 현재
     * 시간보다 미래의 시간으로만 설정이 가능합니다.
     *
     *
     *
     * URI           : /openapi/reservation
     * 기능          : 회의 예약 호출 Method GET
     * Request Type  : JSON
     * Response Type : JSON
     * Method        : POST
     * 설명          : 회의 예약을 진행할 수 있다.
     *
     *
     */

    public Map<String,Object> callConferenceReservation(Map<String,Object> parameter) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        String apiUrl = RemoteMeetProperties.REMOTE_MEETING_OPEN_API_RESERVATION_URL;

        StringBuffer responseData = this.remoteCallApiConnectionResultStringBuffer(apiUrl,parameter,REQUEST_METHOD_POST,RESULT_TYPE_JSON,CONTENT_TYPE_JSON,AUTHORIATION_BEARER,PARAMETER_TYPE_JSON);

        JsonParser parser = new JsonParser();
        JsonObject jsonObj = null;
        Map<String,Object> reserviationResultMap = new HashMap<String, Object>();

        if(StringUtils.isNotEmpty(responseData.toString())){
            Object obj = parser.parse( responseData.toString() );
            jsonObj = (JsonObject) obj;
        }

        String returnCode = jsonObj.get("returnCode") == null? "" : jsonObj.get("returnCode").getAsString();
        reserviationResultMap.put("returnCode",returnCode);
        if(null != jsonObj.get("returnData")){
            JsonObject returnDataObject = jsonObj.get("returnData") == null? new JsonObject() : jsonObj.get("returnData").getAsJsonObject();
            String reservationDateID = returnDataObject.get("reservationDateID") == null? "":returnDataObject.get("reservationDateID").getAsString(); //예약회의 ID
            reserviationResultMap.put("reservationDateID",reservationDateID);
        }else{
            String message = jsonObj.get("message") == null? "" : jsonObj.get("message").getAsString();
            reserviationResultMap.put("message",message);
        }

        String fiId = parameter.get("sitebrfg_uuid") == null? "" : parameter.get("sitebrfg_uuid").toString();
        String firmId = parameter.get("sitebrfg_vconf_uuid") == null? "" : parameter.get("sitebrfg_vconf_uuid").toString();
        reserviationResultMap.put("sitebrfg_uuid",fiId);
        reserviationResultMap.put("sitebrfg_vconf_uuid",firmId);


       return reserviationResultMap;
    }


    /**
     *
     * Remote Call API
     *
     * 예약 회의 조회하기
     * - 예약을 완료하고 수신한 예약 정보로 예약된 회의 상세 정보를 조회할 수 있습니다.
     * 예약된 회의를 시작하기 위해서는 반드시 조회된 회의 시작 URI 를 가지고 있어야 합니다.
     *
     * URI           : /openapi/reservation/{reservationDateId}
     * 기능          : 예약회의 조회 호출 Method GET
     * Request Type  : -
     * Response Type : JSON
     * Method        : GET
     * 설명          : 예약된 회의를 조회할 수 있다.
     *
     *
     */

    public Map<String,Object> callConferenceReservationFind(Map<String,Object> parameter) {

        String reservationDateId = parameter.get("reservationDateID") == null? "" : parameter.get("reservationDateID").toString();
        String apiUrl = RemoteMeetProperties.REMOTE_MEETING_OPEN_API_RESERVATION_URL + URL_CONTEXT_SLASH + reservationDateId;

        StringBuffer responseData = this.remoteCallApiConnectionResultStringBuffer(apiUrl,parameter,REQUEST_METHOD_GET,RESULT_TYPE_JSON,CONTENT_TYPE_JSON,AUTHORIATION_BEARER,PARAMETER_TYPE_STRING);

        /**
         *
         * RESPONSE SAMPLE
         *
         * {
         *      "returnCode": "40100",
         *      "returnData": {
         *      "reservationID": "ff8080817236b88a017236b8cbb70000",
         *      "reservationDateID": "ff8080817236b88a017236b8cbc70002",
         *      "mtg_tit": "회의 제목",
         *      "description": "회의 내용을 입력합니다.",
         *      "startTime": 1747803600000,
         *      "endTime": 1747807200000,
         *      "startUrl":
         *      "https://www.api.remotemeeting.com/openapi/reservation/start?reservationId=ff8080817236b88a017236b8cbb70000"
         * }
         * }
         */

        JsonParser parser = new JsonParser();
        JsonObject jsonObj = null;
        if(StringUtils.isNotEmpty(responseData.toString())){
            Object obj = parser.parse( responseData.toString() );
            jsonObj = (JsonObject) obj;
        }

        String returnCode = jsonObj.get("returnCode") == null? "" : jsonObj.get("returnCode").getAsString();
        Map<String,Object> resultMap = new HashMap<String, Object>();
        resultMap.put("returnCode",returnCode);

        if(null != jsonObj.get("returnData")){
            JsonObject returnDataObject = jsonObj.get("returnData") == null? new JsonObject() : jsonObj.get("returnData").getAsJsonObject();

            //response data는
            String reservationId = returnDataObject.get("reservationId") == null? "":returnDataObject.get("reservationId").getAsString(); //예약회의 ID
            String getReservationDateId = returnDataObject.get("reservationDateId") == null? "":returnDataObject.get("reservationDateId").getAsString(); // 예약 일시 ID
            String mtg_tit = returnDataObject.get("mtg_tit") == null? "":returnDataObject.get("mtg_tit").getAsString(); // 회의제목
            String description= returnDataObject.get("description") == null? "": returnDataObject.get("description").getAsString(); // 회의내용

            String startUrl = returnDataObject.get("startUrl") == null? "" : returnDataObject.get("startUrl").getAsString(); //회의 시작 URL
            long startTime = returnDataObject.get("startTime") == null? null : (long)returnDataObject.get("startTime").getAsLong(); // 시작 예약 일시
            long endTime = returnDataObject.get("endTime")  == null? null : (long)returnDataObject.get("endTime").getAsLong(); // 회의 종료 일시   yyyyMMdd hhmm

            Date startDate=new Date(startTime);
            Date endDate=new Date(endTime);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String startDateFormat = sdf.format(startDate);
            String endDateFormat = sdf.format(endDate);

            resultMap.put("vconf_rsvn_uuid",reservationId);
            resultMap.put("vconf_rsvn_dttm_uuid",getReservationDateId);
            resultMap.put("vconf_rsvn_url",startUrl);
            resultMap.put("mtg_tit",mtg_tit);
            resultMap.put("description",description);
            resultMap.put("startTime",startDateFormat);
            resultMap.put("endTime",endDateFormat);
            resultMap.put("returnCode",returnCode);

            String fiId = parameter.get("sitebrfg_uuid") == null? "" : parameter.get("sitebrfg_uuid").toString();
            String firmId = parameter.get("sitebrfg_vconf_uuid") == null? "" : parameter.get("sitebrfg_vconf_uuid").toString();
            resultMap.put("sitebrfg_uuid",fiId);
            resultMap.put("sitebrfg_vconf_uuid",firmId);
            resultMap.put("vconf_sts_ccd", RemoteMeetingProgCode.REMOTE_MEETING_CONFERENCE_NOT_START.getCode());
        }else{
            String message = jsonObj.get("message") == null? "" : jsonObj.get("message").getAsString();
            throw new CommonException(message);
        }


        return resultMap;
    }




    /**
     *
     * Remote Call API
     *
     * 예약 회의 삭제 하기
     *
     *
     * URI           : /openapi/reservation/{reservationDateId}
     * 기능          : 회의 예약 삭제 호출 Method GET
     * Request Type  : Parameters
     * Response Type : JSON
     * Method        : DELETE
     * 설명          : 예약된 회의를 삭제할 수 있다.
     *
     *
     */
    public Map<String,Object> callConferenceReservationDelete(Map<String,Object> parameter) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr
        String reservationDateID = parameter.get("vconf_rsvn_uuid") == null ? "" : parameter.get("vconf_rsvn_uuid").toString();

        String apiUrl = RemoteMeetProperties.REMOTE_MEETING_OPEN_API_RESERVATION_URL + URL_CONTEXT_SLASH + reservationDateID;
        parameter.put("reservationDateId",reservationDateID); //예약회의 ID

        StringBuffer responseData = this.remoteCallApiConnectionResultStringBuffer(apiUrl,parameter,REQUEST_METHOD_DELETE,RESULT_TYPE_JSON,CONTENT_TYPE_JSON,AUTHORIATION_BEARER,PARAMETER_TYPE_JSON);

        JsonParser parser = new JsonParser();
        JsonObject jsonObj = null;
        if(StringUtils.isNotEmpty(responseData.toString())){
            Object obj = parser.parse( responseData.toString() );
            jsonObj = (JsonObject) obj;
        }

        String returnCode = jsonObj.get("returnCode") == null ? "" : jsonObj.get("returnCode").getAsString();
        String message = jsonObj.get("message") == null ? "" :  jsonObj.get("message").getAsString();

        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("returnCode",returnCode);
        resultMap.put("message",message);
        return resultMap;
    }




    /**
     *
     * Remote Call API
     *
     * 예약 회의 시작 하기
     *
     * URI           : /openapi/reservation/start
     * 기능          : 예약 회의 시작하기 호출 Method GET
     * Request Type  : Parameter
     * Response Type : -
     * Method        : GET
     * 설명          : 회의 예약 정보로 새로운 창(탭)에서 회의를 바로 개설할 수 있다.
     *                 회의가 개설되면 callback URL 로 회의 정보가 전달됩니다.
     *
     *
     */
    public Map<String,Object> callConferenceReservationStart(String reservationId , String confrenceJoinName , String firmId) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        String apiUrl = RemoteMeetProperties.REMOTE_MEETING_OPEN_API_RESERVATION_URL + URL_CONTEXT_SLASH +"start";

        Map<String,Object> parameter = new HashMap<String, Object>();

        //TODO 해당 URL은 자체적으로 만든 URL을 담아 Result 받은내역을 표기하는? 화면으로 사용 필요
        parameter.put("startCallbackUrl", startcallbackurl);
        parameter.put("endCallbackUrl", endcallbackurl);

        parameter.put("reservationId",reservationId); // 예약 ID
        parameter.put("displayName",confrenceJoinName); // 참여자 이름

        Map<String,Object> resultMap = new HashMap<String, Object>();

        try{
            resultMap = this.remoteCallApiConnectionRedirectCallUrlGenerate(apiUrl,parameter,REQUEST_METHOD_GET,RESULT_TYPE_NONE,CONTENT_TYPE_URL_ENCODING,AUTHORIATION_BASIC,PARAMETER_TYPE_STRING,firmId);
        }catch (Exception e){
            new CommonException("RemoteCall Meeting Conection ERROR");
        }

        return resultMap;


    }

    /**
     *
     * Remote Call API
     *
     * 녹화 영상 다운로드 요청
     *
     *
     * URI           : /openapi/recordFile/downloadRequest
     * 기능          : 녹화 영상 다운로드 요청 호출 Method GET (프로세스 진척도)
     * Request Type  : JSON
     * Response Type : JSON
     * Method        : POST
     * 설명          : 녹화 영상의 정보를 요청하여 다음 단계를 진행할 수 있습니다.
     *
     *
     */
    public Map<String,Object> callConferenceRecodeVideoFileDownloadRequest(String recordFileId) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        String apiUrl = "https://api.remotemeeting.com/openapi/recordFile/downloadRequest";


        Map<String,Object> parameter = new HashMap<String, Object>();
        parameter.put("recordFileId",recordFileId); //회의 ID
        parameter.put("debugMode","1");

        StringBuffer responseData = this.remoteCallApiConnectionResultStringBuffer(apiUrl,parameter,REQUEST_METHOD_POST,RESULT_TYPE_JSON,CONTENT_TYPE_URL_ENCODING,AUTHORIATION_BEARER,PARAMETER_TYPE_STRING);

        /**
         *
         * RESPONSE SAMPLE
         *
         * {
         *      "returnCode": "40100",
         *      "message": "string",
         *      "debugMessage": "string",
         *      "returnData": {
         *      "progressCheckUrl":"https://docse.remotemeeting.com/record_file/progress?downloadKey=key"
         * }
         * }
         *
         *
         */

        //response data는
        //responseMap.get("progressCheckUrl") // 녹화파일 진척도 확인 URL

        JsonParser parser = new JsonParser();
        JsonObject jsonObj = null;
        if(StringUtils.isNotEmpty(responseData.toString())){
            Object obj = parser.parse( responseData.toString() );
            jsonObj = (JsonObject) obj;
        }

        String returnCode = jsonObj.get("returnCode") == null? "" : jsonObj.get("returnCode").getAsString();
        JsonObject returnDataObject = jsonObj.get("returnData") == null ? new JsonObject() : jsonObj.get("returnData").getAsJsonObject();

        //response data는
        String progressCheckUrl = returnDataObject.get("progressCheckUrl") == null? "":returnDataObject.get("progressCheckUrl").getAsString(); // 녹화파일 진척도 확인 URL

        logger.info("returnCode = "+returnCode);
        logger.info("progressCheckUrl = "+progressCheckUrl);


        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("returnCode",returnCode);
        resultMap.put("progressCheckUrl",progressCheckUrl);

        return resultMap;
    }



    public Map<String,Object> callConferenceRecodeVideoFileDownloadInfoSearch(String conferenceId) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        String apiUrl = "https://api.remotemeeting.com/openapi/recordFile";

        Map<String,Object> parameter = new HashMap<String, Object>();
        parameter.put("conferenceId",conferenceId); //회의 ID

        StringBuffer responseData = this.remoteCallApiConnectionResultStringBuffer(apiUrl,parameter,REQUEST_METHOD_GET,RESULT_TYPE_JSON,CONTENT_TYPE_JSON,AUTHORIATION_BEARER,PARAMETER_TYPE_STRING);
        /*StringBuffer responseData = this.remoteCallApiConnectionResultStringBuffer(apiUrl,parameter,REQUEST_METHOD_POST,RESULT_TYPE_JSON,CONTENT_TYPE_JSON,AUTHORIATION_BEARER,PARAMETER_TYPE_JSON);*/

        /**
         *
         * RESPONSE SAMPLE
         *
         * {
         *      "returnCode": "40100",
         *      "message": "string",
         *      "debugMessage": "string",
         *      "returnData": {
         *      "progressCheckUrl":"https://docse.remotemeeting.com/record_file/progress?downloadKey=key"
         * }
         * }
         *
         *
         */

        //response data는
        //responseMap.get("progressCheckUrl") // 녹화파일 진척도 확인 URL

        JsonParser parser = new JsonParser();
        JsonObject jsonObj = null;
        if(StringUtils.isNotEmpty(responseData.toString())){
            Object obj = parser.parse( responseData.toString() );
            jsonObj = (JsonObject) obj;
        }

        String returnCode = jsonObj.get("returnCode") == null? "" : jsonObj.get("returnCode").getAsString();
        JsonObject returnDataObject = jsonObj.get("returnData") == null ? new JsonObject() : jsonObj.get("returnData").getAsJsonObject();

        String reservationId = returnDataObject.get("conferenceId") == null? "":returnDataObject.get("conferenceId").getAsString(); // 회의 ID

        JsonArray recodrdFiles = returnDataObject.get("recordFiles") == null? new JsonArray() : returnDataObject.get("recordFiles").getAsJsonArray(); // 녹화 파일 목록

        List<Map<String,Object>> recodrdMapList = new ArrayList<Map<String,Object>>();

        for(JsonElement fileElement : recodrdFiles){
            Map<String,Object> recodrdMap = new HashMap<String,Object>();
            JsonObject jsonObject =  fileElement.getAsJsonObject();

            boolean downloadable = jsonObject.get("downloadable") == null? false : jsonObject.get("downloadable").getAsBoolean(); // 다운로드 가능 여부
            String recordedFileId = jsonObject.get("recordedFileId") == null? "":jsonObject.get("recordedFileId").getAsString(); // recordedFile ID
            //date
            long startTime = jsonObject.get("startTime") == null? null : jsonObject.get("startTime").getAsLong(); // 시작 예약 일시
            long endTime = jsonObject.get("endTime")  == null? null : jsonObject.get("endTime").getAsLong(); // 회의 종료 일시   yyyyMMdd hhmm
            Date startDate=new Date(startTime);
            Date endDate=new Date(endTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String startDateFormat = sdf.format(startDate);
            String endDateFormat = sdf.format(endDate);

            recodrdMap.put("recordedFileId",recordedFileId);
            recodrdMap.put("downloadable",downloadable);
            recodrdMap.put("startDateFormat",startDateFormat);
            recodrdMap.put("endDateFormat",endDateFormat);
            recodrdMapList.add(recodrdMap);
        }

        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("returnCode",returnCode);
        resultMap.put("reservationId",reservationId);
        resultMap.put("recordFiles",recodrdMapList);



        return resultMap;
    }


    /**
     *
     * Remote Call API
     *
     * 녹화 영상 다운로드 준비하기
     * - “4.7.1. 녹화 영상 정보 요청하기” 에서 획득한 URL(progressCheckUrl) 로 녹화 영상 다운로드 정보를 요청합니다.
     *
     *
     * URI           : /record_file/progress
     * 기능          : 녹화 영상 진척도 확인 호출 Method GET
     * Request Type  : -
     * Response Type : JSON
     * Method        : GET
     * 설명          : 녹화 영상 파일 다운로드 준비가 완료되었는지 확인 할 수 있습니다. ( 다운로드 가능한 URL 취득 )
     *
     *
     */
    public Map<String,Object> callConferenceRecodeVideoFileDownloadUrlRequest(String progressCheckUrl) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        // callConferenceRecodeVideoFileDownloadRequest method에서 response 받은 progressCheckUrl 로 호출 필요
        String apiUrl = progressCheckUrl;


        Map<String,Object> parameter = new HashMap<String, Object>();

        StringBuffer responseData = this.remoteCallApiConnectionResultStringBuffer(apiUrl,parameter,REQUEST_METHOD_GET,RESULT_TYPE_JSON,CONTENT_TYPE_JSON,AUTHORIATION_BASIC,PARAMETER_TYPE_STRING);

        /**
         *
         * RESPONSE SAMPLE
         *
         * {
         *      "returnCode": "40100",
         *      "message": "string",
         *      "debugMessage": "string",
         *      "returnData": {
         *                       "downloadUrl": "string"
         *                    }
         * }
         *
         *
         */

        //response data는
        //String downloadUrl = responseMap.get("downloadUrl") // 녹화파일 다운로드 URL

        JsonParser parser = new JsonParser();
        JsonObject jsonObj = null;
        if(StringUtils.isNotEmpty(responseData.toString())){
            Object obj = parser.parse( responseData.toString() );
            jsonObj = (JsonObject) obj;
        }

        String returnCode = jsonObj.get("returnCode") == null? "": jsonObj.get("returnCode").getAsString();
        JsonObject returnDataObject = jsonObj.get("returnData") == null?  new JsonObject(): jsonObj.get("returnData").getAsJsonObject();

        //response data는
        String downloadUrl = returnDataObject.get("downloadUrl") == null? "":returnDataObject.get("downloadUrl").getAsString(); // 녹화파일 다운로드 URL

        logger.info("returnCode = "+returnCode);
        logger.info("downloadUrl = "+downloadUrl);


        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("returnCode",returnCode);
        resultMap.put("downloadUrl",downloadUrl);

        return resultMap;
    }


    /**
     *
     * Remote Call API
     *
     * 녹화 영상 다운로드 시작하기
     * - “4.7.2. 다운로드 준비하기” 에서 획득한 URL 로 녹화 영상 다운로드를 요청합니다.
     *
     *
     * URI           : /record_file/download
     * 기능          : 녹화 영상 다운로드 호출 Method GET
     * Request Type  : -
     * Response Type : JSON
     * Method        : GET
     * 설명          : 요청한 녹화 영상 파일을 다운로드 할 수 있습니다.
     *
     *
     */
    public Map<String,Object>  callConferenceRecodeVideoFileDownloadStart(String downloadUrl) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        Map<String,Object> parameter = new HashMap<String, Object>();
        String fileName = "test.mp4";
        parameter.put("filename",fileName);

        Map<String,Object> resultMap = new HashMap<String, Object>();

        try{
            resultMap = this.remoteCallApiRecDownloadCallUrlGenerate(downloadUrl,parameter,REQUEST_METHOD_GET,RESULT_TYPE_NONE,CONTENT_TYPE_URL_ENCODING,AUTHORIATION_BEARER,PARAMETER_TYPE_STRING);
        }catch (Exception e){
            new CommonException("RemoteCall Meeting Conection ERROR");
        }

        return resultMap;
    }






    /**
     *
     * Remote Call API
     *
     *  회의록 조회하기
     * - RemoteMeeting 서버로 부터 받은 CID 를 통해 회의록이 작성된 회의에 대하여 회의록 내용을
     *   조회할 수 있습니다.
     *
     *
     * URI           : /openapi/conference/{conferenceId}/mtg_body
     * 기능          : 회의록 조회하기
     * Request Type  : -
     * Response Type : JSON
     * Method        : GET
     * 설명          : 작성된 (수동)회의록 데이터를 조회할 수 있습니다.
     *
     *
     */
    public Map<String,Object> callConferenceNotesFind(String getConferenceId) {
        Map<String,Object> resultMap = new HashMap<String,Object>();

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        // callConferenceRecodeVideoFileDownloadRequest method에서 response 받은 progressCheckUrl 로 호출 필요
        String apiUrl = "https://api.remotemeeting.com/openapi/conference/"+getConferenceId+"/mtg_body";


        Map<String,Object> parameter = new HashMap<String, Object>();
        parameter.put("debugMode", "1");

        StringBuffer responseData = this.remoteCallApiConnectionResultStringBuffer(apiUrl,parameter,REQUEST_METHOD_GET,RESULT_TYPE_JSON,CONTENT_TYPE_JSON,AUTHORIATION_BEARER,PARAMETER_TYPE_STRING);

        /**
         *
         *
         * {
         * "returnCode": "40100",
         * "returnData": {
         *      "conferenceId": "0aacad2ac70a493d91c6b7e4ce263e8b",
         *      "startTime": 1467937934376,
         *      "endTime": 1467937934576,
         *      "documents": [
         *      {
         *            "fileName" : "ssss.pdf"
         *      }
         * ],
         * "meetingNotes": {
         *      "conferenceId": "0aacad2ac70a493d91c6b7e4ce262e81",
         *      "participants": "eric,mike",
         *      "mtg_theme": "무궁화 꽃이 피었습니다.",
         *      "mtg_body": "송혜교",
         *      "lastUpdatedTime": 1469706653553,
         *      "lastUpdatedUser": "guest",
         *      "tasks": [
         *          {
         *          "taskTitle": "풀하우스",
         *          "isComplete": false
         *          },
         *          {
         *          "taskTitle": "태양의 후예",
         *          "isComplete": true
         *          }
         *          ]
         *       },
         *       "isUsedStt": true
         *       }
         * }
         *
         *
         */

        //response data는
        //String downloadUrl = responseMap.get("downloadUrl") // 녹화파일 다운로드 URL

        JsonParser parser = new JsonParser();
        JsonObject jsonObj = null;
        if(StringUtils.isNotEmpty(responseData.toString())){
            Object obj = parser.parse( responseData.toString() );
            jsonObj = (JsonObject) obj;
        }

        String returnCode = jsonObj.get("returnCode") == null? "" : jsonObj.get("returnCode").getAsString();
        JsonObject returnDataObject =  jsonObj.get("returnData") == null? new JsonObject() : jsonObj.get("returnData").getAsJsonObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        //response data는
        String conferenceId = returnDataObject.get("conferenceId") == null? "":returnDataObject.get("conferenceId").getAsString(); // 회의 ID
        String mtg_tit = returnDataObject.get("mtg_tit") == null? "":returnDataObject.get("mtg_tit").getAsString(); // 회의 제목


        //date
        long startTime = returnDataObject.get("startTime") == null? null : returnDataObject.get("startTime").getAsLong(); // 시작 예약 일시
        long endTime = returnDataObject.get("endTime")  == null? null : returnDataObject.get("endTime").getAsLong(); // 회의 종료 일시   yyyyMMdd hhmm
        Date startDate=new Date(startTime);
        Date endDate=new Date(endTime);
        String startDateFormat = sdf.format(startDate);
        String endDateFormat = sdf.format(endDate);



        // file List
        JsonArray documents = returnDataObject.get("documents") == null? new JsonArray() : returnDataObject.get("documents").getAsJsonArray();

        // 문서이름
        List<String> fileNameList = new ArrayList<String>();

        for(JsonElement docElement : documents){
            JsonObject jsonObject =  docElement.getAsJsonObject();
            String fileName = jsonObject.get("fileName") == null? "" : jsonObject.get("fileName").getAsString();
            fileNameList.add(fileName);
        }


        if(null != fileNameList && fileNameList.size() > 0) resultMap.put("fileNameList",fileNameList);


        //회의록 Object
        JsonObject meetingNotes = returnDataObject.get("meetingNotes") == null? new JsonObject() : returnDataObject.get("meetingNotes").getAsJsonObject();
        String meetingConferenceId = meetingNotes.get("conferenceId") == null? "" : meetingNotes.get("conferenceId").getAsString(); // 회의 ID
        String participants = meetingNotes.get("participants") == null? "" : meetingNotes.get("participants").getAsString(); // 회의 참여자 이름(=displayName)
        String mtg_theme = meetingNotes.get("mtg_theme") == null? "" : meetingNotes.get("mtg_theme").getAsString(); // 회의록에서 작성한 Title (주제)
        String mtg_body = meetingNotes.get("mtg_body") == null? "" : meetingNotes.get("mtg_body").getAsString(); // 회의록 본문 내용 (회의록 본문)
        String lastUpdatedUser = meetingNotes.get("lastUpdatedUser") == null? "" : meetingNotes.get("lastUpdatedUser").getAsString(); // 최근 수정자(=displayName)

       /* long lastUpdatedTime = meetingNotes.get("lastUpdatedTime") == null? 0 : meetingNotes.get("lastUpdatedTime").getAsLong(); // 최근 수정 시간
        if( 0 != lastUpdatedTime){
            Date lastUpdatedDate =new Date(lastUpdatedTime);
            String meetingastUpdatedFormat = sdf.format(lastUpdatedDate);
            resultMap.put("fnl_mod_dttm",meetingastUpdatedFormat);
        }*/

       //20220614 최종 수정 시간이 String으로 넘어와서, 우선 주석 처리
        resultMap.put("fnl_mod_dttm",new Date());



        //Task Array Object
        JsonArray tasks = meetingNotes.get("tasks") == null? new JsonArray() : meetingNotes.get("tasks").getAsJsonArray();

        //TaskMap List
        List<Map<String,Object>> taskMapList = new ArrayList<Map<String,Object>>();

        for(JsonElement taskElement : tasks){
            Map<String,Object> taskMap = new HashMap<String,Object>();
            JsonObject jsonObject =  taskElement.getAsJsonObject();
            String isComplete = jsonObject.get("isComplete") == null? "" : jsonObject.get("isComplete").getAsString(); //Task 항목 체크여부( true : 체크 , false : 미체크)
            String taskTitle = jsonObject.get("taskTitle") == null? "" : jsonObject.get("taskTitle").getAsString(); //Task 제목 ( 회의록에 작성된 Task )

            if(("true").equals(isComplete)){
                taskMap.put("task_yn","Y");
            }else{
                taskMap.put("task_yn","N");
            }

            taskMap.put("task_tit",taskTitle);
            taskMapList.add(taskMap);
        }

        if(null != taskMapList && taskMapList.size() > 0)  resultMap.put("taskMapList",taskMapList);

        String isUsedStt = meetingNotes.get("isUsedStt") == null? "" : meetingNotes.get("isUsedStt").getAsString(); //AI 회의록 사용 유무 ( true : 사용 , false : 미사용)



        resultMap.put("returnCode",returnCode);
        resultMap.put("conferenceId",conferenceId);
        resultMap.put("mtg_tit",mtg_tit);
        resultMap.put("startDateFormat",startDateFormat);
        resultMap.put("endDateFormat",endDateFormat);

        resultMap.put("mtg_atne_nm",participants);
        resultMap.put("mtg_body",mtg_body);
        resultMap.put("mtg_theme",mtg_theme);
        resultMap.put("fnl_modr_id",lastUpdatedUser);


        return resultMap;
    }

    /**
     *
     * Remote Call API
     *
     *  . A.I. 회의록 조회하기
     * - RemoteMeeting 서버로 부터 받은 CID 를 통해 회의록이 작성된 회의에 대하여 A.I. 회의록 내용을
     *   조회할 수 있습니다.
     *
     *
     * URI           : /openapi/conference/sttlog
     * 기능          : 회의록 조회하기
     * Request Type  : Parameter
     * Response Type : JSON
     * Method        : GET
     * 설명          : 작성된 A.I. 회의록 데이터를 조회할 수 있습니다
     *
     *
     */
    public Map<String,Object> callAIConferenceNotesFind(String conferenceId) {

        // Client ID : 9f4e699a-edf4-4f31-951a-f5ffb88046fb
        // Secret : MF8tdFveO1QiddPGizAr

        String apiUrl = "https://api.remotemeeting.com//penapi/conference/sttlog";


        Map<String,Object> parameter = new HashMap<String, Object>();
        parameter.put("conferenceId",conferenceId); //회의 ID

        //페이징 처리, 솔루션 단위에서는 모두 저장하는 형태로 진행
        //parameter.put("page",page); //page ( 기본값 = 1)
        //parameter.put("pageSize",pageSize); // pageSize (기본값 = 15 )

        StringBuffer responseData = this.remoteCallApiConnectionResultStringBuffer(apiUrl,parameter,REQUEST_METHOD_GET,RESULT_TYPE_JSON,CONTENT_TYPE_JSON,AUTHORIATION_BEARER,PARAMETER_TYPE_JSON);

        /**
         *
         * RESPONSE SAMPLE
         *
         * {
         * "returnCode": "40100",
         * "returnData": [
         * {
         * "seq": 5,
         * "sentence": "음성 기록을 시작합니다.",
         * "timestamp": 1592444025663,
         * "endpointId": "d7213147184147588bb25fd65fa6f7bd",
         * "displayName": "test"
         * },
         * {
         * "seq": 4,
         * "sentence": "안녕하세요",
         * "timestamp": 1592444021262,
         * "endpointId": "d7213147184147588bb25fd65fa6f7bd",
         * "displayName": "test"
         * },
         * {
         * "seq": 2,
         * "sentence": "기록 잘되고 있습니다.",
         * "timestamp": 1592444011956,
         * "endpointId": "42d5ee9f80c947b189985297bb94da2a",
         * "displayName": "opener@remotemeeting.com"
         * },
         * {
         * "seq": 1,
         * "sentence": "회의 첫번째 안건입니다.",
         * "timestamp": 1592444008648,
         * "endpointId": "42d5ee9f80c947b189985297bb94da2a",
         * "displayName": "opener@remotemeeting.com"
         * }
         * ]
         * }
         *
         *
         */

        JsonParser parser = new JsonParser();
        JsonObject jsonObj = null;
        if(StringUtils.isNotEmpty(responseData.toString())){
            Object obj = parser.parse( responseData.toString() );
            jsonObj = (JsonObject) obj;
        }

        String returnCode = jsonObj.get("returnCode") == null? "" : jsonObj.get("returnCode").getAsString();
        JsonArray jsonArray = jsonObj.get("returnData") == null? new JsonArray() : jsonObj.get("returnData").getAsJsonArray();


        //TaskMap List
        List<Map<String,Object>> chattingList = new ArrayList<Map<String,Object>>();

        for(JsonElement jsonElement : jsonArray){
            Map<String,Object> chattingObject = new HashMap<String,Object>();
            JsonObject jsonObject =  jsonElement.getAsJsonObject();

            //response data는
            String seq = jsonObject.get("seq") == null? "":jsonObject.get("seq").getAsString(); // 음성 기록 순서
            String sentence = jsonObject.get("sentence") == null? "":jsonObject.get("sentence").getAsString(); // 음성 기록 내용
            String timestamp = jsonObject.get("timestamp") == null? "":jsonObject.get("timestamp").getAsString(); // 음성 기록 시간
            String endpointId = jsonObject.get("endpointId") == null? "":jsonObject.get("endpointId").getAsString(); // 회의 참여자 고유 ID
            String displayName = jsonObject.get("displayName") == null? "":jsonObject.get("displayName").getAsString(); // 회의 참여자 이름

            chattingObject.put("seq",seq);
            chattingObject.put("sentence",sentence);

            chattingObject.put("endpointId",endpointId);
            chattingObject.put("displayName",displayName);

            Date timestampDate = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String timestampFormat = sdf.format(timestampDate);
            chattingObject.put("timestamp",timestampFormat);

            chattingList.add(chattingObject);
        }


        logger.info("returnCode = "+returnCode);

        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("returnCode",returnCode);
        if(null != chattingList && chattingList.size() > 0)  resultMap.put("chattingList",chattingList);


        return resultMap;
    }





}
