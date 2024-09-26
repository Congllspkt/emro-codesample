package smartsuite.app.bp.pro.po;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.po.service.PoReceiptService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/pro/po/**/")
public class PoReceiptController {
	
	@Inject
	PoReceiptService poReceiptService;
	
	@RequestMapping(value = "findListPoReceipt.do")
	public @ResponseBody List findListPoReceipt(@RequestBody Map param) {
		return poReceiptService.findListPoReceipt(param);
	}
	
	@RequestMapping(value = "receiptReqs.do")
	public @ResponseBody ResultMap receiptReqs(@RequestBody Map param) {
		return poReceiptService.receiptReqs(param);
	}
	
	@RequestMapping(value = "returnReqs.do")
	public @ResponseBody ResultMap returnReqs(@RequestBody Map param) {
		return poReceiptService.returnReqs(param);
	}
	
	@RequestMapping(value = "changePurcGrp.do")
	public @ResponseBody ResultMap changePurcGrp(@RequestBody Map param) {
		return poReceiptService.changePurcGrp(param);
	}
	
	@RequestMapping(value = "findListPoReceiptItemByUprcItem.do")
	public @ResponseBody List findListPoReceiptItemByUprcItem(@RequestBody Map param) {
		return poReceiptService.findListPoReceiptItemByUprcItem(param);
	}
	
	@RequestMapping(value = "receiptUprcItemReqs.do")
	public @ResponseBody ResultMap receiptUprcItemReqs(@RequestBody Map param) {
		return poReceiptService.receiptUprcItemReqs(param);
	}
	
	@RequestMapping(value = "returnUprcItemReqs.do")
	public @ResponseBody ResultMap returnUprcItemReqs(@RequestBody Map param) {
		return poReceiptService.returnUprcItemReqs(param);
	}
	
	@RequestMapping(value = "changeUprcItemPurcGrp.do")
	public @ResponseBody ResultMap changeUprcItemPurcGrp(@RequestBody Map param) {
		return poReceiptService.changeUprcItemPurcGrp(param);
	}
	
	@RequestMapping(value = "findItemUprcAloc.do")
	public @ResponseBody Map findItemUprcAloc(@RequestBody Map param) {
		return poReceiptService.findItemUprcAloc(param);
	}
	
	@RequestMapping(value = "findListUprccntr.do")
	public @ResponseBody List findListUprccntr(@RequestBody Map param) {
		return poReceiptService.findListUprccntr(param);
	}
	
	@RequestMapping(value = "findListPoReqItemRcptQta.do")
	public @ResponseBody List findListPoReqItemRcptQta(@RequestBody Map param) {
		return poReceiptService.findListPoReqItemRcptQta(param);
	}
	
	@RequestMapping(value = "savePoReqItemRcptQtaAloc.do")
	public @ResponseBody ResultMap savePoReqItemRcptQtaAloc(@RequestBody Map param) {
		return poReceiptService.savePoReqItemRcptQtaAloc(param);
	}
	
	@RequestMapping(value = "createUprccntrPo.do")
	public @ResponseBody ResultMap createUprccntrPo(@RequestBody Map param) {
		return poReceiptService.createUprccntrPo(param);
	}

	@RequestMapping(value = "findPoReceiptByPoReqUuid.do")
	public @ResponseBody Map findPoReceiptByPoReqUuid(@RequestBody Map param) {
		return poReceiptService.findPoReceiptByUuid(param);
	}
}
