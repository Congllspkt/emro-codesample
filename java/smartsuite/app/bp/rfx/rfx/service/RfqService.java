package smartsuite.app.bp.rfx.rfx.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.factory.RfxFactory;
import smartsuite.app.bp.rfx.rfx.strategy.IRfxRankingStrategy;
import smartsuite.app.bp.rfx.rfx.strategy.RfxStrategy;
import smartsuite.app.bp.rfx.rfxpreinsp.service.RfxPreInspService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.scheduler.core.ScheduleService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Rfq 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @see 
 * @FileName RfqService.java
 * @package smartsuite.app.bp.rfx.rfx
 * @Since 2016. 5. 16
 * @변경이력 : [2016. 5. 16] Yeon-u Kim 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfqService implements RfxStrategy {


	/** The shared service. */
	@Inject
	SharedService sharedService;
	
	/** The schedule service. */
	@Inject
	ScheduleService scheduleService;

	/** The pro status processor. */
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	RfxService rfxService;

	@Inject
	RfxBidService rfxBidService;
	
	/** The factory. */
	@Inject
	RfxFactory factory;
	
	@Inject
	RfxPreInspService rfxPreInspService;
	
	
	@Override
	public void startRfx(Map<String, Object> param) {
		rfxStatusService.startRfx(param);
		
		// Rfx사전심사 등록
		rfxPreInspService.registRfxPreInsp(param);
	}

	@Override
	public ResultMap closeRfx(Map param) {
		String imdtOpenYn = param.get("immed_open_use_yn").toString();
		
		// 상태 업데이트 (마감 - 개찰전)
		rfxStatusService.closeRfx(param);

		// 즉시개찰인 경우
		if("Y".equals(imdtOpenYn)) {
			// RFQ 개찰 처리
			rfxService.openRfx(param);
		}
		// Rfx사전심사 강제마감시 프로세스
		//rfxPreInspService.closeRfxPreInsp(param);
		
		return ResultMap.SUCCESS();
	}

	@Override
	public void computeRanking(Map<String, Object> param) {
		if("BYTOT".equals((String)param.get("item_slctn_typ_ccd"))) {
			// 총액별 ranking
			computeDocRanking(param);
		} else {
			// 품목별 ranking
			computeItmRanking(param);
		}
	}
	
	private void computeDocRanking(Map<String, Object> param) {
		// 업체 견적서 조회
		List<Map<String, Object>> rfxBids = rfxBidService.findListRfxBidScore(param);
		
		IRfxRankingStrategy strategy = factory.getRfxRankingStrategy("AMOUNT");
		
		// 제한적 최저가 ranking 구하기
		if(param.get("slctn_typ_ccd") != null && "LTD_LOWSTPRC".equals((String)param.get("slctn_typ_ccd"))) {
			strategy.computeRanking(rfxBids, "exch_rfx_bid_amt", "lmt_amt");
		} else {
			// 최저가 ranking 구하기
			strategy.computeRanking(rfxBids, "exch_rfx_bid_amt");
		}
		
		// 점수 및 ranking 업데이트
		for(Map<String, Object> rfxBid : rfxBids) {
			rfxBidService.updateRfxBidRanking(rfxBid);
		}
	}
	
	private void computeItmRanking(Map<String, Object> param) {
		List<Map<String, Object>> rfxItems = rfxBidService.findListRfxBidItemAmt(param);
		
		for(Map<String, Object> rfxItem : rfxItems) {
			// 품목 별 견적 내역
			List<Map<String, Object>> itemQtas = (List<Map<String, Object>>)rfxItem.get("itemQtas");
			
			IRfxRankingStrategy strategy = factory.getRfxRankingStrategy("AMOUNT");
			if(param.get("slctn_typ_ccd") != null && "LTD_LOWSTPRC".equals((String)param.get("slctn_typ_ccd"))) {
				// 제한적 최저가 ranking 구하기
				strategy.computeRanking(itemQtas, "exch_rfx_item_subm_uprc", "rfx_lmt_uprc");
			} else {
				// 최저가 ranking 구하기
				strategy.computeRanking(itemQtas, "exch_rfx_item_subm_uprc");
			}
			
			// 점수 및 ranking 업데이트
			for(Map<String, Object> itemQta : itemQtas) {
				rfxBidService.updateRfxBidItemRanking(itemQta);
			}
		}
	}
}
