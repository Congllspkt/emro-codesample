package smartsuite.app.bp.onboarding.obdSetup.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.onboarding.obdSetup.service.ObdEvalshtSetupService;
import smartsuite.app.bp.onboarding.obdSetup.service.OegSetupService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class ObdSetupEventListener {
	
	@Inject
	ObdEvalshtSetupService obdEvalshtSetupService;

	@Inject
	OegSetupService oegSetupService;

	/**
	 *
	 * @param ObdInfo 온보딩 평가 프로세스 UUID, 평가항목 평가자 권한 공통코드
	 */
	@EventListener(condition ="#event.eventId == 'findListFactChrGrpEvaltrAboutObd'")
	public void saveEvalTmplInfo(CustomSpringEvent event){
		List<Map<String,Object>> result = obdEvalshtSetupService.findListFactChrGrpEvaltr((Map) event.getData());
		event.setResult(result);
	}

	/**
	 *
	 * @param Map 평가템플릿 사용여부 조회(온보딩 평가시트)
	 */
	@EventListener(condition ="#event.eventId == 'findEvalTmplUseYnInObdEvalSht'")
	public void findEvalTmplUseYnInObdEvalSht(CustomSpringEvent event){
		String useYn = obdEvalshtSetupService.findEvalTmplUseYnInObdEvalSht((Map) event.getData());
		event.setResult(useYn);
	}
	
	/**
	 *
	 * @param Map 평가템플릿 상태값 조회(온보딩 평가시트)
	 */
	@EventListener(condition ="#event.eventId == 'findEvalTmplStsInObdEvalSht'")
	public void findEvalTmplStsInObdEvalSht(CustomSpringEvent event){
		String sts = obdEvalshtSetupService.findEvalTmplStsInObdEvalSht((Map) event.getData());
		event.setResult(sts);
	}

	/**
	 *
	 * @param Map 온보딩 평가시트 확정 여부 조회
	 */
	@EventListener(condition ="#event.eventId == 'checkObdEvalShtConfirmYnByEvalTmpl'")
	public void checkObdEvalShtConfirmYnByEvalTmpl(CustomSpringEvent event){
		String useYn = obdEvalshtSetupService.checkObdEvalShtConfirmYnByEvalTmpl((Map) event.getData());
		event.setResult(useYn);
	}

	/**
	 *
	 * @param Map 온보딩 평가시트 프로세스 - 평가템플릿을 매핑한다.
	 */
	@EventListener(condition ="#event.eventId == 'saveMappingEvaltmplUuidToObdEvalshtPrcs'")
	public void saveMappingEvaltmplUuidToObdEvalshtPrcs(CustomSpringEvent event){
		obdEvalshtSetupService.saveMappingEvaltmplUuidToObdEvalshtPrcs((Map) event.getData());
	}

	/**
	 *
	 * @param Map 온보딩 평가시트 프로세스 - 수정 전 원본 평가템플릿을 매핑한다.
	 */
	@EventListener(condition ="#event.eventId == 'saveMappingOrgnObdEvaltmplUuidToObdEvalsht'")
	public void saveMappingOrgnObdEvaltmplUuidToObdEvalsht(CustomSpringEvent event){
		obdEvalshtSetupService.saveMappingOrgnObdEvaltmplUuidToObdEvalsht((Map) event.getData());
	}
}
