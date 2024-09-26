package smartsuite.app.sp.srm.eval.result.appeal.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpAppealRepository {

	private static final String NAMESPACE = "sp-appeal.";

	@Inject
	private SqlSession sqlSession;

	public List findAppealList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findAppealList", param);
	}

	public List findListPeSubjQualifactorResult(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjQualifactorResult", param);
	}

	public List<Map<String, Object>> findListPeSubjCalcfactorResult(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjCalcfactorResult", param);
	}

	public void insertAppealQualiEvalfact(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertAppealQualiEvalfact", param);
	}

	public void updateAppealQualiEvalfact(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "updateAppealQualiEvalfact", param);
	}

	public List findAppealSubmList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findAppealSubmList", param);
	}

	public void cancelAppealQuantList(Map param) {
		sqlSession.update(NAMESPACE + "cancelAppealQuantList", param);
	}

	public void cancelAppealQualiList(Map param) {
		sqlSession.update(NAMESPACE + "cancelAppealQualiList", param);
	}

	public List findListVmg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVmg", param);
	}

	public void insertAppealCalcfact(Map param) {
		sqlSession.insert(NAMESPACE + "insertAppealCalcfact", param);
	}

	public void updateAppealCalcfact(Map param) {
		sqlSession.update(NAMESPACE + "updateAppealCalcfact", param);
	}

	public Map findAppealQualiDetail(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAppealQualiDetail", param);
	}

	public Map findAppealCalcDetail(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAppealCalcDetail", param);
	}

	public void deleteAppealCalcfact(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAppealCalcfact", param);
	}

	public void deleteAppealQualiEvalfact(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAppealQualiEvalfact", param);
	}

	public List findListPegByPe(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPegByPe", param);
	}
}
