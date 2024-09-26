package smartsuite.app.bp.workplace.setup.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.config.workplace.vo.WorkTaskMethodSetting;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * The Interface WorkplaceSetupDAO.
 */
@Service
public class WorkplaceSetupRepository {
	
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE="workplace-setup.";
	
	/**
	 * list all tenant 조회한다.
	 *
	 * @return the list< map< string, object>>
	 * @Date : 2019. 4. 22
	 * @Method Name : findListAllTenant
	 */
	public List<Map<String, Object>> findListAllTenant(){
		return sqlSession.selectList(NAMESPACE+"findListAllTenant");
	}
	
	/**
	 * Select list all settings (work method - 선행/후행 여부 조회)
	 *
	 * @param tenId
	 * @return the list
	 */
	public List<WorkTaskMethodSetting> findListAllSetting(String tenId) {
		return sqlSession.selectList(NAMESPACE+"findListAllSetting",tenId);
	}

	/**
	 * 메인 업무를 등록한다.
	 *
	 * @param param
	 */
	public void insertMainTask (Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertMainTask", param);
	}
	/**
	 * 메인 업무를 수정한다.
	 *
	 * @param param
	 */
	public void updateMainTask (Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateMainTask", param);
	}
	/**
	 * 업무 상태를 등록한다.
	 *
	 * @param param
	 */
	public void insertTaskStatus (Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertTaskStatus", param);
	}
	/**
	 * 업무 상태를 수정한다.
	 *
	 * @param param
	 */
	public void updateTaskStatus (Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateTaskStatus", param);
	}

	/**
	 * 메인 업무 하위로 등록된 업무 상태 목록을 조회한다.
	 *
	 * @param param
	 */
	public List findListTaskStatusByMainTaskUuid(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListTaskStatusByMainTaskUuid", param);
	}
	/**
	 * 메인 업무 하위에 등록된 업무 상태를 삭제한다. (sts = 'D')
	 *
	 * @param param
	 */
	public void deleteTaskStatusByMainTaskUuid(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deleteTaskStatusByMainTaskUuid", param);
	}

	/**
	 * 메인 업무를 삭제한다. (sts = 'D')
	 *
	 * @param param
	 */
	public void deleteMainTask(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deleteMainTask", param);
	}
	
	/**
	 * 업무 상태를 삭제한다. (sts = 'D')
	 *
	 * @param param
	 */
	public void deleteTaskStatusByTaskStsUuid(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deleteTaskStatusByTaskStsUuid", param);
	}

	/**
	 * main/task status 목록 조회
	 *
	 * @param param
	 * @return list
	 */
	public List<Map<String, Object>> findListMainTaskStatus(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListMainTaskStatus", param);
	}

	/**
	 * main/task status 목록 조회
	 *
	 * @param param
	 * @return list
	 */
	public Map<String, Object> findMainTaskDetail(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findMainTaskDetail", param);
	}

	/**
	 * MenuCd - ActCd 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListMenuAct(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListMenuAct", param);
	}

	/**
	 * 상태 적용 코드, 그룹 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListStsCode(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListStsCode", param);
	}

	/**
	 * main task list 조회
	 *
	 * @param param
	 * @return list
	 */
	public List<Map<String, Object>> findListAllMainTask(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListAllMainTask", param);
	}

	/**
	 * workplace aop list 조회
	 *
	 * @param param
	 * @return list
	 */
	public List<Map<String, Object>> findListWorkplaceAop(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListWorkplaceAop", param);
	}


	/**
	 * 업무 메서드 워크플레이스 메서드를 삭제한다. (aop 설정) (sts = 'D')
	 *
	 * @param param
	 * @return list
	 */
	public void deleteTaskMethodWkplcMethod(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deleteTaskMethodWkplcMethod", param);
	}

	/**
	 *  workplace AOP 저장(신규)
	 *
	 * @param param
	 * @return list
	 */
	public void insertTaskMethodWkplcMethod(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertTaskMethodWkplcMethod", param);
	}
	/**
	 *  workplace AOP 저장(수정)
	 *
	 * @param param
	 * @return list
	 */
	public void updateTaskMethodWkplcMethod(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "updateTaskMethodWkplcMethod", param);
	}

	/**
	 * 중복 work method 여부
	 *
	 * @param workMethod
	 * @return
	 */
	public boolean checkDuplicatedWorkplaceAop(Map<String, Object> workMethod) {
		return sqlSession.selectOne(NAMESPACE + "checkDuplicatedWorkplaceAop", workMethod);
	}

	/**
	 * data source 목록  조회
	 *
	 * @param param
	 * @return map
	 */
	public List<Map<String, Object>> findListDataSource(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListDataSource", param);
	}

	/**
	 *  데이터소스 저장(신규)
	 *
	 * @param param
	 * @return list
	 */
	public void insertDataSource(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertDataSource", param);
	}
	/**
	 *  데이터소스 저장(수정)
	 *
	 * @param param
	 * @return list
	 */
	public void updateDataSource(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "updateDataSource", param);
	}

	public boolean checkDuplicatedDataSource(Map item) {
		return sqlSession.selectOne(NAMESPACE + "checkDuplicatedDataSource", item);
	}

	/**
	 * 데이터소스 저장(수정) (sts = 'D')
	 *
	 * @param param
	 */
	public void deleteDataSource(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deleteDataSource", param);
	}

	/**
	 * data source list 조회
	 *
	 * @param
	 * @return list
	 */
	public List<Map<String, Object>> findListAllDataSource(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListAllDataSource", param);
	}
}
