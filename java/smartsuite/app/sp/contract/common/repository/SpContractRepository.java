package smartsuite.app.sp.contract.common.repository;

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
@Transactional
public class SpContractRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-contract.";
	
	
	public FloaterStream largeFindListSpContract(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListSpContract", param);
	}
	
	public Map findContract(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findContract", param);
	}

	public void updateContractStatus(Map param) {
		sqlSession.update(NAMESPACE + "updateContractStatus", param);
	} 
	
	public void insertCntrHistory(Map param) {
		sqlSession.insert(NAMESPACE + "insertCntrHistory", param);
	}

	public List findListCntrHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCntrHistory", param);
	}
	
	public void updateApprovalUseYn(Map param) {
		sqlSession.update(NAMESPACE + "updateApprovalUseYn", param);
	}
}
