package smartsuite.app.bp.workplace.task;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.workplace.task.service.WorkTaskService;

@Service
@Transactional
public class WorkplaceProcessor {

	@Inject
	private WorkTaskService workTaskService;

	/**
	 * PR 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {pr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftPr
	 */
	public void saveDraftPr(Map<String, Object> param) {
		workTaskService.createTaskPr(param);
	}

	/**
	 * PR 삭제
	 *
	 * @param param
	 */
	public void deletePr(Map<String, Object> param) {
		workTaskService.deleteTaskPr(param);
	}

	/**
	 * PR 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {pr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalPr
	 */
	public void submitApprovalPr(Map<String, Object> param) {
		workTaskService.createTaskPr(param);
	}

	/**
	 * PR 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {pr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalPr
	 */
	public void approveApprovalPr(Map<String, Object> param) {
		workTaskService.createTaskPr(param);
		workTaskService.createTaskPrItem(param);
	}

	/**
	 * PR 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {pr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalPr
	 */
	public void rejectApprovalPr(Map<String, Object> param) {
		workTaskService.createTaskPr(param);
	}

	/**
	 * PR 결재취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {pr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalPr
	 */
	public void cancelApprovalPr(Map<String, Object> param) {
		workTaskService.createTaskPr(param);
	}

	/**
	 * PR 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {pr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalPr
	 */
	public void bypassApprovalPr(Map<String, Object> param) {
		workTaskService.createTaskPr(param);
		workTaskService.createTaskPrItem(param);
	}

	/**
	 * PR 변경으로 인한 이전차수 종료처리
	 *
	 * @param param {prev_pr_id}
	 */
	public void closePrevPrByModPr(Map<String, Object> param) {
		workTaskService.closeTaskPrevPrItem(param);
	}

	/**
	 * PR 반송처리 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {pr_item_uuids}
	 * @Date : 2016. 2. 2
	 * @Method Name : returnPr
	 */
	public void returnPr(Map<String, Object> param) {
		workTaskService.createTaskReturnPrItem(param);
	}

	/**
	 * PR 접수처리 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {pr_item_uuids}
	 * @Date : 2016. 2. 2
	 * @Method Name : receivePr
	 */
	public void receivePr(Map<String, Object> param) {
		workTaskService.createTaskReceivePrItem(param);
	}

	/**
	 * RFI 임시저장 시 상태변경 처리를 한다
	 *
	 * @author kh_lee
	 * @param param {rfi_uuid}
	 * @Date : 2017. 5.30
	 * @Method Name : temporarySaveRfi
	 */
	public void temporarySaveRfi(Map<String, Object> param) {
		workTaskService.createTaskRfi(param);
	}

	/**
	 * RFI 요청 시 상태변경 처리를 한다
	 *
	 * @author : kh_lee
	 * @param param {rfi_uuid}
	 * @Date : 2017. 5.30
	 * @Method Name : requestRfi
	 */
	public void requestRfi(Map<String, Object> param) {
		workTaskService.createTaskRfi(param);
		workTaskService.createTaskRfiVendor(param);
	}

	/**
	 * RFI 마감 시 상태변경 처리를 한다
	 *
	 * @author : kh_lee
	 * @param param {rfi_uuid}
	 * @Date : 2017. 5.30
	 * @Method Name : closeRfi
	 */
	public void closeRfi(Map<String, Object> param) {
		workTaskService.createTaskRfi(param);
		workTaskService.closeTaskRfiVendor(param);
	}

	/**
	 * RFI 종료 시 상태변경 처리를 한다
	 *
	 * @author : kh_lee
	 * @param param {rfi_uuid}
	 * @Date : 2017. 5.30
	 * @Method Name : completeRfi
	 */
	public void completeRfi(Map<String, Object> param) {
		workTaskService.createTaskRfi(param);
		workTaskService.closeTaskRfiVendor(param);
	}

