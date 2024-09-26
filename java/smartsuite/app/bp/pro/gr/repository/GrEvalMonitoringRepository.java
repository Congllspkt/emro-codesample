package smartsuite.app.bp.pro.gr.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class GrEvalMonitoringRepository {
	
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "gr-eval-monitoring.";
	
	public String findCreateGeYnByGrEvalsht(Map grEvalshtInfo) {
		return sqlSession.selectOne(NAMESPACE + "findCreateGeYnByGrEvalsht", grEvalshtInfo);
	}
	
	public String findCreateGeYnByGeg(Map row) {
		return sqlSession.selectOne(NAMESPACE + "findCreateGeYnByGeg", row);
	}
}
