package smartsuite.app.bp.itemMaster.master;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.itemMaster.itemcommon.service.ItemCommonService;
import smartsuite.app.bp.itemMaster.master.service.ItemMasterService;
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
@RequestMapping(value="**/itemMaster/master/**/")
public class ItemMasterController {

	@Inject
	ItemMasterService itemMasterService;

	@Inject
	ItemCommonService itemCommonService;

	/**
	 * 품목 현황 조회
	 *
	 * @param
	 * @return the PageResult
	 */
	@RequestMapping(value = "findListItemMaster.do")
	public @ResponseBody PageResult findListItemMaster(@RequestBody Map<String, Object> param) {
		List result = itemMasterService.findListItemMaster(param);
		return new PageResult(result);
	}

	/**
	 * 품목정보 조회
	 *
	 * @param
	 * @return the resultmap
	 */
	@RequestMapping(value = "findInfoItemMaster.do")
	public @ResponseBody ResultMap findInfoItemMaster(@RequestBody Map<String, Object> param) {
		return itemMasterService.findInfoItemMaster(param);
	}

	/**
	 * 품목정보 조회 (품목 변경)
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "findInfoAllItemMaster.do")
	public @ResponseBody ResultMap findInfoAllItemMaster(@RequestBody Map<String, Object> param) {
		return itemMasterService.findInfoAllItemMaster(param);
	}

	/**
	 * 배정 속성 목록 조회
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findListItemAsgnAttrFromItemMaster.do")
	public @ResponseBody List<Map<String, Object>> findListItemAsgnAttrFromItemMaster(@RequestBody Map<String, Object> param) {
		return itemMasterService.findListItemAsgnAttrFromItemMaster(param);
	}

	/**
	 * 품목 정보 변경
	 *
	 * @param
	 * @return the resultmap
	 */
	@RequestMapping(value = "modifyInfoItemMaster.do")
	public @ResponseBody ResultMap modifyInfoItemMaster(@RequestBody Map<String, Object> param) {
		return itemMasterService.modifyInfoItemMaster(param);
	}

	/**
	 * 품목 등록 요청 정보가 있는지 체크(cms에서)
	 *
	 * @param
	 * @return the resultmap
	 */
	@RequestMapping(value = "checkExistedItemRegReqFromItemMaster.do")
	public @ResponseBody ResultMap checkExistedItemRegReqFromItemMaster(@RequestBody Map<String, Object> param) {
		return itemMasterService.checkExistedItemRegReqFromItemMaster(param);
	}

	/**
	 * 품목 변경 요청 하기 위한 정보 조회
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "findInfoChangeItemMaster.do")
	public @ResponseBody ResultMap findInfoChangeItemMaster(@RequestBody Map<String, Object> param) {
		return itemMasterService.findInfoChangeItemMaster(param);
	}

	/**
	 * 품목 변경 요청 cms로 발송
	 *
	 * @param
	 * @return the resultmap
	 */
	@RequestMapping(value = "saveInfoChangeItemReq.do")
	public @ResponseBody ResultMap saveInfoChangeItemReq(@RequestBody Map<String, Object> param) {
		return itemMasterService.saveInfoChangeItemReq(param);
	}


	/**
	 * Item-doctor 연동 유사도 조회
	 *
	 * @param
	 * @return the resultmap
	 */
	@RequestMapping(value = "findListItemSimilarityFromMaster.do")
	public @ResponseBody ResultMap findListItemSimilarityFromMaster(@RequestBody Map<String, Object> param) {
		return itemCommonService.findListItemSimilarityFromMaster(param);
	}
}