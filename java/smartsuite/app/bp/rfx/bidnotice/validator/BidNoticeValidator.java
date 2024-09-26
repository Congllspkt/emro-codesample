package smartsuite.app.bp.rfx.bidnotice.validator;

import java.util.Map;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.rfx.bidnotice.repository.BidNoticeRepository;
import smartsuite.app.common.shared.ResultMap;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class BidNoticeValidator {
	
	@Inject
	BidNoticeRepository bidNoticeRepository;

	public ResultMap validate(Map param) {
		String notiId = (String) param.get("rfx_prentc_uuid");
		
		// 신규 생성 시 통과
		if(Strings.isNullOrEmpty(notiId)) {
			return ResultMap.SKIP();
		}

		List checkList = bidNoticeRepository.findListPreBidNoti(param);
		if(checkList.size() > 0){
			return ResultMap.DUPLICATED();
		}
		
		// UPDATE or DELETE 시 : 화면에서 조회한 시점의 진행상태값과 현재 DB에서의 진행상태값을 비교
		Map checkResult = bidNoticeRepository.compareBidNotiHdSts(param);
		return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
	}
}
