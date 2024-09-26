package smartsuite.app.bp.rfx.sitebriefing.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class SiteBriefingRepository {
	
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "site-briefing.";
	
	public FloaterStream findListFieldIntro(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListFieldIntro", param);
	}
	
	public Map findInfoFieldIntro(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoFieldIntro", param);
	}
	
	public List<Map> findListFiItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListFiItem", param);
	}
	
	public List<Map> findListFiVendor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListFiVendor", param);
	}
	
	public void insertFiHd(Map fiData) {
		sqlSession.insert(NAMESPACE + "insertFiHd", fiData);
	}
	
	public void updateFiHd(Map fiData) {
		sqlSession.update(NAMESPACE + "updateFiHd", fiData);
	}
	
	public void deleteFiItem(Map fiItem) {
		sqlSession.delete(NAMESPACE + "deleteFiItem", fiItem);
	}
	
	public void deleteFiVendor(Map fiVendor) {
		sqlSession.delete(NAMESPACE + "deleteFiVendor", fiVendor);
	}
	
	public void updateFiItem(Map fiItem) {
		sqlSession.update(NAMESPACE + "updateFiItem", fiItem);
	}
	
	public void updateFiVendor(Map fiVendor) {
		sqlSession.update(NAMESPACE + "updateFiVendor", fiVendor);
	}
	
	public void insertFiItem(Map fiItem) {
		sqlSession.insert(NAMESPACE + "insertFiItem", fiItem);
	}
	
	public void insertFiVendor(Map fiVendor) {
		sqlSession.insert(NAMESPACE + "insertFiVendor", fiVendor);
	}
	
	public void deleteFiHd(Map param) {
		sqlSession.delete(NAMESPACE + "deleteFiHd", param);
	}
	
	public void updateAttendConfirm(Map param) {
		sqlSession.update(NAMESPACE + "updateAttendConfirm", param);
	}
	
	public void updateVendorAttendYn(Map vendor) {
		sqlSession.update(NAMESPACE + "updateVendorAttendYn", vendor);
	}
	
	public List<Map> findListFieldIntroPopup(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListFieldIntroPopup", param);
	}
	
	public Map selectRfxFieldIntroHd(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectRfxFieldIntroHd", param);
	}
	
	public void updateRfxHdFieldInfo(Map fiData) {
		sqlSession.update(NAMESPACE + "updateRfxHdFieldInfo", fiData);
	}
	
	public List<Map> selectRfxItems(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectRfxItems", param);
	}
	
	public List<Map> selectRfxVendors(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectRfxVendors", param);
	}
	
	public void deleteFiItems(Map param) {
		sqlSession.delete(NAMESPACE + "deleteFiItems", param);
	}
	
	public void deleteFiVendors(Map param) {
		sqlSession.delete(NAMESPACE + "deleteFiVendors", param);
	}
	
	public List<TemplateMailData.Receiver> findListFiVendorReceiver(Map fiInfo) {
		return sqlSession.selectList(NAMESPACE + "findListFiVendorReceiver", fiInfo);
	}
}
