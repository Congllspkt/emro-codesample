package smartsuite.app.oauthapi.pr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.oauthapi.pr.service.OAuthApiPrService;

import javax.inject.Inject;
import java.util.Map;

@Controller
@RequestMapping("/pr")
public class OAuthApiPrController {
	
	@Inject
	OAuthApiPrService oAuthApiPrService;
	
	@RequestMapping(value = "/getItems", method = RequestMethod.POST)
	public @ResponseBody Map getItems(@RequestParam Map param) {
		return oAuthApiPrService.getItems(param);
	}
}
