package smartsuite.app.bp.pro.catalog.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CatalogRepository {
	private static final String NAMESPACE = "catalog.";

	@Inject
	private SqlSession sqlSession;

	public List<Map<String, Object>> findListUprcItemWithCatalog(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUprcItemWithCatalog", param);
	}

	public List<Map<String, Object>> findListUprcItemWithoutCatalog(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUprcItemWithoutCatalog", param);
	}
}
