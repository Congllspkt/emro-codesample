package smartsuite.config.workplace.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.config.workplace.vo.WorkTaskMethodSetting;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class WorkplaceSetupDaoRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE="workplace-setup.";

	/**
	 * list all tenant 조회한다.
	 *
	 * @return the list< map< string, object>>
	 * @Date : 2019. 4. 22
	 * @Method Name : findListAllTenant
	 */
	public List<Map<String, Object>> findListAllTenant(){
		return sqlSession.selectList(NAMESPACE+"findListAllTenant");
	}

	/**
	 * Select list all settings (work method - 선행/후행 여부 조회)
	 *
	 * @param sys_id the sys id
	 * @return the list
	 */
	public List<WorkTaskMethodSetting> findListAllSetting(String tenId) {
		return sqlSession.selectList(NAMESPACE+"findListAllSetting",tenId);
	}
}
