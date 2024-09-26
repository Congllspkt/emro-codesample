package smartsuite.app.bp.rfx.rfx.strategy;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * RfxScoreRankingStrategy Class 입니다.
 *
 * @author Yeon-u Kim
 */
@Service
public class RfxScoreRankingStrategy implements IRfxRankingStrategy {

	final private static String RANKING = "slctn_rank";
	
	/* (non-Javadoc)
	 * @see smartsuite.app.bp.rfx.rfx.strategy.IRfxRankingStrategy#computeRanking(java.util.List)
	 */
	@Override
	public void computeRanking(List<Map<String, Object>> attends, final String scoreKey) {
		orderByScore(attends, scoreKey, null);
	}

	/**
	 * 제한점수 존재 시 ranking 구하기
	 * 
	 * @param attends
	 * @param scoreKey
	 * @param lowerLimitKey
	 * @author kh_lee
	 */
	@Override
	public void computeRanking(List<Map<String, Object>> attends, final String scoreKey, final String lowerLimitKey) {
		orderByScore(attends, scoreKey, lowerLimitKey);
	}
	
	private void orderByScore(List<Map<String, Object>> attends, final String scoreKey, final String lowerLimitKey) {
		
		// scoreKey로 정렬
		Collections.sort(attends, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
            	return ((BigDecimal)obj1.get(scoreKey)).compareTo((BigDecimal)obj2.get(scoreKey));
            }
        });
		
		// ranking 구하기
		int ranking = 1, attendsCnt = attends.size(), lastAttendCnt = 0;
		for(int i=attendsCnt-1; i>=0; i--) {
			Map<String, Object> attend = attends.get(i);
			BigDecimal score = (BigDecimal)attend.get(scoreKey);
			BigDecimal limitScore = Strings.isNullOrEmpty(lowerLimitKey) ? null : (BigDecimal)attend.get(lowerLimitKey);
			
			//위에서 sort하여, nextScore.compareTo(score) <= 0 임
			if(score.compareTo(BigDecimal.ZERO) == 0 || limitScore != null && score.compareTo(limitScore) < 0) {
				attend.put(RANKING, attendsCnt); // 제한점수 보다 낮으면 꼴지 처리 (kh_lee. SNINEQA-123)
				lastAttendCnt++;
			} else {
				attend.put(RANKING, ranking++);
				
				if(i < attendsCnt-1) {
					Map<String, Object> nextAttend = attends.get(i+1);
					BigDecimal nextScore = (BigDecimal)nextAttend.get(scoreKey);
					
					// 앞과 동점일 경우 동일 순위로 처리 (kh_lee. SNINEQA-167)
					if(nextScore.compareTo(score) == 0) {
						attend.put(RANKING, nextAttend.get(RANKING));
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
