package smartsuite.app.bp.pro.upcr.validator;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.shared.validator.ProValidator;
import smartsuite.app.bp.pro.upcr.repository.UpcrRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.status.AvailAbleStatus;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class UpcrValidator extends ProValidator {
	
	@Inject
	UpcrRepository prRepository;

	private static final String NAMESPACE = "upcr.";
	
	@Inject
	SqlSession sqlSession;
	
	@Override
	public ResultMap validate(Map param) {
		String upcrId = (String) param.get("upcr_uuid");
		String upcrtemId = (String) param.get("upcr_item_uuid");
		List<String> upcrtemIds = (List<String>) param.get("upcr_item_uuids");
		
		if(upcrtemIds != null && !upcrtemIds.isEmpty()) {
			// 단가계약요청 변경/접수/반송 또는 구매그룹변경 시 : PR 품목건들의 유효상태 확인
			return super.validateByUpcrItems(param);
			
		} else if(!Strings.isNullOrEmpty(upcrtemId)) {
			// 단가구매 전환 시 : 화면에서 조회한 시점의 PR 품목 진행상태값과 현재 DB에서의 진행상태값을 비교
			Map checkResult = sqlSession.selectOne("upcr-item.compareUpcrDtSts", param);
			
			return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
			
		} else if(!Strings.isNullOrEmpty(upcrId)) {
			//Pr header만체크
			// 구매요청 UPDATE or DELETE 시 : 화면에서 조회한 시점의 PR 진행상태값과 현재 DB에서의 진행상태값을 비교
			Map checkResult = sqlSession.selectOne(NAMESPACE + "compareUpcrHdSts", param);
			
			return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
			
		} else {
			return ResultMap.SUCCESS();
		}
	}
	
	public ResultMap validateCreateRfxByUpcr(List<String> upcrItemIds) {
		if(upcrItemIds == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(upcrItemIds.isEmpty()) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map param = Maps.newHashMap();
		param.put("upcr_item_uuids", upcrItemIds);
		param.put("availableStsList", AvailAbleStatus.prCreateRfxPoStatusList());
		
		ResultMap validator = super.validateByUpcrItems(param);
		return validator;
	}
}