package smartsuite.app.bp.rfx.rfx.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfxNxtPrcsRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-nxt-prcs.";
	
	public void insertRfxNxtPrcsReq(Map rfxNxtPrcsReqData) {
		sqlSession.insert(NAMESPACE + "insertRfxNxtPrcsReq", rfxNxtPrcsReqData);
	}
	
	public void updateRfxNxtPrcsReq(Map rfxNxtPrcsReqData) {
		sqlSession.update(NAMESPACE + "updateRfxNxtPrcsReq", rfxNxtPrcsReqData);
	}
	
	public Map findRfxNxtPrcsReq(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxNxtPrcsReq", param);
	}
	
	public void cancelConfirmRfxNxtPrcsReq(Map param) {
		sqlSession.update(NAMESPACE + "cancelConfirmRfxNxtPrcsReq", param);
	}
	
	public void updateRfxSlctnVdByDeleteRfxNxtPrcsReq(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxSlctnVdByDeleteRfxNxtPrcsReq", param);
	}
	
	public void updateRfxReceiptByDeleteRfxNxtPrcsReq(Map param) {
		sqlSession.update(NAMESPACE + "updateRfxReceiptByDeleteRfxNxtPrcsReq", param);
	}
	
	public void deleteRfxNxtPrcsReq(Map param) {
		sqlSession.delete(NAMESPACE + "deleteRfxNxtPrcsReq", param);
	}
	
	public Map findRfxByRfxUuid(Map checkedRow) {
		return sqlSession.selectOne(NAMESPACE + "findRfxByRfxUuid", checkedRow);
	}
	
	public Map findRfxReceiptByRfxNxtPrcsReq(Map checkedRow) {
		return sqlSession.selectOne(NAMESPACE + "findRfxReceiptByRfxNxtPrcsReq", checkedRow);
	}
	
	public List<Map> findListRfxReceiptItemCntrByRfxNxtPrcsReq(Map checkedRow) {
		return sqlSession.selectList(NAMESPACE + "findListRfxReceiptItemCntrByRfxNxtPrcsReq", checkedRow);
	}
	
	public void updateRequestStatus(Map param) {
		sqlSession.update(NAMESPACE + "updateRequestStatus", param);
	}
	
	public List<Map> findRfxItemByRfxNxtPrcsReq(Map checkedRow) {
		return sqlSession.selectList(NAMESPACE + "findRfxItemByRfxNxtPrcsReq", checkedRow);
	}
	
	public List<Map> findListRfxReceiptByRfxNxtPrcsReq(Map checkedRow) {
		return sqlSession.selectList(NAMESPACE + "findListRfxReceiptByRfxNxtPrcsReq", checkedRow);
	}
}
