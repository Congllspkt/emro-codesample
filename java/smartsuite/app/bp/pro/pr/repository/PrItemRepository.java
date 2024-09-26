package smartsuite.app.bp.pro.pr.repository;

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
public class PrItemRepository {

	@Inject
	private SqlSession sqlSession;
	
	@Inject
	QueryFloaterInSqlSession queryFoaterInSqlSession;

	private static final String NAMESPACE = "pr-item.";

	public void deletePrItem(Map prItem) {
		sqlSession.delete(NAMESPACE + "deletePrItem", prItem);
	}

	public void insertPrItem(Map prItem) {
		sqlSession.insert(NAMESPACE + "insertPrItem", prItem);
	}

	public void updatePrItem(Map prItem) {
		sqlSession.update(NAMESPACE + "updatePrItem", prItem);
	}
	
	public List findPrItemByPr(Map prData) {
		return sqlSession.selectList(NAMESPACE + "findPrItemByPr", prData);
	}
	
	public Map findPrItemByUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPrItemByUuid", param);
	}

	/**
	 * 구매진행현황을 조회한다.
	 *
	 * @author : kh_lee
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 14
	 * @Method Name : findListPrItemProg
	 */
	public FloaterStream findListPrItemProg(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListPrItemProg", param);
	}
	
	public List findListPrItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPrItem", param);
	}
	
	/**
	 * 결재서식에서 이전차수 구매요청 item 정보를 조회하는 함수
	 * 
	 * @author : LMS
	 * @param param( pr_no: 구매요청 번호 , pr_revno: 현재차수-1한 값)
	 * @return
	 * @Date : 2017. 05. 29
	 * @Method Name : findPreviousPrItems
	 */
	public List<Map> findPreviousPrItems(Map param) {
		return sqlSession.selectList(NAMESPACE + "findPreviousPrItems", param);
	}
	
	public void updatePrItemPurcGrp(Map updateParam) {
		sqlSession.update(NAMESPACE + "updatePrItemPurcGrp", updateParam);
	}

	public List<Map> findListPrItemHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPrItemHistory", param);
	}

	public List<Map> findListComparePrItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListComparePrItem", param);
	}

	public void updatePrItemPrPurp(Map param) {
		sqlSession.update(NAMESPACE + "updatePrItemPrPurp", param);
	}

	public Map findPrByPrItemId(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPrByPrItemId", param);
	}

    public List<Map<String, Object>> findListPrItemIdsByPrId(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListPrItemIdsByPrId",param);
    }

    public List<Map<String, Object>> findListPrItemAutoPoY(Map prData) {
		return sqlSession.selectList(NAMESPACE+"findListPrItemAutoPoY",prData);
    }

    public Map comparePrDtSts(Map param) {
		return sqlSession.selectOne(NAMESPACE+"comparePrDtSts", param);
	}

	public Map<String, Object> findPrItem(Map param) {
		return sqlSession.selectOne(NAMESPACE+"findPrItem",param);
	}

    public void updatePrItemQtaInfo(Map<String, Object> insertQta) {
		sqlSession.update(NAMESPACE+"updatePrItemQtaInfo",insertQta);
    }

	public List<Map<String,Object>> findListMultiBapCntAndQtaYnByPr(Map param){
		return sqlSession.selectList(NAMESPACE+"findListMultiBapCntAndQtaYnByPr",param);
	}

	public List<Map<String, Object>> findListPrePrItemList(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPrePrItemList", param);
	}

    public List<Map<String, Object>> findListPrItemByPrItemUuids(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPrItemByPrItemUuids",param);
    }
}
