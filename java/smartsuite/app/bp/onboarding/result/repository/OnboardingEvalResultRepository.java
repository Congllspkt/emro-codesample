package smartsuite.app.bp.onboarding.result.repository;

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
public class OnboardingEvalResultRepository {
	private static final String NAMESPACE = "obd-result.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 온보딩평가 완료 처리대기 목록 조회
	 *
	 * @param param
	 */
	public FloaterStream findListCompleteOnboardingEval(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListCompleteOnboardingEval", param);
	}

	/** 온보딩평가 모니터링 - 미등록완료 저장 (sts = 'D')
     *
     * @param param the param
     */
    public void saveListOnboardingEvalUnRegCompleteOe(Map<String, Object> param) {
        sqlSession.update(NAMESPACE+"saveListOnboardingEvalUnRegCompleteOe", param);
    }

	/**
	 * 온보딩평가 이력 목록 조회
	 *
	 * @param param
	 */
	public FloaterStream findListOnboardingEvalHistrec(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListOnboardingEvalHistrec", param);
	}

	/**
	 * 대상 온보딩평가그룹에 협력사 마스터 변경이력 UUID 저장
	 *
	 * @param param
	 */
	public void saveOnboardingEvalVendorAprvInfo(Map param) { sqlSession.update(NAMESPACE + "saveOnboardingEvalVendorAprvInfo", param); }

	/**
	 * 등록대상 협력사관리그룹에 결재대상여부 변경
	 *
	 * @param param
	 */
	public void saveOnboardingEvalVmgAprvSubj(Map param) { sqlSession.update(NAMESPACE + "saveOnboardingEvalVmgAprvSubj", param); }

	/**
	 * 온보딩평가 프로세스 처리완료
	 *
	 * @param param
	 */
	public void saveOnboardingEvalPrcsPrcsgEnd(Map param) { sqlSession.update(NAMESPACE + "saveOnboardingEvalPrcsPrcsgEnd", param); }
	
	/**
	 * 온보딩평가 요청 처리완료
	 *
	 * @param param
	 */
	public void saveOnboardingEvalPrcsgEnd(Map param) { sqlSession.update(NAMESPACE + "saveOnboardingEvalPrcsgEnd", param); }

	/**
	 * 등록대상 협력사관리그룹에 결재대상 취소
	 *
	 * @param param
	 */
	public void saveCancelOnboardingEvalVmgAprvSubj(Map param) { sqlSession.update(NAMESPACE + "saveCancelOnboardingEvalVmgAprvSubj", param); }

	/**
	 * 대상 온보딩평가그룹에 협력사 마스터 변경이력 UUID 초기화
	 *
	 * @param param
	 */
	public void saveCancelOnboardingEvalVendorAprvInfo(Map param) { sqlSession.update(NAMESPACE + "saveCancelOnboardingEvalVendorAprvInfo", param); }

	/**
	 * 협력사별 온보딩 프로세스 결과 리스트 조회
	 *
	 * @param param
	 */
	public List findVendorObdEvalInfoList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findVendorObdEvalInfoList", param);
	}

	/**
	 * 협력사-운영조직 : 협력사관리유형 정보 조회
	 *
	 * @param param {vd_cd, oorg_cd}
	 */
	public List findListVendorManagementTypeInfo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorManagementTypeInfo", param);
	}

	/**
	 * 등록 협력사관리그룹 조회
	 *
	 * @param param {vd_cd, oorg_cd}
	 */
	public List findListVendorManagementGroup(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorManagementGroup", param);
	}

	/**
	 * 협력사 운영조직별 미등록 SG 목록 조회 (단, 한 번 통과된 유효한 OEG에 속한 SG여야 한다.)
	 *
	 * @param param {vd_cd, oorg_cd}
	 * @return List
	 */
	public List<Map<String, Object>> findListUnregisteredSgByOeg(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListUnregisteredSgByOeg", param);
	}
}
