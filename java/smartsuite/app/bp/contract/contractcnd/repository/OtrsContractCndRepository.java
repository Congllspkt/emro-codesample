package smartsuite.app.bp.contract.contractcnd.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class OtrsContractCndRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "otrs-contract-cnd.";


	public void insertOtrsCntr(Map param) {
		sqlSession.insert(NAMESPACE + "insertOtrsCntr", param);
	}

	public void updateOtrsCntr(Map param) {
		sqlSession.update(NAMESPACE + "updateOtrsCntr", param);
	}

	public void insertOtrsCntrInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertOtrsCntrInfo", param);
	}

	public void updateOtrsCntrInfo(Map param) {
		sqlSession.update(NAMESPACE + "updateOtrsCntrInfo", param);
	}

	public void insertOtrsCntrPymtExpt(Map param) {
		sqlSession.insert(NAMESPACE + "insertOtrsCntrPymtExpt", param);
	}

	public void deleteOtrsCntrPymtExptByOtrsCntr(Map param) {
		sqlSession.delete(NAMESPACE + "deleteOtrsCntrPymtExptByOtrsCntr", param);
	}

	public void deleteOtrsCntrInfoByOtrsCntr(Map param) {
		sqlSession.delete(NAMESPACE + "deleteOtrsCntrInfoByOtrsCntr", param);
	}

	public void deleteOtrsCntr(Map param) {
		sqlSession.delete(NAMESPACE + "deleteOtrsCntr", param);
	}

	public Map findOtrsCntr(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findOtrsCntr", param);
	}

	public Map findOtrsCntrInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findOtrsCntrInfo", param);
	}

	public List findListOtrsCntrPymtExpt(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListOtrsCntrPymtExpt", param);
	}

}
