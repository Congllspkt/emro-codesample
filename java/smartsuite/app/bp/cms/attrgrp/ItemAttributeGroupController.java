package smartsuite.app.bp.cms.attrgrp;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.cms.attrgrp.service.ItemAttributeGroupService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

/**
 * 품목 속성 그룹 관련 controller
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/attrgrp/**/")
public class ItemAttributeGroupController {
	
	@Inject
	ItemAttributeGroupService itemAttributeGroupService;
	
	/**
	 * 속성 그룹 목록 조회
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findListAttributeGroup.do")
	public @ResponseBody FloaterStream findListAttributeGroup(@RequestBody Map<String, Object> param) {
		return itemAttributeGroupService.findListAttributeGroup(param);
	}
	
	/**
	 * 속성 그룹 목록 삭제
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "deleteListAttributeGroup.do")
	public @ResponseBody ResultMap deleteListAttributeGroup(@RequestBody Map<String, Object> param) {
		return itemAttributeGroupService.deleteListAttributeGroup(param);
	}
	
	/**
	 * 속성 그룹 상세 조회
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "findInfoAttributeGroup.do")
	public @ResponseBody Map<String, Object>findInfoAttributeGroup(@RequestBody Map<String, Object> param) {
		return itemAttributeGroupService.findInfoAttributeGroup(param);
	}
	
	/**
	 * 속성 그룹 저장
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "saveInfoAttributeGroup.do")
	public @ResponseBody ResultMap saveInfoAttributeGroup(@RequestBody Map<String, Object> param) {
		return itemAttributeGroupService.saveInfoAttributeGroup(param);
	}
	
	/**
	 * 속성 그룹 배정속성 조회
	 *
	 * @param
	 * @return FloaterStream
	 */
	@RequestMapping(value = "findListAssignedAttributeGroup.do")
	public @ResponseBody FloaterStream findListAssignedAttributeGroup(@RequestBody Map<String, Object> param) {
		return itemAttributeGroupService.findListAssignedAttributeGroup(param);
	}

	/**
	 * 속성 그룹 배정속성 항목 삭제
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "deleteAssignedAttributeGroup.do")
	public @ResponseBody ResultMap deleteAssignedAttributeGroup(@RequestBody Map<String, Object> param) {
		return itemAttributeGroupService.deleteAssignedAttributeGroup(param);
	}

	/**
	 * 속성 그룹 배정속성 항목 저장
	 *
	 * @param
	 * @return the map
	 */
	@RequestMapping(value = "saveListAssignedAttributeGroup.do")
	public @ResponseBody ResultMap saveListAssignedAttributeGroup(@RequestBody Map<String, Object> param) {
		return itemAttributeGroupService.saveListAssignedAttributeGroup(param);
	}

	/**
	 * 배정된 속성 그룹 항목 조회(팝업, combobox)
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findComboAttrGrpAsgn.do")
	public @ResponseBody List findComboAttrGrpAsgn(@RequestBody Map<String, Object> param) {
		return itemAttributeGroupService.findComboAttrGrpAsgn(param);
	}
	/**
	 * 배정된 속성 그룹 항목 조회(팝업)
	 *
	 * @param
	 * @return the list
	 */
	@RequestMapping(value = "findListAttrGrpAsgn.do")
	public @ResponseBody List findListAttrGrpAsgn(@RequestBody Map<String, Object> param) {
		return itemAttributeGroupService.findListAttrGrpAsgn(param);
	}
	
}