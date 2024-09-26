package smartsuite.app.bp.itemMaster.itemcommon.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.context.i18n.LocaleContextHolder;
import smartsuite.app.bp.itemMaster.itemcat.service.ItemCatService;
import smartsuite.app.bp.itemMaster.itemcommon.event.ItemCommonEventPublisher;
import smartsuite.app.bp.itemMaster.itemcommon.repository.ItemCommonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.message.MessageUtil;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;
import smartsuite.module.ModuleManager;


import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class ItemCommonService {

	@Inject
	ItemCommonRepository itemCommonRepository;

	@Inject
	ItemCommonEventPublisher itemCommonEventPublisher;

	@Inject
	ItemCatService itemCatService;

	@Inject
	SharedService sharedService;

	@Inject
	MessageUtil messageUtil;

	private String itemDoctorModule = "item-doctor";

	private String proModule = "pro";
	
	/**
	 * 품목팝업 분류 조회
	 *
	 * @param
	 * @return list
	 */
	public FloaterStream findListItemCatFromSearchPopup(Map<String, Object> param) {
		FloaterStream resultList;
		String itemcatNm = param.get("itemcat_nm") == null ? "" : (String) param.get("itemcat_nm");

		if(Strings.isNullOrEmpty(itemcatNm)) {
			resultList = itemCatService.findListItemCat(param);
		} else {
			resultList = itemCatService.findListItemCatWithCdOrNm(param);
		}

		return resultList;
	}
	
	/**
	 * 품목팝업 품목 목록 조회
	 *
	 * @param
	 * @return list
	 */
	public FloaterStream findListItemMasterFromSearchPopup(Map<String, Object> param) {
		return itemCommonRepository.findListItemMasterFromSearchPopup(param);
	}

	/**
	 * 나의 품목 저장 삭제
	 *
	 * @param
	 * @return resultmap
	 */
	public ResultMap saveListMyItemFromSearchPopup(Map<String, Object> param){
		List<Map<String,Object>> insertMyItems = (List<Map<String, Object>>) param.getOrDefault("insertMyItems", Lists.newArrayList());
		if(!insertMyItems.isEmpty()) {
			for(Map<String,Object> insertMyItem : insertMyItems){
				itemCommonRepository.insertInfoMyItem(insertMyItem);
			}
		}

		List<Map<String,Object>> deleteMyItems = (List<Map<String, Object>>) param.getOrDefault("deleteMyItems", Lists.newArrayList());
		if(!deleteMyItems.isEmpty()) {
			for(Map<String,Object> deleteMyItem : deleteMyItems){
				itemCommonRepository.deleteInfoMyItem(deleteMyItem);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 품목코드가 동일한 것이 있는 지 체크
	 *
	 * @param
	 * @return the void
	 */
	public ResultMap checkItemMasterDuplicateWithResult(Map param) {
		ResultMap resultMap = ResultMap.getInstance();

		Map item = this.checkItemMasterDuplicate(param);
		if((Integer)item.get("cnt") > 0) {
			String resultMsg = messageUtil.getCodeMessage("STD.N2101", item.get("item_cd"), null, LocaleContextHolder.getLocale());
			resultMap.setResultMessage(resultMsg);
			return resultMap.FAIL(resultMap.getResultMessage());
		}

		return ResultMap.SUCCESS(item);
	}

	/**
	 * 품목코드가 동일한 것이 있는 지 체크(cnt, item_cd 조회)
	 *
	 * @param
	 * @return the void
	 */
	public Map<String, Object> checkItemMasterDuplicate(Map param) {
		return itemCommonRepository.checkItemMasterDuplicate(param);
	}

	/**
	 * 품목 and 품목 운영조직 정보 update
	 *
	 * @param
	 * @return the void
	 */
	public void updateItemWithOorg(Map<String, Object> itemInfo, List<Map<String, Object>> oorgList) {
		this.updateItem(itemInfo);
		this.updateItemOorg(itemInfo, oorgList);
	}

	/**
	 * 품목 마스터 update
	 *
	 * @param
	 * @return the void
	 */
	public void updateItem(Map<String, Object> param) {
		itemCommonRepository.updateItem(param);
	}

	/**
	 * 품목 운영조직 정보 등록 요청 update
	 *
	 * @param
	 * @return the void
	 */
	public void updateItemOorg(Map<String, Object> itemInfo, List<Map<String, Object>> oorgList) {
		this.deleteAllItemOorg(itemInfo);
		this.insertItemOorg(itemInfo, oorgList);
	}

	/**
	 * 품목 운영조직 등록 요청 삭제 (전체)
	 *
	 * @param
	 * @return the void
	 */
	public void deleteAllItemOorg(Map<String, Object> param) {
		itemCommonRepository.deleteAllItemOorg(param);
	}

	/**
	 * 품목 and 품목 운영조직 정보 insert
	 *
	 * @param
	 * @return the void
	 */
	public void insertItemWithOorg(Map<String, Object> itemInfo, List<Map<String, Object>> oorgList) {
		this.insertItem(itemInfo);
		this.insertItemOorg(itemInfo, oorgList);
	}

	/**
	 * 품목 정보 insert
	 *
	 * @param
	 * @return the void
	 */
	public void insertItem(Map<String, Object> param) {
		itemCommonRepository.insertItem(param);
	}

	/**
	 * 품목 운영조직 정보 insert
	 *
	 * @param
	 * @return the void
	 */
	public void insertItemOorg(Map<String, Object> param, List<Map<String, Object>> oorgList) {
		Object itemCd = param.get("item_cd");

		for(Map<String, Object> oorgInfo : oorgList) {
			oorgInfo.put("item_cd", itemCd);
			itemCommonRepository.insertItemOorg(oorgInfo);
		}
	}

	/**
	 * 품목 이력정보 insert
	 *
	 * @param
	 * @return the void
	 */
	public void insertItemHistrecWithSeq(Map<String, Object> param) {
		param.put("mod_seqno", this.getMaxItemSeq(param));
		//품목 변경 이력을 저장
		this.insertItemHistrec(param);
	}

	/**
	 * 품목 이력정보 max seq 채번
	 *
	 * @param
	 * @return the void
	 */
	public int getMaxItemSeq(Map<String, Object> param) {
		return itemCommonRepository.getMaxItemSeq(param);
	}

	/**
	 * 품목 이력정보 insert
	 *
	 * @param
	 * @return the void
	 */
	public void insertItemHistrec(Map<String, Object> param) {
		itemCommonRepository.insertItemHistrec(param);
	}

	/**
	 * Item-doctor 연동 유사도 조회
	 *
	 * @param
	 * @return the resultmap
	 */
	public ResultMap findListItemSimilarityFromMaster(Map param) {
		ModuleManager moduleManager = new ModuleManager();
		List<Map<String, Object>> resultList = Lists.newArrayList();
		if(moduleManager.exist(itemDoctorModule)) {
			resultList = itemCommonEventPublisher.findListItemSimilarityFromMaster(param);
		} else {
			return ResultMap.FAIL();
		}

		return ResultMap.SUCCESS(resultList);
	}

	/**
	 * 초기 운영조직 리스트 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListInitItemOorg(Map<String, Object> param) {
		List<Map<String, Object>> oorgList = sharedService.findListOperationOrganizationByUser("IO");
		for(Map<String, Object> oorgInfo : oorgList) {
			oorgInfo.put("use_yn", "N");
			oorgInfo.put("tl_yn", "N");
			oorgInfo.put("qta_yn", "N");
		}
		return oorgList;
	}

	/**
	 * 품목 운영조직 db 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListItemOorg(Map<String, Object> param) {
		return itemCommonRepository.findListItemOorg(param);
	}
	
	public Map<String, Object> findItemOorgInfo(Map<String, Object> param) {
		if(param == null) {
			return null;
		}
		if(!param.containsKey("item_cd")) {
			return null;
		}
		if(!param.containsKey("oorg_cd")) {
			return null;
		}
		return itemCommonRepository.findItemOorgInfo(param);
	}

	/**
	 * 품목 운영조직 조합 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> getListItemOorg(Map<String, Object> param) {
		List<Map<String, Object>> oorgList = this.findListInitItemOorg(param);
		List<Map<String, Object>> itemOorgList = this.findListItemOorg(param);

		Object itemCd = param.get("item_cd");
		ModuleManager moduleManager = new ModuleManager();
		if(moduleManager.exist(proModule) && !Objects.isNull(itemCd)) {
			List<Map<String, Object>> bpaMultVdList = this.findListBpaMultVdByItemCdFromMaster(param);
			if(!bpaMultVdList.isEmpty()) {
				itemOorgList = this.mergeBpaMultVdMasterList(itemOorgList, bpaMultVdList);
			}
		}

		return this.mergeTlQtaMasterList(oorgList, itemOorgList);
	}

	/**
	 * 품목 운영조직 bpa, 멀티협력사 여부 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListBpaMultVdByItemCdFromMaster(Map<String, Object> param) {
		return itemCommonEventPublisher.findListBpaYnAndMutlVdYnByItemCdFromMaster(param);
	}

	/**
	 * bpa, 멀티협력사 조합
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> mergeBpaMultVdMasterList(List<Map<String, Object>> aList, List<Map<String, Object>> bList) {
		for(Map<String, Object> aInfo : aList) {
			for(Map<String, Object> bInfo : bList) {
				if(aInfo.get("oorg_cd").equals(bInfo.get("oorg_cd"))) {
					aInfo.put("bpa_yn", bInfo.get("bpa_yn"));
					aInfo.put("mult_vd_yn", bInfo.get("mult_vd_yn"));
					aInfo.put("qta_subj_yn", bInfo.get("qta_subj_yn"));
				}
			}
		}

		return aList;
	}

	/**
	 * 품목 운영조직 조합
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> mergeTlQtaMasterList(List<Map<String, Object>> aList, List<Map<String, Object>> bList) {
		for(Map<String, Object> aInfo : aList) {
			for(Map<String, Object> bInfo : bList) {
				if(aInfo.get("oorg_cd").equals(bInfo.get("oorg_cd"))) {
					aInfo.put("use_yn", "Y");
					aInfo.put("tl_yn", bInfo.get("tl_yn"));
					aInfo.put("qta_yn", bInfo.get("qta_yn"));
					aInfo.put("bpa_yn", bInfo.get("bpa_yn"));
					aInfo.put("mult_vd_yn", bInfo.get("mult_vd_yn"));
					aInfo.put("qta_subj_yn", bInfo.get("qta_subj_yn"));
				}
			}
		}

		return aList;
	}

	/**
	 * 품목 운영조직 조합 조회(품목 현황에서만 사용)
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> getListItemOorgOnly(Map<String, Object> param) {
		List<Map<String, Object>> itemOorgList = this.findListItemOorg(param);

		Object itemCd = param.get("item_cd");
		ModuleManager moduleManager = new ModuleManager();
		if(moduleManager.exist(proModule) && !Objects.isNull(itemCd)) {
			List<Map<String, Object>> bpaMultVdList = this.findListBpaMultVdByItemCdFromMaster(param);
			if(!bpaMultVdList.isEmpty()) {
				itemOorgList = this.mergeBpaMultVdMasterList(itemOorgList, bpaMultVdList);
			}
		}

		return itemOorgList;
	}
}