	/**
	 * RFI 삭제 시 상태변경 처리를 한다
	 *
	 * @author kh_lee
	 * @param param {rfi_uuid}
	 * @Date : 2017. 5.30
	 * @Method Name : deleteRfi
	 */
	public void deleteRfi(Map<String, Object> param) {
		workTaskService.deleteTaskRfi(param);
	}

	/**
	 * RFx 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftRfx
	 */
	public void saveDraftRfx(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
	}

	/**
	 * RFx 삭제 시 상태변경 처리를 한다
	 *
	 * @author kh_lee
	 * @param param {rfx_uuid}
	 * @Date : 2016. 5.24
	 * @Method Name : deleteRfx
	 */
	public void deleteRfx(Map<String, Object> param) {
		workTaskService.deleteTaskRfx(param);
		workTaskService.createTaskReqRfx(param);
	}

	/**
	 * RFx 진행품의 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalRfxProg
	 */
	public void submitApprovalRfxProg(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
	}

	/**
	 * RFx 진행품의 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalRfxProg
	 */
	public void approveApprovalRfxProg(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.createTaskRfxVendor(param);
	}

	/**
	 * RFx 마감시간 변경
	 *
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : updateRfxCloseDt
	 */
	public void updateRfxCloseDt(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.createTaskRfxVendor(param);
	}

	/**
	 * RFx 진행품의 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalRfx
	 */
	public void rejectApprovalRfxProg(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
	}

	/**
	 * RFx 진행품의 결재취소/삭제 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalRfxProg
	 */
	public void cancelApprovalRfxProg(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
	}

	/**
	 * RFx 진행품의 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalRfxProg
	 */
	public void bypassApprovalRfxProg(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.createTaskRfxVendor(param);
	}

	/**
	 * RFx 시작 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : startRfx
	 */
	public void startRfx(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.createTaskRfxVendor(param);
	}

	/**
	 * RFx 마감 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : closeRfx
	 */
	public void closeRfx(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.closeTaskRfxVendor(param);
	}

	/**
	 * RFx 개찰 시 상태변경 처리를 한다
	 *
	 * @param param {rfx_uuid}
	 * @Method Name : openRfx
	 */
	public void openRfx(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
	}

	/**
	 * RFx 유찰 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : dropRfx
	 */
	public void dropRfx(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.closeTaskRfxVendor(param);
	}

	/**
	 * RFx 취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : dropRfx
	 */
	public void cancelRfx(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.closeTaskRfxVendor(param);
	}

	/**
	 * RFx multi-round(재견적/입찰) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : multiRoundRfx
	 */
	public void multiRoundRfx(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.closeTaskRfxVendor(param);
	}

	/**
	 * RFx 선정품의 결재 임시저장 시 상태변경 처리를 한다.
	 *
	 * @param param {rfx_uuid}
	 */
	public void temporarySaveApprovalRfxResult(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
	}

	/**
	 * RFx 선정품의 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalRfxResult
	 */
	public void submitApprovalRfxResult(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
	}

	/**
	 * RFx 선정품의 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalRfxResult
	 */
	public void approveApprovalRfxResult(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.closeTaskRfxVendor(param);
	}

	/**
	 * RFx 선정품의 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalRfxResult
	 */
	public void rejectApprovalRfxResult(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
	}

	/**
	 * RFx 선정품의 결재취소/삭제 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalRfxResult
	 */
	public void cancelApprovalRfxResult(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
	}

	/**
	 * RFx 선정품의 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalRfxResult
	 */
	public void bypassApprovalRfxResult(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.closeTaskRfxVendor(param);
	}

	/**
	 * RFx 선정 취소시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid, po_uuids or cntr_uuids}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelRfxResult
	 */
	public void cancelRfxResult(Map<String, Object> param) {
		workTaskService.createTaskRfx(param);
		workTaskService.deleteTaskPO(param);
		//workTaskService.deleteTaskCntr(param);
	}

	/**
	 * RFx 일부품목 선정 취소시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuids or cntr_uuids}
	 * @Date : 2016. 2. 2
	 * @Method Name : closeRfxItemResult
	 */
	public void closeRfxItemResult(Map<String, Object> param) {
		workTaskService.deleteTaskPO(param);
		//workTaskService.deleteTaskCntr(param);
	}

