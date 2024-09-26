package smartsuite.app.bp.pro.pr.repository;

import org.apache.ibatis.session.SqlSession;
import org.hibernate.type.IntegerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class PrRepository {
	
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "pr.";

	/**
	 * 구매요청 목적 코드를 조회한다.
	 */
	public List getPrPurpCcd(String param) {
		return sqlSession.selectList(NAMESPACE + "getPrPurpCcd", param);
	}
	
	/**
	 * list pr 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 3. 8
	 * @Method Name : findListPr
	 */
	public FloaterStream findListPr(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListPr", param);
	}

	/**
	 * pr 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2016. 3. 10
	 * @Method Name : findPr
	 */
	public Map findPr(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPr", param);
	}

	/**
	 * pr rev max 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the string
	 * @Date : 2016. 4. 29
	 * @Method Name : findPrRevMax
	 */
	public Integer findPrRevMax(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPrRevMax", param);
	}
	
	/**
	 * 구매요청 마스터를 삭제한다.
	 * 
	 * @param prData - 삭제할 구매요청 마스터
	 */
	public void deletePr(Map prData) {
		sqlSession.delete(NAMESPACE + "deletePr", prData);
	}
	
	/**
	 * 구매요청 마스터 신규저장한다.
	 * 
	 * @param prData - 신규 저장할 구매요청마스터
	 */
	public void insertPr(Map prData) {
		sqlSession.insert(NAMESPACE + "insertPr", prData);
	}
	
	/**
	 * 구매요청 마스터 정보를 변경한다.
	 * 
	 * @param prData - 변경할 구매요청마스터
	 */
	public void updatePr(Map prData) {
		sqlSession.update(NAMESPACE + "updatePr", prData);
	}
	
	/**
	 * 내담당구매그룹 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 14
	 * @Method Name : findListMyPurcGrpCd
	 */
	public List findListMyPurcGrpCd(Map param) {
		return sqlSession.selectList(NAMESPACE+"findListMyPurcGrpCd",param);
	}
	
	/**
	 * 결재서식에서 이전차수 구매요청정보 조회하는 함수
	 * 
	 * @author : LMS
	 * @param param( pr_no: 구매요청 번호 , pr_revno: 현재차수-1한 값)
	 * @return
	 * @Date : 2017. 05. 29
	 * @Method Name : findPreviousPrInfo
	 */
	public Map findPreviousPrInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPreviousPrInfo", param);
	}
	
	/**
	 * 이전차수 구매요청 ID 찾기
	 * 
	 * @param param
	 * @return
	 */
	public String findPrevPrId(String prId) {
		return sqlSession.selectOne(NAMESPACE + "findPrevPrId", prId);
	}

	public List<Map> findListPrHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPrHistory", param);
	}

	public Map findComparePrData(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findComparePrData", param);
	}

	public void updatePrTotAmt(Map param) {
		sqlSession.update(NAMESPACE + "updatePrTotAmt", param);
	}

    public Integer countAutoPoItem(Map<String, Object> pr) {
		return sqlSession.selectOne(NAMESPACE+"countAutoPoItem",pr);
    }

	public Integer countTouchlessItem(Map<String, Object> pr){
		return sqlSession.selectOne(NAMESPACE+"countTouchlessItem",pr);
	}


    public List<Map<String,Object>> findListPrByPrItems(Map param) {
		return sqlSession.selectList(NAMESPACE+"findListPrByPrItems",param);
    }

	public Map comparePrHdSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "comparePrHdSts", param);
	}

	public Map findPrItemUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPrItemUuid", param);
	}
}
