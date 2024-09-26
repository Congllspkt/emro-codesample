package smartsuite.app.bp.eform.seal.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 간편서명 관련 처리하는 레파지토리 Class입니다.
 *
 * @FileName EFormSealRepository.java
 */
@Service
@Transactional
@SuppressWarnings({ "rawtypes" })
public class EFormSealRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "seal.";

	/**
	 * 인장 목록 조회
	 * 
	 * @param param
	 * @return
	 */
	public List findListSeal(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSeal", param);
	}

	/**
	 * 인장 단건 조회
	 * 
	 * @param param
	 * @return
	 */
	public Map findSeal(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSeal", param);
	}

	/**
	 * 인장 신규 생성
	 * 
	 * @param param
	 */
	public void insertSeal(Map param) {
		sqlSession.insert(NAMESPACE + "insertSeal", param);
	}

	/**
	 * 인장 수정
	 * 
	 * @param param
	 */
	public void updateSeal(Map param) {
		sqlSession.update(NAMESPACE + "updateSeal", param);
	}

	/**
	 * 인장 삭제
	 * 
	 * @param param
	 */
	public void deleteSeal(Map param) {
		sqlSession.delete(NAMESPACE + "deleteSeal", param);
	}

	/**
	 * 회사 등록 인장 리스트
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findCompanySealList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findCompanySealList", param);
	}

	/**
	 * 유저 등록 인장 리스트
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findUserSealList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findUserSealList", param);
	}

}