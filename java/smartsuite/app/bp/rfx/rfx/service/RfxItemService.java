package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.receipt.service.RfxReceiptService;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.bp.rfx.rfx.repository.RfxItemRepository;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.module.ModuleManager;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxItemService {
	
	@Inject
	RfxItemRepository rfxItemRepository;
	
	@Inject
	RfxItemPriceFactorService rfxItemPriceFactorService;
	
	@Inject
	RfxVendorItemService rfxVendorItemService;
	
	@Inject
	RfxReceiptService rfxReceiptService;
	
	@Inject
	RfxEventPublisher rfxEventPublisher;

	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	SharedService sharedService;

	private String pcmModule = "pcm";
	
	/**
	 * 기존재 견적품목 다건 삭제
	 *
	 * @param rfxItems - 견적품목 다건
	 * @return void
	 */
	public void deleteRfxItem(Map rfxData, List<Map> rfxItems) {
		if(rfxData == null) {
			return;
		}
		if(rfxItems == null) {
			return;
		}
		if(rfxItems.isEmpty()) {
			return;
		}
		
		int rfx_rnd = Integer.parseInt(rfxData.get("rfx_rnd").toString());
		if(rfx_rnd == 1) {
			//1차수 품목을 삭제하는 경우에만 PR 모듈에 삭제되었음을 알림
			rfxEventPublisher.deleteRfxItem(rfxItems);
			rfxReceiptService.cancelProcess(rfxItems);
		}
		rfxItemPriceFactorService.deletePriceFactorByRfxItem(rfxItems);
		rfxVendorItemService.deleteRfxVendorItemByRfxItem(rfxItems);
		for(Map rfxItem : rfxItems) {
			rfxItemRepository.deleteRfxItem(rfxItem);
		}
	}
	
	/**
	 * 기존재 견적품목 삭제
	 *
	 * @param rfxItem - 견적품목
	 * @return void
	 */
	public void deleteRfxItem(Map rfxData, Map rfxItem) {
		if(rfxData == null) {
			return;
		}
		if(rfxItem == null) {
			return;
		}

		rfxEventPublisher.deleteRfxItem(rfxItem);
		rfxItemPriceFactorService.deletePriceFactorByRfxItem(rfxItem);
		rfxVendorItemService.deleteRfxVendorItemByRfxItem(rfxItem);
		rfxItemRepository.deleteRfxItem(rfxItem);
	}
	
	/**
	 * 기존재 견적품목 다건 내용변경
	 *
	 * @param rfxItems - 견적품목 변경건 다건
	 * @return void
	 */
	public void updateRfxItem(List<Map> rfxItems) {
		if(rfxItems == null) {
			return;
		}
		if(rfxItems.isEmpty()) {
			return;
		}
		
		for(Map rfxItem : rfxItems) {
			this.updateRfxItem(rfxItem);
		}
	}
	
	/**
	 * 기존재 견적품목 내용변경
	 *
	 * @param rfxItem - 견적품목 변경건
	 * @return void
	 */
	public void updateRfxItem(Map rfxItem) {
		rfxItemRepository.updateRfxItem(rfxItem);
	}
	
	/**
	 * 견적품목 저장
	 *
	 * @param rfxData  - 견적 마스터
	 * @param rfxItems - 저장할 견적품목 다건
	 * @return void
	 */
	public void insertRfxItem(Map rfxData, List<Map> rfxItems) {
		if(rfxData == null) {
			return;
		}
		if(rfxItems == null) {
			return;
		}
		if(rfxItems.isEmpty()) {
			return;
		}
		
		for(Map rfxItem : rfxItems) {
			this.insertRfxItem(rfxData, rfxItem);
		}
		
		rfxVendorItemService.insertRfxVendorItemByRfxItem(rfxData, rfxItems);
	}
	
	private void insertRfxItem(Map rfxData, Map rfxItem) {
		String rfxItemId = UUID.randomUUID().toString();
		
		rfxItem.put("oorg_cd", rfxData.get("oorg_cd"));
		rfxItem.put("rfx_item_uuid", rfxItemId);
		rfxItem.put("rfx_uuid", rfxData.get("rfx_uuid"));
		rfxItem.put("rfx_no", rfxData.get("rfx_no"));
		rfxItem.put("cur_ccd", rfxData.get("cur_ccd"));
		rfxItem.put("rfx_rnd", rfxData.get("rfx_rnd"));
		rfxItem.put("slctn_yn", "N");
		rfxItem.put("vd_stl_cause", "");
		
		// 미등록 품목(무코드 품목)인 경우, 무코드품목번호 채번
		/*if(this.isNoCodeItem(Integer.parseInt(rfxData.get("rfx_rnd").toString()), rfxItem)) {
			rfxItem.put("item_cd", sharedService.generateDocumentNumber("NOCDMT"));
		}*/
		
		rfxItemRepository.insertRfxItem(rfxItem);
		
		int rfx_rnd = Integer.parseInt(rfxData.get("rfx_rnd").toString());
		if(rfx_rnd == 1) {
			rfxReceiptService.updateProgressStatus((String) rfxItem.get("rfx_req_rcpt_uuid"), (String) rfxData.get("rfx_typ_ccd") + "_PRGSG");
		}
	}
	
	/**
	 * 무코드 견적품목여부 판단
	 *
	 * @param rfxRev  - RFX 진행차수
	 * @param rfxItem - 견적품목
	 * @return boolean
	 */
	private boolean isNoCodeItem(Integer rfxRev, Map rfxItem) {
		boolean result = true;
		if(rfxRev > 1) {
			result = false;
		} else if(!Strings.isNullOrEmpty((String) rfxItem.get("item_cd"))) {
			result = false;
		} else if(!Strings.isNullOrEmpty((String) rfxItem.get("pr_item_uuid"))) {
			result = false;
		} else if(!"CDLS".equals(rfxItem.get("item_cd_crn_typ_ccd"))) {
			result = false;
		}
		return result;
	}
	
	/**
	 * list rfx item 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @author : Yeon-u Kim
	 * @Date : 2016. 5. 11
	 * @Method Name : searchRfxItemByRfx
	 */
	public List searchRfxItemByRfx(Map param) {
		return rfxItemRepository.searchRfxItemByRfx(param);
	}
	
	/**
	 * 견적마스터 대상으로 견적품목을 전부 삭제한다.
	 *
	 * @param rfxData - 삭제할 견적마스터
	 */
	public void deleteRfxItemByRfx(Map rfxData) {
		if(ModuleManager.exist(pcmModule) && "PSR".equals(rfxData.get("rfx_purp_ccd"))) {
			Map returnParam = Maps.newHashMap();
			Map<String, Object> rfxHeader = rfxStatusService.selectRfxHeaderForSr(rfxData);
			List<Map<String, Object>> srItems = rfxStatusService.selectSrItemIdByRfx(rfxData);

			//삭제시에는 접수로 보냄
			rfxHeader.put("rfx_sts_ccd", "RCPT");
			returnParam.put("header", rfxHeader);
			returnParam.put("items", srItems);

			rfxEventPublisher.updateProgStsFromRfx(returnParam);
		} else {
			List rfxItems = this.searchRfxItemByRfx(rfxData);
			this.deleteRfxItem(rfxData, rfxItems);
		}

	}
	
	/**
	 * multi round 생성을 위한 견적요청 품목 데이터 조회한다.<br>
	 * 선정 종료되지 않은 품목만 조회한다.
	 *
	 * @param rfxId - 견적요청 ID
	 * @return List - 견적요청품목 다건
	 */
	public List searchRfxItemByMultiRound(String rfxId) {
		if(rfxId == null) {
			return null;
		}
		
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", rfxId);
		param.put("ed_yn", "N");
		
		return this.searchRfxItemByRfx(param);
	}
	
	/**
	 * 견적요청품목 복사를 위한 견적요청품목 클리어 데이터를 반환한다.<br><br>
	 *
	 * @param rfxData - 견적요청 마스터
	 * @return List
	 */
	public List getCopyRfxItems(Map rfxData) {
		if(rfxData == null) {
			return null;
		}
		
		List rfxItems = rfxItemRepository.searchRfxItemByRfx(rfxData);
		//rfx 복사 시 pr정보 삭제
		for(Map rfxItem : (List<Map>) rfxItems) {
			rfxItem.put("rfx_item_uuid", null);
			rfxItem.put("pr_no", null);
			rfxItem.put("pr_item_uuid", null);
			rfxItem.put("pr_lno", null);
			rfxItem.put("rfx_uuid", null);
			rfxItem.put("rfx_no", null);
			rfxItem.put("rfx_rnd", 1);
			rfxItem.put("slctn_yn", "N");
			rfxItem.put("slctn_qty", null);
			rfxItem.put("ed_yn", "N");
			if("CDLS".equals((String) rfxItem.get("item_cd_crn_typ_ccd"))) {
				rfxItem.put("item_cd", null);
			}
		}
		return rfxItems;
	}
	
	/**
	 * 견적 결과품의 승인 시 견적품목 별 선정 여부 및 선정수량 업데이트한다.<br><br>
	 * <b>Required:</b><br>
	 * rfxData.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param rfxData
	 */
	public void updateRfxItemStlInfo(Map rfxData) {
		if(rfxData == null) {
			return;
		}
		if(Strings.isNullOrEmpty((String) rfxData.get("rfx_uuid"))) {
			return;
		}
		
		List rfxItemStls = rfxItemRepository.selectRfxItemStlInfo(rfxData);
		if(rfxItemStls.isEmpty()) {
			return;
		}
		
		for(Map rfxItemStl : (List<Map>) rfxItemStls) {
			rfxItemRepository.updateRfxItemStlInfo(rfxItemStl);
		}
	}
	
	public List findListRfxItemDefaultDataByAiItemIds(Map param) {
		return rfxItemRepository.findListRfxItemDefaultDataByAiItemIds(param);
	}
}
