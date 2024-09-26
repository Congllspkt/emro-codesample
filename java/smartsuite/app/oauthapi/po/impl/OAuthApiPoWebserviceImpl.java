package smartsuite.app.oauthapi.po.impl;

import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.oauthapi.po.OAuthApiPoWebservice;
import smartsuite.app.oauthapi.po.bean.SendPoParam;
import smartsuite.app.oauthapi.po.bean.WsResult;
import smartsuite.webservice.ManagedWebServiceBean;
import smartsuite.webservice.ManagedWebServiceMethod;
import smartsuite.webservice.ServiceKind;

import javax.inject.Inject;
import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "smartsuite.app.oauthapi.po.OAuthApiPoWebservice")
@ManagedWebServiceBean(description = "PO Webservice")
public class OAuthApiPoWebserviceImpl implements OAuthApiPoWebservice {
	
	@Inject
	SharedService sharedService;
	
	@Override
	@ManagedWebServiceMethod(description = "Create PO", servicekind = ServiceKind.SOAP)
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
