package smartsuite.app.sp.vendorMaster.vendorInfo.repository;

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
public class SpVendorMasterRepository {
	private static final String NAMESPACE = "sp-vendor-master.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 운영단위별 운영조직 조회
	 *
	 * @param param
	 */
	public List findListOorgCdAll(String param) {
		return sqlSession.selectList(NAMESPACE + "findListOorgCdAll", param);
	}

	/**
	 * 협력사 목록 조회
	 *
	 * @param param
	 */
	public FloaterStream findListVdProfile(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListVdProfile", param);
	}

	/**
	 * 운영조직 조회 (getBasicOperOrg)
	 *
	 * @param param
	 */
	public Map findVendorOperationOrganizationInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVendorOperationOrganizationInfo", param);
	}

	/**
	 * 기본정보 조회 (getBasicInfo)
	 *
	 * @param param
	 */
	public Map findBasicVendorInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findBasicVendorInfo", param);
	}

	public List findVendorAttachmentList(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findVendorAttachmentList", param);
	}

	public void insertVendorOperationOrganizationInfo(Map param) { sqlSession.insert(NAMESPACE+"insertVendorOperationOrganizationInfo", param); }

	public void updateVendorOperationOrganizationInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorOperationOrganizationInfo",param); }

	public void updateBasicVendorInfo(Map param) { sqlSession.update(NAMESPACE + "updateBasicVendorInfo",param); }

	public void insertVendorAttachmentInfo(Map param) { sqlSession.insert(NAMESPACE+"insertVendorAttachmentInfo", param); }

	public void updateVendorAttachmentInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorAttachmentInfo",param); }

	/**
	 * 협력사 계좌정보 조회
	 *
	 * @param param
	 */
	public List findVendorBankAccountInfo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findVendorBankAccountInfo", param);
	}

	public void insertVendorBankAccountInfo(Map param) { sqlSession.insert(NAMESPACE+"insertVendorBankAccountInfo", param); }

	public void updateVendorBankAccountInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorBankAccountInfo",param); }

	public void deleteVendorBankAccountInfo(Map param) { sqlSession.update(NAMESPACE + "deleteVendorBankAccountInfo", param); }

	/**
	 * 사용대상 협력사관리유형 목록을 조회
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findListVmtUsing(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVmtUsing", param);
	}

	/**
	 * 협력사-운영조직 : 협력사관리유형 정보 조회
	 *
	 * @param param
	 */
	public List findListVendorManagementTypeInfo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorManagementTypeInfo", param);
	}

	/**
	 * 등록 협력사관리그룹 조회
	 *
	 * @param param
	 */
	public List findListVendorManagementGroup(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorManagementGroup", param);
	}

	/**
	 * 추가 거래가능 협력사관리그룹 조회
	 *
	 * @param param
	 */
	public FloaterStream findListUnregisteredVendorManagementGroup(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListUnregisteredVendorManagementGroup", param);
	}


	public List findListVendorInfoChangeRequest (Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "findListVendorInfoChangeRequest", param);
	}

	public Map checkErpVdCd(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "checkErpVdCd", param);
	}
	public Map checkChangeRequestYn(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "checkChangeRequestYn", param);
	}
	public Integer getMaxVendorChangeReqRev(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE+"getMaxVendorChangeReqRev", param);
	}
	public Map findVendorInfoChangeRequestInfo(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "findVendorInfoChangeRequestInfo", param);
	}
	public void insertVendorInfoChangeRequest(Map<String, Object> param){
		sqlSession.insert(NAMESPACE+"insertVendorInfoChangeRequest", param);
	}
	public void updateVendorInfoChange(Map<String, Object> param){
		sqlSession.update(NAMESPACE+"updateVendorInfoChange", param);
	}
	public void sendVendorInfoChange(Map<String, Object> param){
		sqlSession.update(NAMESPACE+"sendVendorInfoChange", param);
	}
	public void insertCopyVendorInfoChange(Map<String, Object> param){
		sqlSession.insert(NAMESPACE+"insertCopyVendorInfoChange", param);
	}
	public void deleteVendorChangeRequestInfo(Map<String, Object> param){
		sqlSession.delete(NAMESPACE+"deleteVendorChangeRequestInfo", param);
	}

	/**
	 * 협력사 첨부파일 변경 이력 조회
	 *
	 * @param param
	 */
	public List findAttachmentListVendorHistrecInfo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findAttachmentListVendorHistrecInfo", param);
	}
	/**
	 * 협력사 첨부파일 변경 요청 조회
	 *
	 * @param param
	 */
	public List findVendorAttachmentChangeRequestList(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findVendorAttachmentChangeRequestList", param);
	}

	/**
	 * 협력사 계좌정보 변경 이력 조회
	 *
	 * @param param
	 */
	public List findVendorBankAccountHistrecList(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findVendorBankAccountHistrecList", param);
	}

	/**
	 * 협력사 계좌정보 변경 요청 조회
	 *
	 * @param param
	 */
	public List findVendorBankAccountChangeRequestList(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findVendorBankAccountChangeRequestList", param);
	}

	/**
	 * 협력사 첨부파일 변경 요청(insert)
	 *
	 * @param param
	 */
	public void insertVendorAttachmentChangeRequest(Map param){
		sqlSession.insert(NAMESPACE + "insertVendorAttachmentChangeRequest", param);
	}

	/**
	 * 협력사 첨부파일 변경 요청 수정
	 *
	 * @param param
	 */
	public void updateVendorAttachmentChangeRequest(Map param){
		sqlSession.update(NAMESPACE + "updateVendorAttachmentChangeRequest", param);
	}

	/**
	 * 협력사 계좌정보 변경 요청(insert)
	 *
	 * @param param
	 */
	public void insertVendorBankAccountInfoChange(Map param){
		sqlSession.insert(NAMESPACE + "insertVendorBankAccountInfoChange", param);
	}

	/**
	 * 협력사 계좌정보 변경 요청 수정
	 *
	 * @param param
	 */
	public void updateVendorBankAccountInfoChange(Map param){
		sqlSession.update(NAMESPACE + "updateVendorBankAccountInfoChange", param);
	}

	public Map findVendorBankInfo(Map param){
		return sqlSession.selectOne(NAMESPACE + "findVendorBankInfo", param);
	}

	public List findPoPossOorgCdByVd(Map param){
		return sqlSession.selectList(NAMESPACE + "findPoPossOorgCdByVd", param);
	}

	/**
	 * 협력사 첨부파일 변경요청 삭제
	 *
	 * @param param
	 */
	public void deleteVendorAttachmentChangeRequestInfo(Map param){
		sqlSession.delete(NAMESPACE + "deleteVendorAttachmentChangeRequestInfo", param);
	}

	/**
	 * 협력사 계좌정보 변경요청 삭제
	 *
	 * @param param
	 */
	public void deleteVendorBankAccountChangeRequestInfo(Map param){
		sqlSession.delete(NAMESPACE + "deleteVendorBankAccountChangeRequestInfo", param);
	}
	
	/**
	 * 협력사 첨부파일 변경요청 조회
	 *
	 * @param param
	 */
	public List findListVendorAttachmentChangeRequest(Map param){
		return sqlSession.selectList(NAMESPACE + "findListVendorAttachmentChangeRequest", param);
	}	
	
	/**
	 * 협력사 첨부파일 변경요청 복사
	 *
	 * @param param
	 */
	public void insertCopyVEndorAttachmentChangeRequest(Map param){
		sqlSession.insert(NAMESPACE + "insertCopyVEndorAttachmentChangeRequest", param);
	}

	/**
	 * 협력사의 이전 차수 조회
	 *
	 * @param param
	 */
	public Map findPrevHistrecForVendorReqMainInfoChange(Map param){
		return sqlSession.selectOne(NAMESPACE + "findPrevHistrecForVendorReqMainInfoChange", param);
	}

	/**
	 * 기본정보 이력 조회
	 *
	 * @param param
	 */
	public Map findBasicVendorHistrecInfo(Map param){
		return sqlSession.selectOne(NAMESPACE + "findBasicVendorHistrecInfo", param);
	}

	/**
	 * 운영조직 이전 차수 정보 조회
	 *
	 * @param param
	 */
	public Map findPrevOorgHistrecForVendorReqMainInfoChange(Map param){
		return sqlSession.selectOne(NAMESPACE + "findPrevOorgHistrecForVendorReqMainInfoChange", param);
	}
	
	/**
	 * 운영조직 이력 정보 조회
	 *
	 * @param param
	 */
	public Map findVendorOperationOrganizationHistrecInfo(Map param){
		return sqlSession.selectOne(NAMESPACE + "findVendorOperationOrganizationHistrecInfo", param);
	}
}
