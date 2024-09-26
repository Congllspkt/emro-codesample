package smartsuite.app.bp.commonEval.evalResult.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class EvalResultRepository {
	private static final String NAMESPACE = "eval-result.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 평가항목 사용여부 확인
	 *
	 * @param param
	 */
	public String selectEvalFactorUsedYnInEvalResult(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectEvalFactorUsedYnInEvalResult", param);
	}

	public String selectEvalTmplUsedYnInEvalResult(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectEvalTmplUsedYnInEvalResult", param);
	}
}
