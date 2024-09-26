package smartsuite.app.bp.commonEval.evalResult.repository;


import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CalculateCalcfactRepository {
	/**
	 * The sql session.
	 */
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "calcfact-res.";

	// 평가항목 계산항목 무실적 처리
	public void updateNullValueYn(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateNullValueYn", param);
	}

	// 평가대상의 계산항목 목록을 조회한다.
	public List findListEvalSubjCalcfact(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListEvalSubjCalcfact", param);
	}

	// 집계 Procedure Parameter 목록을 조회한다.
	public List findListProcedureParameter(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListProcedureParameter", param);
	}

	// 계산항목 값 집계 프로시저 호출
	public void updateCalcfactValByProcedure(Map<String,Object> procedureMap) {
		sqlSession.selectOne(NAMESPACE + "updateCalcfactValByProcedure", procedureMap);
	}


	// 계산항목의 집계 방식이 테이블인 경우 조건 컬럼 조회
	public List findListCalcfactCondCol(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListCalcfactCondCol", param);
	}

	// 계산항목 값 집계를 테이블 기준으로 실행
	public void updateCalcfactValByTable(Map<String, Object> row) {
		sqlSession.update(NAMESPACE + "updateCalcfactValByTable", row);
	}

	// 계산항목 값 집계를 평가점수 기준으로 실행
	public void updateCalcfactValByEvalSc(Map<String, Object> row) {
		sqlSession.update(NAMESPACE + "updateCalcfactValByEvalSc", row);
	}
	// 계산항목 값 집계를 평가항목 점수 기준으로 실행
	public void updateCalcfactValByEvalfactSc(Map<String, Object> row) {
		sqlSession.update(NAMESPACE + "updateCalcfactValByEvalfactSc", row);
	}

	// 정량평가 항목의 실적항목 결과 목록을 조회한다.
	public List findListQuantEvalfactCalcfactRes(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListQuantEvalfactCalcfactRes", param);
	}

	// 정량항목의 정량 계산식 값을 저장한다.
	public void updateQuantEvalFactQuantFmlaVal(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateQuantEvalFactQuantFmlaVal", param);
	}
	// 정량항목의 정량 계산식 값 null 값 여부를 저장한다.
	public void updateQuantEvalfactQuantFmlaValNullv(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateQuantEvalfactQuantFmlaValNullv", param);
	}

	//  평가항목 스케일 구분 해당없음 : 결과값 저장
	public void updateQuantEvalfactRealApply(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateQuantEvalfactRealApply", param);
	}
	//  평가항목 스케일 구분 적용 : 이상-미만
	public void updateQuantEvalfactScaleAvdUd(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateQuantEvalfactScaleAvdUd", param);
	}

	//  평가항목 스케일 구분 적용 : 초과-이하
	public void updateQuantEvalfactScaleOvBlw(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateQuantEvalfactScaleOvBlw", param);
	}

	//  정량항목의 스케일 점수가 null값인 경우 기본점수로 업데이트 한다.
	public void updateQuantEvalfactScaleScNullvElemSc(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateQuantEvalfactScaleScNullvElemSc", param);
	}

	//  정량항목의 스케일 점수가 null값인 경우 평균 점수로 업데이트 한다.
	public void updateQuantEvalfactScaleScNullvAvgSc(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateQuantEvalfactScaleScNullvAvgSc", param);
	}

	// 정량항목의 스케일 점수가 null값인 경우 null값 스케일 대상의 점수로 업데이트 한다.
	public void updateQuantEvalfactScaleScNullvScaleSc(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateQuantEvalfactScaleScNullvScaleSc", param);
	}

	//  정량평가 항목 가중치 계산
	public void updateQuantEvalfactScWgt(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateQuantEvalfactScWgt", param);
	}

	// 계산항목 계산 필요여부를 N으로 업데이트한다
	public void updateCalcfactCalcRequiredYn(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateCalcfactCalcRequiredYn", param);
	}


}
