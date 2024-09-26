package smartsuite.app.oauthapi.itemMaster;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.oauthapi.itemMaster.service.OAuthApiItemService;

import javax.inject.Inject;
import java.util.Map;

@Controller
@RequestMapping("/item")
public class OAuthApiItemController {
	
	@Inject
	OAuthApiItemService oAuthApiItemService;
	
	@RequestMapping(value = "/getItems", method = RequestMethod.POST)
	public @ResponseBody Map getItems(@RequestParam Map param) {
		return oAuthApiItemService.getItems(param);
	}
	
	@RequestMapping(value = "/getCategories", method = RequestMethod.POST)
	public @ResponseBody Map getCategories(@RequestParam Map param) {
        return oAuthApiItemService.getCategories(param);
    }
}
