package smartsuite.app.bp.itemMaster.master.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.itemMaster.item.event.ItemEventPublisher;
import smartsuite.app.bp.itemMaster.master.event.ItemMasterEventPublisher;
import smartsuite.app.bp.itemMaster.master.repository.ItemMasterRepository;
import smartsuite.app.bp.itemMaster.itemcommon.service.ItemCommonService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.module.ModuleManager;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * 품목 관련 service
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes","unchecked"})
public class ItemMasterService {

	@Inject
	ItemMasterRepository itemMasterRepository;

	@Inject
	ItemCommonService itemCommonService;

	@Inject
	StdFileService stdFileService;

	@Inject
	ItemMasterEventPublisher itemMasterEventPublisher;

	private String cmsModule = "cms";

	/**
	 * 품목현황 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListItemMaster(Map<String, Object> param) {
		return itemMasterRepository.findListItemMaster(param);
	}

	/**
	 * 품목현황 상세 조회
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap findInfoItemMaster(Map<String, Object> param) {
		ModuleManager moduleManager = new ModuleManager();
		Map<String, Object> resultMap = Maps.newHashMap();
		Boolean isView = param.get("is_view") == null ? false : (Boolean) param.get("is_view");

		Map<String, Object> itemInfo = itemMasterRepository.findInfoItemMaster(param);
		if(isView && moduleManager.exist(cmsModule)) {
			itemInfo.put("item_reg_req_cnt", itemMasterEventPublisher.findCntProgressingItemRegReq(param));
		}

		resultMap.put("itemInfo", itemInfo);
		resultMap.put("oorgList", itemCommonService.getListItemOorg(param));

		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 품목현황 상세 조회(변경 요청)
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap findInfoAllItemMaster(Map<String, Object> param) {
		ModuleManager moduleManager = new ModuleManager();
		Map<String, Object> resultMap = Maps.newHashMap();

		List<Map<String, Object>> asgnAttrList = Lists.newArrayList();
		Map<String, Object> itemInfo = itemMasterRepository.findInfoItemMaster(param);
		if(moduleManager.exist(cmsModule)) {
			asgnAttrList = itemMasterEventPublisher.findListItemAsgnAttrFromItemMaster(param);
		}

		resultMap.put("itemInfo", itemInfo);
		resultMap.put("oorgList", itemCommonService.getListItemOorg(param));
		resultMap.put("asgnAttrList", asgnAttrList);
		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * cms 모듈이 있는 경우 cms에서 Itemcat_iattr 기반 배정속성 정보를 가져온다.
	 *
	 * @param
	 * @return the void
	 */
	public List<Map<String, Object>> findListItemAsgnAttrFromItemMaster(Map<String, Object> param) {
		ModuleManager moduleManager = new ModuleManager();
		List<Map<String, Object>> asgnAttrList = Lists.newArrayList();
		if(moduleManager.exist(cmsModule)) {
			asgnAttrList = itemMasterEventPublisher.findListItemAsgnAttr(param);
		}

		return asgnAttrList;
	}

	/**
	 * 품목마스터 품목 정보를 변경한다.
	 *
	 * @param
	 * @return the void
	 */
	public ResultMap modifyInfoItemMaster(Map<String, Object> param) {
		ModuleManager moduleManager = new ModuleManager();
		Map<String, Object> itemInfo = (Map<String, Object>) param.getOrDefault("itemInfo", Maps.newHashMap());
		List<Map<String, Object>> oorgList = (List<Map<String, Object>>) param.getOrDefault("oorgList", Lists.newArrayList());
		List<Map<String, Object>> asgnAttrList = (List<Map<String, Object>>) param.getOrDefault("asgnAttrList", Lists.newArrayList());

		//품목 기본 정보를 갱신
		itemCommonService.updateItemWithOorg(itemInfo, oorgList);
		if(moduleManager.exist(cmsModule)) {
			//품목 속성 정보를 모두 삭제 후 저장
			itemMasterEventPublisher.insertItemIattrAfterDelete(itemInfo, asgnAttrList);
		}
		return ResultMap.SUCCESS(itemInfo);
	}

	/**
	 * 품목 변경 요청 하기 위한 조건 정보 조회
	 *
	 * @param
	 * @return the resultmap
	 */
	public ResultMap checkExistedItemRegReqFromItemMaster(Map<String, Object> param) {
		ModuleManager moduleManager = new ModuleManager();
		ResultMap resultMap = ResultMap.getInstance();

		if(moduleManager.exist(cmsModule)) {
			Boolean checkReq = itemMasterEventPublisher.checkExistedItemRegReqFromItemMaster(param);

			if(checkReq) {
				resultMap.setResultStatus(ResultMap.STATUS.FAIL);
				resultMap.setResultMessage("STD.CMS1010");
			} else {
				resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
			}
		} else {
			resultMap.setResultStatus(ResultMap.STATUS.FAIL);
		}

		return resultMap;
	}

	/**
	 * 품목 변경 요청 하기 위한 정보 조회
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap findInfoChangeItemMaster(Map<String, Object> param) {
		ModuleManager moduleManager = new ModuleManager();
		Map<String, Object> resultMap = Maps.newHashMap();
		Map<String, Object> itemInfo = itemMasterRepository.findInfoItemMasterForChange(param);
		itemInfo.put("item_reg_req_no", "");

		String athgUuid = (String) itemInfo.get("athg_uuid");
		if(!Strings.isNullOrEmpty(athgUuid)) {
			String newAthgUuid = stdFileService.copyFile(athgUuid);
			itemInfo.put("athg_uuid", newAthgUuid);
		}

		String imgAthgUuid = (String) itemInfo.get("img_athg_uuid");
		if(!Strings.isNullOrEmpty(imgAthgUuid)) {
			String newImgAthgUuid = stdFileService.copyFile(imgAthgUuid);
			itemInfo.put("img_athg_uuid", newImgAthgUuid);
		}

		if(moduleManager.exist(cmsModule)) {
			if(itemInfo != null && itemInfo.get("itemcat_lvl_4_cd") != null) {
				Map<String, Object> attrParam = Maps.newHashMap();
				attrParam.put("itemcat_cd", itemInfo.get("itemcat_lvl_4_cd"));
				attrParam.put("itemcat_lvl", 4);

				resultMap.put("asgnAttrList", itemMasterEventPublisher.findListItemAsgnAttrByItemcatFromItemMaster(attrParam));
			}
		}

		resultMap.put("itemInfo", itemInfo);
		resultMap.put("oorgList", itemCommonService.getListItemOorg(param));
		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 품목 변경 요청 cms로 발송
	 *
	 * @param
	 * @return the resultmap
	 */
	public ResultMap saveInfoChangeItemReq(Map<String, Object> param) {
		ModuleManager moduleManager = new ModuleManager();
		ResultMap resultMap = ResultMap.getInstance();
		if(moduleManager.exist(cmsModule)){
			resultMap = itemMasterEventPublisher.saveInfoChangeItemReq(param);
		} else {
			resultMap.setResultStatus(ResultMap.FAIL());
		}

		if(resultMap.isFail()) {
			return ResultMap.FAIL(resultMap.getResultMessage());
		}

		return ResultMap.SUCCESS(resultMap.getResultData());
	}
	
	public Map findItemByItemCd(Map param) {
		return itemMasterRepository.findItemByItemCd(param);
	}
}
