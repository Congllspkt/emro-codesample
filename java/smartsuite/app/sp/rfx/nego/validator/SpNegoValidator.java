package smartsuite.app.sp.rfx.nego.validator;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.sp.rfx.nego.repository.SpNegoRepository;

/**
 * 업체 협상 유효성 체크
 * @author user
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class SpNegoValidator {
	
	@Inject
	SpNegoRepository spNegoRepository;
	
	/**
	 * 업체 진행상태 유효성 확인
	 * @param param
	 */
	public void validate(Map param) {
		// 협상진행상태체크
		this.checkNegoProgSts(param);
		this.checkVdNegoProgSts(param);
	}
	
	/**
	 * 협상진행상태 체크
	 * @param param
	 */
	public void checkNegoProgSts(Map param){
		ValidatorUtil.handleResultMapByCheckResult(spNegoRepository.checkNegoProgSts(param));
	}
	
	/**
	 * 업체 협상진행상태 유효성 확인
	 * @param param
	 */
	public void checkVdNegoProgSts(Map param) {
		ValidatorUtil.handleResultMapByCheckResult(spNegoRepository.checkVdProgSts(param));
	}
}
