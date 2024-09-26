package smartsuite.app.common.status.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.upcr.event.UpcrEventPublisher;
import smartsuite.app.common.status.repository.UpcrStatusRepository;
import smartsuite.module.ModuleManager;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UpcrStatusService {
    @Inject
    UpcrStatusRepository upcrStatusRepository;

	@Inject
	UpcrEventPublisher upcrEventPublisher;

	private String targetPriceModule = "target-price";

    /**
	 * PR 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br><br>
	 * <b>Option:</b><br>
	 * param.pr_chg_yn - 변경 여부<br>
	 *
	 * @param param
	 */
	public void saveDraftUpcr(Map param) {
		upcrStatusRepository.saveDraftUpcrHd(param);
		upcrStatusRepository.saveDraftUpcrDt(param);
	}

	/**
	 * PR 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br><br>
	 * <b>Option:</b><br>
	 * param.pr_chg_yn - 변경 여부<br>
	 *
	 * @param param
	 */
	public void submitApprovalUpcr(Map param) {
		upcrStatusRepository.submitApprovalUpcrHd(param);
		upcrStatusRepository.submitApprovalUpcrDt(param);
	}

	/**
	 * PR 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br>
	 *
	 * @param param
	 */
	public void approveApprovalUpcr(Map param) {
		upcrStatusRepository.approveApprovalUpcrHd(param);
		upcrStatusRepository.approveApprovalUpcrDt(param);
	}

	/**
	 * PR 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br><br>
	 * <b>Option:</b><br>
	 * param.pr_chg_yn - 변경 여부<br>
	 *
	 * @param param
	 */
	public void rejectApprovalUpcr(Map param) {
		upcrStatusRepository.rejectApprovalUpcrHd(param);
		upcrStatusRepository.rejectApprovalUpcrDt(param);
	}

	/**
	 * PR 결재취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br><br>
	 * <b>Option:</b><br>
	 * param.pr_chg_yn - 변경 여부<br>
	 *
	 * @param param
	 */
	public void cancelApprovalUpcr(Map param) {
		upcrStatusRepository.cancelApprovalUpcrHd(param);
		upcrStatusRepository.cancelApprovalUpcrDt(param);
	}

	/**
	 * PR 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br>
	 *
	 * @param param
	 */
	public void bypassApprovalUpcr(Map param) {
		upcrStatusRepository.bypassApprovalUpcrHd(param);
		upcrStatusRepository.bypassApprovalUpcrDt(param);
	}

	/**
	 * PR 변경으로 인한 이전차수 종료처리한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br>
	 * param.prev_pr_id - 이전 구매요청 ID<br>
	 *
	 * @param param
	 */
	public void closePrevUpcrByModUpcr(Map param) {
		List<Map> infos = upcrStatusRepository.selectPrevUpcrByModUpcrDt(param);
		for(Map info : infos) {
			upcrStatusRepository.closePrevUpcrByModUpcrDt(info);
		}
	}

	/**
	 * PR 반송처리 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_ret_rsn - 구매요청 반려 사유<br>
	 * param.pr_item_uuid or param.pr_item_uuids - 구매요청 품목 ID<br>
	 *
	 * @param param
	 */
	public void returnUpcr(Map param) {
		upcrStatusRepository.returnUpcrDt(param);
	}

	/**
	 * PR 접수처리 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuid or param.pr_item_uuids - 구매요청 품목 ID<br>
	 *
	 * @param param
	 */
	public void receiveUpcr(Map param) {
		upcrStatusRepository.receiveUpcrDt(param);
	}

	/**
	 * RFI 임시저장 시 PR 품목 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void temporarySaveRfi(Map param) {
		upcrStatusRepository.temporarySaveRfiUpcrDt(param);
	}

	/**
	 * RFI 요청 시 PR 품목 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void requestRfi(Map param) {
		upcrStatusRepository.requestRfiUpcrDt(param);
	}

	/**
	 * RFI 종료 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void completeRfi(Map param) {
		upcrStatusRepository.completeRfiUpcrDt(param);
	}

	/**
	 * RFI 품목 삭제 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void deleteRfiItem(Map param) {
		upcrStatusRepository.deleteRfiItemUpcrDt(param);
	}

	/**
	 * RFx 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void saveDraftRfx(Map param) {
		upcrStatusRepository.saveDraftRfxUpcrDt(param);
	}

	/**
	 * RFx 품목 삭제 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void deleteRfxItem(Map param) {
		upcrStatusRepository.deleteRfxItemUpcrDt(param);
	}

	/**
	 * RFx 진행품의 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void submitApprovalRfxProg(Map param) {
		upcrStatusRepository.submitApprovalRfxProgUpcrDt(param);
	}

	/**
	 * RFx 진행품의 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void approveApprovalRfxProg(Map param) {
		upcrStatusRepository.approveApprovalRfxProgUpcrDt(param);
	}

	/**
	 * RFx 진행품의 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void rejectApprovalRfxProg(Map param) {
		upcrStatusRepository.rejectApprovalRfxProgUpcrDt(param);
	}

	/**
	 * RFx 진행품의 결재취소/삭제 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void cancelApprovalRfxProg(Map param) {
		upcrStatusRepository.cancelApprovalRfxProgUpcrDt(param);
	}

	/**
	 * RFx 진행품의 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void bypassApprovalRfxProg(Map param) {
		upcrStatusRepository.bypassApprovalRfxProgUpcrDt(param);
	}

	/**
	 * RFx 시작 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void startRfx(Map param) {
		upcrStatusRepository.startRfxUpcrDt(param);
	}

	/**
	 * RFx 마감 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void closeRfx(Map param) {
		upcrStatusRepository.closeRfxUpcrDt(param);
	}

	/**
	 * RFx 유찰 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void dropRfx(Map param) {
		upcrStatusRepository.dropRfxHdUpcrDt(param);
	}

	/**
	 * RFx 취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void cancelRfx(Map param) {
		upcrStatusRepository.cancelRfxHdUpcrDt(param);
	}

	/**
	 * RFx multi-round(재견적/입찰) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void multiRoundRfx(Map param) {
		upcrStatusRepository.multiRoundRfxUpcrDt(param);
	}

	/**
	 * RFx 선정품의 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void submitApprovalRfxResult(Map param) {
		upcrStatusRepository.submitApprovalRfxResultUpcrDt(param);
	}

	/**
	 * RFx 선정품의 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.selectedItems - 선정된 구매요청 품목 ID 다건<br>
	 * param.nonSelectedItems - 선정되지 않은 구매요청 품목 ID 다건<br>
	 * param.rfx_res_typ - 견적 결과 유형<br>
	 *
	 * @param param
	 */
	public void approveApprovalRfxResult(Map param) {
		List selectedItems = (List) param.get("selectedUpcrItems");
		if(selectedItems!= null  && selectedItems.size() > 0) {
			upcrStatusRepository.approveApprovalRfxResultUpcrDtBySelectedItem(param);

			if (ModuleManager.exist(targetPriceModule)) {
				List<Map<String, Object>> priceGateItems = upcrStatusRepository.findPriceGateItems(param);

				for (Map<String, Object> priceGateItem : priceGateItems) {
					upcrStatusRepository.updatePriceGateByRfx(priceGateItem);
				}

				Map<String, Object> eventParam = Maps.newHashMap();

				eventParam.put("priceGateItems", priceGateItems);
				upcrEventPublisher.updateRfxUpcr(eventParam);
			}
		}

		List nonSelectedItems = (List) param.get("nonSelectedUpcrItems");
		if(nonSelectedItems!= null  && nonSelectedItems.size() > 0) {
			upcrStatusRepository.approveApprovalRfxResultUpcrDtByNonSelectedItem(param);
		}
	}

	/**
	 * RFx 선정품의 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void rejectApprovalRfxResult(Map param) {
		upcrStatusRepository.rejectApprovalRfxResultUpcrDt(param);
	}

	/**
	 * RFx 선정품의 결재취소/삭제 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void cancelApprovalRfxResult(Map param) {
		upcrStatusRepository.cancelApprovalRfxResultUpcrDt(param);
	}

	/**
	 * RFx 선정품의 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * param.rfx_res_typ - 견적 결과 유형<br>
	 *
	 * @param param
	 */
	public void bypassApprovalRfxResult(Map param) {
		List selectedItems = (List) param.get("selectedUpcrItems");
		if(selectedItems != null  && selectedItems.size() > 0) {
			upcrStatusRepository.bypassApprovalRfxResultUpcrDtBySelectedItem(param);

			if (ModuleManager.exist(targetPriceModule)) {
				List<Map<String, Object>> priceGateItems = upcrStatusRepository.findPriceGateItems(param);

				for (Map<String, Object> priceGateItem : priceGateItems) {
					upcrStatusRepository.updatePriceGateByRfx(priceGateItem);
				}

				Map<String, Object> eventParam = Maps.newHashMap();

				eventParam.put("priceGateItems", priceGateItems);
				upcrEventPublisher.updateRfxUpcr(eventParam);
			}
		}

		List nonSelectedItems = (List) param.get("nonSelectedUpcrItems");
		if(nonSelectedItems != null  && nonSelectedItems.size() > 0) {
			upcrStatusRepository.bypassApprovalRfxResultUpcrDtByNonSelectedItem(param);
		}
	}

	/**
	 * RFx 선정 취소시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void cancelRfxResult(Map param) {
			upcrStatusRepository.cancelRfxResultUpcrDt(param);
	}

	/**
	 * RFx 품목 종결처리 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 *
	 * @param param
	 */
	public void closeRfxResult(Map param) {
		upcrStatusRepository.closeRfxResultUpcrDt(param);
	}
}
