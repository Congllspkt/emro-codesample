package smartsuite.app.bp.contract.contractcnd.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.contract.contractcnd.repository.OtrsContractCndRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class OtrsContractCndService implements ContractCndService {
	
	@Inject
	OtrsContractCndRepository otrsContractCndRepository;
	@Inject
	SharedService sharedService;

	@Override
	public Map find(Map param) {
		if(param == null) {
			return null;
		}
		if(param.get("otrs_cntr_uuid") == null) {
			return null;
		}

		Map result = Maps.newHashMap();
		result.put("otrsCntrData", otrsContractCndRepository.findOtrsCntr(param));
		result.put("otrsCntrInfoData", otrsContractCndRepository.findOtrsCntrInfo(param));
		result.put("otrsCntrPymtExptList", otrsContractCndRepository.findListOtrsCntrPymtExpt(param));
		return result;
	}

	@Override
	public String save(Map param) {
		ResultMap resultMap = this.saveOtrsCntr(param);
		if(resultMap.isSuccess()) {
			Map resultData = resultMap.getResultData();
			return (String) resultData.get("otrs_cntr_uuid");
		}
		return null;
	}

	@Override
	public ResultMap delete(String cntrCndUuid) {
		Map param = Maps.newHashMap();
		param.put("otrs_cntr_uuid", cntrCndUuid);
		return this.deleteOtrsCntr(param);
	}

	@Override
	public Map getTemplate(String cntrCndUuid) {
		Map param = Maps.newHashMap();
		param.put("otrs_cntr_uuid", cntrCndUuid);
		return this.find(param);
	}

	@Override
	public String copy(String cntrCndUuid) {
		Map param = Maps.newHashMap();
		param.put("otrs_cntr_uuid", cntrCndUuid);

		Map findMap = this.find(param);
		Map otrsCntrData = (Map) findMap.get("otrsCntrData");
		Map otrsCntrInfoData = (Map) findMap.get("otrsCntrInfoData");
		List<Map> otrsCntrPymtExptList = (List<Map>) findMap.get("otrsCntrPymtExptList");

		otrsCntrData.put("otrs_cntr_uuid", null);
		otrsCntrInfoData.put("otrs_cntr_info_uuid", null);
		otrsCntrInfoData.put("otrs_cntr_uuid", null);

		Map copyParam = Maps.newHashMap();
		copyParam.put("otrsCntrData", otrsCntrData);
		copyParam.put("otrsCntrInfoData", otrsCntrInfoData);
		copyParam.put("paymentPlans", otrsCntrPymtExptList);

		ResultMap resultMap = this.saveOtrsCntr(copyParam);
		if(resultMap.isSuccess()) {
			Map resultData = resultMap.getResultData();
			return (String) resultData.get("otrs_cntr_uuid");
		}
		return null;
	}

	@Override
	public Map copyData(String cntrCndUuid) {
		Map param = Maps.newHashMap();
		param.put("otrs_cntr_uuid", cntrCndUuid);

		Map findMap = this.find(param);
		Map otrsCntrData = (Map) findMap.get("otrsCntrData");
		Map otrsCntrInfoData = (Map) findMap.get("otrsCntrInfoData");
		List<Map> otrsCntrPymtExptList = (List<Map>) findMap.get("otrsCntrPymtExptList");

		otrsCntrData.put("otrs_cntr_uuid", null);
		otrsCntrInfoData.put("otrs_cntr_info_uuid", null);
		otrsCntrInfoData.put("otrs_cntr_uuid", null);

		Map copyParam = Maps.newHashMap();
		copyParam.put("otrsCntrData", otrsCntrData);
		copyParam.put("otrsCntrInfoData", otrsCntrInfoData);
		copyParam.put("otrsCntrPymtExptList", otrsCntrPymtExptList);

		return copyParam;
	}


	private ResultMap saveOtrsCntr(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}

		boolean isNew = false;
		Map otrsCntrData = (Map) param.get("otrsCntrData");
		Map otrsCntrInfoData = (Map) param.get("otrsCntrInfoData");
		List<Map> paymentPlans = (List<Map>) param.get("paymentPlans");

		if(otrsCntrData == null) {
			otrsCntrData = Maps.newHashMap();
		}
		if(otrsCntrInfoData == null) {
			otrsCntrInfoData = Maps.newHashMap();
		}
		String otrsCntrUuid = (String) otrsCntrData.get("otrs_cntr_uuid");
		if(otrsCntrUuid == null) {
			isNew = true;
			otrsCntrUuid = UUID.randomUUID().toString();
			otrsCntrData.put("otrs_cntr_uuid", otrsCntrUuid);
		}

		if(isNew) {
			otrsContractCndRepository.insertOtrsCntr(otrsCntrData);
		} else {
			otrsContractCndRepository.updateOtrsCntr(otrsCntrData);
		}

		this.deleteOtrsCntrPymtExptByOtrsCntr(otrsCntrData);
		this.insertOtrsCntrPymtExpt(paymentPlans, otrsCntrUuid);

		if(otrsCntrInfoData.get("otrs_cntr_info_uuid") == null) {
			this.insertOtrsCntrInfo(otrsCntrData, otrsCntrInfoData);
		} else {
			this.updateOtrsCntrInfo(otrsCntrInfoData);
		}

		return ResultMap.SUCCESS(otrsCntrData);
	}

	private void deleteOtrsCntrPymtExptByOtrsCntr(Map otrsCntrData) {
		if(otrsCntrData == null) {
			return;
		}
		otrsContractCndRepository.deleteOtrsCntrPymtExptByOtrsCntr(otrsCntrData);
	}

	private void insertOtrsCntrPymtExpt(List<Map> insertPaymentPlans, String otrsCntrUuid) {
		if(insertPaymentPlans == null || otrsCntrUuid == null) {
			return;
		}
		int rev = 1;
		for(Map insertPaymentPlan : insertPaymentPlans) {
			insertPaymentPlan.put("otrs_cntr_pymt_info_uuid", UUID.randomUUID().toString());
			insertPaymentPlan.put("otrs_cntr_uuid", otrsCntrUuid);
			insertPaymentPlan.put("pymt_revno", rev++);
			otrsContractCndRepository.insertOtrsCntrPymtExpt(insertPaymentPlan);
		}
	}

	private void insertOtrsCntrInfo(Map otrsCntrData, Map otrsCntrInfoData) {
		if(otrsCntrData == null || otrsCntrInfoData == null) {
			return;
		}
		otrsCntrInfoData.put("otrs_cntr_info_uuid", UUID.randomUUID().toString());
		otrsCntrInfoData.put("otrs_cntr_uuid", otrsCntrData.get("otrs_cntr_uuid"));
		otrsContractCndRepository.insertOtrsCntrInfo(otrsCntrInfoData);
	}

	private void updateOtrsCntrInfo(Map otrsCntrInfoData) {
		if(otrsCntrInfoData == null) {
			return;
		}
		otrsContractCndRepository.updateOtrsCntrInfo(otrsCntrInfoData);
	}

	public ResultMap deleteOtrsCntr(Map param) {
		if(!param.containsKey("otrs_cntr_uuid")) {
			return ResultMap.SKIP();
		}
		otrsContractCndRepository.deleteOtrsCntrPymtExptByOtrsCntr(param);
		otrsContractCndRepository.deleteOtrsCntrInfoByOtrsCntr(param);
		otrsContractCndRepository.deleteOtrsCntr(param);
		return ResultMap.SUCCESS();
	}
	
}
