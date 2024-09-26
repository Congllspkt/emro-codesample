package smartsuite.app.bp.rfx.auction;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.auction.service.AuctionBidService;
import smartsuite.app.bp.rfx.auction.service.AuctionService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/rfx/auction/**/")
public class AuctionController {

	@Inject
	AuctionService auctionService;
	
	@Inject
	AuctionBidService auctionBidService;
	
	/**
	 * 역경매 진행현황 조회를 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건
	 * @return list : 역경매 목록
	 * @Date : 2016. 5.24
	 * @Method Name : findListAuction
	 */
	@RequestMapping(value="findListAuction.do")
	public @ResponseBody FloaterStream findListAuction(@RequestBody Map param) {
		// 대용량 처리
		return auctionService.findListAuction(param);
	}
	
	/**
	 * 역경매 정보 조회를 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return map : 역경매 정보
	 * @Date : 2016. 5.24
	 * @Method Name : findAuction
	 */
	@RequestMapping(value="findAuction.do")
	public @ResponseBody Map findAuction(@RequestBody Map param) {
		return auctionService.findAuctionInfo(param);
	}
	
	@RequestMapping(value = "findRfxDefaultDataByReqItemIds.do")
	public @ResponseBody Map findRfxDefaultDataByReqItemIds(@RequestBody Map param) {
		return auctionService.findRfxDefaultDataByReqItemIds(param);
	}
	
	/**
	 * 역경매 정보 저장을 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 역경매 정보
	 * @return resultMap : 역경매 저장 후 처리 결과를 담은 map
	 * @Date : 2016. 5.24
	 * @Method Name : saveAuction
	 */
	@RequestMapping(value="saveAuction.do")
	public @ResponseBody ResultMap saveAuction(@RequestBody Map param) {
		return auctionService.saveDraftAuction(param);
	}
	
	/**
	 * 역경매 다중 삭제를 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 삭제할 역경매 목록 {deleteAuctions: 역경매목록}
	 * @return resultMap : 역경매 다중 삭제 후 처리 결과를 담은 map
	 * @Date : 2016. 5.26
	 * @Method Name : deleteListAuction
	 */
	@RequestMapping(value="deleteListAuction.do")
	public @ResponseBody ResultMap deleteListAuction(@RequestBody Map param) {
		return auctionService.deleteListAuction(param);
	}
	
	/**
	 * 역경매 삭제를 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 삭제할 역경매 정보 {rfx_uuid: 아이디}
	 * @return resultMap : 역경매 삭제 후 처리 결과를 담은 map
	 * @Date : 2016. 5.24
	 * @Method Name : deleteAuction
	 */
	@RequestMapping(value="deleteAuction.do")
	public @ResponseBody ResultMap deleteAuction(@RequestBody Map param) {
		return auctionService.deleteAuction(param);
	}
	
	/**
	 * 역경매 즉시 업체전송을 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 업체전송할 역경매 정보 {rfx_uuid: 아이디}
	 * @return resultMap : 역경매 업체전송 후 처리 결과를 담은 map
	 * @Date : 2016. 5.24
	 */
	@RequestMapping(value="directAuctionBid.do")
	public @ResponseBody ResultMap directAuctionBid(@RequestBody Map param) {
		return auctionService.directAuctionBid(param);
	}
	
	/**
	 * 역경매 마감일자 수정을 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 역경매 마감일자 수정 정보 {rfx_uuid: 아이디, rfx_clsg_dttm: 마감 일시}
	 * @return resultMap : 역경매 마감일자 수정 후 처리 결과를 담은 map
	 * @Date : 2016. 5.26
	 * @Method Name : updateAuctionCloseDt
	 */
	@RequestMapping(value="updateAuctionCloseDt.do")
	public @ResponseBody ResultMap updateAuctionCloseDt(@RequestBody Map param) {
		return auctionService.updateAuctionCloseDt(param);
	}
	
