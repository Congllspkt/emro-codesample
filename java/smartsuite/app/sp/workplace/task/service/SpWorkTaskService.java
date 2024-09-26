package smartsuite.app.sp.workplace.task.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.workplace.shared.service.TaskSharedService;
import smartsuite.app.sp.workplace.task.repository.SpWorkTaskRepository;
import smartsuite.config.workplace.lib.WorkplaceCacheConst;


@Service
@Transactional
public class SpWorkTaskService {
	
	@Inject
	private TaskSharedService taskSharedService;

	/** The sql session. */
	@Inject
	SpWorkTaskRepository workTaskRepository;

	/** The namespace. */


	/**************************************************
	 * 
	 * WORK 조회
	 * 
	 **************************************************/
	
	/**
	 * RFI Vendor 조회
	 * 
	 * @param param {rfi_vd_uuid}
	 * @return RFI Vendor
	 */
	private Map<String, Object> findRfiVendor(Map<String, Object> param) {
		return workTaskRepository.findRfiVendor(param);
	}
	
	/**
	 * RFx Vendor 조회
	 * 
	 * @param param {rfx_uuid}
	 * @return RFx Vendor
	 */
	private Map<String, Object> findRfxVendor(Map<String, Object> param) {
		return  workTaskRepository.findRfxVendor(param);
	}

	/**
	 * purc_typ_ccd="CONSTSVC"(공사용역)인 PO list 조회
	 * 
	 * @param param {po_uuids or po_uuid}
	 * @return PO list
	 */
	private List<Map<String, Object>> findListPoItemConstSvc(Map<String, Object> param) {
		param.put("purc_typ_ccd", "CONSTSVC");
		return workTaskRepository.findListPo(param);
	}

	/**
	 * purc_typ_ccd="QTY"(물품)인 PO ITEM list 조회
	 * 
	 * @param param {po_item_uuids or po_uuids or gr_uuid or asn_uuid or po_uuid}
	 * @return PO ITEM list
	 */
	private List<Map<String, Object>> findListPoItemQty(Map<String, Object> param) {
		param.put("purc_typ_ccd", "QTY");
		return workTaskRepository.findListPoItem(param);
	}

	/**************************************************
	 * 
	 * Create Task
	 * 
	 **************************************************/
	
	/**
	 * (SP)Create RFI Vendor task : (SP)RFI 협력사 task create 처리
	 * 
	 * @param {rfi_vd_uuid}
	 */
	public void createTaskRfiVendor(Map<String, Object> param) {
		// RFI VD task 생성
		Map<String, Object> rfiVendorInfo = findRfiVendor(param);
		if(rfiVendorInfo != null) {
			rfiVendorInfo.put(WorkplaceCacheConst.TASK_UUID, rfiVendorInfo.get("rfi_vd_uuid"));
			rfiVendorInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_RFI);
			rfiVendorInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.RFI_SUBM_STS);
			rfiVendorInfo.put(WorkplaceCacheConst.TASK_STS_CCD, rfiVendorInfo.get("rfi_subm_sts_ccd"));
			
