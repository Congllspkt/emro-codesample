package smartsuite.app.bp.edoc.template.repository;

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
public class ClauseRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "clause.";
	
	public List getAttrAll(Map param) {
		return sqlSession.selectList(NAMESPACE + "getAttrAll");
	}
	
	public FloaterStream largeSearchListCntrItem(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "searchListCntrItem", param);
	}
	
	public List searchListCntrItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "searchListCntrItem", param);
	}
		
	public int checkCntrItemName(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkCntrItemName",param);
	}
	
	public int checkCntrTmpItemVariable(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkCntrTmpItemVariable",param);
	}
	
	public void insertCntrItem(Map param) {
		sqlSession.insert(NAMESPACE + "insertCntrItem",param);
	}
	
	public void deleteCntrItem(Map param) {
		sqlSession.delete(NAMESPACE + "deleteCntrItem",param);
	}
	
	public Map findDynamicTmpById(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDynamicTmpById",param);
	}
	
	public void saveDynamicTmp(Map param) {
		sqlSession.update(NAMESPACE + "saveDynamicTmp",param);
	}
	
	public void updateCntrItem(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrItem",param);
	}
	
	public List findDynamicTmpByVariable(Map param) {
		return sqlSession.selectList(NAMESPACE + "findDynamicTmpByVariable",param);
	}
	
	public Map findListCntrItem(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findListCntrItem", param);
	}

	public FloaterStream largeFindListCntrClause(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListCntrClause", param);
	}

	public Map getCntrClauseByCntrClId(String param) {
		return sqlSession.selectOne(NAMESPACE + "getCntrClauseByCntrClId", param);
	}
	
	public List findCntrClauseUseByCntrClUuid(String param) {
		return sqlSession.selectList(NAMESPACE + "findCntrClauseUseByCntrClUuid", param);
	}
	
	public void insertCntrClauseUse(Map param) {
		sqlSession.update(NAMESPACE + "insertCntrClauseUse", param);
	}

	public void deleteCntrClauseUse(Map param) {
		sqlSession.update(NAMESPACE + "deleteCntrClauseUse", param);
	}
	
	public int getCntrClauseUseCnt(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getCntrClauseUseCnt", param);
	}

	public Map findCntrClause(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCntrClause", param);
	}

	public List findClauseCntrUseByCntrClUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "findClauseCntrUseByCntrClUuid", param);
	}
}
