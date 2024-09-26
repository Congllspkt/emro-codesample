package smartsuite.app.common.survey.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class SurveyRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "survey.";
	
	public List<Map> findListSurvey(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurvey", param);
	}
	
	public void deleteSurveyAnsBySurveyUuid(Map row) {
		sqlSession.delete(NAMESPACE + "deleteSurveyAnsBySurveyUuid", row);
	}
	
	public void deleteSurveyQnBySurveyUuid(Map row) {
		sqlSession.delete(NAMESPACE + "deleteSurveyQnBySurveyUuid", row);
	}
	
	public void deleteSurveySectBySurveyUuid(Map row) {
		sqlSession.delete(NAMESPACE + "deleteSurveySectBySurveyUuid", row);
	}
	
	public void deleteSurveySubjectBySurveyUuid(Map row) {
		sqlSession.delete(NAMESPACE + "deleteSurveySubjectBySurveyUuid", row);
	}
	
	public void deleteSurvey(Map row) {
		sqlSession.delete(NAMESPACE + "deleteSurvey", row);
	}
	
	public void directCloseSurveyBySurveyUuid(Map row) {
		sqlSession.delete(NAMESPACE + "directCloseSurveyBySurveyUuid", row);
	}
	
	public Map findSurveyByUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSurveyByUuid", param);
	}
	
	public List<Map> findListSurveySubjectsBySurveyUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveySubjectsBySurveyUuid", param);
	}
	
	public List<Map> findListSurveySectBySurveyUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveySectBySurveyUuid", param);
	}
	
	public List<Map> findListSurveyQnBySurveyUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyQnBySurveyUuid", param);
	}
	
	public List<Map> findListSurveyAnsBySurveyUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyAnsBySurveyUuid", param);
	}
	
	public void insertSurvey(Map survey) {
		sqlSession.insert(NAMESPACE + "insertSurvey", survey);
	}
	
	public void updateSurvey(Map survey) {
		sqlSession.update(NAMESPACE + "updateSurvey", survey);
	}
	
	public void insertSurveySect(Map surveySect) {
		sqlSession.insert(NAMESPACE + "insertSurveySect", surveySect);
	}
	
	public void insertSurveyQn(Map surveyQn) {
		sqlSession.insert(NAMESPACE + "insertSurveyQn", surveyQn);
	}
	
	public void insertSurveyAn(Map surveyAn) {
		sqlSession.insert(NAMESPACE + "insertSurveyAn", surveyAn);
	}
	
	public void deleteSurveySubject(Map subject) {
		sqlSession.delete(NAMESPACE + "deleteSurveySubject", subject);
	}
	
	public void insertSurveySubject(Map subject) {
		sqlSession.insert(NAMESPACE + "insertSurveySubject", subject);
	}
	
	public void updatePublishSurvey(Map survey) {
		sqlSession.update(NAMESPACE + "updatePublishSurvey", survey);
	}
}
