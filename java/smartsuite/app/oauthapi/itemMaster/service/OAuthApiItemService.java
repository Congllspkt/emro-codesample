package smartsuite.app.oauthapi.itemMaster.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.oauthapi.itemMaster.repository.OAuthApiItemRepository;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class OAuthApiItemService {
	
	@Inject
	OAuthApiItemRepository oAuthApiItemRepository;
	
	public Map getItems(Map param) {
		Map resultData = Maps.newHashMap();
		resultData.put("item", oAuthApiItemRepository.findListItem(param));
		
		Map response = Maps.newHashMap();
		response.put("result_status", "S");
		response.put("result_code", "");
		response.put("result_message", "");
		response.put("result_data", resultData);
		return response;
	}
	
	public Map getCategories(Map param) {
		Map resultData = Maps.newHashMap();
		resultData.put("item", oAuthApiItemRepository.findListItemCategory(param));
		
		Map response = Maps.newHashMap();
		response.put("result_status", "S");
		response.put("result_code", "");
		response.put("result_message", "");
		response.put("result_data", resultData);
		return response;
	}
}
