package smartsuite.app.common.survey.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.survey.repository.SurveyTemplateRepository;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SurveyTemplateService {
	
	@Inject
	SurveyTemplateRepository surveyTemplateRepository;
	
	@Inject
	SharedService sharedService;
	
	public List<Map> findListSurveyAnsTemplate(Map param) {
		return surveyTemplateRepository.findListSurveyAnsTemplate(param);
	}
	
	public ResultMap deleteListSurveyAnsTemplate(Map param) {
		List<Map> deleteList = (List<Map>) param.get("deleteList");
		
		for(Map row : deleteList) {
			surveyTemplateRepository.deleteAllSurveyAnsTemplateOptionByTemplate(row);
			surveyTemplateRepository.deleteSurveyAnsTemplate(row);
		}
		return ResultMap.SUCCESS();
	}
	
	public Map findSurveyAnsTemplateByUuid(Map param) {
		Map surveyAnsTemplate = surveyTemplateRepository.findSurveyAnsTemplateByUuid(param);
		List<Map> surveyAnsTemplateOptions = surveyTemplateRepository.findListSurveyAnsTemplateOptionByTemplateUuid(param);
		
		Map result = Maps.newHashMap();
		result.put("surveyAnsTemplate", surveyAnsTemplate);
		result.put("surveyAnsTemplateOptions", surveyAnsTemplateOptions);
		return result;
	}
	
	public ResultMap saveSurveyAnsTemplate(Map param) {
		Map surveyAnsTemplate = (Map) param.get("surveyAnsTemplate");
		List<Map> surveyAnsTemplateOptions = (List<Map>) param.get("surveyAnsTemplateOptions");
		
		boolean isNew = false;
		if(surveyAnsTemplate.get("surv_ans_tmpl_uuid") == null) {
			isNew = true;
		}
		
		if(isNew) {
			String survAnsTmplUuid = UUID.randomUUID().toString();
			surveyAnsTemplate.put("surv_ans_tmpl_uuid", survAnsTmplUuid);
			
			surveyTemplateRepository.insertSurveyAnsTemplate(surveyAnsTemplate);
		} else {
			surveyTemplateRepository.updateSurveyAnsTemplate(surveyAnsTemplate);
		}
		
		surveyTemplateRepository.deleteAllSurveyAnsTemplateOptionByTemplate(surveyAnsTemplate);
		
		// 답변 유형에 따라 저장 데이터 클린징
		List<String> choiceAnsTypCcds = Lists.newArrayList("SINGLE", "MULTI", "RANK", "DROPDOWN");
		List<String> multiSelAnsTypCcds = Lists.newArrayList("MULTI", "RANK");
		String linearAnsTypCcd = "LINEAR";
		String shortAnsTypCcd = "SHORTANS";
		String essayAnsTypCcd = "ESSAYANS";
		String dateAnsTypCcd = "DATE";
		
		String ansTypCcd = (String) surveyAnsTemplate.get("ans_typ_ccd");
		if(!choiceAnsTypCcds.contains(ansTypCcd)) {
			// 객관식이 아닌 경우 객관식 옵션 제거
			surveyAnsTemplateOptions = Lists.newArrayList();
		}
		if(!multiSelAnsTypCcds.contains(ansTypCcd)) {
			surveyAnsTemplate.remove("max_sel_cnt");
		}
		if(!linearAnsTypCcd.equals(ansTypCcd)) {
			// 선형 배율 아닌 경우 관련 옵션 제거
			surveyAnsTemplate.remove("lnr_min_val_ccd");
			surveyAnsTemplate.remove("lnr_max_val_ccd");
			surveyAnsTemplate.remove("lnr_min_nm");
			surveyAnsTemplate.remove("lnr_max_nm");
		}
		if(!shortAnsTypCcd.equals(ansTypCcd) && !essayAnsTypCcd.equals(ansTypCcd)) {
			// 주관식 아닌 경우 관련 옵션 제거
			surveyAnsTemplate.remove("inp_lmt");
		}
		if(!shortAnsTypCcd.equals(ansTypCcd)) {
			// 주관식 단답 아닌 경우 관련 옵션 제거
			surveyAnsTemplate.remove("shortans_inp_form_ccd");
			surveyAnsTemplate.remove("regexp");
		}
		if(!dateAnsTypCcd.equals(ansTypCcd)) {
			surveyAnsTemplate.remove("st_dt_lmt");
			surveyAnsTemplate.remove("ed_dt_lmt");
		}
		
		this.insertSurveyAnsTemplateOption(surveyAnsTemplate, surveyAnsTemplateOptions);
		return ResultMap.SUCCESS(surveyAnsTemplate);
	}
	
	private void insertSurveyAnsTemplateOption(Map surveyAnsTemplate, List<Map> surveyAnsTemplateOptions) {
		for(Map surveyAnsTemplateOption : surveyAnsTemplateOptions) {
			surveyAnsTemplateOption.put("surv_ans_tmpl_uuid", surveyAnsTemplate.get("surv_ans_tmpl_uuid"));
			surveyAnsTemplateOption.put("surv_ans_tmpl_opt_uuid", UUID.randomUUID().toString());
			surveyTemplateRepository.insertSurveyAnsTemplateOption(surveyAnsTemplateOption);
		}
	}
	
	public List<Map> findListSurveyTemplate(Map param) {
		return surveyTemplateRepository.findListSurveyTemplate(param);
	}
	
	public ResultMap deleteListSurveyTemplate(Map param) {
		List<Map> deleteList = (List<Map>) param.get("deleteList");
		
		for(Map row : deleteList) {
			surveyTemplateRepository.deleteSurveyTemplateAnsBySurveyTemplateUuid(row);
			surveyTemplateRepository.deleteSurveyTemplateQnBySurveyTemplateUuid(row);
			surveyTemplateRepository.deleteSurveyTemplateSectBySurveyTemplateUuid(row);
			surveyTemplateRepository.deleteSurveyTemplate(row);
		}
		return ResultMap.SUCCESS();
	}
	
	public Map findSurveyTemplateByUuid(Map param) {
		Map surveyTemplate = surveyTemplateRepository.findSurveyTemplateByUuid(param);
		List<Map> surveyTemplateSects = surveyTemplateRepository.findListSurveyTemplateSectBySurveyTemplateUuid(param);
		List<Map> surveyTemplateQns = surveyTemplateRepository.findListSurveyTemplateQnBySurveyTemplateUuid(param);
		List<Map> surveyTemplateAnss = surveyTemplateRepository.findListSurveyTemplateAnsBySurveyTemplateUuid(param);
		
		List<Map> recursiveSurveyTemplateSects = this.makeRecursiveSurveyTemplateSects(surveyTemplateSects, surveyTemplateQns, surveyTemplateAnss);
		
		Map result = Maps.newHashMap();
		result.put("surveyTemplate", surveyTemplate);
		result.put("sections", recursiveSurveyTemplateSects);
		return result;
	}
	
	private List<Map> makeRecursiveSurveyTemplateSects(List<Map> surveyTemplateSects, List<Map> surveyTemplateQns, List<Map> surveyTemplateAnss) {
		// 질문에 객관식 답변 옵션 연결
		for(Map surveyTemplateQn : surveyTemplateQns) {
			List<Map> filteredSurveyTemplateAns = surveyTemplateAnss.stream()
					.filter(surveyTemplateAn -> surveyTemplateAn.get("surv_tmpl_qn_uuid").equals(surveyTemplateQn.get("surv_tmpl_qn_uuid")))
					.collect(Collectors.toList());
			
			surveyTemplateQn.put("answers", filteredSurveyTemplateAns);
		}
		
		// 상위 질문에 하위 질문 연결
		List<Map> surveyTemplateAllChildrenQns = surveyTemplateQns.stream()
				.filter(surveyTemplateQn -> surveyTemplateQn.get("par_surv_tmpl_qn_uuid") != null)
				.collect(Collectors.toList());
		
		int childrenQnsCnt = surveyTemplateAllChildrenQns.size();
		for(int i = 0; i < surveyTemplateAllChildrenQns.size(); i++) {
			Map surveyTemplateChildrenQn = surveyTemplateAllChildrenQns.get(i);
			Map surveyTemplateParentQn = this.findSurveyTemplateQnByChildren(surveyTemplateQns, surveyTemplateChildrenQn);
			
			if(surveyTemplateParentQn == null) {
				continue;
			}
			List<Map> surveyTemplateChildrenQns = null;
			if(surveyTemplateParentQn.get("questions") == null) {
				surveyTemplateChildrenQns = Lists.newArrayList();
				surveyTemplateParentQn.put("questions", surveyTemplateChildrenQns);
			} else {
				surveyTemplateChildrenQns = (List<Map>) surveyTemplateParentQn.get("questions");
			}
			surveyTemplateChildrenQns.add(surveyTemplateChildrenQn);
		}
		// 하위 질문을 전체 질문 리스트에서 제거 하는 로직을 분리함.
		for(int i = 0; i < surveyTemplateAllChildrenQns.size(); i++) {
			Map surveyTemplateChildrenQn = surveyTemplateAllChildrenQns.get(i);
			surveyTemplateQns.remove(surveyTemplateChildrenQn);
		}
		
		for(Map surveyTemplateSect : surveyTemplateSects) {
			List<Map> filteredSurveyTemplateQns = surveyTemplateQns.stream()
					.filter(surveyTemplateQn -> surveyTemplateQn.get("surv_tmpl_sect_uuid").equals(surveyTemplateSect.get("surv_tmpl_sect_uuid")))
					.collect(Collectors.toList());
			
			surveyTemplateSect.put("questions", filteredSurveyTemplateQns);
		}
		return surveyTemplateSects;
	}
	
	private Map findSurveyTemplateQnByChildren(List<Map> surveyTemplateQns, Map surveyTemplateChildrenQn) {
		List<Map> filteredSurveyTemplateQns = surveyTemplateQns.stream()
				.filter(surveyTemplateQn -> surveyTemplateChildrenQn.get("par_surv_tmpl_qn_uuid").equals(surveyTemplateQn.get("surv_tmpl_qn_uuid")))
				.collect(Collectors.toList());
		
		if(filteredSurveyTemplateQns != null && filteredSurveyTemplateQns.size() > 0) {
			return filteredSurveyTemplateQns.get(0);
		} else {
			return null;
		}
	}
	
	public ResultMap saveSurveyTemplate(Map param) {
		Map surveyTemplate = (Map) param.get("surveyTemplate");
		List<Map> sections = (List<Map>) param.get("sections");
		
		boolean isNew = false;
		if(surveyTemplate.get("surv_tmpl_uuid") == null) {
			isNew = true;
		}
		
		if(isNew) {
			String survTmplUuid = UUID.randomUUID().toString();
			surveyTemplate.put("surv_tmpl_uuid", survTmplUuid);
			
			surveyTemplateRepository.insertSurveyTemplate(surveyTemplate);
		} else {
			// 설문 템플릿의 섹션, 질문, 답변 전부 삭제 후 다시 생성
			surveyTemplateRepository.deleteSurveyTemplateAnsBySurveyTemplateUuid(surveyTemplate);
			surveyTemplateRepository.deleteSurveyTemplateQnBySurveyTemplateUuid(surveyTemplate);
			surveyTemplateRepository.deleteSurveyTemplateSectBySurveyTemplateUuid(surveyTemplate);
			
			surveyTemplateRepository.updateSurveyTemplate(surveyTemplate);
		}
		
		this.saveSurveyTemplateSections(surveyTemplate, sections);
		return ResultMap.SUCCESS(surveyTemplate);
	}
	
	private void saveSurveyTemplateSections(Map surveyTemplate, List<Map> surveyTemplateSects) {
		if(surveyTemplateSects == null) {
			return;
		}
		String sectOrdRandYn = (String) surveyTemplate.get("sect_ord_rand_yn");
		List<Integer> randomList = Lists.newArrayList();
		for(int i = 0; i < surveyTemplateSects.size(); i++) {
			randomList.add(i);
		}
		if("Y".equals(sectOrdRandYn)) {
			Collections.shuffle(randomList);
		}
		
		String survTypCcd = (String) surveyTemplate.get("surv_typ_ccd");
		for(int i = 0; i < surveyTemplateSects.size(); i++) {
			Map surveyTemplateSect = surveyTemplateSects.get(i);
			surveyTemplateSect.put("surv_tmpl_uuid", surveyTemplate.get("surv_tmpl_uuid"));
			surveyTemplateSect.put("surv_tmpl_sect_uuid", UUID.randomUUID().toString());
			surveyTemplateSect.put("surv_sect_sort", i+1);
			surveyTemplateSect.put("surv_sect_rand_sort", randomList.get(i));
			
			if(!"COMM".equals(survTypCcd)) {
				if(Strings.isNullOrEmpty((String) surveyTemplateSect.get("surv_tmpl_sect_cd"))) {
					surveyTemplateSect.put("surv_tmpl_sect_cd", sharedService.generateDocumentNumber("SV"));
				}
			}
			
			surveyTemplateRepository.insertSurveyTemplateSect(surveyTemplateSect);
			
			List<Map> surveyTemplateQns = (List<Map>) surveyTemplateSect.get("questions");
			this.saveSurveyTemplateQns(surveyTemplate, surveyTemplateSect, null, surveyTemplateQns);
		}
	}
	
	private void saveSurveyTemplateQns(Map surveyTemplate, Map surveyTemplateSect, Map surveyTemplateParentQn, List<Map> surveyTemplateQns) {
		if(surveyTemplateQns == null) {
			return;
		}
		String qnOrdRandYn = (String) surveyTemplate.get("qn_ord_rand_yn");
		List<Integer> randomList = Lists.newArrayList();
		for(int i = 0; i < surveyTemplateQns.size(); i++) {
			randomList.add(i);
		}
		if("Y".equals(qnOrdRandYn)) {
			Collections.shuffle(randomList);
		}
		
		String survTypCcd = (String) surveyTemplate.get("surv_typ_ccd");
		for(int i = 0; i < surveyTemplateQns.size(); i++) {
			Map surveyTemplateQn = surveyTemplateQns.get(i);
			surveyTemplateQn.put("surv_tmpl_uuid", surveyTemplateSect.get("surv_tmpl_uuid"));
			surveyTemplateQn.put("surv_tmpl_sect_uuid", surveyTemplateSect.get("surv_tmpl_sect_uuid"));
			surveyTemplateQn.put("surv_tmpl_qn_uuid", UUID.randomUUID().toString());
			surveyTemplateQn.put("surv_qn_sort", i+1);
			surveyTemplateQn.put("surv_qn_rand_sort", randomList.get(i));
			
			if(!"COMM".equals(survTypCcd)) {
				if(Strings.isNullOrEmpty((String) surveyTemplateQn.get("surv_tmpl_qn_cd"))) {
					surveyTemplateQn.put("surv_tmpl_qn_cd", sharedService.generateDocumentNumber("SV"));
				}
			}
			
			// 답변 유형에 따라 저장 데이터 클린징
			List<String> choiceAnsTypCcds = Lists.newArrayList("SINGLE", "MULTI", "RANK", "DROPDOWN", "LINEAR");
			List<String> multiSelAnsTypCcds = Lists.newArrayList("MULTI", "RANK");
			String linearAnsTypCcd = "LINEAR";
			String shortAnsTypCcd = "SHORTANS";
			String essayAnsTypCcd = "ESSAYANS";
			String dateAnsTypCcd = "DATE";
			
			String ansTypCcd = (String) surveyTemplateQn.get("ans_typ_ccd");
			if(!choiceAnsTypCcds.contains(ansTypCcd)) {
				// 객관식이 아닌 경우 객관식 옵션 제거
				surveyTemplateQn.put("answers", null);
			}
			if(!multiSelAnsTypCcds.contains(ansTypCcd)) {
				surveyTemplateQn.remove("max_sel_cnt");
			}
			if(!linearAnsTypCcd.equals(ansTypCcd)) {
				// 선형 배율 아닌 경우 관련 옵션 제거
				surveyTemplateQn.remove("lnr_min_val_ccd");
				surveyTemplateQn.remove("lnr_max_val_ccd");
				surveyTemplateQn.remove("lnr_min_nm");
				surveyTemplateQn.remove("lnr_max_nm");
			}
			if(!shortAnsTypCcd.equals(ansTypCcd) && !essayAnsTypCcd.equals(ansTypCcd)) {
				// 주관식 아닌 경우 관련 옵션 제거
				surveyTemplateQn.remove("inp_lmt");
			}
			if(!shortAnsTypCcd.equals(ansTypCcd)) {
				// 주관식 단답 아닌 경우 관련 옵션 제거
				surveyTemplateQn.remove("shortans_inp_form_ccd");
				surveyTemplateQn.remove("regexp");
			}
			if(!dateAnsTypCcd.equals(ansTypCcd)) {
				surveyTemplateQn.remove("st_dt_lmt");
				surveyTemplateQn.remove("ed_dt_lmt");
			}
			
			if(surveyTemplateParentQn != null) {
				surveyTemplateQn.put("par_surv_tmpl_qn_uuid", surveyTemplateParentQn.get("surv_tmpl_qn_uuid"));
				
				if(surveyTemplateQn.get("par_surv_ans_opt_nm") != null) {
					List<Map> surveyTemplateParentAnss = (List<Map>) surveyTemplateParentQn.get("answers");
					for(Map surveyTemplateParentAns : surveyTemplateParentAnss) {
						if(surveyTemplateParentAns.get("opt_nm").equals(surveyTemplateQn.get("par_surv_ans_opt_nm"))) {
							surveyTemplateQn.put("par_surv_tmpl_ans_opt_uuid", surveyTemplateParentAns.get("surv_tmpl_ans_opt_uuid"));
							break;
						}
					}
				}
			}
			surveyTemplateRepository.insertSurveyTemplateQn(surveyTemplateQn);
			
			List<Map> surveyTemplateAnss = (List<Map>) surveyTemplateQn.get("answers");
			this.saveSurveyTemplateAnss(surveyTemplate, surveyTemplateQn, surveyTemplateAnss);
			
			List<Map> surveyTemplateChildQns = (List<Map>) surveyTemplateQn.get("questions");
			this.saveSurveyTemplateQns(surveyTemplate, surveyTemplateSect, surveyTemplateQn, surveyTemplateChildQns);
		}
	}
	
	private void saveSurveyTemplateAnss(Map surveyTemplate, Map surveyTemplateQn, List<Map> surveyTemplateAnss) {
		if(surveyTemplateAnss == null) {
			return;
		}
		String ansOrdRandYn = (String) surveyTemplate.get("ans_ord_rand_yn");
		List<Integer> randomList = Lists.newArrayList();
		for(int i = 0; i < surveyTemplateAnss.size(); i++) {
			randomList.add(i);
		}
		if("Y".equals(ansOrdRandYn)) {
			Collections.shuffle(randomList);
		}
		
		for(int i = 0; i < surveyTemplateAnss.size(); i++) {
			Map surveyTemplateAn = surveyTemplateAnss.get(i);
			surveyTemplateAn.put("surv_tmpl_uuid", surveyTemplateQn.get("surv_tmpl_uuid"));
			surveyTemplateAn.put("surv_tmpl_sect_uuid", surveyTemplateQn.get("surv_tmpl_sect_uuid"));
			surveyTemplateAn.put("surv_tmpl_qn_uuid", surveyTemplateQn.get("surv_tmpl_qn_uuid"));
			surveyTemplateAn.put("surv_tmpl_ans_opt_uuid", UUID.randomUUID().toString());
			surveyTemplateAn.put("opt_rand_sort", randomList.get(i));
			
			surveyTemplateRepository.insertSurveyTemplateAn(surveyTemplateAn);
		}
	}
	
	public Map findSurveyTemplatePreview(Map param) {
		Map surveyTemplate = surveyTemplateRepository.findSurveyTemplateByUuid(param);
		List<Map> surveyTemplateSects = surveyTemplateRepository.findListSurveyTemplateSectBySurveyTemplateUuid(surveyTemplate);
		List<Map> surveyTemplateQns = surveyTemplateRepository.findListSurveyTemplateQnBySurveyTemplateUuid(surveyTemplate);
		List<Map> surveyTemplateAnss = surveyTemplateRepository.findListSurveyTemplateAnsBySurveyTemplateUuid(surveyTemplate);
		
		List<Map> recursiveSurveyTemplateSects = this.makeRecursiveSurveyTemplateSects(surveyTemplateSects, surveyTemplateQns, surveyTemplateAnss);
		
		Map result = Maps.newHashMap();
		result.put("surveyTemplate", surveyTemplate);
		result.put("sections", recursiveSurveyTemplateSects);
		return result;
	}
}