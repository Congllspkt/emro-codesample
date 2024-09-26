package smartsuite.app.common.status.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.status.respository.PerformanceEvalStatusRepository;

import javax.inject.Inject;
import java.util.Map;

/**
 * Performance Eval 상태를 관리하는 서비스입니다.
 *
 * @author hj.jang
 * @FileName PerformanceEvalStatusService.java
 * @package smartsuite.app.bp.performance.request
 * @변경이력 : [2023. 6. 26] hj.jang 최초작성
 * @see
 * @since 2023. 06. 10
 */
@Service
@Transactional
public class PerformanceEvalStatusService {

	@Inject
	private PerformanceEvalStatusRepository pfmcStatusRepository;
	public void saveDraftPfmcEval(Map<String, Object> param) {
		pfmcStatusRepository.saveDraftPfmcEval(param);
	}
	public void saveRequestPfmcEval(Map<String, Object> param) {
		pfmcStatusRepository.saveRequestPfmcEval(param);
	}
	public void evaltrProgPfmcEval(Map<String, Object> param) {
		pfmcStatusRepository.evaltrProgPfmcEval(param);
	}
	
	// 집계완료 상태 취소
	public void updateCancleCmpldPfmcEval(Map<String, Object> param) {
		pfmcStatusRepository.updateCancleCmpldPfmcEval(param);
	}

	// 평가생성 요청 성공 시 퍼포먼스 평가 요청 상태를 "정성평가" 로 수정
	public void successCreateQualiEvalSubjRes(Map<String, Object> param) {
		pfmcStatusRepository.successCreateQualiEvalSubjRes(param);
	}

	// 평가생성 요청 성공 시 퍼포먼스 평가 요청 상태를 "정량평가" 로 수정
	public void successCreateQuantEvalSubjRes(Map<String, Object> param) {
		pfmcStatusRepository.successCreateQuantEvalSubjRes(param);
	}

	// 평가생성 요청 실패 시 퍼포먼스 평가 요청 상태를 "통보평가" 로 수정
	public void failCreateEvalSubjRes(Map<String, Object> param) {
		pfmcStatusRepository.failCreateEvalSubjRes(param);
	}

	// 퍼포먼스 평가 마감 로직 시작 시 퍼포먼스 평가 요청 상태를 "평가집계" 로 수정
	public void startClosePfmcEval(Map<String, Object> param) {
		pfmcStatusRepository.startClosePfmcEval(param);
	}

	// 퍼포먼스 평가 마감 로직 종료 시 퍼포먼스 평가 요청 상태를 "집계완료" 로 수정
	public void endClosePfmcEval(Map<String, Object> param) {
		pfmcStatusRepository.endClosePfmcEval(param);
	}

	// 퍼포먼스 평가 마감 해제 시 퍼포먼스 평가 요청 상태를 "정성평가"로 수정
	public void cancelClosedPfmcEval(Map<String,Object> param) {
		pfmcStatusRepository.cancelClosedPfmcEval(param);
	}

	// 퍼포먼스 평가 이의제기 통보 시 이의제기 상태를 "이의제기 통보"로 수정
	public void noticeAppeal(Map<String,Object> param) {
		pfmcStatusRepository.noticeAppeal(param);
	}

	// 퍼포먼스 평가 완료 결재 승인 시 퍼포먼스 평가 요청 상태를 "승인완료" 로 수정
	public void approveApprovalPfmcEval(Map<String,Object> param) {
		pfmcStatusRepository.approveApprovalPfmcEval(param);
	}

	// 퍼포먼스 평가 완료 결재 반려 시 퍼포먼스 평가 요청 상태를 "결재반려" 로 수정
	public void rejectApprovalPfmcEval(Map<String,Object> param) {
		pfmcStatusRepository.rejectApprovalPfmcEval(param);
	}

	// 퍼포먼스 평가 완료 결재 취소 시 퍼포먼스 평가 요청 상태를 "집계완료" 로 수정
	public void cancelApprovalPfmcEval(Map<String,Object> param) {
		pfmcStatusRepository.cancelApprovalPfmcEval(param);
	}

	// 퍼포먼스 평가 완료 결재 상신 시 퍼포먼스 평가 요청 상태를 "결재요청" 로 수정
	public void submitApprovalPfmcEval(Map<String,Object> param) {
		pfmcStatusRepository.submitApprovalPfmcEval(param);
	}

	// 퍼포먼스 평가 요청 취소 시 퍼포먼스 평가 요청 상태를 "작성중" 으로 수정
	public void cancelRequestPfmcEval(Map<String, Object> param) {
		pfmcStatusRepository.cancelRequestPfmcEval(param);
	}
	
	// (결재 없이) 퍼포먼스 평가 완료 시 퍼포먼스 평가 요청 상태를 "승인"으로 수정
	public void confirmPfmcEvalResult(Map<String, Object> param) {
		pfmcStatusRepository.confirmPfmcEvalResult(param);
	}

	// 퍼포먼스 평가 이의제기 종료 시 이의제기 상태를 "이의제기 종료" 상태로 수정
	public void endPfmcEvalAppeal(Map<String, Object> param) {
		pfmcStatusRepository.endPfmcEvalAppeal(param);
	}

}
