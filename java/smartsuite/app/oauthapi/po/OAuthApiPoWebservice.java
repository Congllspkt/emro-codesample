package smartsuite.app.oauthapi.po;

import smartsuite.app.oauthapi.po.bean.SendPoParam;
import smartsuite.app.oauthapi.po.bean.WsResult;

import javax.jws.WebService;

@WebService
public interface OAuthApiPoWebservice {

	WsResult sendPo(SendPoParam param);
}
