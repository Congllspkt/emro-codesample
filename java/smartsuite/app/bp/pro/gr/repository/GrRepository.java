package smartsuite.app.bp.pro.gr.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class GrRepository {
	private static final String NAMESPACE = "gr.";
	
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 검수 헤더 생성
	 *
	 * @param param
	 */
	public void insertGr(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertGr", param);
	}
	
	/**
	 * 검수 헤더 수정
	 *
	 * @param param
	 */
	public void updateGrByGrUuid(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateGrByGrUuid", param);
	}
	
	/**
	 * 검수 헤더 삭제
	 *
	 * @param param
	 */
	public void deleteGrByGrUuid(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deleteGrByGrUuid", param);
	}
	
	/**
	 * 입고 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream searchGr(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "searchGr", param);
	}
	
	/**
	 * 검수 헤더 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findGrByGrId(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findGrByGrId", param);
	}
	
	/**
	 * 기성 회차 증가
	 *
	 * @param grData
	 * @return
	 */
	public Integer findGrIncrementRevisionByPoNo(Map<String, Object> grData) {
		return sqlSession.selectOne(NAMESPACE + "findGrIncrementRevisionByPoNo", grData);
	}
	
	/**
	 * 검수헤더 진행상태 비교
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> compareGrHeaderStatus(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "compareGrHeaderStatus", param);
	}
	
	/**
	 * 검수등록 가능여부 확인
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> checkGrCreatable(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "checkGrCreatable", param);
	}
}
