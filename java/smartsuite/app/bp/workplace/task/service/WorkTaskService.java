package smartsuite.app.bp.workplace.task.service;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.inject.Inject;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.workplace.task.repository.WorkTaskRepository;
import smartsuite.app.common.workplace.shared.service.TaskSharedService;
import smartsuite.config.workplace.lib.WorkplaceCacheConst;

@Service
@Transactional
public class WorkTaskService {

	/** The Constant logger. */
	private static final Logger LOG = LoggerFactory.getLogger(WorkTaskService.class);

	@Inject
	private TaskSharedService taskSharedService;

	@Inject
	private WorkTaskRepository workTaskRepository;



	/**************************************************
	 *
	 * WORK 조회
	 *
	 **************************************************/
	/**
	 * PR 조회
	 *
	 * @param param {pr_uuid}
	 * @return PR
	 */
	private Map<String, Object> findPr(Map<String, Object> param) {
		return workTaskRepository.findPr(param);
	}

	/**
	 * PR ITEM 리스트 조회
	 *
	 * @param param {pr_item_uuids or pr_uuid or pr_item_uuid}
	 * @return PR ITEM list
	 */
	private List<Map<String, Object>> findListPrItem(Map<String, Object> param) {
		return workTaskRepository.findListPrItem(param);
	}

	/**
	 * RFI 조회
	 *
	 * @param param {rfi_uuid}
	 * @return RFI
	 */
	private Map<String, Object> findRfi(Map<String, Object> param) {
		return workTaskRepository.findRfi(param);
	}
	/**
	 * RFX 조회
	 *
	 * @param param {rfx_uuid}
	 * @return RFX
	 */
	private Map<String, Object> findRfx(Map<String, Object> param) {
		return workTaskRepository.findRfx(param);
	}

	/**
	 * RFx Vendor list 조회
	 *
	 * @param param {rfx_uuid}
	 * @return RFx Vendor list
	 */
	private List<Map<String, Object>> findListRfxVendor(Map<String, Object> param) {
		return workTaskRepository.findListRfxVendor(param);
	}

	/**
	 * RFx Vendor 조회
	 *
	 * @param param {rfx_uuid}
	 * @return RFx Vendor
	 */
	private Map<String, Object> findRfxVendor(Map<String, Object> param) {
		return workTaskRepository.findRfxVendor(param);
	}

	/**
	 * EVAL_SUBJ_EVALTR_RES 조회
	 *
	 * @param param {eval_subj_evaltr_res_uuid}
	 * @return EVAL_SUBJ_EVALTR_RES
	 */
	private Map<String, Object> findEvalSubjEvaltrRes(Map<String, Object> param) {
		return workTaskRepository.findEvalSubjEvaltrRes(param);
	}
	/**
	 * purc_typ_ccd="CONSTSVC"(공사용역) PO list 조회
	 *
	 * @param param {po_uuids or po_uuid}
	 * @return PO list
	 */
	private List<Map<String, Object>> findListPoConstSvc(Map<String, Object> param) {
		param.put("purc_typ_ccd", "CONSTSVC");
		return workTaskRepository.findListPo(param);
	}

	/**
	 * purc_typ_ccd="QTY"(물품) PO ITEM list 조회
	 *
	 * @param param {po_item_uuids or po_uuids or gr_uuid or asn_uuid or po_uuid}
	 * @return PO ITEM list
	 */
	private List<Map<String, Object>> findListPoItem(Map<String, Object> param) {
		param.put("purc_typ_ccd", "QTY");
		return workTaskRepository.findListPoItem(param);
	}

	/**
	 * INHD list 조회
	 *
	 * @param param {cntr_uuids}
	 * @return INHD list
	 */
	private List<Map<String, Object>> findListCntr(Map<String, Object> param) {
		return workTaskRepository.findListCntr(param);
	}

	/**
	 * 계약(CNTR) 조회
	 *
	 * @param param {cntr_uuids or cntr_uuid}
	 * @return INHD
	 */
	private Map<String, Object> findCntrByReq(Map<String, Object> param) {
		return workTaskRepository.findCntrByReq(param);
	}

	/**
	 * GR 조회
	 *
	 * @param param {gr_uuid}
	 * @return
	 */
	private Map<String, Object> findGr(Map<String, Object> param) {
		return workTaskRepository.findGr(param);
	}

	/**
	 * GR EVAL_SUBJ_EVALTR_RES list 조회
	 *
	 * @param param {gr_no, gr_rev}
	 * @return EVAL_SUBJ_EVALTR_RES list
	 */
	private List<Map<String, Object>> findListGrEvalSubjEvaltrRes(Map<String, Object> param) {
		return workTaskRepository.findListGrEvalSubjEvaltrRes(param);
	}

	/**
	 * GR EVAL_SUBJ_EVALTR_RES list 조회
	 *
	 * @param param {eval_typ_cd, eval_kind_cd, gr_no, gr_rev, evaltr_uuid}
	 * @return
	 */
	private List<Map<String, Object>> findListGrEvalSubjEvaltrResByEvaltr(Map<String, Object> param) {
		return workTaskRepository.findListGrEvalSubjEvaltrResByEvaltr(param);
	}

	/**************************************************
	 *
	 * DELETE TASK
	 *
	 **************************************************/

	/**
	 * Delete PR task : PR task delete 처리
	 *
	 * @param param {pr_uuid}
	 */
	public void deleteTaskPr(Map<String, Object> param) {
		Map<String, Object> deleteTarget = new HashMap<String, Object>();

		// 1. PR task 삭제
		deleteTarget.put(WorkplaceCacheConst.TASK_UUID, param.get("pr_uuid").toString());
		taskSharedService.deleteTask(deleteTarget);
	}

	/**
	 * Delete RFI task : RFI task delete 처리
	 *
	 * @param param {rfi_uuid}
	 */
	public void deleteTaskRfi(Map<String, Object> param) {
		// RFI task 삭제
		Map<String, Object> rfiInfo = findRfi(param);
		if(rfiInfo != null) {
			rfiInfo.put(WorkplaceCacheConst.TASK_UUID, rfiInfo.get("rfi_uuid"));
			taskSharedService.deleteTask(rfiInfo);
		}
	}

