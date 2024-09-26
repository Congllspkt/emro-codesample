package smartsuite.app.bp.itemMaster.itemcat.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

//품목 속성 Pool 관련 처리하는 서비스 Class입니다.
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemCatRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "item-cat.";


	/**
	 * 품목분류 마스터 조회
	 *
	 * @param
	 * @return the list
	 */
	public FloaterStream findListItemCat(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListItemCat", param);
	}

	/**
	 * 품목분류 마스터 조회
	 * 조회조건이 있는 경우
	 *
	 * @param
	 * @return the list
	 */
	public FloaterStream findListItemCatWithCdOrNm(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListItemCatWithCdOrNm", param);
	}

	/**
	 * 품목분류 상세정보
	 *
	 * @param
	 * @return the map
	 */
	public Map findInfoItemCatClass(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoItemCatClass", param);
	}

	/**
	 * 유사 품목분류 정보
	 *
	 * @param
	 * @return the list
	 */
	public List findListItemCatSimilar(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListItemCatSimilar", param);
	}

	/**
	 * 품목분류 이력정보
	 *
	 * @param
	 * @return the list
	 */
	public List findListItemCatHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListItemCatHistory", param);
	}

	/**
	 * 사용하고 있는 품목코드가 있는 지 확인
	 *
	 * @param
	 * @return int
	 */
	public int checkCntUseItemCat(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkCntUseItemCat", param);
	}

	/**
	 * 동일한 분류코드가 있는 지 확인
	 *
	 * @param
	 * @return int
	 */
	public int checkCntItemCatClass(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkCntItemCatClass", param);
	}

	/**
	 * 품목분류 insert
	 *
	 * @param
	 * @return void
	 */
	public void insertItemCat(Map param) {
		sqlSession.insert(NAMESPACE + "insertItemCat", param);
	}

	/**
	 * 품목분류 update
	 *
	 * @param
	 * @return void
	 */
	public void updateItemCat(Map param) {
		sqlSession.update(NAMESPACE + "updateItemCat", param);
	}

	/**
	 * 품목분류 delete
	 *
	 * @param
	 * @return void
	 */
	public void deleteItemCat(Map param) {
		sqlSession.delete(NAMESPACE + "deleteItemCat", param);
	}

	/**
	 * 유사 품목분류 insert
	 *
	 * @param
	 * @return void
	 */
	public void insertSimItemCat(Map param) {
		sqlSession.insert(NAMESPACE + "insertSimItemCat", param);
	}

	/**
	 * 유사 품목분류 delete
	 *
	 * @param
	 * @return void
	 */
	public void deleteSimItemCat(Map param) {
		sqlSession.delete(NAMESPACE + "deleteSimItemCat", param);
	}

	/**
	 * 품목분류이력 insert
	 *
	 * @param
	 * @return void
	 */
	public void insertItemCatHistRec(Map param) {
		sqlSession.insert(NAMESPACE + "insertItemCatHistRec", param);
	}

	/**
	 * 품목분류이력 delete
	 *
	 * @param
	 * @return void
	 */
	public void deleteItemCatHistRec(Map param) {
		sqlSession.delete(NAMESPACE + "deleteItemCatHistRec", param);
	}
}
