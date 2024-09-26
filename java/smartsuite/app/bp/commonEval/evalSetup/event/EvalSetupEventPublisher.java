package smartsuite.app.bp.commonEval.evalSetup.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class EvalSetupEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;

	/**
	 * 평가항목별 평가자 조회를 요청한다.
	 * 
	 * @param param - 평가자를 조회할 평가 템플릿 또는 프로세스 정보
	 * @return void
	 */
	public List findListFactChrGrpEvaltr(Map param) {
		String obdEvalshtPrcsUuid = param.get("obd_evalsht_prcs_uuid") == null ? "" : param.get("obd_evalsht_prcs_uuid").toString();
		String pfmcEvalshtUuid = param.get("pfmc_evalsht_uuid") == null ? "" : param.get("pfmc_evalsht_uuid").toString();

		CustomSpringEvent event = null;
		if(!"".equals(obdEvalshtPrcsUuid)){  // 온보딩평가
			event = CustomSpringEvent.toCompleteEvent("findListFactChrGrpEvaltrAboutObd", param);
		}else if(!"".equals(pfmcEvalshtUuid)){  // 퍼포먼스평가
			event = CustomSpringEvent.toCompleteEvent("findListFactChrGrpEvaltrAboutPfmc", param);
		}

		if(event != null){
			publisher.publishEvent(event);
			return (List) event.getResult();
		}else{
			return new ArrayList();
		}
	}

	/**
	 * 평가템플릿 사용 여부를 조회한다.
	 *
	 * @param param
	 * @return void
	 */
	public String findEvalTmplUseYn(Map param) {
		String evalTaskTypCcd = param.get("eval_task_typ_ccd") == null ? "" : param.get("eval_task_typ_ccd").toString();

		CustomSpringEvent event = null;
		if("OE".equals(evalTaskTypCcd)){
			// work_evaltmpl_uuid 존재 시, 현재 평가시트 프로세스 외, 다른 평가시트 프로세스에서의 평가템플릿 사용여부 조회
			// work_evaltmpl_uuid 미존재 시, 모든 평가시트 프로세스에서의 평가템플릿 사용여부 조회
			event = CustomSpringEvent.toCompleteEvent("findEvalTmplUseYnInObdEvalSht", param); // 온보딩 평가시트
		} else if("PE".equals(evalTaskTypCcd)){
			// work_evaltmpl_uuid 존재 시, 현재 평가시트 외, 다른 평가시트에서의 평가템플릿 사용여부 조회
			// work_evaltmpl_uuid 미존재 시, 모든 평가시트에서의 평가템플릿 사용여부 조회
			event = CustomSpringEvent.toCompleteEvent("findEvalTmplUseYnInPfmcEvalSht", param); // 퍼포먼스 평가시트
		}
		if(event != null){
			publisher.publishEvent(event);
			return (String) event.getResult();
		}else{
			return "";
		}
	}
	
	/**
	 * 평가템플릿 상태값을 조회한다.
	 *
	 * @param param
	 * @return void
	 */
	public String findEvalTmplSts(Map param) {
		String evalTaskTypCcd = param.get("eval_task_typ_ccd") == null ? "" : param.get("eval_task_typ_ccd").toString();

		CustomSpringEvent event = null;
		if("OE".equals(evalTaskTypCcd)){
			// work_evaltmpl_uuid 존재 시, 현재 평가시트 프로세스 외, 다른 평가시트 프로세스에서의 평가템플릿 사용여부 조회
			// work_evaltmpl_uuid 미존재 시, 모든 평가시트 프로세스에서의 평가템플릿 사용여부 조회
			event = CustomSpringEvent.toCompleteEvent("findEvalTmplStsInObdEvalSht", param); // 온보딩 평가시트
		} else if("PE".equals(evalTaskTypCcd)){
			// work_evaltmpl_uuid 존재 시, 현재 평가시트 외, 다른 평가시트에서의 평가템플릿 사용여부 조회
			// work_evaltmpl_uuid 미존재 시, 모든 평가시트에서의 평가템플릿 사용여부 조회
			event = CustomSpringEvent.toCompleteEvent("findEvalTmplStsInPfmcEvalSht", param); // 퍼포먼스 평가시트
		} else if("NPE".equals(evalTaskTypCcd)){
			// work_evaltmpl_uuid 존재 시, 현재 평가시트 외, 다른 평가시트에서의 평가템플릿 사용여부 조회
			// work_evaltmpl_uuid 미존재 시, 모든 평가시트에서의 평가템플릿 사용여부 조회
			event = CustomSpringEvent.toCompleteEvent("findEvalTmplStsInNpeEvalSht", param); // 비가격평가 평가시트
		} else if("GE".equals(evalTaskTypCcd)){
			// work_evaltmpl_uuid 존재 시, 현재 평가시트 외, 다른 평가시트에서의 평가템플릿 사용여부 조회
			// work_evaltmpl_uuid 미존재 시, 모든 평가시트에서의 평가템플릿 사용여부 조회
            event = CustomSpringEvent.toCompleteEvent("findEvalTmplStsInGeEvalSht", param); // 입고/기성평가 평가시트
		}
		if(event != null){
			publisher.publishEvent(event);
			return (String) event.getResult();
		}else{
			return "";
		}
	}

	/**
	 * 평가템플릿 확정 가능 여부를 조회한다.
	 *
	 * @param param
	 * @return void
	 */
	public String checkEvalTmplConfirmYn(Map param) {
		String evalTaskTypCcd = param.get("eval_task_typ_ccd") == null ? "" : param.get("eval_task_typ_ccd").toString();

		CustomSpringEvent event = null;
		if("OE".equals(evalTaskTypCcd)){
			event = CustomSpringEvent.toCompleteEvent("checkObdEvalShtConfirmYnByEvalTmpl", param); // 온보딩 평가시트
		} else if("PE".equals(evalTaskTypCcd)){
			event = CustomSpringEvent.toCompleteEvent("checkPfmcEvalShtConfirmYnByEvalTmpl", param); // 퍼포먼스 평가시트
		}

		if(event != null){
			publisher.publishEvent(event);
			return (String) event.getResult();
		}else{
			return "";
		}
	}

	/**
	 * 업무 - 평가템플릿 매핑을 요청한다.
	 *
	 * @param param
	 * @return void
	 */
	public void saveMappingEvaltmplUuidToWork(Map param) {
		String evalTaskTypCcd = param.get("eval_task_typ_ccd") == null ? "" : param.get("eval_task_typ_ccd").toString();

		CustomSpringEvent event = null;
		if("OE".equals(evalTaskTypCcd)){
			event = CustomSpringEvent.toCompleteEvent("saveMappingEvaltmplUuidToObdEvalshtPrcs", param); // 온보딩 평가시트 프로세스
		} else if("PE".equals(evalTaskTypCcd)){
			event = CustomSpringEvent.toCompleteEvent("saveMappingEvaltmplUuidToPfmcEvalsht", param); // 퍼포먼스 평가시트
		} else if("GE".equals(evalTaskTypCcd)) {
			event = CustomSpringEvent.toCompleteEvent("saveMappingEvaltmplUuidToGrEvalshtPrcs", param); // 입고 평가시트 프로세스
		} else if("NPE".equals(evalTaskTypCcd)) {
			event = CustomSpringEvent.toCompleteEvent("saveMappingEvaltmplUuidToNpeEvalshtPrcs", param);
		}
		publisher.publishEvent(event);
	}

	/**
	 * 업무 - 수정 전 원본 평가템플릿 매핑을 요청한다.
	 *
	 * @param param
	 * @return void
	 */
	public void saveMappingOrgnEvaltmplUuidToWork(Map param) {
		String evalTaskTypCcd = param.get("eval_task_typ_ccd") == null ? "" : param.get("eval_task_typ_ccd").toString();

		CustomSpringEvent event = null;
		if("OE".equals(evalTaskTypCcd)){
			event = CustomSpringEvent.toCompleteEvent("saveMappingOrgnObdEvaltmplUuidToObdEvalsht", param); // 온보딩 평가시트 프로세스
		} else if("PE".equals(evalTaskTypCcd)){
			event = CustomSpringEvent.toCompleteEvent("saveMappingOrgnPfmcEvaltmplUuidToPfmcEvalsht", param); // 퍼포먼스 평가시트
		}
		publisher.publishEvent(event);
	}
}