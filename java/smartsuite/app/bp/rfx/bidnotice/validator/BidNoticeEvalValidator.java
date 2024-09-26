package smartsuite.app.bp.rfx.bidnotice.validator;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.rfx.bidnotice.repository.BidNoticeEvalRepository;
import smartsuite.app.common.shared.ResultMap;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class BidNoticeEvalValidator {

	@Inject
	BidNoticeEvalRepository bidNoticeEvalRepository;
	
	public ResultMap validate(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		// UPDATE or DELETE 시 : 화면에서 조회한 시점의 진행상태값과 현재 DB에서의 진행상태값을 비교
		Map checkResult = bidNoticeEvalRepository.compareBidNotiHdStsByEval(param);
		return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
	}
}
