package smartsuite.app.bp.commonEval.evalSetup.repository;

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
public class EvalFactorRepository {
	private static final String NAMESPACE = "eval-factor.";

	@Inject
	private SqlSession sqlSession;

	/**
	 * 평가항목 목록을 조회
	 *
	 * @param param
	 */
	public FloaterStream findListEvalFactor(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListEvalFactor", param);
	}

	/**
	 * 평가항목 확정여부 조회
	 *
	 * @param param
	 * @return
	 */
	public String selectEvalFactorCnfdYnForDelete(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectEvalFactorCnfdYnForDelete", param);
	}

	/**
	 * 평가 항목에 매핑된 모든 스케일 삭제
	 *
	 * @param param
	 */
	public void deleteEvalfactScale(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvalfactScale", param);
	}

	/**
	 * 평가항목에 매핑된 모든 계산항목 삭제
	 *
	 * @param param
	 */
	public void deleteEvalFactCalcFact(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvalFactCalcFact", param);
	}

	/**
	 * 평가항목 평가 업무 유형 삭제
	 *
	 * @param param
	 */
	public void deleteEvalFactEvalTask(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvalFactEvalTask", param);
	}

	/**
	 * 평가항목 삭제
	 *
	 * @param param
	 */
	public void deleteEvalFactor(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvalFactor", param);
	}

	/**
	 * 평가항목 정보 조회
	 *
	 * @param param
	 */
	public Map findEvalFactor(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findEvalFactor", param);
	}

	/**
	 *
	 *
	 * @param param
	 */
	public List findListEvalFactRecFact(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListEvalFactRecFact", param);
	}

	/**
	 * 평가항목 스케일 조회
	 *
	 * @param param
	 */
	public List findListEvalfactScale(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListEvalfactScale", param);
	}
	/**
	 * 평가항목 복사
	 *
	 * @param param
	 */
	public void insertEvalFactorCopy(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalFactorCopy", param);
	}
	
	/**
	 * 평가항목 계산항목 복사
	 *
	 * @param param
	 */
	public void insertEvalFactCalcFactCopy(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalFactCalcFactCopy", param);
	}

	/**
	 * 평가항목 업무유형 복사
	 *
	 * @param param
	 */
	public void insertEvalTaskTypCcdCopy(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalTaskTypCcdCopy", param);
	}

	/**
	 * 평가항목에 매핑된 평가항목 스케일목록 조회
	 *
	 * @param param
	 */
	public List  selectEvalfactScaleByEvalfactCopy(Map param) {
		return sqlSession.selectList(NAMESPACE + " selectEvalfactScaleByEvalfactCopy", param);
	}

	/**
	 * 평가항목 스케일 저장
	 *
	 * @param param
	 */
	public void insertEvalfactScale(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalfactScale", param);
	}

	public void updateCnfdYnEvalFactor(Map param) {
		sqlSession.update(NAMESPACE + "updateCnfdYnEvalFactor", param);
	}

	/**
	 * 평가항목 저장
	 *
	 * @param param
	 */
	public void insertEvalFactor(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalFactor", param);
	}
	
	/**
	 * 평가항목 운영조직 저장
	 *
	 * @param param
	 */
	public void insertEvalFactorOorg(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalFactorOorg", param);
	}

	/**
	 * 평가업무 유형 공통코드 저장
	 *
	 * @param param
	 */
	public void insertEvalTaskTypCcd(Map param) {
		sqlSession.insert(NAMESPACE + "insertEvalTaskTypCcd", param);
	}

	/**
	 * 평가항목 정보 수정
	 *
	 * @param param
	 */
	public void updateEvalFactor(Map param) {
		sqlSession.update(NAMESPACE + "updateEvalFactor", param);
	}
	/**
	 * 계산식에 포함된 실적항목 저장
	 *
	 * @param param
	 */
	public void mergeEvalFactRecFact(Map param) {
		sqlSession.insert(NAMESPACE + "mergeEvalFactRecFact", param);
	}

	/**
	 * 평가항목군 조회
	 *
	 * @param param
	 */
	public List findEvalFactorGrp(Map param) {
		return sqlSession.selectList(NAMESPACE + "findEvalFactorGrp", param);
	}
	
	/**
	 * 평가항목군 저장
	 *
	 * @param param
	 */
	public void insertEfactg(Map param) {
		sqlSession.insert(NAMESPACE + "insertEfactg", param);
	}

	/**
	 * 평가항목군 수정
	 *
	 * @param param
	 */
	public void updateEfactg(Map param) {
		sqlSession.insert(NAMESPACE + "updateEfactg", param);
	}

	/**
	 * 평가항목군 명 중복 조회
	 *
	 * @param param
	 */
	public String checkEvalFactorGrpNm(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkEvalFactorGrpNm", param);
	}
	/**
	 * 평가항목군 삭제
	 *
	 * @param param
	 */
	public void deleteEvalFactorGrp(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvalFactorGrp", param);
	}

	/**
	 * 적용 평가업무 조회
	 *
	 * @param param
	 */
	public List findListApplyEvalTask(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListApplyEvalTask", param);
	}

	/**
	 * 평가항목이 적용된 평가 업무, 평가템플릿, 평가항목군 조회
	 *
	 * @param param
	 */
	public List findEvalFactorApplyInfo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findEvalFactorApplyInfo", param);
	}

	/**
	 * 평가항목 평가 업무 유형 삭제
	 *
	 * @param param
	 */
	public void deleteEvalTaskByEvalFact(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEvalTaskByEvalFact", param);
	}
}
