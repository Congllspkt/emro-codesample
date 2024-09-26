package smartsuite.app.bp.onboarding.request;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.onboarding.request.service.OnboardingEvalMonitoringService;
import smartsuite.app.bp.onboarding.request.service.OnboardingEvalRequestService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * obd eval result/monitoring 컨트롤러 Class입니다.
 *
 * @author yjPark
 * @see
 * @since 2023. 6. 18
 * @FileName OnboardingEvalRequestController.java
 * @package smartsuite.app.bp.onboarding.request
 * @변경이력 : [2023. 6. 18] yjPark 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value="**/bp/**/")
public class OnboardingEvalRequestController {

	@Inject
	OnboardingEvalRequestService onboardingEvalRequestService;
	
	@Inject
	OnboardingEvalMonitoringService onboardingEvalMonitoringService;

	/**
	 * 온보딩평가 요청 대상인 협력사 목록을 조회한다. (운영조직 무관)
	 * 
	 * @author : swnam
	 * @param param
	 * @Date : 2024. 05. 02
	 */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping(value = "findListVendorAll.do")
    public @ResponseBody FloaterStream findListVendorAll(@RequestBody Map param) {
    	// 대용량 처리
        return onboardingEvalRequestService.findListVendorAll(param);
    }
	
	/**
	 * 온보딩평가 요청 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the obd eval req list
	 * @Date : 2023. 6. 18
	 * @Method Name : findListReqOnboardingEval
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListReqOnboardingEval.do")
	public @ResponseBody FloaterStream findListReqOnboardingEval(@RequestBody Map param) {
		// 대용량 처리
		return onboardingEvalMonitoringService.findListReqOnboardingEval(param);
	}

	/**
	 * 온보딩평가 절차 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the obd eval prcs list
	 * @Date : 2023. 6. 18
	 * @Method Name : findListOnboardingEvalProcess
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListOnboardingEvalProcess.do")
	public @ResponseBody List findListOnboardingEvalProcess(@RequestBody Map param) {
		return onboardingEvalMonitoringService.findListOnboardingEvalProcess(param);
	}

	/**
     * 온보딩평가 모니터링 - 평가마감 저장을 요청한다.
     *
	 * @author : yjPark
     * @param param the param
     * @return the map
	 * @Date : 2023. 6. 26
	 * @Method Name : saveListOnboardingEvalComplete
     */
    @AuthCheck (authCode = Const.SAVE)
    @RequestMapping(value = "saveListOnboardingEvalComplete.do")
    public @ResponseBody ResultMap saveListOnboardingEvalComplete(@RequestBody Map param) {
        return onboardingEvalMonitoringService.saveListOnboardingEvalComplete(param);
    }

	/**
     * 온보딩평가 모니터링 - 조건부합격 저장을 요청한다.
     *
	 * @author : yjPark
     * @param param the param
     * @return the map
	 * @Date : 2023. 6. 26
	 * @Method Name : saveProcessConditionalPass
     */
    @AuthCheck (authCode = Const.SAVE)
    @RequestMapping(value = "saveProcessConditionalPass.do")
    public @ResponseBody ResultMap saveProcessConditionalPass(@RequestBody Map param) {
        return onboardingEvalMonitoringService.saveProcessConditionalPass(param);
    }

	/**
     * 온보딩평가 모니터링 - 절차이동 저장을 요청한다.
     *
	 * @author : yjPark
     * @param param the param
     * @return the map
	 * @Date : 2023. 6. 26
	 * @Method Name : saveMoveNextPrcsProcess
     */
    /* 등록심사 진행관리 - 절차이동 저장 */
    @AuthCheck (authCode = Const.SAVE)
    @RequestMapping(value = "saveMoveNextPrcsProcess.do")
    public @ResponseBody ResultMap saveMoveNextPrcsProcess(@RequestBody Map param) {
        return onboardingEvalMonitoringService.saveMoveNextPrcsProcess(param);
    }

