package smartsuite.app.sp.edoc.estamptax.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpEstampTaxRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-estamptax.";

	/**
	 * 인지세 정보 가져오기
	 *
	 * @param Map
	 * @return Map
	 */
	public Map findEstampUrlInfo(Map param) {

		return sqlSession.selectOne(NAMESPACE + "findEstampUrlInfo", param);
	}

	/**
	 * 인지세 생성
	 *
	 * @param Map
	 * @return Map
	 */
	public void insertStamptaxPurchaseInfo(Map param) {

		sqlSession.insert(NAMESPACE + "insertStamptaxPurchaseInfo", param);
	}

	/**
	 * 인지세 저장
	 *
	 * @param Map
	 * @return Map
	 */
	public void updateEstampTaxSts(Map param) {

		sqlSession.insert(NAMESPACE + "updateEstampTaxSts", param);
	}

	/**
	 * 인지세 변경정보 조회
	 *
	 * @param Map
	 * @return int
	 */
	public int getPreviousElecStampInfo(Map param) {

		return sqlSession.selectOne(NAMESPACE + "getPreviousElecStampInfo", param);
	}

	/**
	 * 계약정보 조회
	 *
	 * @param Map
	 * @return Map
	 */
	public Map getContract(Map param) {

		return sqlSession.selectOne(NAMESPACE + "getContract", param);
	}

	/**
	 * 인지세 목록 조회
	 *
	 * @param Map
	 * @return List
	 */
	public List findStampTaxList(Map param) {

		return sqlSession.selectList(NAMESPACE + "findStampTaxList", param);
	}

	/**
	 * 인지세 목록 조회
	 *
	 * @param Map
	 * @return String
	 */
	public String getBuyerBizRegNo(Map param) {

		return sqlSession.selectOne(NAMESPACE + "getBuyerBizRegNo", param);
	}

	/**
	 * 인지세 정보 조회
	 *
	 * @param Map
	 * @return Map
	 */
	public Map getSupplierStamptaxInfo(Map param) {

		return sqlSession.selectOne(NAMESPACE + "getSupplierStamptaxInfo", param);
	}

	/**
	 * 인지세 정보 수정
	 *
	 * @param Map
	 * @return void
	 */
	public void updateEstampFileGrpCd(Map param) {

		sqlSession.update(NAMESPACE + "updateEstampFileGrpCd", param);
	}

	/**
	 * 인지세 이력 조회
	 *
	 * @param Map
	 * @return List
	 */
	public List findStampTaxHistoryList(Map param) {

		return sqlSession.selectList(NAMESPACE + "findStampTaxHistoryList", param);
	}

	public void insertEstampTax(Map<String,Object> param) {
		sqlSession.insert(NAMESPACE + "insertEstampTax", param);
	}

	public void updateEstampTax(Map<String,Object> param) {
		sqlSession.update(NAMESPACE + "updateEstampTax", param);
	}

	public int findCountEstampData(Map<String,Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findCountEstampData", param);
	}

	public Map findEstampTax(Map<String,Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findEstampTax", param);
	}

	public Map findReqEstampTax(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findReqEstampTax", param);
	}

	public int checkMinRevNoByCntrNo(String param) {
		return sqlSession.selectOne(NAMESPACE + "checkMinRevNoByCntrNo", param);
	}
}
