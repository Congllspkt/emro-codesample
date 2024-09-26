package smartsuite.app.bp.rfx.bidnotice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.bidnotice.service.BidNoticeEvalService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * BidNotiEval 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @since 2021. 5. 27
 * @FileName BidNotiEvalController.java
 * @package smartsuite.app.bp.rfx.bidnotice
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/rfx/bidnotice/**/")
public class BidNoticeEvalController {

	/** The bid noti eval service. */
	@Inject
	BidNoticeEvalService bidNoticeEvalService;
	
	/**
	 * Find list bid noti eval.
	 *
	 * @param param the param
	 * @return the floater stream
	 */
	@RequestMapping(value = "findListBidNotiEval.do")
	public @ResponseBody FloaterStream findListBidNotiEval(@RequestBody Map param){
		// 대용량 처리
		return bidNoticeEvalService.findListBidNotiEval(param);
	}
	
	/**
	 * Find list bid noti eval detail.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "findListBidNotiEvalDetail.do")
	public @ResponseBody Map findListBidNotiEvalDetail(@RequestBody Map param){
		return bidNoticeEvalService.findListBidNotiEvalDetail(param);
	}
	
	/**
	 * Disqual bid noti eval.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "disqualBidNotiEval.do")
	public @ResponseBody ResultMap disqualBidNotiEval(@RequestBody Map param){
		return bidNoticeEvalService.disqualBidNotiEval(param);
	}
	
	/**
	 * Qual bid noti eval.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "qualBidNotiEval.do")
	public @ResponseBody ResultMap qualBidNotiEval(@RequestBody Map param){
		return bidNoticeEvalService.qualBidNotiEval(param);
	}
	
	
	/**
	 * Complete bid noti eval.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "completeBidNotiEval.do")
	public @ResponseBody ResultMap completeBidNotiEval(@RequestBody Map param){
		return bidNoticeEvalService.completeBidNotiEval(param);
	}
	
	/**
	 * Find bid noti part.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "findBidNotiPart.do")
	public @ResponseBody Map findBidNotiPart(@RequestBody Map param){
		return bidNoticeEvalService.findBidNotiPart(param);
	}
	
	/**
	 * Find list bid noti part attach.
	 *
	 * @param param the param
	 * @return the list
	 */
	@RequestMapping(value = "findListBidNotiPartAttach.do")
	public @ResponseBody List<Map> findListBidNotiPartAttach(@RequestBody Map param){
		return bidNoticeEvalService.findListBidNotiPartAttach(param);
	}
	
}
