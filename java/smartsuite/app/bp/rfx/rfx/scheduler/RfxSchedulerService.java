 package smartsuite.app.bp.rfx.rfx.scheduler;

 import com.google.common.collect.Maps;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 import smartsuite.app.bp.rfx.rfx.repository.RfxRepository;
 import smartsuite.app.common.mail.MailService;
 import smartsuite.exception.CommonException;
 import smartsuite.exception.ErrorCode;
 import smartsuite.scheduler.core.ScheduleService;

 import javax.inject.Inject;
 import java.util.Date;
 import java.util.Map;

/**
 * RfxSchduler 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @see 
 * @FileName RfxSchdulerService.java
 * @package smartsuite.app.bp.rfx.rfx.schduler
 * @Since 2016. 5. 19
 * @변경이력 : [2016. 5. 19] Yeon-u Kim 최초작성
 */
@SuppressWarnings ({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxSchedulerService{
	
	@Inject
	RfxRepository rfxRepository;
	
	/** The schedule service. */
	@Inject
	ScheduleService scheduleService;

	@Inject
	MailService mailService;
	/**
	 * 견적요청 시 처리
	 * 
	 * @param rfxInfo
	 */
	public void noticeRfx(Map rfxInfo) {
		String rfxId = (String) rfxInfo.get("rfx_uuid");
		
		deleteStartRfxJob(rfxId);						// 1. 기존 등록된 시작 스케쥴러 삭제
		deleteCloseRfxJob(rfxId);						// 2. 기존 등록된 마감 스케쥴러 삭제
		
		Map data = Maps.newHashMap();
		data.put("rfx_uuid", rfxInfo.get("rfx_uuid"));
		data.put("rfx_typ_ccd", rfxInfo.get("rfx_typ_ccd"));
		
		Object[] args = new Object[]{data};
		
		// 견적 시작 스케줄러
		if("N".equals(rfxInfo.get("immed_st_use_yn"))) {
			Date startDt = (Date) rfxInfo.get("rfx_st_dttm");
			registStartRfxJob(rfxId, startDt, args);	// 3. 시작 스케쥴러 등록
		}
		
		// 견적 마감 스케줄러
		Date closeDt = (Date) rfxInfo.get("rfx_clsg_dttm");
		registCloseRfxJob(rfxId, closeDt, args);	// 4. 마감 스케쥴러 등록

		// RFX 발신
		mailService.sendAsync("RFX_NOTICE_SEND", rfxId);
	}
	
	/**
	 * 마감 일시 변경 시 처리
	 * 
	 * @param param
	 */
	public void changeRfxCloseDt(Map param) {
		String rfxId = (String)param.get("rfx_uuid");
		
		deleteCloseRfxJob(rfxId);						// 1. 기존 등록된 마감 스케쥴러 삭제
		
		Map rfxInfo = rfxRepository.findRfx(param);
		if(rfxInfo != null) {
			Map data = Maps.newHashMap();
			data.put("rfx_typ_ccd", rfxInfo.get("rfx_typ_ccd"));
			data.put("rfx_uuid" , rfxInfo.get("rfx_uuid"));
			
			//마감 일시
			Date closeDt = (Date) rfxInfo.get("rfx_clsg_dttm");
			
			registCloseRfxJob(rfxId, closeDt, new Object[]{data});	//2. 새로운 마감 스케쥴러 등록
		}
	}
	
	/**
	 * 강제마감 시 처리
	 * 
	 * @param param
	 */
	public void bypassCloseRfx(Map param) {
		String rfxId = (String)param.get("rfx_uuid");
		
		deleteCloseRfxJob(rfxId);						// 기존 등록된 마감 스케쥴러 삭제
	}
	
	/**
	 * 취소 시 처리
	 * 
	 * @param param
	 */
	public void cancelRfx(Map param) {
		String rfxId = (String)param.get("rfx_uuid");
		
		deleteStartRfxJob(rfxId);						// 기존 등록된 시작 스케쥴러 삭제
		deleteCloseRfxJob(rfxId);						// 기존 등록된 마감 스케쥴러 삭제
	}
	
	/**
	 * 평가 마감 시 처리
	 * 
	 * @param param
	 */
	public void endRfxEval(Map param) {
		String evalId = (String) param.get("eval_id");
		
		deleteEndRfxEvalJob(evalId);						// 기존 등록된 스케쥴러 삭제
		
		Date scheduleDate = new Date();
		scheduleDate = new Date(scheduleDate.getTime() + 2000);
		
		registEndRfxEvalJob(evalId, scheduleDate, new Object[]{param});	// 스케쥴러 등록
	}

	/**
	 * 견적 시작 일시 스케줄러 등록.
	 * 
	 * @param rfxId
	 * @param rfxStartDt
	 * @param args
	 * @throws Exception
	 */
	private void registStartRfxJob(String rfxId, Date rfxStartDt, Object[] args) {
		try {
			scheduleService.registSchedule(Class.forName(RfxJobConst.RFX_SERVICE_CLASS_NAME),
					RfxJobConst.RFX_START_METHOD_NAME,
					args,
					rfxStartDt,
					RfxJobConst.RFX_JOB_GROUP,
					rfxId,
					RfxJobConst.RFX_START_JOB_NAME,
					null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	/**
	 * 마감 스케줄러 등록
	 * 
	 * @param rfxId
	 * @param rfxCloseDt
	 * @param args
	 * @throws Exception
	 */
	private void registCloseRfxJob(String rfxId, Date rfxCloseDt, Object[] args) {
		try {
			scheduleService.registSchedule(Class.forName(RfxJobConst.RFX_SERVICE_CLASS_NAME),
					RfxJobConst.RFX_CLOSE_METHOD_NAME,
					args,
					rfxCloseDt,
					RfxJobConst.RFX_JOB_GROUP,
					rfxId,
					RfxJobConst.RFX_CLOSE_JOB_NAME,
					null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	/**
	 * 평가마감 스케줄러 등록
	 * 
	 * @param evalId
	 * @param evalCloseDt
	 * @param args
	 * @throws Exception
	 */
	private void registEndRfxEvalJob(String rfxId, Date evalCloseDt, Object[] args) {
		try {
			scheduleService.registSchedule(Class.forName(RfxJobConst.RFX_SERVICE_CLASS_NAME),
					RfxJobConst.RFX_EVAL_END_METHOD_NAME,
					args,
					evalCloseDt,
					RfxJobConst.RFX_JOB_GROUP,
					rfxId,
					RfxJobConst.RFX_EVAL_END_JOB_NAME,
					null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	/**
	 * 견적일시 시작 스케줄러 job 삭제한다.
	 *
	 * @author : Yeon-u Kim
	 * @param rfxId the rfx id
	 * @throws Exception the exception
	 * @Date : 2016. 8. 16
	 * @Method Name : deleteStartRfxJob
	 */
	private void deleteStartRfxJob(String rfxId) {
		if(rfxId != null) {
			try {
				scheduleService.removeScheduleTrigger(Class.forName(RfxJobConst.RFX_SERVICE_CLASS_NAME),
						RfxJobConst.RFX_START_METHOD_NAME,
						RfxJobConst.RFX_JOB_GROUP,
						rfxId);
			} catch (ClassNotFoundException e) {
				throw new CommonException(ErrorCode.FAIL, e);
			}
		}
	}
	
	/**
	 * 마감 스케줄러를 삭제한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @throws Exception the exception
	 * @Date : 2016. 5. 20
	 * @Method Name : deleteCloseQuatationJob
	 */
	private void deleteCloseRfxJob(String rfxId) {
		if(rfxId != null) {
			try {
				scheduleService.removeScheduleTrigger(Class.forName(RfxJobConst.RFX_SERVICE_CLASS_NAME),
						RfxJobConst.RFX_CLOSE_METHOD_NAME,
						RfxJobConst.RFX_JOB_GROUP,
						rfxId);
			} catch (ClassNotFoundException e) {
				throw new CommonException(ErrorCode.FAIL, e);
			}
		}
	}
	
	/**
	 * 평가마감 스케줄러를 삭제한다.
	 *
	 * @param param the param
	 * @throws Exception the exception
	 * @Date : 2016. 5. 20
	 * @Method Name : deleteEndRfxEvalJob
	 */
	private void deleteEndRfxEvalJob(String evalId) {
		if(evalId != null) {
			try {
				scheduleService.removeScheduleTrigger(Class.forName(RfxJobConst.RFX_SERVICE_CLASS_NAME),
						RfxJobConst.RFX_EVAL_END_METHOD_NAME,
						RfxJobConst.RFX_JOB_GROUP,
						evalId);
			} catch(ClassNotFoundException e){
				throw new CommonException(ErrorCode.FAIL, e);
			}
		}
	}
}
