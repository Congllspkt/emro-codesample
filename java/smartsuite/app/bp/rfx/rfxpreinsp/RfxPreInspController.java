package smartsuite.app.bp.rfx.rfxpreinsp;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.rfx.rfxpreinsp.service.RfxPreInspService;

import smartsuite.app.common.shared.ResultMap;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/rfx/rfxpreinsp/**/")
public class RfxPreInspController {
	
	@Inject
	RfxPreInspService rfxPreInspService;
	
	@RequestMapping(value = "findListRfxPreInsp.do")
	public @ResponseBody List findListRfxPreInsp(@RequestBody Map param){
		return rfxPreInspService.findListRfxPreInsp(param);
	}
	
	@RequestMapping(value = "byPassCloseRfxPreInsps.do")
	public @ResponseBody ResultMap byPassCloseRfxPreInsps(@RequestBody Map saveParam) {
		return rfxPreInspService.byPassCloseRfxPreInsps(saveParam);
	}
	
	@RequestMapping(value = "findListRfxPreInspAppAttach.do")
	public @ResponseBody List findListRfxPreInspAppAttach(@RequestBody Map param){
		return rfxPreInspService.findListRfxPreInspAppAttach(param);
	}
	
	@RequestMapping(value = "findListRfxPreInspDetail.do")
	public @ResponseBody Map findListRfxPreInspDetail(@RequestBody Map param){
		return rfxPreInspService.findListRfxPreInspDetail(param);
	}
	
	@RequestMapping(value = "findRfxPreInspApp.do")
	public @ResponseBody Map<String, Object> findRfxPreInspApp(@RequestBody Map param){
		return rfxPreInspService.findRfxPreInspApp(param);
	}
	
	@RequestMapping(value = "passRfxPreInsp.do")
	public @ResponseBody ResultMap passRfxPreInsp(@RequestBody Map param){
		return rfxPreInspService.passRfxPreInsp(param);
	}
	
	@RequestMapping(value = "unPassRfxPreInsp.do")
	public @ResponseBody ResultMap unPassRfxPreInsp(@RequestBody Map param){
		return rfxPreInspService.unPassRfxPreInsp(param);
	}
	@RequestMapping(value = "compRfxPreInsp.do")
	public @ResponseBody ResultMap compRfxPreInsp(@RequestBody Map param){
		return rfxPreInspService.compRfxPreInsp(param);
	}
}
