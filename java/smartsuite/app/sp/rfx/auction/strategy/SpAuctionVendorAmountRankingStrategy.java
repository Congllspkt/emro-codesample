package smartsuite.app.sp.rfx.auction.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class SpAuctionVendorAmountRankingStrategy {

	final private static String RANKING = "slctn_rank";
	
	public void computeRanking(List<Map<String, Object>> attends, String amtKey) {
		TreeMap<BigDecimal, List<Map<String, Object>>> rankedSortedAttends = new TreeMap<BigDecimal, List<Map<String, Object>>>();
		
		for(Map<String, Object> attend : attends) {
			BigDecimal amount = (BigDecimal)attend.get(amtKey);
			
			List<Map<String, Object>> rankedAttends = null;
			if(rankedSortedAttends.containsKey(amount)) {
				rankedAttends = rankedSortedAttends.get(amount);
			} else {
				rankedSortedAttends.put(amount, rankedAttends = Lists.newArrayList());
			}
			rankedAttends.add(attend);
		}
		
		List<List<Map<String, Object>>> rankedAttends = Lists.newArrayList();
		
		for(BigDecimal amount : rankedSortedAttends.keySet()) {
			rankedAttends.add(rankedSortedAttends.get(amount));
		}
		
		for(int i = 0; i < rankedAttends.size(); i++) {
			List<Map<String, Object>> sortAttends = rankedAttends.get(i);
			for(Map<String, Object> attend : sortAttends) {
				int ranking = i + 1;
				attend.put(RANKING, ranking);
			}
		}
	}
}
