package smartsuite.app.bp.workplace.monitoring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.workplace.monitoring.service.WorkplaceMonitoringService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="**/workplace/monitoring/**/")
public class WorkplaceMonitoringController {

	@Inject
	private WorkplaceMonitoringService workplaceMonitoringService;

	@RequestMapping(value="findListErrorTask.do", method = RequestMethod.POST)
	public @ResponseBody List findListErrorTask(@RequestBody Map<String, Object> param) {
		return workplaceMonitoringService.findListErrorTask(param);
	}
	@RequestMapping(value="findErrorTaskDetailAuthty.do", method = RequestMethod.POST)
	public @ResponseBody Map findErrorTaskDetailAuthty(@RequestBody Map<String, Object> param) {
		return workplaceMonitoringService.findErrorTaskDetailAuthty(param);
	}
	@RequestMapping(value="saveErrorTaskMonitoringInfo.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap saveErrorTaskMonitoringInfo(@RequestBody Map<String, Object> param) {
		return workplaceMonitoringService.saveErrorTaskMonitoringInfo(param);
	}
	@RequestMapping(value="createTaskByErrorTask.do", method = RequestMethod.POST)
	public @ResponseBody ResultMap createTaskByErrorTask(@RequestBody Map<String, Object> param) {
		return workplaceMonitoringService.createTaskByErrorTask(param);
	}

}
