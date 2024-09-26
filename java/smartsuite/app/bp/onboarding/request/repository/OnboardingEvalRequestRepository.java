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
public class OnboardingEvalRequestRepository {
	private static final String NAMESPACE = "obd-req.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 온보딩평가 요청 대상인 협력사 목록 조회 (운영조직 무관)
	 *
	 * @param param
	 */
	public FloaterStream findListVendorAll(Map<String, Object> param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListVendorAll", param);
	}
	
	/**
	 * 평가요청한 온보딩평가그룹 요청 정보(=OE) 존재여부 확인
	 *
	 * @param param
	 */
	public String findRegOeExistInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRegOeExistInfo", param);
	}

	/**
	 * 온보딩평가 요청 저장
	 *
	 * @param param
	 */
	public void insertOe(Map param) {
		sqlSession.insert(NAMESPACE + "insertOe", param);
	}

	/**
	 * 온보딩평가 요청 온보딩그룹 저장
	 *
	 * @param param
	 */
	public void insertOeVmg(Map param) {
		sqlSession.insert(NAMESPACE + "insertOeVmg", param);
	}

	/**
	 * 온보딩평가 프로세스 저장(PE_PRCS)
	 *
	 * @param param
	 */
	public void insertOePrcs(Map param) {
		sqlSession.insert(NAMESPACE + "insertOePrcs", param);
	}

	/**
	 * 온보딩평가 프로세스 평가자 저장(OE_PRCS_EVALTR)
	 *
	 * @param param
	 */
	public void insertOePrcsEvaltr(Map param) {
		sqlSession.insert(NAMESPACE + "insertOePrcsEvaltr", param);
	}

	/**
	 * 온보딩평가의 전체 온보딩평가 프로세스 조회(OE_PRCS)
	 *
	 * @param param
	 */
	public List findListAllOePrcs(Map param) {
		return sqlSession.selectList(NAMESPACE+"findListAllOePrcs", param);
	}

	/**
	 * 이전 온보딩평가 프로세스 중 최종 결과 조회(Pre OE_PRCS)
	 *
	 * @param param
	 */
	public Map findPreObdEvalPrcsResult(Map param) {
		return sqlSession.selectOne(NAMESPACE+"findPreObdEvalPrcsResult", param);
	}

	/**
	 * 이전 온보딩평가 프로세스를 참조하여 현재 프로세스의 결과 적용
	 *
	 * @param param
	 */
	public void updateObdEvalPrcdResult(Map param) {
		sqlSession.update(NAMESPACE + "updateObdEvalPrcdResult", param);
	}

	/**
	 * 평가대상 온보딩평가그룹 조회
	 *
	 * @param param
	 */
	public Map findObdEvalTargObdEvalGrp(Map param) {
		return sqlSession.selectOne(NAMESPACE+"findObdEvalTargObdEvalGrp", param);
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
	 * 온보딩평가 평가 완료 - OE
	 *
	 * @param param
	 */
	public void saveOnboardingEvalComplete(Map param) {
		sqlSession.update(NAMESPACE + "saveOnboardingEvalComplete", param);
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
	 * eval 평가대상 평가자 결과 아이디를 온보딩평가대상 평가자(OE_PRCS_EVALTR) 테이블에 저장
	 *
	 * @param param
	 */
	public void updateEvalSubjEvaltrResKey(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateEvalSubjEvaltrResKey", param);
	}

	/**
	 *  OE 생성 전 평가그룹의 유효 평가시트 정보를 조회한다.
	 *
	 * @param param
	 */
	public Map findOeReqObdEvalshtInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findOeReqObdEvalshtInfo", param);
	}

	/**
	 *  프로세스 적용 대상 여부가 N 인 경우 OE 상태 변경
	 *
	 * @param param
	 */
	public void updateOeByPrcsApplSubjN(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateOeByPrcsApplSubjN", param);
	}
}
