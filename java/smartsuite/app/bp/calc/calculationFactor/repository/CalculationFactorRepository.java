package smartsuite.app.bp.calc.calculationFactor.repository;

import org.apache.ibatis.session.SqlSession;
import org.apache.poi.ss.formula.functions.Na;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CalculationFactorRepository {

	private static final String NAMESPACE = "calc-factor.";

	@Inject
	private SqlSession sqlSession;

	public FloaterStream findListCalcFactor(Map param) {
		return new QueryFloaterStream(sqlSession,NAMESPACE + "findListCalcFactor", param);
	}

	public void insertCalcFactor(Map<String, Object> saveParam) {
		sqlSession.insert("insertCalcFactor", saveParam);
	}

	public void updateCalcFactor(Map<String, Object> saveParam) {
		sqlSession.update("updateCalcFactor", saveParam);
	}

	public Map findCalcFactorInfo(Map param) {
		return sqlSession.selectOne("findCalcFactorInfo", param);
	}

	public void deleteListCalcFactor(Map<String, Object> param) {
		sqlSession.update("deleteListCalcFactor", param);
	}

	public FloaterStream findListProcedureCode(Map param) {
		return new QueryFloaterStream(sqlSession,NAMESPACE + "findListProcedureCode", param);
	}

	public void insertListProcedureCode(Map<String, Object> saveParam) {
		sqlSession.insert(NAMESPACE + "insertListProcedureCode", saveParam);
	}

	public void updateListProcedureCode(Map<String, Object> saveParam) {
		sqlSession.update(NAMESPACE + "updateListProcedureCode", saveParam);
	}

	public void deleteListProcedureCode(Map<String, Object> param) {
		sqlSession.update("deleteListProcedureCode", param);
	}

	public FloaterStream findListParameterCode(Map param) {
		return new QueryFloaterStream(sqlSession,NAMESPACE + "findListParameterCode", param);
	}

	public void insertListParameterCode(Map<String, Object> saveParam) {
		sqlSession.insert(NAMESPACE + "insertListParameterCode", saveParam);
	}

	public void updateListParameterCode(Map<String, Object> saveParam) {
		sqlSession.update(NAMESPACE + "updateListParameterCode", saveParam);
	}

	public void insertListProcedureParameter(Map<String, Object> saveParam) {
		sqlSession.insert(NAMESPACE + "insertListProcedureParameter", saveParam);
	}

	public void deleteListProcedureParameterByPrcrCd(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteListProcedureParameterByPrcrCd", param);
	}

	public void deleteListParameterCode(Map<String, Object> param) {
		sqlSession.update("deleteListParameterCode", param);
	}

	public void deleteListProcedureParameterByParmCd(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteListProcedureParameterByParmCd", param);
	}

	public Map checkDuplicatedProcedureName(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "checkDuplicatedProcedureName", param);
	}

	public Map checkDuplicatedParameterName(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkDuplicatedParameterName", param);
	}

	public List findListDbTable(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListDbTable", param);
	}

	public List findListDbColumn(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListDbColumn", param);
	}

	public List findListCalcCndColumn(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCalcCndColumn", param);
	}

	public void insertDbTblInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertDbTblInfo", param);
	}

	public void updateDbTblInfo(Map param) {
		sqlSession.update(NAMESPACE + "updateDbTblInfo", param);
	}

	public List<Map<String, Object>> findListDbTblColumn(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListDbTblColumn", param);
	}

	public List<Map<String, Object>> findListDbTblCondition(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListDbTblCondition", param);
	}

	public void insertDbColInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertDbColInfo", param);
	}

	public void updateDbColInfo(Map param) {
		sqlSession.update(NAMESPACE + "updateDbColInfo", param);
	}

	public void deleteListCndColumn(Map param) {
		sqlSession.delete(NAMESPACE + "deleteListCndColumn", param);
	}

	public void insertCndColumn(Map param) {
		sqlSession.insert(NAMESPACE + "insertCndColumn", param);
	}
}
