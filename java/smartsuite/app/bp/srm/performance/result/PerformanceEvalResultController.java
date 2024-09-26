package smartsuite.app.bp.srm.performance.result;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.srm.performance.result.service.PerformanceEvalResultService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Performance Result 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @author hj.jang
 * @see
 * @since 2023. 06. 29
 * @FileName PerformanceEvalResultController.java
 * @package smartsuite.app.bp.performance.result
 * @변경이력 : [2023. 6. 29] hj.jang 최초작성
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/performance/**/")
public class PerformanceEvalResultController {

	@Inject
	PerformanceEvalResultService pfmcEvalResService;

	/**
	 * 퍼포먼스평가 결과 목록 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 29
	 * @Method Name : findListPfmcEvalResult
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPfmcEvalResult.do")
	public @ResponseBody List findListPfmcEvalResult(@RequestBody Map param){

		return pfmcEvalResService.findListPfmcEvalResult(param);
	}
	
	/**
	 * 퍼포먼스평가 결과 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 6
	 * @Method Name : findPfmcEvalRes
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findPfmcEvalRes.do")
	public @ResponseBody Map findPfmcEvalRes(@RequestBody Map param){
		return pfmcEvalResService.findPfmcEvalRes(param);
	}

	/**
	 * 퍼포먼스평가 결과 평가그룹 목록 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 6
	 * @Method Name : findListPegByPe
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPegByPe.do")
	public @ResponseBody List findListPegByPe(@RequestBody Map param){
		return pfmcEvalResService.findListPegByPe(param);
	}
	
	/**
	 * 퍼포먼스평가 결과 협력사관리그룹 목록 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 6
	 * @Method Name : findListVmgByPe
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListVmgByPe.do")
	public @ResponseBody List findListVmgByPe(@RequestBody Map param){
		return pfmcEvalResService.findListVmgByPe(param);
	}

	/**
	 * 퍼포먼스평가 평가대상 결과 상세 목록 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 6
	 * @Method Name : findListPeSubjResult
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPeSubjResult.do")
	public @ResponseBody List findListPeSubjResult(@RequestBody Map param){
		return pfmcEvalResService.findListPeSubjResult(param);
	}

	/**
	 * 퍼포먼스평가 종합의견 목록 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 12. 29
	 * @Method Name : findListPeComprehensiveOpinion
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPeComprehensiveOpinion.do")
	public @ResponseBody List findListPeComprehensiveOpinion(@RequestBody Map param){
		return pfmcEvalResService.findListPeComprehensiveOpinion(param);
	}

	/**
	 * 퍼포먼스평가 평가항목 결과 상세 목록 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 6
	 * @Method Name : findListPeSubjEvalfactResult
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPeSubjEvalfactResult.do")
	public @ResponseBody List findListPeSubjEvalfactResult(@RequestBody Map param){
		return pfmcEvalResService.findListPeSubjEvalfactResult(param);
	}

	/**
	 * 퍼포먼스평가 평가항목 의견 상세 목록 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 12. 29
	 * @Method Name : findListPeSubjEvalfactOpinion
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPeSubjEvalfactOpinion.do")
	public @ResponseBody List findListPeSubjEvalfactOpinion(@RequestBody Map param){
		return pfmcEvalResService.findListPeSubjEvalfactOpinion(param);
	}

	/**
	 * 퍼포먼스평가 대상 평가 재요청을 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 9
	 * @Method Name : saveRePfmcEvalReq
	 */
	@AuthCheck(authCode= Const.SAVE)
	@RequestMapping(value = "saveRePfmcEvalReq.do")
	public @ResponseBody ResultMap saveRePfmcEvalReq(@RequestBody Map param){
		return pfmcEvalResService.saveRePfmcEvalReq(param);
	}

