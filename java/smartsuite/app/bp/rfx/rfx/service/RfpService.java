package smartsuite.app.bp.rfx.rfx.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.factory.RfxFactory;
import smartsuite.app.bp.rfx.rfx.strategy.IRfxRankingStrategy;
import smartsuite.app.bp.rfx.rfx.strategy.RfxStrategy;
import smartsuite.app.bp.rfx.rfxpreinsp.service.RfxPreInspService;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.scheduler.core.ScheduleService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Rfp 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @FileName RfpService.java
 * @package smartsuite.app.bp.rfx.rfx
 * @Since 2016. 5. 19
 * @변경이력 : [2016. 5. 19] Yeon-u Kim 최초작성
 * @see
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfpService implements RfxStrategy {
	
	
	/**
	 * The shared service.
	 */
	@Inject
	SharedService sharedService;
	
	/**
	 * The schedule service.
	 */
	@Inject
	ScheduleService scheduleService;
	
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	RfxEvalService rfxEvalService;
	
	@Inject
	RfxService rfxService;
	
	@Inject
	RfxBidService rfxBidService;
	
	/**
	 * The factory.
	 */
	@Inject
	RfxFactory factory;
	
	@Inject
	FormatterProvider formatterProvider;
	
	@Inject
	RfxPreInspService rfxPreInspService;
	
	@Override
	public void startRfx(Map<String, Object> param) {
		rfxStatusService.startRfx(param);
		
		// Rfx사전심사 등록
		rfxPreInspService.registRfxPreInsp(param);
	}
	
	@Override
	public ResultMap closeRfx(Map rfxInfo) {
		ResultMap resultMap = ResultMap.SUCCESS();
		
		// 견적제출 업체가 존재할 경우에만 평가정보 생성
		int vdSubmitCnt = Integer.parseInt(rfxInfo.get("vd_subm_cnt").toString());
		if(vdSubmitCnt > 0) {
			// 평가생성
			ResultMap evalResultMap = rfxEvalService.createEval(rfxInfo);
			if(!evalResultMap.isSuccess()) {
				return evalResultMap;
			}
			
			int rfx_rnd = Integer.parseInt(rfxInfo.get("rfx_rnd").toString());
			if(rfx_rnd > 1) {
				// 2차수부터는 전차수의 평가 점수 복사
				resultMap = rfxEvalService.copyEval(rfxInfo);
			}
		}
		
		// 상태 업데이트 (마감 처리 - RFxHd, PrDt)
		rfxStatusService.closeRfx(rfxInfo);
		
		// 견적제출 업체가 존재하지 않을 경우 즉시개찰여부와 상관없이 강제 개찰 처리 (RFxHd)
		if(vdSubmitCnt == 0) {
			rfxStatusService.openRfx(rfxInfo);
		}
		
		return resultMap;
	}
	
	@Override
	public void computeRanking(Map<String, Object> param) {
		List<Map<String, Object>> rfxBids = rfxBidService.findListRfxBidScore(param);
		Map rfxInfo = rfxService.findRfx(param);
		
		BigDecimal passScore = (BigDecimal) rfxInfo.get("compreval_pass_sc");
		if(rfxBids != null && !rfxBids.isEmpty()) {
			//sql결과값이 order by 가 rfx_bid_amt 오름차순으로 조회되기때문에 첫번째 row가 최저가
			BigDecimal lowerPrice = (BigDecimal) rfxBids.get(0).get("exch_rfx_bid_amt");
			
			for(Map<String, Object> rfxBid : rfxBids) {
				// 비가격평가 가중치 점수 계산
				BigDecimal npeConvScore = BigDecimal.ZERO;
				
				if(rfxBid.get("npe_avg_sc") != null) {
					BigDecimal npeAvgSc = (BigDecimal) rfxBid.get("npe_avg_sc");
					BigDecimal npeRo = (BigDecimal) rfxBid.get("npe_ro");
					npeConvScore = formatterProvider.getPrecFormat("proScore", npeAvgSc.multiply(npeRo).divide(new BigDecimal(100)), "");
				}
				
				// 가격 점수 계산 (100점 만점 기준)
				BigDecimal priceCalcScore = BigDecimal.ZERO;
				String prcEvalTyp = (String) rfxBid.get("cbe_typ_ccd");
				BigDecimal amount = (BigDecimal) rfxBid.get("exch_rfx_bid_amt");
				
				if("MIN_AMT_CRTRA_RO".equals(prcEvalTyp)) {            // 최소금액기준비율
					// 총금액이 0보다 클경우만 계산함
					if(BigDecimal.ZERO.compareTo(amount) < 0) {
						BigDecimal suv = amount.subtract(lowerPrice).divide(amount, 10, BigDecimal.ROUND_CEILING);
						priceCalcScore = BigDecimal.ONE.subtract(suv).multiply(new BigDecimal(100));
					}
				} else if("LNR".equals(prcEvalTyp)) {    // Linear
					BigDecimal minVal = (BigDecimal) rfxBid.get("min_val");    //최소 값(금액) - 최저 하한금액(총 요청금액 이하)
					BigDecimal minScore = (BigDecimal) rfxBid.get("min_sc");    //최소 값(금액)의 점수 - 금액이 작을 수록 높은 점수
					BigDecimal maxVal = (BigDecimal) rfxBid.get("max_val");    //최대 값(금액) - 최대 상한금액
					BigDecimal maxScore = (BigDecimal) rfxBid.get("max_sc");    //최대 값(금액)의 점수 - 금액이 클 수록 낮은 점수
					
					BigDecimal suv = maxScore.subtract(minScore).divide(maxVal.subtract(minVal), 10, BigDecimal.ROUND_CEILING);
					priceCalcScore = minScore.add(amount.subtract(minVal).multiply(suv));
				}
				
				// 100점 넘으면 100점으로
				if(priceCalcScore.compareTo(new BigDecimal(100)) > 0) {
					priceCalcScore = new BigDecimal(100);
				} else if(priceCalcScore.compareTo(BigDecimal.ZERO) < 0) {
					priceCalcScore = BigDecimal.ZERO;
				}
				
				// 가격평가 가중치 점수 계산
				BigDecimal priceEvalRate = (BigDecimal) rfxBid.get("cbe_ro");
				BigDecimal priceScore = formatterProvider.getPrecFormat("proScore", priceCalcScore.multiply(priceEvalRate).divide(new BigDecimal(100)), "");
				BigDecimal totalScore = priceScore.add(npeConvScore);
				
				rfxBid.put("npe_conv_sc", npeConvScore);                                                        // 가중치 적용한 비가격평가 합계 점수
				rfxBid.put("cbe_sc", formatterProvider.getPrecFormat("proScore", priceCalcScore, ""));    // 100점 만점의 가격평가 점수
				rfxBid.put("cbe_conv_sc", priceScore);                                                            // 가중치 적용한 가격평가 점수
				rfxBid.put("eval_ttl_sc", totalScore);                                                            // 가중치 적용한 가격평가 점수 + 가중치 적용한 비가격평가 합계 점수
				
				if(passScore.compareTo(totalScore) > 0) {
					rfxBid.put("npe_pass_yn", "N");
				} else {
					rfxBid.put("npe_pass_yn", "Y");
				}
			}
			
			IRfxRankingStrategy strategy = factory.getRfxRankingStrategy("SCORE");
			strategy.computeRanking(rfxBids, "eval_ttl_sc");
			
			// 점수 및 ranking 업데이트
			for(Map<String, Object> rfxBid : rfxBids) {
				rfxBidService.updateRfxBidRanking(rfxBid);
			}
		}
	}
}
