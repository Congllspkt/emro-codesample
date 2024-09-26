package smartsuite.app.bp.pro.gr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.gr.service.GrEvalSetupService;
import smartsuite.app.bp.pro.gr.service.GrEvalshtSetupService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/pro/gr/evalsetup/**/")
public class GrEvalSetupController {
	
	@Inject
	GrEvalSetupService grEvalSetupService;
	
	@Inject
	GrEvalshtSetupService grEvalshtSetupService;

	@RequestMapping(value = "findListGemt.do")
	public @ResponseBody List findListGemt(@RequestBody Map param) {
		return grEvalSetupService.findListGemt(param);
	}
	
	@RequestMapping(value = "saveListGemt.do")
	public @ResponseBody ResultMap saveListGemt(@RequestBody Map param) {
		return grEvalSetupService.saveListGemt(param);
	}
	
	@RequestMapping(value = "deleteListGemt.do")
	public @ResponseBody ResultMap deleteListGemt(@RequestBody Map param) {
		return grEvalSetupService.deleteListGemt(param);
	}
	
	@RequestMapping(value = "findListOorgGemt.do")
	public @ResponseBody List findListOorgGemt(@RequestBody Map param) {
		return grEvalSetupService.findListOorgGemt(param);
	}
	
	@RequestMapping(value = "findListGemg.do")
	public @ResponseBody List findListGemg(@RequestBody Map param) {
		return grEvalSetupService.findListGemg(param);
	}
	
	@RequestMapping(value = "saveListGemg.do")
	public @ResponseBody ResultMap saveListGemg(@RequestBody Map param) {
		return grEvalSetupService.saveListGemg(param);
	}
	
	@RequestMapping(value = "deleteListGemg.do")
	public @ResponseBody ResultMap deleteListGemg(@RequestBody Map param) {
		return grEvalSetupService.deleteListGemg(param);
	}
	
	@RequestMapping(value = "findListGeg.do")
	public @ResponseBody List findListGeg(@RequestBody Map param) {
		return grEvalSetupService.findListGeg(param);
	}
	
	@RequestMapping(value = "findGeg.do")
	public @ResponseBody ResultMap findGeg(@RequestBody Map param) {
		return grEvalSetupService.findGeg(param);
	}
	
	@RequestMapping(value = "saveGeg.do")
	public @ResponseBody ResultMap saveGeg(@RequestBody Map param) {
		return grEvalSetupService.saveGeg(param);
	}
	
	@RequestMapping(value = "checkValidBeforeSaveGeg.do")
	public @ResponseBody ResultMap checkValidBeforeSaveGeg(@RequestBody Map param) {
		return grEvalSetupService.checkValidBeforeSaveGeg(param);
	}
	
	@RequestMapping (value = "findGrEvalsht.do")
	public @ResponseBody ResultMap findGrEvalsht(@RequestBody Map param) {
		return grEvalshtSetupService.findGrEvalsht(param);
	}
	
	@RequestMapping (value = "saveGrEvalsht.do")
	public @ResponseBody ResultMap saveGrEvalsht(@RequestBody Map param) {
		return grEvalshtSetupService.saveGrEvalsht(param);
	}
	
	@RequestMapping (value = "saveImportGrEvalsht.do")
	public @ResponseBody ResultMap saveImportGrEvalsht(@RequestBody Map param) {
		return grEvalshtSetupService.saveImportGrEvalsht(param);
	}
	
	@RequestMapping (value = "saveVersionupGrEvalsht.do")
	public @ResponseBody ResultMap saveVersionupGrEvalsht(@RequestBody Map param) {
		return grEvalshtSetupService.saveVersionupGrEvalsht(param);
	}
	
	@RequestMapping (value = "deleteGrEvalsht.do")
	public @ResponseBody ResultMap deleteGrEvalsht(@RequestBody Map param) {
		return grEvalshtSetupService.deleteGrEvalsht(param);
	}
	
	@RequestMapping (value = "saveGrEvalshtPrcs.do")
	public @ResponseBody ResultMap saveGrEvalshtPrcs(@RequestBody Map param) {
		return grEvalshtSetupService.saveGrEvalshtPrcs(param);
	}
	
	@RequestMapping (value = "saveAllGrEvalshtAndPrcses.do")
	public @ResponseBody ResultMap saveAllGrEvalshtAndPrcses(@RequestBody Map param) {
		return grEvalshtSetupService.saveAllGrEvalshtAndPrcses(param);
	}
	
	@RequestMapping (value = "saveCnfdYnGrEvalsht.do")
	public @ResponseBody ResultMap saveCnfdYnGrEvalsht(@RequestBody Map param) {
		return grEvalSetupService.saveCnfdYnGrEvalsht(param);
	}
	
	@RequestMapping(value = "findListGrEvalshtPrcsEvaltr.do")
	public @ResponseBody List findListGrEvalshtPrcsEvaltr(@RequestBody Map param) {
		return grEvalshtSetupService.findListGrEvalshtPrcsEvaltr(param);
	}
	
	
	@RequestMapping (value = "saveListGrEvalshtPrcsEvaltr.do")
	public @ResponseBody ResultMap saveListGrEvalshtPrcsEvaltr(@RequestBody Map param) {
		return grEvalshtSetupService.saveListGrEvalshtPrcsEvaltr(param);
	}
	
	@RequestMapping (value = "findListGrEvalsht.do")
	public @ResponseBody FloaterStream findListGrEvalsht(@RequestBody Map param) {
		// 대용량 처리
		return grEvalshtSetupService.findListGrEvalsht(param);
	}
	
	@RequestMapping (value = "deleteListGeg.do")
	public @ResponseBody ResultMap deleteListGeg(@RequestBody Map param) {
		return grEvalSetupService.deleteListGeg(param);
	}
	
	@RequestMapping(value = "findListGemgEvaltr.do")
	public @ResponseBody List findListGemgEvaltr(@RequestBody Map param) {
		return grEvalSetupService.findListGemgEvaltr(param);
	}
	
	@RequestMapping(value = "deleteListGemgEvaltr.do")
	public @ResponseBody ResultMap deleteListGemgEvaltr(@RequestBody Map param) {
		return grEvalSetupService.deleteListGemgEvaltr(param);
	}
	
	@RequestMapping(value = "saveListGemgEvaltr.do")
	public @ResponseBody ResultMap saveListGemgEvaltr(@RequestBody Map param) {
		return grEvalSetupService.saveListGemgEvaltr(param);
	}
}