package smartsuite.app.bp.srm.performance.request.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PerformanceEvalMailRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "pfmc-mail.";

	// 평가요청 시 평가 수행 메일 템플릿 파라미터 조회
	public Map findPeSubjEvaltrCreateMailInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPeSubjEvaltrCreateMailInfo", param);
	}

}
