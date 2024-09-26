package smartsuite.app.bp.workplace.dashboard.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WorkplaceRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "workplace.";

	/**
	 * Workplace > cc-page-title-bar에 위치한 신규/메모/통보/제외 count
	 *
	 * @param param {}
	 * @return
	 */
	public Map<String, Object> findTaskCount(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findTaskCount", param);
	}

	/**
	 * Work List > 지연/임박/일반/신규 count
	 *
	 * @param param {work_ids, tick_tit, open_days, only_memo, only_noti, only_exception}
	 * @return
	 */
	public Map<String, Object> findListTaskCount(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findListTaskCount", param);
	}

	/**
	 * Work List> 목록 조회
	 *
	 * @param param {work_ids, tick_tit, open_days, only_memo, only_noti, only_exception, dely_srch_yn, warn_srch_yn, norm_srch_yn, new_srch_yn}
	 * @return
	 */
	public List<Map<String, Object>> findListTask(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListTask", param);
	}

	/**
	 * Work List > up_work_id로 목록 조회
	 *
	 * @param param {up_work_id}
	 * @return
	 */
	public List<Map<String, Object>> findListTaskStatusByMainTaskUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListTaskStatusByMainTaskUuid", param);
	}

	/**
	 * Work List > 메모 조회
	 *
	 * @param param {tick_ids}
	 * @return
	 */
	public List<Map<String, Object>> findListMemo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListMemo", param);
	}

	public void insertTaskMemo(Map<String, Object> task) {
		sqlSession.insert(NAMESPACE + "insertTaskMemo", task);	// TASK_MEMO
	}

	public void deleteTaskMemo(Map<String, Object> task) {
		sqlSession.delete(NAMESPACE + "deleteTaskMemo", task);	// task memo : tick_id, reg_id=username
	}

	public void deleteException(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteException", param);	//TASK ASGT XCEPT : tick_id, reg_id=username
	}
	public void insertException(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertException", param);	// TASK ASGT XCEPT
	}

	public void insertTaskCloseDtChange(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertTaskCloseDtChange", param);	// TASK_CLSD_DT_CHG
	}

	public void updateTaskClsgDt(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateTaskClsgDt", param);	// TASK : task_uuid, clsg_dt
	}

	public Map<String, Object> findTaskAuthty(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findTaskAuthty", param);
	}

	public Map<String, Object> findPreAuthtyInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPreAuthtyInfo", param);
	}

	public void updateTaskAuthty(Map<String, Object> param){
		sqlSession.insert(NAMESPACE + "updateTaskAuthty", param); // TASK_AUTHTY
	}

	public void insertTaskReassign(Map<String, Object> param){
		sqlSession.insert(NAMESPACE + "insertTaskReassign", param); // TASK_RASGN
	}

	public void deleteReceivedReassign(Map<String, Object> param){
		sqlSession.delete(NAMESPACE + "deleteReceivedReassign", param); // TASK_RASGN : task_uuid, rcpnt_id=username
	}


	public List<Map<String, Object>> findListTaskByRfxReq(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListTaskByRfxReq", param);
	}
}
