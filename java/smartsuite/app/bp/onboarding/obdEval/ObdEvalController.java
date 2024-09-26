package smartsuite.app.bp.onboarding.obdEval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.onboarding.obdEval.service.ObdEvalService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * obd eval 컨트롤러 Class입니다.
 *
 * @author yjPark
 * @see
 * @since 2023. 6. 18
 * @FileName ObdEvalController.java
 * @package smartsuite.app.bp.vs.obdEval
 * @변경이력 : [2023. 6. 18] yjPark 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value="**/bp/**/")
public class ObdEvalController {

	/** obd eval service. */
	@Inject
	ObdEvalService obdEvalService;

	/**
	 * 온보딩평가그룹 조회를 요청한다. (콤보박스용)
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the obd eval group list for combobox
	 * @Date : 2023. 7. 3
	 * @Method Name : findListOegForCombobox
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListOegForCombobox.do")
	public @ResponseBody FloaterStream findListOegForCombobox(@RequestBody Map param) {
		// 대용량 처리
		return obdEvalService.findListOegForCombobox(param);
	}

	/**
	 * 온보딩평가 수행 목록 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the obd eval list to be fulfilled
	 * @Date : 2023. 6. 23
	 * @Method Name : findListOnboardingEvalFulfill
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListOnboardingEvalFulfill.do")
	public @ResponseBody FloaterStream findListOnboardingEvalFulfill(@RequestBody Map param) {
		return obdEvalService.findListOnboardingEvalFulfill(param);
	}

	/**
	 * 온보딩평가 평가수행 대상 정보 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the obd eval subject information
	 * @Date : 2023. 7. 1
	 * @Method Name : findOnboardingEvalSubjectInfo
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findOnboardingEvalSubjectInfo.do")
	public @ResponseBody ResultMap findOnboardingEvalSubjectInfo(@RequestBody Map param) {
		return obdEvalService.findOnboardingEvalSubjectInfo(param);
	}

	/**
	 * (온보딩)평가수행 항목 정보 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the evalfact information to be fulfilled
	 * @Date : 2023. 7. 1
	 * @Method Name : findOnboardingEvalfactFulfillInfo
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findOnboardingEvalfactFulfillInfo.do")
	public @ResponseBody ResultMap findOnboardingEvalfactFulfillInfo(@RequestBody Map param) {
		return obdEvalService.findOnboardingEvalfactFulfillInfo(param);
	}

	/**
	 * (온보딩)평가수행 정보 저장을 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 13
	 * @Method Name : saveOnboardingEvalFulfillment
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "saveOnboardingEvalFulfillment.do")
	public @ResponseBody ResultMap saveOnboardingEvalFulfillment(@RequestBody Map param) {
		return obdEvalService.saveOnboardingEvalFulfillment(param);
	}

	/**
	 * 처리 상태 콤보박스 목록 조회
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the oe req sts
	 * @Date : 2023. 6. 22
	 * @Method Name : findOePrgsStsCcd
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findOePrgsStsCcd.do")
	public @ResponseBody List findOePrgsStsCcd(@RequestBody String param) {
		return obdEvalService.findOePrgsStsCcd(param);
	}

	@RequestMapping(value = "findOeEvalByEvalSubjEvaltrResId.do")
	public @ResponseBody Map findOeEvalByEvalSubjEvaltrResId(@RequestBody Map param) {
		return obdEvalService.findOeEvalByEvalSubjEvaltrResId(param);
	}
}