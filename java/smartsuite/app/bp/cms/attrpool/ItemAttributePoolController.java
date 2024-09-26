package smartsuite.app.bp.cms.attrpool;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.cms.attrpool.service.ItemAttributePoolService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;


/**
 * 품목 속성 Pool 관련 controller
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/attrpool/**/")
public class ItemAttributePoolController {
	
	@Inject
	ItemAttributePoolService itemAttributePoolService;
	
	/**
	 * 속성 Pool 조회
	 *
	 * @param
	 * @return the FloaterStream
	 */
	@RequestMapping(value = "findListAttributePool.do")
	public @ResponseBody FloaterStream findListAttributePool(@RequestBody Map<String, Object> param) {
		return itemAttributePoolService.findListAttributePool(param);
	}

	/**
	 * 속성 Pool 삭제
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "deleteListAttributePool.do")
	public @ResponseBody ResultMap deleteListAttributePool(@RequestBody Map<String, Object> param) {
		return itemAttributePoolService.deleteListAttributePool(param);
	}

	/**
	 * 속성 Pool 상세 조회
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "findInfoAttributePool.do")
	public @ResponseBody Map<String, Object>findInfoAttributePool(@RequestBody Map<String, Object> param) {
		return itemAttributePoolService.findInfoAttributePool(param);
	}

	/**
	 * 속성 Pool 분류배정현황 조회
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findListAssignedAttributePool.do")
	public @ResponseBody List findListAssignedAttributePool(@RequestBody Map<String, Object> param) {
		return itemAttributePoolService.findListAssignedAttributePool(param);
	}

	/**
	 * 속성 Pool 저장
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "saveInfoAttributePool.do")
	public @ResponseBody ResultMap saveInfoAttributePool(@RequestBody Map<String, Object> param) {
		return itemAttributePoolService.saveInfoAttributePool(param);
	}
	
}