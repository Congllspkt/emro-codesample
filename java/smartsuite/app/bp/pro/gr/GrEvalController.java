package smartsuite.app.bp.pro.gr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.gr.service.GrEvalService;

import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/pro/gr/**/")
public class GrEvalController {
	
	@Inject
	GrEvalService grEvalService;
	
	@RequestMapping(value = "findGrInfo.do")
	public @ResponseBody Map findGrInfo(@RequestBody Map param) {
		return grEvalService.findGrInfo(param);
	}
	
	@RequestMapping(value = "findGeInfo.do")
	public @ResponseBody Map findGeInfo(@RequestBody Map param) {
		return grEvalService.findGeInfo(param);
	}
	
	@RequestMapping(value = "findListGemt.do")
	public @ResponseBody List findListGemt(@RequestBody Map param) {
		return grEvalService.findListGemt(param);
	}
	
	@RequestMapping(value = "findListGegByGemt.do")
	public @ResponseBody Map findListGegByGemt(@RequestBody Map param) {
		return grEvalService.findListGegByGemt(param);
	}
	
	@RequestMapping(value = "saveGrEval.do")
	public @ResponseBody ResultMap saveGrEval(@RequestBody Map param) {
		return grEvalService.saveGrEval(param);
	}
	
	@RequestMapping(value = "submitGrEval.do")
	public @ResponseBody ResultMap submitGrEval(@RequestBody Map param) {
		return grEvalService.submitGrEval(param);
	}
	
	@RequestMapping(value = "deleteGrEval.do")
	public @ResponseBody ResultMap deleteGrEval(@RequestBody Map param) {
		return grEvalService.deleteGrEval(param);
	}
	
	@RequestMapping(value = "findListGePerform.do")
	public @ResponseBody List findListGePerform(@RequestBody Map param) {
		return grEvalService.findListGePerform(param);
	}
	
	@RequestMapping(value = "findGeEvalSubjectEvaltrInfo.do")
	public @ResponseBody Map findGeEvalSubjectEvaltrInfo(@RequestBody Map param) {
		return grEvalService.findGeEvalSubjectEvaltrInfo(param);
	}
	
	@RequestMapping(value = "findGeEvalfactFulfillInfo.do")
	public @ResponseBody ResultMap findGeEvalfactFulfillInfo(@RequestBody Map param) {
		return grEvalService.findGeEvalfactFulfillInfo(param);
	}
	
	@RequestMapping(value = "saveGeEvalFulfillment.do")
	public @ResponseBody ResultMap saveGeEvalFulfillment(@RequestBody Map param) {
		return grEvalService.saveGeEvalFulfillment(param);
	}
	
	@RequestMapping(value = "findListMonitoringGePrcsEvaltr.do")
	public @ResponseBody List findListMonitoringGePrcsEvaltr(@RequestBody Map param) {
		return grEvalService.findListMonitoringGePrcsEvaltr(param);
	}

	@RequestMapping(value = "findGeEvalByEvalSubjEvaltrResId.do")
	public @ResponseBody Map findGeEvalByEvalSubjEvaltrResId(@RequestBody Map param) {
		return grEvalService.findGeEvalByEvalSubjEvaltrResId(param);
	}
}
