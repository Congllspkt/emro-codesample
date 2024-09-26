package smartsuite.app.bp.onboarding.result;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.onboarding.result.service.OnboardingEvalResultService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.Map;

/**
 * obd eval result 컨트롤러 Class입니다.
 *
 * @author yjPark
 * @see
 * @since 2023. 6. 18
 * @FileName OnboardingEvalResultController.java
 * @package smartsuite.app.bp.onboarding.result
 * @변경이력 : [2023. 6. 18] yjPark 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value="**/bp/**/")
public class OnboardingEvalResultController {

	@Inject
	OnboardingEvalResultService onboardingEvalResultService;

	/**
	 * 온보딩평가 완료 처리대기 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the obd eval req list
	 * @Date : 2023. 6. 25
	 * @Method Name : findListCompleteOnboardingEval
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListCompleteOnboardingEval.do")
	public @ResponseBody FloaterStream findListCompleteOnboardingEval(@RequestBody Map param) {
		// 대용량 처리
		return onboardingEvalResultService.findListCompleteOnboardingEval(param);
	}

	/**
     * 온보딩평가 품의 - 미등록완료 저장을 요청한다.
     *
     * @author : yjPark
     * @param param the param
     * @return the map
     * @Date : 2023. 6. 27
     * @Method Name : saveListOnboardingEvalUnRegComplete
     */
    @AuthCheck (authCode = Const.SAVE)
    @RequestMapping(value = "saveListOnboardingEvalUnRegComplete.do")
    public @ResponseBody ResultMap saveListOnboardingEvalUnRegComplete(@RequestBody Map param) {
        return onboardingEvalResultService.saveListOnboardingEvalUnRegComplete(param);
    }

	/**
     * 협력사 변경 상태 조회 (findVendorModifyStatus)
     *
     * @author : yjpark
	 * @param param the param
	 * @return the vdHistrecInfo
	 * @Date : 2023. 7. 14
	 * @Method Name : findVendorModifyStatus
     */
    @AuthCheck(authCode = Const.READ)
    @RequestMapping(value = "findVendorModifyStatus.do")
    public @ResponseBody Map findVendorModifyStatus(@RequestBody Map param){
    	return onboardingEvalResultService.findVendorModifyStatus(param);
    }

	/**
	 * 온보딩평가 완료 처리대기 목록 조회를 요청한다.
	 *
	 * @author : jsKim
	 * @param param the param
	 * @return the obd eval record list
	 * @Date : 2023. 7. 10
	 * @Method Name : findListOnboardingEvalhistrec
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListOnboardingEvalHistrec.do")
	public @ResponseBody FloaterStream findListOnboardingEvalHistrec(@RequestBody Map param) {
		// 대용량 처리
		return onboardingEvalResultService.findListOnboardingEvalHistrec(param);
	}
}