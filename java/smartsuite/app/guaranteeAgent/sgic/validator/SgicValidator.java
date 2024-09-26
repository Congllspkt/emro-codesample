package smartsuite.app.guaranteeAgent.sgic.validator;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import smartsuite.app.common.shared.ResultMap;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class SgicValidator {
	
	/**
	 * 데이터 존재 여부 확인
	 * 기준 : ecntr_uuid & cntr_egur_uuid
	 * @return
	 */
	public ResultMap validateById(Map param) {
		String gurUuid = (String)param.get("gur_uuid");
		
		if( Strings.isNullOrEmpty(gurUuid)) {
			return ResultMap.NOT_EXISTS(); 
		} else {
			return ResultMap.SUCCESS();
		}
	}
	
	

}
