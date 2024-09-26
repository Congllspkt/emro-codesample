package smartsuite.app.bp.workplace.monitoring.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.workplace.monitoring.repository.WorkplaceMonitoringRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.workplace.shared.service.TaskSharedService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class WorkplaceMonitoringService {

	@Inject
	WorkplaceMonitoringRepository wkplcMonitoringRepository;

	@Inject
	TaskSharedService taskSharedService;

	public List findListErrorTask( Map<String, Object> param) {
		return wkplcMonitoringRepository.findListErrorTask(param);
	}
	public Map findErrorTaskDetailAuthty( Map<String, Object> param) {
		Map<String, Object> findParam = Maps.newHashMap(param);
		Map<String, Object> errorTaskDetail = Maps.newHashMap();
		Map<String, Object> collData = null;
		Map<String, Object> currData = null;

		// 수집정보 조회
		findParam.put("err_task_dtl_uuid", param.getOrDefault("coll_err_task_dtl_uuid", ""));
		collData = wkplcMonitoringRepository.findErrorTaskDetail(findParam);
		if(collData != null) {
			List<Map<String, Object>> errTaskAuthtyList = wkplcMonitoringRepository.findListErrorTaskAuthty(findParam);
			collData.put("authtyList", errTaskAuthtyList);
		}

		// 현재정보 조회
		findParam.put("err_task_dtl_uuid", param.getOrDefault("curr_err_task_dtl_uuid", ""));
		currData = wkplcMonitoringRepository.findErrorTaskDetail(findParam);
		if(currData != null) {
			List<Map<String, Object>> errTaskAuthtyList = wkplcMonitoringRepository.findListErrorTaskAuthty(findParam);
			currData.put("authtyList", errTaskAuthtyList);
		}
		errorTaskDetail.put("collData", collData);
		errorTaskDetail.put("currData", currData);
		return errorTaskDetail;
	}

	public ResultMap saveErrorTaskMonitoringInfo(Map<String, Object> param) {
		// 기존 수집 정보 삭제 처리.
		this.deleteErrorTaskInfo(param);

		List<Map<String, Object>> datSrcList = wkplcMonitoringRepository.findListDatSrcUsed(param);

		Map<String, Object> findErrorTaskParam = Maps.newHashMap();
		findErrorTaskParam.put("datSrcList", datSrcList);
		wkplcMonitoringRepository.insertErrorTaskInfo(findErrorTaskParam);
		wkplcMonitoringRepository.insertErrorTaskDetailInfo(findErrorTaskParam);
		wkplcMonitoringRepository.insertErrorTaskAuthtyInfoByTask(findErrorTaskParam);

		if(datSrcList!= null && !datSrcList.isEmpty()) {

			wkplcMonitoringRepository.insertErrorTaskAuthtyInfoByDatSrc(findErrorTaskParam);
		}
		return ResultMap.SUCCESS();
	}

	public void deleteErrorTaskInfo(Map<String, Object> param) {
		wkplcMonitoringRepository.deleteErrorTaskAuthty(param);
		wkplcMonitoringRepository.deleteErrorTaskDetail(param);
		wkplcMonitoringRepository.deleteErrorTask(param);
	}

	public ResultMap createTaskByErrorTask(Map<String, Object> param) {
		List<Map<String, Object>> taskList = (List<Map<String, Object>>)param.getOrDefault("taskList", Lists.newArrayList());

		if(taskList == null || taskList.isEmpty()) {
			return ResultMap.FAIL();
		}

		for(Map<String, Object> task : taskList) {
			// 에러 유형
			String errTypCcd = (String)task.getOrDefault("err_typ_ccd", "");
			// 현재 데이터의 업무 상태 UUID
			String taskStsUuid = (String)task.getOrDefault("curr_task_sts_uuid", "");
			// 업무 UUID
			String taskUuid = (String)task.getOrDefault("task_uuid", "");

			// 1. 기존 task 데이터 삭제
			this.deleteTaskByTaskUuid(task);

			// 2. 현재 데이터로 task 생성
			if(!taskStsUuid.equals("")) {
				// 2-1. 현제 업무 상태 UUID 로 업무 상태 설정정보 조회
				task.put("task_sts_uuid", taskStsUuid);
				Map<String ,Object> config = wkplcMonitoringRepository.findTaskConfigByTaskStsUuid(task);

				if(config != null) {
					config.put("task_uuid", taskUuid);
					wkplcMonitoringRepository.insertTaskByDatSrc(config);
					wkplcMonitoringRepository.insertTaskAuthtyByDatSrc(config);
				}
			}
		}
		return ResultMap.SUCCESS();
	}


	// task uuid 로 기존에 생성된 task 삭제
	public void deleteTaskByTaskUuid(Map<String, Object> param) {
		taskSharedService.deleteTask(param);
	}

}
