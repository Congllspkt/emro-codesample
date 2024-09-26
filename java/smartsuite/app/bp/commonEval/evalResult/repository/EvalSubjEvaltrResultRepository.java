package smartsuite.app.bp.commonEval.evalResult.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class EvalSubjEvaltrResultRepository {
	/** The sql session. */
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "eval-subj-evaltr-res.";

	public void insertEvalSubjEvaltrRes(Map param) {
		sqlSession.update(NAMESPACE + "insertEvalSubjEvaltrRes", param);
	}
	public void deleteEvalSubjEvaltrRes(Map param) {
		sqlSession.update(NAMESPACE + "deleteEvalSubjEvaltrRes", param);
	}
	public Map findEvalSubjEvaltrRes(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findEvalSubjEvaltrRes", param);
	}

	// 평가대상 평가자 결과 데이터의 평가자아이디 변경
	public void changeEvalSubjEvaltrRes(Map param) {
		sqlSession.update(NAMESPACE + "changeEvalSubjEvaltrRes", param);
	}

	// 평가대상 평가자 종합의견 및 종합 첨부파일 저장
	public void updateEvalSubjEvaltrFulfill(Map param) {
		sqlSession.update(NAMESPACE + "updateEvalSubjEvaltrFulfill", param);
	}

	// 평가자 평가점수를 계산 후 저장한다.
	public void updateEvalSubjEvaltrSc(Map param) {
		sqlSession.update(NAMESPACE + "updateEvalSubjEvaltrSc", param);
	}

}
