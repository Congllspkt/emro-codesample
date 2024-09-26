package smartsuite.app.sp.onboarding.obdEval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.onboarding.obdEval.service.SpOnboardingEvalService;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Sp obd eval 컨트롤러 Class입니다.
 *
 * @author sykim
 * @see
 * @since 2023. 6. 29
 * @FileName SpObdEvalController.java
 * @package smartsuite.app.sp.onboarding.obdEval
 * @변경이력 : [2023. 6. 29] sykim 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value="**/sp/onboarding/obdEval/**")
public class SpOnboardingEvalController {

	/** obd eval service. */
	@Inject
	SpOnboardingEvalService spOnboardingEvalService;

	/**
	 * 운영단위별 운영조직 조회.
	 * @param param
	 * @Date : 2023. 07. 17
	 * @author yjPark
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "findListOorgCdAll.do")
	public @ResponseBody List findListOorgCdAll(@RequestBody String param) {
		return spOnboardingEvalService.findListOorgCdAll(param);
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
		return spOnboardingEvalService.findListOnboardingEvalFulfill(param);
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
		return spOnboardingEvalService.findOnboardingEvalSubjectInfo(param);
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
		return spOnboardingEvalService.findOnboardingEvalfactFulfillInfo(param);
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
		return spOnboardingEvalService.saveOnboardingEvalFulfillment(param);
	}
}