	/**
	 * Delete RFx task : RFx task delete 처리
	 *
	 * @param param {rfx_uuid}
	 */
	public void deleteTaskRfx(Map<String, Object> param) {
		Map<String, Object> deleteTarget = new HashMap<String, Object>();

		// 1. RFX task 삭제
		deleteTarget.put(WorkplaceCacheConst.TASK_UUID, param.get("rfx_uuid").toString());
		taskSharedService.deleteTask(deleteTarget);
	}

	/**
	 * Delete Contract task : 계약 task delete 처리
	 *
	 * @param param {cntr_uuid or cntr_uuids}
	 */
	public void deleteTaskCntr(Map<String, Object> param) {
		Map<String, Object> deleteTarget = new HashMap<String, Object>();

		deleteTarget.put(WorkplaceCacheConst.TASK_UUID, param.get("cntr_uuid").toString());
		taskSharedService.deleteTask(deleteTarget);
	}

	/**
	 * Delete PO task : 발주 task delete 처리
	 *
	 * @param param {po_uuid or po_uuids}
	 */
	public void deleteTaskPO(Map<String, Object> param) {
		//PO ITEM task 삭제
		List<Map<String, Object>> poItemList = workTaskRepository.findListPoItem(param);
		for(Map<String, Object> poItem : poItemList) {
			poItem.put(WorkplaceCacheConst.TASK_UUID, poItem.get("po_item_uuid"));
			taskSharedService.deleteTask(poItem);
		}

		//PO task 삭제
		List<Map<String, Object>> poList = workTaskRepository.findListPo(param);
		for(Map<String, Object> po : poList) {
			po.put(WorkplaceCacheConst.TASK_UUID, po.get("po_uuid"));
			taskSharedService.deleteTask(po);
		}
	}

	/**
	 * Delete Product ASN task : 검수요청 task delete 처리
	 *
	 * @param param {asn_uuid}
	 */
	public void deleteTaskAsn(Map<String, Object> param) {
		// ASN task 삭제
		Map<String, Object> asnInfo = workTaskRepository.findAsn(param);
		if(asnInfo != null) {
			asnInfo.put(WorkplaceCacheConst.TASK_UUID, asnInfo.get("po_uuid"));
			taskSharedService.deleteTask(asnInfo);
		}
	}

	/**
	 * DELETE Product GR task : 검수 task delete 처리
	 *
	 * @param param {gr_uuid}
	 */
	public void deleteTaskGr(Map<String, Object> param) {
		// GR task 삭제
		Map<String, Object> grInfo = findGr(param);
		if(grInfo != null) {
			grInfo.put(WorkplaceCacheConst.TASK_UUID, grInfo.get("gr_uuid"));
			taskSharedService.deleteTask(grInfo);
		}
	}

	/**
	 * DELETE PP task : 기성 task delete 처리
	 *
	 * @param param {gr_uuid}
	 */
	public void deleteTaskPp(Map<String, Object> param) {
		// GR task 삭제
		Map<String, Object> grInfo = findGr(param);
		if(grInfo != null) {
			grInfo.put(WorkplaceCacheConst.TASK_UUID, grInfo.get("gr_uuid"));
			taskSharedService.deleteTask(grInfo);
		}
	}

	/**
	 * DELETE Gr EVAL_SUBJ_EVALTR_RES task : 기성평가 평가자 task delete 처리
	 *
	 * @param param {eval_typ_cd, eval_kind_cd, gr_no, gr_rev, evaltr_uuid}
	 */
	public void deleteTaskGrEvalSubjEvaltrRes(Map<String, Object> param) {
		List<Map<String, Object>> evalSubjEvaltrResList = findListGrEvalSubjEvaltrResByEvaltr(param);
		for(Map<String, Object> evalSubjEvaltrRes : evalSubjEvaltrResList) {
			evalSubjEvaltrRes.put(WorkplaceCacheConst.TASK_UUID, evalSubjEvaltrRes.get("eval_subj_evaltr_res_uuid"));
			taskSharedService.deleteTask(evalSubjEvaltrRes);
		}
	}

	/**
	 * delete Request Contract (rfx 후속 프로세스)
	 *
	 * @param {cntr_req_uuid}
	 */
	public void deleteRequestContract(Map<String, Object> param) {
		Map<String, Object> deleteTarget = Maps.newHashMap();

		if(param.get("cntr_uuid") != null) {    //계약 목록에서 반려 시
			deleteTarget.put(WorkplaceCacheConst.TASK_UUID, param.get("cntr_uuid"));
			taskSharedService.deleteTask(deleteTarget);
		} else if(param.get("cntr_req_rcpt_uuid") != null) {    //계약 요청 목록에서 반려 시
			deleteTarget.put(WorkplaceCacheConst.TASK_UUID, param.get("cntr_req_rcpt_uuid"));
			taskSharedService.deleteTask(deleteTarget);
		}
	}

	public void closeRequestContract(Map<String, Object> param) {
		Map<String, Object> deleteTarget = Maps.newHashMap();

		deleteTarget.put(WorkplaceCacheConst.TASK_UUID, param.get("cntr_req_uuid"));
		taskSharedService.deleteTask(deleteTarget);
	}



	/**************************************************
	 *
	 * Close TASK
	 *
	 **************************************************/
	/**
	 * Close Previous PR Item task : 이전 차수의 PR ITEM task delete 처리
	 *
	 * @param param {prev_pr_id}
	 */
	public void closeTaskPrevPrItem(Map<String, Object> param) {
		Map<String, Object> deleteTarget = new HashMap<String, Object>();
		List<String> taskUuids = new ArrayList<String>();
		param.put("pr_uuid", param.get("prev_pr_id"));

		// PR ITEM task 종료
		List<Map<String, Object>> prItems = findListPrItem(param);
		for(Map<String, Object> prItem : prItems) {
			taskUuids.add(prItem.get("pr_item_uuid").toString());
		}
		deleteTarget.put(WorkplaceCacheConst.TASK_UUIDS, taskUuids);
		taskSharedService.deleteTask(deleteTarget);
	}

