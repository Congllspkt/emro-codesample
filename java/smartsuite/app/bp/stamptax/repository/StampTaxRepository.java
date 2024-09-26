package smartsuite.app.bp.stamptax.repository;

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
public class StampTaxRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "stamptax.";
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

	public Map findStampTaxWithCntr(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findStampTaxWithCntr", param);
	}

	public Map findContractorInfo(Map<String,Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findContractorInfo", param);
	}

	public void insertStampTax(Map<String,Object> param) {
		sqlSession.insert(NAMESPACE + "insertStampTax", param);
	}

	public int findStampTaxCnt(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findStampTaxCnt", param);
	}

	public Map<String, Object> findOfflStampTaxInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findOfflStampTaxInfo", param);
	}

	public void saveStampTaxFile(Map param) {
		sqlSession.update(NAMESPACE + "saveStampTaxFile", param);
	}

	public int findPreContractCnt(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPreContractCnt", param);
	}

	public List findListPreContractInfo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPreContractInfo", param);
	}

	public List findListStampTaxPayHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListStampTaxPayHistory", param);
	}

	public Map findTotalPreStampTaxPay(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findTotalPreStampTaxPay", param);
	}

	public void saveRefundStampTax(Map param) {
		sqlSession.update(NAMESPACE + "saveRefundStampTax", param);
	}

	public Map findPreRefundInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPreRefundInfo", param);
	}

	public List findListStaxByCntrNo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListStaxByCntrNo", param);
	}

	public Map findTotalStampTaxPay(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findTotalStampTaxPay", param);
	}

	public Map findMaxRevNoStampTaxAmt(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findMaxRevNoStampTaxAmtByCntrNo", param);
	}

	public int findMaxStaxCntrRevNo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findMaxStaxCntrRevNoByCntrUuid", param);
	}

	public List findCntrListForStampTax(Map param) {
		return sqlSession.selectList(NAMESPACE + "findCntrListForStampTax", param);
	}

	public void deleteStampTax(Map param) {
		sqlSession.delete(NAMESPACE + "deleteStampTax", param);
	}

}
