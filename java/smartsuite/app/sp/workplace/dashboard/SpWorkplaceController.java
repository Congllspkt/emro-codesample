package smartsuite.app.sp.workplace.dashboard;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.workplace.dashboard.service.SpWorkplaceService;
import smartsuite.security.annotation.AuthCheck;

@SuppressWarnings("rawtypes")
@Controller
@RequestMapping (value = "**/sp/workplace/**")
public class SpWorkplaceController {

	@Inject
	SpWorkplaceService workplaceService;

	/**
	 * Workplace > cc-page-title-bar에 위치한 신규/메모/통보/제외 count
	 *
	 * @param param {}
	 * @return
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value="findSpTaskCount.do", method = RequestMethod.POST)
	public @ResponseBody Map findSpTaskCount(@RequestBody Map<String, Object> param) {
		return workplaceService.findSpTaskCount(param);
	}

	/**
	 * Work List > 지연/도래/일반/신규 별 count
	 *
	 * @param param {work_ids, tick_tit, open_days, only_memo, only_noti, only_exception}
	 * @return
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value="findListSpTaskCount.do", method = RequestMethod.POST)
	public @ResponseBody Map findListSpTaskCount(@RequestBody Map<String, Object> param) {
		return workplaceService.findListSpTaskCount(param);
	}

	/**
	 * Work List> 목록 조회
	 *
	 * @param param {work_ids, tick_tit, open_days, only_memo, only_noti, only_exception, dely_srch_yn, warn_srch_yn, norm_srch_yn, new_srch_yn}
	 * @return
	 */
	@RequestMapping(value="findListSpTask.do", method = RequestMethod.POST)
	public @ResponseBody List findListSpTask(@RequestBody Map<String, Object> param) {
		return workplaceService.findListSpTask(param);
	}

	/**
	 * Work List > main task uuid로 task status 목록 조회
	 *
	 * @param param {main_task_uuid}
	 * @return
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value="findListSpTaskStatusByMainTaskUuid.do", method = RequestMethod.POST)
	public @ResponseBody List findListSpTaskStatusByMainTaskUuid(@RequestBody Map<String, Object> param) {
		return workplaceService.findListSpTaskStatusByMainTaskUuid(param);
	}
	
	/**
	 * Work List > 제외복원
	 * 
	 * @param param {workList}
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="deleteListSpAssignException.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap deleteListSpAssignException(@RequestBody Map<String, Object> param) {
		return workplaceService.deleteListSpAssignException(param);
	}
	
	/**
	 * Task List > 제외
	 * 
	 * @param param {taskList, xcept_typ_ccd, xcept_dtl_rsn}
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="updateListAssignException.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap updateListAssignException(@RequestBody Map<String, Object> param) {
		return workplaceService.updateListAssignException(param);
	}
	
	/**
	 * Task List > 메모 조회
	 * 
	 * @param param {tick_ids}
	 * @return
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value="findListSpMemo.do", method = RequestMethod.POST)
	public @ResponseBody List findListSpMemo(@RequestBody Map<String, Object> param) {
		return workplaceService.findListSpMemo(param);
	}
	
	/**
	 * Work List > 메모 등록
	 * 
	 * @param param {workList, memo, alrm_dt, email_alrm_yn, sms_alrm_yn, email, sms_no}
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="updateListSpMemo.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap updateListSpMemo(@RequestBody Map<String, Object> param) {
		return workplaceService.updateListSpMemo(param);
	}
	
	/**
	 * Work List > 메모 삭제
	 * 
	 * @param param {workList}
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="deleteListSpMemo.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap deleteListSpMemo(@RequestBody Map<String, Object> param) {
		return workplaceService.deleteListSpMemo(param);
	}
}
