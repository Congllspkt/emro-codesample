package smartsuite.app.bp.edoc.network.repository;

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
public class NetWorkConnectRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "connect.";
	
	public void insertIpInfo(Map param) {
		sqlSession.insert(NAMESPACE + "insertIpInfo", param);
	}
	
	public void updateIpInfo(Map param) {
		sqlSession.update(NAMESPACE + "updateIpInfo", param);
	}
	
	public void deleteIpInfo(Map param) {
		sqlSession.delete(NAMESPACE + "deleteIpInfo", param);
	}
	
	public List selectIpList(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectIpList", param);
	}
	
	public void updateConnectInfo(Map param) {
		sqlSession.update(NAMESPACE + "updateConnectInfo", param);
	}
	
}
