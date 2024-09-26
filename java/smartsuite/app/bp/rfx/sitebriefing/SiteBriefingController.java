package smartsuite.app.bp.rfx.sitebriefing;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.RemoteMeetingCallService;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.RemoteMeetingReservationService;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.RemoteMeetingResultService;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.model.RemoteMeetScheduler;
import smartsuite.app.bp.rfx.sitebriefing.service.SiteBriefingService;

import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 현장설명회 관련 처리 하는 controller class
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/bp/rfx/sitebriefing/")
public class SiteBriefingController {
	
	@Inject
	SiteBriefingService siteBriefingService;
	
	@Inject
	RemoteMeetingReservationService remoteMeetingReservationService;
	
	@Inject
	RemoteMeetingCallService remoteMeetingCallService;
	
	@Inject
	RemoteMeetingResultService remoteMeetingResultService;
	
	@Inject
	RemoteMeetScheduler remoteMeetScheduler;
	
	/**
	 * 현장 설명회 현황 목록 조회 (대용량 처리)
	 *
	 * @param param 조회 조건 object
	 * @return FloaterStream
	 */
	@RequestMapping(value = "findListFieldIntro.do")
	public @ResponseBody FloaterStream findListFieldIntro(@RequestBody Map param) {
		return siteBriefingService.findListFieldIntro(param);
	}
	
	/**
	 * 현장 설명회 상세 조회
	 *
	 * @param param 조회 조건 object
	 * @return Map
	 */
	@RequestMapping(value = "findInfoFieldIntro.do")
	public @ResponseBody Map findInfoFieldIntro(@RequestBody Map param) {
		return siteBriefingService.findInfoFieldIntro(param);
	}
	
	/**
	 * 화상회의 공유 정보 조회
	 *
	 * @param param 조회 조건 object
	 * @return Map
	 */
	@RequestMapping(value = "sharedRemoteMeetInfo.do")
	public @ResponseBody Map sharedRemoteMeetInfo(@RequestBody Map param) {
		return siteBriefingService.sharedRemoteMeetInfo(param);
	}
	
	@RequestMapping(value = "findNoteInfoTaskList.do")
	public @ResponseBody Map findNoteInfoTaskList(@RequestBody Map param) {
		return siteBriefingService.findNoteInfoTaskList(param);
	}
	
	/**
	 * 현장 설명회 저장
	 *
	 * @param param 현장설명회 정보 object
	 * @return Map
	 */
	@RequestMapping(value = "tempSaveFieldIntro.do")
	public @ResponseBody Map tempSaveFieldIntro(@RequestBody Map param) {
		return siteBriefingService.tempSaveFieldIntro(param);
	}
	
	/**
	 * 현장 설명회 통보
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	@RequestMapping(value = "notifyFieldIntro.do")
	public @ResponseBody Map notifyFieldIntro(@RequestBody Map param) {
		return siteBriefingService.notifyFieldIntro(param);
	}
	
	/**
	 * 현장 설명회 헤디, 품목, 업체 정보 저장
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	@RequestMapping(value = "cancelFieldIntro.do")
	public @ResponseBody Map cancelFieldIntro(@RequestBody Map param) {
		return siteBriefingService.cancelFieldIntro(param);
	}
	
	/**
	 * 현장 설명회 삭제
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	@RequestMapping(value = "deleteFieldIntro.do")
	public @ResponseBody Map deleteFieldIntro(@RequestBody Map param) {
		return siteBriefingService.deleteFieldIntro(param);
	}
	
	/**
	 * 현장 설명회 복수건 삭제 (목록에서 여러건 선택 후 삭제 시)
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	@RequestMapping(value = "deleteListFieldIntro.do")
	public @ResponseBody Map deleteListFieldIntro(@RequestBody Map param) {
		return siteBriefingService.deleteListFieldIntro(param);
	}
	
	/**
	 * 현장 설명회 업체 참석 확정 처리
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	@RequestMapping(value = "confirmFieldIntro.do")
	public @ResponseBody Map confirmFieldIntro(@RequestBody Map param) {
		return siteBriefingService.confirmFieldIntro(param);
	}
	
	/**
	 * 견적 생성 시 현장설명회 목록 조회
	 *
	 * @param param 현장 설명회 정보 object
	 * @return List
	 */
	@RequestMapping(value = "findListFieldIntroPopup.do")
	public @ResponseBody List<Map> findListFieldIntroPopup(@RequestBody Map param) {
		return siteBriefingService.findListFieldIntroPopup(param);
	}
	
	
	/**
	 * 현장 설명회 강제 종료 (목록에서 여러건 선택 후 종료)
	 *
	 * @param param 현장 설명회 정보 object
	 * @return Map
	 */
	@RequestMapping(value = "closeListFieldIntro.do")
	public @ResponseBody Map closeListFieldIntro(@RequestBody Map param) {
		return siteBriefingService.closeListFieldIntro(param);
	}
	
