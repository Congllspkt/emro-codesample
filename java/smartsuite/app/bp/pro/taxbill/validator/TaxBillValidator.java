package smartsuite.app.bp.pro.taxbill.validator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.inv.repository.InvoiceRepository;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.bp.pro.shared.validator.ProValidator;
import smartsuite.app.bp.pro.taxbill.repository.TaxBillRepository;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class TaxBillValidator extends ProValidator {
	
	@Inject
	private TaxBillRepository taxBillRepository;
	
	@Inject
	private InvoiceRepository invoiceRepository;
	
	@Override
	public ResultMap validate(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		
		List<String> taxbillUuids = (List<String>) (param.get("taxbill_uuids") == null ? Lists.newArrayList() : param.get("taxbill_uuids"));
		if(!taxbillUuids.isEmpty()) {
			return checkTaxBilldRequested(param);
		}
		
		List<String> invUuids = (List<String>) (param.get("inv_uuids") == null ? Lists.newArrayList() : param.get("inv_uuids"));
		if(!invUuids.isEmpty()) {
			resultMap = checkInvoicesCompleted(param);
			
			if(!ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
				return resultMap;
			}
		}
		
		String taxbillUuid = param.get("taxbill_uuid") == null ? null : param.get("taxbill_uuid").toString();
		if(Strings.isNullOrEmpty(taxbillUuid)) {
			// 신규 생성 시
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		} else {
			// UPDATE 시 : 화면에서 조회한 시점의 진행상태값과 현재 DB에서의 진행상태값을 비교
			Map<String, Object> checkResult = taxBillRepository.compareTaxbillStatus(param);
			resultMap = ValidatorUtil.getResultMapByCheckResult(param, checkResult);
		}
		
		return resultMap;
	}
	
	/**
	 * 송장 확정여부 확인
	 *
	 * @param param
	 * @return
	 */
	private ResultMap checkInvoicesCompleted(Map<String, Object> param) {
		param.put("availableSts", "CNFD");
		
		List<Map<String, Object>> checkValidYnList = invoiceRepository.compareInvoiceListStatus(param);
		
		return ValidatorUtil.getResultMapByCheckValidYnList(
				(List<String>) param.get("inv_uuids"),
				checkValidYnList,
				"inv_uuid",
				ProConst.IV_STATE_ERR);
	}
	
	/**
	 * 세금계산서 역발행요청 상태 확인
	 *
	 * @param param
	 * @return
	 */
	private ResultMap checkTaxBilldRequested(Map<String, Object> param) {
		param.put("availableSts", "REVISSUE_REQ");
		
		List<Map<String, Object>> checkValidYnList = taxBillRepository.compareTaxbillListStatus(param);
		
		return ValidatorUtil.getResultMapByCheckValidYnList(
				(List<String>) param.get("taxbill_uuids"),
				checkValidYnList,
				"taxbill_uuid",
				Const.INVALID_STATUS_ERR);
	}
}
