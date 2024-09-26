package smartsuite.app.sp.rfx.bidnotice;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.bidnotice.service.SpBidNoticeService;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/sp/rfx/bidnotice/**/")
public class SpBidNoticeController {

	/** The auction vendor service. */
	@Inject
	SpBidNoticeService spBidNoticeService;
	
	/**
	 * 협력사 입찰공고현황 조회를 요청한다.
	 * 
	 * @param param : 검색조건
	 * @return list : 입찰공고현황(협력사)
	 * @Date : 2021. 5. 17
	 * @Method Name : findListSpBidNoti
	 */
	@RequestMapping(value="findListSpBidNoti.do")
	public @ResponseBody List findListSpBidNoti(@RequestBody Map param) {
		return spBidNoticeService.findListSpBidNoti(param);
	}
	
	/**
	 * 입찰참가신청 상세정보를 조회한다.
	 * 
	 * @return list : 입찰참가신청
	 * @Date : 2021. 5. 17
	 * @Method Name : findDetailSpBidNoti
	 */
	@RequestMapping(value="findDetailSpBidNoti.do")
	public @ResponseBody Map findDetailSpBidNoti(@RequestBody Map param) {
		return spBidNoticeService.findDetailSpBidNoti(param);
	}
	
	/**
	 * 입찰참가신청 정보를 조회한다.
	 * 
	 * @return list : 입찰참가신청
	 * @Date : 2021. 5. 17
	 * @Method Name : findInfoSpBidNoti
	 */
	@RequestMapping(value="findInfoSpBidNoti.do")
	public @ResponseBody Map findInfoSpBidNoti(@RequestBody Map param) {
		return spBidNoticeService.findInfoSpBidNoti(param);
	}
	
	/**
	 * bid noti 저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : saveSpBidNoti
	 */
	@RequestMapping(value = "saveSpBidNoti.do")
	public @ResponseBody ResultMap saveSpBidNoti(@RequestBody Map param){
		return spBidNoticeService.saveSpBidNoti(param);
	}
	
	/**
	 * bid noti 삭제를 요청한다.
	 *
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : deleteSpBidNoti
	 */
	@RequestMapping(value = "deleteSpBidNoti.do")
	public @ResponseBody ResultMap deleteSpBidNoti(@RequestBody Map param){
		return spBidNoticeService.deleteSpBidNoti(param);
	}
	
}