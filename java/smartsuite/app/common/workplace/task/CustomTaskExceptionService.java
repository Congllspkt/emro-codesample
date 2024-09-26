package smartsuite.app.common.workplace.task;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomTaskExceptionService {

	public String findTaskTitle(Map<String, Object> param) {
		String workId = (String)param.get("work_id");

		/**
		 * TODO : 추후 각 프로젝트에서 custom하게 사용할 work에 대해 기술

		if(CustomWorkplaceConst.CUSTOM_EX_TITLE_WORK_IDS.contains(workId)) {
			
		}
		 */
		return null;
	}
	
	public Map<String, Object> findCloseDtInfo(Map<String, Object> param) {
		String workId = (String)param.get("work_id");

		/** TODO : 추후 각 프로젝트에서 custom하게 사용할 work에 대해 기술
		if(CustomWorkplaceConst.CUSTOM_EX_CLOSE_DT_WORK_IDS.contains(workId)) {
			
		}
		 */
		return null;
	}
	
	public Map<String, Object> findCloseDtInfoByDataType(Map<String, Object> param) {
		String workId = (String)param.get("work_id");

		/** TODO : 추후 각 프로젝트에서 custom하게 사용할 work에 대해 기술
		if(CustomWorkplaceConst.CUSTOM_EX_CLOSE_DT_TYPE_WORK_IDS.contains(workId)) {
			
		}
		 */
		return null;
	}
}
