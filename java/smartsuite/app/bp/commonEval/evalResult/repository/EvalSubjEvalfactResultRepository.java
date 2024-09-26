package smartsuite.app.bp.commonEval.evalResult.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EvalSubjEvalfactResultRepository {
	/** The sql session. */
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "eval-subj-evalfact-res.";

	/**
	 * 평가항목군 결과를 생성한다.
	 * @param param(Map param)
	 */
	public void insertEvalSubjEfactgRes(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalSubjEfactgRes", param);
	}

	/**
	 * 평가항목 결과를 생성한다.
	 * @param param
	 */
	public void insertEvalSubjEvalfactRes(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalSubjEvalfactRes", param);
	}

	/**
	 * 평가대상 계산항목 결과를 생성한다.
	 * @param param
	 */
	public void insertEvalCalcfactRes(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalCalcfactRes", param);
	}
	public void deleteEvalSubjEfactgRes(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvalSubjEfactgRes", param);
	}
	public void deleteEvalSubjEvalfactRes(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvalSubjEvalfactRes", param);
	}

	public void deleteEvalCalcfactRes(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvalCalcfactRes", param);
	}

	// 평가대상의 평가항목별 정성 점수를 계산한다.
	public void calculateQualiEvalSubjEvalfactSc(Map param) {
		sqlSession.update(NAMESPACE + "calculateQualiEvalSubjEvalfactSc", param);
	}

	// 평가대상의 평가항목군별로 점수를 계산한다.
	public void calculateEvalSubjEfactgSc(Map param) {
		sqlSession.update(NAMESPACE + "calculateEvalSubjEfactgSc", param);
	}

	// 정량 / 정성 평가 여부를 반환한다.
	public String getEvalfactTypEvalfactExistYn(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "getEvalfactTypEvalfactExistYn", param);
	}

	// 평가요청의 평가대상 평가항목 결과를 조회한다.
	public List findListEvalSubjEvalfactRes(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListEvalSubjEvalfactRes", param);
	}

	// 정량항목 결과를 담당자별 평가점수에 반영한다.
	public void updateEvaltrScByQuantEvalfactSc(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateEvaltrScByQuantEvalfactSc", param);
	}

	// 평가요청 평가대상의 계산항목 목록을 조회한다.
	public List findListEvalSubjCalcfactRes(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListEvalSubjCalcfactRes", param);
	}

	// 계산항목 값을 조정한다.
	public void updateEvalSubjCalcfactByAdj(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateEvalSubjCalcfactByAdj", param);
	}
}
