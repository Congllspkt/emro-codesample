package smartsuite.app.bp.rfx.rfi.scheduler;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfi.repository.RfiRepository;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.scheduler.core.ScheduleService;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfiSchedulerService {
	
	@Inject
	RfiRepository rfiRepository;
	
	/** The schedule service. */
	@Inject
	ScheduleService scheduleService;
	
	/**
	 * RFI 요청 시
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void requestRfi(Map<String, Object> param) {
		Map rfiInfo = rfiRepository.findRfi(param);
		if(rfiInfo == null) {
			return;
		}
		
		String rfiId = (String) rfiInfo.get("rfi_uuid");
		
		// 기존 RFI 마감 스케쥴러 삭제
		deleteCloseRfiJob(rfiId);
		
		Map data = Maps.newHashMap();
		data.put("rfi_uuid", rfiId);
		Object[] args = new Object[]{data};
		
		if(rfiInfo.get("rfi_clsg_dttm") != null) {
			Date rfiCloseDate = (Date) rfiInfo.get("rfi_clsg_dttm");
			
			// RFI 마감 스케쥴러 등록
			registCloseRfiJob(rfiId, rfiCloseDate, args);
		}
	}
	
	/**
	 * RFI 종료 처리 시
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void completeRfi(Map<String, Object> param) {
		Map rfiInfo = rfiRepository.findRfi(param);
		if(rfiInfo == null) {
			return;
		}
		
		// RFI 상태가 진행중 인 경우(아직 마감되지 않음)
		if("PRGSG".equals(rfiInfo.get("rfi_sts_ccd"))) {
			// RFI 마감 스케쥴러 삭제
			deleteCloseRfiJob((String) rfiInfo.get("rfi_uuid"));
		}
	}
	
	private void registCloseRfiJob(String rfiId, Date scheduleDate, Object[] args) {
		try {
			scheduleService.registSchedule(
					Class.forName(RfiJobConst.RFI_SERVICE_CLASS_NAME),
					RfiJobConst.RFI_CLOSE_METHOD_NAME,
					args,
					scheduleDate,
					RfiJobConst.RFI_JOB_GROUP,
					rfiId,
					RfiJobConst.RFI_CLOSE_JOB_NAME,
					null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	private void deleteCloseRfiJob(String rfiId) {
		try {
			scheduleService.removeScheduleTrigger(
					Class.forName(RfiJobConst.RFI_SERVICE_CLASS_NAME),
					RfiJobConst.RFI_CLOSE_METHOD_NAME,
					RfiJobConst.RFI_JOB_GROUP,
					rfiId);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
}
