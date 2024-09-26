package smartsuite.app.bp.rfx.config;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.config.service.RfxConfigService;


import javax.inject.Inject;
import java.util.List;
import java.util.Map;


@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/rfx/config/**/")
public class RfxConfigController {

	@Inject
	RfxConfigService rfxConfigService;

	/**
	 * Rfx configuration Rfx 복사 목록 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfxCopy.do")
	public @ResponseBody List findListRfxCopy(@RequestBody Map param) {
		return rfxConfigService.findListRfxCopy(param);
	}
	@RequestMapping(value = "findDataByReqItemIdsFromConfig.do")
	public @ResponseBody Map findDataByReqItemIdsFromConfig(@RequestBody Map param) {
		return rfxConfigService.findDataByReqItemIdsFromConfig(param);
	}
}