package smartsuite.app.bp.rfx.bidnotice.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.bidnotice.repository.BidNoticeEvalRepository;
import smartsuite.app.bp.rfx.bidnotice.validator.BidNoticeEvalValidator;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class BidNoticeEvalService {
	
	@Inject
	BidNoticeEvalRepository bidNoticeEvalRepository;
	
	@Inject
	BidNoticeService bidNoticeService;

	@Inject
	BidNoticeEvalValidator bidNoticeEvalValidator;

	@Inject
	MailService mailService;
	
	private static final String NOTI_PROG_STS_NO_E = "NOTI_PROG_STS_NO_E";
	
	public FloaterStream findListBidNotiEval(Map param) {
		return bidNoticeEvalRepository.findListBidNotiEval(param);
	}

	public Map findListBidNotiEvalDetail(Map param) {
		Map resultMap = Maps.newHashMap();
		
		Map bidNotiInfo = bidNoticeService.findBidNoti(param);
		List<Map> vdList;
		if("OBID".equals(bidNotiInfo.get("comp_typ_ccd"))) {
			vdList = bidNoticeEvalRepository.findListBidNotiEvalDetailByOpen(param);
		} else {
			vdList = bidNoticeEvalRepository.findListBidNotiEvalDetail(param);
		}
		
		List<Map> attCntList = bidNoticeEvalRepository.findListAttachCnt(param);
		Map attCntMap = Maps.newHashMap();
		for(Map attCnt : attCntList) {
			attCntMap.put(attCnt.get("rfx_prentc_afp_uuid"), attCnt.get("athf_cnt"));
		}
		
		for(Map vdInfo : vdList){
			String notiPartAppId = (String) vdInfo.get("rfx_prentc_afp_uuid");
			if(!Strings.isNullOrEmpty(notiPartAppId)) {
				vdInfo.put("athf_cnt", attCntMap.get(notiPartAppId));
			} else {
				vdInfo.put("athf_cnt", 0);
			}
		}
		
		resultMap.put("bidNotiInfo", bidNotiInfo);
		resultMap.put("vdList", vdList);
		return resultMap;
	}

	public ResultMap disqualBidNotiEval(Map param) {
		Map bidNotiInfo = (Map) param.get("bidNotiInfo");
		
		ResultMap validator = bidNoticeEvalValidator.validate(bidNotiInfo);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		//상태처리
		bidNoticeEvalRepository.disqualBidNotiEval(param);

		return ResultMap.SUCCESS();
	}

	public ResultMap qualBidNotiEval(Map param) {
		Map bidNotiInfo = (Map) param.get("bidNotiInfo");
		
		ResultMap validator = bidNoticeEvalValidator.validate(bidNotiInfo);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		//상태처리
		bidNoticeEvalRepository.qualBidNotiEval(param);

		return ResultMap.SUCCESS();
	}

	public ResultMap completeBidNotiEval(Map param) {
		ResultMap validator = bidNoticeEvalValidator.validate(param);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		Map bidNotiInfo = bidNoticeEvalRepository.findBidNotiInfo(param);
		if(!"CLSG".equals(bidNotiInfo.get("rfx_prentc_sts_ccd"))) {
			return ResultMap.builder()
							.resultStatus(NOTI_PROG_STS_NO_E)
							.build();
		}
		
		int evalYesCnt = bidNoticeEvalRepository.findListEvalVdCnt(param);
		if(evalYesCnt >= 2) {
			//2개이상 적격인경우 심사완료 적합처리
			bidNoticeEvalRepository.updateEvalRstYes(param);
		} else {
			// 2개미만 적격인경우 심사완료 부적합처리
			bidNoticeEvalRepository.updateEvalRstNo(param);
		}
		bidNoticeEvalRepository.updateBidNotiEvalVd(param);

		//비 적격 메일 발송
		mailService.sendAsync("BID_DIS_NOTI_QUAL_EVAL", (String) bidNotiInfo.get("rfx_prentc_uuid"),param);
		// 적격 메일 발송
		mailService.sendAsync("BID_NOTI_QUAL_EVAL", (String) bidNotiInfo.get("rfx_prentc_uuid"),param);

		return ResultMap.SUCCESS();
	}

	public Map findBidNotiPart(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("bidNotiData", bidNoticeEvalRepository.findBidNotiPartInfo(param));
		resultMap.put("attachList", this.findListBidNotiPartAttach(param));
		return resultMap;
	}
	

	public List<Map> findListBidNotiPartAttach(Map param) {
		return bidNoticeEvalRepository.findListBidPartedAttach(param);
	}
}
