package smartsuite.app.bp.workplace.setup;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;

import smartsuite.app.bp.workplace.setup.service.WorkplaceSetupService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.config.workplace.WorkplaceProvider;
import smartsuite.scheduler.core.ScheduleService;
import smartsuite.security.annotation.AuthCheck;

@Controller
@RequestMapping(value="**/workplace/setup/**/")
public class WorkplaceSetupController {
	/** The Workplace setup service. */
	@Inject
	private WorkplaceSetupService workplaceSetupService;
	
	@Inject
	private WorkplaceProvider workplace;

	@Inject
	ScheduleService scheduleService;


	/**
	 * Workplace 등록관리 > 마이그레이션
	 * 
	 * @param param
	 * @return
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value="requestTaskMigration.do")
	public @ResponseBody ResultMap requestTaskMigration(@RequestBody Map<String, Object> param) {
		return workplaceSetupService.requestTaskMigration(param);
	}
	
	/**
	 * Workplace 등록관리 > 등록(저장)
	 * 
	 * @param param
	 * @return ResultMap
	 */
	@RequestMapping(value="saveMainTaskStatus.do")
	public @ResponseBody ResultMap saveMainTaskStatus(@RequestBody Map param) {
		return workplaceSetupService.saveMainTaskStatus(param);
	}
	
	/**
	 * Workplace 등록관리 > 삭제
	 * 
	 * @param param
	 * @return ResultMap
	 */
	@RequestMapping(value="deleteMainTaskStatus.do")
	public @ResponseBody ResultMap deleteMainTaskStatus(@RequestBody Map param) {
		return workplaceSetupService.deleteMainTaskStatus(param);
	}
	
	/**
	 * main/task status 목록 조회 
	 * 
	 * @param param
	 * @return list
	 */
	@RequestMapping(value="findListMainTaskStatus.do")
	public @ResponseBody List<Map<String, Object>> findListMainTaskStatus(@RequestBody Map<String, Object> param) {
		return workplaceSetupService.findListMainTaskStatus(param);
	}
	
	/**
	 * main/task status 상세 조회 
	 * 
	 * @param param
	 * @return map
	 */
	@RequestMapping(value="findMainTaskDetail.do")
	public @ResponseBody Map<String, Object> findMainTaskDetail(@RequestBody Map<String, Object> param) {
		return workplaceSetupService.findMainTaskDetail(param);
	}
	
	/**
	 * MenuCd - ActCd 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findListMenuAct.do")
	public @ResponseBody List<Map<String, Object>> findListMenuAct(@RequestBody Map<String, Object> param) {
		return workplaceSetupService.findListMenuAct(param);
	}
	
	/**
	 * 공통코드 그룹-상세 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findListStsCode.do")
	public @ResponseBody List<Map<String, Object>> findListStsCode(@RequestBody Map<String, Object> param) {
		return workplaceSetupService.findListStsCode(param);
	}
	
	/**
	 * main task list 조회
	 * 
	 * @param param
	 * @return list
	 */
	@RequestMapping(value="findListAllMainTask.do")
	public @ResponseBody List<Map<String, Object>> findListAllMainTask(@RequestBody Map<String, Object> param){
		return workplaceSetupService.findListAllMainTask(param);
	}
	
	/**
	 * workplace aop list 조회
	 *
	 * @param param
	 * @return list
	 */
	@RequestMapping(value="findListWorkplaceAop.do")
	public @ResponseBody List<Map<String, Object>> findListWorkplaceAop(@RequestBody Map<String, Object> param){
		return workplaceSetupService.findListWorkplaceAop(param);
	}


	/**
	 * workplace aop list 삭제
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value="deleteListWorkplaceAop.do")
	public @ResponseBody Map<String, Object> deleteListWorkplaceAop(@RequestBody Map<String, Object> param){
		return workplaceSetupService.deleteListWorkplaceAop(param);
	}
	
	/**
	 * workplace aop list 저장
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value="saveListWorkplaceAop.do")
	public @ResponseBody Map<String, Object> saveListWorkplaceAop(@RequestBody Map<String, Object> param){
		return workplaceSetupService.saveListWorkplaceAop(param);
	}


	/**
	 * cache 적재
	 *
	 * @param param the param
	 * @return the map
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="loadWorkplaceSetupToCache.do")
	public @ResponseBody Map<String, Object> loadWorkplaceSetupToCache(@RequestBody Map<String, Object> param){
		Map<String, Object> resultMap = Maps.newHashMap();
		workplace.loadBySys();
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}

	/**
	 * data source 목록  조회 
	 * 
	 * @param param
	 * @return map
	 */
	@RequestMapping(value="findListDataSource.do")
	public @ResponseBody List<Map<String, Object>> findListDataSource(@RequestBody Map<String, Object> param) {
		return workplaceSetupService.findListDataSource(param);
	}
	
	/**
	 * data source 저장
	 * 
	 * @param param
	 * @return ResultMap
	 */
	@RequestMapping(value="saveDataSource.do")
	public @ResponseBody ResultMap saveDataSource(@RequestBody Map<String, Object> param){
		return workplaceSetupService.saveDataSource(param);
	}
	
	/**
	 * data source 삭제
	 * 
	 * @param param
	 * @return ResultMap
	 */
	@RequestMapping(value="deleteDataSource.do")
	public @ResponseBody ResultMap deleteDataSource(@RequestBody Map<String, Object> param){
		return workplaceSetupService.deleteDataSource(param);
	}
	
	/**
	 * data source list 조회
	 * 
	 * @param param
	 * @return list
	 */
	@RequestMapping(value="findListAllDataSource.do")
	public @ResponseBody List<Map<String, Object>> findListAllDataSource(@RequestBody Map<String, Object> param){
		return workplaceSetupService.findListAllDataSource();
	}


}
