package smartsuite.app.bp.pro.gr.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.gr.service.GrEvalshtSetupService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class GrEvalSetupEventListener {
	
	@Inject
	GrEvalshtSetupService grEvalshtSetupService;
	
	@EventListener(condition ="#event.eventId == 'saveMappingEvaltmplUuidToGrEvalshtPrcs'")
	public void saveMappingEvaltmplUuidToGrEvalshtPrcs(CustomSpringEvent event){
		grEvalshtSetupService.saveMappingEvaltmplUuidToGrEvalshtPrcs((Map) event.getData());
	}

	/**
	 *
	 * @param Map 평가템플릿 상태값 조회(입고/기성평가 평가시트)
	 */
	@EventListener(condition ="#event.eventId == 'findEvalTmplStsInGeEvalSht'")
	public void findEvalTmplStsInGeEvalSht(CustomSpringEvent event){
		String sts = grEvalshtSetupService.findEvalTmplStsInGeEvalSht((Map) event.getData());
		event.setResult(sts);
	}
}
