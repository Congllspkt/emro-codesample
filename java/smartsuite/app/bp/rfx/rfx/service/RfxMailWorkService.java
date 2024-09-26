package smartsuite.app.bp.rfx.rfx.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.repository.RfxMailWorkRepository;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxMailWorkService {
	
	@Inject
	RfxMailWorkRepository rfxMailWorkRepository;
	
	public List<Map> findListRfxVendorListAndHistory(Map param) {
		return rfxMailWorkRepository.findListRfxVendorListAndHistory(param);
	}
	
	public ResultMap findRfxVendorHistoryProcess(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		Map<String,Object> rfxVendorEmailWorkHistory = rfxMailWorkRepository.findRfxVendorHistoryProcess(param);
		
		//email 상태 코드
		String emailWorkSts = rfxVendorEmailWorkHistory.getOrDefault("eml_task_sts_ccd","") == null? "":(String)rfxVendorEmailWorkHistory.getOrDefault("eml_task_sts_ccd","");
		
		//회신 완료이기 때문에, 발송되면 안됨.
		if(("RE_CMPLD").equals(emailWorkSts)){
			resultMap.setResultStatus(ResultMap.STATUS.FAIL);
			resultMap.setResultMessage("회신 완료된 업체 입니다. 재발송이 불가능 합니다.");
		}else{
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		}
		
		return resultMap;
	}
}
