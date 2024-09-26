package smartsuite.app.bp.commonEval.evalResult.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.commonEval.common.EvalConst;
import smartsuite.app.bp.commonEval.evalResult.repository.EvalSubjResultRepository;
import smartsuite.app.bp.commonEval.evalResult.validator.EvalResultValidator;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class EvalSubjResultService {

	@Inject
	EvalResultValidator evalResultValidator;

	@Inject
	EvalSubjResultRepository evalSubjRepository;

	@Inject
	EvalSubjEvaltrResultService evaltrService;

	@Inject
	EvalSubjEvalfactResultService evalfactService;

	@Inject
	EvaltrEvalfactResultService evaltrEvalfactService;

	// 평가대상 결과 생성
	public ResultMap insertEvalSubjRes(Map param) {

		// parameter 유효성 체크 후 success가 아니면 바로 return
		ResultMap validator = evalResultValidator.validEvalSubjResParam(param);
		if(!validator.isSuccess()) {
			return validator;
		}

		// 평가대상결과 이미 존재하는 경우 duplicated로 return
		validator = evalResultValidator.validEvalSubjResSts(param, EvalConst.CREATE);
		if(!validator.isSuccess()) {
			return validator;
		}

		Map<String, Object> resultData = Maps.newHashMap();
		Map<String, Object> createParam = Maps.newHashMap(param);
		String evalSubjResUuid = UUID.randomUUID().toString();
		createParam.put(EvalConst.EVAL_SUBJ_RES_UUID, evalSubjResUuid);

		// 평가대상 결과 생성
		evalSubjRepository.insertEvalSubjRes(createParam);

		return ResultMap.SUCCESS(createParam);

	}

	public ResultMap deleteEvalSubjRes(Map param) {
		Map<String, Object> subjResInfo = this.getEvalSubjRes(param);

		if(subjResInfo != null) {

			// 1. 평가항목 평가자 결과 삭제
			//evaltrEvalfactService.deleteEvaltrEvalfactByEvalSubjResUuid(subjResInfo);

			// 2. 평가자 결과 삭제 (평가자 평가항목 결과 + 평가자  결과)
			subjResInfo.put(EvalConst.REQ_TYPE, EvalConst.SUBJ);
			evaltrService.deleteEvalSubjEvaltrRes(subjResInfo);

			// 3. 평가항목 결과 삭제
			evalfactService.deleteEvalSubjEvalfactResByEvalSubjResUuid(subjResInfo);

			// 4. 평가대상 결과 삭제
			evalSubjRepository.deleteEvalSubjRes(subjResInfo);
		}

		return ResultMap.SUCCESS();
	}

	public Map getEvalSubjRes(Map param) {
		return evalSubjRepository.findEvalSubjRes(param);
	}

	public ResultMap deleteEvalSubjResByEvalReqUuid (Map param) {

		// 평가대상 평가자 결과 삭제
		evaltrService.deleteEvalSubjEvaltrResByEvalReqUuid(param);

		// 평가항목 결과 삭제
		evalfactService.deleteEvalSubjEvalfactRes(param);

		// 삭제 단위 : 평가요청 아이디 전체 삭제
		param.put(EvalConst.REQ_TYPE, EvalConst.ALL);

		// 평가대상 결과 삭제
		evalSubjRepository.deleteEvalSubjRes(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 평가대상 점수를 계산한다.
	 * @author : hj.jang
	 * param.evalSubjMap - 평가대상 정보
	 * param.tenId - 테넌트 아이디
	 * */
	public void calculateEvalSubjResScByEvalReq(Map<String, Object> evalSubjMap) {
		Map<String, Object> evalScMap = Maps.newHashMap(evalSubjMap);
		evalScMap.put(EvalConst.REQ_TYPE, EvalConst.ALL);
		evalSubjRepository.calculateEvalSubjResSc(evalScMap);
	}

	/**
	 * 평가대상 점수를 계산한다.
	 * @author : hj.jang
	 * param.evalSubjMap - 평가대상 정보
	 * param.tenId - 테넌트 아이디
	 * */
	public void calculateEvalSubjResScByEvalSubj(Map<String, Object> evalSubjMap) {
		Map<String, Object> evalScMap = Maps.newHashMap(evalSubjMap);
		evalScMap.put(EvalConst.REQ_TYPE, EvalConst.SUBJ);
		evalSubjRepository.calculateEvalSubjResSc(evalScMap);
	}

	/**
	 * 평가요청 단위의 평가대상 결과 목록을 요청한다.
	 * @author : hj.jang
	 * param.ten_id - 테넌트 아이디
	 * param.eval_req_uuid - 평가 요청 아이디
	 * */
	public List findListEvalSubjRes(Map<String, Object> param) {
		return evalSubjRepository.findListEvalSubjRes(param);
	}

}
