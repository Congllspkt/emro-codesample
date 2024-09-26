package smartsuite.app.sp.rfx.bidnotice.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.app.sp.rfx.bidnotice.service.SpBidNoticeStatsService;
import smartsuite.mybatis.data.impl.PageResult;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class SpBidNoticeEventListener {
	
	@Inject
	SpBidNoticeStatsService spBidNoticeStatsService;
	
	/**
	 * 조회요청을 받아 살아있는 입찰공고 건수를 반환한다.
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'getBidNoticeTotalCount'")
	public void getBidNoticeTotalCount(CustomSpringEvent event) {
		int cnt = spBidNoticeStatsService.getBidNoticeTotalCount();
		event.setResult(cnt);
	}
	
	/**
	 * 페이지정보를 받아 살아있는 입찰공고 목록을 페이지 유형으로 반환한다.
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'findPagingBidNoticeList'")
	public void findPagingBidNoticeList(CustomSpringEvent event) {
		if(event.getData() == null) {
			return;
		}
		PageResult result = spBidNoticeStatsService.findPagingBidNoticeList((Map) event.getData());
		event.setResult(result);
	}
	
	/**
	 * 입찰공고 상세내역을 템플릿 유형으로 변환 후 반환한다.
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'getBidNoticeTemplateContent'")
	public void getBidNoticeTemplateContent(CustomSpringEvent event) {
		if(event.getData() == null) {
			return;
		}
		Map param = (Map) event.getData();
		if(param.get("rfx_uuid") == null) {
			return;
		}
		try {
			String content = spBidNoticeStatsService.getBidNoticeTemplateContent(param);
			event.setResult(content);
		} catch(Exception e) {
			event.setResult(null);
		}
	}
}
