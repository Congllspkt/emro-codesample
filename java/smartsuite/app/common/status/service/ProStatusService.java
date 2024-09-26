package smartsuite.app.common.status.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.status.event.ProStatusPublisher;
import smartsuite.app.common.status.repository.*;

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
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class ProStatusService {

	// 구매요청 진행상태
	@Inject
	PrStatusRepository prStatusRepository;
	
	// 구매오더 진행상태
	@Inject
	PoStatusRepository poStatusRepository;
	
	// 단가계약 진행상태
	@Inject
	UnitPriceContractStatusRepository unitPriceContractStatusRepository;
	
	// 검수/납품요청 진행상태
	@Inject
	AsnStatusRepository asnStatusRepository;
	
	// 입고 진행상태
	@Inject
	GrStatusRepository grStatusRepository;
	
	// 송장 진행상태
	@Inject
	InvStatusRepository invStatusRepository;

	@Inject
	ProStatusPublisher proSharedPublisher;
	
	@Inject
	TaxBillStatusRepository taxBillStatusRepository;

	@Inject
	QtaStatusRepository qtaStatusRepository;

	@Inject
	UpcrStatusRepository upcrStatusRepository;

	/**
	 * PR 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br><br>
	 * <b>Option:</b><br>
	 * param.pr_chg_yn - 변경 여부<br>
	 * 
	 * @param param
	 */
	public void saveDraftPr(Map param) {
		prStatusRepository.saveDraftPrHd(param);
		prStatusRepository.saveDraftPrDt(param);
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
	public void submitApprovalPr(Map param) {
		prStatusRepository.submitApprovalPrHd(param);
		prStatusRepository.submitApprovalPrDt(param);
	}

	/**
	 * PR 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br>
	 * 
	 * @param param
	 */
	public void approveApprovalPr(Map param) {
		prStatusRepository.approveApprovalPrHd(param);
		prStatusRepository.approveApprovalPrDt(param);
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
	public void rejectApprovalPr(Map param) {
		prStatusRepository.rejectApprovalPrHd(param);
		prStatusRepository.rejectApprovalPrDt(param);
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
	public void cancelApprovalPr(Map param) {
		prStatusRepository.cancelApprovalPrHd(param);
		prStatusRepository.cancelApprovalPrDt(param);
	}

	/**
	 * PR 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br>
	 * 
	 * @param param
	 */
	public void bypassApprovalPr(Map param) {
		prStatusRepository.bypassApprovalPrHd(param);
		prStatusRepository.bypassApprovalPrDt(param);
	}

	/**
	 * PR 변경으로 인한 이전차수 종료처리한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID<br>
	 * param.prev_pr_id - 이전 구매요청 ID<br>
	 * 
	 * @param param
	 */
	public void closePrevPrByModPr(Map param) {
		List<Map> infos = prStatusRepository.selectPrevPrByModPrDt(param);
		for(Map info : infos) {
			prStatusRepository.closePrevPrByModPrDt(info);
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
	public void returnPr(Map param) {
		prStatusRepository.returnPrDt(param);
	}

	/**
	 * PR 접수처리 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuid or param.pr_item_uuids - 구매요청 품목 ID<br>
	 * 
	 * @param param
	 */
	public void receivePr(Map param) {
		prStatusRepository.receivePrDt(param);
	}

	/**
	 * RFI 임시저장 시 PR 품목 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void temporarySaveRfi(Map param) {
		prStatusRepository.temporarySaveRfiPrDt(param);
	}

	/**
	 * RFI 요청 시 PR 품목 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void requestRfi(Map param) {
		prStatusRepository.requestRfiPrDt(param);
	}

	/**
	 * RFI 종료 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void completeRfi(Map param) {
		prStatusRepository.completeRfiPrDt(param);
	}

	/**
	 * RFI 품목 삭제 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void deleteRfiItem(Map param) {
		prStatusRepository.deleteRfiItemPrDt(param);
	}

	/**
	 * RFx 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void saveDraftRfx(Map param) {
		prStatusRepository.saveDraftRfxPrDt(param);
	}

	/**
	 * RFx 품목 삭제 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void deleteRfxItem(Map param) {
		prStatusRepository.deleteRfxItemPrDt(param);
	}

	/**
	 * RFx 진행품의 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void submitApprovalRfxProg(Map param) {
		prStatusRepository.submitApprovalRfxProgPrDt(param);
	}

	/**
	 * RFx 진행품의 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void approveApprovalRfxProg(Map param) {
		prStatusRepository.approveApprovalRfxProgPrDt(param);
	}

	/**
	 * RFx 진행품의 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void rejectApprovalRfxProg(Map param) {
		prStatusRepository.rejectApprovalRfxProgPrDt(param);
	}

	/**
	 * RFx 진행품의 결재취소/삭제 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void cancelApprovalRfxProg(Map param) {
		prStatusRepository.cancelApprovalRfxProgPrDt(param);
	}

	/**
	 * RFx 진행품의 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void bypassApprovalRfxProg(Map param) {
		prStatusRepository.bypassApprovalRfxProgPrDt(param);
	}

	/**
	 * RFx 시작 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void startRfx(Map param) {
		prStatusRepository.startRfxPrDt(param);
	}

	/**
	 * RFx 마감 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void closeRfx(Map param) {
		prStatusRepository.closeRfxPrDt(param);
	}
	
	/**
	 * RFx 유찰 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void dropRfx(Map param) {
		prStatusRepository.dropRfxHdPrDt(param);
	}

	/**
	 * RFx 취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void cancelRfx(Map param) {
		prStatusRepository.cancelRfxHdPrDt(param);
	}

	/**
	 * RFx multi-round(재견적/입찰) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void multiRoundRfx(Map param) {
		prStatusRepository.multiRoundRfxPrDt(param);
	}

	/**
	 * RFx 선정품의 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void submitApprovalRfxResult(Map param) {
		prStatusRepository.submitApprovalRfxResultPrDt(param);
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
		List selectedItems = (List) param.get("selectedPrItems");
		if(selectedItems != null  && selectedItems.size() > 0) {
			prStatusRepository.approveApprovalRfxResultPrDtBySelectedItem(param);
		}
		
		List nonSelectedItems = (List) param.get("nonSelectedPrItems");
		if(nonSelectedItems != null  && nonSelectedItems.size() > 0) {
			prStatusRepository.approveApprovalRfxResultPrDtByNonSelectedItem(param);
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
		prStatusRepository.rejectApprovalRfxResultPrDt(param);
	}

	/**
	 * RFx 선정품의 결재취소/삭제 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void cancelApprovalRfxResult(Map param) {
		prStatusRepository.cancelApprovalRfxResultPrDt(param);
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
		List selectedItems = (List) param.get("selectedPrItems");
		if(selectedItems != null && selectedItems.size() > 0) {
			prStatusRepository.bypassApprovalRfxResultPrDtBySelectedItem(param);
		}
		
		List nonSelectedItems = (List) param.get("nonSelectedPrItems");
		if(nonSelectedItems != null  && nonSelectedItems.size() > 0) {
			prStatusRepository.bypassApprovalRfxResultPrDtByNonSelectedItem(param);
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
			prStatusRepository.cancelRfxResultPrDt(param);
	}

	/**
	 * RFx 품목 종결처리 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_item_uuids - 구매요청 품목 ID 다건<br>
	 * 
	 * @param param
	 */
	public void closeRfxResult(Map param) {
		prStatusRepository.closeRfxResultPrDt(param);
	}
	
	/**
	 * PO 작성대기 상태 건 생성시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void createDraftPo(Map param) {
		poStatusRepository.createDraftPoHd(param);
		poStatusRepository.createDraftPoDt(param);
		//TODO Rfx 연결 필요
		//sqlSession.update(NAMESPACE + "closeRfxItems", param);
	}

	/**
	 * PO 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftPo(Map param) {
		Map poReqRcptInfo = poStatusRepository.findPoReqRcptByPo(param);
		if(poReqRcptInfo.get("po_req_rcpt_uuid") != null) {
			poStatusRepository.updatePoReqRcptBySaveDraftPo(poReqRcptInfo);
		}
		List<Map> poReqItemRcptList = poStatusRepository.findListPoReqItemRcptByPo(param);
		List<String> poReqItemRcptUuids = Lists.newArrayList();
		for(Map poReqItemRcpt : poReqItemRcptList) {
			if(poReqItemRcpt.get("po_req_item_rcpt_qta_aloc_uuid") != null) {
				// 단가계약 쿼터 배분을 사용하여 생성된 품목
				poStatusRepository.updatePoReqItemRcptQtaBySaveDraftPo(poReqItemRcpt);
			}
			if(!poReqItemRcptUuids.contains(poReqItemRcpt.get("po_req_item_rcpt_uuid"))) {
				poReqItemRcptUuids.add((String) poReqItemRcpt.get("po_req_item_rcpt_uuid"));
			}
		}
		for(String poReqItemRcptUuid : poReqItemRcptUuids) {
			Map itemParam = Maps.newHashMap();
			itemParam.put("po_req_item_rcpt_uuid", poReqItemRcptUuid);
			poStatusRepository.updatePoReqItemRcptBySaveDraftPo(itemParam);
		}
		
		poStatusRepository.saveDraftPoHd(param);
		poStatusRepository.saveDraftPoDt(param);
		prStatusRepository.saveDraftPoPrDt(param);
	}
	
	/**
	 * PO 삭제 시 상태변경 처리를 한다.<br>
	 * 1차수의 PO 삭제 시 해당 구매요청 품목을 구매요청접수 상태로 되돌린다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void deletePo(Map param) {
		Map poReqRcptInfo = poStatusRepository.findPoReqRcptByPo(param);
		if(poReqRcptInfo.get("po_req_rcpt_uuid") != null) {
			poStatusRepository.updatePoReqRcptByDeletePo(poReqRcptInfo);
		}
		List<Map> poReqItemRcptList = poStatusRepository.findListPoReqItemRcptByPo(param);
		List<String> poReqItemRcptUuids = Lists.newArrayList();
		for(Map poReqItemRcpt : poReqItemRcptList) {
			if(poReqItemRcpt.get("po_req_item_rcpt_qta_aloc_uuid") != null) {
				// 단가계약 쿼터 배분을 사용하여 생성된 품목
				poStatusRepository.deletePoReqItemRcptQtaByDeletePo(poReqItemRcpt);
			}
			if(!poReqItemRcptUuids.contains(poReqItemRcpt.get("po_req_item_rcpt_uuid"))) {
				poReqItemRcptUuids.add((String) poReqItemRcpt.get("po_req_item_rcpt_uuid"));
			}
		}
		for(String poReqItemRcptUuid : poReqItemRcptUuids) {
			Map itemParam = Maps.newHashMap();
			itemParam.put("po_req_item_rcpt_uuid", poReqItemRcptUuid);
			poStatusRepository.updatePoReqItemRcptByDeletePo(itemParam);
		}
		
		prStatusRepository.deletePoPrDt(param);
	}
	
	/**
	 * PO 품목 삭제 시 상태변경 처리를 한다.<br>
	 * 2차수 이상의 PO 삭제 시 해당 구매요청 품목을 구매요청접수 상태로 되돌린다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_item_uuid - 구매오더 품목 ID<br>
	 * 
	 * @param param
	 */
	public void deletePoItem(Map param) {
		prStatusRepository.deletePoItemPrDt(param);
	}
	
	/**
	 * PO 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void submitApprovalPo(Map param) {
		poStatusRepository.submitApprovalPoHd(param);
		poStatusRepository.submitApprovalPoDt(param);
		prStatusRepository.submitApprovalPoPrDt(param);
	}

	/**
	 * PO 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * param.cntr_use_yn - 계약 여부<br>
	 * param.cntr_use_yn - 전자계약 여부<br>
	 * 
	 * @param param
	 */
	public void approveApprovalPo(Map param) {
		Map poReqRcptInfo = poStatusRepository.findPoReqRcptByPo(param);
		if(poReqRcptInfo.get("po_req_rcpt_uuid") != null) {
			poStatusRepository.updatePoReqRcptBySaveDraftPo(poReqRcptInfo);
		}
		
		poStatusRepository.approveApprovalPoHd(param);
		poStatusRepository.approveApprovalPoDt(param);
		prStatusRepository.approveApprovalPoPrDt(param);
	}

	/**
	 * PO 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void rejectApprovalPo(Map param) {
		poStatusRepository.rejectApprovalPoHd(param);
		poStatusRepository.rejectApprovalPoDt(param);
		prStatusRepository.rejectApprovalPoPrDt(param);
	}

	/**
	 * PO 결재취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void cancelApprovalPo(Map param) {
		poStatusRepository.cancelApprovalPoHd(param);
		poStatusRepository.cancelApprovalPoDt(param);
		prStatusRepository.cancelApprovalPoPrDt(param);
	}

	/**
	 * PO 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * param.cntr_use_yn - 계약 여부<br>
	 * param.cntr_use_yn - 전자계약 여부<br>
	 * 
	 * @param param
	 */
	public void bypassApprovalPo(Map param) {
		Map poReqRcptInfo = poStatusRepository.findPoReqRcptByPo(param);
		if(poReqRcptInfo.get("po_req_rcpt_uuid") != null) {
			poStatusRepository.updatePoReqRcptBySaveDraftPo(poReqRcptInfo);
		}
		List<Map> poReqItemRcptList = poStatusRepository.findListPoReqItemRcptByPo(param);
		List<String> poReqItemRcptUuids = Lists.newArrayList();
		for(Map poReqItemRcpt : poReqItemRcptList) {
			if(poReqItemRcpt.get("po_req_item_rcpt_qta_aloc_uuid") != null) {
				// 단가계약 쿼터 배분을 사용하여 생성된 품목
				poStatusRepository.updatePoReqItemRcptQtaBySaveDraftPo(poReqItemRcpt);
			}
			if(!poReqItemRcptUuids.contains(poReqItemRcpt.get("po_req_item_rcpt_uuid"))) {
				poReqItemRcptUuids.add((String) poReqItemRcpt.get("po_req_item_rcpt_uuid"));
			}
		}
		for(String poReqItemRcptUuid : poReqItemRcptUuids) {
			Map itemParam = Maps.newHashMap();
			itemParam.put("po_req_item_rcpt_uuid", poReqItemRcptUuid);
			poStatusRepository.updatePoReqItemRcptBySaveDraftPo(itemParam);
		}
		
		poStatusRepository.bypassApprovalPoHd(param);
		poStatusRepository.bypassApprovalPoDt(param);
		prStatusRepository.bypassApprovalPoPrDt(param);
	}
	
	public void updatePoReceiptComplete(Map<String, Object> param) {
		Map poReqRcptInfo = poStatusRepository.findPoReqRcptByPo(param);
		if(poReqRcptInfo.get("po_req_rcpt_uuid") != null) {
			poStatusRepository.updatePoReqRcptCompleteByCompletePo(poReqRcptInfo);
		}
		List<Map> poReqItemRcptList = poStatusRepository.findListPoReqItemRcptByPo(param);
		List<String> poReqItemRcptUuids = Lists.newArrayList();
		for(Map poReqItemRcpt : poReqItemRcptList) {
			if(poReqItemRcpt.get("po_req_item_rcpt_qta_aloc_uuid") != null) {
				// 단가계약 쿼터 배분을 사용하여 생성된 품목
				poStatusRepository.updatePoReqItemRcptQtaCompleteByCompletePo(poReqItemRcpt);
			}
			if(!poReqItemRcptUuids.contains(poReqItemRcpt.get("po_req_item_rcpt_uuid"))) {
				poReqItemRcptUuids.add((String) poReqItemRcpt.get("po_req_item_rcpt_uuid"));
			}
		}
		for(String poReqItemRcptUuid : poReqItemRcptUuids) {
			Map itemParam = Maps.newHashMap();
			itemParam.put("po_req_item_rcpt_uuid", poReqItemRcptUuid);
			poStatusRepository.updatePoReqItemRcptCompleteByCompletePo(itemParam);
		}
	}

	/**
	 * 구매오더 전자계약 담당자 반송 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * param.uprccntr_revno - 계약 차수<br>
	 * 
	 * @param param
	 */
	public void returnElecCntrPo(Map param) {
		poStatusRepository.returnElecCntrPoHd(param);
		prStatusRepository.returnElecCntrPoPrDt(param);
		poStatusRepository.returnElecCntrPoAprv(param);
	}
	
	/**
	 * 구매오더 전자계약 협력사 서명완료 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void completeElecCntrPo(Map param) {
		poStatusRepository.completeElecCntrPoHd(param);
		poStatusRepository.completeElecCntrPoDt(param);
		prStatusRepository.completeElecCntrPoPrDt(param);
	}

	/**
	 * 협력사 PO 접수 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID or param.po_uuids - 구매오더 ID 다건<br>
	 * 
	 * @param param
	 */
	public void acceptPoByVendor(Map param) {
		poStatusRepository.acceptPoByVendor(param);
	}

	/**
	 * 협력사 PO 거부 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID or param.po_uuids - 구매오더 ID 다건<br>
	 * 
	 * @param param
	 */
	public void rejectPoByVendor(Map param) {
		poStatusRepository.rejectPoByVendor(param);
	}

	/**
	 * PO 변경요청 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftPoChangeRequest(Map param) {
		poStatusRepository.saveDraftPoChangeRequestHd(param);
	}

	/**
	 * PO 변경요청 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void submitApprovalPoChangeRequest(Map param) {
		poStatusRepository.submitApprovalPoChangeRequestHd(param);
	}

	/**
	 * PO 변경요청 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void approveApprovalPoChangeRequest(Map param) {
		poStatusRepository.approveApprovalPoChangeRequestHd(param);
	}

	/**
	 * PO 변경요청 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void rejectApprovalPoChangeRequest(Map param) {
		poStatusRepository.rejectApprovalPoChangeRequestHd(param);
	}

	/**
	 * PO 변경요청 결재취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void cancelApprovalPoChangeRequest(Map param) {
		poStatusRepository.cancelApprovalPoChangeRequestHd(param);
	}

	/**
	 * PO 변경요청 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void bypassApprovalPoChangeRequest(Map param) {
		poStatusRepository.bypassApprovalPoChangeRequestHd(param);
	}

	/**
	 * PO 변경요청 반송 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void returnPoChangeRequest(Map param) {
		poStatusRepository.returnPoChangeRequestHd(param);
	}

	/**
	 * PO 변경 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftPoChange(Map param) {
		poStatusRepository.saveDraftPoChangeHd(param);
	}

	/**
	 * PO 변경 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void submitApprovalPoChange(Map param) {
		poStatusRepository.submitApprovalPoChangeHd(param);
	}

	/**
	 * PO 변경 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * param.cntr_use_yn - 계약 여부<br>
	 * 
	 * @param param
	 */
	public void approveApprovalPoChange(Map param) {
		poStatusRepository.approveApprovalPoChangeHd(param);
	}

	/**
	 * PO 변경 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void rejectApprovalPoChange(Map param) {
		poStatusRepository.rejectApprovalPoChangeHd(param);
	}

	/**
	 * PO 변경 결재취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void cancelApprovalPoChange(Map param) {
		poStatusRepository.cancelApprovalPoChangeHd(param);
	}

	/**
	 * PO 변경 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * param.cntr_use_yn - 계약 여부<br>
	 * 
	 * @param param
	 */
	public void bypassApprovalPoChange(Map param) {
		poStatusRepository.bypassApprovalPoChangeHd(param);
	}

	/**
	 * PO 해지 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void closePo(Map param) {
		poStatusRepository.closePoHd(param);
		poStatusRepository.closePoDt(param);
		prStatusRepository.closePoPrDt(param);
		this.updatePoReceiptComplete(param);
	}

	/**
	 * 단가계약 작성대기 상태 건 생성시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void createDraftUnitPriceContract(Map param) {
		unitPriceContractStatusRepository.createDraftPriceContractHd(param);
		prStatusRepository.createDraftPriceContractPrDt(param);
		upcrStatusRepository.createDraftPriceContractUpcrDt(param);
		//RFX에서 처리 completeRfx sqlSession.update(NAMESPACE + "closeRfxItems", param);
	}
	
	/**
	 * 단가계약 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftUnitPriceContract(Map param) {
		unitPriceContractStatusRepository.saveDraftPriceContractHd(param);
		prStatusRepository.saveDraftPriceContractPrDt(param);
		upcrStatusRepository.saveDraftPriceContractUpcrDt(param);
	}

	/**
	 * 단가계약 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void submitApprovalPriceContract(Map param) {
		unitPriceContractStatusRepository.submitApprovalPriceContractHd(param);
		prStatusRepository.submitApprovalPriceContractPrDt(param);
		upcrStatusRepository.submitApprovalPriceContractUpcrDt(param);
	}

	/**
	 * 단가계약 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * param.cntr_use_yn - 전자계약 여부<br>
	 * 
	 * @param param
	 */
	public void approveApprovalPriceContract(Map param) {
		unitPriceContractStatusRepository.approveApprovalPriceContractHd(param);
		prStatusRepository.approveApprovalPriceContractPrDt(param);
		upcrStatusRepository.approveApprovalPriceContractUpcrDt(param);
	}

	/**
	 * 단가계약 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void rejectApprovalPriceContract(Map param) {
		unitPriceContractStatusRepository.rejectApprovalPriceContractHd(param);
		prStatusRepository.rejectApprovalPriceContractPrDt(param);
		upcrStatusRepository.rejectApprovalPriceContractUpcrDt(param);
	}

	/**
	 * 단가계약 결재취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void cancelApprovalPriceContract(Map param) {
		unitPriceContractStatusRepository.cancelApprovalPriceContractHd(param);
		prStatusRepository.cancelApprovalPriceContractPrDt(param);
	}

	/**
	 * 단가계약 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * param.cntr_use_yn - 전자계약 여부<br>
	 * 
	 * @param param
	 */
	public void bypassApprovalUnitPriceContract(Map param) {
		unitPriceContractStatusRepository.bypassApprovalPriceContractHd(param);
		prStatusRepository.bypassApprovalPriceContractPrDt(param);
		upcrStatusRepository.bypassApprovalPriceContractUpcrDt(param);
	}

	/**
	 * 단가계약 전자계약 담당자 반송 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * param.uprccntr_revno - 단가계약 차수<br>
	 * 
	 * @param param
	 */
	public void returnElecCntrUnitPriceContract(Map param) {
		unitPriceContractStatusRepository.returnElecCntrPriceContractHd(param);
		prStatusRepository.returnElecCntrPriceContractPrDt(param);
		upcrStatusRepository.returnElecCntrPriceContractUpcrDt(param);
		unitPriceContractStatusRepository.returnElecCntrPriceContractAprv(param);
	}
	
	/**
	 * 단가계약 전자계약 협력사 서명완료 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void completeElecCntrUnitPriceContract(Map param) {
		unitPriceContractStatusRepository.completeElecCntrPriceContractHd(param);
		prStatusRepository.completeElecCntrPriceContractPrDt(param);
		upcrStatusRepository.completeElecCntrPriceContractUpcrDt(param);
	}

	/**
	 * 단가계약 변경요청 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftUnitPriceContractChangeRequest(Map param) {
		unitPriceContractStatusRepository.saveDraftPriceContractChangeRequestHd(param);
	}

	/**
	 * 단가계약 변경요청 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void submitApprovalPriceContractChangeRequest(Map param) {
		unitPriceContractStatusRepository.submitApprovalPriceContractChangeRequestHd(param);
	}

	/**
	 * 단가계약 변경요청 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void approveApprovalPriceContractChangeRequest(Map param) {
		unitPriceContractStatusRepository.approveApprovalPriceContractChangeRequestHd(param);
	}

	/**
	 * 단가계약 변경요청 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void rejectApprovalPriceContractChangeRequest(Map param) {
		unitPriceContractStatusRepository.rejectApprovalPriceContractChangeRequestHd(param);
	}

	/**
	 * 단가게약 변경요청 결재취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void cancelApprovalPriceContractChangeRequest(Map param) {
		unitPriceContractStatusRepository.cancelApprovalPriceContractChangeRequestHd(param);
	}

	/**
	 * 단가계약 변경요청 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void bypassApprovalUnitPriceContractChangeRequest(Map param) {
		unitPriceContractStatusRepository.bypassApprovalPriceContractChangeRequestHd(param);
	}

	/**
	 * 단가계약 변경요청 반송 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void returnUnitPriceContractChangeRequest(Map param) {
		unitPriceContractStatusRepository.returnPriceContractChangeRequestHd(param);
	}

	/**
	 * 단가계약 변경 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftUnitPriceContractChange(Map param) {
		unitPriceContractStatusRepository.saveDraftPriceContractChangeHd(param);
	}

	/**
	 * 단가계약 변경 결재상신 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void submitApprovalPriceContractChange(Map param) {
		unitPriceContractStatusRepository.submitApprovalPriceContractChangeHd(param);
	}

	/**
	 * 단가계약 변경 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * param.cntr_use_yn - 전자계약 여부<br>
	 * 
	 * @param param
	 */
	public void approveApprovalPriceContractChange(Map param) {
		unitPriceContractStatusRepository.approveApprovalPriceContractChangeHd(param);
	}

	/**
	 * 단가계약 변경 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void rejectApprovalPriceContractChange(Map param) {
		unitPriceContractStatusRepository.rejectApprovalPriceContractChangeHd(param);
	}

	/**
	 * 단가계약 변경 결재취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void cancelApprovalPriceContractChange(Map param) {
		unitPriceContractStatusRepository.cancelApprovalPriceContractChangeHd(param);
	}

	/**
	 * 단가계약 변경 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * param.cntr_use_yn - 전자계약 여부<br>
	 * 
	 * @param param
	 */
	public void bypassApprovalUnitPriceContractChange(Map param) {
		unitPriceContractStatusRepository.bypassApprovalPriceContractChangeHd(param);
	}

	/**
	 * 단가계약 해지 결재 Bypass(승인처리) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.uprccntr_uuid - 단가계약 ID<br>
	 * 
	 * @param param
	 */
	public void closeUnitPriceContract(Map param) {
		unitPriceContractStatusRepository.closePriceContractHd(param);
	}

	/**
	 * 납품예정(ASN) 임시저장 (협력사) 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.asn_uuid - 납품예정 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftAsn(Map param) {
		asnStatusRepository.saveDraftAsnHd(param);
		asnStatusRepository.saveDraftAsnDt(param);
	}

	/**
	 * 납품예정(ASN) (협력사) 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.asn_uuid - 납품예정 ID<br>
	 * 
	 * @param param
	 */
	public void requestAsn(Map param) {
		asnStatusRepository.requestAsnHd(param);
		asnStatusRepository.requestAsnDt(param);
		poStatusRepository.requestAsnPoDt(param);
		prStatusRepository.requestAsnPrDt(param);
	}

	/**
	 * 검수등록(입고,GR) 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftGr(Map param) {
		grStatusRepository.saveDraftGrHd(param);
		grStatusRepository.saveDraftGrDt(param);
		asnStatusRepository.saveDraftGrAsnHd(param);
		asnStatusRepository.saveDraftGrAsnDt(param);
		poStatusRepository.saveDraftGrPoDt(param);
		prStatusRepository.saveDraftGrPrDt(param);
	}

	/**
	 * 검수등록(임시저장) 삭제한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void deleteGr(Map param) {
		asnStatusRepository.deleteGrAsnHd(param);
		asnStatusRepository.deleteGrAsnDt(param);
	}
	
	public void submitGrEval(Map param) {
		grStatusRepository.submitGrEval(param);
	}
	
	public void completeGrEval(Map param) {
		grStatusRepository.completeGrEval(param);
	}

	/**
	 * 검수승인(입고,GR) 결재요청 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void submitApprovalGr(Map param) {
		grStatusRepository.submitApprovalGrHd(param);
	}

	/**
	 * 검수승인(입고,GR) 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void approveApprovalGr(Map param) {
		grStatusRepository.approveApprovalGrHd(param);
		grStatusRepository.approveApprovalGrDt(param);
		asnStatusRepository.approveApprovalGrAsnHd(param);
		asnStatusRepository.approveApprovalGrAsnDt(param);
		
		List<Map> grPoDts = poStatusRepository.selectApprovalGrPoDt(param);
		for(Map grPoDt : grPoDts) {
			poStatusRepository.approveApprovalGrPoDt(grPoDt);
		}
		
		List<Map> prItems = prStatusRepository.checkPoQtyWithGrQty(param);
		for(Map prItem : prItems) {
			prStatusRepository.approveApprovalGrPrDtByItem(prItem);
		}
	}

	/**
	 * 검수승인(입고,GR) 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void rejectApprovalGr(Map param) {
		grStatusRepository.rejectApprovalGrHd(param);
	}

	/**
	 * 검수승인(입고,GR) 결재취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void cancelApprovalGr(Map param) {
		grStatusRepository.cancelApprovalGrHd(param);
	}
	
	/**
	 * 검수승인(입고,GR) 결재승인처리(Bypass) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void bypassApprovalGr(Map param) {
		grStatusRepository.bypassApprovalGrHd(param);
		grStatusRepository.bypassApprovalGrDt(param);
		asnStatusRepository.bypassApprovalGrAsnHd(param);
		asnStatusRepository.bypassApprovalGrAsnDt(param);
		
		List<Map> grPoDts = poStatusRepository.selectBypassApprovalGrPoDt(param);
		for(Map grPoDt : grPoDts) {
			poStatusRepository.bypassApprovalGrPoDt(grPoDt);
		}

		List<Map> prItems = prStatusRepository.checkPoQtyWithGrQty(param);
		for(Map prItem : prItems) {
			prStatusRepository.bypassApprovalGrPrDtByItem(prItem);
		}
	}

	/**
	 * 납품예정(ASN) 반려처리 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.asn_uuid - 납품예정 ID<br>
	 * 
	 * @param param
	 */
	public void rejectRequestAsn(Map param) {
		asnStatusRepository.rejectRequestAsnHd(param);
		asnStatusRepository.rejectRequestAsnDt(param);
		poStatusRepository.rejectRequestAsnPoDt(param);
		prStatusRepository.rejectRequestAsnPrDt(param);
	}

	/**
	 * 검수(입고,GR) 취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void cancelGr(Map param) {
		grStatusRepository.cancelGrHd(param);
		grStatusRepository.cancelGrDt(param);
		asnStatusRepository.cancelGrAsnHd(param);
		asnStatusRepository.cancelGrAsnDt(param);
		poStatusRepository.cancelGrPoHd(param);
		poStatusRepository.cancelGrPoDt(param);
		prStatusRepository.cancelGrPrDt(param);
	}

	/**
	 * 기성요청 임시저장 (협력사) 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.asn_uuid - 기성요청 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftServiceAsn(Map param) {
		asnStatusRepository.saveDraftServiceAsnHd(param);
		poStatusRepository.saveDraftServiceAsnPoHd(param);
	}
	
	/**
	 * 기성요청 삭제 (협력사).<br><br>
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 * 
	 * @param param
	 */
	public void deleteServiceAsn(Map param) {
		List<Map> poList = poStatusRepository.findListAsnHdByDeleteAsnPo(param);
		if(poList == null || poList.size() == 0) {
			poStatusRepository.deleteServiceAsnPoHdByAllAsnDelete(param);
		} else {
			poStatusRepository.deleteServiceAsnPoHd(param);
		}
	}

	/**
	 * 기성요청 (협력사) 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.asn_uuid - 기성요청 ID<br>
	 * 
	 * @param param
	 */
	public void requestServiceAsn(Map param) {
		asnStatusRepository.requestServiceAsnHd(param);
		asnStatusRepository.requestServiceAsnDt(param);
		poStatusRepository.requestServiceAsnPoHd(param);
		poStatusRepository.requestServiceAsnPoDt(param);
		prStatusRepository.requestServiceAsnPrDt(param);
	}

	/**
	 * 기성등록(입고,GR) 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftServiceGr(Map param) {
		grStatusRepository.saveDraftServiceGrHd(param);
		grStatusRepository.saveDraftServiceGrDt(param);
		asnStatusRepository.saveDraftServiceGrAsnHd(param);
		asnStatusRepository.saveDraftServiceGrAsnDt(param);
		poStatusRepository.saveDraftServiceGrPoHd(param);
		poStatusRepository.saveDraftServiceGrPoDt(param);
		prStatusRepository.saveDraftServiceGrPrDt(param);
	}

	/**
	 * 기성등록(임시저장) 삭제한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void deleteServiceGr(Map param) {
		asnStatusRepository.deleteServiceGrAsnHd(param);
		asnStatusRepository.deleteServiceGrAsnDt(param);
		
		Map grPoHd = poStatusRepository.selectServiceGrPoHd(param);
		if(grPoHd != null) {
			poStatusRepository.deleteServiceGrPoHd(grPoHd);
		}
	}

	/**
	 * 기성승인(입고,GR) 결재요청 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void submitApprovalServiceGr(Map param) {
		grStatusRepository.submitApprovalServiceGrHd(param);
		poStatusRepository.submitApprovalServiceGrPoHd(param);
	}

	/**
	 * 기성승인(입고,GR) 결재승인 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void approveApprovalServiceGr(Map param) {
		grStatusRepository.approveApprovalServiceGrHd(param);
		grStatusRepository.approveApprovalServiceGrDt(param);
		asnStatusRepository.approveApprovalServiceGrAsnHd(param);
		asnStatusRepository.approveApprovalServiceGrAsnDt(param);
		
		List<Map> grPoHds = poStatusRepository.selectApprovalServiceGrPoHd(param);
		for(Map grPoHd : grPoHds) {
			poStatusRepository.approveApprovalServiceGrPoHd(grPoHd);
		}
		
		List<Map> grPoDts = poStatusRepository.selectApprovalServiceGrPoDt(param);
		for(Map grPoDt : grPoDts) {
			poStatusRepository.approveApprovalServiceGrPoDt(grPoDt);
		}

		List<Map> prItems = prStatusRepository.checkPoAmtWithGrAmt(param);
		for(Map prItem : prItems) {
			prStatusRepository.approveApprovalServiceGrPrDtByItem(prItem);
		}
	}

	/**
	 * 기성승인(입고,GR) 결재반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void rejectApprovalServiceGr(Map param) {
		grStatusRepository.rejectApprovalServiceGrHd(param);
		poStatusRepository.rejectApprovalServiceGrPoHd(param);
	}

	/**
	 * 기성승인(입고,GR) 결재취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void cancelApprovalServiceGr(Map param) {
		grStatusRepository.cancelApprovalServiceGrHd(param);
		poStatusRepository.cancelApprovalServiceGrPoHd(param);
	}

	/**
	 * 기성승인(입고,GR) 결재승인처리(Bypass) 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * 
	 * @param param
	 */
	public void bypassApprovalServiceGr(Map param) {
		grStatusRepository.bypassApprovalServiceGrHd(param);
		grStatusRepository.bypassApprovalServiceGrDt(param);
		asnStatusRepository.bypassApprovalServiceGrAsnHd(param);
		asnStatusRepository.bypassApprovalServiceGrAsnDt(param);
		
		List<Map> grPoHds = poStatusRepository.selectBypassApprovalServiceGrPoHd(param);
		for(Map grPoHd : grPoHds) {
			poStatusRepository.bypassApprovalServiceGrPoHd(grPoHd);
		}
			
		List<Map> grPoDts = poStatusRepository.selectBypassApprovalServiceGrPoDt(param);
		for(Map grPoDt : grPoDts) {
			poStatusRepository.bypassApprovalServiceGrPoDt(grPoDt);
		}

		List<Map> prItems = prStatusRepository.checkPoAmtWithGrAmt(param);
		for(Map prItem : prItems) {
			prStatusRepository.bypassApprovalServiceGrPrDtByItem(prItem);
		}
	}

	/**
	 * 기성요청 반려처리 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.asn_uuid - 기성요청 ID<br>
	 * 
	 * @param param
	 */
	public void rejectRequestServiceAsn(Map param) {
		asnStatusRepository.rejectRequestServiceAsnHd(param);
		asnStatusRepository.rejectRequestServiceAsnDt(param);
		poStatusRepository.rejectRequestServiceAsnPoHd(param);
		poStatusRepository.rejectRequestServiceAsnPoDt(param);
		prStatusRepository.rejectRequestServiceAsnPrDt(param);
	}

	/**
	 * 기성(입고,GR) 취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.gr_uuid - 입고 ID<br>
	 * param.apymt_yn - 선급금 여부<br>
	 * 
	 * @param param
	 */
	public void cancelServiceGr(Map param) {
		grStatusRepository.cancelServiceGrHd(param);
		grStatusRepository.cancelServiceGrDt(param);
		asnStatusRepository.cancelServiceGrAsnHd(param);
		asnStatusRepository.cancelServiceGrAsnDt(param);
		
		if(param.get("apymt_yn") != null && "Y".equals(param.get("apymt_yn"))) {
			poStatusRepository.cancelServiceGrPoHdPrePay(param);
			
		} else {
			Map pohd = poStatusRepository.selectCancelServiceGrPoHd(param);
			if(pohd != null) {
				poStatusRepository.cancelServiceGrPoHd(pohd);
			}
		}
		
		poStatusRepository.cancelServiceGrPoDt(param);
		prStatusRepository.cancelServiceGrPrDt(param);
	}
	
	/**
	 * 송장 임시저장 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.inv_uuid - 송장 ID<br>
	 * 
	 * @param param
	 */
	public void saveDraftInvoice(Map param) {
		invStatusRepository.saveDraftInvoice(param);
	}
	
	/**
	 * 송장 확정 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.inv_uuid - 송장 ID<br>
	 * 
	 * @param param
	 */
	public void confirmInvoice(Map param) {
		invStatusRepository.confirmInvoice(param);
	}

	/**
	 * 송장 확정취소 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.inv_uuid - 송장 ID or param.inv_uuids - 송장 ID 다건<br>
	 * 
	 * @param param
	 */
	public void cancelInvoice(Map param) {
		invStatusRepository.cancelInvoice(param);
	}

	/**
	 * 송장 승인 요청시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.inv_uuid - 송장 ID or param.inv_uuids - 송장 ID 다건<br>
	 *
	 * @param param
	 */
	public void requestInvoice(Map param) {
		invStatusRepository.requestInvoice(param);
	}

	/**
	 * 송장 반려 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.inv_uuid - 송장 ID or param.inv_uuids - 송장 ID 다건<br>
	 *
	 * @param param
	 */
	public void returnInvoice(Map param) {
		invStatusRepository.returnInvoice(param);
	}

	/**
	 * 세금계산서 역발행요청 시 상태변경 처리를 한다.<br><br>
	 * <b>Required:</b><br>
	 * param.taxbill_uuid - 세금계산서 역발행 ID<br>
	 * 
	 * @param param
	 */
	public void requestTaxBill(Map param) {
		taxBillStatusRepository.requestTaxBillTxHd(param);
		taxBillStatusRepository.requestTaxBillTxDt(param);
	}

	public List findListReferenceDoc(Map param) {
		List<Map<String, Object>> lists = Lists.newArrayList();
		lists = proSharedPublisher.findListReferenceDoc(param);
		return lists;
	}

	/* 전자계약 생성시 단가계약 전자계약 진행상태 업데이트 */
	public void createPriceEcntr(Map<String, Object> param) {
		unitPriceContractStatusRepository.createEcntr(param);
	}

	/* 전자계약 해지시 단가계약 전자계약 진행상태 업데이트 */
	public void terminatePriceEcntr(Map<String, Object> param) {
		unitPriceContractStatusRepository.terminateEcntr(param);
	}

	/* 전자계약 삭제시 단가계약 전자계약 진행상태 업데이트 */
	public void deletePriceEcntr(Map<String, Object> param) {
		unitPriceContractStatusRepository.deleteEcntr(param);
	}
	
	/* 전자계약 생성시 발주 전자계약 진행상태 업데이트 */
	public void createPoEcntr(Map<String, Object> param) {
		poStatusRepository.createEcntr(param);
	}
	
	/* 전자계약 해지시 발주 전자계약 진행상태 업데이트 */
	public void terminatePoEcntr(Map<String, Object> param) {
		poStatusRepository.terminateEcntr(param);
	}

	/* 전자계약 삭제시 발주 전자계약 진행상태 업데이트 */
	public void deletePoEcntr(Map<String, Object> param) {
		poStatusRepository.deleteEcntr(param);
	}

	public void updateCreatePoPr(Map<String, Object> param) {
		poStatusRepository.updateCreatePoPr(param);
	}

	public void approveApprovalQta(Map param) {
		qtaStatusRepository.approveApprovalQta(param);
	}

	public void rejectApprovalQta(Map param) {
		qtaStatusRepository.rejectApprovalQta(param);
	}

	public void cancelApprovalQta(Map param) {
		qtaStatusRepository.cancelApprovalQta(param);
	}

	public void submitApprovalQta(Map param) {
		qtaStatusRepository.submitApprovalQta(param);
	}


	public void bypassApprovalQta(Map<String, Object> qta) {
		qtaStatusRepository.bypassApprovalQta(qta);
	}

	public void saveDraftQta(Map param) {
		qtaStatusRepository.saveDraftQta(param);
	}
	
	public void updatePoByRequestChgContractDelete(Map poData) {
		poStatusRepository.updatePoByRequestChgContractDelete(poData);
	}
	
	public void updatePoByRequestChgContract(Map poData) {
		poStatusRepository.updatePoByRequestChgContract(poData);
	}
	
	public void updatePoByRequestTrmnContract(Map poData) {
		poStatusRepository.updatePoByRequestTrmnContract(poData);
	}
}
