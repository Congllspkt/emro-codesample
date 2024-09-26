package smartsuite.app.bp.commonEval.evalResult.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.commonEval.evalResult.service.EvalResultService;
import smartsuite.app.bp.commonEval.evalResult.service.EvaltrEvalfactResultService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class EvalResultEventListener {

	@Inject
	EvalResultService evalResultService;

	@Inject
	EvaltrEvalfactResultService evaltrEvalfactResultService;

	/**
	 * 평가대상결과를 여러 건 생성한다.
	 * <br><br>
	 * <b>Required:</b><br>
	 * param.ten_id - 시스템아이디 <br>
	 * param.evalSubjList - 평가대상 목록 <br>
	 * param.eval_req_uuid - 평가요청 아이디 <br>
	 * <b>Optional:</b><br>
	 * param.evalSubjList.evalSubjEvaltrList - 평가대상 평가자 목록 <br>
	 * <br>
	 * <b>return event.data</b>
	 * ResultMap.resultStatus - String "S" || "F" (평가대상 결과 생성 성공 시 "S", 실패 시 "F")<br>
	 * ResultMap.resultMessage - String (평가대상 결과 생성 실패 시 실패 사유)<br>
	 * ResultMap.resultData - Map { create_prgs_sts : "QUANT" || "QUALI" }  ( 평가 진행 상태. QUANT : 정량 평가 상태(정량항목 존재, 정성항목 미존재 시), QUALI : 정성 평가 상태(정성평가 존재시))<br>
	 * ResultMap.resultList - List [ {ten_id : tenant, <b>eval_subj_res_uuid : uuid, eval_sc : number</b>, evalSubjEvaltrList : [{ <b>eval_subj_evaltr_res_uuid : uuid </b>}] } ]<br>
	 */
	@EventListener(condition = "#event.eventId == 'createEvalSubjResEvalReq'")
	public void createEvalSubjResEvalReq(CustomSpringEvent event) {

		ResultMap validator = evalResultService.createEvalSubjResEvalReq((Map) event.getData());
		event.setResult(validator);
	}

	/**
	 * 단 건의 평가대상에 대해 평가대상 결과, 평가자 결과, 평가대상 평가항목 결과를 생성한다.
	 * <br><br>
	 * <b>Required:</b><br>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_task_typ_ccd - 업무유형 공통코드 <br>
	 * param.evamltmpl_uuid - 평가템플릿 아이디 <br>
	 * return event.data <br>
	 * ResultMap <br>
	 * ResultMap.resultStatus - String "S" || "F" (평가대상 결과 생성 성공 시 "S", 실패 시 "F")<br>
	 * ResultMap.resultMessage - String (평가대상 결과 생성 실패 시 실패 사유)<br>
	 * ResultMap.resultData - {eval_subj_res_uuid : uuid, eval_sc : number, create_prgs_sts : "QUANT" or "QUALI"} (생성된 평가 대상의 평가 대상 결과 아이디, 평가 생성 시 정량항목 계산된 점수, 평가 생성 상태)
	 */
	@EventListener(condition = "#event.eventId == 'createEvalSubjResAdd'")
	public void createEvalSubjResAdd(CustomSpringEvent event) {

		ResultMap validator = evalResultService.createEvalSubjResAdd((Map) event.getData());
		event.setResult(validator);
	}

	@EventListener(condition = "#event.eventId == 'createEvalSubjRes'")
	public void createEvalSubjRes(CustomSpringEvent event) {

		ResultMap validator = evalResultService.createEvalSubjResAdd((Map) event.getData());
		event.setResult(validator);
	}

	/**
	 * 평가대상결과를 삭제한다.
	 * <br><br>
	 * <b>Required:</b><br>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.eval_subj_res_uuid - 평가 대상 결과 아이디 <br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'deleteEvalSubjRes'")
	public void deleteEvalSubjRes(CustomSpringEvent event) {

		ResultMap validator = evalResultService.deleteEvalSubjRes((Map) event.getData());
		event.setResult(validator);
	}


	/**
	 * 평가대상 평가자결과를 생성한다.
	 * <br><br>
	 * <b>Required:</b><br>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_task_typ_ccd - 업무유형 공통코드
	 * param.oorg_cd - 운영조직 코드
	 * param.evamltmpl_uuid - 평가템플릿 아이디
	 * param.eval_subj_res_uuid - 평가대상 아이디
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'createEvalSubjEvaltrRes'")
	public void createEvalSubjEvaltrRes(CustomSpringEvent event) {

		ResultMap validator = evalResultService.createEvalSubjEvaltrRes((Map) event.getData());
		event.setResult(validator);
	}

	/**
	 * 평가대상 평가자 결과를 삭제한다.
	 * <br><br> 필수 속성 그룹 A 혹은 B
	 * <b>Required A</b><br>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가대상 평가자 결과 아이디 <br>
	 * <b> Required B</b>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.eval_subj_res_uuid - 평가대상 결과 아이디 <br>
	 * param.evalfact_evaltr_authty_ccd - 평가항목 평가자 권한 공통코드 <br>
	 * param.evaltr_id - 평가자 아이디 <br>
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'deleteEvalSubjEvaltrRes'")
	public void deleteEvalSubjEvaltrRes(CustomSpringEvent event) {

		ResultMap validator = evalResultService.deleteEvalSubjEvaltrRes((Map) event.getData());
		event.setResult(validator);
	}
	/**
	 * 평가대상 평가자 결과의 평가자아이디를 변경한다.
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가 대상 평가자 결과 아이디 <br>
	 * param.evaltr_id - 변경할 평가자 아이디 <br>
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'changeEvalSubjEvaltrRes'")
	public void changeEvalSubjEvaltrRes(CustomSpringEvent event) {

		ResultMap validator = evalResultService.changeEvalSubjEvaltrRes((Map) event.getData());
		event.setResult(validator);
	}

	/**
	 * 평가대상 평가자 결과의 평가자아이디를 변경한다.
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_req_uuid - 평가 요청 아이디 <br>
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'deleteEvalByEvalReqUuid'")
	public void deleteEvalByEvalReqUuid(CustomSpringEvent event) {

		ResultMap validator = evalResultService.deleteEvalByEvalReqUuid((Map) event.getData());
		event.setResult(validator);
	}
	/**
	 * 정성평가 진행상태 "제출" 상태를 취소한다.
	 * @param event
	 * @return event
	 * @Date : 2023. 7. 6
	 * @Method Name : updateCancleSubmEvaltrPrgsSts
	 */
	@EventListener(condition = "#event.eventId == 'updateCancleSubmEvaltrPrgsSts'")
	public void updateCancleSubmEvaltrPrgsSts(CustomSpringEvent event) {

		ResultMap validator = evalResultService.updateCancleSubmEvaltrPrgsSts((Map) event.getData());
		event.setResult(validator);
	}

	/**
	 * 평가자 평가항목 결과를 저장한다. (평가 수행 후 저장)
	 * <br><br>
	 * <b>Required:</b><br>
	 * param.ten_id - 테넌트 아이디
	 * param.evalSubjMap - 평가대상 정보 <br>
	 * param.saveEvaltmplList - 저장할 평가템플릿 목록 <br>
	 * param.saveEvalfactList - 저장할 평가항목 목록 <br>
	 * param.saveEvalfactScaleList - 저장할 평가항목 스케일 목록 <br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'saveEvaltrEvalfactRes'")
	public void saveEvaltrEvalfactRes(CustomSpringEvent event) {

		ResultMap saveResult = evalResultService.saveEvaltrEvalfactRes((Map) event.getData());
		event.setResult(saveResult);
	}

	/**
	 * 평가대상 평가항목 결과를 조회한다.
	 * @param event
	 * @return event
	 * @Date : 2023. 7. 16
	 * @Method Name : findListEvalSubjEvalfactRes
	 */
	@EventListener(condition = "#event.eventId == 'findListEvalSubjEvalfactRes'")
	public void findListEvalSubjEvalfactRes(CustomSpringEvent event) {

		List evalSubjEvalfactResList = evalResultService.findListEvalSubjEvalfactRes((Map) event.getData());
		event.setResult(evalSubjEvalfactResList);
	}

	/**
	 * 평가대상 결과 목록을 조회한다.
	 * @param event
	 * @return event
	 * @Date : 2023. 7. 24
	 * @Method Name : findListEvalSubjRes
	 */
	@EventListener(condition = "#event.eventId == 'findListEvalSubjRes'")
	public void findListEvalSubjRes(CustomSpringEvent event) {

		List evalSubjResList = evalResultService.findListEvalSubjRes((Map) event.getData());
		event.setResult(evalSubjResList);
	}

	/**
	 * 평가대상 계산항목 목록을 조회한다.
	 * @param event
	 * @return event
	 * @Date : 2023. 7. 24
	 * @Method Name : findListEvalSubjCalcfactRes
	 */
	@EventListener(condition = "#event.eventId == 'findListEvalSubjCalcfactRes'")
	public void findListEvalSubjCalcfactRes(CustomSpringEvent event) {

		List evalSubjCalcfactResList = evalResultService.findListEvalSubjCalcfactRes((Map) event.getData());
		event.setResult(evalSubjCalcfactResList);
	}

	/**
	 * 평가요청 건에 대한 평가 대상 점수를 계산한다. (계산항목 집계, 평가항목 점수, 평가항목군 점수 재계산한 후 평가대상 점수 산출)
	 * @param event
	 * @return event
	 * @Date : 2023. 7. 24
	 * @Method Name : calculateEvalSubjResByEvalReqWithCalcfact
	 */
	@EventListener(condition = "#event.eventId == 'calculateEvalSubjResByEvalReqWithCalcfact'")
	public void calculateEvalSubjResByEvalReqWithCalcfact(CustomSpringEvent event) {

		ResultMap calculateResult = evalResultService.calculateEvalSubjResByEvalReqWithCalcfact((Map) event.getData());
		event.setResult(calculateResult);
	}

	/**
	 * 정량항목 점수를 재계산한다. (계산항목 집계하지 않고 평가 정량 항목 점수, 평가항목군 점수 재계산한 후 평가대상 점수 산출)
	 * @param event
	 * @return event
	 * @Date : 2023. 7. 26
	 * @Method Name : reCalculateQuantEvalfact
	 */
	@EventListener(condition = "#event.eventId == 'reCalculateQuantEvalfact'")
	public void reCalculateQuantEvalfact(CustomSpringEvent event) {

		ResultMap calculateResult = evalResultService.reCalculateQuantEvalfact((Map) event.getData());
		event.setResult(calculateResult);
	}

	/**
	 * 계산항목 점수를 재집계한다. (계산항목 집계 & 정량 평가항목 점수 계산 & 정성 평가자 존재 시 정성평가자 점수에 반영. 항목군점수 & 평가대상 결과에는 반영 X)
	 * @param event
	 * @return event
	 * @Date : 2023. 7. 26
	 * @Method Name : reCalculateCalcfactVal
	 */
	@EventListener(condition = "#event.eventId == 'reCalculateCalcfactVal'")
	public void reCalculateCalcfactVal(CustomSpringEvent event) {

		ResultMap calculateResult = evalResultService.reCalculateCalcfactVal((Map) event.getData());
		event.setResult(calculateResult);
	}

	/**
	 * 평가대상 계산항목 조정점수를 수정한다.
	 * @param event
	 * @return event
	 * @Date : 2023. 7. 26
	 * @Method Name : updateEvalSubjCalcfactByAdj
	 */
	@EventListener(condition = "#event.eventId == 'updateEvalSubjCalcfactByAdj'")
	public void updateEvalSubjCalcfactByAdj(CustomSpringEvent event) {
		ResultMap updateResult = evalResultService.updateEvalSubjCalcfactByAdj((Map) event.getData());
		event.setResult(updateResult);
	}

	/**
	 * 평가자별 평가항목별 정성평가 점수를 조회한다.
	 * @param event
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디
	 * param.eval_req_uuid - 평가 요청 아이디
	 * param.eval_subj_res_uuid - 평가 대상 결과 아이디
	 * @return event
	 * @Date : 2023. 7. 24
	 * @Method Name : findListEvaltrEvalfactResSc
	 */
	@EventListener(condition = "#event.eventId == 'findListEvaltrEvalfactResSc'")
	public void findListEvaltrEvalfactResSc(CustomSpringEvent event) {
		List evaltrEvalfactResSc = evalResultService.findListEvaltrEvalfactResSc((Map) event.getData());
		event.setResult(evaltrEvalfactResSc);
	}
}
