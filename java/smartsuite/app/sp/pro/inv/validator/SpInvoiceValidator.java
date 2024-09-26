package smartsuite.app.sp.pro.inv.validator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.bp.pro.shared.validator.ProValidator;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.pro.inv.repository.SpInvoiceRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class SpInvoiceValidator extends ProValidator {
	
	@Inject
	private SpInvoiceRepository invoiceRepository;
	
	@Override
	public ResultMap validate(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		
		List<String> invoiceUuids = (List<String>) (param.get("inv_uuids") == null ? Lists.newArrayList() : param.get("inv_uuids"));

		if(!invoiceUuids.isEmpty()) {
			return checkInvoicesCompleted(param);
		}

		List<String> grItemUuids = (List<String>) (param.get("gr_item_uuids") == null ? Lists.newArrayList() : param.get("gr_item_uuids"));
		if(!grItemUuids.isEmpty()) {
			// gr item 완료여부 확인
			resultMap = checkGrItemCompleted(param);
			if(!ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
				return resultMap;
			}
		}

		String invoiceUuId = (String) param.get("inv_uuid");
		if(Strings.isNullOrEmpty(invoiceUuId)) {
			// 협락사, 구매사 동시에 신규 생성할 경우 체크로직 필요
			// invoice 생성 확인
			boolean isInvCreated = isAlreadyInvoiceCreated(param);
			if(isInvCreated){
				resultMap.setResultStatus(ResultMap.STATUS.INVALID_STATUS_ERR);
				return resultMap;
			}

			// 신규 생성 시
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
			return resultMap;
		}
		// UPDATE 시 : 화면에서 조회한 시점의 검수 진행상태값과 현재 DB에서의 진행상태값을 비교
		Map<String, Object> checkResult = invoiceRepository.compareInvoiceStatus(param);
		resultMap = ValidatorUtil.getResultMapByCheckResult(param, checkResult);

		return resultMap;
	}
	
	/**
	 * GR Item (검수/기성) 완료여부 확인
	 *
	 * @param param
	 * @return
	 */
	private ResultMap checkGrItemCompleted(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		List<Map<String, Object>> invalidGrItems = Lists.newArrayList();
		
		if(param.get("gr_item_uuids") != null) {
			invalidGrItems = invoiceRepository.checkCompletedGrItem(param);
			
		}
		if(invalidGrItems.isEmpty()) {
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		} else {
			resultMap.setResultStatus(ProConst.GR_STATE_ERR);
			resultMap.setResultList(invalidGrItems);
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
				Const.INVALID_STATUS_ERR);
	}

	private boolean isAlreadyInvoiceCreated(Map<String, Object> param){
		ResultMap resultMap = ResultMap.getInstance();
		List<Map<String, Object>> invoiceItems = invoiceRepository.searchInvoiceItemsByGrIds(param);

		if(invoiceItems.size() == 0) {
			return false;
		}
		return true;
	}
}
