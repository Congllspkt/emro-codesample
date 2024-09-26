package smartsuite.app.bp.itemMaster.itemcat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.itemMaster.itemcat.service.ItemCatService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * 품목분류 관련 controller
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/itemMaster/itemcat/**/")
public class ItemCatController {
	
	@Inject
    ItemCatService itemCatService;

	/**
	 * 품목분류 마스터 조회
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findListItemCat.do")
	public @ResponseBody FloaterStream findListItemCat(@RequestBody Map<String, Object> param) {
		return itemCatService.findListItemCat(param);
	}

	/**
	 * 품목분류 마스터 조회
	 * 조회조건이 있는 경우
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findListItemCatWithCdOrNm.do")
	public @ResponseBody FloaterStream findListItemCatWithCdOrNm(@RequestBody Map<String, Object> param) {
		return itemCatService.findListItemCatWithCdOrNm(param);
	}

	/**
	 * 품목분류 삭제
	 *
	 * @param 
	 * @return the ResultMap
	 */
	@RequestMapping(value = "deleteListItemCat.do")
	public @ResponseBody ResultMap deleteListItemCat(@RequestBody Map<String, Object> param) {
		return itemCatService.deleteListItemCat(param);
	}

	/**
	 * 품목분류 상세조회
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "findInfoItemCat.do")
	public @ResponseBody Map<String, Object> findInfoItemCat(@RequestBody Map<String, Object> param) {
		return itemCatService.findInfoItemCat(param);
	}

	/**
	 * 품목분류 이력조회
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findListItemCatHistory.do")
	public @ResponseBody List findListItemCatHistory(@RequestBody Map<String, Object> param) {
		return itemCatService.findListItemCatHistory(param);
	}


	/**
	 * 품목분류 저장
	 *
	 * @param
	 * @return the resultMap
	 */
	@RequestMapping(value = "saveInfoItemCat.do")
	public @ResponseBody ResultMap saveInfoItemCat(@RequestBody Map<String, Object> param) {
		return itemCatService.saveInfoItemCat(param);
	}

	/**
	 * 유사분류목록 삭제
	 *
	 * @param
	 * @return the ResultMap
	 */
	@RequestMapping(value = "deleteListSimilar.do")
	public @ResponseBody ResultMap deleteListSimilar(@RequestBody Map<String, Object> param) {
		return itemCatService.deleteListSimilar(param);
	}
}