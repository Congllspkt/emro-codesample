package smartsuite.app.bp.srm.performance.result.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.PfmcConst;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class PerformanceEvalResultRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "pfmc-res.";


	// 퍼포먼스평가 평가대상 결과 생성을 위한 퍼포먼스 평가대상 목록 조회
	public List findListPeSubjForCreateRes(Map param) { return sqlSession.selectList(NAMESPACE + "findListPeSubjForCreateRes", param); }

	public void deletePeSubjRes(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deletePeSubjRes", param);
	}

	// 퍼포먼스평가대상 결과를 생성한다.
	public void createPeSubjResByPe(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertPeSubjResByPe", param);
	}
	// 퍼포먼스평가대상 결과를 생성한다.
	public void createPeSubjResByPeSubj(Map<String, Object> param) {

		sqlSession.insert(NAMESPACE + "insertPeSubjResByPeSubj", param);
	}

	// 퍼포먼스평가대상 결과를 조회한다.
	public List findListPeSubjRes(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjRes", param);
	}

	// eval 평가대상 결과 아이디를 퍼포먼스평가대상 결과 테이블에 저장
	public void updateEvalSubjResKey(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateEvalSubjResKey", param);
	}

	// 평가대상 평가자 결과 생성을 위해 퍼포먼스평가대상 평가자 목록 조회 (pe_subj_uuid 단위로 조회)
	public List findListPeSubjEvaltrForCreateRes(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjEvaltrForCreateRes", param);
	}

	// eval 평가대상 평가자 결과 아이디를 퍼포먼스평가대상 평가자 테이블에 저장
	public void updateEvalSubjEvaltrResKey(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateEvalSubjEvaltrResKey", param);
	}

	// 퍼포먼스 평가결과 목록 조회
	public List findListPfmcEvalResult(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPfmcEvalResult", param);
	}

	// 퍼포먼스 평가결과 조회
	public Map findPfmcEvalRes(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPfmcEvalRes", param);
	}
	// 퍼포먼스 평가결과 평가그룹 목록 조회
	public List findListPegByPe(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPegByPe", param);
	}

	// 퍼포먼스 평가결과 협력사관리그룹 목록 조회
	public List findListVmgByPe(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListVmgByPe", param);
	}

	// 퍼포먼스 평가대상 결과 목록 조회
	public List findListPeSubjResult(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjResult", param);
	}

	// 퍼포먼스 평가 종합의견 목록 조회
	public List findListPeComprehensiveOpinion(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeComprehensiveOpinion", param);
	}

	// 퍼포먼스 평가대상 평가항목 결과 목록 조회
	public List findListPeSubjEvalfactResult(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjEvalfactResult", param);
	}

	// 퍼포먼스 평가대상 평가항목 의견 목록 조회
	public List findListPeSubjEvalfactOpinion(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjEvalfactOpinion", param);
	}

	// 퍼포먼스 평가 요청된 평가그룹의 평가등급을 조회한다.
	public List findListPeEvalGrd(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeEvalGrd", param);
	}

	// 평가그룹의 평가데이타 결과 조회 (평가 연도, 등급별 수)
	public List findListPeGrdCntByPeg(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeGrdCntByPeg", param);
	}

	// 평가 생성을 위해 평가대상 결과와 평가자 정보를 조회한다.
	public List findListPeSubjResAndEvaltrForCreateRes(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjResAndEvaltrForCreateRes", param);
	}

	// 퍼포먼스 평가대상 결과 조정 정보를 조회한다.
	public Map findPeSubjResAdjInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPeSubjResAdjInfo", param);
	}

	// 퍼포먼스 평가대상 결과 조정 정보를 저장한다.
	public void updatePeSubjResAdjInfo(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updatePeSubjResAdjInfo", param);
	}

	// 퍼포먼스 평가대상의 결과의 순위를 계산한다.
	public void calculatePeSubjResRank(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "calculatePeSubjResRank", param);
	}
	
	// 퍼포먼스 평가대상 결과의 등급을 계산한다.
	public void calculatePeSubjResGrade(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "calculatePeSubjResGrade", param);
	}

	// 퍼포먼스 평가 마감 가능 여부를 조회한다.
	public Map checkClosablePfmcEval(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "checkClosablePfmcEval", param);
	}
	// 퍼포먼스 평가 마감 여부를 조회한다.
	public String getPfmcEvalStatusClosedYn(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "getPfmcEvalStatusClosedYn", param);
	}

	// 평가공통의 평가대상 계산항목 결과를 가져와 퍼포먼스 평가대상 계산항목 결과에 저장한다.
	public void mergePeSubjCalcfactRes(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "mergePeSubjCalcfactRes", param);
	}

	// 평가공통의 평가대상 결과를 가져와 퍼포먼스 평가대상 결과에 저장한다.
	public void updatePeSubjResEvalSc(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updatePeSubjResEvalSc", param);
	}

	// 평가요청의 정량항목 목록을 조회한다.
	public List findListPeQuantEvalfactResult(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeQuantEvalfactResult", param);
	}

	// 퍼포먼스 평가 결과 > 평가 항목별 점수 팝업
	public List findListPfmcEvalDetailScoreInfo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPfmcEvalDetailScoreInfo", param);
	}

	// 퍼포먼스 평가 결과 > 평가 항목별 점수 팝업 내 이전 평가결과 리스트(차트) 조회
	public List findListEvalSubjPrePfmcResult(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListEvalSubjPrePfmcResult", param);
	}

	// 퍼포먼스 평가 결과 > 평가 항목별 점수 팝업 내 평가항목 별 이전 평가결과 리스트(차트) 조회
	public List findListPreFactScoreInfo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPreFactScoreInfo", param);
	}

	// 퍼포먼스 평가 대상 정량항목의 계산항목 결과를 조회한다.
	public List findListPeSubjCalcfactResult(Map<String,Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjCalcfactResult", param);
	}

	// 퍼포먼스 평가 대상 조정 계산항목을 조회한다.
	public List findListAdjCalcfactByFact(Map<String,Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListAdjCalcfactByFact", param);
	}

	// 퍼포먼스 평가 결과 > 평가에 포함된 모든 계산항목 목록 조회
	public List findListCalcfactByPeUsed(Map<String,Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListCalcfactByPeUsed", param);
	}

	// 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 조정 정보 조회
	public Map findPeSubjCalcfactAdj(Map<String,Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPeSubjCalcfactAdj", param);
	}

	// 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 조정 정보 조회
	public String selectPfmcEvalResultAdjustableYn(Map<String,Object> param) {
		return sqlSession.selectOne(NAMESPACE + "selectPfmcEvalResultAdjustableYn", param);
	}

	// 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 조정 정보 조회
	public void updatePeSubjCalcfactAdj(Map<String,Object> param) {
		sqlSession.update(NAMESPACE + "updatePeSubjCalcfactAdj", param);
	}

	// 재집계 제외 계산항목 목록 조회
	public List findListDatCollXceptCalcfact(Map<String,Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListDatCollXceptCalcfact", param);
	}

	// 재집계 제외 계산항목 목록 조회
	public void updatePeSubjCalcfactCalcReqdYn(Map<String,Object> param) {
		sqlSession.update(NAMESPACE + "updatePeSubjCalcfactCalcReqdYn", param);
	}

	// 평가 수행 메일 발송할 평가대상 평가자 목록 조회
	public List findListPeSubjEvaltrForMail(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjEvaltrForMail", param);
	}

}