			taskSharedService.createTask(rfiVendorInfo);
		}
	}
	
	/**
	 * (SP)Create RFx Vendor task : (SP)RFx 협력사 task create 처리
	 * 
	 * @param param {rfx_uuid}
	 */
	public void createTaskSpRfxVendor(Map<String, Object> param) {
		// RFX Vendor task 생성
		Map<String, Object> rfxVendorInfo = findRfxVendor(param);
		if(rfxVendorInfo != null) {
			rfxVendorInfo.put(WorkplaceCacheConst.TASK_UUID , rfxVendorInfo.get("rfx_vd_uuid"));

			// 역경매
			if(rfxVendorInfo.get("rfx_typ_ccd") != null && "RAUC".equals((String)rfxVendorInfo.get("rfx_typ_ccd"))){
				rfxVendorInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_R_AUCTION);
			}else{
				rfxVendorInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_RFX_BID);
			}
			rfxVendorInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.RFX_BID_STS);
			rfxVendorInfo.put(WorkplaceCacheConst.TASK_STS_CCD , rfxVendorInfo.get("rfx_bid_sts_ccd"));
			
			taskSharedService.createTask(rfxVendorInfo);
		}
	}
	
	/**
	 * Create PO task : 발주 task create 처리
	 * 
	 * @param param {po_uuids or po_uuid}
	 */
	public void createTaskPo(Map<String, Object> param) {
		// PO task 생성
		List<Map<String, Object>> poList = workTaskRepository.findListPo(param);
		for(Map<String, Object> poInfo : poList) {
			poInfo.put(WorkplaceCacheConst.TASK_UUID , poInfo.get("po_uuid"));
			poInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PO);
			poInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_STS);
			poInfo.put(WorkplaceCacheConst.TASK_STS_CCD , poInfo.get("po_sts_ccd"));
			
			taskSharedService.createTask(poInfo);
		}
	}
	
	/**
	 * Create PO task : 발주 협력사 접수/거부 task create 처리
	 * 
	 * @param param {po_uuids or po_uuid}
	 */
	public void createTaskPoBuyer(Map<String, Object> param) {
		// PO task 생성
		List<Map<String, Object>> poList = workTaskRepository.findListPo(param);
		for(Map<String, Object> poInfo : poList) {
			poInfo.put(WorkplaceCacheConst.TASK_UUID , poInfo.get("po_uuid"));
			poInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PO);
			poInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_VD_STS);
			poInfo.put(WorkplaceCacheConst.TASK_STS_CCD , poInfo.get("vd_po_sts_ccd"));
			
			taskSharedService.createTask(poInfo);
		}
	}
	
	/**
	 * Create PO task : 공사/용역 발주 헤더 task create 처리
	 * 
	 * @param param {po_uuids}
	 */
	public void createTaskCompleteServicePo(Map<String, Object> param) {
		// purc_typ_ccd="CONSTSVC"(공사용역)인 PO task 생성
		List<Map<String, Object>> poList = findListPoItemConstSvc(param);
		for(Map<String, Object> poInfo : poList) {
			poInfo.put(WorkplaceCacheConst.TASK_UUID , poInfo.get("po_uuid"));
			poInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PP);
			poInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_CMPLD_YN);
			poInfo.put(WorkplaceCacheConst.TASK_STS_CCD , poInfo.get("po_cmpld_yn"));
			
			taskSharedService.createTask(poInfo);
		}
	}
	
	/**
	 * Create PO Item task : 물품 발주 품목 task create 처리
	 * 
	 * @param param {po_uuids}
	 */
	public void createTaskCompleteProductPoItem(Map<String, Object> param) {
		// purc_typ_ccd="QTY"(물품)인 PO ITEM task 생성
		List<Map<String, Object>> poItemList = findListPoItemQty(param);
		for(Map<String, Object> poItem : poItemList) {
			poItem.put(WorkplaceCacheConst.TASK_UUID , poItem.get("po_item_uuid"));
			poItem.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PO_ITEM_LNO_QTY);
			poItem.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_CMPLD_YN);
			poItem.put(WorkplaceCacheConst.TASK_STS_CCD , isPoItemCompleted(poItem) ? "Y" : "N");
			
			taskSharedService.createTask(poItem);
		}
	}
	
	/**
	 * 발주 품목 종료여부
	 * 
	 * @param poItem
	 * @return
	 */
	private boolean isPoItemCompleted(Map<String, Object> poItem) {
		String poCmpldYn   = (String)poItem.get("po_cmpld_yn");		// 종료 여부
		String poCrcEndYn = (String)poItem.get("po_crc_end_yn");	// 강제종료 여부
		
		return "Y".equals(poCmpldYn) || "Y".equals(poCrcEndYn);
	}

	/**
	 * Create (SP)PO task : (SP)발주 task create 처리
	 * 
	 * @param param {po_uuids or po_uuid}
	 */
	public void createTaskPoVendor(Map<String, Object> param) {
		// PO task 생성
		List<Map<String, Object>> poList = workTaskRepository.findListPo(param);
		for(Map<String, Object> poInfo : poList) {
			poInfo.put(WorkplaceCacheConst.TASK_UUID , poInfo.get("po_uuid"));
			poInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_CNTR_PO_LIST);
			poInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_VD_STS);
			poInfo.put(WorkplaceCacheConst.TASK_STS_CCD , poInfo.get("vd_po_sts_ccd"));
			
			taskSharedService.createTask(poInfo);
		}
	}
	
	/**
	 * Create (SP)PO task : (SP) 공사/용역 발주 헤더 task create 처리
	 * 
	 * @param param {po_uuids}
	 */
	public void createTaskCompleteServicePoByVendor(Map<String, Object> param) {
		// purc_typ_ccd="CONSTSVC"(공사용역)인 PO task 생성
		List<Map<String, Object>> poList = findListPoItemConstSvc(param);
		for(Map<String, Object> poInfo : poList) {
			poInfo.put(WorkplaceCacheConst.TASK_UUID , poInfo.get("po_uuid"));
			poInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_PP_CURSITU_CONSTSVC);
			poInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_CMPLD_YN);
			poInfo.put(WorkplaceCacheConst.TASK_STS_CCD , poInfo.get("po_cmpld_yn"));
			
			taskSharedService.createTask(poInfo);
		}
	}
	
	/**
	 * Create (SP)PO Item task : (SP) 물품 발주 품목 task create 처리
	 * 
	 * @param param {po_uuids}
	 */
	public void createTaskCompleteProductPoItemByVendor(Map<String, Object> param) {
		// purc_typ_ccd="QTY"(물품)인 poItem task 생성
		List<Map<String, Object>> poItemList = findListPoItemQty(param);
		for(Map<String, Object> poItem : poItemList) {
			poItem.put(WorkplaceCacheConst.TASK_UUID , poItem.get("po_item_uuid"));
			poItem.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_PO_ITEM_LNO_LIST);
			poItem.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_CMPLD_YN);
			poItem.put(WorkplaceCacheConst.TASK_STS_CCD , isPoItemCompleted(poItem) ? "Y" : "N");
			
			taskSharedService.createTask(poItem);
		}
	}
	
	/**
	 * Create Product ASN task : 검수요청 task create 처리
	 * 
	 * @param param {asn_uuid or gr_uuid}
	 */
	public void createTaskReqAsn(Map<String, Object> param) {
		// ASN task 생성
		Map<String, Object> asnInfo = workTaskRepository.findAsn(param);
		if(asnInfo != null) {
			asnInfo.put(WorkplaceCacheConst.TASK_UUID , asnInfo.get("asn_uuid"));
			asnInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.ASN);
			asnInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.ASN_STS);
			asnInfo.put(WorkplaceCacheConst.TASK_STS_CCD , asnInfo.get("asn_sts_ccd"));
			
			taskSharedService.createTask(asnInfo);
		}
	}
	
	/**
	 * Create (SP)Product ASN task : (SP)검수요청 task create 처리
	 * 
	 * @param param {asn_uuid or gr_uuid}
	 */
	public void createTaskAsn(Map<String, Object> param) {
		// ASN task 생성
		Map<String, Object> asnInfo = workTaskRepository.findAsn(param);
		if(asnInfo != null) {
			asnInfo.put(WorkplaceCacheConst.TASK_UUID , asnInfo.get("asn_uuid"));
			asnInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_ASN);
			asnInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.ASN_STS);
			asnInfo.put(WorkplaceCacheConst.TASK_STS_CCD , asnInfo.get("asn_sts_ccd"));
			
			taskSharedService.createTask(asnInfo);
		}
	}
	
	/**
	 * Create (SP)PPREQ task : (SP)기성요청 task create 처리
	 * 
	 * @param param {asn_uuid or gr_uuid}
	 */
	public void createTaskPpreq(Map<String, Object> param) {
		// ASN task 생성
		Map<String, Object> asnInfo = workTaskRepository.findAsn(param);
		if(asnInfo != null) {
			asnInfo.put(WorkplaceCacheConst.TASK_UUID , asnInfo.get("asn_uuid"));
			asnInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_PP_CURSITU_CONSTSVC);
			asnInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PPREQ_STS);
			asnInfo.put(WorkplaceCacheConst.TASK_STS_CCD , asnInfo.get("asn_sts_ccd"));
			
			taskSharedService.createTask(asnInfo);
		}
	}
	
	/**************************************************
	 * 
	 * DELETE TASK
	 * 
	 **************************************************/
	/**
	 * Delete Product ASN task : 검수요청 task delete 처리
	 * 
	 * @param param {asn_uuid}
	 */
	public void deleteTaskAsn(Map<String, Object> param) {

		// ASN task 삭제
		Map<String, Object> asnInfo = workTaskRepository.findAsn(param);
		if(asnInfo != null) {
			asnInfo.put(WorkplaceCacheConst.TASK_UUID, asnInfo.get("asn_uuid"));
			taskSharedService.deleteTask(asnInfo);
		}
	}
	
	/**
	 * Delete PPREQ task : 기성요청 task delete 처리
	 * 
	 * @param param {asn_uuid}
	 */
	public void deleteTaskPpreq(Map<String, Object> param) {
		// ASN task 삭제
		Map<String, Object> asnInfo = workTaskRepository.findAsn(param);
		if(asnInfo != null) {
			asnInfo.put(WorkplaceCacheConst.TASK_UUID, asnInfo.get("asn_uuid"));
			taskSharedService.deleteTask(asnInfo);
		}
	}
	
}