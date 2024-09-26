package smartsuite.app.sp.pro.pp.validator;

import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.common.shared.ResultMap;

import java.util.Map;

@Service
public class SpProgressPaymentValidator {

	/**
	 * 기성요청 상태 정합성 확인
	 * @param param
	 * @param checkResult
	 * @return
	 */
	public ResultMap compareProgressPaymentRequestStatus(Map<String, Object> param, Map<String, Object> checkResult) {
		// UPDATE 시 : 화면에서 조회한 시점의 기성요청 진행상태값과 현재 DB에서의 진행상태값을 비교
		return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
    }

    /**
     * 기성요청 등록 가능여부 체크
     *
     * @param param
     * @return
     */
    public ResultMap checkPoStatus(Map<String, Object> param, Map<String, Object> invalidResult) {
        if (invalidResult == null) {
            return ResultMap.SUCCESS();
        }
		return ResultMap.builder().resultStatus(ProConst.PO_STATE_ERR).resultData(invalidResult).build();
    }
}
