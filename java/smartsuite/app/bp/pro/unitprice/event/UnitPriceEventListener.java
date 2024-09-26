package smartsuite.app.bp.pro.unitprice.event;

import com.google.common.collect.Maps;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.unitprice.service.UnitPriceService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UnitPriceEventListener {

	@Inject
	UnitPriceService unitPriceService;
	
	/**
	 * 단가 생성 요청 시
	 *
	 * @param event
	 * @return void
	 *
	 * event.data.cntr_uuid - 계약 uuid
	 */
	@EventListener(condition = "#event.eventId == 'createUnitPrice'")
	public void createUnitPrice(CustomSpringEvent event) {
		Map<String,Object> param = (Map<String, Object>) event.getData();
		
		if(!param.containsKey("cntr_uuid")) {
			return;
		}
		
		// 단가테이블 저장
		unitPriceService.createUnitPrice(param);
		//단가정보 생성후 spot pr item 건들을 정리
		unitPriceService.resetSpotPrItem(param);
	}
	
	@EventListener(condition = "#event.eventId == 'ITEM_UPRCCNTR'")
	public void findListVendorRcmdItemUprccntr(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(unitPriceService.findListVendorRcmdItemUprccntr(param));
	}
	
	@EventListener(condition = "#event.eventId == 'SG_UPRCCNTR'")
	public void findListVendorRcmdSgUprccntr(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(unitPriceService.findListVendorRcmdSgUprccntr(param));
	}

	/**
	 * 품목에서 계약 여부 체크
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'findListBpaYnAndMutlVdYnByItemCd'")
	public void findListBpaYnAndMutlVdYnByItemCd(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(unitPriceService.findListBpaYnAndMutlVdYnByItemCd(param));
	}
	
	@EventListener(condition = "#event.eventId == 'findListUnitPriceByItemAndOorg'")
	public void findListUnitPriceByItemAndOorg(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		List<Map> resultList = unitPriceService.findListUnitPriceByItemAndOorg(param);
		event.setResult(resultList);
	}
	
	@EventListener(condition = "#event.eventId == 'findListUnitPriceQtaInfoByItemAndOorg'")
	public void findListUnitPriceQtaInfoByItemAndOorg(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		List<Map> resultList = unitPriceService.findListUnitPriceQtaInfoByItemAndOorg(param);
		event.setResult(resultList);
	}
}
