package smartsuite.app.bp.onboarding.obdSetup.repository;

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
public class ObdEvalshtSetupRepository {
	private static final String NAMESPACE = "obd-evalsht-setup.";
	
	@Inject
	private SqlSession sqlSession;

	/**
	 * 온보딩 평가시트 목록을 조회
	 *
	 * @param param
	 */
	public FloaterStream findListObdEvalsht(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListObdEvalsht", param);
	}

	/**
	 * 온보딩 평가시트 조회
	 *
	 * @param param
	 */
	public Map<String, Object> findObdEvalshtInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findObdEvalshtInfo", param);
	}

	/**
	 * 온보딩 평가시트 프로세스 조회
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findObdEvalshtInfoPrcs(Map param) {
		return sqlSession.selectList(NAMESPACE + "findObdEvalshtInfoPrcs", param);
	}

	/**
	 * 온보딩 평가시트 프로세스 - 평가템플릿 UUID 리스트 조회
	 *
	 * @param param
	 */
	public List<String> findListEvaltmplUuidOfObdEvalshtPrcses(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListEvaltmplUuidOfObdEvalshtPrcses", param);
	}

	/**
	 * 온보딩 평가시트 저장
	 *
	 * @param param
	 */
	public void insertObdEvalsht(Map param) {
		sqlSession.insert(NAMESPACE + "insertObdEvalsht", param);
	}

	/**
	 * 온보딩 평가시트 수정
	 *
	 * @param param
	 */
	public void updateObdEvalsht(Map param) {
		sqlSession.update(NAMESPACE + "updateObdEvalsht", param);
	}
	/**
	 * 이전 온보딩 평가시트 수정
	 *
	 * @param param
	 */
	public void updatePrevObdEvalsht(Map param) {
		sqlSession.update(NAMESPACE + "updatePrevObdEvalsht", param);
	}
	/**
	 * 온보딩 평가시트 프로세스 이전 평가 템플릿 정보 update
	 *
	 * @param param
	 */
	public void updatePrevEvaltmplUuidObdEvalshtPrcs(Map param) {
		sqlSession.update(NAMESPACE + "updatePrevEvaltmplUuidObdEvalshtPrcs", param);
	}
	/**
	 * 온보딩 평가시트 확정
	 *
	 * @param param
	 */
	public void updateCnfdYnObdEvalsht(Map param) {
		sqlSession.update(NAMESPACE + "updateCnfdYnObdEvalsht", param);
	}

	/**
	 * 온보딩 시트 - 온보딩 프로세스 저장
	 *
	 * @param param
	 */
	public void insertObdEvalshtPrcs(Map param) {
		sqlSession.insert(NAMESPACE + "insertObdEvalshtPrcs", param);
	}

	/**
	 * 온보딩 시트 - 온보딩 프로세스 수정
	 *
	 * @param param
	 */
	public void updateObdEvalshtPrcs(Map param) {
		sqlSession.update(NAMESPACE + "updateObdEvalshtPrcs", param);
	}

	/**
	 * 온보딩 시트 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateObdEvalshtStsByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updateObdEvalshtStsByDelete", param);
	}

	/**
	 * 온보딩 시트 - 온보딩 프로세스 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateObdEvalshtPrcsStsByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updateObdEvalshtPrcsStsByDelete", param);
	}

	/**
	 * 온보딩평가그룹 - 평가시트이력 조회
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findListObdEvalshtHis(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListObdEvalshtHis", param);
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
	public List findListOegVmgUuid(Map param){
		return sqlSession.selectList(NAMESPACE + "findListOegVmgUuid", param);
	}

	/**
	 * 평가시트 프로세스 평가자 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateEvaltrByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updateEvaltrByDelete", param);
	}

	/**
	 * 평가시트 프로세스 평가항목별 평가자 저장
	 *
	 * @param param
	 */
	public void insertFactChrGrpEvaltr(Map param) {
		sqlSession.insert(NAMESPACE + "insertFactChrGrpEvaltr", param);
	}

	/**
	 * 평가시트 프로세스 평가항목별 평가자 수정
	 *
	 * @param param
	 */
	public void updateFactChrGrpEvaltr(Map param) {
		sqlSession.update(NAMESPACE + "updateFactChrGrpEvaltr", param);
	}

	/**
	 * 평가시트 프로세스 평가항목별 평가자 삭제 (sts = 'D')
	 *
	 * @param param
	 */
	public void updateFactChrGrpEvaltrByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updateFactChrGrpEvaltrByDelete", param);
	}
	/**
	 * 평가템플릿 사용여부 조회 - 온보딩 평가시트
	 *
	 * @param param
	 */
	public String findEvalTmplUseYnInObdEvalSht(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findEvalTmplUseYnInObdEvalSht", param);
	}

	/**
	 * 평가템플릿 상태값 조회 - 온보딩 평가시트
	 *
	 * @param param
	 */
	public List findEvalTmplStsInObdEvalSht(Map param) {
		return sqlSession.selectList(NAMESPACE + "findEvalTmplStsInObdEvalSht", param);
	}

	/**
	 * 온보딩 평가시트 확정여부 조회
	 *
	 * @param param
	 */
	public String checkObdEvalShtConfirmYnByEvalTmpl(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkObdEvalShtConfirmYnByEvalTmpl", param);
	}

	/**
	 * 온보딩 평가시트 copy
	 *
	 * @param param
	 */
	public void copyObdEvalsht(Map param) {
		sqlSession.insert(NAMESPACE + "copyObdEvalsht", param);
	}
	/**
	 * 온보딩 평가시트 자가진단 대상 copy
	 *
	 * @param param
	 */
	public void copyObdEvalshtPrcs(Map param) {
		sqlSession.insert(NAMESPACE + "copyObdEvalshtPrcs", param);
	}
	/**
	 * 온보딩 평가시트 항목별 담당자 copy
	 *
	 * @param param
	 */
	public void copyListObdFactChrGrpEvaltr(Map param) {
		sqlSession.insert(NAMESPACE + "copyListObdFactChrGrpEvaltr", param);
	}

	/**
	 * 온보딩 평가시트 프로세스 - 평가템플릿 매핑
	 *
	 * @param param
	 */
	public void saveMappingEvaltmplUuidToObdEvalshtPrcs(Map param) {
		sqlSession.update(NAMESPACE + "saveMappingEvaltmplUuidToObdEvalshtPrcs", param);
	}

	/**
	 * 온보딩 평가시트 프로세스s - 수정 전 원본 평가템플릿 매핑
	 *
	 * @param param
	 */
	public void saveMappingOrgnObdEvaltmplUuidToObdEvalsht(Map param) {
		sqlSession.update(NAMESPACE + "saveMappingOrgnObdEvaltmplUuidToObdEvalsht", param);
	}
}
