package smartsuite.app.bp.workplace.dashboard.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.workplace.dashboard.repository.WorkplaceRepository;
import smartsuite.app.common.shared.ResultMap;

@SuppressWarnings({"unchecked"})
@Service
@Transactional
public class WorkplaceService {

	@Inject
	WorkplaceRepository workplaceRepository;
	
	/**
	 * Workplace > cc-page-title-bar에 위치한 신규/메모/통보/제외 count
	 * 
	 * @param param {}
	 * @return
	 */
	public Map<String, Object> findTaskCount(Map<String, Object> param) {
		return workplaceRepository.findTaskCount(param);
	}
	
	/**
	 * Work List > 지연/임박/일반/신규 count
	 * 
	 * @param param {work_ids, tick_tit, open_days, only_memo, only_noti, only_exception}
	 * @return
	 */
	public Map<String, Object> findListTaskCount(Map<String, Object> param) {
		return workplaceRepository.findListTaskCount(param);
	}

	/**
	 * Work List> 목록 조회
	 * 
	 * @param param {work_ids, tick_tit, open_days, only_memo, only_noti, only_exception, dely_srch_yn, warn_srch_yn, norm_srch_yn, new_srch_yn}
	 * @return
	 */
	public List<Map<String, Object>> findListTask(Map<String, Object> param) {
		return workplaceRepository.findListTask(param);
	}
	
	/**
	 * Work List > main task uuid로 task status 목록 조회
	 * 
	 * @param param {main_task_uuid}
	 * @return
	 */
	public List<Map<String, Object>> findListTaskStatusByMainTaskUuid(Map<String, Object> param) {
		return workplaceRepository.findListTaskStatusByMainTaskUuid(param);
	}

