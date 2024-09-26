package smartsuite.app.bp.vendorMaster.vendorInfo.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.QueryFloaterInSqlSession;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VendorInfoRepository {
	private static final String NAMESPACE = "vendor-info.";
	
	@Inject
	private SqlSession sqlSession;

	@Inject
	private QueryFloaterInSqlSession queryFoaterInSqlSession;

	/**
	 * 공장정보를 조회한다. (findListPlantInfo)
	 *
	 * @param param
	 */
	public List findListVdFatyInfo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListVdFatyInfo", param);
	}

	/**
	 * 공장 일련번호 조회 (getMaxPlantSeq)
	 *
	 * @param param
	 */
	public Integer findVdFatyMaxSeq(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVdFatyMaxSeq", param);
	}

	public void insertListVdFatyInfo(Map param) { sqlSession.insert(NAMESPACE+"insertListVdFatyInfo", param); }

	public void updateListVdFatyInfo(Map param) { sqlSession.update(NAMESPACE + "updateListVdFatyInfo",param); }

	public void deleteListVdFatyInfo(Map<String, Object> param) { sqlSession.update(NAMESPACE + "deleteListVdFatyInfo", param); }

	public void deleteListVdFacInfoByFatyUuid(Map<String, Object> param) { sqlSession.update(NAMESPACE + "deleteListVdFacInfoByFatyUuid", param); }

	/**
	 * 설비정보를 조회한다.
	 *
	 * @param param
	 */
	public List findListVdFacInfo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListVdFacInfo", param);
	}

	/**
	 * 설비 일련번호 조회 (getMaxEquipmentsSeq)
	 *
	 * @param param
	 */
	public Integer findVdFacMaxSeq(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVdFacMaxSeq", param);
	}

	public void insertListVdFacInfo(Map param) { sqlSession.insert(NAMESPACE+"insertListVdFacInfo", param); }

	public void updateListVdFacInfo(Map param) { sqlSession.update(NAMESPACE + "updateListVdFacInfo",param); }

	public void deleteListVdFacInfo(Map<String, Object> param) { sqlSession.update(NAMESPACE + "deleteListVdFacInfo", param); }

	public List findVdInviteMailList(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findVdInviteMailList", param);
	}
	public List checkDuplicatedVdInfo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "checkDuplicatedVdInfo", param);
	}
	public Map findVdInviteMailInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVdInviteMailInfo", param);
	}
	public void insertVendorInviteMail(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertVendorInviteMail", param);
	}
	public List findListVendorInviteValidate(Map<String, Object> param) {
		return queryFoaterInSqlSession.selectList(NAMESPACE + "findListVendorInviteValidate", param, param.get("bizregnos"));
	}
	public List findListVendorFinance(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorFinance", param);
	}
	public List findListCertManager(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListCertManager", param);
	}
	public List findListVdFatyFac(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListVdFatyFac", param);
	}

	public List findListFinanceInfo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListFinanceInfo", param);
	}
	public void insertListFinanceInfo(Map<String, Object> param){
		sqlSession.insert(NAMESPACE+"insertListFinanceInfo", param);
	}
	public void updateListFinanceInfo(Map<String, Object> param){
		sqlSession.update(NAMESPACE+"updateListFinanceInfo", param);
	}

	public void deleteFinanceInfo(Map<String, Object> param){
		sqlSession.delete(NAMESPACE+"deleteFinanceInfo", param);
	}
	public List findListLaborInfo(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "findListLaborInfo", param);
	}
	public void insertListLaborInfo(Map<String, Object> param){
		sqlSession.insert(NAMESPACE+"insertListLaborInfo", param);
	}
	public void updateListLaborInfo(Map<String, Object> param){
		sqlSession.update(NAMESPACE+"updateListLaborInfo", param);
	}
	public void deleteLaborInfo(Map<String, Object> param){
		sqlSession.delete(NAMESPACE+"deleteLaborInfo", param);
	}
	public List findListCertInfo(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "findListCertInfo", param);
	}
	public void insertListCertInfo(Map<String, Object> param){
		sqlSession.insert(NAMESPACE+"insertListCertInfo", param);
	}
	public void updateListCertInfo(Map<String, Object> param){
		sqlSession.update(NAMESPACE+"updateListCertInfo", param);
	}
	public void deleteCertInfo(Map<String, Object> param){
		sqlSession.delete(NAMESPACE+"deleteCertInfo", param);
	}

}
