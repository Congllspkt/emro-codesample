package smartsuite.app.bp.pro.po.validator;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.po.repository.PoReceiptRepository;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class PoReceiptValidator {
	
	@Inject
	PoReceiptRepository poReceiptRepository;
	
	public ResultMap receiptValidate(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("po_req_rcpt_uuid") == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map poReceiptItem = poReceiptRepository.findPoReceiptByUuid(param);
		if(poReceiptItem == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(!"RCPT_WTG".equals(poReceiptItem.get("rcpt_sts_ccd"))) {
			return ResultMap.builder().resultStatus(ResultMap.STATUS.INVALID_STATUS_ERR)
					.resultData(poReceiptItem)
					.build();
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap rejectValidate(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("po_req_rcpt_uuid") == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map poReceiptItem = poReceiptRepository.findPoReceiptByUuid(param);
		if(poReceiptItem == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(!("RCPT_WTG".equals(poReceiptItem.get("rcpt_sts_ccd")) ||
				"RCPT".equals(poReceiptItem.get("rcpt_sts_ccd")))) {
			return ResultMap.builder().resultStatus(ResultMap.STATUS.INVALID_STATUS_ERR)
					.resultData(poReceiptItem)
					.build();
		}
		if(!"WTG".equals(poReceiptItem.get("prgs_sts_ccd"))) {
			return ResultMap.builder().resultStatus(ResultMap.STATUS.INVALID_STATUS_ERR)
					.resultData(poReceiptItem)
					.build();
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap changePurcGrpValidate(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(param.get("po_req_rcpt_uuid") == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map poReceiptItem = poReceiptRepository.findPoReceiptByUuid(param);
		if(poReceiptItem == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(!("RCPT_WTG".equals(poReceiptItem.get("rcpt_sts_ccd")) ||
				"RCPT".equals(poReceiptItem.get("rcpt_sts_ccd")))) {
			return ResultMap.builder().resultStatus(ResultMap.STATUS.INVALID_STATUS_ERR)
					.resultData(poReceiptItem)
					.build();
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap ReceiptUprcItemValidate(Map param) {
		List<Map<String, Object>> checkValidYnList = poReceiptRepository.compareListPoReceiptUprcItemSts(param, param.get("po_req_item_rcpt_uuids"));
		
		return ValidatorUtil.getResultMapByCheckValidYnList(
				(List<String>) param.get("po_req_item_rcpt_uuids"),
				checkValidYnList,
				"po_req_item_rcpt_uuid",
				"PO_RCPT_ITEM_STS_ERR"
		);
	}
}
