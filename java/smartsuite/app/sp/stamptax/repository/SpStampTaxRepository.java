package smartsuite.app.sp.stamptax.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpStampTaxRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-stamptax.";
	public FloaterStream largeSearchListStampTax(Map param) {
		return new QueryFloaterStream(sqlSession,NAMESPACE + "searchListStampTax",param);
	}

	public void updateStampTaxSts(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateStampTaxSts", param);
	}

	public Map findStampTax(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findStampTax", param);
	}

	public void updateStampTax(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateStampTax", param);
	}

	public Map<String, Object> findOfflStampTaxInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findOfflStampTaxInfo", param);
	}

	public void saveStampTaxFile(Map param) {
		sqlSession.update(NAMESPACE + "saveStampTaxFile", param);
	}

	public List findListStampTaxPayHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListStampTaxPayHistory", param);
	}

	public Map findTotalPreStampTaxPay(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findTotalPreStampTaxPay", param);
	}

	public int findMaxStaxCntrRevNo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findMaxStaxCntrRevNoByCntrUuid", param);
	}

	public void saveRefundStampTax(Map param) {
		sqlSession.update(NAMESPACE + "saveRefundStampTax", param);
	}

}
