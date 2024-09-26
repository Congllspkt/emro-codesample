package smartsuite.app.sp.rfx.rfi.validator;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.rfi.repository.SpRfiRepository;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class SpRfiValidator {
	
	@Inject
	SpRfiRepository spRfiRepository;
	
	public ResultMap validate(Map param) {
		// 화면에서 조회한 시점의 RFI 업체 제출 상태값과 현재 DB에서의 업체 제출 상태값을 비교
		Map checkResult = spRfiRepository.compareRfiVdSts(param);
		return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
	}
}