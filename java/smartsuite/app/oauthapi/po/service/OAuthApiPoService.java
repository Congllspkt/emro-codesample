package smartsuite.app.oauthapi.po.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.oauthapi.po.repository.OAuthApiPoRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OAuthApiPoService {
	
	@Inject
	OAuthApiPoRepository oAuthApiPoRepository;
	
	@Inject
	SharedService sharedService;
	
	public Map sendPo(Map param) {
		if(param.get("header") == null) {
			return this.responseData("E", "not exists header", null);
		}
		if(param.get("item") == null) {
			return this.responseData("E", "not exists item", null);
		}
		
		List<Map> headers = (List<Map>) param.get("header");
		List<Map> items = (List<Map>) param.get("item");
		
		/*this.createPo(headers);
		this.createPoItem(items);*/
		
		String poNo = sharedService.generateDocumentNumber("PO");
		
		return this.responseData("S", null, param);
	}
	
	private void createPo(List<Map> headers) {
		for(Map header : headers) {
			oAuthApiPoRepository.createPo(header);
		}
	}
	
	private void createPoItem(List<Map> items) {
		for(Map item : items) {
			oAuthApiPoRepository.createPoItem(item);
		}
	}
	
	protected Map responseData(String resultStatus, String resultMessage, Map resultData) {
		Map response = Maps.newHashMap();
        response.put("result_status", resultStatus);
        response.put("result_code", "");
        response.put("result_message", resultMessage);
        response.put("result_data", resultData);
        return response;
	}
}
