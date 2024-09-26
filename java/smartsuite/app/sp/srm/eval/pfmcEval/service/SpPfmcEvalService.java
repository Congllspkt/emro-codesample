package smartsuite.app.sp.srm.eval.pfmcEval.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.performance.result.service.PerformanceEvalResultService;
import smartsuite.app.common.PfmcConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.srm.eval.pfmcEval.event.SpPfmcEvalEventPublisher;
import smartsuite.app.sp.srm.eval.pfmcEval.repository.SpPfmcEvalRepository;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;

/**
 * pfmc eval 서비스 Class입니다.
 *
 * @author sykim
 * @see
 * @FileName PfmcEvalService.java
 * @package smartsuite.app.bp.performance.pfmcEval.service
 * @Since 2023. 6. 23
 * @변경이력 : [2023. 6. 23] sykim 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SpPfmcEvalService {

	@Inject
	private SpPfmcEvalRepository spPfmcEvalRepository;

	@Inject
	private SpPfmcEvalEventPublisher spPfmcEvalEventPublisher;

	@Inject
	private PerformanceEvalResultService pfmcEvalResultService;

	/**
	 * 퍼포먼스평가 수행 목록을 조회한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return List
	 * @Date : 2023. 6. 23
	 * @Method Name : findListPerformanceEvalFulfill
	 */
	public FloaterStream findListPerformanceEvalFulfill(Map<String, Object> param) {
		// 대용량 처리
		return spPfmcEvalRepository.findListPerformanceEvalFulfill(param);
	}

	/**
	 * 퍼포먼스평가 평가수행 대상 정보를 조회한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 1
	 * @Method Name : findOnboardingEvalSubjectInfo
	 */
	public ResultMap findPerformanceEvalSubjectInfo(final Map<String, Object> param) {
		Map<String, Object> resultData = Maps.newHashMap();
		// 퍼포먼스평가 정보 조회
		final Map<String, Object> evalInfo = spPfmcEvalRepository.findPerformanceEvalFulfillInfo(param);

		if(evalInfo != null && !evalInfo.isEmpty()){
			param.putAll(evalInfo);
			resultData.put("data" , evalInfo);
			// 정성평가 수행 대상 목록 조회
			resultData.put("list", spPfmcEvalRepository.findPerformanceEvalSubjectList(param));
			return ResultMap.SUCCESS(resultData);
		} else {
			return ResultMap.FAIL(resultData);
		}
	}

	/**
	 * (퍼포먼스)평가수행 항목 정보 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the evalfact information to be fulfilled
	 * @Date : 2023. 7. 1
	 * @Method Name : findPerformanceEvalfactFulfillInfo
	 */
	public ResultMap findPerformanceEvalfactFulfillInfo(Map<String, Object> param) {
		return spPfmcEvalEventPublisher.findPerformanceEvalfactFulfillInfo(param);
	}

	/**
	 * (퍼포먼스)평가수행 정보를 저장한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 13
	 * @Method Name : savePerformanceEvalFulfillment
	 */
	public ResultMap savePerformanceEvalFulfillment(Map<String, Object> param) {
		ResultMap saveResultMap = spPfmcEvalEventPublisher.savePerformanceEvalFulfillment(param);
		Map<String, Object> resultData = saveResultMap.getResultData();

		// 평가자 미제출 건 수가 0인 경우 평가 마감
		int notSubmEvaltrCnt;
		if(resultData.getOrDefault(PfmcConst.NOT_SUBM_EVALTR_CNT, "-1") instanceof BigDecimal) {
			notSubmEvaltrCnt = ((BigDecimal) resultData.getOrDefault(PfmcConst.NOT_SUBM_EVALTR_CNT, "-1")).intValue();
		} else {
			notSubmEvaltrCnt = (Integer) resultData.getOrDefault(PfmcConst.NOT_SUBM_EVALTR_CNT, "-1");
		}
		if(notSubmEvaltrCnt == 0) {
			spPfmcEvalEventPublisher.calculateEvalSubjResByEvalReqWithCalcfact(resultData);
			pfmcEvalResultService.closePfmcEval(resultData);
		}
		return saveResultMap;
	}
}
