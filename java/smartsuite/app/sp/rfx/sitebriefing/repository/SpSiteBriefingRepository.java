package smartsuite.app.sp.rfx.sitebriefing.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class SpSiteBriefingRepository {
	
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-site-briefing.";
	
	public FloaterStream findListSpFieldIntro(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListSpFieldIntro", param);
	}
	
	public Map findInfoSpFieldIntro(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoSpFieldIntro", param);
	}
	
	public List<Map> findListSpFiItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSpFiItem", param);
	}
	
	public void submitSpFieldIntro(Map param) {
		sqlSession.update(NAMESPACE + "submitSpFieldIntro", param);
	}
	
	public void submitSpFieldAttend(Map fiData) {
		sqlSession.update(NAMESPACE + "submitSpFieldAttend", fiData);
	}
}
