package smartsuite.app.bp.pro.qta.validator;

import com.google.common.base.Strings;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.shared.validator.ProValidator;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

@Service
public class QtaValidator extends ProValidator {

	private static final String NAMESPACE = "qta.";

	@Inject
    SqlSession sqlSession;

    @Override
    public ResultMap validate(Map<String, Object> param) {
        String qtaUuid = (String) param.get("qta_uuid");

		if(!Strings.isNullOrEmpty(qtaUuid)) {
			//QTA header만체크
			// 화면 qta 상태 체크
			Map checkResult = sqlSession.selectOne(NAMESPACE + "compareQtaHdSts", param);
			return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
		} else {
			return ResultMap.SUCCESS();
		}
    }
}
