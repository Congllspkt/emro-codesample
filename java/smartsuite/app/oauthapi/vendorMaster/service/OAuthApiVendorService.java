package smartsuite.app.oauthapi.vendorMaster.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.oauthapi.vendorMaster.repository.OAuthApiVendorRepository;

import javax.inject.Inject;
import java.util.Map;

@Service
public class OAuthApiVendorService {
	
	@Inject
	OAuthApiVendorRepository oAuthApiVendorRepository;
	
	public Map getVendors(Map param) {
		Map resultData = Maps.newHashMap();
		resultData.put("item", oAuthApiVendorRepository.findListVendor(param));
		
		Map response = Maps.newHashMap();
		response.put("result_status", "S");
		response.put("result_code", "");
		response.put("result_message", "");
		response.put("result_data", resultData);
		return response;
	}
}
