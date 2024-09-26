package smartsuite.app.bp.pro.po;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.po.service.PoEvalService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/pro/po/")
public class PoEvalController {
	
	@Inject
	PoEvalService poEvalService;
	
	@RequestMapping(value = "findPoInfo.do")
	public @ResponseBody Map<String, Object> findPoInfo(@RequestBody Map param) {
		return poEvalService.findPoInfo(param);
	}
	
	@RequestMapping(value = "findGeInfo.do")
	public @ResponseBody Map findGeInfo(@RequestBody Map param) {
		return poEvalService.findGeInfo(param);
	}
	
	@RequestMapping(value = "savePoEval.do")
	public @ResponseBody ResultMap savePoEval(@RequestBody Map param) {
		return poEvalService.savePoEval(param);
	}
	
	@RequestMapping(value = "deletePoEval.do")
	public @ResponseBody ResultMap deletePoEval(@RequestBody Map param) {
		return poEvalService.deletePoEval(param);
	}
}
