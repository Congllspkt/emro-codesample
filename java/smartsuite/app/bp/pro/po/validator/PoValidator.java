package smartsuite.app.bp.pro.po.validator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.bp.pro.shared.validator.ProValidator;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class PoValidator extends ProValidator {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "po.";
	
	@Override
	public ResultMap validate(Map<String, Object> param) {
		String poId = (param.get("po_uuid") == null) ? null : param.get("po_uuid").toString();
		
		// 신규 생성 시
		if(Strings.isNullOrEmpty(poId)) {
			int poRev = param.get("po_revno") == null ? 1 : Integer.parseInt(param.get("po_revno").toString()); // 발주 차수
			if(poRev == 1) {									// 구매요청 없이 PO 생성하는 경우 validation 수행하지 않음
				return ResultMap.SUCCESS();
			} else {											// 발주변경요청, 발주변경 신규 생성 시 이전 차수에 대한 validation check
				Map<String, Object> checkResult = sqlSession.selectOne(NAMESPACE + "checkPrevPoHdSts", param);
				return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
			}
		} else {
			// UPDATE 시 : 화면에서 조회한 시점의 PO 진행상태값과 현재 DB에서의 진행상태값을 비교
			Map<String, Object> checkResult = sqlSession.selectOne(NAMESPACE + "comparePoHdSts", param);
			return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
		}
	}
}