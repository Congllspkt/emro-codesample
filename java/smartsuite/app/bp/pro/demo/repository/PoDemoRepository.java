package smartsuite.app.bp.pro.demo.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class PoDemoRepository {
	
	/** The sql session. */
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "po-demo.";
	
	public FloaterStream findListPoDemo(Map<String, Object> param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListPoHeaderDemo", param);
	}

	public void saveInfoPoIfSts(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "saveInfoPoIfSts", param);
	}

	public Map findInfoIfPoDemo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoIfPoDemo", param);
	}

	public Map findInfoIfPoHeaderDemo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoIfPoHeaderDemo", param);
	}

	public List findInfoIfPoItemDemo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findInfoIfPoItemDemo", param);
	}

	public void updatePoIfError(Map param) {
		sqlSession.update(NAMESPACE + "updatePoIfError", param);
	}

	public void updateCntrIfError(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrIfError", param);
	}

	/**
	 * 문서 출력물을 위한 poHeader정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findInfoDocumentOutputPoHeader(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE+"findInfoDocumentOutputPoHeader", param);
	}

	/**
	 * 문서 출력물을 위한 poDetail정보 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListDocumentOutputPoDetail(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE+"findListDocumentOutputPoDetail", param);
	}
}
