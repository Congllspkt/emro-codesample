package smartsuite.app.common.workplace.shared.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WorkplaceMailRepository {

	/** The sql session. */
	@Inject
	private SqlSession sqlSession;

	/** The namespace. */
	@Inject
	private static final String NAMESPACE = "workplace-mail.";


	public List findListWorkplaceAlarmReceiverUsr(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListWorkplaceAlarmReceiverUsr", param);
	}

}
