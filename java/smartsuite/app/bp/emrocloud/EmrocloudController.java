package smartsuite.app.bp.emrocloud;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.emrocloud.service.EmrocloudService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/emrocloud/**/")
public class EmrocloudController {
	
	@Inject
	EmrocloudService emrocloudService;
	
	@RequestMapping(value = "findListEmrocloudVd.do")
	public @ResponseBody List<Map> findListEmrocloudVd(@RequestBody Map param){
		return emrocloudService.findListEmrocloudVd(param);
	}
}
