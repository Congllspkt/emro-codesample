package smartsuite.app.sp.vendorMaster.vendorImprove.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpVendorImproveRepository {
	private static final String NAMESPACE = "sp-vendor-improve.";

	@Inject
	private SqlSession sqlSession;

	public List findListImprove(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListImprove", param);
	}

	public Map findImproveDetail(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findImproveDetail", param);
	}

	public void updateImproveInfo(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateImproveInfo", param);
	}
}
