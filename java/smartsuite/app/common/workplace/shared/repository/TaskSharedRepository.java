package smartsuite.app.common.workplace.shared.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class TaskSharedRepository {

	/** The sql session. */
	@Inject
	private SqlSession sqlSession;

	/** The namespace. */
	@Inject
	private static final String NAMESPACE = "task-shared.";

	/**
	 * open task 생성
	 *
	 * @param openTask
	 */
	public void insertOpenTask(Map<String, Object> openTask) {
		sqlSession.insert(NAMESPACE + "insertOpenTask", openTask);        // task 생성
	}

	public void insertOpenAuth(Map<String, Object> openAuthty) {
		sqlSession.insert(NAMESPACE + "insertOpenAuth", openAuthty);
	}

	// task 수정
	// param : operTask
	public void updateOpenTask(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateOpenTask", param);
	}

	// 기존 task Authty 삭제
	// param : openTask
	public void deleteOpenTaskAuthty(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteOpenTaskAuthty", param);
	}

	public void deleteOpenTaskClsgDtChange(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteOpenTaskClsgDtChange" , param);
	}

	public void deleteOpenTaskMemo(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteOpenTaskMemo"        , param);

	}

	public void deleteOpenTaskAsgtXcept(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteOpenTaskAsgtXcept", param);
	}
	public void deleteOpenTaskReAssign(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteOpenTaskReAssign", param);
	}
	public void deleteOpenTask(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteOpenTask"   , param);
	}

	public String findTaskUuidByTaskUuid(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findTaskUuidByTaskUuid", param);	// task_uuid로 조회
	}

	public Map findOpenTask(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findOpenTask", param);	// task_uuid로 조회
	}

	public Map findTaskConfig(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findTaskConfig", param);	// menu_cd, grp_ccd, task_sts_ccd로 조회
	}

	public Map findTaskDataSrc(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findTaskDataSrc", param);
	}
	public List findTaskAuthtyDataSrc(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findTaskAuthtyDataSrc", param);
	}

	public List findListTaskStatusUsed(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListTaskStatusUsed", param);
	}

	public List findListDataSourceTaskData(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListDataSourceTaskData", param);
	}
}
