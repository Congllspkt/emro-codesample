package smartsuite.app.oauthapi.pr.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.oauthapi.pr.repository.OAuthApiPrRepository;

import javax.inject.Inject;
import java.util.Map;

@Service
public class OAuthApiPrService {
	
	@Inject
	OAuthApiPrRepository oAuthApiPrRepository;
	public Map getItems(Map param) {
		Map resultData = Maps.newHashMap();
		resultData.put("header", oAuthApiPrRepository.findListPrHeader(param));
		resultData.put("item", oAuthApiPrRepository.findListPrItem(param));
		
		Map response = Maps.newHashMap();
		response.put("result_status", "S");
		response.put("result_code", "");
		response.put("result_message", "");
		response.put("result_data", resultData);
		return response;
	}
}
