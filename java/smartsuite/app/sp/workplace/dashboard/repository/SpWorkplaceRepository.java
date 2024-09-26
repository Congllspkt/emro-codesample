package smartsuite.app.sp.workplace.dashboard.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpWorkplaceRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "sp-workplace.";

	/**
	 * Workplace > cc-page-title-bar에 위치한 신규/메모/통보/제외 count
	 *
	 * @param param {}
	 * @return
	 */
	public Map<String, Object> findSpTaskCount(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findSpTaskCount", param);
	}

	/**
	 * Work List > 지연/임박/일반/신규 count
	 *
	 * @param param {work_ids, tick_tit, open_days, only_memo, only_noti, only_exception}
	 * @return
	 */
	public Map<String, Object> findListSpTaskCount(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findListSpTaskCount", param);
	}

	/**
	 * Work List> 목록 조회
	 *
	 * @param param {work_ids, tick_tit, open_days, only_memo, only_noti, only_exception, dely_srch_yn, warn_srch_yn, norm_srch_yn, new_srch_yn}
	 * @return
	 */
	public List<Map<String, Object>> findListSpTask(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListSpTask", param);
	}

	/**
	 * Work List > up_work_id로 목록 조회
	 *
	 * @param param {up_work_id}
	 * @return
	 */
	public List<Map<String, Object>> findListSpTaskStatusByMainTaskUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListSpTaskStatusByMainTaskUuid", param);
	}

	public void insertTaskMemo(Map<String, Object> task) {
		sqlSession.insert(NAMESPACE + "insertTaskMemo", task);	// TASK_MEMO
	}

	/**
	 * Work List > 메모 조회
	 *
	 * @param param {tick_ids}
	 * @return
	 */
	public List<Map<String, Object>> findListSpMemo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListSpMemo", param);
	}

	public void deleteSpException(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteSpException", param);	//TASK ASGT XCEPT : tick_id, reg_id=username
	}
	public void insertSpException(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertSpException", param);	// TASK ASGT XCEPT
	}


	public void insertSpTaskMemo(Map<String, Object> task) {
		sqlSession.insert(NAMESPACE + "insertSpTaskMemo", task);	// TASK_MEMO
	}

	public void deleteSpTaskMemo(Map<String, Object> task) {
		sqlSession.delete(NAMESPACE + "deleteSpTaskMemo", task);	// ESTOTMM : tick_id, reg_id=username
	}
}
