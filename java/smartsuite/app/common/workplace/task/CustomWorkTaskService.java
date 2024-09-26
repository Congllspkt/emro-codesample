package smartsuite.app.common.workplace.task;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomWorkTaskService {

	@SuppressWarnings("unused")
	@Inject
	private SqlSession sqlSession;

	@SuppressWarnings("unused")
	@Inject
	private static final String NAMESPACE = "custom-work-task.";
	
	//@SuppressWarnings("unused")
	//@Inject
	//private TaskSharedService taskSharedService;
	
	/**
	 * TODO : 추후 각 프로젝트에서 custom하게 사용
	 * 
	public void createTaskPR(Map<String, Object> param) {
		Map<String, Object> pr = sqlSession.selectOne(NAMESPACE + "findPR", param);
		
		pr.put("app_id", pr.get("pr_id"));
		taskSharedService.createTask(pr);
	}
	 */
}
