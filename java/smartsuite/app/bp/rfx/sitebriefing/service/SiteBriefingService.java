package smartsuite.app.bp.rfx.sitebriefing.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.RemoteMeetingCallService;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.RemoteMeetingReservationService;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.code.RemoteMeetingProgCode;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.RemoteMeetProperties;
import smartsuite.app.bp.rfx.sitebriefing.repository.RemoteMeetingRepository;
import smartsuite.app.bp.rfx.sitebriefing.repository.SiteBriefingRepository;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.data.FloaterStream;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.scheduler.core.ScheduleService;

import javax.inject.Inject;
import java.util.*;

/**
 * 현장설명회 관련 처리 하는 service class
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SiteBriefingService {
	
	@Inject
	SiteBriefingRepository siteBriefingRepository;
	
	@Inject
	RemoteMeetingRepository remoteMeetingRepository;
	
	@Value("#{rfx['remote.meeting.reservation.process']}")
	private boolean remote_meeting_reservation_process;
	
	//현장 설명회 진행
	private static final String PROG_STS_PROCESS = "PRGSG";
	
	//현장 설명회 취소
	private static final String PROG_STS_CANCEL = "D";
	
	//현장 설명회 저장
	private static final String PROG_STS_TEMP = "T";
	
	@Inject
	SharedService sharedService;
	
	@Inject
	ScheduleService scheduleService;
	
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	RemoteMeetingReservationService remoteMeetingReservationService;
	
	@Inject
	RemoteMeetingCallService remoteMeetingCallService;
	@Inject
	MailService mailService;
	
	/**
	 * 현장 설명회 현황 목록 조회 (대용량 처리)
	 *
	 * @param param 조회 조건 object
	 * @return FloaterStream
	 */
	public FloaterStream findListFieldIntro(Map param) {
		return siteBriefingRepository.findListFieldIntro(param);
	}
	
	/**
	 * 현장 설명회 상세 조회
	 *
	 * @param param 조회 조건 object
	 * @return Map
	 */
	public Map findInfoFieldIntro(Map param) {
		Map resultMap = Maps.newHashMap();
		
		Map fiData = siteBriefingRepository.findInfoFieldIntro(param);
		List<Map> fiItems = siteBriefingRepository.findListFiItem(param);
		List<Map> fiVendors = siteBriefingRepository.findListFiVendor(param);
		
		resultMap.put("fiData", fiData);
		resultMap.put("fiItems", fiItems);
		resultMap.put("fiVendors", fiVendors);
		//todo 리모트미팅 프로퍼티 java -> properties 변경
		resultMap.put("rmReservationYN", remote_meeting_reservation_process);
		
		// 화상회의 사용여부에 따른 조회
		String rmYn = (String) fiData.get("vconf_use_yn");
		if(!remote_meeting_reservation_process && "Y".equals(rmYn)) {
			String fiId = (String) fiData.get("sitebrfg_uuid");
			fiData.put("task_uuid", fiId);
			Map remoteConferenceInfo = remoteMeetingRepository.selectRemoteMeetingInfo(fiData);
			
			// 개설된 화상회의가 있는지.
			boolean remoteMeetingExistCheck = (null != remoteConferenceInfo && remoteConferenceInfo.values().size() > 0);
			resultMap.put("rmExistCheck", remoteMeetingExistCheck);
		}
		
		return resultMap;
	}
	
	/**
	 * 화상회의 공유 정보 조회
	 *
	 * @param param 조회 조건 object
	 * @return Map
	 */
	public Map sharedRemoteMeetInfo(Map param) {
		Map resultMap = Maps.newHashMap();
		
		// 최종 회의록 정보
		List<Map> remoteMeetNotes = new ArrayList<Map>();
		
		// 화상회의 존재 여부 및 연결된 회의 정보 , 회의록 , 참석자 가져오기
		List<Map> remoteMeetList = remoteMeetingRepository.selectRemoteMeetingInfoAndNoteList(param);
		
		Map fiData = siteBriefingRepository.findInfoFieldIntro(param);
		for(Map remoteMeetInfo : remoteMeetList) {
			String noteId = remoteMeetInfo.get("mtgmins_uuid") == null ? "" : remoteMeetInfo.get("mtgmins_uuid").toString();
			if(StringUtils.isEmpty(noteId)) continue;
			
			List<Map> remoteMeetJoinUserList = remoteMeetingRepository.remoteMeetJoinUserList(remoteMeetInfo); // 화상회의 직접 참여 Nickname
			remoteMeetInfo.put("remoteMeetJoinUserList", remoteMeetJoinUserList);
			
			remoteMeetNotes.add(remoteMeetInfo);
		}
		
		List<Map> fiVendors = siteBriefingRepository.findListFiVendor(param); // 화상회의 참석 예정 업체
		
		resultMap.put("fiData", fiData);
		resultMap.put("remoteMeetNotes", remoteMeetNotes);
		resultMap.put("fiVendors", fiVendors);
		return resultMap;
	}
	
	
	/**
	 * 회의록에 연결된 task list 정보 가져오기
	 *
	 * @param param
	 * @return
	 */
	public Map findNoteInfoTaskList(Map param) {
		Map resultMap = Maps.newHashMap();
		
		List<Map> taskList = remoteMeetingRepository.selectRemoteMeetingNoteInfoTaskList(param); // 회의록에 연결된 Task 정보
		Map noteInfo;
		if(taskList.size() > 0) {
			noteInfo = taskList.get(0);
		} else {
			noteInfo = Maps.newHashMap();
		}
		
		resultMap.put("taskList", taskList);
		resultMap.put("noteInfo", noteInfo);
		
		return resultMap;
	}
	
	/**
	 * 현장 설명회 저장
	 *
	 * @param param 현장설명회 정보 object
	 * @return Map
	 */
	public Map tempSaveFieldIntro(Map param) {
		Map resultMap = this.saveFieldIntro(param);
		rfxStatusService.saveSiteBriefingProgSts(resultMap);
		return resultMap;
	}
	
	/**
	 * 현장 설명회 통보
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	public Map notifyFieldIntro(Map param) {
		Map remoteMeetInfo = Maps.newHashMap();
		Map resultMap = Maps.newHashMap();
		
		
		resultMap = this.saveFieldIntro(param);
		String fiId = (String) resultMap.get("sitebrfg_uuid");
		Map fiData = param.get("fiData") == null ? new HashMap() : (Map) param.get("fiData");
		Date fiEndDt = fiData.get("sitebrfg_clsg_dttm") == null ? null : (Date) fiData.get("sitebrfg_clsg_dttm");
		Object[] args = new Object[]{fiData};
		
		// 비대면 화상회의 예약
		String remoteMeetingYn = (String) fiData.get("vconf_use_yn");
		
		Map remoteConferenceInfo = siteBriefingRepository.findInfoFieldIntro(fiData);
		
		if("Y".equals(remoteMeetingYn)) {
			// 개설된 화상회의가 있는지 체크
			fiData.put("task_uuid", fiId);
			
			String rmProgSts = "";
			
			if(null != remoteConferenceInfo) {
				rmProgSts = remoteConferenceInfo.get("vconf_sts_ccd") == null ? "" : remoteConferenceInfo.get("vconf_sts_ccd").toString();
			} // 회의 정보가 있을경우만.
			
			// API 형태이기 때문에 진행된 상태의 회의인지. 체크
			if(!(RemoteMeetingProgCode.REMOTE_MEETING_STATUS_NULL.getCode().equals(rmProgSts) || RemoteMeetingProgCode.REMOTE_MEETING_CONFERENCE_NOT_START.getCode().equals(rmProgSts))) {
				resultMap.put(Const.RESULT_STATUS, Const.FAIL);
				resultMap.put(Const.RESULT_MSG, "해당 회의는 이미 진행된 회의 입니다. 다시 한 번 확인해주세요.");
				return resultMap;
			} else {
				if(RemoteMeetProperties.REMOTE_MEETING_RESERVATION_PROCESS_YN) {
					// 비대면화상회의 예약 프로세스를 태울 경우 ( 리모트콜에서는 예약 프로세스를 태우기가 불가능함. 레퍼런스로만 확인 필요
					remoteMeetInfo = remoteMeetingReservationService.reservationRemoteMeetingConference(fiData);
					if(!remoteMeetInfo.isEmpty()) {
						resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
						resultMap.put(Const.RESULT_MSG, "비대면 화상회의가 정상적으로 예약되었습니다.");
					}
				}
				
				// 현장설명회 시간이 지날 경우 상태값이 미진행 상태라면, 종료 처리 ( 스케줄러는 제외, 현장설명회가 종료시간 보다 오버할때가 있을수있을꺼같음. )
				/*if(remoteMeetingResultService.isEndRemoteCallFieldJobExistCheck()){
					remoteMeetingResultService.deleteEndRemoteCallFieldJobJob(fiId); // 스케줄러 삭제
				}
				remoteMeetingResultService.registEndRemoteCallFieldJob(fiId, fiEndDt, args);*/
			}
		}
		
		rfxStatusService.notifySiteBriefingProgSts(resultMap);
		
		// 설명회 통보 취소 시 정상적으로 스케줄러가 삭제되지 않는 경우가 있어 체크후 삭제 처리함.
		if(isRegistryEndFieldJobExistCheck()) registEndFieldIntroJob(fiId);
		
		// 설명회 종료 스케줄러 등록
		registEndFieldIntroJob(fiId, fiEndDt, args);
		
		//협력사 통보 시 메일 발송 ( 비대면만인지 확인 필요 )
		mailService.sendAsync("SITEBRIEF_VENDOR_NOTIFY", fiId, remoteConferenceInfo);
		
		return resultMap;
	}
	
	/**
	 * 현장 설명회 헤디, 품목, 업체 정보 저장
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	private Map saveFieldIntro(Map param) {
		Map fiData = (Map) param.get("fiData");
		List<Map> deleteFiItems = (List<Map>) param.get("deleteFiItems");
		List<Map> deleteFiVendors = (List<Map>) param.get("deleteFiVendors");
		List<Map> insertFiItems = (List<Map>) param.get("insertFiItems");
		List<Map> updateFiItems = (List<Map>) param.get("updateFiItems");
		List<Map> insertFiVendors = (List<Map>) param.get("insertFiVendors");
		List<Map> updateFiVendors = (List<Map>) param.get("updateFiVendors");
		
		// 헤더
		if(fiData.get("sitebrfg_uuid") == null || Strings.isNullOrEmpty((String) fiData.get("sitebrfg_uuid"))) {
			fiData.put("sitebrfg_uuid", UUID.randomUUID().toString());
			fiData.put("rcrd_athg_uuid", UUID.randomUUID().toString());
			String fiNo = sharedService.generateDocumentNumber("FI");
			fiData.put("sitebrfg_no", fiNo);
			
			siteBriefingRepository.insertFiHd(fiData);
		} else {
			siteBriefingRepository.updateFiHd(fiData);
		}
		
		// 삭제
		if(deleteFiItems != null) {
			for(Map fiItem : deleteFiItems) {
				siteBriefingRepository.deleteFiItem(fiItem);
			}
		}
		if(deleteFiVendors != null) {
			for(Map fiVendor : deleteFiVendors) {
				siteBriefingRepository.deleteFiVendor(fiVendor);
			}
		}
		
		// 수정
		if(updateFiItems != null) {
			for(Map fiItem : updateFiItems) {
				fiItem.put("sitebrfg_uuid", fiData.get("sitebrfg_uuid"));
				siteBriefingRepository.updateFiItem(fiItem);
			}
		}
		if(updateFiVendors != null) {
			for(Map fiVendor : updateFiVendors) {
				fiVendor.put("sitebrfg_uuid", fiData.get("sitebrfg_uuid"));
				siteBriefingRepository.updateFiVendor(fiVendor);
			}
		}
		
		// 추가
		if(insertFiItems != null) {
			for(Map fiItem : insertFiItems) {
				String fiItemId = UUID.randomUUID().toString();
				fiItem.put("oorg_cd", fiData.get("oorg_cd"));
				fiItem.put("sitebrfg_item_uuid", fiItemId);
				fiItem.put("sitebrfg_uuid", fiData.get("sitebrfg_uuid"));
				
				siteBriefingRepository.insertFiItem(fiItem);
			}
		}
		if(insertFiVendors != null) {
			for(Map fiVendor : insertFiVendors) {
				String fiVdId = UUID.randomUUID().toString();
				fiVendor.put("sitebrfg_vd_uuid", fiVdId);
				fiVendor.put("sitebrfg_uuid", fiData.get("sitebrfg_uuid"));
				
				siteBriefingRepository.insertFiVendor(fiVendor);
			}
		}
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("sitebrfg_uuid", fiData.get("sitebrfg_uuid"));
		return resultMap;
	}
	
	/**
	 * 현장 설명회 통보 취소
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	public Map cancelFieldIntro(Map param) {
		rfxStatusService.cancelSiteBriefingProgSts(param); // 통보취소
		// 기존 등록 된 스케줄러 삭제
		String fiId = (String) param.get("sitebrfg_uuid");
		this.registEndFieldIntroJob(fiId);
		
		//화상회의 진행 여부에 따른 삭제
		String remoteMeetingYn = param.get("vconf_use_yn") == null ? "" : param.get("vconf_use_yn").toString();
		isRemoteCallCancelProcess(remoteMeetingYn, param);
		return param;
	}
	
	/**
	 * 현장 설명회 삭제
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	public Map deleteFieldIntro(Map param) {
		siteBriefingRepository.deleteFiItems(param);
		siteBriefingRepository.deleteFiVendors(param);
		siteBriefingRepository.deleteFiHd(param);
		
		//화상회의 진행 여부에 따른 삭제
		String remoteMeetingYn = param.get("vconf_use_yn") == null ? "" : param.get("vconf_use_yn").toString();
		isRemoteCallCancelProcess(remoteMeetingYn, param);
		
		Map resultMap = Maps.newHashMap();
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}
	
	/**
	 * 현장 설명회 복수건 삭제 (목록에서 여러건 선택 후 삭제 시)
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	public Map deleteListFieldIntro(Map param) {
		List<Map> deleteList = (List<Map>) param.get("deleteList");
		
		for(Map deleteInfo : deleteList) {
			this.deleteFieldIntro(deleteInfo);
		}
		
		Map resultMap = Maps.newHashMap();
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}
	
	/**
	 * 현장 설명회 업체 참석 확정 처리
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	public Map confirmFieldIntro(Map param) {
		// 참석 확정 여부 update
		siteBriefingRepository.updateAttendConfirm(param);
		List<Map> vendors = (List<Map>) param.get("vendors");
		
		// 업체 참석여부 UPDATE
		if(vendors != null) {
			String sitebrfgUuid = param.get("sitebrfg_uuid") == null? "" : (String) param.get("sitebrfg_uuid");

			for(Map vendor : vendors) {
				siteBriefingRepository.updateVendorAttendYn(vendor);
			}

			// 협력사 참석 확정 메일
			mailService.sendAsync("SITEBRIEF_NOTIFY_ATTENDANCE_CONFIRM", sitebrfgUuid, param);
		}
		return param;
		
	}
	
	/**
	 * 현장 설명회 종료 처리 스케줄러 등록
	 *
	 * @param fiId    현장 설명회 아이디
	 * @param fiEndDt 스케줄러 실행 일시
	 * @param args    스케줄러에서 사용될 parameter
	 */
	private void registEndFieldIntroJob(String fiId, Date fiEndDt, Object[] args) {
		try {
			scheduleService.registSchedule(Class.forName("smartsuite.app.bp.rfx.sitebriefing.service.SiteBriefingService"),
			                               "endFieldIntro",
			                               args,
			                               fiEndDt,
			                               "FI",
			                               fiId,
			                               "FI_END",
			                               null);
		} catch(ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	/**
	 * 현장 설명회 종료 처리 스케줄러 삭제
	 *
	 * @param fiId 현장 설명회 아이디
	 */
	private void registEndFieldIntroJob(String fiId) {
		if(fiId != null) {
			try {
				scheduleService.removeScheduleTrigger(Class.forName("smartsuite.app.bp.rfx.sitebriefing.service.SiteBriefingService"),
				                                      "endFieldIntro",
				                                      "FI",
				                                      fiId);
			} catch(ClassNotFoundException e) {
				throw new CommonException(ErrorCode.FAIL, e);
			}
		}
		
	}
	
	
	/**
	 * 현장 설명회 종료 처리
	 *
	 * @param param 현장 설명회 정보 object
	 */
	public void endFieldIntro(LinkedHashMap param) {
		rfxStatusService.closeSiteBriefingProgSts(param);
	}
	
	/**
	 * 견적 생성 시 현장설명회 목록 조회
	 *
	 * @param param 현장 설명회 정보 object
	 * @return List
	 */
	public List<Map> findListFieldIntroPopup(Map param) {
		return siteBriefingRepository.findListFieldIntroPopup(param);
	}
	
	/**
	 * 견적 생성 시 현장설명회 정보 직접 생성
	 *
	 * @param param {rfx_id}
	 */
	public void directFieldIntro(Map param) {
		Map resultMap = Maps.newHashMap();
		// [STEP 1. 견적 현장설명회 정보 조회 및 현장설명회 헤더 생성]
		Map fiData = siteBriefingRepository.selectRfxFieldIntroHd(param);
		fiData.put("sitebrfg_uuid", UUID.randomUUID().toString());
		String fiNo = sharedService.generateDocumentNumber("FI");
		fiData.put("sitebrfg_no", fiNo);
		fiData.put("sitebrfg_tit", fiData.get("rfx_no").toString
				() + "_현장설명회 직접 등록");
		fiData.put("rcrd_athg_uuid", UUID.randomUUID().toString());
		
		siteBriefingRepository.insertFiHd(fiData);
		// 견적 헤더 sitebrfg_uuid 업데이트
		siteBriefingRepository.updateRfxHdFieldInfo(fiData);
		
		// [STEP 2. 견적 품목 정보 조회 및 현장설명회 품목 정보 생성]
		List<Map> fiItems = siteBriefingRepository.selectRfxItems(param);
		for(Map fiItem : fiItems) {
			String fiItemId = UUID.randomUUID().toString();
			fiItem.put("sitebrfg_item_uuid", fiItemId);
			fiItem.put("sitebrfg_uuid", fiData.get("sitebrfg_uuid"));
			siteBriefingRepository.insertFiItem(fiItem);
		}
		
		// [STEP 3. 견적 업체 정보 조회 및 현장설명회 업체 정보 생성]
		List<Map> fiVendors = siteBriefingRepository.selectRfxVendors(param);
		for(Map fiVendor : fiVendors) {
			String fiVdId = UUID.randomUUID().toString();
			fiVendor.put("sitebrfg_vd_uuid", fiVdId);
			fiVendor.put("sitebrfg_uuid", fiData.get("sitebrfg_uuid"));
			siteBriefingRepository.insertFiVendor(fiVendor);
		}
	}
	
	/**
	 * 현장 설명회 강제 종료 (목록에서 여러건 선택 후 종료)
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	public Map closeListFieldIntro(Map param) {
		List<Map> closeList = (List<Map>) param.get("closeList");
		
		for(Map fiInfo : closeList) {
			rfxStatusService.closeSiteBriefingProgSts(fiInfo);
			
			// 기존 등록 된 스케줄러 삭제
			String fiId = (String) fiInfo.get("sitebrfg_uuid");
			this.registEndFieldIntroJob(fiId);
			
			//화상회의 진행 여부에 따른 삭제
			String remoteMeetingYn = fiInfo.get("vconf_use_yn") == null ? "" : fiInfo.get("vconf_use_yn").toString();
			this.isRemoteCallCancelProcess(remoteMeetingYn, param);

			// 현장설명회 협력사 참석 종료 메일
			mailService.sendAsync("SITEBRIEF_NOTIFY_ATTENDANCE_CLOSING", fiId, fiInfo);
		}
		
		Map resultMap = Maps.newHashMap();
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}
	
	
	public boolean isRegistryEndFieldJobExistCheck() {
		try {
			return scheduleService.isJobExist("FI", "FI_END");
		} catch(SchedulerException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	
	/**
	 * 리모트콜 cancel 처리간 예약 프로세스 / 바로 개설 프로세스에 따라 호출을 다르게 가져가도록 하는 핸들링 메소드
	 * 리모트콜내에서는 예약 프로세스는 사용이 불가하다 ( 레퍼런스 용도로만 확인필요 )
	 *
	 * @param remoteMeetingYn
	 * @param param
	 */
	public void isRemoteCallCancelProcess(String remoteMeetingYn, Map param) {
		if(StringUtils.isNotEmpty(remoteMeetingYn) && ("Y").equals(remoteMeetingYn)) {
			if(RemoteMeetProperties.REMOTE_MEETING_RESERVATION_PROCESS_YN) {
				// 예약 프로세스를 태울 경우,
				remoteMeetingReservationService.reservationRemoteMeetingDelete(param);
			} else {
				// 직접 개설일 경우
				remoteMeetingCallService.remoteMeetingDelete(param);
			}
		}
	}
	
	
	public Map saveRemoteMeetShared(Map param) {
		List<Map> vendors = param.get("vendors") == null ? new ArrayList<Map>() : (List<Map>) param.get("vendors");
		Map fiData = param.get("fiData") == null ? new HashMap() : (Map) param.get("fiData");
		
		remoteMeetingRepository.updateRemoteMeetingSharedYn(fiData);
		
		// 업체 참석여부 UPDATE
		if(vendors != null) {
			for(Map vendor : vendors) {
				vendor.putAll(fiData);
				siteBriefingRepository.updateVendorAttendYn(vendor);
			}
		}
		return param;
		
	}
	
	
	/**
	 * 현장설명회 참여 Vendor List popup
	 *
	 * @param param
	 * @return
	 */
	public List<Map> findFieldIntroVendorList(Map param) {
		return siteBriefingRepository.findListFiVendor(param);
	}
}
