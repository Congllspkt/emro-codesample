package smartsuite.app.bp.rfx.rfx.service;

import org.springframework.stereotype.Service;
import smartsuite.app.bp.rfx.rfx.repository.RfxVendorItemRepository;

import javax.inject.Inject;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfxVendorItemService {
	
	@Inject
	RfxVendorItemRepository rfxVendorItemRepository;

	/**
	 * 견적품목과 견적협력사 간 연결고리 삭제한다.
	 * 
	 * @param rfxItems - 견적품목 다건
	 * @return void
	 */
	public void deleteRfxVendorItemByRfxItem(List<Map> rfxItems) {
		if(rfxItems == null) {
			return;
		}
		if(rfxItems.isEmpty()) {
			return;
		}
		
		for(Map rfxItem : rfxItems) {
			this.deleteRfxVendorItemByRfxItem(rfxItem);
		}
	}

	/**
	 * 
	 * @param rfxItem - 견적품목
	 * @return void
	 */
	public void deleteRfxVendorItemByRfxItem(Map rfxItem) {
		rfxVendorItemRepository.deleteRfxVendorItemByRfxItem(rfxItem);
	}

	/**
	 * 견적협력사와 견적품목 간 연결고리 삭제한다.
	 * 
	 * @param rfxVendors - 견적협력사 다건
	 * @return void
	 */
	public void deleteRfxVendorItemByRfxVendor(List<Map> rfxVendors) {
		if(rfxVendors == null) {
			return;
		}
		if(rfxVendors.isEmpty()) {
			return;
		}
		
		for(Map rfxVendor : rfxVendors) {
			this.deleteRfxVendorItemByRfxVendor(rfxVendor);
		}
	}

	/**
	 * 
	 * @param rfxVendor - 견적협력사
	 * @return void
	 */
	public void deleteRfxVendorItemByRfxVendor(Map rfxVendor) {
		rfxVendorItemRepository.deleteRfxVendorItemByVendor(rfxVendor);
	}

	/**
	 * 견적품목 저장 시 품목별 업체정보 저장
	 * 
	 * @param rfxData - 견적마스터
	 * @param rfxItems - 견적품목 다건
	 * @return void
	 */
	public void insertRfxVendorItemByRfxItem(Map rfxData, List rfxItems) {
		if(rfxData == null) {
			return;
		}
		if(rfxItems == null) {
			return;
		}
		if(rfxItems.isEmpty()) {
			return;
		}
		
		List rfxVendors = rfxVendorItemRepository.searchRfxVendorByRfx(rfxData);
		if(rfxVendors == null) {
			return;
		}
		if(rfxVendors.isEmpty()) {
			return;
		}
		
		for(Map rfxVendor : (List<Map>) rfxVendors) {
			for(Map rfxItem : (List<Map>) rfxItems) {
				rfxVendor.put("rfx_item_uuid", rfxItem.get("rfx_item_uuid"));
				rfxVendorItemRepository.insertRfxVendorItem(rfxVendor);
			}
		}
	}

	/**
	 * 
	 * @param rfxData - 견적마스터
	 * @param rfxVendors - 견적대상협력사 다건
	 */
	public void insertRfxVendorItemByRfxVendor(Map rfxData, List rfxVendors) {
		if(rfxData == null) {
			return;
		}
		if(rfxVendors == null) {
			return;
		}
		if(rfxVendors.isEmpty()) {
			return;
		}
		
		List rfxItems = rfxVendorItemRepository.searchRfxItemByRfx(rfxData);
		if(rfxItems == null) {
			return;
		}
		if(rfxItems.isEmpty()) {
			return;
		}
		
		for(Map rfxVendor : (List<Map>) rfxVendors) {
			for(Map rfxItem : (List<Map>) rfxItems) {
				rfxVendor.put("rfx_item_uuid", rfxItem.get("rfx_item_uuid"));
				rfxVendorItemRepository.insertRfxVendorItem(rfxVendor);
			}
		}
	}
}
