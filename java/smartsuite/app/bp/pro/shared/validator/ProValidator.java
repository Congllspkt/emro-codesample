package smartsuite.app.bp.pro.shared.validator;

import smartsuite.app.bp.admin.validator.Validator;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.common.shared.QueryFloaterInSqlSession;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ("unchecked")
public abstract class ProValidator implements Validator {
	
	@Inject
	QueryFloaterInSqlSession sqlSession;

	public abstract ResultMap validate(Map<String, Object> param);

	/**
	 * PR ITEM 복수 건 진행상태 체크
	 * 
	 * @param param {pr_item_uuids, availableStsList or pr_sts_ccd}
	 * @return
	 */
	protected ResultMap validateByPrItems(Map<String, Object> param) {
		List<Map<String, Object>> checkValidYnList = sqlSession.selectList("pr-item.compareListPrDtSts", param, param.get("pr_item_uuids"));
		
		return ValidatorUtil.getResultMapByCheckValidYnList(
				(List<String>)param.get("pr_item_uuids"),
				checkValidYnList,
				"pr_item_uuid",
				ProConst.PR_ITEM_STS_ERR);
	}

	protected ResultMap validateByUpcrItems(Map<String, Object> param) {
		List<Map<String, Object>> checkValidYnList = sqlSession.selectList("upcr-item.compareListUpcrDtSts", param, param.get("upcr_item_uuids"));

		return ValidatorUtil.getResultMapByCheckValidYnList(
				(List<String>)param.get("upcr_item_uuids"),
				checkValidYnList,
				"upcr_item_uuid",
				ProConst.UPCR_ITEM_STS_ERR);
	}
}