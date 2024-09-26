package smartsuite.app.bp.rfx.sitebriefing.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RemoteMeetingRepository {
	
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "remote-meeting.";
	
	public Map selectRemoteMeetingInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "selectRemoteMeetingInfo", param);
	}
	
	public List<Map> selectRemoteMeetingInfoAndNoteList(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectRemoteMeetingInfoAndNoteList", param);
	}
	
	public List<Map> remoteMeetJoinUserList(Map remoteMeetInfo) {
		return sqlSession.selectList(NAMESPACE + "remoteMeetJoinUserList", remoteMeetInfo);
	}
	
	public List<Map> selectRemoteMeetingNoteInfoTaskList(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectRemoteMeetingNoteInfoTaskList", param);
	}
	
	public void updateRemoteMeetingSharedYn(Map fiData) {
		sqlSession.update(NAMESPACE + "updateRemoteMeetingSharedYn", fiData);
	}
	
	public void insertReservationRemoteMeeting(Map remoteMeetInfo) {
		sqlSession.insert(NAMESPACE + "insertReservationRemoteMeeting", remoteMeetInfo);
	}
	
	public void updateFiProgStsReservationMeeting(Map remoteMeetInfo) {
		sqlSession.update(NAMESPACE + "updateFiProgStsReservationMeeting", remoteMeetInfo);
	}
	
	public Map<String, Object> selectRemoteMeetingInfoExistCheck(Map<String, Object> fiData) {
		return sqlSession.selectOne(NAMESPACE + "selectRemoteMeetingInfoExistCheck", fiData);
	}
	
	public void deleteRemoteMeetingForFiIDAndNotReservation(Map<String, Object> fiData) {
		sqlSession.delete(NAMESPACE + "deleteRemoteMeetingForFiIDAndNotReservation", fiData);
	}
	
	public void deleteFiProgStsReservationMeeting(Map<String, Object> fiData) {
		sqlSession.delete(NAMESPACE + "deleteFiProgStsReservationMeeting", fiData);
	}
	
	public void deleteFiRemoteMeetingInfo(Map<String, Object> fiData) {
		sqlSession.delete(NAMESPACE + "deleteFiRemoteMeetingInfo", fiData);
	}
	
	public Map<String, Object> selectReservationAndRemoteMeetInfoForFirmId(Map<String, Object> requestInfoMap) {
		return sqlSession.selectOne(NAMESPACE + "selectReservationAndRemoteMeetInfoForFirmId", requestInfoMap);
	}
	
	public Map<String, Object> selectReservationRemoteMeetingForFirmId(Map<String, Object> resultMap) {
		return sqlSession.selectOne(NAMESPACE + "selectReservationRemoteMeetingForFirmId", resultMap);
	}
}
