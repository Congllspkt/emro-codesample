package smartsuite.app.bp.pro.portalwidget;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProPortalWidgetService {

	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "pro-portal-widget.";
	
	/**
	 * 구매요청 품목 진행현황 포틀릿
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListPrProgress(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPrProgress", param);
	}
	
	/**
	 * 구매요청 TOP 5 품목 포틀릿
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListPrFreqItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPrFreqItem", param);
	}
}
