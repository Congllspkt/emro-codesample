package smartsuite.app.bp.commonEval.common.status.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class CommonEvalStatusRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "eval-status.";

	// 평가대상 평가자 결과 신규 저장
	public void insertEvalSubjEvaltrRes(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "insertEvalSubjEvaltrRes", param);
	}

	// 정성평가 진행상태 "제출" 상태를 "저장"으로 변경
	public void updateCancleSubmEvaltrPrgsSts(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateCancleSubmEvaltrPrgsSts", param);
	}

	/**
	 * 평가자 평가항목 결과 저장 시 정성평가 진행상태를 저장으로 저장한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 13
	 * @Method Name : saveEvalSubjEvaltrFulfill
	 */
	public void saveEvalSubjEvaltrFulfill(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "saveEvalSubjEvaltrFulfill", param);
	}
}
