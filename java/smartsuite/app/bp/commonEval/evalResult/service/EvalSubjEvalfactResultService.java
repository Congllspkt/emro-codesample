package smartsuite.app.bp.commonEval.evalResult.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.commonEval.common.EvalConst;
import smartsuite.app.bp.commonEval.evalResult.repository.EvalSubjEvalfactResultRepository;
import smartsuite.app.bp.commonEval.evalResult.validator.EvalResultValidator;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EvalSubjEvalfactResultService {

	@Inject
	EvalResultValidator evalValidator;

	@Inject
	EvalSubjEvalfactResultRepository evalfactResRepository;

	@Inject
	CalculateCalcfactService calcfactService;


	/**
	 * 평가항목 결과를 생성한다. <br>
	 * 평가항목군 결과, 평가항목 결과, 평가대상 실적항목 결과(정량항목 존재 시) <br><br>
	 * <b>Required:</b><br>
	 * param.ten_id - 시스템 아이디
	 * param.eval_task_typ_ccd - 평가업무유형 공통코드
	 * param.oorg_cd - 운영조직 코드 <br>
	 * param.eval_subj_uuid - 평가대상 uuid <br>
	 * param.evaltmpl_uuid - 평가 템플릿 uuid  <br>
	 * <b>Optional:</b><br>
	 * param.dfn_regr_id - 생성자 아이디 <br>
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap insertEvalSubjEvalfactRes(Map param) {

		if(!EvalConst.ALL.equals(param.getOrDefault(EvalConst.REQ_TYPE, ""))) {
			// parameter 필수값 체크 및 평가대상결과 존재여부 확인
			ResultMap validator = this.isValid(param, EvalConst.CREATE);
			if(!validator.isSuccess()) {
				return validator;
			}
		}
		// 평가항목군 결과 생성
		evalfactResRepository.insertEvalSubjEfactgRes(param);
		// 평가항목 결과 생성
		evalfactResRepository.insertEvalSubjEvalfactRes(param);

		return ResultMap.SUCCESS(param);

	}

	/**
	 * 평가항목 계산항목 결과를 생성한다. <br>
	 * <b>Required:</b><br>
	 * param.ten_id - 시스템 아이디
	 * param.eval_task_typ_ccd - 평가업무유형 공통코드
	 * param.oorg_cd - 운영조직 코드 <br>
	 * param.eval_subj_uuid - 평가대상 uuid <br>
	 * param.evaltmpl_uuid - 평가 템플릿 uuid  <br>
	 * <b>Optional:</b><br>
	 * param.dfn_regr_id - 생성자 아이디 <br>
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap insertEvalCalcfactRes(Map param) {
		evalfactResRepository.insertEvalCalcfactRes(param);
		return ResultMap.SUCCESS();
	}
	/**
	 * 평가대상에 해당하는 평가항목 결과를 삭제한다. <br>
	 *
	 * <b>Required:</b><br>
	 * param.eval_subj_uuid - 평가대상 uuid <br>
	 */
	public void deleteEvalSubjEvalfactResByEvalSubjResUuid(Map param) {
		param.put(EvalConst.REQ_TYPE, EvalConst.SUBJ);
		evalfactResRepository.deleteEvalSubjEfactgRes(param);
		evalfactResRepository.deleteEvalSubjEvalfactRes(param);
		evalfactResRepository.deleteEvalCalcfactRes(param);
	}

	public ResultMap deleteEvalSubjEvalfactRes(Map param) {

		param.put(EvalConst.REQ_TYPE, EvalConst.ALL);
		evalfactResRepository.deleteEvalSubjEfactgRes(param);
		evalfactResRepository.deleteEvalSubjEvalfactRes(param);
		// 실적항목 결과 삭제. 정량항목 존재 여부 판단 후 실행?
		evalfactResRepository.deleteEvalCalcfactRes(param);
		return ResultMap.SUCCESS();

	}

	private ResultMap isValid(Map param, String reqType) {
		ResultMap validator = evalValidator.validEvalFactResParam(param);
		if(!validator.isSuccess()) {
			return validator;
		}

		// 평가항목 결과 이미 존재하는 경우 FAIL로 return
		return evalValidator.validEvalFactResSts(param, reqType);
	}

	/**
	 * 정량/정성 평가항목 존재 여부를 반환한다. <br><br>
	 *<b>Required: </b>
	 * param.evaltmpl_uuid
	 * @param param the param
	 * @param checkCls the str (QUANT / QUALI)
	 * @return the boolean
	 */
	private String checkEvalfactTyp(Map<String, Object> param, final String checkCls) {
		if (EvalConst.QUANT.equals(checkCls)) { // 정량 'QUALI'
			param.remove(EvalConst.EVALFACT_TYP_CCD_LIST);
			param.put(EvalConst.EVALFACT_TYP_CCD_LIST, Arrays.asList(EvalConst.QUANT_DIV_CCD_LIST));
		} else if (EvalConst.QUALI.equals(checkCls)) { // 정성 'QUALI', 'QUALI_MULTPL_SET', 'QUALI_SC_INP'
			param.remove(EvalConst.EVALFACT_TYP_CCD_LIST);
			param.put(EvalConst.EVALFACT_TYP_CCD_LIST, Arrays.asList(EvalConst.QUALI_DIV_CCD_LIST));
		} else {
			return EvalConst.N_STR_VAL;
		}
		// tmpl에 만들기
		//return evalfactResRepository.checkEvalfactTyp(param);

		return EvalConst.N_STR_VAL;
	}

	/**
	 * 평가대상의 평가항목별로 점수를 계산한다. (평가요청 기준)
	 * @param evalSubjMap
	 * evalSubjMap.ten_id
	 * evalSubjMap.req_type
	 * evalSubjMap.calc_subj_evalfact_typ
	 * evalSubjMap.eval_req_uuid
	 * @return void
	 */
	public void calculateEvalfactScByEvalReq(Map<String, Object> evalSubjMap) {
		Map<String, Object> calcMap = Maps.newHashMap(evalSubjMap);
		calcMap.put(EvalConst.REQ_TYPE, EvalConst.ALL); // 평가요청 아이디에 해당하는 평가대상 전체에 대해 집계 요청

		this.calculateEvalfactSc(calcMap);
	}

	/**
	 * 평가대상의 평가항목별로 점수를 계산한다. (평가대상 단건 기준)
	 * @param evalSubjMap
	 * evalSubjMap.ten_id
	 * evalSubjMap.req_type
	 * evalSubjMap.calc_subj_evalfact_typ
	 * evalSubjMap.eval_req_uuid
	 * evalSubjMap.eval_subj_res_uuid
	 * @return void
	 */
	public void calculateEvalfactScByEvalSubj(Map<String, Object> evalSubjMap, String tenId) {
		Map<String, Object> calcMap = Maps.newHashMap(evalSubjMap);
		calcMap.put(EvalConst.TEN_ID, tenId);
		calcMap.put(EvalConst.REQ_TYPE, EvalConst.SUBJ); // 평가대상 한 건에 대해서만 집계 요청
		calcMap.put(EvalConst.CALC_SUBJ_EVALFACT_TYP, EvalConst.QUALI);  // 정성평가 항목에 대해서만 집계 요청

		this.calculateEvalfactSc(calcMap);
	}

	/**
	 * 평가대상의 평가항목 점수를 계산한다.
	 * @param param
	 * param.ten_id
	 * param.req_type
	 * param.calc_subj_evalfact_typ (quali / quant / all)
	 * param.eval_req_uuid
	 * param.eval_subj_res_uuid
	 * @return void
	 */
	public void calculateEvalfactSc (Map<String, Object> param) {
		final String hasQualiYn = this.checkEvalfactTypCcd(param, EvalConst.QUALI);		// 정성항목 존재여부
		final String hasQuantYn = this.checkEvalfactTypCcd(param, EvalConst.QUANT);		// 정량항목 존재여부

		final String calcSubjEvalfactTyp = (String)param.getOrDefault(EvalConst.CALC_SUBJ_EVALFACT_TYP, EvalConst.ALL);	// 집계 대상 정량정성 구분. QUANT / QUALI / ALL

		final Boolean isCalcQuali = EvalConst.Y_STR_VAL.equals(hasQualiYn) && (EvalConst.QUALI.equals(calcSubjEvalfactTyp) || EvalConst.ALL.equals(calcSubjEvalfactTyp));
		final Boolean isCalcQuant = EvalConst.Y_STR_VAL.equals(hasQuantYn) && (EvalConst.QUANT.equals(calcSubjEvalfactTyp) || EvalConst.ALL.equals(calcSubjEvalfactTyp));

		// 정성 평가항목 결과 집계
		if(isCalcQuali) {
			this.calculateEvalSubjQualiEvalfactSc(param);
		}

		// 평가항목 결과 (정량)
		if(isCalcQuant) {
			this.calculateEvalSubjQuantEvalfactSc(param);
		}

		// 정성평가 결과 대상  또는 정량평가 집계 대상인 경우
		if(isCalcQuali || isCalcQuant) {
			// 평가항목군 결과
			this.calculateEvalSubjEfactgSc(param);
		}
	}

	/**
	 * 평가대상의 평가항목 정성 점수를 계산한다.
	 * @param param
	 * @return void
	 */
	public void calculateEvalSubjQualiEvalfactSc (Map<String, Object> param) {
		Map<String, Object> qualiInfo = Maps.newHashMap(param);
		final String[] addCndCcdList = { EvalConst.EVALFACT_TYP_EXPNT, EvalConst.EVALFACT_TYP_DEDTN}; // 가점, 감점

		qualiInfo.put(EvalConst.EVALFACT_TYP_CCD_LIST, Arrays.asList(EvalConst.QUALI_DIV_CCD_LIST)); // 정성 구분 공통코드 목록

		qualiInfo.remove(EvalConst.ADD_CND_CCD);
		qualiInfo.remove(EvalConst.ADD_CND_CCD_LIST);

		// 정성평가 항목 결과 집계 (일반항목)
		qualiInfo.put(EvalConst.ADD_CND_CCD, EvalConst.EVALFACT_TYP_GEN);
		evalfactResRepository.calculateQualiEvalSubjEvalfactSc(qualiInfo);

		// 정성평가 항목 결과 집계 (가점, 감점)
		qualiInfo.put(EvalConst.ADD_CND_CCD_LIST, Arrays.asList(addCndCcdList));
		evalfactResRepository.calculateQualiEvalSubjEvalfactSc(qualiInfo);

	}

	/**
	 * 평가대상의 평가항목 정량 점수를 계산한다.
	 * @param param
	 * @return void
	 */
	public void calculateEvalSubjQuantEvalfactSc(Map<String, Object> param) {
		// 계산항목 값 수집 요청 여부
		if(EvalConst.Y_STR_VAL.equals((String)param.getOrDefault(EvalConst.COLL_VAL_REQ_YN, EvalConst.N_STR_VAL))) {
			// 계산항목 재수집 요청 여부
			param.put(EvalConst.CALCFACT_RE_COLL_REQ_YN, param.getOrDefault(EvalConst.CALCFACT_RE_COLL_REQ_YN, EvalConst.N_STR_VAL));
			// 계산항목 값 수집(CALCFACT)
			calcfactService.calculateCalcfactValResult(param);
		}
		// 정량항목 결과 계산(QUANT_EVALFACT)
		calcfactService.calculateQuantEvalfactResult(param);

		// 정량항목 결과를 담당자별 평가점수에 반영
		this.updateEvaltrScByQuantEvalfactSc(param);

	}

	// 정량항목 결과 담당자별 평가점수에 반영
	public void updateEvaltrScByQuantEvalfactSc(Map<String, Object> param) {
		evalfactResRepository.updateEvaltrScByQuantEvalfactSc(param);
	}

	// 정량 / 정성 평가 여부를 반환한다.
	public String checkEvalfactTypCcd(Map<String, Object> evalSubjMap, String evalfactTypStr) {
		if(EvalConst.QUANT.equals(evalfactTypStr)) { // QUANT : 정량
			evalSubjMap.remove(EvalConst.EVALFACT_TYP_CCD_LIST);
			evalSubjMap.put(EvalConst.EVALFACT_TYP_CCD, EvalConst.QUANT);
		} else if(EvalConst.QUALI.equals(evalfactTypStr)) { // QUALI :  정성
			evalSubjMap.remove(EvalConst.EVALFACT_TYP_CCD_LIST);
			evalSubjMap.put(EvalConst.EVALFACT_TYP_CCD_LIST, Arrays.asList(EvalConst.QUALI_DIV_CCD_LIST));
		} else {
			return EvalConst.N_STR_VAL;
		}
		return evalfactResRepository.getEvalfactTypEvalfactExistYn(evalSubjMap);
	}

	/**
	 * 평가대상의 평가항목별로 점수를 계산한다.
	 * @param param
	 * @return void
	 */
	public void calculateEvalSubjEfactgSc (Map<String, Object> param) {
		Map<String, Object> qualiInfo = Maps.newHashMap(param);

		// 평가항목군 결과 집계
		evalfactResRepository.calculateEvalSubjEfactgSc(qualiInfo);
	}

	/**
	 * 평가요청의 평가대상 평가항목 결과를 조회한다.
	 * @param param
	 * @return List
	 */
	public List findListEvalSubjEvalfactRes (Map<String, Object> param) {

		return evalfactResRepository.findListEvalSubjEvalfactRes(param);
	}

	/**
	 * 평가요청의 평가대상 계산항목 결과를 조회한다.
	 * @param param
	 * @return List
	 *  */
	public List findListEvalSubjCalcfactRes (Map<String, Object> param) {
		return evalfactResRepository.findListEvalSubjCalcfactRes(param);
	}

	/**
	 * 평가요청 건에 대한 평가점수를 집계한다. (평가항목군 및 평가항목 점수, 평가대상 점수)
	 * @param param
	 * @return void
	 */
	public void updateEvalSubjCalcfactByAdj(Map<String, Object> param) {
		evalfactResRepository.updateEvalSubjCalcfactByAdj(param);
	}
	
	
}
