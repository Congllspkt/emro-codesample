package smartsuite.app.bp.contract.common.repository;

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
public class ContractRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "contract.";

	public Map findContract(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findContract", param);
	}

	public Map findChangeContract(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findChangeContract", param);
	}

	public void insertContract(Map param) {
		sqlSession.insert(NAMESPACE + "insertContract", param);
	}

	public void updateContract(Map param) {
		sqlSession.update(NAMESPACE + "updateContract", param);
	}
	
	public void deleteContract(Map param) {
		sqlSession.delete(NAMESPACE + "deleteContract", param);
	}

	public void insertContractor(Map param) {
		sqlSession.insert(NAMESPACE + "insertContractor", param);
	}

	public void updateContractor(Map param) {
		sqlSession.update(NAMESPACE + "updateContractor", param);
	}

	public void updateContractorByVdCd(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateContractorByVdCd", param);
	}

	public void deleteContractor(Map param) {
		sqlSession.delete(NAMESPACE + "deleteContractor", param);
	}

	public FloaterStream largeFindListContract(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListContract", param);
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
	
	public List findListContractor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListContractor", param);
	}

	public List findListCntrVendor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCntrVendor", param);
	}

	public String getMaxCntrRevNo(String cntrNo) {
		return sqlSession.selectOne(NAMESPACE + "getMaxCntrRevNo", cntrNo);
	}

	public void terminateContract(Map param) {
		sqlSession.update(NAMESPACE + "terminateContract", param);
	}

	public void deleteContractHistory(Map param) {
		sqlSession.delete(NAMESPACE + "deleteContractHistory", param);
	}

	public String getContractDocWithdrawalPossYn(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getContractDocWithdrawalPossYn", param);
	}
	public void updateTaskApvlUseYn(Map param) {
		sqlSession.update(NAMESPACE + "updateTaskApvlUseYn", param);
	}

	public String validateCompanyInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "validateCompanyInfo", param);
	}
}
