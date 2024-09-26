package smartsuite.app.bp.pro.catalog.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.catalog.service.CatalogService;
import smartsuite.app.bp.pro.po.service.PoCntrReqService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class CatalogEventListener {
	
	@Inject
	CatalogService catalogService;
	
	/**
	 * 카탈로그 단가계약 목록 조회
	 *
	 * @param event
	 *
	 * data.req_uuid 계약 변경/해지 프로세스 요청 UUID
	 */
	@EventListener(condition = "#event.eventId == 'findListUprcItemWithCatalog'")
	public void findListUprcItemWithCatalog(CustomSpringEvent event) throws Exception {
		List<Map<String,Object>> resultList = catalogService.findListUprcItemWithCatalog((Map) event.getData());
		event.setResult(resultList);
	}

	@EventListener(condition = "#event.eventId == 'findListUprcItemWithoutCatalog'")
	public void findListUprcItemWithoutCatalog(CustomSpringEvent event) {
		List<Map<String,Object>> resultList = catalogService.findListUprcItemWithoutCatalog((Map) event.getData());
		event.setResult(resultList);
	}

}
