package smartsuite.app.sp.rfx.bidnotice.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class SpBidNoticeRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-bid-notice.";
	
	public List<Map> findListSpBidNoti(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSpBidNoti", param);
	}
	
	public Map findInfoSpBidPart(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoSpBidPart", param);
	}
	
	public boolean isExistsSpNotiPartAttach(Map param) {
		return sqlSession.selectOne(NAMESPACE + "isExistsSpNotiPartAttach", param);
	}
	
	public Map checkSpNotiPartSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkSpNotiPartSts", param);
	}
	
	public Map checkSpNotiPartTime(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkSpNotiPartTime", param);
	}
	
	public Map findInfoSpBidPartVendor(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoSpBidPartVendor", param);
	}
	
	public Map findInfoSpBidPartVendorChr(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoSpBidPartVendorChr", param);
	}
	
	public List<Map> findListSpBidPartAttach(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSpBidPartAttach", param);
	}
	
	public List<Map> findListSpBidPartedAttach(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListSpBidPartedAttach", param);
	}
	
	public void deleteSpBidNotiAtt(Map param) {
		sqlSession.delete(NAMESPACE + "deleteSpBidNotiAtt", param);
	}
	
	public void deleteSpBidNoti(Map param) {
		sqlSession.delete(NAMESPACE + "deleteSpBidNoti", param);
	}
	
	public boolean isExistSpBindNotiPart(Map param) {
		return sqlSession.selectOne(NAMESPACE + "isExistSpBindNotiPart", param);
	}
	
	public void updateSpBidNoti(Map param) {
		sqlSession.update(NAMESPACE + "updateSpBidNoti", param);
	}
	
	public void insertSpBidNoti(Map param) {
		sqlSession.insert(NAMESPACE + "insertSpBidNoti", param);
	}
	
	public void updateSpBidNotiAttach(Map param) {
		sqlSession.update(NAMESPACE + "updateSpBidNotiAttach", param);
	}
	
	public void insertSpBidNotiAttach(Map param) {
		sqlSession.insert(NAMESPACE + "insertSpBidNotiAttach", param);
	}
}
