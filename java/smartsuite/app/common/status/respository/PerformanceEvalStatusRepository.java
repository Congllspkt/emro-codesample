package smartsuite.app.common.status.respository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class PerformanceEvalStatusRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "pfmc-status.";

	// 퍼포먼스 평가 임시저장
	public void saveDraftPfmcEval(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "saveDraftPfmcEval", param);
	}

	// 퍼포먼스 평가 요청 (생성 대기)
	public void saveRequestPfmcEval(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "saveRequestPfmcEval", param);
	}

	// 퍼포먼스 평가 요청 (생성 대기)
	public void evaltrProgPfmcEval(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "evaltrProgPfmcEval", param);
	}

	// 집계완료 상태 취소
	public void updateCancleCmpldPfmcEval(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateCancleCmpldPfmcEval", param);
	}

	// 평가생성 요청 성공 시 퍼포먼스 평가 요청 상태를 "정성평가" 로 수정
	public void successCreateQualiEvalSubjRes(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "successCreateQualiEvalSubjRes", param);
	}

	// 평가생성 요청 성공 시 퍼포먼스 평가 요청 상태를 "정성평가" 로 수정 (todo 정성 / 정량 평가 상태에 따라 수행상태 코드 다르게 저장 필요)
	public void successCreateQuantEvalSubjRes(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "successCreateQuantEvalSubjRes", param);
	}

	// 평가생성 요청 실패 시 퍼포먼스 평가 요청 상태를 "통보평가" 로 수정
	public void failCreateEvalSubjRes(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "failCreateEvalSubjRes", param);
	}

	// 퍼포먼스 평가 마감 로직 시작 시 퍼포먼스 평가 요청 상태를 "평가집계" 로 수정
	public void startClosePfmcEval(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "startClosePfmcEval", param);
	}

	// 퍼포먼스 평가 마감 로직 종료 시 퍼포먼스 평가 요청 상태를 "집계완료" 로 수정
	public void endClosePfmcEval(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "endClosePfmcEval", param);
	}

	// 퍼포먼스 평가 마감 해제 시 퍼포먼스 평가 요청 상태를 "정성평가"로 수정
	public void cancelClosedPfmcEval(Map<String,Object> param) {
		sqlSession.update(NAMESPACE + "cancelClosedPfmcEval", param);
	}

	// 퍼포먼스 평가 이의제기 통보 시 이의제기 상태를 "이의제기 통보"로 수정
	public void noticeAppeal(Map<String,Object> param) {
		sqlSession.update(NAMESPACE + "noticeAppeal", param);
	}

	// 퍼포먼스 평가 완료 결재 승인 시 퍼포먼스 평가 요청 상태를 "승인완료" 로 수정
	public void approveApprovalPfmcEval(Map<String,Object> param) {
		sqlSession.update(NAMESPACE + "approveApprovalPfmcEval", param);
	}

	// 퍼포먼스 평가 완료 결재 반려 시 퍼포먼스 평가 요청 상태를 "결재반려" 로 수정
	public void rejectApprovalPfmcEval(Map<String,Object> param) {
		sqlSession.update(NAMESPACE + "rejectApprovalPfmcEval", param);
	}

	// 퍼포먼스 평가 완료 결재 취소 시 퍼포먼스 평가 요청 상태를 "집계완료" 로 수정
	public void cancelApprovalPfmcEval(Map<String,Object> param) {
		sqlSession.update(NAMESPACE + "cancelApprovalPfmcEval", param);
	}

	// 퍼포먼스 평가 완료 결재 상신 시 퍼포먼스 평가 요청 상태를 "결재요청" 로 수정
	public void submitApprovalPfmcEval(Map<String,Object> param) {
		sqlSession.update(NAMESPACE + "submitApprovalPfmcEval", param);
	}

	// 퍼포먼스 평가 요청 취소 시 퍼포먼스 평가 요청 상태를 "작성중" 으로 수정
	public void cancelRequestPfmcEval(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "cancelRequestPfmcEval", param);
	}

	// (결재 없이) 퍼포먼스 평가 완료 시 퍼포먼스 평가 요청 상태를 "승인"으로 수정
	public void confirmPfmcEvalResult(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "confirmPfmcEvalResult", param);
	}

	// 퍼포먼스 평가 이의제기 종료 시 이의제기 상태를 "이의제기 종료" 상태로 수정
	public void endPfmcEvalAppeal(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "endPfmcEvalAppeal", param);
	}
}
