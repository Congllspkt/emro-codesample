package smartsuite.app.bp.commonEval.evalResult.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EvalSubjResultRepository {
	/** The sql session. */
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "eval-subj-res.";

	public void insertEvalSubjRes(Map param) {
		sqlSession.update(NAMESPACE + "insertEvalSubjRes", param);
	}
	public void deleteEvalSubjRes(Map param) {
		sqlSession.update(NAMESPACE + "deleteEvalSubjRes", param);
	}

	public Map findEvalSubjRes(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findEvalSubjRes", param);
	}

	// 평가대상 점수 계산
	public void calculateEvalSubjResSc(Map param) {
		sqlSession.update(NAMESPACE + "calculateEvalSubjResSc", param);
	}

	// 평가대상 결과 목록
	public List findListEvalSubjRes(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListEvalSubjRes", param);
	}


}
