package smartsuite.app.bp.cms.attrgrp.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemAttributeGroupRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "item-attribute-group.";

	public FloaterStream findListAttributeGroup(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListAttributeGroup", param);
	}

	public void deleteItemCategoryItemAttributeByAttributeGroup(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteItemCategoryItemAttributeByAttributeGroup", param);
	}

	public void deleteInfoItemAttributeGrouping(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteInfoItemAttributeGrouping", param);
	}

	public void deleteInfoItemAttributeGroup(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteInfoItemAttributeGroup", param);
	}

	public Map<String, Object>findInfoAttributeGroup(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findInfoAttributeGroup", param);
	}

	public void insertInfoItemAttributeGroup(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertInfoItemAttributeGroup", param);
	}

	public void updateInfoItemAttributeGroup(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateInfoItemAttributeGroup", param);
	}

	public FloaterStream findListAssignedAttributeGroup(Map<String, Object> param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListAssignedAttributeGroup", param);
	}

	public void deleteInfoItemCategoryAttribute(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteInfoItemCategoryAttribute", param);
	}

	public void deleteInfoItemCategoryGrouping(Map<String, Object> param) {
		sqlSession.delete(NAMESPACE + "deleteInfoItemCategoryGrouping", param);
	}

	public void updateItemAttributeGrouping(Map<String, Object> param) {
		sqlSession.update(NAMESPACE + "updateItemAttributeGrouping", param);
	}

	public void insertItemAttributeGrouping(Map<String, Object> param) {
		sqlSession.insert(NAMESPACE + "insertItemAttributeGrouping", param);
	}

	public List<Map<String,Object>> findComboAttrGrpAsgn(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findComboAttrGrpAsgn", param);
	}

	public List<Map<String,Object>> findListAttrGrpAsgn(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListAttrGrpAsgn", param);
	}
}
