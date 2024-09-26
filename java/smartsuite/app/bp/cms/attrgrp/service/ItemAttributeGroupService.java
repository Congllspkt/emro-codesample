package smartsuite.app.bp.cms.attrgrp.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.cms.cmsCommon.service.CmsCommonService;
import smartsuite.app.bp.cms.attrgrp.repository.ItemAttributeGroupRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;

/**
 * 품목 속성 그룹 관련 service
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemAttributeGroupService {
	
	@Inject
	SharedService sharedService;
	
	@Inject
	CmsCommonService cmsCommonService;

	@Inject
	ItemAttributeGroupRepository itemAttributeGroupRepository;

	/**
	 * 속성 그룹 목록 조회
	 *
	 * @param
	 * @return the list
	 */
	public FloaterStream findListAttributeGroup(Map<String, Object> param) {
		return itemAttributeGroupRepository.findListAttributeGroup(param);
	}

	/**
	 * 속성 그룹 목록 삭제
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap deleteListAttributeGroup(Map<String, Object> param){
		List<Map<String, Object>> deleteList = (List<Map<String, Object>>)param.get("deleteList");

		for(Map<String, Object>row : deleteList){
			this.deleteOneItemAttributeGroup(row);
		}
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 속성 그룹 단건 삭제
	 *
	 * @param
	 * @return void
	 */
	private void deleteOneItemAttributeGroup(Map<String, Object> param) {
		// 품목분류속성 mapping 삭제 ( Attribute Group 에 속한 Item Category Item Attribute를 수정한다. )
		this.deleteItemCategoryItemAttributeByAttributeGroup(param);
		// 속성그룹 - 품목속성그룹속성 mapping 삭제
		this.deleteInfoItemAttributeGrouping(param);
		// 품목속성그룹 삭제
		this.deleteInfoItemAttributeGroup(param);
	}

	/**
	 * 속성그룹코드와 연관된 품목분류 속성 매핑 삭제
	 *
	 * @param
	 * @return void
	 */
	public void deleteItemCategoryItemAttributeByAttributeGroup(Map<String, Object> param){
		itemAttributeGroupRepository.deleteItemCategoryItemAttributeByAttributeGroup(param);
	}

	/**
	 * 품목그룹 속성 항목을 삭제한다.(by iattr_grp_cd, 속성그룹삭제시 배정된 속성도 같이 삭제한다.)
	 *
	 * @param
	 * @return void
	 */
	public void deleteInfoItemAttributeGrouping(Map<String, Object> param) {
		itemAttributeGroupRepository.deleteInfoItemAttributeGrouping(param);
	}

	/**
	 * 품목속성그룹 삭제한다.
	 *
	 * @param
	 * @return void
	 */
	public void deleteInfoItemAttributeGroup(Map<String, Object> item){
		itemAttributeGroupRepository.deleteInfoItemAttributeGroup(item);
	}

	/**
	 * 속성 그룹 정보 단건 조회
	 *
	 * @param
	 * @return the map
	 */
	public Map<String, Object>findInfoAttributeGroup(Map<String, Object> param) {
		return itemAttributeGroupRepository.findInfoAttributeGroup(param);
	}

	/**
	 * 속성 그룹 정보 저장
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap saveInfoAttributeGroup(Map<String, Object> param){
		ResultMap resultMap = ResultMap.getInstance();

		Map<String, Object> info = (Map<String, Object>)param.get("attrGrpInfo");
		boolean isNew = info.get("is_new") == null ? false : (boolean) info.get("is_new");

		if(isNew){
			info.put("iattr_grp_cd", sharedService.generateDocumentNumber("AG"));
			this.insertInfoItemAttributeGroup(info);
		}else{
			this.updateInfoItemAttributeGroup(info);
		}

		resultMap.setResultData(info);
		return ResultMap.SUCCESS(resultMap.getResultData());
	}

	/**
	 * 속성 그룹 추가
	 *
	 * @param
	 * @return void
	 */
	public void insertInfoItemAttributeGroup(Map<String, Object> item){
		itemAttributeGroupRepository.insertInfoItemAttributeGroup(item);
	}

	/**
	 * 속성 그룹 수정
	 *
	 * @param
	 * @return void
	 */
	public void updateInfoItemAttributeGroup(Map<String, Object> item){
		itemAttributeGroupRepository.updateInfoItemAttributeGroup(item);
	}

	/**
	 * 속성그룹 배정속성 항목 리스트를 조회
	 *
	 * @param
	 * @return FloaterStream
	 */
	public FloaterStream findListAssignedAttributeGroup(Map<String, Object> param) {
		return itemAttributeGroupRepository.findListAssignedAttributeGroup(param);
	}
	
	/**
	 * 속성그룹 배정속성 항목 삭제
	 *
	 * @param
	 * @return the mpa
	 */
	public ResultMap deleteAssignedAttributeGroup(Map<String, Object> param){
		ResultMap resultMap = ResultMap.getInstance();

		List<Map<String, Object>> deleteList = param.get("deleteList") == null ? Lists.newArrayList() : (List<Map<String, Object>>)param.get("deleteList");

		for(Map<String, Object> row : deleteList){
			// 품목분류 배정속성 삭제
			this.deleteInfoItemCategoryAttribute(row);
			this.deleteInfoItemCategoryGrouping(row);
		}

		// 속성 그룹 배정 속성, 사용 분류 집계
		cmsCommonService.updateCountListItemAttributeGroup(deleteList);

		return ResultMap.SUCCESS();
	}

	/**
	 * 품목 분류 품목속성 삭제
	 *
	 * @param
	 * @return void
	 */
	public void deleteInfoItemCategoryAttribute(Map<String, Object> param){
		itemAttributeGroupRepository.deleteInfoItemCategoryAttribute(param);
	}

	/**
	 * 품목 분류 그룹핑 삭제
	 *
	 * @param
	 * @return void
	 */
	public void deleteInfoItemCategoryGrouping(Map<String, Object> param){
		itemAttributeGroupRepository.deleteInfoItemCategoryGrouping(param);
	}

	/**
	 * 속성그룹 배정속성 항목 저장
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap saveListAssignedAttributeGroup(Map<String, Object> param){
		List<Map<String, Object>> updateList = param.get("updateList") == null ? Lists.newArrayList() : (List<Map<String, Object>>)param.get("updateList");
		List<Map<String, Object>> insertList = param.get("insertList") == null ? Lists.newArrayList() : (List<Map<String, Object>>)param.get("insertList");
		
		this.updateListItemAttributeGrouping(updateList);
		this.insertListItemAttributeGrouping(insertList);
		
		// 속성 그룹 배정 속성, 사용 분류 집계
		cmsCommonService.updateCountListItemAttributeGroup(insertList);

		return ResultMap.SUCCESS();
	}

	/**
	 * 속성그룹 배정속성 항목 저장
	 *
	 * @param
	 * @return void
	 */
	private void insertListItemAttributeGrouping(List<Map<String, Object>> insertList) {
		for(Map<String, Object> row : insertList){
			this.insertItemAttributeGrouping(row);
		}
	}

	/**
	 * IATTR_GRPG(품목 속성 그룹 속성 Mapping).
	 *
	 * @param
	 * @return void
	 */
	public void insertItemAttributeGrouping(Map<String, Object> param){
		itemAttributeGroupRepository.insertItemAttributeGrouping(param);
	}

	/**
	 * 속성그룹 배정속성 항목 수정
	 *
	 * @param
	 * @return void
	 */
	private void updateListItemAttributeGrouping(List<Map<String, Object>> updateList) {
		for(Map<String, Object> row : updateList){
			this.updateItemAttributeGrouping(row);
		}
	}

	/**
	 * IATTR_GRPG(품목 속성 그룹 속성 Mapping).
	 *
	 * @param
	 * @return void
	 */
	public void updateItemAttributeGrouping(Map<String, Object> param){
		itemAttributeGroupRepository.updateItemAttributeGrouping(param);
	}

	/**
	 * 배정된 속성 그룹 항목 조회(팝업, 콤보박스)
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findComboAttrGrpAsgn(Map<String, Object> param) {
		return itemAttributeGroupRepository.findComboAttrGrpAsgn(param);
	}

	/**
	 * 배정된 속성 그룹 항목 조회(팝업)
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String, Object>> findListAttrGrpAsgn(Map<String, Object> param) {
		return itemAttributeGroupRepository.findListAttrGrpAsgn(param);
	}
}
