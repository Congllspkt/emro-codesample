package smartsuite.app.bp.pro.shared.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * PrShared 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @see 
 * @FileName ProSharedService.java
 * @package smartsuite.app.bp.pro.shared
 * @Since 2016. 3. 11
 * @변경이력 : [2016. 3. 11] Yeon-u Kim 최초작성
 */
@SuppressWarnings ("unchecked")
@Service
public class ProSharedRepository {

	/** The sql session. */
	@Inject
	private SqlSession sqlSession;

	/** The NAMESPACE. */
	private static final String NAMESPACE = "pro-shared.";

	/**
	 * list cate item 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 3. 11
	 * @Method Name : findListCateItem
	 */
	public List<Map<String, Object>> findListCateItemAndBpa(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListCateItemAndBpa", param);
	}

	public List<Map<String, Object>> findListPoHistory(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPoHistory", param);
	}
	
	public List<Map<String, Object>> findListUnitPriceContractHistory(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListUnitPriceContractHistory", param);
	}
	
	public List<Map<String, Object>> findListCntrItemGroupByPrice(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListCntrItemGroupByPrice", param);
	}
	
	public List<Map<String, Object>> findListMonthlyPoAmt(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListMonthlyPoAmt", param);
	}

	/**
	 * 품목 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findItemBasicInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findItemBasicInfo", param);
	}

	/**
	 * 특정 품목에 대한 협력사 별 발주금액 합계 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListVendorPoTotAmtByItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorPoTotAmtByItem", param);
	}

	/**
	 * 특정 품목에 대한 협력사 별 발주단가 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListVendorPoItemPriceByItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorPoItemPriceByItem", param);
	}

	/**
	 * 협력사 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findVendorBasicInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findVendorBasicInfo", param);
	}

	/**
	 * 특정 협력사에 대한 연도별 구매품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListYearlyPoItemByVendor(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListYearlyPoItemByVendor", param);
	}

	/**
	 * 특정 협력사에 대한 연도별 구매이력 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListYearlyPoTotAmtByVendor(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListYearlyPoTotAmtByVendor", param);
	}

	/**
	 * 특정 협력사에 대한 년월 별 구매이력 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListMonthlyPoTotAmtByVendor(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListMonthlyPoTotAmtByVendor", param);
	}
}
