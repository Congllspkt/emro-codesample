package smartsuite.app.bp.rfx.rfi.validator;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.rfx.rfi.repository.RfiRepository;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class RfiValidator {
	
	@Inject
	RfiRepository rfiRepository;
	
	public ResultMap compareRfiProgSts(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(Strings.isNullOrEmpty((String) param.get("rfi_uuid"))) {
			return ResultMap.SKIP();
		}
		
		Map checkResult = rfiRepository.compareRfiHdSts(param);
		ResultMap validator = ValidatorUtil.getResultMapByCheckResult(param, checkResult);
		return validator;
	}
}