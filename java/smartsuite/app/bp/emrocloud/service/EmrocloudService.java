package smartsuite.app.bp.emrocloud.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.ifproxy.core.entity.ExecutionLevel;
import smartsuite.ifproxy.restful.entity.RestfulInput;
import smartsuite.ifproxy.restful.entity.RestfulOutput;
import smartsuite.ifproxy.web.ExecuteService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EmrocloudService {
	
	@Inject
	ExecuteService executeService;
	
	public List<Map> findListEmrocloudVd(Map param) {
		
		RestfulInput input = new RestfulInput();
		input.setQueryParameters(param);
		
		RestfulOutput output = executeService.executeRestful("GET_EMROCLOUD_DEV_VD", input, ExecutionLevel.Test);
		try {
			Map outputBody = output.getBodyAsMap();
			List<Map> resultData = (List<Map>) outputBody.get("result_data");
			return resultData;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
