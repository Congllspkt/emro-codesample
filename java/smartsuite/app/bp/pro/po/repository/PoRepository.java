package smartsuite.app.bp.pro.po.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class PoRepository {
	
	/** The sql session. */
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "po.";
	
	/**
	 * 발주헤더를 등록한다.
	 *
	 * @param param the param
	 * @Method Name : insertPoHeader
	 */
	public void insertPoHeader(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertPoHeader", param);
	}

	
	/**
	 * 지급계획을 등록한다.
	 * 
	 * @param param
	 */
	public void insertPaymentPlan(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertPaymentPlan", param);
	}

	/**
	 * 발주헤더를 수정한다.
	 *
	 * @param param the param
	 * @Method Name : updatePoHeader
	 */
	public void updatePoHeader(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updatePoHeader", param);
	}
	
	public FloaterStream findListPo(Map<String, Object> param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListPoHeader", param);
	}

	public FloaterStream findListCntr(Map<String, Object> param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListCntrHeader", param);
	}

	public Map<String, Object> findPoHeader(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPoHeader", param);
	}

	public Map<String, Object> findCntrHeader(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findCntrHeader", param);
	}
	
	public void deletePoHeader(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "deletePoHeader", param);
	}
	
	public Map findPrevPoByModification(Map param) {
		//findPrevPoByModification
		return sqlSession.selectOne(NAMESPACE + "findPrevPoByModification", param);
	}


	public void updatePrevPoByModification(Map<String, Object> info) {
		sqlSession.update(NAMESPACE + "updatePrevPoByModification", info);
	}


	public Map<String, Object> findPoHeaderByPoNoAndPoRev(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPoHeaderByPoNoAndPoRev", param);
	}


	public Map<String, Object> findComparePoData(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findComparePoData", param);
	}


	public List<Map<String, Object>> findStamptaxAmtRange(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findStamptaxAmtRange", param);
	}


	public List<Map<String, Object>> findPoComplete(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findPoComplete", param);
	}


	public void updatePoComplete(Map<String, Object> info) {
		sqlSession.update(NAMESPACE + "updatePoComplete", info);
	}


	public int getMaxRevisionPoHeader(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "getMaxRevisionPoHeader", param);
	}

	public List<Map<String, Object>> checkPoItemState(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "checkPoItemState", param);
	}


	public void updatePreviousPo(Map<String, Object> param) {
		sqlSession.update(NAMESPACE+"updatePreviousPo",param);
	}


	public void updateCurrentPo(Map<String, Object> param) {
		sqlSession.update(NAMESPACE+"updateCurrentPo",param);
	}


	public void updatePoHeaderPoCreDate(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updatePoHeaderPoCreDate", param);
	}


	public Map<String, Object> findCurrentPo(Map<String, Object> param) {
		return  sqlSession.selectOne(NAMESPACE + "findCurrentPo", param);
	}

	public List<Map<String,Object>> findListPoHistory(Map<String, Object> param) {
		return  sqlSession.selectList(NAMESPACE + "findListPoHistory", param);
	}

	public Map<String, Object> findOrderCntrInfo(Map<String, Object> param) {
		return  sqlSession.selectOne(NAMESPACE + "findOrderCntrInfo", param);
	}
	
	/**
	 * 기성요청대상 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream searchProgressPaymentRequestTarget(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "searchProgressPaymentRequestTarget", param);
	}
	
	/**
	 * 발주 기준 선급금 등록 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findAdvancePaymentInfomationByPoUuId(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findAdvancePaymentInfomationByPoUuId", param);
	}
	
	public List<Map<String, Object>> findListYearlyPoItemByVendor(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListYearlyPoItemByVendor", param);
	}
	
	public List<Map<String, Object>> findListYearlyPoTotAmtByVendor(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListYearlyPoTotAmtByVendor", param);
	}
	
	public List<Map<String, Object>> findListMonthlyPoTotAmtByVendor(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListMonthlyPoTotAmtByVendor", param);
	}
	
	public List<Map<String, Object>> findListVendorPoTotAmtByItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorPoTotAmtByItem", param);
	}
	
	public List<Map<String, Object>> findListVendorPoItemPriceByItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorPoItemPriceByItem", param);
	}
	
	/**
	 * 문서 출력물을 위한 poHeader정보 조회
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findInfoDocumentOutputPoHeader(Map<String, Object> param){
		return sqlSession.selectOne(NAMESPACE+"findInfoDocumentOutputPoHeader", param);
	}
	
	/**
	 * 문서 출력물을 위한 poDetail정보 조회 
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListDocumentOutputPoDetail(Map<String, Object> param){
		return sqlSession.selectList(NAMESPACE+"findListDocumentOutputPoDetail", param);
	}
	
    public String findRfxIdByPoId(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE+"findRfxIdByPoId",param);
    }

	public List<Map<String, Object>> findListOrderCntrMaker(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE+"findListOrderCntrMaker", param);
	}
	
	public List<String> findListVendorRcmdWthnoneyrItemPo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorRcmdWthnoneyrItemPo", param);
	}
	
	public List<String> findListVendorRcmdWthnoneyrSgPo(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListVendorRcmdWthnoneyrSgPo", param);
	}
	
	public Map<String, Object> findProgressPaymentDefaultDataByPo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findProgressPaymentDefaultDataByPo", param);
	}

	/**
	 * 발주 거부 카운트 조회
	 * @param param
	 * @return
	 */
	public Map findReturnedPoCount(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findReturnedPoCount", param);
	}

	/**
	 * 발주 유형 카운트
	 * @param param
	 * @return
	 */
	public Map findPoTypeCount(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPoTypeCount", param);
	}

	public List findListWorkplaceDashboardPoData(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListWorkplaceDashboardPoData", param);
	}
}
