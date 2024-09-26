package smartsuite.app.common.survey;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.survey.service.SurveyTemplateService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/bp/common/survey/**/")
public class SurveyTemplateController {
	
	@Inject
	SurveyTemplateService surveyTemplateService;
	
	@RequestMapping(value = "findListSurveyAnsTemplate.do")
	public @ResponseBody List<Map> findListSurveyAnsTemplate(@RequestBody Map param) {
		return surveyTemplateService.findListSurveyAnsTemplate(param);
	}
	
	@RequestMapping(value = "deleteListSurveyAnsTemplate.do")
	public @ResponseBody ResultMap deleteListSurveyAnsTemplate(@RequestBody Map param) {
		return surveyTemplateService.deleteListSurveyAnsTemplate(param);
	}
	
	@RequestMapping(value = "findSurveyAnsTemplateByUuid.do")
	public @ResponseBody Map findSurveyAnsTemplateByUuid(@RequestBody Map param) {
		return surveyTemplateService.findSurveyAnsTemplateByUuid(param);
	}
	
	@RequestMapping(value = "saveSurveyAnsTemplate.do")
	public @ResponseBody ResultMap saveSurveyAnsTemplate(@RequestBody Map param) {
		return surveyTemplateService.saveSurveyAnsTemplate(param);
	}
	
	@RequestMapping(value = "findListSurveyTemplate.do")
	public @ResponseBody List<Map> findListSurveyTemplate(@RequestBody Map param) {
		return surveyTemplateService.findListSurveyTemplate(param);
	}
	
	@RequestMapping(value = "deleteListSurveyTemplate.do")
	public @ResponseBody ResultMap deleteListSurveyTemplate(@RequestBody Map param) {
		return surveyTemplateService.deleteListSurveyTemplate(param);
	}
	
	@RequestMapping(value = "findSurveyTemplateByUuid.do")
	public @ResponseBody Map findSurveyTemplateByUuid(@RequestBody Map param) {
		return surveyTemplateService.findSurveyTemplateByUuid(param);
	}
	
	@RequestMapping(value = "saveSurveyTemplate.do")
	public @ResponseBody ResultMap saveSurveyTemplate(@RequestBody Map param) {
		return surveyTemplateService.saveSurveyTemplate(param);
	}
	
	@RequestMapping(value = "findSurveyTemplatePreview.do")
	public @ResponseBody Map findSurveyTemplatePreview(@RequestBody Map param) {
		return surveyTemplateService.findSurveyTemplatePreview(param);
	}
}