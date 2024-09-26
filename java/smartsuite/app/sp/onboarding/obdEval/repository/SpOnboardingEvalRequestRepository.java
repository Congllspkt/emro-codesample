package smartsuite.app.sp.onboarding.obdEval.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpOnboardingEvalRequestRepository {
	private static final String NAMESPACE = "sp-obd-req.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 불합격 또는 진행중인 평가 프로세스 조회
	 *
	 * @param param
	 */
	public Map findActOrFailOnboardingEvalProcessCnt(Map param) {
		return sqlSession.selectOne(NAMESPACE+"findActOrFailOnboardingEvalProcessCnt", param);
	}
	
	/**
	 * 프로세스 대기 중인 다음 온보딩 평가 프로세스 절차 UUID 조회
	 *
	 * @param param
	 */
	public String findNextOnboardingEvalProcessPrcsUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE+"findNextOnboardingEvalProcessPrcsUuid", param);
	}

	/**
	 * 온보딩 평가 프로세스 정보 조회
	 *
	 * @param param
	 */
	public Map findNextOnboardingEvalProcess(Map param) {
		return sqlSession.selectOne(NAMESPACE+"findNextOnboardingEvalProcess", param);
	}

	/**
	 * eval 평가대상 결과 아이디를 온보딩평가대상(OE_PRCS) 테이블에 저장
	 *
	 * @param param
	 */
	public void updateEvalSubjResKey(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateEvalSubjResKey", param);
	}

	/**
	 * eval 평가대상 평가자 결과 아이디를 온보딩평가대상 평가자(OE_PRCS_EVALTR) 테이블에 저장
	 *
	 * @param param
	 */
	public void updateEvalSubjEvaltrResKey(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateEvalSubjEvaltrResKey", param);
	}

	/**
	 * 온보딩 프로세스 평가대상 평가자 목록 조회 (oe_prcs_uuid 단위로 조회)
	 *
	 * @param param
	 */
	public List findListOePrcsEvalSubjEvaltrByOePrcsEvalSubjRes(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListOePrcsEvalSubjEvaltrByOePrcsEvalSubjRes", param);
	}

	/**
	 * 온보딩평가 절차 목록 조회
	 *
	 * @param param
	 */
	public List findListOnboardingEvalProcess(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListOnboardingEvalProcess", param);
	}

	/** 온보딩평가 모니터링 - 조건부합격 저장 (온보딩평가 프로세스 마감 저장)
     *
     * @param param the param
     */
    public void saveListOnboardingEvalCompleteOePrcs(Map<String, Object> param) {
        sqlSession.update(NAMESPACE+"saveListOnboardingEvalCompleteOePrcs", param);
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
}
