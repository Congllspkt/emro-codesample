package smartsuite.app.bp.rfx.bidnotice.scheduler;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.scheduler.core.ScheduleService;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * BidNotiScheduler 관련 처리하는 서비스 Class입니다.
 *
 * @see 
 * @FileName BidNotiSchedulerService.java
 * @package smartsuite.app.bp.rfx.bidnotice.scheduler
 * @Since 2021. 5. 14
 */
@SuppressWarnings ({ "rawtypes"})
@Service
@Transactional
public class BidNoticeSchedulerService {

	/** The sql session. */
	@Inject
	SqlSession sqlSession;

	/** The schedule service. */
	@Inject
	ScheduleService scheduleService;
	
	/**
	 * Removes the schduler.
	 *
	 * @param param the param
	 */
	public void removeSchduler(Map<String, Object> param) {
		String notiId = (String)param.get("rfx_prentc_uuid");
		this.deleteStartRfxJob(notiId);						// 1. 기존 등록된 시작 스케쥴러 삭제
		this.deleteCloseRfxJob(notiId);						// 2. 기존 등록된 마감 스케쥴러 삭제
	}

	/**
	 * close rfx job 삭제한다.
	 *
	 * @param notiId the noti id
	 * @Date : 2021. 5. 14
	 * @Method Name : deleteCloseRfxJob
	 */
	private void deleteCloseRfxJob(String notiId) {
		if(notiId != null) {
			try {
				scheduleService.removeScheduleTrigger(Class.forName(BidNoticeJobConst.BN_SERVICE_CLASS_NAME),
						BidNoticeJobConst.BN_CLOSE_METHOD_NAME,
						BidNoticeJobConst.BN_JOB_GROUP,
						notiId);
			} catch (ClassNotFoundException e) {
				throw new CommonException(ErrorCode.FAIL, e);
			}
		}
	}

	/**
	 * start rfx job 삭제한다.
	 *
	 * @param notiId the noti id
	 * @Date : 2021. 5. 14
	 * @Method Name : deleteStartRfxJob
	 */
	private void deleteStartRfxJob(String notiId) {
		if(notiId != null) {
			try {
				scheduleService.removeScheduleTrigger(Class.forName(BidNoticeJobConst.BN_SERVICE_CLASS_NAME),
						BidNoticeJobConst.BN_START_METHOD_NAME,
						BidNoticeJobConst.BN_JOB_GROUP,
						notiId);
			} catch (ClassNotFoundException e) {
				throw new CommonException(ErrorCode.FAIL, e);
			}
		}
	}

	/**
	 * Regist start scheduler.
	 *
	 * @param bidNotiInfo the bid noti info
	 */
	public void registStartScheduler(Map<String, Object> bidNotiInfo) {
		String notiId = (String)bidNotiInfo.get("rfx_prentc_uuid");
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("rfx_prentc_uuid", notiId);
		Object[] args = new Object[]{data};
		Date startDt = (Date)bidNotiInfo.get("rfx_prentc_st_dttm");
		registStartBidNotiJob(notiId, startDt, args);	// 시작 스케쥴러 등록
	}

	/**
	 * Regist start bid noti job.
	 *
	 * @param notiId the noti id
	 * @param startDt the start dt
	 * @param args the args
	 */
	private void registStartBidNotiJob(String notiId, Date startDt, Object[] args) {
		try {
			scheduleService.registSchedule(Class.forName(BidNoticeJobConst.BN_SERVICE_CLASS_NAME),
					BidNoticeJobConst.BN_START_METHOD_NAME,
					args,
					startDt,
					BidNoticeJobConst.BN_JOB_GROUP,
					notiId,
					BidNoticeJobConst.BN_START_JOB_NAME,
					null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}

	/**
	 * Regist close scheduler.
	 *
	 * @param bidNotiInfo the bid noti info
	 */
	public void registCloseScheduler(Map<String, Object> bidNotiInfo) {
		String notiId = (String)bidNotiInfo.get("rfx_prentc_uuid");
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("rfx_prentc_uuid", notiId);
		Object[] args = new Object[]{data};
		Date closeDt = (Date)bidNotiInfo.get("rfx_prentc_clsg_dttm");
		registCloseBidNotiJob(notiId, closeDt, args);	// 종료 스케쥴러 등록
	}

	/**
	 * Regist close bid noti job.
	 *
	 * @param notiId the noti id
	 * @param closeDt the close dt
	 * @param args the args
	 */
	private void registCloseBidNotiJob(String notiId, Date closeDt, Object[] args) {
		try {
			scheduleService.registSchedule(Class.forName(BidNoticeJobConst.BN_SERVICE_CLASS_NAME),
					BidNoticeJobConst.BN_CLOSE_METHOD_NAME,
					args,
					closeDt,
					BidNoticeJobConst.BN_JOB_GROUP,
					notiId,
					BidNoticeJobConst.BN_CLOSE_JOB_NAME,
					null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
}
