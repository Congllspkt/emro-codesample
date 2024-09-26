package smartsuite.app.bp.cms.attrasgn;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.cms.attrasgn.service.ItemAttributeAsgnService;
import smartsuite.app.bp.itemMaster.itemcat.service.ItemCatService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

/**
 * 속성 배정 관련 controller
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/attrasgn/**/")
public class ItemAttributeAsgnController {
	
	@Inject
	ItemAttributeAsgnService itemAttributeAsgnService;

	@Inject
	ItemCatService itemCatService;

	/**
	 * 속성배정 목록 조회
	 *
	 * @param
	 * @return the FloaterStream
	 */
	@RequestMapping(value = "findListItemCatByCms.do")
	public @ResponseBody FloaterStream findListItemCatByCms(@RequestBody Map<String, Object> param) {
		return itemCatService.findListItemCat(param);
	}

	/**
	 * 속성배정 목록 조회(조회조건 추가)
	 *
	 * @param
	 * @return the FloaterStream
	 */
	@RequestMapping(value = "findListItemCatWithCdOrNmByCms.do")
	public @ResponseBody FloaterStream findListItemCatWithCdOrNmByCms(@RequestBody Map<String, Object> param) {
		return itemCatService.findListItemCatWithCdOrNm(param);
	}

	/**
	 * 속성배정항목 목록 조회
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findListAsgnAttrMapping.do")
	public @ResponseBody List<Map<String, Object>> findListAsgnAttrMapping(@RequestBody Map<String, Object> param) {
		return itemAttributeAsgnService.findListAsgnAttrMapping(param);
	}

	/**
	 * 배정된 속성 항목을 저장
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "saveInfoAsgnAttr.do")
	public @ResponseBody ResultMap saveInfoAsgnAttr(@RequestBody Map<String, Object> param) {
		return itemAttributeAsgnService.saveInfoAsgnAttr(param);
	}

	/**
	 * 배정된 속성 항목을 삭제
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "deleteListAsgnAttr.do")
	public @ResponseBody ResultMap deleteListAsgnAttr(@RequestBody Map<String, Object> param) {
		return itemAttributeAsgnService.deleteListAsgnAttr(param);
	}
}