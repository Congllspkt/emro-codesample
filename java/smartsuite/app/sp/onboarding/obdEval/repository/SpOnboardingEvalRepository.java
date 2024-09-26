package smartsuite.app.sp.onboarding.obdEval.repository;

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
public class SpOnboardingEvalRepository {
	private static final String NAMESPACE = "sp-obd-eval.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 운영단위별 운영조직 조회
	 *
	 * @param param
	 */
	public List findListOorgCdAll(String param) {
		return sqlSession.selectList(NAMESPACE + "findListOorgCdAll", param);
	}

	/**
	 * 온보딩평가 절차 목록 조회
	 *
	 * @param param
	 */
	public List findListOnboardingEvalProcess(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListOnboardingEvalProcess", param);
	}

	/**
	 * 온보딩평가 수행 목록 조회
	 *
	 * @param param
	 */
	public FloaterStream findListOnboardingEvalFulfill(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListOnboardingEvalFulfill", param);
	}

	/**
	 * 온보딩평가 평가수행 대상 정보 조회
	 *
	 * @param param
	 */
	public Map findOnboardingEvalSubjectInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE+"findOnboardingEvalSubjectInfo", param);
	}

	/**
	 * 온보딩평가 프로세스 평가자 점수, 상태 업데이트
	 *
	 * @param param
	 */
	public void updateObdEvalPrcsEvaltrSc(Map param) {
		sqlSession.update(NAMESPACE+"updateObdEvalPrcsEvaltrSc", param);
	}

	/**
	 * 온보딩평가 프로세스 점수, 상태 업데이트
	 *
	 * @param param
	 */
	public void updateObdEvalPrcsSc(Map param) {
		sqlSession.update(NAMESPACE+"updateObdEvalPrcsSc", param);
	}

	/**
	 * 온보딩평가 평가 완료 - OE
	 *
	 * @param param
	 */
	public void saveOnboardingEvalComplete(Map param) {
		sqlSession.update(NAMESPACE + "saveOnboardingEvalComplete", param);
	}

	/**
	 * 온보딩평가 마감 여부 조회
	 *
	 * @param param
	 */
	public String getObdEvalPrcsStatusClosedYn(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "getObdEvalPrcsStatusClosedYn", param);
	}

	/**
	 * 온보딩평가 프로세스 마감 로직 시작 시 온보딩평가 프로세스 상태를 "평가집계" 로 수정
	 *
	 * @param param
	 */
	public void startCloseObdEval(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "startCloseObdEval", param);
	}
}
