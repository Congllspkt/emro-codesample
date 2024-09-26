package smartsuite.app.sp.rfx.rfxpreinsp.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class SpRfxPreInspRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-rfx-pre-insp.";

	public List findListRfxPreInsp(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxPreInsp", param);
	}

	public Map findRfxPreInspApp(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxPreInspApp", param);
	}

	public Map findInfoVendor(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoVendor", param);
	}

	public Map findRfxData(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxData", param);
	}

	public List findListRfxItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxItem", param);
	}

	public List findListRfxPreInspAppAttach(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxPreInspAppAttach", param);
	}

	public boolean isExistRfxPreInspAppAttach(Map param) {
		return sqlSession.selectOne(NAMESPACE + "isExistRfxPreInspAppAttach", param);
	}

	public List findListRfxPreInspAppedAttach(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxPreInspAppedAttach", param);
	}

	public boolean validRfxPreInspNoClose(Map param) {
		return sqlSession.selectOne(NAMESPACE + "validRfxPreInspNoClose", param);
	}

	public Map checkRfxHdStsP(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkRfxHdStsP", param);
	}

	public void insertRfxPreInspApp(Map rfxPreInspAppData) {
		sqlSession.insert(NAMESPACE + "insertRfxPreInspApp", rfxPreInspAppData);
	}

	public void updateRfxPreInspApp(Map rfxPreInspAppData) {
		sqlSession.update(NAMESPACE + "updateRfxPreInspApp", rfxPreInspAppData);
	}

	public void insertRfxPreInspAppAttach(Map attach) {
		sqlSession.insert(NAMESPACE + "insertRfxPreInspAppAttach", attach);
	}

	public void updateRfxPreInspAppAttach(Map attach) {
		sqlSession.update(NAMESPACE + "updateRfxPreInspAppAttach", attach);
	}

	public Map findRfxPurcChrMailInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxPurcChrMailInfo", param);
	}

	public void deleteRfxPreInspAttach(Map rfxPreInspAppInfo) {
		sqlSession.update(NAMESPACE + "deleteRfxPreInspAttach", rfxPreInspAppInfo);
	}

	public void deleteRfxPreInspApp(Map rfxPreInspAppInfo) {
		sqlSession.update(NAMESPACE + "deleteRfxPreInspApp", rfxPreInspAppInfo);
	}

	public Map validRfxPreInspCloseStsByRfxId(Map param) {
		return sqlSession.selectOne(NAMESPACE + "validRfxPreInspCloseStsByRfxId", param);
	}

	public Map checkValidRfxPreInspApp(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkValidRfxPreInspApp", param);
	}
}
