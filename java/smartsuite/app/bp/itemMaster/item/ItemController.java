package smartsuite.app.bp.itemMaster.item;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.itemMaster.item.service.ItemService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.mybatis.data.impl.PageResult;

import javax.inject.Inject;

import java.util.List;
import java.util.Map;


/**
 * 품목 관련 controller
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/itemMaster/item/**/")
public class ItemController {

	@Inject
	ItemService itemService;

	/**
	 * 품목 현황 조회
	 *
	 * @param
	 * @return the PageResult
	 */
	@RequestMapping(value = "findListItem.do")
	public @ResponseBody PageResult findListItem(@RequestBody Map<String, Object> param) {
		List result = itemService.findListItem(param);
		return new PageResult(result);
	}

	/**
	 * 품목정보 조회
	 *
	 * @param
	 * @return the resultmap
	 */
	@RequestMapping(value = "findInfoItem.do")
	public @ResponseBody ResultMap findInfoItem(@RequestBody Map<String, Object> param) {
		return itemService.findInfoItem(param);
	}
}