package smartsuite.app.common.workplace.shared.service;

import java.util.*;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import smartsuite.app.common.workplace.shared.repository.TaskSharedRepository;

/**
 * Task 관련 처리하는 서비스 Class입니다.
 *
 * @author 
 * @see 
 * @FileName TaskSharedService.java
 * @package smartsuite.app.common.shared
 * @Since 
 * @변경이력 : 
 */
@Service
@Transactional
public class TaskSharedService {

	/** The sql session. */
	@Inject
	private TaskSharedRepository taskSharedRepository;

	/**
	 * open task 생성
	 * 
	 * @param openTask
	 * @param openAuthtyList
	 */
	private void insertOpenTask(Map<String, Object> openTask, List<Map<String, Object>> openAuthtyList) {
		taskSharedRepository.insertOpenTask(openTask);        // task 생성

		// task Authty 생성
		for(Map<String, Object> openAuthty : openAuthtyList) {
			String taskAuthtyUuid = UUID.randomUUID().toString();
			openAuthty.put("task_authty_uuid", taskAuthtyUuid);
			taskSharedRepository.insertOpenAuth(openAuthty);
		}
	}
	
	/**
	 * open task 수정
	 * 
	 * @param openTask
	 * @param openAuthtyList
	 */
	private void updateOpenTask(Map<String, Object> openTask, List<Map<String, Object>> openAuthtyList) {
		taskSharedRepository.updateOpenTask(openTask);        // task 수정

		this.deleteOpenTaskAuthtyInfo(openTask);  // 기존 task Authty 삭제

		for(Map<String, Object> openAuthty : openAuthtyList) {               // task Authty 생성
			String taskAuthtyUuid = UUID.randomUUID().toString();
			openAuthty.put("task_authty_uuid", taskAuthtyUuid);
			taskSharedRepository.insertOpenAuth(openAuthty);
		}
	}
	
	/**
	 * open task 삭제
	 * 
	 * @param param {task_uuid or task_uuids}
	 */
	private void deleteOpenTask(Map<String, Object> param) {
		this.deleteOpenTaskAuthtyInfo(param);
		taskSharedRepository.deleteOpenTaskClsgDtChange(param);
		taskSharedRepository.deleteOpenTask(param);
	}

	private void deleteOpenTaskAuthtyInfo(Map<String, Object> param) {
		taskSharedRepository.deleteOpenTaskMemo(param);
		taskSharedRepository.deleteOpenTaskAsgtXcept(param);
		taskSharedRepository.deleteOpenTaskReAssign(param);
		taskSharedRepository.deleteOpenTaskAuthty(param);
	}


