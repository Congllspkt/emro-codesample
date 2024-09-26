package smartsuite.app.bp.srm.sourcinggroup.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SourcingGroupRepository {
	private static final String NAMESPACE = "sourcing-group.";
	
	@Inject
	private SqlSession sqlSession;

	public FloaterStream findListSourcingGroup(Map param){
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListSourcingGroup", param);
	}

	public void updateStatusSourcingGroupForDelete(Map param){
		sqlSession.update(NAMESPACE + "updateStatusSourcingGroupForDelete", param);
	}

	public void insertSourcingGroupInfo(Map param){
		sqlSession.insert(NAMESPACE + "insertSourcingGroupInfo", param);
	}

	public Map findSourcingGroupInfo(Map param){
		return sqlSession.selectOne(NAMESPACE + "findSourcingGroupInfo", param);
	}
	public void updateSourcingGroup(Map param){
		sqlSession.update(NAMESPACE + "updateSourcingGroup", param);
	}

	public FloaterStream findListSourcingGroupVendor(Map param){
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListSourcingGroupVendor", param);
	}

	public void insertSourcingGroupVendor(Map param){
		sqlSession.insert(NAMESPACE + "insertSourcingGroupVendor", param);
	}

	public List findListSourcingGroupUser(Map param){
		return sqlSession.selectList(NAMESPACE + "findListSourcingGroupUser", param);
	}
	public void deleteListSourcingGroupUser(Map param){
		sqlSession.delete(NAMESPACE + "deleteListSourcingGroupUser", param);
	}
	public void insertSourcingGroupUser(Map param){
		sqlSession.insert(NAMESPACE + "insertSourcingGroupUser", param);
	}
	public void updateSourcingGroupUser(Map param){
		sqlSession.update(NAMESPACE + "updateSourcingGroupUser", param);
	}
	public void mergeSourcingGroupPic(Map param){
		sqlSession.update(NAMESPACE + "mergeSourcingGroupPic", param);
	}

	public FloaterStream findListSourcingGroupItem(Map param){
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListSourcingGroupItem", param);
	}
	public void insertSourcingGroupItem(Map param){
		sqlSession.insert(NAMESPACE + "insertSourcingGroupItem", param);
	}
	public void deleteSourcingGroupItem(Map param){
		sqlSession.delete(NAMESPACE + "deleteSourcingGroupItem", param);
	}

	public FloaterStream findListCate(Map param){
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListCate", param);
	}

	public List<Map<String, Object>> findListCateItem(Map param){
		return sqlSession.selectList(NAMESPACE + "findListCateItem", param);
	}

	public List<Map<String, Object>> findListVendorInfoEO(Map param){
		return sqlSession.selectList(NAMESPACE + "findListVendorInfoEO", param);
	}
}
