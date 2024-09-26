package smartsuite.app.bp.itemMaster.itemlink.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.itemMaster.itemcommon.repository.ItemCommonRepository;
import smartsuite.app.bp.itemMaster.itemlink.repository.ItemLinkRepository;
import smartsuite.app.common.shared.ResultMap;


import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * 품목 운영조직 연결 관련 service
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes","unchecked"})
public class ItemLinkService {

	@Inject
	ItemLinkRepository itemLinkRepository;

	@Inject
	ItemCommonRepository itemCommonRepository;

	/**
	 * 품목 운영조직 연결 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListItemLink(Map<String, Object> param) {
		return itemLinkRepository.findListItemLink(param);
	}

	/**
	 * 운영조직을 추가
	 *
	 * @param
	 * @return the resultmap
	 */
	public ResultMap saveListItemOorg(Map<String, Object> param){
		List<Map<String, Object>> checkedItems = (List<Map<String, Object>>) param.getOrDefault("checkedItems", Lists.newArrayList());
		Map<String, Object> oorgInfo = (Map<String, Object>) param.getOrDefault("oorgInfo", Maps.newHashMap());

		for(Map row : checkedItems){
			row.put("oorg_cd", oorgInfo.get("oorg_cd"));
			itemCommonRepository.insertItemOorg(row);
		}

		return ResultMap.SUCCESS();
	}
}
