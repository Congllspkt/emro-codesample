package smartsuite.app.sp.rfx.rfx.repository;

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
public class SpRfxBidRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-rfx-bid.";
	
	/**
	 * list rfx vd 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 17
	 * @Method Name : findListRfxVd
	 */
	public FloaterStream findListRfxVd(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListRfxVd", param);
	}

	/**
	 * rfx qta 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2016. 5. 17
	 */
	public Map findRfxBid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxBid", param);
	}
	
	/**
	 * 이전차수의 rfx qta 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 17
	 */
	public Map findRfxBidPrevRev(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxBidPrevRev", param);
	}
	
	/**
	 * RFx 업체 견적서 헤더 신규생성
	 * @param rfxBid
	 */
	public void insertRfxBid(Map rfxBid) {
		sqlSession.insert(NAMESPACE + "insertRfxBid", rfxBid);
	}
	
	/**
	 * RFx 업체 견적서 헤더 수정
	 * @param rfxBid
	 */
	public void updateRfxBid(Map rfxBid) {
		sqlSession.update(NAMESPACE + "updateRfxBid", rfxBid);
	}

	public Map checkRfxCsVds(Map checkParam) {
		return sqlSession.selectOne(NAMESPACE + "checkRfxCsVds", checkParam);
	}

	public void updateRfxBidValidYn(Map prevRfxBid) {
		sqlSession.update(NAMESPACE + "updateRfxBidValidYn", prevRfxBid);
	}

	public void updateRfxBidByAbandon(Map updateBid) {
		sqlSession.update(NAMESPACE + "updateRfxBidByAbandon", updateBid);
	}

	/**
	 * 선정취소 사유 조회
	 *
	 * @author : Chaerin yu
	 * @param
	 * @return
	 * @Date : 2017. 07. 28
	 * @Method Name : findInfoCancelCause
	 */
	public Map findInfoCancelCause(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoCancelCause", param);
	}
	
	/**
	 * 공동수급협정서 현황 조회
	 * 
	 * @param param
	 * @return
	 */
	public FloaterStream findListRfxCs(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListRfxCs", param);
	}

	public Map findRfxCs(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxCs", param);
	}

	public List findListRfxCsVd(Map rfxCsData) {
		return sqlSession.selectList(NAMESPACE + "findListRfxCsVd", rfxCsData);
	}

	public List findListRfxCsVdTarget(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxCsVdTarget", param);
	}

	public Map checkRfxBids(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkRfxBids", param);
	}

	public void deleteRfxCs(Map csVd) {
		sqlSession.delete(NAMESPACE + "deleteRfxCs", csVd);
	}

	public void insertRfxCs(Map csVd) {
		sqlSession.insert(NAMESPACE + "insertRfxCs", csVd);
	}

	public void updateRfxCs(Map csVd) {
		sqlSession.update(NAMESPACE + "updateRfxCs", csVd);
	}

	public void updateRfxCsSbmtY(Map csData) {
		sqlSession.update(NAMESPACE + "updateRfxCsSbmtY", csData);
	}

	public void updateRfxCsSendY(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxCsSendY", param);
	}

	public void deleteRfxCsByCancel(Map param) {
		sqlSession.delete(NAMESPACE + "deleteRfxCsByCancel", param);
	}
	
	public Map findRfxByRfxVdUuid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxByRfxVdUuid", param);
	}
}
