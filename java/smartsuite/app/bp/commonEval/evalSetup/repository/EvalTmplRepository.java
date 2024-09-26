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
public class EvalTmplRepository {
	private static final String NAMESPACE = "eval-tmpl.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 평가항목 사용여부 확인
	 *
	 * @param param
	 */
	public String selectEvalFactUsedYnInEvalTmpl(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectEvalFactUsedYnInEvalTmpl", param);
	}

	/**
	 * 평가 템플릿 리스트 조회
	 *
	 * @param param
	 */
	public FloaterStream findListEvalTmpl(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListEvalTmpl", param);
	}
	public Map findEvalTmplInfo(Map param){
		return sqlSession.selectOne(NAMESPACE + "findEvalTmplInfo", param);
	}

	public List findListEvalTmplEfactgList(Map param){
		return sqlSession.selectList(NAMESPACE + "findListEvalTmplEfactgList", param);
	}

	public List findListEvalTmplFactor(Map param){
		return sqlSession.selectList(NAMESPACE + "findListEvalTmplFactor", param);
	}
	public List findListEvalTmplEvalFactScale(Map param){
		return sqlSession.selectList(NAMESPACE + "findListEvalTmplEvalFactScale", param);
	}

	public void insertEvalTmpl(Map param){
		sqlSession.insert(NAMESPACE + "insertEvalTmpl", param);
	}
	public void updateEvalTmpl(Map param){
		sqlSession.update(NAMESPACE + "updateEvalTmpl", param);
	}

	public void deleteEvalTmpl(Map param){
		sqlSession.delete(NAMESPACE + "deleteEvalTmpl", param);
	}

	public void deleteEvalTmplEvalFactorScale(Map param){
		sqlSession.delete(NAMESPACE + "deleteEvalTmplEvalFactorScale", param);
	}

	public void deleteEvalTmplEvalFactor(Map param){
		sqlSession.delete(NAMESPACE + "deleteEvalTmplEvalFactor", param);
	}
	public void deleteEvalTmplEfactg(Map param){
		sqlSession.delete(NAMESPACE + "deleteEvalTmplEfactg", param);
	}
	public void insertEvalTmplEfactg(Map param){
		sqlSession.insert(NAMESPACE + "insertEvalTmplEfactg", param);
	}
	public void insertEvalTmplEvalfact(Map param){
		sqlSession.insert(NAMESPACE + "insertEvalTmplEvalfact", param);
	}
	public void insertEvalTmplEvalfactScale(Map param){
		sqlSession.insert(NAMESPACE + "insertEvalTmplEvalfactScale", param);
	}
	public void updateEvalTmplEfactg(Map param){
		sqlSession.update(NAMESPACE + "updateEvalTmplEfactg", param);
	}
	public void updateEvalTmplEvalfact(Map param){
		sqlSession.update(NAMESPACE + "updateEvalTmplEvalfact", param);
	}
	public void updateEvalTmplEvalfactScale(Map param){
		sqlSession.update(NAMESPACE + "updateEvalTmplEvalfactScale", param);
	}

	public void insertEvalTmplCopy(Map param){
		sqlSession.insert(NAMESPACE + "insertEvalTmplCopy", param);
	}

	public void insertEvalTmplEfactgCopy(Map param){
		sqlSession.insert(NAMESPACE + "insertEvalTmplEfactgCopy", param);
	}

	public void insertEvalTmplEvalfactCopy(Map param){
		sqlSession.insert(NAMESPACE + "insertEvalTmplEvalfactCopy", param);
	}

	public void insertEvalTmplEvalfactScaleCopy(Map param){
		sqlSession.insert(NAMESPACE + "insertEvalTmplEvalfactScaleCopy", param);
	}
	public void deleteEvalTmplEvalFactorByEfactg(Map param){
		sqlSession.delete(NAMESPACE + "deleteEvalTmplEvalFactorByEfactg", param);
	}
	public void deleteEvalTmplEfactgByEfactg(Map param){
		sqlSession.delete(NAMESPACE + "deleteEvalTmplEfactgByEfactg", param);
	}

	public void deleteEvalTmplEvalFactorByEvalFact(Map param){
		sqlSession.delete(NAMESPACE + "deleteEvalTmplEvalFactorByEvalFact", param);
	}
	public String selectEfactgUsedYnInEvalTmpl(Map param){
		return sqlSession.selectOne(NAMESPACE + "selectEfactgUsedYnInEvalTmpl", param);
	}

	public void saveEvalTmplEvalFactScale(Map param){
		sqlSession.update(NAMESPACE + "saveEvalTmplEvalFactScale", param);
	}

	public void updateCnfdYnEvalTmpl(Map param){
		sqlSession.update(NAMESPACE + "updateCnfdYnEvalTmpl", param);
	}

	public void updateEvalTmplEvalFactorScaleStsByDelete(Map param){
		sqlSession.update(NAMESPACE + "updateEvalTmplEvalFactorScaleStsByDelete", param);
	}
	public void updateEvalTmplEvalFactorStsByDelete(Map param){
		sqlSession.update(NAMESPACE + "updateEvalTmplEvalFactorStsByDelete", param);
	}
	public void updateEvalTmplEfactgStsByDelete(Map param){
		sqlSession.update(NAMESPACE + "updateEvalTmplEfactgStsByDelete", param);
	}
	public void updateEvalTmplStsByDelete(Map param){
		sqlSession.update(NAMESPACE + "updateEvalTmplStsByDelete", param);
	}
}
