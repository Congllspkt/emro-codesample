package smartsuite.app.common.workplace.task;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomWorkplaceProcessor {

	@SuppressWarnings("unused")
	@Inject
	private CustomWorkTaskService customWorkTaskService;
	
	/**
	 * TODO : 추후 각 프로젝트에서 custom하게 사용할 task 발생/소멸 실행자를 기술
	 * 
	public void savePr(Map<String, Object> param) {
		customWorkTaskService.createTaskPR(param);
	}
	 */
}
