package smartsuite.app.common.glossary.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings ({ "rawtypes"})
public class GlossaryRepository {

	/** The sql session. */
	@Inject
	private SqlSession sqlSession;

	/** The namespace. */
	private static final String NAMESPACE = "glossary.";
	
	/**
	 * 용어집 정보 조회
	 * @param param
	 * @return
	 */
	public List findListGlossary(Map param) {
		return sqlSession.selectList(NAMESPACE+"findListGlossary", param);
	}
	
	/**
	 * 용어집 정보 추가
	 * @param param
	 */
	public void insertGlossary(Map<String, Object> param) {
		sqlSession.update(NAMESPACE+"insertGlossary", param);
	}
	
	/**
	 * 용어집 정보 수정
	 * @param param
	 */
	public void updateGlossary(Map<String, Object> param) {
		sqlSession.update(NAMESPACE+"updateGlossary", param);
	}
	
	/**
	 * 용어집 정보 삭제
	 * @param param
	 */
	public void deleteGlossary(Map<String, Object> param) {
		sqlSession.update(NAMESPACE+"deleteGlossary", param);
	}
}
