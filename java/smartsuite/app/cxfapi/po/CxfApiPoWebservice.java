package smartsuite.app.cxfapi.po;

import smartsuite.app.cxfapi.po.bean.SendPoParam;
import smartsuite.app.cxfapi.po.bean.WsResult;

import javax.jws.WebService;

@WebService
public interface CxfApiPoWebservice {

	WsResult sendPo(SendPoParam param);
}