	/**
	 * Task List > 제외
	 * 
	 * @param param {taskList, xcept_typ_ccd, xcept_dtl_rsn}
	 * @return
	 */
	public ResultMap updateListAssignException(Map<String, Object> param) {
		List<Map<String, Object>> taskList = (List<Map<String, Object>>) param.get("taskList");
		
		// 기존 TASK ASGT XCEPT 삭제 (task_uuid, reg_id)
		deleteBatchAssignException(taskList);
		
		for(Map<String, Object> task: taskList) {
			task.put("task_asgt_xcept_uuid", UUID.randomUUID().toString());
			task.put("xcept_typ_ccd", param.get("xcept_typ_ccd"));
			task.put("xcept_dtl_rsn", param.get("xcept_dtl_rsn"));
		}
		
		// 신규 TASK ASGT XCEPT 추가
		insertBatchAssignException(taskList);
		
		return ResultMap.SUCCESS();
	}
	private void deleteBatchAssignException(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task: taskList) {
			workplaceRepository.deleteException(task);	// TASK ASGT XCEPT : task_uuid, task_authty_uuid, reg_id=username
		}
	}
	private void insertBatchAssignException(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task: taskList) {
			workplaceRepository.insertException(task);	// TASK ASGT XCEPT
		}
	}
	
	/**
	 * Task List > 제외복원
	 * 
	 * @param param {taskList}
	 * @return
	 */
	public ResultMap deleteListAssignException(Map<String, Object> param) {
		// TASK ASGT XCEPT 삭제 (task_uuid, reg_id)
		deleteBatchAssignException((List<Map<String, Object>>) param.get("taskList"));
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * Task List > 마감일자 변경
	 * 
	 * @param param {taskList, clsg_dt, chg_rsn}
	 * @return
	 */
	public ResultMap updateListClosedChange(Map<String, Object> param) {
		List<Map<String, Object>> taskList = (List<Map<String, Object>>) param.get("taskList");
		
		for(Map<String, Object> task: taskList) {
			task.put("task_clsg_dt_chg_uuid", UUID.randomUUID().toString());
			task.put("pre_clsg_dt", task.get("clsg_dt")); // 현재 마감일자를 이전 마감일자로 set
			task.put("clsg_dt"  , param.get("clsg_dt"));
			task.put("chg_rsn", param.get("chg_rsn"));
		}
		
		// 신규 TASK_CLSD_DT_CHG(이력) 추가
		insertBatchTaskCloseDtChange(taskList);
		
		// TASK.CLSG_DT 수정
		updateBatchTaskClsgDt(taskList);
		return ResultMap.SUCCESS();
	}

	private void insertBatchTaskCloseDtChange(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task: taskList) {
			workplaceRepository.insertTaskCloseDtChange(task); // TASK_CLSD_DT_CHG
		}
	}
	private void updateBatchTaskClsgDt(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task: taskList) {
			workplaceRepository.updateTaskClsgDt(task); // TASK : task_uuid, clsg_dt
		}
	}
	
	/**
	 * Task List > 메모 조회
	 * 
	 * @param param {task_uuid, task_authty_uuid}
	 * @return
	 */
	public List<Map<String, Object>> findListMemo(Map<String, Object> param) {
		return workplaceRepository.findListMemo(param);
	}
	
	/**
	 * Task List > 메모 등록
	 * 
	 * @param param {taskList, memo, alrm_dt, email_alrm_yn, sms_alrm_yn, email, sms_no}
	 * @return
	 */
	public ResultMap updateListMemo(Map<String, Object> param) {
		List<Map<String, Object>> taskList = (List<Map<String, Object>>) param.get("taskList");
		
		// 기존 TASK MEMO 삭제 (task_uuid, regr_id)
		deleteBatchTaskMemo(taskList);
		
		for(Map<String, Object> task: taskList) {
			task.put("task_memo_uuid", UUID.randomUUID().toString());
			task.put("memo", param.get("memo"));
			task.put("tit", param.get("tit"));
		}
		
		// 신규 TASK_MEMO 추가
		insertBatchTaskMemo(taskList);
		
		return ResultMap.SUCCESS();
	}

	private void deleteBatchTaskMemo(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task: taskList) {
			workplaceRepository.deleteTaskMemo(task);
		}
	}
	private void insertBatchTaskMemo(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task: taskList) {
			workplaceRepository.insertTaskMemo(task);	// TASK_MEMO
		}
	}

	/**
	 * Task List > 메모 삭제
	 * 
	 * @param param {taskList}
	 * @return
	 */
	public ResultMap deleteListMemo(Map<String, Object> param){
		List<Map<String, Object>> taskList = (List<Map<String, Object>>) param.get("taskList");
		
		// TASK_MEMO 삭제 (task_uuid, reg_id)
		deleteBatchTaskMemo(taskList);

		return ResultMap.SUCCESS();
	}
	
	/**
	 * Task List > 업무 재배정 통보
	 * 
	 * @param param {taskList, rcpnt_id, noti_rsn, noti_desc}
	 * @return
	 */
	public ResultMap updateListReassign(Map<String, Object> param) {
		List<Map<String, Object>> taskList = (List<Map<String, Object>>) param.get("taskList");
		
		for(Map<String, Object> task: taskList) {
			Map<String, Object> authInfo = workplaceRepository.findTaskAuthty(task); // TASK_AUTHTY : task_uuid, task_authty_uuid
			task.put("authty_div_ccd" , "BUYER"); // 팝업으로 할당 통보하는 것은 수신자가 항상 BUYER(user)
			task.put("authty_cd"      , param.get("rcpnt_id"));
			task.put("rcpnt_id"    , param.get("rcpnt_id"));
			task.put("rasgn_rsn_ccd"  , param.get("rasgn_rsn_ccd"));
			task.put("rasgn_rsn"      , param.get("rasgn_rsn"));
			task.put("rasgn_pre_authty_div_ccd", authInfo.get("authty_div_ccd"));
			task.put("rasgn_pre_authty_cd", authInfo.get("authty_cd"));
			task.put("pre_reg_id", authInfo.get("reg_id"));

			// 신규 insert를 위한 uuid 채번
			task.put("task_rasgn_uuid", UUID.randomUUID().toString());
		}

		// 재배정 정보 TASK_AUTHTY(권한) UPDATE
		updateBatchTaskAuthty(taskList);
		
		// 신규 TASK_RASGN(재배정) 추가
		insertBatchReassign(taskList);
		
		return ResultMap.SUCCESS();
	}

	private void updateBatchTaskAuthty(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task: taskList) {
			workplaceRepository.updateTaskAuthty(task); // TASK : task_uuid, clsg_dt
		}
	}
	private void insertBatchReassign(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task : taskList) {
			workplaceRepository.insertTaskReassign(task); // TASK_RASGN
		}
	}
	
	/**
	 * Task List > 업무 재배정 통보 반송
	 * 
	 * @param param {taskList}
	 * @return
	 */
	public ResultMap deleteListReassign(Map<String, Object> param) {
		List<Map<String, Object>> taskList = (List<Map<String, Object>>) param.get("taskList");
		
		for(Map<String, Object> task : taskList) {
			// TASK_RASGN(재배정),TASK_AUTHTY(권한) 삭제하기 전에 TASK_RASGN 에서 pre-auth 정보를 가져온다
			Map<String, Object> preAuthInfo = workplaceRepository.findPreAuthtyInfo(task);
			task.put("authty_div_ccd", preAuthInfo.get("rasgn_pre_authty_div_ccd"));
			task.put("authty_cd", preAuthInfo.get("rasgn_pre_authty_cd"));
		}
		
		// 기존 통보받은 TASK_RASGN(재배정) 삭제 (task_uuid, rcpnt_id)
		deleteBatchReceivedReassign(taskList);

		// 재배정 정보 TASK_AUTHTY(권한) UPDATE
		updateBatchTaskAuthty(taskList);

		return ResultMap.SUCCESS();
	}

	private void deleteBatchReceivedReassign(List<Map<String, Object>> taskList) {
		for(Map<String, Object> task : taskList) {
			workplaceRepository.deleteReceivedReassign(task); // TASK_RASGN : task_uuid, rcpnt_id=username
		}
	}

	public List<Map<String, Object>> findListTaskByRfxReq(Map<String, Object> param) {
		return workplaceRepository.findListTaskByRfxReq(param);
	}
	
}
