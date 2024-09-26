package smartsuite.app.bp.commonEval.evalResult.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.commonEval.evalResult.service.EvalResultService;
import smartsuite.app.bp.commonEval.evalResult.service.EvaltrEvalfactResultService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@Service
public class EvaltrEvalfactResultEventListener {

	@Inject
	EvaltrEvalfactResultService evaltrEvalfactResultService;

	/**
	 * 평가수행용 평가항목을 조회한다.
	 *
	 * Required:
	 * param.ten_id - 시스템아이디
	 * param.eval_task_typ_ccd - 업무유형 공통코드
	 * param.evamltmpl_uuid - 평가템플릿 아이디
	 * param.eval_subj_evaltr_res_uuid - 평가대상 평가자 아이디
	 * param.evalfact_evaltr_authty_ccd - 평가항목 평가자 권한 공통코드
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'findEvalfactFulfillInfo'")
	public void findEvalfactFulfillInfo(CustomSpringEvent event) {

		ResultMap resultMap = evaltrEvalfactResultService.findEvalfactFulfillInfo((Map) event.getData());
		event.setResult(resultMap);
	}
}
