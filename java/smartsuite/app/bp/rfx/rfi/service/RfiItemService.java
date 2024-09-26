package smartsuite.app.bp.rfx.rfi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.receipt.service.RfxReceiptService;
import smartsuite.app.bp.rfx.rfi.event.RfiEventPublisher;
import smartsuite.app.bp.rfx.rfi.repository.RfiItemRepository;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfiItemService {
	
	@Inject
	RfxReceiptService rfxReceiptService;

	@Inject
	RfiItemRepository rfiItemRepository;

	@Inject
	RfiEventPublisher rfiEventPublisher;

	@Inject
	RfxStatusService rfxStatusService;

	@Inject
	SharedService sharedService;

	public void deleteRfiItem(List rfiItems) {
		if(rfiItems == null) {
			return;
		}
		if(rfiItems.isEmpty()) {
			return;
		}
		
		rfxReceiptService.cancelProcess(rfiItems);
		for(Map rfiItem : (List<Map>) rfiItems) {
			this.deleteRfiItem(rfiItem);
		}
	}

	private void deleteRfiItem(Map rfiItem) {
		if(rfiItem == null) {
			return;
		}

		rfxStatusService.deleteRfiItem(rfiItem);
		rfiItemRepository.deleteRfiItem(rfiItem);
	}

	public void insertRfiItem(Map rfiData, List rfiItems) {
		if(rfiData == null) {
			return;
		}
		if(rfiItems == null) {
			return;
		}
		if(rfiItems.isEmpty()) {
			return;
		}

		for(Map rfiItem : (List<Map>) rfiItems) {
			this.insertRfiItem(rfiData, rfiItem);
		}
	}

	private void insertRfiItem(Map rfiData, Map rfiItem) {
		if(rfiData == null) {
			return;
		}
		if(rfiItem == null) {
			return;
		}

		rfiItem.put("rfi_item_uuid", UUID.randomUUID().toString());
		rfiItem.put("rfi_uuid", rfiData.get("rfi_uuid"));
		rfiItem.put("rfi_no", rfiData.get("rfi_no"));
		rfiItem.put("oorg_cd", rfiData.get("oorg_cd"));

		// 미등록 품목(무코드 품목)인 경우, 무코드품목번호 채번
		/*String prItemId = (String) rfiItem.get("pr_item_uuid");
		if(Strings.isNullOrEmpty(prItemId) && "CDLS".equals((String)rfiItem.get("item_cd_crn_typ_ccd"))) {
			rfiItem.put("item_cd", sharedService.generateDocumentNumber("NOCDMT"));
		}*/

		rfiItemRepository.insertRfiItem(rfiItem);
		rfxReceiptService.updateProgressStatus((String) rfiItem.get("rfx_req_rcpt_uuid"), "RFI_PRGSG");
	}

	public void updateRfiItem(List rfiItems) {
		if(rfiItems == null) {
			return;
		}
		if(rfiItems.isEmpty()) {
			return;
		}

		for(Map rfiItem : (List<Map>) rfiItems) {
			this.updateRfiItem(rfiItem);
		}
	}

	private void updateRfiItem(Map rfiItem) {
		if(rfiItem == null) {

		}

		rfiItemRepository.updateRfiItem(rfiItem);
	}

	public void deleteRfiItemByRfi(Map param) {
		if(param == null) {
			return;
		}

		rfiItemRepository.deleteRfiItemByRfi(param);
	}

	public List<Map> findListRfiItem(Map param) {
		return rfiItemRepository.findListRfiItem(param);
	}
}
