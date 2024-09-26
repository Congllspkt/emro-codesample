package smartsuite.app.bp.workplace.setup.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import smartsuite.app.bp.workplace.setup.repository.WorkplaceSetupRepository;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.workplace.shared.service.TaskSharedService;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.scheduler.core.ScheduleService;
import smartsuite.security.authentication.Auth;

/**
 * The Class WorkplaceSetupService.
 */
@Service
@Transactional
@SuppressWarnings({"unchecked"})
public class WorkplaceSetupService {

	@Inject
	SharedService shared;

	@Inject
	ScheduleService scheduleService;
	
	@Inject
	TaskSharedService taskSharedService;

	@Inject
	WorkplaceSetupRepository workplaceSetupRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkplaceSetupService.class);

	private static final String TASK_SHARED_SERVICE_CLASS_NAME = "smartsuite.app.common.workplace.shared.service.TaskSharedService";

	private static final String WORKPLACE_TASK_JOB_GROUP = "WORKPLACE_TASK";

	private static final String MIGRATION_ALL_TASKS_JOB_NAME = "MIGRATION_ALL_TASKS";

	private static final String MIGRATION_ALL_TASKS_METHOD_NAME = "migrationAllTasks";

	/**
	 * task migration (전체 task migration)
	 *
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap requestTaskMigration(Map<String, Object> param) {
		try {

			String tenantId = Auth.getCurrentTenantId();
			Map<String, Object> migParam = Maps.newHashMap();
			if(param != null){
				migParam = param;
			}
			migParam.put("tenant", tenantId);

			scheduleService.removeScheduleTrigger(Class.forName(TASK_SHARED_SERVICE_CLASS_NAME),
					MIGRATION_ALL_TASKS_METHOD_NAME,
					WORKPLACE_TASK_JOB_GROUP,
					tenantId);

			scheduleService.registSchedule(Class.forName(TASK_SHARED_SERVICE_CLASS_NAME),
					MIGRATION_ALL_TASKS_METHOD_NAME,
					new Object[] {param},
					new Date(),
					WORKPLACE_TASK_JOB_GROUP,
					tenantId,
					MIGRATION_ALL_TASKS_JOB_NAME);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}

		return ResultMap.SUCCESS();
	}

	public Map<String, Object> verifySpChar(Map<String, Object> param, String tgtKey){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		Map<String, Object> resultParam = (Map<String, Object>) param;

		for(String key : resultParam.keySet()) {
			if(tgtKey.indexOf(key) > -1) {
				if(null != resultParam.get(key) && !"".equals(resultParam.get(key))) {
					String orgValue = (String)resultParam.get(key);
					String tgtValue = checkSpChar((String)resultParam.get(key));
					if(!orgValue.equals(tgtValue)) {
						resultMap.put(Const.RESULT_STATUS, Const.FAIL);
						return resultMap;
					}
				}
			}
		}

		return resultMap;
	}
	public String checkSpChar(String tgtValue) {
		String pattern = "^[a-zA-Z0-9-_]*$";
		String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z-_]";

		String input = tgtValue;

		boolean isSpChar = Pattern.matches(pattern, tgtValue);

		if(!isSpChar) {
			input = tgtValue.replaceAll(match, "");
		}

		return input;
	}
	/**
	 * Workplace 등록관리 > 등록(저장)
	 * 
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap saveMainTaskStatus(Map param) {
		boolean isMainSave= (Boolean) param.get("isMainSave");
		boolean initData  = (Boolean) param.get("initData");
		
		LOGGER.info("MAIN WORK YN : {}", isMainSave);
		LOGGER.info("INSERT YN : {}", initData);
		
		// sub Work일 경우, Table 검증하기
		/*if(!isMainSave){
			//검증이 빠졌을 때, 테이블, 컬럼명에 특수문자가 들어가있는지 체크한다.(허용 범위 : 알파벳 대소문자, 숫자, -, _)
			//특수문자 허용을 금지하는 필드 값
			String tgtKey = "tick_tb, tick_sts_col, tick_app_col, tit_tb, tit_col, tit_link_col, close_tb. close_col, close_link_col, chr_tb, chr_col, mng_tb, mng_col";
			
			Map<String,Object> verificationResult = this.verifySpChar(param, tgtKey);
			if(null != verificationResult && Const.FAIL.equals(verificationResult.get(Const.RESULT_STATUS))) {
				verificationResult.put(Const.RESULT_DATA, "SPECIALCHAR");
				return verificationResult;
			}
		}*/
		
		if(isMainSave) {
			if(initData) { 
				String mainTaskCd = shared.generateDocumentNumber("WRK_M");
				String usrTypCcd = (String) param.get("usr_typ_ccd");
				
				mainTaskCd = mainTaskCd.replaceAll("\\[TYP\\]", usrTypCcd.equals("BUYER") ? "BP" : "SP");
				param.put("main_task_cd", mainTaskCd);
				
				workplaceSetupRepository.insertMainTask(param);
			} else {
				workplaceSetupRepository.updateMainTask(param);
			}
		} else {
			if(initData) {
				String mainTaskCd = (String) param.get("main_task_cd");
				String taskStsCd = shared.generateDocumentNumber("WRK_S");
				
				taskStsCd = taskStsCd.replaceAll("\\[WRK_M\\]", mainTaskCd);
				param.put("task_sts_cd", taskStsCd);

				workplaceSetupRepository.insertTaskStatus(param);
			} else {
				workplaceSetupRepository.updateTaskStatus(param);
			}
		}
		
		return ResultMap.SUCCESS();
	}

	/**
	 * Workplace 등록관리 > 삭제
	 * 
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap deleteMainTaskStatus(Map param) {
		
		List<Map<String,Object>> deleteList = (List<Map<String, Object>>) param.get("deleteList");
		
		for(Map<String,Object> row : deleteList){
			if("ROOT".equals(row.get("up_task_sts_cd"))){
				// 자식 노드 조회(task_status) 후 선 삭제
				List<Map<String, Object>> taskStatus = workplaceSetupRepository.findListTaskStatusByMainTaskUuid(row);
				if(taskStatus.size() > 0) {
					workplaceSetupRepository.deleteTaskStatusByMainTaskUuid(row);
				}
				workplaceSetupRepository.deleteMainTask(row);
			} else {
				workplaceSetupRepository.deleteTaskStatusByTaskStsUuid(row);
			}
		}
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * main/task status 목록 조회 
	 * 
	 * @param param
	 * @return list
	 */
	public List<Map<String, Object>> findListMainTaskStatus(Map<String, Object> param) {
		return workplaceSetupRepository.findListMainTaskStatus(param);
	}

	/**
	 * main/task status 상세 조회 
	 * 
	 * @param param
	 * @return map
	 */
	public Map<String, Object> findMainTaskDetail(Map<String, Object> param) {

		Map<String, Object> mainTaskDetail = workplaceSetupRepository.findMainTaskDetail(param);
		
		if("Y".equals(param.get("root_yn"))) {
			mainTaskDetail.put("flag", "ROOT");
		} else {
			mainTaskDetail.put("flag", "CHILDREN");
		}
		  
		return mainTaskDetail;
	}

	/**
	 * MenuCd - ActCd 조회
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListMenuAct(Map<String, Object> param) {
		return workplaceSetupRepository.findListMenuAct(param);
	}

	/**
	 * 상태 적용 코드, 그룹 조회
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListStsCode(Map<String, Object> param) {
		return workplaceSetupRepository.findListStsCode(param);
	}
	
	/**
	 * main task list 조회
	 * 
	 * @param param
	 * @return list
	 */
	public List<Map<String, Object>> findListAllMainTask(Map<String, Object> param) {
		return workplaceSetupRepository.findListAllMainTask(param);
	}
	
	/**
	 * workplace aop list 조회
	 *
	 * @param param
	 * @return list
	 */
	public List<Map<String, Object>> findListWorkplaceAop(Map<String, Object> param) {
		return workplaceSetupRepository.findListWorkplaceAop(param);
	}


	/**
	 * workplace aop list 삭제
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> deleteListWorkplaceAop(Map<String, Object> param) {
		List<Map<String, Object>> deleteItems = (List<Map<String, Object>>)param.get("deleteItems");
		for(Map<String, Object> taskMethWkplcMeth : deleteItems) {
			workplaceSetupRepository.deleteTaskMethodWkplcMethod(taskMethWkplcMeth);
			removeCache(taskMethWkplcMeth);
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}
	
	/**
	 * workplace aop list 저장
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> saveListWorkplaceAop(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		List<Map<String, Object>> insertItems = (List<Map<String, Object>>)param.get("insertItems");
		List<Map<String, Object>> updateItems = (List<Map<String, Object>>)param.get("updateItems");
		
		// 신규추가/수정 건 중복 체크
		List<Map<String, Object>> dupItems = Lists.newArrayList();
		for(Map<String, Object> item : insertItems) {
			if(workplaceSetupRepository.checkDuplicatedWorkplaceAop(item)) {
				dupItems.add(item);
			}
		}
		for(Map<String, Object> item : updateItems) {
			if(workplaceSetupRepository.checkDuplicatedWorkplaceAop(item)) {
				dupItems.add(item);
			}
		}
		
		if(dupItems.size() > 0) {
			resultMap.put(Const.RESULT_STATUS, Const.DUPLICATED);
			resultMap.put(Const.RESULT_DATA, dupItems);
		} else {
			for(Map<String, Object> item : insertItems) {
				workplaceSetupRepository.insertTaskMethodWkplcMethod(item);
			}
			for(Map<String, Object> item : updateItems) {
				workplaceSetupRepository.updateTaskMethodWkplcMethod(item);
			}
			resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		}
		
		return resultMap;
	}

	/**
	 * remove Cache
	 *
	 * @param taskMethWkplcMeth
	 */
	private void removeCache(Map<String, Object> taskMethWkplcMeth){
		String tenant = Auth.getCurrentTenantId();
		deletePostCache(tenant, taskMethWkplcMeth);
		deletePreCache(tenant, taskMethWkplcMeth);
	}
	
	/**
	 * delete PRE Cache
	 *
	 * @param tenant the tenant
	 * @param param the param
	 */
	@SuppressWarnings("unused")
	@CacheEvict(value="workplace-setup", key="#tenant + '_PRE_' + #param.get(\"task_class\").toString()+'_' + #param.get(\"task_method\").toString()")
	public void deletePreCache(String tenant, Map<String, Object> param) {
		//TODO
	}
	
	/**
	 * delete POST Cache
	 *
	 * @param tenant the tenant
	 * @param param the param
	 */
	@SuppressWarnings("unused")
	@CacheEvict(value="workplace-setup", key="#tenant + '_POST_' + #param.get(\"task_class\").toString() +'_' + #param.get(\"task_method\").toString()")
	public void deletePostCache(String tenant, Map<String, Object> param) {
		//TODO
	}

	/**
	 * data source 목록  조회 
	 * 
	 * @param param
	 * @return map
	 */
	public List<Map<String, Object>> findListDataSource(Map<String, Object> param) {
		Map<String, Object> searchParam = (Map<String, Object>) param.get("searchParam");
		return workplaceSetupRepository.findListDataSource(searchParam);
	}
	
	/**
	 * data source 저장
	 * 
	 * @param param
	 * @return 
	 */
	@RequestMapping(value="saveDataSource.do")
	public ResultMap saveDataSource(@RequestBody Map<String, Object> param){
		List<Map<String, Object>> insertItems = (List<Map<String, Object>>) param.get("insertItems");
		List<Map<String, Object>> updateItems = (List<Map<String, Object>>) param.get("updateItems");

		// 중복 체크
		List<Map<String, Object>> dupItems = Lists.newArrayList();
		for(Map<String, Object> item : insertItems) {
			if(workplaceSetupRepository.checkDuplicatedDataSource(item)) {
				dupItems.add(item);
			}
		}
		
		for(Map<String, Object> item : updateItems) {
			if(workplaceSetupRepository.checkDuplicatedDataSource(item)) {
				dupItems.add(item);
			}
		}
		
		if(dupItems.size() > 0) {
			return ResultMap.DUPLICATED();
		} else {
			
			for(Map<String, Object> item : insertItems) {
				workplaceSetupRepository.insertDataSource(item);
			}
			
			for(Map<String, Object> item : updateItems) {
				workplaceSetupRepository.updateDataSource(item);
			}
			
			return ResultMap.SUCCESS();
		}
	}

	/**
	 * data source 삭제
	 * 
	 * @param param
	 * @return
	 */
	public ResultMap deleteDataSource(@RequestBody Map<String, Object> param){
		
		List<Map<String, Object>> deleteItems = (List<Map<String, Object>>) param.get("deleteItems");
		
		for(Map<String, Object> row : deleteItems) {
			workplaceSetupRepository.deleteDataSource(row);
		}
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * data source list 조회
	 * 
	 * @param
	 * @return list
	 */
	public List<Map<String, Object>> findListAllDataSource() {
		Map<String, Object> param = Maps.newHashMap();
		return workplaceSetupRepository.findListAllDataSource(param);
	}

}
