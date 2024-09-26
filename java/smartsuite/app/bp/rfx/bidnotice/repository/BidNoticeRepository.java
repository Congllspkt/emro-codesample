package smartsuite.app.bp.rfx.bidnotice.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class BidNoticeRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "bid-notice.";
	
	public FloaterStream findListBidNoti(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListBidNoti", param);
	}

	public Map findBidNoti(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findBidNoti", param);
	}

	public List<Map> findListBidNotiVendor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListBidNotiVendor", param);
	}

	public Map compareBidNotiHdSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "compareBidNotiHdSts", param);
	}

	public void deleteBidNotiVendor(Map bidNotiVendor) {
		sqlSession.delete(NAMESPACE + "deleteBidNotiVendor", bidNotiVendor);
	}

	public void insertBidNotiVendor(Map bidNotiVendor) {
		sqlSession.insert(NAMESPACE + "insertBidNotiVendor", bidNotiVendor);
	}

	public void updateBidNotiVendor(Map bidNotiVendor) {
		sqlSession.update(NAMESPACE + "updateBidNotiVendor", bidNotiVendor);
	}

	public void insertBidNoti(Map bidNotiData) {
		sqlSession.insert(NAMESPACE + "insertBidNoti", bidNotiData);
	}

	public void updateBidNoti(Map bidNotiData) {
		sqlSession.update(NAMESPACE + "updateBidNoti", bidNotiData);
	}

	public void deleteBidNotiVendorsByBidNoti(Map bidNoti) {
		sqlSession.delete(NAMESPACE + "deleteBidNotiVendorsByBidNoti", bidNoti);
	}

	public void deleteBidNoti(Map bidNoti) {
		sqlSession.delete(NAMESPACE + "deleteBidNoti", bidNoti);
	}

	public List<Map> findListBidNotiVdByCptTypCdNC(Map bidNotiInfo) {
		return sqlSession.selectList(NAMESPACE + "findListBidNotiVdByCptTypCdNC", bidNotiInfo);
	}

	public Map findBidNotiChangeInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findBidNotiChangeInfo", param);
	}

	public Map findLastBidNotiChangeInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findLastBidNotiChangeInfo", param);
	}

	public void insertBidNotiTimeChange(Map bidNotiData) {
		sqlSession.insert(NAMESPACE + "insertBidNotiTimeChange", bidNotiData);
	}

	public void updateBidNotiTimeChange(Map bidNotiData) {
		sqlSession.update(NAMESPACE + "updateBidNotiTimeChange", bidNotiData);
	}

	public void changeBidNotiTime(Map resultData) {
		sqlSession.update(NAMESPACE + "changeBidNotiTime", resultData);
	}

	public void deleteDraftBNTimeChange(Map param) {
		sqlSession.delete(NAMESPACE + "deleteDraftBNTimeChange", param);
	}
	
	/**
	 * list bid noti time change 조회한다.
	 *
	 * @param param the param
	 * @return the floater stream
	 * @Date : 2021. 5. 18
	 * @Method Name : findListBidNotiTimeChange
	 */
	public FloaterStream findListBidNotiTimeChange(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListBidNotiTimeChange", param);
	}
	
	public FloaterStream findListBidNotiComplete(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListBidNotiComplete", param);
	}

	public List<Map> findListBidNotiCompleteVdList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListBidNotiCompleteVdList",param);
	}

	public void updateBidNotiCancelCause(Map bidNotiData) {
		sqlSession.update(NAMESPACE + "updateBidNotiCancelCause", bidNotiData);
	}

	public List<Map> findListPreBidNoti(Map param){
		return sqlSession.selectList(NAMESPACE + "findListPreBidNoti", param);
	}

	/**
	 * 적격/비적격 업체 검색
	 * @param param
	 * @return
	 */
	public List<Map> findListBidNotiQualificationEval(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListBidNotiQualificationEval", param);
	}

	public void updateCloseBidNotice(Map param) {
		sqlSession.update(NAMESPACE + "updateCloseBidNotice", param);
	}

	public void updateCloseBidNoticeCause(Map param) {
		sqlSession.update(NAMESPACE + "updateCloseBidNoticeCause", param);
	}
}