	/**
	 * Close RFI Vendor task : RFI 협력사 task delete 처리
	 *
	 * @param param {rfi_uuid}
	 */
	public void closeTaskRfiVendor(Map<String, Object> param) {
		// friVendor task 종료
		List<Map<String, Object>> rfiVendorList = workTaskRepository.findListRfiVendor(param);
		for(Map<String, Object> rfiVendor : rfiVendorList) {
			rfiVendor.put(WorkplaceCacheConst.TASK_UUID, rfiVendor.get("rfi_vd_uuid"));
			taskSharedService.deleteTask(rfiVendor);
		}
	}

	/**
	 * Close RFx Vendor task : RFx 협력사 task delete 처리
	 *
	 * @param param {rfx_uuid}
	 */
	public void closeTaskRfxVendor(Map<String, Object> param) {
		// RFx Vendor 상태 Update
		List<Map<String, Object>> rfxVendorList = findListRfxVendor(param);
		for(Map<String, Object> rfxVendor : rfxVendorList) {
			rfxVendor.put(WorkplaceCacheConst.TASK_UUID, rfxVendor.get(WorkplaceCacheConst.TASK_UUID));
			taskSharedService.deleteTask(rfxVendor);
		}

	}

	/**
	 * Close RFx Eval task : RFx 평가 task delete 처리
	 *
	 * @param param {rfx_uuid}
	 */
	public void closeTaskNpeEvalSubjEvaltrRes(Map<String, Object> param) {
		// EVAL_SUBJ_EVALTR_RES task 종료
		List<Map<String, Object>> evalSubjEvaltrResList = workTaskRepository.findListNpeEvalSubjEvaltrRes(param);
		for(Map<String, Object> evalSubjEvaltrRes : evalSubjEvaltrResList) {
			evalSubjEvaltrRes.put(WorkplaceCacheConst.TASK_UUID, evalSubjEvaltrRes.get("eval_subj_evaltr_res_uuid"));
			taskSharedService.deleteTask(evalSubjEvaltrRes);
		}
	}

	/**
	 * Close PO, POItem task : task delete 처리
	 *
	 * @param param {po_uuid}
	 */
	public void closeTaskPO(Map<String, Object> param) {
		// PO task 종료
		Map<String, Object> poInfo = workTaskRepository.findPrevPo(param);
		if(poInfo != null) {
			poInfo.put(WorkplaceCacheConst.TASK_UUID, poInfo.get("po_uuid"));
			taskSharedService.deleteTask(poInfo);

			Map<String, Object> dtParam = new HashMap<String, Object>();
			dtParam.put("po_uuid", poInfo.get("po_uuid"));

			List<Map<String, Object>> poItemList = workTaskRepository.findListPoItem(dtParam);
			for(Map<String, Object> poItem : poItemList) {
				poItem.put(WorkplaceCacheConst.TASK_UUID, poItem.get("po_item_uuid"));
				taskSharedService.deleteTask(poItem);
			}
		}
	}

	/**
	 * Close PO, POItem task : 이전차수의 발주 task delete 처리
	 *
	 * @param param {po_uuid}
	 */
	public void closeTaskPrePO(Map<String, Object> param) {
		// 이전차수 PO task 종료
		Map<String, Object> prevPo = workTaskRepository.findPrevPo(param);
		if(prevPo != null) {
			prevPo.put(WorkplaceCacheConst.TASK_UUID, prevPo.get("po_uuid"));
			taskSharedService.deleteTask(prevPo);

			Map<String, Object> dtParam = new HashMap<String, Object>();
			dtParam.put("po_uuid", prevPo.get("po_uuid"));

			List<Map<String, Object>> prevPoItemList = workTaskRepository.findListPoItem(dtParam);
			for(Map<String, Object> prevPoItem : prevPoItemList) {
				prevPoItem.put(WorkplaceCacheConst.TASK_UUID, prevPoItem.get("po_item_uuid"));
				taskSharedService.deleteTask(prevPoItem);
			}
		}
	}

	/**
	 * Close ASN task : 검수요청 task delete 처리
	 *
	 * @param param {asn_uuid or gr_uuid}
	 */
	public void closeTaskAsn(Map<String, Object> param) {
		// ASN task 종료
		Map<String, Object> asnInfo = workTaskRepository.findAsn(param);
		if(asnInfo != null) {
			asnInfo.put(WorkplaceCacheConst.TASK_UUID, asnInfo.get("asn_uuid"));
			taskSharedService.deleteTask(asnInfo);
		}
	}

	/**
	 * Close GR Eval task : 검수/기성 평가 task delete 처리
	 *
	 * @param param {gr_uuid}
	 */
	public void closeTaskGrEvalSubjEvaltrRes(Map<String, Object> param) {
		// EVAL_SUBJ_EVALTR_RES task 종료
		List<Map<String, Object>> evalSubjEvaltrResList = findListGrEvalSubjEvaltrRes(param);
		for(Map<String, Object> evalSubjEvaltrRes : evalSubjEvaltrResList) {
			evalSubjEvaltrRes.put(WorkplaceCacheConst.TASK_UUID, evalSubjEvaltrRes.get("eval_subj_evaltr_res_uuid"));
			taskSharedService.deleteTask(evalSubjEvaltrRes);
		}
	}

	public void closeTaskRequestContract(Map<String, Object> param) {
		// EVAL_SUBJ_EVALTR_RES task 종료
		List<Map<String, Object>> evalSubjEvaltrResList = findListGrEvalSubjEvaltrRes(param);
		for(Map<String, Object> evalSubjEvaltrRes : evalSubjEvaltrResList) {
			evalSubjEvaltrRes.put(WorkplaceCacheConst.TASK_UUID, evalSubjEvaltrRes.get("eval_subj_evaltr_res_uuid"));
			taskSharedService.deleteTask(evalSubjEvaltrRes);
		}
	}


	/**************************************************
	 *
	 * Create TASK
	 *
	 **************************************************/
	/**
	 * Create PR task : PR task create 처리
	 *
	 * @param param {pr_uuid}
	 */
	public void createTaskPr(Map<String, Object> param) {
		// PR task 생성
		Map<String, Object> prInfo = findPr(param);
		if(prInfo != null) {
			prInfo.put(WorkplaceCacheConst.TASK_UUID , prInfo.get("pr_uuid"));
			prInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PR);
			prInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PR_PRGS_STS);
			prInfo.put(WorkplaceCacheConst.TASK_STS_CCD , prInfo.get("pr_sts_ccd"));

