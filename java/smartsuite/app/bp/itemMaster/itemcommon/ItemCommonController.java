package smartsuite.app.bp.itemMaster.itemcommon;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.itemMaster.itemcommon.service.ItemCommonService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

/**
 * 품목 공통 관련 controller
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/itemMaster/itemcommon/**/")
public class ItemCommonController {
	
	@Inject
	ItemCommonService itemCommonService;

	/**
	 * 품목분류 마스터 조회
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findListItemCatFromSearchPopup.do")
	public @ResponseBody FloaterStream findListItemCatFromSearchPopup(@RequestBody Map<String, Object> param) {
		return itemCommonService.findListItemCatFromSearchPopup(param);
	}

	/**
	 * 품목 마스터 조회
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findListItemMasterFromSearchPopup.do")
	public @ResponseBody FloaterStream findListItemMasterFromSearchPopup(@RequestBody Map<String, Object> param) {
		return itemCommonService.findListItemMasterFromSearchPopup(param);
	}

	/**
	 * 나의품목저장, 삭제
	 *
	 * @param
	 * @return resultmap
	 */
	@RequestMapping(value="saveListMyItemFromSearchPopup.do")
	public @ResponseBody ResultMap saveListMyItemFromSearchPopup(@RequestBody Map param){
		return itemCommonService.saveListMyItemFromSearchPopup(param);
	}
}