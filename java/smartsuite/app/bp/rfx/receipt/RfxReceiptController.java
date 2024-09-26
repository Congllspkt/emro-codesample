package smartsuite.app.bp.rfx.receipt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.receipt.service.RfxReceiptService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/rfx/receipt/**/")
public class RfxReceiptController {
	
	@Inject
	RfxReceiptService rfxReceiptService;
	
	@RequestMapping(value = "findListRfxReceipt.do")
	public @ResponseBody List findListRfxReceipt(@RequestBody Map param) {
		return rfxReceiptService.findListRfxReceipt(param);
	}
	
	@RequestMapping(value = "receiptReqs.do")
	public @ResponseBody ResultMap receiptReqs(@RequestBody Map param) {
		return rfxReceiptService.receiptReqs(param);
	}
	
	@RequestMapping(value = "returnReqs.do")
	public @ResponseBody ResultMap returnReqs(@RequestBody Map param) {
		return rfxReceiptService.returnReqs(param);
	}
	
	@RequestMapping(value = "changePurcGrp.do")
	public @ResponseBody ResultMap changePurcGrp(@RequestBody Map param) {
		return rfxReceiptService.changePurcGrp(param);
	}
	
	@RequestMapping(value = "checkReqItemsForNextStep.do")
	public @ResponseBody ResultMap checkReqItemsForNextStep(@RequestBody Map param) {
		return rfxReceiptService.checkReqItemsForNextStep(param);
	}
	
	@RequestMapping(value = "saveNxtPrcsReq.do")
	public @ResponseBody ResultMap saveNxtPrcsReq(@RequestBody Map param) {
		return rfxReceiptService.saveNxtPrcsReq(param);
	}
	
	@RequestMapping(value = "findListUprccntr.do")
	public @ResponseBody List findListUprccntr(@RequestBody Map param) {
		return rfxReceiptService.findListUprccntr(param);
	}
	
	@RequestMapping(value = "requestUprccntrPo.do")
	public @ResponseBody ResultMap requestUprccntrPo(@RequestBody Map param) {
		return rfxReceiptService.requestUprccntrPo(param);
	}
	
	@RequestMapping(value = "findListRfxReqRcptQta.do")
	public @ResponseBody List findListRfxReqRcptQta(@RequestBody Map param) {
		return rfxReceiptService.findListRfxReqRcptQta(param);
	}
	
	@RequestMapping(value = "saveRfxNxtPrcsReq.do")
	public @ResponseBody ResultMap saveRfxNxtPrcsReq(@RequestBody Map param) {
		return rfxReceiptService.saveRfxNxtPrcsReq(param);
	}
	
	@RequestMapping(value = "directRequestNxtPrcs.do")
	public @ResponseBody ResultMap directRequestNxtPrcs(@RequestBody Map param) {
		return rfxReceiptService.directRequestNxtPrcs(param);
	}

	@RequestMapping(value = "requestCopyPrToUpcr.do")
	public @ResponseBody ResultMap requestCopyPrToUpcr(@RequestBody Map param) {
		return rfxReceiptService.requestCopyPrToUpcr(param);
	}

	@RequestMapping(value = "findRfxDefaultDataByRfxReqUuid.do")
	public @ResponseBody Map findRfxDefaultDataByRfxReqUuid(@RequestBody Map param){
		return rfxReceiptService.findRfxDefaultDataByRfxReqUuid(param);
	}
}
