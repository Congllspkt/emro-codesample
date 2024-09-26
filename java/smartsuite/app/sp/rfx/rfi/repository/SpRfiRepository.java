package smartsuite.app.sp.rfx.rfi.repository;

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
public class SpRfiRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-rfi.";
	
	/**
	 * RFI 현황 조회
	 * @param param
	 * @return
	 */
	public FloaterStream findListRfi(Map param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListRfi", param);
	}

	public Map findRfi(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfi", param);
	}

	public List findListRfiItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfiItem", param);
	}

	public Map compareRfiVdSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "compareRfiVdSts", param);
	}

	public void updateRfiVendor(Map rfiQtaData) {
		sqlSession.update(NAMESPACE + "updateRfiVendor", rfiQtaData);
	}
}
