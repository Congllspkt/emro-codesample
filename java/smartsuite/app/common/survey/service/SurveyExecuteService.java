package smartsuite.app.common.survey.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.survey.repository.SurveyExecuteRepository;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SurveyExecuteService {
	
	@Inject
	SurveyExecuteRepository surveyExecuteRepository;
	
	public List<Map> findListSurveyExecute(Map param) {
		return surveyExecuteRepository.findListSurveyExecute(param);
	}
	
	public Map findSurveyExecute(Map param) {
		Map survey = surveyExecuteRepository.findSurveyExecute(param);
		List<Map> surveySects = surveyExecuteRepository.findListSurveySect(survey);
		List<Map> surveyQns = surveyExecuteRepository.findListSurveyQn(survey);
		List<Map> surveyAnss = surveyExecuteRepository.findListSurveyAns(survey);
		
		List<Map> surveySubjectAnss = surveyExecuteRepository.findListSurveySubjectAnswer(survey);
		List<Map> surveySubjectAnsOpts = surveyExecuteRepository.findListSurveySubjectAnswerOptions(survey);
		List<Map> surveySubjectGridAnss = surveyExecuteRepository.findListSurveySubjectGridAnswer(survey);
		List<Map> surveySubjectGridAnsOpts = surveyExecuteRepository.findListSurveySubjectGridAnswerOptions(survey);
		
		List<Map> recursiveSurveySects = this.makeRecursiveSurveySects(
				surveySects,
				surveyQns,
				surveyAnss,
				surveySubjectAnss,
				surveySubjectAnsOpts,
				surveySubjectGridAnss,
				surveySubjectGridAnsOpts);
		
		Map result = Maps.newHashMap();
		result.put("survey", survey);
		result.put("sections", recursiveSurveySects);
		return result;
	}
	
	private List<Map> makeRecursiveSurveySects(
			List<Map> surveySects,
			List<Map> surveyQns,
			List<Map> surveyAnss,
			List<Map> surveySubjectAnss,
			List<Map> surveySubjectAnsOpts,
			List<Map> surveySubjectGridAnss,
			List<Map> surveySubjectGridAnsOpts) {
		
		// 질문에 객관식 답변 옵션 연결
		for(Map surveyQn : surveyQns) {
			List<Map> filteredSurveyAns = surveyAnss.stream()
					.filter(surveyAn -> surveyAn.get("surv_qn_uuid").equals(surveyQn.get("surv_qn_uuid")))
					.collect(Collectors.toList());
			
			List<Map> filteredSubjectAns = surveySubjectAnss.stream()
					.filter(surveySubjectAn -> surveySubjectAn.get("surv_qn_uuid").equals(surveyQn.get("surv_qn_uuid")))
					.collect(Collectors.toList());
			
			List<Map> filteredSubjectAnsOpts = surveySubjectAnsOpts.stream()
					.filter(surveySubjectAnOpt -> surveySubjectAnOpt.get("surv_qn_uuid").equals(surveyQn.get("surv_qn_uuid")))
					.collect(Collectors.toList());
			
			List<Map> filteredSubjectGridAns = surveySubjectGridAnss.stream()
					.filter(surveySubjectGridAn -> surveySubjectGridAn.get("surv_qn_uuid").equals(surveyQn.get("surv_qn_uuid")))
					.collect(Collectors.toList());
			
			List<Map> filteredSubjectGridAnsOpts = surveySubjectGridAnsOpts.stream()
					.filter(surveySubjectGridAnOpt -> surveySubjectGridAnOpt.get("surv_qn_uuid").equals(surveyQn.get("surv_qn_uuid")))
					.collect(Collectors.toList());
			
			surveyQn.put("answers", filteredSurveyAns);
			surveyQn.put("subjectAnswer", filteredSubjectAns.isEmpty() ? null : filteredSubjectAns.get(0));
			surveyQn.put("subjectAnswerOpt", filteredSubjectAnsOpts.isEmpty() ? null : filteredSubjectAnsOpts.get(0));
			surveyQn.put("subjectGridAnswer", filteredSubjectGridAns.isEmpty() ? null : filteredSubjectGridAns.get(0));
			surveyQn.put("subjectGridAnswerOpt", filteredSubjectGridAnsOpts.isEmpty() ? null : filteredSubjectGridAnsOpts.get(0));
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
	
	public ResultMap submitSurveyExecute(Map param) {
		Map survey = (Map) param.get("survey");
		List<Map> answers = (List<Map>) param.get("answers");
		
		surveyExecuteRepository.deleteSurveySubjectAnsOptionMapping(survey);
		surveyExecuteRepository.deleteSurveySubjectAnsMapping(survey);
		surveyExecuteRepository.deleteSurveySubjectGridAnsOptionMapping(survey);
		surveyExecuteRepository.deleteSurveySubjectGridAnsMapping(survey);
		
		surveyExecuteRepository.submitSurveyExecute(survey);
		this.insertSurveyExecuteAnswers(survey, answers);
		
		if(!"NA".equals(survey.get("stat_meth_ccd"))) {
			// 통계방식이 존재하므로 계산
			Map calculateSurveySubject = surveyExecuteRepository.calculateSurveySubject(survey);
			surveyExecuteRepository.updateSurveySubjectScore(calculateSurveySubject);
		}
		return ResultMap.SUCCESS(survey);
	}
	
	private void insertSurveyExecuteAnswers(Map survey, List<Map> answers) {
		for(Map answer : answers) {
			if(answer.get("par_surv_qn_uuid") == null) {
				// 일반 답변 형태인 경우
				this.insertSurveyExecuteAnswer(survey, answer);
			} else {
				// 그리드 답변 형태인 경우
				this.insertSurveyExecuteGridAnswer(survey, answer);
			}
		}
	}
	
	private void insertSurveyExecuteAnswer(Map survey, Map answer) {
		answer.put("surv_subj_uuid", survey.get("surv_subj_uuid"));
		surveyExecuteRepository.insertSurveyExecuteAnswer(answer);
		
		// 선택형인데 단건인 경우
		if(answer.get("surv_ans_opt_uuid") != null) {
			surveyExecuteRepository.insertSurveyExecuteAnswerOption(answer);
		}
		
		// 선택형인데 다중인 경우
		List<Map> answerOptions = (List<Map>) answer.get("answers");
		if(answerOptions != null && !answerOptions.isEmpty()) {
			for(Map option : answerOptions) {
				option.put("surv_subj_uuid", survey.get("surv_subj_uuid"));
				surveyExecuteRepository.insertSurveyExecuteAnswerOption(option);
			}
		}
	}
	
	private void insertSurveyExecuteGridAnswer(Map survey, Map answer) {
		answer.put("surv_subj_uuid", survey.get("surv_subj_uuid"));
		surveyExecuteRepository.insertSurveyExecuteGridAnswer(answer);
		
		// 선택형인데 단건인 경우
		if(answer.get("surv_ans_opt_uuid") != null) {
			surveyExecuteRepository.insertSurveyExecuteGridAnswerOption(answer);
		}
		
		// 선택형인데 다중인 경우
		List<Map> answerOptions = (List<Map>) answer.get("answers");
		if(answerOptions != null && !answerOptions.isEmpty()) {
			for(Map option : answerOptions) {
				option.put("surv_subj_uuid", survey.get("surv_subj_uuid"));
				surveyExecuteRepository.insertSurveyExecuteGridAnswerOption(option);
			}
		}
	}
}