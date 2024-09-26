package smartsuite.app.bp.rfx.auction.validator;

import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.rfx.auction.repository.AuctionRepository;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class AuctionValidator {

	@Inject
	AuctionRepository auctionRepository;
	
	public ResultMap validate(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		String rfxId = (String) param.get("rfx_uuid");
		if(rfxId == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		// UPDATE 시 : 화면에서 조회한 시점의 역경매 진행상태값과 현재 DB에서의 진행상태값을 비교
		Map checkResult = auctionRepository.compareAuctionHdSts(param);
		return ValidatorUtil.getResultMapByCheckResult(param, checkResult);
	}

	public ResultMap saveAuctionBidForAgent(Map auctionBid) {
		// 견적요청 진행상태
		String rfxProgSts = (String) auctionBid.get("rfx_sts_ccd");
		// 견적서 진행상태
		String progSts = (String) auctionBid.get("rfx_bid_sts_ccd");
		
		// 견적요청 진행상태가 '진행중'이 아닌 경우
		if(!"PRGSG".equals(rfxProgSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_END)
							.build();
		}
		// '견적 포기'건인 경우
		if("GUP".equals(progSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_REJECT)
							.build();
		}
		// '견적 제출'건 존재하는 경우
		if("SUBM".equals(progSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.QTA_SUBMIT)
							.build();
		}
		
		return ResultMap.SUCCESS();
	}

	public ResultMap submitAuctionBidForAgent(Map auctionBid) {
		// 견적요청 진행상태
		String rfxProgSts = (String) auctionBid.get("rfx_sts_ccd");
		// 견적서 진행상태
		String progSts = (String) auctionBid.get("rfx_bid_sts_ccd");
		
		// 견적요청 진행상태가 '진행중'이 아닌 경우
		if(!"PRGSG".equals(rfxProgSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_END)
							.build();
		}
		// '견적 포기'건인 경우
		if("GUP".equals(progSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_REJECT)
							.build();
		}
		
		return ResultMap.SUCCESS();
	}

	public ResultMap abandonAuctionBidForAgent(Map auctionBid) {
		// 견적요청 진행상태
		String rfxProgSts = (String) auctionBid.get("rfx_sts_ccd");
		// 견적서 진행상태
		String progSts = (String) auctionBid.get("rfx_bid_sts_ccd");
		
		// 견적요청 진행상태가 '진행중'이 아닌 경우
		if(!"PRGSG".equals(rfxProgSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_END)
							.build();
		}
		// '견적 포기'건인 경우
		if("GUP".equals(progSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_REJECT)
							.build();
		}
		
		return ResultMap.SUCCESS();
	}

}
