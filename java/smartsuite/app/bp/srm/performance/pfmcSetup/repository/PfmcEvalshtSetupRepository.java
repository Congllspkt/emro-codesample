package smartsuite.app.bp.srm.performance.pfmcSetup.repository;

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
public class PfmcEvalshtSetupRepository {
	private static final String NAMESPACE = "pfmc-evalsht-setup.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 퍼포먼스 평가시트 목록을 조회
	 *
	 * @param param
	 */
	public FloaterStream findListPfmcEvalsht(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListPfmcEvalsht", param);
	}

	/**
	 * 퍼포먼스 평가시트 조회
	 *
	 * @param param
	 */
	public Map<String, Object> findPfmcEvalshtInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPfmcEvalshtInfo", param);
	}

	/**
	 * 퍼포먼스 평가시트 저장
	 *
	 * @param param
	 */
	public void insertPfmcEvalsht(Map param) {
		sqlSession.insert(NAMESPACE + "insertPfmcEvalsht", param);
	}

	/**
	 * 퍼포먼스 평가시트 수정
	 *
	 * @param param
	 */
	public void updatePfmcEvalsht(Map param) {
		sqlSession.update(NAMESPACE + "updatePfmcEvalsht", param);
	}
	/**
	 * 이전 퍼포먼스 평가시트 수정
	 *
	 * @param param
	 */
	public void updatePrevPfmcEvalsht(Map param) {
		sqlSession.update(NAMESPACE + "updatePrevPfmcEvalsht", param);
	}
	/**
	 * 퍼포먼스 평가시트 확정
	 *
	 * @param param
	 */
	public void updateCnfdYnPfmcEvalsht(Map param) {
		sqlSession.update(NAMESPACE + "updateCnfdYnPfmcEvalsht", param);
	}

	/**
	 * 퍼포먼스 시트 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updatePfmcEvalshtStsByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updatePfmcEvalshtStsByDelete", param);
	}

	/**
	 * 퍼포먼스평가그룹 - 평가시트이력 조회
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findListPfmcEvalshtHis(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPfmcEvalshtHis", param);
	}

	/**
	 * 평가템플릿 평가항목별 평가자 조회
	 *
	 * @param param
	 */
	public List findListFactChrGrpEvaltr(Map param){
		return sqlSession.selectList(NAMESPACE + "findListFactChrGrpEvaltr", param);
	}

	/**
	 * 평가템플릿 협력사관리그룹 평가자 조회대상협력사관리그룹 UUid 목록 조회
	 *
	 * @param param
	 */
	public List findListPegVmgUuid(Map param){
		return sqlSession.selectList(NAMESPACE + "findListPegVmgUuid", param);
	}

	/**
	 * 평가시트 평가자 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateEvaltrByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updateEvaltrByDelete", param);
	}

	/**
	 * 평가시트 평가항목별 평가자 저장
	 *
	 * @param param
	 */
	public void insertFactChrGrpEvaltr(Map param) {
		sqlSession.insert(NAMESPACE + "insertFactChrGrpEvaltr", param);
	}

	/**
	 * 평가시트 평가항목별 평가자 수정
	 *
	 * @param param
	 */
	public void updateFactChrGrpEvaltr(Map param) {
		sqlSession.update(NAMESPACE + "updateFactChrGrpEvaltr", param);
	}

	/**
	 * 평가시트 평가항목별 평가자 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateFactChrGrpEvaltrByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updateFactChrGrpEvaltrByDelete", param);
	}

	/**
	 * 퍼포먼스 평가시트 자가진단 대상 삭제 (delete)
	 *
	 * @param param
	 */
	public void deletePfmcSlfckSubj(Map param) {
		sqlSession.update(NAMESPACE + "deletePfmcSlfckSubj", param);
	}

	/**
	 * 퍼포먼스 평가시트 자가진단 대상 저장
	 *
	 * @param param
	 */
	public void insertPfmcSlfckSubj(Map param) {
		sqlSession.insert(NAMESPACE + "insertPfmcSlfckSubj", param);
	}


	/**
	 * 평가템플릿 사용여부 조회 - 퍼포먼스 평가시트
	 *
	 * @param param
	 */
	public String findEvalTmplUseYnInPfmcEvalSht(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findEvalTmplUseYnInPfmcEvalSht", param);
	}

	/**
	 * 평가템플릿 상태값 조회 - 퍼포먼스 평가시트
	 *
	 * @param param
	 */
	public List findEvalTmplStsInPfmcEvalSht(Map param) {
		return sqlSession.selectList(NAMESPACE + "findEvalTmplStsInPfmcEvalSht", param);
	}

	/**
	 * 퍼포먼스 평가시트 확정여부 조회
	 *
	 * @param param
	 */
	public String checkPfmcEvalShtConfirmYnByEvalTmpl(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkPfmcEvalShtConfirmYnByEvalTmpl", param);
	}

	/**
	 * 퍼포먼스 평가시트 copy
	 *
	 * @param param
	 */
	public void copyPfmcEvalsht(Map param) {
		sqlSession.insert(NAMESPACE + "copyPfmcEvalsht", param);
	}
	/**
	 * 퍼포먼스 평가시트 자가진단 대상 copy
	 *
	 * @param param
	 */
	public void copyListPfmcSlfckSubj(Map param) {
		sqlSession.insert(NAMESPACE + "copyListPfmcSlfckSubj", param);
	}
	/**
	 * 퍼포먼스 평가시트 항목별 담당자 copy
	 *
	 * @param param
	 */
	public void copyListPfmcFactChrGrpEvaltr(Map param) {
		sqlSession.insert(NAMESPACE + "copyListPfmcFactChrGrpEvaltr", param);
	}

	/**
	 * 퍼포먼스 평가시트 - 평가템플릿 매핑
	 *
	 * @param param
	 */
	public void saveMappingEvaltmplUuidToPfmcEvalsht(Map param) {
		sqlSession.update(NAMESPACE + "saveMappingEvaltmplUuidToPfmcEvalsht", param);
	}

	/**
	 * 퍼포먼스 평가시트 - 수정 전 원본 평가템플릿 매핑
	 *
	 * @param param
	 */
	public void saveMappingOrgnPfmcEvaltmplUuidToPfmcEvalsht(Map param) {
		sqlSession.update(NAMESPACE + "saveMappingOrgnPfmcEvaltmplUuidToPfmcEvalsht", param);
	}
}
