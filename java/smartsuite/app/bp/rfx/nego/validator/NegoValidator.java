package smartsuite.app.bp.rfx.nego.validator;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.rfx.nego.repository.NegoRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;

/**
 * 협상 유효성 체크
 * @author user
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class NegoValidator {
	
	@Inject
	NegoRepository negoRepository;
	
	/**
	 * 협상생성 가능여부 체크
	 */
	public ResultMap validate(Map param) {
		String rfxId = (String) param.get("rfx_uuid");
		//rfxId가 없는 경우
		if(rfxId == null || "".equals(rfxId)) {
			throw new CommonException(ErrorCode.NOT_EXIST);
		}
		//중복되는 협상이 있을경우
		int cnt = negoRepository.existsNegoCnt(param);
		if(cnt > 0) {
			throw new CommonException(ErrorCode.DUPLICATED);
		}
		
		this.checkRfxProgSts(param);
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 견적진행상태 체크
	 * @param param
	 */
	public void checkRfxProgSts(Map param) {
		ValidatorUtil.handleResultMapByCheckResult(negoRepository.compareRfxProgSts(param));
	}
	
	/**
	 * 협상진행상태 체크
	 * @param param
	 */
	public void checkNegoProgSts(Map param){
		ValidatorUtil.handleResultMapByCheckResult(negoRepository.checkNegoProgSts(param));
	}
	
	/**
	 * 재협상상태 체크
	 * @param param
	 */
	public void checkReNegoSts(Map param) {
		ValidatorUtil.handleResultMapByCheckResult(negoRepository.checkReNegoSts(param));
	}

}
