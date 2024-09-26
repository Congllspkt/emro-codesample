package smartsuite.app.bp.pro.pr.validator;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.pr.repository.PrItemRepository;
import smartsuite.app.bp.pro.pr.repository.PrRepository;
import smartsuite.app.bp.pro.shared.validator.ProValidator;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.AvailAbleStatus;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class PrValidator extends ProValidator {
	
	@Inject
	PrRepository prRepository;

	@Inject
	PrItemRepository prItemRepository;

	@Inject
	SharedService sharedService;

	@Inject
	MessageSource messageSource;


	@Override
	public ResultMap validate(Map param) {
		String prId = (String) param.get("pr_uuid");
		String prItemId = (String) param.get("pr_item_uuid");
		List<String> prItemIds = (List<String>) param.get("pr_item_uuids");
		String curCcd = param.get("cur_ccd") == null ? "" : param.get("cur_ccd").toString();

		if(StringUtils.isNotEmpty(curCcd)) {
			boolean validCur = false;
			List<Map> curCodes = sharedService.findCommonCode("C004");
			for(Map curCode : curCodes) {
				String curCd = (String) curCode.get("data");
				if(curCd.equals(curCcd)) {
					validCur = true;
					break;
				}
			}
			if(!validCur) {
				ResultMap resultMap = ResultMap.getInstance();
				resultMap.setResultData(param);
				resultMap.setResultStatus(ResultMap.STATUS.FAIL);
				resultMap.setResultMessage(messageSource.getMessage("STD.MW0003", new Object[]{curCcd},  LocaleContextHolder.getLocale()));
				return resultMap;
			}else{
				return ResultMap.SUCCESS();
			}
		}else if(prItemIds != null && !prItemIds.isEmpty()) {
			// 구매요청 변경/접수/반송 또는 구매그룹변경 시 : PR 품목건들의 유효상태 확인
			return super.validateByPrItems(param);
			
		} else if(StringUtils.isNotEmpty(prItemId)) {
			// 단가구매 전환 시 : 화면에서 조회한 시점의 PR 품목 진행상태값과 현재 DB에서의 진행상태값을 비교
			Map checkResult =  prItemRepository.comparePrDtSts(param);
			
			return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
			
		} else if(StringUtils.isNotEmpty(prId)) {
			//Pr header만체크
			// 구매요청 UPDATE or DELETE 시 : 화면에서 조회한 시점의 PR 진행상태값과 현재 DB에서의 진행상태값을 비교
			Map checkResult = prRepository.comparePrHdSts(param);

			return ValidatorUtil.getResultMapByCheckResult(param, checkResult);

		} else {
			return ResultMap.SUCCESS();
		}
	}
	
	public ResultMap validateCreateRfxByPr(List<String> prItemIds) {
		if(prItemIds == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(prItemIds.isEmpty()) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map param = Maps.newHashMap();
		param.put("pr_item_uuids", prItemIds);
		param.put("availableStsList", AvailAbleStatus.prCreateRfxPoStatusList());
		
		ResultMap validator = super.validateByPrItems(param);
		return validator;
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