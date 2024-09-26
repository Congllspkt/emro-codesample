package smartsuite.app.bp.edoc.estamptax.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class EstampTaxRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "estamptax.";
	
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
	 * 인지세 정보 저장
	 *
	 * @param Map
	 * @return void
	 */
	public void insertStamptaxPurchaseInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertStamptaxPurchaseInfo", param);
	}
	
	/**
	 * 인지세 정보 수정
	 *
	 * @param Map
	 * @return void
	 */
	public void updateEstampTaxSts(Map param) {
		sqlSession.update(NAMESPACE + "updateEstampTaxSts", param);
	}
	
	/**
	 * 인지세 변경정보 확인
	 *
	 * @param Map
	 * @return int
	 */
	public int getPreviousElecStampInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getPreviousElecStampInfo", param);
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
	 * 구매사 사업자번호 조회
	 *
	 * @param Map
	 * @return Map
	 */
	public String getBuyerBizRegNo() {
		return sqlSession.selectOne(NAMESPACE + "getBuyerBizRegNo");
	}
	
	/**
	 * 구매사 인지세 정보 조회
	 *
	 * @param Map
	 * @return Map
	 */
	public Map getBuyerStamptaxInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getBuyerStamptaxInfo", param);
	}
	
	/**
	 * 전자수입인지 파일그룹코드 업데이트
	 *
	 * @param Map
	 * @return Map
	 */
	public void updateEstampFileGrpCd(Map param) {
		sqlSession.update(NAMESPACE + "updateEstampFileGrpCd", param);
	}
	
	/**
	 * 인지세 이력 목록 조회
	 *
	 * @param Map
	 * @return List
	 */
	public List findStampTaxHistoryList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findStampTaxHistoryList", param);
	}


	public Map<String, Object> findContract(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findContract", param);
	}

	public int findPaidStampTaxAmt(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPaidStampTaxAmt", param);
	}

	public int findCountEstampData(Map<String,Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findCountEstampData", param);
	}

	public void insertEstampTax(Map<String,Object> param) {
		sqlSession.insert(NAMESPACE + "insertEstampTax", param);
	}

	public void updateEstampTax(Map<String,Object> param) {
		sqlSession.update(NAMESPACE + "updateEstampTax", param);
	}


	public Map findReqEstampTax(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findReqEstampTax", param);
	}

	public List findListEStampTaxPayHistory(Map<Object, String> param) {
		return sqlSession.selectList(NAMESPACE + "findListEStampTaxPayHistory", param);
	}

	public int checkMinRevNoByCntrNo(String param) {
		return sqlSession.selectOne(NAMESPACE + "checkMinRevNoByCntrNo", param);
	}
}
