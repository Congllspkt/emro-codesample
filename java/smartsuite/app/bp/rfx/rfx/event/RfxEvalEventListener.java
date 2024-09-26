package smartsuite.app.bp.rfx.rfx.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.service.RfxEvalService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxEvalEventListener {
	
	@Inject
	RfxEvalService rfxEvalService;
	
	@EventListener(condition = "#event.eventId == 'saveMappingEvaltmplUuidToNpeEvalshtPrcs'")
	public void saveMappingEvaltmplUuidToNpeEvalshtPrcs(CustomSpringEvent event) {
		rfxEvalService.saveMappingEvaltmplUuidToNpeEvalshtPrcs((Map) event.getData());
	}

	/**
	 *
	 * @param Map 평가템플릿 상태값 조회(비가격평가 평가시트)
	 */
	@EventListener(condition ="#event.eventId == 'findEvalTmplStsInNpeEvalSht'")
	public void findEvalTmplStsInNpeEvalSht(CustomSpringEvent event){
		String sts = rfxEvalService.findEvalTmplStsInNpeEvalSht((Map) event.getData());
		event.setResult(sts);
	}
}
