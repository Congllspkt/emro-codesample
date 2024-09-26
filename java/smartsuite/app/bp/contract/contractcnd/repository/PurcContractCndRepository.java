package smartsuite.app.bp.contract.contractcnd.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class PurcContractCndRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "purc-contract-cnd.";
	
	public void insertPurcCntrInfo(Map purcCntrInfoData) {
		sqlSession.insert(NAMESPACE + "insertPurcCntrInfo", purcCntrInfoData);
	}
	
	public void updatePurcCntrInfo(Map purcCntrInfoData) {
		sqlSession.insert(NAMESPACE + "updatePurcCntrInfo", purcCntrInfoData);
	}
	
	public void insertPurcCntr(Map purcCntrData) {
		sqlSession.insert(NAMESPACE + "insertPurcCntr", purcCntrData);
	}
	
	public void updatePurcCntr(Map purcCntrData) {
		sqlSession.update(NAMESPACE + "updatePurcCntr", purcCntrData);
	}
	
	public void insertPurcCntrItem(Map insertItem) {
		sqlSession.insert(NAMESPACE + "insertPurcCntrItem", insertItem);
	}
	
	public void updatePurcCntrItem(Map updateItem) {
		sqlSession.update(NAMESPACE + "updatePurcCntrItem", updateItem);
	}
	
	public void deletePurcCntrItem(Map deleteItem) {
		sqlSession.delete(NAMESPACE + "deletePurcCntrItem", deleteItem);
	}
	
	public void insertPurcCntrPymtExpt(Map insertPaymentPlan) {
		sqlSession.insert(NAMESPACE + "insertPurcCntrPymtExpt", insertPaymentPlan);
	}
	
	public BigDecimal calcSupplyAmountByItem(Map purcCntrData) {
		return sqlSession.selectOne(NAMESPACE + "calcSupplyAmountByItem", purcCntrData);
	}
	
	public void updatePurcCntrAmt(Map purcCntrData) {
		sqlSession.update(NAMESPACE + "updatePurcCntrAmt", purcCntrData);
	}
	
	public void updateRfxNxtPrcs(Map updateItem) {
		sqlSession.update(NAMESPACE + "updateRfxNxtPrcs", updateItem);
	}
	
	public Map findPurcCntr(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPurcCntr", param);
	}
	
	public Map findPurcCntrInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findPurcCntrInfo", param);
	}
	
	public List<Map> findListPurcCntrItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPurcCntrItem", param);
	}
	
	public List<Map> findListPurcCntrPymtExpt(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPurcCntrPymtExpt", param);
	}
	
	public void updateRfxSlctnVdByDeleteRfxNxtPrcsReq(Map purcCntrData) {
		sqlSession.update(NAMESPACE + "updateRfxSlctnVdByDeleteRfxNxtPrcsReq", purcCntrData);
	}
	
	public void deletePurcCntrPymtExptByPurcCntr(Map purcCntrData) {
		sqlSession.delete(NAMESPACE + "deletePurcCntrPymtExptByPurcCntr", purcCntrData);
	}
	
	public void deletePurcCntrItemByPurcCntr(Map purcCntrData) {
		sqlSession.delete(NAMESPACE + "deletePurcCntrItemByPurcCntr", purcCntrData);
	}
	
	public void deletePurcCntrInfoByPurcCntr(Map purcCntrData) {
		sqlSession.delete(NAMESPACE + "deletePurcCntrInfoByPurcCntr", purcCntrData);
	}
	
	public void deletePurcCntr(Map purcCntrData) {
		sqlSession.delete(NAMESPACE + "deletePurcCntr", purcCntrData);
	}
	
	public void insertCs(Map insertCs) {
		sqlSession.insert(NAMESPACE + "insertCs", insertCs);
	}
	
	public List<Map> findListCstm(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCstm", param);
	}
}
