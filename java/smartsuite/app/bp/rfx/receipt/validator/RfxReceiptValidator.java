package smartsuite.app.bp.rfx.receipt.validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.rfx.receipt.repository.RfxReceiptRepository;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class RfxReceiptValidator {
	
	@Inject
	RfxReceiptRepository rfxReceiptRepository;
	
	public ResultMap validate(Map param) {
		List<Map<String, Object>> checkValidYnList = rfxReceiptRepository.compareListRfxReceiptSts(param, param.get("rfx_req_rcpt_uuids"));
		
		return ValidatorUtil.getResultMapByCheckValidYnList(
				(List<String>) param.get("rfx_req_rcpt_uuids"),
				checkValidYnList,
				"rfx_req_rcpt_uuid",
				"RFX_RCPT_ITEM_STS_ERR"
		);
	}
}
