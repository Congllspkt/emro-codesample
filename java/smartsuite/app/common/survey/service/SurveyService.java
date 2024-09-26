package smartsuite.app.common.survey.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.survey.repository.SurveyRepository;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SurveyService {
	
	@Inject
	SurveyRepository surveyRepository;
	
	@Inject
	SharedService sharedService;
	
	public List<Map> findListSurvey(Map param) {
		return surveyRepository.findListSurvey(param);
	}
	
	public ResultMap deleteListSurvey(Map param) {
		List<Map> deleteList = (List<Map>) param.get("deleteList");
		
		for(Map row : deleteList) {
			surveyRepository.deleteSurveyAnsBySurveyUuid(row);
			surveyRepository.deleteSurveyQnBySurveyUuid(row);
			surveyRepository.deleteSurveySectBySurveyUuid(row);
			surveyRepository.deleteSurveySubjectBySurveyUuid(row);
			surveyRepository.deleteSurvey(row);
		}
		return ResultMap.SUCCESS();
	}
	
	public ResultMap directCloseListSurvey(Map param) {
		List<Map> list = (List<Map>) param.get("list");
		
		for(Map row : list) {
			surveyRepository.directCloseSurveyBySurveyUuid(row);
		}
		return ResultMap.SUCCESS();
	}
	
	public Map findSurveyByUuid(Map param) {
		Map survey = surveyRepository.findSurveyByUuid(param);
		List<Map> surveySubjects = surveyRepository.findListSurveySubjectsBySurveyUuid(param);
		List<Map> surveySects = surveyRepository.findListSurveySectBySurveyUuid(param);
		List<Map> surveyQns = surveyRepository.findListSurveyQnBySurveyUuid(param);
		List<Map> surveyAnss = surveyRepository.findListSurveyAnsBySurveyUuid(param);
		
		List<Map> recursiveSurveySects = this.makeRecursiveSurveySects(surveySects, surveyQns, surveyAnss);
		
		Map result = Maps.newHashMap();
		result.put("survey", survey);
		result.put("subjects", surveySubjects);
		result.put("sections", recursiveSurveySects);
		return result;
	}
	
	private List<Map> makeRecursiveSurveySects(List<Map> surveySects, List<Map> surveyQns, List<Map> surveyAnss) {
		// 질문에 객관식 답변 옵션 연결
		for(Map surveyQn : surveyQns) {
			List<Map> filteredSurveyAns = surveyAnss.stream()
					.filter(surveyAn -> surveyAn.get("surv_qn_uuid").equals(surveyQn.get("surv_qn_uuid")))
					.collect(Collectors.toList());
			
			surveyQn.put("answers", filteredSurveyAns);
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
	
	public ResultMap saveSurvey(Map param) {
		Map survey = (Map) param.get("survey");
		List<Map> insertSubjects = (List<Map>) param.get("insertSubjects");
		List<Map> deleteSubjects = (List<Map>) param.get("deleteSubjects");
		List<Map> sections = (List<Map>) param.get("sections");
		
		boolean isNew = false;
		if(survey.get("surv_uuid") == null) {
			isNew = true;
		}
		
		if(isNew) {
			String survUuid = UUID.randomUUID().toString();
			survey.put("surv_uuid", survUuid);
			survey.put("surv_prgs_sts_ccd", "CRNG");
			
			surveyRepository.insertSurvey(survey);
		} else {
			// 설문 템플릿의 섹션, 질문, 답변 전부 삭제 후 다시 생성
			surveyRepository.deleteSurveyAnsBySurveyUuid(survey);
			surveyRepository.deleteSurveyQnBySurveyUuid(survey);
			surveyRepository.deleteSurveySectBySurveyUuid(survey);
			
			surveyRepository.updateSurvey(survey);
		}
		
		this.insertSurveySubjects(survey, insertSubjects);
		this.deleteSurveySubjects(deleteSubjects);
		
		this.saveSurveySections(survey, sections);
		return ResultMap.SUCCESS(survey);
	}
	
	private void deleteSurveySubjects(List<Map> deleteSubjects) {
		for(Map subject : deleteSubjects) {
			surveyRepository.deleteSurveySubject(subject);
		}
	}
	
	private void insertSurveySubjects(Map survey, List<Map> insertSubjects) {
		for(Map subject : insertSubjects) {
			subject.put("surv_uuid", survey.get("surv_uuid"));
			subject.put("surv_subj_uuid", UUID.randomUUID().toString());
			surveyRepository.insertSurveySubject(subject);
		}
	}
	
	private void saveSurveySections(Map survey, List<Map> surveySects) {
		if(surveySects == null) {
			return;
		}
		String sectOrdRandYn = (String) survey.get("sect_ord_rand_yn");
		List<Integer> randomList = Lists.newArrayList();
		for(int i = 0; i < surveySects.size(); i++) {
			randomList.add(i);
		}
		if("Y".equals(sectOrdRandYn)) {
			Collections.shuffle(randomList);
		}
		
		String survTypCcd = (String) survey.get("surv_typ_ccd");
		for(int i = 0; i < surveySects.size(); i++) {
			Map surveySect = surveySects.get(i);
			surveySect.put("surv_uuid", survey.get("surv_uuid"));
			surveySect.put("surv_sect_uuid", UUID.randomUUID().toString());
			surveySect.put("surv_sect_sort", i + 1);
			surveySect.put("surv_sect_rand_sort", randomList.get(i));
			
			if(!"COMM".equals(survTypCcd)) {
				if(Strings.isNullOrEmpty((String) surveySect.get("surv_sect_cd"))) {
					surveySect.put("surv_sect_cd", sharedService.generateDocumentNumber("SV"));
				}
			}
			
			surveyRepository.insertSurveySect(surveySect);
			
			List<Map> surveyQns = (List<Map>) surveySect.get("questions");
			this.saveSurveyQns(survey, surveySect, null, surveyQns);
		}
	}
	
	private void saveSurveyQns(Map survey, Map surveySect, Map surveyParentQn, List<Map> surveyQns) {
		if(surveyQns == null) {
			return;
		}
		String qnOrdRandYn = (String) survey.get("qn_ord_rand_yn");
		List<Integer> randomList = Lists.newArrayList();
		for(int i = 0; i < surveyQns.size(); i++) {
			randomList.add(i);
		}
		if("Y".equals(qnOrdRandYn)) {
			Collections.shuffle(randomList);
		}
		
		String survTypCcd = (String) survey.get("surv_typ_ccd");
		for(int i = 0; i < surveyQns.size(); i++) {
			Map surveyQn = surveyQns.get(i);
			surveyQn.put("surv_uuid", surveySect.get("surv_uuid"));
			surveyQn.put("surv_sect_uuid", surveySect.get("surv_sect_uuid"));
			surveyQn.put("surv_qn_uuid", UUID.randomUUID().toString());
			surveyQn.put("surv_qn_sort", i + 1);
			surveyQn.put("surv_qn_rand_sort", randomList.get(i));
			
			if(!"COMM".equals(survTypCcd)) {
				if(Strings.isNullOrEmpty((String) surveyQn.get("surv_qn_cd"))) {
					surveyQn.put("surv_qn_cd", sharedService.generateDocumentNumber("SV"));
				}
			}
			
			// 답변 유형에 따라 저장 데이터 클린징
			List<String> choiceAnsTypCcds = Lists.newArrayList("SINGLE", "MULTI", "RANK", "DROPDOWN", "LINEAR");
			List<String> multiSelAnsTypCcds = Lists.newArrayList("MULTI", "RANK");
			String linearAnsTypCcd = "LINEAR";
			String shortAnsTypCcd = "SHORTANS";
			String essayAnsTypCcd = "ESSAYANS";
			String dateAnsTypCcd = "DATE";
			
			String ansTypCcd = (String) surveyQn.get("ans_typ_ccd");
			if(!choiceAnsTypCcds.contains(ansTypCcd)) {
				// 객관식이 아닌 경우 객관식 옵션 제거
				surveyQn.put("answers", null);
			}
			if(!multiSelAnsTypCcds.contains(ansTypCcd)) {
				surveyQn.remove("max_sel_cnt");
			}
			if(!linearAnsTypCcd.equals(ansTypCcd)) {
				// 선형 배율 아닌 경우 관련 옵션 제거
				surveyQn.remove("lnr_min_val_ccd");
				surveyQn.remove("lnr_max_val_ccd");
				surveyQn.remove("lnr_min_nm");
				surveyQn.remove("lnr_max_nm");
			}
			if(!shortAnsTypCcd.equals(ansTypCcd) && !essayAnsTypCcd.equals(ansTypCcd)) {
				// 주관식 아닌 경우 관련 옵션 제거
				surveyQn.remove("inp_lmt");
			}
			if(!shortAnsTypCcd.equals(ansTypCcd)) {
				// 주관식 단답 아닌 경우 관련 옵션 제거
				surveyQn.remove("shortans_inp_form_ccd");
				surveyQn.remove("regexp");
			}
			if(!dateAnsTypCcd.equals(ansTypCcd)) {
				surveyQn.remove("st_dt_lmt");
				surveyQn.remove("ed_dt_lmt");
			}
			
			if(surveyParentQn != null) {
				surveyQn.put("par_surv_qn_uuid", surveyParentQn.get("surv_qn_uuid"));
				
				if(surveyQn.get("par_surv_ans_opt_nm") != null) {
					List<Map> surveyParentAnss = (List<Map>) surveyParentQn.get("answers");
					for(Map surveyParentAns : surveyParentAnss) {
						if(surveyParentAns.get("opt_nm").equals(surveyQn.get("par_surv_ans_opt_nm"))) {
							surveyQn.put("par_surv_ans_opt_uuid", surveyParentAns.get("surv_ans_opt_uuid"));
							break;
						}
					}
				}
			}
			surveyRepository.insertSurveyQn(surveyQn);
			
			List<Map> surveyAnss = (List<Map>) surveyQn.get("answers");
			this.saveSurveyAnss(survey, surveyQn, surveyAnss);
			
			List<Map> surveyChildQns = (List<Map>) surveyQn.get("questions");
			this.saveSurveyQns(survey, surveySect, surveyQn, surveyChildQns);
		}
	}
	
	private void saveSurveyAnss(Map survey, Map surveyQn, List<Map> surveyAnss) {
		if(surveyAnss == null) {
			return;
		}
		String ansOrdRandYn = (String) survey.get("ans_ord_rand_yn");
		List<Integer> randomList = Lists.newArrayList();
		for(int i = 0; i < surveyAnss.size(); i++) {
			randomList.add(i);
		}
		if("Y".equals(ansOrdRandYn)) {
			Collections.shuffle(randomList);
		}
		
		for(int i = 0; i < surveyAnss.size(); i++) {
			Map surveyAn = surveyAnss.get(i);
			surveyAn.put("surv_uuid", surveyQn.get("surv_uuid"));
			surveyAn.put("surv_sect_uuid", surveyQn.get("surv_sect_uuid"));
			surveyAn.put("surv_qn_uuid", surveyQn.get("surv_qn_uuid"));
			surveyAn.put("surv_ans_opt_uuid", UUID.randomUUID().toString());
			surveyAn.put("opt_rand_sort", randomList.get(i));
			
			surveyRepository.insertSurveyAn(surveyAn);
		}
	}
	
	public ResultMap publishSurvey(Map param) {
		ResultMap result = this.saveSurvey(param);
		if(result.isFail()) {
			return result;
		}
		
		Map survey = result.getResultData();
		survey.put("surv_prgs_sts_ccd", "PRGSG");
		
		surveyRepository.updatePublishSurvey(survey);
		return ResultMap.SUCCESS(survey);
	}
}