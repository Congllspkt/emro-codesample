package smartsuite.app.common.survey;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.survey.service.SurveyExecuteService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/bp/common/survey/**/")
public class SurveyExecuteController {
	
	@Inject
	SurveyExecuteService surveyExecuteService;
	
	@RequestMapping(value = "findListSurveyExecute.do")
	public @ResponseBody List<Map> findListSurveyExecute(@RequestBody Map param) {
		return surveyExecuteService.findListSurveyExecute(param);
	}
	
	@RequestMapping(value = "findSurveyExecute.do")
	public @ResponseBody Map findSurveyExecute(@RequestBody Map param) {
		return surveyExecuteService.findSurveyExecute(param);
	}
	
	@RequestMapping(value = "submitSurveyExecute.do")
	public @ResponseBody ResultMap submitSurveyExecute(@RequestBody Map param) {
		return surveyExecuteService.submitSurveyExecute(param);
	}
}