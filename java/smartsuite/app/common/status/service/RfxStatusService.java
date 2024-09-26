package smartsuite.app.common.status.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.common.status.repository.RfxStatusRepository;
import smartsuite.module.ModuleManager;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * PRO 모듈 상태값 변경 관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @see
 * @FileName ProStatusProcessor.java
 * @package smartsuite.app.shared
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfxStatusService {

	@Inject
	RfxStatusRepository rfxStatusRepository;
	
	@Inject
	RfxEventPublisher rfxEventPublisher;

	private String pcmModule = "pcm";
	
	/**
	 * RFI 저장 시 상태변경 처리를 한다
	 *
	 * @author kh_lee
	 * @param param
	 * @Date : 2017. 5.30
	 * @Method Name : temporarySaveRfi
	 */
	public void temporarySaveRfi(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfi_uuid", ""))) {
			return;
		}
		rfxStatusRepository.temporarySaveRfi(param);
		
		Map evenParam = this.makeEventParamFindItemIdByRfi(param);
		if(evenParam == null) {
			return;
		}
		rfxEventPublisher.temporarySaveRfi(evenParam);
	}

	/**
	 * RFI 요청 시 상태변경 처리를 한다
	 *
	 * @author : kh_lee
	 * @param param
	 * @Date : 2017. 5.30
	 * @Method Name : requestRfi
	 */
	public void requestRfi(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfi_uuid", ""))) {
			return;
		}
		rfxStatusRepository.requestRfi(param);
		
		Map evenParam = this.makeEventParamFindItemIdByRfi(param);
		if(evenParam == null) {
			return;
		}
		rfxEventPublisher.requestRfi(evenParam);
	}

	/**
	 * RFI 마감 시 상태변경 처리를 한다
	 *
	 * @author : kh_lee
	 * @param param
	 * @Date : 2017. 5.30
	 * @Method Name : closeRfi
	 */
	public void closeRfi(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfi_uuid", ""))) {
			return;
		}
		rfxStatusRepository.closeRfi(param);
	}

	/**
	 * RFI 종료 시 상태변경 처리를 한다
	 *
	 * @author : kh_lee
	 * @param param
	 * @Date : 2017. 5.30
	 * @Method Name : completeRfi
	 */
	public void completeRfi(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfi_uuid", ""))) {
			return;
		}
		rfxStatusRepository.completeRfi(param);
		
		Map evenParam = this.makeEventParamFindItemIdByRfi(param);
		if(evenParam == null) {
			return;
		}
		rfxEventPublisher.completeRfi(evenParam);
	}

	/**
	 * RFI 삭제 시 상태변경 처리를 한다
	 *
	 * @author kh_lee
	 * @param param
	 * @Date : 2017. 5.30
	 * @Method Name : deleteRfi
	 */
	public void deleteRfi(Map param) {
		Map evenParam = this.makeEventParamFindItemIdByRfi(param);
		if(evenParam == null) {
			return;
		}
		rfxEventPublisher.deleteRfiItem(evenParam);
	}

	/**
	 * RFI 품목 삭제 시 상태변경 처리를 한다
	 *
	 * @author kh_lee
	 * @param param
	 * @Date : 2017. 5.30
	 * @Method Name : deleteRfiItem
	 */
	public void deleteRfiItem(Map param) {
		Map evenParam = this.makeEventParamFindItemIdByRfi(param);
		if(evenParam == null) {
			return;
		}
		rfxEventPublisher.deleteRfiItem(evenParam);
	}

	private Map makeEventParamFindItemIdByRfi(Map param){
		Map eventParam = Maps.newHashMap();
		List<String> prItemIds = rfxStatusRepository.selectPrItemIdByRfi(param);
		List<String> upcrItemIds = rfxStatusRepository.selectUpcrItemIdByRfi(param);

		if(prItemIds.size() == 0 && upcrItemIds.size() == 0) {
			return null;
		}
		if(prItemIds.size() != 0){
			eventParam.put("pr_item_uuids", prItemIds);
		}
		if(upcrItemIds.size() != 0){
			eventParam.put("upcr_item_uuids", upcrItemIds);
		}
		return eventParam;
	}
	
	/**
	 * RFx 저장 시 상태변경 처리를 한다<br><br>
	 * 
	 * @param rfx_uuid - 견적요청 ID
	 * @return void
	 */
	public void saveDraftRfx(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.saveDraftRfx(param);

		// 요청목적이 SR인경우 분기
		if(ModuleManager.exist(pcmModule) &&"PSR".equals(param.get("rfx_purp_ccd"))) {
			Map srParam = this.makeEventParamFindItemsByRfxForSr(param);
			rfxEventPublisher.updateProgStsFromRfx(srParam);
		} else {
			Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
			if(eventParam == null){
				return;
			}
			// 견적요청 임시저장에 따라 구매요청 품목 ID 존재하는 경우 구매요청 모듈에 알린다.
			rfxEventPublisher.saveDraftRfx(eventParam);
		}
	}

	/**
	 * RFx 삭제 시 상태변경 처리를 한다<br><br>
	 * <b>Required:</b><br>
	 * param.prev_rfx_uuid - 이전 견적요청 ID<br>
	 * 
	 * @param param
	 * @return void
	 */
	public void deleteRfx(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("pre_rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.updatePreRfxByDeleteCurrentRfx(param);
	}

	/**
	 * RFx 진행품의 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalRfxProg
	 */
	public void submitApprovalRfxProg(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.submitApprovalRfxProg(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.submitApprovalRfxProg(eventParam);
	}

	/**
	 * RFx 진행품의 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalRfxProg
	 */
	public void approveApprovalRfxProg(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.approveApprovalRfxProg(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.approveApprovalRfxProg(eventParam);
	}

	/**
	 * RFx 진행품의 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalRfx
	 */
	public void rejectApprovalRfxProg(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.rejectApprovalRfxProg(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.rejectApprovalRfxProg(eventParam);
	}

	/**
	 * RFx 진행품의 결재취소/삭제 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalRfxProg
	 */
	public void cancelApprovalRfxProg(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.cancelApprovalRfxProg(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.cancelApprovalRfxProg(eventParam);
	}

	/**
	 * RFx 진행품의 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.xxx - 속성 설명<br>
	 * param.zzz - 속성 설명
	 * 
	 * @param param
	 * @return void
	 */
	public void bypassApprovalRfxProg(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.bypassApprovalRfxProg(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.bypassApprovalRfxProg(eventParam);
	}

	/**
	 * RFx 시작 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : startRfx
	 */
	public void startRfx(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.startRfx(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.startRfx(eventParam);
	}

	/**
	 * RFx 마감 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br><br>
	 * 
	 * <b>Option:</b><br>
	 * param.ery_clsg_rsn - 강제마감 사유
	 * 
	 * @param param
	 * @return void
	 */
	public void closeRfx(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.closeRfx(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.closeRfx(eventParam);
	}
	
	/**
	 * RFx 개찰 시 상태변경 처리를 한다
	 * 
	 * @param param
	 * @Method Name : openRfx
	 */
	public void openRfx(Map param) {
		rfxStatusRepository.openRfx(param);
	}
	
	/**
	 * 비가격평가 재수행
	 *
	 * @param param
	 */
	public void reExecuteNpeEval(Map param) {
		if(param == null) {
			return;
		}
		
		rfxStatusRepository.reExecuteNpeEval(param);
	}

	/**
	 * RFx 유찰 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 * param.rfx_no - 견적요청 번호<br>
	 * param.rfx_rnd - 견적요청 차수<br><br>
	 * 
	 * <b>Option:</b><br>
	 * param.faildbid_rsn - 유찰 사유
	 * 
	 * @param param
	 * @return void
	 */
	public void dropRfx(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_no", ""))) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_rnd", ""))) {
			return;
		}
		rfxStatusRepository.dropRfxHd(param);
		rfxStatusRepository.dropRfxQtaHd(param);
		rfxStatusRepository.dropRfxQtaDt(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.dropRfx(eventParam);
	}

	/**
	 * RFx 취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : dropRfx
	 */
	public void cancelRfx(Map param){
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		
		//Transaction이 끝나므로 아래 status변경 전에 위에서 먼저 조회해야함
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);

		rfxStatusRepository.cancelRfxHd(param);
		rfxStatusRepository.cancelRfxQtaHd(param);
		rfxStatusRepository.cancelRfxQtaDt(param);
		rfxStatusRepository.cancelRfxItems(param);
		
		List<String> rfxReqRcptUuids = rfxStatusRepository.findListRfxReqRcptUuidByRfx(param);
		if(!rfxReqRcptUuids.isEmpty()) {
			Map reqParam = Maps.newHashMap();
			reqParam.put("rfx_req_rcpt_uuids", rfxReqRcptUuids);
			rfxStatusRepository.updateRfxReqRcptByCancelRfxItem(reqParam);
		}

		if(eventParam != null) {
			rfxEventPublisher.cancelRfx(eventParam);
		}
	}

	/**
	 * RFx multi-round(재견적/입찰) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 * 
	 * @param param
	 * @param rfxItems 
	 * @return void
	 */
	public void multiRoundRfx(Map param, List rfxItems) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.multiRoundRfxHd(param);
		rfxStatusRepository.multiRoundRfxQtaHd(param);
		rfxStatusRepository.multiRoundRfxQtaDt(param);
		rfxEventPublisher.multiRoundRfx(rfxItems);
	}

	/**
	 * RFx 선정품의 결재 저장 시 상태변경 처리를 한다.
	 *
	 * @param param
	 */
	public void temporarySaveApprovalRfxResult(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.temporarySaveApprovalRfxResult(param);
	}

	/**
	 * RFx 선정품의 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalRfxResult
	 */
	public void submitApprovalRfxResult(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.submitApprovalRfxResult(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.submitApprovalRfxResult(eventParam);
	}

	/**
	 * RFx 선정품의 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalRfxResult
	 */
	public void approveApprovalRfxResult(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.approveApprovalRfxResult(param);

		if(ModuleManager.exist(pcmModule) && "PSR".equals(param.get("rfx_purp_ccd"))) {
			Map eventParam = this.makeEventParamStlItemsByRfxForSr(param);
			if(eventParam == null){
				return;
			}
			rfxEventPublisher.updateProgStsFromRfx(eventParam);
		} else {
			Map eventParam = this.makeEventParamStlItemsByRfx(param);
			if(eventParam == null){
				return;
			}
			rfxEventPublisher.bypassApprovalRfxResult(eventParam);
		}
	}

	/**
	 * RFx 선정품의 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalRfxResult
	 */
	public void rejectApprovalRfxResult(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.rejectApprovalRfxResult(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.rejectApprovalRfxResult(eventParam);
	}

	private Map makeEventParamFindItemsByRfxAndOther(Map param){
		Map eventParam = Maps.newHashMap();
		List<String> prItemIds = rfxStatusRepository.selectPrItemIdByRfx(param);
		List<String> upcrItemIds = rfxStatusRepository.selectUpcrItemIdByRfx(param);
		if(prItemIds.size() == 0 && upcrItemIds.size() == 0) {
			return null;
		}
		if(prItemIds.size() != 0){
			eventParam.put("pr_item_uuids", prItemIds);
		}
		if(upcrItemIds.size() != 0){
			eventParam.put("upcr_item_uuids", upcrItemIds);
		}
		return eventParam;
	}

	private Map makeEventParamFindItemsByRfxForSr(Map param){
		Map returnParam = Maps.newHashMap();
		Map<String, Object> rfxHeader = this.selectRfxHeaderForSr(param);
        List<Map<String, Object>> srItems = this.selectSrItemIdByRfx(param);

		returnParam.put("header", rfxHeader);
		returnParam.put("items", srItems);
        return returnParam;
	}

	public Map selectRfxHeaderForSr(Map param) {
		return rfxStatusRepository.selectRfxHeaderForSr(param);
	}

	public List selectSrItemIdByRfx(Map param) {
		return rfxStatusRepository.selectSrItemIdByRfx(param);
	}

	/**
	 * RFx 선정품의 결재취소/삭제 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalRfxResult
	 */
	public void cancelApprovalRfxResult(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.cancelApprovalRfxResult(param);
		
		Map eventParam = this.makeEventParamFindItemsByRfxAndOther(param);
		if(eventParam == null){
			return;
		}
		rfxEventPublisher.cancelApprovalRfxResult(eventParam);
	}

	/**
	 * RFx 선정품의 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalRfxResult
	 */
	public void bypassApprovalRfxResult(Map param) {
		if(param == null) {
			return;
		}
		if("".equals(param.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.bypassApprovalRfxResult(param);

		if(ModuleManager.exist(pcmModule) && "PSR".equals(param.get("rfx_purp_ccd"))) {
			Map eventParam = this.makeEventParamStlItemsByRfxForSr(param);
			if(eventParam == null){
				return;
			}
			rfxEventPublisher.updateProgStsFromRfx(eventParam);
		} else {
			Map eventParam = this.makeEventParamStlItemsByRfx(param);
			if(eventParam == null){
				return;
			}
			rfxEventPublisher.bypassApprovalRfxResult(eventParam);
		}

	}

	private Map makeEventParamStlItemsByRfx(Map param){
		Map eventParam = Maps.newHashMap();
		List<Map> prItemInfos = rfxStatusRepository.selectStlPrItemIdByRfx(param);
		List<Map> upcrItemInfos = rfxStatusRepository.selectStlUpcrItemIdByRfx(param);
		eventParam.put("rfx_purp_ccd",param.get("rfx_purp_ccd"));

		if(prItemInfos.size() == 0 && upcrItemInfos.size() == 0) {
			return null;
		}
		if(prItemInfos.size() != 0){
			List<String> selectedItems = Lists.newArrayList();
			List<String> nonSelectedItems = Lists.newArrayList();
			for(Map prItemInfo : prItemInfos) {
				if("Y".equals(prItemInfo.get("slctn_yn"))) {
					selectedItems.add((String) prItemInfo.get("pr_item_uuid"));
				} else if("N".equals(prItemInfo.get("slctn_yn"))) {
					nonSelectedItems.add((String) prItemInfo.get("pr_item_uuid"));
				}
			}
			eventParam.put("selectedPrItems", selectedItems);
			eventParam.put("nonSelectedPrItems", nonSelectedItems);
		}
		if(upcrItemInfos.size() != 0){
			List<String> selectedUpcrItems = Lists.newArrayList();
			List<String> nonSelectedUpcrItems = Lists.newArrayList();
			for(Map upcrItemInfo : upcrItemInfos) {
				if("Y".equals(upcrItemInfo.get("slctn_yn"))) {
					selectedUpcrItems.add((String) upcrItemInfo.get("upcr_item_uuid"));
				} else if("N".equals(upcrItemInfo.get("slctn_yn"))) {
					nonSelectedUpcrItems.add((String) upcrItemInfo.get("pr_item_uuid"));
				}
			}
			eventParam.put("selectedUpcrItems", selectedUpcrItems);
			eventParam.put("nonSelectedUpcrItems", nonSelectedUpcrItems);
		}
		return eventParam;
	}

	private Map makeEventParamStlItemsByRfxForSr(Map param){
		Map eventParam = Maps.newHashMap();
		List<Map> srItemList = rfxStatusRepository.selectStlSrItemIdByRfx(param);
		Map rfxHeader = rfxStatusRepository.selectRfxHeaderForSr(param);

		if(srItemList.size() == 0) {
			return null;
		}
		if(srItemList.size() != 0){
			eventParam.put("items", srItemList);
			eventParam.put("header", rfxHeader);
		}
		return eventParam;
	}

	/**
	 * RFx 선정 취소시 상태변경 처리를 한다
	 *
	 * @param param
	 * <br>
	 * param.rfx_bid_item_uuids - 선정 업체 품목 UUIDs<br>
	 * param.rfx_bid_uuids - 선정 업체 UUIDs<br>
	 * param.rfx_item_uuids - 선정 품목 UUIDs<br>
	 */
	public void cancelRfxResult(Map param) {
		rfxStatusRepository.cancelRfxQtaResultDt(param);
		rfxStatusRepository.cancelRfxQtaResultHd(param);
		rfxStatusRepository.cancelRfxResultDt(param);
		
		//협상존재시 취소 로직
		param.put("rfx_sts_ccd", "CLSG");
		param.put("apvl_typ_ccd", "RFX_VD_SLCTN");
		this.cancelNegoResult(param);
		
		// 품목 종료 건수
		int rfxItemEndFlagCnt = rfxStatusRepository.findCountItemEndFlagByRfx(param);
		// 품목 선정 건수
		int rfxItemSlctnCnt = rfxStatusRepository.findCountItemSlctnByRfx(param);
		if(rfxItemEndFlagCnt == 0 && rfxItemSlctnCnt == 0) {
			// 모든 품목이 종료 상태가 아니고 모든 품목의 선정이 취소되었으므로 마감시점으로 되돌림
			rfxStatusRepository.cancelRfxResultHd(param);
			rfxStatusRepository.cancelRfxResultAprv(param);
		} else {
			// 선정 취소된 품목 중 선정되어 있는 업체의 품목으로 존재하는지 확인
			List<Map> existSlctnItemList = rfxStatusRepository.findListVdSlctnByCancelRfxItem(param);
			
			List<String> rfxItemUuids = (List<String>) param.get("rfx_item_uuids");
			List<String> rfxReqRcptUuids = (List<String>) param.get("rfx_req_rcpt_uuids");
			List<String> prItemUuids = (List<String>) param.get("pr_item_uuids");
			List<String> upcrItemUuids = (List<String>) param.get("upcr_item_uuids");
			
			// 특정 품목의 선정된 타 업체가 존재하는 경우 해당 품목은 종료 처리 및 RFX 요청 목록 / 구매요청 / 단가계약 요청 back process 수행하지 않는다.
			for(Map existSlctnItem : existSlctnItemList) {
				rfxReqRcptUuids.remove(existSlctnItem.get("rfx_req_rcpt_uuid"));
				prItemUuids.remove(existSlctnItem.get("pr_item_uuid"));
				upcrItemUuids.remove(existSlctnItem.get("upcr_item_uuid"));
			}
			
			// 이미 종료 되어 back process 진행된 품목이 존재하거나 취소 이후에도 선정 품목이 존재하므로 이 경우 취소 품목 종료 처리 후 RFX 요청 목록으로 되돌림
			if(!rfxItemUuids.isEmpty()) {
				rfxStatusRepository.updateRfxItemEnd(param);
			}
			
			// 품목 별 종료 후 RFX 요청 목록으로 되돌림
			//List<String> rfxReqRcptUuids = (List<String>) param.get("rfx_req_rcpt_uuids");
			if(!rfxReqRcptUuids.isEmpty()) {
				rfxStatusRepository.updateRfxReqRcptByCancelRfxItem(param);
			}
			
			rfxEventPublisher.cancelRfx(param);
		}
	}
	
	/**
	 * RFx 품목 종결처리 시 상태변경 처리를 한다
	 *
	 * @param param
	 * @Method Name : closeRfxItemResult
	 */
	public void closeRfxItemResult(Map param) {
		rfxStatusRepository.cancelRfxQtaResultDt(param);

		List rfxBids = rfxStatusRepository.selectRfxQtaResultHd(param);
		for(Map rfxBid : (List<Map>) rfxBids) {
			rfxBid.put("slctn_cncl_rsn", param.get("slctn_cncl_rsn"));
			rfxStatusRepository.closeRfxQtaResultHd(rfxBid);
		}
		
		List rfxItems = rfxStatusRepository.selectRfxResultDt(param);
		for(Map rfxItem : (List<Map>)rfxItems ) {
			rfxStatusRepository.closeRfxResultDt(rfxItem);
		}
		
		if(param.get("pr_item_uuids") != null) {
			List prItemIds = rfxStatusRepository.selectRfxResultPrDt(param);
			if(!prItemIds.isEmpty()){
				Map eventParam = Maps.newHashMap();
				eventParam.put("pr_item_uuids",prItemIds);
				rfxEventPublisher.closeRfxResult(eventParam);
			}
		}else if(!ObjectUtils.isEmpty(param.get("upcr_item_uuids"))) {
			List upcrItemIds = rfxStatusRepository.selectRfxResultUpcrDt(param);
			if(!upcrItemIds.isEmpty()){
				Map eventUpcrParam = Maps.newHashMap();
				eventUpcrParam.put("upcr_item_uuids",upcrItemIds);
				rfxEventPublisher.closeRfxResult(eventUpcrParam);
			}
		}else{
			//구매요청이없는경우 N SMARTNINE-5328
			rfxStatusRepository.cancelRfxResultDt(param);
		}
	}
	
	/**
	 * 협상 생성 시 견적요청 진행상태 변경<br><br>
	 * <b>Required:</b><br>
	 * rfxData.rfx_uuid - 견적요청 ID<br>
	 * 
	 * @param rfxData
	 */
	public void saveNego(Map rfxData) {
		if(rfxData == null) {
			return;
		}
		if("".equals(rfxData.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.saveNego(rfxData);
	}
	
	/**
	 * 협상 취소 시 견적요청 진행상태 변경<br><br>
	 * <b>Required:</b><br>
	 * rfxData.rfx_uuid - 견적요청 ID<br>
	 * 
	 * @param rfxData
	 */
	public void cancelNego(Map rfxData) {
		if(rfxData == null) {
			return;
		}
		if("".equals(rfxData.getOrDefault("rfx_uuid", ""))) {
			return;
		}
		rfxStatusRepository.cancelNego(rfxData);
	}
	
	/**
	 * 협상 타결 시 협상상태 업데이트<br><br>
	 * 
	 * @param negoData
	 */
	public void negoSuccess(Map negoData) {
		if(negoData == null) {
			return;
		}
		rfxStatusRepository.negoSuccess(negoData);
	}

	/**
	 * 협상 결렬 시 협상상태 업데이트<br><br>
	 * 
	 * @param negoData
	 */
	public void negoFailure(Map negoData) {
		if(negoData == null) {
			return;
		}
		rfxStatusRepository.negoFailure(negoData);
	}
	
	/**
	 * 협상 여부에 따라 견적요청 진행상태 변경 : 마감 -> 협상진행중으로 변경
	 * 
	 * @param rfxId
	 */
	public void updateRfxProgStsByNegoSts(String rfxId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", rfxId);
		
		rfxStatusRepository.updateRfxProgStsByNegoSts(param);
	}
	
	/**
	 * 협상 종료 시 협상상태 업데이트<br><br>
	 * 
	 * @param param
	 */
	public void updateCloseNego(Map param) {
		rfxStatusRepository.updateCloseNego(param);
	}

	/**
	 * 단가계약 / 발주 작성대기 상태 건 생성시 상태변경 처리를 한다
	 */
	public void createPriceContractAndPo(Map param) {
		rfxStatusRepository.closeRfxItems(param);
	}
	
	protected List<Map> findListReferenceDoc(Map<String,Object> param) {
		List<Map> docList = Lists.newArrayList();
		/*
		 * if("PR".equals(param.get("appType"))) docList =
		 * sqlSession.selectList(NAMESPACE+"findListReferenceDocFromPR",param); else
		 * if("RFX".equals(param.get("appType"))) docList =
		 * sqlSession.selectList(NAMESPACE+"findListReferenceDocFromRFX",param); else
		 * if("AUCTION".equals(param.get("appType"))) docList =
		 * sqlSession.selectList(NAMESPACE+"findListReferenceDocFromRFX",param); else
		 * if("PO".equals(param.get("appType"))) docList =
		 * sqlSession.selectList(NAMESPACE+"findListReferenceDocFromPO",param); else
		 * if("CONTRACT".equals(param.get("appType"))) docList =
		 * sqlSession.selectList(NAMESPACE+"findListReferenceDocFromContract",param);
		 * else if("AR".equals(param.get("appType"))) docList =
		 * sqlSession.selectList(NAMESPACE+"findListReferenceDocFromAR",param); else
		 * if("GR".equals(param.get("appType"))) docList =
		 * sqlSession.selectList(NAMESPACE+"findListReferenceDocFromGR",param);
		 */

		return docList;
	}
	
	/**
	 * RFX 평가 진행 상태를 시작으로 변경한다.<br><br>
	 * <b>Option:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 */
	public void startRfxEval(Map param){
		if(param == null) {
			return;
		}
		rfxStatusRepository.startRfxEval(param);
	}
	
	/**
	 * RFX 평가 진행 상태를 완료로 변경한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br><br>
	 * 
	 * @param param
	 */
	public void completeRfxEval(Map param){
		if(param == null) {
			return;
		}
		rfxStatusRepository.completeRfxEval(param);
	}
	
	public void saveDraftBidNoti(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.saveDraftBidNoti(param);
	}
	
	public void submitApprovalBidNoti(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.submitApprovalBidNoti(param);
	}
	
	public void rejectApprovalBidNoti(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.rejectApprovalBidNoti(param);
	}
	
	public void cancelApprovalBidNoti(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.cancelApprovalBidNoti(param);
	}
	
	public void approvalBidNotiProg(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.approvalBidNotiProg(param);
	}
	
	public void closeBidNoti(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.closeBidNoti(param);
	}
	
	public void waitBidNoti(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.waitBidNoti(param);
	}
	
	public void processBidNoti(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.processBidNoti(param);
	}
	
	public void startBidNotiProg(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.startBidNotiProg(param);
	}
	
	public void closeBidNotiProg(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.closeBidNotiProg(param);
	}
	
	public void cancelBidNoti(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.cancelBidNoti(param);
	}
	
	public void saveDraftBidNotiTimeChange(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.saveDraftBidNotiTimeChange(param);
	}
	
	public void passBidNotiTimeChange(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.passBidNotiTimeChange(param);
	}
	
	public void correctPrevBidNoti(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.correctPrevBidNoti(param);
	}
	
	public void progressReBidNoti(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.progressReBidNoti(param);
	}
	
	public void saveSiteBriefingProgSts(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.saveSiteBriefingProgSts(param);
	}
	
	public void notifySiteBriefingProgSts(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.notifySiteBriefingProgSts(param);
	}
	
	public void cancelSiteBriefingProgSts(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.cancelSiteBriefingProgSts(param);
	}
	
	public void closeSiteBriefingProgSts(Map param) {
		if(param == null) {
			return;
		}
		rfxStatusRepository.closeSiteBriefingProgSts(param);
	}

	public List<Map<String, Object>> findListReferenceIds(Map<String, Object> param) {
		return rfxStatusRepository.findListReferenceIds(param);
	}

	public List<Map<String, Object>> findListReferenceDocFromRFX(Map<String, Object> param) {
		return rfxStatusRepository.findListReferenceDocFromRFX(param);
	}

	public List<Map<String, Object>> findListReferenceDocFromRFXByPR(Map<String, Object> param) {
		return rfxStatusRepository.findListReferenceDocFromRFXByPR(param);
	}

	public List<Map<String, Object>> findListReferenceDocFromRFXByUPCR(Map<String, Object> param) {
		return rfxStatusRepository.findListReferenceDocFromRFXByUPCR(param);
	}
	public List<Map<String, Object>> findListReferenceDocFromRFXByRfxItemUuids(Map<String, Object> param) {
		return rfxStatusRepository.findListReferenceDocFromRFXByRfxItemUuids(param);
	}
	
	/**
	 * 협상 취소 로직
	 * @param param
	 */
	private void cancelNegoResult(Map param) {
		Boolean isNegoRfx = false;
		
		if(null != param.get("rfx_typ_ccd") && "RFP".equals(param.get("rfx_typ_ccd"))){
			isNegoRfx = rfxStatusRepository.isNegoRfx(param);
		}
		
		if(isNegoRfx) {
			// 협상데이터가 존재하면
			param.put("rfx_sts_ccd", "NEGO_PRGSG");
			param.put("apvl_typ_ccd", "NEGO");
			rfxStatusRepository.initNegoStlYN(param);
		}
	}

	public List<Map<String, Object>> findListReferenceDocFromAUCTION(Map<String, Object> param) {
		return rfxStatusRepository.findListReferenceDocFromAUCTION(param);
	}

	public List<Map<String, Object>> findListReferenceDocFromAUCTIONByPR(Map<String, Object> param) {
		return rfxStatusRepository.findListReferenceDocFromAUCTIONByPR(param);
	}

	public List<Map<String, Object>> findListReferenceDocFromAUCTIONByUPCR(Map<String, Object> param) {
		return rfxStatusRepository.findListReferenceDocFromAUCTIONByUPCR(param);
	}
	public List<Map<String, Object>> findListReferenceDocFromAUCTIONByRfxItemUuids(Map<String, Object> param) {
		return rfxStatusRepository.findListReferenceDocFromAUCTIONByRfxItemUuids(param);
	}
}
