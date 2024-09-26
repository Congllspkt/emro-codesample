package smartsuite.app.sp.pro.catalog.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpCatalogRepository {
	private static final String NAMESPACE = "sp-catalog.";

	@Inject
	private SqlSession sqlSession;

	/**
	 * 카탈로그 목록 조회
	 *
	 * @param param
	 */
	public List<Map<String, Object>> findCatalogList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findCatalogList", param);
	}

	/**
	 * 카탈로그 정보 조회
	 *
	 * @param param
	 */
	public Map findCatalogInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCatalogInfo", param);
	}

	/**
	 * 카탈로그 삭제
	 *
	 * @param param
	 */
	public void insertCatalog(Map param) {
		sqlSession.insert(NAMESPACE + "insertCatalog", param);
	}

	/**
	 * 카탈로그 삭제
	 *
	 * @param param
	 */
	public void deleteCatalog(Map param) {
		sqlSession.update(NAMESPACE + "deleteCatalog", param);
	}

    public void applyCatalogToUprccntrItem(Map param) {
		sqlSession.update(NAMESPACE + "applyCatalogToUprccntrItem", param);
	}

	public void cancelCatalogFromUprccntrItem(Map param) {
		sqlSession.update(NAMESPACE + "cancelCatalogFromUprccntrItem", param);
	}


	public List<Map<String, Object>> findListUprccntrItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUprccntrItem", param);
	}

	public void updateCatalog(Map param) {
		sqlSession.update(NAMESPACE + "updateCatalog", param);
	}

	public Map findUprcInfoWithCatalog(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findUprcInfoWithCatalog", param);
	}

	public Object findListThumbnail(String thnlAthfUuid) {
		return sqlSession.selectList(NAMESPACE + "findListThumbnail", thnlAthfUuid);
	}

	public List<Map<String, Object>> findListAppliedUprcItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAppliedUprcItem", param);
	}
}
