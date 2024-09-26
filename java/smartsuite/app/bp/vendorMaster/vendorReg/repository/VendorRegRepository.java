package smartsuite.app.bp.vendorMaster.vendorReg.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VendorRegRepository {
	private static final String NAMESPACE = "vendor-reg.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 중복체크.
	 *
	 * @param param
	 */
	public List checkDuplicatedVdInfo(Map param) {
		return sqlSession.selectList(NAMESPACE+"checkDuplicatedVdInfo", param);
	}

	public void insertVdInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertVdInfo", param);
	}

	public void insertUserInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertUserInfo", param);
	}

	public void insertUserAuth(Map param) {
		sqlSession.insert(NAMESPACE + "insertUserAuth", param);
	}

	public Map findDetailRegVdInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDetailRegVdInfo", param);
	}

	public List findVendorAttachmentList(Map param) {
		return sqlSession.selectList(NAMESPACE+"findVendorAttachmentList", param);
	}

	public void updateBasicInfoMaster(Map param) { sqlSession.update(NAMESPACE + "updateBasicInfoMaster",param); }

	public void insertVendorAttachmentInfo(Map param) { sqlSession.insert(NAMESPACE + "insertVendorAttachmentInfo", param); }

	public void updateVendorAttachmentInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorAttachmentInfo",param); }

	public List findVendorFinanceInfo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findVendorFinanceInfo", param);
	}

	public void insertVendorFinanceInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertVendorFinanceInfo", param);
	}

	public void updateVendorFinanceInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorFinanceInfo",param); }
}
