package smartsuite.app.bp.commonEval.evalResult.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.commonEval.common.EvalConst;
import smartsuite.app.bp.commonEval.evalResult.repository.EvaltrEvalfactResultRepository;
import smartsuite.app.bp.commonEval.evalResult.validator.EvalResultValidator;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EvaltrEvalfactResultService {

	@Inject
	EvaltrEvalfactResultRepository evaltrResMgtRepository;

	@Inject
	EvalResultValidator evalResultValidator;

	public void deleteEvaltrEvalfactByEvalSubjResUuid(Map param) {
		param.put(EvalConst.REQ_TYPE, EvalConst.SUBJ);
		evaltrResMgtRepository.deleteEvaltrEvalfactRes(param);
	}

	public ResultMap deleteEvaltrEvalfactRes(Map param) {

		// 평가대상결과 존재하지 않는 경우 not_exists 로 return 필요?
//		ResultMap validator = evalResultValidator.validEvalSubjResSts(param, EvalConst.DELETE);
//		if(!validator.isSuccess()) {
//			return validator;
//		} else {
//			evaltrResMgtRepository.deleteEvaltrEvalfactRes(param);
//			return ResultMap.SUCCESS();
//		}
		evaltrResMgtRepository.deleteEvaltrEvalfactRes(param);
		return ResultMap.SUCCESS();
	}


	/**
	 * 평가 수행용 항목 정보를 조회한다.
	 *
	 * @author : sykim
	 * param.eval_task_typ_ccd : 평가 업무 유형 공통코드(PE, OE, NPE..)
	 * param.eval_subj_res_uuid : 평가 대상 결과 uuid
	 * evaltr_id : 평가자 ID
	 * evaltmpl_uuid : 평가템플릿 ID
	 * slfck_evaltr_yn : 자체점검 평가자 유무 (PE만 해당)
	 * pe_subj_uuid : 퍼포먼스 평가 대상 uuid (자체점검 결과 조회용, PE만 해당)
	 * @return the Map
	 * @Date : 2023. 7. 5
	 * @Method Name : findEvalfactFulfillInfo
	 */
	public ResultMap findEvalfactFulfillInfo(final Map<String, Object> param) {
		final Map<String, Object> resultData = Maps.newHashMap();

		// 평가항목 찾기 목록 조회
		final List<Map<String, Object>> evalfactList = evaltrResMgtRepository.findEvalfactListForSearch(param);
		resultData.put("evalfactSearchList", evalfactList);

		// 평가수행 평가템플릿 목록 조회
		resultData.put("evalTemplateList", evaltrResMgtRepository.findEvaltemplateListForFulfill(param));
		if(evalfactList != null && !evalfactList.isEmpty()){
			// 평가수행 항목 목록 조회
			resultData.put("evalfactEvalList", evaltrResMgtRepository.findEvalfactListForFulfill(param));
		}

		// 평가수행 항목 스케일/세부항목 목록 조회
		resultData.put("factScaleList", evaltrResMgtRepository.findEvalfactScaleList(param));
		// 평가수행 정량항목 실측 결과 목록 조회
		resultData.put("quantFactResult", evaltrResMgtRepository.findListQuantitativeEvalfactResult(param));

		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 평가요청 아이디로 평가항목 평가자 결과 전체 삭제
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7. 8
	 * @Method Name : deleteEvaltrEvalfactResByEvalReqUuid
	 */
	public void deleteEvaltrEvalfactResByEvalReqUuid(Map param) {
		param.put(EvalConst.REQ_TYPE, EvalConst.ALL);
		evaltrResMgtRepository.deleteEvaltrEvalfactRes(param);
	}

	/**
	 * 평가요청 아이디로 평가항목 평가자 결과 전체 삭제
	 *
	 * @author : hj.jang
	 * param.evalSubjMap : 저장할 평가대상 평가자 정보
	 * param.saveEvalfactScaleList  : 저장할 평가항목 스케일 목록
	 * param.ten_id : 테넌트 아이디
	 * @return the Map
	 * @Date : 2023. 7. 13
	 * @Method Name : saveEvaltrEvalfactResScale
	 */
	public void saveEvaltrEvalfactScaleFulfill(Map<String, Object> evalSubjMap, List<Map<String, Object>> evalfactScaleList, String tenId) {
		// 1. 기존 평가자 평가항목결과 삭제
		evaltrResMgtRepository.deleteEvaltrEvalfactRes(evalSubjMap);

		// 2. 평가자 평가항목 결과 저장
		if (evalfactScaleList != null && !evalfactScaleList.isEmpty()) {
			for (Map<String, Object> evalfactScale : evalfactScaleList) {
				evalfactScale.put(EvalConst.TEN_ID, tenId);
				// 정성평가 항목 스케일 저장
				evaltrResMgtRepository.insertEvaltrEvalfactScaleFulfill(evalfactScale);
			}
		}
	}
	/**
	 * 평가자별 평가항목별 정성평가 점수를 조회한다.
	 *
	 * @author : hj.jang
	 * param.evalSubjMap : 저장할 평가대상 평가자 정보
	 * param.saveEvalfactScaleList  : 저장할 평가항목 스케일 목록
	 * param.ten_id : 테넌트 아이디
	 * @return the Map
	 * @Date : 2023. 7. 13
	 * @Method Name : findListEvaltrEvalfactResSc
	 */
	public List findListEvaltrEvalfactResSc(Map<String, Object> evalSubjData) {
		// 1. 기존 평가자 평가항목결과 삭제
		return evaltrResMgtRepository.findListEvaltrEvalfactResSc(evalSubjData);
	}
}
