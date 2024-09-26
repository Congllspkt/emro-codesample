package smartsuite.app.oauthapi.po;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.oauthapi.po.service.OAuthApiPoService;

import javax.inject.Inject;
import java.util.Map;

@Controller
@RequestMapping("/po")
public class OAuthApiPoController {
	
	@Inject
	OAuthApiPoService oAuthApiPoService;
	
	@RequestMapping(value = "/sendPo", method = RequestMethod.POST)
	public @ResponseBody Map sendPo(@RequestParam Map param) {
		return oAuthApiPoService.sendPo(param);
	}
}
