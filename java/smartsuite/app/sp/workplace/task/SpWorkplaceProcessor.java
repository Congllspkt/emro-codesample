package smartsuite.app.sp.workplace.task;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.workplace.task.service.SpWorkTaskService;

@Service
@Transactional
public class SpWorkplaceProcessor {

	@Inject
	private SpWorkTaskService workTaskService;
	
	/**
	 * rfi 견적서 임시저장 시 상태변경 처리를 한다
	 * 
	 * @param {rfi_vd_uuid}
	 * @Method Name : temporarySaveRfiBid
	 */
	public void temporarySaveRfiBid(Map<String, Object> param) {
		workTaskService.createTaskRfiVendor(param);
	}
	
	/**
	 * rfi 견적서 제출 시 상태변경 처리를 한다
	 *
	 * @param {rfi_vd_uuid}
	 * @Method Name : submitRfiBid
	 */
	public void submitRfiBid(Map<String, Object> param) {
		workTaskService.createTaskRfiVendor(param);
	}

	/**
	 * rfi 거부 시 상태변경 처리를 한다
	 *
	 * @param {rfi_vd_uuid}
	 * @Method Name : abandonRfiBid
	 */
	public void abandonRfiBid(Map<String, Object> param) {

		workTaskService.createTaskRfiVendor(param);
	}
	
	/**
	 * rfx 견적서 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim	
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : saveTempRfxBid
	 */
	public void saveTempRfxBid(Map<String, Object> param) {
		workTaskService.createTaskSpRfxVendor(param);
	}

	/**
	 * rfx 견적포기 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : abandonRfxBid
	 */
	public void abandonRfxBid(Map<String, Object> param) {
		workTaskService.createTaskSpRfxVendor(param);
	}
	
	/**
	 * rfx 견적서 제출 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : submitRfxBid
	 */
	public void submitRfxBid(Map<String, Object> param) {
		workTaskService.createTaskSpRfxVendor(param);
	}
	
	/**
	 * 역경매 견적서 임시저장 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : saveAuctionQta
	 */
	public void saveAuctionQta(Map<String, Object> param) {
		workTaskService.createTaskSpRfxVendor(param);
	}

	/**
	 * 역경매 견적포기 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : abandonAuctionQta
	 */
	public void abandonAuctionQta(Map<String, Object> param) {
		workTaskService.createTaskSpRfxVendor(param);
	}
	
	/**
	 * 역경매 견적서 제출 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param
	 * @Date : 2016. 2. 2
	 * @Method Name : submitAuctionQta
	 */
	public void submitAuctionQta(Map<String, Object> param) {
		workTaskService.createTaskSpRfxVendor(param);
	}
	
	/**
	 * 협력사 PO 접수 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuids}
	 * @Date : 2016. 2. 2
	 * @Method Name : acceptPoByVendor
	 */
	public void acceptPoByVendor(Map<String, Object> param) {
		workTaskService.createTaskPo(param);
		// workTaskService.createTaskVdPo(param);
		workTaskService.createTaskCompleteServicePo(param);
		workTaskService.createTaskCompleteProductPoItem(param);
		
		workTaskService.createTaskPoVendor(param);
		workTaskService.createTaskCompleteServicePoByVendor(param);
		workTaskService.createTaskCompleteProductPoItemByVendor(param);
	}
	
	/**
	 * 협력사 PO 거부 시 상태변경 처리를 한다
	 *
	 * @author : JongKyu Kim
	 * @param param {po_uuids}
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectPoByVendor
	 */
	public void rejectPoByVendor(Map<String, Object> param) {
		workTaskService.createTaskPoVendor(param);
		workTaskService.createTaskPo(param);
		workTaskService.createTaskPoBuyer(param);
	}

	/**
	 * 검수요청(ASN) 임시저장 (협력사)
	 *
	 * @author : JongKyu Kim
	 * @param param {asn_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftAsn
	 */
	public void saveDraftAsn(Map<String, Object> param) {
		workTaskService.createTaskAsn(param);
	}

	/**
	 * 검수요청(ASN) 삭제 (협력사)
	 *
	 * @param param {asn_uuid}
	 * @Method Name : deleteAsn
	 */
	public void deleteAsn(Map<String, Object> param) {
		// deleteTask
		workTaskService.deleteTaskAsn(param);
	}

	/**
	 * 검수요청(ASN) 임시저장 (협력사)
	 *
	 * @author : JongKyu Kim
	 * @param param {asn_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftAsn
	 */
	public void saveDraftAsn(Map<String, Object> param, final Object returnData) {
		if(returnData != null && returnData instanceof ResultMap) {
			if(!((ResultMap) returnData).isSuccess()){
				return;
			}
		}
		workTaskService.createTaskAsn(param);
	}

	/**
	 * 검수요청(ASN) 삭제 (협력사)
	 *
	 * @param param {asn_uuid}
	 * @Method Name : deleteAsn
	 */
	public void deleteAsn(Map<String, Object> param, final Object returnData) {
		// deleteTask
		if(returnData != null && returnData instanceof ResultMap) {
			if(!((ResultMap) returnData).isSuccess()){
				return;
			}
		}
		workTaskService.deleteTaskAsn(param);
	}

	/**
	 * 기성요청(PPREQ) 임시저장 (협력사)
	 *
	 * @author : JongKyu Kim
	 * @param param {asn_uuid}
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftPpreq
	 */
	public void saveDraftPpreq(Map<String, Object> param) {
		workTaskService.createTaskPpreq(param);
	}
	
	/**
	 * 기성요청(PPREQ) 삭제 (협력사)
	 * 
	 * @param param {asn_uuid}
	 * @Method Name : deletePpreq
	 */
	public void deletePpreq(Map<String, Object> param) {
		workTaskService.deleteTaskPpreq(param);
	}

}
