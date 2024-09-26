package smartsuite.app.common.survey.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class SurveyReportRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "survey-report.";
	
	public Map findSurveyByUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSurveyByUuid", param);
	}
	
	public List<Map> findListSurveySubjectBySurveyUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveySubjectBySurveyUuid", param);
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
	
	public List<Map> findListSelectAnswer(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSelectAnswer", param);
	}
	
	public List<Map> findListSurveyTextAnsBySurveyUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSurveyTextAnsBySurveyUuid", param);
	}
}
