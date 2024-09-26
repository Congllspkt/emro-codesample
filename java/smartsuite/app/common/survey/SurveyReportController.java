package smartsuite.app.common.survey;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.survey.service.SurveyReportService;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/bp/common/survey/**/")
public class SurveyReportController {
	
	@Inject
	SurveyReportService surveyReportService;
	
	@RequestMapping(value = "findSurveyReport.do")
	public @ResponseBody Map findSurveyReport(@RequestBody Map param) {
		return surveyReportService.findSurveyReport(param);
	}
}