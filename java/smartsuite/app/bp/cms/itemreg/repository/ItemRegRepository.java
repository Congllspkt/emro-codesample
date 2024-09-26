package smartsuite.app.bp.cms.itemreg.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;


@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemRegRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "item-reg.";


	public FloaterStream findListItemReg(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListItemReg", param);
	}

	public Map<String,Object> findInfoItemReg(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoItemReg", param);
	}

	public List<Map<String, Object>> findListItemChangeHistory(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListItemChangeHistory", param);
	}

	public int checkExistedApvdAndRet(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "checkExistedApvdAndRet", param);
	}
}
