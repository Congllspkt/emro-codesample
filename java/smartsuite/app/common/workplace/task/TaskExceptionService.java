package smartsuite.app.common.workplace.task;

import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.common.workplace.shared.WorkplaceConst;

@Service
@Transactional
public class TaskExceptionService {

	/** The sql session. */
	@Inject
	private SqlSession sqlSession;

	/** The namespace. */
	@Inject
	private static final String NAMESPACE = "task-exception.";
	
	@Inject
	private CustomTaskExceptionService customTaskExceptionService;
	
	/**
	 * task title - 예외 처리
	 * 
	 * @param param {work_id, app_id} 
	 * @return
	 */
	public String findTaskTitle(Map<String, Object> param) {
		String workId = (String)param.get("work_id");
		
		// PR 접수 대기
		if(WorkplaceConst.EX_TITLE_PR_ITEM_WORK_IDS.contains(workId)) {
			return sqlSession.selectOne(NAMESPACE + "findTaskTitleByPrItem", param);
			
		// RFx 비가격평가, 검수평가
		} else if(WorkplaceConst.EX_TITLE_PRO_EVAL_WORK_IDS.contains(workId)) {
			return sqlSession.selectOne(NAMESPACE + "findTaskTitleByProEval", param);
		
		// PO (물품) 납품대기
		} else if(WorkplaceConst.EX_TITLE_PO_ITEM_WORK_IDS.contains(workId)) {
			return sqlSession.selectOne(NAMESPACE + "findTaskTitleByPoItem", param);
		
		} else {
			return customTaskExceptionService.findTaskTitle(param);
		}
	}
	
	/**
	 * due dt - 업무 특성 별 예외 처리
	 * 
	 * @param param {work_id, app_id, close_bas_day, arr_bas_day, new_bas_day}
	 * @return
	 */
	public Map<String, Object> findCloseDtInfo(Map<String, Object> param) {
		String workId = (String)param.get("work_id");
		
		// PR
		if(WorkplaceConst.EX_CLOSE_DT_PR_WORK_IDS.contains(workId)) {
			// 구매요청 일반구매-공사용역 인 경우
			return sqlSession.selectOne(NAMESPACE + "findCloseDtInfoByPrNcCt", param);
			
		// RFx 시작
		} else if(WorkplaceConst.EX_CLOSE_DT_RFX_START_WORK_IDS.contains(workId)) {
			// RFx 즉시시작인 경우
			return sqlSession.selectOne(NAMESPACE + "findCloseDtInfoByRfxImdtStart", param);
			
		// RFx 종료
		} else if(WorkplaceConst.EX_CLOSE_DT_RFX_CLOSE_WORK_IDS.contains(workId)) {
			// RFx 일반구매-공사용역 인 경우
			return sqlSession.selectOne(NAMESPACE + "findCloseDtInfoByRfxNcCt", param);
		
		// PO
		} else if(WorkplaceConst.EX_CLOSE_DT_PO_WORK_IDS.contains(workId)) {
			// 발주 공사용역 인 경우
			return sqlSession.selectOne(NAMESPACE + "findCloseDtInfoByPoCt", param);
		
		// 기성 납품대기
		} else if(WorkplaceConst.EX_CLOSE_DT_AR_READY_WORK_IDS.contains(workId)) {
			param.put("aggregate_type", "MAX");
			
			// 발주 공사용역 인 경우
			return sqlSession.selectOne(NAMESPACE + "findCloseDtInfoByPoCt", param);
			
		} else {
			return customTaskExceptionService.findCloseDtInfo(param);
		}
	}
	
	/**
	 * due dt - 해당 컬럼의 DATA TYPE에 따른 예외 처리
	 * 
	 * @param param {work_id, app_id, close_bas_day, arr_bas_day, new_bas_day, close_tb, close_col, close_link_col, tick_tb, tick_app_col}
	 * @return
	 */
	public Map<String, Object> findCloseDtInfoByDataType(Map<String, Object> param) {
		String workId = (String)param.get("work_id");
		
		// 날짜 타입 컬럼인 경우
		if(WorkplaceConst.EX_CLOSE_DT_TYPE_DATE_WORK_IDS.contains(workId)) {
			return sqlSession.selectOne(NAMESPACE +"findCloseDtInfoForDateType", param);
		
		} else {
			return customTaskExceptionService.findCloseDtInfoByDataType(param);
		}
	}
}
