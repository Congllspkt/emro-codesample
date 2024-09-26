package smartsuite.app.bp.srm.performance.request.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class PerformanceEvalRequestRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "pfmc-req.";

	public Map findPfmcEvalReq(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPfmcEvalReq", param);
	}

	// 퍼포먼스평가그룹 목록 조회
	public List findListPfmcEvalPeg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPfmcEvalPeg", param);
	}

	// 퍼포먼스평가 저장
	public void insertPe(Map param) {
		sqlSession.insert(NAMESPACE + "insertPe", param);
	}

	// 퍼포먼스평가 퍼포먼스평가그룹 저장
	public void insertPePeg(Map param) {
		sqlSession.insert(NAMESPACE + "insertPePeg", param);
	}
	// 퍼포먼스평가 평가그룹등급 저장
	public void insertPeEvalGrd(Map param) {
		sqlSession.insert(NAMESPACE + "insertPeEvalGrd", param);
	}

	// 퍼포먼스평가 퍼포먼스평가그룹 조회
	public Map findPePeg(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPePeg", param);
	}

	// 퍼포먼스 평가시트의 평가항목 담당자 권한 목록 조회
	public List findListPfmcEvalshtEvalfactAuthty(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPfmcEvalshtEvalfactAuthty", param);
	}
	
	// 퍼포먼스평가 평가그룹의 평가등급 목록 조회
	public List findListPeEvalGrd(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPeEvalGrd", param);
	}
	
	// 퍼포먼스평가 수정 저장
	public void updatePe(Map param) {
		sqlSession.update(NAMESPACE + "updatePe", param);
	}

	// 퍼포먼스평가 대상 평가자 삭제
	public void deletePeSubjEvaltr(Map param) {
		sqlSession.delete(NAMESPACE + "deletePeSubjEvaltr", param);
	}

	// 퍼포먼스평가 대상 삭제
	public void deletePeSubj(Map param) {
		sqlSession.delete(NAMESPACE + "deletePeSubj", param);
	}

	// 퍼포먼스평가 등급 삭제
	public void deletePeEvalGrd(Map param) {
		sqlSession.delete(NAMESPACE + "deletePeEvalGrd", param);
	}

	// 퍼포먼스평가 그룹 삭제
	public void deletePePeg(Map param) {
		sqlSession.delete(NAMESPACE + "deletePePeg", param);
	}

	// 퍼포먼스평가 요청대상 목록 조회
	public List findListPeSubjByPePeg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjByPePeg", param);
	}

	// 퍼포먼스평가 평가대상 신규 저장
	public void insertPeSubj(Map param) {
		sqlSession.delete(NAMESPACE + "insertPeSubj", param);
	}

	public Map getRequestablePeSubjSlfck(Map param) { return sqlSession.selectOne(NAMESPACE + "getRequestablePeSubjSlfck", param); }

	public void insertPeSubjEvaltrFact(Map param) {
		sqlSession.insert(NAMESPACE + "insertPeSubjEvaltrFact", param);
	}

	// 퍼포먼스 평가대상 평가자 저장 : 구매담당자
	public void insertPeSubjEvaltrPurc(Map param) {
		sqlSession.insert(NAMESPACE + "insertPeSubjEvaltrPurc", param);
	}

	// 퍼포먼스 평가대상 평가자 저장 : 협력사 (자체점검 담당자)
	public void insertPeSubjEvaltrVd(Map param) {
		sqlSession.insert(NAMESPACE + "insertPeSubjEvaltrVd", param);
	}
	
	// 퍼포먼스 평가대상 평가자 저장 : VMG 담당자
	public void insertPeSubjEvaltrVmg(Map param) {
		sqlSession.insert(NAMESPACE + "insertPeSubjEvaltrVmg", param);
	}

	public List findListPeSubjEvaltr(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjEvaltr", param);
	}
	
	// 퍼포먼스평가 대상 평가자 신규 저장 (기존 존재하는 평가자가 아닌경우 insert)
	public void mergePeSubjEvaltrAdd(Map param) {
		sqlSession.insert(NAMESPACE + "mergePeSubjEvaltrAdd", param);
	}

	// 퍼포먼스평가 대상 평가자 신규 저장 (pe_subj_evaltr_uuid를 parameter로 생성)
	public void insertPeSubjEvaltrAdd(Map param) {
		sqlSession.insert(NAMESPACE + "insertPeSubjEvaltrAdd", param);
	}

	// 퍼포먼스평가 대상 평가자 수정
	public void updatePeSubjEvaltr(Map param) {
		sqlSession.update(NAMESPACE + "updatePeSubjEvaltr", param);
	}

	// 퍼포먼스평가 요청 대상 추가 목록을 조회한다.
	public List findListPrgsPeSubjAdd(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPrgsPeSubjAdd", param);
	}

	//
	public void insertPrgsPeSubjAdd(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertPrgsPeSubjAdd", param);
	}

	// 퍼포먼스평가 요청 목록 정보 조회
	public List findListPfmcEvalPrgs(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPfmcEvalPrgs", param);
	}

	// 특정 퍼포먼스평가그룹 협력사관리그룹으로 퍼포먼스평가 통보 전 임시저장 데이터 조회
	public List findPeInfoBeforeCrngVmg(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findPeInfoBeforeCrngVmg", param);
	}

	// 퍼포먼스평가 진행 정보 조회
	public Map findPfmcEvalPrgsInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPfmcEvalPrgsInfo", param);
	}

	// 퍼포먼스평가 진행 대상 목록 조회
	public List findListPfmcEvalSubjPrgs(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPfmcEvalSubjPrgs", param);
	}
	// 퍼포먼스평가 요청대상 평가자 목록 조회
	public List findListPrgsPeSubjEvaltr(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPrgsPeSubjEvaltr", param);
	}

	public String existPeSubjEvaltrYn(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "existPeSubjEvaltrYn", param);
	}

	// 퍼포먼스 평가 요청 아이디로 퍼포먼스 평가대상 전체 삭제
	public void deletePeSubjByPeUuid(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deletePeSubjByPeUuid", param);
	}

	// 퍼포먼스평가 오청 아이디로 퍼포먼스 평가대상 평가자 전체 삭제
	public void deletePeSubjEvaltrByPeUuid(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deletePeSubjEvaltrByPeUuid", param);
	}

	// 퍼포먼스평가 오청 아이디로 퍼포먼스 평가그룹 전체 삭제
	public void deletePePegByPeUuid(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deletePePegByPeUuid", param);
	}

	// 퍼포먼스 평가 요청 삭제
	public void deletePe(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deletePe", param);
	}

	// 퍼포먼스 평가요청 저장 전 유효성 검사
	// EFCT_VMG_YN, EXISTS_PE_SUBJ_YN, EXISTS_PE_EVALTR_YN, EFCT_AUTHTY_YN
	public List checkValidPfmcReqBeforeSave(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "checkValidPfmcReqBeforeSave", param);
	}

	// 퍼포먼스평가 요청된 상태인지 여부
	public String getPfmcEvalStatusRequestedYn(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "getPfmcEvalStatusRequestedYn", param);
	}

	// 평가그룹의 협력사관리그룹과 맞지 않는 퍼포먼스 평가대상 협력사관리그룹 조회
	public List findListInvalidPeSubjByPegVmg(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListInvalidPeSubjByPegVmg", param);
	}

	// 퍼포먼스 요청 평가그룹의 펑가시트를 퍼포먼스평가그룹의 현재시점 유효한 평가시트로 업데이트한다.
	public void updatePePegValidPfmcEvalshtByPeg(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updatePePegValidPfmcEvalshtByPeg", param);
	}
	// 퍼포먼스 요청 평가대상의 펑가시트를 퍼포먼스평가그룹의 현재시점 유효한 평가시트로 업데이트한다.
	public void updatePeSubjValidPfmcEvalshtByPeg(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updatePeSubjValidPfmcEvalshtByPeg", param);
	}

	// 퍼포먼스 평가대상 평가자에 평가 템플릿의 평가항목권한자가 누락된 경우 추가로 생성한다.
	public void insertPeSubjEvaltrValidAuthtyByTmpl(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertPeSubjEvaltrEffectAuthtyByTmpl", param);
	}

	// 평가시트 템플릿의 평가항목권한과 맞지 않는 퍼포먼스 평가대상 평가자를 삭제한다.
	public void deletePeSubjEvaltrInvalidAuthtyByTmpl(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deletePeSubjEvaltrInvalidAuthtyByTmpl", param);
	}

	// 퍼포먼스 요청 취소 시 퍼포먼스평가대상 평가자에서 eval 관련 필드를 null로 업데이트 한다.
	public void cancelRequestPeSubjEvaltr(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "cancelRequestPeSubjEvaltr", param);
	}
}
