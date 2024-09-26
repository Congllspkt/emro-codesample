package smartsuite.app.sp.rfx.rfxpreinsp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.rfxpreinsp.service.SpRfxPreInspService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/sp/rfx/rfxpreinsp/**/")
public class SpRfxPreInspController {
	
	@Inject
	SpRfxPreInspService spRfxPreInspService;
	
	@RequestMapping(value = "findListRfxPreInsp.do")
	public @ResponseBody List findListRfxPreInsp(@RequestBody Map param){
		return spRfxPreInspService.findListRfxPreInsp(param);
	}
	
	@RequestMapping(value = "findRfxPreInspApp.do")
	public @ResponseBody Map findRfxPreInspApp(@RequestBody Map param){
		return spRfxPreInspService.findRfxPreInspApp(param);
	}
	
	@RequestMapping(value = "saveRfxPreInspApp.do")
	public @ResponseBody ResultMap saveRfxPreInspApp(@RequestBody Map param){
		return spRfxPreInspService.saveRfxPreInspApp(param);
	}
	
	@RequestMapping(value = "deleteRfxPreInspApp.do")
	public @ResponseBody ResultMap deleteRfxPreInspApp(@RequestBody Map param){
		return spRfxPreInspService.deleteRfxPreInspApp(param);
	}
	
	@RequestMapping(value="checkValidRfxPreInspAppReg.do")
	public @ResponseBody ResultMap checkValidRfxPreInspAppReg(@RequestBody Map param){
		return spRfxPreInspService.checkValidRfxPreInspAppReg(param);
	}
}
