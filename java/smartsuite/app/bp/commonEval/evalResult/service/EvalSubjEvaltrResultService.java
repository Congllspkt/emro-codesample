package smartsuite.app.bp.commonEval.evalResult.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.commonEval.common.EvalConst;
import smartsuite.app.bp.commonEval.common.status.service.CommonEvalStatusService;
import smartsuite.app.bp.commonEval.evalResult.repository.EvalSubjEvaltrResultRepository;
import smartsuite.app.bp.commonEval.evalResult.validator.EvalResultValidator;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class EvalSubjEvaltrResultService {

	@Inject
	EvalResultValidator evalResultValidator;

	@Inject
	EvalSubjEvaltrResultRepository evaltrResRepository;

	@Inject
	EvaltrEvalfactResultService evaltrEvalfactResService;

	@Inject
	CommonEvalStatusService evalStatusService;

	public ResultMap insertEvalSubjEvaltrRes(Map param) {

		// parameter 유효성 체크 후 success가 아니면 바로 return
		ResultMap validator = evalResultValidator.validEvalSubjEvaltrResParam(param);
		if(!validator.isSuccess()) {
			return validator;
		}

		// 평가대상 평가자 이미 존재하는 경우 duplicated로 return
		validator = evalResultValidator.validEvalSubjEvaltrResSts(param, EvalConst.CREATE);
		if(!validator.isSuccess()) {
			return validator;
		} else {

			Map<String, Object> resultData = Maps.newHashMap();
			Map<String, Object> createParam = Maps.newHashMap(param);
			String evalSubjEvaltrResUuid = UUID.randomUUID().toString();
			createParam.put("eval_subj_evaltr_res_uuid",evalSubjEvaltrResUuid);
			
			evaltrResRepository.insertEvalSubjEvaltrRes(createParam);  // 평가대상 평가자 결과 생성
			evalStatusService.insertEvalSubjEvaltrRes(createParam);  // 평가대상 평가자 결과 정성평가 진행상태 수정
			
			resultData.put("eval_subj_evaltr_res_uuid", evalSubjEvaltrResUuid);
			return ResultMap.SUCCESS(resultData);
		}
	}

	public ResultMap deleteEvalSubjEvaltrRes	(Map param) {
		String reqType = (String)param.getOrDefault(EvalConst.REQ_TYPE, EvalConst.EVALTR);
		Map deleteEvaltrParam = null;

		if(reqType.equals(EvalConst.EVALTR)) {
			deleteEvaltrParam = this.getEvalSubjEvaltrRes(param);
		} else {
			deleteEvaltrParam = Maps.newHashMap(param);
		}
		// 1. 평가항목 평가자 결과 삭제
		evaltrEvalfactResService.deleteEvaltrEvalfactRes(deleteEvaltrParam);

		// 2. 평가대상 평가자 결과 삭제
		evaltrResRepository.deleteEvalSubjEvaltrRes(deleteEvaltrParam);

		return ResultMap.SUCCESS();
	}

	public Map getEvalSubjEvaltrRes	(Map param) {
		return evaltrResRepository.findEvalSubjEvaltrRes(param);
	}

	/**
	 * 평가대상 평가자 결과의 평가자 아이디 변경
	 *
	 * @author : hj.jang
	 * @param param the param
	 * <br><br>
	 * <b>Required</b><br>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가대상 평가자 결과 아이디 <br>
	 * @return the map< string, object>
	 * @Date : 2023.07.05
	 * @Method Name : changeEvalSubjEvaltrRes
	 */
	public ResultMap changeEvalSubjEvaltrRes (Map param) {
		evaltrResRepository.changeEvalSubjEvaltrRes(param);
		return ResultMap.SUCCESS();
	}

	public void deleteEvalSubjEvaltrResByEvalReqUuid(Map param) {
		// 삭제 단위 : 평가 요청 아이디 전체
		param.put(EvalConst.REQ_TYPE, EvalConst.ALL);

		// 평가자 평가항목 결과 삭제
		evaltrEvalfactResService.deleteEvaltrEvalfactResByEvalReqUuid(param);

		// 평가대상 평가자 결과 삭제
		evaltrResRepository.deleteEvalSubjEvaltrRes(param);
	}

	/**
	 * 정성평가 진행상태 "제출" 상태를 취소한다.
	 * @author : hj.jang
	 * @param param the param
	 * <b>Required</b>
	 * param.ten_id - 시스템아이디 <br>
	 * param.eval_subj_res_uuid - 평가 대상 결과 아이디 <br>
	 * @return the ResultMap
	 * @Date : 2023. 7. 6
	 * @Method Name : updateCancleSubmEvaltrPrgsSts
	 */
	public ResultMap updateCancleSubmEvaltrPrgsSts(Map param) {

		//정성평가 진행상태 "제출" 상태를 취소한다.
		evalStatusService.updateCancleSubmEvaltrPrgsSts(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 정성평가자 평가 수행 정보를 저장한다. (점수 계산 및 진행상태 수정)
	 * @author : hj.jang
	 * <b>Required</b>
	 * @param evalSubjMap - 평가대상 정보 <br>
	 * @param saveEvaltmplList - 저장할 평가템플릿 목록 <br>
	 * @param tenId - 시스템아이디 <br>
	 * @return the ResultMap
	 * @Date : 2023. 7. 6
	 * @Method Name : saveEvalSubjEvaltrFulfill
	 */
	public void saveEvalSubjEvaltrFulfill(Map<String, Object> evalSubjMap, List<Map<String, Object>> saveEvaltmplList, String tenId) {

		if(!saveEvaltmplList.isEmpty()) {
			for(Map<String, Object> evaltmpl : saveEvaltmplList) {
				evaltmpl.put(EvalConst.TEN_ID, tenId);
				evaltrResRepository.updateEvalSubjEvaltrFulfill(evaltmpl);
			}
		}

		//정성평가 진행상태를 "저장" 으로 변경한다.
		// todo jhj evalSubjMap.eval_prgs_sts_ccd 넘어온 값을 바로 저장할 지 확인 필요
		evalSubjMap.put(EvalConst.TEN_ID, tenId);
		evalStatusService.saveEvalSubjEvaltrFulfill(evalSubjMap);
	}

	/**
	 * 평가대상 평가자 결과 점수 계산
	 * @author : hj.jang
	 * <b>Required</b>
	 * @param evalSubjMap - 평가대상 정보 <br>
	 * @param tenId - 시스템아이디 <br>
	 * @return
	 * @Date : 2023. 7. 13
	 * @Method Name : updateEvalSubjEvaltrSc
	 */
	public void updateEvalSubjEvaltrSc(Map<String, Object> evalSubjMap, String tenId) {

		// 평가자 평가점수를 계산 후 저장한다.
		Map<String, Object> calcEvaltrScParam = Maps.newHashMap(evalSubjMap);
		calcEvaltrScParam.put(EvalConst.TEN_ID, tenId);

		evaltrResRepository.updateEvalSubjEvaltrSc(calcEvaltrScParam);
	}
}