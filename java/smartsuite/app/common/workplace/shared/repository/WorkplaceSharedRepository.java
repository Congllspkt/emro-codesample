package smartsuite.app.common.workplace.shared.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WorkplaceSharedRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "shared-workplace.";

	/**
	 * Main Work Status > Main Work 별 지연/임박/일반/총계 count (cc-task-bar-chart, cc-main-work-status에서 사용)
	 *
	 * @param param {}
	 * @return
	 */
	public List findListMainTaskCount(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListMainTaskCount", param);
	}

	/**
	 * Sub Work Status > Sub Work 별 알람/메모/통보/제외/지연/임박/일반/신규 여부 조회 (cc-sub-work-status에서 사용)
	 *
	 * @param param {}
	 * @return
	 */
	public List findListTaskStatus(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListTaskStatus", param);
	}

	/**
	 * Sub Work Status > Main Work 리스트 (cc-work-line-chart에서 상단 탭 생성 시 사용)
	 *
	 * @param param {}
	 * @return
	 */
	public List findListMainTask(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListMainTask", param);
	}

	/**
	 * Sub Work Status > Sub Work 별 지연/임박/총계 count (cc-work-line-chart에서 사용)
	 *
	 * @param param {up_work_id}
	 * @return
	 */
	public List findListTaskStatusCount(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListTaskStatusCount", param);
	}

	public BigDecimal findItemSimilarityByItemDoctor(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findItemSimilarityByItemDoctor", param);
	}

	public BigDecimal findAutoPoItemRecommendByPriceDoctor(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findAutoPoItemRecommendByPriceDoctor", param);
	}
}