	public void saveNego(Map<String, Object> param) {
		workTaskService.createTaskNego(param);
	}

	/**
	 * PO 작성대기 상태 건 생성시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : createDraftPo
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 */
	public void createDraftPo(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftPo
	 * <b>Required:</b><br>
	 * param.po_uuid - 구매오더 ID<br>
	 */
	public void saveDraftPo(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
		workTaskService.closeTaskPoReq(param);
	}

	/**
	 * PO 삭제 시 상태변경 처리를 한다
	 *
	 * @param param {po_uuid}
	 * @Date : 2018. 2. 22
	 * @Method Name : deletePo
	 */
	public void deletePo(Map<String, Object> param) {
		workTaskService.deleteTaskPO(param);
		workTaskService.createTaskReqPo(param);
	}

	/**
	 * PO 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalPo
	 */
	public void submitApprovalPo(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalPo
	 */
	public void approveApprovalPo(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
		// 계약 진행하지 않을 경우만, 협력사 접수대기
		if(!"Y".equals(param.get("cntr_use_yn"))) {
			// workTaskService.createTaskVdPo(param);
			workTaskService.createTaskPoVendor(param);
		}
	}

	/**
	 * PO 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalPo
	 */
	public void rejectApprovalPo(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 결재취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalPo
	 */
	public void cancelApprovalPo(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid, cntr_use_yn}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalPo
	 */
	public void bypassApprovalPo(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
		// 계약 진행하지 않을 경우만, 협력사 접수대기
		if(!"Y".equals(param.get("cntr_use_yn"))) {
			//workTaskService.createTaskVdPo(param);
			workTaskService.createTaskPoVendor(param);
		}
	}

	/**
	 * PO - 전자계약 완료 시 상태변경 처리를 한다 (전자계약 완료 시, 협력사 PO 접수처리도 함)
	 *
	 * @param param {po_uuid}
	 * @date : 2017. 4.12
	 * @Method Name : completeElecCntrPo
	 */
	public void completeElecCntrPo(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
		//workTaskService.createTaskVdPo(param);
		workTaskService.createTaskCompleteServicePo(param);
		workTaskService.createTaskCompleteProductPoItem(param);

		workTaskService.createTaskPoVendor(param);
		workTaskService.createTaskCompleteServicePoByVendor(param);
		workTaskService.createTaskCompleteProductPoItemByVendor(param);
	}

	/**
	 * PO - 발주품목 발주완료여부 수정 (품목 단위의 발주완료는 물품일 경우만 가능함)
	 *
	 * @param param {po_item_uuids}
	 * @date : 2017. 4.12
	 * @Method Name : updatePoItemComplete
	 */
	public void updatePoItemComplete(Map<String, Object> param) {
		workTaskService.createTaskCompleteProductPoItem(param);
		workTaskService.createTaskCompleteProductPoItemByVendor(param);
	}

	/**
	 * PO - 발주의 발주완료여부 수정
	 *
	 * @param param {po_uuids}
	 * @date : 2017. 4.12
	 * @Method Name : updatePoComplete
	 */
	public void updatePoComplete(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
		//workTaskService.createTaskVdPo(param);
		workTaskService.createTaskCompleteServicePo(param);
		workTaskService.createTaskCompleteProductPoItem(param);

		workTaskService.createTaskPoVendor(param);
		workTaskService.createTaskCompleteServicePoByVendor(param);
		workTaskService.createTaskCompleteProductPoItemByVendor(param);
	}


	/**
	 * PO 변경요청 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftPoChangeRequest
	 */
	public void saveDraftPoChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 변경요청 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalPoChangeRequest
	 */
	public void submitApprovalPoChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 변경요청 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalPoChangeRequest
	 */
	public void approveApprovalPoChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 변경요청 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalPoChangeRequest
	 */
	public void rejectApprovalPoChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 변경요청 결재취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalPoChangeRequest
	 */
	public void cancelApprovalPoChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 변경요청 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalPoChangeRequest
	 */
	public void bypassApprovalPoChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 변경요청 반송 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : returnPoChangeRequest
	 */
	public void returnPoChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
		workTaskService.createTaskReturnChangeRequestPo(param);
	}

	/**
	 * PO 변경 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftPoChange
	 */
	public void saveDraftPoChange(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 변경 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalPoChange
	 */
	public void submitApprovalPoChange(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 변경 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalPoChange
	 */
	public void approveApprovalPoChange(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
		//workTaskService.createTaskVdPo(param);
		workTaskService.createTaskPoVendor(param);
		workTaskService.closeTaskPrePO(param);
	}

	/**
	 * PO 변경 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalPoChange
	 */
	public void rejectApprovalPoChange(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 변경 결재취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalPoChange
	 */
	public void cancelApprovalPoChange(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
	}

	/**
	 * PO 변경 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalPoChange
	 */
	public void bypassApprovalPoChange(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
		//workTaskService.createTaskVdPo(param);
		workTaskService.createTaskPoVendor(param);
		workTaskService.closeTaskPrePO(param);
	}

	/**
	 * PO 해지 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : closePo
	 */
	public void closePo(Map<String, Object> param) {
		workTaskService.closeTaskPO(param);
	}

	/**
	 * 단가계약 작성대기 상태 건 생성시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : createDraftPriceContract
	 */
	public void createDraftPriceContract(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftPriceContract
	 */
	public void saveDraftPriceContract(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 삭제 시 상태변경 처리를 한다
	 *
	 * @param param {cntr_uuid}
	 * @Date : 2018. 2. 22
	 * @Method Name : deletePriceContract
	 */
	public void deletePriceContract(Map<String, Object> param) {
		//workTaskService.deleteTaskCntr(param);
	}

	/**
	 * 단가계약 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalPriceContract
	 */
	public void submitApprovalPriceContract(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalPo
	 */
	public void approveApprovalPriceContract(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalPriceContract
	 */
	public void rejectApprovalPriceContract(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가게약 결재취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalPo
	 */
	public void cancelApprovalPriceContract(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalPo
	 */
	public void bypassApprovalPriceContract(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 - 전자계약 완료 시 상태변경 처리를 한다
	 *
	 * @param param {cntr_uuid}
	 * @date : 2017. 4.12
	 * @Method Name : completeElecCntrPriceContract
	 */
	public void completeElecCntrPriceContract(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 변경요청 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftPriceContractChangeRequest
	 */
	public void saveDraftPriceContractChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 변경요청 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalPriceContractChangeRequest
	 */
	public void submitApprovalPriceContractChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 변경요청 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalPriceContractChangeRequest
	 */
	public void approveApprovalPriceContractChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 변경요청 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalPriceContractChangeRequest
	 */
	public void rejectApprovalPriceContractChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가게약 변경요청 결재취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalPriceContractChangeRequest
	 */
	public void cancelApprovalPriceContractChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 변경요청 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalPriceContractChangeRequest
	 */
	public void bypassApprovalPriceContractChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 변경요청 반송 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : returnPriceContractChangeRequest
	 */
	public void returnPriceContractChangeRequest(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
		workTaskService.createTaskReturnChangeRequestINHD(param);
	}

	/**
	 * 단가계약 변경 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftPriceContractChange
	 */
	public void saveDraftPriceContractChange(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 변경 결재상신 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalPriceContractChange
	 */
	public void submitApprovalPriceContractChange(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 변경 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalPriceContractChange
	 */
	public void approveApprovalPriceContractChange(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 변경 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalPriceContractChange
	 */
	public void rejectApprovalPriceContractChange(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가게약 변경 결재취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalPriceContractChange
	 */
	public void cancelApprovalPriceContractChange(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 변경 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalPriceContractChange
	 */
	public void bypassApprovalPriceContractChange(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * 단가계약 해지 결재 Bypass(승인처리) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {cntr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : closePriceContract
	 */
	public void closePriceContract(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}


	/**
	 * 검수등록(입고,GR) 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftGr
	 */
	public void saveDraftGr(Map<String, Object> param) {
		workTaskService.createTaskReceiveAsn(param);
		workTaskService.createTaskGr(param);

		workTaskService.createTaskAsn(param);
	}

	/**
	 * 검수등록(입고,GR) 삭제
	 *
	 * @param param {gr_uuid}
	 */
	public void deleteGr(Map<String, Object> param) {
		// GR을 삭제해도 ASN 요청건은 요청상태로 남아있어야 함
		workTaskService.createTaskReceiveAsn(param);
		workTaskService.deleteTaskGr(param);

		workTaskService.createTaskReqAsn(param);
		workTaskService.createTaskAsn(param);
	}

	/**
	 * 검수승인(입고,GR) 결재요청 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalGr
	 */
	public void submitApprovalGr(Map<String, Object> param) {
		workTaskService.createTaskReceiveAsn(param);
		workTaskService.createTaskGr(param);
	}

	/**
	 * 검수승인(입고,GR) 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalGr
	 */
	public void approveApprovalGr(Map<String, Object> param) {
		workTaskService.closeTaskAsn(param);
		workTaskService.createTaskGr(param);
	}

	/**
	 * 검수승인(입고,GR) 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalGr
	 */
	public void rejectApprovalGr(Map<String, Object> param) {
		workTaskService.createTaskReceiveAsn(param);
		workTaskService.createTaskGr(param);
	}

	/**
	 * 검수승인(입고,GR) 결재취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalGr
	 */
	public void cancelApprovalGr(Map<String, Object> param) {
		workTaskService.createTaskReceiveAsn(param);
		workTaskService.createTaskGr(param);
	}

	/**
	 * 검수승인(입고,GR) 결재승인처리(Bypass) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalGr
	 */
	public void bypassApprovalGr(Map<String, Object> param) {
		workTaskService.closeTaskAsn(param);
		workTaskService.createTaskGr(param);
	}

	/**
	 * 검수요청(ASN)
	 *
	 * @author : JongKyu Kim
	 * @param param {asn_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : requestAsn
	 */
	public void requestAsn(Map<String, Object> param) {
		workTaskService.createTaskReqAsn(param);
		workTaskService.createTaskAsn(param);
	}

	/**
	 * 검수요청(ASN) 반려처리 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectRequestAsn
	 */
	public void rejectRequestAsn(Map<String, Object> param) {
		workTaskService.createTaskReceiveAsn(param);
		workTaskService.createTaskAsn(param);
	}

	/**
	 * 검수(입고,GR) 취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelGr
	 */
	public void cancelGr(Map<String, Object> param) {
		workTaskService.createTaskReceiveAsn(param);
		workTaskService.createTaskGr(param);

		workTaskService.createTaskAsn(param);

		workTaskService.createTaskCompleteProductPoItem(param);
		workTaskService.createTaskCompleteProductPoItemByVendor(param);
	}



	/**
	 * 기성등록(PP) 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftPp
	 */
	public void saveDraftPp(Map<String, Object> param) {
		workTaskService.createTaskReceivePpreq(param);
		workTaskService.createTaskPp(param);
	}

	/**
	 * 기성등록(PP) 삭제
	 *
	 * @param param {gr_uuid}
	 */
	public void deletePp(Map<String, Object> param) {
		// GR을 삭제해도 ASN 요청건은 요청상태로 남아있어야 함
		workTaskService.createTaskReceivePpreq(param);
		workTaskService.deleteTaskPp(param);
	}

	/**
	 * 기성승인(PP) 결재요청 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : submitApprovalPp
	 */
	public void submitApprovalPp(Map<String, Object> param) {
		workTaskService.createTaskReceivePpreq(param);
		workTaskService.createTaskPp(param);
	}

	/**
	 * 기성승인(PP) 결재승인 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : approveApprovalPp
	 */
	public void approveApprovalPp(Map<String, Object> param) {
		workTaskService.createTaskReceivePpreq(param);
		workTaskService.createTaskPp(param);
	}

	/**
	 * 기성승인(PP) 결재반려 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectApprovalPp
	 */
	public void rejectApprovalPp(Map<String, Object> param) {
		workTaskService.createTaskReceivePpreq(param);
		workTaskService.createTaskPp(param);
	}

	/**
	 * 기성승인(PP) 결재취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelApprovalPp
	 */
	public void cancelApprovalPp(Map<String, Object> param) {
		workTaskService.createTaskReceivePpreq(param);
		workTaskService.createTaskPp(param);
	}

	/**
	 * 기성승인(PP) 결재승인처리(Bypass) 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : bypassApprovalPp
	 */
	public void bypassApprovalPp(Map<String, Object> param) {
		workTaskService.createTaskReceivePpreq(param);
		workTaskService.createTaskPp(param);
	}

	/**
	 * 기성요청(PPREQ)
	 *
	 * @author : JongKyu Kim
	 * @param param {asn_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : requestPpreq
	 */
	public void requestPpreq(Map<String, Object> param) {
		workTaskService.createTaskPpreq(param);
		workTaskService.createTaskReceivePpreq(param);
	}

	/**
	 * 기성요청(PPREQ) 반려처리 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectRequestPpreq
	 */
	public void rejectRequestPpreq(Map<String, Object> param) {
		workTaskService.createTaskReceivePpreq(param);
		workTaskService.createTaskPpreq(param);
	}

	/**
	 * 기성(PP) 취소 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {gr_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : cancelPp
	 */
	public void cancelPp(Map<String, Object> param) {
		workTaskService.createTaskReceivePpreq(param);
		workTaskService.createTaskPp(param);

		workTaskService.createTaskPpreq(param);

		workTaskService.createTaskCompleteServicePo(param);
		workTaskService.createTaskCompleteServicePoByVendor(param);
	}

	/**
	 * 비가격 평가 생성 시 호출 (Muilty Round 시 동일 호출)
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : startNpeEval
	 */
	public void startNpeEval(Map<String, Object> param) {
		workTaskService.createTaskAllNpeEvalSubjEvaltrRes(param);
	}

	/**
	 * 비가격 평가 종료 시 호출
	 *
	 * @author : JongKyu Kim
	 * @param param {rfx_no, rfx_rev}
	 * @Date : 2016. 2. 2
	 * @Method Name : completeNpeEval
	 */
	public void completeNpeEval(Map<String, Object> param) {
		workTaskService.closeTaskNpeEvalSubjEvaltrRes(param);
	}

	/**
	 * 평가 담당자 임시저장/제출 시 호출
	 *
	 * @author : JongKyu Kim
	 * @param param {srv_eval_prog_mgt_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveEvalPerformInfo
	 */
	public void saveEvalPerformInfo(Map<String, Object> param) {
		workTaskService.createTaskSaveEvalSubjEvaltrRes(param);
	}

	/**
	 * 검수/기성 평가 담당자 임시저장/제출 시 호출
	 *
	 * @author : JongKyu Kim
	 * @param param {eval_subj_evaltr_res_uuid}
	 * @Date : 2024. 01. 17
	 * @Method Name : saveGeEvalFulfillment
	 */
	public void saveGeEvalFulfillment(Map<String, Object> param) {
		workTaskService.createTaskSaveEvalSubjEvaltrRes(param);
	}

	/**
	 * 검수/기성 평가 시작 시 호출
	 *
	 * @param param {gr_uuid}
	 * @Method Name : submitGrEval
	 */
	public void submitGrEval(Map<String, Object> param) {
		workTaskService.createTaskAllGrPPEvalSubjEvaltrRes(param);
	}

	/**
	 * 검수/기성 평가 종료 시 호출
	 *
	 * @param param {gr_uuid}
	 * @Method Name : completeGrEval
	 */
	public void completeGrEval(Map<String, Object> param) {
		workTaskService.closeTaskGrEvalSubjEvaltrRes(param);
	}

	/**
	 * 검수/기성 평가통보 이후 평가자 삭제 시 호출
	 *
	 * @param param {eval_typ_cd, eval_kind_cd, gr_no, gr_rev, eval_chr_uuid}
	 */
	public void deleteGrEvaluator(Map<String, Object> param) {
		workTaskService.deleteTaskGrEvalSubjEvaltrRes(param);
	}

	/**
	 * 검수/기성 평가 담당자 임시저장/제출 시 호출
	 *
	 * @author : hj.jang
	 * @param param {eval_subj_evaltr_res_uuid}
	 * @Date : 2024. 01. 23
	 * @Method Name : saveNpeEvalFulfillment
	 */
	public void saveNpeEvalFulfillment(Map<String, Object> param) {
		workTaskService.createTaskSaveEvalSubjEvaltrRes(param);
	}

	public void errorTest(Map<String, Object> param) {
		workTaskService.errorTest(param);
	}

	/**
	 * RFX 요청 목록 직계약/직발주 프로세스 진행 시
	 * 이전 task close 처리
	 *
	 * @param param
	 */
	public void updateProgressStatus(Map<String, Object> param) {
		workTaskService.closeTaskReqRfx(param);
	}

	/**
	 * RFX 후속 프로세스 - 계약 요청(FROM RFX 요청 목록)
	 *
	 * @Param {cntr_req_uuid}
	 */
	public void requestContract(Map<String, Object> param) {
		workTaskService.createTaskRequestContract(param);
	}

	/**
	 * RFX 후속 프로세스 - 계약 요청 반려 (FROM RFX 요청 목록)
	 * 반려 버튼 클릭 시, 상태는 반려로 변경
	 *
	 * @param {cntr_req_uuid}
	 */
	public void returnContractReq(Map<String, Object> param) {
		workTaskService.deleteRequestContract(param);
	}

	/**
	 * 계약 생성
	 *
	 * @param {cntr_req_rcpt_uuid}
	 */
	public void createContractByReq(Map<String, Object> param) {
		workTaskService.createTaskCntrByReq(param);
		workTaskService.closeRequestContract(param);
	}

	/**
	 * 계약 요청 반려(FROM RFX)
	 *
	 * @param
	 */
	public void rejectReqRfx(Map<String, Object> param) {
		workTaskService.deleteRequestContract(param);
	}

	/**
	 * 계약 임시저장 (계약 상태 : 작성중)
	 *
	 * @param param
	 */
	public void saveDraftContract(Map<String, Object> param) {
		workTaskService.createTaskContract(param);
	}

	/**
	 * 계약 삭제
	 *
	 * @param param
	 */
	public void returnContract(Map<String, Object> param) {
		workTaskService.deleteTaskCntr(param);
		workTaskService.createTaskRequestContract(param);
	}

	/**
	 * 계약 완료 (계약 상태 : 계약 완료)
	 *
	 * @param param
	 */
	public void completeContract(Map<String, Object> param) {
		workTaskService.deleteTaskCntrReq(param);
		workTaskService.createTaskContract(param);
	}

	/**
	 * 계약 해지 (계약 상태 : 계약 해지)
	 *
	 * @param param
	 */
	public void terminateContract(Map<String, Object> param) {
		workTaskService.createTaskCntr(param);
	}

	/**
	 * RFX 요청 데이터 적재
	 *
	 * @param param
	 */
	public void receiptReqRfx(Map<String, Object> param) {
		workTaskService.createTaskReqRfx(param);
	}

	/**
	 * RFX 요청 데이터 반려
	 *
	 * @param param
	 */
	public void returnReqs(Map<String, Object> param) {
		workTaskService.deleteTaskReqRfx(param);
	}

 	/**
	 * PO 요청 데이터 적재
	 *
	 * @param {po_req_uuid(rfx_nxt_prcs_req_uuid}
	 */
	public void receiptReqPo(Map<String, Object> param) {
		workTaskService.createTaskReqPo(param);
	}

	/**
	 * PO 요청 데이터 반려
	 *
	 * @param param
	 */
	public void returnReqsPo(Map<String, Object> param) {
		workTaskService.deleteTaskPoReq(param);
	}

}