	/**
	 * 다중 건의 역경매를 조기 마감 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 강제마감할 역경매 목록 {rfxDatas: 역경매 목록}
	 * @return resultMap : 역경매 강제마감 후 처리 결과를 담은 map
	 * @Date : 2016. 5.26
	 * @Method Name : byPassCloseListAuction
	 */
	@RequestMapping(value="byPassCloseListAuction.do")
	public @ResponseBody ResultMap byPassCloseListAuction(@RequestBody Map param) {
		return auctionService.byPassCloseListAuction(param);
	}
	
	/**
	 * 역경매 결과 정보를 요청한다.
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return map : 역경매 정보
	 * @Date : 2016. 6. 8
	 * @Method Name : findAuctionResult
	 */
	@RequestMapping(value="findAuctionResult.do")
	public @ResponseBody Map findAuctionResult(@RequestBody Map param) {
		return auctionService.findAuction(param);
	}
	
	/**
	 * 역경매 참여 견적 품목 목록을 요청한다. (품목별)
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return list : 역경매 참여 견적 품목 목록
	 * @Date : 2016. 6. 8
	 * @Method Name : findListAllItemAttend
	 */
	@RequestMapping(value="findListAllAuctionItemAttend.do")
	public @ResponseBody List findListAllItemAttend(@RequestBody Map param) {
		return auctionBidService.findListAllItemAttend(param);
	}
	
	/**
	 * 역경매 참여 견적 목록을 요청한다. (업체별)
	 * 
	 * @author kh_lee
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return list : 역경매 참여 견적 목록
	 * @Date : 2016. 6. 8
	 * @Method Name : findListVdAttend
	 */
	@RequestMapping(value="findListAuctionVdAttend.do")
	public @ResponseBody Map findListVdAttend(@RequestBody Map param) {
		return auctionBidService.findListVdAttend(param);
	}
	
	/**
	 * 역경매 업체 선정을 요청한다. (업체별)
	 * 
	 * @author kh_lee
	 * @param param
	 * @return resultMap : 역경매 업체 선정 후 처리 결과를 담은 map
	 * @Date : 2016. 6. 8
	 * @Method Name : selectAuctionAttend
	 */
	@RequestMapping(value="selectAuctionAttend.do")
	public @ResponseBody ResultMap selectAuctionAttend(@RequestBody Map param) {
		return auctionService.selectAuctionAttend(param);
	}
	
	/**
	 * 역경매 업체 선정을 요청한다. (품목별)
	 * 
	 * @author kh_lee
	 * @param param
	 * @return resultMap : 역경매 업체 선정 후 처리 결과를 담은 map
	 * @Date : 2016. 6. 8
	 * @Method Name : selectAuctionItemAttends
	 */
	@RequestMapping(value="selectAuctionItemAttends.do")
	public @ResponseBody ResultMap selectAuctionItemAttends(@RequestBody Map param) {
		return auctionService.selectAuctionItemAttends(param);
	}
	
	/**
	 * 역경매 결과품의 존재여부 확인.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "checkAuctionResultApproval.do")
	public @ResponseBody ResultMap checkAuctionResultApproval(@RequestBody Map param){
		return auctionService.checkAuctionResultApproval(param);
	}
	
	/**
	 * 역경매 업체 선정을 완료 요청한다.
	 * 
	 * @author kh_lee
	 * @param param
	 * @return resultMap : 역경매 업체 선정 완료 후 처리 결과를 담은 map
	 * @Date : 2016. 6. 8
	 * @Method Name : bypassApprovalAuctionResult
	 */
	@RequestMapping(value="bypassApprovalAuctionResult.do")
	public @ResponseBody ResultMap bypassApprovalAuctionResult(@RequestBody Map param) {
		return auctionService.bypassApprovalAuctionResult(param);
	}
	
