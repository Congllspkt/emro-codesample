package smartsuite.app.bp.edoc.template.validator;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;

/**
 * 전자계약 서식관리 관련 처리를 하는 서비스 Class입니다.
 *
 * @author Minji Choi
 * @see 
 * @FileName ClausetService.java
 * @package smartsuite.app.bp.edoc.contract
 * @since 2018.01.29
 * @변경이력 : [2018.01.29] Minji Choi 최초작성
 */

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class ClauseValidator {

	public ResultMap checkCntrItemName(int nameCheckResult) {
		
		if (nameCheckResult > 0) {
			return ResultMap.USED();
		}else {
			return ResultMap.SUCCESS();
		}
	}
	
	public ResultMap checkCntrTmpItemVariable(int tmpVariableCheckResult) {
		
		if (tmpVariableCheckResult > 0) {
			return ResultMap.DUPLICATED();
		}else {
			return ResultMap.SUCCESS();
		}
	}
	
}