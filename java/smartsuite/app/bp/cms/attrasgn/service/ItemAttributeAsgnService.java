package smartsuite.app.bp.cms.attrasgn.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.cms.attrasgn.repository.ItemAttributeAsgnRepository;
import smartsuite.app.bp.cms.cmsCommon.service.CmsCommonService;
import smartsuite.app.bp.itemMaster.itemcat.service.ItemCatService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * 속성 배정 관련 service
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemAttributeAsgnService {
	@Inject
	ItemAttributeAsgnRepository itemAttributeAsgnRepository;

	@Inject
	CmsCommonService cmsCommonService;

	@Inject
	ItemCatService itemCatService;

	/**
	 * 속성배정항목 목록 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListAsgnAttrMapping(Map<String, Object> param) {
		return itemAttributeAsgnRepository.findListAsgnAttrMapping(param);
	}

	/**
	 * 배정된 속성 항목을 저장
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap saveInfoAsgnAttr(Map<String, Object> param) {
		Map<String, Object> itemCatInfo = param.get("itemCatInfo") == null ? Maps.newHashMap() : (Map<String, Object>) param.get("itemCatInfo");
		// 하위 품목분류 코드 리스트
		List<String> childrenCateCds = this.findListChildrenItemCatCd((String)itemCatInfo.get("itemcat_cd"));

		//수정된 배정 속성 항목 저장
		List<Map<String, Object>> updateList = param.get("updateAsgnAttrList") == null? Lists.newArrayList() : (List<Map<String, Object>>)param.get("updateAsgnAttrList");
		this.updateInfoAsgnAttr(updateList, childrenCateCds);

		//추가된 배정 속성 항목 저장
		List<Map<String, Object>> insertList = param.get("insertAsgnAttrList") == null? Lists.newArrayList() : (List<Map<String, Object>>)param.get("insertAsgnAttrList");
		this.insertInfoAsgnAttr(insertList, itemCatInfo, childrenCateCds);

		//history 저장
		List<Map<String, Object>> historyList = param.get("histList") == null? Lists.newArrayList() : (List<Map<String, Object>>)param.get("histList");
		itemCatService.insertListItemCatHistRec(historyList);

		// 속성 그룹 배정 속성, 사용 분류 집계
		cmsCommonService.updateCountListItemAttributeGroup(insertList);

		return ResultMap.SUCCESS();
	}

	/**
	 * 수정된 배정 속성 항목을 저장한다.(자식 노드에도)
	 *
	 * @param
	 * @return the list
	 */
	public void updateInfoAsgnAttr(List<Map<String, Object>> updateList, List<String> childrenCateCds) {
		for(Map<String, Object> row : updateList){
			this.updateInfoItemcatIattr(row);

			if(!childrenCateCds.isEmpty()) {
				row.put("children_itemcat_cds", childrenCateCds);
				//자식 배정속성 반영
				this.updateInfoItemcatIattrToChildren(row);
			}
		}
	}

	/**
	 * 하위 itemcat_cd 리스트를 조회한다.
	 *
	 * @param
	 * @return the list
	 */
	public List<String> findListChildrenItemCatCd(String param) {
		return itemAttributeAsgnRepository.findListChildrenItemCatCd(param);
	}

	/**
	 * 배정 속성 테이블 수정
	 *
	 * @param
	 * @return void
	 */
	public void updateInfoItemcatIattr(Map<String, Object> param) {
		itemAttributeAsgnRepository.updateInfoItemcatIattr(param);
	}

	/**
	 * 배정 속성 테이블 수정(자식 노드)
	 *
	 * @param
	 * @return void
	 */
	public void updateInfoItemcatIattrToChildren(Map<String, Object> param) {
		itemAttributeAsgnRepository.updateInfoItemcatIattrToChildren(param);
	}

	public void insertInfoAsgnAttr(List<Map<String, Object>> insertList, Map<String, Object> itemCatInfo, List<String> childrenCateCds) {
		for(Map<String, Object>row: insertList){
			row.put("itemcat_cd", itemCatInfo.get("itemcat_cd") );
			row.put("itemcat_lvl", itemCatInfo.get("itemcat_lvl") );
			row.put("ih_itemcat_lvl", itemCatInfo.get("itemcat_lvl") );

			Map<String, Object> asgnAttrInfo = this.findInfoAsgnAttr(row);

			if(asgnAttrInfo == null) {
				this.insertInfoItemcatIattr(row);
			} else {
				this.updateInfoItemcatIattr(row);
			}

			if(!childrenCateCds.isEmpty()) {
				row.put("children_cate_cds", childrenCateCds);
				//자식 배정속성 반영
				this.mergeInfoItemcatIattrChildren(row);
			}
		}
	}

	/**
	 * 배정 속성 조회 단건
	 *
	 * @param
	 * @return the map
	 */
	public Map findInfoAsgnAttr(Map<String, Object> param) {
		return itemAttributeAsgnRepository.findInfoAsgnAttr(param);
	}

	/**
	 * 배정 속성 테이블 insert
	 *
	 * @param
	 * @return void
	 */
	public void insertInfoItemcatIattr(Map<String, Object> param) {
		itemAttributeAsgnRepository.insertInfoItemcatIattr(param);
	}

	/**
	 * Merge esmtrcm children.
	 *
	 * @param item the item
	 * @return the int
	 */
	public void mergeInfoItemcatIattrChildren(Map<String, Object> param){
		List<Map<String, Object>> childrenList = this.findListItemcatIattrChildren(param);

		if(0 < childrenList.size()) {
			this.updateInfoItemcatIattrChildren(param);
		} else {
			this.insertInfoItemcatIattrChildren(param);
		}
	}

	/**
	 * 자식 노드의 품목분류 속성을 조회
	 *
	 * @param
	 * @return the list
	 */
	public List findListItemcatIattrChildren(Map<String, Object> param) {
		return itemAttributeAsgnRepository.findListItemcatIattrChildren(param);
	}

	/**
	 * 자식 노드의 품목분류 속성을 수정
	 *
	 * @param
	 * @return void
	 */
	public void updateInfoItemcatIattrChildren(Map<String, Object> param) {
		itemAttributeAsgnRepository.updateInfoItemcatIattrChildren(param);
	}

	/**
	 * 자식 노드의 품목분류 속성을 저장
	 *
	 * @param
	 * @return void
	 */
	public void insertInfoItemcatIattrChildren(Map<String, Object> param) {
		itemAttributeAsgnRepository.insertInfoItemcatIattrChildren(param);
	}

	/**
	 * 배정된 속성 항목을 삭제
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap deleteListAsgnAttr(Map<String, Object> param){

		Map<String, Object> itemCatInfo = (Map<String, Object>)param.get("itemCatInfo");
		List<Map<String, Object>> deleteList = (List<Map<String, Object>>)param.get("deleteList");

		// 하위 품목분류 코드 리스트
		List<String> childrenCateCds = this.findListChildrenItemCatCd((String)itemCatInfo.get("itemcat_cd"));

		// DELETE
		for(Map row : deleteList){
			if(!childrenCateCds.isEmpty()) {
				// 자식 배정속성 삭제
				row.put("children_itemcat_cds", childrenCateCds);
				this.deleteInfoItemcatIattrChildren(row);
			}
			// 배정속성 삭제
			this.deleteInfoItemcatIattr(row);
		}

		// 속성 그룹 배정 속성, 사용 분류 집계
		cmsCommonService.updateCountListItemAttributeGroup(deleteList);

		// 이력저장
		List<Map<String, Object>> histList = (List<Map<String, Object>>) param.get("histList");
		itemCatService.insertListItemCatHistRec(histList);

		return ResultMap.SUCCESS();
	}

	/**
	 * 자식 분류에도 동일한 배정된 속성 항목이 있으면 삭제(ITEMCAT_IATTR)
	 *
	 * @param
	 * @return the map
	 */
	public void deleteInfoItemcatIattrChildren(Map<String, Object> param) {
		itemAttributeAsgnRepository.deleteInfoItemcatIattrChildren(param);
	}

	/**
	 * 배정된 속성 항목을 삭제(ITEMCAT_IATTR)
	 *
	 * @param
	 * @return the map
	 */
	public void deleteInfoItemcatIattr(Map<String, Object> param) {
		itemAttributeAsgnRepository.deleteInfoItemcatIattr(param);
	}

	/**
	 * EVENT ITEMCAT에서 넘어온 ASGN DELETE
	 *
	 * @param
	 * @return void
	 */
	public void deleteInfoItemcatIattrFromItemcat(Map<String, Object> param) {
		// 분류에 매핑된 배정속성을 조회한다.(ITEMCAT_IATTR)
		List<Map<String, Object>> asgnList = this.findListAttrMapping(param);

		// 넘어온 분류에 관련된  ITEMCAT_IATTR 데이터를 삭제한다.
		this.deleteInfoItemcatIattr(param);
		// cnt 다시 계산
		for(Map<String, Object> row : asgnList) {
			cmsCommonService.updateCountInfoItemAttributeGroup(row);
		}
	}

	/**
	 * 분류에 매핑된 배정속성을 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListAttrMapping(Map<String, Object> param) {
		return itemAttributeAsgnRepository.findListAttrMapping(param);
	}
}
