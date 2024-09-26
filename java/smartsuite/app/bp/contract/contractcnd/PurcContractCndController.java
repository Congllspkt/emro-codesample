package smartsuite.app.bp.contract.contractcnd;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.contract.contractcnd.service.PurcContractCndService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/contract/contractcnd/**/")
public class PurcContractCndController {
	
	@Inject
	PurcContractCndService purcContractCndService;
	
	@RequestMapping(value = "findPurcCntr.do")
	public @ResponseBody Map findPurcCntr(@RequestBody Map param) {
		return purcContractCndService.find(param);
	}
	
	@RequestMapping(value = "savePurcCntr.do")
	public @ResponseBody ResultMap savePurcCntr(@RequestBody Map param) {
		return purcContractCndService.savePurcCntr(param, false);
	}
	
	@RequestMapping(value = "deletePurcCntr.do")
	public @ResponseBody ResultMap deletePurcCntr(@RequestBody Map param) {
		return purcContractCndService.deletePurcCntr(param);
	}
}
