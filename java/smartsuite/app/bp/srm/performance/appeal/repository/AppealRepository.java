package smartsuite.app.bp.srm.performance.appeal.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AppealRepository {

	final static String NAMESPACE = "appeal.";

	@Inject
	SqlSession sqlSession;

	public Map findAppealNoticeInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAppealNoticeInfo", param);
	}

	public List findAppealList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findAppealList", param);
	}

	public void noticeAppeal(Map param) {
		sqlSession.insert(NAMESPACE + "noticeAppeal", param);
	}

	public Map findAppealQualiDetail(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAppealQualiDetail", param);
	}

	public void updateAppealQualiDetail(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateAppealQualiDetail", param);
	}

	public void insertAppeal(Map param) {
		sqlSession.insert(NAMESPACE + "insertAppeal", param);
	}

	public void updateAppeal(Map param) {
		sqlSession.insert(NAMESPACE + "updateAppeal", param);
	}

	public void closeAppeal(Map param) {
		sqlSession.update(NAMESPACE + "closeAppeal", param);
	}

	public Map findAppealCalcDetail(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAppealCalcDetail", param);
	}

	public void updateAppealCalcDetail(Map param) {
		sqlSession.update(NAMESPACE + "updateAppealCalcDetail", param);
	}

	public List<Map<String, Object>> findListAppealVendorForMail(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAppealVendorForMail", param);
	}
}
