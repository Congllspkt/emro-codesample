package smartsuite.app.bp.edoc.template.event;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.edoc.template.service.MainTemplateService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class TemplateEventListener {

	@Inject
	MainTemplateService mainTemplateService;
	
	/**
	 * 계약서식 콤보형태로 제공한다. <br><br>
	 * <b>Required:</b><br>
	 * param.xxx - 속성 설명<br>
	 * param.zzz - 속성 설명
	 * 
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'findCntrdocFormList'")
	public ResultMap findCntrdocFormList(CustomSpringEvent event) {
		//rfxStatusService.cancelRfxResult((Map) event.getData());
		ResultMap resultMap = ResultMap.getInstance();
		List cntrDocFormList = mainTemplateService.findCntrdocFormList((Map)event.getData());
		resultMap.setResultList(cntrDocFormList);
		return resultMap;
	}
	
}
