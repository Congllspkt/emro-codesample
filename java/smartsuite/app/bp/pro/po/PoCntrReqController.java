package smartsuite.app.bp.pro.po;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.po.service.PoCntrReqService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/pro/po/")
public class PoCntrReqController {
	
	@Inject
	PoCntrReqService poCntrReqService;
	
	@RequestMapping(value = "findDefaultPoCntrReq.do")
	public @ResponseBody Map findDefaultPoCntrReq(@RequestBody Map param) {
		return poCntrReqService.findDefaultPoCntrReq(param);
	}
	
	@RequestMapping(value = "findPoCntrReq.do")
	public @ResponseBody Map findPoCntrReq(@RequestBody Map param) {
		return poCntrReqService.findPoCntrReq(param);
	}
	
	@RequestMapping(value = "requestPoCntrReq.do")
	public @ResponseBody ResultMap requestPoCntrReq(@RequestBody Map param) {
		return poCntrReqService.requestPoCntrReq(param);
	}
	
	@RequestMapping(value = "deletePoCntrReq.do")
	public @ResponseBody ResultMap deletePoCntrReq(@RequestBody Map param) {
		return poCntrReqService.deletePoCntrReq(param);
	}
}
