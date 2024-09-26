package smartsuite.app.bp.rfx.rfx.repository;

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
public class RfxRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx.";

	public FloaterStream findListRfx(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE+"findListRfx",param);
	}
	
	/**
	 * RFx 헤더 신규생성
	 * @param rfxData
	 */
	public void insertRfx(Map rfxData) {
		sqlSession.insert(NAMESPACE + "insertRfx", rfxData);
	}
	
	/**
	 * RFx 헤더 수정
	 * @param rfxData
	 */
	public void updateRfx(Map rfxData) {
		sqlSession.update(NAMESPACE + "updateRfx", rfxData);
	}
	
	/**
	 * 견적마스터 삭제
	 * @param rfxData - 삭제할 견적마스터
	 */
	public void deleteRfx(Map rfxData) {
		sqlSession.delete(NAMESPACE + "deleteRfx", rfxData);
	}
	
	/**
	 * RFx 최종 차수 조회
	 * 
	 * @param param {rfx_no}
	 * @return
	 */
	public Integer findRfxMaxRevision(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxMaxRevision", param);
	}
	
	/**
	 * rfx 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 11
	 * @Method Name : findRfx
	 */
	public Map findRfx(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfx",param);
	}
	
	/**
	 * rfx 요청업체의 견적서 리스트를 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 22
	 * @Method Name : findListRfxVdQta
	 */
	public List findListRfxVdBid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxVdBid",param);
	}
	
	/**
	 * 견적 시작일시를 수정한다.
	 * 
	 * @param param
	 */
	public void updateStartDt(Map param) {
		sqlSession.update(NAMESPACE + "updateStartDt", param);
	}

	/**
	 * 견적 마감일시를 수정한다.
	 * 
	 * @param saveParam
	 */
	public void updateCloseDt(Map param) {
		sqlSession.update(NAMESPACE + "updateCloseDt", param);
	}

	public Map findRfxByResult(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxByResult", param);
	}

	public int checkRfxResultApproval(Map rfxData) {
		return sqlSession.selectOne(NAMESPACE + "checkRfxResultApproval", rfxData);
	}

	public String findRfxResultApprovalId(Map rfxData) {
		return sqlSession.selectOne(NAMESPACE + "findRfxResultApprovalId", rfxData);
	}

	public List findListCsByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCsByRfx", param);
	}

	public Map findRfxCs(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxCs", param);
	}

	public List findListRfxCsVd(Map csData) {
		return sqlSession.selectList(NAMESPACE + "findListRfxCsVd", csData);
	}
	
	public List findListRfxSlctnVd(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxSlctnVd", param);
	}
	
	public void updateRfxSlctnNxtPrc(Map updateItem) {
		sqlSession.update(NAMESPACE + "updateRfxSlctnNxtPrc", updateItem);
	}
	
	public void insertRfxSlctnVd(Map slctnVd) {
		sqlSession.insert(NAMESPACE + "insertRfxSlctnVd", slctnVd);
	}
	
	public List<Map> findListRfxSlctnVdDetail(Map checked) {
		return sqlSession.selectList(NAMESPACE + "findListRfxSlctnVdDetail", checked);
	}
	
	public void deleteRfxSlctnVd(Map checked) {
		sqlSession.delete(NAMESPACE + "deleteRfxSlctnVd", checked);
	}
	
	public List findListRfxResult(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxResult", param);
	}
	
	public List<Map> findListOrderOperOrg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListOrderOperOrg", param);
	}
}
