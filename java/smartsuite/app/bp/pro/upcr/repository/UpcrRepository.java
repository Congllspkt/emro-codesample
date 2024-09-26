package smartsuite.app.bp.pro.upcr.repository;

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
public class UpcrRepository {
	
	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "upcr.";

	/**
	 * 구매요청 목적 코드를 조회한다.
	 */
	public List getUpcrPurpCcd(String param) {
		return sqlSession.selectList(NAMESPACE + "getUpcrPurpCcd", param);
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
	public FloaterStream findListUpcr(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListUpcr", param);
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
	public Map findUpcr(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findUpcr", param);
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
	public Integer findUpcrRevMax(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findUpcrRevMax", param);
	}
	
	/**
	 * 구매요청 마스터를 삭제한다.
	 * 
	 * @param prData - 삭제할 구매요청 마스터
	 */
	public void deleteUpcr(Map prData) {
		sqlSession.delete(NAMESPACE + "deleteUpcr", prData);
	}
	
	/**
	 * 구매요청 마스터 신규저장한다.
	 * 
	 * @param prData - 신규 저장할 구매요청마스터
	 */
	public void insertUpcr(Map prData) {
		sqlSession.insert(NAMESPACE + "insertUpcr", prData);
	}
	
	/**
	 * 구매요청 마스터 정보를 변경한다.
	 * 
	 * @param prData - 변경할 구매요청마스터
	 */
	public void updateUpcr(Map prData) {
		sqlSession.update(NAMESPACE + "updateUpcr", prData);
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
	public Map findPreviousUpcrInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPreviousUpcrInfo", param);
	}
	
	/**
	 * 이전차수 구매요청 ID 찾기
	 * 
	 * @return
	 */
	public String findPrevUpcrId(String upcrId) {
		return sqlSession.selectOne(NAMESPACE + "findPrevUpcrId", upcrId);
	}

	public List<Map> findListUpcrHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListUpcrHistory", param);
	}

	public Map findCompareUpcrData(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCompareUpcrData", param);
	}

	public void updateUpcrTotAmt(Map param) {
		sqlSession.update(NAMESPACE + "updateUpcrTotAmt", param);
	}

    public List<Map<String,Object>> findListUpcrByUpcrItems(Map param) {
		return sqlSession.selectList(NAMESPACE+"findListUpcrByUpcrItems",param);
    }

	public List<Map<String, Object>> findListPriceGateByUpcr(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPriceGateByUpcr", param);
	}

	public List<Map<String, Object>> findListPriceGateItemsByUpcr(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPriceGateItemsByUpcr", param);
	}

	public Object updatePriceGateByUpcr(Map param) {
		return sqlSession.update(NAMESPACE + "updatePriceGateByUpcr", param);
	}
}
