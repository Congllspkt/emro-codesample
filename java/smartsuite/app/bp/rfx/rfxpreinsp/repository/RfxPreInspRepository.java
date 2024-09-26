package smartsuite.app.bp.rfx.rfxpreinsp.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxPreInspRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "rfx-pre-insp.";
	
	public Map findRfx(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfx", param);
	}
	
	public List findListRfxPreInsp(Map param){
		return sqlSession.selectList(NAMESPACE + "findListRfxPreInsp",param);
	}
	
	public Map findRfxPreInsp(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxPreInsp",param);
	}
	
	public Map findRfxPreInspByRfxId(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxPreInspByRfxId", param);
	}

	public void updatePreInspStartDt(Map saveParam) {
		sqlSession.update(NAMESPACE + "updatePreInspStartDt", saveParam);
	}

	public void deleteRfxPreInsp(Map rfxPreInsp) {
		sqlSession.update(NAMESPACE + "deleteRfxPreInsp", rfxPreInsp);
	}

	public void insertRfxPreInsp(Map saveParam) {
		sqlSession.insert(NAMESPACE + "insertRfxPreInsp", saveParam);
	}

	public Map findNoCloseRfxPreInspByRfxId(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findNoCloseRfxPreInspByRfxId", param);
	}

	public void updateRfxPreInspEndDt(Map rfxPreInsp) {
		sqlSession.update(NAMESPACE + "updateRfxPreInspEndDt", rfxPreInsp);
	}

	public void updateCloseRfxPreInsp(Map rfxPreInsp) {
		sqlSession.update(NAMESPACE + "updateCloseRfxPreInsp", rfxPreInsp);
	}

	public List findListRfxPreInspAppAttach(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxPreInspAppAttach", param);
	}

	public List findListRfxPreInspDetailByOpen(Map rfxPreInspInfo) {
		return sqlSession.selectList(NAMESPACE + "findListRfxPreInspDetailByOpen", rfxPreInspInfo);
	}

	public List findListRfxPreInspDetail(Map rfxPreInspInfo) {
		return sqlSession.selectList(NAMESPACE + "findListRfxPreInspDetail", rfxPreInspInfo);
	}

	public Map findRfxPreInspApp(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxPreInspApp", param);
	}

	public List findListRfxPreInspAppedAttach(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxPreInspAppedAttach", param);
	}

	public void updateRfxPreInspEvalRstCd(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxPreInspEvalRstCd", param);
	}

	public void compRfxPreInsp(Map param) {
		sqlSession.update(NAMESPACE + "compRfxPreInsp", param);
	}

	public Map findVdRfxPreInspApp(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findVdRfxPreInspApp", param);
	}

	public List<Map> findListRfxPreInspQualification(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxPreInspQualification", param);
	}
}
