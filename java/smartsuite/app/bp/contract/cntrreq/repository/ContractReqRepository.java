package smartsuite.app.bp.contract.cntrreq.repository;

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
public class ContractReqRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "contract-req.";
	
	public FloaterStream largeFindListContractReq(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListContractReq", param);
	}

	public Map findContractReq(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findContractReq", param);
	}

	public Map findContractReqWk(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findContractReqWk", param);
	}
	
	public void insertContractReq(Map param) {
		sqlSession.insert(NAMESPACE + "insertContractReq", param);
	}
	
	public void updateContractReqSts(Map param) {
		sqlSession.update(NAMESPACE + "updateContractReqSts", param);
	}
	
}