	/**
	 * 예약된 화상회의 시작 체크 및 개설
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "reservationNowStartRemoteMeeting.do")
	public @ResponseBody Map reservationNowStartRemoteMeeting(@RequestBody Map param) {
		return remoteMeetingReservationService.reservationNowStartRemoteMeeting(param);
	}
	
	/**
	 * 화상회의 시작 체크 및 바로 개설
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "nowStartRemoteMeeting.do")
	public @ResponseBody Map nowStartRemoteMeeting(@RequestBody Map param) {
		return remoteMeetingCallService.nowStartRemoteMeetingConference(param);
	}
	
	/**
	 * 화상회의 시작 체크 및 바로 개설
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "validateRemoteMeetingExist.do")
	public @ResponseBody Map validateRemoteMeetingExist(@RequestBody Map param) {
		return remoteMeetingCallService.validateRemoteMeetingExist(param);
	}
	
	/**
	 * 화상회의 회의록 즉시 수신
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "noteResponseApiNowCall.do")
	public @ResponseBody void noteResponseApiNowCall(@RequestBody Map param) {
		remoteMeetScheduler.noteResponseApiCall((HashMap) param);
	}
	
	/**
	 * Remote call Start callbackURL을 수신 ( 회의 생성 결과 수신 )
	 *
	 * @throws Exception
	 */
	@RequestMapping(value = "**/remoteConferenceStartCallBackUrl.do", method = RequestMethod.POST)
	public void callConferenceStartCallBackUrl(HttpServletRequest request) throws Exception {
		StringBuffer responseData = new StringBuffer();
		JsonParser parser = new JsonParser();
		JsonObject jsonObj = null;
		
		try {
			BufferedReader reader = request.getReader();
			
			while(null != reader.readLine()) {
				responseData.append(reader.readLine());
			}
			
			if(StringUtils.isNotEmpty(responseData.toString())) {
				Object obj = parser.parse(responseData.toString());
				jsonObj = (JsonObject) obj;
			}
		} catch(Exception e) {
			e.printStackTrace();
			//LOGGER.error("Error reading JSON string: " + e.toString());
		}
		remoteMeetingResultService.startCallbackResultProcess(jsonObj);
	}
	
	/**
	 * Remote call End callbackURL을 수신
	 *
	 * @throws Exception
	 */
	@RequestMapping(value = "**/remoteConferenceEndCallBackUrl.do", method = RequestMethod.POST)
	public void callConferenceEndCallBackUrl(HttpServletRequest request) throws Exception {
		StringBuffer responseData = new StringBuffer();
		JsonParser parser = new JsonParser();
		JsonObject jsonObj = null;
		
		try {
			BufferedReader reader = request.getReader();
			while(null != reader.readLine()) {
				responseData.append(reader.readLine());
			}
			
			if(StringUtils.isNotEmpty(responseData.toString())) {
				Object obj = parser.parse(responseData.toString());
				jsonObj = (JsonObject) obj;
			}
		} catch(Exception e) {
			e.printStackTrace();
			//LOGGER.error("Error reading JSON string: " + e.toString());
		}
		
		remoteMeetingResultService.endCallbackResultProcess(jsonObj);
	}
	
	/**
	 * 현장 설명회 화상회의 공유 저장
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveRemoteMeetShared.do")
	public @ResponseBody Map saveRemoteMeetShared(@RequestBody Map param) {
		return siteBriefingService.saveRemoteMeetShared(param);
	}
	
	/**
	 * 현장 설명회 녹화영상 가져오기 프로세스
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "recVideoDown.do")
	public @ResponseBody void recVideoDown(@RequestBody Map param) {
		remoteMeetScheduler.recVideoDown((HashMap) param);
	}
	
	/**
	 * 현장설명회 업체 리스트 가져오기
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findFieldIntroVendorList.do")
	public @ResponseBody List<Map> findFieldIntroVendorList(@RequestBody Map param) {
		return siteBriefingService.findFieldIntroVendorList(param);
	}
}
