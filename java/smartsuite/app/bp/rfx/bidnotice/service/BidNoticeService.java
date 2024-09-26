package smartsuite.app.bp.rfx.bidnotice.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.bidnotice.repository.BidNoticeRepository;
import smartsuite.app.bp.rfx.bidnotice.scheduler.BidNoticeSchedulerService;
import smartsuite.app.bp.rfx.bidnotice.validator.BidNoticeValidator;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.message.MessageUtil;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.data.FloaterStream;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.util.*;

/**
 * BidNotice 관련 처리하는 서비스 Class입니다.
 *
 * @FileName BidNoticeService.java
 * @package smartsuite.app.bp.rfx.bidnotice
 * @Since 2021. 5. 14
 * @see
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class BidNoticeService {

	@Inject
	BidNoticeRepository bidNoticeRepository;

	@Inject
	RfxStatusService rfxStatusService;

	/**
	 * The shared service.
	 */
	@Inject
	SharedService sharedService;

	/**
	 * The bid noti validator.
	 */
	@Inject
	BidNoticeValidator bidNoticeValidator;

	/**
	 * The bid noti scheduler service.
	 */
	@Inject
	BidNoticeSchedulerService bidNoticeSchedulerService;

	@Inject
	MessageUtil messageUtil;

	/**
	 * The attach service.
	 */
	@Inject
	StdFileService stdFileService;

	@Inject
	MailService mailService;

	/**
	 * 정정공고
	 */
	private static final String NOTI_TYP_A = "PRENTC_CORR";
	/**
	 * 마감
	 */
	private static final String NOTI_PROG_STS_E = "E";
	/**
	 * 재공고
	 */
	private static final String NOTI_TYP_R = "RENTC";

	/**
	 * list bid noti 조회한다.
	 *
	 * @param param the param
	 * @return the floater stream
	 * @Date : 2021. 5. 14
	 * @Method Name : findListBidNoti
	 */
	public FloaterStream findListBidNoti(Map param) {
		return bidNoticeRepository.findListBidNoti(param);
	}

	/**
	 * bid noti info 조회한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : findBidNotiInfo
	 */
	public Map findBidNotiInfo(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("bidNotiData", this.findBidNoti(param));
		resultMap.put("vdList", this.findListBidNotiVendor(param));

		return resultMap;
	}

	/**
	 * bid noti 조회한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : findBidNoti
	 */
	public Map findBidNoti(Map param) {
		return bidNoticeRepository.findBidNoti(param);
	}

	/**
	 * list bid noti vendor 조회한다.
	 *
	 * @param param the param
	 * @return the list< map< string, object>>
	 * @Date : 2021. 5. 14
	 * @Method Name : findListBidNotiVendor
	 */
	public List<Map> findListBidNotiVendor(Map param) {
		return bidNoticeRepository.findListBidNotiVendor(param);
	}

	/**
	 * bid noti 임시 저장한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : saveDraftBidNoti
	 */
	public ResultMap saveDraftBidNoti(Map param) {
		Map bidNotiData = (Map) param.get("bidNotiData");

		ResultMap validator = bidNoticeValidator.validate(bidNotiData);
		if(!validator.isSuccess()) {
			return validator;
		}

		ResultMap resultMap = this.saveBidNoti(param);
		if(resultMap.isSuccess()) {
			//상태처리
			Map resultData = resultMap.getResultData();
			rfxStatusService.saveDraftBidNoti(resultData);
		}

		return resultMap;
	}

	/**
	 * bid noti 저장한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : saveBidNoti
	 */
	private ResultMap saveBidNoti(Map param) {
		Map bidNotiData = (Map) param.get("bidNotiData");
		List<Map> deleteVendors = (List<Map>) param.get("deleteVendors");
		List<Map> insertVendors = (List<Map>) param.get("insertVendors");
		List<Map> updateVendors = (List<Map>) param.get("updateVendors");

		int notiRev = 1;
		if(bidNotiData.get("rfx_prentc_rnd") != null) {
			notiRev = Integer.parseInt(bidNotiData.get("rfx_prentc_rnd").toString());
		}

		if(Strings.isNullOrEmpty((String) bidNotiData.get("rfx_prentc_uuid"))) {
			//신규 id생성
			bidNotiData.put("rfx_prentc_uuid", UUID.randomUUID().toString());
			// Noti No 채번
			if(Strings.isNullOrEmpty((String) bidNotiData.get("rfx_prentc_no"))) {
				bidNotiData.put("rfx_prentc_no", sharedService.generateDocumentNumber("BN"));
			}
			bidNotiData.put("rfx_prentc_rnd", notiRev);

			bidNoticeRepository.insertBidNoti(bidNotiData);
		} else {
			bidNoticeRepository.updateBidNoti(bidNotiData);
		}

		// 삭제
		this.deleteBidNotiVendor(deleteVendors);
		// 추가업체
		this.insertBidNotiVendor(bidNotiData, insertVendors);
		// 수정
		this.updateBidNotiVendor(updateVendors);

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("rfx_prentc_uuid", bidNotiData.get("rfx_prentc_uuid"));
		resultDataMap.put("rfx_prentc_no", bidNotiData.get("rfx_prentc_no"));
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * bid noti vendor 수정한다.
	 *
	 * @param updateVendor the update vendor
	 * @Date : 2021. 5. 14
	 * @Method Name : updateBidNotiVendor
	 */
	private void updateBidNotiVendor(List<Map> bidNotiVendors) {
		if(bidNotiVendors == null) {
			return;
		}
		if(bidNotiVendors.isEmpty()) {
			return;
		}

		for(Map bidNotiVendor : bidNotiVendors) {
			this.updateBidNotiVendor(bidNotiVendor);
		}
	}

	private void updateBidNotiVendor(Map bidNotiVendor) {
		if(bidNotiVendor == null) {
			return;
		}

		bidNoticeRepository.updateBidNotiVendor(bidNotiVendor);
	}

	/**
	 * bid noti vendor 입력한다.
	 *
	 * @param insertVendor the insert vendor
	 * @Date : 2021. 5. 14
	 * @Method Name : insertBidNotiVendor
	 */
	private void insertBidNotiVendor(Map bidNotiData, List<Map> bidNotiVendors) {
		if(bidNotiData == null) {
			return;
		}
		if(bidNotiVendors == null) {
			return;
		}
		if(bidNotiVendors.isEmpty()) {
			return;
		}

		for(Map bidNotiVendor : bidNotiVendors) {
			this.insertBidNotiVendor(bidNotiData, bidNotiVendor);
		}
	}


	private void insertBidNotiVendor(Map bidNotiData, Map bidNotiVendor) {
		if(bidNotiData == null) {
			return;
		}
		if(bidNotiVendor == null) {
			return;
		}

		bidNotiVendor.put("rfx_prentc_uuid", bidNotiData.get("rfx_prentc_uuid"));
		bidNotiVendor.put("rfx_prentc_no", bidNotiData.get("rfx_prentc_no"));
		bidNotiVendor.put("rfx_prentc_rnd", bidNotiData.get("rfx_prentc_rnd"));

		bidNoticeRepository.insertBidNotiVendor(bidNotiVendor);
	}

	private void deleteBidNotiVendor(List<Map> bidNotiVendors) {
		if(bidNotiVendors == null) {
			return;
		}
		if(bidNotiVendors.isEmpty()) {
			return;
		}

		for(Map bidNotiVendor : bidNotiVendors) {
			this.deleteBidNotiVendor(bidNotiVendor);
		}
	}

	private void deleteBidNotiVendor(Map bidNotiVendor) {
		if(bidNotiVendor == null) {
			return;
		}

		bidNoticeRepository.deleteBidNotiVendor(bidNotiVendor);
	}

	/**
	 * bid noti 삭제한다.
	 *
	 * @param bidNoti the delete bid noti
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : deleteBidNoti
	 */
	public ResultMap deleteBidNoti(Map bidNoti) {
		if(bidNoti == null) {
			return ResultMap.NOT_EXISTS();
		}

		// validate
		ResultMap validator = bidNoticeValidator.validate(bidNoti);
		if(!validator.isSuccess()) {
			return validator;
		}

		bidNoticeRepository.deleteBidNotiVendorsByBidNoti(bidNoti);
		bidNoticeRepository.deleteBidNoti(bidNoti);
		return ResultMap.SUCCESS();
	}


	/**
	 * Copy bid noti.
	 *
	 * @param sourceBidNotiData the source bid noti data
	 * @return the map
	 */
	public ResultMap copyBidNoti(Map sourceBidNotiData) {
		Map bidNotiInfo = this.findBidNoti(sourceBidNotiData);
		if(bidNotiInfo == null) {
			return ResultMap.NOT_EXISTS();
		}

		String copyName = messageUtil.getCodeMessage("STD.N2101",
				bidNotiInfo.get("rfx_prentc_tit"),
				"복사본_{0}",
				LocaleContextHolder.getLocale());

		bidNotiInfo.put("rfx_prentc_uuid", "");
		bidNotiInfo.put("rfx_prentc_no", "");
		bidNotiInfo.put("rfx_prentc_rnd", 1);
		bidNotiInfo.put("prev_rfx_prentc_uuid", "");
		bidNotiInfo.put("rfx_prentc_tit", copyName);
		bidNotiInfo.put("rfx_prentc_res_sts_ccd", "");
		bidNotiInfo.put("prev_rfx_prentc_res_sts_ccd", "");
		bidNotiInfo.put("rfx_prentc_crn_typ_ccd", "NEW_PRENTC");

		// 내부용 첨부파일 복사
		bidNotiInfo.put("buyer_athg_uuid", stdFileService.copyFile((String) bidNotiInfo.get("buyer_athg_uuid")));
		// 협력사용 첨부파일 복사
		bidNotiInfo.put("vd_athg_uuid", stdFileService.copyFile((String) bidNotiInfo.get("vd_athg_uuid")));

		List<Map> bidNotiVendors = this.findListBidNotiVendor(sourceBidNotiData);
		for(Map bidNotiVendor : bidNotiVendors) {
			bidNotiVendor.put("rfx_prentc_uuid", null);
			bidNotiVendor.put("rfx_prentc_no", null);
			bidNotiVendor.put("rfx_prentc_rnd", 1);
		}

		Map saveParam = Maps.newHashMap();
		saveParam.put("bidNotiData", bidNotiInfo);
		saveParam.put("insertBidNotiVendors", bidNotiVendors);
		// 복사 데이터 저장
		return this.saveDraftBidNoti(saveParam);
	}

	/**
	 * Regedit bid noti.
	 *
	 * @param param the param
	 */
	public void regeditBidNoti(Map param) {
		// 스케쥴러 등록 -  공고 처리
		Map bidNotiInfo = this.findBidNoti(param);
		//등록 시간과 공고일시 체크
		Date today = new Date();
		Date notiStartDt = (Date) bidNotiInfo.get("rfx_prentc_st_dttm");
		Date notiCloseDt = (Date) bidNotiInfo.get("rfx_prentc_clsg_dttm");

		//정정공고건인경우 이전 공고를 공고 취소
		if(NOTI_TYP_A.equals(bidNotiInfo.get("rfx_prentc_crn_typ_ccd"))) {
			rfxStatusService.correctPrevBidNoti(bidNotiInfo);
		}

		bidNoticeSchedulerService.removeSchduler(bidNotiInfo);
		//공고마감일시가 현재시간보다 작은경우 E: 마감
		if(notiCloseDt.before(today)) {
			rfxStatusService.closeBidNoti(bidNotiInfo);
		} else {
			//현재시간보다 공고일시가 큰 경우 W : 공고대기
			if(notiStartDt.after(today)) {
				rfxStatusService.waitBidNoti(bidNotiInfo);
				//시작,종료스케줄러 //공고메일발송
				bidNoticeSchedulerService.registStartScheduler(bidNotiInfo);
				bidNoticeSchedulerService.registCloseScheduler(bidNotiInfo);
			} else {
				// 현재시간 이전에 공고일시가 이미진행중인 경우 P : 9공고진행
				rfxStatusService.startBidNotiProg(bidNotiInfo);
				// 지명 공고메일발송
				mailService.sendAsync("BID_NOTI_MAIL", (String) bidNotiInfo.get("rfx_prentc_uuid"));
				// 종료스케줄러
				bidNoticeSchedulerService.registCloseScheduler(bidNotiInfo);
			}
		}
	}

	/**
	 * Start bid noti.
	 *
	 * @param param the param
	 */
	public void startBidNoti(HashMap<String, Object> param) {
		Map bidNotiInfo = findBidNoti(param);
		if(bidNotiInfo == null) {
			return;
		}

		// 상태처리
		rfxStatusService.startBidNotiProg(bidNotiInfo);
		// 지명 공고메일발송
		mailService.sendAsync("BID_NOTI_MAIL", (String) bidNotiInfo.get("rfx_prentc_uuid"));
	}

	/**
	 * Close bid noti.
	 *
	 * @param param the param
	 */
	public void closeBidNoti(HashMap<String, Object> param) {
		Map bidNotiInfo = this.findBidNoti(param);
		if(bidNotiInfo == null) {
			return;
		}

		//상태처리
		rfxStatusService.closeBidNotiProg(bidNotiInfo);
	}

	/**
	 * Cancel bid noti.
	 *
	 * @param bidNotiData the bid noti data
	 * @return the map
	 */
	public ResultMap cancelBidNoti(Map bidNotiData) {
		if(bidNotiData == null) {
			return ResultMap.NOT_EXISTS();
		}

		ResultMap validator = bidNoticeValidator.validate(bidNotiData);
		if(!validator.isSuccess()) {
			return validator;
		}

		bidNoticeRepository.updateBidNotiCancelCause(bidNotiData);
		rfxStatusService.cancelBidNoti(bidNotiData);
		// 스케줄러삭제
		bidNoticeSchedulerService.removeSchduler(bidNotiData);
		return ResultMap.SUCCESS();
	}

	/**
	 * bid noti change info 조회한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : findBidNotiChangeInfo
	 */
	public Map findBidNotiChangeInfo(Map param) {
		Map resultMap = Maps.newHashMap();
		Map history = Maps.newHashMap();

		if(Strings.isNullOrEmpty((String) param.get("rfx_prentc_dttm_chg_uuid"))) {
			//작성중인 변경건
			Map lastBidNoti = bidNoticeRepository.findLastBidNotiChangeInfo(param);
			if(lastBidNoti != null) {
				// 승인건은 버전업
				if("APVD".equals(lastBidNoti.get("apvl_sts_ccd")) || "APVL_PRGSG".equals(lastBidNoti.get("apvl_sts_ccd"))) {
					int modRev = Integer.parseInt(lastBidNoti.get("rfx_prentc_dttm_chg_revno").toString()); // 차수
					modRev++;
					resultMap.put("rfx_prentc_dttm_chg_revno", modRev);
				} else {
					history = lastBidNoti;
				}
			}
		} else {
			//일반적인 조회
			history = bidNoticeRepository.findBidNotiChangeInfo(param);
		}

		if(history.isEmpty()) {
			resultMap.putAll(this.findBidNoti(param));
		} else {
			resultMap = history;
		}
		return resultMap;
	}


	/**
	 * bid noti time change 저장한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 18
	 * @Method Name : saveBidNotiTimeChange
	 */
	public ResultMap saveBidNotiTimeChange(Map param) {
		Map bidNotiData = (Map) param.get("bidNotiData");
		if(bidNotiData == null) {
			return ResultMap.NOT_EXISTS();
		}

		int modRev = 1;
		if(bidNotiData.get("rfx_prentc_dttm_chg_revno") != null) {
			modRev = Integer.parseInt(bidNotiData.get("rfx_prentc_dttm_chg_revno").toString());
		}

		if(Strings.isNullOrEmpty((String) bidNotiData.get("rfx_prentc_dttm_chg_uuid"))) {
			//신규 id생성
			bidNotiData.put("rfx_prentc_dttm_chg_uuid", UUID.randomUUID().toString());
			bidNotiData.put("rfx_prentc_dttm_chg_revno", modRev);

			bidNoticeRepository.insertBidNotiTimeChange(bidNotiData);
		} else {
			bidNoticeRepository.updateBidNotiTimeChange(bidNotiData);
		}
		return ResultMap.SUCCESS(bidNotiData);
	}

	/**
	 * bid noti time change 임시 저장한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 18
	 * @Method Name : saveDraftBidNotiTimeChange
	 */
	public ResultMap saveDraftBidNotiTimeChange(Map param) {
		Map bidNotiData = (Map) param.get("bidNotiData");
		ResultMap validator = bidNoticeValidator.validate(bidNotiData);
		if(!validator.isSuccess()) {
			return validator;
		}

		ResultMap resultMap = this.saveBidNotiTimeChange(param);
		if(resultMap.isSuccess()) {
			//상태처리
			Map resultData = resultMap.getResultData();
			rfxStatusService.saveDraftBidNotiTimeChange(resultData);
		}
		return resultMap;
	}

	/**
	 * Bypass bid noti time change.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap bypassBidNotiTimeChange(Map param) {
		Map bidNotiData = (Map) param.get("bidNotiData");

		ResultMap validator = bidNoticeValidator.validate(bidNotiData);
		if(!validator.isSuccess()) {
			return validator;
		}

		ResultMap resultMap = this.saveBidNotiTimeChange(param);
		if(resultMap.isSuccess()) {
			//상태처리
			Map resultData = resultMap.getResultData();
			rfxStatusService.passBidNotiTimeChange(resultData);
			bidNoticeRepository.changeBidNotiTime(resultData);

			// 지명 공고 마감일시 변경
			mailService.sendAsync("BID_NOTI_TIME_CHANGE", (String) bidNotiData.get("rfx_prentc_uuid"));

			//스케줄러등록
			this.regeditBidNoti(bidNotiData);
		}
		return resultMap;
	}

	/**
	 * draft BN time change 삭제한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 18
	 * @Method Name : deleteDraftBNTimeChange
	 */
	public ResultMap deleteDraftBNTimeChange(Map param) {
		ResultMap validator = bidNoticeValidator.validate(param);
		if(!validator.isSuccess()) {
			return validator;
		}

		bidNoticeRepository.deleteDraftBNTimeChange(param);
		return ResultMap.SUCCESS();
	}


	/**
	 * list bid noti time change 조회한다.
	 *
	 * @param param the param
	 * @return the floater stream
	 * @Date : 2021. 5. 18
	 * @Method Name : findListBidNotiTimeChange
	 */
	public FloaterStream findListBidNotiTimeChange(Map param) {
		return bidNoticeRepository.findListBidNotiTimeChange(param);
	}

	public FloaterStream findListBidNotiComplete(Map param) {
		return bidNoticeRepository.findListBidNotiComplete(param);
	}

	public List<Map> findListBidNotiCompleteVdList(Map param) {
		return bidNoticeRepository.findListBidNotiCompleteVdList(param);
	}

	/**
	 * courrect bid noti 저장한다.
	 *
	 * @param sourceBidNotiData the source bid noti data
	 * @return the map< string, object>
	 * @Date : 2021. 6. 8
	 * @Method Name : saveCorrectBidNoti
	 */
	private ResultMap saveCorrectBidNoti(Map sourceBidNotiData) {
		Map bidNotiInfo = this.findBidNoti(sourceBidNotiData);
		if(bidNotiInfo == null) {
			return ResultMap.NOT_EXISTS();
		}

		int notiRev = 1;
		if(bidNotiInfo.get("rfx_prentc_rnd") != null) {
			notiRev = Integer.parseInt(bidNotiInfo.get("rfx_prentc_rnd").toString());
		}

		String copyName = messageUtil.getCodeMessage("STD.BN0023",
				bidNotiInfo.get("rfx_prentc_tit"),
				"[정정공고]_{0}",
				LocaleContextHolder.getLocale());

		bidNotiInfo.put("pre_rfx_prentc_uuid", bidNotiInfo.get("rfx_prentc_uuid"));
		bidNotiInfo.put("pre_rfx_prentc_res_sts_ccd", bidNotiInfo.get("rfx_prentc_res_sts_ccd"));
		String notiNo = bidNotiInfo.get("rfx_prentc_no") == null ? "" : bidNotiInfo.get("rfx_prentc_no").toString();
		bidNotiInfo.put("pre_rfx_prentc_no", notiNo);
		bidNotiInfo.put("rfx_prentc_uuid", null);
		bidNotiInfo.put("rfx_prentc_rnd", notiRev + 1);
		bidNotiInfo.put("rfx_prentc_tit", copyName);
		bidNotiInfo.put("rfx_prentc_res_sts_ccd", "");
		bidNotiInfo.put("rfx_prentc_crn_typ_ccd", NOTI_TYP_A);

		// 내부용 첨부파일 복사
		bidNotiInfo.put("buyer_athg_uuid", stdFileService.copyFile((String) bidNotiInfo.get("buyer_athg_uuid")));
		// 협력사용 첨부파일 복사
		bidNotiInfo.put("vd_athg_uuid", stdFileService.copyFile((String) bidNotiInfo.get("vd_athg_uuid")));

		List<Map> bidNotiVendors = this.findListBidNotiVendor(sourceBidNotiData);
		for(Map bidNotiVendor : bidNotiVendors) {
			bidNotiVendor.put("rfx_prentc_uuid", null);
			bidNotiVendor.put("rfx_prentc_no", null);
			bidNotiVendor.put("rfx_prentc_rnd", notiRev + 1);
		}

		Map saveParam = Maps.newHashMap();
		saveParam.put("bidNotiData", bidNotiInfo);
		saveParam.put("insertBidNotiVendors", bidNotiVendors);
		// 복사 데이터 저장
		return this.saveDraftBidNoti(saveParam);
	}

	/**
	 * Check Correct bid noti.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap checkCorrectBidNoti(Map param) {
		if(this.checkInfoBidNoti(param)) {
			return ResultMap.DUPLICATED();
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * Check Correct bid noti.
	 *
	 * @param param the param
	 * @return the map
	 */
	public boolean checkInfoBidNoti(Map param) {
		List bidList = bidNoticeRepository.findListPreBidNoti(param);
		return bidList.size() > 0;
	}

	/**
	 * Correct bid noti.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap correctBidNoti(Map param) {
		Map bidNotiData = (Map) param.get("bidNotiData");

		ResultMap validator = bidNoticeValidator.validate(bidNotiData);
		if(!validator.isSuccess()) {
			return validator;
		}

		// 마감인지 상태체크
		if("CLSG".equals(bidNotiData.get("rfx_prentc_sts_ccd"))) {
			// 마감인경우 정정공고 불가
			return ResultMap.builder()
					.resultStatus(NOTI_PROG_STS_E)
					.build();
		}
		return this.saveCorrectBidNoti(bidNotiData);
	}

	/**
	 * Re new bid notice.
	 *
	 * @param bidNotiData the bid noti data
	 * @return the map
	 */
	public ResultMap reNewBidNotice(Map bidNotiData) {
		ResultMap validator = bidNoticeValidator.validate(bidNotiData);
		if(!validator.isSuccess()) {
			return validator;
		}

		//마감인지 상태체크
		//마감일 경우 재공고로 수정
		if(!"CLSG".equals((String) bidNotiData.get("rfx_prentc_sts_ccd"))) {
			return ResultMap.builder()
					.resultStatus(NOTI_PROG_STS_E)
					.build();
		}

		ResultMap resultMap = this.saveReBidNoti(bidNotiData);
		rfxStatusService.progressReBidNoti(bidNotiData);
		return resultMap;
	}

	/**
	 * re bid noti 저장한다.
	 *
	 * @param sourceBidNotiData the source bid noti data
	 * @return the map< string, object>
	 * @Date : 2021. 6. 18
	 * @Method Name : saveReBidNoti
	 */
	private ResultMap saveReBidNoti(Map sourceBidNotiData) {
		Map bidNotiInfo = this.findBidNoti(sourceBidNotiData);
		if(bidNotiInfo == null) {
			return ResultMap.NOT_EXISTS();
		}

		String copyName = messageUtil.getCodeMessage("STD.BN0025",
				bidNotiInfo.get("rfx_prentc_tit"),
				"재공고_{0}",
				LocaleContextHolder.getLocale());

		bidNotiInfo.put("pre_rfx_prentc_uuid", bidNotiInfo.get("rfx_prentc_uuid"));
		bidNotiInfo.put("pre_rfx_prentc_res_sts_ccd", bidNotiInfo.get("rfx_prentc_res_sts_ccd"));
		bidNotiInfo.put("pre_rfx_prentc_no", bidNotiInfo.get("rfx_prentc_no"));

		bidNotiInfo.put("rfx_prentc_uuid", "");
		bidNotiInfo.put("rfx_prentc_no", "");
		bidNotiInfo.put("rfx_prentc_rnd", 1);
		bidNotiInfo.put("prev_rfx_prentc_uuid", "");
		bidNotiInfo.put("rfx_prentc_tit", copyName);
		bidNotiInfo.put("rfx_prentc_res_sts_ccd", "");
		bidNotiInfo.put("prev_rfx_prentc_res_sts_ccd", "");
		bidNotiInfo.put("rfx_prentc_crn_typ_ccd", NOTI_TYP_R);    //재공고

		// 내부용 첨부파일 복사
		bidNotiInfo.put("buyer_athg_uuid", stdFileService.copyFile((String) bidNotiInfo.get("buyer_athg_uuid")));
		// 협력사용 첨부파일 복사
		bidNotiInfo.put("vd_athg_uuid", stdFileService.copyFile((String) bidNotiInfo.get("vd_athg_uuid")));

		List<Map> bidNotiVendors = this.findListBidNotiVendor(sourceBidNotiData);
		for(Map bidNotiVendor : bidNotiVendors) {
			bidNotiVendor.put("rfx_prentc_uuid", null);
			bidNotiVendor.put("rfx_prentc_no", null);
			bidNotiVendor.put("rfx_prentc_rnd", 1);
		}

		Map saveParam = Maps.newHashMap();
		saveParam.put("bidNotiData", bidNotiInfo);
		saveParam.put("insertBidNotiVendors", bidNotiVendors);
		// 복사 데이터 저장
		return this.saveDraftBidNoti(saveParam);
	}

	/**
	 * byPassCloseBidNotices
	 *
	 * @param param
	 * @return
	 */
	public ResultMap byPassCloseBidNotices(Map param){
		List bidNotiDatas = (List)param.get("bidNotiDatas");

		for(Map bidNotiData : (List<Map>) bidNotiDatas) {
			ResultMap validator = bidNoticeValidator.validate(bidNotiData);
			if(!validator.isSuccess()) {
				return validator;
			}

			ResultMap bidNotiInfo = this.byPassCloseBidNotice(bidNotiData);

			if(!bidNotiInfo.isSuccess()) {
				return ResultMap.builder()
						.resultStatus(ResultMap.STATUS.INVALID_STATUS_ERR)
						.resultData(param)
						.build();
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * byPassCloseBidNotice
	 *
	 * @param param
	 * @return
	 */
	private ResultMap byPassCloseBidNotice(Map param) {
		Map bidNoticeData = this.findBidNoti(param);

		if(bidNoticeData == null) {
			return ResultMap.builder()
					.resultStatus(ResultMap.STATUS.NOT_EXIST)
					.resultData(param)
					.build();
		}

		bidNoticeData.put("rfx_prentc_ery_clsg_rsn", param.get("rfx_prentc_ery_clsg_rsn"));
		this.closebidNotice(bidNoticeData);

		return ResultMap.SUCCESS(bidNoticeData);
	}

	/**
	 * closeBidNotice
	 *
	 * @param param
	 */
	public void closebidNotice(Map<String, Object> param) {
		Map bidNotiData = Maps.newHashMap();
		bidNotiData = param;

		if(bidNotiData != null) {
			//마감처리
			Map bidNotiInfo = Maps.newHashMap();
			String rfxPrentcUuid = (String) bidNotiData.get("rfx_prentc_uuid");
			bidNotiInfo.put("rfx_prentc_uuid", rfxPrentcUuid);
			bidNoticeSchedulerService.removeSchduler(bidNotiInfo);

			Date ClsgDttm = new Date();
			bidNotiData.put("rfx_prentc_clsg_dttm", ClsgDttm);

			bidNoticeRepository.updateCloseBidNotice(bidNotiData);          //마감일시 업데이트
			bidNoticeRepository.updateCloseBidNoticeCause(bidNotiData);     //마감사유 및 상태 업데이트

			this.saveBidNoticeHistory(bidNotiData);
		}
	}

	/**
	 * saveBidNoticeHistory
	 *
	 * @param param
	 * @return
	 */
	public ResultMap saveBidNoticeHistory(Map param){
		Map bidNotiData = Maps.newHashMap();
		bidNotiData = param;
		if(bidNotiData == null) {
			return ResultMap.NOT_EXISTS();
		}

		int modRev = 1;
		if(bidNotiData.get("rfx_prentc_dttm_chg_revno") != null) {
			modRev = Integer.parseInt(bidNotiData.get("rfx_prentc_dttm_chg_revno").toString());
		}

		if(Strings.isNullOrEmpty((String) bidNotiData.get("rfx_prentc_dttm_chg_uuid"))) {
			//신규 id생성
			bidNotiData.put("rfx_prentc_dttm_chg_uuid", UUID.randomUUID().toString());
			bidNotiData.put("rfx_prentc_dttm_chg_revno", modRev);
			bidNotiData.put("chg_rsn", bidNotiData.get("rfx_prentc_ery_clsg_rsn"));
			bidNotiData.put("mod_rfx_prentc_st_dttm", bidNotiData.get("rfx_prentc_st_dttm"));
			bidNotiData.put("mod_rfx_prentc_clsg_dttm", bidNotiData.get("rfx_prentc_clsg_dttm"));

			bidNoticeRepository.insertBidNotiTimeChange(bidNotiData);
		}
		return ResultMap.SUCCESS(bidNotiData);
	}

}