	/**
	 * 역경매 유찰 요청한다.
	 * 
	 * @author kh_lee
	 * @param param
	 * @return resultMap : 역경매 유찰 후 처리 결과를 담은 map
	 * @Date : 2016. 6. 8
	 * @Method Name : dropAuction
	 */
	@RequestMapping(value="dropAuction.do")
	public @ResponseBody ResultMap dropAuction(@RequestBody Map param) {
		return auctionService.dropAuction(param);
	}
	
	/**
	 * 역경매 취소를 요청한다.
	 * 
	 * @author kh_lee
	 * @param param
	 * @return resultMap : 역경매 취소 후 처리 결과를 담은 map
	 * @Date : 2016.12.01
	 * @Method Name : cancelAuction
	 */
	@RequestMapping(value="cancelAuction.do")
	public @ResponseBody ResultMap cancelAuction(@RequestBody Map param) {
		return auctionService.cancelAuction(param);
	}
	
	/**
	 * 역경매 재견적을 요청한다.
	 * 
	 * @author kh_lee
	 * @param param
	 * @return resultMap : 역경매 재견적 저장 후 처리 결과를 담은 map
	 * @Date : 2016. 6. 9
	 * @Method Name : saveMultiRoundAuction
	 */
	@RequestMapping(value="saveMultiRoundAuction.do")
	public @ResponseBody ResultMap saveMultiRoundAuction(@RequestBody Map param) {
		return auctionService.saveMultiRoundAuction(param);
	}
	
	/**
	 * 역경매 견적서 작성 (업체대행) 상세정보 조회를 요청한다.
	 * 
	 * @author kh_lee
	 * @param param
	 * @return map : 역경매 견적서 작성 (업체대행) 헤더 및 품목 정보
	 * @Date : 2016. 6.17
	 */
	@RequestMapping(value = "findAuctionBidBuyer.do")
	public @ResponseBody Map findAuctionBidInfo(@RequestBody Map param) {
		return auctionBidService.findAuctionBidInfo(param);
	}
	
	/**
	 * 역경매 견적 대상 업체 목록 조회 (제출현황 팝업 조회)
	 * 
	 * @author kh_lee
	 * @param param
	 * @return list
	 * @Date : 2017. 4. 5
	 */
	@RequestMapping(value="findListAuctionTargetVd.do")
	public @ResponseBody List findListAuctionTargetVd(@RequestBody Map param) {
		return auctionBidService.findListAuctionTargetVd(param);
	}
	
	/**
	 * 역경매 견적 제출 업체 목록 조회 (제출현황 팝업 조회)
	 * 
	 * @author kh_lee
	 * @param param
	 * @return list
	 * @Date : 2017. 4. 5
	 */
	@RequestMapping(value="findListAuctionSubmitVd.do")
	public @ResponseBody List findListAuctionSubmitVd(@RequestBody Map param) {
		return auctionBidService.findListAuctionSubmitVd(param);
	}
	
	/**
	 * RFx Ranking 구하기
	 * @param param
	 */
	@RequestMapping(value = "computeRanking.do")
	public @ResponseBody void computeRanking(@RequestBody Map param) {
		auctionBidService.computeRanking(param);
	}

	/**
	 * RFI -> RFX
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findRfxDefaultDataByRfiId.do")
	public @ResponseBody Map findRfxDefaultDataByRfiId(@RequestBody Map param){
		return auctionService.findRfxDefaultDataByRfiId(param);
	}
	
	@RequestMapping(value = "findListAuctionSlctnVd.do")
	public @ResponseBody List findListAuctionSlctnVd(@RequestBody Map param) {
		return auctionService.findListAuctionSlctnVd(param);
	}
	
	@RequestMapping(value = "saveAuctionSlctnNxtPrcs.do")
	public @ResponseBody ResultMap saveAuctionSlctnNxtPrcs(@RequestBody Map param) {
		return auctionService.saveAuctionSlctnNxtPrcs(param);
	}
}