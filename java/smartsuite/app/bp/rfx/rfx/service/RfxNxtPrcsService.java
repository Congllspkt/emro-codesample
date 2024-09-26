package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.contract.cntrreq.service.ContractReqService;
import smartsuite.app.bp.contract.contractcnd.service.PurcContractCndService;
import smartsuite.app.bp.contract.data.ContractReq;
import smartsuite.app.bp.rfx.receipt.service.RfxReceiptService;
import smartsuite.app.bp.rfx.rfx.event.RfxNxtPrcsEventPublisher;
import smartsuite.app.common.message.MessageUtil;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.util.MapUtils;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.bp.rfx.rfx.repository.RfxNxtPrcsRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxNxtPrcsService {
	
	@Inject
	RfxBidService rfxBidService;
	
	@Inject
	RfxReceiptService rfxReceiptService;
	
	@Inject
	RfxNxtPrcsRepository rfxNxtPrcsRepository;
	
	@Inject
	RfxNxtPrcsEventPublisher rfxNxtPrcsEventPublisher;
	
	@Inject
	PurcContractCndService purcContractCndService;
	
	@Inject
	ContractReqService contractReqService;
	
	@Inject
	SharedService sharedService;
	
	@Inject
	FormatterProvider formatterProvider;
	
	@Inject
	MessageUtil messageUtil;
	
	public Map findDefaultNxtPrcsReqDataByReqItems(Map param) {
		if(param == null) {
			return null;
		}
		
		List<Map> receiptList = rfxReceiptService.findRfxDefaultDataByReqRcpts((List) param.get("rfx_req_rcpt_uuids"));
		if(receiptList.isEmpty()) {
			return null;
		}
		
		int maxNo = 10;
		for(Map receiptItem : (List<Map>) receiptList) {
			receiptItem.put("item_qty", receiptItem.get("req_qty"));
			receiptItem.put("item_uprc", receiptItem.get("req_uprc"));
			receiptItem.put("item_amt", receiptItem.get("req_amt"));
			receiptItem.put("item_lno", maxNo);
			maxNo += 10;
		}
		
		Map firstReceiptItem = (Map) receiptList.get(0);
		Map rfxNxtPrcsReqData = Maps.newHashMap();
		rfxNxtPrcsReqData.put("oorg_cd", firstReceiptItem.get("oorg_cd"));
		rfxNxtPrcsReqData.put("purc_typ_ccd", firstReceiptItem.get("purc_typ_ccd"));
		rfxNxtPrcsReqData.put("cur_ccd", firstReceiptItem.get("cur_ccd"));
		rfxNxtPrcsReqData.put("rfx_purp_ccd", firstReceiptItem.get("rfx_purp_ccd"));
		rfxNxtPrcsReqData.put("disp_purc_grp_nm", firstReceiptItem.get("purc_grp_nm"));
		rfxNxtPrcsReqData.put("purc_grp_cd", firstReceiptItem.get("purc_grp_cd"));
		rfxNxtPrcsReqData.put("cnfd_yn", "N");
		rfxNxtPrcsReqData.put("req_sts_ccd", null);
		
		if("UPRCCNTR_SGNG".equals(rfxNxtPrcsReqData.get("rfx_purp_ccd")) || "PSR".equals(rfxNxtPrcsReqData.get("rfx_purp_ccd"))) {
			rfxNxtPrcsReqData.put("nxt_prcs_ccd", "UPRCCNTR");
		} else {
			// SPOT구매 인 경우 계약 기본값
			rfxNxtPrcsReqData.put("nxt_prcs_ccd", "CNTR");
		}
		rfxNxtPrcsReqData.put("cnd_typ_ccd", rfxNxtPrcsReqData.get("nxt_prcs_ccd"));
		
		Map result = Maps.newHashMap();
		result.put("rfxNxtPrcsReqData", rfxNxtPrcsReqData);
		result.put("purcCntrData", Maps.newHashMap(rfxNxtPrcsReqData));
		result.put("purcCntrInfoData", Maps.newHashMap());
		result.put("purcCntrItemList", receiptList);
		return result;
	}
	
	public Map findRfxNxtPrcsReq(Map param) {
		if(param == null) {
			return null;
		}
		if(param.get("rfx_nxt_prcs_req_uuid") == null) {
			return null;
		}
		
		return rfxNxtPrcsRepository.findRfxNxtPrcsReq(param);
	}
	
	/**
	 * 후속 프로세스 진행을 위한 요청 정보 생성
	 * 요청 정보 - 품목 items
	 * 요청 정보 - 계약 정보 cntrInfo
	 *
	 * @param param
	 * @return
	 */
	public ResultMap createRfxNxtPrcsReq(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map rfxNxtPrcsReqData = (Map) param.get("rfxNxtPrcsReqData");
		Map purcCntrData = (Map) param.get("purcCntrData");
		Map purcCntrInfoData = (Map) param.get("purcCntrInfoData");
		List<Map> insertItems = (List<Map>) param.get("insertItems");
		List<Map> insertCsList = (List<Map>) param.get("insertCsList");
		
		String rfxNxtPrcsReqUuid = UUID.randomUUID().toString();
		String cndNo = sharedService.generateDocumentNumber("RXR");
		rfxNxtPrcsReqData.put("rfx_nxt_prcs_req_uuid", rfxNxtPrcsReqUuid);
		rfxNxtPrcsReqData.put("nxt_prcs_no", cndNo);
		
		if(rfxNxtPrcsReqData.get("cnfd_yn") == null) {
			rfxNxtPrcsReqData.put("cnfd_yn", "N");
		}
		
		if(purcCntrData == null) {
			purcCntrData = rfxNxtPrcsReqData;
		}
		purcCntrData.put("cnd_typ_ccd", rfxNxtPrcsReqData.get("nxt_prcs_ccd"));
		
		Map cntrParam = Maps.newHashMap();
		cntrParam.put("purcCntrData", purcCntrData);
		cntrParam.put("purcCntrInfoData", purcCntrInfoData);
		cntrParam.put("insertItems", insertItems);
		cntrParam.put("insertCsList", insertCsList);
		ResultMap purcContractResult = purcContractCndService.savePurcCntr(cntrParam, false);
		if(purcContractResult.isSuccess()) {
			Map resultData = purcContractResult.getResultData();
			rfxNxtPrcsReqData.put("purc_cntr_uuid", resultData.get("purc_cntr_uuid"));
		}
		
		rfxNxtPrcsRepository.insertRfxNxtPrcsReq(rfxNxtPrcsReqData);
		return ResultMap.SUCCESS(rfxNxtPrcsReqData);
	}
	
	public ResultMap saveRfxNxtPrcsReq(Map param) {
		Map rfxNxtPrcsReqData = (Map) param.get("rfxNxtPrcsReqData");
		
		ResultMap purcContractResult = purcContractCndService.savePurcCntr(param, false);
		if(purcContractResult.isSuccess()) {
			Map resultData = purcContractResult.getResultData();
			rfxNxtPrcsReqData.put("purc_cntr_uuid", resultData.get("purc_cntr_uuid"));
		}
		
		if(rfxNxtPrcsReqData.get("rfx_nxt_prcs_req_uuid") == null) {
			String rfxNxtPrcsReqUuid = UUID.randomUUID().toString();
			String cndNo = sharedService.generateDocumentNumber("RXR");
			rfxNxtPrcsReqData.put("rfx_nxt_prcs_req_uuid", rfxNxtPrcsReqUuid);
			rfxNxtPrcsReqData.put("nxt_prcs_no", cndNo);
			
			if(rfxNxtPrcsReqData.get("cnfd_yn") == null) {
				rfxNxtPrcsReqData.put("cnfd_yn", "N");
			}
			rfxNxtPrcsRepository.insertRfxNxtPrcsReq(rfxNxtPrcsReqData);
		} else {
			rfxNxtPrcsRepository.updateRfxNxtPrcsReq(rfxNxtPrcsReqData);
		}
		
		return ResultMap.SUCCESS(rfxNxtPrcsReqData);
	}
	
	public ResultMap confirmRfxNxtPrcsReq(Map param) {
		Map rfxNxtPrcsReqData = (Map) param.get("rfxNxtPrcsReqData");
		
		ResultMap purcContractResult = purcContractCndService.savePurcCntr(param, false);
		if(purcContractResult.isSuccess()) {
			Map resultData = purcContractResult.getResultData();
			rfxNxtPrcsReqData.put("purc_cntr_uuid", resultData.get("purc_cntr_uuid"));
		}
		
		rfxNxtPrcsReqData.put("cnfd_yn", "Y");
		rfxNxtPrcsRepository.updateRfxNxtPrcsReq(rfxNxtPrcsReqData);
		return ResultMap.SUCCESS(param);
	}
	
	public ResultMap cancelConfirmRfxNxtPrcsReq(Map param) {
		rfxNxtPrcsRepository.cancelConfirmRfxNxtPrcsReq(param);
		return ResultMap.SUCCESS(param);
	}
	
	public ResultMap deleteRfxNxtPrcsReq(Map param) {
		rfxNxtPrcsRepository.updateRfxSlctnVdByDeleteRfxNxtPrcsReq(param);
		rfxNxtPrcsRepository.updateRfxReceiptByDeleteRfxNxtPrcsReq(param);
		rfxNxtPrcsRepository.deleteRfxNxtPrcsReq(param);
		purcContractCndService.deletePurcCntr(param);
		return ResultMap.SUCCESS();
	}
	
	public List<ResultMap> requestNxtPrcs(Map param) {
		List<Map> checkedRows = (List<Map>) param.get("checkedRows");
		
		List<ResultMap> resultList = Lists.newArrayList();
		for(Map checkedRow : checkedRows) {
			Map rfxNxtPrcsReqData = this.findRfxNxtPrcsReq(checkedRow);
			Map rfxData = rfxNxtPrcsRepository.findRfxByRfxUuid(checkedRow);
			
			ResultMap resultMap;
			String reqStsCcd = null;
			if("PO".equals(rfxNxtPrcsReqData.get("nxt_prcs_ccd"))) {
				reqStsCcd = "PO_PRGSG";
				resultMap = this.requestPo(rfxData, rfxNxtPrcsReqData);
			} else {
				reqStsCcd = "REQ";
				resultMap = this.requestContract(rfxData, rfxNxtPrcsReqData);
			}
			
			if(resultMap.isSuccess()) {
				this.updateRequestStatus(
						(String) rfxNxtPrcsReqData.get("rfx_nxt_prcs_req_uuid"),
						reqStsCcd,
						null
				);
				List<Map> items = rfxNxtPrcsRepository.findRfxItemByRfxNxtPrcsReq(rfxNxtPrcsReqData);
				for(Map item : items) {
					rfxReceiptService.updateProgressStatus((String) item.get("rfx_req_rcpt_uuid"), "CMPLD");
				}
			}
		}
		
		return resultList;
	}
	
	public List<ResultMap> requestNxtPrcsByRfxRcpt(Map param, boolean touchless) {
		List<Map> checkedRows = (List<Map>) param.get("checkedRows");
		
		List<ResultMap> resultList = Lists.newArrayList();
		for(Map checkedRow : checkedRows) {
			Map rfxNxtPrcsReqData = this.findRfxNxtPrcsReq(checkedRow);
			Map rfxReceiptData = rfxNxtPrcsRepository.findRfxReceiptByRfxNxtPrcsReq(checkedRow);
			List<Map> rfxReceiptItemCntrList = rfxNxtPrcsRepository.findListRfxReceiptItemCntrByRfxNxtPrcsReq(checkedRow);
			
			ResultMap resultMap;
			String reqStsCcd = null;
			if("PO".equals(rfxNxtPrcsReqData.get("nxt_prcs_ccd"))) {
				reqStsCcd = "PO_PRGSG";
				resultMap = this.requestPoByRfxRcpt(rfxReceiptData, rfxNxtPrcsReqData, rfxReceiptItemCntrList, touchless);
			} else {
				reqStsCcd = "REQ";
				resultMap = this.requestContract(rfxReceiptData, rfxNxtPrcsReqData);
			}
			
			if(resultMap.isSuccess()) {
				this.updateRequestStatus(
						(String) rfxNxtPrcsReqData.get("rfx_nxt_prcs_req_uuid"),
						reqStsCcd,
						null
				);
				List<Map> items = rfxNxtPrcsRepository.findListRfxReceiptByRfxNxtPrcsReq(rfxNxtPrcsReqData);
				//items.addAll(rfxNxtPrcsRepository.findListRfxReceiptQtaByRfxNxtPrcsReq(rfxNxtPrcsReqData));
				for(Map item : items) {
					rfxReceiptService.updateProgressStatus((String) item.get("rfx_req_rcpt_uuid"), "CMPLD");
				}
			}
		}
		
		return resultList;
	}
	
	public void updateRequestStatus(String rfxNxtPrcsReqUuid, String reqStsCcd, String retRsn) {
		Map param = Maps.newHashMap();
		param.put("rfx_nxt_prcs_req_uuid", rfxNxtPrcsReqUuid);
		param.put("req_sts_ccd", reqStsCcd);
		param.put("ret_rsn", retRsn);
		rfxNxtPrcsRepository.updateRequestStatus(param);
	}
	
	/**
	 * 발주 계약 / 단가 계약 요청
	 *
	 * @param rfxData RFX 데이터
	 * @param rfxNxtPrcsReqData RFX 후속 프로세스 데이터
	 */
	private ResultMap requestContract(Map rfxData, Map rfxNxtPrcsReqData) {
		Map purcCntr = purcContractCndService.find(rfxNxtPrcsReqData);
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		List<Map> purcCntrItemList = (List<Map>) purcCntr.get("purcCntrItemList");
		
		String title = "";
		String itemNm = (String) purcCntrItemList.get(0).get("disp_item_nm");
		
		if(purcCntrItemList.size() == 1) {
			title = itemNm;
		} else {
			title = messageUtil.getCodeMessage("STD.N3100", Lists.newArrayList(itemNm, purcCntrItemList.size()-1), null);
		}
		
		String cntrType;
		if("UPRCCNTR".equals(rfxNxtPrcsReqData.get("nxt_prcs_ccd"))) {
			cntrType = CntrConst.CNTRDOC_TYPE.UNIT_PRICE;
		} else {
			cntrType = CntrConst.CNTRDOC_TYPE.PO;
		}
		
		ContractReq contractReq = ContractReq.RFX(
				cntrType,
				(String) rfxNxtPrcsReqData.get("rfx_nxt_prcs_req_uuid"),
				(String) rfxNxtPrcsReqData.get("purc_cntr_uuid"),
				title,
				Auth.getCurrentUserName(),
				(String) purcCntrData.get("vd_cd"),
				(String) rfxNxtPrcsReqData.get("oorg_cd"),
				(String) rfxNxtPrcsReqData.get("purc_grp_cd")
		);
		return contractReqService.requestContract(contractReq);
	}
	
	/**
	 * 계약 없이 발주 요청
	 *
	 * @param rfxNxtPrcsReqData RFX 후속 프로세스 데이터
	 */
	private ResultMap requestPo(Map rfxData, Map rfxNxtPrcsReqData) {
		Map purcCntr = purcContractCndService.find(rfxNxtPrcsReqData);
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		List<Map> purcCntrItemList = (List<Map>) purcCntr.get("purcCntrItemList");
		
		String title = "";
		String itemNm = (String) purcCntrItemList.get(0).get("disp_item_nm");
		
		if(purcCntrItemList.size() == 1) {
			title = itemNm;
		} else {
			title = messageUtil.getCodeMessage("STD.N3100", Lists.newArrayList(itemNm, purcCntrItemList.size()-1), null);
		}
		
		Map eventParam = Maps.newHashMap();
		MapUtils.copyToDiffKeyValue(eventParam, Lists.newArrayList(
				"oorg_cd",
				"purc_typ_ccd",
				"req_pic_id"
		), rfxData, Lists.newArrayList(
				"oorg_cd",
				"purc_typ_ccd",
				"purc_pic_id"
		));
		eventParam.put("req_tit", title);
		eventParam.put("purc_grp_cd", rfxNxtPrcsReqData.get("purc_grp_cd"));
		eventParam.put("req_doc_typ_ccd", "RFX");
		eventParam.put("po_typ_ccd", "SPOTPURC");
		eventParam.put("po_chg_typ_ccd", "NEW");
		eventParam.put("po_req_uuid", rfxNxtPrcsReqData.get("rfx_nxt_prcs_req_uuid"));
		eventParam.put("po_cnd_uuid", rfxNxtPrcsReqData.get("purc_cntr_uuid"));
		eventParam.put("vd_cd", purcCntrData.get("vd_cd"));
		
		rfxNxtPrcsEventPublisher.requestPo(eventParam);
		return ResultMap.SUCCESS();
		/*Map purcCntr = purcContractCndService.find(rfxNxtPrcsReqData);
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		Map purcCntrInfoData = (Map) purcCntr.get("purcCntrInfoData");
		List<Map> purcCntrItemList = (List<Map>) purcCntr.get("purcCntrItemList");
		List<Map> purcCntrPymtExptList = (List<Map>) purcCntr.get("purcCntrPymtExptList");
		List<Map> purcCntrCstmList = (List<Map>) purcCntr.get("purcCntrCstmList");
		
		Map poData = Maps.newHashMap(purcCntrInfoData);
		poData.put("oorg_cd", purcCntrData.get("oorg_cd"));
		poData.put("purc_typ_ccd", purcCntrData.get("purc_typ_ccd"));
		poData.put("purc_grp_cd", rfxNxtPrcsReqData.get("purc_grp_cd"));
		poData.put("cur_ccd", purcCntrData.get("cur_ccd"));
		poData.put("conv_cur_ccd", rfxData.get("cur_ccd"));
		poData.put("vd_cd", purcCntrData.get("vd_cd"));
		poData.put("erp_vd_cd", purcCntrData.get("erp_vd_cd"));
		poData.put("po_req_typ_ccd", "RFX");
		poData.put("po_tit", rfxData.get("title"));
		poData.put("domovrs_div_ccd", purcCntrData.get("domovrs_div_ccd"));
		poData.put("rfx_uuid", rfxData.get("rfx_uuid"));
		poData.put("rfx_no", rfxData.get("rfx_no"));
		poData.put("rfx_rnd", rfxData.get("rfx_rnd"));
		
		poData.put("po_amt", purcCntrData.get("sup_amt"));
		
		Map firstCstmInfo = null;
		if(purcCntrCstmList.isEmpty()) {
			poData.put("cstm_yn", "N");
		} else {
			firstCstmInfo = purcCntrCstmList.get(0);
			poData.put("cstm_yn", "Y");
			poData.put("cstm_typ_ccd", firstCstmInfo.get("cstm_typ_ccd"));
			
			// 컨소시엄 구성
			if("IRBC".equals(firstCstmInfo.get("cstm_typ_ccd"))) {
				BigDecimal poTotAmt = (BigDecimal) poData.get("po_amt");
				String cur_ccd = (String) poData.get("cur_ccd");
				for(Map csData : (List<Map>) purcCntrCstmList) {
					BigDecimal rate = (BigDecimal) csData.get("invt_ro");
					csData.put("amt", formatterProvider.getPrecFormat("amt", poTotAmt.multiply(rate).divide(new BigDecimal(100)), cur_ccd));
				}
			}
		}
		
		for(Map purcCntrItem : purcCntrItemList) {
			purcCntrItem.put("purc_typ_ccd", purcCntrData.get("purc_typ_ccd"));
			purcCntrItem.put("purc_grp_cd", rfxNxtPrcsReqData.get("purc_grp_cd"));
			purcCntrItem.put("po_lno", purcCntrItem.get("item_lno"));
			purcCntrItem.put("po_qty", purcCntrItem.get("item_qty"));
			purcCntrItem.put("po_uprc", purcCntrItem.get("item_uprc"));
			purcCntrItem.put("po_amt", purcCntrItem.get("item_amt"));
		}
		
		// 발주 생성을 위한 데이터
		Map poParam = Maps.newHashMap();
		poParam.put("poData", poData);
		poParam.put("insertItems", purcCntrItemList);
		poParam.put("paymentPlans", purcCntrPymtExptList);
		poParam.put("insertPoCss", purcCntrCstmList);
		
		rfxNxtPrcsEventPublisher.createPo(poParam);
		*/
	}
	
	/**
	 * RFX 접수에서 바로 발주 요청
	 *
	 * @param rfxNxtPrcsReqData RFX 후속 프로세스 데이터
	 */
	private ResultMap requestPoByRfxRcpt(Map rfxReceiptData, Map rfxNxtPrcsReqData, List<Map> rfxReceiptItemCntrList, boolean touchless) {
		Map purcCntr = purcContractCndService.find(rfxNxtPrcsReqData);
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		
		Map eventParam = Maps.newHashMap();
		MapUtils.copyToKeyValue(eventParam, purcCntrData, Lists.newArrayList(
				"oorg_cd",
				"purc_typ_ccd"
		));
		eventParam.put("req_tit", rfxReceiptData.get("title"));
		eventParam.put("req_pic_id", rfxReceiptData.get("purc_pic_id"));
		eventParam.put("purc_grp_cd", rfxNxtPrcsReqData.get("purc_grp_cd"));
		eventParam.put("req_doc_typ_ccd", "RFX");
		/*String poCrnTypCcd;
		if(touchless) {
			poCrnTypCcd = "AUTOPO";
		} else if("UPRCCNTR_PURC".equals(rfxReceiptData.get("req_purp_ccd"))) {
			poCrnTypCcd = "UPRCCNTR";
		} else {
			poCrnTypCcd = "DIRECTPO";
		}*/
		eventParam.put("po_typ_ccd", "URGPURC");
		eventParam.put("po_chg_typ_ccd", "NEW");
		eventParam.put("po_req_uuid", rfxNxtPrcsReqData.get("rfx_nxt_prcs_req_uuid"));
		eventParam.put("po_cnd_uuid", rfxNxtPrcsReqData.get("purc_cntr_uuid"));
		eventParam.put("vd_cd", purcCntrData.get("vd_cd"));
		
		rfxNxtPrcsEventPublisher.requestPo(eventParam);
		return ResultMap.SUCCESS();
		
		/*Map purcCntr = purcContractCndService.find(rfxNxtPrcsReqData);
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		Map purcCntrInfoData = (Map) purcCntr.get("purcCntrInfoData");
		List<Map> purcCntrItemList = (List<Map>) purcCntr.get("purcCntrItemList");
		List<Map> purcCntrPymtExptList = (List<Map>) purcCntr.get("purcCntrPymtExptList");
		List<Map> purcCntrCstmList = (List<Map>) purcCntr.get("purcCntrCstmList");
		
		Map poData = Maps.newHashMap(purcCntrInfoData);
		poData.put("oorg_cd", purcCntrData.get("oorg_cd"));
		poData.put("purc_typ_ccd", purcCntrData.get("purc_typ_ccd"));
		poData.put("purc_grp_cd", rfxNxtPrcsReqData.get("purc_grp_cd"));
		poData.put("cur_ccd", purcCntrData.get("cur_ccd"));
		poData.put("conv_cur_ccd", purcCntrData.get("cur_ccd"));
		poData.put("vd_cd", purcCntrData.get("vd_cd"));
		poData.put("erp_vd_cd", purcCntrData.get("erp_vd_cd"));
		poData.put("po_req_typ_ccd", "PR");
		poData.put("po_tit", rfxReceiptData.get("title"));
		poData.put("domovrs_div_ccd", purcCntrData.get("domovrs_div_ccd"));
		poData.put("rfx_uuid", rfxReceiptData.get("rfx_uuid"));
		poData.put("rfx_no", rfxReceiptData.get("rfx_no"));
		poData.put("rfx_rnd", rfxReceiptData.get("rfx_rnd"));
		
		poData.put("po_amt", purcCntrData.get("sup_amt"));
		
		Map firstCstmInfo = null;
		if(purcCntrCstmList.isEmpty()) {
			poData.put("cstm_yn", "N");
		} else {
			firstCstmInfo = purcCntrCstmList.get(0);
			poData.put("cstm_yn", "Y");
			poData.put("cstm_typ_ccd", firstCstmInfo.get("cstm_typ_ccd"));
			
			// 컨소시엄 구성
			if("IRBC".equals(firstCstmInfo.get("cstm_typ_ccd"))) {
				BigDecimal poTotAmt = (BigDecimal) poData.get("po_amt");
				String cur_ccd = (String) poData.get("cur_ccd");
				for(Map csData : (List<Map>) purcCntrCstmList) {
					BigDecimal rate = (BigDecimal) csData.get("invt_ro");
					csData.put("amt", formatterProvider.getPrecFormat("amt", poTotAmt.multiply(rate).divide(new BigDecimal(100)), cur_ccd));
				}
			}
		}
		
		Map<String, Map> rfxReceiptItemMap = Maps.newHashMap();
		for(Map rfxReceiptItem : rfxReceiptItemCntrList) {
			rfxReceiptItemMap.put((String) rfxReceiptItem.get("pr_item_uuid"), rfxReceiptItem);
		}
		for(Map purcCntrItem : purcCntrItemList) {
			purcCntrItem.put("purc_typ_ccd", purcCntrData.get("purc_typ_ccd"));
			purcCntrItem.put("purc_grp_cd", rfxNxtPrcsReqData.get("purc_grp_cd"));
			purcCntrItem.put("po_lno", purcCntrItem.get("item_lno"));
			purcCntrItem.put("po_qty", purcCntrItem.get("item_qty"));
			purcCntrItem.put("po_uprc", purcCntrItem.get("item_uprc"));
			purcCntrItem.put("po_amt", purcCntrItem.get("item_amt"));
			
			Map rfxReceiptItem = rfxReceiptItemMap.get(purcCntrItem.get("pr_item_uuid"));
			if(rfxReceiptItem != null) {
				purcCntrItem.put("cntr_uuid", rfxReceiptItem.get("cntr_uuid"));
				purcCntrItem.put("cntr_no", rfxReceiptItem.get("cntr_no"));
				purcCntrItem.put("cntr_revno", rfxReceiptItem.get("cntr_revno"));
				purcCntrItem.put("purc_cntr_item_uuid", rfxReceiptItem.get("purc_cntr_item_uuid"));
			}
		}
		
		// 발주 생성을 위한 데이터
		Map poParam = Maps.newHashMap();
		poParam.put("poData", poData);
		poParam.put("insertItems", purcCntrItemList);
		poParam.put("paymentPlans", purcCntrPymtExptList);
		poParam.put("insertPoCss", purcCntrCstmList);
		
		if(touchless) {
			rfxNxtPrcsEventPublisher.createPoPr(poParam);
		} else {
			rfxNxtPrcsEventPublisher.createDraftPoPr(poParam);
		}
		return ResultMap.SUCCESS();*/
	}
	
	public ResultMap receiptReqRfx(Map data) {
		if(data == null) {
			return ResultMap.NOT_EXISTS("data not exists");
		}
		if(data.get("req_uuid") == null) {
			return ResultMap.NOT_EXISTS("req_uuid not exists");
		}
		data.put("rfx_nxt_prcs_req_uuid", data.get("req_uuid"));
		Map rfxNxtPrcsReqData = this.findRfxNxtPrcsReq(data);
		this.updateRequestStatus(
				(String) data.get("rfx_nxt_prcs_req_uuid"),
				rfxNxtPrcsReqData.get("nxt_prcs_ccd") + "_PRGSG",
				null
		);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap rejectReqRfx(Map data) {
		if(data == null) {
			return ResultMap.NOT_EXISTS("data not exists");
		}
		if(data.get("req_uuid") == null) {
			return ResultMap.NOT_EXISTS("req_uuid not exists");
		}
		if(data.get("ret_rsn") == null) {
			return ResultMap.NOT_EXISTS("ret_rsn not exists");
		}
		this.updateRequestStatus(
				(String) data.get("req_uuid"),
				"RET",
				(String) data.get("ret_rsn")
		);
		
		Map param = Maps.newHashMap();
		param.put("rfx_nxt_prcs_req_uuid", data.get("req_uuid"));
		
		Map rfxNxtPrcsReqData = this.findRfxNxtPrcsReq(param);
		List<Map> rfxReqRcptList = rfxNxtPrcsRepository.findListRfxReceiptByRfxNxtPrcsReq(param);
		for(Map rfxReqRcpt : rfxReqRcptList) {
			rfxReceiptService.updateProgressStatus((String) rfxReqRcpt.get("rfx_req_rcpt_uuid"), "NXT_REQ_RET");
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap completeReqRfx(Map data) {
		if(data == null) {
			return ResultMap.NOT_EXISTS("data not exists");
		}
		if(data.get("req_uuid") == null) {
			return ResultMap.NOT_EXISTS("req_uuid not exists");
		}
		this.updateRequestStatus(
				(String) data.get("req_uuid"),
				"CMPLD",
				null
		);
		return ResultMap.SUCCESS();
	}
}
