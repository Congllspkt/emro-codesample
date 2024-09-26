package smartsuite.app.sp.rfx.auction.validator;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.auction.repository.SpAuctionBidItemRepository;
import smartsuite.app.sp.rfx.auction.repository.SpAuctionBidRepository;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class SpAuctionValidator {
	
	@Inject
	SpAuctionBidRepository spAuctionBidRepository;
	
	@Inject
	SpAuctionBidItemRepository spAuctionBidItemRepository;

	public ResultMap saveAuctionBid(Map auctionBid) {
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

	public ResultMap submitAuctionBid(Map auctionBid, Map rfxBidData, List<Map> rfxBidItems) {
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
		// 견적요청 진행상태 '진행중'인 경우 최저 금액 입찰 아닌 경우
		if("PRGSG".equals(rfxProgSts)) {
			// 견적 유형
			String vdStlTyp = (String) auctionBid.get("item_slctn_typ_ccd");
			
			// 총액별 시 최저 견적금액 조회
			if("BYTOT".equals(vdStlTyp)) {
				Map checkResult = spAuctionBidRepository.findMinBidAmt(auctionBid);
				if(checkResult.get("min_qta_amt") != null) {
					BigDecimal minBidAmt = (BigDecimal) checkResult.get("min_qta_amt");
					BigDecimal bidAmt = new BigDecimal((String) rfxBidData.get("rfx_bid_amt"));
					
					if(minBidAmt.compareTo(BigDecimal.ZERO) > 0 && minBidAmt.compareTo(bidAmt) <= 0) {
						return ResultMap.builder()
										.resultStatus(RfxConst.AUCTION_AMT_CHECK_ERR)
										.build();
					}
				}
			}
			
			// 품목별 시 품목별 최저 견적금액 조회
			if("BYITEM".equals(vdStlTyp)) {
				List<Map> checkResultList = spAuctionBidItemRepository.findListMinBidItemAmt(auctionBid);
				
				Map checkResultMap = Maps.newHashMap();
				for(Map checkResult : checkResultList) {
					checkResultMap.put(checkResult.get("rfx_item_uuid"), checkResult);
				}
				
				for(Map auctionBidItem : rfxBidItems) {
					Map checkResult = (Map) checkResultMap.get((String) auctionBidItem.get("rfx_item_uuid"));
					if(checkResult != null) {
						BigDecimal minItemAmt = (BigDecimal) checkResult.get("min_item_amt");
						BigDecimal itemAmt = new BigDecimal((String) auctionBidItem.get("rfx_item_subm_amt"));
						
						if(minItemAmt.compareTo(itemAmt) <= 0) {
							return ResultMap.builder()
											.resultStatus(RfxConst.AUCTION_AMT_CHECK_ERR)
											.build();
						}
					}
				}
			}
		}
		// '견적 포기'건인 경우
		if("GUP".equals(progSts)) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_REJECT)
							.build();
		}
		
		return ResultMap.SUCCESS();
	}

	public ResultMap abandonAuctionBid(Map auctionBid) {
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
