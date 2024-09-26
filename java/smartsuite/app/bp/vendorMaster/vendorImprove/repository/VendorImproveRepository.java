package smartsuite.app.bp.vendorMaster.vendorImprove.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VendorImproveRepository {

	private static final String NAMESPACE = "vendor-improve.";

	@Inject
	private SqlSession sqlSession;

	public List findListImprove(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListImprove", param);
	}

	public void insertImproveReqInfo(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertImproveReqInfo", param);
	}

	public void updateImproveReqInfo(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateImproveReqInfo", param);
	}

	public Map findImproveDetail(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findImproveDetail", param);
	}

	public void deleteImproveInfo(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteImproveInfo", param);
	}

	// 퍼포먼스 평가 대상 결과의 개선관리 등록 데이터를 조회한다.
	public List findListPeSubjVdImprove(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPeSubjVdImprove", param);
	}

	// 퍼포먼스 평가 대상 결과의 개선관리 등록 데이터를 생성한다.
	public void insertPeSubjVdImprove(Map param) {
		sqlSession.insert(NAMESPACE + "insertPeSubjVdImprove", param);
	}
}
