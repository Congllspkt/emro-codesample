package smartsuite.app.bp.pro.asn.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;

import java.util.List;
import java.util.Map;

@Service
public class AsnRepository {
	private static final String NAMESPACE = "asn.";
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
	 * 납품예정 헤더 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findAsn(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findAsn", param);
	}
	
	/**
	 * 납품예정 반려사유 수정
	 *
	 * @param param the param
	 */
	public void updateAsnRejectOpinion(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateAsnRejectOpinion", param);
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
	 * 기성 반려사유를 수정한다.
	 *
	 * @param param the param
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : updateAsnRejectOpn
	 */
	public void updateAsnRejectOpn(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateAsnRejectOpn", param);
	}
	
	/**
	 * 기성요청 아이디로 선급금 여부 조회
	 *
	 * @param appId
	 * @return
	 */
	public String findAdvancePaymentYnByAsnUuid(String appId) {
		return sqlSession.selectOne(NAMESPACE + "findAdvancePaymentYnByAsnUuid", appId);
	}
	
	/**
	 * 기성요청 상세정보
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findProgressPaymentRequest(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findProgressPaymentRequest", param);
	}
	
	/**
	 * 기성승인 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findProgressPaymentByAsnUuId(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findProgressPaymentByAsnUuId", param);
	}
	
	/**
	 * 납품예정 아이디로 입고대상 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findGrByAsnUuid(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findGrByAsnUuid", param);
	}
	
	public Map<String, Object> findPoEvalSetInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPoEvalSetInfo", param);
	}
	
	/**
	 * 문서 출력물을 위한 arHeader 조회
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findInfoDocumentOutputAsnHeader(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE+"findInfoDocumentOutputAsnHeader", param);
	}
	
	/**
	 * 문서 출력물을 위한 arDetail 조회
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListDocumentOutputAsnDetail(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE+"findListDocumentOutputAsnDetail", param);
	}
	
	public void updateGrPicByAsnUuid(Map<String, Object> grData) {
		sqlSession.update(NAMESPACE + "updateGrPicByAsnUuid", grData);
	}
}
