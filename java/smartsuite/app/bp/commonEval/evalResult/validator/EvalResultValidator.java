package smartsuite.app.bp.commonEval.evalResult.validator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.commonEval.common.EvalConst;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.*;

@SuppressWarnings("unchecked")
@Service
public class EvalResultValidator {

	@Inject
	SqlSession sqlSession;

	private static final String EVAL_SUBJ_NAMESPACE = "eval-subj-res.";
	private static final String EVAL_FACT_NAMESPACE = "eval-subj-evalfact-res.";
	private static final String EVAL_SUBJ_EVALTR_NAMESPACE = "eval-subj-evaltr-res.";
	private static final String EVALTR_EVALFACT_NAMESPACE = "evaltr-evalfact-res.";

	public ResultMap validEvalSubjResParam(Map param) {
		String[] evalSubjReqFields = EvalConst.EVAL_SUBJ_REQ_FIELDS;
		List<Map<String, Object>> invalidList = Lists.newArrayList();

		for (String reqField : evalSubjReqFields) {
			// 필수 필드의 값이 파라미터에 존재하지 않을 경우
			if (StringUtils.isEmpty((String) param.get(reqField.toLowerCase()))) {
				Map<String, Object> invalidData = Maps.newHashMap();
				invalidData.put(EvalConst.NOT_EXISTS, reqField);
				invalidList.add(invalidData);
			}
		}

		if (invalidList.size() > 0) {
			return ResultMap.FAIL(invalidList);
		} else {
			return ResultMap.SUCCESS();
		}

	}

	public ResultMap validEvalSubjResSts(Map param, String reqType) {
		// 평가 대상 조회
		String evalSubjUuid = (String)param.get(EvalConst.EVAL_SUBJ_RES_UUID);
		if(Strings.isNullOrEmpty(evalSubjUuid)) {
			return ResultMap.SUCCESS();
		}

		Map<String, Object> evalSubj = sqlSession.selectOne(EVAL_SUBJ_NAMESPACE + "findEvalSubjRes", param);
		Map resultData = Maps.newHashMap();

		// 생성 요청인 경우 평가대상이 평가대상결과 테이블에 이미 존재할 경우 DUPLICATED
		if(EvalConst.CREATE.equals(reqType)) {
			if(evalSubj == null || evalSubj.isEmpty()) {
				return ResultMap.SUCCESS();
			} else {
				resultData.put(EvalConst.DUPLICATED, evalSubj);
				return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultMessage("생성 요청 : 평가대상 테이블에 이미 존재함").resultData(resultData).build();
			}
		}
		// 삭제 요청인 경우 평가대상이 평가대상결과 테이블에 존재하지 않을 경우 NOT_EXISTS
		else if(EvalConst.DELETE.equals(reqType)) {
			if(evalSubj == null || evalSubj.isEmpty()) {
				resultData.put(EvalConst.NOT_EXISTS, param);
				return ResultMap.FAIL(resultData);
			} else {
				return ResultMap.SUCCESS();
			}
		}

		return ResultMap.INVALID();
	}
	public ResultMap validEvalFactResParam(Map param) {
		String[] evalFactReqFields = EvalConst.EVAL_FACT_REQ_FIELDS;
		List<Map<String, Object>> invalidList = Lists.newArrayList();

		for (String reqField : evalFactReqFields) {
			// 필수 필드의 값이 파라미터에 존재하지 않을 경우
			if (StringUtils.isEmpty((String) param.get(reqField.toLowerCase()))) {
				Map<String, Object> invalidData = Maps.newHashMap();
				invalidData.put(EvalConst.NOT_EXISTS, reqField);
				invalidList.add(invalidData);
			}
		}

		if (invalidList.size() > 0) {
			return ResultMap.FAIL();
			//return ResultMap.FAIL(invalidList);
		} else {
			return ResultMap.SUCCESS();
		}

	}

	public ResultMap validEvalFactResSts(Map param, String reqType) {
		// 평가 대상 조회
		List<Map<String, Object>> evalFactList = sqlSession.selectList(EVAL_FACT_NAMESPACE + "findEvalSubjEfactRes", param);
		Map resultData = Maps.newHashMap();

		// 생성 요청인 경우 평가대상이 평가대상결과 테이블에 이미 존재할 시 DUPLICATED
		if(EvalConst.CREATE.equals(reqType)) {
			if(evalFactList == null || evalFactList.isEmpty()) {
				return ResultMap.SUCCESS();
			} else {
				return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultMessage("생성 요청 : 평가항목결과 테이블에 이미 존재함.").resultData(resultData).build();
			}
		}
		// 삭제 요청인 경우 평가대상 평가항목 결과가 테이블에 존재하지 않을 시 NOT_EXISTS
		else if(EvalConst.DELETE.equals(reqType)) {
			if(evalFactList == null) {
				resultData.put(EvalConst.NOT_EXISTS, param);
				return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultMessage("삭제 요청 : 평가항목 결과가 테이블에 존재하지 않음").resultData(resultData).build();
			} else {
				return ResultMap.SUCCESS();
			}
		}

		return ResultMap.INVALID();
	}

