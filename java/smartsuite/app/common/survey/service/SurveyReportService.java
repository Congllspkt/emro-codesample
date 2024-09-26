package smartsuite.app.common.survey.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.message.MessageUtil;
import smartsuite.app.common.survey.repository.SurveyReportRepository;
import smartsuite.app.common.util.ConvertUtils;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SurveyReportService {
	
	@Inject
	SurveyReportRepository surveyReportRepository;
	
	@Inject
	MessageUtil messageUtil;
	
	public Map findSurveyReport(Map param) {
		Map survey = surveyReportRepository.findSurveyByUuid(param);
		List<Map> surveySubjects = surveyReportRepository.findListSurveySubjectBySurveyUuid(param);
		List<Map> surveySects = surveyReportRepository.findListSurveySectBySurveyUuid(param);
		List<Map> surveyQns = surveyReportRepository.findListSurveyQnBySurveyUuid(param);
		List<Map> surveyAnss = surveyReportRepository.findListSurveyAnsBySurveyUuid(param);
		List<Map> surveySelAnss = surveyReportRepository.findListSelectAnswer(param);
		List<Map> surveyTextAnss = surveyReportRepository.findListSurveyTextAnsBySurveyUuid(param);
		
		List<Map> recursiveSurveySects = this.makeRecursiveSurveySects(
				surveySects,
				surveyQns,
				surveyAnss,
				surveySelAnss,
				surveyTextAnss);
		
		Map result = Maps.newHashMap();
		result.put("survey", survey);
		result.put("subjects", surveySubjects);
		result.put("sections", recursiveSurveySects);
		return result;
	}
	
	private List<Map> makeRecursiveSurveySects(
			List<Map> surveySects,
			List<Map> surveyQns,
			List<Map> surveyAnss,
			List<Map> surveySelAnss,
			List<Map> surveyTextAnss) {
		
		// 질문에 객관식 답변 옵션 연결
		for(Map surveyQn : surveyQns) {
			List<Map> filteredSurveyAns = surveyAnss.stream()
					.filter(surveyAn -> surveyAn.get("surv_qn_uuid").equals(surveyQn.get("surv_qn_uuid")))
					.collect(Collectors.toList());
			
			List<Map> filteredSurveySelAns = surveySelAnss.stream()
					.filter(surveySelAn -> surveySelAn.get("surv_qn_uuid").equals(surveyQn.get("surv_qn_uuid")))
					.collect(Collectors.toList());
			
			List<Map> filteredSurveyTextAns = surveyTextAnss.stream()
					.filter(surveyTextAn -> surveyTextAn.get("surv_qn_uuid").equals(surveyQn.get("surv_qn_uuid")))
					.collect(Collectors.toList());
			
			surveyQn.put("answers", filteredSurveyAns);
			surveyQn.put("selectedAnswers", filteredSurveySelAns);
			surveyQn.put("textAnswers", filteredSurveyTextAns);
		}
		
		// 상위 질문에 하위 질문 연결
		List<Map> surveyAllChildrenQns = surveyQns.stream()
				.filter(surveyQn -> surveyQn.get("par_surv_qn_uuid") != null)
				.collect(Collectors.toList());
		
		int childrenQnsCnt = surveyAllChildrenQns.size();
		for(int i = 0; i < surveyAllChildrenQns.size(); i++) {
			Map surveyChildrenQn = surveyAllChildrenQns.get(i);
			Map surveyParentQn = this.findSurveyQnByChildren(surveyQns, surveyChildrenQn);
			
			if(surveyParentQn == null) {
				continue;
			}
			List<Map> surveyChildrenQns = null;
			if(surveyParentQn.get("questions") == null) {
				surveyChildrenQns = Lists.newArrayList();
				surveyParentQn.put("questions", surveyChildrenQns);
			} else {
				surveyChildrenQns = (List<Map>) surveyParentQn.get("questions");
			}
			surveyChildrenQns.add(surveyChildrenQn);
		}
		// 하위 질문을 전체 질문 리스트에서 제거 하는 로직을 분리함.
		for(int i = 0; i < surveyAllChildrenQns.size(); i++) {
			Map surveyChildrenQn = surveyAllChildrenQns.get(i);
			surveyQns.remove(surveyChildrenQn);
		}
		
		for(Map surveySect : surveySects) {
			List<Map> filteredSurveyQns = surveyQns.stream()
					.filter(surveyQn -> surveyQn.get("surv_sect_uuid").equals(surveySect.get("surv_sect_uuid")))
					.collect(Collectors.toList());
			
			surveySect.put("questions", filteredSurveyQns);
		}
		return surveySects;
	}
	
	private Map findSurveyQnByChildren(List<Map> surveyQns, Map surveyChildrenQn) {
		List<Map> filteredSurveyQns = surveyQns.stream()
				.filter(surveyQn -> surveyChildrenQn.get("par_surv_qn_uuid").equals(surveyQn.get("surv_qn_uuid")))
				.collect(Collectors.toList());
		
		if(filteredSurveyQns != null && filteredSurveyQns.size() > 0) {
			return filteredSurveyQns.get(0);
		} else {
			return null;
		}
	}
}