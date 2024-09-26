package smartsuite.app.sp.rfx.auction;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.auction.service.SpAuctionBidService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/sp/rfx/auction/**/")
public class SpAuctionBidController {

	/** The auction vendor service. */
	@Inject
	SpAuctionBidService spAuctionBidService;
	
	/**
	 * 역경매 현황 조회를 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건
	 * @return list : 역경매 현황 목록
	 * @Date : 2016. 6. 1
	 * @Method Name : findListAuctionVd
	 */
	@RequestMapping(value="findListAuctionVd.do")
	public @ResponseBody FloaterStream findListAuctionVd(@RequestBody Map param) {
		// 대용량 처리
		return spAuctionBidService.findListAuctionVd(param);
	}
	
	/**
	 * 역경매 정보 조회를 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return map : 역경매 정보
	 * @Date : 2016. 6. 1
	 */
	@RequestMapping(value="findAuctionBid.do")
	public @ResponseBody Map findAuctionBidInfo(@RequestBody Map param) {
		return spAuctionBidService.findAuctionBidInfo(param);
	}
	
	/**
	 * 역경매 견적 정보 저장을 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 역경매 견적 정보
	 * @return resultMap : 역경매 견적 정보 저장 후 처리 결과를 담은 map
	 * @Date : 2016. 6. 1
	 */
	@RequestMapping(value="saveAuctionBid.do")
	public @ResponseBody ResultMap saveAuctionBid(@RequestBody Map param) {
		return spAuctionBidService.temporarySaveAuctionBid(param);
	}
	
	/**
	 * 역경매 견적 정보 제출을 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 역경매 견적 정보
	 * @return resultMap : 역경매 견적 정보 제출 후 처리 결과를 담은 map
	 * @Date : 2016. 6. 1
	 */
	@RequestMapping(value="submitAuctionBid.do")
	public @ResponseBody ResultMap submitAuctionBid(@RequestBody Map param) {
		return spAuctionBidService.submitAuctionBid(param);
	}
	
	/**
	 * 역경매 견적 포기를 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 역경매 견적 정보 {rfx_uuid : 역경매 아이디, rfx_bid_uuid : 역경매 견적 아이디}
	 * @return resultMap : 역경매 견적 포기 후 처리 결과를 담은 map
	 * @Date : 2016. 6. 1
	 */
	@RequestMapping(value="abandonAuctionBid.do")
	public @ResponseBody ResultMap abandonAuctionBid(@RequestBody Map param) {
		return spAuctionBidService.abandonAuctionBid(param);
	}
}