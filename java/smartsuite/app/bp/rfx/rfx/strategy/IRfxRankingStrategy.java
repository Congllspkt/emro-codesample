package smartsuite.app.bp.rfx.rfx.strategy;

import java.util.List;
import java.util.Map;

/**
 * IRfxRankingStrategy Interface 입니다.
 *
 * @author Yeon-u Kim
 */
public interface IRfxRankingStrategy {
	
	/**
	 * Compute ranking.
	 *
	 * @param attends the attends
	 * @param amtKey the amt key
	 */
	//랭킹계산
	void computeRanking(List<Map<String,Object>> attends,String amtKey);
	
	/**
	 * 제한 기준이 있는 경우의 ranking 계산
	 * 
	 * @param attends
	 * @param amtKey
	 * @param lowerLimitKey
	 * @author kh_lee
	 */
	void computeRanking(List<Map<String,Object>> attends, String amtKey, String lowerLimitKey);
}
