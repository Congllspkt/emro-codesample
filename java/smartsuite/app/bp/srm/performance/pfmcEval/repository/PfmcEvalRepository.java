package smartsuite.app.bp.srm.performance.pfmcEval.repository;

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
public class PfmcEvalRepository {
	private static final String NAMESPACE = "pfmc-eval.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 퍼포먼스평가 수행 목록을 조회
	 *
	 * @param param
	 */
	public FloaterStream findListPerformanceEvalFulfill(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListPerformanceEvalFulfill", param);
	}

	/**
	 * 퍼포먼스평가 평가수행 정보 조회
	 *
	 * @param param
	 */
	public Map findPerformanceEvalFulfillInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE+"findPerformanceEvalFulfillInfo", param);
	}

	/**
	 * 퍼포먼스평가 평가수행 대상 정보 조회
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findPerformanceEvalSubjectList(Map param) {
		return sqlSession.selectList(NAMESPACE+"findPerformanceEvalSubjectList", param);
	}

	/**
	 * 퍼포먼스 평가시트 사용여부 체크 (PE 생성 여부)
	 *
	 * @param param
	 */
	public String findCreatePeYnByPfmcEvalsht(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCreatePeYnByPfmcEvalsht", param);
	}

	/**
	 * 퍼포먼스평가그룹 사용여부 체크 (PE 생성 여부)
	 *
	 * @param param
	 */
	public String findCreatePeYnByPeg(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCreatePeYnByPeg", param);
	}

	/**
	 * 협력사별 퍼포먼스평가 결과 리스트 조회
	 *
	 * @param param
	 */
	public List findVendorPfmcEvalInfoList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findVendorPfmcEvalInfoList", param);
	}
}
