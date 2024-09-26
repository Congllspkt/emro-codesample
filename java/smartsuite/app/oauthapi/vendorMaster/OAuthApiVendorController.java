package smartsuite.app.oauthapi.vendorMaster;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.oauthapi.vendorMaster.service.OAuthApiVendorService;

import javax.inject.Inject;
import java.util.Map;

@Controller
@RequestMapping("/vendor")
public class OAuthApiVendorController {
	
	@Inject
	OAuthApiVendorService oAuthApiVendorService;
	
	@RequestMapping(value = "/getVendors", method = RequestMethod.POST)
	public @ResponseBody Map getVendors(@RequestParam Map param) {
		return oAuthApiVendorService.getVendors(param);
	}
}
