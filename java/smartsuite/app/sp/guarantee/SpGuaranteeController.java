package smartsuite.app.sp.guarantee;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/sp/guarantee/**")
public class SpGuaranteeController {

	@Inject
	SpGuaranteeService spGuaranteeService;

	/**
	 * 업체 보증보험 처리현황 조회
	 */
	@RequestMapping(value = "largeFindGuarList.do")
	public @ResponseBody FloaterStream largeFindGuarList(@RequestBody Map param) {
		// 대용량 처리
		return spGuaranteeService.largeFindGuarList(param);
	}

	/**
	 * 오프라인보증보험 조회
	 */
	@RequestMapping(value = "getOfflineBondInfo.do")
	public @ResponseBody Map getOfflineBondInfo(@RequestBody Map<String,Object> param) throws Exception{
		return spGuaranteeService.getOfflineBondInfo(param);
	}
	/**
	 * 오프라인보증보험 보증승인요청
	 */
	@RequestMapping(value ="saveOfflineGuarInfo.do")
	public @ResponseBody ResultMap saveOfflineGuarInfo(@RequestBody Map<String, Object> param) throws Exception {
		return spGuaranteeService.saveOfflineGuarInfo(param);
	}
	/**
	 * 오프라인보증보험 보증취소
	 */
	@RequestMapping(value ="saveOfflineGuarCancel.do")
	public @ResponseBody ResultMap saveOfflineGuarCancel(@RequestBody Map<String, Object> param) throws Exception {
		return spGuaranteeService.saveOfflineGuarCancel(param);
	}

	/**
	 * 보증 요청 정보 조회
	 */
	@RequestMapping(value = "getRequestBond.do")
	public @ResponseBody Map getRequestBond(@RequestBody Map param) {
		return spGuaranteeService.getRequestBond(param);
	}
}
