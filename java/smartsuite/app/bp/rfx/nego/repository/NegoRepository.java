package smartsuite.app.bp.rfx.nego.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class NegoRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "nego.";
	
	public void deleteNegoTargetHeaderSts(Map item) {
		sqlSession.update(NAMESPACE + "cancelNegoTargetHeader", item);
	}

	public List findNegoTargetList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findNegoTargetList", param);
	}

	public Map findNegoRfxData(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findNegoRfxData", param);
	}

	public List findNegoBidData(Map param) {
		return sqlSession.selectList(NAMESPACE + "findNegoBidData", param);
	}

	public void insertNegoHeader(Map param) {
		sqlSession.insert(NAMESPACE + "insertNegoHeader", param);
	}

	public List findBidItems(Map rfxBid) {
		return sqlSession.selectList(NAMESPACE + "findBidItems", rfxBid);
	}

	public List getUserMailInfo(Map rfxBid) {
		return sqlSession.selectList(NAMESPACE + "getUserMailInfo", rfxBid);
	}

	public Map compareRfxProgSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "compareRfxProgSts", param);
	}
	
	public Map checkNegoProgSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkNegoProgSts", param);
	}
	
	public Map checkReNegoSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkReNegoSts", param);
	}
	
	public int existsNegoCnt(Map param) {
		return sqlSession.selectOne(NAMESPACE + "existsNegoCnt", param);
	}

	public void requestNego(Map param) {
		sqlSession.update(NAMESPACE + "requestNego", param);
	}

	public void updateNegoCont(Map negoData) {
		sqlSession.update(NAMESPACE + "updateNegoCont", negoData);
	}

	public void updateNegoStl(Map rfxBid) {
		sqlSession.update(NAMESPACE + "updateNegoStl", rfxBid);
	}

	public void updateCloseNegoCause(Map negoData) {
		sqlSession.update(NAMESPACE + "updateCloseNegoCause", negoData);
	}

	public void updateReNegoCause(Map param) {
		sqlSession.update(NAMESPACE + "updateReNegoCause", param);
	}

	public void copyNegoHeader(Map param) {
		sqlSession.insert(NAMESPACE + "copyNegoHeader", param);
	}
	
	/**
	 * 협상 선정품의 결재 작성건 존재여부
	 *
	 * @param param the param
	 * @return the map
	 */
	public Boolean checkNegoApproval(Map<String, Object> rfxData) {
		return ((Integer) sqlSession.selectOne(NAMESPACE + "checkNegoApproval", rfxData)) > 0;
	}
	
	/**
	 * 협상 선정품의 결재 작성건 uuid 조회
	 * @param rfxData
	 * @return
	 */
	public String findNegoApprovalUuid(Map rfxData) {
		return sqlSession.selectOne(NAMESPACE + "findNegoApprovalUuid", rfxData);
	}
	
	/**
	 * 선정여부 초기화 대상조회
	 * @param rfxData
	 */
	public List<Map<String, Object>> searchInitSelectionYnFromBidding(Map<String, Object> rfxData) {
		return sqlSession.selectList(NAMESPACE + "searchInitSelectionYnFromBidding", rfxData);
	}
	
	/**
	 * 협상 선정여부 초기화
	 * @param rfxData
	 */
	public void initNegoSelectionYN(Map rfxData) {
		sqlSession.update(NAMESPACE + "initNegoSelectionYN", rfxData);
	}
	
	/**
	 * 협상 상태 마감으로 수정
	 * @param rfxData
	 */
	public void updateNegoStatusCodeToClosing(Map rfxData) {
		sqlSession.update(NAMESPACE + "updateNegoStatusCodeToClosing", rfxData);
	}
	
	public Map findNegoTargetListByRfxUuid(Map rfx) {
		return sqlSession.selectOne(NAMESPACE + "findNegoTargetListByRfxUuid", rfx);
	}
}
