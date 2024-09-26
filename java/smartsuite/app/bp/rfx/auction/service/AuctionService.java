package smartsuite.app.bp.rfx.auction.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.approval.service.ApprovalService;
import smartsuite.app.bp.rfx.auction.repository.AuctionRepository;
import smartsuite.app.bp.rfx.auction.scheduler.AuctionJobConst;
import smartsuite.app.bp.rfx.auction.scheduler.AuctionSchedulerService;
import smartsuite.app.bp.rfx.auction.validator.AuctionValidator;
import smartsuite.app.bp.rfx.receipt.service.RfxReceiptService;
import smartsuite.app.bp.rfx.rfi.service.RfiService;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.bp.rfx.rfx.service.RfxNxtPrcsService;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class AuctionService {

	@Inject
	AuctionRepository auctionRepository;

	@Inject
	AuctionItemService auctionItemService;

	@Inject
	AuctionVendorService auctionVendorService;

	@Inject
	RfxReceiptService rfxReceiptService;

	@Inject
	RfxEventPublisher rfxEventPublisher;

	@Inject
	RfiService rfiService;

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

	@Inject
	AuctionBidService auctionBidService;

	@Inject
	AuctionSchedulerService auctionSchedulerService;

	@Inject
	StdFileService stdFileService;

	@Inject
	ApprovalService approvalService;

	@Inject
	AuctionValidator auctionValidator;

	@Inject
	RfxNxtPrcsService rfxNxtPrcsService;

	public FloaterStream findListAuction(Map param) {
		return auctionRepository.findListAuction(param);
	}

	public Map findAuction(Map param) {
		return auctionRepository.findAuction(param);
	}

	public Map findAuctionInfo(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxData", auctionRepository.findAuction(param));
		resultMap.put("auctionItems", auctionItemService.findListAuctionItem(param));
		resultMap.put("auctionVendors", auctionVendorService.findListAuctionVendor(param));

		return resultMap;
	}

	public Map findRfxDefaultDataByReqItemIds(Map param) {
		if(param == null) {
			return null;
		}
		if(param.get("rfx_req_rcpt_uuids") == null) {
			return null;
		}

		List auctionItems = rfxReceiptService.findRfxDefaultDataByReqRcpts((List) param.get("rfx_req_rcpt_uuids"));
		if(auctionItems.isEmpty()) {
			return null;
		}

		int maxNo = 10;
		for(Map auctionItem : (List<Map>) auctionItems) {
			auctionItem.put("rfx_qty", auctionItem.get("req_qty"));
			auctionItem.put("rfx_req_uprc", auctionItem.get("req_uprc"));
			auctionItem.put("rfx_req_amt", auctionItem.get("req_amt"));
			auctionItem.put("rfx_lno", maxNo);
			maxNo += 10;
		}

		Map firstAuctionItem = (Map) auctionItems.get(0);

		Map rfxData = Maps.newHashMap();
		rfxData.put("rfx_rnd", 1);
		rfxData.put("rfx_typ_ccd", "RAUC");
		rfxData.put("oorg_cd", firstAuctionItem.get("oorg_cd"));
		rfxData.put("purc_typ_ccd", firstAuctionItem.get("purc_typ_ccd"));
		rfxData.put("cur_ccd", firstAuctionItem.get("cur_ccd"));
		rfxData.put("rfx_purp_ccd", firstAuctionItem.get("rfx_purp_ccd"));
		rfxData.put("rfx_sts_ccd", "CRNG");
		rfxData.put("rfx_apvl_sts_ccd", "CRNG");
		rfxData.put("rauc_subm_min_unit_amt", 1);
		rfxData.put("auto_ext_use_yn", "N");
		rfxData.put("trgprc_use_yn", "N");
		rfxData.put("purc_grp_cd", firstAuctionItem.get("purc_grp_cd"));

		Map userInfo = Auth.getCurrentUserInfo();
		rfxData.put("purc_pic_id", userInfo.get("usr_id"));
		rfxData.put("purc_chr_nm", userInfo.get("disp_usr_nm"));
		rfxData.put("purc_chr_dept_nm", userInfo.get("disp_dept_nm"));
		rfxData.put("purc_chr_phone_no", userInfo.get("phone_no"));

		if("PR".equals(firstAuctionItem.get("req_doc_typ_ccd"))) {
			rfxData.put("has_pr_item_yn", "Y");
			rfxData.put("has_upcr_item_yn", "N");
		} else if("UPCR".equals(firstAuctionItem.get("req_doc_typ_ccd"))) {
			rfxData.put("has_pr_item_yn", "N");
			rfxData.put("has_upcr_item_yn", "Y");
		}

		rfxData.put("has_no_cd_item", this.hasNoCdItems(auctionItems) ? "Y" : "N");
		rfxData.put("auctionItems", auctionItems);

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

	/**
	 * 역경매 정보를 저장한다.
	 *
	 * @param param : 역경매 정보, 역경매 품목 목록, 역경매 협력사 목록을 담은 parameter
	 * @return resultMap : 역경매 저장 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 5.24
	 * @Method Name : saveAuction
	 */
	private ResultMap saveAuction(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		List insertAuctionItems = (List) param.get("insertAuctionItems");
		List updateAuctionItems = (List) param.get("updateAuctionItems");
		List deleteAuctionItems = (List) param.get("deleteAuctionItems");
		List insertAuctionVendors = (List) param.get("insertAuctionVendors");
		List updateAuctionVendors = (List) param.get("updateAuctionVendors");
		List deleteAuctionVendors = (List) param.get("deleteAuctionVendors");

		int rfxRev = 1;
		if(rfxData.get("rfx_rnd") != null) {
			rfxRev = Integer.parseInt(rfxData.get("rfx_rnd").toString());
		}

		if(Strings.isNullOrEmpty((String) rfxData.get("rfx_uuid"))) {
			//신규 id생성
			rfxData.put("rfx_uuid", UUID.randomUUID().toString());
			// 번호 채번
			rfxData.put("rfx_no", sharedService.generateDocumentNumber("RA"));
			// 차수 : 최초 생성시 1
			rfxData.put("rfx_rnd", rfxRev);

			auctionRepository.insertAuction(rfxData);

			if(rfxData.get("rfi_uuid") != null) {
				rfiService.updateRfiByRfxUuid(rfxData);
			}
		} else {
			auctionRepository.updateAuction(rfxData);
		}

		//삭제
		auctionItemService.deleteAuctionItem(rfxData, deleteAuctionItems);
		auctionVendorService.deleteAuctionVendor(deleteAuctionVendors);

		//추가
		auctionItemService.insertAuctionItem(rfxData, insertAuctionItems);
		auctionVendorService.insertAuctionVendor(rfxData, insertAuctionVendors);

		//수정
		auctionItemService.updateAuctionItem(updateAuctionItems);
		auctionVendorService.updateAuctionVendor(updateAuctionVendors);

		// resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("rfx_uuid", rfxData.get("rfx_uuid"));
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 역경매 저장
	 *
	 * @param param
	 * @return
	 */
	public ResultMap saveDraftAuction(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		List<String> prItemIds = (List) param.get("prItemIds");
		List<String> upcrItemIds = (List) param.get("upcrItemIds");

		if(rfxData == null) {
			return ResultMap.NOT_EXISTS();
		}

		String rfxId = (String) rfxData.get("rfx_uuid");
		// 구매요청접수 화면에서 넘어와 신규 RFx 생성하는 경우
		if(Strings.isNullOrEmpty(rfxId) && !prItemIds.isEmpty()) {
			ResultMap validator = rfxEventPublisher.validateCreateRfxByPr(prItemIds);
			if(validator.isSuccess()) {
				// 구매요청품목 존재 시 구매요청품목 접수 처리
				rfxEventPublisher.receivePr(prItemIds);
			} else {
				return validator;
			}
		}else if(Strings.isNullOrEmpty(rfxId) && !upcrItemIds.isEmpty()) {
			ResultMap validator = rfxEventPublisher.validateCreateRfxByUpcr(upcrItemIds);
			if(validator.isSuccess()) {
				// 단가계약요청품목 존재 시 단가계약요청품목 접수 처리
				rfxEventPublisher.receiveUpcr(upcrItemIds);
			} else {
				return validator;
			}
		}

		ResultMap resultMap = this.saveAuction(param);
		if(resultMap.isSuccess()) {
			Map resultData = resultMap.getResultData();

			// 견적요청 진행상태를 임시저장으로 변경한다.
			rfxStatusService.saveDraftRfx(resultData);
		}

		return resultMap;
	}

	/**
	 * 역경매 재견적 정보를 조회한다.
	 *
	 * @param prevAuction : 역경매 재견적 대상 id를 담은 parameter
	 * @return resultMap : 역경매 재견적 정보
	 * @author kh_lee
	 * @Date : 2016. 6. 9
	 * @Method Name : findAuctionDataByMultiRound
	 */
	private ResultMap findAuctionDataByMultiRound(Map prevAuction) {
		Map rfxData = auctionRepository.findAuction(prevAuction);

		Integer revision = auctionRepository.findAuctionMaxRevision(rfxData);
		rfxData.put("rfx_uuid", null);
		rfxData.put("prev_rfx_uuid", prevAuction.get("rfx_uuid"));
		rfxData.put("rfx_rnd", revision + 1);
		rfxData.put("rfx_sts_ccd", "CRNG");
		rfxData.put("rfx_res_sts_ccd", null);
		rfxData.put("rfx_apvl_sts_ccd", "CRNG");
		rfxData.put("sub_work_cd", "CRNG");

		//재견적 시 마감 일시 초기값 설정 (ZER-427)
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 7);
		cal.set(Calendar.HOUR_OF_DAY, 18);
		cal.set(Calendar.MINUTE, 0);
		rfxData.put("rfx_clsg_dttm", cal.getTime());

		if("Y".equals(rfxData.get("auto_ext_use_yn"))) {
			rfxData.put("auto_ext_poss_wd", 0); //자동 연장 회수 초기화
		}

		// 내부용 첨부파일 복사
		String intnAttNo = (String) rfxData.get("buyer_athg_uuid");
		rfxData.put("buyer_athg_uuid", stdFileService.copyFile(intnAttNo));

		// 협력사용 첨부파일 복사
		String extnAttNo = (String) rfxData.get("vd_athg_uuid");
		rfxData.put("vd_athg_uuid", stdFileService.copyFile(extnAttNo));

		List auctionItems = auctionItemService.findListAuctionItemByMultiRound((String) prevAuction.get("rfx_uuid"));

		// SMARTNINE-1732 - 재견적 시 견적참여업체가 하나도 없을 때, 이전 요청 업체들을 불러오도록 함
		int vdSubmitCnt = Integer.parseInt(rfxData.get("vd_subm_cnt").toString());
		List auctionVendors = auctionVendorService.findListAuctionVendorByMultiRound((String) prevAuction.get("rfx_uuid"), vdSubmitCnt);

		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxData", rfxData);
		resultMap.put("auctionItems", auctionItems);
		resultMap.put("auctionVendors", auctionVendors);

		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 역경매 재견적 정보를 저장한다.
	 *
	 * @param param : 역경매 정보, 역경매 품목 목록, 역경매 협력사 목록을 담은 parameter
	 * @return resultMap : 역경매 재견적 저장 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 6. 9
	 * @Method Name : saveMultiRoundAuction
	 */
	public ResultMap saveMultiRoundAuction(Map param) {
		Map prevAuction = (Map) param.get("rfxData");

		// validate
		ResultMap validator = auctionValidator.validate(prevAuction);
		if(!validator.isSuccess()) {
			return validator;
		}

		ResultMap multiRoundResultMap = this.findAuctionDataByMultiRound(prevAuction);
		if(!multiRoundResultMap.isSuccess()) {
			return ResultMap.FAIL();
		}

		Map rfxData = (Map) multiRoundResultMap.getResultData().get("rfxData");
		List auctionItems = (List) multiRoundResultMap.getResultData().get("auctionItems");
		List auctionVendors = (List) multiRoundResultMap.getResultData().get("auctionVendors");

		// 기존 차수의 역경매 상태값 변경
		rfxStatusService.multiRoundRfx(prevAuction, auctionItems);

		Map saveParam = Maps.newHashMap();
		saveParam.put("rfxData", rfxData);
		saveParam.put("insertAuctionItems", auctionItems);
		saveParam.put("insertAuctionVendors", auctionVendors);

		// 재견적 생성
		ResultMap saveAuctionResultMap = this.saveAuction(saveParam);
		if(!saveAuctionResultMap.isSuccess()) {
			return ResultMap.FAIL();
		}

		Map resultData = saveAuctionResultMap.getResultData();

		// 재견적 건 저장 상태값 변경
		rfxStatusService.saveDraftRfx(resultData);

		return saveAuctionResultMap;
	}


	/**
	 * 다중 건 역경매를 삭제한다.
	 *
	 * @param param
	 * @return resultMap : 역경매 삭제 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 5.25
	 * @Method Name : deleteListAuction
	 */
	public ResultMap deleteListAuction(Map param) {
		List invalidAuctions = Lists.newArrayList();
		List notExistAuctions = Lists.newArrayList();

		List deleteAuctions = (List) param.get("deleteAuctions");
		for(Map auction : (List<Map>) deleteAuctions) {
			ResultMap result = this.deleteAuction(auction);

			if(Const.INVALID_STATUS_ERR.equals(result.getResultStatus())) {
				invalidAuctions.add(result.getResultData());
			} else if(Const.NOT_EXIST.equals(result.getResultStatus())) {
				notExistAuctions.add(auction);
			}
		}
		return ValidatorUtil.setupDataListForValidationDataList(deleteAuctions, invalidAuctions, notExistAuctions);
	}

	/**
	 * 역경매를 삭제한다.
	 *
	 * @param param : 삭제할 역경매 정보 {rfx_uuid: 아이디}
	 * @return resultMap : 역경매 삭제 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 5.24
	 * @Method Name : deleteAuction
	 */
	public ResultMap deleteAuction(Map param) {
		// validate
		ResultMap validator = auctionValidator.validate(param);
		if(!validator.isSuccess()) {
			return validator;
		}
		auctionVendorService.deleteAuctionVendorByAuction(param);
		auctionItemService.deleteAuctionItemByAuction(param);
		auctionRepository.deleteAuction(param);
		rfxStatusService.deleteRfx(param);

		if(rfiService.checkRfiData(param)) {
			rfiService.updateRfiByRfxUuid(param);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 역경매를 즉시 업체전송한다.
	 *
	 * @param param : 업체전송할 역경매 정보 {rfx_uuid: 아이디}
	 * @return resultMap : 역경매 업체전송 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 5.24
	 */
	public ResultMap directAuctionBid(Map param) {
		// Bypass 상태변경 처리 - 공고중, 진행품의 승인처리
		rfxStatusService.bypassApprovalRfxProg(param);

		// 스케쥴러 등록 - 역경매 공고
		auctionSchedulerService.noticeAuction(param);

		// resultData 셋팅 
		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("rfx_uuid", param.get("rfx_uuid"));
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 역경매 투찰시작 (Job 수행 service)
	 *
	 * @param param : 역경매 정보 {rfx_uuid: 아이디}
	 * @author kh_lee
	 * @Date : 2016. 5.26
	 * @Method Name : startAuction
	 */
	public void startAuction(HashMap<String, Object> param) {
		rfxStatusService.startRfx(param);
	}

	/**
	 * 역경매 투찰마감 (Job 수행 service)
	 *
	 * @param param : 역경매 정보 {rfx_uuid: 아이디}
	 * @author kh_lee
	 * @Date : 2016. 5.26
	 * @Method Name : closeAuction
	 */
	public void closeAuction(HashMap<String, Object> param) {
		Map auctionInfo = auctionRepository.findAuction(param);

		if(auctionInfo == null) {
			return;
		}

		String autoExtYn = (String) auctionInfo.get("auto_ext_use_yn");
		int vdSubmitCnt = Integer.parseInt(auctionInfo.get("vd_subm_cnt").toString());
		int extCnt = Integer.parseInt(auctionInfo.get("auto_ext_poss_wd").toString());

		// 자동연장여부 : "Y", 제출업체 수 : 0 인 경우
		if("Y".equals(autoExtYn) && vdSubmitCnt == 0
				&& auctionInfo.get("auto_ext_tm") != null
				&& AuctionJobConst.MAX_AUTO_EXT_COUNT > extCnt) {

			// 자동연장 처리
			Calendar cal = Calendar.getInstance();
			Date date = (Date) auctionInfo.get("rfx_clsg_dttm");
			cal.setTime(date);

			// 연장시간 (역경매에서는 "분"으로 사용)
			BigDecimal extHour = (BigDecimal) auctionInfo.get("auto_ext_tm");
			cal.add(Calendar.HOUR, extHour.intValue());
			Date newCloseDt = new Date(cal.getTimeInMillis());

			//마감 일시 변경
			Map option = Maps.newHashMap();
			option.put("auto_ext_poss_wd", extCnt + 1);
			this.updateCloseDate((String) auctionInfo.get("rfx_uuid"), newCloseDt, option);
		} else {
			// Ranking 구하기
			auctionBidService.computeRanking(auctionInfo);
			// 마감 상태 업데이트
			rfxStatusService.closeRfx(param);
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

		// 마감시간 변경
		auctionRepository.updateCloseDt(param);
		// 마감 스케줄러 변경
		auctionSchedulerService.changeAuctionCloseTime(param);
	}

	/**
	 * 역경매 마감일자를 수정한다.
	 *
	 * @param param : 역경매 마감일자 수정 정보 {rfx_uuid: 아이디, rfx_clsg_dttm: 마감 일시}
	 * @return resultMap : 역경매 마감일자 수정 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 5.26
	 * @Method Name : updateAuctionCloseDt
	 */
	public ResultMap updateAuctionCloseDt(Map param) {
		ResultMap resultMap;
		Map auctionInfo = auctionRepository.findAuction(param);

		if(auctionInfo == null) {
			return ResultMap.NOT_EXISTS();
		}

		String rfxProgSts = (String) auctionInfo.get("rfx_sts_ccd");

		// 공고중 이거나 진행중인 상태만 마감일자 수정
		if("N".equals(rfxProgSts) || "PRGSG".equals(rfxProgSts)) {
			String rfxId = (String) param.get("rfx_uuid");
			Date rfxCloseDt = (Date) param.get("rfx_clsg_dttm");
			Map option = Maps.newHashMap();
			option.put("open_dttm", param.get("open_dttm"));
			option.put("clsg_tm_adj_rsn", param.get("clsg_tm_adj_rsn"));

			this.updateCloseDate(rfxId, rfxCloseDt, option);

			resultMap = ResultMap.SUCCESS();
		} else {
			resultMap = ResultMap.builder()
					.resultStatus(RfxConst.RFX_END)
					.resultData(auctionInfo)
					.build();
		}
		return resultMap;
	}

	/**
	 * 다중 건의 역경매를 조기 마감한다.
	 *
	 * @param param : 조기마감할 역경매 목록
	 * @return resultMap : 역경매 강제마감 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 5.26
	 * @Method Name : byPassCloseListAuction
	 */
	public ResultMap byPassCloseListAuction(Map param) {
		List failAuctionList = Lists.newArrayList();
		List rfxDatas = (List) param.get("rfxDatas");

		for(Map rfxData : (List<Map>) rfxDatas) {
			ResultMap byPassResultMap = this.byPassCloseAuction(rfxData);

			if(!byPassResultMap.isSuccess()) {
				failAuctionList.add(byPassResultMap);
			}
		}

		if(failAuctionList.isEmpty()) {
			return ResultMap.SUCCESS();
		} else {
			return ResultMap.builder()
					.resultStatus(RfxConst.RFX_CLOSE_BYPASS_ERR)
					.resultList(failAuctionList)
					.build();
		}
	}

	/**
	 * 역경매를 조기 마감한다.
	 *
	 * @param param : 조기마감할 역경매 정보 {rfx_uuid: 아이디}
	 * @return resultMap : 역경매 강제마감 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 5.26
	 * @Method Name : byPassCloseAuction
	 */
	private ResultMap byPassCloseAuction(Map param) {
		Map auctionInfo = auctionRepository.findAuction(param);
		if(auctionInfo == null) {
			return ResultMap.NOT_EXISTS();
		}

		String rfxProgSts = (String) auctionInfo.get("rfx_sts_ccd");
		// 공고중 이거나 진행중인 상태만 마감처리
		List progStats = Lists.newArrayList("NTC_PRGSG", "PRGSG");
		if(!progStats.contains(rfxProgSts)) {
			return ResultMap.builder()
					.resultStatus(RfxConst.RFX_END)
					.resultData(auctionInfo)
					.build();
		}

		// 마감 스케줄러 삭제
		auctionSchedulerService.byPassCloseAuction(param);
		// Ranking 구하기
		auctionBidService.computeRanking(auctionInfo);

		// 현재시각을 마감처리시간으로 변경함.
		Map updateParam = Maps.newHashMap();
		updateParam.put("rfx_uuid", auctionInfo.get("rfx_uuid"));
		updateParam.put("rfx_clsg_dttm", new Date());

		auctionRepository.updateCloseDt(updateParam);

		// 마감 상태 업데이트
		rfxStatusService.closeRfx(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 역경매 업체 선정을 저장한다. (업체별)
	 *
	 * @param param
	 * @return resultMap : 역경매 업체 선정 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 6. 8
	 * @Method Name : selectAuctionAttend
	 */
	public ResultMap selectAuctionAttend(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("rfxData") == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("auctionQtas") == null) {
			return ResultMap.NOT_EXISTS();
		}

		Map rfxData = (Map) param.get("rfxData");
		// validate
		ResultMap validator = auctionValidator.validate(rfxData);
		if(!validator.isSuccess()) {
			return validator;
		}

		if(validator.isSuccess()) {
			auctionBidService.updateAuctionBidStlRsn(rfxData);
			auctionBidService.updateAuctionBidStl((List) param.get("auctionQtas"));
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 역경매 업체 선정을 저장한다. (품목별)
	 *
	 * @param param
	 * @return resultMap : 역경매 업체 선정 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 6. 8
	 * @Method Name : selectAuctionItemAttends
	 */
	public ResultMap selectAuctionItemAttends(Map param) {
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
		ResultMap validator = auctionValidator.validate(rfxData);
		if(!validator.isSuccess()) {
			return validator;
		}

		auctionBidService.updateAuctionBidStlRsn(rfxData);
		auctionBidService.updateAuctionBidItemsStl(rfxData, (List) param.get("rfxBidItems"));
		return ResultMap.SUCCESS();
	}

	/**
	 * 역경매 결과품의 존재여부 확인.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap checkAuctionResultApproval(Map param) {
		Map rfxData = (Map) param.get("rfxData");

		if(auctionRepository.checkAuctionResultApproval(rfxData) > 0) {
			return ResultMap.FAIL();
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 역경매 결과품의 아이디 조회.
	 *
	 * @param param the param
	 * @return the map
	 */
	public String findAuctionResultApprovalId(Map param) {
		return auctionRepository.findAuctionResultApprovalId(param);
	}

	/**
	 * 역경매를 유찰한다.
	 *
	 * @param param
	 * @return resultMap : 역경매 유찰 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 6. 8
	 * @Method Name : dropAuction
	 */
	public ResultMap dropAuction(Map param) {
		ResultMap validator = auctionValidator.validate(param);
		if(!validator.isSuccess()) {
			return validator;
		}

		this.deleteApproval(param);
		rfxStatusService.dropRfx(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 역경매를 취소한다.
	 *
	 * @param param
	 * @return resulMap : 역경매 취소 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016.12.01
	 * @Method Name : cancelAuction
	 */
	public ResultMap cancelAuction(Map param) {
		ResultMap validator = auctionValidator.validate(param);
		if(!validator.isSuccess()) {
			return validator;
		}

		rfxStatusService.cancelRfx(param);
		//취소 시 스케쥴러 서비스에서 기존 스케쥴러 삭제
		auctionSchedulerService.cancelAuction(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 역경매 업체 선정을 완료한다.
	 *
	 * @param param
	 * @return resultMap : 역경매 업체 선정 완료 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 6. 8
	 * @Method Name : bypassApprovalAuctionResult
	 */
	public ResultMap bypassApprovalAuctionResult(Map param) {
		Map rfxData = (Map) param.get("rfxData");

		ResultMap validator = auctionValidator.validate(rfxData);
		if(!validator.isSuccess()) {
			return validator;
		}

		this.deleteApproval(rfxData);
		Map auctionInfo = this.findAuction(rfxData);
		rfxStatusService.bypassApprovalRfxResult(rfxData);
		auctionItemService.updateAuctionItemStlInfo(auctionInfo);

		this.completeAuction(auctionInfo);
		return ResultMap.SUCCESS();
	}

	public void completeAuction(Map auctionInfo) {
		if(auctionInfo == null) {
			return;
		}

		List<Map> slctnVds = auctionBidService.findListAuctionBidSlctnByAuction(auctionInfo);
		for(Map slctnVd : slctnVds) {
			slctnVd.put("rfx_slctn_vd_uuid", UUID.randomUUID().toString());
			slctnVd.put("rfx_uuid", auctionInfo.get("rfx_uuid"));
			auctionRepository.insertAuctionSlctnVd(slctnVd);
		}
	}

	public void deleteApproval(Map rfxData) {
		if(rfxData == null) {
			return;
		}
		String deleteApprovalYn = (String) rfxData.get("deleteApprovalYn");
		if(Strings.isNullOrEmpty(deleteApprovalYn)) {
			return;
		}
		if(!"Y".equals(deleteApprovalYn)) {
			return;
		}

		String aprvId = auctionRepository.findAuctionResultApprovalId(rfxData);
		if(Strings.isNullOrEmpty(aprvId)) {
			return;
		}
		
		Map auctionHeader = this.findAuction(rfxData);

		Map deleteApproval = Maps.newHashMap();
		deleteApproval.put("apvl_uuid", aprvId);
		deleteApproval.put("task_uuid", auctionHeader.get("rfx_uuid"));
		String slctnApvlStsCcd = (String) ("APVL_CNCL".equals(auctionHeader.get("rfx_slctn_apvl_sts_ccd")) ? "CRNG" : auctionHeader.get("rfx_slctn_apvl_sts_ccd"));
		deleteApproval.put("prev_apvl_sts_ccd", slctnApvlStsCcd);

		Map aprvParam = Maps.newHashMap();
		aprvParam.put("deleteApproval", deleteApproval);
		approvalService.deleteApproval(aprvParam);
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

	public List<Map<String, Object>> findListAuctionReferenceIds(Map<String, Object> param) {
		return rfxStatusService.findListReferenceIds(param);
	}

	public List<Map<String, Object>> findListReferenceDocFromAUCTION(Map<String, Object> param) {
		return rfxStatusService.findListReferenceDocFromAUCTION(param);
	}

	public List<Map<String, Object>> findListReferenceDocFromAUCTIONByUPCR(Map<String, Object> param) {
		return rfxStatusService.findListReferenceDocFromAUCTIONByUPCR(param);
	}

	public List<Map<String, Object>> findListReferenceDocFromAUCTIONByPR(Map<String, Object> param) {
		return rfxStatusService.findListReferenceDocFromAUCTIONByPR(param);
	}

	public List<Map<String, Object>> findListReferenceDocFromAUCTIONByRfxItemUuids(Map<String, Object> param) {
		return rfxStatusService.findListReferenceDocFromAUCTIONByRfxItemUuids(param);
	}

	public List findListAuctionSlctnVd(Map param) {
		if(param == null) {
			return null;
		}

		return auctionRepository.findListAuctionSlctnVd(param);
	}

	public ResultMap saveAuctionSlctnNxtPrcs(Map param) {
		if(param == null) {
			return null;
		}

		List<Map> updateItems = (List<Map>) param.get("updateItems");
		for(Map updateItem : updateItems) {
			this.updateAuctionSlctnNxtPrc(updateItem);
		}

		return ResultMap.SUCCESS();
	}

	protected void updateAuctionSlctnNxtPrc(Map updateItem) {
		if(updateItem == null) {
			return;
		}
		String rfxNxtPrcsReqUuid = (String) updateItem.get("rfx_nxt_prcs_req_uuid");
		if(Strings.isNullOrEmpty(rfxNxtPrcsReqUuid)) {
			List auctionBidSlctnItemList = auctionBidService.findAuctionBidSlctnItemByAuctionAndVdCd(updateItem);

			Map param = Maps.newHashMap();
			param.put("rfxNxtPrcsReqData", updateItem);
			param.put("purcCntrData", updateItem);
			param.put("purcCntrInfoData", updateItem);
			param.put("insertItems", auctionBidSlctnItemList);
			ResultMap result = rfxNxtPrcsService.createRfxNxtPrcsReq(param);
			Map resultData = result.getResultData();
			updateItem.put("rfx_nxt_prcs_req_uuid", resultData.get("rfx_nxt_prcs_req_uuid"));
			auctionRepository.updateAuctionSlctnNxtPrc(updateItem);
		}
	}
}