package smartsuite.app.bp.srm.performance.pfmcSetup.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PegSetupRepository {
	private static final String NAMESPACE = "peg-setup.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 퍼포먼스평가그룹 목록을 조회
	 *
	 * @param param
	 */
	public FloaterStream findListPeg(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListPeg", param);
	}

	/**
	 * 퍼포먼스평가그룹 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updatePegByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updatePegByDelete", param);
	}

	/**
	 * 퍼포먼스 협력사관리그룹 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateVmgStsByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updateVmgStsByDelete", param);
	}

	/**
	 * 퍼포먼스평가그룹 Mater 저장
	 *
	 * @param param
	 */
	public void insertPegMaster(Map param) {
		sqlSession.insert(NAMESPACE + "insertPegMaster", param);
	}

	/**
	 * 퍼포먼스평가그룹 Mater 수정
	 *
	 * @param param
	 */
	public void updatePegMaster(Map param) {
		sqlSession.update(NAMESPACE + "updatePegMaster", param);
	}

	/**
	 * 퍼포먼스평가그룹 - 협력사관리그룹을 사용 중인 타 퍼포먼스평가그룹 정보 조회
	 *
	 * @param param
	 */
	public Map findPegInfoInUsingVmg(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPegInfoInUsingVmg", param);
	}

	/**
	 * 퍼포먼스평가그룹 - 협력사관리그룹 저장
	 *
	 * @param param
	 */
	public void insertPegVmg(Map param) {
		sqlSession.insert(NAMESPACE + "insertPegVmg", param);
	}
	/**
	 * 퍼포먼스평가그룹 - 협력사관리그룹 삭제
	 *
	 * @param param
	 */
	public void deletePegVmg(Map param) {
		sqlSession.delete(NAMESPACE + "deletePegVmg", param);
	}
	
	/**
	 * 퍼포먼스평가그룹 - 평가등급 저장
	 *
	 * @param param
	 */
	public void insertEvalgrd(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalgrd", param);
	}
	/**
	 * 퍼포먼스평가그룹 - 평가등급 수정
	 *
	 * @param param
	 */
	public void updateEvalgrd(Map param) {
		sqlSession.update(NAMESPACE + "updateEvalgrd", param);
	}
	/**
	 * 퍼포먼스평가그룹 - 평가등급 삭제
	 *
	 * @param param
	 */
	public void deleteEvalgrd(Map param) {
		sqlSession.update(NAMESPACE + "deleteEvalgrd", param);
	}

	/**
	 * 퍼포먼스평가그룹 Mater 조회
	 *
	 * @param param
	 */
	public Map<String, Object> findPegMaster(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPegMaster", param);
	}

	/**
	 * 퍼포먼스 평가그룹 - 협력사관리그룹 조회
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findListPegVmg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPegVmg", param);
	}

	/**
	 * 퍼포먼스 평가그룹 - 평가등급 조회
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findListPegEvalGrd(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPegEvalGrd", param);
	}

}
