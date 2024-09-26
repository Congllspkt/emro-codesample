package smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model;

public class RemoteMeetProperties {

    // 화상회의 예약 프로세스 진행 여부 ( 리모트콜에서는 예약 프로세스를 태우는것이 불가능함 레퍼런스 용도로만 사용 필요 )
    public static final boolean REMOTE_MEETING_RESERVATION_PROCESS_YN = false;

    // 리모트콜 Oauth key
    public static final String CLIENTID = "a24c8c1b-a000-40f8-9f42-5cbff3ec9adf";
    public static final String SECRETKEY = "eJe3yGCJzEMVeaJk0rqR";


    // 리모트콜 예약 URL API
    public static final String REMOTE_MEETING_OPEN_API_RESERVATION_URL = "https://api.remotemeeting.com/openapi/reservation";


    /**
     * 회의 모드
     *
     * option: 영상회의 : video
     *         문서공유 : document
     *         화면공유 : screen
     *         세미나 : semina
     */
    public static final String CONFERENCE_OPTION = "video";

    /**
     * 해상도
     *
     *  option : 일반화질 : 360
     *           HD화질 : 720
     */
    public static final String VIDEO_QUALITY_OPTION = "360";

    // 현재 생성된 회의가 있으면 참석
    public static final boolean REMOTE_MEET_CREATE_Y = true;

    // 현재 생성된 회의가 없으면 개설
    public static final boolean REMOTE_MEET_CREATE_N = false;

}
