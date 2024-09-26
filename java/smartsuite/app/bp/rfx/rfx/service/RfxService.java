package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.mailWork.MailWorkService;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.approval.service.ApprovalService;
import smartsuite.app.bp.rfx.receipt.service.RfxReceiptService;
import smartsuite.app.bp.rfx.rfi.service.RfiService;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.bp.rfx.rfx.factory.RfxFactory;
import smartsuite.app.bp.rfx.rfx.repository.RfxRepository;
import smartsuite.app.bp.rfx.rfx.scheduler.RfxJobConst;
import smartsuite.app.bp.rfx.rfx.scheduler.RfxSchedulerService;
import smartsuite.app.bp.rfx.rfx.strategy.RfxStrategy;
import smartsuite.app.bp.rfx.rfx.validator.RfxValidator;
import smartsuite.app.bp.rfx.rfxpreinsp.service.RfxPreInspService;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.message.MessageUtil;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.CountryLanguage;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.data.FloaterStream;
import smartsuite.module.ModuleManager;
import smartsuite.security.authentication.Auth;
import smartsuite.security.core.crypto.AESCipherUtil;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Rfx 관련 처리하는 서비스 Class입니다.
 *
 * @FileName RfxService.java
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxService {
	
	@Inject
	RfxRepository rfxRepository;
	
	@Inject
	RfxItemService rfxItemService;
	
	@Inject
	RfxVendorService rfxVendorService;

	@Inject
	RfxVendorRcmdService rfxVendorRcmdService;
	
	@Inject
	RfxItemPriceFactorService rfxItemPriceFactorService;
	
	@Inject
	RfxBidService rfxBidService;

	@Inject
	RfiService rfiService;
	
	@Inject
	RfxReceiptService rfxReceiptService;

	@Inject
	RfxPreInspService rfxPreInspService;
	
	@Inject
	RfxEventPublisher rfxEventPublisher;
	
	@Inject
	ApprovalService approvalService;
	
	@Inject
	MessageUtil messageUtil;
	
	/**
	 * The shared service.
	 */
	@Inject
	SharedService sharedService;
	
	/**
	 * The pro status processor.
	 */
	@Inject
	RfxStatusService rfxStatusService;
	
	/**
	 * The rfq schduler service.
	 */
	@Inject
	RfxSchedulerService rfxSchedulerService;
	
	/**
	 * The factory.
	 */
	@Inject
	RfxFactory factory;
	
	@Inject
	RfxEvalService rfxEvalService;
	
	@Inject
	StdFileService stdFileService;
	
	@Inject
	RfxValidator rfxValidator;
	
	@Inject
	MailWorkService mailWorkService;
	
	@Inject
	MailService mailService;
	
	@Inject
	RfxNxtPrcsService rfxNxtPrcsService;
	
	/**
	 * 견적 저장
	 *
	 * @param param - 견적 저장을 위한 데이터
	 * @return map - 저장 결과
	 */
	public ResultMap saveRfx(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		List<Map> deleteRfxItems = (List<Map>) param.get("deleteRfxItems");
		List<Map> deleteRfxVendors = (List<Map>) param.get("deleteRfxVendors");
		List<Map> insertRfxItems = (List<Map>) param.get("insertRfxItems");
		List<Map> updateRfxItems = (List<Map>) param.get("updateRfxItems");
		List<Map> insertRfxVendors = (List<Map>) param.get("insertRfxVendors");
		List<Map> updateRfxVendors = (List<Map>) param.get("updateRfxVendors");

		if(Strings.isNullOrEmpty((String) rfxData.get("rfx_uuid"))) {
			//신규 id생성
			rfxData.put("rfx_uuid", UUID.randomUUID().toString());
			
			// RFx No 채번
			if(Strings.isNullOrEmpty((String) rfxData.get("rfx_no"))) {
				String rfxNo = sharedService.generateDocumentNumber("RX");
				rfxData.put("rfx_no", rfxNo);
			}
			// 차수 최초 작성시 1번
			if(rfxData.get("rfx_rnd") == null) {
				rfxData.put("rfx_rnd", 1);
			}
			//최초 마감일시등록
			rfxData.put("rfx_chg_clsg_dttm", rfxData.get("rfx_clsg_dttm"));

			// 구매그룹 등록
			if(insertRfxItems != null && insertRfxItems.size() > 0) {
				rfxData.put("purc_grp_cd", insertRfxItems.get(0).get("purc_grp_cd"));
			} else if (updateRfxItems != null && updateRfxItems.size() > 0) {
				rfxData.put("purc_grp_cd", updateRfxItems.get(0).get("purc_grp_cd"));
			}
			rfxRepository.insertRfx(rfxData);

			if(rfxData.get("rfi_uuid") != null) {
				rfiService.updateRfiByRfxUuid(rfxData);
			}

			// 1차수 신규 RFX 생성 시 비가격평가 초기화
			rfxEvalService.initializedRfxNpeFact(rfxData);
		} else {
			rfxRepository.updateRfx(rfxData);
		}
		
		// 삭제
		rfxItemService.deleteRfxItem(rfxData, deleteRfxItems);
		rfxVendorService.deleteRfxVendor(deleteRfxVendors);
		
		// 추가
		rfxItemService.insertRfxItem(rfxData, insertRfxItems);
		rfxVendorService.insertRfxVendor(rfxData, insertRfxVendors);
		
		// 수정
		rfxItemService.updateRfxItem(updateRfxItems);
		rfxVendorService.updateRfxVendor(updateRfxVendors);
		
		// 원가구성항목설정여부가 "N"인 경우 원가구성설정여부 초기화
		rfxItemPriceFactorService.settingPriceFactorValueN(rfxData);
		
		//resultData 셋팅
		Map resultData = Maps.newHashMap();
		resultData.put("rfx_uuid", rfxData.get("rfx_uuid"));
		resultData.put("rfx_no", rfxData.get("rfx_no"));
		resultData.put("rfx_purp_ccd", rfxData.get("rfx_purp_ccd"));
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * RFx 저장
	 *
	 * @param param the param
	 * @return the map
	 * @author : Yeon-u Kim
	 * @Date : 2016. 5. 24
	 * @Method Name : saveDraftRfx
	 */
	public ResultMap saveDraftRfx(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		List<String> prItemIds = (List) param.get("prItemIds");
		List<String> upcrItemIds = (List) param.get("upcrItemIds");
		List<String> srItemIds = (List) param.get("srItemIds");

		if(rfxData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		ResultMap resultMap = this.saveRfx(param);
		if(resultMap.isSuccess()) {
			Map resultData = resultMap.getResultData();
			
			// 견적요청 진행상태를 임시저장으로 변경한다.
			rfxStatusService.saveDraftRfx(resultData);
		}
		
		return resultMap;
	}
	
	/**
	 * @param param
	 * @return
	 */
	public Map findRfx(Map param) {
		return rfxRepository.findRfx(param);
	}
	
	/**
	 * rfx data 조회한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : Yeon-u Kim
	 * @Date : 2016. 9. 30
	 * @Method Name : findRfxData
	 */
	public Map findRfxData(Map param) {
		Map result = Maps.newHashMap();
		result.put("rfxData", rfxRepository.findRfx(param));
		result.put("rfxItems", rfxItemService.searchRfxItemByRfx((Map) result.get("rfxData")));
		result.put("rfxVendors", rfxVendorService.searchRfxVendorByRfx((Map) result.get("rfxData")));
		return result;
	}
	
	public Map findRfxDefaultDataByReqItemIds(Map param) {
		if(param == null) {
			return null;
		}
		if(param.get("rfx_req_rcpt_uuids") == null) {
			return null;
		}
		
		List rfxItems = rfxReceiptService.findRfxDefaultDataByReqRcpts((List) param.get("rfx_req_rcpt_uuids"));
		if(rfxItems.isEmpty()) {
			return null;
		}
		
		int maxNo = 10;
		List<String> oorgCds = Lists.newArrayList();
		for(Map rfxItem : (List<Map>) rfxItems) {
			rfxItem.put("rfx_qty", rfxItem.get("req_qty"));
			rfxItem.put("rfx_req_uprc", rfxItem.get("req_uprc"));
			rfxItem.put("rfx_req_amt", rfxItem.get("req_amt"));
			rfxItem.put("rfx_lno", maxNo);
			maxNo += 10;
			
			oorgCds.add((String) rfxItem.get("oorg_cd"));
		}
		
		Map oorgParam = Maps.newHashMap();
		oorgParam.put("oorgCds", oorgCds);
		List<Map> orderOorgs = rfxRepository.findListOrderOperOrg(oorgParam);
		
		Map firstRfxItem = (Map) rfxItems.get(0);
		String oorgCd = null;
		if(orderOorgs.isEmpty()) {
			oorgCd = (String) firstRfxItem.get("oorg_cd");
		} else {
			oorgCd = (String) orderOorgs.get(0).get("oorg_cd");
		}
		
		Map rfxData = Maps.newHashMap();
		rfxData.put("rfx_rnd", 1);
		rfxData.put("oorg_cd", oorgCd);
		rfxData.put("purc_typ_ccd", firstRfxItem.get("purc_typ_ccd"));
		rfxData.put("cur_ccd", firstRfxItem.get("cur_ccd"));
		rfxData.put("rfx_purp_ccd", firstRfxItem.get("rfx_purp_ccd"));
		rfxData.put("purc_grp_cd", firstRfxItem.get("purc_grp_cd"));
		
		Map userInfo = Auth.getCurrentUserInfo();
		rfxData.put("purc_pic_id", userInfo.get("usr_id"));
		rfxData.put("purc_chr_nm", userInfo.get("disp_usr_nm"));
		rfxData.put("purc_chr_dept_nm", userInfo.get("disp_dept_nm"));
		rfxData.put("purc_chr_phone_no", userInfo.get("phone_no"));
		
		if("PR".equals(firstRfxItem.get("req_doc_typ_ccd"))) {
			rfxData.put("has_pr_item_yn", "Y");
			rfxData.put("has_upcr_item_yn", "N");
		} else if("UPCR".equals(firstRfxItem.get("req_doc_typ_ccd"))) {
			rfxData.put("has_pr_item_yn", "N");
			rfxData.put("has_upcr_item_yn", "Y");
		}
		
		rfxData.put("has_no_cd_item", this.hasNoCdItems(rfxItems) ? "Y" : "N");
		rfxData.put("rfxItems", rfxItems);
		return rfxData;
	}
	
	private Boolean hasNoCdItems(List rfxItems){
		boolean hasNoCdItem = false;
		for(Map rfxItem : (List<Map>) rfxItems) {
			if("CDLS".equals(rfxItem.get("item_cd_crn_typ_ccd"))) {
				hasNoCdItem = true;
				break;
			}
		}
		return hasNoCdItem;
	}

	public Map findRfxDefaultDataByAiItemIds(Map param) {
		if(param == null) {
			return null;
		}
		if(param.get("item_cds") == null) {
			return null;
		}
		
		List rfxItems = rfxItemService.findListRfxItemDefaultDataByAiItemIds(param);
		if(rfxItems.isEmpty()) {
			return null;
		}
		
		Map rfxData = Maps.newHashMap();
		rfxData.put("rfx_rnd", 1);
		rfxData.put("oorg_cd", param.get("oorg_cd"));
		rfxData.put("rfx_purp_ccd", "UPRCCNTR_SGNG");
		rfxData.put("has_pr_item_yn", "N");
		rfxData.put("has_upcr_item_yn", "N");
		rfxData.put("has_no_cd_item", "N");
		rfxData.put("rfxItems", rfxItems);
		return rfxData;
	}
	
	/**
	 * list rfx 삭제한다.
	 *
	 * @param saveParam the save param
	 * @return the map
	 * @author : Yeon-u Kim
	 * @Date : 2016. 5. 11
	 * @Method Name : deleteListRfx
	 */
	public ResultMap deleteListRfx(Map saveParam) {
		List invalidRfxs = Lists.newArrayList();
		List notExistRfxs = Lists.newArrayList();
		
		List deleteRfxs = (List) saveParam.get("deleteRfxs");
		for(Map rfx : (List<Map>) deleteRfxs) {
			ResultMap result = this.deleteRfx(rfx);
			
			if(Const.INVALID_STATUS_ERR.equals(result.getResultStatus())) {
				invalidRfxs.add(result.getResultData());
			} else if(Const.NOT_EXIST.equals(result.getResultStatus())) {
				notExistRfxs.add(rfx);
			}
		}
		
		ResultMap validator = ValidatorUtil.setupDataListForValidationDataList(deleteRfxs, invalidRfxs, notExistRfxs);
		return validator;
	}
	
	/**
	 * rfx 삭제한다.
	 *
	 * @param deleteRfx the save param
	 * @return the map
	 * @author : Yeon-u Kim
	 * @Date : 2016. 5. 12
	 * @Method Name : deleteRfx
	 */
	public ResultMap deleteRfx(Map deleteRfx) {
		ResultMap validator = rfxValidator.existRfxId(deleteRfx);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		this.deleteRfxProcess(deleteRfx);
		return ResultMap.SUCCESS();
	}
	
	protected void deleteRfxProcess(Map deleteRfx) {
		Map rfxData = rfxRepository.findRfx(deleteRfx);
		rfxEvalService.deleteRfxNpeFact(rfxData);
		rfxItemService.deleteRfxItemByRfx(rfxData);
		rfxVendorService.deleteRfxVendorByRfx(rfxData);
		rfxRepository.deleteRfx(rfxData);
		rfxStatusService.deleteRfx(rfxData);

		if(rfiService.checkRfiData(deleteRfx)) {
			rfiService.updateRfiByRfxUuid(rfxData);
		}
	}
	
	/**
	 * 결재없이 업체전송
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap directRfxBid(Map<String, Object> param) {
		//결재없이 진행된 경우 - 진행상태:공고중, 결재상태:승인
		rfxStatusService.bypassApprovalRfxProg(param);
		
		// 스케쥴러 등록 - RFx 공고
		Map rfxInfo = rfxRepository.findRfx(param);
		
		rfxSchedulerService.noticeRfx(rfxInfo);
        // 신규업체 메일발송 로직 추가
        this.inviteVendorForRfx(rfxInfo);
		// 즉시 시작인 경우 바로 시작 로직 수행
		if("Y".equals(rfxInfo.get("immed_st_use_yn"))) {
			HashMap data = Maps.newHashMap();
			data.put("rfx_uuid", rfxInfo.get("rfx_uuid"));
			data.put("rfx_typ_ccd", rfxInfo.get("rfx_typ_ccd"));
			this.startRfx(data);
		}
		
		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("rfx_uuid", rfxInfo.get("rfx_uuid"));
		
		return ResultMap.SUCCESS(resultDataMap);
	}
    
    /**
     * 신규 업체 초대
     *
     * @param rfxInfo
     */
    public void inviteVendorForRfx(Map rfxInfo) {
        // 신규 업체 초대 저장 로직 추가
        saveRfxInviteVendors(rfxInfo);
        //초대 대상 업체 조회
        List<Map> vendors = rfxVendorService.searchRfxInviteNewVendor(rfxInfo);
        
        //메일발송
        for(Map vendor : vendors) {
            vendor.put("link", getEncodeQueryString(vendor, "spBidRfx", "rfx_vd_uuid"));
            sendMailForRfxInviteNewVendor(vendor);
        }
    }
    
    /**
     * rfx vendor 기준 신규 협력사 초청 리스트 저장
     * @param rfxInfo
     */
    public void saveRfxInviteVendors(Map rfxInfo) {
        rfxVendorService.saveInviteNewVendors(rfxVendorService.searchRfxNewVendor(rfxInfo));
    }
    
    /**
     * 신규 협력사 rfx를 위한 초대 메일 발송
     *
     * @param vendor
     */
    public void sendMailForRfxInviteNewVendor(Map vendor) {
        mailService.sendAsync(CountryLanguage.getLocaleByCountryCode((String) vendor.get("ctry_ccd")), "RFX_INVITE_NEW_VENDOR", null, vendor);
    }
    
    /**
     * 신규 업체 메일 발송 query Parameter Setting
     *
     * @param data
     * @param taskKey
     * @param taskUuid
     * @return
     */
    private String getEncodeQueryString(Map data, String taskKey, String taskUuid) {
        StringBuffer queryString = new StringBuffer();
        
        queryString.append("ctry=").append(data.get("ctry_ccd")).append("&data=");
        
        StringBuffer sb = new StringBuffer();
        sb.append("task_key=").append(taskKey);
        sb.append("&task_uuid=").append(data.get(taskUuid));
        
        try {
            AESCipherUtil aesCipherUtil = new AESCipherUtil();
            String encryptedData = aesCipherUtil.encrypt(sb.toString());
            queryString.append(URLEncoder.encode(encryptedData, StandardCharsets.UTF_8.toString()));
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding failed", e);
        }
        
        return queryString.toString();
    }
    
    /**
	 * RFX 시작<br>
	 * 스케줄러에서 수행
	 *
	 * @param param the param
	 */
	public void startRfx(HashMap<String, Object> param) {
		Map rfxInfo = this.findRfx(param);
		if(rfxInfo == null) {
			return;
		}
		
		if("Y".equals(rfxInfo.get("immed_st_use_yn"))) {
			// RFx 시작일시를 현재일시로 update
			updateRfxStartDt(rfxInfo);
		}
		
		String rfxTyp = (String) param.get("rfx_typ_ccd");
		RfxStrategy strategy = factory.getRfxStrategy(rfxTyp);
		if(strategy != null) {
			strategy.startRfx(param);
		}
	}
	
	/**
	 * 평가마감 스케쥴러 호출
	 *
	 * @param param
	 */
	public void endRfxEval(HashMap<String, Object> param) {
		// TODO 재설계
		// Eval 정보로 RFx 조회
		/*Map<String, Object> rfxData = rfxEvalService.findRfxByEval(param);
		
		if(rfxData != null) {
			// 모든 평가(ESREVMA)가 종료된 경우
			if("Y".equals((String) rfxData.get("all_eval_comp_yn"))) {
				// RFX 평가 상태 : '완료'로 update
				rfxStatusService.completeRfxEval(rfxData);
				
				// 즉시개찰인 경우
				if("Y".equals((String) rfxData.get("immed_open_use_yn"))) {
					// RFP 개찰 처리
					this.openRfx(rfxData);
				}
			}
		}*/
	}
	
	/**
	 * RFx 시작일시 수정
	 *
	 * @param param the param
	 */
	public void updateRfxStartDt(Map<String, Object> param) {
		Map updateRfx = Maps.newHashMap();
		updateRfx.put("rfx_uuid", param.get("rfx_uuid"));
		updateRfx.put("rfx_st_dttm", new Date());
		
		rfxRepository.updateStartDt(updateRfx);
	}
	
	/**
	 * RFX 마감 스케쥴러 수행 서비스
	 *
	 * @param param the param
	 */
	public void closeRfx(HashMap<String, Object> param) {
		Map rfxInfo = this.findRfx(param);
		
		if(rfxInfo == null) {
			return;
		}
		
		String autoExtYn = (String) rfxInfo.get("auto_ext_use_yn");
		int vdSubmitCnt = Integer.parseInt(rfxInfo.get("vd_subm_cnt").toString());
		int extCnt;
		if(rfxInfo.get("auto_ext_poss_wd") == null) {
			extCnt = 0;
		} else {
			extCnt = Integer.parseInt(rfxInfo.get("auto_ext_poss_wd").toString());
		}
		
		// 자동연장여부 : "Y", 제출업체 수 : 0 인 경우
		if("Y".equals(autoExtYn) && vdSubmitCnt == 0
				&& rfxInfo.get("auto_ext_tm") != null
				&& RfxJobConst.MAX_AUTO_EXT_COUNT > extCnt) {
			//자동연장 처리
			//자동연장 사용시 마감일시 변경처리
			Calendar cal = Calendar.getInstance();
			Date date = (Date) rfxInfo.get("rfx_clsg_dttm");
			cal.setTime(date);
			
			BigDecimal extHour = (BigDecimal) rfxInfo.get("auto_ext_tm");
			cal.add(Calendar.HOUR, extHour.intValue());
			Date newCloseDt = new Date(cal.getTimeInMillis());
			
			//마감 일시 변경
			Map option = Maps.newHashMap();
			option.put("auto_ext_poss_wd", extCnt + 1);
			this.updateCloseDate((String) rfxInfo.get("rfx_uuid"), newCloseDt, option);
		} else {
			String rfxTyp = (String) param.get("rfx_typ_ccd");
			RfxStrategy strategy = factory.getRfxStrategy(rfxTyp);
			if(strategy != null) {
				ResultMap closeRfxResultMap = strategy.closeRfx(rfxInfo);
				// 마감처리 후 후속조치가 있는 경우
				this.processCloseRfxResult(rfxInfo, closeRfxResultMap);
			}
		}
	}
	
	private void processCloseRfxResult(Map rfxInfo, ResultMap closeRfxResultMap) {
		// RFP인 경우 재견적 마감 시 비가격평가 복사 로직이 수행된 경우 자동 개찰을 수행한다.
		if("RFP".equals(rfxInfo.get("rfx_typ_ccd"))) {
			Map<String, Object> evalSubjMap = closeRfxResultMap.getResultData();
			// 모든 평가 대상의 모든 평가자 제출상태이므로 자동 개찰 수행
			if(evalSubjMap != null && (Boolean) evalSubjMap.get("complete_eval")) {
				this.bypassOpenRfx(rfxInfo);
			}
		}
	}
	
	/**
	 * 견적 마감일자를 변경한다<br><br>
	 *
	 * <b>Required:</b><br>
	 * rfxId<br>
	 * closeDt<br>
	 * <br>
	 * <b>Options:</b><br>
	 * option.auto_ext_poss_wd - 자동연장 횟수<br>
	 * option.open_dttm - 개찰일자<br>
	 * option.clsg_tm_adj_rsn - 마감시간 조정사유<br>
	 *
	 * @param rfxId   - 견적요청 ID
	 * @param closeDt - 변경할 마감일자
	 * @param option  - 추가 정보
	 * @return void
	 */
	private void updateCloseDate(String rfxId, Date closeDt, Map option) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", rfxId);
		param.put("rfx_clsg_dttm", closeDt);
		if(option != null) {
			param.putAll(option);
		}
		
		rfxRepository.updateCloseDt(param);
		
		//마감 스케줄러 변경
		rfxSchedulerService.changeRfxCloseDt(param);
	}
	
	/**
	 * Ranking 구하기
	 *
	 * @param rfxData
	 */
	public void computeRanking(Map<String, Object> rfxData) {
		RfxStrategy strategy = factory.getRfxStrategy((String) rfxData.get("rfx_typ_ccd"));
		if(strategy != null) {
			strategy.computeRanking(rfxData);
		}
	}
	
	/**
	 * 여러건 조기 마감.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap byPassCloseRfxs(Map param) {
		List failRfxList = Lists.newArrayList();
		List rfxDatas = (List) param.get("rfxDatas");
		
		for(Map rfxData : (List<Map>) rfxDatas) {
			ResultMap byPassResultMap = this.byPassCloseRfx(rfxData);
			if(!byPassResultMap.isSuccess()) {
				failRfxList.add(byPassResultMap);
			}
		}
		
		if(failRfxList.isEmpty()) {
			return ResultMap.SUCCESS();
		} else {
			return ResultMap.builder()
			                .resultStatus(RfxConst.RFX_CLOSE_BYPASS_ERR)
			                .resultList(failRfxList)
			                .build();
		}
	}
	
	/**
	 * 조기 마감
	 *
	 * @param param the param
	 * @return the map
	 */
	private ResultMap byPassCloseRfx(Map param) {
		Map rfxInfo = this.findRfx(param);
		if(rfxInfo == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		String rfxProgSts = (String) rfxInfo.get("rfx_sts_ccd");
		// 공고중 이거나 진행중인 상태만 마감처리
		List progStats = Lists.newArrayList("NTC_PRGSG", "PRGSG");
		if(!progStats.contains(rfxProgSts)) {
			return ResultMap.builder()
			                .resultStatus(RfxConst.RFX_END)
			                .resultData(rfxInfo)
			                .build();
		}
		
		String rfxId = (String) rfxInfo.get("rfx_uuid");
		String rfxTyp = (String) rfxInfo.get("rfx_typ_ccd");
		
		//조기 마감처리
		RfxStrategy strategy = factory.getRfxStrategy(rfxTyp);
		if(strategy != null) {
			rfxInfo.put("ery_clsg_rsn", (String) param.get("ery_clsg_rsn"));
			ResultMap closeRfxResultMap = strategy.closeRfx(rfxInfo);
			
			if(!closeRfxResultMap.isSuccess()) {
				return closeRfxResultMap;
			}
			
			//강제마감 처리 성공시에만
			Map scheduleParam = Maps.newHashMap();
			scheduleParam.put("rfx_uuid", rfxId);
			
			//강제마감 처리 시 마감 스케쥴러 등록 건 삭제 처리
			rfxSchedulerService.bypassCloseRfx(scheduleParam);
			
			Map saveParam = Maps.newHashMap();
			saveParam.put("rfx_uuid", rfxId);
			saveParam.put("rfx_clsg_dttm", new Date());    //현재시각을 마감처리시간으로 변경함.
			
			//마감시간 업데이트
			rfxRepository.updateCloseDt(saveParam);
			
			// 마감처리 후 후속조치가 있는 경우
			this.processCloseRfxResult(rfxInfo, closeRfxResultMap);

			// 견적결과 메일발송
			mailService.sendAsync("RFX_PASS_MAIL", rfxId);
			mailService.sendAsync("RFX_NOPASS_MAIL", rfxId);
		}
		return ResultMap.SUCCESS();
	}
	
	/**
	 * RFx 마감 일시 변경
	 *
	 * @param param the param
	 * @return the map
	 * @author : Yeon-u Kim
	 * @Date : 2016. 5. 20
	 * @Method Name : updateRfxCloseDt
	 */
	public ResultMap updateRfxCloseDt(Map param) {
		ResultMap resultMap;
		Map rfxInfo = this.findRfx(param);
		
		if(rfxInfo == null) {
			//RFX 삭제 또는 존재하지 않음
			return ResultMap.NOT_EXISTS();
		}
		
		String rfxProgSts = (String) rfxInfo.get("rfx_sts_ccd");
		
		// 공고중 이거나 진행중인 상태만 마감처리
		if("NTC_PRGSG".equals(rfxProgSts) || "PRGSG".equals(rfxProgSts)) {
			String rfxId = (String) param.get("rfx_uuid");
			Date rfxCloseDt = (Date) param.get("rfx_clsg_dttm");
			Map option = Maps.newHashMap();
			option.put("open_dttm", param.get("open_dttm"));
			option.put("clsg_tm_adj_rsn", param.get("clsg_tm_adj_rsn"));
			
			//마감 일시 변경
			this.updateCloseDate(rfxId, rfxCloseDt, option);
			
			resultMap = ResultMap.SUCCESS();
		} else {
			//RFX 마감
			resultMap = ResultMap.builder()
			                     .resultStatus(RfxConst.RFX_END)
			                     .resultData(rfxInfo)
			                     .build();
		}
		
		return resultMap;
	}
	
	/**
	 * RFx multi round 생성을 위한 데이터 조회
	 *
	 * @param prevRfx the param
	 * @return the map
	 * @author : Yeon-u Kim
	 * @Date : 2016. 9. 30
	 * @Method Name : findRfxDataByMultiRound
	 */
	public ResultMap findRfxDataByMultiRound(Map prevRfx) {
		Map rfxData = this.findRfx(prevRfx);
		
		Integer revision = rfxRepository.findRfxMaxRevision(rfxData);
		rfxData.put("rfx_uuid", null);
		rfxData.put("prev_rfx_uuid", prevRfx.get("rfx_uuid"));
		rfxData.put("rfx_rnd", revision + 1);
		rfxData.put("rfx_sts_ccd", "CRNG");
		rfxData.put("rfx_res_sts_ccd", null);
		rfxData.put("prev_rfx_end_sts", null);
		rfxData.put("rfx_apvl_sts_ccd", "CRNG");
		rfxData.put("rfx_slctn_apvl_sts_ccd", "CRNG");
		rfxData.put("sub_work_cd", "CRNG");
		
		//재견적 시 마감 일시 초기값 설정 (ZER-427)
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 7);
		cal.set(Calendar.HOUR_OF_DAY, 18);
		cal.set(Calendar.MINUTE, 0);
		rfxData.put("rfx_clsg_dttm", cal.getTime());
		
		if("Y".equals(rfxData.get("auto_ext_use_yn"))) {
			rfxData.put("auto_ext_poss_wd", 0);    //자동 연장 회수 초기화
		}
		
		// 내부용 첨부파일 복사
		String intnAttNo = (String) rfxData.get("buyer_athg_uuid");
		rfxData.put("buyer_athg_uuid", stdFileService.copyFile(intnAttNo));
		
		// 협력사용 첨부파일 복사
		String extnAttNo = (String) rfxData.get("vd_athg_uuid");
		rfxData.put("vd_athg_uuid", stdFileService.copyFile(extnAttNo));
		
		// multi round 생성 위한 기존 견적요청 품목 조회
		List rfxItems = rfxItemService.searchRfxItemByMultiRound((String) prevRfx.get("rfx_uuid"));
		
		// multi round 생성 위한 기존 견적대상업체 조회
		int vdSubmitCnt = Integer.parseInt(rfxData.get("vd_subm_cnt").toString());
		List rfxVendors = rfxVendorService.searchRfxVendorByMultiRound((String) prevRfx.get("rfx_uuid"), vdSubmitCnt);
		
		Map resultData = Maps.newHashMap();
		resultData.put("rfxData", rfxData);
		resultData.put("rfxItems", rfxItems);
		resultData.put("rfxVendors", rfxVendors);
		
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * Multi round rfx(재견적).
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap saveMultiRoundRfx(Map param) {
		// 기존 견적요청
		Map prevRfx = (Map) param.get("rfxData");
		
		// validate
		ResultMap validator = rfxValidator.existRfxId(prevRfx);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		ResultMap multiRoundResultMap = this.findRfxDataByMultiRound(prevRfx);
		if(!multiRoundResultMap.isSuccess()) {
			return ResultMap.FAIL();
		}
		
		Map rfxData = (Map) multiRoundResultMap.getResultData().get("rfxData");
		List rfxItems = (List) multiRoundResultMap.getResultData().get("rfxItems");
		List rfxVendors = (List) multiRoundResultMap.getResultData().get("rfxVendors");
		
		String prevRfxId = (String) prevRfx.get("rfx_uuid");
		
		// 재견적(기존에 있는 rfx 상태값 변경)
		rfxStatusService.multiRoundRfx(prevRfx, rfxItems);
		
		Map saveParam = Maps.newHashMap();
		saveParam.put("rfxData", rfxData);
		saveParam.put("insertRfxItems", rfxItems);
		saveParam.put("insertRfxVendors", rfxVendors);
		
		// 견적데이터 저장
		ResultMap saveRfxResultMap = this.saveRfx(saveParam);
		if(!saveRfxResultMap.isSuccess()) {
			return ResultMap.FAIL();
		}
		
		Map resultData = saveRfxResultMap.getResultData();
		String newRfxId = (String) resultData.get("rfx_uuid");
		String priceFactorSetYn = (String) rfxData.get("coststr_use_yn");
		
		// 원가구성항목 설정
		rfxItemPriceFactorService.copyPriceFactorByRfx(priceFactorSetYn, prevRfxId, newRfxId);
		// 종합평가인 경우 비가격평가 설정 복사
		rfxEvalService.copyRfxNpeSetupByRfx(prevRfxId, newRfxId);
		// 저장 진행상태 변경
		rfxStatusService.saveDraftRfx(resultData);
		
		return saveRfxResultMap;
	}
	
	/**
	 * Copy rfx.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap copyRfx(Map sourceRfxData) {
		if(sourceRfxData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map rfxInfo = this.findRfx(sourceRfxData);
		Map copyRfxInfo = this.getCopyRfxHeader(rfxInfo);
		List copyRfxItems = rfxItemService.getCopyRfxItems(sourceRfxData);
		List copyRfxVendors = rfxVendorService.getCopyRfxVendors(sourceRfxData);
		
		Map saveParam = Maps.newHashMap();
		saveParam.put("rfxData", copyRfxInfo);
		saveParam.put("insertRfxItems", copyRfxItems);
		saveParam.put("insertRfxVendors", copyRfxVendors);
		
		// 복사 데이터 저장
		ResultMap saveResultMap = this.saveDraftRfx(saveParam);
		if(!saveResultMap.isSuccess()) {
			return ResultMap.FAIL();
		}
		
		Map resultData = saveResultMap.getResultData();
		String sourceRfxId = (String) sourceRfxData.get("rfx_uuid");
		String newRfxId = (String) resultData.get("rfx_uuid");
		String priFactSetYn = (String) copyRfxInfo.get("coststr_use_yn");
		
		//원가구성항목 복사
		rfxItemPriceFactorService.copyPriceFactorByRfx(priFactSetYn, sourceRfxId, newRfxId);
		//종합평가 and SRM 모듈 존재 시
		if("COMPREVAL".equals(rfxInfo.get("slctn_typ_ccd")) && ModuleManager.exist("SRM")) {
			rfxEvalService.copyRfxNpeSetupByRfx(sourceRfxId, newRfxId);
		}
		
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * 견적요청 복사 시 헤더정보를 초기화하며 복사한다.<br><br>
	 *
	 * @param rfxInfo
	 * @return Map
	 */
	private Map getCopyRfxHeader(Map rfxInfo) {
		Map copyRfxInfo = Maps.newHashMap(rfxInfo);
		copyRfxInfo.put("rfx_uuid", "");
		copyRfxInfo.put("rfx_no", "");
		copyRfxInfo.put("rfx_rnd", 1);
		copyRfxInfo.put("prev_rfx_uuid", "");
		copyRfxInfo.put("rfx_tit", messageUtil.getCodeMessage("STD.N2101", copyRfxInfo.get("rfx_tit"), "복사본_{0}"));
		copyRfxInfo.put("rfx_res_sts_ccd", "");
		copyRfxInfo.put("prev_rfx_end_sts", "");
		copyRfxInfo.put("rfx_slctn_apvl_sts_ccd", "");
		
		// 내부용 첨부파일 복사
		copyRfxInfo.put("buyer_athg_uuid", stdFileService.copyFile((String) rfxInfo.get("buyer_athg_uuid")));
		// 협력사용 첨부파일 복사
		copyRfxInfo.put("vd_athg_uuid", stdFileService.copyFile((String) rfxInfo.get("vd_athg_uuid")));
		return copyRfxInfo;
	}
	
	/**
	 * RFx 정보 및 품목 목록(가격군 포함) 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 * @return
	 */
	public Map findRfxAndListRfxItemWithPriceGroup(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxData", this.findRfx(param));
		resultMap.put("rfxItems", rfxItemPriceFactorService.searchRfxItemWithPriceGroup(param));
		return resultMap;
	}
	
	/**
	 * 품목군 별 가격군 목록 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.oorg_cd - 조직코드<br>
	 * param.coststr_purc_typ_ccd - 품목군<br>
	 *
	 * @param param
	 * @return List
	 */
	public List searchPriceGroupByItemGrpTyp(Map param) {
		return rfxItemPriceFactorService.searchPriceGroupByItemGrpTyp(param);
	}
	
	/**
	 * 견적요청 품목 별 원가구성항목 목록 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.oorg_cd - 조직코드<br>
	 * param.rfx_item_uuid - 견적요청 품목 ID<br>
	 * param.costfact_grp_cd - 가격군 코드<br>
	 *
	 * @param param
	 * @return List
	 */
	public List searchPriceFactorByRfxItem(Map param) {
		return rfxItemPriceFactorService.searchPriceFactorByRfxItem(param);
	}
	
	/**
	 * RFx 품목 별 원가구성항목 저장한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfxData - 견적요청 마스터<br>
	 * param.rfxItem - 견적요청 품목<br>
	 * param.checkItem - 견적요청 품목 - 변경 전 정보<br>
	 *
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap saveRfxItemPriceFactor(Map param) {
		// validate
		ResultMap validator = rfxValidator.existRfxId((Map) param.get("rfxData"));
		if(!validator.isSuccess()) {
			return validator;
		}
		
		rfxItemPriceFactorService.saveRfxItemPriceFactor(param);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 평가기준 설정 및 원가구성항목 설정 확인<br><br>
	 * <b>Required:</b><br>
	 * param.rfxData - 견적요청 데이터<br>
	 *
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap checkBeforeRequestRfx(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		
		// validate
		ResultMap validator = rfxValidator.existRfxId(rfxData);
		if(validator.isSuccess()) {
            if("RFP".equals(rfxData.get("rfx_typ_ccd"))){
                // 비가격평가 설정 체크
                ResultMap checkNonPriResultMap = rfxEvalService.checkNonPriEvalSet(rfxData);
                if(!checkNonPriResultMap.isSuccess()) {
                    return checkNonPriResultMap;
                }
            }
            if(rfxData.get("coststr_use_yn") != null && "Y".equals(rfxData.get("coststr_use_yn"))){
                // 원가구성항목 설정 체크
                ResultMap checkPriFactResultMap = rfxItemPriceFactorService.checkPriFactSet(rfxData);
                if(!checkPriFactResultMap.isSuccess()) {
                    return checkPriFactResultMap;
                }
            }
		}
		return ResultMap.SUCCESS();
	}
	
	public FloaterStream findListRfx(Map param) {
		return rfxRepository.findListRfx(param);
	}
	
	public List findListRfxVdBid(Map param) {
		return rfxRepository.findListRfxVdBid(param);
	}
	
	public List findListRfxRcmdOption(Map param) {
		return rfxVendorRcmdService.findListRfxRcmdOption(param);
	}
	
	public List findListRfxRcmdTargVendor(Map param) {
		return rfxVendorRcmdService.findListRfxRcmdTargVendor(param);
	}
	
	/**
	 * rfx by result 조회한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : Yeon-u Kim
	 * @Date : 2016. 9. 30
	 * @Method Name : findRfxByResult
	 */
	public Map findRfxByResult(Map param) {
		Map result = rfxRepository.findRfxByResult(param);
		
		// 견적 품목에 견적요청 품목 UUID 포함되어 있을 경우 PRO 모듈에 견적요청 정보 요청하여 세팅
		if(!Strings.isNullOrEmpty((String) result.get("pr_item_uuid"))) {
			List<Map> prInfos = rfxEventPublisher.findListPrByPrItems(Lists.newArrayList((String) result.get("pr_item_uuid")));
			if(prInfos.size() > 0) {
				Map prInfo = prInfos.get(0);
				result.put("pr_uuid", prInfo.get("pr_uuid"));
			}
		}
		return result;
	}
	
	/**
	 * RFQ 품목별 견적서를 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @author : Yeon-u Kim
	 * @Date : 2016. 5. 23
	 * @Method Name : findListRfxItemRfxAttends
	 */
	public List findListRfxItemRfxAttends(Map param) {
		if(param == null) {
			return null;
		}
		
		Map rfxInfo = this.findRfxByResult((Map) param.get("rfxData"));
		List rfxBidItems = rfxBidService.findListRfxItemWithBidItems(rfxInfo);
		return rfxBidItems;
	}
	
	/**
	 * RFQ 총액별 견적서를 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @author : Yeon-u Kim
	 * @Date : 2016. 5. 25
	 * @Method Name : findListRfxAmountRfxQtaAttends
	 */
	public Map findListRfxAmountRfxBidAttends(Map param) {
		if(param == null) {
			return null;
		}
		Map rfxInfo = this.findRfxByResult((Map) param.get("rfxData"));
		//업체 견적서 조회
		Map result = rfxBidService.findListRfxBidWithBidItems(rfxInfo);
		return result;
	}
	
	/**
	 * 종합평가 견적서를 조회한다.
	 *
	 * @param param
	 * @return
	 */
	public Map findListRankingTotalEvalAmount(Map param) {
		if(param == null) {
			return null;
		}
		Map rfxInfo = this.findRfxByResult((Map) param.get("rfxData"));
		//업체 견적서 조회, 종합평가 견적서와 동일
		Map result = rfxBidService.findListRfxBidWithBidItems(rfxInfo);
		return result;
	}
	
	/**
	 * 견적서 선정여부를 저장한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfxData - 견적요청 마스터<br>
	 * param.rfxBids - 선정할 견적서<br>
	 *
	 * @param param
	 * @return
	 */
	public ResultMap selectRfxBids(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("rfxData") == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("rfxBids") == null) {
			return ResultMap.NOT_EXISTS();
		}

		Map rfxData = (Map) param.get("rfxData");
		// validate
		ResultMap validator = rfxValidator.existRfxId(rfxData);
		if(!validator.isSuccess()) {
			return validator;
		}

		rfxBidService.updateRfxStlRsn(rfxData);
		rfxBidService.updateRfxBidStl((List) param.get("rfxBids"));
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 견적서 아이템 선정여부를 저장한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfxData - 견적요청 마스터<br>
	 * param.rfxBidItems - 선정할 견적서 품목<br>
	 *
	 * @param param
	 * @return
	 */
	public ResultMap selectRfxBidItems(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("rfxData") == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("rfxBidItems") == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map rfxData = (Map) param.get("rfxData");
		// validate
		ResultMap validator = rfxValidator.existRfxId(rfxData);
		if(!validator.isSuccess()) {
			return validator;
		}

		rfxBidService.updateRfxStlRsn(rfxData);
		rfxBidService.updateRfxBidItemsStl(rfxData, (List) param.get("rfxBidItems"));
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 개찰 처리
	 *
	 * @param param
	 * @return
	 */
	public ResultMap bypassOpenRfx(Map param) {
		Map rfxData = this.findRfxByResult(param);
		Map rfxPreInspData = rfxPreInspService.findRfxPreInspByRfxId(param);
		
		ResultMap validator = rfxValidator.bypassOpenRfx(rfxData, rfxPreInspData);
		if(!validator.isSuccess()) {
			return validator;
		}
		this.openRfx(rfxData);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 개찰 - Ranking을 구하고 상태를 업데이트 한다. (마감 - 업체선정중)
	 * <pre>
	 * (1) RfqService.closeRfx : RFQ 마감 시 호출됨
	 * (2) endRfxEval : RFP 평가 마감시 호출됨
	 * (3) bypassOpenRfx : 개찰 버튼 클릭 시 호출됨
	 * </pre>
	 *
	 * @param param
	 */
	public void openRfx(Map param) {
		// 개찰 시 금액 관련 필드 복호화
		rfxBidService.updateDecryptAmount(param);
		
		// 상태 업데이트 (마감 - 업체선정중)
		rfxStatusService.openRfx(param);
		
		// Ranking 구하기
		this.computeRanking(param);

		// 개찰 메일
		mailService.sendAsync("RFX_OPEN", (String) param.get("rfx_uuid"),param);
	}
	
	/**
	 * rfx 유찰 처리.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap dropRfx(Map param) {
		ResultMap validator = rfxValidator.existRfxId(param);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		// 비가격평가 마감
		this.forceCloseRfxEval(param);
		rfxStatusService.dropRfx(param);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * Cancel rfx.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap cancelRfx(Map param) {
		ResultMap validator = rfxValidator.existRfxId(param);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		rfxStatusService.cancelRfx(param);
		
		//취소 시 스케쥴러 서비스에서 기존 스케쥴러 삭제
		rfxSchedulerService.cancelRfx(param);
		
		//취소시 RFX 사전 심사 취소
		rfxPreInspService.cancelRfxPreInsp(param);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * rfx 결과 선정처리.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap bypassApprovalRfxResult(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		
		ResultMap validator = rfxValidator.existRfxId(rfxData);
		if(!validator.isSuccess()) {
			return validator;
		}

		Map rfxInfo = this.findRfxByResult(rfxData);
		//RFX 선정결과 등록
		if(rfxData.getOrDefault("slctn_rsn","") != rfxInfo.getOrDefault("slctn_rsn","") ){
			rfxInfo.put("slctn_rsn",rfxData.get("slctn_rsn"));
		}
		rfxItemService.updateRfxItemStlInfo(rfxInfo);
		rfxStatusService.bypassApprovalRfxResult(rfxInfo);

		this.completeRfx(rfxInfo);
		return ResultMap.SUCCESS();
	}
	
	public void completeRfx(Map rfxInfo) {
		if(rfxInfo == null) {
			return;
		}

		List<Map> slctnVds = rfxBidService.findListRfxBidSlctnByRfx(rfxInfo);
		for(Map slctnVd : slctnVds) {
			slctnVd.put("rfx_slctn_vd_uuid", UUID.randomUUID().toString());
			slctnVd.put("rfx_uuid", rfxInfo.get("rfx_uuid"));
			rfxRepository.insertRfxSlctnVd(slctnVd);
			
			// 단가계약 체결인 경우 다음 단계 단가계약만 존재하므로 다음 단계 선택 바로 저장
			if("UPRCCNTR_SGNG".equals(rfxInfo.get("rfx_purp_ccd")) || "PSR".equals(rfxInfo.get("rfx_purp_ccd"))) {
				slctnVd.put("nxt_prcs_ccd", "UPRCCNTR");
				this.updateRfxSlctnNxtPrc(slctnVd);
			}
		}
		
		// 견적결과 메일발송
		mailService.sendAsync("RFX_PASS_MAIL", (String) rfxInfo.get("rfx_uuid"));
		mailService.sendAsync("RFX_NOPASS_MAIL", (String) rfxInfo.get("rfx_uuid"));
	}
	
	/**
	 * 원가구성 작성 비교 조회
	 *
	 * @param param
	 * @return
	 */
	
	/**
	 * 견적요청 기준 제출업체의 품목별 원가구성 금액 조회<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 * @return Map
	 */
	public Map findListRfxBidItemPriceFactorCompare(Map param) {
		Map rfxInfo = this.findRfx(param);
		
		rfxInfo.put("rfx_bid_sts_ccd", "SUBM"); //제출업체
		List rfxBidVendors = rfxBidService.findListRfxBid(rfxInfo);
		List rfxBidVendorFactors = rfxBidService.findListRfxBidItemPriceFactorCompare(rfxInfo);
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("qtaVendors", rfxBidVendors);
		resultMap.put("qtaFactors", rfxBidVendorFactors);
		return resultMap;
	}
	
	/**
	 * 견적 결과품의 존재여부 확인한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap checkRfxResultApproval(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		
		if(rfxRepository.checkRfxResultApproval(rfxData) > 0) {
			return ResultMap.FAIL();
		} else {
			return ResultMap.SUCCESS();
		}
	}
	
	/**
	 * 견적 결과품의 삭제한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfxData - 견적요청 마스터<br>
	 *
	 * @param param
	 */
	public ResultMap deleteRfxResultApproval(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		
		String aprvId = rfxRepository.findRfxResultApprovalId(rfxData);
		if(aprvId == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map rfxHeader = rfxRepository.findRfxByResult(rfxData);
		Map deleteParam = Maps.newHashMap();
		Map deleteApproval = Maps.newHashMap();
		deleteApproval.put("apvl_uuid", aprvId);
		deleteApproval.put("task_uuid", (String) rfxHeader.get("rfx_uuid"));
		String slctnApvlStsCcd = (String) ("APVL_CNCL".equals(rfxHeader.get("rfx_slctn_apvl_sts_ccd")) ? "CRNG" : rfxHeader.get("rfx_slctn_apvl_sts_ccd"));
		deleteApproval.put("prev_apvl_sts_ccd", slctnApvlStsCcd);
		deleteParam.put("deleteApproval", deleteApproval);
		
		approvalService.deleteApproval(deleteParam);
		return ResultMap.SUCCESS();
	}
	
	public List findListRfxQtaCompareByRfx(Map param) {
		return rfxBidService.findListRfxBidCompareByRfx(param);
	}
	
	/**
	 * 이메일 견적내용을 업체로 전송한다.<br><br>
	 * <b>Required:</b><br>
	 * param.xxx - 속성 설명<br>
	 * param.zzz - 속성 설명
	 *
	 * @param param
	 * @return
	 */
	public ResultMap rfxSendMail(Map<String, Object> param) {
		Map rfx = rfxRepository.findRfx(param);
		if(rfx == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map data = Maps.newHashMap();
		data.put("rfx_typ_ccd", rfx.get("rfx_typ_ccd"));
		data.put("rfx_uuid", rfx.get("rfx_uuid"));
		
		// 업체 업무 이메일 발송 관련 로직
		// 기존업체
		List rfxVendors = (List) param.get("rfxVendor");
		
		try {
			// 발송할 내역이 존재할 경우만 하기와 같이 처리
			for(Map rfxVendor : (List<Map>) rfxVendors) {
				Map rfxBidData = rfxBidService.findRfxBid(rfxVendor);
				List rfxBidItems = rfxBidService.findListRfxBidItemForAgent(rfxBidData);
				
				SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				String rfxCloseDt = dtFormat.format((Date) rfxBidData.get("rfx_clsg_dttm"));
				rfxBidData.put("rfx_clsg_dttm_fmt", rfxCloseDt);    // 마감 일시
				
				SimpleDateFormat getFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
				SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				
				for(Map rfxBidItem : (List<Map>) rfxBidItems) {
					if(rfxBidItem.get("req_dlvy_dt") != null) {
						rfxBidItem.put("req_dlvy_dt_fmt", dFormat.format(getFormat.parse((String) rfxBidItem.get("req_dlvy_dt"))));
					}
					if(rfxBidItem.get("const_st_dt") != null) {
						rfxBidItem.put("const_st_dt_fmt", dFormat.format(getFormat.parse((String) rfxBidItem.get("const_st_dt"))));
					}
					if(rfxBidItem.get("const_exp_dt") != null) {
						rfxBidItem.put("const_exp_dt_fmt", dFormat.format(getFormat.parse((String) rfxBidItem.get("const_exp_dt"))));
					}
				}
				
				// Excel에 들어가는 data 정보
				data.put("rfxBidData", rfxBidData);
				data.put("rfxBidItems", rfxBidItems);
				
				//메일 발송 정보
				Map excelInfoData = Maps.newHashMap();
				String rfxPurp = (String) rfxBidData.get("rfx_purp_ccd");
				String purcTyp = (String) rfxBidData.get("purc_typ_ccd");
				String workCd = "RFX_" + rfxPurp + ("UPRCCNTR_SGNG".equals(rfxPurp) ? "" : ("_" + purcTyp));
				excelInfoData.put("eml_task_typ_ccd", workCd);                            // 업무 유형 코드
				
				List args = Lists.newArrayList(rfx.get("rfx_no"), rfx.get("rfx_tit"), rfxBidData.get("vd_nm"));
				String mailTit = messageUtil.getCodeMessage("[{0}] {1} 이메일견적 요청", args, null, LocaleContextHolder.getLocale());
				String fileNm = messageUtil.getCodeMessage("[{0}]{1}_이메일견적_{2}", args, null, LocaleContextHolder.getLocale());
				
				excelInfoData.put("eml_tit", mailTit);                        // 실제 발송될 mail title
				excelInfoData.put("email_file_nm", fileNm);                            // 실제 발송될 excel file name
				excelInfoData.put("rcpnt_eml", rfxVendor.get("vd_pic_eml"));    // 실제 발송될 to address
				excelInfoData.put("to_nm", rfxVendor.get("vd_pic_nm"));        // 실제 발송될 to name
				excelInfoData.put("task_uuid", rfx.get("rfx_uuid"));                // 업무 app_id
				excelInfoData.put("eml_task_dtl_uuid", rfxBidData.get("vd_cd"));        // 업무 상세 id (ex. vd_cd)
				
				//excel common source
				mailWorkService.excelFieldSetupCreate(excelInfoData, data);
			}
			return ResultMap.SUCCESS();
		} catch(Exception e) {
			e.printStackTrace();
			return ResultMap.FAIL();
		}
	}
	
	/**
	 * 해당 RFx의 공동수급협정서 현황 조회
	 *
	 * @param param
	 * @return
	 */
	public List findListCsByRfx(Map param) {
		return rfxRepository.findListCsByRfx(param);
	}
	
	/**
	 * 공동수급협정서 상세 조회
	 *
	 * @param param
	 * @return
	 */
	public Map findRfxCsData(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxData", this.findRfx(param));
		resultMap.put("csData", rfxRepository.findRfxCs(param));
		resultMap.put("csVdList", rfxRepository.findListRfxCsVd(param));
		return resultMap;
	}
	
	public ResultMap completeNpeEval(Map param) {
		Map rfxInfo = rfxRepository.findRfx(param);
		// 개찰 전인 경우 개찰 수행
		if("OPEN_WTG".equals(rfxInfo.get("rfx_res_sts_ccd"))) {
			this.bypassOpenRfx(rfxInfo);
		} else {
			// 개찰 이후인 경우 점수 재계산
			this.computeRanking(rfxInfo);
		}
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 평가 강제 마감
	 *
	 * @param param
	 * @return
	 */
	public ResultMap forceCloseRfxEval(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		ResultMap validator = rfxValidator.existRfxId(param);
		if(!validator.isSuccess()) {
			return validator;
		}
		validator = rfxValidator.forceCloseRfxEval(param);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		// 평가 진행상태 종료 update
		rfxEvalService.updateCompleteNpeEval(param);
		return this.completeNpeEval(param);
	}

	/**
	 * RFI -> RFX
	 *
	 * @param param
	 * @return
	 */
	public Map findRfxDefaultDataByRfiId(Map param) {
		Map rfxData = Maps.newHashMap();
		List rfxReqRcptUuids = (List) param.get("rfx_req_rcpt_uuids");

		if(param == null) {
			return null;
		}
		
		Map rfiData = rfiService.findDefaultDataByRfi(param);
		
		rfxData = this.findRfxDefaultDataByReqItemIds(param);
		rfxData.put("rfiData", rfiData.get("rfiData"));
		rfxData.put("rfiVendors", rfiData.get("rfiVendors"));
		return rfxData;
	}
	
	public List findListRfxSlctnVd(Map param) {
		if(param == null) {
			return null;
		}
		
		return rfxRepository.findListRfxSlctnVd(param);
	}
	
	public ResultMap saveRfxSlctnNxtPrcs(Map param) {
		if(param == null) {
			return null;
		}
		
		List<Map> updateItems = (List<Map>) param.get("updateItems");
		for(Map updateItem : updateItems) {
			this.updateRfxSlctnNxtPrc(updateItem);
		}
		
		return ResultMap.SUCCESS();
	}
	
	protected void updateRfxSlctnNxtPrc(Map updateItem) {
		if(updateItem == null) {
			return;
		}
		String rfxNxtPrcsReqUuid = (String) updateItem.get("rfx_nxt_prcs_req_uuid");
		if(Strings.isNullOrEmpty(rfxNxtPrcsReqUuid)) {
			List rfxBidSlctnItemList = rfxBidService.findRfxBidSlctnItemByRfxAndVdCd(updateItem);
			List rfxCsList = rfxBidService.findListCsByBid(updateItem);
			
			Map param = Maps.newHashMap();
			param.put("rfxNxtPrcsReqData", updateItem);
			param.put("purcCntrData", updateItem);
			param.put("purcCntrInfoData", updateItem);
			param.put("insertItems", rfxBidSlctnItemList);
			param.put("insertCsList", rfxCsList);
			ResultMap result = rfxNxtPrcsService.createRfxNxtPrcsReq(param);
			Map resultData = result.getResultData();
			updateItem.put("rfx_nxt_prcs_req_uuid", resultData.get("rfx_nxt_prcs_req_uuid"));
			rfxRepository.updateRfxSlctnNxtPrc(updateItem);
		}
	}
	
	/**
	 * 선정 후 취소 수행
	 *
	 * @param param
	 * @return
	 */
	public ResultMap cancelSlctnRfx(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		List<Map> checkedRows = (List<Map>) param.get("checkedRows");
		
		List<String> rfxBidUuids = Lists.newArrayList();
		List<String> rfxBidItemUuids = Lists.newArrayList();
		List<String> rfxItemUuids = Lists.newArrayList();
		List<String> rfxReqRcptUuids = Lists.newArrayList();
		List<String> prItemUuids = Lists.newArrayList();
		List<String> upcrItemUuids = Lists.newArrayList();
		for(Map checked : checkedRows) {
			List<Map> detailList = rfxRepository.findListRfxSlctnVdDetail(checked);
			for(Map detail : detailList) {
				if(!rfxBidUuids.contains(detail.get("rfx_bid_uuid"))) {
					rfxBidUuids.add((String) detail.get("rfx_bid_uuid"));
				}
				if(!rfxBidItemUuids.contains(detail.get("rfx_bid_item_uuid"))) {
					rfxBidItemUuids.add((String) detail.get("rfx_bid_item_uuid"));
				}
				if(!rfxItemUuids.contains(detail.get("rfx_item_uuid"))) {
					rfxItemUuids.add((String) detail.get("rfx_item_uuid"));
				}
				if(!rfxReqRcptUuids.contains(detail.get("rfx_req_rcpt_uuid"))) {
					rfxReqRcptUuids.add((String) detail.get("rfx_req_rcpt_uuid"));
				}
				if(!prItemUuids.contains(detail.get("pr_item_uuid"))) {
					prItemUuids.add((String) detail.get("pr_item_uuid"));
				}
				if(!upcrItemUuids.contains(detail.get("upcr_item_uuid"))) {
					upcrItemUuids.add((String) detail.get("upcr_item_uuid"));
				}
			}
			
			rfxNxtPrcsService.deleteRfxNxtPrcsReq(checked);
			rfxRepository.deleteRfxSlctnVd(checked);
		}
		
		Map data = Maps.newHashMap();
		data.put("rfx_uuid", rfxData.get("rfx_uuid"));
		data.put("slctn_cncl_rsn", param.get("slctn_cncl_rsn"));
		data.put("item_slctn_typ_ccd", rfxData.get("item_slctn_typ_ccd"));
		data.put("rfx_bid_uuids", rfxBidUuids);
		data.put("rfx_bid_item_uuids", rfxBidItemUuids);
		data.put("rfx_item_uuids", rfxItemUuids);
		data.put("rfx_req_rcpt_uuids", rfxReqRcptUuids);
		data.put("pr_item_uuids", prItemUuids);
		data.put("upcr_item_uuids", upcrItemUuids);
		
		rfxStatusService.cancelRfxResult(data);
		return ResultMap.SUCCESS();
	}
	
	public List findListRfxResult(Map param) {
		return rfxRepository.findListRfxResult(param);
	}
}
