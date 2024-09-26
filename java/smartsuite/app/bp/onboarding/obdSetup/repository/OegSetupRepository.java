package smartsuite.app.bp.onboarding.obdSetup.repository;

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
public class OegSetupRepository {
	private static final String NAMESPACE = "oeg-setup.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 온보딩평가그룹 목록을 조회
	 *
	 * @param param
	 */
	public FloaterStream findListOeg(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListOeg", param);
	}






	/**
	 * 온보딩평가그룹 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateOegByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updateOegByDelete", param);
	}

	/**
	 * 온보딩 협력사관리그룹 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateVmgStsByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updateVmgStsByDelete", param);
	}

	/**
	 * 온보딩평가그룹 Mater 저장
	 *
	 * @param param
	 */
	public void insertOegMaster(Map param) {
		sqlSession.insert(NAMESPACE + "insertOegMaster", param);
	}

	/**
	 * 온보딩평가그룹 Mater 수정
	 *
	 * @param param
	 */
	public void updateOegMaster(Map param) {
		sqlSession.update(NAMESPACE + "updateOegMaster", param);
	}

	/**
	 * 온보딩평가그룹 - 협력사관리그룹을 사용 중인 타 온보딩평가그룹 정보 조회
	 *
	 * @param param
	 */
	public Map findOegInfoInUsingVmg(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findOegInfoInUsingVmg", param);
	}

	/**
	 * 온보딩평가그룹 - 협력사관리그룹 저장
	 *
	 * @param param
	 */
	public void insertOegVmg(Map param) {
		sqlSession.insert(NAMESPACE + "insertOegVmg", param);
	}
	/**
	 * 온보딩평가그룹 - 협력사관리그룹 삭제 (delete)
	 *
	 * @param param
	 */
	public void deleteOegVmg(Map param) {
		sqlSession.delete(NAMESPACE + "deleteOegVmg", param);
	}

	/**
	 * 온보딩평가그룹 Mater 조회
	 *
	 * @param param
	 */
	public Map<String, Object> findOegMaster(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findOegMaster", param);
	}

	/**
	 * 온보딩 평가그룹 - 협력사관리그룹 조회
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findListOegVmg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListOegVmg", param);
	}

}