			taskSharedService.createTask(prInfo);
		}
	}

	/**
	 * Create PR Item task : PR 품목 task create 처리
	 *
	 * @param param {pr_uuid or pr_item_uuids}
	 */
	public void createTaskPrItem(Map<String, Object> param) {
		// PR ITEM task 생성
		List<Map<String, Object>> prItems = findListPrItem(param);
		for(Map<String, Object> prItem : prItems){
			prItem.put(WorkplaceCacheConst.TASK_UUID , prItem.get("pr_item_uuid"));
			prItem.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PR);
			prItem.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PR_ITEM_STS);
			prItem.put(WorkplaceCacheConst.TASK_STS_CCD , prItem.get("pr_sts_ccd"));

			taskSharedService.createTask(prItem);
		}
	}

	/**
	 * Create PR Item task : 반송 PR 품목 task create 처리
	 *
	 * @param param {pr_item_uuids}
	 */
	public void createTaskReturnPrItem(Map<String, Object> param) {
		// PR ITEM task 생성
		List<Map<String, Object>> prItems = findListPrItem(param);
		for(Map<String, Object> prItem : prItems){
			prItem.put(WorkplaceCacheConst.TASK_UUID , prItem.get("pr_item_uuid"));
			prItem.put(WorkplaceCacheConst.MENU_CD, "PRO10030");
			prItem.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PR_ITEM_STS);
			prItem.put(WorkplaceCacheConst.TASK_STS_CCD , prItem.get("pr_prog_sts"));

			taskSharedService.createTask(prItem);
		}
	}

	/**
	 * Create PR Item task : 접수 PR 품목 task create 처리
	 *
	 * @param param {pr_item_uuids}
	 */
	public void createTaskReceivePrItem(Map<String, Object> param) {
		// PR ITEM task 생성
		List<Map<String, Object>> prItems = findListPrItem(param);
		for(Map<String, Object> prItem : prItems){
			prItem.put(WorkplaceCacheConst.TASK_UUID , prItem.get("pr_item_uuid"));
			prItem.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PR);
			prItem.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PR_ITEM_STS);
			prItem.put(WorkplaceCacheConst.TASK_STS_CCD , prItem.get("pr_sts_ccd"));

			taskSharedService.createTask(prItem);
		}
	}

	/**
	 * Create RFI task : RFI task create 처리
	 *
	 * @param param {rfi_uuid}
	 */
	public void createTaskRfi(Map<String, Object> param) {
		// RFI task 생성
		Map<String, Object> rfi = findRfi(param);
		if(rfi != null) {
			rfi.put(WorkplaceCacheConst.TASK_UUID , rfi.get("rfi_uuid"));
			rfi.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.RFI);
			rfi.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.RFI_STS);
			rfi.put(WorkplaceCacheConst.TASK_STS_CCD , rfi.get("rfi_sts_ccd"));

			taskSharedService.createTask(rfi);
		}
	}

	/**
	 * Create RFI Vendor : RFI 협력사 task create 처리
	 *
	 * @param param {rfi_uuid}
	 */
	public void createTaskRfiVendor(Map<String, Object> param) {
		// RFI VD task 생성
		List<Map<String, Object>> rfiVendorList = workTaskRepository.findListRfiVendor(param);
		for(Map<String, Object> rfiVendor : rfiVendorList) {
			rfiVendor.put(WorkplaceCacheConst.TASK_UUID , rfiVendor.get("rfi_vd_uuid"));
			rfiVendor.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_RFI);
			rfiVendor.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.RFI_SUBM_STS);
			rfiVendor.put(WorkplaceCacheConst.TASK_STS_CCD , rfiVendor.get("rfi_subm_sts_ccd"));

			taskSharedService.createTask(rfiVendor);
		}
	}

	/**
	 * (SP)Create RFI Vendor task : (SP)RFI 협력사 task create 처리
	 *
	 * @param {rfi_vd_uuid}
	 */
	public void createTaskSpRfiVd(Map<String, Object> param) {
		// RFI VD task 생성
		Map<String, Object> rfiVendorInfo = workTaskRepository.findRfiVendor(param);
		if(rfiVendorInfo != null) {
			rfiVendorInfo.put(WorkplaceCacheConst.TASK_UUID , rfiVendorInfo.get("rfi_vd_uuid"));
			rfiVendorInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_RFI);
			rfiVendorInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.RFI_SUBM_STS);
			rfiVendorInfo.put(WorkplaceCacheConst.TASK_STS_CCD , rfiVendorInfo.get("rfi_sbmt_sts"));

			taskSharedService.createTask(rfiVendorInfo);
		}
	}

	/**
	 * Create RFx : RFx task create 처리
	 *
	 * @param param {rfx_uuid}
	 */
	public void createTaskRfx(Map<String, Object> param) {
		// RFX task 생성
		Map<String, Object> rfxInfo = findRfx(param);
		if(rfxInfo != null) {
			rfxInfo.put(WorkplaceCacheConst.TASK_UUID , rfxInfo.get("rfx_uuid"));

			// 역경매
			if(rfxInfo.get("rfx_typ_ccd") != null && "RAUC".equals((String)rfxInfo.get("rfx_typ_ccd"))) {
				rfxInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.R_AUCTION);
			} else {
				rfxInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.RFX);
			}
			rfxInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.RFX_STS);
			rfxInfo.put(WorkplaceCacheConst.TASK_STS_CCD , rfxInfo.get("rfx_sts_ccd"));

			taskSharedService.createTask(rfxInfo);
		}
	}

	/**
	 * Create RFx Vendor task : RFx 협력사 task create 처리
	 *
	 * @param param {rfx_uuid}
	 */
	public void createTaskRfxVendor(Map<String, Object> param) {
		// RFx Vendor task 생성
		List<Map<String, Object>> rfxVendorList = findListRfxVendor(param);
		for(Map<String, Object> rfxVendor : rfxVendorList){
			rfxVendor.put(WorkplaceCacheConst.TASK_UUID , rfxVendor.get("rfx_vd_uuid"));

			// 역경매
			if(rfxVendor.get("rfx_typ_ccd") != null && "RAUC".equals((String)rfxVendor.get("rfx_typ_ccd"))) {
				rfxVendor.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_R_AUCTION);
			} else {
				rfxVendor.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_RFX_BID);
			}
			rfxVendor.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.RFX_BID_STS);
			rfxVendor.put(WorkplaceCacheConst.TASK_STS_CCD , rfxVendor.get("rfx_bid_sts_ccd"));

			taskSharedService.createTask(rfxVendor);
		}
	}

	/**
	 * Create NPE Eval task : RFx 모든 평가자 평가 task create 처리
	 *
	 * @param param {rfx_uuid}
	 */
	public void createTaskAllNpeEvalSubjEvaltrRes(Map<String, Object> param) {
		// EVAL_SUBJ_EVALTR_RES task 생성
		List<Map<String, Object>> evalSubjEvaltrResList = workTaskRepository.findListNpeEvalSubjEvaltrRes(param);
		for(Map<String, Object> evalSubjEvaltrRes : evalSubjEvaltrResList) {
			evalSubjEvaltrRes.put(WorkplaceCacheConst.TASK_UUID , evalSubjEvaltrRes.get("eval_subj_evaltr_res_uuid"));
			evalSubjEvaltrRes.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.NPE_EXCU);
			evalSubjEvaltrRes.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PRO_EVAL_PRGS_STS);
			evalSubjEvaltrRes.put(WorkplaceCacheConst.TASK_STS_CCD , evalSubjEvaltrRes.get("eval_sts_ccd"));

			taskSharedService.createTask(evalSubjEvaltrRes);
		}
	}

	/**
	 * Create Contract task : 계약 task create 처리
	 *
	 * @param param {cntr_req_rcpt_uuid}
	 */
	public void createTaskCntrByReq(Map<String, Object> param) {
		// CNTR task 생성
		Map<String, Object> cntrInfo = findCntrByReq(param);
		if(cntrInfo != null) {
			if("RFX".equals(cntrInfo.get("pre_req_tpy_ccd"))) {
				cntrInfo.put(WorkplaceCacheConst.TASK_UUID, cntrInfo.get("cntr_req_rcpt_uuid"));
				cntrInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.CNTR);
				cntrInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.CNTR_STS);
				cntrInfo.put(WorkplaceCacheConst.TASK_STS_CCD, cntrInfo.get("cntr_sts_ccd"));

				taskSharedService.createTask(cntrInfo);
			} else if ("PO".equals(cntrInfo.get("pre_req_tpy_ccd"))) {

			} else if ("OBC".equals(cntrInfo.get("pre_req_tpy_ccd"))) {

			}
		}
	}

	public void createTaskContract(Map<String, Object> param) {
		Map<String, Object> findContractInfo = findRequestContract(param);

		if(findContractInfo != null) {
			if(findContractInfo.get("cntr_uuid") != null) {
				findContractInfo.put(WorkplaceCacheConst.TASK_UUID , findContractInfo.get("cntr_uuid"));
				findContractInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.CNTR);
				findContractInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.CNTR_STS);
				findContractInfo.put(WorkplaceCacheConst.TASK_STS_CCD , findContractInfo.get("cntr_sts_ccd"));
			} else if(findContractInfo.get("cntr_req_rcpt_uuid") != null) {
				findContractInfo.put(WorkplaceCacheConst.TASK_UUID , findContractInfo.get("cntr_req_rcpt_uuid"));
				findContractInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.CNTR);
				findContractInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.CNTR_STS);
				findContractInfo.put(WorkplaceCacheConst.TASK_STS_CCD , findContractInfo.get("cntr_sts_ccd"));
			}

			taskSharedService.createTask(findContractInfo);
		}
	}

	public void createTaskCntr(Map<String, Object> param) {
	}

	/**
	 * Create Price Contract task : 단가계약 변경요청 반송 시 task create 처리
	 *
	 * @param param {cntr_uuid}
	 */
	public void createTaskReturnChangeRequestINHD(Map<String, Object> param) {
		// INHD task 생성
		Map<String, Object> inHD = findCntrByReq(param);
		if(inHD != null) {
			inHD.put(WorkplaceCacheConst.TASK_UUID , inHD.get("cntr_uuid"));
			inHD.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.CNTR);
			inHD.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.CNTR_STS);
			inHD.put(WorkplaceCacheConst.TASK_STS_CCD , inHD.get("cntr_prog_sts"));

			taskSharedService.createTask(inHD);
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
		for(Map<String, Object> po : poList) {
			po.put(WorkplaceCacheConst.TASK_UUID , po.get("po_uuid"));
			po.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PO);
			po.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_STS);
			po.put(WorkplaceCacheConst.TASK_STS_CCD , po.get("po_sts_ccd"));

			taskSharedService.createTask(po);
		}
	}

	/**
	 * Create PO task : 발주 변경요청 반송 시 task create 처리
	 *
	 * @param param {po_uuids or po_uuid}
	 */
	public void createTaskReturnChangeRequestPo(Map<String, Object> param) {
		// PO task 생성
		List<Map<String, Object>> poList = workTaskRepository.findListPo(param);
		for(Map<String, Object> po : poList) {
			po.put(WorkplaceCacheConst.TASK_UUID , po.get("po_uuid"));
			po.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PO_REQ_DEPT);
			po.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_STS);
			po.put(WorkplaceCacheConst.TASK_STS_CCD , po.get("po_sts_ccd"));

			taskSharedService.createTask(po);
		}
	}

	/**
	 * Create PO task : 발주 협력사 접수대기/접수 task create 처리
	 *
	 * @param param {po_uuids or po_uuid}
	 *
	 *  [todo hj.jang] 내부담당자가 협력사 접수 대기 상태에 대한 Task 생성하는 로직 필요한지 확인 필요
	 */
	public void createTaskVdPo(Map<String, Object> param) {
		// PO task 생성
		List<Map<String, Object>> poList = workTaskRepository.findListPo(param);
		for(Map<String, Object> po : poList) {
			po.put(WorkplaceCacheConst.TASK_UUID , po.get("po_uuid"));
			po.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PO);
			po.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_VD_STS);
			po.put(WorkplaceCacheConst.TASK_STS_CCD , po.get("vd_po_sts_ccd"));

			taskSharedService.createTask(po);
		}
	}

	/**
	 * Create PO task : 공사/용역 발주 헤더 task create 처리
	 *
	 * @param param {po_uuids or asn_uuid or gr_uuid or po_uuid}
	 */
	public void createTaskCompleteServicePo(Map<String, Object> param) {
		// purc_typ_ccd="CONSTSVC"(공사용역)인 PO task 생성
		List<Map<String, Object>> poList = findListPoConstSvc(param);
		for(Map<String, Object> po : poList) {
			po.put(WorkplaceCacheConst.TASK_UUID , po.get("po_uuid"));
			po.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PP);
			po.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_CMPLD_YN);
			po.put(WorkplaceCacheConst.TASK_STS_CCD , po.get("po_cmpld_yn"));

			taskSharedService.createTask(po);
		}
	}

	/**
	 * Create PO Item task : 물품 발주 품목 task create 처리
	 *
	 * @param param {po_item_uuids or po_uuids or asn_uuid or gr_uuid or po_uuid}
	 */
	public void createTaskCompleteProductPoItem(Map<String, Object> param) {
		// purc_typ_ccd="QTY"(물품)인 PO ITEM task 생성
		List<Map<String, Object>> poItemList = findListPoItem(param);
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
		String poCompYn   = (String)poItem.get("po_cmpld_yn");		// 종료 여부
		String poCrcEndYn = (String)poItem.get("po_crc_end_yn");	// 강제종료 여부

		return "Y".equals(poCompYn) || "Y".equals(poCrcEndYn);
	}

	/**
	 * Create (SP)PO task : (SP)발주 접수대기/접수 task create 처리
	 *
	 * @param param {po_uuids or po_uuid}
	 */
	public void createTaskPoVendor(Map<String, Object> param) {
		// PO task 생성
		List<Map<String, Object>> poList = workTaskRepository.findListPo(param);
		for(Map<String, Object> po : poList) {
			po.put(WorkplaceCacheConst.TASK_UUID , po.get("po_uuid"));
			po.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_CNTR_PO_LIST);
			po.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_VD_STS);
			po.put(WorkplaceCacheConst.TASK_STS_CCD , po.get("vd_po_sts_ccd"));

			taskSharedService.createTask(po);
		}
	}

	/**
	 * Create (SP)PO task : (SP) 공사/용역 발주 헤더 task create 처리
	 *
	 * @param param {po_uuids or asn_uuid or gr_uuid or po_uuid}
	 */
	public void createTaskCompleteServicePoByVendor(Map<String, Object> param) {
		// purc_typ_ccd="CONSTSVC"(공사용역)인 PO task 생성
		List<Map<String, Object>> poList = findListPoConstSvc(param);
		for(Map<String, Object> po : poList) {
			po.put(WorkplaceCacheConst.TASK_UUID , po.get("po_uuid"));
			po.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_PP_CURSITU_CONSTSVC);
			po.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_CMPLD_YN);
			po.put(WorkplaceCacheConst.TASK_STS_CCD , po.get("po_cmpld_yn"));

			taskSharedService.createTask(po);
		}
	}

	/**
	 * Create (SP)PO Item task : (SP) 물품 발주 품목 task create 처리
	 *
	 * @param param {po_item_uuids or po_uuids or asn_uuid or gr_uuid or po_uuid}
	 */
	public void createTaskCompleteProductPoItemByVendor(Map<String, Object> param) {
		// purc_typ_ccd="QTY"(물품)인 PO ITEM task 생성
		List<Map<String, Object>> poItemList = findListPoItem(param);
		for(Map<String, Object> poItem : poItemList) {
			poItem.put(WorkplaceCacheConst.TASK_UUID , poItem.get("po_item_uuid"));
			poItem.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.VD_PO_ITEM_LNO_LIST);
			poItem.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_CMPLD_YN);
			poItem.put(WorkplaceCacheConst.TASK_STS_CCD , isPoItemCompleted(poItem) ? "Y" : "N");

			taskSharedService.createTask(poItem);
		}
	}

	/**
	 * Create PO Eval task : PO 모든 평가자 평가 task create 처리
	 *
	 * @param param {po_no, po_rev}
	 */
	public void createTaskAllPoEvalSubjEvaltrRes(Map<String, Object> param) {
		// EVAL_SUBJ_EVALTR_RES task 생성
		List<Map<String, Object>> evalSubjEvaltrResList = workTaskRepository.findListNpeEvalSubjEvaltrRes(param);
		for(Map<String, Object> evalSubjEvaltrRes : evalSubjEvaltrResList) {
			evalSubjEvaltrRes.put(WorkplaceCacheConst.TASK_UUID , evalSubjEvaltrRes.get("eval_subj_evaltr_res_uuid"));
			evalSubjEvaltrRes.put(WorkplaceCacheConst.MENU_CD, "");
			evalSubjEvaltrRes.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.EVAL_PRGS_STS);
			evalSubjEvaltrRes.put(WorkplaceCacheConst.TASK_STS_CCD , evalSubjEvaltrRes.get("exc_sts"));

			taskSharedService.createTask(evalSubjEvaltrRes);
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
			asnInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.ASN_GRREQ_STS);
			asnInfo.put(WorkplaceCacheConst.TASK_STS_CCD , asnInfo.get("asn_sts_ccd"));

			taskSharedService.createTask(asnInfo);
		}
	}

	/**
	 * Create Product ASN task : 검수요청 건 담당자가 검수등록 진행 시 task create 처리
	 *
	 * @param param {gr_uuid}
	 */
	public void createTaskReceiveAsn(Map<String, Object> param) {
		// ASN task 생성
		Map<String, Object> asnInfo = workTaskRepository.findAsn(param);
		if(asnInfo != null) {
			asnInfo.put(WorkplaceCacheConst.TASK_UUID , asnInfo.get("asn_uuid"));
			asnInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.GR);
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
	 * Create Product GR task : 검수등록 task create 처리
	 *
	 * @param param {gr_uuid}
	 */
	public void createTaskGr(Map<String, Object> param) {
		// GR task 생성
		Map<String, Object> grInfo = findGr(param);
		if(grInfo != null) {
			grInfo.put(WorkplaceCacheConst.TASK_UUID , grInfo.get("gr_uuid"));
			grInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.GR);
			grInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.GR_STS);
			grInfo.put(WorkplaceCacheConst.TASK_STS_CCD , grInfo.get("gr_sts_ccd"));

			taskSharedService.createTask(grInfo);
		}
	}

	/**
	 * Create PPREQ task : 기성요청 task create 처리
	 *
	 * @param param {asn_uuid or gr_uuid}
	 */
	public void createTaskReceivePpreq(Map<String, Object> param) {
		// ASN task 생성
		Map<String, Object> asnInfo = workTaskRepository.findAsn(param);
		if(asnInfo != null) {
			asnInfo.put(WorkplaceCacheConst.TASK_UUID , asnInfo.get("asn_uuid"));
			asnInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PP);
			asnInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PPREQ_STS);
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

	/**
	 * Create PP task : 기성등록 task create 처리
	 *
	 * @param param {gr_uuid}
	 */
	public void createTaskPp(Map<String, Object> param) {
		// GR task 생성
		Map<String, Object> grInfo = findGr(param);
		if(grInfo != null) {
			grInfo.put(WorkplaceCacheConst.TASK_UUID , grInfo.get("gr_uuid"));
			grInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PP);
			grInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.ASN_PP_STS);
			grInfo.put(WorkplaceCacheConst.TASK_STS_CCD , grInfo.get("gr_sts_ccd"));

			taskSharedService.createTask(grInfo);
		}
	}

	/**
	 * Create GR/PP Eval task : GR/PP 모든 평가자 평가 task create 처리
	 *
	 * @param param {gr_uuid}
	 */
	public void createTaskAllGrPPEvalSubjEvaltrRes(Map<String, Object> param) {
		// EVAL_SUBJ_EVALTR_RES task 생성
		List<Map<String, Object>> evalSubjEvaltrResList = findListGrEvalSubjEvaltrRes(param);
		for(Map<String, Object> evalSubjEvaltrRes : evalSubjEvaltrResList) {
			evalSubjEvaltrRes.put(WorkplaceCacheConst.TASK_UUID , evalSubjEvaltrRes.get("eval_subj_evaltr_res_uuid"));
			evalSubjEvaltrRes.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PRO_EVAL_PRGS_STS);
			evalSubjEvaltrRes.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.GR_PP_EVAL);
			evalSubjEvaltrRes.put(WorkplaceCacheConst.TASK_STS_CCD , evalSubjEvaltrRes.get("eval_sts_ccd"));

			taskSharedService.createTask(evalSubjEvaltrRes);
		}
	}

	/**
	 * Create Eval task : 평가자 평가 수행 시 task create 처리
	 *
	 * @param param {eval_subj_evaltr_res_uuid}
	 */
	public void createTaskSaveEvalSubjEvaltrRes(Map<String, Object> param) {
		// EVAL_SUBJ_EVALTR_RES task 생성
		Map<String, Object> evalSubjEvaltrResInfo = findEvalSubjEvaltrRes(param);
		if(evalSubjEvaltrResInfo != null) {
			evalSubjEvaltrResInfo.put(WorkplaceCacheConst.TASK_UUID , evalSubjEvaltrResInfo.get("eval_subj_evaltr_res_uuid"));

			String evalTaskTypCcd = (String)evalSubjEvaltrResInfo.get("eval_task_typ_ccd");
			if("NPE".equals(evalTaskTypCcd)) {			// RFx 비가격평가
				evalSubjEvaltrResInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.NPE_EXCU);
				evalSubjEvaltrResInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PRO_EVAL_PRGS_STS);
				evalSubjEvaltrResInfo.put(WorkplaceCacheConst.TASK_STS_CCD , evalSubjEvaltrResInfo.get("npe_eval_sts_ccd"));
			} else if("GE".equals(evalTaskTypCcd)) {		// 검수/기성 평가
				evalSubjEvaltrResInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.GR_PP_EVAL);
				evalSubjEvaltrResInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PRO_EVAL_PRGS_STS);
				evalSubjEvaltrResInfo.put(WorkplaceCacheConst.TASK_STS_CCD , evalSubjEvaltrResInfo.get("ge_eval_sts_ccd"));
			}
		}

		taskSharedService.createTask(evalSubjEvaltrResInfo);
	}

	/**
	 * Create RequestContract task : RFX 후속 프로세스로 계약 요청 시 task create 처리
	 *
	 * @param {reqid : rfx 요청 목록으로부터 넘어온 신규 데이터 일 경우}
	 */
	public void createTaskRequestContract(Map<String, Object> param) {
		if(param.get("cntr_req_uuid") == null) {
			return;
		}

		Map<String, Object> findContractInfoByReq = findContractByReq(param);

		if (findContractInfoByReq != null) {
			findContractInfoByReq.put(WorkplaceCacheConst.TASK_UUID, findContractInfoByReq.get("cntr_req_uuid"));
			findContractInfoByReq.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.CNTR_REQ);
			findContractInfoByReq.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.REQ_RCPT_STS);
			findContractInfoByReq.put(WorkplaceCacheConst.TASK_STS_CCD, findContractInfoByReq.get("cntr_req_rcpt_sts_ccd"));

			taskSharedService.createTask(findContractInfoByReq);
		}
	}

	/**
	 * Request Contract 조회 (rfx 후속 프로세스)
	 *
	 * @param param
	 */
	private Map<String, Object> findRequestContract(Map<String, Object> param) {
		return workTaskRepository.findRequestContract(param);
	}

	private Map<String, Object> findContractByReq(Map<String, Object> param) {
		return workTaskRepository.findContractByReq(param);
	}

	public void errorTest(Map<String, Object> param) {
		try {
			workTaskRepository.errorTest(param);
		} catch(DataAccessException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

	public void deleteTaskCntrReq(Map<String, Object> param) {
		Map<String, Object> deleteTarget = new HashMap<String, Object>();

		if(param.get("cntr_uuid") != null) {    //계약 데이터 삭제
			deleteTarget.put(WorkplaceCacheConst.TASK_UUID, param.get("cntr_uuid"));
			taskSharedService.deleteTask(deleteTarget);
		} else if(param.get("cntr_req_rcpt_uuid") != null) {    //계약 요청 데이터 삭제
			deleteTarget.put(WorkplaceCacheConst.TASK_UUID, param.get("cntr_req_rcpt_uuid").toString());
			taskSharedService.deleteTask(deleteTarget);
		}
	}

	/**
	 * RFX 요청 데이터 적재
	 *
	 * @param {req_uuid}
	 */
	public void createTaskReqRfx(Map<String, Object> param) {
		List<Map<String, Object>> findReqRfxInfo = findListReceiptReqRfx(param);

		for(Map<String, Object> reqRfxInfo : findReqRfxInfo) {
			if(reqRfxInfo != null) {
				reqRfxInfo.put(WorkplaceCacheConst.TASK_UUID, reqRfxInfo.get("rfx_req_rcpt_uuid"));
				reqRfxInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.RFX_RCPT);
				reqRfxInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.RFX_RCPT_STS);
				reqRfxInfo.put(WorkplaceCacheConst.TASK_STS_CCD, reqRfxInfo.get("rcpt_sts_ccd"));

				taskSharedService.createTask(reqRfxInfo);
			}
		}
	}

	/**
	 * RFX 요청 반려
	 *
	 * @param {rfx_req_rcpt_uuid}
	 */
	public void deleteTaskReqRfx(Map<String, Object> param) {
		Map<String, Object> deleteTarget = Maps.newHashMap();
		List<Map<String, Object>> checkedList = (List<Map<String, Object>>) param.get("checkedList");

		if(checkedList.size() > 0) {
			for(Map<String, Object> checkedMap : checkedList) {
				Map<String, Object> sendParam = Maps.newHashMap();
				sendParam.put("rfx_req_rcpt_uuid", checkedMap.get("rfx_req_rcpt_uuid"));

				Map<String, Object> getParam = findListReqRfx(sendParam);
				deleteTarget.put(WorkplaceCacheConst.TASK_UUID, getParam.get("rfx_req_rcpt_uuid"));
				taskSharedService.deleteTask(deleteTarget);
			}
		}
	}

	/**
	 * rfx 요청 목록에서 다른 프로세스 진행 시 이전 task close 처리
	 *
	 * @param param
	 */
	public void closeTaskReqRfx(Map<String, Object> param) {
		Map<String, Object> deleteTarget = Maps.newHashMap();
		Map<String, Object> sendParam = Maps.newHashMap();
		sendParam.put("rfx_req_rcpt_uuid", param.get("updateProgressStatus"));

		deleteTarget.put(WorkplaceCacheConst.TASK_UUID, sendParam.get("rfx_req_rcpt_uuid"));
		taskSharedService.deleteTask(deleteTarget);
	}

	private List<Map<String, Object>> findListReceiptReqRfx(Map<String, Object> param) {
		return workTaskRepository.findListReceiptReqRfx(param);
	}

	private Map<String, Object> findListReqRfx(Map<String, Object> param) {
		return workTaskRepository.findListReqRfx(param);
	}

	/**
	 * PO 요청 데이터 적재
	 *
	 * @param {po_req_uuid}
	 */
	public void createTaskReqPo(Map<String, Object> param) {
		if(param.get("po_req_rcpt_uuid") == null) {
			Map<String, Object> findPo = findPoInfo((Map<String, Object>) param.get("po_uuid"));
			param.put("po_req_rcpt_uuid", findPo.get("po_req_rcpt_uuid"));
		}
		Map<String, Object> findReqPoInfo = findReqPo(param);
		if (findReqPoInfo != null) {
			findReqPoInfo.put(WorkplaceCacheConst.TASK_UUID, findReqPoInfo.get("po_req_rcpt_uuid"));     //.get("rfx_nxt_prcs_req_uuid");
			findReqPoInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.PO_RCPT);
			findReqPoInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.PO_RCPT_STS);
			findReqPoInfo.put(WorkplaceCacheConst.TASK_STS_CCD, findReqPoInfo.get("rcpt_sts_ccd"));
		}
		taskSharedService.createTask(findReqPoInfo);
	}

	private Map<String, Object> findReqPo(Map<String, Object> param) {
		return workTaskRepository.findReqPo(param);
	}

	public void createTaskNego(Map<String, Object> param) {
		Map<String, Object> findNegoInfo = findNegoInfo(param);
		if(findNegoInfo != null) {
			findNegoInfo.put(WorkplaceCacheConst.TASK_UUID, findNegoInfo.get("po_req_uuid"));     //.get("rfx_nxt_prcs_req_uuid");
			findNegoInfo.put(WorkplaceCacheConst.MENU_CD, WorkplaceCacheConst.NEGO);
			findNegoInfo.put(WorkplaceCacheConst.GRP_CCD, WorkplaceCacheConst.RFX_STS);
			findNegoInfo.put(WorkplaceCacheConst.TASK_STS_CCD, findNegoInfo.get("rfx_sts_ccd"));
		}
	}

	private Map<String, Object> findNegoInfo(Map<String, Object> param) {
		return null;
	}

	public void closeTaskPoReq(Map<String, Object> param) {
		Map<String, Object> deleteTarget = Maps.newHashMap();

		Map<String, Object> poInfo = findPoInfo(param);

		if(poInfo != null) {
			deleteTarget.put(WorkplaceCacheConst.TASK_UUID, poInfo.get("po_req_rcpt_uuid"));
			taskSharedService.deleteTask(deleteTarget);
		}
	}

	private Map<String, Object> findPoInfo(Map<String, Object> param) {
		return workTaskRepository.findPoInfo(param);
	}

	public void deleteTaskPoReq(Map<String, Object> param) {
		Map<String, Object> deleteTarget = Maps.newHashMap();

		deleteTarget.put(WorkplaceCacheConst.TASK_UUID, param.get("po_req_rcpt_uuid"));
		taskSharedService.deleteTask(deleteTarget);
	}
}