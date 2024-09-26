package smartsuite.app.bp.vendorMaster.vendorInfo.repository;

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
public class VendorMasterRepository {
	private static final String NAMESPACE = "vendor-master.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 운영조직 목록 조회
	 *
	 * @param param
	 */
	public List findListOorgCdAll(String param) {
		// 대용량 처리
		return sqlSession.selectList(NAMESPACE + "findListOorgCdAll", param);
	}

	/**
	 * 협력사 목록 조회
	 *
	 * @param param
	 */
	public FloaterStream findListVendorProfile(Map<String, Object> param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListVendorProfile", param);
	}

	/**
	 * 기본정보 조회
	 *
	 * @param param
	 */
	public Map findBasicVendorInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findBasicVendorInfo", param);
	}

	public List findVendorAttachmentList(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findVendorAttachmentList", param);
	}

	/**
	 * 운영조직 조회
	 *
	 * @param param
	 */
	public Map findVendorOperationOrganizationInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVendorOperationOrganizationInfo", param);
	}

	/**
	 * 기본정보 이력 조회
	 *
	 * @param param
	 */
	public Map findBasicVendorHistrecInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findBasicVendorHistrecInfo", param);
	}

	/**
	 * 협력사 첨부파일 이력 조회
	 *
	 * @param param
	 */
	public List findAttachmentListVendorHistrecInfo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findAttachmentListVendorHistrecInfo", param);
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
	 * 운영조직 협력사 이력 조회
	 *
	 * @param param
	 */
	public Map findVendorOperationOrganizationHistrecInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVendorOperationOrganizationHistrecInfo", param);
	}

	/**
	 * 협력사 계좌정보 조회
	 *
	 * @param param
	 */
	public List findVendorBankAccountInfo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findVendorBankAccountInfo", param);
	}

	/**
	 * 사용대상 협력사관리유형 목록을 조회
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findListVmtUsing(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVmtUsing", param);
	}

	/**
	 * 추가 거래가능 협력사관리그룹 조회
	 *
	 * @param param
	 */
	public FloaterStream findListUnregisteredVendorManagementGroup(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListUnregisteredVendorManagementGroup", param);
	}

	/**
	 * 협력사 변경이력 차수 조회
	 *
	 * @param param
	 */
	public Integer findVdHistrecModSeq(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVdHistrecModSeq", param);
	}

	public Map findVendorModifyStatus(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVendorModifyStatus", param);
	}

	/**
	 * 운영조직 협력사 추가/수정
	 *
	 * @param param
	 */
	public void insertVendorOperationOrganizationInfo(Map param) { sqlSession.insert(NAMESPACE + "insertVendorOperationOrganizationInfo", param); }

	public void updateVendorOperationOrganizationInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorOperationOrganizationInfo", param); }

	/**
	 * 운영조직 협력사 담당자 수정
	 *
	 * @param param
	 */
	public void updateVendorOperationOrganizationPicInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorOperationOrganizationPicInfo", param); }

	/**
	 * 운영조직 협력사관리그룹 RFX 추천 대상 여부 수정
	 *
	 * @param param
	 */
	public void saveVendorOperationOrganizationVmgRcmdYn(Map param) { sqlSession.update(NAMESPACE + "saveVendorOperationOrganizationVmgRcmdYn", param); }

	/**
	 * 협력사 기본정보 수정
	 *
	 * @param param
	 */
	public void updateBasicVendorInfo(Map param) { sqlSession.update(NAMESPACE + "updateBasicVendorInfo", param); }

	/**
	 * 협력사 첨부파일 추가/수정
	 *
	 * @param param
	 */
	public void insertVendorAttachmentInfo(Map param) { sqlSession.insert(NAMESPACE + "insertVendorAttachmentInfo", param); }

	public void updateVendorAttachmentInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorAttachmentInfo",param); }


	/**
	 * 협력사 첨부파일 이력 추가/수정/삭제
	 *
	 * @param param
	 */
	public void insertVendorAttachmentHistrecInfo(Map param) { sqlSession.insert(NAMESPACE + "insertVendorAttachmentHistrecInfo", param); }

	public void updateVendorAttachmentHistrecInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorAttachmentHistrecInfo",param); }

	public void deleteVendorAttachmentHistrecInfo(Map param) { sqlSession.delete(NAMESPACE + "deleteVendorAttachmentHistrecInfo",param); }

	/**
	 * 협력사 계좌정보 이력 추가/수정/삭제
	 *
	 * @param param
	 */
	public void insertVendorBankAccountHistrecInfo(Map param) { sqlSession.insert(NAMESPACE + "insertVendorBankAccountHistrecInfo", param); }

	public void updateVendorBankAccountHistrecInfo(Map param) { sqlSession.update(NAMESPACE + "updateVendorBankAccountHistrecInfo",param); }

	public void deleteVendorBankAccountHistrecInfo(Map param) { sqlSession.delete(NAMESPACE + "deleteVendorBankAccountHistrecInfo",param); }

	/**
	 * 협력사 계좌정보 추가/삭제
	 *
	 * @param param
	 */
	public void insertVendorBankAccountInfo(Map param) { sqlSession.insert(NAMESPACE+"insertVendorBankAccountInfo", param); }
	public void deleteVendorBankAccountInfo(Map param) { sqlSession.update(NAMESPACE + "deleteVendorBankAccountInfo", param); }

	/**
	 * 운영조직 협력사 협력사관리그룹 추가/수정
	 *
	 * @param param
	 */
	public void insertVdOorgVmg(Map param) { sqlSession.insert(NAMESPACE + "insertVdOorgVmg", param); }

	/**
	 * 협력사 변경 이력 추가/수정/삭제
	 *
	 * @param param
	 */
	public void insertVdInfoHistory(Map param) { sqlSession.insert(NAMESPACE + "insertVdInfoHistory", param); }

	public void updateVdInfoHistory(Map param) { sqlSession.update(NAMESPACE + "updateVdInfoHistory",param); }

	public void deleteVdInfoHistory(Map param) { sqlSession.delete(NAMESPACE + "deleteVdInfoHistory",param); }

	/**
	 * 운영조직 협력사 변경 이력 추가/수정/삭제
	 *
	 * @param param
	 */
	public void insertVdOorgInfoHistory(Map param) { sqlSession.insert(NAMESPACE + "insertVdOorgInfoHistory",param); }

	public void updateVdOorgInfoHistory(Map param) { sqlSession.update(NAMESPACE + "updateVdOorgInfoHistory",param); }

	public void deleteVdOorgInfoHistory(Map param) { sqlSession.delete(NAMESPACE + "deleteVdOorgInfoHistory",param); }

	/**
	 * 운영조직 협력사 협력사관리그룹 변경 이력 추가/수정/삭제
	 *
	 * @param param
	 */
	public void insertVdOorgVmgInfoHistory(Map param) { sqlSession.insert(NAMESPACE + "insertVdOorgVmgInfoHistory",param); }

	public void deleteVdOorgVmgInfoHistory(Map param) { sqlSession.delete(NAMESPACE + "deleteVdOorgVmgInfoHistory",param); }

	/**
	 * 결재 상태에 따른 변경이력 업데이트
	 *
	 * @param param
	 */
	public void updateVdHistoryByApproval(Map param) { sqlSession.update(NAMESPACE + "updateVdHistoryByApproval", param); }

	public void updateVdOorgHistoryByRegApproval(Map param) { sqlSession.update(NAMESPACE + "updateVdOorgHistoryByRegApproval", param); }

	public void updateVdOorgHistoryByStopApproval(Map param) { sqlSession.update(NAMESPACE + "updateVdOorgHistoryByStopApproval", param); }

	public void updateVdOorgVmgHistoryByApproval(Map param) { sqlSession.update(NAMESPACE + "updateVdOorgVmgHistoryByApproval", param); }

	public List findVendorOperationOrganizationVmgInfoHistory(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findVendorOperationOrganizationVmgInfoHistory", param);
	}

	/**
	 * 협력사 정보 업데이트
	 *
	 * @param param
	 */
	public void updateVdInfoChg(Map param) { sqlSession.update(NAMESPACE + "updateVdInfoChg", param); }
	public void updateVdOorgInfoChg(Map param) { sqlSession.update(NAMESPACE + "updateVdOorgInfoChg", param); }

	public void updateVdPoPossInfo(Map param) { sqlSession.update(NAMESPACE + "updateVdPoPossInfo", param); }
	public void updateVdOorgPoPossInfo(Map param) { sqlSession.update(NAMESPACE + "updateVdOorgPoPossInfo", param); }

	public void updateVdOorgInfoByStop(Map param) { sqlSession.update(NAMESPACE + "updateVdOorgInfoByStop", param); }

	public void updateVdOorgVmgInfo(Map param) { sqlSession.update(NAMESPACE + "updateVdOorgVmgInfo", param); }

	/**
	 * 협력사 이력 정보 업데이트 (From Current To History)
	 *
	 * @param param
	 */
	public void mergeVendorBankAccountInfoFromCurrentToHistory(Map param) { sqlSession.update(NAMESPACE + "mergeVendorBankAccountInfoFromCurrentToHistory", param); }
	public void mergeVendorAttachmentInfoFromCurrentToHistory(Map param) { sqlSession.update(NAMESPACE + "mergeVendorAttachmentInfoFromCurrentToHistory", param); }
	public void updateVdInfoChgFromCurrentToHistory(Map param) { sqlSession.update(NAMESPACE + "updateVdInfoChgFromCurrentToHistory", param); }
	public void updateVdOorgInfoChgFromCurrentToHistory(Map param) { sqlSession.update(NAMESPACE + "updateVdOorgInfoChgFromCurrentToHistory", param); }
	public void updateVdOorgPoPossInfoFromCurrentToHistory(Map param) { sqlSession.update(NAMESPACE + "updateVdOorgPoPossInfoFromCurrentToHistory", param); }

	/**
	 * 협력사 정규업체 권한 유무 조회
	 *
	 * @param param
	 */
	public Integer findVendorAuthOfficial(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVendorAuthOfficial", param);
	}

	/**
	 * 협력사 변경이력 결재정보 조회
	 *
	 * @param param
	 */
	public Map findVdHistrecApprovalInfoForDelete(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVdHistrecApprovalInfoForDelete", param);
	}

	/**
	 * 진행중인 협력사 결재로 인한 Current 결재 ROCK 여부 조회
	 *
	 * @param param
	 */
	public List findVendorApprovalRockYn(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findVendorApprovalRockYn", param);
	}

	/**
	 * 진행중인 협력사 결재 정보 조회
	 *
	 * @param param
	 */
	public Map findProgressingVendorApprovalInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findProgressingVendorApprovalInfo", param);
	}


	public FloaterStream findListVendorManagementGroupCursitu(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListVendorManagementGroupCursitu", param);
	}

	/**
	 * vendor 잠재 업체로 update
	 *
	 * @param param
	 */
	public void updateVdOorgObdTypCcdToPtnl(Map param) { sqlSession.insert(NAMESPACE + "updateVdOorgObdTypCcdToPtnl", param); }
	
	/** 전 운영조직에 대한 등록정보 체크
     *
     * @param param the param
     */
	public Map findIsEditableRegReq(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findIsEditableRegReq", param);
	}
	
	/** 현재 운영조직에 대한 등록정보 체크
     *
     * @param param the param
     */
	public Map findIsEditableRegReqByOorgCd(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findIsEditableRegReqByOorgCd", param);
	}

	/**
	 * 등록/등록취소 대상 협력사관리그룹 목록 조회
	 *
	 * @param param
	 */
	public List findVendorTargVmgInfo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findVendorTargVmgInfo", param);
	}

	/**
	 * 정보변경요청 접수 목록을 조회한다.
	 *
	 * @param param the param
	 */
	public List findListVendorReqMainInfoChangeReceipt(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "findListVendorReqMainInfoChangeReceipt", param);
	}

	/**
	 * 정보변경요청 반려정보 update
	 *
	 * @param param the param
	 */
	public void updateVdInfoChgRejectInfo(Map<String, Object> param){
		sqlSession.update(NAMESPACE + "updateVdInfoChgRejectInfo", param);
	}

	/**
	 * 정보변경요청 변경요청상태 update
	 *
	 * @param param the param
	 */
	public void updateChgReqStsCcdAboutVdInfoChg(Map<String, Object> param){
		sqlSession.update(NAMESPACE + "updateChgReqStsCcdAboutVdInfoChg", param);
	}

	/**
	 * 선택한 차수의 이전차수, 협력사 정보 변경 요청 정보 조회
	 *
	 * @param param the param
	 */
	public Map findPrevHistrecForVendorReqMainInfoChange(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "findPrevHistrecForVendorReqMainInfoChange", param);
	}
	/**
	 * 선택한 차수의 이전차수, 협력사 운영조직 정보 변경 요청 정보 조회
	 *
	 * @param param the param
	 */
	public Map findPrevOorgHistrecForVendorReqMainInfoChange(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "findPrevOorgHistrecForVendorReqMainInfoChange", param);
	}

	/**
	 * 협력사 정보 변경 요청 정보 조회 (VdInfo)
	 *
	 * @param param the param
	 */
	public Map findBasicVendorReqMainInfoChange(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "findBasicVendorReqMainInfoChange", param);
	}
	/**
	 * 협력사 정보 변경 요청 정보 조회 (OorgInfo)
	 *
	 * @param param the param
	 */
	public Map findVendorOperationOrganizationReqMainInfoChange(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "findVendorOperationOrganizationReqMainInfoChange", param);
	}

	/**
	 * 협력사 정보 변경 요청 정보 조회 (AthfList)
	 *
	 * @param param the param
	 */
	public List findAttachmentListVendorReqMainInfoChange(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "findAttachmentListVendorReqMainInfoChange", param);
	}

	/**
	 * 협력사 정보 변경 요청 정보 조회 (bnkAcctList)
	 *
	 * @param param the param
	 */
	public List findBankAccountVendorReqMainInfoChange(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "findBankAccountVendorReqMainInfoChange", param);
	}

	/**
	 * 선택한 차수의 이전차수, 협력사 정보 변경 이력 조회
	 *
	 * @param param the param
	 */
	public Map findPrevHistrecForVendorInfoChange(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "findPrevHistrecForVendorInfoChange", param);
	}

	/**
	 * 선택한 차수의 이전차수, 협력사 운영조직 변경 이력 조회
	 *
	 * @param param the param
	 */
	public Map findPrevOorgHistrecForVendorInfoChange(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE + "findPrevOorgHistrecForVendorInfoChange", param);
	}

	/**
	 * 협력사 결재 이력 목록 조회
	 *
	 * @param param
	 */
	public FloaterStream findListVendorApprovalHistory(Map param) { return new QueryFloaterStream(sqlSession, NAMESPACE + "findListVendorApprovalHistory", param); }

	/**
	 * 작성중(=임시저장) 상태의 이력 조회
	 *
	 * @param param the param
	 */
	public List findListVdChgHistrecInCrngSts(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "findListVdChgHistrecInCrngSts", param);
	}

	/**
	 * 기본거래계약 요청 정보 update (vdOorgHistrec)
	 *
	 * @param param the param
	 */
	public void updateVdOorgHistrecContractReqInfo(Map<String, Object> param){
		sqlSession.update(NAMESPACE + "updateVdOorgHistrecContractReqInfo", param);
	}

	/**
	 * 기본거래계약 정보 update (vdOorg)
	 *
	 * @param param the param
	 */
	public void updateVdOorgContractInfo(Map<String, Object> param){
		sqlSession.update(NAMESPACE + "updateVdOorgContractInfo", param);
	}

	/**
	 * 협력사의 연도별 RFx 실적 목록 조회 (통합정보)
	 *
	 * @param param the param
	 */
	public List findListYearlyVendorRfxInfo(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "findListYearlyVendorRfxInfo", param);
	}

	/**
	 * 협력사의 연도별 소싱그룹 발주, 입고 금액 조회
	 *
	 * @param param the param
	 */
	public List findListVendorPoGrAmtBySG(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "findListVendorPoGrAmtBySG", param);
	}

	/**
	 * 업로드 사용자 기준으로 업로드 데이터 삭제
	 *
	 */
	public void deleteVMGUploadByUser(){
		sqlSession.delete(NAMESPACE + "deleteVMGUploadByUser");
	}

	/**
	 * 업로드 사용자 기준으로 업로드 데이터 삭제
	 *
	 */
	public void deleteVdMasterUploadByUser(){
		sqlSession.delete(NAMESPACE + "deleteVdMasterUploadByUser");
	}

	/**
	 * 업로드 사용자 기준으로 업로드 데이터 삭제
	 *
	 * @param param the param
	 */
	public List insertVdMasterUploadInfo(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "insertVdMasterUploadInfo", param);
	}
	/**
	 * 업로드 사용자 기준으로 업로드 데이터 삭제
	 *
	 * @param param the param
	 */
	public List insertVMGMasterUploadInfo(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE + "insertVMGMasterUploadInfo", param);
	}

	/**
	 * 업로드 사용자 기준으로 협력사 업로드 데이터 유효성 검증
	 *
	 */
	public List uploadVdMasterValidate(){
		return sqlSession.selectList(NAMESPACE + "uploadVdMasterValidate");
	}
	/**
	 * 업로드 사용자 기준으로 협력사 업로드 데이터 유효성 검증
	 *
	 */
	public List uploadVMGMasterValidate(){
		return sqlSession.selectList(NAMESPACE + "uploadVMGMasterValidate");
	}
    
    /**
     * 구매 운영 조직으로 업체 운영조직 조회
     * @param vendorInfo
     * @return
     */
    public List searchVendorOorgInfo(Map vendorInfo) {
        return sqlSession.selectList(NAMESPACE + "searchVendorOorgInfo",vendorInfo);
    }
}
