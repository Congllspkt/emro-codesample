package smartsuite.app.sp.workplace.dashboard.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.workplace.dashboard.repository.SpWorkplaceRepository;

@SuppressWarnings({"unchecked"})
@Service
@Transactional
public class SpWorkplaceService {
	
	@Inject
	SpWorkplaceRepository spWorkplaceRepository;

	/**
	 * Workplace > cc-page-title-bar에 위치한 신규/메모/통보/제외 count
	 * 
	 * @param param {}
	 * @return
	 */
	public Map<String,Object> findSpTaskCount(Map<String, Object> param) {
		return spWorkplaceRepository.findSpTaskCount(param);
	}

	/**
	 * Work List > 지연/임박/일반/신규 count
	 *
	 * @param param {task_uuids, task_tit, open_days, only_memo, only_noti, only_exception}
	 * @return
	 */
	public Map<String, Object> findListSpTaskCount(Map<String, Object> param) {
		return spWorkplaceRepository.findListSpTaskCount(param);
	}

	/**
	 * Work List> 목록 조회
	 * 
	 * @param param {task_uuids, task_tit, open_days, only_memo, only_noti, only_exception, dely_srch_yn, warn_srch_yn, norm_srch_yn, new_srch_yn}
	 * @return
	 */
	public List<Map<String,Object>> findListSpTask(Map<String, Object> param) {
		return spWorkplaceRepository.findListSpTask(param);
	}

	/**
	 * Work List > main task uuid로 task status 목록 조회
	 *
	 * @param param {main_task_uuid}
	 * @return
	 */
	public List<Map<String, Object>> findListSpTaskStatusByMainTaskUuid(Map<String, Object> param) {
		return spWorkplaceRepository.findListSpTaskStatusByMainTaskUuid(param);
	}

	/**
	 * Task List > 제외
	 * 
	 * @param param {taskList, xcept_typ_ccd, xcept_dtl_rsn}
	 * @return
	 */
	public ResultMap updateListAssignException(Map<String, Object> param) {
		List<Map<String, Object>> taskList = (List<Map<String, Object>>)param.get("taskList");
		
		// 기존 TASK ASGT XCEPT 삭제 (task_uuid, reg_id)
		deleteSpBatchAssignException(taskList);
		
		for(Map<String, Object> task: taskList) {
			task.put("task_asgt_xcept_uuid", UUID.randomUUID().toString());
			task.put("xcept_typ_ccd", param.get("xcept_typ_ccd"));
			task.put("xcept_dtl_rsn", param.get("xcept_dtl_rsn"));
		}
		
		// 신규 TASK ASGT XCEPT 추가
		insertSpBatchAssignException(taskList);

		return ResultMap.SUCCESS();
	}
	private void deleteSpBatchAssignException(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task: taskList) {
			spWorkplaceRepository.deleteSpException(task);	// TASK ASGT XCEPT : task_uuid, task_authty_uuid, reg_id=username
		}
	}
	private void insertSpBatchAssignException(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task: taskList) {
			spWorkplaceRepository.insertSpException(task);	// TASK ASGT XCEPT
		}
	}

	/**
	 * Task List > 제외복원
	 * 
	 * @param param {taskList}
	 * @return
	 */
	public ResultMap deleteListSpAssignException(Map<String, Object> param) {
		// TASK ASGT XCEPT 삭제 (task_uuid, reg_id)
		deleteSpBatchAssignException((List<Map<String, Object>>)param.get("taskList"));

		return ResultMap.SUCCESS();
	}

	/**
	 * Task List > 메모 조회
	 * 
	 * @param param {task_uuid, task_authty_uuid}}
	 * @return
	 */
	public List<Map<String, Object>> findListSpMemo(Map<String, Object> param) {
		return spWorkplaceRepository.findListSpMemo(param);
	}
	
	/**
	 * Task List > 메모 등록
	 * 
	 * @param param {taskList, memo, alrm_dt, email_alrm_yn, sms_alrm_yn, email, sms_no}
	 * @return
	 */
	public ResultMap updateListSpMemo(Map<String, Object> param) {
		List<Map<String, Object>> taskList = (List<Map<String, Object>>) param.get("taskList");

		// 기존 TASK MEMO 삭제 (task_uuid, regr_id)
		deleteSpBatchTaskMemo(taskList);

		for(Map<String, Object> task: taskList) {
			task.put("task_memo_uuid", UUID.randomUUID().toString());
			task.put("memo", param.get("memo"));
			task.put("tit", param.get("tit"));
		}
		
		// 신규 task memo 추가
		insertSpBatchTaskMemo(taskList);

		return ResultMap.SUCCESS();
	}

	private void deleteSpBatchTaskMemo(List<Map<String, Object>> taskist) {
		for(Map<String, Object> task: taskist) {
			spWorkplaceRepository.deleteSpTaskMemo(task);
		}
	}
	private void insertSpBatchTaskMemo(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task: taskList) {
			spWorkplaceRepository.insertSpTaskMemo(task);	// TASK_MEMO
		}
	}

	/**
	 * Task List > 메모 삭제
	 * 
	 * @param param {taskList}
	 * @return
	 */
	public ResultMap deleteListSpMemo(Map<String, Object> param){
		List<Map<String, Object>> taskList = (List<Map<String, Object>>) param.get("taskList");
		
		// TASK MEMO 삭제 (task_uuid, reg_id)
		deleteSpBatchTaskMemo(taskList);

		return ResultMap.SUCCESS();
	}
}
