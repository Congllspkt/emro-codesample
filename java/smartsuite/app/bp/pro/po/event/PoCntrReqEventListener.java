package smartsuite.app.bp.pro.po.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.po.service.PoCntrReqService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@Transactional
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class PoCntrReqEventListener {
	
	@Inject
	PoCntrReqService poCntrReqService;
	
	/**
	 * 계약 변경/해지 요청 접수 시
	 *
	 * @param event
	 *
	 * data.req_uuid 계약 변경/해지 프로세스 요청 UUID
	 */
	@EventListener(condition = "#event.eventId == 'receiptCntrChgReqByPo'")
	public void receiptCntrChgReqByPo(CustomSpringEvent event) {
		ResultMap resultMap = poCntrReqService.receiptCntrChgReqByPo((Map) event.getData());
		event.setResult(resultMap);
	}
	
	/**
	 * 계약 변경/해지 요청 반려 시
	 *
	 * @param event
	 *
	 * data.req_uuid 계약 변경/해지 프로세스 요청 UUID
	 * data.ret_rsn 반려 사유
	 */
	@EventListener(condition = "#event.eventId == 'rejectCntrChgReqByPo'")
	public void rejectCntrChgReqByPo(CustomSpringEvent event) {
		ResultMap resultMap = poCntrReqService.rejectCntrChgReqByPo((Map) event.getData());
		event.setResult(resultMap);
	}
	
	/**
	 * 계약 변경/해지 요청 완료 시
	 *
	 * @param event
	 *
	 * data.req_uuid 계약 변경/해지 프로세스 요청 UUID
	 * data.cntr_uuid 변경된 계약 UUID
	 */
	@EventListener(condition = "#event.eventId == 'completeCntrChgReqByPo'")
	public void completeCntrChgReqByPo(CustomSpringEvent event) {
		ResultMap resultMap = poCntrReqService.completeCntrChgReqByPo((Map) event.getData());
		event.setResult(resultMap);
	}
}
