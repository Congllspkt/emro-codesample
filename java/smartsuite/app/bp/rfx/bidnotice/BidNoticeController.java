package smartsuite.app.bp.rfx.bidnotice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.bidnotice.service.BidNoticeService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * BidNotice 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @since 2021. 5. 14
 * @FileName BidNoticeController.java
 * @package smartsuite.app.bp.rfx.bidnotice
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/rfx/bidnotice/**/")
public class BidNoticeController {

	/** The bid notice service. */
	@Inject
	BidNoticeService bidNoticeService;
	
	/**
	 * Find list bid noti.
	 *
	 * @param param the param
	 * @return the floater stream
	 */
	@RequestMapping(value = "findListBidNoti.do")
	public @ResponseBody FloaterStream findListBidNoti(@RequestBody Map param) {
		// 대용량 처리
		return bidNoticeService.findListBidNoti(param);
	}
	
	/**
	 * Find bid noti.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "findBidNoti.do")
	public @ResponseBody Map findBidNoti(@RequestBody Map param) {
		return bidNoticeService.findBidNotiInfo(param);
	}
	
	/**
	 * bid noti 임시 저장한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : saveDraftBidNoti
	 */
	@RequestMapping(value = "saveDraftBidNoti.do")
	public @ResponseBody ResultMap saveDraftBidNoti(@RequestBody Map param) {
		return bidNoticeService.saveDraftBidNoti(param);
	}
	
	/**
	 * bid noti 삭제를 요청한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : deleteBidNoti
	 */
	@RequestMapping(value = "deleteBidNoti.do")
	public @ResponseBody ResultMap deleteBidNoti(@RequestBody Map param) {
		return bidNoticeService.deleteBidNoti(param);
	}
	
	/**
	 * Copy bid noti.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "copyBidNoti.do")
	public @ResponseBody ResultMap copyBidNoti(@RequestBody Map param) {
		return bidNoticeService.copyBidNoti(param);
	}
	
	/**
	 * Cancel bid noti.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "cancelBidNoti.do")
	public @ResponseBody ResultMap cancelBidNoti(@RequestBody Map param) {
		return bidNoticeService.cancelBidNoti(param);
	}
	
	/**
	 * Find bid noti change info.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "findBidNotiChangeInfo.do")
	public @ResponseBody Map findBidNotiChangeInfo(@RequestBody Map param) {
		return bidNoticeService.findBidNotiChangeInfo(param);
	}

	/**
	 * bid noti time change 임시 저장한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 18
	 * @Method Name : saveDraftBidNotiTimeChange
	 */
	@RequestMapping(value = "saveDraftBidNotiTimeChange.do")
	public @ResponseBody ResultMap saveDraftBidNotiTimeChange(@RequestBody Map param) {
		return bidNoticeService.saveDraftBidNotiTimeChange(param);
	}
	
	/**
	 * draft BN time change 삭제를 요청한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 18
	 * @Method Name : deleteDraftBNTimeChange
	 */
	@RequestMapping(value = "deleteDraftBNTimeChange.do")
	public @ResponseBody ResultMap deleteDraftBNTimeChange(@RequestBody Map param) {
		return bidNoticeService.deleteDraftBNTimeChange(param);
	}
	
	/**
	 * Bypass bid noti time change.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "bypassBidNotiTimeChange.do")
	public @ResponseBody ResultMap bypassBidNotiTimeChange(@RequestBody Map param) {
		return bidNoticeService.bypassBidNotiTimeChange(param);
	}

	/**
	 * Find list bid noti time change.
	 *
	 * @param param the param
	 * @return the floater stream
	 */
	@RequestMapping(value = "findListBidNotiTimeChange.do")
	public @ResponseBody FloaterStream findListBidNotiTimeChange(@RequestBody Map param) {
		return bidNoticeService.findListBidNotiTimeChange(param);
	}
	
	
	/**
	 * Find list bid noti complete.
	 *
	 * @param param the param
	 * @return the floater stream
	 */
	@RequestMapping(value = "findListBidNotiComplete.do")
	public @ResponseBody FloaterStream findListBidNotiComplete(@RequestBody Map param) {
		return bidNoticeService.findListBidNotiComplete(param);
	}
	
	/**
	 * Find list bid noti complete vd list.
	 *
	 * @param param the param
	 * @return the list
	 */
	@RequestMapping(value = "findListBidNotiCompleteVdList.do")
	public @ResponseBody List<Map> findListBidNotiCompleteVdList(@RequestBody Map param) {
		return bidNoticeService.findListBidNotiCompleteVdList(param);
	}

	/**
     * Check Correct bid noti.
	 *
	 * @param param the param
	 * @return the map
	 */
	 @RequestMapping(value = "checkCorrectBidNoti.do")
	 public @ResponseBody ResultMap checkCorrectBidNoti(@RequestBody Map param) {
         return bidNoticeService.checkCorrectBidNoti(param);
     }
	
	/**
	 * Correct bid noti.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "correctBidNoti.do")
	public @ResponseBody ResultMap correctBidNoti(@RequestBody Map param) {
		return bidNoticeService.correctBidNoti(param);
	}
	
	/**
	 * Re new bid notice.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "reNewBidNotice.do")
	public @ResponseBody ResultMap reNewBidNotice(@RequestBody Map param) {
		return bidNoticeService.reNewBidNotice(param);
	}

	/**
	 * Bypass bid noti close
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "byPassCloseBidNotices.do")
	public @ResponseBody ResultMap byPassCloseBidNotices(@RequestBody Map param) {
		return bidNoticeService.byPassCloseBidNotices(param);
	}
}