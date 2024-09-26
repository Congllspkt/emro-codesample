package smartsuite.app.sp.guarantee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.shared.GuarConst;
import smartsuite.app.sp.guarantee.repository.SpGuaranteeRepository;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.Map;


@Service
@Transactional
@SuppressWarnings({"rawtypes"})
public class SpGuaranteeService {
	@Inject
	SpGuaranteeRepository spGuaranteeRepository;

	/**
	 * 보증 조회
	 */
	public FloaterStream largeFindGuarList(Map<String, Object> param) {
		// 대용량 처리
		return spGuaranteeRepository.LargeSearchSpGuarList(param);
	}

	/**
	 * 오프라인 보증정보 조회
	 */
	public Map getOfflineBondInfo(Map<String,Object> param){

		return spGuaranteeRepository.getOfflineBondInfo(param);
	}
	/**
	 * 오프라인보증보험 보증승인요청
	 */
	public ResultMap saveOfflineGuarInfo(Map<String, Object> param) {
		param.put("gur_ath_typ_ccd", GuarConst.ATTACH_TYPE.OFFLINE);
		param.put("gur_sts_ccd", GuarConst.GUAR_STATUS.APPROVAL_WAITING);
		spGuaranteeRepository.saveOfflineGuarInfo(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 오프라인보증보험 보증취소
	 */
	public ResultMap saveOfflineGuarCancel(Map<String, Object> param) {
		param.put("gur_sts_ccd", GuarConst.GUAR_STATUS.PUBLIC_WAITING);
		spGuaranteeRepository.saveOfflineGuarCancel(param);
		return ResultMap.SUCCESS();
	}
	/**
	 * 보증 요청 정보 조회
	 */
	public Map getRequestBond(Map param) {
		return spGuaranteeRepository.getRequestBond(param);
	}
}
