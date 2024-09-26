package smartsuite.app.common.survey.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class SurveyTemplateRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "survey-template.";
	
	public List<Map> findListSurveyAnsTemplate(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyAnsTemplate", param);
	}
	
	public void deleteSurveyAnsTemplate(Map row) {
		sqlSession.delete(NAMESPACE + "deleteSurveyAnsTemplate", row);
	}
	
	public Map findSurveyAnsTemplateByUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSurveyAnsTemplateByUuid", param);
	}
	
	public List<Map> findListSurveyAnsTemplateOptionByTemplateUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyAnsTemplateOptionByTemplateUuid", param);
	}
	
	public void insertSurveyAnsTemplate(Map surveyAnsTemplate) {
		sqlSession.insert(NAMESPACE + "insertSurveyAnsTemplate", surveyAnsTemplate);
	}
	
	public void updateSurveyAnsTemplate(Map surveyAnsTemplate) {
		sqlSession.update(NAMESPACE + "updateSurveyAnsTemplate", surveyAnsTemplate);
	}
	
	public void deleteAllSurveyAnsTemplateOptionByTemplate(Map surveyAnsTemplate) {
		sqlSession.delete(NAMESPACE + "deleteAllSurveyAnsTemplateOptionByTemplate", surveyAnsTemplate);
	}
	
	public void insertSurveyAnsTemplateOption(Map surveyAnsTemplateOption) {
		sqlSession.insert(NAMESPACE + "insertSurveyAnsTemplateOption", surveyAnsTemplateOption);
	}
	
	public List<Map> findListSurveyTemplate(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyTemplate", param);
	}
	
	public void deleteSurveyTemplateAnsBySurveyTemplateUuid(Map row) {
		sqlSession.delete(NAMESPACE + "deleteSurveyTemplateAnsBySurveyTemplateUuid", row);
	}
	
	public void deleteSurveyTemplateQnBySurveyTemplateUuid(Map row) {
		sqlSession.delete(NAMESPACE + "deleteSurveyTemplateQnBySurveyTemplateUuid", row);
	}
	
	public void deleteSurveyTemplateSectBySurveyTemplateUuid(Map row) {
		sqlSession.delete(NAMESPACE + "deleteSurveyTemplateSectBySurveyTemplateUuid", row);
	}
	
	public void deleteSurveyTemplate(Map row) {
		sqlSession.delete(NAMESPACE + "deleteSurveyTemplate", row);
	}
	
	public Map findSurveyTemplateByUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSurveyTemplateByUuid", param);
	}
	
	public List<Map> findListSurveyTemplateSectBySurveyTemplateUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyTemplateSectBySurveyTemplateUuid", param);
	}
	
	public List<Map> findListSurveyTemplateQnBySurveyTemplateUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyTemplateQnBySurveyTemplateUuid", param);
	}
	
	public List<Map> findListSurveyTemplateAnsBySurveyTemplateUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyTemplateAnsBySurveyTemplateUuid", param);
	}
	
	public void insertSurveyTemplate(Map surveyTemplate) {
		sqlSession.insert(NAMESPACE + "insertSurveyTemplate", surveyTemplate);
	}
	
	public void updateSurveyTemplate(Map surveyTemplate) {
		sqlSession.update(NAMESPACE + "updateSurveyTemplate", surveyTemplate);
	}
	
	public void insertSurveyTemplateSect(Map surveyTemplateSect) {
		sqlSession.insert(NAMESPACE + "insertSurveyTemplateSect", surveyTemplateSect);
	}
	
	public void insertSurveyTemplateQn(Map surveyTemplateQn) {
		sqlSession.insert(NAMESPACE + "insertSurveyTemplateQn", surveyTemplateQn);
	}
	
	public void insertSurveyTemplateAn(Map surveyTemplateAn) {
		sqlSession.insert(NAMESPACE + "insertSurveyTemplateAn", surveyTemplateAn);
	}
}
