package smartsuite.app.bp.rfx.sitebriefing.remotemeeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code.RemoteMeetingProgCode;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.ReservationRemoteMeetInfo;
import smartsuite.app.bp.rfx.sitebriefing.repository.RemoteMeetingRepository;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.sp.rfx.sitebriefing.repository.SpSiteBriefingRepository;
import smartsuite.exception.CommonException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Transactional
@Service
public class RemoteMeetingCallService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger("remoteMeetAopExecution");
	
	@Inject
	RemoteMeetingAPIService remoteMeetingAPIService;
	
	@Inject
	RemoteMeetingResultService remoteMeetingResultService;
	
	@Inject
	RemoteMeetingRepository remoteMeetingRepository;
	
	@Inject
	SpSiteBriefingRepository spSiteBriefingRepository;
	
	@Inject
	MailService mailService;
	
	public Map<String, Object> nowStartRemoteMeetingConference(Map<String, Object> fiData) {
		Map<String, Object> resultRemoteMeetInfo = new HashMap<String, Object>();
		
		// 개설된 화상회의가 있는지 체크
		Map<String, Object> remoteConferenceInfo = remoteMeetingRepository.selectRemoteMeetingInfo(fiData);
		
		// 개설된 회의가 있을 경우, 참석 url을 꺼내어 result로 던져주고, 없을 경우 바로 개설을 통하여 얻어온 URL을 던저준다.
		if(null != remoteConferenceInfo && remoteConferenceInfo.values().size() > 0) {
			String joinUrl = remoteConferenceInfo.get("mtg_url") == null ? "" : remoteConferenceInfo.get("mtg_url").toString();
			resultRemoteMeetInfo.put("url", joinUrl);
		} else {
			/**
			 * 넘어온 현장설명회 map을 remotecall reservation info map으로 변경
			 * ( 바로 개설 시에도, callback으로 생성되었다는 내역이 넘어오기전까진 예약으로 치부
			 */
			Map<String, Object> remoteMeetInfo = ReservationRemoteMeetInfo.remoteMeetingNowStartSetInfo(fiData);
			
			remoteMeetingRepository.insertReservationRemoteMeeting(remoteMeetInfo);
			remoteMeetingRepository.updateFiProgStsReservationMeeting(remoteMeetInfo);
			
			resultRemoteMeetInfo = remoteMeetingAPIService.callConferenceNowStart(remoteMeetInfo);
			
			mailService.sendAsync("VENDOR_MEETING_JOIN_REQUEST", (String) fiData.get("sitebrfg_uuid"));
		}
		return resultRemoteMeetInfo;
	}
	
	
	/**
	 * 참석 & 개설에 따른 validation check
	 *
	 * @param fiData
	 * @return
	 */
	public Map<String, Object> validateRemoteMeetingExist(Map<String, Object> fiData) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// true 참석 & false 개설
		boolean existCheck = fiData.get("rmExistCheck") == null ? false : (Boolean) fiData.get("rmExistCheck");
		
		// 개설된 화상회의가 있는지 체크
		Map<String, Object> remoteConferenceInfo = remoteMeetingRepository.selectRemoteMeetingInfoExistCheck(fiData);
		
		String rmProgSts = "";
		
		if(null != remoteConferenceInfo) {
			rmProgSts = remoteConferenceInfo.get("vconf_sts_ccd") == null ? "" : remoteConferenceInfo.get("vconf_sts_ccd").toString();
		}
		
		if(RemoteMeetingProgCode.endCheckConference(rmProgSts)) {
			resultMap.put(Const.RESULT_STATUS, Const.FAIL);
			resultMap.put(Const.RESULT_MSG, "해당 회의는 종료된 회의입니다. 다시 한 번 확인해주세요.");
		} else {
			if(existCheck) {
				if(null != remoteConferenceInfo && remoteConferenceInfo.values().size() > 0) {
					resultMap.put("check", true);
				} else {
					resultMap.put("check", false);
					resultMap.put(Const.RESULT_MSG, "현재 개설된 화상회의가 존재하지 않습니다. 새로 개설하시겠습니까?");
				}
			} else {
				if(null != remoteConferenceInfo && remoteConferenceInfo.values().size() > 0) {
					resultMap.put("check", false);
					resultMap.put(Const.RESULT_MSG, "현재 개설된 화상회의가 존재합니다. 해당 개설된 화상회의로 참석하시겠습니까?");
				} else {
					resultMap.put("check", true);
				}
			}
		}
		
		return resultMap;
	}
	
	
	/**
	 * 화상회의 통보 취소 / 강제 종료 / 삭제 처리 간 프로세스
	 *
	 * @param fiData
	 * @return
	 */
	public Map<String, Object> remoteMeetingDelete(Map<String, Object> fiData) {
		
		Map<String, Object> remoteMeetInfoDeleteResultMap = new HashMap<String, Object>();
		
		try {
			remoteMeetingRepository.deleteRemoteMeetingForFiIDAndNotReservation(fiData);
			
			fiData.put("vconf_sts_ccd", RemoteMeetingProgCode.REMOTE_MEETING_STATUS_NULL.getCode());
			remoteMeetingRepository.deleteFiProgStsReservationMeeting(fiData);
			
			String fiId = fiData.get("sitebrfg_uuid") == null ? "" : fiData.get("sitebrfg_uuid").toString();
			
			remoteMeetingRepository.deleteFiRemoteMeetingInfo(fiData);
			
			if(remoteMeetingResultService.isEndRemoteCallFieldJobExistCheck()) { // 스케줄러 존재여부 확인
				remoteMeetingResultService.deleteEndRemoteCallFieldJobJob(fiId); // 스케줄러 삭제
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw new CommonException(e.getMessage());
		}
		
		return remoteMeetInfoDeleteResultMap;
	}
	
	/**
	 * 협력사 회의 참석 URL GET
	 *
	 * @param fiData
	 * @return
	 */
	public Map<String, Object> remoteMeetingConferenceVendorJoin(Map<String, Object> fiData) {
		Map<String, Object> resultRemoteMeetInfo = new HashMap<String, Object>();
		
		// 개설된 화상회의가 있는지 체크
		String fiId = fiData.get("sitebrfg_uuid") == null ? "" : fiData.get("sitebrfg_uuid").toString();
		fiData.put("task_uuid", fiId);
		
		// 개설된 화상회의가 있는지 체크
		Map<String, Object> remoteConferenceInfo = remoteMeetingRepository.selectRemoteMeetingInfoExistCheck(fiData);
		
		String rmProgSts = "";
		
		if(null != remoteConferenceInfo) {
			rmProgSts = remoteConferenceInfo.get("vconf_sts_ccd") == null ? "" : remoteConferenceInfo.get("vconf_sts_ccd").toString();
		}
		
		if(RemoteMeetingProgCode.endCheckConference(rmProgSts)) {
			resultRemoteMeetInfo.put(Const.RESULT_STATUS, Const.FAIL);
			resultRemoteMeetInfo.put(Const.RESULT_MSG, "해당 회의는 종료된 회의입니다. 참석이 불가능합니다.");
		} else {
			// 개설된 회의가 있을 경우, 참석 url을 꺼내어 result로 던져주고, 없을 경우 바로 개설을 통하여 얻어온 URL을 던저준다.
			if(null != remoteConferenceInfo && remoteConferenceInfo.values().size() > 0) {
				// 참석을 눌러 URL을 받은 업체는 참석를 "Y"로 둔다.
				fiData.put("atnd_yn", "Y");
				spSiteBriefingRepository.submitSpFieldAttend(fiData);
				
				// return으로 받은 join url 던져주기
				String joinUrl = remoteConferenceInfo.get("mtg_url") == null ? "" : remoteConferenceInfo.get("mtg_url").toString();
				resultRemoteMeetInfo.put("url", joinUrl);
			} else {
				resultRemoteMeetInfo.put(Const.RESULT_STATUS, Const.FAIL);
				resultRemoteMeetInfo.put(Const.RESULT_MSG, " 현재 진행 중인 회의가 없습니다. 아직 개설 전이거나, 종료된 상태 일 수도 있습니다. 다시 한 번 확인해주세요.");
			}
		}
		
		return resultRemoteMeetInfo;
	}
}
