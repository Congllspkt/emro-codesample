package smartsuite.app.sp.pro.po.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class SpPoRepository {
    /** The sql session. */
    @Inject
    private SqlSession sqlSession;

    private static final String NAMESPACE = "sp-po.";

    public FloaterStream findListPo(Map<String, Object> param) {
        return new QueryFloaterStream(sqlSession, NAMESPACE + "findListPoHeader", param);
    }

    public Map<String, Object> findPoHeader(Map<String, Object> param) {
        return  sqlSession.selectOne(NAMESPACE + "findPoHeader", param);
    }
		
	/**
	 * 초기 기성요청 헤더 조회
	 *
	 * @param param
	 * @return
	 */
	 public Map<String, Object> findInitProgressPaymentRequestByPoUuid(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "findInitProgressPaymentRequestByPoUuid", param);
    }
	
	/**
     * 기성요청 가능여부 확인
     * @param param
     * @return
     */
    public Map<String, Object> validateProgressPaymentRequestStatus(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "validateProgressPaymentRequestStatus", param);
    }
	
	/**
     * 발주기준 기성현황
     * @param param
     * @return
     */
    public FloaterStream searchPoForProgressPayment(Map<String, Object> param) {
        return new QueryFloaterStream(sqlSession, NAMESPACE + "searchPoForProgressPayment", param);
    }
    
    /**
	 * 문서 출력물을 위한 poHeader정보 조회
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findInfoDocumentOutputSpPoHeader(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE+"findInfoDocumentOutputSpPoHeader", param);
	}
	
	/**
	 * 문서 출력물을 위한 poDetail정보 조회 
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListDocumentOutputSpPoDetail(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE+"findListDocumentOutputSpPoDetail", param);
	}

    public String findRfxIdByPoId(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE+"findRfxIdByPoId",param);
    }
}