	/**
     * 온보딩평가 요청 목록 팝업 조회를 요청한다.
	 *
     * @author : yjPark
     * @param param the param
     * @return the list
	 * @Date : 2023. 6. 25
	 * @Method Name : findListReqInfo
     */
    /* 요청 목록 팝업 조회 */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping(value = "findListReqInfo.do")
    public @ResponseBody List findListReqInfo(@RequestBody Map param) {
        return onboardingEvalMonitoringService.findListReqInfo(param);
    }

	/**
     * 온보딩평가 평가결과상세 목록 조회를 요청한다.
     *
     * @author : yjPark
     * @param param the param
     * @return the list
     * @Date : 2023. 6. 25
     * @Method Name : findListObdEvalDetailScoreInfo
     */
    @AuthCheck (authCode = Const.READ)
    @RequestMapping(value = "findListObdEvalDetailScoreInfo.do")
    public @ResponseBody List findListObdEvalDetailScoreInfo(@RequestBody Map param) {
        return onboardingEvalMonitoringService.findListObdEvalDetailScoreInfo(param);
    }

	/**
     * 온보딩평가 프로세스 정보를 조회한다.
     *
	 * @author : yjpark
	 * @param param the param
     * @return the Map
	 * @Date : 2023. 7. 05
	 * @Method Name : findOePrcsInfo
     */
    @AuthCheck(authCode = Const.READ)
    @RequestMapping(value="findOePrcsInfo.do")
    public @ResponseBody Map<String,Object> findOePrcsInfo(@RequestBody Map param){
    	return onboardingEvalMonitoringService.findOePrcsInfo(param);
    }

	/**
     * 온보딩평가 프로세스 평가 담당자 목록을 조회한다.
     *
	 * @author : yjpark
	 * @param param the param
     * @return the Map
	 * @Date : 2023. 7. 05
	 * @Method Name : findListOePrcsEvaltr
     */
    @AuthCheck(authCode = Const.READ)
    @RequestMapping(value="findListOePrcsEvaltr.do")
    public @ResponseBody List<Map<String,Object>> findListOePrcsEvaltr(@RequestBody Map param){
    	return onboardingEvalMonitoringService.findListOePrcsEvaltr(param);
    }

	/**
     * 온보딩평가 프로세스 평가 담당자를 저장한다.
     *
	 * @author : yjpark
	 * @param param the param
     * @return the map
	 * @Date : 2023. 7. 06
	 * @Method Name : saveOePrcsEvaltr
     */
    @AuthCheck(authCode = Const.SAVE)
    @RequestMapping(value="saveOePrcsEvaltr.do")
    public @ResponseBody ResultMap saveOePrcsEvaltr(@RequestBody Map param){
    	return onboardingEvalMonitoringService.saveOePrcsEvaltr(param);
    }

	/**
     * 온보딩평가 프로세스 평가 담당자를 삭제한다.
     *
     * @author : yjpark
     * @param param the param
     * @return the map
     * @Date : 2023. 7. 06
     * @Method Name : deleteOePrcsEvaltr
     */
    @AuthCheck(authCode = Const.SAVE)
    @RequestMapping(value="deleteOePrcsEvaltr.do")
    public @ResponseBody ResultMap deleteOePrcsEvaltr(@RequestBody Map param){
    	return onboardingEvalMonitoringService.deleteOePrcsEvaltr(param);
    }

	/**
     * 온보딩평가 프로세스를 마감한다.
     *
     * @author : yjpark
     * @param param the param
     * @return the map
     * @Date : 2023. 7. 06
     * @Method Name : closeReqObdEvalPrcs
     */
    @AuthCheck(authCode = Const.SAVE)
    @RequestMapping(value="closeReqObdEvalPrcs.do")
    public @ResponseBody ResultMap closeReqObdEvalPrcs(@RequestBody Map param){
    	return onboardingEvalMonitoringService.closeReqObdEvalPrcs(param);
    }
}