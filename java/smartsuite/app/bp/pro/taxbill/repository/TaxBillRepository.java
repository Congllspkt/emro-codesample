package smartsuite.app.bp.pro.taxbill.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class TaxBillRepository {
	private static final String NAMESPACE = "taxbill.";
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 세금계산서 삭제
	 *
	 * @param param
	 */
	public void deleteTaxBill(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteTaxBill", param);
	}
	
	/**
	 * 세금계산서 생성
	 *
	 * @param taxbillData
	 */
	public void insertTaxBill(Map<String, Object> taxbillData) {
		sqlSession.insert(NAMESPACE + "insertTaxBill", taxbillData);
	}
	
	/**
	 * 세금계산서 업체 메일 변경
	 *
	 * @param taxbillData
	 */
	public void updateTaxBillVendorEmail(Map<String, Object> taxbillData) {
		sqlSession.update(NAMESPACE + "updateTaxBillVendorEmail", taxbillData);
	}
	
	/**
	 * 세금계산서 상세 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findTaxBill(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findTaxBill", param);
	}
	
	/**
	 * 세금계산서 발행현황 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream searchTaxBill(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "searchTaxBill", param);
	}
	
	/**
	 * 세금계산서 상태 비교
	 * @param param
	 * @return
	 */
	public Map<String, Object> compareTaxbillStatus(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "compareTaxbillStatus", param);
	}
	
	/**
	 * 세금계산서 리스트 상태 비교
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> compareTaxbillListStatus(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "compareTaxbillListStatus", param);
	}
}
