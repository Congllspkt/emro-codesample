package smartsuite.app.bp.rfx.auction.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.auction.repository.AuctionItemRepository;
import smartsuite.app.bp.rfx.receipt.service.RfxReceiptService;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.app.common.util.ListUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class AuctionItemService {

	@Inject
	AuctionItemRepository auctionItemRepository;

	@Inject
	AuctionVendorItemService auctionVendorItemService;
	
	@Inject
	RfxReceiptService rfxReceiptService;

	@Inject
	RfxStatusService rfxStatusService;

	@Inject
	RfxEventPublisher rfxEventPublisher;

	@Inject
	SharedService sharedService;

	public List findListAuctionItem(Map param) {
		List<Map> auctionItems = auctionItemRepository.findListAuctionItem(param);

		List<String> prItemUuids = ListUtils.getArrayValuesByList(auctionItems, "pr_item_uuid");
		List<String> upcrItemUuids = ListUtils.getArrayValuesByList(auctionItems, "upcr_item_uuid");
		if(prItemUuids != null){
			List  prBaseList = rfxEventPublisher.findListPrByPrItems(prItemUuids);
			if(prBaseList != null) {
				auctionItems = ListUtils.leftOuterJoin(auctionItems, prBaseList, Lists.newArrayList("ten_id", "pr_item_uuid"), Lists.newArrayList("pr_uuid"));
			}
		}
		if(upcrItemUuids != null){
			List  upcrBaseList = rfxEventPublisher.findListUpcrByUpcrItems(prItemUuids);
			if(upcrBaseList != null) {
				auctionItems = ListUtils.leftOuterJoin(auctionItems, upcrBaseList, Lists.newArrayList("ten_id", "upcr_item_uuid"), Lists.newArrayList("upcr_uuid"));
			}
		}

		return auctionItems;
	}

	public void deleteAuctionItem(Map rfxData, List auctionItems) {
		if(auctionItems == null) {
			return;
		}
		if(auctionItems.isEmpty()) {
			return;
		}
		
		rfxReceiptService.cancelProcess(auctionItems);
		for(Map auctionItem : (List<Map>) auctionItems) {
			this.deleteAuctionItem(rfxData, auctionItem);
		}
	}

	public void deleteAuctionItem(Map rfxData, Map auctionItem) {
		if(auctionItem == null) {
			return;
		}

		int rfx_rnd = Integer.parseInt(rfxData.get("rfx_rnd").toString());
		if(rfx_rnd == 1) {
			//1차수 품목을 삭제하는 경우에만 PR 모듈에 삭제되었음을 알림
			rfxEventPublisher.deleteRfxItem(auctionItem);
		}
		auctionVendorItemService.deleteAuctionVendorItemsByRfxItem(auctionItem);
		auctionItemRepository.deleteAuctionItem(auctionItem);
	}

	public void insertAuctionItem(Map rfxData, List auctionItems) {
		if(rfxData == null) {
			return;
		}
		if(auctionItems == null) {
			return;
		}
		if(auctionItems.isEmpty()) {
			return;
		}

		//rfxItem 추가
		for(Map auctionItem : (List<Map>) auctionItems) {
			this.insertAuctionItem(rfxData, auctionItem);
		}

		auctionVendorItemService.insertAuctionVendorItemByAuctionItem(rfxData, auctionItems);
	}

	private void insertAuctionItem(Map rfxData, Map auctionItem) {
		if(rfxData == null) {
			return;
		}
		if(auctionItem == null) {
			return;
		}

		String rfxItemId = UUID.randomUUID().toString();
		auctionItem.put("oorg_cd", rfxData.get("oorg_cd"));
		auctionItem.put("rfx_item_uuid", rfxItemId);
		auctionItem.put("rfx_uuid", rfxData.get("rfx_uuid"));
		auctionItem.put("rfx_no", rfxData.get("rfx_no"));
		auctionItem.put("cur_ccd", rfxData.get("cur_ccd"));
		auctionItem.put("rfx_rnd", rfxData.get("rfx_rnd"));
		auctionItem.put("slctn_yn", "N");
		auctionItem.put("vd_stl_cause", "");

		// 미등록 품목(무코드 품목)인 경우, 무코드품목번호 채번
		/*if(this.isNoCodeItem(Integer.parseInt(rfxData.get("rfx_rnd").toString()), auctionItem)) {
			auctionItem.put("item_cd", sharedService.generateDocumentNumber("NOCDMT"));
		}*/

		auctionItemRepository.insertAuctionItem(auctionItem);
		
		int rfx_rnd = Integer.parseInt(rfxData.get("rfx_rnd").toString());
		if(rfx_rnd == 1) {
			rfxReceiptService.updateProgressStatus((String) auctionItem.get("rfx_req_rcpt_uuid"), "RA_PRGSG");
		}
	}

	/**
	 * 무코드 견적품목여부 판단
	 *
	 * @param rfxRev      - RFX 진행차수
	 * @param auctionItem - 견적품목
	 * @return boolean
	 */
	private boolean isNoCodeItem(Integer rfxRev, Map auctionItem) {
		boolean result = true;
		if(rfxRev > 1) {
			result = false;
		} else if(!Strings.isNullOrEmpty((String) auctionItem.get("item_cd"))) {
			result = false;
		} else if(!Strings.isNullOrEmpty((String) auctionItem.get("pr_item_uuid"))) {
			result = false;
		} else if(!"CDLS".equals(auctionItem.get("item_cd_crn_typ_ccd"))) {
			result = false;
		}
		return result;
	}

	public void updateAuctionItem(List auctionItems) {
		if(auctionItems == null) {
			return;
		}
		if(auctionItems.isEmpty()) {
			return;
		}

		for(Map auctionItem : (List<Map>) auctionItems) {
			this.updateAuctionItem(auctionItem);
		}
	}

	private void updateAuctionItem(Map auctionItem) {
		if(auctionItem == null) {
			return;
		}
		auctionItemRepository.updateAuctionItem(auctionItem);
	}

	public void deleteAuctionItemByAuction(Map param) {
		List auctionItems = this.findListAuctionItem(param);
		this.deleteAuctionItem(param, auctionItems);
	}

	public List findListAuctionItemByMultiRound(String rfxId) {
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", rfxId);
		param.put("ed_yn", "N");    //선정종료되지 않은 품목

		return this.findListAuctionItem(param);
	}

	public void updateAuctionItemStlInfo(Map auctionInfo) {
		if(auctionInfo == null) {
			return;
		}
		if(Strings.isNullOrEmpty((String) auctionInfo.get("rfx_uuid"))) {
			return;
		}

		List auctionItemStls = auctionItemRepository.selectAuctionItemStlInfo(auctionInfo);
		if(auctionItemStls.isEmpty()) {
			return;
		}

		for(Map auctionItemStl : (List<Map>) auctionItemStls) {
			auctionItemRepository.updateAuctionItemStlInfo(auctionItemStl);
		}
	}
}
