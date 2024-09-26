package smartsuite.app.bp.workplace.monitoring.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class WorkplaceMonitoringRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE="workplace-monitoring.";


	/**
	 * 수집된 오류 task 목록 조회
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2024. 02. 15
	 * @Method Name : findListErrorTask
	 */
	public List findListErrorTask(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListErrorTask", param);
	}

	/**
	 * 사용 중인 데이터 소스 목록 조회
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2024. 02. 15
	 * @Method Name : findListDatSrcUsed
	 */
	public List findListDatSrcUsed(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListDatSrcUsed", param);
	}

	public void insertErrorTaskInfo(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertErrorTaskInfo", param);
	}

	public void insertErrorTaskDetailInfo(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertErrorTaskDetailInfo", param);
	}

	public void insertErrorTaskAuthtyInfoByTask(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertErrorTaskAuthtyInfoByTask", param);
	}

	public void insertErrorTaskAuthtyInfoByDatSrc(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertErrorTaskAuthtyInfoByDatSrc", param);
	}

	public Map findErrorTaskDetail(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findErrorTaskDetail", param);
	}

	public List findListErrorTaskAuthty(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListErrorTaskAuthty", param);
	}

	public void deleteErrorTaskAuthty(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteErrorTaskAuthty", param);
	}
	public void deleteErrorTaskDetail(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteErrorTaskDetail", param);
	}

	public void deleteErrorTask(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteErrorTask", param);
	}

	public Map findTaskConfigByTaskStsUuid(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findTaskConfigByTaskStsUuid", param);
	}

	public void insertTaskByDatSrc(Map<String ,Object> param) {
		sqlSession.insert(NAMESPACE + "insertTaskByDatSrc", param);
	}

	public void insertTaskAuthtyByDatSrc(Map<String ,Object> param) {
		sqlSession.insert(NAMESPACE + "insertTaskAuthtyByDatSrc", param);
	}
}
