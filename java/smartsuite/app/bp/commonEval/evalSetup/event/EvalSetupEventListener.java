package smartsuite.app.bp.commonEval.evalSetup.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.commonEval.evalSetup.service.EvalTmplService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class EvalSetupEventListener {
	
	@Inject
	EvalTmplService evalTmplService;

	/**
	 *
	 * @param evalTmplParam - 저장할 평가 템플릿 및 fact 정보
	 */
	@EventListener(condition ="#event.eventId == 'saveEvalTmplInfo'")
	public void saveEvalTmplInfo(CustomSpringEvent event){
		ResultMap result = evalTmplService.saveEvalTmplInfo((Map) event.getData());
		event.setResult(result);
	}

	/**
     *
     * @param pfmcEvalshtInfo - 확정중인 퍼포번스 평가시트정보 (tmpl_uuid, cnfd_yn 필수)
	 * @return void
     */
    @EventListener(condition ="#event.eventId == 'updateCnfdYnEvalTmpl'")
    public void updateCnfdYnEvalTmpl(CustomSpringEvent event){
        evalTmplService.updateCnfdYnEvalTmpl((Map) event.getData());
    }
}