	/**
	 * 평가그룹으로 퍼포먼스 평가 결과 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 10
	 * @Method Name : findPeGrdCntByPeg
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="findPeGrdCntByPeg.do")
	public @ResponseBody Map findPeGrdCntByPeg(@RequestBody Map param){
		return pfmcEvalResService.findPeGrdCntByPeg(param);
	}

	/**
	 * 퍼포먼스평가대상의 평가결과 조정정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 10
	 * @Method Name : findPeSubjResAdjInfo
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="findPeSubjResAdjInfo.do")
	public @ResponseBody Map findPeSubjResAdjInfo(@RequestBody Map param){
		return pfmcEvalResService.findPeSubjResAdjInfo(param);
	}
	/**
	 * 퍼포먼스평가대상의 평가결과 조정정보를 저장한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 10
	 * @Method Name : savePeSubjResAdjInfo
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="savePeSubjResAdjInfo.do")
	public @ResponseBody ResultMap savePeSubjResAdjInfo(@RequestBody Map param){
		return pfmcEvalResService.savePeSubjResAdjInfo(param);
	}

	/**
	 * 퍼포먼스 평가 마감 가능 여부를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 23
	 * @Method Name : checkClosablePfmcEval
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="checkClosablePfmcEval.do")
	public @ResponseBody Map checkClosablePfmcEval(@RequestBody Map param){
		return pfmcEvalResService.checkClosablePfmcEval(param);
	}

	/**
	 * 퍼포먼스 평가를 마감한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 10
	 * @Method Name : closePfmcEval
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="closePfmcEval.do")
	public @ResponseBody ResultMap closePfmcEval(@RequestBody Map param){
		return pfmcEvalResService.closePfmcEval(param);
	}

	/**
	 * 퍼포먼스 평가 마감을 해제한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 24
	 * @Method Name : cancelClosedPfmcEval
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="cancelClosedPfmcEval.do")
	public @ResponseBody ResultMap cancelClosedPfmcEval(@RequestBody Map param){
		return pfmcEvalResService.cancelClosedPfmcEval(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 정량항목 결과 목록을 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListPeQuantEvalfactResult
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="findListPeQuantEvalfactResult.do")
	public @ResponseBody List findListPeQuantEvalfactResult(@RequestBody Map param){
		return pfmcEvalResService.findListPeQuantEvalfactResult(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 정량항목 계산항목 결과 목록을 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListPeSubjCalcfactResult
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="findListPeSubjCalcfactResult.do")
	public @ResponseBody List findListPeSubjCalcfactResult(@RequestBody Map param){
		return pfmcEvalResService.findListPeSubjCalcfactResult(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 평가 항목별 점수 팝업
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListPfmcEvalDetailScoreInfo
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="findListPfmcEvalDetailScoreInfo.do")
	public @ResponseBody Map findListPfmcEvalDetailScoreInfo(@RequestBody Map param){
		return pfmcEvalResService.findListPfmcEvalDetailScoreInfo(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 평가 항목별 점수 팝업
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map
	 * @Date : 2024. 01. 10
	 * @Method Name : findListPreFactScoreInfo
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="findListPreFactScoreInfo.do")
	public @ResponseBody List findListPreFactScoreInfo(@RequestBody Map param){
		return pfmcEvalResService.findListPreFactScoreInfo(param);
	}
	
	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정정보 조회
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListAdjCalcfactByFact
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="findListAdjCalcfactByFact.do")
	public @ResponseBody List findListAdjCalcfactByFact(@RequestBody Map param){
		return pfmcEvalResService.findListAdjCalcfactByFact(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 평가에 포함된 모든 계산항목 목록 조회
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListCalcfactByPeUsed
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="findListCalcfactByPeUsed.do")
	public @ResponseBody List findListCalcfactByPeUsed(@RequestBody Map param){
		return pfmcEvalResService.findListCalcfactByPeUsed(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 조정 정보 조회
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 24
	 * @Method Name : findPeSubjCalcfactAdj
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="findPeSubjCalcfactAdj.do")
	public @ResponseBody Map findPeSubjCalcfactAdj(@RequestBody Map param){
		return pfmcEvalResService.findPeSubjCalcfactAdj(param);
	}
	
	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 조정 정보 저장
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 26
	 * @Method Name : savePeSubjCalcfactAdj
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="savePeSubjCalcfactAdj.do")
	public @ResponseBody ResultMap savePeSubjCalcfactAdj(@RequestBody Map param){
		return pfmcEvalResService.savePeSubjCalcfactAdj(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 집계제외여부 목록 저장
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 26
	 * @Method Name : savePeSubjCalcfactAdjList
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="savePeSubjCalcfactAdjList.do")
	public @ResponseBody ResultMap savePeSubjCalcfactAdjList(@RequestBody Map param){
		return pfmcEvalResService.savePeSubjCalcfactAdjList(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 정량 평가항목 결과 재계산 호출
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 26
	 * @Method Name : reCalculateQuantEvalfact
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="reCalculateQuantEvalfact.do")
	public @ResponseBody ResultMap reCalculateQuantEvalfact(@RequestBody Map param){
		return pfmcEvalResService.reCalculateQuantEvalfact(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 재집계 호출
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 26
	 * @Method Name : reCalculateQuantEvalfact
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="reCalculateCalcfactVal.do")
	public @ResponseBody ResultMap reCalculateCalcfactVal(@RequestBody Map param){
		return pfmcEvalResService.reCalculateCalcfactVal(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 평가대상 결과 탭 > 개선관리 등록
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 08. 10
	 * @Method Name : savePeSubjResultImprove
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="savePeSubjResultImprove.do")
	public @ResponseBody ResultMap savePeSubjResultImprove(@RequestBody Map param){
		return pfmcEvalResService.savePeSubjResultImprove(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 평가 결과 상세화면 > 평가 완료
	 * 퍼포먼스 평가 결과를 결재없이 확정한다.
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 08. 14
	 * @Method Name : confirmPfmcEvalResult
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="confirmPfmcEvalResult.do")
	public @ResponseBody ResultMap confirmPfmcEvalResult(@RequestBody Map param){
		return pfmcEvalResService.confirmPfmcEvalResult(param);
	}


}
