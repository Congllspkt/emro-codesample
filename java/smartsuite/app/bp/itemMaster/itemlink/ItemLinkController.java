package smartsuite.app.bp.itemMaster.itemlink;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.itemMaster.itemlink.service.ItemLinkService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.mybatis.data.impl.PageResult;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;


/**
 * 품목 운영조직 연결 관련 controller
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/itemMaster/itemlink/**/")
public class ItemLinkController {

	@Inject
	ItemLinkService itemLinkService;

	/**
	 * 품목 운영조직 연결 조회
	 *
	 * @param
	 * @return the PageResult
	 */
	@RequestMapping(value = "findListItemLink.do")
	public @ResponseBody PageResult findListItemLink(@RequestBody Map<String, Object> param) {
		List result = itemLinkService.findListItemLink(param);
		return new PageResult(result);
	}


	/**
	 * 운영조직 추가
	 *
	 * @param
	 * @return the resultmap
	 */
	@RequestMapping(value = "saveListItemOorg.do")
	public @ResponseBody ResultMap saveListItemOorg(@RequestBody Map<String, Object> param) {
		return itemLinkService.saveListItemOorg(param);
	}
}