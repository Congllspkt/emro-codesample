package smartsuite.app.bp.rfx.rfx;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.rfx.service.RfxMailWorkService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/bp/rfx/rfx/**/")
public class RfxMailWorkController {
	
	@Inject
	RfxMailWorkService rfxMailWorkService;
	
	/**
	 * rfx id를 가지고 vendor 관련 정보 및 담당자 발송내역을 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfxVendorListAndHistory.do")
	public @ResponseBody List findListRfxVendorListAndHistory(@RequestBody Map param) {
		return rfxMailWorkService.findListRfxVendorListAndHistory(param);
	}
	
	
	/**
	 * rfx id & vd_cd 가지고 vendor 관련 정보 및 담당자 발송내역을 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findRfxVendorHistoryProcess.do")
	public @ResponseBody ResultMap findRfxVendorHistoryProcess(@RequestBody Map param) {
		return rfxMailWorkService.findRfxVendorHistoryProcess(param);
	}
}