	public ResultMap validEvaltrEvalfactResParam(Map param) {
		String[] evalSubjReqFields = EvalConst.EVALTR_EVALFACT_REQ_FIELDS;
		List<Map<String, Object>> invalidList = Lists.newArrayList();

		for (String reqField : evalSubjReqFields) {
			// 필수 필드의 값이 파라미터에 존재하지 않을 경우
			if (StringUtils.isEmpty((String) param.get(reqField.toLowerCase()))) {
				Map<String, Object> invalidData = Maps.newHashMap();
				invalidData.put(EvalConst.NOT_EXISTS, reqField);
				invalidList.add(invalidData);
			}
		}

		if (invalidList.size() > 0) {
			return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultMessage("파라미터 오류 : 평가항목 평가자 결과 생성 시 필수 파라미터 값 없음").build();
			//return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultMessage("파라미터 오류 : 평가항목 평가자 결과 생성 시 필수 파라미터 값 없음").resultList(invalidList).build();
		} else {
			return ResultMap.SUCCESS();
		}

	}

	public ResultMap validEvaltrEvalfactResSts(Map param, String reqType) {
		// 평가 대상 조회
		Map<String, Object> evalSubj = sqlSession.selectOne(EVAL_SUBJ_NAMESPACE + "findEvalSubjRes", param);
		Map<String, Object> resultData = Maps.newHashMap();

		// 생성 요청인 경우 평가대상이 평가대상결과 테이블에 이미 존재할 경우 DUPLICATED
		if(EvalConst.CREATE.equals(reqType)) {
			if(evalSubj == null || evalSubj.isEmpty()) {
				return ResultMap.SUCCESS();
			} else {
				resultData.put(EvalConst.DUPLICATED, evalSubj);
				return ResultMap.FAIL(resultData);
			}
		}
		// 삭제 요청인 경우 평가대상이 평가대상결과 테이블에 존재하지 않을 경우 NOT_EXISTS
		else if(EvalConst.DELETE.equals(reqType)) {
			if(evalSubj == null) {
				resultData.put(EvalConst.NOT_EXISTS, param);
				return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultMessage("삭제 요청 : 평가대상결과 테이블에 존재하지 않음.").resultData(resultData).build();
			} else {
				return ResultMap.SUCCESS();
			}
		}
		return ResultMap.INVALID();
	}


	public ResultMap validEvalSubjEvaltrResParam(Map param) {
		String[] evalSubjEvaltrReqFields = EvalConst.EVAL_SUBJ_EVALTR_REQ_FIELDS;
		List<Map<String, Object>> invalidList = Lists.newArrayList();

		for (String reqField : evalSubjEvaltrReqFields) {
			// 필수 필드의 값이 파라미터에 존재하지 않을 경우
			if (StringUtils.isEmpty((String) param.get(reqField.toLowerCase()))) {
				Map<String, Object> invalidData = Maps.newHashMap();
				invalidData.put(EvalConst.NOT_EXISTS, reqField);
				invalidList.add(invalidData);
			}
		}

		if (invalidList.size() > 0) {
			return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultMessage("파라미터 오류 : 필수 필드의 값이 없음. resultList 참고").resultList(invalidList).build();
		} else {
			return ResultMap.SUCCESS();
		}
	}

	public ResultMap validEvalSubjEvaltrResSts(Map param, String reqType) {
		// 평가 대상 조회
		Map<String, Object> evalSubjEvaltr = sqlSession.selectOne(EVAL_SUBJ_EVALTR_NAMESPACE + "findEvalSubjEvaltrRes", param);
		Map<String, Object> resultData = Maps.newHashMap();

		// 생성 요청인 경우 평가대상이 평가대상결과 테이블에 이미 존재할 경우 DUPLICATED
		if(EvalConst.CREATE.equals(reqType)) {
			if(evalSubjEvaltr == null || evalSubjEvaltr.isEmpty()) {
				return ResultMap.SUCCESS();
			} else {
				resultData.put(EvalConst.DUPLICATED, evalSubjEvaltr);
				return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultMessage("생성 요청 : 대상이 평가대상 결과 테이블에 존재함").resultData(resultData).build();
			}
		}
		// 삭제 요청인 경우 평가대상이 평가대상결과 테이블에 존재하지 않을 경우 NOT_EXISTS ?
		else if(EvalConst.DELETE.equals(reqType)) {
			if(evalSubjEvaltr == null) {
				resultData.put(EvalConst.NOT_EXISTS, param);
				return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultMessage("삭제 요청 : 대상이 평가대상 결과 테이블에 존재하지 않음").resultData(resultData).build();
			} else {
				return ResultMap.SUCCESS();
			}
		}
		return ResultMap.INVALID();
	}

	// 평가자 평가항목 결과 저장 파라미터 유효성 검사
	public ResultMap validSaveEvaltrEvalfactResParam (Map param) {
		String[] evaltrEvalfactSaveFields = EvalConst.EVALTR_EVALFACT_SAVE_FIELDS;
		List<Map<String, Object>> invalidList = Lists.newArrayList();

		for (String saveField : evaltrEvalfactSaveFields) {
			// 필수 필드의 값이 파라미터에 존재하지 않을 경우
			if ((Object) param.get(saveField) == null) {
				Map<String, Object> invalidData = Maps.newHashMap();
				invalidData.put(EvalConst.NOT_EXISTS, saveField);
				invalidList.add(invalidData);
			}
		}
		if (invalidList.size() > 0) {
			return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultMessage("파라미터 오류 : 필수 필드의 값이 없음. resultList 참고 \n" + invalidList.toString()).resultList(invalidList).build();
		} else {
			return ResultMap.SUCCESS();
		}
	}
}
