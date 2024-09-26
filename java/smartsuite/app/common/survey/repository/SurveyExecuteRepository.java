package smartsuite.app.common.survey.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class SurveyExecuteRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "survey-execute.";
	
	public List<Map> findListSurveyExecute(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyExecute", param);
	}
	
	public Map findSurveyExecute(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSurveyExecute", param);
	}
	
	public List<Map> findListSurveySect(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveySect", param);
	}
	
	public List<Map> findListSurveyQn(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyQn", param);
	}
	
	public List<Map> findListSurveyAns(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyAns", param);
	}
	
	public List<Map> findListSurveySubjectAnswer(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveySubjectAnswer", param);
	}
	
	public List<Map> findListSurveySubjectAnswerOptions(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveySubjectAnswerOptions", param);
	}
	
	public List<Map> findListSurveySubjectGridAnswer(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveySubjectGridAnswer", param);
	}
	
	public List<Map> findListSurveySubjectGridAnswerOptions(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveySubjectGridAnswerOptions", param);
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
	
	public void deleteSurveySubjectAnsOptionMapping(Map survey) {
		sqlSession.delete(NAMESPACE + "deleteSurveySubjectAnsOptionMapping", survey);
	}
	
	public void deleteSurveySubjectAnsMapping(Map survey) {
		sqlSession.delete(NAMESPACE + "deleteSurveySubjectAnsMapping", survey);
	}
	
	public void deleteSurveySubjectGridAnsOptionMapping(Map survey) {
		sqlSession.delete(NAMESPACE + "deleteSurveySubjectGridAnsOptionMapping", survey);
	}
	
	public void deleteSurveySubjectGridAnsMapping(Map survey) {
		sqlSession.delete(NAMESPACE + "deleteSurveySubjectGridAnsMapping", survey);
	}
	
	public void insertSurveyExecuteAnswer(Map answer) {
		sqlSession.insert(NAMESPACE + "insertSurveyExecuteAnswer", answer);
	}
	
	public void insertSurveyExecuteAnswerOption(Map option) {
		sqlSession.insert(NAMESPACE + "insertSurveyExecuteAnswerOption", option);
	}
	
	public void insertSurveyExecuteGridAnswer(Map answer) {
		sqlSession.insert(NAMESPACE + "insertSurveyExecuteGridAnswer", answer);
	}
	
	public void insertSurveyExecuteGridAnswerOption(Map option) {
		sqlSession.insert(NAMESPACE + "insertSurveyExecuteGridAnswerOption", option);
	}
	
	public void submitSurveyExecute(Map survey) {
		sqlSession.update(NAMESPACE + "submitSurveyExecute", survey);
	}
	
	public Map calculateSurveySubject(Map survey) {
		return sqlSession.selectOne(NAMESPACE + "calculateSurveySubject", survey);
	}
	
	public void updateSurveySubjectScore(Map calculateSurveySubject) {
		sqlSession.update(NAMESPACE + "updateSurveySubjectScore", calculateSurveySubject);
	}
}
