package smartsuite.app.bp.guarantee;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.guarantee.service.GuaranteeService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;


@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/guarantee/**")
public class GuaranteeController {

	@Inject
	GuaranteeService guaranteeService;

	/**
	 * 보증요청대상 조회
	 */
	@RequestMapping(value = "largeFindGuarRequestList.do")
	public @ResponseBody FloaterStream largeFindGuarRequestList(@RequestBody Map param) {
		// 대용량 처리
		return guaranteeService.largeFindGuarRequestList(param);
	}

	/**
	 * 보증요청
	 */
	@RequestMapping(value = "requestGuar.do")
	public @ResponseBody void requestGuar(@RequestBody Map param) {
		guaranteeService.requestGuar(param);
	}

	/**
	 * 보증보험 처리현황 조회
	 */
	@RequestMapping(value = "largeFindGuarList.do")
	public @ResponseBody FloaterStream largeFindGuarList(@RequestBody Map param) {
		// 대용량 처리
		return guaranteeService.largeFindGuarList(param);
	}

	/**
	 * 오프라인보증보험 조회
	 */
	@RequestMapping(value = "getOfflineBondInfo.do")
	public @ResponseBody Map getOfflineBondInfo(@RequestBody Map<String,Object> param) throws Exception{
		return guaranteeService.getOfflineBondInfo(param);
	}

	/**
	 * 오프라인보증보험 승인
	 */
	@RequestMapping(value = "saveOfflineApprove.do")
	public @ResponseBody Map saveOfflineApprove(@RequestBody Map<String,Object> param) throws Exception{
		return guaranteeService.saveOfflineApprove(param);
	}

	/**
	 * 오프라인보증보험 반려
	 */
	@RequestMapping(value = "saveOfflineReject.do")
	public @ResponseBody Map saveOfflineReject(@RequestBody Map<String,Object> param) throws Exception{
		return guaranteeService.saveOfflineReject(param);
	}

}
