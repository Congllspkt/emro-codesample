package smartsuite.app.bp.rfx.rfxpreinsp.scheduler;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.scheduler.core.ScheduleService;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

@SuppressWarnings ({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxPreInspSchedulerService {
	
	/** The schedule service. */
	@Inject
	ScheduleService scheduleService;
	
	public void deleteRfxPreInsp(String rfxId){
		deleteStartRfxPreInspJob(rfxId);						// 1. 기존 등록된 시작 스케쥴러 삭제
		deleteEndRfxPreInspJob(rfxId);						// 2. 기존 등록된 마감 스케쥴러 삭제
	}
	
	public void noticeRfxPreInsp(Map rfxInfo, Map rfxPreInsp) {
		String rfxId = (String) rfxInfo.get("rfx_uuid");
		if(rfxPreInsp != null) {
			//기존 rfx 사전 심사 수정
			deleteRfxPreInsp(rfxId);
		}
		
		Map data = Maps.newHashMap();
		data.put("rfx_uuid", rfxId);
		
		Object[] args = new Object[]{data};
		
		// 시작일시
		Date startDt = new Date();	// 즉시시작일 경우 현재시간
		if("N".equals(rfxInfo.get("immed_rfx_presn_st_use_yn"))) {
			startDt = (Date) rfxInfo.get("rfx_presn_st_dttm");
		}
		
		// 마감 일시
		Date endDt = (Date) rfxInfo.get("rfx_presn_clsg_dttm");
		
		registStartRfxPreInspJob(rfxId, startDt, args);	// 3. 시작 스케쥴러 등록
		registEndRfxPreInspJob(rfxId, endDt, args);	// 4. 마감 스케쥴러 등록
		
	}
	
	private void registStartRfxPreInspJob(String rfxPreInspId, Date rfxPreInspStartDt, Object[] args) {
		try {
			scheduleService.registSchedule(Class.forName(RfxPreInspJobConst.RFX_PRE_INSP_SERVICE_CLASS_NAME),
					RfxPreInspJobConst.RFX_PRE_INSP_START_METHOD_NAME,
					args,
					rfxPreInspStartDt,
					RfxPreInspJobConst.RFX_PRE_INSP_JOB_GROUP,
					rfxPreInspId,
					RfxPreInspJobConst.RFX_PRE_INSP_START_JOB_NAME,
					null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	private void registEndRfxPreInspJob(String rfxPreInspId, Date rfxPreInspEndDt, Object[] args) {
		try {
			scheduleService.registSchedule(Class.forName(RfxPreInspJobConst.RFX_PRE_INSP_SERVICE_CLASS_NAME),
					RfxPreInspJobConst.RFX_PRE_INSP_END_METHOD_NAME,
					args,
					rfxPreInspEndDt,
					RfxPreInspJobConst.RFX_PRE_INSP_JOB_GROUP,
					rfxPreInspId,
					RfxPreInspJobConst.RFX_PRE_INSP_END_JOB_NAME,
					null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	
	private void deleteStartRfxPreInspJob(String rfxPreInspId) {
		if(rfxPreInspId != null) {
			try {
				scheduleService.removeScheduleTrigger(Class.forName(RfxPreInspJobConst.RFX_PRE_INSP_SERVICE_CLASS_NAME),
						RfxPreInspJobConst.RFX_PRE_INSP_START_METHOD_NAME,
						RfxPreInspJobConst.RFX_PRE_INSP_JOB_GROUP,
						rfxPreInspId);
			} catch (ClassNotFoundException e) {
				throw new CommonException(ErrorCode.FAIL, e);
			}
		}
	}
	
	private void deleteEndRfxPreInspJob(String rfxPreInspId) {
		if(rfxPreInspId != null) {
			try {
				scheduleService.removeScheduleTrigger(Class.forName(RfxPreInspJobConst.RFX_PRE_INSP_SERVICE_CLASS_NAME),
						RfxPreInspJobConst.RFX_PRE_INSP_END_METHOD_NAME,
						RfxPreInspJobConst.RFX_PRE_INSP_JOB_GROUP,
						rfxPreInspId);
			} catch (ClassNotFoundException e) {
				throw new CommonException(ErrorCode.FAIL, e);
			}
		}
	}
}
