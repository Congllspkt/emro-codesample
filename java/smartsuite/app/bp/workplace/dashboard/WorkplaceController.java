package smartsuite.app.bp.workplace.dashboard;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.workplace.dashboard.service.WorkplaceService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.security.annotation.AuthCheck;

@SuppressWarnings("rawtypes")
@Controller
@RequestMapping (value = "**/bp/workplace/**")
public class WorkplaceController {
	
	@Inject
	WorkplaceService workplaceService;
	
	/**
	 * Workplace > cc-page-title-bar에 위치한 신규/메모/통보/제외 count
	 * 
	 * @param param {}
	 * @return
	 */
	@RequestMapping(value="findTaskCount.do", method = RequestMethod.POST)
	public @ResponseBody Map findTaskCount(@RequestBody Map<String, Object> param) {
		return workplaceService.findTaskCount(param);
	}
	
	/**
	 * Work List > 지연/임박/일반/신규 별 count
	 * 
	 * @param param {work_ids, tick_tit, open_days, only_memo, only_noti, only_exception}
	 * @return
	 */
	@RequestMapping(value="findListTaskCount.do", method = RequestMethod.POST)
	public @ResponseBody Map findListTaskCount(@RequestBody Map<String, Object> param) {
		return workplaceService.findListTaskCount(param);
	}
	
	/**
	 * Work List> 목록 조회
	 * 
	 * @param param {work_ids, tick_tit, open_days, only_memo, only_noti, only_exception, dely_srch_yn, warn_srch_yn, norm_srch_yn, new_srch_yn}
	 * @return
	 */
	/*@AuthCheck (fixedMenuCode = FixedMenuCodeConst.WORKPLACE)*/
	@RequestMapping(value="findListTask.do", method = RequestMethod.POST)
	public @ResponseBody List findListTask(@RequestBody Map<String, Object> param) {
		return workplaceService.findListTask(param);
	}
	
	/**
	 * Work List > main task uuid로 task status 목록 조회
	 *
	 * @param param {main_task_uuid}
	 * @return
	 */
	@RequestMapping(value="findListTaskStatusByMainTaskUuid.do", method = RequestMethod.POST)
	public @ResponseBody List findListTaskStatusByMainTaskUuid(@RequestBody Map<String, Object> param) {
		return workplaceService.findListTaskStatusByMainTaskUuid(param);
	}
	
	/**
	 * Task List > 제외
	 * 
	 * @param param {taskList, xcept_typ_ccd, xcept_dtl_rsn}
	 * @return
	 */
	@RequestMapping(value="updateListAssignException.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap updateListAssignException(@RequestBody Map<String, Object> param) {
		return workplaceService.updateListAssignException(param);
	}
	
	/**
	 * Task List > 제외복원
	 * 
	 * @param param {taskList}
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="deleteListAssignException.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap deleteListAssignException(@RequestBody Map<String, Object> param) {
		return workplaceService.deleteListAssignException(param);
	}
	
	/**
	 * Task List > 마감일자 변경
	 * 
	 * @param param {taskList, chg_dt, chg_rsn}
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="updateListClosedChange.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap updateListClosedChange(@RequestBody Map<String, Object> param) {
		return workplaceService.updateListClosedChange(param);
	}

	/**
	 * Task List > 메모 조회
	 * 
	 * @param param {tick_ids}
	 * @return
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value="findListMemo.do", method = RequestMethod.POST)
	public @ResponseBody List findListMemo(@RequestBody Map<String, Object> param) {
		return workplaceService.findListMemo(param);
	}
	
	/**
	 * Task List > 메모 등록
	 * 
	 * @param param {taskList, memo, alrm_dt, email_alrm_yn, sms_alrm_yn, email, sms_no}
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="updateListMemo.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap updateListMemo(@RequestBody Map<String, Object> param) {
		return workplaceService.updateListMemo(param);
	}

	/**
	 * Task List > 메모 삭제
	 * 
	 * @param param {taskList}
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="deleteListMemo.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap deleteListMemo(@RequestBody Map<String, Object> param) {
		return workplaceService.deleteListMemo(param);
	}

	/**
	 * Task List > 업무 재배정 통보
	 * 
	 * @param param {taskList, rcpnt_id, noti_rsn, noti_desc}
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="updateListReassign.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap updateListReassign(@RequestBody Map<String, Object> param) {
		return workplaceService.updateListReassign(param);
	}

	/**
	 * Task List > 업무 재배정 통보 반송
	 * 
	 * @param param {taskList}
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="deleteListReassign.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap deleteListReassign(@RequestBody Map<String, Object> param) {
		return workplaceService.deleteListReassign(param);
	}

	/**
	 * To-do list에서 rfx 요청 목록 데이터 조회
	 *
	 */
	@AuthCheck
	@RequestMapping(value="findListTaskByRfxReq.do", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, Object>> findListTaskByRfxReq(@RequestBody Map<String, Object> param) {
		return workplaceService.findListTaskByRfxReq(param);
	}
	
}
