package smartsuite.app.common.workplace.shared;

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
import smartsuite.app.common.workplace.shared.service.WorkplaceSharedService;
import smartsuite.security.annotation.AuthCheck;

@SuppressWarnings("rawtypes")
@Controller
@RequestMapping (value = "**/workplace/**")
public class WorkplaceSharedController {
	
	@Inject
	WorkplaceService workplaceService;
	
	@Inject
	WorkplaceSharedService workplaceSharedService;

	/**
	 * Main Task Status > Main Task 별 지연/임박/일반/총계 count (cc-task-bar-chart, cc-main-work-status에서 사용)
	 * 
	 * @param param {}
	 * @return
	 */
	@RequestMapping(value="findListMainTaskCount.do", method = RequestMethod.POST)
	public @ResponseBody List findListMainTaskCount(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findListMainTaskCount(param);
	}
	
	/**
	 * Work Status > Main Task 리스트 (cc-work-line-chart에서 상단 탭 생성 시 사용)
	 * 
	 * @param param {up_work_id}
	 * @return
	 */
	@RequestMapping(value="findListMainTask.do", method = RequestMethod.POST)
	public @ResponseBody List findListMainTask(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findListMainTask(param);
	}

	/**
	 * sc-widget-work-list에서 사용
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findListTaskByWidget.do", method = RequestMethod.POST)
	public @ResponseBody List findListTaskByWidget(@RequestBody Map<String, Object> param) {
		return workplaceService.findListTask(param);
	}
	
	/**
	 * Task Status > Task Status 별 지연/임박/총계 count (cc-task-line-chart에서 사용)
	 * 
	 * @param param {up_work_id}
	 * @return
	 */
	@RequestMapping(value="findListTaskStatusCount.do", method = RequestMethod.POST)
	public @ResponseBody List findListTaskStatusCount(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findListTaskStatusCount(param);
	}
	
	/**
	 * Task Status > Task Status 별 알람/메모/통보/제외/지연/임박/일반/신규 여부 조회 (cc-sub-work-status에서 사용)
	 * 
	 * @param param {}
	 * @return
	 */
	@RequestMapping(value="findListTaskStatus.do", method = RequestMethod.POST)
	public @ResponseBody List findListTaskStatus(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findListTaskStatus(param);
	}
	
	/**
	 * Work List2 > 지연/임박/일반/신규 별 count
	 * 
	 * @param param {task_sts_uuids, task_tit, open_days, only_memo, only_noti, only_exception}
	 * @return
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value="findListTaskCount2.do", method = RequestMethod.POST)
	public @ResponseBody Map findListTaskCount2(@RequestBody Map<String, Object> param) {
		return workplaceService.findListTaskCount(param);
	}
	
	/**
	 * Work List2 > up_work_id로 목록 조회
	 * 
	 * @param param {up_work_id}
	 * @return
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value="findListWorkByUpWorkId2.do", method = RequestMethod.POST)
	public @ResponseBody List findListWorkByUpWorkId2(@RequestBody Map<String, Object> param) {
		return workplaceService.findListTaskStatusByMainTaskUuid(param);
	}

	@RequestMapping(value="findReturnedPoCount.do", method = RequestMethod.POST)
	public @ResponseBody Map findReturnedPoCount(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findReturnedPoCount(param);
	}
	@RequestMapping(value="findPoTypeCount.do", method = RequestMethod.POST)
	public @ResponseBody Map findPoTypeCount(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findPoTypeCount(param);
	}

	@RequestMapping(value="findUnitPriceAdequacyCount.do", method = RequestMethod.POST)
	public @ResponseBody Map findUnitPriceAdequacyCount(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findUnitPriceAdequacyCount(param);
	}

	@RequestMapping(value="findListWorkplaceDashboardPoData.do", method = RequestMethod.POST)
	public @ResponseBody List findListWorkplaceDashboardPoData(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findListWorkplaceDashboardPoData(param);
	}

	@RequestMapping(value="findListWorkplaceDashboardUnitPrice.do", method = RequestMethod.POST)
	public @ResponseBody List findListWorkplaceDashboardUnitPrice(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findListWorkplaceDashboardUnitPrice(param);
	}

	@RequestMapping(value="findItemDoctorData.do", method = RequestMethod.POST)
	public @ResponseBody Map findItemDoctorData(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findItemDoctorData(param);
	}


	@RequestMapping(value="findContractTypeStatus.do", method = RequestMethod.POST)
	public @ResponseBody Map findContractTypeStatus(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findContractTypeStatusByDashboard(param);
	}

	@RequestMapping(value="findNonStandardContractPercent.do", method = RequestMethod.POST)
	public @ResponseBody Map findNonStandardContractPercent(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findNonStandardContractPercent(param);
	}
	@RequestMapping(value="findContractExpirationNotification.do", method = RequestMethod.POST)
	public @ResponseBody Map findContractExpirationNotification(@RequestBody Map<String, Object> param) {
		return workplaceSharedService.findContractExpirationNotification(param);
	}
}
