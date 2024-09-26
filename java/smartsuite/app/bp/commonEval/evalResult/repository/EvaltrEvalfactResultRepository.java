package smartsuite.app.bp.commonEval.evalResult.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EvaltrEvalfactResultRepository {

	/** The sql session. */
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "evaltr-evalfact-res.";

	public void deleteEvaltrEvalfactRes(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvaltrEvalfactRes", param);
	}

	/**
	 * 평가항목 찾기 목록 조회
	 *
	 * @param param
	 */
	public List findEvalfactListForSearch(Map param) {
		return sqlSession.selectList(NAMESPACE + "findEvalfactListForSearch", param);
	}

	/**
	 * 평가 수행용 평가템플릿 목록 조회
	 *
	 * @param param
	 */
	public List findEvaltemplateListForFulfill(Map param) {
		return sqlSession.selectList(NAMESPACE + "findEvaltemplateListForFulfill", param);
	}

	/**
	 * 평가 수행용 평가항목 목록 조회
	 *
	 * @param param
	 */
	public List findEvalfactListForFulfill(Map param) {
		return sqlSession.selectList(NAMESPACE + "findEvalfactListForFulfill", param);
	}

	/**
	 * 평가 수행용 평가항목 - 스케일 목록 조회
	 *
	 * @param param
	 */
	public List findEvalfactScaleList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findEvalfactScaleList", param);
	}

	/**
	 * 정량 평가항목 계산값 조회
	 *
	 * @param param
	 */
	public List findListQuantitativeEvalfactResult(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListQuantitativeEvalfactResult", param);
	}

	/**
	 * 평가요청 아이디로 평가항목 평가자 결과 전체 삭제
	 *
	 * @param param
	 */
	public void deleteEvaltrEvalfactResByEvalReqUuid(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvaltrEvalfactResByEvalReqUuid", param);
	}

	/**
	 * 평가요청 아이디로 평가항목 평가자 결과 전체 삭제
	 *
	 * @param param
	 */
	public void insertEvaltrEvalfactScaleFulfill(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvaltrEvalfactScaleFulfill", param);
	}

	/**
	 * 평가자별 평가항목별 정성평가 점수를 조회한다.
	 *
	 * @param param
	 */
	public List findListEvaltrEvalfactResSc(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListEvaltrEvalfactResSc", param);
	}
}
