package smartsuite.app.bp.pro.taxbill.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

@Service
public class TaxBillCommonRepository {
	private static final String NAMESPACE = "taxbill.";
	@Inject
	private SqlSession sqlSession;
	
	/**
	 * 협력사 담당자 조회
	 *
	 * @param vdChrParam
	 * @return
	 */
	public Map<String, Object> findSupplierPersonInCharge(Map<String, Object> vdChrParam) {
		return sqlSession.selectOne(NAMESPACE + "findSupplierPersonInCharge", vdChrParam);
	}
	
	/**
	 * 조직 조회
	 *
	 * @param compParam
	 * @return
	 */
	public Map<String, Object> findCompanyByOorgCd(Map<String, Object> compParam) {
		return sqlSession.selectOne(NAMESPACE + "findCompanyByOorgCd", compParam);
	}
}