	/**
	 * Task 발행
	 *
	 * @param param {task_uuid, menu_cd, grp_ccd, task_sts_ccd}
	 */
	public void createTask(Map<String, Object> param) {
		// 이전 task id 조회
		String preTaskUuid = taskSharedRepository.findTaskUuidByTaskUuid(param);	// task_uuid로 조회
		Map<String, Object> preTask = null;
		List<String> preAuthtyCd = null;
		if(!Strings.isNullOrEmpty(preTaskUuid)) {
			// 이전 task 삭제되기 전에 Auth Code 조회
			preTask = new HashMap<String, Object>();
			preTask.put("task_uuid", preTaskUuid);
		}
		
		boolean isNew = false;
		Map<String, Object> openTask = taskSharedRepository.findOpenTask(param);	// task_uuid, dtl_cd, usr_cls로 조회
		if(openTask == null) {
			isNew = true;	// 동일 상태의 task 존재하지 않으면 신규
			
			// 이전 task 존재 시 (and 동일 상태 아님)
			if(preTask != null) {
				// 이전 task close 처리
				// TODO. insertCloseTask(preTask);
				deleteOpenTask(preTask);
			}
		}

		// Task Config 조회 >> return 데이터 소스, 지연 통보 기준 일수, 임박 통보 기준 일수, 신규 통보 기준 일수
		Map<String, Object> taskConfig = taskSharedRepository.findTaskConfig(param);	// menu_cd, grp_ccd, task_sts_ccd로 조회
		if (taskConfig != null) {
			try {
				String taskUuid = (String)param.get("task_uuid");
				taskConfig.put("task_uuid", taskUuid);

				// DataSrc 조회 동적 sql 실행 >> return task_tit, task_sts_ccd, clsg_dt, 지연/임박/신규 표시 시작 일자  계산값
				Map<String, Object> taskDataSrc = taskSharedRepository.findTaskDataSrc(taskConfig);

				// taskDataSrc 조회 결과 없는 경우 -> 해당 업무는 task 생성 대상이 아님.
				if(taskDataSrc == null) {
					// 이전 task 존재 시 (이전 task의 상태는 동일한데 taskDataSrc에서 조회가 안되는 경우 삭제 처리)
					if(preTask != null) {
						// 이전 task close 처리
						deleteOpenTask(preTask);
					}
					return;
				}

				// Authty Code DataSrc 조회 동적 sql 실행 >> return task_authty_div_ccd, task_authty_cd
				List<Map<String, Object>> taskDataSrcAuthtys = taskSharedRepository.findTaskAuthtyDataSrc(taskConfig);

				// 신규
				if(isNew) {
					// open task 생성
					Map<String, Object> newTaskInfo = new HashMap<String, Object>();
					newTaskInfo.put("task_uuid"        , taskUuid);
					newTaskInfo.put("task_sts_uuid"    , taskConfig.get("task_sts_uuid"));
					newTaskInfo.put("task_tit"         , taskDataSrc.get("task_tit"));
					newTaskInfo.put("clsg_dt"           , taskDataSrc.get("task_clsg_dt"));
					newTaskInfo.put("delay_dspy_st_dt" , taskDataSrc.get("task_delay_dspy_st_dt"));
					newTaskInfo.put("immnt_dspy_st_dt" , taskDataSrc.get("task_immnt_dspy_st_dt"));
					newTaskInfo.put("new_dspy_ed_dt"   , taskDataSrc.get("task_new_dspy_ed_dt"));

					List<Map<String, Object>> newAuthtyList = new ArrayList<Map<String, Object>>();
					for(Map<String, Object> taskDataSrcAuthty : taskDataSrcAuthtys){
						Map<String, Object> newAuthtyInfo = new HashMap<String, Object>();
						newAuthtyInfo.put("task_uuid"      , taskUuid);
						newAuthtyInfo.put("authty_div_ccd" , taskDataSrcAuthty.get("task_authty_div_ccd"));
						newAuthtyInfo.put("authty_cd"      , taskDataSrcAuthty.get("task_authty_cd"));

						newAuthtyList.add(newAuthtyInfo);
					}

					// Open task 생성
					insertOpenTask(newTaskInfo, newAuthtyList);
				} // 수정
				else {
					Map<String, Object> taskInfo = new HashMap<String, Object>();
					taskInfo.put("task_uuid"        , preTaskUuid);
					taskInfo.put("task_sts_uuid"    , taskConfig.get("task_sts_uuid"));
					taskInfo.put("task_tit"         , taskDataSrc.get("task_tit"));
					taskInfo.put("clsg_dt"           , taskDataSrc.get("task_clsg_dt"));
					taskInfo.put("delay_dspy_st_dt" , taskDataSrc.get("task_delay_dspy_st_dt"));
					taskInfo.put("immnt_dspy_st_dt" , taskDataSrc.get("task_immnt_dspy_st_dt"));
					taskInfo.put("new_dspy_ed_dt"   , taskDataSrc.get("task_new_dspy_ed_dt"));

					List<Map<String, Object>> authtyList = new ArrayList<Map<String, Object>>();
					for(Map<String, Object> taskDataSrcAuthty : taskDataSrcAuthtys){
						Map<String, Object> authtyInfo = new HashMap<String, Object>();
						authtyInfo.put("task_uuid"      , preTaskUuid);
						authtyInfo.put("authty_div_ccd" , taskDataSrcAuthty.get("task_authty_div_ccd"));
						authtyInfo.put("authty_cd"      , taskDataSrcAuthty.get("task_authty_cd"));

						authtyList.add(authtyInfo);
					}

					// Open task 수정
					updateOpenTask(taskInfo, authtyList);
				}
			} catch(Exception e) {
				// TODO 추후 예외 처리 세분화 필요.
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Task 삭제
	 *
	 * @param param {task_uuid or task_uuids}
	 */
	public void deleteTask(Map<String, Object> param) {
		try {
			deleteOpenTask(param);
			// TODO. deleteCloseTask(param);
		} catch(Exception e) {
			// TODO 추후 예외 처리 세분화 필요.
			e.printStackTrace();
		}
	}

	/**
	 * Task 마이그레이션
	 *
	 * @param param
	 */
	public void migrationAllTasks(Map<String, Object> param) {
		List<Map<String, Object>> taskStatusList = taskSharedRepository.findListTaskStatusUsed(param);
		for(Map<String, Object> taskStatus : taskStatusList) {
			List<Map<String, Object>> taskList = taskSharedRepository.findListDataSourceTaskData(taskStatus);
			for(Map<String, Object> taskData : taskList) {
				// task 생성
				createTask(taskData);
			}
		}
	}

}
