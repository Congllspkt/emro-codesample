package smartsuite.app.bp.rfx.rfx;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.rfx.service.RfxNxtPrcsService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/rfx/rfx/**/")
public class RfxNxtPrcsController {
	
	@Inject
	RfxNxtPrcsService rfxNxtPrcsService;
	
	@RequestMapping(value = "findDefaultNxtPrcsReqDataByReqItems.do")
	public @ResponseBody Map findDefaultNxtPrcsReqDataByReqItems(@RequestBody Map param) {
		return rfxNxtPrcsService.findDefaultNxtPrcsReqDataByReqItems(param);
	}
	
	@RequestMapping(value = "findRfxNxtPrcsReq.do")
	public @ResponseBody Map findRfxNxtPrcsReq(@RequestBody Map param) {
		return rfxNxtPrcsService.findRfxNxtPrcsReq(param);
	}
	
	@RequestMapping(value = "saveRfxNxtPrcsReq.do")
	public @ResponseBody ResultMap saveRfxNxtPrcsReq(@RequestBody Map param) {
		return rfxNxtPrcsService.saveRfxNxtPrcsReq(param);
	}
	
	@RequestMapping(value = "confirmRfxNxtPrcsReq.do")
	public @ResponseBody ResultMap confirmRfxNxtPrcsReq(@RequestBody Map param) {
		return rfxNxtPrcsService.confirmRfxNxtPrcsReq(param);
	}
	
	@RequestMapping(value = "cancelConfirmRfxNxtPrcsReq.do")
	public @ResponseBody ResultMap cancelConfirmRfxNxtPrcsReq(@RequestBody Map param) {
		return rfxNxtPrcsService.cancelConfirmRfxNxtPrcsReq(param);
	}
	
	@RequestMapping(value = "deleteRfxNxtPrcsReq.do")
	public @ResponseBody ResultMap deleteRfxNxtPrcsReq(@RequestBody Map param) {
		return rfxNxtPrcsService.deleteRfxNxtPrcsReq(param);
	}
	
	@RequestMapping(value = "requestNxtPrcs.do")
	public @ResponseBody List<ResultMap> requestNxtPrcs(@RequestBody Map param) {
		return rfxNxtPrcsService.requestNxtPrcs(param);
	}
}
