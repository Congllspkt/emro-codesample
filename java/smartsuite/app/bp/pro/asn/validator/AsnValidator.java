package smartsuite.app.bp.pro.asn.validator;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.common.shared.ResultMap;

import java.util.Map;

@Service
public class AsnValidator {
	/**
	 * 납품예정 헤더 검증결과 확인
	 *
	 * @param targetAsnData
	 * @param comparedResult
	 * @return
	 */
	public ResultMap checkValidationResultOfAsnData(Map<String, Object> targetAsnData, Map<String, Object> comparedResult) {
		return ValidatorUtil.getResultMapByCheckResult(targetAsnData, comparedResult);
		
	}
	
	/**
	 * 검수요청이 존재하는지
	 *
	 * @param targetAsnData
	 * @return
	 */
	public Boolean existsGoodsInspectionRequest(Map<String, Object> targetAsnData) {
		String asnUuid = targetAsnData.get("asn_uuid") == null ? null : targetAsnData.get("asn_uuid").toString();
		return !Strings.isNullOrEmpty(asnUuid);
	}
}
