package smartsuite.app.bp.srm.performance.pfmcSetup.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.performance.pfmcSetup.service.PegSetupService;
import smartsuite.app.bp.srm.performance.pfmcSetup.service.PfmcEvalshtSetupService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class PfmcSetupEventListener {
	
	@Inject
	PfmcEvalshtSetupService pfmcEvalshtSetupService;

	@Inject
	PegSetupService pegSetupService;

	/**
	 *
	 * @param PfmcInfo 퍼포먼스 평가 프로세스 UUID, 평가항목 평가자 권한 공통코드
	 */
	@EventListener(condition ="#event.eventId == 'findListFactChrGrpEvaltrAboutPfmc'")
	public void saveEvalTmplInfo(CustomSpringEvent event){
		List<Map<String,Object>> result = pfmcEvalshtSetupService.findListFactChrGrpEvaltr((Map) event.getData());
		event.setResult(result);
	}

	/**
	 *
	 * @param Map 평가템플릿 사용여부 조회(퍼포먼스 평가시트)
	 */
	@EventListener(condition ="#event.eventId == 'findEvalTmplUseYnInPfmcEvalSht'")
	public void findEvalTmplUseYnInPfmcEvalSht(CustomSpringEvent event){
		String useYn = pfmcEvalshtSetupService.findEvalTmplUseYnInPfmcEvalSht((Map) event.getData());
		event.setResult(useYn);
	}

	/**
	 *
	 * @param Map 평가템플릿 상태값 조회(퍼포먼스 평가시트)
	 */
	@EventListener(condition ="#event.eventId == 'findEvalTmplStsInPfmcEvalSht'")
	public void findEvalTmplStsInPfmcEvalSht(CustomSpringEvent event){
		String sts = pfmcEvalshtSetupService.findEvalTmplStsInPfmcEvalSht((Map) event.getData());
		event.setResult(sts);
	}

	/**
	 *
	 * @param Map 퍼포먼스 평가시트 확정여부 조회
	 */
	@EventListener(condition ="#event.eventId == 'checkPfmcEvalShtConfirmYnByEvalTmpl'")
	public void checkPfmcEvalShtConfirmYnByEvalTmpl(CustomSpringEvent event){
		String useYn = pfmcEvalshtSetupService.checkPfmcEvalShtConfirmYnByEvalTmpl((Map) event.getData());
		event.setResult(useYn);
	}

	/**
	 *
	 * @param Map 퍼포먼스 평가시트 - 평가템플릿을 매핑한다.
	 */
	@EventListener(condition ="#event.eventId == 'saveMappingEvaltmplUuidToPfmcEvalsht'")
	public void saveMappingEvaltmplUuidToPfmcEvalsht(CustomSpringEvent event){
		pfmcEvalshtSetupService.saveMappingEvaltmplUuidToPfmcEvalsht((Map) event.getData());
	}

	/**
	 *
	 * @param Map 퍼포먼스 평가시트 - 수정 전 원본 평가템플릿을 매핑한다.
	 */
	@EventListener(condition ="#event.eventId == 'saveMappingOrgnPfmcEvaltmplUuidToPfmcEvalsht'")
	public void saveMappingOrgnPfmcEvaltmplUuidToPfmcEvalsht(CustomSpringEvent event){
		pfmcEvalshtSetupService.saveMappingOrgnPfmcEvaltmplUuidToPfmcEvalsht((Map) event.getData());
	}
}
