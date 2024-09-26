package smartsuite.app.bp.rfx.rfx.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.service.RfxNxtPrcsService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxNxtPrcsEventListener {
	
	@Inject
	RfxNxtPrcsService rfxNxtPrcsService;
	
	/**
	 * 후속 프로세스에서 요청 접수 시
	 *
	 * @param event
	 *
	 * data.req_uuid RFX 후속 프로세스 요청 UUID
	 */
	@EventListener(condition = "#event.eventId == 'receiptReqRfx'")
	public void receiptReqRfx(CustomSpringEvent event) {
		ResultMap resultMap = rfxNxtPrcsService.receiptReqRfx((Map) event.getData());
		event.setResult(resultMap);
	}
	
	/**
	 * 후속 프로세스에서 요청 반려 시
	 *
	 * @param event
	 *
	 * data.req_uuid RFX 후속 프로세스 요청 UUID
	 * data.ret_rsn 반려 사유
	 */
	@EventListener(condition = "#event.eventId == 'rejectReqRfx'")
	public void rejectReqRfx(CustomSpringEvent event) {
		ResultMap resultMap = rfxNxtPrcsService.rejectReqRfx((Map) event.getData());
		event.setResult(resultMap);
	}
	
	/**
	 * 후속 프로세스에서 업무 완료 시
	 *
	 * @param event
	 *
	 * data.req_uuid RFX 후속 프로세스 요청 UUID
	 */
	@EventListener(condition = "#event.eventId == 'completeReqRfx'")
	public void completeReqRfx(CustomSpringEvent event) {
		ResultMap resultMap = rfxNxtPrcsService.completeReqRfx((Map) event.getData());
		event.setResult(resultMap);
	}
}
