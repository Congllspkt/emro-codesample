package smartsuite.app.bp.commonEval.evalResult.service;

import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.commonEval.common.EvalConst;
import smartsuite.app.bp.commonEval.evalResult.repository.EvalResultRepository;
import smartsuite.app.bp.commonEval.evalResult.validator.EvalResultValidator;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class EvalResultService {

	@Inject
	private EvalResultRepository evalResultRepository;

	@Inject
	EvalSubjResultService evalSubjResService;

	@Inject
	EvalSubjEvalfactResultService evalfactResService;

	@Inject
	EvalSubjEvaltrResultService evaltrResService;

	@Inject
	EvaltrEvalfactResultService evaltrEvalfactResService;

	@Inject
	EvalResultValidator evalResultValidator;

	private final SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

	/**
	 * 평가템플릿 사용여부를 조회한다
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 13
	 */
	public String selectEvalTmplUsedYnInEvalResult(Map<String, Object> param){
		return evalResultRepository.selectEvalTmplUsedYnInEvalResult(param);
	}

	/**
	 * 평가항목 결과 테이블의 평가항목 확인
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 13
	 */
	public String selectEvalFactorUsedYnInEvalResult(Map<String, Object> param){
		return evalResultRepository.selectEvalFactorUsedYnInEvalResult(param);
	}
	/**
	 * 평가대상결과를 여러 건 생성한다.
	 * <br>param<br>
	 * <b>Required:</b><br>
	 * param.ten_id - 시스템아이디 <br>
	 * param.evalSubjList - 평가대상 목록<br>
	 * param.eval_req_uuid - 평가요청 아이디<br>
	 * <b>Optional:</b><br>
	 * param.evalSubjList.evalSubjEvaltrList - 평가대상 평가자 목록<br>
	 *<br>
	 * <b>return ResultMap</b><br>
	 * ResultMap.resultStatus - String "S" || "F" (평가대상 결과 생성 성공 시 "S", 실패 시 "F")<br>
	 * ResultMap.resultMessage - String (평가대상 결과 생성 실패 시 실패 사유)<br>
	 * ResultMap.resultData - Map { create_prgs_sts : "QUANT" || "QUALI" }  ( 평가 진행 상태. QUANT : 정량 평가 상태, QUALI : 정성 평가 상태)<br>
	 * ResultMap.resultList - List [ {ten_id : tenant, <b>eval_subj_res_uuid : uuid, eval_sc : number </b>, evalSubjEvaltrList : [{ <b>eval_subj_evaltr_res_uuid : uuid</b> }] } ]<br>
	 */
	public ResultMap createEvalSubjResEvalReq(final Map param) {
		List<Map<String, Object>> evalSubjList = (List)param.getOrDefault(EvalConst.EVAL_SUBJ_LIST, Lists.newArrayList());
		String evalReqUuid = (String)param.getOrDefault(EvalConst.EVAL_REQ_UUID, "");
		String tenId = (String)param.getOrDefault(EvalConst.TEN_ID, "");
		List<Map<String, Object>> evalSubjResultList = Lists.newArrayList();

		if(evalSubjList.isEmpty()) {
			return ResultMap.FAIL("평가요청 : 평가대상 목록이 존재하지 않습니다.");
		}

		for(Map<String, Object> evalSubj : evalSubjList) {
			evalSubj.put(EvalConst.TEN_ID, tenId);
			// 평가대상 결과 및 평가대상 평가자 결과 생성
			ResultMap createEvalResultMap = this.createEvalSubjResAndEvaltr(evalSubj);
			if(createEvalResultMap.isSuccess()) {
				evalSubjResultList.add(createEvalResultMap.getResultData());
			}
		}
		// 평가항목 결과 생성 시 평가요청의 모든 평가대상에 대한 항목 결과를 생성한다.
		param.put(EvalConst.REQ_TYPE, EvalConst.ALL);
		evalfactResService.insertEvalSubjEvalfactRes(param);

		// 정량항목 존재여부
		final String hasQuantYn = evalfactResService.checkEvalfactTypCcd(param, EvalConst.QUANT);
		if(EvalConst.Y_STR_VAL.equals(hasQuantYn)) {
			// 계산항목 결과 생성. (계산항목 값 수집, 계산항목 값 계산 X)
			evalfactResService.insertEvalCalcfactRes(param);
		}

		// 정성항목 존재여부
		final String hasQualiYn = evalfactResService.checkEvalfactTypCcd(param, EvalConst.QUALI);
		if(EvalConst.N_STR_VAL.equals(hasQualiYn)) {
			// 정성 담당자 삭제
			evaltrResService.deleteEvalSubjEvaltrResByEvalReqUuid(param);
		}

		// 계산항목 수집 요청 여부 "Y"
		param.put(EvalConst.COLL_VAL_REQ_YN, "Y");
		// 평가 생성 후 평가항목 결과 및 평가대상 결과 점수를 계산한다.
		ResultMap dftCalculateResultMap = this.calculateEvalfactResAndEvalSubjResAfterCreate(param, hasQuantYn, hasQualiYn);

		if(dftCalculateResultMap.isSuccess()) {
			// 평가대상 별로 점수를 조회하여 return data에 담는다.
			for(Map<String, Object> evalSubjResult : evalSubjResultList) {
				Map<String, Object> evalSubjScoreMap = this.evalSubjResService.getEvalSubjRes(evalSubjResult);
				if(evalSubjScoreMap != null) {
					// 평가대상 점수
					evalSubjResult.put("eval_sc", evalSubjScoreMap.get("eval_sc"));
				}
			}
		}

		return ResultMap.builder().resultStatus(ResultMap.STATUS.SUCCESS).resultList(evalSubjResultList).resultData(dftCalculateResultMap.getResultData()).build();
	}

	// 평가데이터 생성 후 평가대상 평가항목 점수 및 평가대상 점수를 계산한다.
	public ResultMap calculateEvalfactResAndEvalSubjResAfterCreate(final Map param, String hasQuantYn, String hasQualiYn) {
		// COLL_VAL_REQ_YN == "Y" : 정량 계산 시에 계산항목을 수집한다.
		param.put(EvalConst.COLL_VAL_REQ_YN, EvalConst.Y_STR_VAL);

		Map<String, Object> resultData = Maps.newHashMap();

		// 정량/정성 항목 모두 존재 : 계산항목 수집 후 정량 항목 계산 & 정성평가자 담당자 점수에 정량항목 점수 반영 (항목군 점수, 평가대상 점수에 반영 X)
		if (EvalConst.Y_STR_VAL.equals(hasQuantYn) && EvalConst.Y_STR_VAL.equals(hasQualiYn)) {
			evalfactResService.calculateEvalSubjQuantEvalfactSc(param);

			// 정성평가 수행상태
			resultData.put(EvalConst.CREATE_PRGS_STS, EvalConst.QUALI);
		} 
		// 정량 항목 존재, 정성 항목 미존재 : 계산항목 집계 후 정량 항목 계산 & 평가 항목 계산 & 평가 항목군 점수 계산 & 평가대상 점수 계산
		else if(EvalConst.Y_STR_VAL.equals(hasQuantYn) && !EvalConst.Y_STR_VAL.equals(hasQualiYn)){
			// 계산항목, 평가항목, 평가항목군 점수 계산 (평가요청 기준)
			// 평가대상 점수 계산
			this.calculateEvalSubjResAndEvalfactResByEvalReq(param);

			// 정량평가 수행상태
			resultData.put(EvalConst.CREATE_PRGS_STS, EvalConst.QUANT);
		}
		// 정성 항목 존재, 정량 항목 미존재 : 점수 계산 할 것 없음.
		else {
			// 정성평가 수행상태
			resultData.put(EvalConst.CREATE_PRGS_STS, EvalConst.QUALI);
		}
		return ResultMap.SUCCESS(resultData);
	}

	public ResultMap createEvalSubjResAndEvaltr(final Map param) {
		// 평가대상 결과 생성
		ResultMap evalSubjResultMap = evalSubjResService.insertEvalSubjRes(param);
		if(!evalSubjResultMap.isSuccess()) {
			return evalSubjResultMap;
		}
		Map<String, Object> evalSubjResultData = evalSubjResultMap.getResultData();

		// 평가자 결과 생성
		List<Map<String, Object>> evalSubjEvaltrList = (List)evalSubjResultData.getOrDefault(EvalConst.EVAL_SUBJ_EVALTR_LIST, Lists.newArrayList());
		if(evalSubjEvaltrList.isEmpty()) {
			return ResultMap.SUCCESS(evalSubjResultData);
		}

		for(Map<String, Object> evalSubjEvaltr : evalSubjEvaltrList) {
			evalSubjEvaltr.put(EvalConst.TEN_ID, evalSubjResultData.getOrDefault(EvalConst.TEN_ID, ""));
			evalSubjEvaltr.put(EvalConst.EVAL_SUBJ_RES_UUID, evalSubjResultData.getOrDefault(EvalConst.EVAL_SUBJ_RES_UUID, ""));
			ResultMap evaltrResultMap = evaltrResService.insertEvalSubjEvaltrRes(evalSubjEvaltr);
			Map evaltrResultData = evaltrResultMap.getResultData();
			if(evaltrResultMap.isSuccess()) {
				evalSubjEvaltr.put(EvalConst.EVAL_SUBJ_EVALTR_RES_UUID, evaltrResultMap.getResultData().getOrDefault(EvalConst.EVAL_SUBJ_EVALTR_RES_UUID, ""));
			} else {
				evalSubjEvaltr.put(EvalConst.EVAL_SUBJ_EVALTR_RES_UUID, "");
			}
		}
		param.put(EvalConst.REQ_TYPE, EvalConst.SUBJ);

		return ResultMap.SUCCESS(evalSubjResultData);

	}

	/**
	 * 단 건의 평가대상에 대해 평가대상 결과, 평가자 결과, 평가대상 평가항목 결과를 생성한다.
	 * <br><br>
	 * <b>Required:</b><br>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_task_typ_ccd - 업무유형 공통코드 <br>
	 * param.evamltmpl_uuid - 평가템플릿 아이디 <br>
	 * return ResultMap <br>
	 * ResultMap.resultStatus - String "S" || "F" (평가대상 결과 생성 성공 시 "S", 실패 시 "F")<br>
	 * ResultMap.resultMessage - String (평가대상 결과 생성 실패 시 실패 사유)<br>
	 * ResultMap.resultData - {eval_subj_res_uuid : uuid, eval_sc : number, create_prgs_sts : "QUANT" or "QUALI} (생성된 평가 대상의 평가 대상 결과 아이디 , 정량항목 계산된 점수, 평가상태)
	 */
	public ResultMap createEvalSubjResAdd(final Map param) {
		// 0. 평가대상결과 생성 결과맵
		ResultMap createResultMap = null;
		Map createResultData = Maps.newHashMap(param);

		// 1. 평가 대상 및 평가 대상 평가자 결과 생성
		ResultMap evalSubjResultMap = this.createEvalSubjResAndEvaltr(createResultData);
		if(!evalSubjResultMap.isSuccess()) {
			createResultMap = evalSubjResultMap;
			return createResultMap;
		}

		// 2. 평가대상 평가항목 결과 생성
		createResultData.put(EvalConst.EVAL_SUBJ_RES_UUID, evalSubjResultMap.getResultData().getOrDefault(EvalConst.EVAL_SUBJ_RES_UUID, ""));
		createResultData.put(EvalConst.REQ_TYPE, EvalConst.SUBJ); // 평가항목 결과 생성 시 평가대상 단건에 대한 결과만 생성한다.
		ResultMap evalfactResultMap = evalfactResService.insertEvalSubjEvalfactRes(createResultData);
		if(!evalfactResultMap.isSuccess()) {
			createResultMap = evalfactResultMap;
			return createResultMap;
		}

		// 정량항목 존재여부
		final String hasQuantYn = evalfactResService.checkEvalfactTypCcd(createResultData, EvalConst.QUANT);
		if(EvalConst.Y_STR_VAL.equals(hasQuantYn)) {
			// 계산항목 결과 생성.
			evalfactResService.insertEvalCalcfactRes(createResultData);
		}

		// 정성항목 존재여부
		final String hasQualiYn = evalfactResService.checkEvalfactTypCcd(createResultData, EvalConst.QUALI);
		if(EvalConst.N_STR_VAL.equals(hasQualiYn)) {
			// 정성 담당자 삭제
			evaltrResService.deleteEvalSubjEvaltrResByEvalReqUuid(createResultData);
		}

		// 계산항목 수집 요청 여부 "Y" (평가대상이 추가되었으므로 다시 평가점수 수집, 계산)
		createResultData.put(EvalConst.COLL_VAL_REQ_YN, "Y");
		ResultMap dftCalculateResultMap = this.calculateEvalfactResAndEvalSubjResAfterCreate(createResultData, hasQuantYn, hasQualiYn);

		if(dftCalculateResultMap.isSuccess()) {
			// 평가대상 별로 점수를 조회하여 return data에 담는다.
			Map<String, Object> evalSubjScoreMap = this.evalSubjResService.getEvalSubjRes(evalSubjResultMap.getResultData());
			if(evalSubjScoreMap != null) {
				// 평가대상 점수
				createResultData.put("eval_sc", evalSubjScoreMap.get("eval_sc"));
			}

			// 평가 결과 생성 상태를 저장한다.
			createResultData.put(EvalConst.CREATE_PRGS_STS, dftCalculateResultMap.getResultData().get(EvalConst.CREATE_PRGS_STS));
		}

		return createResultMap.SUCCESS(createResultData);
	}

	/**
	 * 평가대상결과를 삭제한다.
	 * <br>param<br>
	 * <b>Required:</b><br>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.eval_subj_res_uuid - 평가 대상 결과 아이디 <br>
	 * <br>
	 * return resultMap
	 */
	public ResultMap deleteEvalSubjRes(Map param) {

		return evalSubjResService.deleteEvalSubjRes(param);
	}

	// 평가대상 평가자 결과 생성
	public ResultMap createEvalSubjEvaltrRes(final Map param) {

		// [TODO HJ.JANG] duplicated인 경우 삭제하고 다시 생성할지 결정 필요
		ResultMap evaltrResultMap = evaltrResService.insertEvalSubjEvaltrRes(param);
		Map evaltrResultData = evaltrResultMap.getResultData();

		// 평가자점수에 정량항목 점수 update.
		if(evaltrResultMap.isSuccess()) {
			evaltrResultData.put(EvalConst.REQ_TYPE, EvalConst.EVALTR);
			evalfactResService.updateEvaltrScByQuantEvalfactSc(evaltrResultData);
		}
		return evaltrResultMap;
	}

	// 평가대상 평가자 결과 삭제
	public ResultMap deleteEvalSubjEvaltrRes(Map param) {

		return evaltrResService.deleteEvalSubjEvaltrRes(param);
	}

	/**
	 * 평가항목 평가자 결과의 평가자 아이디를 변경한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * <b>Required</b><br>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가대상 평가자 결과 아이디 <br>
	 * @Date : 2023. 7. 06
	 */
	public ResultMap changeEvalSubjEvaltrRes(Map param) {

		// 평가대상 평가자 결과 데이터의 평가자 아이디 변경
		return evaltrResService.changeEvalSubjEvaltrRes(param);
	}

	/**
	 * 평가 요청 아이디로 평가 전체 데이터를 삭제한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * <b>Required</b><br>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_req_uuid - 평가 요청 아이디 <br>
	 * @Date : 2023. 7. 08
	 */
	public ResultMap deleteEvalByEvalReqUuid(Map param) {
		// 평가자 평가항목 결과 삭제
		//evaltrEvalfactResService.deleteEvaltrEvalfactResByEvalReqUuid(param);

		// 평가대상 평가자 결과 삭제
		//evaltrResService.deleteEvalSubjEvaltrResByEvalReqUuid(param);

		// 평가대상 결과
		return evalSubjResService.deleteEvalSubjResByEvalReqUuid(param);
	}

	/**
	 * 정성평가 진행상태 "제출" 상태를 취소한다.
	 * @author : hj.jang
	 * @param param the param
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_subj_res_uuid - 평가 대상 평가자 결과 아이디 (pe_uuid) <br>
	 * @return the ResultMap
	 * @Date : 2023. 7. 6
	 * @Method Name : updateCancleSubmEvaltrPrgsSts
	 */
	public ResultMap updateCancleSubmEvaltrPrgsSts(Map param) {

		// 정성평가 진행상태를 "저장" 으로 변경
		return evaltrResService.updateCancleSubmEvaltrPrgsSts(param);
	}

	/**
	 * 평가자 평가항목 결과를 저장한다.
	 * @author : hj.jang
	 * @param param the param
	 * <b>Required</b>
	 * param.ten_id - 테넌트 아이디
	 * param.evalSubjMap - 평가대상 정보 <br>
	 * param.saveEvaltmplList - 저장할 평가템플릿 목록 <br>
	 * param.saveEvalfactList - 저장할 평가항목 목록 <br>
	 * param.saveEvalfactScaleList - 저장할 평가항목 스케일 목록 <br>
	 * @return the ResultMap
	 * @Date : 2023. 7. 6
	 * @Method Name : saveEvaltrEvalfactRes
	 */
	public ResultMap saveEvaltrEvalfactRes(Map param) {
		String tenId = (String)param.getOrDefault(EvalConst.TEN_ID, "");
		ResultMap validResultMap = evalResultValidator.validSaveEvaltrEvalfactResParam(param);

		if(!validResultMap.isSuccess()) {
			return validResultMap;
		}

		Map<String, Object> evalSubjMap = (Map<String, Object>)param.getOrDefault("evalSubjMap", Maps.newHashMap());
		List<Map<String, Object>> saveEvaltmplList = (List<Map<String, Object>>)param.getOrDefault("saveEvaltmplList", Lists.newArrayList());
		List<Map<String, Object>> saveEvalfactScaleList = (List<Map<String, Object>>)param.getOrDefault("saveEvalfactScaleList", Lists.newArrayList());

		// 일괄적용 처리 기능 사용 여부 확인 필요
		// 1. 평가자 평가항목 결과 저장 (평가항목 스케일 코드, 점수, 첨부파일 아이디, 항목의견 저장)
		if(!saveEvalfactScaleList.isEmpty()) {
			evaltrEvalfactResService.saveEvaltrEvalfactScaleFulfill(evalSubjMap, saveEvalfactScaleList, tenId);
		}

		// 2. 평가자 첨부파일/ 상태 수정 (eval_subj_evaltr_res)
		if(!evalSubjMap.isEmpty()) {
			evaltrResService.saveEvalSubjEvaltrFulfill(evalSubjMap, saveEvaltmplList, tenId);
		}

		// 6. 평가대상 평가자 점수 계산
		evaltrResService.updateEvalSubjEvaltrSc(evalSubjMap, tenId);

		// 7. 평가대상 평가항목 점수 계산 (평가대상 단건 기준)
		evalfactResService.calculateEvalfactScByEvalSubj(evalSubjMap, tenId);

		// 8. 평가대상 점수 계산 (평가대상 단건 기준)
		evalSubjResService.calculateEvalSubjResScByEvalSubj(evalSubjMap);

		// 9. 평가대상 점수, 평가자 점수 return
		Map<String, Object> evalSubjRes = evalSubjResService.getEvalSubjRes(evalSubjMap);
		Map<String, Object> evaltrRes = evaltrResService.getEvalSubjEvaltrRes(evalSubjMap);

		Map<String, Object> resultData = Maps.newHashMap(evalSubjMap);
		resultData.put("eval_sc", evalSubjRes.getOrDefault("eval_sc", null));
		resultData.put("evaltr_sc", evaltrRes.getOrDefault("evaltr_sc", null));
		resultData.put("not_subm_evaltr_cnt", evaltrRes.getOrDefault("not_subm_evaltr_cnt", null));

		return ResultMap.builder().resultStatus(ResultMap.STATUS.SUCCESS).resultData(resultData).build();
	}

	/**
	 * 평가대상 평가항목 결과를 조회한다.
	 * @author : hj.jang
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디
	 * param.eval_req_uuid - 평가 요청 아이디 (pe_uuid / oe_prcs_uuid)
	 * @return the List
	 * @Date : 2023. 7. 6
	 * @Method Name : findListEvalSubjEvalfactRes
	 */
	public List findListEvalSubjEvalfactRes(Map param) {

		// 평가대상 평가항모 결과 조회
		return evalfactResService.findListEvalSubjEvalfactRes(param);
	}

	/**
	 * 평가대상 결과를 조회한다.
	 * @author : hj.jang
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디
	 * param.eval_req_uuid - 평가 요청 아이디 (pe_uuid / oe_prcs_uuid)
	 * @return the List
	 * @Date : 2023. 7. 24
	 * @Method Name : findListEvalSubjRes
	 */
	public List findListEvalSubjRes(Map param) {

		// 평가대상 결과 조회
		return evalSubjResService.findListEvalSubjRes(param);
	}

	/**
	 * 평가대상 계산항목 결과를 조회한다.
	 * @author : hj.jang
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디
	 * param.eval_req_uuid - 평가 요청 아이디 (pe_uuid / oe_prcs_uuid)
	 * @return the List
	 * @Date : 2023. 7. 24
	 * @Method Name : findListEvalSubjCalcfactRes
	 */
	public List findListEvalSubjCalcfactRes(Map param) {

		// 평가대상 계산항목 결과 조회
		return evalfactResService.findListEvalSubjCalcfactRes(param);
	}

	/**
	 * 평가요청 건에 대한 평가 대상 점수를 계산한다. (계산항목 수집, 평가항목 점수, 평가항목군 점수 재계산한 후 평가대상 점수 산출)
	 * @author : hj.jang
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디
	 * param.eval_req_uuid - 평가 요청 아이디 (pe_uuid / oe_prcs_uuid)
	 * @return the ResultMap
	 * @Date : 2023. 7. 24
	 * @Method Name : calculateEvalSubjResByEvalReqWithCalcfact
	 */
	public ResultMap calculateEvalSubjResByEvalReqWithCalcfact(Map<String, Object> param) {
		param.put(EvalConst.REQ_TYPE, EvalConst.ALL);

		// 계산항목 값 수집을 요청한다. (평가대상 추가된 건에 대해 계산항목 수집 및 정량항목을 계산하기 위함. )
		param.put(EvalConst.COLL_VAL_REQ_YN, EvalConst.Y_STR_VAL);

		this.calculateEvalSubjResAndEvalfactResByEvalReq(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 평가요청 건에 대한 평가 대상 점수를 계산한다. (계산항목 수집하지 않고 평가 항목 점수, 평가항목군 점수 재계산한 후 평가대상 점수 산출) <br>
	 * 계산항목 점수를 조정한 후, 점수를 재계산 할 때 사용한다. <br><br>
	 * @author : hj.jang
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디
	 * param.eval_req_uuid - 평가 요청 아이디 (pe_uuid / oe_prcs_uuid)
	 * @return the ResultMap
	 * @Date : 2023. 7. 24
	 * @Method Name : reCalculateQuantEvalfact
	 */
	public ResultMap reCalculateQuantEvalfact(Map<String, Object> param) {
		param.put(EvalConst.REQ_TYPE, EvalConst.ALL);

		// 계산항목 값 수집을 제외한다.
		param.put(EvalConst.COLL_VAL_REQ_YN, EvalConst.N_STR_VAL);

		// 평가항목 계산 시에 정량항목 점수만 새로 계산한다.
		param.put(EvalConst.CALC_SUBJ_EVALFACT_TYP, EvalConst.QUANT);

		// 계산항목 재계산 요청 여부 Y
		param.put(EvalConst.CALCFACT_RE_CALC_REQ_YN, EvalConst.Y_STR_VAL);

		this.calculateEvalSubjResAndEvalfactResByEvalReq(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 계산항목 점수를 재계산한다. (계산항목 수집 & 정량 평가항목 점수 계산 & 정성 평가자 존재 시 정성평가자 점수에 반영. 평가대상 결과에는 반영 X) <br>
	 * <br>
	 * @author : hj.jang
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디
	 * param.eval_req_uuid - 평가 요청 아이디 (pe_uuid / oe_prcs_uuid)
	 * @return the ResultMap
	 * @Date : 2023. 7. 24
	 * @Method Name : reCalculateCalcfactVal
	 */
	public ResultMap reCalculateCalcfactVal(Map<String, Object> param) {
		param.put(EvalConst.REQ_TYPE, EvalConst.ALL);

		// 계산항목 값 수집을 요청한다.
		param.put(EvalConst.COLL_VAL_REQ_YN, EvalConst.Y_STR_VAL);
		param.put(EvalConst.CALCFACT_RE_COLL_REQ_YN, EvalConst.Y_STR_VAL);
		evalfactResService.calculateEvalSubjQuantEvalfactSc(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 계산항목의 수집 값을 조정한다.
	 * @author : hj.jang
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디
	 * param.eval_subj_calcfact_uuid - 평가대상 계산항목 아이디 
	 * param.calcfact_val - 계산항목 값
	 * @return the ResultMap
	 * @Date : 2023. 7. 24
	 * @Method Name : updateEvalSubjCalcfactByAdj
	 */
	public ResultMap updateEvalSubjCalcfactByAdj(Map<String, Object> param) {

		// 계산항목 점수 수정
		evalfactResService.updateEvalSubjCalcfactByAdj(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 평가요청 건에 대한 평가점수를 집계한다. (평가항목군 및 평가항목 점수, 평가대상 점수)
	 * @author : hj.jang
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디
	 * param.eval_req_uuid - 평가 요청 아이디 (pe_uuid / oe_prcs_uuid)
	 * @return the ResultMap
	 * @Date : 2023. 7. 24
	 * @Method Name : calculateEvalSubjResAndEvalfactResByEvalReq
	 */
	public ResultMap calculateEvalSubjResAndEvalfactResByEvalReq(Map<String, Object> param) {

		// 평가대상 평가항목 점수 계산 (평가요청 기준)
		evalfactResService.calculateEvalfactScByEvalReq(param);

		// 평가대상 점수 계산
		evalSubjResService.calculateEvalSubjResScByEvalReq(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 평가자별 평가항목별 정성평가 점수를 조회한다.
	 * @author : hj.jang
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디
	 * param.eval_req_uuid - 평가 요청 아이디
	 * param.eval_subj_res_uuid - 평가 대상 결과 아이디
	 * @return the ResultMap
	 * @Date : 2023. 7. 24
	 * @Method Name : findListEvaltrEvalfactResSc
	 */
	public List findListEvaltrEvalfactResSc(Map<String, Object> param) {
		return evaltrEvalfactResService.findListEvaltrEvalfactResSc(param);
	}

}