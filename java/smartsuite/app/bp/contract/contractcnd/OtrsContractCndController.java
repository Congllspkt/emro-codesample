package smartsuite.app.bp.contract.contractcnd;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;

import smartsuite.app.bp.contract.contractcnd.service.OtrsContractCndService;
import smartsuite.app.common.shared.ResultMap;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/contract/contractcnd/**/")
public class OtrsContractCndController {
	
	@Inject
	OtrsContractCndService otrsContractCndService;
	
	@RequestMapping(value = "findOtrsCntr.do")
	public @ResponseBody Map findOtrsCntr(@RequestBody Map param) {
		return otrsContractCndService.find(param);
	}
	
	@RequestMapping(value = "saveOtrsCntr.do")
	public @ResponseBody ResultMap saveOtrsCntr(@RequestBody Map param) {
		String cntrCndId = otrsContractCndService.save(param);
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("cntr_cnd_uuid", cntrCndId);
		return ResultMap.SUCCESS(resultMap);
	}
	
}
