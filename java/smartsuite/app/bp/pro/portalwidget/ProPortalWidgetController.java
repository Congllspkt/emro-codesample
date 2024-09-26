package smartsuite.app.bp.pro.portalwidget;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="**/portal/widget/pro/**/")
public class ProPortalWidgetController {

	@Inject
	ProPortalWidgetService proPortalWidgetService;
	
	/**
	 * 구매요청 품목 진행현황 포틀릿
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findListPrProgress.do")
	public @ResponseBody List<Map<String, Object>> findListPrProgress(@RequestBody Map<String, Object> param) {
		return proPortalWidgetService.findListPrProgress(param);
	}
	
	/**
	 * 구매요청 TOP 5 품목 포틀릿
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findListPrFreqItem.do")
	public @ResponseBody List<Map<String, Object>> findListPrFreqItem(@RequestBody Map<String, Object> param) {
		return proPortalWidgetService.findListPrFreqItem(param);
	}
}
