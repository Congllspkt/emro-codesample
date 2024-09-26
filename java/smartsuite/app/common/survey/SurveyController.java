package smartsuite.app.common.survey;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.survey.service.SurveyService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/bp/common/survey/**/")
public class SurveyController {
	
	@Inject
	SurveyService surveyService;
	
	@RequestMapping(value = "findListSurvey.do")
	public @ResponseBody List<Map> findListSurvey(@RequestBody Map param) {
		return surveyService.findListSurvey(param);
	}
	
	@RequestMapping(value = "deleteListSurvey.do")
	public @ResponseBody ResultMap deleteListSurvey(@RequestBody Map param) {
		return surveyService.deleteListSurvey(param);
	}
	
	@RequestMapping(value = "directCloseListSurvey.do")
	public @ResponseBody ResultMap directCloseListSurvey(@RequestBody Map param) {
		return surveyService.directCloseListSurvey(param);
	}
	
	@RequestMapping(value = "findSurveyByUuid.do")
	public @ResponseBody Map findSurveyByUuid(@RequestBody Map param) {
		return surveyService.findSurveyByUuid(param);
	}
	
	@RequestMapping(value = "saveSurvey.do")
	public @ResponseBody ResultMap saveSurvey(@RequestBody Map param) {
		return surveyService.saveSurvey(param);
	}
	
	@RequestMapping(value = "publishSurvey.do")
	public @ResponseBody ResultMap publishSurvey(@RequestBody Map param) {
		return surveyService.publishSurvey(param);
	}
}