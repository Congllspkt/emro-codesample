package smartsuite.app.bp.contract.demo.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class ContractDemoRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "contract-demo.";


	public FloaterStream largeFindListContractDemo(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListContractDemo", param);
	}

	public void saveInfoCntrIfSts(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "saveInfoCntrIfSts", param);
	}

	public Map findInfoIfCntrDemo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoIfCntrDemo", param);
	}

	public Map findInfoIfCntrHeaderDemo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoIfCntrHeaderDemo", param);
	}

}
