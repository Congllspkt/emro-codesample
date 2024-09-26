package smartsuite.app.sp.rfx.bidnotice.validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.bidnotice.repository.SpBidNoticeRepository;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpBidNoticeValidator {
	
	@Inject
	SpBidNoticeRepository spBidNoticeRepository;

	public ResultMap validateNotiEnd(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map notiStsInfo = spBidNoticeRepository.checkSpNotiPartSts(param);
		if(notiStsInfo == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(!"PRENTC_PRGSG".equals(notiStsInfo.get("rfx_prentc_sts_ccd"))) {
			return ResultMap.INVALID();
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap validateNotiEndTime(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map notiTimeInfo = spBidNoticeRepository.checkSpNotiPartTime(param);
		if(notiTimeInfo == null) {
			return ResultMap.NOT_EXISTS();
		}
		if("N".equals(notiTimeInfo.get("submit_rfx_bid_efct_yn"))) {
			return ResultMap.INVALID();
		}
		
		return ResultMap.SUCCESS();
	}
}
