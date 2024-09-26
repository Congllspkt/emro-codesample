package smartsuite.app.sp.srm.eval.pfmcEval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.srm.performance.pfmcSetup.service.PegSetupService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.srm.eval.pfmcEval.service.SpPfmcEvalService;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.Map;

/**
 * pfmc eval 컨트롤러 Class입니다.
 *
 * @author sykim
 * @see
 * @since 2023. 6. 23
 * @FileName PfmcEvalController.java
 * @package smartsuite.app.bp.performance.pfmcEval
 * @변경이력 : [2023. 6. 23] sykim 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value="**/sp/**/")
public class SpPfmcEvalController {

	/** pfmc eval service. */
	@Inject
	SpPfmcEvalService spPfmcEvalService;

	@Inject
	PegSetupService pegSetupService;

	/**
	 * 퍼포먼스평가그룹 조회를 요청한다. (콤보박스용)
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the performance eval group list for combobox
	 * @Date : 2023. 7. 3
	 * @Method Name : findListPegForCombobox
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListPegForCombobox.do")
	public @ResponseBody FloaterStream findListPegForCombobox(@RequestBody Map param) {
		// 대용량 처리
		return pegSetupService.findListPeg(param);
	}

	/**
	 * 퍼포먼스평가 수행 목록 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the performance eval list to be fulfilled
	 * @Date : 2023. 6. 23
	 * @Method Name : findListPeg
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListPerformanceEvalFulfill.do")
	public @ResponseBody FloaterStream findListPerformanceEvalFulfill(@RequestBody Map param) {
		// 대용량 처리
		return spPfmcEvalService.findListPerformanceEvalFulfill(param);
	}

	/**
	 * 퍼포먼스평가 수행 정보 및 대상 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the performance eval information and information about subjects
	 * @Date : 2023. 7. 1
	 * @Method Name : findPerformanceEvalSubjectInfo
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findPerformanceEvalSubjectInfo.do")
	public @ResponseBody ResultMap findPerformanceEvalSubjectInfo(@RequestBody Map param) {
		return spPfmcEvalService.findPerformanceEvalSubjectInfo(param);
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
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findPerformanceEvalfactFulfillInfo.do")
	public @ResponseBody ResultMap findPerformanceEvalfactFulfillInfo(@RequestBody Map param) {
		return spPfmcEvalService.findPerformanceEvalfactFulfillInfo(param);
	}

	/**
	 * (퍼포먼스)평가수행 정보 저장을 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 13
	 * @Method Name : savePerformanceEvalFulfillment
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "savePerformanceEvalFulfillment.do")
	public @ResponseBody ResultMap savePerformanceEvalFulfillment(@RequestBody Map param) {
		return spPfmcEvalService.savePerformanceEvalFulfillment(param);
	}
}