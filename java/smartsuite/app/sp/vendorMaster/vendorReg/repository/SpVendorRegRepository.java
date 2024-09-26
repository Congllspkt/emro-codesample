package smartsuite.app.sp.vendorMaster.vendorReg.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpVendorRegRepository {
	private static final String NAMESPACE = "sp-vendor-reg.";
	
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 중복체크
	 *
	 * @param param
	 */
	public List checkDuplicatedVdInfo(Map param) {
		return sqlSession.selectList(NAMESPACE + "checkDuplicatedVdInfo", param);
	}
	
	/**
	 * 약관 정보 조회(현재일자에 유효한 약관)
	 *
	 * @param param
	 */
	public List findTermsList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findTermsList", param);
	}

	public void insertVdInfo(Map param) { sqlSession.insert(NAMESPACE + "insertVdInfo", param); }

	public void insertUserInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertUserInfo", param);
	}

	public void insertUserAuth(Map param) {
		sqlSession.insert(NAMESPACE + "insertUserAuth", param);
	}

	public void insertTermcndHist(Map termsInfo) {
		sqlSession.insert(NAMESPACE + "insertTermcndHist", termsInfo);
	}

	public Map findDetailRegVdInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDetailRegVdInfo", param);
	}

	public List findVendorAttachmentList(Map param) {
		return sqlSession.selectList(NAMESPACE+"findVendorAttachmentList", param);
	}

	public List findVendorBankAccountInfo(Map param) {
		return sqlSession.selectList(NAMESPACE+"findVendorBankAccountInfo", param);
	}

	public void updateBasicInfoMaster(Map param) { sqlSession.update(NAMESPACE + "updateBasicInfoMaster",param); }

	public void insertVendorBankAccountInfo(Map param) { sqlSession.insert(NAMESPACE+"insertVendorBankAccountInfo", param); }

	public void updateVendorBankAccountInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorBankAccountInfo",param); }

	public void deleteVendorBankAccountInfo(Map param) { sqlSession.update(NAMESPACE + "deleteVendorBankAccountInfo", param); }

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