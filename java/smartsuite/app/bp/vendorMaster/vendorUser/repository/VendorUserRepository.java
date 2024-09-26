package smartsuite.app.bp.vendorMaster.vendorUser.repository;

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
public class VendorUserRepository {
	private static final String NAMESPACE = "vendor-user.";
	
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 운영조직 목록 조회.
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findListOperOrgAll(String param) {
		return sqlSession.selectList(NAMESPACE + "findListOperOrgAll", param);
	}
	
	/**
	 * 협력사 목록 조회
	 *
	 * @param param
	 */
	public FloaterStream findListVdInfo(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListVdInfo", param);
	}

	public List findListPic(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPic", param);
	}
	public Map findPicInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPicInfo",param);
	}

	public void updatePicInfo(Map param){
		sqlSession.update(NAMESPACE+"updatePicInfo",param);
	}

	public void deletePicRole(Map param){
		sqlSession.delete(NAMESPACE+"deletePicRole", param);
	}
	public void deletePicRoleByRole(Map param){
		sqlSession.delete(NAMESPACE+"deletePicRoleByRole", param);
	}
	public void deletePicUser(Map param){
		sqlSession.delete(NAMESPACE+"deletePicUser", param);
	}
	public void updatePicUserInfo(Map param){
		sqlSession.update(NAMESPACE+"updatePicUserInfo", param);
	}

	public void insertPicUserInfo(Map param) {
		sqlSession.insert(NAMESPACE+"insertPicUserInfo", param);
	}

	public void insertPicRole(Map param) {
		sqlSession.insert(NAMESPACE+"insertPicRole", param);
	}

	public void saveInfoAcctLockYn(Map param){
		sqlSession.update(NAMESPACE + "saveInfoAcctLockYn", param);
	}

	public void initPw(Map param){
		sqlSession.update(NAMESPACE + "initPw", param);
	}

	public List findListRoleByPicUser(Map param){
		return sqlSession.selectList(NAMESPACE + "findListRoleByPicUser", param);
	}

	/**
	 * 협력사별 담당자 리스트 조회
	 * @param param
	 * @return
	 */
	public List findListVendorPicToRenew(Map param){
		return sqlSession.selectList(NAMESPACE + "findListVendorPicToRenew", param);
	}
}
