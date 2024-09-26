package smartsuite.app.bp.rfx.auction.strategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class AuctionAmountRankingStrategy {
	
	final private static String RANKING = "slctn_rank";

	public List<Map<String, Object>> computeRanking(List<Map<String, Object>> attends, String amtKey) {
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
		return attends;
	}
	
	public List<Map<String, Object>> computeRankingByItem(List<Map<String, Object>> allItemAttends, String itemKey, String amtKey) {
		HashMap<String, List<Map<String, Object>>> auctionItemsMap = new HashMap<String, List<Map<String,Object>>>(); 
		
		for(Map<String, Object> itemAttend : allItemAttends) {
			String key = (String)itemAttend.get(itemKey);
			
			List<Map<String, Object>> auctionItemAttends = Lists.newArrayList();
			if(auctionItemsMap.containsKey(key)) {
				auctionItemAttends = auctionItemsMap.get(key);
			}
			auctionItemAttends.add(itemAttend);
			auctionItemsMap.put(key, auctionItemAttends);
		}
		
		for(String key : auctionItemsMap.keySet()) {
			computeRanking(auctionItemsMap.get(key), amtKey);
		}
		return allItemAttends;
	}
}
