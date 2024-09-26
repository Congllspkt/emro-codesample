package smartsuite.app.bp.rfx.rfx.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import org.springframework.transaction.annotation.Transactional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfxEvalRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-eval.";
	
	public Map findRfxNpeFactSetup(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxNpeFactSetup", param);
	}
	
	public List<Map> findListRfxNpeFactEvaltr(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxNpeFactEvaltr", param);
	}
	
	public void deleteRfxNpeFactEvaltrByRfx(Map rfxData) {
		sqlSession.delete(NAMESPACE + "deleteRfxNpeFactEvaltrByRfx", rfxData);
	}
	
	public void clearRfxNpeFactSetupByRfx(Map rfxData) {
		sqlSession.delete(NAMESPACE + "clearRfxNpeFactSetupByRfx", rfxData);
	}
	
	public void insertRfxNpeFactSetup(Map npeFactSetup) {
		sqlSession.insert(NAMESPACE + "insertRfxNpeFactSetup", npeFactSetup);
	}
	
	public void updateRfxNpeFactSetup(Map npeFactSetup) {
		sqlSession.update(NAMESPACE + "updateRfxNpeFactSetup", npeFactSetup);
	}
	
	public void insertRfxNpeFactEvaltr(Map npeFactEvaltr) {
		sqlSession.insert(NAMESPACE + "insertRfxNpeFactEvaltr", npeFactEvaltr);
	}
	
	public void updateRfxNpeFactEvaltr(Map npeFactEvaltr) {
		sqlSession.update(NAMESPACE + "updateRfxNpeFactEvaltr", npeFactEvaltr);
	}
	
	public void deleteRfxNpeFactEvaltr(Map npeFactEvaltr) {
		sqlSession.delete(NAMESPACE + "deleteRfxNpeFactEvaltr", npeFactEvaltr);
	}
	
	public List<Map> findListEvalTmpl(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListEvalTmpl", param);
	}
	
	public List findListPreNonPriRfxDetail(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPreNonPriRfxDetail", param);
	}
	
	public void updateEvalSubjResUuid(Map<String, Object> submRfxBid) {
		sqlSession.update(NAMESPACE + "updateEvalSubjResUuid", submRfxBid);
	}
	
	public void updateEvalSubjEvaltrResUuid(Map<String, Object> rfxBidEvaltr) {
		sqlSession.update(NAMESPACE + "updateEvalSubjEvaltrResUuid", rfxBidEvaltr);
	}
	
	public List findListNpePerform(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListNpePerform", param);
	}
	
	public Map findRfxNpeEvalInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxNpeEvalInfo", param);
	}
	
	public List findNpeEvalSubjectInfo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findNpeEvalSubjectInfo", param);
	}
	
	public void updateEvaltrScore(Map<String, Object> resultData) {
		sqlSession.update(NAMESPACE + "updateEvaltrScore", resultData);
	}
	
	public void updateEvalTargetScore(Map<String, Object> resultData) {
		sqlSession.update(NAMESPACE + "updateEvalTargetScore", resultData);
	}
	
	public boolean validateNpeEvalComplete(Map<String, Object> evalSubjMap) {
		return sqlSession.selectOne(NAMESPACE + "validateNpeEvalComplete", evalSubjMap);
	}
	
	public void updateCompleteNpeEval(Map<String, Object> evalSubjMap) {
		sqlSession.update(NAMESPACE + "updateCompleteNpeEval", evalSubjMap);
	}
	
	public List findListTargetVendorByRfxUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListTargetVendorByRfxUuid", param);
	}
	
	public Map findRfx(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfx", param);
	}
	
	public void saveMappingEvaltmplUuidByNpeFact(Map param) {
		sqlSession.update(NAMESPACE + "saveMappingEvaltmplUuidByNpeFact", param);
	}
	
	public void updateConfirmRfxNpeFact(Map npeFactSetup) {
		sqlSession.update(NAMESPACE + "updateConfirmRfxNpeFact", npeFactSetup);
	}
	
	public int countRfxEvalempl(Map npeFactSetup) {
		return sqlSession.selectOne(NAMESPACE + "countRfxEvalempl", npeFactSetup);
	}
	
	public void deleteRfxNpeFactSetup(Map param) {
		sqlSession.delete(NAMESPACE + "deleteRfxNpeFactSetup", param);
	}

	public Map findNpeEvalByEvalSubjEvaltrResId(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findNpeEvalByEvalSubjEvaltrResId", param);
	}

	public List findEvalTmplStsInNpeEvalSht(Map param) {
		return sqlSession.selectList(NAMESPACE + "findEvalTmplStsInNpeEvalSht", param);
	}
}
