package smartsuite.app.bp.pro.upcr.repository;

import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.QueryFloaterInSqlSession;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class UpcrItemRepository {

	@Inject
	private SqlSession sqlSession;
	
	@Inject
	QueryFloaterInSqlSession queryFoaterInSqlSession;

	private static final String NAMESPACE = "upcr-item.";

	public void deleteUpcrItem(Map upcrItem) {
		sqlSession.delete(NAMESPACE + "deleteUpcrItem", upcrItem);
	}

	public void insertUpcrItem(Map upcrItem) {
		sqlSession.insert(NAMESPACE + "insertUpcrItem", upcrItem);
	}

	public void updateUpcrItem(Map upcrItem) {
		sqlSession.update(NAMESPACE + "updateUpcrItem", upcrItem);
	}
	
	public List findUpcrItemByUpcr(Map upcrData) {
		return sqlSession.selectList(NAMESPACE + "findUpcrItemByUpcr", upcrData);
	}

	public List findListRfxItemDefaultDataByUpcrItemIds(List upcrItemIds) {
		return queryFoaterInSqlSession.selectList(NAMESPACE + "findListRfxItemDefaultDataByUpcrItemIds", Maps.newHashMap(), upcrItemIds);
	}

	public List findListRfiItemDefaultDataByUpcrItemIds(Map<String,Object> upcrItemIds) {
		return sqlSession.selectList(NAMESPACE + "findListRfiItemDefaultDataByUpcrItemIds", upcrItemIds);
	}
	
	/**
	 * 구매진행현황을 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 */
	public FloaterStream findListUpcrItemProg(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListUpcrItemProg", param);
	}
	
	public List findListUpcrItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUpcrItem", param);
	}
	
	/**
	 * list upcr and upcr items 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 */
	public FloaterStream findListUpcrAndUpcrItems(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListUpcrAndUpcrItems", param);
	}
	
	/**
	 * 결재서식에서 이전차수 구매요청 item 정보를 조회하는 함수
	 * 
	 * @param param( upcr_no: 구매요청 번호 , upcr_revno: 현재차수-1한 값)
	 * @return
	 */
	public List<Map> findPreviousUpcrItems(Map param) {
		return sqlSession.selectList(NAMESPACE + "findPreviousUpcrItems", param);
	}
	
	public void updateUpcrItemPurcGrp(Map updateParam) {
		sqlSession.update(NAMESPACE + "updateUpcrItemPurcGrp", updateParam);
	}

	public List<Map> findListUpcrItemHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUpcrItemHistory", param);
	}

	public List<Map> findListCompareUpcrItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCompareUpcrItem", param);
	}

	public void updateUpcrItemUpcrPurp(Map param) {
		sqlSession.update(NAMESPACE + "updateUpcrItemUpcrPurp", param);
	}

	public Map findUpcrByUpcrItemId(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findUpcrByUpcrItemId", param);
	}

    public List<Map<String, Object>> findListUpcrItemIdsByUpcrId(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListUpcrItemIdsByUpcrId",param);
    }

    public List<Map<String, Object>> findListUpcrItemAutoPoY(Map upcrData) {
		return sqlSession.selectList(NAMESPACE+"findListUpcrItemAutoPoY",upcrData);
    }

    public List<Map<String, Object>> findListUpcrItemByUpcrItemUuids(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListUpcrItemByUpcrItemUuids",param);
    }
}
