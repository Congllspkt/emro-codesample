package smartsuite.app.sp.pro.asn.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;

import java.util.List;
import java.util.Map;

@Service
public class SpAsnRepository {
    private static final String NAMESPACE = "sp-asn.";
    @Inject
    private SqlSession sqlSession;

    /**
     * 납품예정 목록 조회
     *
     * @param param
     * @return
     */
    public FloaterStream searchAsn(Map<String, Object> param) {
        return new QueryFloaterStream(sqlSession, NAMESPACE + "searchAsn", param);
    }

    /**
     * 납품예정 데이터 생성
     *
     * @param param
     */
    public void insertAsnData(Map<String, Object> param) {
        sqlSession.insert(NAMESPACE + "insertAsnData", param);
    }

    /**
     * 납품예정 데이터 수정
     *
     * @param param
     */
    public void updateAsnData(Map<String, Object> param) {
        sqlSession.update(NAMESPACE + "updateAsnData", param);
    }

    /**
     * 납품예정 데이터 삭제상태로 변경
     *
     * @param param
     */
    public void updateDeleteAsn(Map<String, Object> param) {
        sqlSession.update(NAMESPACE + "updateDeleteAsn", param);
    }

    /**
     * 납품예정 데이터 조회
     *
     * @param param
     * @return
     */
    public Map<String, Object> findAsnData(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "findAsnData", param);
    }

    /**
     * 납품예정 상태 비교
     *
     * @param param
     * @return
     */
    public Map<String, Object> compareAsnStatus(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "compareAsnStatus", param);
    }
	
	/**
	 * 발주아이디에 따른 기성차수의 값을 반환한다.
	 * @param param
	 * @return
	 */
	public Integer getNewAsnRev(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "getNewAsnRev", param);
    }
	
	/**
     * 기성요청 아이디로 선급금여부를 조회한다.
     * @param appId
     * @return
     */
    public String findAdvancePaymentYnByAsnUuid(String appId) {
        return sqlSession.selectOne(NAMESPACE + "findAdvancePaymentYnByAsnUuid", appId);
    }
	
	/**
     * 기성요청 헤더상태
     * @param param
     * @return
     */
    public Map<String, Object> getProgressPaymentRequestStatus(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "getProgressPaymentRequestStatus", param);
    }
	
	/**
     * 기성헤더 조회
     * @param param
     * @return
     */
    public Map<String, Object> findProgressPaymentRequest(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "findProgressPaymentRequest", param);
    }
    
    /**
	 * 문서 출력물을 위한 spAsnHeader 조회
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findInfoDocumentOutputSpAsnHeader(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE+"findInfoDocumentOutputSpAsnHeader", param);
	}
	
	/**
	 * 문서 출력물을 위한 spAsnDetail 조회
	 *  
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListDocumentOutputSpAsnDetail(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE+"findListDocumentOutputSpAsnDetail", param);
	}
	
	public void updateAsnPaymentTypeCommonCode(Map<String, Object> updateHeader) {
		sqlSession.update(NAMESPACE + "updateAsnPaymentTypeCommonCode", updateHeader);
	}
	
	public Map<String, Object> getProgressPaymentTypeRelatedInfo(Map<String, Object> asnHdInfo) {
		return sqlSession.selectOne(NAMESPACE + "getProgressPaymentTypeRelatedInfo", asnHdInfo);
	}
}
