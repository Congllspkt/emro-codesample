package smartsuite.app.bp.rfx.config.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfxConfigRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-config.";
	public List findListRfxCopy(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxCopy", param);
	}
}
