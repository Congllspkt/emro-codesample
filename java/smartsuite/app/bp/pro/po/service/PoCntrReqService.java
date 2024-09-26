package smartsuite.app.bp.pro.po.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.contract.cntrreq.service.ContractReqService;
import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.bp.contract.contractcnd.service.PurcContractCndService;
import smartsuite.app.bp.contract.data.ContractReq;
import smartsuite.app.bp.pro.po.repository.PoCntrReqRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.common.util.MapUtils;
import smartsuite.app.shared.CntrConst;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class PoCntrReqService {
	
	@Inject
	PoCntrReqRepository poCntrReqRepository;
	
	@Inject
	PoService poService;
	
	@Inject
	ContractService contractService;
	
	@Inject
	PurcContractCndService purcContractCndService;
	
	@Inject
	ContractReqService contractReqService;
	
	@Inject
	SharedService sharedService;
	
	@Inject
	ProStatusService proStatusService;
	
	public Map findDefaultPoCntrReq(Map param) {
		Map poData = poService.findPo(param);
		List<Map> poItems = (List<Map>) poData.get("items");
		List<Map> paymentPlans = (List<Map>) poData.get("paymentPlans");
		
		Map poCntrReqData = Maps.newHashMap();
		poCntrReqData.put("po_uuid", poData.get("po_uuid"));
		poCntrReqData.put("cntr_req_typ_ccd", param.get("cntr_req_typ_ccd"));
		
		if(poData.get("cntr_uuid") != null) {
			Map contractInfo = contractService.findContract(poData);
			
			poCntrReqData.put("purc_grp_cd", contractInfo.get("purc_grp_cd"));
			poCntrReqData.put("disp_purc_grp_nm", contractInfo.get("purc_grp_nm"));
			poCntrReqData.put("cntr_pic_id", contractInfo.get("cntr_pic_id"));
			poCntrReqData.put("disp_cntr_pic_nm", contractInfo.get("cntr_pic_nm"));
		}
		
		Map result = Maps.newHashMap();
		result.put("poCntrReqData", poCntrReqData);
		if("CHG".equals(poCntrReqData.get("cntr_req_typ_ccd"))) {
			result.put("purcCntrData", this.makePurcCntrByPoData(poData));
			result.put("purcCntrInfoData", this.makePurcCntrInfoByPoData(poData));
			result.put("purcCntrItemList", this.makePurcCntrItemListByPoItems(poItems));
			result.put("purcCntrPymtExptList", this.makePurcCntrPymtExptListByPaymentPlans(paymentPlans));
		}
		return result;
	}
	
	private Map makePurcCntrByPoData(Map poData) {
		Map purcCntr = Maps.newHashMap();
		purcCntr.put("cnd_typ_ccd", "CNTR");
		MapUtils.copyToKeyValue(purcCntr, poData, Lists.newArrayList(
				"oorg_cd",
				"purc_typ_ccd",
				"domovrs_div_ccd",
				"vd_cd",
				"disp_vd_nm",
				"erp_vd_cd",
				"cur_ccd",
				"cntr_st_dt",
				"cntr_exp_dt",
				"cntr_amt",
				"sup_amt",
				"vat_amt"
		));
		return purcCntr;
	}
	
	private Map makePurcCntrInfoByPoData(Map poData) {
		Map purcCntrInfo = Maps.newHashMap();
		MapUtils.copyToKeyValue(purcCntrInfo, poData, Lists.newArrayList(
				"pymtmeth_ccd",
				"pymtmeth_expln",
				"pymtmeth_use_yn",
				"dlvymeth_ccd",
				"dlvymeth_expln",
				"dlvymeth_use_yn",
				"tax_typ_ccd",
				"otrexp",
				"otrexp_expln",
				"pymtmeth_cnd",
				"dfrm_ro",
				"cpgur_use_yn",
				"cpgur_typ_ccd",
				"cpgur_ro",
				"cpgur_amt",
				"cpgur_st_dt",
				"cpgur_exp_dt",
				"defpgur_use_yn",
				"defpgur_typ_ccd",
				"defpgur_ro",
				"defpgur_amt",
				"defpgur_pd_typ_ccd",
				"defpgur_pd",
				"apymtgur_use_yn",
				"apymtgur_typ_ccd",
				"apymtgur_ro",
				"apymtgur_amt",
				"apymtgur_st_dt",
				"apymtgur_exp_dt",
				"stax_yn",
				"sttpymt_ro_typ_ccd",
				"sttpymtmeth_ccd"
		));
		return purcCntrInfo;
	}
	
	private List<Map> makePurcCntrItemListByPoItems(List<Map> poItems) {
		List<Map> purcCntrItemList = Lists.newArrayList();
		for(Map poItem : poItems) {
			Map purcCntrItem = Maps.newHashMap();
			MapUtils.copyToKeyValue(purcCntrItem, poItem, Lists.newArrayList(
					"plt_cd",
					"item_oorg_cd",
					"item_cd",
					"item_nm",
					"item_nm_en",
					"disp_item_nm",
					"item_spec",
					"item_spec_dtl",
					"item_cd_crn_typ_ccd",
					"uom_ccd",
					"item_qty",
					"item_uprc",
					"item_amt",
					"df_yn",
					"req_dlvy_dt",
					"dlvy_dt",
					"cntr_st_dt",
					"cntr_exp_dt",
					"dlvy_plc",
					"pr_item_uuid",
					"pr_no",
					"pr_lno",
					"rfx_item_uuid",
					"rfx_no",
					"rfx_rnd",
					"rfx_lno",
					"rfx_bid_item_uuid",
					"rfx_bid_no",
					"dlvy_ldtm"
			));
			MapUtils.copyToDiffKeyValue(purcCntrItem, Lists.newArrayList(
					"item_lno",
					"item_qty",
					"item_uprc",
					"item_amt"
			), poItem, Lists.newArrayList(
					"po_lno",
					"po_qty",
					"po_uprc",
					"po_amt"
			));
			purcCntrItemList.add(purcCntrItem);
		}
		return purcCntrItemList;
	}
	
	private List<Map> makePurcCntrPymtExptListByPaymentPlans(List<Map> paymentPlans) {
		List<Map> purcCntrPymtExptList = Lists.newArrayList();
		for(Map paymentPlan : paymentPlans) {
			Map purcCntrPymtExpt = Maps.newHashMap();
			MapUtils.copyToKeyValue(purcCntrPymtExpt, paymentPlan, Lists.newArrayList(
					"pymt_typ_ccd",
					"pymt_ro",
					"pymt_amt",
					"pymt_expt_dt",
					"pymt_revno"
			));
			purcCntrPymtExptList.add(purcCntrPymtExpt);
		}
		return purcCntrPymtExptList;
	}
	
	public Map findPoCntrReq(Map param) {
		if(param == null) {
			return null;
		}
		if(param.get("po_cntr_req_uuid") == null) {
			return null;
		}
		
		Map poCntrReqData = poCntrReqRepository.findPoCntrReq(param);
		return poCntrReqData;
	}
	
	public ResultMap savePoCntrReq(Map param) {
		Map poCntrReqData = (Map) param.get("poCntrReqData");
		
		if("CHG".equals(poCntrReqData.get("cntr_req_typ_ccd"))) {
			ResultMap purcContractResult = purcContractCndService.savePurcCntr(param, false);
			if(purcContractResult.isSuccess()) {
				Map resultData = purcContractResult.getResultData();
				poCntrReqData.put("purc_cntr_uuid", resultData.get("purc_cntr_uuid"));
			}
		}
		
		boolean isNew = false;
		if(poCntrReqData.get("po_cntr_req_uuid") == null) {
			isNew = true;
			
			poCntrReqData.put("po_cntr_req_uuid", UUID.randomUUID().toString());
		}
		
		if(isNew) {
			poCntrReqRepository.insertPoCntrReq(poCntrReqData);
		} else {
			poCntrReqRepository.updatePoCntrReq(poCntrReqData);
		}
		
		return ResultMap.SUCCESS(poCntrReqData);
	}
	
	public ResultMap requestPoCntrReq(Map param) {
		ResultMap saveMap = this.savePoCntrReq(param);
		if(saveMap.isFail()) {
			return saveMap;
		}
		
		Map poCntrReqData = saveMap.getResultData();
		Map poData = poService.findPo(poCntrReqData);
		ResultMap requestMap;
		if("CHG".equals(poCntrReqData.get("cntr_req_typ_ccd"))) {
			requestMap = this.requestChgContract(poCntrReqData, poData);
		} else {
			requestMap = this.requestTrmnContract(poCntrReqData, poData);
		}
		
		if(requestMap.isFail()) {
			return requestMap;
		}
		this.updateRequestStatus((String) poCntrReqData.get("po_cntr_req_uuid"), "REQ", null);
		if("CHG".equals(poCntrReqData.get("cntr_req_typ_ccd"))) {
			proStatusService.updatePoByRequestChgContract(poData);
		} else {
			proStatusService.updatePoByRequestTrmnContract(poData);
		}
		return ResultMap.SUCCESS();
	}
	
	private ResultMap requestChgContract(Map poCntrReqData, Map poData) {
		ContractReq contractReq = ContractReq.PO_CHANGE(
				(String) poCntrReqData.get("po_cntr_req_uuid"),
				(String) poCntrReqData.get("purc_cntr_uuid"),
				(String) poData.get("po_tit"),
				(String) Auth.getCurrentUserInfo().get("usr_id"),
				(String) poData.get("vd_cd"),
				(String) poData.get("oorg_cd"),
				(String) poCntrReqData.get("purc_grp_cd"),
				(String) poData.get("cntr_uuid")
		);
		contractReq.setReqRsn((String) poCntrReqData.get("cntr_req_rsn"));
		return contractReqService.requestContract(contractReq);
	}
	
	private ResultMap requestTrmnContract(Map poCntrReqData, Map poData) {
		ContractReq contractReq = ContractReq.PO_TERMINATION(
				(String) poCntrReqData.get("po_cntr_req_uuid"),
				null,
				(String) poData.get("po_tit"),
				(String) Auth.getCurrentUserInfo().get("usr_id"),
				(String) poData.get("vd_cd"),
				(String) poData.get("oorg_cd"),
				(String) poCntrReqData.get("purc_grp_cd"),
				(String) poData.get("cntr_uuid")
		);
		contractReq.setReqRsn((String) poCntrReqData.get("cntr_req_rsn"));
		return contractReqService.requestContract(contractReq);
	}
	
	public void updateRequestStatus(String poCntrReqUuid, String reqStsCcd, String retRsn) {
		Map param = Maps.newHashMap();
		param.put("po_cntr_req_uuid", poCntrReqUuid);
		param.put("req_sts_ccd", reqStsCcd);
		param.put("ret_rsn", retRsn);
		poCntrReqRepository.updateRequestStatus(param);
	}
	
	public ResultMap receiptCntrChgReqByPo(Map data) {
		this.updateRequestStatus((String) data.get("req_uuid"), "PRGSG", null);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap rejectCntrChgReqByPo(Map data) {
		this.updateRequestStatus((String) data.get("req_uuid"), "RET", (String) data.get("ret_rsn"));
		return ResultMap.SUCCESS();
	}
	
	public ResultMap completeCntrChgReqByPo(Map data) {
		String reqUuid = (String) data.get("req_uuid");
		String cntrUuid = (String) data.get("cntr_uuid");
		
		Map param = Maps.newHashMap();
		param.put("po_cntr_req_uuid", reqUuid);
		
		Map poCntrReqData = this.findPoCntrReq(param);
		Map poData = poService.findPo(poCntrReqData);
		
		ResultMap resultMap = ResultMap.SUCCESS();
		if("CHG".equals(poCntrReqData.get("cntr_req_typ_ccd"))) {
			resultMap = this.poChangeByCntrChgReq(poCntrReqData, poData, cntrUuid);
		} else if("TRMN".equals(poCntrReqData.get("cntr_req_typ_ccd"))) {
			resultMap = this.poTerminateByCntrChgReq(poData);
		}
		this.updateRequestStatus(reqUuid, "CMPLD", null);
		return resultMap;
	}
	
	private ResultMap poTerminateByCntrChgReq(Map poData) {
		proStatusService.closePo(poData);
		return ResultMap.SUCCESS();
	}
	
	private ResultMap poChangeByCntrChgReq(Map poCntrReqData, Map poData, String cntrUuid) {
		List<Map> poItemList = (List<Map>) poData.get("items");
		List<Map> paymentPlanList = (List<Map>) poData.get("paymentPlans");
		
		Map purcCntr = purcContractCndService.find(poCntrReqData);
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		Map purcCntrInfoData = (Map) purcCntr.get("purcCntrInfoData");
		List<Map> purcCntrItemList = (List<Map>) purcCntr.get("purcCntrItemList");
		List<Map> purcCntrPymtExptList = (List<Map>) purcCntr.get("purcCntrPymtExptList");
		List<Map> purcCntrCstmList = (List<Map>) purcCntr.get("purcCntrCstmList");
		
		BigDecimal poRevno = ConvertUtils.convertBigDecimal(poData.get("po_revno"));
		Map newPoData = this.makePoDataByPurcCntrByCntrChgReq(poData, purcCntrData, purcCntrInfoData, purcCntrItemList, purcCntrPymtExptList, purcCntrCstmList, cntrUuid);
		List<Map> newPoItems = this.makePoItemDataByPurcCntrByCntrChgReq(poItemList, purcCntrItemList);
		
		newPoData.put("po_uuid", null);
		newPoData.put("po_revno", ConvertUtils.convertString(poRevno.add(BigDecimal.ONE)));
		
		Map saveParam = Maps.newHashMap();
		saveParam.put("poData", newPoData);
		saveParam.put("insertItems", newPoItems);
		saveParam.put("paymentPlans", purcCntrPymtExptList);
		
		String newPoUuid = poService.savePo(saveParam);
		
		Map keyParam = Maps.newHashMap();
		keyParam.put("po_uuid", newPoUuid);
		
		proStatusService.bypassApprovalPoChange(keyParam);                   // 1. 상태 업데이트
		poService.updatePrevPoByModification(keyParam, "CNTR_CHG"); // 2. 이전 차수의 상태 업데이트
		poService.updateCurrentPo(keyParam);                                 // 3. 현재 유효한 발주여부(EFCT_PO_YN)를 업데이트 한다.
		return ResultMap.SUCCESS();
	}
	
	private Map makePoDataByPurcCntrByCntrChgReq(Map originPoData, Map purcCntrData, Map purcCntrInfoData, List<Map> purcCntrItemList, List<Map> purcCntrPymtExptList, List<Map> purcCntrCstmList, String cntrUuid) {
		Map poData = Maps.newHashMap(originPoData);
		MapUtils.copyToKeyValue(poData, purcCntrInfoData, Lists.newArrayList(
				"pymtmeth_ccd",
				"pymtmeth_expln",
				"pymtmeth_use_yn",
				"dlvymeth_ccd",
				"dlvymeth_expln",
				"dlvymeth_use_yn",
				"tax_typ_ccd",
				"otrexp",
				"otrexp_expln",
				"pymtmeth_cnd",
				"dfrm_ro",
				"cpgur_use_yn",
				"cpgur_typ_ccd",
				"cpgur_ro",
				"cpgur_amt",
				"cpgur_st_dt",
				"cpgur_exp_dt",
				"defpgur_use_yn",
				"defpgur_typ_ccd",
				"defpgur_ro",
				"defpgur_amt",
				"defpgur_pd_typ_ccd",
				"defpgur_pd",
				"apymtgur_use_yn",
				"apymtgur_typ_ccd",
				"apymtgur_ro",
				"apymtgur_amt",
				"apymtgur_st_dt",
				"apymtgur_exp_dt",
				"stax_yn",
				"sttpymt_ro_typ_ccd",
				"sttpymtmeth_ccd"
		));
		MapUtils.copyToKeyValue(poData, purcCntrData, Lists.newArrayList(
				"domovrs_div_ccd",
				"cur_ccd",
				"cntr_st_dt",
				"cntr_exp_dt",
				"cntr_amt",
				"sup_amt",
				"vat_amt"
		));
		
		Map contractParam = Maps.newHashMap();
		contractParam.put("cntr_uuid", cntrUuid);
		Map contractInfo = contractService.findContract(contractParam);
		if("OFFLINE".equals(contractInfo.get("cntr_sgnmeth_ccd"))) {
			poData.put("cntr_sgng_typ_ccd", "OFFLN_CNTR");
		} else {
			poData.put("cntr_sgng_typ_ccd", "ECNTR");
		}
		
		poData.put("cntr_uuid", contractInfo.get("cntr_uuid"));
		poData.put("cntr_no", contractInfo.get("cntr_no"));
		
		MapUtils.copyToDiffKeyValue(poData, Lists.newArrayList(
				"conv_cur_ccd",
				"po_amt",
				"po_conv_amt"
		), purcCntrData, Lists.newArrayList(
				"cur_ccd",
				"sup_amt",
				"sup_amt"
		));
		
		if(!purcCntrItemList.isEmpty()) {
			Map firstItem = purcCntrItemList.get(0);
			
			// 동일한 단가계약 건을 하나의 발주로 묶었으므로 품목의 단가 및 견적 정보는 하나만 존재
			MapUtils.copyToDiffKeyValue(poData, Lists.newArrayList(
					"rfx_no",
					"rfx_rnd"
			), firstItem, Lists.newArrayList(
					"rfx_no",
					"rfx_rnd"
			));
		}
		
		BigDecimal apymtRo = BigDecimal.ZERO;
		BigDecimal apymtAmt = BigDecimal.ZERO;
		BigDecimal ipymtRo = BigDecimal.ZERO;
		BigDecimal ipymtAmt = BigDecimal.ZERO;
		BigDecimal balRo = BigDecimal.ZERO;
		BigDecimal balAmt = BigDecimal.ZERO;
		for(Map purcCntrPymtExpt : purcCntrPymtExptList) {
			String pymtTypCcd = (String) purcCntrPymtExpt.get("pymt_typ_ccd");
			BigDecimal pymtRo = ConvertUtils.convertBigDecimal(purcCntrPymtExpt.get("pymt_ro"));
			BigDecimal pymtAmt = ConvertUtils.convertBigDecimal(purcCntrPymtExpt.get("pymt_amt"));
			
			if("APYMT".equals(pymtTypCcd)) {
				apymtRo = apymtRo.add(pymtRo);
				apymtAmt = apymtAmt.add(pymtAmt);
			} else if("IPYMT".equals(pymtTypCcd)) {
				ipymtRo = ipymtRo.add(pymtRo);
				ipymtAmt = ipymtAmt.add(pymtAmt);
			} else {
				balRo = balRo.add(pymtRo);
				balAmt = balAmt.add(pymtAmt);
			}
		}
		poData.put("apymt_ro", apymtRo);
		poData.put("apymt_amt", apymtAmt);
		poData.put("ipymt_ro", ipymtRo);
		poData.put("ipymt_amt", ipymtAmt);
		poData.put("bal_ro", balRo);
		poData.put("bal_amt", balAmt);
		
		if(purcCntrCstmList.isEmpty()) {
			poData.put("cstm_yn", "N");
		} else {
			Map firstCstmItem = purcCntrCstmList.get(0);
			
			poData.put("cstm_yn", "Y");
			poData.put("cstm_typ_ccd", firstCstmItem.get("cstm_typ_ccd"));
		}
		
		return poData;
	}
	
	private List<Map> makePoItemDataByPurcCntrByCntrChgReq(List<Map> poItemList, List<Map> purcCntrItemList) {
		Map purcCntrItemMap = Maps.newHashMap();
		for(Map purcCntrItem : purcCntrItemList) {
			purcCntrItemMap.put(purcCntrItem.get("item_lno"), purcCntrItem);
		}
		
		for(Map poItem : poItemList) {
			Map purcCntrItem = (Map) purcCntrItemMap.get(poItem.get("po_lno"));
			if(purcCntrItem == null) {
				continue;
			}
			MapUtils.copyToKeyValue(poItem, purcCntrItem, Lists.newArrayList(
					"df_yn",
					"req_dlvy_dt",
					"dlvy_plc",
					"dlvy_dt"
			));
			MapUtils.copyToDiffKeyValue(poItem, Lists.newArrayList(
					"po_qty",
					"po_uprc",
					"po_amt",
					"const_st_dt",
					"const_exp_dt",
					"cntr_uuid",
					"cntr_no",
					"cntr_revno",
					"purc_cntr_item_uuid"
			), purcCntrItem, Lists.newArrayList(
					"item_qty",
					"item_uprc",
					"item_amt",
					"cntr_st_dt",
					"cntr_exp_dt",
					"ref_cntr_uuid",
					"ref_cntr_no",
					"ref_cntr_revno",
					"ref_purc_cntr_item_uuid"
			));
		}
		
		return poItemList;
	}
	
	public ResultMap deletePoCntrReq(Map param) {
		poCntrReqRepository.deletePoCntrReq(param);
		proStatusService.updatePoByRequestChgContractDelete(param);
		purcContractCndService.deletePurcCntr(param);
		return ResultMap.SUCCESS();
	}
}
