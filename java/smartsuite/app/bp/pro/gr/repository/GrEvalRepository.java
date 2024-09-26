package smartsuite.app.bp.pro.gr.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class GrEvalRepository {
	
	private static final String NAMESPACE = "gr-eval.";
	
	@Inject
	private SqlSession sqlSession;
	
	public Map findGrInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findGrInfo", param);
	}
	
	public Map findGrInfoByGe(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findGrInfoByGe", param);
	}
	
	public Map findGeInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findGeInfo", param);
	}
	
	public Map findGrEvalshtInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findGrEvalshtInfo", param);
	}
	
	public List findListGemt(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGemt", param);
	}
	
	public List<String> findGrPurcTypByGrUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findGrPurcTypByGrUuid", param);
	}
	
	public List<String> findGrItemCatTypByGrUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findGrItemCatTypByGrUuid", param);
	}
	
	public List<String> findGrSgByGrUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findGrSgByGrUuid", param);
	}
	
	public List<Map> findListGegByGemgValues(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGegByGemgValues", param);
	}
	
	public List<String> findVendorInfoByGrUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findVendorInfoByGrUuid", param);
	}
	
	public List findListGePrcsByGe(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGePrcsByGe", param);
	}
	
	public void deleteGePrcsByGe(Map param) {
		sqlSession.delete(NAMESPACE + "deleteGePrcsByGe", param);
	}
	
	public void deleteGePrcsEvaltrByGePrcs(Map param) {
		sqlSession.delete(NAMESPACE + "deleteGePrcsEvaltrByGePrcs", param);
	}
	
	public void insertGe(Map geData) {
		sqlSession.insert(NAMESPACE + "insertGe", geData);
	}
	
	public void updateGe(Map geData) {
		sqlSession.update(NAMESPACE + "updateGe", geData);
	}
	
	public void insertGePrcs(Map grEvalshtInfoPrcs) {
		sqlSession.insert(NAMESPACE + "insertGePrcs", grEvalshtInfoPrcs);
	}
	
	public void insertGePrcsEvaltr(Map gePrcsEvaltr) {
		sqlSession.insert(NAMESPACE + "insertGePrcsEvaltr", gePrcsEvaltr);
	}
	
	public void deleteGe(Map grData) {
		sqlSession.delete(NAMESPACE + "deleteGe", grData);
	}
	
	public List<Map> findListGePrcsEvaltr(Map gegInfo) {
		return sqlSession.selectList(NAMESPACE + "findListGePrcsEvaltr", gegInfo);
	}
	
	public void updateGrEvalStart(Map geData) {
		sqlSession.update(NAMESPACE + "updateGrEvalStart", geData);
	}
	
	public List<Map> findListGePrcsEvaltrByGePrcs(Map gePrcs) {
		return sqlSession.selectList(NAMESPACE + "findListGePrcsEvaltrByGePrcs", gePrcs);
	}
	
	public boolean isCompleteEvaltrEval(Map gePrcs) {
		return sqlSession.selectOne(NAMESPACE + "isCompleteEvaltrEval", gePrcs);
	}
	
	public void updateGePrcsEndEval(Map gePrcs) {
		sqlSession.update(NAMESPACE + "updateGePrcsEndEval", gePrcs);
	}
	
	public Map calcGrEvalPrcsScore(Map geData) {
		return sqlSession.selectOne(NAMESPACE + "calcGrEvalPrcsScore", geData);
	}
	
	public void completeGrEval(Map calcGrEvalPrcsScore) {
		sqlSession.update(NAMESPACE + "completeGrEval", calcGrEvalPrcsScore);
	}
	
	public void startGePrcsEvaltr(Map<String, Object> evalSubjEvaltrInfo) {
		sqlSession.update(NAMESPACE + "startGePrcsEvaltr", evalSubjEvaltrInfo);
	}
	
	public void startGePrcs(Map<String, Object> resultGePrcs) {
		sqlSession.update(NAMESPACE + "startGePrcs", resultGePrcs);
	}
	
	public void updateGePrcsWtg(Map<String, Object> gePrcs) {
		sqlSession.update(NAMESPACE + "updateGePrcsWtg", gePrcs);
	}
	
	public List findListGePerform(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGePerform", param);
	}
	
	public Map findGeEvalSubjectEvaltrInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findGeEvalSubjectEvaltrInfo", param);
	}
	
	public void updateEvaltrScore(Map<String, Object> evalSubjMap) {
		sqlSession.update(NAMESPACE + "updateEvaltrScore", evalSubjMap);
	}
	
	public void updateEvalTargetScore(Map<String, Object> evalSubjMap) {
		sqlSession.update(NAMESPACE + "updateEvalTargetScore", evalSubjMap);
	}
	
	public List findListMonitoringGePrcsEvaltr(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListMonitoringGePrcsEvaltr", param);
	}
	
	public void insertGrGeMapping(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertGrGeMapping", param);
	}
	
	public void deleteGrGeMapping(Map param) {
		sqlSession.delete(NAMESPACE + "deleteGrGeMapping", param);
	}
	
	public void forceCloseGePrcs(Map<String, Object> gePrcs) {
		sqlSession.update(NAMESPACE + "forceCloseGePrcs", gePrcs);
	}

	public Map findGeEvalByEvalSubjEvaltrResId(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findGeEvalByEvalSubjEvaltrResId", param);
	}
}
