package smartsuite.app.bp.onboarding.request.repository;

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
public class OnboardingEvalMonitoringRepository {
	private static final String NAMESPACE = "obd-monitoring.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 온보딩평가 요청 목록 조회
	 *
	 * @param param
	 */
	public FloaterStream findListReqOnboardingEval(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListReqOnboardingEval", param);
	}

	/**
	 * 온보딩평가 절차 목록 조회
	 *
	 * @param param
	 */
	public List findListOnboardingEvalProcess(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListOnboardingEvalProcess", param);
	}

	/** 온보딩평가 모니터링 - 조건부합격 저장 (온보딩평가 마감 저장)
     *
     * @param param the param
     */
    public void saveListOnboardingEvalCompleteOe(Map<String, Object> param) {
        sqlSession.update(NAMESPACE+"saveListOnboardingEvalCompleteOe", param);
    }

	/** 온보딩평가 모니터링 - 조건부합격 저장 (온보딩평가 프로세스 마감 저장)
     *
     * @param param the param
     */
    public void saveListOnboardingEvalCompleteOePrcs(Map<String, Object> param) {
        sqlSession.update(NAMESPACE+"saveListOnboardingEvalCompleteOePrcs", param);
    }

	/** 온보딩평가 모니터링 - 조건부합격 저장 (OE)
     *
     * @param param the param
     */
    public void saveOnboardingEvalConditionalPass(Map<String, Object> param) {
        sqlSession.update(NAMESPACE+"saveOnboardingEvalConditionalPass", param);
    }

	/** 온보딩평가 모니터링 - 조건부합격 저장 (OE_PRCS)
	 *
	 * @param param the param
	 */
	public void saveProcessConditionalPass(Map<String, Object> param) {
		sqlSession.update(NAMESPACE+"saveProcessConditionalPass", param);
	}

	/** 온보딩평가 프로세스 정보 조회 (for Complete OnboardingEvalPrcs)
     *
     * @param param the param
     */
	public Map<String, Object> findOnboardingEvalPrcsInfo(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "findOnboardingEvalPrcsInfo", param);
	}

	/** 온보딩평가 프로세스 평가 결과 UPDATE
     *
     * @param param the param
     */
    public void updateOnboardingEvalPrcsResultSts(Map<String, Object> param) {
        sqlSession.update(NAMESPACE+"updateOnboardingEvalPrcsResultSts", param);
    }

	/** 온보딩평가 요청 결과 불합격 UPDATE
	 *
	 * @param param the param
	 */
	public void updateOnboardingEvalReqResultFail(Map<String, Object> param) {
		sqlSession.update(NAMESPACE+"updateOnboardingEvalReqResultFail", param);
	}

	/**
     * 온보딩평가 요청 목록 팝업 조회
     *
     * @param param the param
     */
    public List<Map<String, Object>> findListReqInfo(Map param) {
        return sqlSession.selectList(NAMESPACE + "findListReqInfo", param);
    }

	/**
     * 온보딩평가 평가결과상세 목록 조회
     *
     * @param param the param
     */
    public List<Map<String, Object>> findListObdEvalDetailScoreInfo(Map param) {
        return sqlSession.selectList(NAMESPACE + "findListObdEvalDetailScoreInfo", param);
    }

	/** 온보딩평가 프로세스 정보 조회
     *
     * @param param the param
     */
	public Map<String, Object> findOePrcsInfo(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "findOePrcsInfo", param);
	}

	/** 온보딩평가 프로세스 평가자 그룹 목록 조회
     *
     * @param param the param
     */
	public List<Map<String, Object>> findOePrcsInfoEvaltrGrp(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findOePrcsInfoEvaltrGrp", param);
	}

	/** 온보딩평가 프로세스 평가 담당자 목록 조회
     *
     * @param param the param
     */
	public List<Map<String, Object>> findListOePrcsEvaltr(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListOePrcsEvaltr", param);
	}

	/**
	 * 온보딩평가그룹 온보딩평가 프로세스 담당자 생성
	 *
	 * @param param
	 */
	public void insertOePrcsEvaltrForManagenent(Map param) {
		sqlSession.insert(NAMESPACE + "insertOePrcsEvaltrForManagenent", param);
	}

	/**
	 * 온보딩평가그룹 온보딩평가 프로세스 담당자 삭제
	 *
	 * @param param
	 */
	public void deleteOePrcsEvaltr(Map param) {
		sqlSession.delete(NAMESPACE + "deleteOePrcsEvaltr", param);
	}

	/** 온보딩평가 프로세스 미제출 평가 담당자 목록 조회
     *
     * @param param the param
     */
	public List<Map<String, Object>> findListEvartrOfIncompleteOePrcs(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListEvartrOfIncompleteOePrcs", param);
	}

	/**
	 * 온보딩평가 결재진행여부 조회
	 *
	 * @param param
	 */
	public Map findReqedOnboardingAprvProgYn(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findReqedOnboardingAprvProgYn", param);
	}

	/**
	 * 온보딩 평가시트 사용여부 체크 (OE 생성 여부)
	 *
	 * @param param
	 */
	public String findCreateOeYnByObdEvalsht(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCreateOeYnByObdEvalsht", param);
	}

	/**
	 * 온보딩평가그룹 사용여부 체크 (OE 생성 여부)
	 *
	 * @param param
	 */
	public String findCreateOeYnByOeg(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCreateOeYnByOeg", param);
	}
}
