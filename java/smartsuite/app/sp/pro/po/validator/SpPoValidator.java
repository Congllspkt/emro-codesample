package smartsuite.app.sp.pro.po.validator;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.bp.pro.shared.validator.ProValidator;
import smartsuite.app.common.shared.ResultMap;

@SuppressWarnings("unchecked")
@Service
public class SpPoValidator extends ProValidator {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-po.";
	
	@Override
	public ResultMap validate(Map<String, Object> param) {
		if(param.get("po_uuids") != null && !((List<String>)param.get("po_uuids")).isEmpty()) {
			// 발주 접수/거부 복수건 처리 시 유효한 상태 체크
			List<Map<String, Object>> checkValidYnList = sqlSession.selectList(NAMESPACE + "compareListPoHdVdSts", param);
			return ValidatorUtil.getResultMapByCheckValidYnList((List<String>)param.get("po_uuids"), checkValidYnList, "po_uuid", ProConst.PO_STATE_ERR);
		} else {
			// 화면에서 조회한 시점의 발주 진행상태값과 현재 DB에서의 진행상태값을 비교
			Map<String, Object> checkResult = sqlSession.selectOne(NAMESPACE + "comparePoHdVdSts", param);
			return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
		}
	}
}