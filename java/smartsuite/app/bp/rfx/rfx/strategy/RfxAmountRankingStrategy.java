package smartsuite.app.bp.rfx.rfx.strategy;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * RfxAmountRankingStrategy Class 입니다.
 *
 * @author Yeon-u Kim
 */
@Service
public class RfxAmountRankingStrategy implements IRfxRankingStrategy {

	final private static String RANKING = "slctn_rank";
	
	/* (non-Javadoc)
	 * @see smartsuite.app.bp.rfx.rfx.strategy.IRfxRankingStrategy#computeRanking(java.util.List)
	 */
	@Override
	public void computeRanking(List<Map<String, Object>> attends, final String amtKey) {
		orderByAmount(attends, amtKey, null);
	}

	/**
	 * 제한적 최저가 ranking 구하기
	 * 
	 * @param attends
	 * @param amtKey
	 * @param lowerLimitKey
	 * @author kh_lee
	 */
	@Override
	public void computeRanking(List<Map<String, Object>> attends, final String amtKey, final String lowerLimitKey) {
		orderByAmount(attends, amtKey, lowerLimitKey);
	}
	
	private void orderByAmount(List<Map<String, Object>> attends, final String amtKey, final String lowerLimitKey) {
		
		// amtKey로 정렬
		Collections.sort(attends, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
            	return ((BigDecimal)obj1.get(amtKey)).compareTo((BigDecimal)obj2.get(amtKey));
            }
        });
		
		// ranking 구하기
		int ranking = 1, attendsCnt = attends.size(), lastAttendCnt = 0;
		for(int i=0; i<attendsCnt; i++) {
			Map<String, Object> attend = attends.get(i);
			BigDecimal amount = (BigDecimal)attend.get(amtKey);
			BigDecimal limitAmount = Strings.isNullOrEmpty(lowerLimitKey) ? null : (BigDecimal)attend.get(lowerLimitKey);
			
			//위에서 sort하여, prevAmount.compareTo(amount) <= 0 임
			//2018.10.12 PMD - Useless parentheses. 사유로 수정됨.
			//if(amount.compareTo(BigDecimal.ZERO) == 0 || (limitAmount != null && amount.compareTo(limitAmount) < 0)) {
			if(amount.compareTo(BigDecimal.ZERO) == 0 || limitAmount != null && amount.compareTo(limitAmount) < 0) {
				attend.put(RANKING, attendsCnt); //제한가 보다 낮으면 꼴지 처리 (kh_lee. SNINEQA-123)
				lastAttendCnt++;
			} else {
				attend.put(RANKING, ranking++);
				
				if(i > 0) {
					Map<String, Object> prevAttend = attends.get(i-1);
					BigDecimal prevAmount = (BigDecimal)prevAttend.get(amtKey);
					
					// 앞과 동일 금액인 경우 동일 순위로 처리 (kh_lee. SNINEQA-167)
					if(prevAmount.compareTo(amount) == 0) {
						attend.put(RANKING, prevAttend.get(RANKING));
					}
				}
			}
		}
		
		if(lastAttendCnt > 1) {
			for(int i=0; i<attendsCnt; i++) {
				Map<String, Object> attend = attends.get(i);
				if((Integer)attend.get(RANKING) == attendsCnt) {
					attend.put(RANKING, attendsCnt-lastAttendCnt+1);
				}
			}
		}
		
		// ranking으로 정렬
		Collections.sort(attends, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
            	return (Integer)obj1.get(RANKING) - (Integer)obj2.get(RANKING);
            }
        });
	}
}
