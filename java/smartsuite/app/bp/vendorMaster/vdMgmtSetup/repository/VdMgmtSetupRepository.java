package smartsuite.app.bp.vendorMaster.vdMgmtSetup.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VdMgmtSetupRepository {
	private static final String NAMESPACE = "vd-mgmt-setup.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 협력사관리유형 목록을 조회
	 *
	 * @param param
	 */
	public FloaterStream findListVmt(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListVmt", param);
	}

	/**
	 * 미등록 협력사관리유형 조회 (findListUnregisteredVendorManagementGroup)
	 *
	 * @param param
	 */
	public List findListUnregisteredVendorManagementType(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUnregisteredVendorManagementType", param);
	}

	/**
	 * 협력사관리유형 삭제 (delete)
	 *
	 * @param param
	 */
	public void deleteVmt(Map param) {
		sqlSession.delete(NAMESPACE + "deleteVmt", param);
	}

	/**
	 * 협력사관리유형 저장 (Insert)
	 *
	 * @param param
	 */
	public void insertListVmt(Map param) {
		sqlSession.insert(NAMESPACE + "insertListVmt", param);
	}

	/**
	 * 협력사관리유형 수정
	 *
	 * @param param
	 */
	public void updateListVmt(Map param) {
		sqlSession.update(NAMESPACE + "updateListVmt", param);
	}

	/**
	 * 협력사관리유형 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateVmtStsByDelete(Map param) {
		sqlSession.delete(NAMESPACE + "updateVmtStsByDelete", param);
	}

	/**
	 * 협력사관리그룹 목록을 조회
	 *
	 * @param param
	 */
	public FloaterStream findListVmg(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListVmg", param);
	}

	/**
	 * 협력사관리그룹 목록을 저장 전 validation
	 *
	 * @param param
	 */
	public Map checkValidBeforeSaveListVmg(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkValidBeforeSaveListVmg", param);
	}

	/**
	 * 협력사관리그룹 저장 (Insert)
	 *
	 * @param param
	 */
	public void insertListVmg(Map param) {
		sqlSession.insert(NAMESPACE + "insertListVmg", param);
	}

	/**
	 * 협력사관리그룹 수정
	 *
	 * @param param
	 */
	public void updateListVmg(Map param) {
		sqlSession.update(NAMESPACE + "updateListVmg", param);
	}

	/**
	 * VMG 삭제 시 Validation
	 *
	 * @param param
	 */
	public int validateDeleteVmg(Map param) {
		return sqlSession.selectOne(NAMESPACE + "validateDeleteVmg", param);
	}

	/**
	 * 협력사관리그룹 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateVmgStsByDelete(Map param) {
		sqlSession.delete(NAMESPACE + "updateVmgStsByDelete", param);
	}

	/**
	 * 협력사관리그룹 명 수정
	 *
	 * @param param
	 */
	public void updateVenderManagementGroupNameBySourcingGroup(Map param) {
		sqlSession.update(NAMESPACE + "updateVenderManagementGroupNameBySourcingGroup", param);
	}

	/**
	 * 소싱그룹 상세 정보 조회
	 *
	 * @param param
	 */
	public Map findSourcingGroupDetail(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSourcingGroupDetail", param);
	}
	
	/**
	 * 협력사관리그룹 품목 분류 목록 조회
	 *
	 * @param param
	 */
	public List findSourcingGroupItemCategoryList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findSourcingGroupItemCategoryList", param);
	}

	/**
	 * 소싱그룹 품목분류 저장
	 *
	 * @param param
	 */
	public void saveSgItemCategoryList(Map param) {
		sqlSession.update(NAMESPACE + "saveSgItemCategoryList", param);
	}

	/**
	 * 운영조직 협력사관리그룹 조회
	 *
	 * @param param
	 */
	public FloaterStream findListOorgVmg(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListOorgVmg", param);
	}

	/**
	 * 운영조직 협력사관리그룹 저장(insert)
	 *
	 * @param param
	 */
	public void insertListOorgVmg(Map param) {
		sqlSession.insert(NAMESPACE + "insertListOorgVmg", param);
	}

	/**
	 * 운영조직 협력사관리그룹 저장(update)
	 *
	 * @param param
	 */
	public void updateListOorgVmg(Map param) {
		sqlSession.update(NAMESPACE + "updateListOorgVmg", param);
	}

	/**
	 * 운영조직 협력사관리그룹 삭제
	 *
	 * @param param
	 */
	public void deleteListOorgVmg(Map param) {
		sqlSession.delete(NAMESPACE + "deleteListOorgVmg", param);
	}

	/**
	 * 운영조직 협력사관리그룹 삭제 validation
	 *
	 * @param param
	 */
	public List validateDeleteVmgOorg(Map param) {
		return sqlSession.selectList(NAMESPACE + "validateDeleteVmgOorg", param);
	}

	/**
	 * 운영조직과 매핑되지 않은 협력사관리그룹 조회
	 *
	 * @param param
	 */
	public FloaterStream findListNonMappingOorgVmg(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListNonMappingOorgVmg", param);
	}

	/**
	 * 협력사관리그룹 평가자 목록 조회
	 *
	 * @param param
	 */
	public FloaterStream findListVendorManagementGroupEvaltr(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListVendorManagementGroupEvaltr", param);
	}

	/**
	 * 협력사관리그룹 평가자 목록 조회 (업무 평가그룹 설정 조회용)
	 *
	 * @param param
	 */
	public List findListVendorManagementGroupEvaltrForView(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorManagementGroupEvaltrForView", param);
	}

	/**
	 * 협력사관리그룹 평가자 저장 (Insert)
	 *
	 * @param param
	 */
	public void insertVendorManagementGroupEvaltr(Map param) {
		sqlSession.insert(NAMESPACE + "insertVendorManagementGroupEvaltr", param);
	}

	/**
	 * 협력사관리그룹 평가자 삭제 (delete)
	 *
	 * @param param
	 */
	public void deleteListVendorManagementGroupEvaltr(Map param) {
		sqlSession.delete(NAMESPACE + "deleteListVendorManagementGroupEvaltr", param);
	}
	/**
	 * 소싱그룹 담당자의 평가자여부에 따라 협력사관리그룹 평가자 삭제
	 *
	 * @param param
	 */
	public void deleteVendorManagementGroupEvaltrBySourcingGroup(Map param) {
		sqlSession.delete(NAMESPACE + "deleteVendorManagementGroupEvaltrBySourcingGroup", param);
	}
}
