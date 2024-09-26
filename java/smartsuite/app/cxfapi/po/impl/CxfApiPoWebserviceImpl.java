package smartsuite.app.cxfapi.po.impl;

import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.cxfapi.po.CxfApiPoWebservice;
import smartsuite.app.cxfapi.po.bean.SendPoParam;
import smartsuite.app.cxfapi.po.bean.WsResult;

import javax.inject.Inject;
import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "smartsuite.app.cxfapi.po.CxfApiPoWebservice")
public class CxfApiPoWebserviceImpl implements CxfApiPoWebservice {
	
	@Inject
	SharedService sharedService;
	
	@Override
	public WsResult sendPo(SendPoParam param) {
		
		if(param.getHeader() == null) {
			return this.responseData("E", "not exists header", null);
		}
		if(param.getItem() == null) {
			return this.responseData("E", "not exists item", null);
		}
		
		List<SendPoParam.Header> headers = param.getHeader();
		List<SendPoParam.Item> items = param.getItem();
		
		/*this.createPo(headers);
		this.createPoItem(items);*/
		
		String poNo = sharedService.generateDocumentNumber("PO");
		
		return this.responseData("S", null, poNo);
	}
	
	protected WsResult responseData(String resultStatus, String resultMessage, String resultData) {
		WsResult result = new WsResult(resultStatus, resultMessage, resultData);
        return result;
	}
}
