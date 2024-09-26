package smartsuite.app.bp.itemMaster.item.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.itemMaster.item.event.ItemEventPublisher;
import smartsuite.app.bp.itemMaster.item.repository.ItemRepository;
import smartsuite.app.bp.itemMaster.itemcommon.service.ItemCommonService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.module.ModuleManager;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * 품목 관련 service
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes","unchecked"})
public class ItemService {

	@Inject
	ItemRepository itemRepository;

	@Inject
	ItemCommonService itemCommonService;

	private String cmsModule = "cms";

	/**
	 * 품목현황 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListItem(Map<String, Object> param) {
		return itemRepository.findListItem(param);
	}

	/**
	 * 품목현황 상세 조회
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap findInfoItem(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();

		resultMap.put("itemInfo", itemRepository.findInfoItem(param));
		param.put("only_yn", true);
		resultMap.put("oorgList", itemCommonService.getListItemOorgOnly(param));

		return ResultMap.SUCCESS(resultMap);
	}

}
