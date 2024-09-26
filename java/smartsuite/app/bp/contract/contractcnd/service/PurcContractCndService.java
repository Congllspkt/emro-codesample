package smartsuite.app.bp.contract.contractcnd.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.contract.contractcnd.repository.PurcContractCndRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.util.ConvertUtils;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class PurcContractCndService implements ContractCndService {
	
	@Inject
	PurcContractCndRepository purcContractCndRepository;
	
	@Inject
	SharedService sharedService;
	
	@Override
	public Map find(Map param) {
		if(param == null) {
			return null;
		}
		if(param.get("purc_cntr_uuid") == null) {
			return null;
		}
		
		Map result = Maps.newHashMap();
		result.put("purcCntrData", purcContractCndRepository.findPurcCntr(param));
		result.put("purcCntrInfoData", purcContractCndRepository.findPurcCntrInfo(param));
		result.put("purcCntrItemList", purcContractCndRepository.findListPurcCntrItem(param));
		result.put("purcCntrPymtExptList", purcContractCndRepository.findListPurcCntrPymtExpt(param));
		result.put("purcCntrCstmList", purcContractCndRepository.findListCstm(param));
		return result;
	}
	
	@Override
	public String save(Map param) {
		ResultMap resultMap = this.savePurcCntr(param, false);
		if(resultMap.isSuccess()) {
			Map resultData = resultMap.getResultData();
			return (String) resultData.get("purc_cntr_uuid");
		}
		return null;
	}
	
	@Override
	public ResultMap delete(String cntrCndUuid) {
		Map param = Maps.newHashMap();
		param.put("purc_cntr_uuid", cntrCndUuid);
		return this.deletePurcCntr(param);
	}
	
	@Override
	public Map getTemplate(String cntrCndUuid) {
		Map param = Maps.newHashMap();
		param.put("purc_cntr_uuid", cntrCndUuid);
		return this.find(param);
	}
	
	@Override
	public String copy(String cntrCndUuid) {
		Map param = Maps.newHashMap();
		param.put("purc_cntr_uuid", cntrCndUuid);
		
		Map findMap = this.find(param);
		Map purcCntrData = (Map) findMap.get("purcCntrData");
		Map purcCntrInfoData = (Map) findMap.get("purcCntrInfoData");
		List<Map> purcCntrItemList = (List<Map>) findMap.get("purcCntrItemList");
		List<Map> purcCntrPymtExptList = (List<Map>) findMap.get("purcCntrPymtExptList");
		List<Map> purcCntrCstmList = (List<Map>) findMap.get("purcCntrCstmList");
		
		purcCntrData.put("purc_cntr_uuid", null);
		purcCntrInfoData.put("purc_cntr_info_uuid", null);
		purcCntrInfoData.put("purc_cntr_uuid", null);
		
		Map copyParam = Maps.newHashMap();
		copyParam.put("purcCntrData", purcCntrData);
		copyParam.put("purcCntrInfoData", purcCntrInfoData);
		copyParam.put("insertItems", purcCntrItemList);
		copyParam.put("paymentPlans", purcCntrPymtExptList);
		copyParam.put("insertCsList", purcCntrCstmList);
		
		ResultMap resultMap = this.savePurcCntr(copyParam, true);
		if(resultMap.isSuccess()) {
			Map resultData = resultMap.getResultData();
			return (String) resultData.get("purc_cntr_uuid");
		}
		return null;
	}

	@Override
	public Map copyData(String cntrCndUuid) {
		Map param = Maps.newHashMap();
		param.put("purc_cntr_uuid", cntrCndUuid);

		Map findMap = this.find(param);
		Map purcCntrData = (Map) findMap.get("purcCntrData");
		Map purcCntrInfoData = (Map) findMap.get("purcCntrInfoData");
		List<Map> purcCntrItemList = (List<Map>) findMap.get("purcCntrItemList");
		List<Map> purcCntrPymtExptList = (List<Map>) findMap.get("purcCntrPymtExptList");
		List<Map> purcCntrCstmList = (List<Map>) findMap.get("purcCntrCstmList");

		purcCntrData.put("purc_cntr_uuid", null);
		purcCntrInfoData.put("purc_cntr_info_uuid", null);
		purcCntrInfoData.put("purc_cntr_uuid", null);

		Map copyParam = Maps.newHashMap();
		copyParam.put("purcCntrData", purcCntrData);
		copyParam.put("purcCntrInfoData", purcCntrInfoData);
		copyParam.put("purcCntrItemList", purcCntrItemList);
		copyParam.put("purcCntrPymtExptList", purcCntrPymtExptList);
		copyParam.put("purcCntrCstmList", purcCntrCstmList);

		return copyParam;
	}

	public Map findPurcCntr(Map param) {
		if(param == null) {
			return null;
		}
		if(param.get("purc_cntr_uuid") == null) {
			return null;
		}
		
		return purcContractCndRepository.findPurcCntr(param);
	}
	
	/**
	 * 후속 프로세스 진행을 위한 요청 정보 생성
	 * 요청 정보 - 품목 items
	 * 요청 정보 - 계약 정보 cntrInfo
	 *
	 * @param param
	 * @return
	 */
	public ResultMap savePurcCntr(Map param, boolean copy) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		boolean isNew = false;
		Map purcCntrData = (Map) param.get("purcCntrData");
		Map purcCntrInfoData = (Map) param.get("purcCntrInfoData");
		List<Map> insertItems = (List<Map>) param.get("insertItems");
		List<Map> updateItems = (List<Map>) param.get("updateItems");
		List<Map> deleteItems = (List<Map>) param.get("deleteItems");
		List<Map> paymentPlans = (List<Map>) param.get("paymentPlans");
		List<Map> insertCsList = (List<Map>) param.get("insertCsList");
		
		if(purcCntrData == null) {
			purcCntrData = Maps.newHashMap();
		}
		if(purcCntrInfoData == null) {
			purcCntrInfoData = Maps.newHashMap();
		}
		String purcCntrUuid = (String) purcCntrData.get("purc_cntr_uuid");
		if(purcCntrUuid == null) {
			isNew = true;
			purcCntrUuid = UUID.randomUUID().toString();
			purcCntrData.put("purc_cntr_uuid", purcCntrUuid);
		}
		
		if(isNew) {
			purcContractCndRepository.insertPurcCntr(purcCntrData);
		} else {
			purcContractCndRepository.updatePurcCntr(purcCntrData);
		}
		
		this.deletePurcCntrItem(deleteItems);
		this.insertPurcCntrItem(insertItems, purcCntrUuid);
		this.updatePurcCntrItem(updateItems);
		
		if(isNew && !copy) {
			BigDecimal cntrAmt = BigDecimal.ZERO;
			BigDecimal supAmt = purcContractCndRepository.calcSupplyAmountByItem(purcCntrData);
			BigDecimal vatAmt = BigDecimal.ZERO;
			
			if(purcCntrInfoData.get("tax_typ_ccd") != null) {
				Map codeParam = Maps.newHashMap();
				codeParam.put("ccd", "C031");
				codeParam.put("dtlcd", purcCntrInfoData.get("tax_typ_ccd"));
				codeParam.put("cstr_cnd_cd", "TAXN_RATE");
				List codeAttributes = sharedService.findListCommonCodeAttributeCode(codeParam);
				if(codeAttributes.size() == 1) {
					Map codeAttribute = (Map) codeAttributes.get(0);
					BigDecimal cstrCndVal = ConvertUtils.convertBigDecimal(codeAttribute.get("cstr_cnd_val"));
					vatAmt = supAmt.multiply(cstrCndVal).divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_DOWN);
				}
			}
			cntrAmt = supAmt.add(vatAmt);
			purcCntrData.put("cntr_amt", cntrAmt.doubleValue());
			purcCntrData.put("sup_amt", supAmt.doubleValue());
			purcCntrData.put("vat_amt", vatAmt.doubleValue());
			purcContractCndRepository.updatePurcCntrAmt(purcCntrData);
			
			// 단가계약용이 아닌 경우 지불조건 필요
			if(purcCntrData.get("cnd_typ_ccd") == null || !"UPRCCNTR".equals(purcCntrData.get("cnd_typ_ccd"))) {
				if(paymentPlans == null) {
					paymentPlans = Lists.newArrayList();
					paymentPlans.add(this.defaultCntrPymtExpt(cntrAmt));
				}
				this.insertPurcCntrPymtExpt(paymentPlans, purcCntrUuid);
			}
		} else {
			this.deletePurcCntrPymtExptByPurcCntr(purcCntrData);
			this.insertPurcCntrPymtExpt(paymentPlans, purcCntrUuid);
		}
		
		// 계약 정보 저장
		if(purcCntrInfoData.get("purc_cntr_info_uuid") == null) {
			this.insertPurcCntrInfo(purcCntrData, purcCntrInfoData);
		} else {
			this.updatePurcCntrInfo(purcCntrInfoData);
		}
		
		this.insertCs(insertCsList, purcCntrUuid);
		
		return ResultMap.SUCCESS(purcCntrData);
	}
	
	private void insertCs(List<Map> insertCsList, String purcCntrUuid) {
		if(insertCsList == null) {
			return;
		}
		if(insertCsList.isEmpty()) {
			return;
		}
		for(Map insertCs : insertCsList) {
			this.insertCs(insertCs, purcCntrUuid);
		}
	}
	
	private void insertCs(Map insertCs, String purcCntrUuid) {
		if(insertCs == null) {
			return;
		}
		insertCs.put("purc_cntr_cstm_vd_uuid", UUID.randomUUID().toString());
		insertCs.put("purc_cntr_uuid", purcCntrUuid);
		purcContractCndRepository.insertCs(insertCs);
	}
	
	private void deletePurcCntrPymtExptByPurcCntr(Map purcCntrData) {
		if(purcCntrData == null) {
			return;
		}
		purcContractCndRepository.deletePurcCntrPymtExptByPurcCntr(purcCntrData);
	}
	
	private void insertPurcCntrPymtExpt(List<Map> insertPaymentPlans, String purcCntrUuid) {
		if(insertPaymentPlans == null) {
			return;
		}
		if(insertPaymentPlans.isEmpty()) {
			return;
		}
		int rev = 1;
		for(Map insertPaymentPlan : insertPaymentPlans) {
			insertPaymentPlan.put("purc_cntr_pymt_expt_uuid", UUID.randomUUID().toString());
			insertPaymentPlan.put("purc_cntr_uuid", purcCntrUuid);
			insertPaymentPlan.put("pymt_revno", rev++);
			this.insertPurcCntrPymtExpt(insertPaymentPlan);
		}
	}
	
	private void insertPurcCntrPymtExpt(Map insertPaymentPlan) {
		if(insertPaymentPlan == null) {
			return;
		}
		purcContractCndRepository.insertPurcCntrPymtExpt(insertPaymentPlan);
	}
	
	private void deletePurcCntrItem(List<Map> deleteItems) {
		if(deleteItems == null) {
			return;
		}
		if(deleteItems.isEmpty()) {
			return;
		}
		for(Map deleteItem : deleteItems) {
			this.deletePurcCntrItem(deleteItem);
		}
	}
	
	private void deletePurcCntrItem(Map deleteItem) {
		if(deleteItem == null) {
			return;
		}
		purcContractCndRepository.deletePurcCntrItem(deleteItem);
	}
	
	private void updatePurcCntrItem(List<Map> updateItems) {
		if(updateItems == null) {
			return;
		}
		if(updateItems.isEmpty()) {
			return;
		}
		for(Map updateItem : updateItems) {
			this.updatePurcCntrItem(updateItem);
		}
	}
	
	private void updatePurcCntrItem(Map updateItem) {
		if(updateItem == null) {
			return;
		}
		purcContractCndRepository.updatePurcCntrItem(updateItem);
	}
	
	private void insertPurcCntrItem(List<Map> insertItems, String purcCntrUuid) {
		if(insertItems == null) {
			return;
		}
		if(insertItems.isEmpty()) {
			return;
		}
		for(Map insertItem : insertItems) {
			insertItem.put("purc_cntr_item_uuid", UUID.randomUUID().toString());
			insertItem.put("purc_cntr_uuid", purcCntrUuid);
			this.insertPurcCntrItem(insertItem);
		}
	}
	
	private void insertPurcCntrItem(Map insertItem) {
		if(insertItem == null) {
			return;
		}
		purcContractCndRepository.insertPurcCntrItem(insertItem);
	}
	
	private Map defaultCntrPymtExpt(BigDecimal cntrAmt) {
		Map cntrPymtExpt = Maps.newHashMap();
		cntrPymtExpt.put("pymt_typ_ccd", "BAL");
		cntrPymtExpt.put("pymt_ro", 100);
		cntrPymtExpt.put("pymt_amt", cntrAmt.doubleValue());
		return cntrPymtExpt;
	}
	
	private void insertPurcCntrInfo(Map purcCntrData, Map purcCntrInfoData) {
		if(purcCntrInfoData == null) {
			return;
		}
		
		purcCntrInfoData.put("purc_cntr_info_uuid", UUID.randomUUID().toString());
		purcCntrInfoData.put("purc_cntr_uuid", purcCntrData.get("purc_cntr_uuid"));
		purcContractCndRepository.insertPurcCntrInfo(purcCntrInfoData);
	}
	
	private void updatePurcCntrInfo(Map purcCntrInfoData) {
		if(purcCntrInfoData == null) {
			return;
		}
		purcContractCndRepository.updatePurcCntrInfo(purcCntrInfoData);
	}
	
	public ResultMap deletePurcCntr(Map param) {
		if(!param.containsKey("purc_cntr_uuid")) {
			return ResultMap.SKIP();
		}
		purcContractCndRepository.deletePurcCntrPymtExptByPurcCntr(param);
		purcContractCndRepository.deletePurcCntrItemByPurcCntr(param);
		purcContractCndRepository.deletePurcCntrInfoByPurcCntr(param);
		purcContractCndRepository.deletePurcCntr(param);
		return ResultMap.SUCCESS();
	}
}
