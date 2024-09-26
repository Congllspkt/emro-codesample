package smartsuite.app.bp.pro.taxbill.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class TaxBillItemRepository {
	private static final String NAMESPACE = "taxbill-item.";
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 세금계산서 품목 삭제
	 *
	 * @param param
	 */
	public void deleteTaxBillItem(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteTaxBillItem", param);
	}
	
	/**
	 * 세금계산서 품목 데이터 생성
	 *
	 * @param taxbillItem
	 */
	public void insertTaxBillItem(Map<String, Object> taxbillItem) {
		sqlSession.insert(NAMESPACE + "insertTaxBillItem", taxbillItem);
	}
	
	
	/**
	 * 세금계산서 발행현황 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map> searchTaxBillItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "searchTaxBillItem", param);
	}
}
